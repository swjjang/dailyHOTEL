package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.Card;
import com.daily.dailyhotel.entity.Guest;
import com.daily.dailyhotel.entity.PaymentTypeEasy;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.Reservation;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundPayment;

import java.util.List;

import io.reactivex.Observable;

public interface ReservationInterface
{
    // Stay Outbound 예약 리스트
    Observable<List<Reservation>> getStayOutBoundReservationList();

    // 예약 리스트
    Observable<List<Reservation>> getReservationList();
}
