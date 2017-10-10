package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.ImageMap;
import com.daily.dailyhotel.entity.StayOutboundDetailImage;

public class StayOutboundDetailImageParcel implements Parcelable
{
    private StayOutboundDetailImage mStayOutboundDetailImage;

    public StayOutboundDetailImageParcel(@NonNull StayOutboundDetailImage stayOutboundDetailImage)
    {
        if (stayOutboundDetailImage == null)
        {
            throw new NullPointerException("stayOutboundDetailImage == null");
        }

        mStayOutboundDetailImage = stayOutboundDetailImage;
    }

    public StayOutboundDetailImageParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public StayOutboundDetailImage getStayOutboundDetailImage()
    {
        return mStayOutboundDetailImage;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mStayOutboundDetailImage.caption);

        ImageMap imageMap = mStayOutboundDetailImage.getImageMap();

        if (imageMap != null)
        {
            dest.writeString(imageMap.bigUrl);
            dest.writeString(imageMap.mediumUrl);
            dest.writeString(imageMap.smallUrl);
        }
    }

    private void readFromParcel(Parcel in)
    {
        mStayOutboundDetailImage = new StayOutboundDetailImage();

        mStayOutboundDetailImage.caption = in.readString();

        if (in.dataSize() > 1)
        {
            ImageMap imageMap = new ImageMap();
            imageMap.bigUrl = in.readString();
            imageMap.mediumUrl = in.readString();
            imageMap.smallUrl = in.readString();

            mStayOutboundDetailImage.setImageMap(imageMap);
        }
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public StayOutboundDetailImageParcel createFromParcel(Parcel in)
        {
            return new StayOutboundDetailImageParcel(in);
        }

        @Override
        public StayOutboundDetailImageParcel[] newArray(int size)
        {
            return new StayOutboundDetailImageParcel[size];
        }

    };
}
