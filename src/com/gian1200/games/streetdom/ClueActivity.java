package com.gian1200.games.streetdom;

import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class ClueActivity extends Activity {

	private Locale locale;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((Application) getApplication()).refreshLanguage();
		locale = getResources().getConfiguration().locale;
		setContentView(R.layout.activity_clue);
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
		getMenuInflater().inflate(R.menu.clue, menu);
		return true;
	}

}
