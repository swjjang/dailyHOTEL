package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.Booking;
import com.daily.dailyhotel.entity.StayOutboundBookingDetail;
import com.twoheart.dailyhotel.network.dto.BaseDto;

import java.util.List;

import io.reactivex.Observable;

public interface BookingInterface
{
    // Stay Outbound 예약 리스트
    Observable<List<Booking>> getStayOutboundBookingList();

    // 예약 리스트
    Observable<List<Booking>> getBookingList();

    // 예약 리스트
    Observable<Boolean> getStayOutboundHideBooking(int reservationIndex);

    Observable<StayOutboundBookingDetail> getStayOutboundBookingDetail(int reservationIndex);
}
