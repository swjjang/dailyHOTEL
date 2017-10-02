package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.TrueReviews;

import io.reactivex.Observable;

public interface StayInterface
{
    Observable<TrueReviews> getStayTrueReviews(int gourmetIndex, int page, int limit);
}
