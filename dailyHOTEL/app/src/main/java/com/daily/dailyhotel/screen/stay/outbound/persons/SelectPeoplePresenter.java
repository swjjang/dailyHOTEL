package com.daily.dailyhotel.screen.stay.outbound.persons;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.widget.DailyToast;
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
        setContentView(R.layout.activity_stay_outbound_persons_data);

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
        getViewInterface().setPeople(mPeople);
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
        final int MAX_NUMBER_OF_ADULT = 8;

        if (mPeople.numberOfAdults + 1 > MAX_NUMBER_OF_ADULT)
        {
            DailyToast.showToast(getActivity(), "최대 8명 까지만 가능합니다.", DailyToast.LENGTH_SHORT);
        } else
        {
            getViewInterface().setAdultCount(++mPeople.numberOfAdults);
        }
    }

    @Override
    public void onAdultMinusClick()
    {
        final int MIN_NUMBER_OF_ADULT = 1;

        if (mPeople.numberOfAdults - 1 < MIN_NUMBER_OF_ADULT)
        {
            DailyToast.showToast(getActivity(), "최소 1명 까지만 가능합니다.", DailyToast.LENGTH_SHORT);
        } else
        {
            getViewInterface().setAdultCount(--mPeople.numberOfAdults);
        }
    }

    @Override
    public void onChildPlusClick()
    {
        final int MAX_NUMBER_OF_CHILDREN = 3;
        final int DEFAULT_CHILD_AGE = -1;

        if (mPeople.getChildAgeList() == null)
        {
            mPeople.setChildAgeList(new ArrayList<>());
        }

        if (mPeople.getChildAgeList().size() + 1 > MAX_NUMBER_OF_CHILDREN)
        {
            DailyToast.showToast(getActivity(), "최대 3명 까지만 가능합니다.", DailyToast.LENGTH_SHORT);
        } else
        {
            mPeople.getChildAgeList().add(DEFAULT_CHILD_AGE);
            getViewInterface().setChildAgeList(mPeople.getChildAgeList());
        }
    }

    @Override
    public void onChildMinusClick()
    {
        final int MIN_NUMBER_OF_ADULT = 0;

        if (mPeople.getChildAgeList().size() - 1 < MIN_NUMBER_OF_ADULT)
        {
            DailyToast.showToast(getActivity(), "최소 0명 까지만 가능합니다.", DailyToast.LENGTH_SHORT);
        } else
        {
            mPeople.getChildAgeList().remove(mPeople.getChildAgeList().size() - 1);
            getViewInterface().setChildAgeList(mPeople.getChildAgeList());
        }
    }
}
