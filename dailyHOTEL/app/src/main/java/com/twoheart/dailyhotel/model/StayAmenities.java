package com.twoheart.dailyhotel.model;

import android.content.Context;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

/**
 * Created by android_sam on 2017. 2. 17..
 */

public enum StayAmenities
{
    WIFI(R.string.label_wifi, R.drawable.f_ic_hotel_04_facilities_01, StayFilter.FLAG_HOTEL_FILTER_AMENITIES_WIFI, AnalyticsManager.Label.SORTFILTER_WIFI),
    FREE_BREAKFAST(R.string.label_breakfast, R.drawable.f_ic_hotel_04_facilities_02, StayFilter.FLAG_HOTEL_FILTER_AMENITIES_BREAKFAST, AnalyticsManager.Label.SORTFILTER_FREEBREAKFAST),
    COOKING(R.string.label_cooking, R.drawable.f_ic_hotel_04_facilities_03, StayFilter.FLAG_HOTEL_FILTER_AMENITIES_COOKING, AnalyticsManager.Label.SORTFILTER_KITCHEN),
    BATHTUB(R.string.label_bathtub, R.drawable.f_ic_hotel_04_facilities_04, StayFilter.FLAG_HOTEL_FILTER_AMENITIES_BATH, AnalyticsManager.Label.SORTFILTER_BATHTUB),
    PARKING(R.string.label_parking, R.drawable.f_ic_hotel_04_facilities_05, StayFilter.FLAG_HOTEL_FILTER_AMENITIES_PARKING, AnalyticsManager.Label.SORTFILTER_PARKINGAVAILABLE),
    POOL(R.string.label_pool, R.drawable.f_ic_hotel_04_facilities_06, StayFilter.FLAG_HOTEL_FILTER_AMENITIES_POOL, AnalyticsManager.Label.SORTFILTER_POOL),
    FITNESS(R.string.label_fitness, R.drawable.f_ic_hotel_04_facilities_07, StayFilter.FLAG_HOTEL_FILTER_AMENITIES_FITNESS, AnalyticsManager.Label.SORTFILTER_FITNESS),
    PET(R.string.label_allowed_pet, R.drawable.ic_detail_facilities_05_pet, StayFilter.FLAG_HOTEL_FILTER_AMENITIES_PET, AnalyticsManager.Label.SORTFILTER_PET),
    BBQ(R.string.label_allowed_barbecue, R.drawable.ic_detail_facilities_06_bbq, StayFilter.FLAG_HOTEL_FILTER_AMENITIES_SHAREDBBQ, AnalyticsManager.Label.SORTFILTER_BBQ);

    private int nameResId;
    private int resId;
    private int flag;
    private String analytics;

    StayAmenities(int nameResId, int resId, int flag, String analytics)
    {
        this.nameResId = nameResId;
        this.resId = resId;
        this.flag = flag;
        this.analytics = analytics;
    }

    public int getNameResId()
    {
        return nameResId;
    }

    public String getName(Context context)
    {
        if (context == null)
        {
            return null;
        }

        return context.getResources().getString(nameResId);
    }

    public int getResId()
    {
        return resId;
    }

    public int getFlag()
    {
        return flag;
    }

    public String getAnalytics()
    {
        return analytics;
    }
}
