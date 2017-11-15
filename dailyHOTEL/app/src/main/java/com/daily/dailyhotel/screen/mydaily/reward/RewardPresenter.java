package com.daily.dailyhotel.screen.mydaily.reward;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Notification;
import com.daily.dailyhotel.entity.RewardDetail;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.RewardRemoteImpl;
import com.daily.dailyhotel.screen.common.web.DailyWebActivity;
import com.daily.dailyhotel.screen.mydaily.reward.history.RewardHistoryActivity;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.LauncherActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyInternalDeepLink;

import java.text.ParseException;

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

    private RewardRemoteImpl mRewardRemoteImpl;
    private CommonRemoteImpl mCommonRemoteImpl;

    private RewardDetail mRewardDetail;

    public interface RewardAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity);

        void onEventHistoryClick(Activity activity);

        void onEventTermsClick(Activity activity);
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

        mRewardRemoteImpl = new RewardRemoteImpl(activity);
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

        getViewInterface().setTitleMessage(DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigRewardStickerRewardTitleMessage());

        if (DailyHotel.isLogin() == false)
        {
            if (DailyRemoteConfigPreference.getInstance(getActivity()).isKeyRemoteConfigRewardStickerCampaignEnabled() == true)
            {
                getViewInterface().setDescriptionMessage(DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigRewardStickerNonMemberCampaignMessage());

                getViewInterface().setCampaignFreeStickerCount(2);
            } else
            {
                getViewInterface().setDescriptionMessage(DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigRewardStickerNonMemberDefaultMessage());

                getViewInterface().setCampaignFreeStickerCount(0);
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

        getViewInterface().setNotificationVisible(DailyUserPreference.getInstance(getActivity()).isBenefitAlarm() == false);
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

        if (DailyHotel.isLogin() == false)
        {
            if (DailyRemoteConfigPreference.getInstance(getActivity()).isKeyRemoteConfigRewardStickerCampaignEnabled() == true)
            {
                getViewInterface().startCampaignStickerAnimation();
            }
        } else
        {
            getViewInterface().setLoginVisible(false);

            if (mRewardDetail != null && mRewardDetail.availableRewardCouponCount > 0 && getViewInterface().isOpenedIssueCoupon() == false)
            {
                getViewInterface().setIssueCouponVisible(true);
                getViewInterface().setIssueCouponAnimation(true);
            }
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

        addCompositeDisposable(mRewardRemoteImpl.getRewardDetail().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<RewardDetail>()
        {
            @Override
            public void accept(RewardDetail rewardDetail) throws Exception
            {
                if (rewardDetail.activeReward == false)
                {
                    getViewInterface().showSimpleDialog(null, getString(R.string.message_reward_system_maintenance), getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                            startActivity(DailyInternalDeepLink.getHomeScreenLink(getActivity()));
                        }
                    }, false);
                }

                if (DailyHotel.isLogin() == true)
                {
                    setRewardDetail(rewardDetail);

                    notifyRewardDetailChanged();
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
        } else
        {
            addCompositeDisposable(observable.subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
            {
                @Override
                public void accept(Boolean aBoolean) throws Exception
                {
                    unLockAll();

                    getViewInterface().setIssueCouponAnimation(true);
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

    @Override
    public void onHistoryClick()
    {
        if (lock() == true)
        {
            return;
        }

        startActivityForResult(RewardHistoryActivity.newInstance(getActivity()), RewardActivity.REQUEST_CODE_REWARD_HISTORY);

        mAnalytics.onEventHistoryClick(getActivity());
    }

    @Override
    public void onTermsClick()
    {
        if (lock() == true)
        {
            return;
        }

        startActivityForResult(DailyWebActivity.newInstance(getActivity(), getString(R.string.label_reward_reward_terms)//
            , DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigStaticUrlDailyRewardTerms()), RewardActivity.REQUEST_CODE_WEB);

        mAnalytics.onEventTermsClick(getActivity());
    }

    @Override
    public void onRewardGuideClick()
    {
        if (lock() == true)
        {
            return;
        }

        startActivityForResult(DailyWebActivity.newInstance(getActivity(), getString(R.string.label_reward_reward_guide)//
            , DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigStaticUrlDailyReward()), RewardActivity.REQUEST_CODE_WEB);
    }

    @Override
    public void onNotificationClick()
    {
        if (lock() == true)
        {
            return;
        }

        screenLock(true);

        addCompositeDisposable(mCommonRemoteImpl.updateNotification(true) //
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Notification>()
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

    @Override
    public void onGoBookingClick()
    {
        if (lock() == true)
        {
            return;
        }

        final String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=hl&dp=0&n=1";

        Intent intent = new Intent(getActivity(), LauncherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(deepLink));

        startActivity(intent);
    }

    private void setRewardDetail(RewardDetail rewardDetail)
    {
        mRewardDetail = rewardDetail;
    }

    private void notifyRewardDetailChanged()
    {
        if (mRewardDetail == null)
        {
            return;
        }

        final int MAX_COUNT = 9;

        getViewInterface().setDescriptionMessage(DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigRewardStickerMemberMessage(mRewardDetail.rewardStickerCount));

        getViewInterface().setStickerCount(mRewardDetail.rewardStickerCount);

        if (mRewardDetail.rewardStickerCount > 0)
        {
            if (DailyTextUtils.isTextEmpty(mRewardDetail.expiredAt) == true)
            {
                getViewInterface().setStickerValidityVisible(false);
            } else
            {
                getViewInterface().setStickerValidityVisible(true);
                try
                {
                    getViewInterface().setStickerValidityText(getString(R.string.label_reward_sticker_validity, DailyCalendar.convertDateFormatString(mRewardDetail.expiredAt, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd")));
                } catch (ParseException e)
                {
                    ExLog.e(e.toString());
                }
            }
        } else
        {
            getViewInterface().setStickerValidityVisible(false);
        }

        if (mRewardDetail.availableRewardCouponCount > 0)
        {
            getViewInterface().setIssueCouponVisible(true);
            getViewInterface().setIssueCouponEnabled(true);
            getViewInterface().setIssueCouponCount(mRewardDetail.availableRewardCouponCount);
            getViewInterface().setIssueCouponAnimation(true);

        } else if (mRewardDetail.rewardStickerCount == MAX_COUNT)
        {
            getViewInterface().setIssueCouponVisible(true);
            getViewInterface().setIssueCouponEnabled(false);
            getViewInterface().setIssueCouponAnimation(false);
        } else
        {
            getViewInterface().setIssueCouponVisible(false);
            getViewInterface().setIssueCouponAnimation(false);
        }

        getViewInterface().setRewardHistoryEnabled(mRewardDetail.hasRewardHistory);
    }
}
