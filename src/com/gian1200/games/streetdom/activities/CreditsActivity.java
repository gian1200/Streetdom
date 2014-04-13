package com.gian1200.games.streetdom.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.gian1200.games.streetdom.R;

public class CreditsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_credits);
	}

	public void onGiancarloClicked(View v) {
		startActivity(new Intent(Intent.ACTION_VIEW,
				Uri.parse(getString(R.string.giancarlo_link))));
	}

	public void onEdwinClicked(View v) {
		startActivity(new Intent(Intent.ACTION_VIEW,
				Uri.parse(getString(R.string.edwin_link))));
	}
}
