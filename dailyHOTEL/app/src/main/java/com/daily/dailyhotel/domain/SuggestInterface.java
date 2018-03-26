package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.GourmetSuggest;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.entity.StaySuggest;

import java.util.List;

import io.reactivex.Observable;

public interface SuggestInterface
{
    Observable<List<StayOutboundSuggest>> getSuggestsByStayOutbound(String keyword);

    Observable<List<StayOutboundSuggest>> getRegionSuggestsByStayOutbound(String keyword);

    Observable<List<StayOutboundSuggest>> getPopularRegionSuggestsByStayOutbound();

    Observable<List<StaySuggest>> getSuggestByStay(String checkInDate, int stays, String keyword);

    Observable<List<GourmetSuggest>> getSuggestsByGourmet(String reservationDate, String term);
}
