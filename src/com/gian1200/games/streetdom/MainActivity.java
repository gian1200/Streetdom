package com.gian1200.games.streetdom;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends Activity {

	Mission mission;
	ShareActionProvider mShareActionProvider;
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Locale locale = new Locale("es");
		// Configuration config = new Configuration();
		// config.locale = locale;
		// getResources().updateConfiguration(config, null);
		setContentView(R.layout.activity_main);
		mission = ((Application) getApplication()).missions.get(0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("current:", getResources().getConfiguration().locale.toString());
		Log.i("current2:", Locale.getDefault().toString());
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PLAY_SERVICES_RESOLUTION_REQUEST:
			if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this,
						R.string.google_play_services_must_be_installed,
						Toast.LENGTH_SHORT).show();
			}
			return;
		}
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

}
