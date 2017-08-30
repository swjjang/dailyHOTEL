package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.Booking;
import com.daily.dailyhotel.entity.GourmetBookingDetail;
import com.daily.dailyhotel.entity.Refund;
import com.daily.dailyhotel.entity.StayBookingDetail;
import com.daily.dailyhotel.entity.StayOutboundBookingDetail;
import com.daily.dailyhotel.entity.WaitingDeposit;

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

    // 예약 상세
    Observable<StayOutboundBookingDetail> getStayOutboundBookingDetail(int reservationIndex);

    // 스테이 예약 상세
    Observable<StayBookingDetail> getStayBookingDetail(String aggregationId);

    // 고메 예약 상세
    Observable<GourmetBookingDetail> getGourmetBookingDetail(String aggregationId);

    // 임금 대기
    Observable<WaitingDeposit> getWaitingDeposit(String aggregationId);
}
