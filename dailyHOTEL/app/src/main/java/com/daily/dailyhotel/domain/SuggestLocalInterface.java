package com.daily.dailyhotel.domain;

import android.content.Context;

import com.daily.dailyhotel.entity.GourmetSuggest;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.entity.StaySuggest;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2017. 9. 29..
 */

public interface SuggestLocalInterface
{
    Observable addStayOutboundSuggestDb(Context context, StayOutboundSuggest stayOutboundSuggest, String keyword);

    Observable<StayOutboundSuggest> getRecentlyStayOutboundSuggest(Context context);

    Observable<List<StayOutboundSuggest>> getRecentlyStayOutboundSuggestList(Context context, int maxCount);

    Observable<String> getRecentlyStayOutboundSuggestKeyword(Context context, final long id);

    Observable<Boolean> deleteAllRecentlyStayOutboundSuggest(Context context);

    Observable<Boolean> deleteRecentlyStayOutboundSuggest(Context context, long id);

    Observable<Boolean> addRecentlyGourmetSuggest(Context context, GourmetSuggest gourmetSuggest, String keyword);

    Observable<List<GourmetSuggest>> getRecentlyGourmetSuggestList(Context context, int maxCount);

    Observable<Boolean> deleteRecentlyGourmetSuggest(Context context, GourmetSuggest gourmetSuggest);

    Observable<Boolean> addRecentlyStaySuggest(Context context, StaySuggest staySuggest, String keyword);

    Observable<List<StaySuggest>> getRecentlyStaySuggestList(Context context, int maxCount);

    Observable<Boolean> deleteRecentlyStaySuggest(Context context, StaySuggest staySuggest);
}
