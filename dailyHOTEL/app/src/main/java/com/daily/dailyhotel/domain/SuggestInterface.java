package com.daily.dailyhotel.domain;

import android.support.v4.util.Pair;

import com.daily.dailyhotel.entity.GourmetSuggestV2;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.entity.StaySuggestV2;
import com.twoheart.dailyhotel.network.model.GourmetKeyword;
import com.twoheart.dailyhotel.network.model.StayKeyword;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public interface SuggestInterface
{
    Observable<List<StayOutboundSuggest>> getSuggestsByStayOutbound(String keyword);

    Observable<List<StayOutboundSuggest>> getRegionSuggestsByStayOutbound(String keyword);

    Observable<Pair<String, ArrayList<StayKeyword>>> getSuggestsByStayInbound(String checkInDate, int stays, String keyword);

    Observable<Pair<String, ArrayList<GourmetKeyword>>> getSuggestsByGourmet(String visitDate, String keyword);

    Observable<List<StayOutboundSuggest>> getPopularRegionSuggestsByStayOutbound();

    Observable<List<StaySuggestV2>> getSuggestByStayV2(String checkInDate, int stays, String keyword);

    Observable<List<GourmetSuggestV2>> getSuggestByGourmetV2(String reservationDate, String term);
}
