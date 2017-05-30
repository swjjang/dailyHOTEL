package com.daily.dailyhotel.screen.stay.outbound.payment;

import android.text.SpannableString;

import com.daily.base.BaseViewInterface;
import com.daily.dailyhotel.entity.Card;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayOutboundPayment;
import com.daily.dailyhotel.entity.UserInformation;

import java.util.List;

public interface StayOutboundPaymentInterface extends BaseViewInterface
{
    void setBooking(SpannableString checkInDate, SpannableString checkOutDate, int nights, String stayName, String roomType);

    void setUserInformation(UserInformation userInformation, String firstName, String lastName, String phone, String email);

    void setPeople(People people);

    void setStayOutboundPayment(int bonus, int nights, int totalPrice, int discountPrice, int paymentPrice, double taxPrice);

    void setSimpleCard(Card card);

    void setRefundDescriptionList(List<String> refundDescriptionList);

    void setMemoPaymentType(String memo);

    void setPaymentTypeEnabled(StayOutboundPayment.PaymentType paymentType, boolean enabled);

    void setPaymentType(StayOutboundPayment.PaymentType paymentType);
}
