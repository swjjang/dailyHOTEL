package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.domain.StayOutboundReceiptInterface;
import com.daily.dailyhotel.entity.StayOutboundReceipt;
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
                return null;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> getStayOutboundEmailReceipt(int bookingIndex)
    {
        return null;
    }
}
