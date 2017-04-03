package com.daily.dailyhotel.screen.mydaily.profile;


import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.BasePresenter;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class ProfileActivity extends BaseActivity<ProfilePresenter>
{
    @NonNull
    @Override
    protected BasePresenter createInstancePresenter()
    {
        return new ProfilePresenter(this);
    }
}
