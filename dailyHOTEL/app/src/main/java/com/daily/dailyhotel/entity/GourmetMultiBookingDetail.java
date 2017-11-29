package com.daily.dailyhotel.entity;

import java.util.List;

/**
 * Created by android_sam on 2017. 11. 29..
 */

public class GourmetMultiBookingDetail
{
    public String aggregationId; // (string): 결제키 ,
    public boolean canceled; // (boolean): 취소여부 ,
    public String canceledAt; // (string, optional): 취소일 ,
    public GuestInfo guestInfo; // (GuestInfo): 고객정보 ,
    public PaymentInfo paymentInfo; // (PaymentInfo): 결제정보 ,
    public List<ReservationInfo> reservationInfos; // (Array[ReservationInfo]): 예약정보 ,
    public RestaurantInfo restaurantInfo; // (RestaurantInfo): 업장정보 ,
    public ReviewInfo reviewInfo; // (ReviewInfo): 리뷰정보 ,
    public List<TicketInfo> ticketInfos; // (Array[TicketInfo]): 티켓정보
}
