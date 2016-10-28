package com.twoheart.dailyhotel.screen.information.wishlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;

/**
 * Created by android_sam on 2016. 10. 27..
 */

public class WishListTabActivity extends BaseActivity
{

    public static Intent newInstance(Context context, PlaceType placeType)
    {
        Intent intent = new Intent(context, WishListTabActivity.class);

        if (placeType != null)
        {
            intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACETYPE, placeType.name());
        }
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
        super.onResume();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

}