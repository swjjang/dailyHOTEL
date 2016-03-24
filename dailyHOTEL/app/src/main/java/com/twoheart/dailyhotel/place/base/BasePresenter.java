package com.twoheart.dailyhotel.place.base;

import android.content.Context;

import com.android.volley.Response;

public abstract class BasePresenter implements Response.ErrorListener
{
    protected String mNetworkTag;
    protected Context mContext;
    protected OnBasePresenterListener mOnPresenterListener;

    protected abstract void onErrorMessage(String message);

    public BasePresenter(Context context, String networkTag, OnBasePresenterListener listener)
    {
        if (context == null || networkTag == null || listener == null)
        {
            throw new NullPointerException();
        }

        mContext = context;
        mNetworkTag = networkTag;
        mOnPresenterListener = listener;
    }
}
