package com.twoheart.dailyhotel.screen.information;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.dailyhotel.screen.common.web.DailyWebActivity;
import com.daily.dailyhotel.screen.event.list.EventListActivity;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseMenuNavigationFragment;
import com.twoheart.dailyhotel.screen.information.notice.NoticeListActivity;
import com.twoheart.dailyhotel.screen.information.terms.TermsNPolicyActivity;
import com.twoheart.dailyhotel.screen.main.MainActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyExternalDeepLink;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class InformationFragment extends BaseMenuNavigationFragment implements Constants
{
    InformationLayout mInformationLayout;
    private BroadcastReceiver mNewEventBroadcastReceiver;
    boolean mIsAttach;
    private DailyDeepLink mDailyDeepLink;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mInformationLayout = new InformationLayout(getActivity(), mOnEventListener);
        mInformationLayout.setOnScrollChangedListener(mOnScreenScrollChangeListener);

        return mInformationLayout.onCreateView(R.layout.fragment_information, container);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mIsAttach = true;
    }

    @Override
    public void onNewBundle(Bundle bundle)
    {
        if (bundle == null)
        {
            return;
        }

        if (bundle.containsKey(Constants.NAME_INTENT_EXTRA_DATA_DEEPLINK) == true)
        {
            try
            {
                mDailyDeepLink = DailyDeepLink.getNewInstance(Uri.parse(bundle.getString(Constants.NAME_INTENT_EXTRA_DATA_DEEPLINK)));
            } catch (Exception e)
            {
                mDailyDeepLink = null;
            }
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        updateNewIcon();

        if (mDailyDeepLink != null)
        {
            if (mDailyDeepLink.isExternalDeepLink() == true)
            {
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) mDailyDeepLink;

                if (externalDeepLink.isEventView() == true)
                {
                    mOnEventListener.startEvent();
                } else if (externalDeepLink.isEventDetailView() == true)
                {
                    onStartEvent(externalDeepLink);
                } else if (externalDeepLink.isNoticeDetailView() == true)
                {
                    onStartNotice(externalDeepLink);
                } else if (externalDeepLink.isFAQView() == true)
                {
                    mOnEventListener.startFAQ();
                } else if (externalDeepLink.isTermsNPolicyView() == true)
                {
                    mOnEventListener.startTermsNPolicy();
                }
            } else
            {

            }

            mDailyDeepLink.clear();
            mDailyDeepLink = null;
        }

        AnalyticsManager.getInstance(getContext()).recordScreen(getActivity(), AnalyticsManager.Screen.MENU, null);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        updateNewIcon();

        registerReceiver();

        unLockUI();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        unregisterReceiver();
    }

    private void updateNewIcon()
    {
        Context context = getContext();

        boolean hasNewEvent = DailyPreference.getInstance(context).hasNewEvent();
        boolean hasNewNotice = DailyPreference.getInstance(context).hasNewNotice() == true || Util.hasNoticeNewList(context) == true;

        mInformationLayout.updateNewIconView(hasNewEvent, hasNewNotice);
    }

    void onStartEvent(DailyDeepLink dailyDeepLink)
    {
        if (isLockUiComponent() == true || mIsAttach == false)
        {
            return;
        }

        lockUiComponent();

        BaseActivity baseActivity = (BaseActivity) getActivity();
        //        baseActivity.startActivityForResult(EventListActivity.newInstance(baseActivity//
        //            , dailyDeepLink != null ? dailyDeepLink.getDeepLink() : null), Constants.CODE_REQUEST_ACTIVITY_EVENT_LIST);


        baseActivity.startActivityForResult(EventListActivity.newInstance(baseActivity//
            , dailyDeepLink != null ? dailyDeepLink.getDeepLink() : null), Constants.CODE_REQUEST_ACTIVITY_EVENT_LIST);
    }

    public void onStartNotice(DailyDeepLink dailyDeepLink)
    {
        if (isLockUiComponent() == true || mIsAttach == false)
        {
            return;
        }

        lockUiComponent();

        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.startActivityForResult(NoticeListActivity.newInstance(baseActivity//
            , dailyDeepLink != null ? dailyDeepLink.getDeepLink() : null), Constants.CODE_REQUEST_ACTIVITY_NOTICE_LIST);

        AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.NOTICE_CLICK, null, null);
    }

    /////////////////////////////////////////////////////////////////
    // EventListener
    /////////////////////////////////////////////////////////////////

    private InformationLayout.OnEventListener mOnEventListener = new InformationLayout.OnEventListener()
    {
        @Override
        public void startGuide()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.startActivityForResult(AboutActivity.newInstance(baseActivity), Constants.CODE_REQUEST_ACTIVITY_ABOUT);

            AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.DAILY_INFO_CLICK, null, null);
        }

        @Override
        public void startLifeStyle()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.startActivityForResult(LifeStyleActivity.newInstance(baseActivity), Constants.CODE_REQUEST_ACTIVITY_LIFESTYLE);

            AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.DAILY_LIFESTYLE_PROJECT_CLICK, null, null);
        }

        @Override
        public void startSNS()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.startActivityForResult(SnsActivity.newInstance(baseActivity), Constants.CODE_REQUEST_ACTIVITY_SNS);

            AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.DAILY_SNS_CLICK, null, null);
        }

        @Override
        public void startEvent()
        {
            onStartEvent(null);
        }

        @Override
        public void startNotice()
        {
            onStartNotice(null);
        }

        @Override
        public void startFAQ()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.startActivityForResult(new Intent(baseActivity, FAQActivity.class), Constants.CODE_REQUEST_ACTIVITY_FAQ);

            AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.FNQ_CLICK, null, null);
        }

        @Override
        public void startContactUs()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.startActivityForResult(new Intent(baseActivity, ContactUsActivity.class), Constants.CODE_REQUEST_ACTIVITY_CONTACTUS);

            AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.INQUIRY_CLICK, null, null);
        }

        @Override
        public void startTermsNPolicy()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.startActivityForResult(new Intent(baseActivity, TermsNPolicyActivity.class), Constants.CODE_REQUEST_ACTIVITY_TERMS_AND_POLICY);

            AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.TNC_CLICK, null, null);
        }

        @Override
        public void onDailyRewardClick()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();

            baseActivity.startActivityForResult(DailyWebActivity.newInstance(baseActivity, getString(R.string.label_daily_reward)//
                , DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigStaticUrlDailyReward()), Constants.CODE_REQUEST_ACTIVITY_DAILY_REWARD);
        }

        @Override
        public void onDailyTrueAwardsClick()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();

            String lowImageUrl = "http://img.dailyhotel.me/resources/images/home_event/180110_dailytureawards_sm.jpg";

            baseActivity.startActivityForResult(com.daily.dailyhotel.screen.common.event.EventWebActivity.newInstance( //
                baseActivity, com.daily.dailyhotel.screen.common.event.EventWebActivity.EventType.HOME_EVENT//
                , DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigStaticUrlDailyTrueAwards() //
                , getString(R.string.label_daily_true_awards), getString(R.string.label_daily_true_awards_share_description), lowImageUrl) //
                , Constants.CODE_REQUEST_ACTIVITY_DAILY_AWARDS);

            AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.TRUE_AWARDS //
                , AnalyticsManager.Action.SEE_MORE, null, null);
        }

        @Override
        public void finish()
        {
            //do nothing.
        }
    };

    private void registerReceiver()
    {
        if (mNewEventBroadcastReceiver != null)
        {
            return;
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.BROADCAST_EVENT_UPDATE);

        mNewEventBroadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (context == null)
                {
                    return;
                }

                boolean hasNewEvent = DailyPreference.getInstance(context).hasNewEvent();
                boolean hasNewNotice = DailyPreference.getInstance(context).hasNewNotice() == true || Util.hasNoticeNewList(context) == true;

                mInformationLayout.updateNewIconView(hasNewEvent, hasNewNotice);
            }
        };

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mNewEventBroadcastReceiver, intentFilter);
    }

    private void unregisterReceiver()
    {
        if (mNewEventBroadcastReceiver == null)
        {
            return;
        }

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mNewEventBroadcastReceiver);
        mNewEventBroadcastReceiver = null;
    }

    @Override
    public void setOnScrollChangedListener(OnScreenScrollChangeListener listener)
    {
        mOnScreenScrollChangeListener = listener;

        if (mInformationLayout != null)
        {
            mInformationLayout.setOnScrollChangedListener(listener);
        }
    }

    @Override
    public void setOnMenuChangeListener(OnMenuChangeListener listener)
    {

    }

    @Override
    public void scrollTop()
    {
        if (mInformationLayout == null)
        {
            return;
        }

        mInformationLayout.scrollTop();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
