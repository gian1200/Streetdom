package com.gian1200.games.streetdom;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class ClueActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clue);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.clue, menu);
		return true;
	}

}
