package com.daily.dailyhotel.screen.home.stay.outbound.payment;

import android.content.DialogInterface;
import android.text.SpannableString;
import android.view.View;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.Card;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.view.DailyBookingPaymentTypeView;

import java.util.List;

public interface StayOutboundPaymentInterface extends BaseDialogViewInterface
{
    void setBooking(SpannableString checkInDate, SpannableString checkOutDate, int nights, String stayName, String roomType);

    void setGuestInformation(String firstName, String lastName, String phone, String email);

    void setGuestPhoneInformation(String phone);

    void setPeople(People people);

    void setStayOutboundPayment(int bonus, int nights, int totalPrice, int discountPrice, double taxPrice);

    void setEasyCard(Card card);

    void setRefundPolicyList(List<String> refundPolicyList);

    void setVendorName(String vendorName);

    void setGuidePaymentType(String text);

    void setPaymentTypeEnabled(DailyBookingPaymentTypeView.PaymentType paymentType, boolean enabled);

    void setPaymentType(DailyBookingPaymentTypeView.PaymentType paymentType);

    void setBonusEnabled(boolean enabled);

    void setBonusSelected(boolean selected);

    void showAgreeTermDialog(DailyBookingPaymentTypeView.PaymentType paymentType//
        , View.OnClickListener onClickListener, DialogInterface.OnCancelListener cancelListener);
}
