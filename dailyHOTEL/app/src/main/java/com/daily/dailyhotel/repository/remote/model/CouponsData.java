package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.twoheart.dailyhotel.model.Coupon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iseung-won on 2017. 9. 28..
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
