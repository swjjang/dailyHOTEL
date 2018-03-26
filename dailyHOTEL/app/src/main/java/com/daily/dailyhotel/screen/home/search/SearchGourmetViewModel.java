package com.daily.dailyhotel.screen.home.search;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetSuggest;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchGourmetViewModel extends ViewModel
{
    private MutableLiveData<GourmetBookDateTime> bookDateTime = new MutableLiveData<>();
    private MutableLiveData<GourmetSuggest> suggest = new MutableLiveData<>();
    public String inputKeyword;
    public float radius;

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

    public void setSuggest(GourmetSuggest suggest)
    {
        this.suggest.setValue(suggest);
    }

    public GourmetSuggest getSuggest()
    {
        return suggest.getValue();
    }

    public void setSuggestObserver(BaseActivity activity, Observer<GourmetSuggest> observer)
    {
        suggest.observe(activity, observer);
    }

    public void removeSuggestObserver(Observer<GourmetSuggest> observer)
    {
        suggest.removeObserver(observer);
    }

    public void setBookDateTimeObserver(BaseActivity activity, Observer<GourmetBookDateTime> observer)
    {
        bookDateTime.observe(activity, observer);
    }

    public void removeBookDateTimeObserver(Observer<GourmetBookDateTime> observer)
    {
        bookDateTime.removeObserver(observer);
    }
}