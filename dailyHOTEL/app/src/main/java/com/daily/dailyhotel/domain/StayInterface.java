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

public interface StayInterface
{
    Observable<TrueReviews> getStayTrueReviews(int gourmetIndex, int page, int limit);
}
