package com.daily.dailyhotel.domain;

import android.content.Context;

import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetSuggest;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.repository.local.model.GourmetSearchResultHistory;
import com.daily.dailyhotel.repository.local.model.StayObSearchResultHistory;
import com.daily.dailyhotel.repository.local.model.StaySearchResultHistory;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2018. 3. 16..
 */

public interface SearchLocalInterface
{
    Observable<Boolean> addStayIbSearchResultHistory(Context context, CommonDateTime commonDateTime, StayBookDateTime stayBookDateTime, StaySuggest suggest);

    Observable<List<StaySearchResultHistory>> getStayIbSearchResultHistoryList(Context context, CommonDateTime commonDateTime, int maxCount);

    Observable<Boolean> deleteStayIbSearchResultHistory(Context context, StaySuggest suggest);

    Observable<Boolean> addGourmetSearchResultHistory(Context context, CommonDateTime commonDateTime, GourmetBookDateTime gourmetBookDateTime, GourmetSuggest suggest);

    Observable<List<GourmetSearchResultHistory>> getGourmetSearchResultHistoryList(Context context, CommonDateTime commonDateTime, int maxCount);

    Observable<Boolean> deleteGourmetSearchResultHistory(Context context, GourmetSuggest suggest);

    Observable<Boolean> addStayObSearchResultHistory(Context context, CommonDateTime commonDateTime, StayBookDateTime stayBookDateTime, StayOutboundSuggest suggest, People people);

    Observable<List<StayObSearchResultHistory>> getStayObSearchResultHistoryList(Context context, CommonDateTime commonDateTime, int maxCount);

    Observable<Boolean> deleteStayObSearchResultHistory(Context context, StayOutboundSuggest suggest);
}
