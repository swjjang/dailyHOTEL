package com.daily.dailyhotel.screen.home.stay.inbound.list;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.Category;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayFilter;
import com.daily.dailyhotel.entity.StayRegion;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.model.DailyCategoryType;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayTabViewModel extends ViewModel
{
    MutableLiveData<CommonDateTime> commonDateTime = new MutableLiveData<>();
    MutableLiveData<StayBookDateTime> stayBookDateTime = new MutableLiveData<>();
    MutableLiveData<StayFilter> stayFilter = new MutableLiveData<>();
    MutableLiveData<StayRegion> stayRegion = new MutableLiveData<>();
    MutableLiveData<Category> selectedCategory = new MutableLiveData<>();
    MutableLiveData<Location> location = new MutableLiveData<>();
    MutableLiveData<StayTabPresenter.ViewType> viewType = new MutableLiveData<>();
    DailyCategoryType categoryType;

    static class StayTabViewModelFactory implements ViewModelProvider.Factory
    {
        private Context mContext;

        public StayTabViewModelFactory(Context context)
        {
            mContext = context;
        }

        @NonNull
        @Override
        public StayTabViewModel create(@NonNull Class modelClass)
        {
            StayTabViewModel stayTabViewModel = new StayTabViewModel();

            stayTabViewModel.stayFilter.setValue(new StayFilter().resetFilter());
            stayTabViewModel.viewType.setValue(StayTabPresenter.ViewType.LIST);

            if (mContext == null)
            {
                stayTabViewModel.selectedCategory.setValue(Category.ALL);
            } else
            {
                String oldCategoryCode = DailyPreference.getInstance(mContext).getStayCategoryCode();
                String oldCategoryName = DailyPreference.getInstance(mContext).getStayCategoryName();

                if (DailyTextUtils.isTextEmpty(oldCategoryCode, oldCategoryName) == false)
                {
                    stayTabViewModel.selectedCategory.setValue(new Category(oldCategoryCode, oldCategoryName));
                } else
                {
                    stayTabViewModel.selectedCategory.setValue(Category.ALL);
                }
            }

            stayTabViewModel.categoryType = DailyCategoryType.STAY_ALL;

            return stayTabViewModel;
        }
    }
}
