package com.daily.dailyhotel.screen.stay.outbound.persons;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.twoheart.dailyhotel.R;

import java.util.ArrayList;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SelectPersonsActivity extends BaseActivity<SelectPersonsPresenter>
{
    public static final String INTENT_EXTRA_DATA_NUMBER_OF_ADULTS = "numberOfAdults";
    public static final String INTENT_EXTRA_DATA_CHILD_LIST = "childList";

    public static Intent newInstance(Context context, int numberOfAdults, ArrayList<String> childList)
    {
        Intent intent = new Intent(context, SelectPersonsActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, numberOfAdults);
        intent.putExtra(INTENT_EXTRA_DATA_CHILD_LIST, childList);

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
    protected SelectPersonsPresenter createInstancePresenter()
    {
        return new SelectPersonsPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
