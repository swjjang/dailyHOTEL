package com.daily.dailyhotel.screen.home.stay.outbound.people;


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

        mAnalytics = new SelectPeopleAnalyticsImpl();

        setRefresh(true);
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

        int numberOfAdults = intent.getIntExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, People.DEFAULT_ADULTS);
        ArrayList<Integer> childAgeList = intent.getIntegerArrayListExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_CHILD_LIST);

        setPeople(numberOfAdults, childAgeList);

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setPeople(mPeople, People.ADULT_MIN_COUNT, People.ADULT_MAX_COUNT, People.CHILD_MIN_COUNT, People.CHILD_MAX_COUNT);
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

        switch (requestCode)
        {
        }
    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
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

        int numberOfAdults = mPeople.numberOfAdults;

        if (++numberOfAdults > People.ADULT_MAX_COUNT)
        {
            unLockAll();
            return;
        }

        setAdultCount(numberOfAdults);

        getViewInterface().setAdultCount(numberOfAdults, People.ADULT_MIN_COUNT, People.ADULT_MAX_COUNT);

        unLockAll();
    }

    @Override
    public void onAdultMinusClick()
    {
        if (lock() == true)
        {
            return;
        }

        int numberOfAdults = mPeople.numberOfAdults;

        if (--numberOfAdults < People.ADULT_MIN_COUNT)
        {
            unLockAll();
            return;
        }

        setAdultCount(numberOfAdults);

        getViewInterface().setAdultCount(numberOfAdults, People.ADULT_MIN_COUNT, People.ADULT_MAX_COUNT);

        unLockAll();
    }

    @Override
    public void onChildPlusClick()
    {
        if (lock() == true)
        {
            return;
        }

        ArrayList<Integer> childAgeList = mPeople.getChildAgeList();

        if (childAgeList == null)
        {
            childAgeList = new ArrayList<>();
        }

        if (childAgeList.size() + 1 > People.CHILD_MAX_COUNT)
        {
            unLockAll();
            return;
        }

        childAgeList.add(People.DEFAULT_CHILD_AGE);

        setChildList(childAgeList);

        getViewInterface().setChildAgeList(childAgeList, People.CHILD_MIN_COUNT, People.CHILD_MAX_COUNT);

        unLockAll();
    }

    @Override
    public void onChildMinusClick()
    {
        if (lock() == true)
        {
            return;
        }

        ArrayList<Integer> childAgeList = mPeople.getChildAgeList();

        if (childAgeList == null)
        {
            childAgeList = new ArrayList<>();
        }

        if (childAgeList.size() - 1 < People.CHILD_MIN_COUNT)
        {
            unLockAll();
            return;
        }

        childAgeList.remove(childAgeList.size() - 1);

        setChildList(childAgeList);

        getViewInterface().setChildAgeList(mPeople.getChildAgeList(), People.CHILD_MIN_COUNT, People.CHILD_MAX_COUNT);

        unLockAll();
    }

    @Override
    public void onSelectedChild1AgeClick(int agePosition)
    {
        if (agePosition < 0)
        {
            return;
        }

        ArrayList<Integer> childAgeList = mPeople.getChildAgeList();

        if (childAgeList == null || childAgeList.size() < 1)
        {
            return;
        }

        childAgeList.set(0, agePosition);

        setChildList(childAgeList);

        getViewInterface().setChildAgeList(childAgeList, People.CHILD_MIN_COUNT, People.CHILD_MAX_COUNT);
    }

    @Override
    public void onSelectedChild2AgeClick(int agePosition)
    {
        if (agePosition < 0)
        {
            return;
        }

        ArrayList<Integer> childAgeList = mPeople.getChildAgeList();

        if (childAgeList == null || childAgeList.size() < 2)
        {
            return;
        }

        childAgeList.set(1, agePosition);

        setChildList(childAgeList);

        getViewInterface().setChildAgeList(childAgeList, People.CHILD_MIN_COUNT, People.CHILD_MAX_COUNT);
    }

    @Override
    public void onSelectedChild3AgeClick(int agePosition)
    {
        if (agePosition < 0)
        {
            return;
        }

        ArrayList<Integer> childAgeList = mPeople.getChildAgeList();

        if (childAgeList == null || childAgeList.size() < 3)
        {
            return;
        }

        childAgeList.set(2, agePosition);

        setChildList(childAgeList);

        getViewInterface().setChildAgeList(childAgeList, People.CHILD_MIN_COUNT, People.CHILD_MAX_COUNT);
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

    private void setPeople(int numberOfAdults, ArrayList<Integer> childAgeList)
    {
        if (mPeople == null)
        {
            mPeople = new People(People.DEFAULT_ADULTS, null);
        }

        setAdultCount(numberOfAdults);
        setChildList(childAgeList);
    }

    private void setAdultCount(int numberOfAdults)
    {
        if (mPeople == null)
        {
            mPeople = new People(People.DEFAULT_ADULTS, null);
        }

        mPeople.numberOfAdults = numberOfAdults;
    }

    private void setChildList(ArrayList<Integer> childAgeList)
    {
        if (mPeople == null)
        {
            mPeople = new People(People.DEFAULT_ADULTS, null);
        }

        mPeople.setChildAgeList(childAgeList);
    }
}
