package com.gian1200.games.streetdom.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.gian1200.games.streetdom.R;
import com.gian1200.util.views.CardView;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class LicensesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_licenses);
		final CardView card = (CardView) findViewById(R.id.license_google_play_services);
		// card.contentTextView.setText(GooglePlayServicesUtil
		// .getOpenSourceSoftwareLicenseInfo(this));
		// card.contentTextView
		// .setText("sldkfj hsdflkhsdfklj sadlfj hsdfljasd hflkjsa hfljsakd fhaslkj hsdj fhsladkfj hsadlkfjhsadlkfjhsdlkjfhlkdjsflkasjdfhasl jfalskdjf haslkdjf hsalkjd fhlksaj fhlkajs fhlkjasfhas dlfjsdflkjhsaldkfjhslakdjfaslkjdflkjasdfhlakjsfhlkajsdfhsl dfjsadlfhsdlkjfsdalkfjsdljkflkjasdfkljasfkljasdhfkljsadhfas dfjasdlfkjsadlfkjasdlfjkhaslkjlsakdjflksadjflaksjd fljkas fdsa djkflsdlkfhlskdjfhlsakdjf hlaksj fhlkasj fhlkasj fhlkasj fhas dlfjh sadlkfj hsdfljhsadlfkjsadlfkjhsaflkjasdlfkjslkjdflkjsa fh+çs fhasdfkhasdlkfjhasdlkfjhasldkjfhlsadkj flasjd fhlj sadflkj");
		card.setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(View v) {
				card.expandCollapseContent();
			}

		});
	}
}
