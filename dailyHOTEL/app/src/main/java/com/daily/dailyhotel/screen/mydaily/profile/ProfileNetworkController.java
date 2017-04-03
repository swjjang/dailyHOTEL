package com.daily.dailyhotel.screen.mydaily.profile;

import android.content.Context;

import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

public class ProfileNetworkController extends BaseNetworkController
{
    public ProfileNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }
}
