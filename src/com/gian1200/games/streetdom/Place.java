package com.gian1200.games.streetdom;

import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable {
	public int id;
	public String name, description, imageURI;
	public double latitude, longitude;
	public boolean visited;

	public Place(int id, String name, String description, String imageURI,
			double latitude, double longitude) {
		this(id, name, description, imageURI, latitude, longitude, false);
	}

	public Place(int id, String name, String description, String imageURI,
			double latitude, double longitude, boolean visited) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.imageURI = imageURI;
		this.latitude = latitude;
		this.longitude = longitude;
		this.visited = visited;
	}

	public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {
		@Override
		public Place createFromParcel(Parcel in) {
			return new Place(in);
		}

		@Override
		public Place[] newArray(int size) {
			return new Place[size];
		}
	};

	private Place(Parcel in) {
		id = in.readInt();
		name = in.readString();
		description = in.readString();
		imageURI = in.readString();
		latitude = in.readDouble();
		longitude = in.readDouble();
		boolean[] booleans = new boolean[1];
		in.readBooleanArray(booleans);
		visited = booleans[0];
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeString(description);
		dest.writeString(imageURI);
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		dest.writeBooleanArray(new boolean[] { visited });
	}

	public void visit() {
		visited = true;
	}

	public void setPosition(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
}
