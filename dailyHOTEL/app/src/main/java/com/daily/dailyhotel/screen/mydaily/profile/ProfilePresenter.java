package com.daily.dailyhotel.screen.mydaily.profile;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.entity.UserBenefit;
import com.daily.dailyhotel.parcel.UserParcel;
import com.daily.dailyhotel.repository.local.ConfigLocalImpl;
import com.daily.dailyhotel.repository.remote.FacebookRemoteImpl;
import com.daily.dailyhotel.repository.remote.KakaoRemoteImpl;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class ProfilePresenter extends BaseExceptionPresenter<ProfileActivity, ProfileViewInterface> implements ProfileView.OnEventListener
{
    ProfileAnalyticsInterface mProfileAnalytics;

    private ProfileRemoteImpl mProfileRemoteImpl;
    private ConfigLocalImpl mConfigLocalImpl;

    private User mUser;

    public interface ProfileAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity);

        void onScreenLogOut(Activity activity);

        void onClearUserInformation(Context context);

        void onEventCopyReferralCode(Context context);

        // 보너스 초과 여부
        void onExceedBonus(Context context, boolean isExceedBonus);
    }

    public ProfilePresenter(@NonNull ProfileActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected ProfileViewInterface createInstanceViewInterface()
    {
        return new ProfileView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(ProfileActivity activity)
    {
        setContentView(R.layout.activity_profile_data);

        setAnalytics(new ProfileAnalyticsImpl());

        mProfileRemoteImpl = new ProfileRemoteImpl(activity);
        mConfigLocalImpl = new ConfigLocalImpl(activity);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mProfileAnalytics = (ProfileAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        if (intent.hasExtra(BaseActivity.INTENT_EXTRA_DATA_DEEPLINK) == true)
        {
        }

        return true;
    }

    @Override
    public void onPostCreate()
    {

    }

    @Override
    public void onStart()
    {
        super.onStart();

        mProfileAnalytics.onScreen(getActivity());
        //
        //        if (DailyDeepLink.getInstance().isValidateLink() == true)
        //        {
        //            if (DailyDeepLink.getInstance().isProfileBirthdayView() == true)
        //            {
        //                if (DailyHotel.isLogin() == true)
        //                {
        //                    mOnEventListener.startEditBirthday(DailyUserPreference.getInstance(this).getBirthday());
        //                } else
        //                {
        //                    mOnEventListener.startEditBirthday(null);
        //                }
        //            }
        //
        //            DailyDeepLink.getInstance().clear();
        //        } else
        //        {
        //            if (DailyHotel.isLogin() == false)
        //            {
        //                showLoginDialog();
        //            }
        //        }
    }

    @Override
    public void onResume()
    {
        super.onResume();


        //        Observable mergedObservable = Observable.merge(mConfigLocalImpl.isLogin()
        //            , mProfileRemoteImpl.getProfile().doOnError(this::onHandleError).doOnNext(this::onUserProfile));
        //
        //
        //        Observable.
        //
        //
        //        addCompositeDisposable(mConfigLocalImpl.isLogin().subscribe(new Consumer<Boolean>()
        //        {
        //            @Override
        //            public void accept(Boolean isLogin) throws Exception
        //            {
        //
        //            }
        //        });

        if (DailyHotel.isLogin() == true)
        {
            screenLock(true);

            addCompositeDisposable(mProfileRemoteImpl.getProfile().doOnError(this::onHandleError).doOnNext(this::onUserProfile).subscribe());
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
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putParcelable("user", new UserParcel(mUser));
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        mUser = ((UserParcel) savedInstanceState.getParcelable("user")).getUser();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

    }

    @Override
    protected void onRefresh(boolean showProgress)
    {

    }

    private void onUserProfile(User user)
    {
        if (user == null)
        {
            ExLog.d("user == null");
            return;
        }

        mUser = user;

        getViewInterface().setEmail(user.userType, user.email);

        switch (user.userType)
        {
            case Constants.DAILY_USER:
                getViewInterface().setPasswordVisible(true);
                getViewInterface().setPhoneNumberVerifiedVisible(true);

                String verifiedDate = null;

                if (user.verified == true && user.phoneVerified == true)
                {
                    try
                    {
                        verifiedDate = DailyCalendar.convertDateFormatString(user.phoneVerifiedAt, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd");
                    } catch (Exception e)
                    {
                        verifiedDate = null;
                    }
                }

                getViewInterface().setPhoneNumberVerified(user.phoneVerified, verifiedDate);
                break;

            default:
                getViewInterface().setPasswordVisible(false);
                getViewInterface().setPhoneNumberVerifiedVisible(false);
                break;
        }

        getViewInterface().setName(user.name);
        getViewInterface().setPhoneNumber(user.phone);
        getViewInterface().setBirthday(user.birthday);
        getViewInterface().setReferralCode(user.referralCode);

        if (user.verified == true)
        {
            if (user.phoneVerified == true)
            {
                addCompositeDisposable(mConfigLocalImpl.setVerified(true).subscribe());
            } else
            {
                Observable<Boolean> mergedObservable = Observable.merge(mConfigLocalImpl.setVerified(false), mConfigLocalImpl.isVerified());

                addCompositeDisposable(mergedObservable.subscribe(new Consumer<Boolean>()
                {
                    @Override
                    public void accept(Boolean verify) throws Exception
                    {
                        if (verify == true)
                        {
                            getViewInterface().showSimpleDialog(null, getString(R.string.message_invalid_verification), getString(R.string.dialog_btn_text_confirm), null);
                        }
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        onHandleError(throwable);
                    }
                }));

                //                // 인증 후 인증이 해지된 경우
                //                addCompositeDisposable(mConfigLocalImpl.isVerified().subscribe(verify ->
                //                {
                //                    if (verify.booleanValue() == true)
                //                    {
                //                        getViewInterface().showSimpleDialog(null, getString(R.string.message_invalid_verification), getString(R.string.dialog_btn_text_confirm), null);
                //                    }
                //
                //                    addCompositeDisposable(mConfigLocalImpl.setVerified(false).subscribe());
                //                }));
            }
        }

        addCompositeDisposable(mProfileRemoteImpl.getBenefit().doOnError(this::onHandleError).doOnNext(this::onUserBenefit).subscribe());
    }

    private void onUserBenefit(UserBenefit userBenefit)
    {
        if (userBenefit == null)
        {
            ExLog.d("userBenefit == null");
            return;
        }

        mProfileAnalytics.onExceedBonus(getActivity(), userBenefit.exceedLimitedBonus);
    }

    @Override
    public void startEditEmail()
    {

    }

    @Override
    public void startEditName(String name)
    {

    }

    @Override
    public void startEditPhone(String phoneNumber)
    {

    }

    @Override
    public void startEditPassword()
    {

    }

    @Override
    public void startEditBirthday(String birthday)
    {

    }

    @Override
    public void onLogOutClick()
    {
        if (lock() == true)
        {
            return;
        }

        getViewInterface().showSimpleDialog(getString(R.string.act_profile_btn_logout), getString(R.string.dialog_msg_chk_wanna_login),//
            getString(R.string.dialog_btn_text_logout), getString(R.string.dialog_btn_text_cancel)//
            , new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mProfileAnalytics.onClearUserInformation(getActivity());
                    mProfileAnalytics.onScreenLogOut(getActivity());

                    new FacebookRemoteImpl().logOut();
                    new KakaoRemoteImpl().logOut();
                }
            }, null, null, new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    unLock();
                }
            }, false);
    }

    @Override
    public void onCodeCopyClick(String code)
    {
        mProfileAnalytics.onEventCopyReferralCode(getActivity());
    }
}
