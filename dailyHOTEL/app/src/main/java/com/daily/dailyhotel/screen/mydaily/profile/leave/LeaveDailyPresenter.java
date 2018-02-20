package com.daily.dailyhotel.screen.mydaily.profile.leave;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.LeaveInfo;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class LeaveDailyPresenter extends BaseExceptionPresenter<LeaveDailyActivity, LeaveDailyInterface.ViewInterface> implements LeaveDailyInterface.OnEventListener
{
    private LeaveDailyInterface.AnalyticsInterface mAnalytics;

    private ProfileRemoteImpl mProfileRemoteImpl;

    private LeaveInfo mLeaveInfo;

    public LeaveDailyPresenter(@NonNull LeaveDailyActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected LeaveDailyInterface.ViewInterface createInstanceViewInterface()
    {
        return new LeaveDailyView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(LeaveDailyActivity activity)
    {
        setContentView(R.layout.activity_leave_daily_data);

        setAnalytics(new LeaveDailyAnalyticsImpl());

        mProfileRemoteImpl = new ProfileRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (LeaveDailyInterface.AnalyticsInterface) analytics;
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

        if (DailyHotel.isLogin() == false)
        {
            setRefresh(false);
            restartExpiredSession();
            return;
        }

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

        addCompositeDisposable(mProfileRemoteImpl.getLeaveInfo().observeOn(AndroidSchedulers.mainThread()) //
            .subscribe(new Consumer<LeaveInfo>()
            {
                @Override
                public void accept(LeaveInfo leaveInfo) throws Exception
                {
                    if (leaveInfo.msgCode != 100 && leaveInfo.msgCode != 101)
                    {
                        throw new BaseException(leaveInfo.msgCode, leaveInfo.msg);
                    }

                    setLeaveInfo(leaveInfo);
                    notifyDataSetChanged();

                    unLockAll();
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    setLeaveInfo(null);
                    onHandleErrorAndFinish(throwable);
                }
            }));
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    private void setLeaveInfo(LeaveInfo leaveInfo)
    {
        mLeaveInfo = leaveInfo;
    }

    private void notifyDataSetChanged()
    {
        if (mLeaveInfo == null)
        {
            return;
        }

        getViewInterface().setLeaveInfo(mLeaveInfo);

        notifyRewardDataSetChanged();
    }

    private void notifyRewardDataSetChanged()
    {
        if (mLeaveInfo == null)
        {
            return;
        }

        if (mLeaveInfo.activeReward == false)
        {
            getViewInterface().setRewardLayoutVisible(false);
            return;
        }

        getViewInterface().setRewardLayoutVisible(true);

        getViewInterface().setRewardInfo( //
            DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigRewardStickerCardTitleMessage()//
            , getString(R.string.label_reward_go_reward) //
            , mLeaveInfo.rewardStickerCount//
            , DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigRewardStickerMemberMessage(mLeaveInfo.rewardStickerCount));

//        getViewInterface().stopCampaignStickerAnimation();
    }

    @Override
    public void onLeaveReasonClick()
    {

    }

    @Override
    public void onLeaveButtonClick()
    {

    }

    @Override
    public void onAgreeCheckedChanged(boolean checked)
    {

    }

    @Override
    public void onRewardGuideClick()
    {

    }

    @Override
    public void onRewardClick()
    {

    }
}
