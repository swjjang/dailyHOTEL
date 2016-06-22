package com.twoheart.dailyhotel.screen.gourmet.list;

import com.twoheart.dailyhotel.place.manager.PlaceEventBannerManager;

/**
 * Created by sheldon on 2016. 6. 17..
 */
public class GourmetEventBannerManager extends PlaceEventBannerManager
{
    private static GourmetEventBannerManager mInstance;

    public static synchronized GourmetEventBannerManager getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new GourmetEventBannerManager();
        }

        return mInstance;
    }

    private GourmetEventBannerManager()
    {
        super();
    }
}
