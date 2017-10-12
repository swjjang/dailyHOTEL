package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.DetailImageInformation;
import com.daily.dailyhotel.entity.ImageMap;

public class DetailImageInformationParcel implements Parcelable
{
    private DetailImageInformation mDetailImageInformation;

    public DetailImageInformationParcel(@NonNull DetailImageInformation detailImageInformation)
    {
        if (detailImageInformation == null)
        {
            throw new NullPointerException("detailImageInformation == null");
        }

        mDetailImageInformation = detailImageInformation;
    }

    public DetailImageInformationParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public DetailImageInformation getDetailImageInformation()
    {
        return mDetailImageInformation;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mDetailImageInformation.caption);

        ImageMap imageMap = mDetailImageInformation.getImageMap();

        if (imageMap != null)
        {
            dest.writeString(imageMap.bigUrl);
            dest.writeString(imageMap.mediumUrl);
            dest.writeString(imageMap.smallUrl);
        }
    }

    private void readFromParcel(Parcel in)
    {
        mDetailImageInformation = new DetailImageInformation();

        mDetailImageInformation.caption = in.readString();

        if (in.dataSize() > 1)
        {
            ImageMap imageMap = new ImageMap();
            imageMap.bigUrl = in.readString();
            imageMap.mediumUrl = in.readString();
            imageMap.smallUrl = in.readString();

            mDetailImageInformation.setImageMap(imageMap);
        }
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public DetailImageInformationParcel createFromParcel(Parcel in)
        {
            return new DetailImageInformationParcel(in);
        }

        @Override
        public DetailImageInformationParcel[] newArray(int size)
        {
            return new DetailImageInformationParcel[size];
        }
    };
}
