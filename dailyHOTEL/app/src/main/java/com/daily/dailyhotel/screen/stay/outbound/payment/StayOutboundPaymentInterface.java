package com.daily.dailyhotel.screen.stay.outbound.payment;

import com.daily.base.BaseViewInterface;
import com.daily.dailyhotel.entity.Card;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayOutboundPayment;
import com.daily.dailyhotel.entity.User;

public interface StayOutboundPaymentInterface extends BaseViewInterface
{
    void setBooking(String checkInDate, String checkOutDate, int nights, String stayName, String roomType);

    void setUser(User user, String firstName, String lastName, String phone, String email);

    void setPeople(People people);

    void setStayOutboundPayment(int bonus, StayOutboundPayment stayOutboundPayment, int nights);

    void setSimpleCard(Card card);
}
