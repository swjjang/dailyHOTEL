package com.daily.dailyhotel.repository.remote;

import android.content.Context;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.WishInterface;
import com.daily.dailyhotel.entity.RecentlyPlace;
import com.daily.dailyhotel.entity.Stay;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.entity.WishCount;
import com.daily.dailyhotel.entity.WishResult;
import com.daily.dailyhotel.repository.remote.model.StayOutboundData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundsData;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.StayWishItem;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class WishRemoteImpl extends BaseRemoteImpl implements WishInterface
{
    @Override
    public Observable<List<RecentlyPlace>> getHomeWishList()
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v4/home/wishes"//
            : "MzMkMjkkNDIkNzIkODIkNjEkNzMkNTQkMCQ2MCQ4OSQxMCQxNSQyMSQ1OSQ5MSQ=$JRDI2RUU4MCURFMYkJDN0YE3ODU4MTUyMYjIyNSzQwN0M1TRkY2MThDMTYPIyRkIL5M0GE0NkVGODc2QOOUFDOTQ0MEDEQV2MUUwRQ==$";

        return mDailyMobileService.getHomeWishList(Crypto.getUrlDecoderEx(API))//
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                List<RecentlyPlace> recentlyPlaceList;

                if (baseDto != null)
                {
                    if (baseDto.msgCode == 100 && baseDto.data != null)
                    {
                        recentlyPlaceList = baseDto.data.getRecentlyPlaceList();
                    } else
                    {
                        throw new BaseException(baseDto.msgCode, baseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return recentlyPlaceList;
            });
    }

    @Override
    public Observable<List<Stay>> getStayWishList()
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v5/wishes/HOTEL"//
            : "NjUkMjckNDAkNjUkNjckMzEkMyQ0OSQ4NCQ2MiQ1MiQ5MiQ4MSQ5NSQzNCQ0NyQ=$QTdCEMEQ3MENFNkVBNDhBRDVBNjUOzQjBgL0QTJGQTFFFREGI1NTEVVFQ0U5NjA2MUkZCMzhHEUNOTIyMUYI3RkQ3FQTU2MZTCIwRA==$";

        return mDailyMobileService.getStayWishList(Crypto.getUrlDecoderEx(API))//
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                List<Stay> stayList = new ArrayList<>();

                if (baseDto != null)
                {
                    if (baseDto.msgCode == 100 && baseDto.data != null)
                    {
                        String imageUrl = baseDto.data.imgUrl;

                        List<StayWishItem> stayWishItemList = baseDto.data.items;

                        if (stayWishItemList != null)
                        {
                            for (StayWishItem stayWishItem : stayWishItemList)
                            {
                                stayList.add(stayWishItem.getStayWish(imageUrl));
                            }
                        }
                    } else
                    {
                        throw new BaseException(baseDto.msgCode, baseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return stayList;
            });
    }

    @Override
    public Observable<WishResult> addStayWish(int wishIndex)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v4/wishes/hotel/add/{stayIndex}"//
            : "NjkkNzkkNzkkOTAkNzckMTE4JDY1JDE3JDEzMyQxMDEkNDgkMTEkOTAkMTYkOTIkNDgk$OTM0RTg5QzkA0RkVBDOMDZFNzRFQkFGNzYxMTcwMjU5NzEzMCzAF3NjgyQzZGOUQxNTFCRAEMyNITQ1M0I3UNjGEE0QjHXI3RUFVFRDc0OEWQxQjlBNDNEQzc4NzI2RATYyRjM0MEE2RNDE4$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{stayIndex}", Integer.toString(wishIndex));

        return mDailyMobileService.addWish(Crypto.getUrlDecoderEx(API, urlParams))//
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                WishResult wishResult = new WishResult();

                if (baseDto != null)
                {
                    wishResult.success = baseDto.msgCode == 100;
                    wishResult.message = baseDto.msg;
                } else
                {
                    throw new BaseException(-1, null);
                }

                return wishResult;
            });
    }

    @Override
    public Observable<WishResult> removeStayWish(int wishIndex)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v4/wishes/hotel/remove/{stayIndex}"//
            : "MTA3JDgxJDk4JDY2JDQkODMkODMkMjQkNTUkNzUkMzUkMjckMTM4JDEwMSQxMzkkMTM1JA==$QjQzUN0UzODUyNkFBM0FBQkJECMZDlFMTNFMKUJDMTE2MzE0MDYyMTZGMBkQ1QkQ5NDU1NTUc3M0QTyOEVGNkI4NQKCTQ4Qjc1QzUGwMjYyMM0U3REY1QTgX2MkYwMkQyRTkxNENM4NzDMQ3$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{stayIndex}", Integer.toString(wishIndex));

        return mDailyMobileService.removeWish(Crypto.getUrlDecoderEx(API, urlParams))//
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                WishResult wishResult = new WishResult();

                if (baseDto != null)
                {
                    wishResult.success = baseDto.msgCode == 100;
                    wishResult.message = baseDto.msg;
                } else
                {
                    throw new BaseException(-1, null);
                }

                return wishResult;
            });
    }

    @Override
    public Observable<List<StayOutbound>> getStayOutboundWishList(Context context)
    {
        final int MAX_VALUE = 100;

        return getStayOutboundWishList(context, MAX_VALUE);
    }

    @Override
    public Observable<List<StayOutbound>> getStayOutboundWishList(Context context, int maxCount)
    {
        final String URL = Constants.DEBUG ? DailyPreference.getInstance(context).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v1/outbound/wishitems"//
            : "NDQkNzUkMzUkNDckODQkODgkODUkMjIkMjgkMjgkNyQzMyQ2NCQyOSQxMDEkNzIk$RTYzREQEyMURBMzI5QzI4RUGQwMTVDXZBRIUUxQUYZyMTgyRDNCANTzI1OTlEMTZFCMzZGOUHU3MTgyMEY4RjYM4RjY5BUMzIH1OQ=S=$";

        return mDailyMobileService.getStayOutboundWishList(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API), maxCount) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<StayOutboundsData>, List<StayOutbound>>()
            {
                @Override
                public List<StayOutbound> apply(@io.reactivex.annotations.NonNull BaseDto<StayOutboundsData> baseDto) throws Exception
                {
                    List<StayOutbound> stayOutboundList = new ArrayList<>();

                    if (baseDto != null)
                    {
                        if (baseDto.msgCode == 100 && baseDto.data != null)
                        {
                            for (StayOutboundData stayOutboundData : baseDto.data.stayOutboundDataList)
                            {
                                stayOutboundList.add(stayOutboundData.getStayOutbound());
                            }
                        } else
                        {
                            throw new BaseException(baseDto.msgCode, baseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return stayOutboundList;
                }
            }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<WishResult> addStayOutboundWish(Context context, int wishIndex)
    {
        final String URL = Constants.DEBUG ? DailyPreference.getInstance(context).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v1/outbound/wishitems/{stayIndex}/add"//
            : "OTUkMTIyJDY3JDExOSQ3MSQxMyQ2NCQxMzQkOTQkNTMkNDYkODgkMTExJDYwJDgxJDUk$REEzNQzg2RDJERXkRFMkQ0MzJGNDk5RjQzQjBBNTdDOUYzMGDdGQ0U4DQUMzMFDYwNzQJwNjVXBMUDE4MDAk2NjREOTWExMEEyNEZFFNkRSGNzQ0RDBRFODhCNkQ5NEZCMKDNGNZTEzNTUD1$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{stayIndex}", Integer.toString(wishIndex));

        return mDailyMobileService.addStayOutboundWish(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API, urlParams))//
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                WishResult wishResult = new WishResult();

                if (baseDto != null)
                {
                    wishResult.success = baseDto.msgCode == 100;
                    wishResult.message = baseDto.msg;
                } else
                {
                    throw new BaseException(-1, null);
                }

                return wishResult;
            });
    }

    @Override
    public Observable<WishResult> removeStayOutboundWish(Context context, int wishIndex)
    {
        final String URL = Constants.DEBUG ? DailyPreference.getInstance(context).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v1/outbound/wishitems/{stayIndex}/remove"//
            : "MiQ5NyQ2NSQ5MyQxMzEkOTMkNjgkMTAyJDEyNyQ2NyQxJDEzNyQxMTIkODIkMTQxJDY0JA==$MYzZk4MUZENzU0MDg0NjkwMTk2QzNERUNCRDRCMkY1QkNCMEJFNDEyOTE2MTcwNkCI3BRWUMY0RDk5M0ZCQAUVFREZCQ0Y0MEEHE4Q0E1EMREM2Q0RNDMzNBMkU0N0M2QjkyVQjNFODZIBCD$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{stayIndex}", Integer.toString(wishIndex));

        return mDailyMobileService.removeStayOutboundWish(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API, urlParams))//
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                WishResult wishResult = new WishResult();

                if (baseDto != null)
                {
                    wishResult.success = baseDto.msgCode == 100;
                    wishResult.message = baseDto.msg;
                } else
                {
                    throw new BaseException(-1, null);
                }

                return wishResult;
            });
    }

    /**
     * 국내 스테이, 고메 위시 개수
     *
     * @return
     */
    @Override
    public Observable<WishCount> getWishCount()
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v4/wishes"//
            : "MzgkMTUkMzIkMTgkNDAkNDEkMzMkMjkkNDMkMTQkMjIkMTkkNDUkNTAkMjgkMzIk$NTBFODQwNDgzNTPAWzNFF0IWxQUIL2OEMER4NzBCWCMjMyNIALIkOEVCNDU=$";

        return mDailyMobileService.getWishCount(Crypto.getUrlDecoderEx(API))//
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                WishCount wishCount;

                if (baseDto != null)
                {
                    if (baseDto.msgCode == 100 && baseDto.data != null)
                    {
                        wishCount = baseDto.data.getWishCount();
                    } else
                    {
                        throw new BaseException(baseDto.msgCode, baseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return wishCount;
            });
    }

    /**
     * 해외 스테이 위시 개수
     *
     * @return
     */
    @Override
    public Observable<Integer> getStayOutboundWishCount(Context context)
    {
        final String URL = Constants.DEBUG ? DailyPreference.getInstance(context).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v1/outbound/wishes"//
            : "MzUkODMkMjgkMjAkMjUkODMkNDIkODckNTMkMjckMjckMTMkNiQ0OCQ0MSQxNiQ=$NDRBMDFlCQzZBOSDXQ5MDlDKMEY2FMSUUJDRRDRGMjFNDFOTNcJyQjM1QjM4NQjI1RTc1NEM2NzZEQUVDQTdBRjBGNkUOwMLDMI1OQ==$";

        return mDailyMobileService.getStayOutboundWishCount(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API))//
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                int wishCount;

                if (baseDto != null)
                {
                    if (baseDto.msgCode == 100 && baseDto.data != null)
                    {
                        wishCount = baseDto.data.wishOutboundCount;
                    } else
                    {
                        throw new BaseException(baseDto.msgCode, baseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return wishCount;
            });
    }
}
