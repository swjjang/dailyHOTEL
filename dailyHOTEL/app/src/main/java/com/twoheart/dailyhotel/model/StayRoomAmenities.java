package com.twoheart.dailyhotel.model;

import android.content.Context;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

/**
 * Created by android_sam on 2017. 2. 20..
 */

public enum StayRoomAmenities
{
    WIFI(R.string.label_wifi, R.drawable.f_ic_hotel_04_facilities_01, StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_WIFI, AnalyticsManager.Label.SORTFILTER_WIFI),
    COOKING(R.string.label_cooking, R.drawable.f_ic_hotel_04_facilities_03, StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_COOKING, AnalyticsManager.Label.SORTFILTER_KITCHEN),
    PC(R.string.label_computer, R.drawable.f_ic_hotel_04_facilities_01, StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_PC, AnalyticsManager.Label.SORTFILTER_PC),
    BATHTUB(R.string.label_bathtub, R.drawable.f_ic_hotel_04_facilities_04, StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_BATHTUB, AnalyticsManager.Label.SORTFILTER_BATHTUB),
    TV(R.string.label_television, R.drawable.f_ic_hotel_04_facilities_01, StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_TV, AnalyticsManager.Label.SORTFILTER_TV),
    WHIRLPOOL(R.string.label_whirlpool, R.drawable.f_ic_hotel_04_facilities_01, StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_WHIRLPOOL, AnalyticsManager.Label.SORTFILTER_WHIRLPOOL),
    PRIVATE_BBQ(R.string.label_private_bbq, R.drawable.f_ic_hotel_04_facilities_01, StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_PRIVATE_BBQ, AnalyticsManager.Label.SORTFILTER_PRIVATE_BBQ),
    FREE_BREAKFAST(R.string.label_breakfast, R.drawable.f_ic_hotel_04_facilities_02, StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_BREAKFAST, AnalyticsManager.Label.SORTFILTER_FREE_BREAKFAST),
    KARAOKE(R.string.label_karaoke, R.drawable.f_ic_hotel_04_facilities_01, StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_KARAOKE, AnalyticsManager.Label.SORTFILTER_KARAOKE),
    PARTY_ROOM(R.string.label_party_room, R.drawable.f_ic_hotel_04_facilities_01, StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_PARTY_ROOM, AnalyticsManager.Label.SORTFILTER_PARTYROOM);

    private int nameResId;
    private int resId;
    private int flag;
    private String analytics;

    StayRoomAmenities(int nameResId, int resId, int flag, String analytics)
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
