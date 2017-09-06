package com.daily.dailyhotel.parcel.analytics;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.network.model.HomePlace;
import com.twoheart.dailyhotel.network.model.Prices;
import com.twoheart.dailyhotel.network.model.RecommendationGourmet;
import com.twoheart.dailyhotel.network.model.RecommendationStay;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

/**
 * Created by android_sam on 2017. 6. 23..
 */

public class GourmetDetailAnalyticsParam implements Parcelable
{
    public static final int SKIP_CHECK_DISCOUNT_PRICE_VALUE = Integer.MIN_VALUE;

    private String addressAreaName; // addressSummary 의 split 이름 stay.addressSummary.split("\\||l|ㅣ|I")  index : 0;
    public int price; // 정가
    public int discountPrice; // 표시가
    public String showOriginalPriceYn = "N"; // stay.price <= 0 || stay.price <= stay.discountPrice ? "N" : "Y"
    public int entryPosition = -1;
    public int totalListCount = -1;
    public boolean isDailyChoice;

    private Province province;

    public GourmetDetailAnalyticsParam()
    {
        super();
    }

    public GourmetDetailAnalyticsParam(Parcel in)
    {
        readFromParcel(in);
    }

    public void setProvince(Province province)
    {
        if (province == null)
        {
            this.province = null;
            return;
        }

        this.province = province;
    }

    public Province getProvince()
    {
        return province;
    }

    public String getProvinceName()
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

    public String getDistrictName()
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

    public String getAddressAreaName()
    {
        return addressAreaName;
    }

    private String getAddressAreaName(String addressSummary)
    {
        if (DailyTextUtils.isTextEmpty(addressSummary) == true)
        {
            return null;
        }

        String[] addressArray = addressSummary.split("\\||l|ㅣ|I");
        return addressArray[0].trim();
    }

    public void setTotalListCount(int listCount)
    {
        totalListCount = listCount;
    }

    private String getShowOriginalPriceYn(int originPrice, int discountPrice)
    {
        return originPrice <= 0 || originPrice <= discountPrice ? "N" : "Y";
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(addressAreaName);
        dest.writeInt(price);
        dest.writeInt(discountPrice);
        dest.writeString(showOriginalPriceYn);
        dest.writeInt(entryPosition);
        dest.writeInt(totalListCount);
        dest.writeInt(isDailyChoice == true ? 1 : 0);
    }

    protected void readFromParcel(Parcel in)
    {
        addressAreaName = in.readString();
        price = in.readInt();
        discountPrice = in.readInt();
        showOriginalPriceYn = in.readString();
        entryPosition = in.readInt();
        totalListCount = in.readInt();
        isDailyChoice = in.readInt() == 1 ? true : false;
    }

    public static final Creator CREATOR = new Creator()
    {
        @Override
        public GourmetDetailAnalyticsParam createFromParcel(Parcel source)
        {
            return new GourmetDetailAnalyticsParam(source);
        }

        @Override
        public GourmetDetailAnalyticsParam[] newArray(int size)
        {
            return new GourmetDetailAnalyticsParam[size];
        }
    };
}
