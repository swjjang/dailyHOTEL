package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.Reservation;

import java.util.List;

import io.reactivex.Observable;

public interface ReservationInterface
{
    // Stay Outbound 예약 리스트
    Observable<List<Reservation>> getStayOutboundReservationList();

    // 예약 리스트
    Observable<List<Reservation>> getReservationList();
}
