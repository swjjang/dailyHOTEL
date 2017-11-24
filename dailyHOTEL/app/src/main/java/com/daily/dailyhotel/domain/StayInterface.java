package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.Region;
import com.daily.dailyhotel.entity.ReviewScores;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayDetail;
import com.daily.dailyhotel.entity.TrueReviews;
import com.daily.dailyhotel.entity.TrueVR;
import com.daily.dailyhotel.entity.WishResult;

import java.util.List;

import io.reactivex.Observable;

public interface StayInterface
{
    Observable<StayDetail> getDetail(int stayIndex, StayBookDateTime stayBookDateTime);

    Observable<Boolean> getHasCoupon(int stayIndex, StayBookDateTime stayBookDateTime);

    Observable<WishResult> addWish(int stayIndex);

    Observable<WishResult> removeWish(int stayIndex);

    Observable<ReviewScores> getReviewScores(int stayIndex);

    Observable<TrueReviews> getTrueReviews(int stayIndex, int page, int limit);

    Observable<List<TrueVR>> getTrueVR(int stayIndex);

    Observable<List<Region>> getRegionList();
}
