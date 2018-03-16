package com.daily.dailyhotel.repository.local;

import android.content.Context;

import com.daily.dailyhotel.domain.SearchLocalInterface;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetSuggestV2;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.entity.StaySuggestV2;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2018. 3. 16..
 */

public class SearchLocalImpl implements SearchLocalInterface
{
    private Context mContext;

    public SearchLocalImpl(Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<Boolean> addStayIbSearchResultHistory(CommonDateTime commonDateTime, StayBookDateTime stayBookDateTime, StaySuggestV2 suggest)
    {
        return null;
    }

    @Override
    public Observable<Boolean> getStayIbSearchResultHistory(CommonDateTime commonDateTime, int maxCount)
    {
        return null;
    }

    @Override
    public Observable<Boolean> deleteStayIbSearchResultHistory(StaySuggestV2 suggest)
    {
        return null;
    }

    @Override
    public Observable<Boolean> addGourmetSearchResultHistory(CommonDateTime commonDateTime, GourmetBookDateTime gourmetBookDateTime, GourmetSuggestV2 suggest)
    {
        return null;
    }

    @Override
    public Observable<Boolean> getGourmetSearchResultHistory(CommonDateTime commonDateTime, int maxCount)
    {
        return null;
    }

    @Override
    public Observable<Boolean> deleteGourmetSearchResultHistory(GourmetSuggestV2 suggest)
    {
        return null;
    }

    @Override
    public Observable<Boolean> addStayObSearchResultHistory(CommonDateTime commonDateTime, StayBookDateTime stayBookDateTime, StayOutboundSuggest suggest, People people)
    {
        return null;
    }

    @Override
    public Observable<Boolean> getstayObSearchResultHistory(CommonDateTime commonDateTime, int maxCount)
    {
        return null;
    }

    @Override
    public Observable<Boolean> deleteStayObSearchResultHistory(StayOutboundSuggest suggest)
    {
        return null;
    }
}
