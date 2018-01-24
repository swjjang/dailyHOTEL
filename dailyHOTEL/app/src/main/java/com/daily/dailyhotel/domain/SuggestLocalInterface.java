package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.StayOutboundSuggest;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2017. 9. 29..
 */

public interface SuggestLocalInterface
{
    Observable addSuggestDb(StayOutboundSuggest stayOutboundSuggest, String keyword);

    Observable<StayOutboundSuggest> getRecentlySuggest();

    Observable<List<StayOutboundSuggest>> getRecentlySuggestList();

    Observable<String> getRecentlySuggestKeyword(final long id);

    Observable deleteAllRecentlySuggest();
}
