package com.daily.dailyhotel.screen.mydaily.profile;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.daily.base.BasePresenter;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.entity.User;
import com.twoheart.dailyhotel.R;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class ProfilePresenter extends BasePresenter<ProfileActivity, ProfileViewInterface> implements ProfileView.OnEventListener
{
    private ProfileAnalyticsInterface mProfileAnalytics;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

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

        mCompositeDisposable.add(mProfileNetworkImpl.getProfile().subscribe(new Consumer<User>()
        {
            @Override
            public void accept(User user) throws Exception
            {

            }


        }));
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

    }

    @Override
    public void onPause()
    {

    }

    @Override
    public void onDestroy()
    {
        mCompositeDisposable.clear();
        mCompositeDisposable.dispose();
    }

    @Override
    public boolean onBackPressed()
    {
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {

    }

    @Override
    public void onClick(View view)
    {

    }
}
