package com.gian1200.games.streetdom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((Application) getApplication()).refreshLanguage();
		setContentView(R.layout.activity_login);
	}

	public void singInGoogle(View v) {

	}

	public void noSignIn(View v) {
		startActivity(new Intent(this, MainActivity.class));
	}
}
