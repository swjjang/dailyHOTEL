package com.daily.dailyhotel.screen.mydaily.reward.history.card;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.RewardCardHistoryDetail;
import com.daily.dailyhotel.repository.remote.RewardRemoteImpl;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Util;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class RewardCardHistoryPresenter extends BaseExceptionPresenter<RewardCartHistoryActivity, RewardCardHistoryInterface> implements RewardCardHistoryView.OnEventListener
{
    private RewardCardHistoryAnalyticsInterface mAnalytics;

    private RewardRemoteImpl mRewardRemoteImpl;

    public interface RewardCardHistoryAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public RewardCardHistoryPresenter(@NonNull RewardCartHistoryActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected RewardCardHistoryInterface createInstanceViewInterface()
    {
        return new RewardCardHistoryView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(RewardCartHistoryActivity activity)
    {
        setContentView(R.layout.activity_reward_card_data);

        setAnalytics(new RewardCardHistoryAnalyticsImpl());

        mRewardRemoteImpl = new RewardRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (RewardCardHistoryAnalyticsInterface) analytics;
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
        getViewInterface().setToolbarTitle(getString(R.string.label_reward_reward_card_history));
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
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

        setRefresh(false);
        screenLock(showProgress);

        addCompositeDisposable(mRewardRemoteImpl.getRewardCardHistoryDetail().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<RewardCardHistoryDetail>()
        {
            @Override
            public void accept(RewardCardHistoryDetail rewardCardHistoryDetail) throws Exception
            {
                if (rewardCardHistoryDetail.activeReward == false)
                {
                    Util.restartApp(getActivity());
                } else
                {
                    getViewInterface().setRewardCardHistoryList(rewardCardHistoryDetail.getRewardCardHistoryList());
                }

                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                onHandleError(throwable);
            }
        }));
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

}
