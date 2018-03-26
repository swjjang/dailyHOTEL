package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.SuggestInterface;
import com.daily.dailyhotel.entity.GourmetSuggestV2;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.repository.remote.model.GourmetSuggestsData;
import com.daily.dailyhotel.repository.remote.model.StaySuggestsData;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SuggestRemoteImpl extends BaseRemoteImpl implements SuggestInterface
{
    public SuggestRemoteImpl(@NonNull Context context)
    {
        super(context);
    }

    @Override
    public Observable<List<StayOutboundSuggest>> getSuggestsByStayOutbound(String keyword)
    {
        final String URL = Constants.DEBUG ? DailyPreference.getInstance(mContext).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v1/category-suggests"//
            : "MCQ1NSQ1MiQ4MSQ2NyQ0NiQ1OSQ1MCQ0MSQ1MyQ4OCQyNSQ1OCQ5JDUyJDE0JA==$XMDI3ODcwTOTMzTNEM4MDQzNzA5OQzA0MTM1NDNENkQwZREI0QOkQX0CQFzUNNxMzAlZEMDhENEE4LNjAxRTRDRUM5MzUAlEQjVEMA==$";

        return mDailyMobileService.getSuggestsByStayOutbound(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API), keyword)//
            .subscribeOn(Schedulers.io()).map((suggestsDataBaseDto) ->
            {
                List<StayOutboundSuggest> list;

                if (suggestsDataBaseDto != null)
                {
                    if (suggestsDataBaseDto.msgCode == 100 && suggestsDataBaseDto.data != null)
                    {
                        list = suggestsDataBaseDto.data.getSuggestList(mContext);
                    } else
                    {
                        throw new BaseException(suggestsDataBaseDto.msgCode, suggestsDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return list;
            }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<StayOutboundSuggest>> getRegionSuggestsByStayOutbound(String keyword)
    {
        final String URL = Constants.DEBUG ? DailyPreference.getInstance(mContext).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v1/category-suggests"//
            : "MCQ1NSQ1MiQ4MSQ2NyQ0NiQ1OSQ1MCQ0MSQ1MyQ4OCQyNSQ1OCQ5JDUyJDE0JA==$XMDI3ODcwTOTMzTNEM4MDQzNzA5OQzA0MTM1NDNENkQwZREI0QOkQX0CQFzUNNxMzAlZEMDhENEE4LNjAxRTRDRUM5MzUAlEQjVEMA==$";

        return mDailyMobileService.getSuggestsByStayOutbound(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API), keyword)//
            .subscribeOn(Schedulers.io()).map((suggestsDataBaseDto) ->
            {
                List<StayOutboundSuggest> list;

                if (suggestsDataBaseDto != null)
                {
                    if (suggestsDataBaseDto.msgCode == 100 && suggestsDataBaseDto.data != null)
                    {
                        list = suggestsDataBaseDto.data.getRegionSuggestList(mContext);
                    } else
                    {
                        throw new BaseException(suggestsDataBaseDto.msgCode, suggestsDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return list;
            }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<StayOutboundSuggest>> getPopularRegionSuggestsByStayOutbound()
    {
        final String URL = Constants.DEBUG ? DailyPreference.getInstance(mContext).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v1/hot-keywords"//
            : "NDEkMTQkNjUkNjckODEkMjAkMTMkNDMkNzMkNTEkMzkkMjMkNSQ1OCQ2NSQ4MSQ=$NDhERQjE0MDhCMUDBgyMjlPEAMDJGNkYzNTFFQ0MzBQjU4TQSTM1MTSA2RHDg5RTJQFRTkwNTYO3HOUMIDxNjQyNDc2QFTAxOTg2MQ==$";

        return mDailyMobileService.getPopularAreaSuggestsByStayOutbound(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API))//
            .subscribeOn(Schedulers.io()).map((suggestsDataBaseDto) ->
            {
                List<StayOutboundSuggest> list;

                if (suggestsDataBaseDto != null)
                {
                    if (suggestsDataBaseDto.msgCode == 100 && suggestsDataBaseDto.data != null)
                    {
                        list = suggestsDataBaseDto.data.getRegionSuggestList(mContext);
                    } else
                    {
                        throw new BaseException(suggestsDataBaseDto.msgCode, suggestsDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return list;
            });
    }

    @Override
    public Observable<List<StaySuggest>> getSuggestByStay(String checkInDate, int stays, String keyword)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v5/hotels/sales/search/suggest"//
            : "MTIzJDEyJDEyNSQxJDIwJDEkODckMTYkODckNDkkOTMkNTMkODkkNTYkODEkOSQ=$OSYDg0RjMF1RkI2HOYTVGMkEQ3NjE1NzU3NjA3N0ZFOUNFN0I2GQjECwNHUQxQUNEQUM0NDc0MDUxMjREQDzM1NzREOEJCETzRCjYyMjk4NEJBQTJFQjJEQTIwMkZGRTZBMDE5NjkBWwOTk4$";

        return mDailyMobileService.getSuggestsByStayInbound(Crypto.getUrlDecoderEx(URL), checkInDate, stays, keyword) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<StaySuggestsData>, List<StaySuggest>>()
            {
                @Override
                public List<StaySuggest> apply(BaseDto<StaySuggestsData> staySuggestsDataBaseDto) throws Exception
                {
                    List<StaySuggest> list;

                    if (staySuggestsDataBaseDto != null)
                    {
                        if (staySuggestsDataBaseDto.msgCode == 100 && staySuggestsDataBaseDto.data != null)
                        {
                            list = staySuggestsDataBaseDto.data.getSuggestList(mContext);
                        } else
                        {
                            throw new BaseException(staySuggestsDataBaseDto.msgCode, staySuggestsDataBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return list;
                }
            });
    }

    @Override
    public Observable<List<GourmetSuggestV2>> getSuggestsByGourmet(String visitDate, String keyword)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v5/gourmet/sales/search/suggest"//
            : "NjgkNjYkMzMkMjQkMTQkMzAkNjkkMTExJDY4JDExOCQ5NiQxMDckODMkOTckMjQkMzIk$NDVFRUFBRTFFNTOQ0RTcwQUEO4CQThCXZMURFRZDY3NkEwN0VDMDY3Q0EwQUExOEY5RjAyMMTjAA3KOEZDMTVEGNjRDNUFCM0I0BQNjFEODc4NjPZEQ0JEHMkM3NUUEzODk1NTNDMjM5NDU4$";


        return mDailyMobileService.getSuggestsByGourmet(Crypto.getUrlDecoderEx(URL), visitDate, keyword) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<GourmetSuggestsData>, List<GourmetSuggestV2>>()
            {
                @Override
                public List<GourmetSuggestV2> apply(BaseDto<GourmetSuggestsData> gourmetSuggestsDataBaseDto) throws Exception
                {
                    List<GourmetSuggestV2> list;

                    if (gourmetSuggestsDataBaseDto != null)
                    {
                        if (gourmetSuggestsDataBaseDto.msgCode == 100 && gourmetSuggestsDataBaseDto.data != null)
                        {
                            list = gourmetSuggestsDataBaseDto.data.getSuggestList(mContext);
                        } else
                        {
                            throw new BaseException(gourmetSuggestsDataBaseDto.msgCode, gourmetSuggestsDataBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return list;
                }
            });
    }
}
