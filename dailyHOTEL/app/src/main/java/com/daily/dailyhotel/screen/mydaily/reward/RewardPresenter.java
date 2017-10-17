package com.daily.dailyhotel.screen.mydaily.reward;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class RewardPresenter extends BaseExceptionPresenter<RewardActivity, RewardInterface> implements RewardView.OnEventListener
{
    private RewardAnalyticsInterface mAnalytics;

    public interface RewardAnalyticsInterface extends BaseAnalyticsInterface
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
        setContentView(R.layout.activity_reward_data);

        setAnalytics(new RewardAnalyticsImpl());

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (RewardAnalyticsInterface) analytics;
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
        getViewInterface().setToolbarTitle(getString(R.string.label_daily_reward));

        getViewInterface().setTitleMessage(DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigRewardStickerTitleMessage());

        if (DailyHotel.isLogin() == false)
        {
            setRefresh(false);

            if (DailyRemoteConfigPreference.getInstance(getActivity()).isKeyRemoteConfigRewardStickerCampaignEnabled() == true)
            {
                getViewInterface().setDescriptionMessage(DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigRewardStickerNonMemberCampaignMessage());
            } else
            {
                getViewInterface().setDescriptionMessage(DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigRewardStickerNonMemberDefaultMessage());
            }

            getViewInterface().setLoginVisible(true);
        } else
        {
            getViewInterface().setLoginVisible(false);
        }

        getViewInterface().setGuideTitleMessage(DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigRewardStickerGuideTitleMessage());
        getViewInterface().setGuideDescriptionMessage(DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigRewardStickerGuideDescriptionMessage());

        getViewInterface().setIssueCouponVisible(true);
        getViewInterface().setIssueCouponEnabled(true);
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

        switch (requestCode)
        {
            case RewardActivity.REQUEST_CODE_LOGIN:
                if (resultCode == Activity.RESULT_OK)
                {

                }
                break;
        }
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

    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onLoginClick()
    {
        if (lock() == true)
        {
            return;
        }

        startActivityForResult(LoginActivity.newInstance(getActivity()), RewardActivity.REQUEST_CODE_LOGIN);
    }

    @Override
    public void onIssueCouponClick()
    {
        Observable<Boolean> observable = getViewInterface().isOpenedIssueCoupon() ? getViewInterface().closeIssueCouponAnimation() : getViewInterface().openIssueCouponAnimation();

        if (observable == null || lock() == true)
        {
            return;
        }

        screenLock(false);

        addCompositeDisposable(observable.subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(Boolean aBoolean) throws Exception
            {
                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                unLockAll();
            }
        }));
    }
}
