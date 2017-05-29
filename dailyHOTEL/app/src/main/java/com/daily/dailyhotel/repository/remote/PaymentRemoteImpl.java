package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.domain.PaymentInterface;
import com.daily.dailyhotel.entity.StayOutboundPayment;

import io.reactivex.Observable;

public class PaymentRemoteImpl implements PaymentInterface
{
    private Context mContext;

    public PaymentRemoteImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<StayOutboundPayment> getStayOutBoundPayment()
    {
        return null;
    }
}
