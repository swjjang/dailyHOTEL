package com.daily.dailyhotel.screen.home.search;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundSuggest;

import java.util.ArrayList;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchStayOutboundViewModel extends ViewModel
{
    private MutableLiveData<StayBookDateTime> mBookDateTime = new MutableLiveData<>();
    private MutableLiveData<StayOutboundSuggest> mSuggest = new MutableLiveData<>();
    private MutableLiveData<People> mPeople = new MutableLiveData<>();
    public String inputKeyword;

    public static class SearchStayOutboundViewModelFactory implements ViewModelProvider.Factory
    {
        public SearchStayOutboundViewModelFactory()
        {
        }

        @NonNull
        @Override
        public SearchStayOutboundViewModel create(@NonNull Class modelClass)
        {
            SearchStayOutboundViewModel viewModel = new SearchStayOutboundViewModel();

            return viewModel;
        }
    }

    public void setBookDateTime(String checkInDateTime, String checkOutDateTime) throws Exception
    {
        mBookDateTime.setValue(new StayBookDateTime(checkInDateTime, checkOutDateTime));
    }

    public void setBookDateTime(String checkInDateTime, int afterCheckInDay, String checkOutDateTime, int afterCheckOutDay) throws Exception
    {
        StayBookDateTime stayBookDateTime = new StayBookDateTime();
        stayBookDateTime.setCheckInDateTime(checkInDateTime, afterCheckInDay);
        stayBookDateTime.setCheckOutDateTime(checkOutDateTime, afterCheckOutDay);

        mBookDateTime.setValue(stayBookDateTime);
    }

    public StayBookDateTime getBookDateTime()
    {
        return mBookDateTime.getValue();
    }

    public void setBookDateTimeObserver(BaseActivity activity, Observer<StayBookDateTime> observer)
    {
        mBookDateTime.observe(activity, observer);
    }

    public void removeBookDateTimeObserver(Observer<StayBookDateTime> observer)
    {
        mBookDateTime.removeObserver(observer);
    }

    public void setSuggest(StayOutboundSuggest suggest)
    {
        this.mSuggest.setValue(suggest);
    }

    public StayOutboundSuggest getSuggest()
    {
        return mSuggest.getValue();
    }

    public void setSuggestObserver(BaseActivity activity, Observer<StayOutboundSuggest> observer)
    {
        mSuggest.observe(activity, observer);
    }

    public void removeSuggestObserver(Observer<StayOutboundSuggest> observer)
    {
        mSuggest.removeObserver(observer);
    }

    public void setPeople(int numberOfAdults, ArrayList<Integer> childAgeList)
    {
        People people = new People(numberOfAdults, childAgeList);
        mPeople.setValue(people);
    }

    public People getPeople()
    {
        return mPeople.getValue();
    }

    public void setPeopleObserver(BaseActivity activity, Observer<People> observer)
    {
        mPeople.observe(activity, observer);
    }

    public void removePeopleObserver(Observer<People> observer)
    {
        mPeople.removeObserver(observer);
    }
}