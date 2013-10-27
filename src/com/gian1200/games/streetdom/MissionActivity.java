package com.gian1200.games.streetdom;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MissionActivity extends Activity {

	TextView title, description;
	Mission mission;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mission);
		Bundle extras = getIntent().getExtras();
		mission = (Mission) extras.getParcelable(getPackageName() + ".mission");
		title = (TextView) findViewById(R.id.mission_title);
		description = (TextView) findViewById(R.id.mission_description);
		title.setText(mission.name);
		description.setText(mission.description);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mission, menu);
		return true;
	}

	public void openMap(View v) {
		Intent intent = new Intent(this, MapActivity.class);
		intent.putExtra(getPackageName() + ".places", mission.places);
		intent.putExtra(getPackageName() + ".currentClue",
				mission.clues[mission.currentClue]);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (data != null) {
				if (!mission.isCompleted) {
					Bundle extras = data.getExtras();
					if (getResources().getInteger(
							R.integer.result_code_right_place) == extras
							.getInt(getPackageName() + ".resultCode")) {
						mission.currentClue++;
						mission.progress = (float) mission.currentClue
								/ mission.clues.length;
						mission.isCompleted = mission.clues.length <= mission.currentClue;
						ArrayList<Mission> missions = ((Application) getApplication()).missions;
						for (Mission mission : missions) {
							if (mission.id == this.mission.id) {
								mission.currentClue = this.mission.currentClue;
								mission.progress = this.mission.progress;
								mission.isCompleted = this.mission.isCompleted;
								if (mission.isCompleted) {
									((Application) getApplication()).incompletedMissions
											.remove(mission);
									if (!((Application) getApplication()).completedMissions
											.contains(mission)) {
										((Application) getApplication()).completedMissions
												.add(mission);
									}
								}
								break;
							}
						}
						((Application) getApplication()).saveData();
					}
				}
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// mission.progress;
	}
}
