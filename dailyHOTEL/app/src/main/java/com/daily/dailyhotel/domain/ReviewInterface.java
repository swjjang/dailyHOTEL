package com.daily.dailyhotel.domain;

import android.content.Context;

import com.daily.dailyhotel.entity.Review;

import io.reactivex.Observable;

public interface ReviewInterface
{
    Observable<Review> getStayReview(int reservationIndex);

    Observable<Review> getGourmetReview(int reservationIndex);

    Observable<Review> getStayOutboundReview(Context context, int reservationIndex);
}
