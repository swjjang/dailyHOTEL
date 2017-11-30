package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.GourmetMultiBookingDetail;
import com.daily.dailyhotel.entity.ReservationInfo;
import com.daily.dailyhotel.entity.TicketInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_sam on 2017. 11. 29..
 */
@JsonObject
public class GourmetMultiBookingDetailData
{
    @JsonField(name = "aggregationId")
    public String aggregationId; // (string): 결제키 ,

    @JsonField(name = "canceled")
    public boolean canceled; // (boolean): 취소여부 ,

    @JsonField(name = "canceledAt")
    public String canceledAt; // (string, optional): 취소일 ,

    @JsonField(name = "guestInfo")
    public GuestInfoData guestInfo; // (GuestInfo): 고객정보 ,

    @JsonField(name = "paymentInfo")
    public PaymentInfoData paymentInfo; // (PaymentInfo): 결제정보 ,

    @JsonField(name = "reservationInfos")
    public List<ReservationInfoData> reservationInfos; // (Array[ReservationInfo]): 예약정보 ,

    @JsonField(name = "restaurantInfo")
    public RestaurantInfoData restaurantInfo; // (RestaurantInfo): 업장정보 ,

    @JsonField(name = "reviewInfo")
    public ReviewInfoData reviewInfo; // (ReviewInfo): 리뷰정보 ,

    @JsonField(name = "ticketInfos")
    public List<TicketInfoData> ticketInfos; // (Array[TicketInfo]): 티켓정보

    public GourmetMultiBookingDetail getGourmetBookingDetail()
    {
        GourmetMultiBookingDetail bookingDetail = new GourmetMultiBookingDetail();

        bookingDetail.aggregationId = this.aggregationId;
        bookingDetail.canceled = this.canceled;
        bookingDetail.canceledAt = this.canceledAt;
        bookingDetail.guestInfo = this.guestInfo == null ? null : this.guestInfo.getGuestInfo();
        bookingDetail.paymentInfo = this.paymentInfo == null ? null : this.paymentInfo.getPaymentInfo();

        List<ReservationInfo> reservationInfoList = new ArrayList<>();
        for (ReservationInfoData reservationInfoData : this.reservationInfos)
        {
            reservationInfoList.add(reservationInfoData.getReservationInfo());
        }

        bookingDetail.reservationInfos = reservationInfoList;

        bookingDetail.restaurantInfo = this.restaurantInfo == null ? null : this.restaurantInfo.getRestaurantInfo();
        bookingDetail.reviewInfo = this.reviewInfo == null ? null : this.reviewInfo.getReviewInfo();

        List<TicketInfo> ticketInfoList = new ArrayList<>();
        for (TicketInfoData ticketInfoData : this.ticketInfos)
        {
            ticketInfoList.add(ticketInfoData.getTicketInfo());
        }

        bookingDetail.ticketInfos = ticketInfoList;

        return bookingDetail;
    }
}
