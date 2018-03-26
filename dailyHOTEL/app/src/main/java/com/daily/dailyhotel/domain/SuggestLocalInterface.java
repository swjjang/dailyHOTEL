package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.GourmetSuggestV2;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.entity.StaySuggest;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2017. 9. 29..
 */

public interface SuggestLocalInterface
{
    Observable addStayOutboundSuggestDb(StayOutboundSuggest stayOutboundSuggest, String keyword);

    Observable<StayOutboundSuggest> getRecentlyStayOutboundSuggest();

    Observable<List<StayOutboundSuggest>> getRecentlyStayOutboundSuggestList(int maxCount);

    Observable<String> getRecentlyStayOutboundSuggestKeyword(final long id);

    Observable<Boolean> deleteAllRecentlyStayOutboundSuggest();

    Observable<Boolean> deleteRecentlyStayOutboundSuggest(long id);

    Observable<Boolean> addRecentlyGourmetSuggest(GourmetSuggestV2 gourmetSuggest, String keyword);

    Observable<List<GourmetSuggestV2>> getRecentlyGourmetSuggestList(int maxCount);

    Observable<Boolean> deleteRecentlyGourmetSuggest(GourmetSuggestV2 gourmetSuggest);

    Observable<Boolean> addRecentlyStaySuggest(StaySuggest staySuggest, String keyword);

    Observable<List<StaySuggest>> getRecentlyStaySuggestList(int maxCount);

    Observable<Boolean> deleteRecentlyStaySuggest(StaySuggest staySuggest);
}
