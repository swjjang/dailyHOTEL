package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.Coupon;
import com.daily.dailyhotel.entity.Coupons;

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

    @JsonField(name = "maxCouponAmount")
    public int maxCouponAmount;

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

    public Coupons getCoupons()
    {
        Coupons result = new Coupons();

        result.coupons = this.getCouponList();
        result.serverDate = this.serverDate;

        if (result.coupons != null && result.coupons.size() > 0 && maxCouponAmount == 0)
        {
            for (Coupon coupon : result.coupons)
            {
                if (maxCouponAmount < coupon.amount)
                {
                    maxCouponAmount = coupon.amount;
                }
            }
        }

        result.maxCouponAmount = this.maxCouponAmount;

        return result;
    }
}
