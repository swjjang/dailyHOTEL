package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable
{
	public int index;
	public String imageUrl;

	public Event()
	{
	}

	public Event(Parcel in)
	{
		readFromParcel(in);
	}

	public Event(int index, String imageUrl)
	{
		this.index = index;
		this.imageUrl = imageUrl;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(index);
		dest.writeString(imageUrl);
	}

	private void readFromParcel(Parcel in)
	{
		index = in.readInt();
		imageUrl = in.readString();
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
	{
		public Event createFromParcel(Parcel in)
		{
			return new Event(in);
		}

		@Override
		public Event[] newArray(int size)
		{
			return new Event[size];
		}

	};

	@Override
	public int describeContents()
	{
		return 0;
	}
}
