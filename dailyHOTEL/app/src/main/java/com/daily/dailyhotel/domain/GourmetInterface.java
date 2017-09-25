package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetDetail;
import com.daily.dailyhotel.entity.ReviewScores;
import com.daily.dailyhotel.entity.TrueReviews;
import com.daily.dailyhotel.entity.WishResult;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetParams;

import java.util.List;

import io.reactivex.Observable;

public interface GourmetInterface
{
    Observable<List<Gourmet>> getGourmetList(GourmetParams gourmetParams);

    Observable<GourmetDetail> getGourmetDetail(int gourmetIndex, GourmetBookDateTime gourmetBookDateTime);

    Observable<Boolean> getGourmetHasCoupon(int gourmetIndex, GourmetBookDateTime gourmetBookDateTime);

    Observable<WishResult> addGourmetWish(int gourmetIndex);

    Observable<WishResult> removeGourmetWish(int gourmetIndex);

    Observable<ReviewScores> getGourmetReviewScores(int gourmetIndex);

    Observable<TrueReviews> getGourmetTrueReviews(int gourmetIndex, int page, int limit);
}
