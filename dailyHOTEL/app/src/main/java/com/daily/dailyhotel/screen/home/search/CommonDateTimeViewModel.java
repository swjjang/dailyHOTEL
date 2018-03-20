package com.daily.dailyhotel.screen.home.search;


import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.CommonDateTime;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class CommonDateTimeViewModel extends ViewModel
{
    public CommonDateTime commonDateTime;

    public static class CommonDateTimeViewModelFactory implements ViewModelProvider.Factory
    {
        public CommonDateTimeViewModelFactory()
        {
        }

        @NonNull
        @Override
        public CommonDateTimeViewModel create(@NonNull Class modelClass)
        {
            CommonDateTimeViewModel viewModel = new CommonDateTimeViewModel();

            return viewModel;
        }
    }
}