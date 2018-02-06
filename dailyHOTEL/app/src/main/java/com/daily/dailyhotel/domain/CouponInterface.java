package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.Coupon;
import com.daily.dailyhotel.entity.Coupons;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2017. 9. 28..
 */

public interface CouponInterface
{
    Observable<List<Coupon>> getCouponHistoryList();

    Observable<Coupons> getGourmetCouponListByPayment(int[] ticketIndexes, int[] ticketCounts);

    Observable<Coupons> getStayCouponListByPayment(int stayIndex, int roomIndex, String checkIn, String checkOut);

    Observable<Coupons> getStayOutboundCouponListByPayment(int stayIndex, int roomIndex, String checkIn, String checkOut);
}
