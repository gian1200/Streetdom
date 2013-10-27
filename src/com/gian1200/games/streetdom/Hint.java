package com.gian1200.games.streetdom;

import android.os.Parcel;
import android.os.Parcelable;

public class Hint implements Parcelable {
	String text;

	// audio, image, video

	public Hint(String text) {
		this.text = text;
	}

	// TODO un constructor por cada tipo de pista

	public static final Parcelable.Creator<Hint> CREATOR = new Parcelable.Creator<Hint>() {
		@Override
		public Hint createFromParcel(Parcel in) {
			return new Hint(in);
		}

		@Override
		public Hint[] newArray(int size) {
			return new Hint[size];
		}
	};

	protected Hint(Parcel in) {
		text = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(text);
	}

}
