package com.gian1200.games.streetdom;

import android.os.Parcel;
import android.os.Parcelable;

public class Mission implements Parcelable {
	int id, pointsToEarn, currentClue;
	String name, description;
	Place[] places;
	Clue[] clues;
	boolean isCompleted;
	float progress;

	public Mission(int id, String name, String description, Place[] places,
			Clue[] clues) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.places = places;
		this.clues = clues;
	}

	public static final Parcelable.Creator<Mission> CREATOR = new Parcelable.Creator<Mission>() {
		@Override
		public Mission createFromParcel(Parcel in) {
			return new Mission(in);
		}

		@Override
		public Mission[] newArray(int size) {
			return new Mission[size];
		}
	};

	private Mission(Parcel in) {
		id = in.readInt();
		pointsToEarn = in.readInt();
		currentClue = in.readInt();
		name = in.readString();
		description = in.readString();
		Object[] parcelables = in.readArray(Place.class.getClassLoader());
		places = new Place[parcelables.length];
		System.arraycopy(parcelables, 0, places, 0, parcelables.length);
		parcelables = in.readArray(Clue.class.getClassLoader());
		clues = new Clue[parcelables.length];
		System.arraycopy(parcelables, 0, clues, 0, parcelables.length);
		boolean[] booleans = new boolean[1];
		in.readBooleanArray(booleans);
		isCompleted = booleans[0];
		progress = in.readFloat();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeInt(pointsToEarn);
		dest.writeInt(currentClue);
		dest.writeString(name);
		dest.writeString(description);
		dest.writeArray(places);
		dest.writeArray(clues);
		dest.writeBooleanArray(new boolean[] { isCompleted });
		dest.writeFloat(progress);
	}

	public Clue getCurrentClue() {
		return clues[currentClue];
	}

	public Place getPlace(int placeId) {
		for (Place place : places) {
			if (place.id == placeId) {
				return place;
			}
		}
		return null;
	}

	public boolean isLastClue() {
		return currentClue + 1 == clues.length;
	}

	public int cluesLeft() {
		return clues.length - currentClue;
	}

	public boolean hasClues() {
		return 0 < cluesLeft();
	}

	public void updateProgress() {
		progress = (float) currentClue / clues.length;
		isCompleted = clues.length <= currentClue;
	}
}
