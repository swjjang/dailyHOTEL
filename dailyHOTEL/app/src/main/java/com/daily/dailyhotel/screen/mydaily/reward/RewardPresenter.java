package com.daily.dailyhotel.screen.mydaily.reward;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class RewardPresenter extends BaseExceptionPresenter<RewardActivity, RewardInterface> implements RewardView.OnEventListener
{
    private CopyAnalyticsInterface mAnalytics;

    public interface CopyAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public RewardPresenter(@NonNull RewardActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected RewardInterface createInstanceViewInterface()
    {
        return new RewardView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(RewardActivity activity)
    {
        setContentView(R.layout.activity_copy_data);

        setAnalytics(new RewardAnalyticsImpl());

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (CopyAnalyticsInterface) analytics;
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
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

}
