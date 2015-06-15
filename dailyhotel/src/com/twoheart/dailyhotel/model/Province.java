package com.twoheart.dailyhotel.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Province implements Parcelable
{
	public int index;
	public String name;
	public int sequence;

	public boolean isSelected;

	public Province()
	{
		super();
	}

	public Province(Parcel in)
	{
		readFromParcel(in);
	}

	public Province(JSONObject jsonObject) throws JSONException
	{
		index = jsonObject.getInt("idx");
		name = jsonObject.getString("name");
		sequence = jsonObject.getInt("seq");
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(index);
		dest.writeString(name);
		dest.writeInt(sequence);
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	protected void readFromParcel(Parcel in)
	{
		this.index = in.readInt();
		this.name = in.readString();
		this.sequence = in.readInt();
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
	{
		public Province createFromParcel(Parcel in)
		{
			return new Province(in);
		}

		@Override
		public Province[] newArray(int size)
		{
			return new Province[size];
		}

	};
}
