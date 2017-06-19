package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.CommonInterface;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.twoheart.dailyhotel.network.DailyMobileAPI;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class CommonRemoteImpl implements CommonInterface
{
    private Context mContext;

    public CommonRemoteImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<CommonDateTime> getCommonDateTime()
    {
        return DailyMobileAPI.getInstance(mContext).getCommonDateTime().map((commonDateTimeDataBaseDto) ->
        {
            CommonDateTime commonDateTime = null;

            if (commonDateTimeDataBaseDto != null)
            {
                if (commonDateTimeDataBaseDto.msgCode == 100 && commonDateTimeDataBaseDto.data != null)
                {
                    commonDateTime = commonDateTimeDataBaseDto.data.getCommonDateTime();
                } else
                {
                    throw new BaseException(commonDateTimeDataBaseDto.msgCode, commonDateTimeDataBaseDto.msg);
                }
            } else
            {
                throw new BaseException(-1, null);
            }

            return commonDateTime;
        }).observeOn(AndroidSchedulers.mainThread());
    }
}
