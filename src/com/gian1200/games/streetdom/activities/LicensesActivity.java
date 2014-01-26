package com.gian1200.games.streetdom.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.gian1200.games.streetdom.R;
import com.gian1200.util.StringUtil;
import com.gian1200.util.views.CardView;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class LicensesActivity extends Activity {

	private CardView googlePlayServicesCard, baseGameUtilCard, androidUtilCard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_licenses);
		googlePlayServicesCard = (CardView) findViewById(R.id.license_google_play_services);
		baseGameUtilCard = (CardView) findViewById(R.id.license_base_game_utils);
		androidUtilCard = (CardView) findViewById(R.id.license_android_util);
		googlePlayServicesCard.contentTextView.setText(GooglePlayServicesUtil
				.getOpenSourceSoftwareLicenseInfo(this));
		baseGameUtilCard.contentTextView.setText(StringUtil
				.getLicenseApache2_0(this, "2013", "Google Inc."));
		androidUtilCard.contentTextView.setText(StringUtil
				.getAndroidUtilLicense(this));
		googlePlayServicesCard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				googlePlayServicesCard.expandorCollapseContent();
			}

		});
		baseGameUtilCard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				baseGameUtilCard.expandorCollapseContent();
			}

		});
		androidUtilCard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				androidUtilCard.expandorCollapseContent();
			}

		});
	}
}
