package com.daily.dailyhotel.screen.stay.outbound.detail.amenities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.daily.base.BaseActivity;
import com.twoheart.dailyhotel.R;

import java.util.ArrayList;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class AmenityListActivity extends BaseActivity<AmenityListPresenter>
{
    static final String INTENT_EXTRA_DATA_AMENITY_LIST = "amenityList";

    public static Intent newInstance(Context context, ArrayList<String> amenityList)
    {
        Intent intent = new Intent(context, AmenityListActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_AMENITY_LIST, amenityList);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected AmenityListPresenter createInstancePresenter()
    {
        return new AmenityListPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
    }
}
