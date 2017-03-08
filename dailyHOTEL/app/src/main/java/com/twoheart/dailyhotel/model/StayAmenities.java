package com.twoheart.dailyhotel.model;

import android.content.Context;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

/**
 * Created by android_sam on 2017. 2. 17..
 */

public enum StayAmenities
{
    PARKING(R.string.label_parking, R.drawable.f_ic_facilities_05, StayFilter.FLAG_HOTEL_FILTER_AMENITIES_PARKING, AnalyticsManager.Label.SORTFILTER_PARKINGAVAILABLE),
    POOL(R.string.label_pool, R.drawable.f_ic_facilities_06, StayFilter.FLAG_HOTEL_FILTER_AMENITIES_POOL, AnalyticsManager.Label.SORTFILTER_POOL),
    FITNESS(R.string.label_fitness, R.drawable.f_ic_facilities_07, StayFilter.FLAG_HOTEL_FILTER_AMENITIES_FITNESS, AnalyticsManager.Label.SORTFILTER_FITNESS),
    SAUNA(R.string.label_sauna, R.drawable.f_ic_facilities_16, StayFilter.FLAG_HOTEL_FILTER_AMENITIES_SAUNA, AnalyticsManager.Label.SORTFILTER_SAUNA),
    BUSINESS_CENTER(R.string.label_business_center, R.drawable.f_ic_facilities_15, StayFilter.FLAG_HOTEL_FILTER_AMENITIES_BUSINESS_CENTER, AnalyticsManager.Label.SORTFILTER_BUSINESS_CENTER),
    KIDS_PLAY_ROOM(R.string.label_kids_play_room, R.drawable.f_ic_facilities_17, StayFilter.FLAG_HOTEL_FILTER_AMENITIES_KIDS_PLAY_ROOM, AnalyticsManager.Label.SORTFILTER_KIDS_PLAY_ROOM),
    BBQ(R.string.label_allowed_barbecue, R.drawable.f_ic_facilities_09, StayFilter.FLAG_HOTEL_FILTER_AMENITIES_SHARED_BBQ, AnalyticsManager.Label.SORTFILTER_BBQ),
    PET(R.string.label_allowed_pet, R.drawable.f_ic_facilities_08, StayFilter.FLAG_HOTEL_FILTER_AMENITIES_PET, AnalyticsManager.Label.SORTFILTER_PET);

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
