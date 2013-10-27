package com.gian1200.games.streetdom;

import android.os.Parcel;
import android.os.Parcelable;

public class Clue extends Hint {

	int placeId;

	public static final Parcelable.Creator<Clue> CREATOR = new Parcelable.Creator<Clue>() {
		@Override
		public Clue createFromParcel(Parcel in) {
			return new Clue(in);
		}

		@Override
		public Clue[] newArray(int size) {
			return new Clue[size];
		}
	};

	public Clue(String text, int placeId) {
		super(text);
		this.placeId = placeId;
	}

	private Clue(Parcel in) {
		super(in);
		placeId = in.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(placeId);
	}
}
