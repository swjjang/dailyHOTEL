package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.SuggestInterface;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.network.dto.BaseListDto;
import com.twoheart.dailyhotel.network.model.GourmetKeyword;
import com.twoheart.dailyhotel.network.model.StayKeyword;
import com.twoheart.dailyhotel.place.layout.PlaceSearchLayout;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import java.util.ArrayList;
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

        final String API = Constants.UNENCRYPTED_URL ? "api/v1/suggests"//
            : "MTEkNDQkMzckNDQkMyQxMiQzOCQ0NCQ3JDQwJDEzJDUyJDIzJDQwJDQ4JDIxJA==$Q0ZUCMzDg1RjYULXwOTcyMMjTI4RkI3NUFEOUNGRjOgSN3BMkPZSFNzXTAQ=$";

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
    public Observable<Pair<String, ArrayList<StayKeyword>>> getSuggestsByStayInbound(String checkInDate, int stays, final String keyword)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/hotels/sales/search/suggest"//
            : "NDYkNDUkMjIkMTkkMTE3JDEzMCQxMiQ4MiQxMTMkMTA5JDEwOSQ0OSQ3MyQ4MiQyMSQzMSQ=$Nzc1NkI5NTdDHNzQzNUYLIzRkLE5NkUD0NjM4RjVCRTZCMzA3MLLTFNBRUM0RjE0MkVERUQyMDNSCOENENjYGzNM0VGREZDMjdFMkU4RUJEMzI0ODkJAxNzZCFNUU0NTUYxMEVDMDgwQDTlE$";

        return mDailyMobileService.getSuggestsByStayInbound(Crypto.getUrlDecoderEx(URL), checkInDate, stays, keyword) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseListDto<StayKeyword>, Pair<String, ArrayList<StayKeyword>>>()
            {
                @Override
                public Pair<String, ArrayList<StayKeyword>> apply(@io.reactivex.annotations.NonNull BaseListDto<StayKeyword> stayKeywordBaseListDto) throws Exception
                {
                    Pair<String, ArrayList<StayKeyword>> pair = new Pair(keyword, new ArrayList<StayKeyword>());

                    if (stayKeywordBaseListDto != null)
                    {
                        if (stayKeywordBaseListDto.msgCode == 100 && stayKeywordBaseListDto.data != null)
                        {
                            for (StayKeyword keyword : stayKeywordBaseListDto.data)
                            {
                                if (keyword.index > 0)
                                {
                                    keyword.icon = PlaceSearchLayout.HOTEL_ICON;
                                }

                                pair.second.add(keyword);
                            }
                        } else
                        {
                            throw new BaseException(stayKeywordBaseListDto.msgCode, stayKeywordBaseListDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return pair;
                }
            }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Pair<String, ArrayList<GourmetKeyword>>> getSuggestsByGourmet(String visitDate, final String keyword)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/gourmet/sales/search/suggest"//
            : "NjMkNzkkMTE3JDQwJDQxJDUzJDk0JDc0JDU2JDE5JDQ0JDYzJDExJDM4JDQ3JDEyJA==$OUYzQkNBN0ZOVFMDc2QjAIzMEU4OTBBODhENkI3OQTc4GNMXKEZBNDQ5MUIA1MFzIwOGTZERTZWDRUIxMTTA3QkQyQUTZGOUYzRjhBROTJGNTc4OTNFOTUzNDQwN0M3OUQVzQkFFMTlBNUM2$";

        return mDailyMobileService.getSuggestsByGourmet(Crypto.getUrlDecoderEx(URL), visitDate, keyword) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseListDto<GourmetKeyword>, Pair<String, ArrayList<GourmetKeyword>>>()
            {
                @Override
                public Pair<String, ArrayList<GourmetKeyword>> apply(@io.reactivex.annotations.NonNull BaseListDto<GourmetKeyword> gourmetKeywordBaseListDto) throws Exception
                {
                    Pair<String, ArrayList<GourmetKeyword>> pair = new Pair(keyword, new ArrayList<GourmetKeyword>());

                    if (gourmetKeywordBaseListDto != null)
                    {
                        if (gourmetKeywordBaseListDto.msgCode == 100 && gourmetKeywordBaseListDto.data != null)
                        {
                            for (GourmetKeyword keyword : gourmetKeywordBaseListDto.data)
                            {
                                if (keyword.index > 0)
                                {
                                    keyword.icon = PlaceSearchLayout.GOURMET_ICON;
                                }

                                pair.second.add(keyword);
                            }
                        } else
                        {
                            throw new BaseException(gourmetKeywordBaseListDto.msgCode, gourmetKeywordBaseListDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }
                    return pair;
                }
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
}
