package com.daily.dailyhotel.screen.mydaily.profile;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class ProfileActivity extends BaseActivity<ProfilePresenter>
{
    public static final int REQUEST_CODE_EDIT_PROFILE = 10000;
    public static final int REQUEST_CODE_EDIT_PROFILE_BIRTHDAY = 10001;
    public static final int REQUEST_CODE_LOGIN = 10002;

    public static Intent newInstance(Context context, String deepLink)
    {
        Intent intent = new Intent(context, ProfileActivity.class);

        if (DailyTextUtils.isTextEmpty(deepLink) == false)
        {
            intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_DEEPLINK, deepLink);
        }

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
    protected ProfilePresenter createInstancePresenter()
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
