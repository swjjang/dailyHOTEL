package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.CouponInterface;
import com.daily.dailyhotel.repository.remote.model.CouponsData;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by iseung-won on 2017. 9. 28..
 */

public class CouponRemoteImpl implements CouponInterface
{
    private Context mContext;

    public CouponRemoteImpl(Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<ArrayList<Coupon>> getCouponHistoryList()
    {
        return DailyMobileAPI.getInstance(mContext).getCouponHistoryList().map(new Function<BaseDto<CouponsData>, ArrayList<Coupon>>()
        {
            @Override
            public ArrayList<Coupon> apply(@NonNull BaseDto<CouponsData> couponsDataBaseDto) throws Exception
            {
                ArrayList<Coupon> couponHistoryList = new ArrayList<>();

                if (couponsDataBaseDto != null)
                {
                    if (couponsDataBaseDto.msgCode == 100 && couponsDataBaseDto.data != null)
                    {
                        CouponsData couponsData = couponsDataBaseDto.data;

                        if (couponsData != null)
                        {
                            couponHistoryList = couponsData.getCouponList();
                        }
                    } else
                    {
                        throw new BaseException(couponsDataBaseDto.msgCode, couponsDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return couponHistoryList;
            }
        }).subscribeOn(Schedulers.io());
    }
}
