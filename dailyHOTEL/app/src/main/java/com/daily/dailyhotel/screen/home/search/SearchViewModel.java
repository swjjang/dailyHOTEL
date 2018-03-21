package com.daily.dailyhotel.screen.home.search;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.entity.StaySuggestV2;
import com.daily.dailyhotel.util.DailyIntentUtils;
import com.twoheart.dailyhotel.util.Constants;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchViewModel extends ViewModel
{
//    public CommonDateTime commonDateTime;
    public MutableLiveData<Constants.ServiceType> serviceType = new MutableLiveData<>();

    public SearchStayViewModel stayViewModel;
    public SearchStayOutboundViewModel stayOutboundViewModel;
    public SearchGourmetViewModel gourmetViewModel;

    static class SearchViewModelFactory implements ViewModelProvider.Factory
    {
        public SearchViewModelFactory()
        {
        }

        @NonNull
        @Override
        public SearchViewModel create(@NonNull Class modelClass)
        {
            SearchViewModel viewModel = new SearchViewModel();

            return viewModel;
        }
    }

    public void setServiceType(Constants.ServiceType serviceType)
    {
        if (this.serviceType.getValue() == serviceType)
        {
            return;
        }

        this.serviceType.setValue(serviceType);
    }

    public void setStayBookDateTime(Intent intent, String checkInDateTimeExtraName, String checkOutDateTimeExtraName) throws Exception
    {
        if (intent == null)
        {
            return;
        }

        if (DailyIntentUtils.hasIntentExtras(intent, checkInDateTimeExtraName, checkOutDateTimeExtraName) == true)
        {
            String checkInDateTime = intent.getStringExtra(checkInDateTimeExtraName);
            String checkOutDateTime = intent.getStringExtra(checkOutDateTimeExtraName);

            setStayBookDateTime(checkInDateTime, checkOutDateTime);
        }
    }

    public void setStayBookDateTime(String checkInDateTime, String checkOutDateTime) throws Exception
    {
        if (stayViewModel == null)
        {
            return;
        }

        stayViewModel.setBookDateTime(checkInDateTime, checkOutDateTime);
    }

    public void setStayBookDateTime(String checkInDateTime, int afterCheckInDay, String checkOutDateTime, int afterCheckOutDay) throws Exception
    {
        if (stayViewModel == null)
        {
            return;
        }

        stayViewModel.setBookDateTime(checkInDateTime, afterCheckInDay, checkOutDateTime, afterCheckOutDay);
    }

    public void setStaySuggest(StaySuggestV2 suggest)
    {
        stayViewModel.setSuggest(suggest);
    }

    public void setStayOutboundBookDateTime(Intent intent, String checkInDateTimeExtraName, String checkOutDateTimeExtraName) throws Exception
    {
        if (intent == null)
        {
            return;
        }

        if (DailyIntentUtils.hasIntentExtras(intent, checkInDateTimeExtraName, checkOutDateTimeExtraName) == true)
        {
            String checkInDateTime = intent.getStringExtra(checkInDateTimeExtraName);
            String checkOutDateTime = intent.getStringExtra(checkOutDateTimeExtraName);

            setStayOutboundBookDateTime(checkInDateTime, checkOutDateTime);
        }
    }

    public void setStayOutboundBookDateTime(String checkInDateTime, String checkOutDateTime) throws Exception
    {
        if (stayViewModel == null)
        {
            return;
        }

        stayOutboundViewModel.setBookDateTime(checkInDateTime, checkOutDateTime);
    }

    public void setStayOutboundBookDateTime(String checkInDateTime, int afterCheckInDay, String checkOutDateTime, int afterCheckOutDay) throws Exception
    {
        if (stayViewModel == null)
        {
            return;
        }

        stayOutboundViewModel.setBookDateTime(checkInDateTime, afterCheckInDay, checkOutDateTime, afterCheckOutDay);
    }

    public void setStayOutboundSuggest(StayOutboundSuggest suggest)
    {
        stayOutboundViewModel.setSuggest(suggest);
    }

    public void setGourmetBookDateTime(Intent intent, String bookDateTimeExtraName) throws Exception
    {
        if (intent == null)
        {
            return;
        }

        if (DailyIntentUtils.hasIntentExtras(intent, bookDateTimeExtraName) == true)
        {
            String bookDateTime = intent.getStringExtra(bookDateTimeExtraName);

            setGourmetBookDateTime(bookDateTime);
        }
    }

    public void setGourmetBookDateTime(String bookDateTime) throws Exception
    {
        if (gourmetViewModel == null)
        {
            return;
        }

        gourmetViewModel.setBookDateTime(bookDateTime);
    }
}