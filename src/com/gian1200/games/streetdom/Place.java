package com.gian1200.games.streetdom;

import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable {
	int id;
	String name, description;
	double latitude, longitude;

	public Place(int id, String name, String description, double latitude,
			double longitude) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.latitude = latitude;
		this.longitude = longitude;
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
		latitude = in.readDouble();
		longitude = in.readDouble();
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
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
	}

}
