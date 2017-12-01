package com.daily.dailyhotel.parcel.analytics;

import android.os.Parcel;
import android.os.Parcelable;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class GourmetPaymentAnalyticsParam implements Parcelable
{
    public String showOriginalPrice;
    public int rankingPosition;
    public int totalListCount;
    public int ratingValue;
    public boolean benefit;
    public int totalPrice;
    public String address;
    public boolean dailyChoice;
    public Province province;
    public String addressAreaName;
    public String categorySub;

    public GourmetPaymentAnalyticsParam()
    {
    }

    public GourmetPaymentAnalyticsParam(Parcel in)
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
        dest.writeString(showOriginalPrice);
        dest.writeInt(rankingPosition);
        dest.writeInt(totalListCount);
        dest.writeInt(ratingValue);
        dest.writeInt(benefit ? 1 : 0);
        dest.writeInt(totalPrice);
        dest.writeString(address);
        dest.writeInt(dailyChoice ? 1 : 0);
        dest.writeParcelable(province, flags);
        dest.writeString(addressAreaName);
        dest.writeString(categorySub);
    }

    void readFromParcel(Parcel in)
    {
        showOriginalPrice = in.readString();
        rankingPosition = in.readInt();
        totalListCount = in.readInt();
        ratingValue = in.readInt();
        benefit = in.readInt() == 1 ? true : false;
        totalPrice = in.readInt();
        address = in.readString();
        dailyChoice = in.readInt() == 1 ? true : false;
        province = in.readParcelable(Province.class.getClassLoader());
        addressAreaName = in.readString();
        categorySub = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public GourmetPaymentAnalyticsParam createFromParcel(Parcel in)
        {
            return new GourmetPaymentAnalyticsParam(in);
        }

        @Override
        public GourmetPaymentAnalyticsParam[] newArray(int size)
        {
            return new GourmetPaymentAnalyticsParam[size];
        }
    };
}
