package com.daily.dailyhotel.screen.mydaily.profile;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BasePresenter;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.entity.UserBenefit;
import com.daily.dailyhotel.repository.local.ProfileLocalImpl;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class ProfilePresenter extends BasePresenter<ProfileActivity, ProfileViewInterface> implements ProfileView.OnEventListener
{
    private ProfileAnalyticsInterface mProfileAnalytics;

    private ProfileRemoteImpl mProfileRemoteImpl;
    private ProfileLocalImpl mProfileLocalImpl;

    private User mUser;

    public interface ProfileAnalyticsInterface extends BaseAnalyticsInterface
    {
        void screenProfile(Activity activity);

        void screenLogOut(Activity activity);

        void clearUserInformation(Context context);

        void eventCopyReferralCode(Context context);

        // 보너스 초과 여부
        void setExceedBonus(Context context, boolean isExceedBonus);
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
    public void initialize(ProfileActivity activity)
    {
        setContentView(R.layout.activity_profile_data);

        setAnalytics(new ProfileAnalyticsImpl());

        mProfileRemoteImpl = new ProfileRemoteImpl(activity);
        mProfileLocalImpl = new ProfileLocalImpl(activity);

        addCompositeDisposable(mProfileRemoteImpl.getProfile().doOnError(this::onHandleError).doOnNext(this::onUserProfile).subscribe());

        mProfileAnalytics.screenProfile(activity);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mProfileAnalytics = (ProfileAnalyticsInterface) analytics;
    }

    @Override
    public void finish()
    {
        onBackPressed();
    }

    @Override
    public void onIntent(Intent intent)
    {

    }

    @Override
    public void onResume()
    {
        super.onResume();
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
                mProfileLocalImpl.setVerified(true);
            } else
            {
                // 인증 후 인증이 해지된 경우
                if (mProfileLocalImpl.isVerified() == true)
                {
                    getViewInterface().showSimpleDialog(null, getString(R.string.message_invalid_verification), getString(R.string.dialog_btn_text_confirm), null);
                }

                mProfileLocalImpl.setVerified(false);
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

        mProfileAnalytics.setExceedBonus(getActivity(), userBenefit.exceedLimitedBonus);
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
        mProfileAnalytics.clearUserInformation(getActivity());
        mProfileAnalytics.screenLogOut(getActivity());
    }

    @Override
    public void onCodeCopyClick(String code)
    {
        mProfileAnalytics.eventCopyReferralCode(getActivity());
    }
}
