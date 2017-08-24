package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.GourmetPayment;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@JsonObject
public class GourmetPaymentData
{
    @JsonField(name = "business_name")
    public String business_name;

    @JsonField(name = "discount")
    public int discount;

    @JsonField(name = "sday")
    public long sday;

    @JsonField(name = "minimum_order_quantity")
    public int minimum_order_quantity;

    @JsonField(name = "max_sale_count")
    public int max_sale_count;

    @JsonField(name = "eating_time_list")
    public List<Long> eating_time_list;

    public GourmetPaymentData()
    {

    }

    public GourmetPayment getGourmetPayment()
    {
        GourmetPayment gourmetPayment = new GourmetPayment();

        gourmetPayment.visitDate = DailyCalendar.format(sday - DailyCalendar.NINE_HOUR_MILLISECOND, DailyCalendar.ISO_8601_FORMAT, TimeZone.getTimeZone("GMT+09:00"));
        gourmetPayment.totalPrice = discount;
        gourmetPayment.businessName = business_name;
        gourmetPayment.minMenuCount = minimum_order_quantity;
        gourmetPayment.maxMenuCount = max_sale_count;

        List<String> timeList = new ArrayList<>();

        for (long time : eating_time_list)
        {
            timeList.add(DailyCalendar.format(time - DailyCalendar.NINE_HOUR_MILLISECOND, DailyCalendar.ISO_8601_FORMAT, TimeZone.getTimeZone("GMT+09:00")));
        }

        gourmetPayment.setVisitTimeList(timeList);

        return gourmetPayment;
    }
}
