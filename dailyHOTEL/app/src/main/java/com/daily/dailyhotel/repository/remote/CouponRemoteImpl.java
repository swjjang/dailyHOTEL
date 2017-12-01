package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.CouponInterface;
import com.daily.dailyhotel.entity.Coupon;
import com.daily.dailyhotel.repository.remote.model.CouponsData;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android_sam on 2017. 9. 28..
 */

public class CouponRemoteImpl extends BaseRemoteImpl implements CouponInterface
{
    public CouponRemoteImpl(Context context)
    {
        super(context);
    }

    @Override
    public Observable<List<Coupon>> getCouponHistoryList()
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/coupons/history"//
            : "NTgkMjgkMzMkNTYkNzEkNTYkNDQkODYkMzAkNTAkMjEkMTkkOSQzMCQyOSQyOCQ=$ODlCNTQ1OOTA3NjczNkJVEQCjRBQSjNFOEXOXDMyPMzM5ODE4RTOBEMEZFGRjA1RTFZg5MjXk2QTVGNUJCDMzMzMzI4ODJJCNzY0Mg==$";

        return mDailyMobileService.getCouponHistoryList(Crypto.getUrlDecoderEx(URL)) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<CouponsData>, List<Coupon>>()
            {
                @Override
                public List<com.daily.dailyhotel.entity.Coupon> apply(@NonNull BaseDto<CouponsData> couponsDataBaseDto) throws Exception
                {
                    List<Coupon> couponHistoryList = new ArrayList<>();

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

    @Override
    public Observable<List<Coupon>> getGourmetCouponListByPayment(int[] ticketSaleIndexes, int[] ticketCounts)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v5/prebooking/gourmet/coupon/info"//
            : "NTUkMTIxJDE3JDkxJDEwOSQxOSQ1JDMwJDMxJDMyJDE0JDY2JDIyJDExOSQzOCQyMCQ=$OUJEREjQwMDI2NFEQ4MCIzWKA5NkRCRjUNMN5RTSM2RTZGMDA1QjVCMUUyRDQyMDdFCNUSNBMUMzMkUwOENEMkI0QUFFNzRGODVBNKkU5NDAzOTk2QkJDN0IQTxREM0RTZFN0ZGENzBCOTEw$";

        JSONArray jsonArray = new JSONArray();

        try
        {
            int length = ticketSaleIndexes.length;

            for (int i = 0; i < length; i++)
            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("saleRecoIdx", ticketSaleIndexes[i]);
                jsonObject.put("count", ticketCounts[i]);

                jsonArray.put(jsonObject);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return mDailyMobileService.getGourmetCouponListByPayment(Crypto.getUrlDecoderEx(URL), jsonArray) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<CouponsData>, List<Coupon>>()
            {
                @Override
                public List<Coupon> apply(@NonNull BaseDto<CouponsData> couponsDataBaseDto) throws Exception
                {
                    List<Coupon> couponList = new ArrayList<>();

                    if (couponsDataBaseDto != null)
                    {
                        if (couponsDataBaseDto.msgCode == 100 && couponsDataBaseDto.data != null)
                        {
                            CouponsData couponsData = couponsDataBaseDto.data;

                            if (couponsData != null)
                            {
                                couponList = couponsData.getCouponList();
                            }
                        } else
                        {
                            throw new BaseException(couponsDataBaseDto.msgCode, couponsDataBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return couponList;
                }
            }).subscribeOn(Schedulers.io());
    }
}
