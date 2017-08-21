package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.SuggestInterface;
import com.daily.dailyhotel.entity.Suggest;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseListDto;
import com.twoheart.dailyhotel.network.model.GourmetKeyword;
import com.twoheart.dailyhotel.network.model.StayKeyword;
import com.twoheart.dailyhotel.place.layout.PlaceSearchLayout;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

public class SuggestRemoteImpl implements SuggestInterface
{
    private Context mContext;

    public SuggestRemoteImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<List<Suggest>> getSuggestsByStayOutbound(String keyword)
    {
        return DailyMobileAPI.getInstance(mContext).getSuggestsByStayOutbound(keyword).map((suggestsDataBaseDto) ->
        {
            List<Suggest> list = null;

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
        return DailyMobileAPI.getInstance(mContext).getSuggestsByStayInbound(checkInDate, stays, keyword) //
            .map(new Function<BaseListDto<StayKeyword>, Pair<String, ArrayList<StayKeyword>>>()
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
        return DailyMobileAPI.getInstance(mContext).getSuggestsByGourmet(visitDate, keyword) //
            .map(new Function<BaseListDto<GourmetKeyword>, Pair<String, ArrayList<GourmetKeyword>>>()
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
}
