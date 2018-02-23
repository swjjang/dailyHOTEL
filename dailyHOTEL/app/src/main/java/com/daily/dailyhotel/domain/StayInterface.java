package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.ReviewScores;
import com.daily.dailyhotel.entity.StayAreaGroup;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayDetail;
import com.daily.dailyhotel.entity.StayFilterCount;
import com.daily.dailyhotel.entity.Stays;
import com.daily.dailyhotel.entity.TrueReviews;
import com.daily.dailyhotel.entity.TrueVR;
import com.daily.dailyhotel.entity.WishResult;
import com.twoheart.dailyhotel.model.DailyCategoryType;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;

public interface StayInterface
{
    Observable<Stays> getList(Map<String, Object> queryMap, String abTestType);

    Observable<StayFilterCount> getListCountByFilter(Map<String, Object> queryMap, String abTestType);

    Observable<StayDetail> getDetail(int stayIndex, StayBookDateTime stayBookDateTime);

    Observable<Boolean> getHasCoupon(int stayIndex, StayBookDateTime stayBookDateTime);

    Observable<WishResult> addWish(int stayIndex);

    Observable<WishResult> removeWish(int stayIndex);

    Observable<ReviewScores> getReviewScores(int stayIndex);

    Observable<TrueReviews> getTrueReviews(int stayIndex, int page, int limit);

    Observable<List<TrueVR>> getTrueVR(int stayIndex);

    Observable<List<StayAreaGroup>> getRegionList(DailyCategoryType categoryType);

    Observable<List<StayAreaGroup>> getSubwayList(DailyCategoryType categoryType);
}
