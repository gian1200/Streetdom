package com.gian1200.games.streetdom;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class PlaceActivity extends Activity {
	Mission mission;
	Place place;
	TextView title, description;
	// Location currentLocation;
	LatLng currentLocation;
	private Locale locale;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((Application) getApplication()).refreshLanguage();
		locale = getResources().getConfiguration().locale;
		setContentView(R.layout.activity_place);
		Bundle extras = getIntent().getExtras();
		// currentLocation = (Location) extras.getParcelable(getPackageName()
		// + ".currentLocation");
		currentLocation = (LatLng) extras.getParcelable(getPackageName()
				+ ".currentLocation");
		mission = extras.getParcelable(getPackageName() + ".mission");
		place = extras.getParcelable(getPackageName() + ".place");
		title = (TextView) findViewById(R.id.place_title);
		description = (TextView) findViewById(R.id.place_description);
		title.setText(place.name);
		description.setText(place.description);
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
		getMenuInflater().inflate(R.menu.place, menu);
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

	public void checkPlace(View v) {
		float[] results = new float[1];
		// Location.distanceBetween(currentLocation.getLatitude(),
		// currentLocation.getLongitude(), place.latitude,
		// place.longitude, results);
		Location.distanceBetween(currentLocation.latitude,
				currentLocation.longitude, place.latitude, place.longitude,
				results);
		if (results[0] <= 50) {
			place.visited = true;
			mission.getPlace(place.id).visit();
			if (place.id == mission.getCurrentClue().placeId) {
				mission.currentClue++;
				mission.updateProgress();
				((Application) getApplication()).updateMission(mission);
				((Application) getApplication()).saveData();
				if (mission.hasClues()) {
					Intent intent = new Intent(this, RightPlaceActivity.class);
					intent.putExtra(getPackageName() + ".mission", mission);
					startActivityForResult(
							intent,
							getResources().getInteger(
									R.integer.request_code_right_place));
				} else {
					Toast.makeText(this, "Last clue. you did it",
							Toast.LENGTH_SHORT).show();
					Intent data = new Intent();
					data.putExtra(getPackageName() + ".mission", mission);
					setResult(
							getResources().getInteger(
									R.integer.result_code_mission_completed),
							data);
					// TODO go to winning mission screen
					finish();
				}
			} else {
				((Application) getApplication()).updateMission(mission);
				((Application) getApplication()).saveData();
				Toast.makeText(this, "NO OK", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "No estás lo suficientemente cerca",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (getResources().getInteger(R.integer.request_code_right_place) == requestCode) {
			if (getResources().getInteger(R.integer.result_code_right_place) == resultCode) {
				setResult(resultCode, data);
				finish();
			}
		}
	}

	@Override
	public void onBackPressed() {
		Intent data = new Intent();
		data.putExtra(getPackageName() + ".mission", mission);
		setResult(RESULT_OK, data);
		finish();
	}
}
