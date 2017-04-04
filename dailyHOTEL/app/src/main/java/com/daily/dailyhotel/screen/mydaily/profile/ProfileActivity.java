package com.daily.dailyhotel.screen.mydaily.profile;


import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.BasePresenter;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityProfileDataBinding;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class ProfileActivity extends BaseActivity<ProfilePresenter>
{
    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, ProfileActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected BasePresenter createInstancePresenter()
    {
        return new ProfilePresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
