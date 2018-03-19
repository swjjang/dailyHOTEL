package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.CommonDateTime;
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
    Observable<Boolean> addStayIbSearchResultHistory(CommonDateTime commonDateTime, StayBookDateTime stayBookDateTime, StaySuggestV2 suggest);

    Observable<List<SearchResultHistory>> getStayIbSearchResultHistoryList(CommonDateTime commonDateTime, int maxCount);

    Observable<Boolean> deleteStayIbSearchResultHistory(StaySuggestV2 suggest);

    Observable<Boolean> addGourmetSearchResultHistory(CommonDateTime commonDateTime, GourmetBookDateTime gourmetBookDateTime, GourmetSuggestV2 suggest);

    Observable<List<SearchResultHistory>> getGourmetSearchResultHistoryList(CommonDateTime commonDateTime, int maxCount);

    Observable<Boolean> deleteGourmetSearchResultHistory(GourmetSuggestV2 suggest);

    Observable<Boolean> addStayObSearchResultHistory(CommonDateTime commonDateTime, StayBookDateTime stayBookDateTime, StayOutboundSuggest suggest, People people);

    Observable<List<SearchResultHistory>> getStayObSearchResultHistoryList(CommonDateTime commonDateTime, int maxCount);

    Observable<Boolean> deleteStayObSearchResultHistory(StayOutboundSuggest suggest);
}
