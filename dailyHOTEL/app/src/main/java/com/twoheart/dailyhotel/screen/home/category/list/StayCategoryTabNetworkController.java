package com.twoheart.dailyhotel.screen.home.category.list;

import android.content.Context;

import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceMainNetworkController;

/**
 * Created by android_sam on 2017. 4. 19..
 */

@Deprecated
public class StayCategoryTabNetworkController extends PlaceMainNetworkController
{
    public StayCategoryTabNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }
}
