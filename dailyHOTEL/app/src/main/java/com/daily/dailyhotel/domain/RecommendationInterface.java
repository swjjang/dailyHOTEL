package com.daily.dailyhotel.domain;

import android.content.Context;

import com.twoheart.dailyhotel.network.model.Recommendation;
import com.twoheart.dailyhotel.network.model.RecommendationGourmet;
import com.twoheart.dailyhotel.network.model.RecommendationPlaceList;
import com.twoheart.dailyhotel.network.model.RecommendationStay;

import java.util.List;

import io.reactivex.Observable;

public interface RecommendationInterface
{
    Observable<List<Recommendation>> getRecommendationList(Context context);

    Observable<RecommendationPlaceList<RecommendationStay>> getRecommendationStayList(int index, String salesDate, int period);

    Observable<RecommendationPlaceList<RecommendationGourmet>> getRecommendationGourmetList(int index, String salesDate, int period);
}
