package com.twoheart.dailyhotel.screen.information;

import android.content.ActivityNotFoundException;
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

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.screen.event.EventListActivity;
import com.twoheart.dailyhotel.screen.information.notice.NoticeListActivity;
import com.twoheart.dailyhotel.screen.information.terms.TermsNPolicyActivity;
import com.twoheart.dailyhotel.screen.main.MainActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Action;

public class InformationFragment extends BaseFragment implements Constants
{
    InformationLayout mInformationLayout;
    private BroadcastReceiver mNewEventBroadcastReceiver;
    boolean mIsAttach;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mInformationLayout = new InformationLayout(getActivity(), mOnEventListener);

        return mInformationLayout.onCreateView(R.layout.fragment_information);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mIsAttach = true;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        updateNewIcon();

        if (DailyDeepLink.getInstance().isValidateLink() == true)
        {
            if (DailyDeepLink.getInstance().isEventView() == true)
            {
                mOnEventListener.startEvent();
            } else if (DailyDeepLink.getInstance().isEventDetailView() == true)
            {
                mOnEventListener.startEvent();
                return;
            } else if (DailyDeepLink.getInstance().isNoticeDetailView() == true)
            {
                mOnEventListener.startNotice();
                return;
            } else if (DailyDeepLink.getInstance().isFAQView() == true)
            {
                mOnEventListener.startFAQ();
            } else if (DailyDeepLink.getInstance().isTermsNPolicyView() == true)
            {
                mOnEventListener.startTermsNPolicy();
            }

            DailyDeepLink.getInstance().clear();
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
        }

        @Override
        public void startEvent()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.startActivityForResult(new Intent(baseActivity, EventListActivity.class), Constants.CODE_REQUEST_ACTIVITY_EVENT_LIST);

            AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION_//
                , Action.EVENT_CLICKED, AnalyticsManager.Label.EVENT_CLICKED, null);
        }

        @Override
        public void startNotice()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.startActivityForResult(new Intent(baseActivity, NoticeListActivity.class), Constants.CODE_REQUEST_ACTIVITY_NOTICE_LIST);

            AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION_//
                , Action.EVENT_CLICKED, AnalyticsManager.Label.EVENT_CLICKED, null);
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

            AnalyticsManager.getInstance(baseActivity).recordScreen(baseActivity, AnalyticsManager.Screen.TERMS_AND_CONDITION, null);
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

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
