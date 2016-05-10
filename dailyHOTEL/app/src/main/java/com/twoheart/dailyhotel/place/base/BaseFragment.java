package com.twoheart.dailyhotel.place.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.util.Constants;

public abstract class BaseFragment extends Fragment implements Constants, ErrorListener
{
    protected String mNetworkTag;

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
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        DailyNetworkAPI.getInstance(baseActivity).cancelAll(mNetworkTag);

        super.onDestroy();
    }

    public void onError(Exception e)
    {
        unLockUI();

        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        baseActivity.onError(e);
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

    protected void onErrorPopupMessage(int msgCode, String message)
    {
        unLockUI();

        final BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        baseActivity.onErrorPopupMessage(msgCode, message);
    }

    protected void onErrorToastMessage(String message)
    {
        unLockUI();

        final BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        baseActivity.onErrorToastMessage(message);
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

        baseActivity.lockUI(isShowProgress);
    }

    public void unLockUI()
    {
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
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return true;
        }

        return baseActivity.isLockUiComponent();
    }

    /**
     * UI Component를 잠금상태로 변경..
     */
    protected void lockUiComponent()
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        baseActivity.lockUiComponent();
    }

    public boolean lockUiComponentAndIsLockUiComponent()
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return true;
        }

        return baseActivity.lockUiComponentAndIsLockUiComponent();
    }

    /**
     * UI Component를 잠금해제로 변경..
     */
    protected void releaseUiComponent()
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        baseActivity.releaseUiComponent();
    }
}
