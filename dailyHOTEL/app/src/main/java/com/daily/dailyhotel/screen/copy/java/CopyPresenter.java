package com.daily.dailyhotel.screen.copy.java;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class CopyPresenter extends BaseExceptionPresenter<CopyActivity, CopyInterface.ViewInterface> implements CopyInterface.OnEventListener
{
    private final CopyInterface.AnalyticsInterface mAnalytics;

    public CopyPresenter(@NonNull CopyActivity activity)
    {
        super(activity);

        mAnalytics = new CopyAnalyticsImpl();
    }

    @NonNull
    @Override
    protected CopyInterface.ViewInterface createInstanceViewInterface()
    {
        return new CopyView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(CopyActivity activity)
    {
        setContentView(R.layout.activity_copy_data);

        setAnalytics(new CopyAnalyticsImpl());

        setRefresh(true);
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        // 꼭 호출해 주세요.
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed()
    {
        return super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();
    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (isFinish() == true || isRefresh() == false)
        {
            return;
        }

        setRefresh(false);
        screenLock(showProgress);
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

}
