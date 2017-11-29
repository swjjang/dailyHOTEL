package com.daily.dailyhotel.parcel.analytics;

import android.os.Parcel;
import android.os.Parcelable;

import com.daily.dailyhotel.entity.StayTown;
import com.daily.dailyhotel.parcel.StayTownParcel;
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
    private StayTown mTown;
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

    public void setTown(StayTown town)
    {
        mTown = town;
    }

    public StayTown getTown()
    {
        return mTown;
    }

    public String getAnalyticsProvinceName()
    {
        if (mTown == null)
        {
            return AnalyticsManager.ValueType.EMPTY;
        }

        return mTown.getDistrict().name;
    }

    public String getAnalyticsDistrictName()
    {
        if (mTown == null)
        {
            return AnalyticsManager.ValueType.EMPTY;
        }

        return mTown.index == StayTown.ALL ? AnalyticsManager.ValueType.ALL_LOCALE_KR : mTown.name;
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

        if (mTown == null)
        {
            dest.writeParcelable(null, flags);
        } else
        {
            dest.writeParcelable(new StayTownParcel(mTown), flags);
        }

        dest.writeParcelable(new StayTownParcel(mTown), flags);
        dest.writeString(addressAreaName);
        dest.writeString(grade.name());
        dest.writeInt(provideRewardSticker ? 1 : 0);
    }

    void readFromParcel(Parcel in)
    {
        nrd = in.readInt() == 1 ? true : false;
        showOriginalPrice = in.readString();
        rankingPosition = in.readInt();
        totalListCount = in.readInt();
        ratingValue = in.readInt();
        benefit = in.readInt() == 1 ? true : false;
        averageDiscount = in.readInt();
        address = in.readString();
        dailyChoice = in.readInt() == 1 ? true : false;

        StayTownParcel stayTownParcel = in.readParcelable(StayTownParcel.class.getClassLoader());

        if (stayTownParcel != null)
        {
            mTown = stayTownParcel.getStayTown();
        }

        addressAreaName = in.readString();

        try
        {
            grade = Stay.Grade.valueOf(in.readString());
        } catch (Exception e)
        {
            grade = Stay.Grade.etc;
        }

        provideRewardSticker = in.readInt() == 1 ? true : false;
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
