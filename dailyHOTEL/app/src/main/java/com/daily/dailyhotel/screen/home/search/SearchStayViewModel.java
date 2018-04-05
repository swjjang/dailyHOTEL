package com.daily.dailyhotel.screen.home.search;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StaySuggest;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchStayViewModel extends ViewModel
{
    private MutableLiveData<StayBookDateTime> mBookDateTime = new MutableLiveData<>();
    private MutableLiveData<StaySuggest> mSuggest = new MutableLiveData<>();
    public String inputKeyword;
    private MutableLiveData<Float> mRadius = new MutableLiveData<>();

    public static class SearchStayViewModelFactory implements ViewModelProvider.Factory
    {
        public SearchStayViewModelFactory()
        {
        }

        @NonNull
        @Override
        public SearchStayViewModel create(@NonNull Class modelClass)
        {
            SearchStayViewModel viewModel = new SearchStayViewModel();

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

    public void setSuggest(StaySuggest suggest)
    {
        this.mSuggest.setValue(suggest);
    }

    public StaySuggest getSuggest()
    {
        return mSuggest.getValue();
    }

    public void setSuggestObserver(BaseActivity activity, Observer<StaySuggest> observer)
    {
        mSuggest.observe(activity, observer);
    }

    public void removeSuggestObserver(Observer<StaySuggest> observer)
    {
        mSuggest.removeObserver(observer);
    }

    public void setRadius(float radius)
    {
        mRadius.setValue(radius);
    }

    public float getRadius()
    {
        return (mRadius.getValue() == null) ? 0.0f : mRadius.getValue();
    }

    public void setRadiusObserver(BaseActivity activity, Observer<Float> observer)
    {
        mRadius.observe(activity, observer);
    }

    public void removeRadiusObserver(Observer<Float> observer)
    {
        mRadius.removeObserver(observer);
    }
}