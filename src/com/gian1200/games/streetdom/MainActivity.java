package com.gian1200.games.streetdom;

import java.util.Locale;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

public class MainActivity extends Activity implements GameHelperListener {

	ShareActionProvider mShareActionProvider;
	private LinearLayout signInBar, signedInBar;
	private TextView greeting;
	private Locale locale;
	private GameHelper mHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((Application) getApplication()).refreshLanguage();
		locale = getResources().getConfiguration().locale;
		setContentView(R.layout.activity_main);
		signInBar = (LinearLayout) findViewById(R.id.main_sign_in_bar);
		signedInBar = (LinearLayout) findViewById(R.id.main_signed_in_bar);
		greeting = (TextView) findViewById(R.id.main_greeting);
		mHelper = new GameHelper(this);
		mHelper.setup(this, GameHelper.CLIENT_GAMES);
		TextView progress = (TextView) findViewById(R.id.main_progress);
		progress.setText(getString(R.string.progress, 100f));
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!locale.equals(getResources().getConfiguration().locale)) {
			finish();
			startActivity(getIntent());
			overridePendingTransition(0, 0);
			return;
		}
		mHelper.onStart(this);
		int errorCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		switch (errorCode) {
		case ConnectionResult.SUCCESS:
			break;
		case ConnectionResult.SERVICE_MISSING:
		case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
		case ConnectionResult.SERVICE_DISABLED:
		case ConnectionResult.SERVICE_INVALID:
			GooglePlayServicesUtil.getErrorDialog(
					errorCode,
					this,
					getResources().getInteger(
							R.integer.request_code_play_services)).show();
			break;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		mHelper.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mHelper.onActivityResult(requestCode, resultCode, data);
		if (getResources().getInteger(R.integer.result_code_erase_data) == resultCode) {

		} else if (getResources().getInteger(
				R.integer.request_code_play_services) == requestCode) {
			if (resultCode != RESULT_OK) {
				Toast.makeText(this,
						R.string.common_google_play_services_update_text,
						Toast.LENGTH_LONG).show();
			}
		} else if (getResources().getInteger(
				R.integer.request_code_achievements) == requestCode) {
		} else if (getResources().getInteger(
				R.integer.request_code_leaderboards) == requestCode) {
		} else {
			Log.d("onActivityResult", "OTRO_requestCode");
		}
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem item = menu.findItem(R.id.menu_item_share);
		if (Build.VERSION_CODES.ICE_CREAM_SANDWICH <= Build.VERSION.SDK_INT) {
			mShareActionProvider = (ShareActionProvider) item
					.getActionProvider();
		}
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem signInOut = menu.findItem(R.id.action_sign_in_out);
		if (mHelper.isSignedIn()) {
			signInOut.setTitle(R.string.sign_out);
		} else {
			signInOut.setTitle(R.string.sign_in);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivityForResult(
					new Intent(this, SettingsActivity.class),
					getResources().getInteger(
							R.integer.request_code_settings_activity));
			return true;
		case R.id.action_sign_in_out:
			if (mHelper.isSignedIn()) {
				signOut();
			} else {
				signIn();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void showMissions(View v) {
		Intent intent = new Intent(this, MissionsActivity.class);
		startActivity(intent);
	}

	public void singInGoogle(View v) {
		signIn();
	}

	public void showAchievements(View v) {
		startActivityForResult(
				mHelper.getGamesClient().getAchievementsIntent(),
				getResources().getInteger(R.integer.request_code_achievements));
	}

	public void showLeaderboard(View v) {
		startActivityForResult(mHelper.getGamesClient()
				.getAllLeaderboardsIntent(),
				getResources().getInteger(R.integer.request_code_leaderboards));
	}

	void signIn() {
		Toast.makeText(this, R.string.signing_in, Toast.LENGTH_SHORT).show();
		mHelper.beginUserInitiatedSignIn();
		// if (!mPlusClient.isConnected()) {
		// if (mConnectionResult == null) {
		// Log.d("singInGoogle", "mConnectionProgressDialog.show");
		// mConnectionProgressDialog.show();
		// } else {
		// Log.d("singInGoogle",
		// "mConnectionResult.startResolutionForResult.show");
		// try {
		// mConnectionResult.startResolutionForResult(this,
		// REQUEST_CODE_RESOLVE_ERR);
		// } catch (SendIntentException e) {
		// Log.d("singInGoogle", "SendIntentException");
		// // Try connecting again.
		// mConnectionResult = null;
		// mPlusClient.connect();
		// }
		// Log.d("singInGoogle", "Done");
		// }
		// }
		updateUI();
	}

	void signOut() {
		// if (mPlusClient.isConnected()) {
		// mPlusClient.clearDefaultAccount();
		// mPlusClient.disconnect();
		// mPlusClient.connect();
		// updateUI();
		// }
		// no se necesita verificar
		mHelper.signOut();
		updateUI();
	}

	void updateUI() {
		if (mHelper != null && mHelper.isSignedIn()) {
			signInBar.setVisibility(View.GONE);
			signedInBar.setVisibility(View.VISIBLE);
			// greeting.setText(getString(R.string.signed_in_greeting, mHelper
			// .getPlusClient().getCurrentPerson().getName()
			// .getGivenName()));
			Log.d("CurrentPlayer", mHelper.getGamesClient().getCurrentPlayer()
					.toString());
			greeting.setText(getString(R.string.signed_in_greeting, mHelper
					.getGamesClient().getCurrentPlayer().getDisplayName()));
		} else {
			signInBar.setVisibility(View.VISIBLE);
			signedInBar.setVisibility(View.GONE);
			greeting.setText(getString(R.string.not_signed_in_greeting));
		}
	}

	@Override
	public void onSignInFailed() {
		Log.d("onSignInFailed", "nononono");
		updateUI();
	}

	@Override
	public void onSignInSucceeded() {
		Log.d("onSignInSucceeded", "sisisisis");
		updateUI();
	}

}
