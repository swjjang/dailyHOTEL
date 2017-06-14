package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.StayOutboundReceiptInterface;
import com.daily.dailyhotel.entity.StayOutboundReceipt;
import com.daily.dailyhotel.repository.remote.model.StayOutboundEmailReceiptData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundReceiptData;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

public class StayOutboundReceiptRemoteImpl implements StayOutboundReceiptInterface
{
    private Context mContext;

    public StayOutboundReceiptRemoteImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<StayOutboundReceipt> getStayOutboundReceipt(int bookingIndex)
    {
        return DailyMobileAPI.getInstance(mContext).getStayOutboundReceipt(bookingIndex).map(new Function<BaseDto<StayOutboundReceiptData>, StayOutboundReceipt>()
        {
            @Override
            public StayOutboundReceipt apply(@io.reactivex.annotations.NonNull BaseDto<StayOutboundReceiptData> stayOutboundReceiptDataBaseDto) throws Exception
            {
                StayOutboundReceipt stayOutboundReceipt = null;

                if (stayOutboundReceiptDataBaseDto != null)
                {
                    if (stayOutboundReceiptDataBaseDto.msgCode == 100 && stayOutboundReceiptDataBaseDto.data != null)
                    {
                        stayOutboundReceipt = stayOutboundReceiptDataBaseDto.data.getStayOutboundReceipt();
                    } else
                    {
                        throw new BaseException(stayOutboundReceiptDataBaseDto.msgCode, stayOutboundReceiptDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return stayOutboundReceipt;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<String> getStayOutboundEmailReceipt(int bookingIndex, String email)
    {
        return DailyMobileAPI.getInstance(mContext).getStayOutboundEmailReceipt(bookingIndex, email).map(new Function<BaseDto<StayOutboundEmailReceiptData>, String>()
        {
            @Override
            public String apply(@io.reactivex.annotations.NonNull BaseDto<StayOutboundEmailReceiptData> stayOutboundEmailReceiptDataBaseDto) throws Exception
            {
                String message = null;

                if (stayOutboundEmailReceiptDataBaseDto != null)
                {
                    if (stayOutboundEmailReceiptDataBaseDto.msgCode == 100 && stayOutboundEmailReceiptDataBaseDto.data != null)
                    {
                        message = stayOutboundEmailReceiptDataBaseDto.data.message;
                    } else
                    {
                        throw new BaseException(stayOutboundEmailReceiptDataBaseDto.msgCode, stayOutboundEmailReceiptDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return message;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }
}
