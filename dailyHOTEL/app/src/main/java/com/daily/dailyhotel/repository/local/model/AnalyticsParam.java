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
import com.twoheart.dailyhotel.network.model.RecommendationGourmet;
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
    private String addressAreaName; // addressSummary 의 split 이름 stay.addressSummary.split("\\||l|ㅣ|I")  index : 0;
    public int price; // 정가
    public int discountPrice; // 표시가
    public String showOriginalPriceYn = "N"; // stay.price <= 0 || stay.price <= stay.discountPrice ? "N" : "Y"
    public int entryPosition = -1;
    public int totalListCount = -1;
    public boolean isDailyChoice;
    public String gradeCode;
    public String gradeName;

    private Province province;

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

        if (stay.discountPrice > 0)
        {
            discountPrice = stay.discountPrice;
        } else
        {
            discountPrice = SKIP_CHECK_DISCOUNT_PRICE_VALUE;
        }

        showOriginalPriceYn = getShowOrginalPriceYn(stay.price, stay.discountPrice);
        entryPosition = stay.entryPosition;
        gradeCode = stay.getGrade().name();
        gradeName = stay.getGrade().getName(context);
        isDailyChoice = stay.isDailyChoice;
    }

    public void setParam(Context context, Gourmet gourmet)
    {
        placeIndex = gourmet.index;
        placeName = gourmet.name;

        addressAreaName = getAddressAreaName(gourmet.addressSummary);
        price = gourmet.price;

        if (gourmet.discountPrice > 0)
        {
            discountPrice = gourmet.discountPrice;
        } else
        {
            discountPrice = SKIP_CHECK_DISCOUNT_PRICE_VALUE;
        }

        discountPrice = gourmet.discountPrice;
        showOriginalPriceYn = getShowOrginalPriceYn(gourmet.price, gourmet.discountPrice);
        entryPosition = gourmet.entryPosition;

        gradeCode = Gourmet.Grade.gourmet.name();
        gradeName = Gourmet.Grade.gourmet.getName(context);

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
        entryPosition = -1;

        if ("GOURMET".equalsIgnoreCase(place.serviceType) == true)
        {
            gradeCode = Gourmet.Grade.gourmet.name();
            gradeName = Gourmet.Grade.gourmet.getName(context);
        } else
        {
            Stay.Grade grade;
            try
            {
                grade = place.details.stayGrade;
            } catch (Exception e)
            {
                grade = Stay.Grade.etc;
            }

            gradeCode = grade.name();
            gradeName = grade.getName(context);
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
        entryPosition = recommendationStay.entryPosition;

        Stay.Grade grade;
        try
        {
            grade = Stay.Grade.valueOf(recommendationStay.grade);
        } catch (Exception e)
        {
            grade = Stay.Grade.etc;
        }

        gradeCode = grade.name();
        gradeName = grade.getName(context);

        isDailyChoice = recommendationStay.isDailyChoice;
    }

    public void setParam(Context context, RecommendationGourmet recommendationGourmet)
    {
        placeIndex = recommendationGourmet.index;
        placeName = recommendationGourmet.name;

        price = recommendationGourmet.price;
        discountPrice = recommendationGourmet.discount;
        showOriginalPriceYn = getShowOrginalPriceYn(recommendationGourmet.price, recommendationGourmet.discount);
        entryPosition = recommendationGourmet.entryPosition;

        gradeCode = Gourmet.Grade.gourmet.name();
        gradeName = Gourmet.Grade.gourmet.getName(context);

        isDailyChoice = recommendationGourmet.isDailyChoice;
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
        if (this.province == null)
        {
            return AnalyticsManager.ValueType.EMPTY;
        }

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
        dest.writeString(addressAreaName);
        dest.writeInt(price);
        dest.writeInt(discountPrice);
        dest.writeString(showOriginalPriceYn);
        dest.writeInt(entryPosition);
        dest.writeInt(totalListCount);
        dest.writeInt(isDailyChoice == true ? 1 : 0);
        dest.writeString(gradeCode);
        dest.writeString(gradeName);
    }

    protected void readFromParcel(Parcel in)
    {
        placeIndex = in.readInt();
        placeName = in.readString();
        addressAreaName = in.readString();
        price = in.readInt();
        discountPrice = in.readInt();
        showOriginalPriceYn = in.readString();
        entryPosition = in.readInt();
        totalListCount = in.readInt();
        isDailyChoice = in.readInt() == 1 ? true : false;
        gradeCode = in.readString();
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
