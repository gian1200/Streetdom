package com.gian1200.games.streetdom;

import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.plus.PlusClient;

public class MainActivity extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener {

	ShareActionProvider mShareActionProvider;
	private LinearLayout signInBar, signedInBar;
	private TextView greeting;
	private Button signOutButton;
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 84395;
	private static final int REQUEST_CODE_RESOLVE_ERR = 21685;

	private ProgressDialog mConnectionProgressDialog;
	private PlusClient mPlusClient;
	private ConnectionResult mConnectionResult;
	private Locale locale;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((Application) getApplication()).refreshLanguage();
		locale = getResources().getConfiguration().locale;
		setContentView(R.layout.activity_main);
		signInBar = (LinearLayout) findViewById(R.id.main_sign_in_bar);
		signedInBar = (LinearLayout) findViewById(R.id.main_signed_in_bar);
		greeting = (TextView) findViewById(R.id.main_greeting);
		signOutButton = (Button) findViewById(R.id.main_sign_out);
		mPlusClient = new PlusClient.Builder(this, this, this).setActions(
				"http://schemas.google.com/AddActivity",
				"http://schemas.google.com/BuyActivity")
		// .setScopes(Scopes.PLUS_LOGIN)
				.build();
		// Progress bar to be displayed if the connection failure is not
		// resolved.
		mConnectionProgressDialog = new ProgressDialog(this);
		mConnectionProgressDialog.setMessage(getString(R.string.signing_in));
	}

	@Override
	protected void onResume() {
		super.onResume();
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
		mPlusClient.connect();
		int errorCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		switch (errorCode) {
		case ConnectionResult.SUCCESS:
			break;
		case ConnectionResult.SERVICE_MISSING:
		case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
		case ConnectionResult.SERVICE_DISABLED:
		case ConnectionResult.SERVICE_INVALID:
			GooglePlayServicesUtil.getErrorDialog(errorCode, this,
					PLAY_SERVICES_RESOLUTION_REQUEST).show();
			break;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		mPlusClient.disconnect();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PLAY_SERVICES_RESOLUTION_REQUEST:
			if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this,
						R.string.common_google_play_services_update_text,
						Toast.LENGTH_LONG).show();
			}
			return;
		case REQUEST_CODE_RESOLVE_ERR:
			switch (resultCode) {
			case RESULT_OK:
				Log.i("onActivityResult", "RESULT_OK");
				mConnectionResult = null;
				mPlusClient.connect();
				break;
			case RESULT_CANCELED:
				Log.i("onActivityResult", "RESULT_CANCELED: "
						+ mConnectionResult.getErrorCode());
				break;
			case RESULT_FIRST_USER:
				Log.i("onActivityResult", "RESULT_FIRST_USER");
				break;
			default:
				Log.i("onActivityResult", "Ni idea, resultCode: " + resultCode);
				break;
			}
			return;
		}

		Log.i("onActivityResult", "OTRO_requestCode");
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem item = menu.findItem(R.id.menu_item_share);
		mShareActionProvider = (ShareActionProvider) item.getActionProvider();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
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
		if (!mPlusClient.isConnected()) {
			if (mConnectionResult == null) {
				Log.i("singInGoogle", "mConnectionProgressDialog.show");
				mConnectionProgressDialog.show();
			} else {
				Log.i("singInGoogle",
						"mConnectionResult.startResolutionForResult.show");
				try {
					mConnectionResult.startResolutionForResult(this,
							REQUEST_CODE_RESOLVE_ERR);
				} catch (SendIntentException e) {
					Log.i("singInGoogle", "SendIntentException");
					// Try connecting again.
					mConnectionResult = null;
					mPlusClient.connect();
				}
				Log.i("singInGoogle", "Done");
			}
		} else {
			updateUI();
		}
	}

	public void signOutGoogle(View v) {
		if (mPlusClient.isConnected()) {
			mPlusClient.clearDefaultAccount();
			mPlusClient.disconnect();
			mPlusClient.connect();
			updateUI();
		}
	}

	public void showAchievements(View v) {

	}

	public void showLeaderboard(View v) {

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (mConnectionProgressDialog.isShowing()) {
			Log.i("onConnectionFailed", "mConnectionProgressDialog.isShowing");
			// The user clicked the sign-in button already. Start to resolve
			// connection errors. Wait until onConnected() to dismiss the
			// connection dialog.
			if (result.hasResolution()) {
				Log.i("onConnectionFailed", "result.hasResolution");
				try {
					result.startResolutionForResult(this,
							REQUEST_CODE_RESOLVE_ERR);
				} catch (SendIntentException e) {
					mPlusClient.connect();
				}
			}
		}

		// Save the intent so that we can start an activity when the user clicks
		// the sign-in button.
		mConnectionResult = result;
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// We've resolved any connection errors.
		mConnectionProgressDialog.dismiss();
		Log.i("onConnected", "conectado!");
		updateUI();
	}

	@Override
	public void onDisconnected() {
		Log.i("onDisconnected", "desconectado!");
		updateUI();
	}

	void updateUI() {
		if (mPlusClient != null && mPlusClient.isConnected()) {
			signInBar.setVisibility(View.GONE);
			signedInBar.setVisibility(View.VISIBLE);
			signOutButton.setVisibility(View.VISIBLE);
			greeting.setText(getString(R.string.signed_in_greeting, mPlusClient
					.getCurrentPerson().getName().getGivenName()));
		} else {
			signInBar.setVisibility(View.VISIBLE);
			signedInBar.setVisibility(View.GONE);
			signOutButton.setVisibility(View.GONE);
			greeting.setText(getString(R.string.not_signed_in_greeting));
		}
	}

}
