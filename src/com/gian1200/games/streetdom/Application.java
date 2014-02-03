package com.gian1200.games.streetdom;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;

import com.gian1200.util.ApplicationUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class Application extends ApplicationUtil {
	User user;
	ArrayList<Mission> missions;
	public ArrayList<Mission> buyableMissions;
	public ArrayList<Mission> incompletedMissions;
	public ArrayList<Mission> completedMissions;
	private int clueCoinsPrice, cluePointsPrice, helpCoinsPrice,
			helpPointsPrice;
	private Calendar lastSinc;
	public Locale[] languages = new Locale[] { null, Locale.ENGLISH,
			new Locale("es") };

	@Override
	public void onCreate() {
		super.onCreate();
		languages[0] = Locale.getDefault();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
	}

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
		fillMisions();
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

	@Override
	protected void removeData(Editor editor) {
		// TODO Auto-generated method stub
	}

	public boolean refreshLanguage() {
		return refreshLanguage(PreferenceManager
				.getDefaultSharedPreferences(this));
	}

	boolean refreshLanguage(SharedPreferences sharedPreferences) {
		if (sharedPreferences.contains("language")) {
			int languageIndex = Integer.parseInt(sharedPreferences.getString(
					"language", "0"));
			if (!getResources().getConfiguration().locale
					.equals(languages[languageIndex])) {
				Configuration config = new Configuration();
				config.locale = languages[languageIndex];
				getResources().updateConfiguration(config, null);
				Log.d("Application.refreshLanguage", "se ha cambiado");
				return true;
			}
		}
		return false;
	}

	private Mission getMisionEdwin(int id, String name, String description,
			String imageURI) {
		// -11.9428475, -76.9757984
		Place[] places = getTestPlaces(-11.9428475, -76.9757984);
		Clue[] clues = getTestClues();
		return new Mission(id, name, description, imageURI, places, clues);
	}

	private Place[] getTestPlaces() {
		return getTestPlaces(-12.09281884, -76.99798929);
	}

	private Clue[] getTestClues() {
		return new Clue[] {
				new Clue(
						"Accused",
						"Source of inspiration. Peruvian Writer. 1969. Nobel Prize.",
						1),
				new Clue(
						"Trap",
						"Source of inspiration. Peruvian Writer. 1969. Nobel Prize.",
						2),
				new Clue(
						"Secret",
						"Source of inspiration. Peruvian Writer. 1969. Nobel Prize.",
						3) };
	}

	private Place[] getTestPlaces(double latitude, double longitude) {
		// -12.09281884,-76.99798929
		String imageURI = "http://cdn.screenrant.com/wp-content/uploads/A-Stormtrooper-lost-in-the-desert.-2560x1080-Imgur.jpg";
		return new Place[] {
				new Place(1, "Palacio de Justicia", "Descripción del lugar 1",
						imageURI, latitude + 0.000270698,
						longitude + 0.000235531),
				new Place(
						2,
						"Bar La Catedral",
						"A very popular bar in the 50s where the famous peruvian writer, Mario Vargas Llosa, got inspiration to write his famous book Conversaciones en la Catedral. In this specific place, the guardians hide out for a few days.",
						imageURI, latitude + 0.000201525,
						longitude - 0.000270065),
				new Place(3, "Hotel Sheraton", "Descripción del lugar 3",
						imageURI, latitude - 0.000202697,
						longitude - 0.000206027),
				new Place(4, "Casa de los guardianes",
						"Descripción del lugar 4", imageURI,
						latitude - 0.000269525, longitude + 0.000240561) };
	}

	private void fillMisions() {
		// -12.09281884,-76.99798929
		Place[] places = getTestPlaces();
		Clue[] clues = getTestClues();
		String imageURI = "http://cdn.screenrant.com/wp-content/uploads/A-Stormtrooper-lost-in-the-desert.-2560x1080-Imgur.jpg";
		missions.add(new Mission(
				1,
				"Miscarriage of Justice",
				"There was a place in our city where the most dangerous criminals were locked. There was a group of people: \"The Guardians\" who were aware of the most important secret of the world. However, they were accused for a crime they didn't commit. Everyone was after them because there was a huge reward. They realised that there was no way out of it and decided to hide the secret. While being chased, they left clues along the path, visible only to those who know where to look. One that leads to another. The last one was the key to find the secret. The most omportant secret on Earth.",
				imageURI, places, clues));
		missions.add(new Mission(2, "Temple of the Sun",
				"Descripción de la misión", imageURI, places, clues));
		missions.add(new Mission(3, "Lost Traveler",
				"Descripcion de la misión Lost Traveler", imageURI, places,
				clues));
		missions.add(new Mission(4, "The path of no return",
				"Descripcion de la misión The path of no return", imageURI,
				places, clues));
		missions.add(new Mission(5, "Forbidden Kingdom",
				"Descripcion de la misión Forbidden Kingdom", imageURI, places,
				clues));
		missions.add(new Mission(6, "The collector",
				"Descripcion de la misión The collector", imageURI, places,
				clues));
		missions.add(new Mission(
				7,
				"The collector de los tesoros más increibles que puedan existir",
				"Descripcion de la misión The collector de los tesoros más increibles que puedan existir",
				imageURI, places, clues));
		missions.add(getMisionEdwin(8, "Los monstruitos diseñadores",
				"Descripción de la misión Los monstruitos diseñadores",
				imageURI));
	}

	public void updateMission(final Mission mission) {
		for (Mission mission2 : missions) {
			if (mission2.id == mission.id) {
				mission2.currentClue = mission.currentClue;
				mission2.progress = mission.progress;
				mission2.isCompleted = mission.isCompleted;
				if (mission2.isCompleted) {
					incompletedMissions.remove(mission2);
					if (!completedMissions.contains(mission2)) {
						completedMissions.add(mission2);
					}
				}
				break;
			}
		}
		// se debería llamar aquí o solo cuando sea necesario?
		// saveData();
	}

}
