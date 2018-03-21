package com.daily.dailyhotel.screen.home.search;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StaySuggestV2;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchStayViewModel extends ViewModel
{
    private MutableLiveData<StayBookDateTime> bookDateTime = new MutableLiveData<>();
    private MutableLiveData<StaySuggestV2> suggest = new MutableLiveData<>();
    public String inputKeyword;
    public float radius;

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
        bookDateTime.setValue(new StayBookDateTime(checkInDateTime, checkOutDateTime));
    }

    public void setBookDateTime(String checkInDateTime, int afterCheckInDay, String checkOutDateTime, int afterCheckOutDay) throws Exception
    {
        StayBookDateTime stayBookDateTime = new StayBookDateTime();
        stayBookDateTime.setCheckInDateTime(checkInDateTime, afterCheckInDay);
        stayBookDateTime.setCheckOutDateTime(checkOutDateTime, afterCheckOutDay);

        bookDateTime.setValue(stayBookDateTime);
    }

    public StayBookDateTime getBookDateTime()
    {
        return bookDateTime.getValue();
    }

    public void setSuggest(StaySuggestV2 suggest)
    {
        this.suggest.setValue(suggest);
    }

    public StaySuggestV2 getSuggest()
    {
        return suggest.getValue();
    }

    public void setSuggestObserver(BaseActivity activity, Observer<StaySuggestV2> observer)
    {
        suggest.observe(activity, observer);
    }

    public void removeSuggestObserver(Observer<StaySuggestV2> observer)
    {
        suggest.removeObserver(observer);
    }

    public void setBookDateTimeObserver(BaseActivity activity, Observer<StayBookDateTime> observer)
    {
        bookDateTime.observe(activity, observer);
    }

    public void removeBookDateTimeObserver(Observer<StayBookDateTime> observer)
    {
        bookDateTime.removeObserver(observer);
    }
}