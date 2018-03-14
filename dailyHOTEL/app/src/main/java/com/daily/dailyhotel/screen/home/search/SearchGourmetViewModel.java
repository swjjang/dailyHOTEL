package com.daily.dailyhotel.screen.home.search;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetSuggestV2;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchGourmetViewModel extends ViewModel
{
    public MutableLiveData<GourmetBookDateTime> bookDateTime = new MutableLiveData<>();
    public MutableLiveData<GourmetSuggestV2> suggest = new MutableLiveData<>();
    public String inputKeyword;

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

    public void setBookDateTime(String bookDateTime) throws Exception
    {
        this.bookDateTime.setValue(new GourmetBookDateTime(bookDateTime));
    }

    public GourmetBookDateTime getBookDateTime()
    {
        return bookDateTime.getValue();
    }
}