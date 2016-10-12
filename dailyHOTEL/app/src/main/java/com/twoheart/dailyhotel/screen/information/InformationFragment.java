package com.twoheart.dailyhotel.screen.information;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.screen.event.EventListActivity;
import com.twoheart.dailyhotel.screen.information.bonus.BonusActivity;
import com.twoheart.dailyhotel.screen.information.bonus.InviteFriendsActivity;
import com.twoheart.dailyhotel.screen.information.coupon.CouponListActivity;
import com.twoheart.dailyhotel.screen.information.creditcard.CreditCardListActivity;
import com.twoheart.dailyhotel.screen.information.member.LoginActivity;
import com.twoheart.dailyhotel.screen.information.member.ProfileActivity;
import com.twoheart.dailyhotel.screen.information.member.SignupStep1Activity;
import com.twoheart.dailyhotel.screen.information.notice.NoticeListActivity;
import com.twoheart.dailyhotel.screen.information.recentplace.RecentPlacesTabActivity;
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

public class InformationFragment extends BaseFragment implements Constants
{
    private InformationLayout mInformationLayout;
    private InformationNetworkController mNetworkController;
    private BroadcastReceiver mNewEventBroadcastReceiver;
    private boolean mIsAttach;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mInformationLayout = new InformationLayout(getActivity(), mOnEventListener);
        mNetworkController = new InformationNetworkController(getActivity(), mNetworkTag, mNetworkControllerListener);

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
        //        AnalyticsManager.getInstance(getActivity()).recordScreen(Screen.INFORMATION);

        super.onStart();

        Context context = getContext();

        boolean hasNewEvent = DailyPreference.getInstance(context).hasNewEvent();
        boolean hasNewCoupon = DailyPreference.getInstance(context).hasNewCoupon();
        boolean hasNewNotice = DailyPreference.getInstance(context).hasNewNotice() == true || Util.hasNoticeNewList(context) == true;

        mInformationLayout.updateNewIconView(hasNewEvent, hasNewCoupon, hasNewNotice);

        if (DailyDeepLink.getInstance().isValidateLink() == true)
        {
            if (DailyDeepLink.getInstance().isEventView() == true)
            {
                mOnEventListener.startEvent();
            } else if (DailyDeepLink.getInstance().isBonusView() == true)
            {
                mOnEventListener.startBonusList();
            } else if (DailyDeepLink.getInstance().isSingUpView() == true)
            {
                startSignUp(DailyDeepLink.getInstance().getRecommenderCode());
            } else if (DailyDeepLink.getInstance().isCouponView() == true)
            {
                mOnEventListener.startCouponList();
            } else if (DailyDeepLink.getInstance().isEventDetailView() == true)
            {
                mOnEventListener.startEvent();
                return;
            } else if (DailyDeepLink.getInstance().isInformationView() == true)
            {

            } else if (DailyDeepLink.getInstance().isRecommendFriendView() == true)
            {
                mOnEventListener.startInviteFriend();
            } else if (DailyDeepLink.getInstance().isRegisterCouponView() == true)
            {
                mOnEventListener.startCouponList();
                return;
            } else if (DailyDeepLink.getInstance().isNoticeDetailView() == true)
            {
                mOnEventListener.startNotice();
                return;
            }

            DailyDeepLink.getInstance().clear();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        checkInformation();

        registerReceiver();

        if (DailyHotel.isLogin() == true)
        {
            // 적립금 및 쿠폰 개수 가져와야 함
            lockUI();

            mNetworkController.requestUserProfile();
        } else
        {
            // 비로그인 상태
            unLockUI();

            mInformationLayout.updateLoginLayout(false, false);
            mInformationLayout.updateAccountLayout(false, 0, 0);
            mInformationLayout.setLinkAlarmVisible(false);
        }

        // 혜택 알림 메세지 가져오기
        mNetworkController.requestPushBenefitText();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        unregisterReceiver();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case CODE_REQEUST_ACTIVITY_SIGNUP:
            {
                break;
            }

            case Constants.REQUEST_CODE_APPLICATION_DETAILS_SETTINGS:
            {
                if (Util.isOverAPI19() == true)
                {
                    boolean isNotificationsEnabled = NotificationManagerCompat.from(getActivity()).areNotificationsEnabled();

                    if (isNotificationsEnabled == true)
                    {

                    } else
                    {

                    }
                }
                break;
            }
        }
    }

    private void startSignUp(String recommenderCode)
    {
        if (isLockUiComponent() == true || mIsAttach == false)
        {
            return;
        }

        lockUiComponent();

        BaseActivity baseActivity = (BaseActivity) getActivity();

        Intent intent;

        if (Util.isTextEmpty(recommenderCode) == true)
        {
            intent = SignupStep1Activity.newInstance(baseActivity, null);
        } else
        {
            intent = SignupStep1Activity.newInstance(baseActivity, recommenderCode, null);
        }

        startActivityForResult(intent, CODE_REQEUST_ACTIVITY_SIGNUP);
    }

    private void checkInformation()
    {
        String benefitMessage = DailyPreference.getInstance(getContext()).getBenefitAlarmMessage();

        if (Util.isTextEmpty(benefitMessage) == true)
        {
            benefitMessage = getResources().getString(R.string.frag_push_alert_subtext);
            DailyPreference.getInstance(getContext()).setBenefitAlarmMessage(benefitMessage);
        }

        mInformationLayout.updatePushText(benefitMessage);

        Context context = getContext();

        boolean hasNewEvent = DailyPreference.getInstance(context).hasNewEvent();
        boolean hasNewCoupon = DailyPreference.getInstance(context).hasNewCoupon();
        boolean hasNewNotice = DailyPreference.getInstance(context).hasNewNotice() == true || Util.hasNoticeNewList(context) == true;

        mInformationLayout.updateNewIconView(hasNewEvent, hasNewCoupon, hasNewNotice);
    }

    /////////////////////////////////////////////////////////////////
    // EventListener
    /////////////////////////////////////////////////////////////////

    private InformationLayout.OnEventListener mOnEventListener = new InformationLayout.OnEventListener()
    {
        @Override
        public void startLogin()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.startActivity(LoginActivity.newInstance(baseActivity));

            AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION, //
                Action.LOGIN_CLICKED, AnalyticsManager.Label.LOGIN_CLICKED, null);
        }

        @Override
        public void startSignUp()
        {
            InformationFragment.this.startSignUp(null);

            BaseActivity baseActivity = (BaseActivity) getActivity();
            AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION,//
                AnalyticsManager.Action.REGISTRATION_CLICKED, AnalyticsManager.Label.MENU_REGISTER_ACCOUNT, null);
        }

        @Override
        public void startEditProfile()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            startActivity(new Intent(baseActivity, ProfileActivity.class));

            //                    AnalyticsManager.getInstance(baseActivity).recordEvent(Screen.INFORMATION, Action.CLICK, Label.PROFILE, 0L);
        }

        @Override
        public void startCouponList()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();

            startActivity(new Intent(baseActivity, CouponListActivity.class));

            AnalyticsManager.getInstance(getActivity()).recordEvent(AnalyticsManager.Category.COUPON_BOX, //
                Action.COUPON_BOX_CLICKED, AnalyticsManager.Label.COUPON_BOX_CLICKED, null);

        }

        @Override
        public void startBonusList()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            startActivity(new Intent(baseActivity, BonusActivity.class));

            AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , Action.CREDIT_MANAGEMENT_CLICKED, AnalyticsManager.Label.CREDIT_MANAGEMENT_CLICKED, null);
        }

        @Override
        public void startCreditCardList()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            startActivity(new Intent(baseActivity, CreditCardListActivity.class));

            AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , Action.CARD_MANAGEMENT_CLICKED, AnalyticsManager.Label.CARD_MANAGEMENT_CLICKED, null);
        }

        @Override
        public void startInviteFriend()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (DailyHotel.isLogin() == false)
            {
                startActivity(InviteFriendsActivity.newInstance(baseActivity));
            } else
            {
                String recommeder = DailyPreference.getInstance(baseActivity).getUserRecommender();
                String name = DailyPreference.getInstance(baseActivity).getUserName();

                startActivity(InviteFriendsActivity.newInstance(baseActivity, recommeder, name));
            }

            AnalyticsManager.getInstance(getActivity()).recordEvent(AnalyticsManager.Category.NAVIGATION, //
                AnalyticsManager.Action.INVITE_FRIEND_CLICKED, AnalyticsManager.Label.INVITE_FRIENDS, null);
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
            startActivity(new Intent(baseActivity, EventListActivity.class));

            AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
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
            startActivity(new Intent(baseActivity, NoticeListActivity.class));

            AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , Action.EVENT_CLICKED, AnalyticsManager.Label.EVENT_CLICKED, null);
        }

        @Override
        public void startCall()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            showCallDialog(baseActivity);

            AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED, AnalyticsManager.Action.MENU, AnalyticsManager.Label.CLICK, null);
        }

        @Override
        public void startEmail()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:help@dailyhotel.co.kr"));
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_text_subject));
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_text_desc, DailyHotel.VERSION, Build.VERSION.RELEASE));
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(Intent.createChooser(intent, getString(R.string.mail_text_dialog_title)));

            //                AnalyticsManager.getInstance(baseActivity).recordEvent(Screen.INFORMATION, Action.CLICK, Label.MAIL_CS, 0L);

        }

        @Override
        public void startAbout()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            startActivity(new Intent(baseActivity, AboutActivity.class));

            //                AnalyticsManager.getInstance(baseActivity).recordEvent(Screen.INFORMATION, Action.CLICK, Label.ABOUT, 0L);

        }

        @Override
        public void startRecentPlaces()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            startActivity(new Intent(baseActivity, RecentPlacesTabActivity.class));
        }

        @Override
        public void onPushClick()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            final BaseActivity baseActivity = (BaseActivity) getActivity();
            boolean isBenefitAlarm = DailyPreference.getInstance(baseActivity).isUserBenefitAlarm(); // 클릭이므로 상태값 변경!
            boolean onOff = !isBenefitAlarm; // 클릭이므로 상태값 변경!

            if (onOff == true)
            {
                mNetworkController.requestPushBenefit(true);
            } else
            {
                String title = baseActivity.getResources().getString(R.string.label_setting_alarm);
                final String message = baseActivity.getResources().getString(R.string.message_benefit_alarm_off);
                String positive = baseActivity.getResources().getString(R.string.dialog_btn_text_yes);
                String negative = baseActivity.getResources().getString(R.string.dialog_btn_text_no);

                baseActivity.showSimpleDialog(title, message, positive, negative, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mNetworkController.requestPushBenefit(false);
                    }
                }, null, null, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        releaseUiComponent();
                    }
                }, true);
            }
        }

        @Override
        public void startFacebook()
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
                baseActivity.startActivity(intent);
            } catch (Exception e)
            {
                try
                {
                    intent.setData(Uri.parse("http://www.facebook.com/dailyhotel"));
                    baseActivity.startActivity(intent);
                } catch (ActivityNotFoundException e1)
                {
                    ExLog.d(e.toString());
                }
            }
        }

        @Override
        public void startInstagram()
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
                intent.setData(Uri.parse("instagram://user?username=dailyhotel_korea"));
                baseActivity.startActivity(intent);
            } catch (Exception e)
            {
                try
                {
                    intent.setData(Uri.parse("http://www.instagram.com/dailyhotel_korea"));
                    baseActivity.startActivity(intent);
                } catch (ActivityNotFoundException e1)
                {
                }
            }
        }

        @Override
        public void startNaverBlog()
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
                intent.setData(Uri.parse("http://blog.naver.com/dailyhotel"));
                baseActivity.startActivity(intent);
            } catch (ActivityNotFoundException e)
            {
                ExLog.d(e.toString());
            }
        }

        @Override
        public void startYouTube()
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
                intent.setData(Uri.parse("https://www.youtube.com/channel/UCNJASbBThd0TFo3qLgl1wuw"));
                baseActivity.startActivity(intent);
            } catch (ActivityNotFoundException e)
            {

            }
        }

        @Override
        public void startTerms()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            Intent intent = new Intent(baseActivity, TermActivity.class);
            startActivity(intent);

        }

        @Override
        public void startPersonal()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            Intent intent = new Intent(baseActivity, PrivacyActivity.class);
            startActivity(intent);
        }

        @Override
        public void startLocationTerms()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            Intent intent = new Intent(baseActivity, LocationTermsActivity.class);
            startActivity(intent);
        }

        @Override
        public void startProtectChildTerms()
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            Intent intent = new Intent(baseActivity, ProtectYouthTermsActivity.class);
            startActivity(intent);
        }

        @Override
        public void startSettingAlarm()
        {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:com.twoheart.dailyhotel"));
            startActivityForResult(intent, Constants.REQUEST_CODE_APPLICATION_DETAILS_SETTINGS);
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

                AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED, AnalyticsManager.Action.MENU, AnalyticsManager.Label.CALL, null);

                if (Util.isTelephonyEnabled(baseActivity) == true)
                {
                    try
                    {
                        baseActivity.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + PHONE_NUMBER_DAILYHOTEL)));
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

        baseActivity.showSimpleDialog(getString(R.string.dialog_notice2), operatingTimeMessage,//
            getString(R.string.dialog_btn_call), getString(R.string.dialog_btn_text_cancel), positiveListener, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED, AnalyticsManager.Action.MENU, AnalyticsManager.Label.CANCEL, null);
                }
            }, null, new DialogInterface.OnDismissListener()
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

                boolean hasNewEvent = DailyPreference.getInstance(context).hasNewEvent();
                boolean hasNewCoupon = DailyPreference.getInstance(context).hasNewCoupon();
                boolean hasNewNotice = DailyPreference.getInstance(context).hasNewNotice() == true || Util.hasNoticeNewList(context) == true;

                mInformationLayout.updateNewIconView(hasNewEvent, hasNewCoupon, hasNewNotice);
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

    /**
     * 유저 정보 리스너
     */
    private InformationNetworkController.OnNetworkControllerListener mNetworkControllerListener //
        = new InformationNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onUserProfile(String type, String email, String name, String recommender, boolean isAgreedBenefit)
        {
            DailyPreference.getInstance(getContext()).setUserInformation(type, email, name, recommender);

            boolean isLogin = DailyHotel.isLogin();
            if (isLogin == true)
            {
                DailyPreference.getInstance(getContext()).setUserBenefitAlarm(isAgreedBenefit);
                AnalyticsManager.getInstance(getContext()).setPushEnabled(isAgreedBenefit, null);

                mInformationLayout.updatePushIcon(isAgreedBenefit);

                if (Util.isOverAPI19() == true && isAgreedBenefit == true)
                {
                    boolean isNotificationsEnabled = NotificationManagerCompat.from(getActivity()).areNotificationsEnabled();

                    if (isNotificationsEnabled == false)
                    {
                        mInformationLayout.setLinkAlarmVisible(true);
                    } else
                    {
                        mInformationLayout.setLinkAlarmVisible(false);
                    }
                }
            }

            mNetworkController.requestUserProfileBenefit();
        }

        @Override
        public void onUserProfileBenefit(int bonus, int couponTotalCount, boolean isExceedBonus)
        {
            if (bonus < 0)
            {
                bonus = 0;
            }

            boolean isLogin = DailyHotel.isLogin();
            mInformationLayout.updateLoginLayout(isLogin, false);
            mInformationLayout.updateAccountLayout(isLogin, bonus, couponTotalCount);

            AnalyticsManager.getInstance(getContext()).setExceedBonus(isExceedBonus);

            unLockUI();
        }

        @Override
        public void onPushBenefitMessage(String message)
        {
            DailyPreference.getInstance(getContext()).setBenefitAlarmMessage(message);

            mInformationLayout.updatePushText(message);

            unLockUI();
        }

        @Override
        public void onBenefitAgreement(final boolean isAgree, String updateDate)
        {
            lockUiComponent();

            final BaseActivity baseActivity = (BaseActivity) getActivity();

            DailyPreference.getInstance(getContext()).setUserBenefitAlarm(isAgree);
            mInformationLayout.updatePushIcon(isAgree);
            AnalyticsManager.getInstance(baseActivity).setPushEnabled(isAgree, AnalyticsManager.ValueType.OTHER);

            if (isAgree == true)
            {
                // 혜택 알림 설정이 off --> on 일때
                String title = baseActivity.getResources().getString(R.string.label_setting_alarm);
                String message = baseActivity.getResources().getString(R.string.message_benefit_alarm_on_confirm_format, updateDate);
                String positive = baseActivity.getResources().getString(R.string.dialog_btn_text_confirm);

                baseActivity.showSimpleDialog(title, message, positive, null, null, null, null, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        releaseUiComponent();
                    }
                }, true);

                if (Util.isOverAPI19() == true)
                {
                    boolean isNotificationsEnabled = NotificationManagerCompat.from(getActivity()).areNotificationsEnabled();

                    if (isNotificationsEnabled == false)
                    {
                        mInformationLayout.setLinkAlarmVisible(true);
                    } else
                    {
                        mInformationLayout.setLinkAlarmVisible(false);
                    }
                }

                AnalyticsManager.getInstance(getActivity()).recordEvent(AnalyticsManager.Category.NAVIGATION, //
                    Action.NOTIFICATION_SETTING_CLICKED, AnalyticsManager.Label.ON, null);
            } else
            {
                // 혜택 알림 설정이 on --> off 일때
                String title = baseActivity.getResources().getString(R.string.label_setting_alarm);
                String message = baseActivity.getResources().getString(R.string.message_benefit_alarm_off_confirm_format, updateDate);
                String positive = baseActivity.getResources().getString(R.string.dialog_btn_text_confirm);

                baseActivity.showSimpleDialog(title, message, positive, null, null, null, null, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        releaseUiComponent();
                    }
                }, true);

                mInformationLayout.setLinkAlarmVisible(false);

                AnalyticsManager.getInstance(getActivity()).recordEvent(AnalyticsManager.Category.NAVIGATION, //
                    Action.NOTIFICATION_SETTING_CLICKED, AnalyticsManager.Label.OFF, null);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            InformationFragment.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            InformationFragment.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            InformationFragment.this.onErrorPopupMessage(msgCode, message, null);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            InformationFragment.this.onErrorToastMessage(message);
        }
    };
}
