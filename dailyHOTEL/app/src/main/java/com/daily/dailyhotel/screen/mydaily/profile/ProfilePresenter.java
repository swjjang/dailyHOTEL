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
import com.daily.dailyhotel.network.ProfileNetworkImpl;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ExLog;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class ProfilePresenter extends BasePresenter<ProfileActivity, ProfileViewInterface> implements ProfileView.OnEventListener
{
    private ProfileAnalyticsInterface mProfileAnalytics;

    private ProfileNetworkImpl mProfileNetworkImpl;

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
        return new ProfileView(getActivity(), new ProfileView.OnEventListener()
        {
            @Override
            public void onClick(View view)
            {

            }

            @Override
            public void finish()
            {

            }
        });
    }

    @Override
    public void initialize(ProfileActivity activity)
    {
        setContentView(R.layout.activity_profile);

        setAnalytics(new ProfileAnalyticsImpl());

        mProfileAnalytics.screenProfile(activity);

        mProfileNetworkImpl = new ProfileNetworkImpl(activity);

        addCompositeDisposable(mProfileNetworkImpl.getProfile().doOnError(this::onObservableError).doOnNext(this::onUserProfile).subscribe());
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

    @Override
    public void onClick(View view)
    {

    }

    public void onUserProfile(User user)
    {
        if (user == null)
        {
            ExLog.d("pinkred : user == null");
            return;
        }

        ExLog.d("pinkred : " + user.name);
    }
}
