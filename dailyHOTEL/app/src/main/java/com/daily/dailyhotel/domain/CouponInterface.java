package com.daily.dailyhotel.domain;

import android.content.Context;

import com.daily.dailyhotel.entity.Coupon;
import com.daily.dailyhotel.entity.Coupons;
import com.daily.dailyhotel.entity.DownloadCouponResult;

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

    Observable<Coupons> getStayOutboundCouponListByPayment(Context context, String checkInDate, String checkOutDate//
        , int stayIndex, String rateCode, String rateKey, String roomTypeCode, String vendorType);

    Observable<Coupons> getStayOutboundCouponListByDetail(Context context, String checkInDate, String checkOutDate//
        , int stayIndex, String[] vendorTypes);

    Observable<DownloadCouponResult> getDownloadCoupon(String couponCode);

    Observable<List<Coupon>> getCouponList();
}
