package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.Booking;
import com.daily.dailyhotel.entity.StayOutboundBookingDetail;
import com.daily.dailyhotel.entity.StayOutboundRefund;
import com.daily.dailyhotel.entity.StayOutboundRefundDetail;

import java.util.List;

import io.reactivex.Observable;

public interface RefundInterface
{
    // Stay Outbound 환불 상세
    Observable<StayOutboundRefundDetail> getStayOutboundRefundDetail(int bookingIndex);

    // Stay Outbound 환불
    Observable<StayOutboundRefund> getStayOutboundRefund(int bookingIndex);
}
