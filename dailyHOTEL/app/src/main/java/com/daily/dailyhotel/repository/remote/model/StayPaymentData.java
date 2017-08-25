package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayPayment;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.TimeZone;

@JsonObject
public class StayPaymentData
{
    @JsonField(name = "provide_transportation")
    public boolean provide_transportation;

    @JsonField(name = "parking")
    public boolean parking;

    @JsonField(name = "no_parking")
    public boolean no_parking;

    @JsonField(name = "business_name")
    public String business_name;

    @JsonField(name = "discount_total")
    public int discount_total;

    @JsonField(name = "discount_avg")
    public int discount_avg;

    @JsonField(name = "check_in_date")
    public long check_in_date;

    @JsonField(name = "check_out_date")
    public long check_out_date;

    @JsonField(name = "refund_type")
    public String refund_type;

    @JsonField(name = "on_sale")
    public boolean on_sale;

    @JsonField(name = "available_rooms")
    public int available_rooms;


    public StayPaymentData()
    {

    }

    public StayPayment getStayPayment()
    {
        StayPayment stayPayment = new StayPayment();

        stayPayment.soldOut = on_sale == false || available_rooms == 0;

        stayPayment.checkInDate = DailyCalendar.format(check_in_date - DailyCalendar.NINE_HOUR_MILLISECOND, DailyCalendar.ISO_8601_FORMAT, TimeZone.getTimeZone("GMT+09:00"));
        stayPayment.checkOutDate = DailyCalendar.format(check_out_date - DailyCalendar.NINE_HOUR_MILLISECOND, DailyCalendar.ISO_8601_FORMAT, TimeZone.getTimeZone("GMT+09:00"));

        stayPayment.refundType = refund_type;
        stayPayment.totalPrice = discount_total;


        if (provide_transportation == true)
        {
            if (no_parking == true)
            {
                stayPayment.transportation = StayPayment.VISIT_TYPE_NO_PARKING;
            } else if (parking == true)
            {
                stayPayment.transportation = StayPayment.VISIT_TYPE_PARKING;
            } else
            {
                stayPayment.transportation = StayPayment.VISIT_TYPE_NONE;
            }
        } else
        {
            stayPayment.transportation = StayPayment.VISIT_TYPE_NONE;
        }

        stayPayment.businessName = business_name;

        return stayPayment;
    }
}
