package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.CouponInterface;
import com.daily.dailyhotel.entity.Coupon;
import com.daily.dailyhotel.entity.Coupons;
import com.daily.dailyhotel.entity.DownloadCouponResult;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.repository.remote.model.CouponsData;
import com.daily.dailyhotel.repository.remote.model.DownloadCouponResultData;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android_sam on 2017. 9. 28..
 */

public class CouponRemoteImpl extends BaseRemoteImpl implements CouponInterface
{
    @Override
    public Observable<List<Coupon>> getCouponHistoryList()
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v3/users/coupons/history"//
            : "NTgkMjgkMzMkNTYkNzEkNTYkNDQkODYkMzAkNTAkMjEkMTkkOSQzMCQyOSQyOCQ=$ODlCNTQ1OOTA3NjczNkJVEQCjRBQSjNFOEXOXDMyPMzM5ODE4RTOBEMEZFGRjA1RTFZg5MjXk2QTVGNUJCDMzMzMzI4ODJJCNzY0Mg==$";

        return mDailyMobileService.getCouponHistoryList(Crypto.getUrlDecoderEx(API)) //
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
    public Observable<Coupons> getGourmetCouponListByPayment(int[] ticketSaleIndexes, int[] ticketCounts)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v5/prebooking/gourmet/coupon/info"//
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

        return mDailyMobileService.getGourmetCouponListByPayment(Crypto.getUrlDecoderEx(API), jsonArray) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<CouponsData>, Coupons>()
            {
                @Override
                public Coupons apply(@NonNull BaseDto<CouponsData> couponsDataBaseDto) throws Exception
                {
                    Coupons coupons = new Coupons();

                    if (couponsDataBaseDto != null)
                    {
                        if (couponsDataBaseDto.msgCode == 100 && couponsDataBaseDto.data != null)
                        {
                            CouponsData couponsData = couponsDataBaseDto.data;

                            if (couponsData != null)
                            {
                                coupons = couponsData.getCoupons();
                            }
                        } else
                        {
                            throw new BaseException(couponsDataBaseDto.msgCode, couponsDataBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return coupons;
                }
            }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Coupons> getStayCouponListByPayment(int stayIndex, int roomIndex, String checkIn, String checkOut)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v2/payment/coupons"//
            : "MjkkMTckODAkNTkkNTEkMzAkMyQ0MiQ2NyQzMiQ0OCQ2NCQ1NCQ3OSQxJDUxJA==$MBDgCxMDk2MDFENTI1QUkFGRkFDREY5OFCNDhGQUQ5NzFM4NTOUC1NjAQzMMjZCRDU2JQKjNCZOTYyOTQT4NDQwRjY5RDPM2N0ZCQQ==$";

        return mDailyMobileService.getStayCouponListByPayment(Crypto.getUrlDecoderEx(URL), stayIndex, roomIndex, checkIn, checkOut) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<CouponsData>, Coupons>()
            {
                @Override
                public Coupons apply(BaseDto<CouponsData> couponsDataBaseDto) throws Exception
                {
                    Coupons coupons = new Coupons();

                    if (couponsDataBaseDto != null)
                    {
                        if (couponsDataBaseDto.msgCode == 100 && couponsDataBaseDto.data != null)
                        {
                            CouponsData couponsData = couponsDataBaseDto.data;
                            if (couponsData != null)
                            {
                                coupons = couponsData.getCoupons();
                            }
                        } else
                        {
                            throw new BaseException(couponsDataBaseDto.msgCode, couponsDataBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return coupons;
                }
            }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Coupons> getStayOutboundCouponListByPayment(Context context, String checkInDate, String checkOutDate//
        , int stayIndex, String rateCode, String rateKey, String roomTypeCode, int roomBedTypeId, People people, String vendorType)
    {
        final String URL = Constants.DEBUG ? DailyPreference.getInstance(context).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v2/outbound/payment/coupons"//
            : "MjAkNzMkMzEkODAkMjUkODUkOTAkOSQxMyQ5NSQyMSQ3NyQ4MyQ4JDg3JDI2JA==$MTE1OUU4VNEDFBMNEJENzAB3MMSjVFVNEJEOEPJEQ0Q1N0Y4N0RDOTVFQUIzN0NGQTM2M0M3MzZDOTdIGYNkQMxNAJzZDRNjIzXQQI==$";

        JSONObject jsonObject = getStayOutboundCouponListJsonObjectByPayment(checkInDate, checkOutDate//
            , stayIndex, rateCode, rateKey, roomTypeCode, roomBedTypeId, people, vendorType);

        return mDailyMobileService.getStayOutboundCouponListByPayment(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API), jsonObject) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<CouponsData>, Coupons>()
            {
                @Override
                public Coupons apply(BaseDto<CouponsData> couponsDataBaseDto) throws Exception
                {
                    Coupons coupons;

                    if (couponsDataBaseDto != null)
                    {
                        if (couponsDataBaseDto.msgCode == 100 && couponsDataBaseDto.data != null)
                        {
                            CouponsData couponsData = couponsDataBaseDto.data;

                            coupons = couponsData == null || couponsData.getCoupons() == null ? new Coupons() : couponsData.getCoupons();
                        } else
                        {
                            throw new BaseException(couponsDataBaseDto.msgCode, couponsDataBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return coupons;
                }
            }).subscribeOn(Schedulers.io());
    }

    private JSONObject getStayOutboundCouponListJsonObjectByPayment(String checkInDate, String checkOutDate//
        , int stayIndex, String rateCode, String rateKey, String roomTypeCode, int roomBedTypeId, People people, String vendorType)
    {
        JSONObject jsonObject = new JSONObject();

        try
        {
            jsonObject.put("arrivalDate", checkInDate);
            jsonObject.put("departureDate", checkOutDate);
            jsonObject.put("outboundHotelId", Integer.toString(stayIndex));
            jsonObject.put("rateCode", rateCode);
            jsonObject.put("rateKey", rateKey);
            jsonObject.put("roomTypeCode", roomTypeCode);

            if (people != null)
            {
                JSONObject roomFindConditionJSONObject = new JSONObject();

                roomFindConditionJSONObject.put("numberOfAdults", people.numberOfAdults);

                ArrayList<Integer> childAgeList = people.getChildAgeList();

                if (childAgeList != null && childAgeList.isEmpty() == false)
                {
                    JSONArray jsonArray = new JSONArray();

                    for (int childAge : childAgeList)
                    {
                        jsonArray.put(childAge);
                    }

                    roomFindConditionJSONObject.put("childAges", jsonArray);
                    roomFindConditionJSONObject.put("numberOfChildren", childAgeList.size());
                } else
                {
                    roomFindConditionJSONObject.put("numberOfChildren", 0);
                }

                roomFindConditionJSONObject.put("roomBedTypeId", roomBedTypeId);

                jsonObject.put("roomFindCondition", roomFindConditionJSONObject);
            }

            jsonObject.put("vendorType", vendorType);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return jsonObject;
    }

    @Override
    public Observable<Coupons> getStayOutboundCouponListByDetail(Context context, String checkInDate, String checkOutDate//
        , int stayIndex, String[] vendorTypes)
    {
        final String URL = Constants.DEBUG ? DailyPreference.getInstance(context).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v3/outbound/hotels/{stayIndex}/coupons"//
            : "MTgkMTA4JDM2JDM4JDEzMSQzMCQ1NyQ5NyQxMTgkMTI3JDQ3JDI5JDUzJDgzJDExNCQxMDQk$QzVGNENBMEUyQzhDNjQJFNDQ3NTUwYNGjhEMTMDyCNkY4MTUR4NkUEwNjgwMTUYyM0I1QzQwNjUwRDcxOTEE5MTg2Q0Q5MUI1RTlDYRUMJFMENCRjQzROUQN5NEJMFQkJDQ0YJyRDM2REVHF$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{stayIndex}", Integer.toString(stayIndex));

        JSONObject jsonObject = getStayOutboundCouponListJsonObjectByDetail(checkInDate, checkOutDate, stayIndex, vendorTypes);

        return mDailyMobileService.getStayOutboundCouponListByPayment(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API, urlParams), jsonObject) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<CouponsData>, Coupons>()
            {
                @Override
                public Coupons apply(BaseDto<CouponsData> couponsDataBaseDto) throws Exception
                {
                    Coupons coupons;

                    if (couponsDataBaseDto != null)
                    {
                        if (couponsDataBaseDto.msgCode == 100 && couponsDataBaseDto.data != null)
                        {
                            CouponsData couponsData = couponsDataBaseDto.data;

                            coupons = couponsData == null || couponsData.getCoupons() == null ? new Coupons() : couponsData.getCoupons();
                        } else
                        {
                            throw new BaseException(couponsDataBaseDto.msgCode, couponsDataBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return coupons;
                }
            }).subscribeOn(Schedulers.io());
    }

    private JSONObject getStayOutboundCouponListJsonObjectByDetail(String checkInDate, String checkOutDate, int stayIndex, String[] vendorTypes)
    {
        JSONObject jsonObject = new JSONObject();

        try
        {
            jsonObject.put("arrivalDate", checkInDate);
            jsonObject.put("departureDate", checkOutDate);
            jsonObject.put("outboundHotelId", Integer.toString(stayIndex));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return jsonObject;
    }

    @Override
    public Observable<DownloadCouponResult> getDownloadCoupon(String couponCode)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v3/users/coupons/download"//
            : "MzMkNTYkMTgkODUkNTMkMzUkOTAkNTQkNyQyMyQxNiQyMCQ4MSQ2MiQ3NCQxMyQ=$QUE2NzVCFMUU5RNEFKCMjSQE3MOjUyRkFBOTVDMGCzlFN0M3QzkzM0VGMTAQO3QVzBMFMTJGMDhFDN0MxMUMHzNDc5RDY4NDLU5YNA==$";

        return mDailyMobileService.getDownloadCoupon(Crypto.getUrlDecoderEx(API), couponCode) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<DownloadCouponResultData>, DownloadCouponResult>()
            {
                @Override
                public DownloadCouponResult apply(@io.reactivex.annotations.NonNull BaseDto<DownloadCouponResultData> baseDto) throws Exception
                {
                    if (baseDto != null)
                    {
                        if (baseDto.msgCode == 100)
                        {
                            return baseDto.data.getResult();
                        } else
                        {
                            throw new BaseException(baseDto.msgCode, baseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }
                }
            }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<Coupon>> getCouponList()
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/coupons"//
            : "NTUkNzUkNDQkMTIkMCQxNyQ4MiQ4NCQzNyQ0MCQ0OCQxNyQyNSQzMSQ2JDEwMiQ=$XMzE5MMTc3MTVEJQjBUSCQkM1QLUQ5NkWY5MUYzQUEQwWRDA5N0UA0MQUU0RDNDM0RXFRTE3RUJBRkY1RUM3NTUZGCNKzk5QTRDRA=B=$";

        return mDailyMobileService.getCouponList(Crypto.getUrlDecoderEx(URL)) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<CouponsData>, List<Coupon>>()
            {
                @Override
                public List<Coupon> apply(BaseDto<CouponsData> couponsDataBaseListDto) throws Exception
                {
                    List<Coupon> couponList;

                    if (couponsDataBaseListDto != null)
                    {
                        if (couponsDataBaseListDto.msgCode == 100 && couponsDataBaseListDto.data != null)
                        {
                            couponList = couponsDataBaseListDto.data.getCouponList();
                        } else
                        {
                            throw new BaseException(couponsDataBaseListDto.msgCode, couponsDataBaseListDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return couponList;
                }
            });
    }

    @Override
    public Observable<BaseDto<Object>> setRegisterCoupon(String couponCode)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/users/coupons/keyword"//
            : "NzUkMzEkMjAkOCQyJDAkNDkkNjEkMiQxOSQ5MCQ2MiQxMiQxNiQzMCQxNSQ=$NNTzJk1NTVDRKNDBdUGMzQX1RkMxFNjTU4OUY2NUEXzRUVCRTk1REUyUOEU0REY0QjHNLFRUIzQzc3RTlCMTRBQUMT0QzNFNNTNGNg==$\n";

        return mDailyMobileService.setRegisterKeywordCoupon(Crypto.getUrlDecoderEx(URL), couponCode) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<Object>, BaseDto<Object>>()
            {
                @Override
                public BaseDto<Object> apply(BaseDto<Object> objectBaseDto) throws Exception
                {
                    if (objectBaseDto != null)
                    {
                        return objectBaseDto;
                    } else
                    {
                        throw new BaseException(-1, null);
                    }
                }
            });
    }

    @Override
    public Observable<Coupons> getStayCouponListByDetail(int stayIndex, String checkIn, int nights)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/hotel/{hotelIdx}/coupons"//
            : "NDAkMzIkMTkkMjMkMTAkNzgkODckNiQyOCQxNyQ4NyQyMyQwJDExJDE2JDc4JA==$YMEQxMDTIyOITBEyFQ0MMwOEICMwOTKdETNTgwMzE3GMkRGNEYwKQURBNDg1NzAzOTlFRDFDNEFDQzXhBREI2OLDNCRUAIxQWTI2Mg==$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{hotelIdx}", Integer.toString(stayIndex));

        return mDailyMobileService.getStayCouponListByDetail(Crypto.getUrlDecoderEx(URL, urlParams), checkIn, nights) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<CouponsData>, Coupons>()
            {
                @Override
                public Coupons apply(BaseDto<CouponsData> couponsDataBaseDto) throws Exception
                {
                    Coupons coupons;

                    if (couponsDataBaseDto != null)
                    {
                        if (couponsDataBaseDto.msgCode == 100 && couponsDataBaseDto.data != null)
                        {
                            coupons = couponsDataBaseDto.data.getCoupons();
                        } else
                        {
                            throw new BaseException(couponsDataBaseDto.msgCode, couponsDataBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return coupons;
                }
            });
    }

    @Override
    public Observable<Coupons> getGourmetCouponListByDetail(int gourmetIndex, String visitDay)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/gourmet/{restaurantIdx}/coupons"//
            : "NTkkNTQkMTA0JDkxJDExNCQ4JDEyMiQxNiQxMjUkMTIzJDI0JDExJDEwNCQxMTMkMTMyJDEwMSQ=$MzI0MTk1POUBM3OEUE1NjQ5RTFAwMDJBOTNGNjVDOEVCOUYxNzU3OEQxNzLkwNTEDwMzE1MTVCRTZFMkY3RDAyM0RBM0JCNHTE2ODXRCRPTkyM0WU4QMkU0Q0UFEMjZGPJQCzFA1RTE5Q0Q4$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{restaurantIdx}", Integer.toString(gourmetIndex));

        return mDailyMobileService.getGourmetCouponListByDetail(Crypto.getUrlDecoderEx(URL, urlParams), visitDay) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<CouponsData>, Coupons>()
            {
                @Override
                public Coupons apply(BaseDto<CouponsData> couponsDataBaseDto) throws Exception
                {
                    Coupons coupons;

                    if (couponsDataBaseDto != null)
                    {
                        if (couponsDataBaseDto.msgCode == 100 && couponsDataBaseDto.data != null)
                        {
                            coupons = couponsDataBaseDto.data.getCoupons();
                        } else
                        {
                            throw new BaseException(couponsDataBaseDto.msgCode, couponsDataBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return coupons;
                }
            });
    }
}
