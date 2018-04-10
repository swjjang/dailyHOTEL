package com.daily.dailyhotel.domain;

import android.content.Context;

import com.daily.dailyhotel.entity.GourmetSuggest;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.entity.StaySuggest;

import java.util.List;

import io.reactivex.Observable;

public interface SuggestInterface
{
    Observable<List<StayOutboundSuggest>> getSuggestsByStayOutbound(Context context, String keyword);

    Observable<List<StayOutboundSuggest>> getPopularRegionSuggestsByStayOutbound(Context context);

    Observable<List<StaySuggest>> getSuggestByStay(Context context, String checkInDate, int stays, String keyword);

    Observable<List<GourmetSuggest>> getSuggestsByGourmet(Context context, String reservationDate, String term);
}
