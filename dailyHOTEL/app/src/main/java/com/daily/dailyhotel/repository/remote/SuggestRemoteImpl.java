package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.BaseException;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.SuggestInterface;
import com.daily.dailyhotel.entity.Suggest;
import com.twoheart.dailyhotel.network.DailyMobileAPI;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class SuggestRemoteImpl implements SuggestInterface
{
    private Context mContext;

    public SuggestRemoteImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<List<Suggest>> getSuggestsByStayOutBound(String keyword)
    {
        return DailyMobileAPI.getInstance(mContext).getSuggestsByStayOutbound(keyword).map((suggestsDataBaseDto) ->
        {
            List<Suggest> list = null;

            if (suggestsDataBaseDto != null)
            {
                if (suggestsDataBaseDto.msgCode == 100 && suggestsDataBaseDto.data != null)
                {
                    list = suggestsDataBaseDto.data.getSuggestList();
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
}
