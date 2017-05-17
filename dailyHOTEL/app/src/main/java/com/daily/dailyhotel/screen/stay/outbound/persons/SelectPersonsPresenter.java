package com.daily.dailyhotel.screen.stay.outbound.persons;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Persons;
import com.twoheart.dailyhotel.R;

import java.util.ArrayList;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SelectPersonsPresenter extends BaseExceptionPresenter<SelectPersonsActivity, SelectPersonsViewInterface> implements SelectPersonsView.OnEventListener
{
    private SelectPersonsAnalyticsInterface mAnalytics;

    private Persons mPersons;

    public interface SelectPersonsAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public SelectPersonsPresenter(@NonNull SelectPersonsActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected SelectPersonsViewInterface createInstanceViewInterface()
    {
        return new SelectPersonsView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(SelectPersonsActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_persons_data);

        setAnalytics(new SelectPersonsAnalyticsImpl());

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (SelectPersonsAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        if (mPersons == null)
        {
            mPersons = new Persons(Persons.DEFAULT_PERSONS, null);
        }

        mPersons.numberOfAdults = intent.getIntExtra(SelectPersonsActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, Persons.DEFAULT_PERSONS);
        mPersons.setChildAgeList(intent.getStringArrayListExtra(SelectPersonsActivity.INTENT_EXTRA_DATA_CHILD_LIST));

        return true;
    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setPersons(mPersons);
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

        if (mPersons.numberOfAdults + 1 > MAX_NUMBER_OF_ADULT)
        {
            DailyToast.showToast(getActivity(), "최대 8명 까지만 가능합니다.", DailyToast.LENGTH_SHORT);
        } else
        {
            getViewInterface().setAdultCount(++mPersons.numberOfAdults);
        }
    }

    @Override
    public void onAdultMinusClick()
    {
        final int MIN_NUMBER_OF_ADULT = 1;

        if (mPersons.numberOfAdults - 1 < MIN_NUMBER_OF_ADULT)
        {
            DailyToast.showToast(getActivity(), "최소 1명 까지만 가능합니다.", DailyToast.LENGTH_SHORT);
        } else
        {
            getViewInterface().setAdultCount(--mPersons.numberOfAdults);
        }
    }

    @Override
    public void onChildPlusClick()
    {
        final int MAX_NUMBER_OF_CHILDREN = 3;
        final String DEFAULT_CHILD_AGE = "";

        if (mPersons.getChildAgeList() == null)
        {
            mPersons.setChildAgeList(new ArrayList<>());
        }

        if (mPersons.getChildAgeList().size() + 1 > MAX_NUMBER_OF_CHILDREN)
        {
            DailyToast.showToast(getActivity(), "최대 3명 까지만 가능합니다.", DailyToast.LENGTH_SHORT);
        } else
        {
            mPersons.getChildAgeList().add(DEFAULT_CHILD_AGE);
            getViewInterface().setChildAgeList(mPersons.getChildAgeList());
        }
    }

    @Override
    public void onChildMinusClick()
    {
        final int MIN_NUMBER_OF_ADULT = 0;

        if (mPersons.getChildAgeList().size() - 1 < MIN_NUMBER_OF_ADULT)
        {
            DailyToast.showToast(getActivity(), "최소 0명 까지만 가능합니다.", DailyToast.LENGTH_SHORT);
        } else
        {
            mPersons.getChildAgeList().remove(mPersons.getChildAgeList().size() - 1);
            getViewInterface().setChildAgeList(mPersons.getChildAgeList());
        }
    }
}
