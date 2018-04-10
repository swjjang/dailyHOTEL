package com.daily.dailyhotel.repository.remote;

import android.content.Context;

import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.network.DailyMobileService;
import com.twoheart.dailyhotel.network.RetrofitHttpClient;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

public class BaseRemoteImpl
{
    DailyMobileService mDailyMobileService;

    public BaseRemoteImpl()
    {
        mDailyMobileService = RetrofitHttpClient.getInstance().getService();
    }

    protected String getBaseUrl(Context context)
    {
        return Constants.DEBUG ? DailyPreference.getInstance(context).getBaseUrl() : Crypto.getUrlDecoderEx(Setting.getServerUrl());
    }
}
