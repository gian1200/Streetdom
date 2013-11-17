package com.gian1200.games.streetdom;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MissionActivity extends Activity {

	TextView title, description;
	Mission mission;
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private static final int STREETDOM_MISSION = 3333;
	private Locale locale;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((Application) getApplication()).refreshLanguage();
		locale = getResources().getConfiguration().locale;
		setContentView(R.layout.activity_mission);
		Bundle extras = getIntent().getExtras();
		mission = (Mission) extras.getParcelable(getPackageName() + ".mission");
		title = (TextView) findViewById(R.id.mission_title);
		description = (TextView) findViewById(R.id.mission_description);
		title.setText(mission.name);
		description.setText(mission.description);
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mission, menu);
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

	public void openMap(View v) {
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
			return;
		}
		Intent intent = new Intent(this, MapActivity.class);
		intent.putExtra(getPackageName() + ".mission", mission);
		intent.putExtra(getPackageName() + ".places", mission.places);
		intent.putExtra(getPackageName() + ".currentClue",
				mission.clues[mission.currentClue]);
		startActivityForResult(intent, STREETDOM_MISSION);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case PLAY_SERVICES_RESOLUTION_REQUEST:
			if (resultCode != RESULT_OK) {
				Toast.makeText(this,
						R.string.common_google_play_services_update_text,
						Toast.LENGTH_LONG).show();
			}
			return;
		case STREETDOM_MISSION:
			Log.d("onActivityResult", "STREETDOM_MISSION");
			if (getResources().getInteger(R.integer.result_code_right_place) == resultCode) {
				Log.d("onActivityResult", "result_code_right_place");
				Bundle extras = data.getExtras();
				mission = extras.getParcelable(getPackageName() + ".mission");
				if (mission.isCompleted) {
					finish();
				} else {
					// TODO actualizar la descripcion y su color
				}
				// Lugar encontrado
				// pistas
			} else {
				Log.d("onActivityResult", "not result_code_right_place?");
			}
		}
	}

	public void mostrarPista(View v) {
		ClueFragment clueFragment = new ClueFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable("clue", mission.getCurrentClue());
		clueFragment.setArguments(bundle);
		clueFragment.show(getFragmentManager(), "clue_fragmemt");
	}
}
