package com.daily.dailyhotel.screen.home.search.gourmet.result;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.screen.home.search.SearchGourmetViewModel;
import com.daily.dailyhotel.util.DailyIntentUtils;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchGourmetResultViewModel extends ViewModel
{
    public MutableLiveData<CommonDateTime> commonDateTime = new MutableLiveData<>();

    public SearchGourmetViewModel gourmetViewModel;
    public MutableLiveData<SearchGourmetResultTabPresenter.ViewType> viewType = new MutableLiveData<>();

    static class SearchGourmetViewModelFactory implements ViewModelProvider.Factory
    {
        public SearchGourmetViewModelFactory()
        {
        }

        @NonNull
        @Override
        public SearchGourmetResultViewModel create(@NonNull Class modelClass)
        {
            SearchGourmetResultViewModel searchViewModel = new SearchGourmetResultViewModel();

            return searchViewModel;
        }
    }

    public void setBookDateTime(Intent intent, String bookDateTimeExtraName) throws Exception
    {
        if (intent == null)
        {
            throw new NullPointerException("intent == null");
        }

        if (DailyIntentUtils.hasIntentExtras(intent, bookDateTimeExtraName) == true)
        {
            String bookDateTime = intent.getStringExtra(bookDateTimeExtraName);

            setBookDateTime(bookDateTime);
        }
    }

    public void setBookDateTime(String bookDateTime) throws Exception
    {
        if (gourmetViewModel == null)
        {
            return;
        }

        gourmetViewModel.setBookDateTime(bookDateTime);
    }
}