package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetSuggestV2;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.entity.StaySuggestV2;
import com.daily.dailyhotel.repository.local.model.SearchResultHistory;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2018. 3. 16..
 */

public interface SearchLocalInterface
{
    Observable<Boolean> addStayIbSearchResultHistory(StayBookDateTime stayBookDateTime, StaySuggestV2 suggest);

    Observable<List<SearchResultHistory>> getStayIbSearchResultHistory(StayBookDateTime stayBookDateTime, int maxCount);

    Observable<Boolean> deleteStayIbSearchResultHistory(StaySuggestV2 suggest);

    Observable<Boolean> addGourmetSearchResultHistory(GourmetBookDateTime gourmetBookDateTime, GourmetSuggestV2 suggest);

    Observable<List<SearchResultHistory>> getGourmetSearchResultHistory(GourmetBookDateTime gourmetBookDateTime, int maxCount);

    Observable<Boolean> deleteGourmetSearchResultHistory(GourmetSuggestV2 suggest);

    Observable<Boolean> addStayObSearchResultHistory(StayBookDateTime stayBookDateTime, StayOutboundSuggest suggest, People people);

    Observable<List<SearchResultHistory>> getStayObSearchResultHistory(StayBookDateTime stayBookDateTime, int maxCount);

    Observable<Boolean> deleteStayObSearchResultHistory(StayOutboundSuggest suggest);
}
