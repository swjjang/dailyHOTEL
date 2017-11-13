package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.twoheart.dailyhotel.network.DailyMobileService;
import com.twoheart.dailyhotel.network.RetrofitHttpClient;

public class BaseRemoteImpl
{
    Context mContext;

    DailyMobileService mDailyMobileService;

    public BaseRemoteImpl(@NonNull Context context)
    {
        mContext = context;

        mDailyMobileService = RetrofitHttpClient.getInstance(context).getService();
    }
}
