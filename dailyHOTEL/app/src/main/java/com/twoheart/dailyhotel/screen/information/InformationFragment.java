package com.twoheart.dailyhotel.screen.information;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.appboy.Appboy;
import com.appboy.enums.NotificationSubscriptionType;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.screen.event.EventListActivity;
import com.twoheart.dailyhotel.screen.information.bonus.BonusActivity;
import com.twoheart.dailyhotel.screen.information.creditcard.CreditCardListActivity;
import com.twoheart.dailyhotel.screen.information.member.LoginActivity;
import com.twoheart.dailyhotel.screen.information.member.ProfileActivity;
import com.twoheart.dailyhotel.screen.information.member.SignupStep1Activity;
import com.twoheart.dailyhotel.screen.information.terms.LocationTermsActivity;
import com.twoheart.dailyhotel.screen.information.terms.PrivacyActivity;
import com.twoheart.dailyhotel.screen.information.terms.ProtectYouthTermsActivity;
import com.twoheart.dailyhotel.screen.information.terms.TermActivity;
import com.twoheart.dailyhotel.screen.main.MainActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Action;
import com.twoheart.dailyhotel.widget.DailyToast;

import org.json.JSONObject;

public class InformationFragment extends BaseFragment implements Constants
{
    private InformationLayout mInformationLayout;
    private BroadcastReceiver mNewEventBroadcastReceiver;
    private boolean mIsAttach;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mInformationLayout = new InformationLayout(getActivity(), mOnEventListener);

        return mInformationLayout.onCreateView(R.layout.fragment_information);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        mIsAttach = true;
    }

    @Override
    public void onStart()
    {
        //        AnalyticsManager.getInstance(getActivity()).recordScreen(Screen.INFORMATION);

        super.onStart();

        if (DailyDeepLink.getInstance().isValidateLink() == true)
        {
            if (DailyDeepLink.getInstance().isEventView() == true)
            {
                startActivity(new Intent(getContext(), EventListActivity.class));
            } else if (DailyDeepLink.getInstance().isBonusView() == true)
            {
                startActivity(new Intent(getContext(), BonusActivity.class));
            } else if (DailyDeepLink.getInstance().isSingUpView() == true)
            {
                Intent intent = SignupStep1Activity.newInstance(getContext(), DailyDeepLink.getInstance().getRecommenderCode());
                startActivity(intent);
            }

            DailyDeepLink.getInstance().clear();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        unLockUI();

        boolean isLogin = Util.isTextEmpty(DailyPreference.getInstance(getContext()).getAuthorization()) == false;

        mInformationLayout.updateLoginLayout(isLogin);
        mInformationLayout.updateAccountLayout(isLogin);

        registerReceiver();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        unregisterReceiver();
    }

    /////////////////////////////////////////////////////////////////
    // EventListener
    /////////////////////////////////////////////////////////////////

    private InformationLayout.OnEventListener mOnEventListener = new InformationLayout.OnEventListener()
    {
        @Override
        public void onLoginClick()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();

            baseActivity.startActivityForResult(new Intent(baseActivity, LoginActivity.class), CODE_REQUEST_ACTIVITY_LOGIN);
            baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

            AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION, Action.LOGIN_CLICKED, AnalyticsManager.Label.LOGIN_CLICKED, null);
        }

        @Override
        public void onSignUpClick()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();

            Intent intent = SignupStep1Activity.newInstance(baseActivity);
            startActivity(intent);
        }

        @Override
        public void showEditProfile()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            startActivity(new Intent(baseActivity, ProfileActivity.class));
            baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

            //                    AnalyticsManager.getInstance(baseActivity).recordEvent(Screen.INFORMATION, Action.CLICK, Label.PROFILE, 0L);
        }

        @Override
        public void showCouponList()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();

            // TODO : 쿠폰함 보기 화면 이동

        }

        @Override
        public void showBonusList()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();

            startActivity(new Intent(baseActivity, BonusActivity.class));
            baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

            AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , Action.CREDIT_MANAGEMENT_CLICKED, AnalyticsManager.Label.CREDIT_MANAGEMENT_CLICKED, null);
        }

        @Override
        public void showCreditCardList()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();

            startActivity(new Intent(baseActivity, CreditCardListActivity.class));
            baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

            AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , Action.CARD_MANAGEMENT_CLICKED, AnalyticsManager.Label.CARD_MANAGEMENT_CLICKED, null);
        }

        @Override
        public void onRecommendClick()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();

            // TODO : 친구추천하고 5,000원 받기

        }

        @Override
        public void showEvent()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();

            startActivity(new Intent(baseActivity, EventListActivity.class));
            baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

            AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , Action.EVENT_CLICKED, AnalyticsManager.Label.EVENT_CLICKED, null);
        }

        @Override
        public void sendCall()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            showCallDialog(baseActivity);

            //                AnalyticsManager.getInstance(baseActivity).recordEvent(Screen.INFORMATION, Action.CLICK, Label.CALL_CS, 0L);

        }

        @Override
        public void sendEmail()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:help@dailyhotel.co.kr"));
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_text_subject));
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_text_desc));
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(Intent.createChooser(intent, getString(R.string.mail_text_dialog_title)));

            //                AnalyticsManager.getInstance(baseActivity).recordEvent(Screen.INFORMATION, Action.CLICK, Label.MAIL_CS, 0L);

        }

        @Override
        public void showAbout()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            startActivity(new Intent(baseActivity, AboutActivity.class));
            baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

            //                AnalyticsManager.getInstance(baseActivity).recordEvent(Screen.INFORMATION, Action.CLICK, Label.ABOUT, 0L);

        }

        @Override
        public void onPushClick(View v)
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (DailyPreference.getInstance(baseActivity).isAllowPush() == true)
            {
                DailyPreference.getInstance(baseActivity).setAllowPush(false);
                ((TextView) v).setText(R.string.label_off);

                Appboy.getInstance(baseActivity).getCurrentUser().setPushNotificationSubscriptionType(NotificationSubscriptionType.UNSUBSCRIBED);
            } else
            {
                DailyPreference.getInstance(baseActivity).setAllowPush(true);
                ((TextView) v).setText(R.string.label_on);

                Appboy.getInstance(baseActivity).getCurrentUser().setPushNotificationSubscriptionType(NotificationSubscriptionType.OPTED_IN);
            }
            releaseUiComponent();

        }

        @Override
        public void onFacebookClick()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            Intent intent = new Intent(Intent.ACTION_VIEW);

            try
            {
                intent.setData(Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/Dailyhotel.Korea"));
                startActivity(intent);
                baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
            } catch (Exception e)
            {
                try
                {
                    intent.setData(Uri.parse("http://www.facebook.com/dailyhotel"));
                    startActivity(intent);
                    baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                } catch (ActivityNotFoundException e1)
                {

                }
            }
        }

        @Override
        public void onInstagramClick()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            Intent intent = new Intent(Intent.ACTION_VIEW);

            try
            {
                intent.setData(Uri.parse("instagram://user?username=dailyhotel"));
                startActivity(intent);
                baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
            } catch (Exception e)
            {
                try
                {
                    intent.setData(Uri.parse("http://www.instagram.com/dailyhotel"));
                    startActivity(intent);
                    baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                } catch (ActivityNotFoundException e1)
                {
                }
            }
        }

        @Override
        public void onNaverBlogClick()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();

            try
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://blog.naver.com/dailyhotels"));
                startActivity(intent);
                baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
            } catch (ActivityNotFoundException e)
            {

            }

        }

        @Override
        public void showTerms()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();

            Intent intent = new Intent(baseActivity, TermActivity.class);
            startActivity(intent);
            baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

        }

        @Override
        public void showPersonal()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            Intent intent = new Intent(baseActivity, PrivacyActivity.class);
            startActivity(intent);
            baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        }

        @Override
        public void showLocationTerms()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();

            Intent intent = new Intent(baseActivity, LocationTermsActivity.class);
            startActivity(intent);
            baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

        }

        @Override
        public void showProtectChildTerms()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();

            Intent intent = new Intent(baseActivity, ProtectYouthTermsActivity.class);
            startActivity(intent);
            baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

        }

        @Override
        public void finish()
        {
            //do nothing.
        }
    };

    private void showCallDialog(final BaseActivity baseActivity)
    {
        View.OnClickListener positiveListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                releaseUiComponent();

                if (Util.isTelephonyEnabled(baseActivity) == true)
                {
                    try
                    {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + PHONE_NUMBER_DAILYHOTEL)));
                    } catch (ActivityNotFoundException e)
                    {
                        DailyToast.showToast(baseActivity, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
                    }
                } else
                {
                    DailyToast.showToast(baseActivity, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
                }
            }
        };

        String operatingTimeMessage = DailyPreference.getInstance(baseActivity).getOperationTimeMessage(baseActivity);

        baseActivity.showSimpleDialog(getString(R.string.dialog_notice2), operatingTimeMessage, getString(R.string.dialog_btn_call), null, positiveListener, null, null, new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                releaseUiComponent();
            }
        }, true);
    }

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

                mInformationLayout.updateNewIconView(context);
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
