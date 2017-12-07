package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.Coupon;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2017. 9. 28..
 */

public interface CouponInterface
{
    Observable<List<Coupon>> getCouponHistoryList();

    Observable<List<Coupon>> getGourmetCouponListByPayment(int[] ticketIndexes, int[] ticketCounts);
}
