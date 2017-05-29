package com.daily.dailyhotel.screen.stay.outbound.people;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.People;
import com.twoheart.dailyhotel.R;

import java.util.ArrayList;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SelectPeoplePresenter extends BaseExceptionPresenter<SelectPeopleActivity, SelectPeopleViewInterface> implements SelectPeopleView.OnEventListener
{
    private SelectPeopleAnalyticsInterface mAnalytics;

    private People mPeople;

    public interface SelectPeopleAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public SelectPeoplePresenter(@NonNull SelectPeopleActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected SelectPeopleViewInterface createInstanceViewInterface()
    {
        return new SelectPeopleView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(SelectPeopleActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_people_data);

        setAnalytics(new SelectPeopleAnalyticsImpl());

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (SelectPeopleAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        if (mPeople == null)
        {
            mPeople = new People(People.DEFAULT_ADULTS, null);
        }

        mPeople.numberOfAdults = intent.getIntExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, People.DEFAULT_ADULTS);
        mPeople.setChildAgeList(intent.getIntegerArrayListExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_CHILD_LIST));

        return true;
    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setPeople(mPeople, People.DEFAULT_ADULT_MIN_COUNT, People.DEFAULT_ADULT_MAX_COUNT, People.DEFAULT_CHILD_MIN_COUNT, People.DEFAULT_CHILD_MAX_COUNT);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        // 꼭 호출해 주세요.
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed()
    {
        return super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();

        switch(requestCode)
        {
        }
    }

    @Override
    protected void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true)
        {
            return;
        }

    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onAdultPlusClick()
    {
        if (lock() == true)
        {
            return;
        }

        getViewInterface().setAdultCount(++mPeople.numberOfAdults, People.DEFAULT_ADULT_MIN_COUNT, People.DEFAULT_ADULT_MAX_COUNT);

        unLockAll();
    }

    @Override
    public void onAdultMinusClick()
    {
        if (lock() == true)
        {
            return;
        }

        getViewInterface().setAdultCount(--mPeople.numberOfAdults, People.DEFAULT_ADULT_MIN_COUNT, People.DEFAULT_ADULT_MAX_COUNT);

        unLockAll();
    }

    @Override
    public void onChildPlusClick()
    {
        if (lock() == true)
        {
            return;
        }

        final int DEFAULT_CHILD_AGE = 0;

        if (mPeople.getChildAgeList() == null)
        {
            mPeople.setChildAgeList(new ArrayList<>());
        }

        mPeople.getChildAgeList().add(DEFAULT_CHILD_AGE);
        getViewInterface().setChildAgeList(mPeople.getChildAgeList(), People.DEFAULT_CHILD_MIN_COUNT, People.DEFAULT_CHILD_MAX_COUNT);

        unLockAll();
    }

    @Override
    public void onChildMinusClick()
    {
        if (lock() == true)
        {
            return;
        }

        mPeople.getChildAgeList().remove(mPeople.getChildAgeList().size() - 1);
        getViewInterface().setChildAgeList(mPeople.getChildAgeList(), People.DEFAULT_CHILD_MIN_COUNT, People.DEFAULT_CHILD_MAX_COUNT);

        unLockAll();
    }

    @Override
    public void onSelectedChild1AgeClick(int agePosition)
    {
        if (agePosition < 0 && mPeople.getChildAgeList().size() < 1)
        {
            return;
        }

        mPeople.getChildAgeList().set(0, agePosition);

        getViewInterface().setChildAgeList(mPeople.getChildAgeList(), People.DEFAULT_CHILD_MIN_COUNT, People.DEFAULT_CHILD_MAX_COUNT);
    }

    @Override
    public void onSelectedChild2AgeClick(int agePosition)
    {
        if (agePosition < 0 && mPeople.getChildAgeList().size() < 2)
        {
            return;
        }

        mPeople.getChildAgeList().set(1, agePosition);

        getViewInterface().setChildAgeList(mPeople.getChildAgeList(), People.DEFAULT_CHILD_MIN_COUNT, People.DEFAULT_CHILD_MAX_COUNT);
    }

    @Override
    public void onSelectedChild3AgeClick(int agePosition)
    {
        if (agePosition < 0 && mPeople.getChildAgeList().size() < 3)
        {
            return;
        }

        mPeople.getChildAgeList().set(2, agePosition);

        getViewInterface().setChildAgeList(mPeople.getChildAgeList(), People.DEFAULT_CHILD_MIN_COUNT, People.DEFAULT_CHILD_MAX_COUNT);
    }

    @Override
    public void onCancelClick()
    {
        onBackClick();
    }

    @Override
    public void onConfirmClick()
    {
        if (mPeople == null || lock() == true)
        {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, mPeople.numberOfAdults);
        intent.putExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_CHILD_LIST, mPeople.getChildAgeList());

        setResult(Activity.RESULT_OK, intent);
        onBackClick();
    }
}
