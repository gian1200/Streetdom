package com.gian1200.games.streetdom.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.gian1200.games.streetdom.Mission;
import com.gian1200.games.streetdom.R;

public class RightPlaceActivity extends Activity {
	Mission mission;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_right_place);
		Bundle extras = getIntent().getExtras();
		mission = extras.getParcelable(getPackageName() + ".mission");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (getResources().getInteger(R.integer.result_code_erase_data) == resultCode) {
			setResult(resultCode);
			finish();
			return;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.right_place, menu);
		return true;
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
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void keepPlaying(View v) {
		keepPlaying();
	}

	@Override
	public void onBackPressed() {
		keepPlaying();
	}

	private void keepPlaying() {
		Intent data = new Intent();
		data.putExtra(getPackageName() + ".mission", mission);
		setResult(getResources().getInteger(R.integer.result_code_right_place),
				data);
		finish();
	}
}
