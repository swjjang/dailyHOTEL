package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by android_sam on 2018. 2. 20..
 */
public class ListDialogItemParcel<T extends Parcelable> implements Parcelable
{
    public String displayName;
    private T mItem;

    public ListDialogItemParcel(String displayName, T item)
    {
        this.displayName = displayName;
        this.mItem = item;
    }

    public T getItem()
    {
        return mItem;
    }

    public ListDialogItemParcel(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(displayName);
        dest.writeParcelable(mItem, flags);
    }

    private void readFromParcel(Parcel in)
    {
        displayName = in.readString();
        mItem = in.readParcelable(getClass().getClassLoader());
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        @Override
        public ListDialogItemParcel createFromParcel(Parcel in)
        {
            return new ListDialogItemParcel(in);
        }

        @Override
        public ListDialogItemParcel[] newArray(int size)
        {
            return new ListDialogItemParcel[size];
        }
    };
}
