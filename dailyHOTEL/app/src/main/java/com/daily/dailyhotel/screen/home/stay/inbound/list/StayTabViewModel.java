package com.daily.dailyhotel.screen.home.stay.inbound.list;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.Category;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayFilter;
import com.daily.dailyhotel.entity.StayRegion;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.util.DailyIntentUtils;
import com.twoheart.dailyhotel.model.DailyCategoryType;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayTabViewModel extends ViewModel
{
    MutableLiveData<CommonDateTime> commonDateTime = new MutableLiveData<>();
    MutableLiveData<StayBookDateTime> bookDateTime = new MutableLiveData<>();
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

    public void setBookDateTime(String checkInDateTime, String checkOutDateTime) throws Exception
    {
        bookDateTime.setValue(new StayBookDateTime(checkInDateTime, checkOutDateTime));
    }

    public void setBookDateTime(String checkInDateTime, int afterCheckInDay, String checkOutDateTime, int afterCheckOutDay) throws Exception
    {
        StayBookDateTime stayBookDateTime = new StayBookDateTime();
        stayBookDateTime.setCheckInDateTime(checkInDateTime, afterCheckInDay);
        stayBookDateTime.setCheckOutDateTime(checkOutDateTime, afterCheckOutDay);

        bookDateTime.setValue(stayBookDateTime);
    }

    public StayBookDateTime getBookDateTime()
    {
        return bookDateTime.getValue();
    }

    public void setBookDateTime(Intent intent, String checkInDateTimeExtraName, String checkOutDateTimeExtraName) throws Exception
    {
        if (intent == null)
        {
            return;
        }

        if (DailyIntentUtils.hasIntentExtras(intent, checkInDateTimeExtraName, checkOutDateTimeExtraName) == true)
        {
            String checkInDateTime = intent.getStringExtra(checkInDateTimeExtraName);
            String checkOutDateTime = intent.getStringExtra(checkOutDateTimeExtraName);

            setBookDateTime(checkInDateTime, checkOutDateTime);
        }
    }

    public void setCategoryType(Intent intent, DailyCategoryType defaultCategoryType)
    {
        if (intent == null)
        {
            categoryType = defaultCategoryType;
        } else
        {
            try
            {
                categoryType = DailyCategoryType.valueOf(intent.getStringExtra(StayTabActivity.INTENT_EXTRA_DATA_CATEGORY_TYPE));
            } catch (Exception e)
            {
                categoryType = defaultCategoryType;
            }
        }
    }
}
