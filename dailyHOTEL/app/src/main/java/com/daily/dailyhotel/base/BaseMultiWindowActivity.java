package com.daily.dailyhotel.base;

import android.content.res.Configuration;

import com.daily.base.BaseActivity;

/**
 * Created by android_sam on 2018. 1. 10..
 */

public abstract class BaseMultiWindowActivity<T1 extends BaseMultiWindowPresenter> extends BaseActivity<T1>
{
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        if (getPresenter() == null)
        {
            return;
        }

        getPresenter().onConfigurationChanged(newConfig);
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode)
    {
        super.onMultiWindowModeChanged(isInMultiWindowMode);

        if (getPresenter() == null)
        {
            return;
        }

        getPresenter().onMultiWindowModeChanged(isInMultiWindowMode);
    }
}