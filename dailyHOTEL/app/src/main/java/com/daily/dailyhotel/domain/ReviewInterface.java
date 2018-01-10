package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.Review;
import com.daily.dailyhotel.entity.RewardCardHistoryDetail;
import com.daily.dailyhotel.entity.RewardDetail;
import com.daily.dailyhotel.entity.RewardHistoryDetail;
import com.daily.dailyhotel.entity.RewardInformation;

import io.reactivex.Observable;

public interface ReviewInterface
{
    Observable<Review> getStayReview(int reservationIndex);

    Observable<Review> getGourmetReview(int reservationIndex);

    Observable<Review> getStayOutboundReview(int reservationIndex);
}
