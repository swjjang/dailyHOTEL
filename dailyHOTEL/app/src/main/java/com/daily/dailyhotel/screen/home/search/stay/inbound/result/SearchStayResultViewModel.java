package com.daily.dailyhotel.screen.home.search.stay.inbound.result;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.location.Location;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.entity.Category;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayFilter;
import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.screen.home.search.SearchStayViewModel;
import com.twoheart.dailyhotel.model.DailyCategoryType;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchStayResultViewModel extends ViewModel
{
    public SearchStayViewModel searchViewModel;
    private CommonDateTime mCommonDateTime;

    private MutableLiveData<SearchStayResultTabPresenter.ViewType> mViewType = new MutableLiveData<>();
    private MutableLiveData<Category> mCategory = new MutableLiveData<>();
    private MutableLiveData<StayFilter> mFilter = new MutableLiveData<>();
    public Location filterLocation;
    public DailyCategoryType categoryType;
    public boolean resetCategory;

    static class SearchStayViewModelFactory implements ViewModelProvider.Factory
    {
        public SearchStayViewModelFactory()
        {
        }

        @NonNull
        @Override
        public SearchStayResultViewModel create(@NonNull Class modelClass)
        {
            SearchStayResultViewModel searchViewModel = new SearchStayResultViewModel();

            searchViewModel.setViewType(SearchStayResultTabPresenter.ViewType.LIST);
            searchViewModel.setFilter(new StayFilter().reset());
            searchViewModel.resetCategory = true;

            return searchViewModel;
        }
    }

    public void setViewType(@NonNull SearchStayResultTabPresenter.ViewType viewType)
    {
        if (viewType == null)
        {
            throw new NullPointerException("viewType == null");
        }

        mViewType.setValue(viewType);
    }

    public SearchStayResultTabPresenter.ViewType getViewType()
    {
        return mViewType.getValue();
    }

    public void setViewTypeObserver(BaseActivity baseActivity, Observer<SearchStayResultTabPresenter.ViewType> observer)
    {
        mViewType.observe(baseActivity, observer);
    }

    public void removeViewTypeObserver(Observer<SearchStayResultTabPresenter.ViewType> observer)
    {
        mViewType.removeObserver(observer);
    }

    public StayBookDateTime getBookDateTime()
    {
        return searchViewModel == null ? null : searchViewModel.getBookDateTime();
    }

    public void setBookDateTimeObserver(BaseActivity activity, Observer<StayBookDateTime> observer)
    {
        searchViewModel.setBookDateTimeObserver(activity, observer);
    }

    public void removeBookDateTimeObserver(Observer<StayBookDateTime> observer)
    {
        searchViewModel.removeBookDateTimeObserver(observer);
    }

    public void setRadiusObserver(BaseActivity activity, Observer<Float> observer)
    {
        searchViewModel.setRadiusObserver(activity, observer);
    }

    public void removeRadiusObserver(Observer<Float> observer)
    {
        searchViewModel.removeRadiusObserver(observer);
    }

    public void setCommonDateTime(CommonDateTime commonDateTime)
    {
        mCommonDateTime = commonDateTime;
    }

    public CommonDateTime getCommonDateTime()
    {
        return mCommonDateTime;
    }

    public void setBookDateTime(String checkInDateTime, String checkOutDateTime) throws Exception
    {
        if (searchViewModel == null)
        {
            return;
        }

        searchViewModel.setBookDateTime(checkInDateTime, checkOutDateTime);
    }

    public void setSuggest(@NonNull StaySuggest suggest)
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

    public StaySuggest getSuggest()
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

    public Category getCategory()
    {
        return mCategory.getValue();
    }

    public void setCategory(@NonNull Category category)
    {
        if (category == null)
        {
            throw new NullPointerException("filter == null");
        }

        mCategory.setValue(category);
    }

    public boolean isDistanceSort()
    {
        return mFilter.getValue().isDistanceSort();
    }

    public StayFilter getFilter()
    {
        return mFilter.getValue();
    }

    public void setFilter(@NonNull StayFilter filter)
    {
        if (filter == null)
        {
            throw new NullPointerException("filter == null");
        }

        mFilter.setValue(filter);
    }

    public void setFilterObserver(BaseActivity baseActivity, Observer<StayFilter> observer)
    {
        mFilter.observe(baseActivity, observer);
    }

    public void removeFilterObserver(Observer<StayFilter> observer)
    {
        mFilter.removeObserver(observer);
    }
}