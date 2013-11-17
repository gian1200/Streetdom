package com.gian1200.games.streetdom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.right_place, menu);
		return true;
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
