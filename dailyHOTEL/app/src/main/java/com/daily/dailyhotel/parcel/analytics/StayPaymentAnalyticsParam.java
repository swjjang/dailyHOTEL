package com.daily.dailyhotel.parcel.analytics;

import android.os.Parcel;
import android.os.Parcelable;

import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.AreaElement;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayRegion;
import com.daily.dailyhotel.parcel.StayRegionParcel;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class StayPaymentAnalyticsParam implements Parcelable
{
    public boolean nrd;
    public String showOriginalPrice;
    public int rankingPosition;
    public int totalListCount;
    public int ratingValue;
    public boolean benefit;
    public int averageDiscount; // 평균 가격
    public String address;
    public boolean dailyChoice;
    private StayRegion mRegion;
    public String addressAreaName;
    public Stay.Grade grade;
    public boolean provideRewardSticker;

    public StayPaymentAnalyticsParam()
    {
    }

    public StayPaymentAnalyticsParam(Parcel in)
    {
        readFromParcel(in);
    }

    public void setRegion(StayRegion region)
    {
        mRegion = region;
    }

    public StayRegion getRegion()
    {
        return mRegion;
    }

    public String getAnalyticsProvinceName()
    {
        if (mRegion == null)
        {
            return AnalyticsManager.ValueType.EMPTY;
        }

        return mRegion.getAreaGroupName();
    }

    public String getAnalyticsDistrictName()
    {
        if (mRegion == null)
        {
            return AnalyticsManager.ValueType.EMPTY;
        }

        AreaElement areaElement = mRegion.getAreaElement();

        return areaElement == null || areaElement.index == StayArea.ALL ? AnalyticsManager.ValueType.ALL_LOCALE_KR : areaElement.name;
    }

    public String getAnalyticsAddressAreaName()
    {
        return addressAreaName;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(nrd ? 1 : 0);
        dest.writeString(showOriginalPrice);
        dest.writeInt(rankingPosition);
        dest.writeInt(totalListCount);
        dest.writeInt(ratingValue);
        dest.writeInt(benefit ? 1 : 0);
        dest.writeInt(averageDiscount);
        dest.writeString(address);
        dest.writeInt(dailyChoice ? 1 : 0);

        if (mRegion == null)
        {
            dest.writeParcelable(null, flags);
        } else
        {
            dest.writeParcelable(new StayRegionParcel(mRegion), flags);
        }

        dest.writeString(addressAreaName);
        dest.writeString(grade.name());
        dest.writeInt(provideRewardSticker ? 1 : 0);
    }

    void readFromParcel(Parcel in)
    {
        nrd = in.readInt() == 1;
        showOriginalPrice = in.readString();
        rankingPosition = in.readInt();
        totalListCount = in.readInt();
        ratingValue = in.readInt();
        benefit = in.readInt() == 1;
        averageDiscount = in.readInt();
        address = in.readString();
        dailyChoice = in.readInt() == 1;

        StayRegionParcel stayRegionParcel = in.readParcelable(StayRegionParcel.class.getClassLoader());

        if (stayRegionParcel != null)
        {
            mRegion = stayRegionParcel.getRegion();
        }

        addressAreaName = in.readString();

        try
        {
            grade = Stay.Grade.valueOf(in.readString());
        } catch (Exception e)
        {
            grade = Stay.Grade.etc;
        }

        provideRewardSticker = in.readInt() == 1;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public StayPaymentAnalyticsParam createFromParcel(Parcel in)
        {
            return new StayPaymentAnalyticsParam(in);
        }

        @Override
        public StayPaymentAnalyticsParam[] newArray(int size)
        {
            return new StayPaymentAnalyticsParam[size];
        }
    };
}
