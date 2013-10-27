package com.gian1200.games.streetdom;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.gian1200.util.ApplicationUtil;

public class Application extends ApplicationUtil {
	User user;
	ArrayList<Mission> missions, buyableMissions, incompletedMissions,
			completedMissions;
	int clueCoinsPrice, cluePointsPrice, helpCoinsPrice, helpPointsPrice;
	Calendar lastSinc;

	@Override
	protected void loadData(SharedPreferences sharedPreferences) {
		clueCoinsPrice = sharedPreferences.getInt("clueCoinsPrice", 0);
		cluePointsPrice = sharedPreferences.getInt("cluePointsPrice", 0);
		helpCoinsPrice = sharedPreferences.getInt("helpCoinsPrice", 0);
		helpPointsPrice = sharedPreferences.getInt("helpPointsPrice", 0);
		lastSinc = Calendar.getInstance();
		lastSinc.setTimeInMillis(sharedPreferences.getLong("lastSyncMillis",
				lastSinc.getTimeInMillis()));
		// TODO leer user de SharedPreferences
		user = new User();
		missions = new ArrayList<Mission>();
		buyableMissions = new ArrayList<Mission>();
		incompletedMissions = new ArrayList<Mission>();
		completedMissions = new ArrayList<Mission>();
		missions.add(returnNuewMission(1));
		missions.add(returnNuewMission(2));
		missions.add(returnNuewMission(3));
		missions.add(returnNuewMission(4));
		missions.add(returnNuewMission(5));
		for (Mission mission : missions) {
			if (mission.isCompleted) {
				completedMissions.add(mission);
			} else {
				incompletedMissions.add(mission);
			}
		}
	}

	@Override
	protected void saveData(Editor editor) {
		editor.putInt("clueCoinsPrice", clueCoinsPrice);
		editor.putInt("cluePointsPrice", cluePointsPrice);
		editor.putInt("helpCoinsPrice", helpCoinsPrice);
		editor.putInt("helpPointsPrice", helpPointsPrice);
		editor.putLong("lastSyncMillis", lastSinc.getTimeInMillis());
		// TODO grabar user en SharedPreferences
	}

	private Mission returnNuewMission(int id) {
		Place[] places = new Place[] {
				new Place(1, "Lugar 1", "Descripción del lugar 1",
						-12.092548137298373, -76.99775375425816),
				new Place(2, "Lugar 2", "Descripción del lugar 2",
						-12.09261731079396, -76.99825935065746),
				new Place(3, "Lugar 3", "Descripción del lugar 3",
						-12.09302153280611, -76.99819531291723),
				new Place(4, "Lugar 4", "Descripción del lugar 4",
						-12.093088360535287, -76.99774872511625) };
		Clue[] clues = new Clue[places.length];
		for (int i = 0; i < places.length; i++) {
			clues[i] = new Clue("Esta es la descripción de la pista",
					places[i].id);
		}
		Mission mission = new Mission(id, "Nombre de la Misión",
				"Descripción de la misión", places, clues);
		return mission;

	}
}
