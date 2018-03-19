package com.daily.dailyhotel.screen.home.search;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.entity.StaySuggestV2;
import com.daily.dailyhotel.parcel.StaySuggestParcel;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchStayViewModel extends ViewModel
{
    public MutableLiveData<StayBookDateTime> bookDateTime = new MutableLiveData<>();
    public MutableLiveData<StaySuggest> suggest = new MutableLiveData<>();
    public String inputKeyword;

    public static class SearchStayViewModelFactory implements ViewModelProvider.Factory
    {
        public SearchStayViewModelFactory()
        {
        }

        @NonNull
        @Override
        public SearchStayViewModel create(@NonNull Class modelClass)
        {
            SearchStayViewModel viewModel = new SearchStayViewModel();

            return viewModel;
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

    public void setSuggest(StaySuggest suggest)
    {
        this.suggest.setValue(suggest);
    }

    public void setSuggest(StaySuggestParcel suggestParcel)
    {
        if (suggestParcel == null)
        {
            return;
        }

        setSuggest(suggestParcel.getSuggest());
    }

    public StaySuggestV2 getSuggest()
    {

    }
}