package com.daily.dailyhotel.screen.home.search;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StaySuggest;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchStayViewModel extends ViewModel
{
    public MutableLiveData<StayBookDateTime> bookDateTime = new MutableLiveData<>();
    public MutableLiveData<StaySuggest> suggest = new MutableLiveData<>();
    public String inputString;

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
}