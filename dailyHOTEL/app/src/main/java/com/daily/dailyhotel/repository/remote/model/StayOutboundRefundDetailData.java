package com.daily.dailyhotel.repository.remote.model;

import android.support.v4.util.Pair;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayOutboundBookingDetail;
import com.daily.dailyhotel.entity.StayOutboundRefundDetail;

import java.util.ArrayList;
import java.util.List;

@JsonObject
public class StayOutboundRefundDetailData
{
    @JsonField(name = "reservationIdx")
    public int reservationIdx;

    @JsonField(name = "hotelId")
    public int hotelId;

    @JsonField(name = "hotelName")
    public String hotelName;

    @JsonField(name = "hotelAddress")
    public String hotelAddress;

    @JsonField(name = "checkinDate")
    public String checkinDate;

    @JsonField(name = "checkinTime")
    public String checkinTime;

    @JsonField(name = "checkoutDate")
    public String checkoutDate;

    @JsonField(name = "checkoutTime")
    public String checkoutTime;

    @JsonField(name = "roomName")
    public String roomName;

    @JsonField(name = "refundStatus")
    public String refundStatus;

    @JsonField(name = "reasons")
    public List<ReasonType> reasons;

    @JsonField(name = "paymentType")
    public String paymentType;

    @JsonField(name = "paymentDate")
    public String paymentDate;

    @JsonField(name = "total")
    public int total;

    @JsonField(name = "paymentAmount")
    public int paymentAmount;

    @JsonField(name = "bonus")
    public int bonus;

    public StayOutboundRefundDetailData()
    {

    }

    public StayOutboundRefundDetail getStayOutboundBookingDetail()
    {
        StayOutboundRefundDetail stayOutboundRefundDetail = new StayOutboundRefundDetail();

        stayOutboundRefundDetail.stayIndex = hotelId;
        stayOutboundRefundDetail.bookingIndex = reservationIdx;
        stayOutboundRefundDetail.name = hotelName;
        stayOutboundRefundDetail.roomName = roomName;
        stayOutboundRefundDetail.address = hotelAddress;
        stayOutboundRefundDetail.paymentPrice = paymentAmount;
        stayOutboundRefundDetail.bonus = bonus;
        stayOutboundRefundDetail.totalPrice = total;
        stayOutboundRefundDetail.refundStatus = StayOutboundBookingDetail.RefundType.valueOf(refundStatus);
        stayOutboundRefundDetail.checkInDate = checkinDate;
        stayOutboundRefundDetail.checkInTime = checkinTime;
        stayOutboundRefundDetail.checkOutDate = checkoutDate;
        stayOutboundRefundDetail.checkOutTime = checkoutTime;
        stayOutboundRefundDetail.paymentType = StayOutboundBookingDetail.PaymentType.valueOf(paymentType);
        stayOutboundRefundDetail.paymentDate = paymentDate;

        List<Pair<String, String>> cancelReasonTypeList = new ArrayList<>();

        if(reasons != null)
        {
            for (ReasonType reasonType : reasons)
            {
                cancelReasonTypeList.add(new Pair(reasonType.key, reasonType.text));
            }
        }

        stayOutboundRefundDetail.setCancelReasonTypeList(cancelReasonTypeList);

        return stayOutboundRefundDetail;
    }

    @JsonObject
    static class ReasonType
    {
        @JsonField(name = "key")
        public String key;

        @JsonField(name = "text")
        public String text;

        public ReasonType()
        {

        }
    }
}
