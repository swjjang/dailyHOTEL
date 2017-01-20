package com.twoheart.dailyhotel.screen.mydaily;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.screen.main.MainActivity;
import com.twoheart.dailyhotel.screen.mydaily.bonus.BonusActivity;
import com.twoheart.dailyhotel.screen.mydaily.bonus.InviteFriendsActivity;
import com.twoheart.dailyhotel.screen.mydaily.coupon.CouponListActivity;
import com.twoheart.dailyhotel.screen.mydaily.creditcard.CreditCardListActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.ProfileActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.SignupStep1Activity;
import com.twoheart.dailyhotel.screen.mydaily.recentplace.RecentPlacesTabActivity;
import com.twoheart.dailyhotel.screen.mydaily.wishlist.WishListTabActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Action;

import retrofit2.Call;
import retrofit2.Response;

public class MyDailyFragment extends BaseFragment implements Constants
{
    MyDailyLayout mMyDailyLayout;
    MyDailyNetworkController mNetworkController;
    private BroadcastReceiver mNewEventBroadcastReceiver;
    boolean mIsAttach;
    private boolean mDontReload;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mMyDailyLayout = new MyDailyLayout(getActivity(), mOnEventListener);
        mNetworkController = new MyDailyNetworkController(getActivity(), mNetworkTag, mNetworkControllerListener);

        return mMyDailyLayout.onCreateView(R.layout.fragment_mydaily);
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

        Context context = getContext();

        boolean hasNewCoupon = DailyPreference.getInstance(context).hasNewCoupon();

        mMyDailyLayout.updateNewIconView(hasNewCoupon);

        AnalyticsManager.getInstance(context).recordScreen(getActivity(), AnalyticsManager.Screen.MYDAILY, null);

        if (DailyDeepLink.getInstance().isValidateLink() == true)
        {
            if (DailyDeepLink.getInstance().isMyDailyView() == true)
            {

            } else if (DailyDeepLink.getInstance().isBonusView() == true)
            {
                mOnEventListener.startBonusList();
            } else if (DailyDeepLink.getInstance().isSingUpView() == true)
            {
                startSignUp(DailyDeepLink.getInstance().getRecommenderCode());
            } else if (DailyDeepLink.getInstance().isCouponView() == true)
            {
                CouponListActivity.SortType sortType;

                String placeType = DailyDeepLink.getInstance().getPlaceType();

                if (Util.isTextEmpty(placeType) == true)
                {
                    sortType = CouponListActivity.SortType.ALL;
                } else
                {
                    try
                    {
                        sortType = CouponListActivity.SortType.valueOf(placeType.toUpperCase());
                    } catch (Exception e)
                    {
                        sortType = CouponListActivity.SortType.ALL;
                    }
                }

                mOnEventListener.startCouponList(sortType);
            } else if (DailyDeepLink.getInstance().isRecommendFriendView() == true)
            {
                mOnEventListener.startInviteFriend();
            } else if (DailyDeepLink.getInstance().isRegisterCouponView() == true)
            {
                mOnEventListener.startCouponList(CouponListActivity.SortType.ALL);
                return;
            } else if (DailyDeepLink.getInstance().isRecentlyWatchHotelView() == true)
            {
                mOnEventListener.startRecentPlaces(PlaceType.HOTEL);
            } else if (DailyDeepLink.getInstance().isRecentlyWatchGourmetView() == true)
            {
                mOnEventListener.startRecentPlaces(PlaceType.FNB);
            } else if (DailyDeepLink.getInstance().isProfileView() == true)
            {
                mOnEventListener.startEditProfile();
            } else if (DailyDeepLink.getInstance().isProfileBirthdayView() == true)
            {
                mOnEventListener.startEditProfile();
                return;
            } else if (DailyDeepLink.getInstance().isWishListHotelView() == true)
            {
                mOnEventListener.startWishList(PlaceType.HOTEL);
            } else if (DailyDeepLink.getInstance().isWishListGourmetView() == true)
            {
                mOnEventListener.startWishList(PlaceType.FNB);
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

        if (mDontReload == true)
        {
            mDontReload = false;
        } else
        {
            if (DailyHotel.isLogin() == true)
            {
                // 적립금 및 쿠폰 개수 가져와야 함
                lockUI();

                mNetworkController.requestUserProfile();
            } else
            {
                // 비로그인 상태
                unLockUI();

                mMyDailyLayout.updateLoginLayout(false, false);
                mMyDailyLayout.updateAccountLayout(false, 0, 0);
                mMyDailyLayout.setLinkAlarmVisible(false);
            }

            // 혜택 알림 메세지 가져오기
            mNetworkController.requestPushBenefitText();
        }
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

        unLockUI();

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_LOGIN:
                if (resultCode == Activity.RESULT_OK)
                {
                }
                break;

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

            case CODE_REQUEST_ACTIVITY_TERMS_AND_POLICY:
            case CODE_REQUEST_ACTIVITY_FAQ:
            case CODE_REQUEST_ACTIVITY_FEEDBACK:
            case CODE_REQUEST_ACTIVITY_CONTACT_US:
                mDontReload = false;
                break;
        }
    }

    void startSignUp(String recommenderCode)
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

        mMyDailyLayout.updatePushText(benefitMessage);

        Context context = getContext();

        boolean hasNewCoupon = DailyPreference.getInstance(context).hasNewCoupon();

        mMyDailyLayout.updateNewIconView(hasNewCoupon);
    }

    /////////////////////////////////////////////////////////////////
    // EventListener
    /////////////////////////////////////////////////////////////////

    private MyDailyLayout.OnEventListener mOnEventListener = new MyDailyLayout.OnEventListener()
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
            startActivityForResult(LoginActivity.newInstance(baseActivity), CODE_REQUEST_ACTIVITY_LOGIN);

            AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION_, //
                Action.LOGIN_CLICKED, AnalyticsManager.Label.LOGIN_CLICKED, null);
        }

        @Override
        public void startSignUp()
        {
            MyDailyFragment.this.startSignUp(null);

            BaseActivity baseActivity = (BaseActivity) getActivity();
            AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION_,//
                Action.REGISTRATION_CLICKED, AnalyticsManager.Label.MENU_REGISTER_ACCOUNT, null);
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
        public void startCouponList(CouponListActivity.SortType sortType)
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();

            Intent intent = CouponListActivity.newInstance(baseActivity, sortType);
            startActivity(intent);

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

            AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION_//
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

            AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION_//
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
                String recommender = DailyPreference.getInstance(baseActivity).getUserRecommender();
                String name = DailyPreference.getInstance(baseActivity).getUserName();

                startActivity(InviteFriendsActivity.newInstance(baseActivity, recommender, name));
            }

            AnalyticsManager.getInstance(getActivity()).recordEvent(AnalyticsManager.Category.NAVIGATION_, //
                Action.INVITE_FRIEND_CLICKED, AnalyticsManager.Label.INVITE_FRIENDS, null);
        }

        @Override
        public void startWishList(PlaceType placeType)
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            Intent intent = WishListTabActivity.newInstance(baseActivity, WishListTabActivity.SourceType.MYDAILY, placeType);

            baseActivity.startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_RECENTPLACE);
        }

        @Override
        public void startRecentPlaces(PlaceType placeType)
        {
            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }


            lockUiComponent();

            BaseActivity baseActivity = (BaseActivity) getActivity();
            Intent intent = RecentPlacesTabActivity.newInstance(baseActivity, RecentPlacesTabActivity.SourceType.MYDAILY, placeType);

            baseActivity.startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_RECENTPLACE);
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

                boolean hasNewCoupon = DailyPreference.getInstance(context).hasNewCoupon();

                mMyDailyLayout.updateNewIconView(hasNewCoupon);
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
    private MyDailyNetworkController.OnNetworkControllerListener mNetworkControllerListener //
        = new MyDailyNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onUserProfile(String type, String email, String name, String birthday, String recommender, boolean isAgreedBenefit)
        {
            DailyPreference.getInstance(getContext()).setUserInformation(type, email, name, birthday, recommender);

            boolean isLogin = DailyHotel.isLogin();
            if (isLogin == true)
            {
                DailyPreference.getInstance(getContext()).setUserBenefitAlarm(isAgreedBenefit);
                AnalyticsManager.getInstance(getContext()).setPushEnabled(isAgreedBenefit, null);

                mMyDailyLayout.updatePushIcon(isAgreedBenefit);

                if (Util.isOverAPI19() == true && isAgreedBenefit == true)
                {
                    boolean isNotificationsEnabled = NotificationManagerCompat.from(getActivity()).areNotificationsEnabled();

                    if (isNotificationsEnabled == false)
                    {
                        mMyDailyLayout.setLinkAlarmVisible(true);
                    } else
                    {
                        mMyDailyLayout.setLinkAlarmVisible(false);
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
            mMyDailyLayout.updateLoginLayout(isLogin, false);
            mMyDailyLayout.updateAccountLayout(isLogin, bonus, couponTotalCount);

            AnalyticsManager.getInstance(getContext()).setExceedBonus(isExceedBonus);

            unLockUI();
        }

        @Override
        public void onPushBenefitMessage(String message)
        {
            DailyPreference.getInstance(getContext()).setBenefitAlarmMessage(message);

            mMyDailyLayout.updatePushText(message);

            unLockUI();
        }

        @Override
        public void onBenefitAgreement(final boolean isAgree, String updateDate)
        {
            lockUiComponent();

            final BaseActivity baseActivity = (BaseActivity) getActivity();

            DailyPreference.getInstance(getContext()).setUserBenefitAlarm(isAgree);
            mMyDailyLayout.updatePushIcon(isAgree);
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
                        mMyDailyLayout.setLinkAlarmVisible(true);
                    } else
                    {
                        mMyDailyLayout.setLinkAlarmVisible(false);
                    }
                }

                AnalyticsManager.getInstance(getActivity()).recordEvent(AnalyticsManager.Category.NAVIGATION_, //
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

                mMyDailyLayout.setLinkAlarmVisible(false);

                AnalyticsManager.getInstance(getActivity()).recordEvent(AnalyticsManager.Category.NAVIGATION_, //
                    Action.NOTIFICATION_SETTING_CLICKED, AnalyticsManager.Label.OFF, null);
            }
        }

        @Override
        public void onError(Throwable e)
        {
            MyDailyFragment.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            MyDailyFragment.this.onErrorPopupMessage(msgCode, message, null);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            MyDailyFragment.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            MyDailyFragment.this.onErrorResponse(call, response);
        }
    };
}
