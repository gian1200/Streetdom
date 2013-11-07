package com.gian1200.games.streetdom;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PlaceActivity extends Activity {
	Place place;
	Clue clue;
	TextView title, description;
	Location currentLocation;
	private Locale locale;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((Application) getApplication()).refreshLanguage();
		locale = getResources().getConfiguration().locale;
		setContentView(R.layout.activity_place);
		Bundle extras = getIntent().getExtras();
		currentLocation = (Location) extras.getParcelable(getPackageName()
				+ ".currentLocation");
		clue = (Clue) extras.getParcelable(getPackageName() + ".currentClue");
		place = (Place) extras.getParcelable(getPackageName() + ".place");
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

	public void checkPlace(View v) {
		float[] results = new float[1];
		Location.distanceBetween(currentLocation.getLatitude(),
				currentLocation.getLongitude(), place.latitude,
				place.longitude, results);
		if (results[0] <= 50) {
			if (place.id == clue.placeId) {
				Intent data = new Intent();
				data.putExtra(getPackageName() + ".resultCode", getResources()
						.getInteger(R.integer.result_code_right_place));
				setResult(RESULT_OK, data);
				finish();
				Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "NO OK", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "No estás lo suficientemente cerca",
					Toast.LENGTH_SHORT).show();

		}
	}
}
