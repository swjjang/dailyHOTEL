package com.daily.dailyhotel.screen.home.search;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundSuggest;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchStayOutboundViewModel extends ViewModel
{
    public MutableLiveData<StayBookDateTime> bookDateTime = new MutableLiveData<>();
    public MutableLiveData<StayOutboundSuggest> suggest = new MutableLiveData<>();
    public MutableLiveData<People> people = new MutableLiveData<>();
    public String inputKeyword;
    public String clickType;

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
}