package com.daily.dailyhotel.screen.home.stay.inbound.payment;

import android.content.DialogInterface;
import android.text.SpannableString;
import android.view.View;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.Card;
import com.daily.dailyhotel.view.DailyBookingPaymentTypeView;

public interface StayPaymentInterface extends BaseDialogViewInterface
{
    void setCheeringMessage(boolean enabledSticker, String titleText, String warningText);

    void setCheeringMessageVisible(boolean visible);

    void setBooking(SpannableString checkInDate, SpannableString checkOutDate, int nights, String stayName, String roomName);

    void setUserInformation(String name, String mobile, String email);

    void setGuestInformation(String name, String mobile, String email);

    void setGuestMobileInformation(String mobile);

    void setBonus(boolean selected, int bonus, int discountPrice);

    void setCoupon(boolean selected, int couponPrice);

    void setDepositSticker(boolean selected);

    void setStayPayment(int nights, int totalPrice, int discountPrice);

    void setEasyCard(Card card);

    void setRefundPolicy(String refundPolicy);

    void setVendorName(String vendorName);

    void setGuidePaymentType(String text);

    void setPaymentTypeEnabled(DailyBookingPaymentTypeView.PaymentType paymentType, boolean enabled);

    void setPaymentType(DailyBookingPaymentTypeView.PaymentType paymentType);

    void setBonusGuideText(String text);

    void setBonusEnabled(boolean enabled);

    void setTransportation(String type);

    void setTransportationType(@StayPaymentPresenter.Transportation String type);

    void setGuestInformationVisible(boolean visible);

    void setOverseas(boolean overseas);

    void showAgreeTermDialog(DailyBookingPaymentTypeView.PaymentType paymentType, int[] messages//
        , View.OnClickListener onClickListener, DialogInterface.OnCancelListener cancelListener);
}
