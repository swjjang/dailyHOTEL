package com.daily.dailyhotel.screen.mydaily.profile;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BasePresenter;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ExLog;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class ProfilePresenter extends BasePresenter<ProfileActivity, ProfileViewInterface> implements ProfileView.OnEventListener
{
    private ProfileAnalyticsInterface mProfileAnalytics;

    private ProfileRemoteImpl mProfileRemoteImpl;

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

        mProfileAnalytics.screenProfile(activity);

        mProfileRemoteImpl = new ProfileRemoteImpl(activity);

        addCompositeDisposable(mProfileRemoteImpl.getProfile().doOnError(this::onHandleError).doOnNext(this::onUserProfile).subscribe());
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mProfileAnalytics = (ProfileAnalyticsInterface) analytics;
    }

    @Override
    public void finish()
    {

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

    public void onUserProfile(User user)
    {
        if (user == null)
        {
            ExLog.d("pinkred : user == null");
            return;
        }

        mUser = user;
        
        ExLog.d("pinkred : " + user.name);

        mUserIndex = userIndex;
        String userType = DailyUserPreference.getInstance(ProfileActivity.this).getType();

        mProfileLayout.updateUserInformation(userType, email, name, Util.addHyphenMobileNumber(ProfileActivity.this, phoneNumber), birthday, referralCode, isVerified, isPhoneVerified, verifiedDate);

        if (isVerified == true)
        {
            if (isPhoneVerified == true)
            {
                DailyPreference.getInstance(ProfileActivity.this).setVerification(true);
            } else
            {
                // 인증 후 인증이 해지된 경우
                if (DailyPreference.getInstance(ProfileActivity.this).isVerification() == true)
                {
                    showSimpleDialog(null, getString(R.string.message_invalid_verification), getString(R.string.dialog_btn_text_confirm), null);
                }

                DailyPreference.getInstance(ProfileActivity.this).setVerification(false);
            }
        }

        mNetworkController.requestUserProfileBenefit();
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

    }

    @Override
    public void onCodeCopyClick(String code)
    {

    }
}
