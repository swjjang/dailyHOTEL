package com.daily.dailyhotel.screen.mydaily.profile.leave;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.exception.BaseException;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.LeaveInfo;
import com.daily.dailyhotel.entity.LeaveReason;
import com.daily.dailyhotel.parcel.LeaveReasonParcel;
import com.daily.dailyhotel.parcel.ListDialogItemParcel;
import com.daily.dailyhotel.repository.local.ConfigLocalImpl;
import com.daily.dailyhotel.repository.remote.FacebookRemoteImpl;
import com.daily.dailyhotel.repository.remote.KakaoRemoteImpl;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.daily.dailyhotel.screen.common.dialog.list.BaseListDialogActivity;
import com.daily.dailyhotel.screen.common.web.DailyWebActivity;
import com.daily.dailyhotel.screen.mydaily.reward.RewardActivity;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class LeaveDailyPresenter extends BaseExceptionPresenter<LeaveDailyActivity, LeaveDailyInterface.ViewInterface> implements LeaveDailyInterface.OnEventListener
{
    LeaveDailyInterface.AnalyticsInterface mAnalytics;

    ProfileRemoteImpl mProfileRemoteImpl;

    private LeaveInfo mLeaveInfo;
    LeaveReason mSelectedReason;

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

        try
        {
            mAnalytics.onScreen(getActivity());
        } catch (Exception e)
        {
            ExLog.d(e.getMessage());
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

        switch (requestCode)
        {
            case LeaveDailyActivity.REQUEST_CODE_LEAVE_REASON:
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    if (data == null)
                    {
                        return;
                    }

                    ListDialogItemParcel selectedParcel = data.getParcelableExtra(BaseListDialogActivity.INTENT_EXTRA_DATA_SELECTED_DATA);
                    if (selectedParcel != null)
                    {
                        LeaveReasonParcel reasonParcel = (LeaveReasonParcel) selectedParcel.getItem();
                        mSelectedReason = reasonParcel.getLeaveReason();
                    }

                    getViewInterface().setLeaveReasonText(mSelectedReason == null ? null : mSelectedReason.reason);
                    getViewInterface().setLeaveButtonEnabled(mSelectedReason != null && getViewInterface().isAgreeChecked());

                    try
                    {
                        mAnalytics.onEventLeaveReasonSelected(getActivity(), mSelectedReason.reason);
                    } catch (Exception e)
                    {
                        ExLog.d(e.getMessage());
                    }
                }
                break;
            }
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

    void setLeaveInfo(LeaveInfo leaveInfo)
    {
        mLeaveInfo = leaveInfo;
    }

    void notifyDataSetChanged()
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
        if (getActivity() == null || mLeaveInfo == null || lock() == true)
        {
            return;
        }

        ArrayList<ListDialogItemParcel> parcelList = new ArrayList<>();
        for (LeaveReason leaveReason : mLeaveInfo.leaveReasonList)
        {
            parcelList.add(new ListDialogItemParcel(leaveReason.reason, new LeaveReasonParcel(leaveReason)));
        }

        ListDialogItemParcel selectedParcel = mSelectedReason == null //
            ? null : new ListDialogItemParcel(mSelectedReason.reason, new LeaveReasonParcel(mSelectedReason));

        Intent intent = BaseListDialogActivity.newInstance(getActivity(), getString(R.string.label_leave_daily_leave_reason_title) //
            , selectedParcel, parcelList, AnalyticsManager.Screen.MEMBER_LEAVE_STEP_3);
        startActivityForResult(intent, LeaveDailyActivity.REQUEST_CODE_LEAVE_REASON);
    }

    @Override
    public void onLeaveButtonClick()
    {
        if (mSelectedReason == null || lock() == true)
        {
            return;
        }

        getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_message_check_leave_daily)//
            , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    screenLock(true);

                    addCompositeDisposable(mProfileRemoteImpl.doUserLeaveDaily(mSelectedReason.index).observeOn(AndroidSchedulers.mainThread()) //
                        .subscribe(aBoolean -> addCompositeDisposable(new ConfigLocalImpl(getActivity()).clear().subscribe(object -> {
                            new FacebookRemoteImpl().logOut();
                            new KakaoRemoteImpl().logOut();

                            Util.restartApp(getActivity());
                        })), throwable -> onHandleError(throwable)));

                    try
                    {
                        mAnalytics.onEventCheckLeaveDialogButtonClick(getActivity(), true);
                    } catch (Exception e)
                    {
                        ExLog.d(e.getMessage());
                    }
                }
            }, null, dialogInterface -> {
                try
                {
                    mAnalytics.onEventCheckLeaveDialogButtonClick(getActivity(), false);
                } catch (Exception e)
                {
                    ExLog.d(e.getMessage());
                }
            }, dialogInterface -> unLockAll(), true);

        try
        {
            mAnalytics.onEventLeaveButtonClick(getActivity());
        } catch (Exception e)
        {
            ExLog.d(e.getMessage());
        }
    }

    @Override
    public void onAgreeCheckedChanged(boolean checked)
    {
        if (getViewInterface() == null)
        {
            return;
        }

        getViewInterface().setLeaveButtonEnabled(mSelectedReason != null && checked);
    }

    @Override
    public void onRewardGuideClick()
    {
        if (lock() == true)
        {
            return;
        }

        startActivityForResult(DailyWebActivity.newInstance(getActivity(), getString(R.string.label_daily_reward)//
            , DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigStaticUrlDailyReward()), LeaveDailyActivity.REQUEST_CODE_WEB);
    }

    @Override
    public void onRewardClick()
    {
        if (lock() == true)
        {
            return;
        }

        Intent intent = RewardActivity.newInstance(getActivity());
        startActivityForResult(intent, LeaveDailyActivity.REQUEST_CODE_REWARD);
    }
}
