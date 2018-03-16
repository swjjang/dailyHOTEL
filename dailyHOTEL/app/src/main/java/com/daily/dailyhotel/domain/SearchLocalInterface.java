package com.daily.dailyhotel.domain;

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

public interface SearchLocalInterface
{
    Observable<Boolean> addStayIbSearchResultHistory(CommonDateTime commonDateTime, StayBookDateTime stayBookDateTime, StaySuggestV2 suggest);

    Observable<Boolean> getStayIbSearchResultHistory(CommonDateTime commonDateTime, int maxCount);

    Observable<Boolean> deleteStayIbSearchResultHistory(StaySuggestV2 suggest);

    Observable<Boolean> addGourmetSearchResultHistory(CommonDateTime commonDateTime, GourmetBookDateTime gourmetBookDateTime, GourmetSuggestV2 suggest);

    Observable<Boolean> getGourmetSearchResultHistory(CommonDateTime commonDateTime, int maxCount);

    Observable<Boolean> deleteGourmetSearchResultHistory(GourmetSuggestV2 suggest);

    Observable<Boolean> addStayObSearchResultHistory(CommonDateTime commonDateTime, StayBookDateTime stayBookDateTime, StayOutboundSuggest suggest, People people);

    Observable<Boolean> getstayObSearchResultHistory(CommonDateTime commonDateTime, int maxCount);

    Observable<Boolean> deleteStayObSearchResultHistory(StayOutboundSuggest suggest);
}
