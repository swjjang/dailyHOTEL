package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.GourmetReceipt;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.text.ParseException;

/**
 * Created by android_sam on 2017. 12. 7..
 */
@JsonObject
public class GourmetReceiptData
{
    @JsonField(name = "couponAmount")
    public int couponAmount; // (integer, optional),

    @JsonField(name = "gourmetReservationIdx")
    public int gourmetReservationIdx; // (integer, optional),

    @JsonField(name = "price")
    public int price; // (integer, optional),

    @JsonField(name = "paidAt")
    public String paidAt; // (string, optional),

    @JsonField(name = "paymentAmount")
    public int paymentAmount; // (integer, optional),

    @JsonField(name = "paymentType")
    public String paymentType; // (string, optional) = ['신용카드', '계좌이체', '휴대폰 결제', '적립금 전액결제', '쿠폰 전액결제', '신용/체크카드 간편결제', '신용/체크카드 일반결제', '수기수수료', '수기결제'],

    @JsonField(name = "restaurantAddress")
    public String restaurantAddress; // (string, optional),

    @JsonField(name = "restaurantName")
    public String restaurantName; // (string, optional),

    @JsonField(name = "sday")
    public String sday; // (string, optional),

    @JsonField(name = "ticketCount")
    public int ticketCount; // (integer, optional),

    @JsonField(name = "userName")
    public String userName; // (string, optional),

    @JsonField(name = "userPhone")
    public String userPhone; // (string, optional)

    @JsonField(name = "notice")
    public String notice; // (string, optional)

    public GourmetReceipt getGourmetReceipt()
    {
        GourmetReceipt gourmetReceipt = new GourmetReceipt();
        gourmetReceipt.couponAmount = couponAmount;
        gourmetReceipt.gourmetReservationIdx = gourmetReservationIdx;
        gourmetReceipt.price = price;

        try
        {
            gourmetReceipt.paidAt = DailyCalendar.convertDateFormatString(paidAt, DailyCalendar.ISO_8601_FORMAT, "yyyy/MM/dd");
        } catch (ParseException e)
        {
            gourmetReceipt.paidAt = paidAt;
        }

        gourmetReceipt.paymentAmount = paymentAmount;
        gourmetReceipt.paymentType = paymentType;
        gourmetReceipt.restaurantAddress = restaurantAddress;
        gourmetReceipt.restaurantName = restaurantName;
        gourmetReceipt.sday = sday;
        gourmetReceipt.ticketCount = ticketCount;
        gourmetReceipt.userName = userName;
        gourmetReceipt.userPhone = userPhone;
        gourmetReceipt.notice = notice;

        return gourmetReceipt;
    }
}
