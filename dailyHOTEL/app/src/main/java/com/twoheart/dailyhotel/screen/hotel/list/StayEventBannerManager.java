package com.twoheart.dailyhotel.screen.hotel.list;

import com.twoheart.dailyhotel.place.manager.PlaceEventBannerManager;

/**
 * Created by android_sam on 2016. 6. 22..
 */
public class StayEventBannerManager extends PlaceEventBannerManager
{
    private static StayEventBannerManager mInstance;

    public static synchronized StayEventBannerManager getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new StayEventBannerManager();
        }

        return mInstance;
    }
}
