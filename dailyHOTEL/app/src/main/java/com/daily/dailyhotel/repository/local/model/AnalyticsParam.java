package com.daily.dailyhotel.repository.local.model;

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
import com.twoheart.dailyhotel.network.model.RecommendationStay;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

/**
 * Created by android_sam on 2017. 6. 23..
 */

public class AnalyticsParam implements Parcelable
{
    public static final int SKIP_CHECK_DISCOUNT_PRICE_VALUE = Integer.MIN_VALUE;

    public int placeIndex;
    public String placeName;
    public String provinceName;
    public String areaName; // province 의 area 명
    public String addressAreaName; // addressSummary 의 split 이름 stay.addressSummary.split("\\||l|ㅣ|I")  index : 0;
    public int price; // 정가
    public int discountPrice; // 표시가
    public String showOriginalPriceYn; // stay.price <= 0 || stay.price <= stay.discountPrice ? "N" : "Y"
    public int listPosition = -1;
    public int totalListCount = -1;
    public boolean isDailyChoice;
    public String gradeName;

    public AnalyticsParam()
    {
        super();
    }

    public AnalyticsParam(Parcel in)
    {
        readFromParcel(in);
    }

    public void setParam(Context context, Stay stay)
    {
        placeIndex = stay.index;
        placeName = stay.name;

        addressAreaName = getAddressAreaName(stay.addressSummary);
        price = stay.price;

        if (discountPrice > 0)
        {
            discountPrice = stay.discountPrice;
        } else
        {
            discountPrice = SKIP_CHECK_DISCOUNT_PRICE_VALUE;
        }

        showOriginalPriceYn = getShowOrginalPriceYn(stay.price, stay.discountPrice);
        listPosition = stay.entryPosition;
        gradeName = stay.getGrade().getName(context);
        isDailyChoice = stay.isDailyChoice;
    }

    public void setParam(Context context, Gourmet gourmet)
    {
        placeIndex = gourmet.index;
        placeName = gourmet.name;

        addressAreaName = getAddressAreaName(gourmet.addressSummary);
        price = gourmet.price;
        discountPrice = gourmet.discountPrice;
        showOriginalPriceYn = getShowOrginalPriceYn(gourmet.price, gourmet.discountPrice);
        listPosition = gourmet.entryPosition;
        isDailyChoice = gourmet.isDailyChoice;
    }

    public void setParam(Context context, HomePlace place)
    {
        placeIndex = place.index;
        placeName = place.title;

        Prices prices = place.prices;
        if (prices != null)
        {
            price = prices.normalPrice;

            if (prices.discountPrice > 0)
            {
                discountPrice = prices.discountPrice;
            }
        } else
        {
            price = 0;
            discountPrice = SKIP_CHECK_DISCOUNT_PRICE_VALUE;
        }

        showOriginalPriceYn = getShowOrginalPriceYn(price, discountPrice);
        listPosition = -1;

        if (place.details == null || place.details.stayGrade == null)
        {
            gradeName = Stay.Grade.etc.getName(context);
        } else
        {
            gradeName = place.details.stayGrade.getName(context);
        }

        isDailyChoice = false;
    }

    public void setParam(Context context, RecommendationStay recommendationStay)
    {
        placeIndex = recommendationStay.index;
        placeName = recommendationStay.name;

        price = recommendationStay.price;
        discountPrice = recommendationStay.discount;
        showOriginalPriceYn = getShowOrginalPriceYn(recommendationStay.price, recommendationStay.discount);
        listPosition = recommendationStay.entryPosition;

        Stay.Grade grade;
        try
        {
            grade = Stay.Grade.valueOf(recommendationStay.grade);
        } catch (Exception e)
        {
            grade = Stay.Grade.etc;
        }

        gradeName = grade.getName(context);

        isDailyChoice = recommendationStay.isDailyChoice;
    }

    public void setProvince(Province province)
    {
        if (province == null)
        {
            provinceName = null;
            areaName = null;
            return;
        }

        if (province instanceof Area)
        {
            Area area = (Area) province;

            provinceName = area.getProvince().name;
            areaName = area.name;
        } else
        {
            provinceName = province.name;
            areaName = AnalyticsManager.ValueType.ALL_LOCALE_KR;
        }
    }

    public void setTotalListCount(int listCount)
    {
        totalListCount = listCount;
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

    private String getShowOrginalPriceYn(int originPrice, int discountPrice)
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
        dest.writeInt(placeIndex);
        dest.writeString(placeName);
        dest.writeString(provinceName);
        dest.writeString(areaName);
        dest.writeString(addressAreaName);
        dest.writeInt(price);
        dest.writeInt(discountPrice);
        dest.writeString(showOriginalPriceYn);
        dest.writeInt(listPosition);
        dest.writeInt(totalListCount);
        dest.writeInt(isDailyChoice == true ? 1 : 0);
        dest.writeString(gradeName);
    }

    protected void readFromParcel(Parcel in)
    {
        placeIndex = in.readInt();
        placeName = in.readString();
        provinceName = in.readString();
        areaName = in.readString();
        addressAreaName = in.readString();
        price = in.readInt();
        discountPrice = in.readInt();
        showOriginalPriceYn = in.readString();
        listPosition = in.readInt();
        totalListCount = in.readInt();
        isDailyChoice = in.readInt() == 1 ? true : false;
        gradeName = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        @Override
        public AnalyticsParam createFromParcel(Parcel source)
        {
            return new AnalyticsParam(source);
        }

        @Override
        public AnalyticsParam[] newArray(int size)
        {
            return new AnalyticsParam[size];
        }
    };
}
