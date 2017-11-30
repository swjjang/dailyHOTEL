package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.Coupon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_sam on 2017. 9. 28..
 */
@JsonObject
public class CouponsData
{
    @JsonField(name = "coupons")
    public List<CouponData> coupons;

    @JsonField(name = "serverDate")
    public String serverDate;

    public ArrayList<Coupon> getCouponList()
    {
        ArrayList<Coupon> couponList = new ArrayList<>();

        for (CouponData couponData : coupons)
        {
            Coupon coupon = couponData.getCoupon(serverDate);
            couponList.add(coupon);
        }

        return couponList;
    }
}
