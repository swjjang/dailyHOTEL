package com.daily.dailyhotel.screen.mydaily.profile;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.daily.base.BasePresenter;
import com.daily.base.OnBaseAnalyticsInterface;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class ProfilePresenter extends BasePresenter<ProfileActivity, ProfileViewInterface> implements ProfileView.OnEventListener
{
    private OnProfileAnalyticsInterface mOnProfileAnalyticsListener;

    public interface OnProfileAnalyticsInterface extends OnBaseAnalyticsInterface
    {
        void analyticsStayClick();
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
        setOnAnalyticsListener(new ProfileAnalyticsImpl());
    }

    @Override
    public void setOnAnalyticsListener(OnBaseAnalyticsInterface listener)
    {
        mOnProfileAnalyticsListener = (OnProfileAnalyticsInterface) listener;
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
