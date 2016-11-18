package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DetailInformation implements Parcelable
{
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public DetailInformation createFromParcel(Parcel in)
        {
            return new DetailInformation(in);
        }

        @Override
        public DetailInformation[] newArray(int size)
        {
            return new DetailInformation[size];
        }

    };
    public String title;
    private List<String> mContentsList;

    public DetailInformation(String title, List<String> contentsList)
    {
        this.title = title;
        mContentsList = contentsList;
    }

    public DetailInformation(Parcel in)
    {
        readFromParcel(in);
    }

    public DetailInformation(JSONObject jsonObject) throws Exception
    {
        Iterator<String> iterator = jsonObject.keys();
        if (iterator.hasNext() == true)
        {
            title = iterator.next();

            JSONArray jsonArray = jsonObject.getJSONArray(title);
            int length = jsonArray.length();

            mContentsList = new ArrayList<>(length);

            for (int i = 0; i < length; i++)
            {
                mContentsList.add(jsonArray.getString(i));
            }
        }
    }

    public List<String> getContentsList()
    {
        return mContentsList;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(title);
        dest.writeList(mContentsList);
    }

    protected void readFromParcel(Parcel in)
    {
        mContentsList = new ArrayList<>();

        title = in.readString();
        in.readList(mContentsList, List.class.getClassLoader());
    }

    @Override
    public int describeContents()
    {
        return 0;
    }
}
