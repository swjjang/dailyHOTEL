package com.daily.dailyhotel.screen.home.search.gourmet.result;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetFilter;
import com.daily.dailyhotel.entity.GourmetSuggestV2;
import com.daily.dailyhotel.screen.home.search.SearchGourmetViewModel;
import com.daily.dailyhotel.util.DailyIntentUtils;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchGourmetResultViewModel extends ViewModel
{
    public SearchGourmetViewModel searchViewModel;

    public MutableLiveData<CommonDateTime> commonDateTime = new MutableLiveData<>();
    public MutableLiveData<GourmetFilter> filter = new MutableLiveData<>();
    public MutableLiveData<SearchGourmetResultTabPresenter.ViewType> viewType = new MutableLiveData<>();
    public MutableLiveData<Location> location = new MutableLiveData<>();

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

            searchViewModel.filter.setValue(new GourmetFilter().reset());

            return searchViewModel;
        }
    }

    public GourmetBookDateTime getBookDateTime()
    {
        return searchViewModel == null ? null : searchViewModel.getBookDateTime();
    }

    public void setBookDateTime(Intent intent, String bookDateTimeExtraName) throws Exception
    {
        if (intent == null || searchViewModel == null)
        {
            return;
        }

        if (DailyIntentUtils.hasIntentExtras(intent, bookDateTimeExtraName) == true)
        {
            String bookDateTime = intent.getStringExtra(bookDateTimeExtraName);

            searchViewModel.setBookDateTime(bookDateTime);
        }
    }

    public void setSuggest(GourmetSuggestV2 suggest)
    {
        if (searchViewModel == null)
        {
            return;
        }

        searchViewModel.suggest.setValue(suggest);
    }

    public GourmetSuggestV2 getSuggest()
    {
        return searchViewModel == null ? null : searchViewModel.suggest.getValue();
    }

    public void setInputKeyword(String inputKeyword)
    {
        if (searchViewModel == null)
        {
            return;
        }

        searchViewModel.inputKeyword = inputKeyword;
    }

    public String getInputKeyword()
    {
        return searchViewModel == null ? null : searchViewModel.inputKeyword;
    }

    public boolean isDistanceSort()
    {
        return filter.getValue() == null ? false : filter.getValue().isDistanceSort();
    }
}