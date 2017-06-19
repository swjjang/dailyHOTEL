package com.daily.dailyhotel.screen.stay.outbound.people;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;

import java.util.ArrayList;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SelectPeopleActivity extends BaseActivity<SelectPeoplePresenter>
{
    public static final String INTENT_EXTRA_DATA_NUMBER_OF_ADULTS = "numberOfAdults";
    public static final String INTENT_EXTRA_DATA_CHILD_LIST = "childList";

    public static Intent newInstance(Context context, int numberOfAdults, ArrayList<Integer> childAgeList)
    {
        Intent intent = new Intent(context, SelectPeopleActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, numberOfAdults);
        intent.putExtra(INTENT_EXTRA_DATA_CHILD_LIST, childAgeList);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected SelectPeoplePresenter createInstancePresenter()
    {
        return new SelectPeoplePresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();
    }
}
