package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.GourmetSuggestV2;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.entity.StaySuggestV2;

import java.util.List;

import io.reactivex.Observable;

public interface SuggestInterface
{
    Observable<List<StayOutboundSuggest>> getSuggestsByStayOutbound(String keyword);

    Observable<List<StayOutboundSuggest>> getRegionSuggestsByStayOutbound(String keyword);

    Observable<List<StayOutboundSuggest>> getPopularRegionSuggestsByStayOutbound();

    Observable<List<StaySuggestV2>> getSuggestByStay(String checkInDate, int stays, String keyword);

    Observable<List<GourmetSuggestV2>> getSuggestsByGourmet(String reservationDate, String term);
}
