package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.StayOutboundPayment;
import com.daily.dailyhotel.entity.Suggest;

import java.util.List;

import io.reactivex.Observable;

public interface PaymentInterface
{
    Observable<StayOutboundPayment> getStayOutBoundPayment();
}
