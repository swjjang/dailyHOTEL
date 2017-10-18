package com.daily.dailyhotel.screen.mydaily.reward;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Notification;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
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

    private CommonRemoteImpl mCommonRemoteImpl;

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

        mCommonRemoteImpl = new CommonRemoteImpl(activity);

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

                getViewInterface().setSticker(1, true, false);
                getViewInterface().setSticker(2, true, false);
                getViewInterface().setSticker(3, false, false);
                getViewInterface().setSticker(4, false, false);
                getViewInterface().setSticker(5, false, false);
                getViewInterface().setSticker(6, false, false);
                getViewInterface().setSticker(7, false, false);
                getViewInterface().setSticker(8, false, false);
                getViewInterface().setSticker(9, false, false);
            } else
            {
                getViewInterface().setDescriptionMessage(DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigRewardStickerNonMemberDefaultMessage());

                getViewInterface().setSticker(1, false, false);
                getViewInterface().setSticker(2, false, false);
                getViewInterface().setSticker(3, false, false);
                getViewInterface().setSticker(4, false, false);
                getViewInterface().setSticker(5, false, false);
                getViewInterface().setSticker(6, false, false);
                getViewInterface().setSticker(7, false, false);
                getViewInterface().setSticker(8, false, false);
                getViewInterface().setSticker(9, false, false);
            }

            getViewInterface().setLoginVisible(true);
            getViewInterface().setStickerValidityVisible(false);
            getViewInterface().setRewardHistoryEnabled(false);
            getViewInterface().setIssueCouponVisible(false);
        } else
        {
            getViewInterface().setLoginVisible(false);
            getViewInterface().setIssueCouponVisible(false);
        }

        getViewInterface().setGuideTitleMessage(DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigRewardStickerGuideTitleMessage());
        getViewInterface().setGuideDescriptionMessage(DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigRewardStickerGuideDescriptionMessage());

        getViewInterface().setIssueCouponVisible(true);
        getViewInterface().setIssueCouponEnabled(true);

        getViewInterface().setIssueCouponAnimation(true);

        boolean isBenefitAlarm = DailyUserPreference.getInstance(getActivity()).isBenefitAlarm();

        getViewInterface().setNotificationVisible(isBenefitAlarm == false);
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

        if (DailyHotel.isLogin() == false && DailyRemoteConfigPreference.getInstance(getActivity()).isKeyRemoteConfigRewardStickerCampaignEnabled() == true)
        {
            getViewInterface().startCampaignStickerAnimation();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        // 애니메이션 제거
        getViewInterface().stopCampaignStickerAnimation();
        getViewInterface().setIssueCouponAnimation(false);
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
        if (getViewInterface().isOpenedIssueCoupon() == true)
        {
            onIssueCouponClick();
            return true;
        }

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
                    setRefresh(true);
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
        boolean isOpened = getViewInterface().isOpenedIssueCoupon();

        Observable<Boolean> observable = isOpened ? getViewInterface().closeIssueCouponAnimation() : getViewInterface().openIssueCouponAnimation();

        if (observable == null || lock() == true)
        {
            return;
        }

        screenLock(false);

        if (isOpened == false)
        {
            getViewInterface().setIssueCouponAnimation(false);
        }

        addCompositeDisposable(observable.subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(Boolean aBoolean) throws Exception
            {
                unLockAll();

                if (isOpened == true)
                {
                    getViewInterface().setIssueCouponAnimation(true);
                }
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

    @Override
    public void onHistoryClick()
    {

    }

    @Override
    public void onTermsClick()
    {

    }

    @Override
    public void onRewardGuideClick()
    {

    }

    @Override
    public void onNotificationClick()
    {
        if (lock() == true)
        {
            return;
        }

        screenLock(true);

        addCompositeDisposable(mCommonRemoteImpl.updateNotification(true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Notification>()
        {
            @Override
            public void accept(Notification notification) throws Exception
            {
                getViewInterface().setNotificationVisible(false);

                DailyUserPreference.getInstance(getActivity()).setBenefitAlarm(notification.agreed);

                // 혜택 알림 설정이 off --> on 일때
                String title = getString(R.string.label_setting_alarm);
                String message = getString(R.string.message_benefit_alarm_on_confirm_format, notification.serverDate);
                String positive = getString(R.string.dialog_btn_text_confirm);

                getViewInterface().showSimpleDialog(title, message, positive, null);

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
}
