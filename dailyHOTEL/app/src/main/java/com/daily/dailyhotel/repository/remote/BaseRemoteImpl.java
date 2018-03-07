package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.network.DailyMobileService;
import com.twoheart.dailyhotel.network.RetrofitHttpClient;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

public class BaseRemoteImpl
{
    Context mContext;

    DailyMobileService mDailyMobileService;

    public BaseRemoteImpl(@NonNull Context context)
    {
        mContext = context;

        mDailyMobileService = RetrofitHttpClient.getInstance(context).getService();
    }

    protected String getBaseUrl()
    {
        return Constants.DEBUG ? DailyPreference.getInstance(mContext).getBaseUrl() : Crypto.getUrlDecoderEx(Setting.getServerUrl());
    }
}
