package com.twoheart.dailyhotel.screen.information.wishlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;

/**
 * Created by android_sam on 2016. 10. 27..
 */

public class WishListTabActivity extends BaseActivity
{

    public static Intent newInstance(Context context) {
        Intent intent = new Intent(context, WishListTabActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wishlist);
    }

    @Override
    protected void onResume()
    {
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

}