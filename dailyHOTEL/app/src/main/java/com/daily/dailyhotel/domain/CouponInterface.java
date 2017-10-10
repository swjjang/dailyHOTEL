package com.daily.dailyhotel.domain;

import com.twoheart.dailyhotel.model.Coupon;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2017. 9. 28..
 */

public interface CouponInterface
{
    Observable<ArrayList<Coupon>> getCouponHistoryList();
}
