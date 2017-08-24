package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.Review;

import io.reactivex.Observable;

public interface CommonInterface
{
    Observable<CommonDateTime> getCommonDateTime();

    Observable<Review> getReview(String placeType, int reservationIndex);
}
