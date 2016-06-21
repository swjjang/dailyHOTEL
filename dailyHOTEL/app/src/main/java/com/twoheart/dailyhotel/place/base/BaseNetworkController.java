package com.twoheart.dailyhotel.place.base;

import android.content.Context;

import com.android.volley.Response;

public abstract class BaseNetworkController
{
    protected String mNetworkTag;
    protected Context mContext;
    protected OnBaseNetworkControllerListener mOnNetworkControllerListener;

    public BaseNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        if (context == null || networkTag == null || listener == null)
        {
            throw new NullPointerException();
        }

        mContext = context;
        mNetworkTag = networkTag;
        mOnNetworkControllerListener = listener;
    }
}
