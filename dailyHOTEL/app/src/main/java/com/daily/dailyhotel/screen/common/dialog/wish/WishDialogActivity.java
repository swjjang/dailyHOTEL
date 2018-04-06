package com.daily.dailyhotel.screen.common.dialog.wish;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class WishDialogActivity extends BaseActivity<WishDialogPresenter>
{
    static final int REQUEST_CODE_LOGIN = 10000;

    static final String INTENT_EXTRA_DATA_SERVICE_TYPE = "serviceType";
    static final String INTENT_EXTRA_DATA_WISH_INDEX = "wishIndex";
    static final String INTENT_EXTRA_DATA_MY_WISH = "myWish";
    static final String INTENT_EXTRA_DATA_CALL_SCREEN = "callByScreen";

    public static final String INTENT_EXTRA_DATA_WISH = "wish";

    public static Intent newInstance(Context context, Constants.ServiceType serviceType, int wishIndex, boolean myWish, String callByScreen)
    {
        Intent intent = new Intent(context, WishDialogActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_SERVICE_TYPE, serviceType.name());
        intent.putExtra(INTENT_EXTRA_DATA_WISH_INDEX, wishIndex);
        intent.putExtra(INTENT_EXTRA_DATA_MY_WISH, myWish);
        intent.putExtra(INTENT_EXTRA_DATA_CALL_SCREEN, callByScreen);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.hold, R.anim.hold);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected WishDialogPresenter createInstancePresenter()
    {
        return new WishDialogPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.hold);
    }
}
