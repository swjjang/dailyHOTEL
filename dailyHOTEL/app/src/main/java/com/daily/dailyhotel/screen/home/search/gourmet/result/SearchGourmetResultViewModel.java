package com.daily.dailyhotel.screen.home.search.gourmet.result;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetFilter;
import com.daily.dailyhotel.entity.GourmetSuggest;
import com.daily.dailyhotel.screen.home.search.SearchGourmetViewModel;
import com.daily.dailyhotel.util.DailyIntentUtils;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchGourmetResultViewModel extends ViewModel
{
    public SearchGourmetViewModel searchViewModel;
    private CommonDateTime mCommonDateTime;

    private MutableLiveData<SearchGourmetResultTabPresenter.ViewType> mViewType = new MutableLiveData<>();

    private MutableLiveData<GourmetFilter> mFilter = new MutableLiveData<>();
    public Location filterLocation;

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

            searchViewModel.setViewType(SearchGourmetResultTabPresenter.ViewType.LIST);
            searchViewModel.setFilter(new GourmetFilter().reset());

            return searchViewModel;
        }
    }

    public void setViewType(@NonNull SearchGourmetResultTabPresenter.ViewType viewType)
    {
        if (viewType == null)
        {
            throw new NullPointerException("viewType == null");
        }

        mViewType.setValue(viewType);
    }

    public SearchGourmetResultTabPresenter.ViewType getViewType()
    {
        return mViewType.getValue();
    }

    public void setViewTypeObserver(BaseActivity baseActivity, Observer<SearchGourmetResultTabPresenter.ViewType> observer)
    {
        mViewType.observe(baseActivity, observer);
    }

    public void removeViewTypeObserver(Observer<SearchGourmetResultTabPresenter.ViewType> observer)
    {
        mViewType.removeObserver(observer);
    }

    public GourmetBookDateTime getBookDateTime()
    {
        return searchViewModel == null ? null : searchViewModel.getBookDateTime();
    }

    public void setCommonDateTime(CommonDateTime commonDateTime)
    {
        mCommonDateTime = commonDateTime;
    }

    public CommonDateTime getCommonDateTime()
    {
        return mCommonDateTime;
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

    public void setBookDateTime(String bookDateTime) throws Exception
    {
        if (searchViewModel == null)
        {
            return;
        }

        searchViewModel.setBookDateTime(bookDateTime);
    }

    public void setSuggest(@NonNull GourmetSuggest suggest)
    {
        if (searchViewModel == null)
        {
            return;
        }

        if (suggest == null)
        {
            throw new NullPointerException("suggest == null");
        }

        searchViewModel.setSuggest(suggest);
    }

    public GourmetSuggest getSuggest()
    {
        return searchViewModel == null ? null : searchViewModel.getSuggest();
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
        return mFilter.getValue().isDistanceSort();
    }

    public GourmetFilter getFilter()
    {
        return mFilter.getValue();
    }

    public void setFilter(@NonNull GourmetFilter filter)
    {
        if (filter == null)
        {
            throw new NullPointerException("filter == null");
        }

        mFilter.setValue(filter);
    }

    public void setFilterObserver(BaseActivity baseActivity, Observer<GourmetFilter> observer)
    {
        mFilter.observe(baseActivity, observer);
    }

    public void removeFilterObserver(Observer<GourmetFilter> observer)
    {
        mFilter.removeObserver(observer);
    }
}