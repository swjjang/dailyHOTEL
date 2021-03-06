package com.daily.dailyhotel.screen.common.area.stay.inbound;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.StayAreaGroup;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayRegion;
import com.daily.dailyhotel.entity.StaySubwayAreaGroup;
import com.twoheart.dailyhotel.model.DailyCategoryType;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayAreaViewModel extends ViewModel
{
    MutableLiveData<List<StayAreaGroup>> areaList = new MutableLiveData<>();
    MutableLiveData<LinkedHashMap<Area, List<StaySubwayAreaGroup>>> subwayMap = new MutableLiveData<>();
    MutableLiveData<StayBookDateTime> bookDateTime = new MutableLiveData<>();
    MutableLiveData<StayRegion> previousArea = new MutableLiveData<>();
    MutableLiveData<Boolean> isAgreeTermsOfLocation = new MutableLiveData<>();
    DailyCategoryType categoryType;

    static class StayAreaViewModelFactory implements ViewModelProvider.Factory
    {
        public StayAreaViewModelFactory()
        {
        }

        @NonNull
        @Override
        public StayAreaViewModel create(@NonNull Class modelClass)
        {
            StayAreaViewModel viewModel = new StayAreaViewModel();

            viewModel.categoryType = DailyCategoryType.STAY_ALL;

            return viewModel;
        }
    }
}
