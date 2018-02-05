package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.StayOutboundSuggest;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2017. 9. 29..
 */

public interface SuggestLocalInterface
{
    Observable addStayOutboundSuggestDb(StayOutboundSuggest stayOutboundSuggest, String keyword);

    Observable<StayOutboundSuggest> getRecentlyStayOutboundSuggest();

    Observable<List<StayOutboundSuggest>> getRecentlyStayOutboundSuggestList();

    Observable<String> getRecentlyStayOutboundSuggestKeyword(final long id);

    Observable<Boolean> deleteAllRecentlyStayOutboundSuggest();

    Observable<Boolean> deleteRecentlyStayOutboundSuggest(long id);
}
