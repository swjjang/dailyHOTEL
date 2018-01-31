package com.daily.dailyhotel.screen.home.search;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.entity.StaySuggest;
import com.twoheart.dailyhotel.util.Constants;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchViewModel extends ViewModel
{
    public MutableLiveData<CommonDateTime> commonDateTime = new MutableLiveData<>();
    public MutableLiveData<Constants.ServiceType> serviceType = new MutableLiveData<>();

    // Stay
    public SearchStayViewModel stayViewModel;

    // Stayoutbound
    public SearchStayOutboundViewModel stayOutboundViewModel;

    // Gourmet
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

    public static class SearchStayViewModel extends ViewModel
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
    }

    public static class SearchStayOutboundViewModel extends ViewModel
    {
        public MutableLiveData<StayBookDateTime> bookDateTime = new MutableLiveData<>();
        public MutableLiveData<StayOutboundSuggest> suggest = new MutableLiveData<>();
        public MutableLiveData<People> people = new MutableLiveData<>();
        public String inputString;

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
    }

    public static class SearchGourmetViewModel extends ViewModel
    {
        public MutableLiveData<GourmetBookDateTime> bookDateTime = new MutableLiveData<>();
        public MutableLiveData<String> suggest = new MutableLiveData<>();
        public String inputString;

        public static class SearchGourmetViewModelFactory implements ViewModelProvider.Factory
        {
            public SearchGourmetViewModelFactory()
            {
            }

            @NonNull
            @Override
            public SearchGourmetViewModel create(@NonNull Class modelClass)
            {
                SearchGourmetViewModel viewModel = new SearchGourmetViewModel();

                return viewModel;
            }
        }

    }
}


