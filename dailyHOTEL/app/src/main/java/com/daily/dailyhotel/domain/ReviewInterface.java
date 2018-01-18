package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.Review;

import io.reactivex.Observable;

public interface ReviewInterface
{
    Observable<Review> getStayReview(int reservationIndex);

    Observable<Review> getGourmetReview(int reservationIndex);

    Observable<Review> getStayOutboundReview(int reservationIndex);
}
