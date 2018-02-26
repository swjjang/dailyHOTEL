package com.daily.dailyhotel.screen.home.search;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.CommonDateTime;
import com.twoheart.dailyhotel.util.Constants;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchViewModel extends ViewModel
{
    public MutableLiveData<CommonDateTime> commonDateTime = new MutableLiveData<>();
    public MutableLiveData<Constants.ServiceType> serviceType = new MutableLiveData<>();

    public SearchStayViewModel stayViewModel;
    public SearchStayOutboundViewModel stayOutboundViewModel;
    public SearchGourmetViewModel gourmetViewModel;

    static class SearchViewModelFactory implements ViewModelProvider.Factory
    {
        public SearchViewModelFactory()
        {
        }

        @NonNull
        @Override
        public SearchViewModel create(@NonNull Class modelClass)
        {
            SearchViewModel searchViewModel = new SearchViewModel();

            return searchViewModel;
        }
    }

    public void setStayBookDateTime(String checkInDateTime, String checkOutDateTime) throws Exception
    {
        if (stayViewModel == null)
        {
            return;
        }

        stayViewModel.setBookDateTime(checkInDateTime, checkOutDateTime);
    }
}