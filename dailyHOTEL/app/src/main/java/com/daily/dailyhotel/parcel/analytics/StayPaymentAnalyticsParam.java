package com.daily.dailyhotel.parcel.analytics;

import android.os.Parcel;
import android.os.Parcelable;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Province;
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
    public Province province;
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

    public String getAnalyticsProvinceName()
    {
        if (this.province == null)
        {
            return AnalyticsManager.ValueType.EMPTY;
        }

        if (this.province instanceof Area)
        {
            Area area = (Area) this.province;
            return area.getProvince().name;
        }

        return this.province.name;
    }

    public String getAnalyticsDistrictName()
    {
        if (this.province == null)
        {
            return AnalyticsManager.ValueType.EMPTY;
        }

        if (this.province instanceof Area)
        {
            Area area = (Area) this.province;
            String provinceName = area.getProvince().name;
            return DailyTextUtils.isTextEmpty(provinceName) == false ? area.name : AnalyticsManager.ValueType.EMPTY;
        }

        return AnalyticsManager.ValueType.ALL_LOCALE_KR;
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
        dest.writeParcelable(province, flags);
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
        province = in.readParcelable(Province.class.getClassLoader());
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
