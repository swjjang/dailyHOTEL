package com.twoheart.dailyhotel.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.OnLoadListener;

public abstract class BaseFragment extends Fragment implements Constants, OnLoadListener, ErrorListener
{
    protected String mNetworkTag;

    /**
     * UI Component의 잠금 상태인지 확인하는 변수..
     */
    private boolean mIsLockUiComponent = false;

    public BaseFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mNetworkTag = getClass().getName();
    }

    @Override
    public void onDestroy()
    {
        DailyNetworkAPI.getInstance().cancelAll(mNetworkTag);

        super.onDestroy();
    }

    public void onError(Exception error)
    {
        unLockUI();

        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        baseActivity.onError(error);
    }

    public void onError()
    {
        unLockUI();

        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        baseActivity.onError();
    }

    @Override
    public void onErrorResponse(VolleyError error)
    {
        unLockUI();

        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        baseActivity.onErrorResponse(error);
    }

    protected void onInternalError(String message)
    {
        unLockUI();

        final BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        if (Util.isTextEmpty(message) == true)
        {
            baseActivity.onInternalError();
        } else
        {
            baseActivity.onInternalError(message);
        }
    }

    public void lockUI()
    {
        lockUI(true);
    }

    public void lockUI(boolean isShowProgress)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        lockUiComponent();

        baseActivity.lockUI(isShowProgress);
    }

    public void unLockUI()
    {
        releaseUiComponent();

        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        baseActivity.unLockUI();
    }

    /**
     * UI Component의 잠금 상태를 확인하는 변수..
     *
     * @return
     */
    protected boolean isLockUiComponent()
    {
        return mIsLockUiComponent;
    }

    /**
     * UI Component를 잠금상태로 변경..
     */
    protected void lockUiComponent()
    {
        mIsLockUiComponent = true;
    }

    /**
     * UI Component를 잠금해제로 변경..
     */
    protected void releaseUiComponent()
    {
        mIsLockUiComponent = false;
    }
}
