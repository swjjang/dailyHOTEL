package com.daily.dailyhotel.screen.home.stay.inbound.payment;

import android.content.DialogInterface;
import android.text.SpannableString;
import android.view.View;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.Card;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.view.DailyBookingPaymentTypeView;

import java.util.List;

public interface StayPaymentInterface extends BaseDialogViewInterface
{
    void setBooking(SpannableString checkInDate, SpannableString checkOutDate, int nights, String stayName, String roomName);

    void setUserInformation(String name, String mobile, String email);

    void setGuestInformation(String name, String mobile, String email);

    void setGuestMobileInformation(String mobile);

    void setStayPayment(boolean usedBonus, int bonus, boolean usedCoupon, int coupon, int nights, int totalPrice, int discountPrice);

    void setEasyCard(Card card);

    void setRefundPolicy(String refundPolicy);

    void setVendorName(String vendorName);

    void setGuidePaymentType(String text);

    void setPaymentTypeEnabled(DailyBookingPaymentTypeView.PaymentType paymentType, boolean enabled);

    void setPaymentType(DailyBookingPaymentTypeView.PaymentType paymentType);

    void setBonusGuideText(String text);

    void setBonusEnabled(boolean enabled);

    void setBonusSelected(boolean selected);

    void setCouponSelected(boolean selected);

    void setTransportation(String type);

    void setTransportationType(StayPaymentPresenter.Transportation type);

    void showGuestInformation();

    void hideGuestInformation();

    void setOverseas(boolean overseas);

    void showAgreeTermDialog(DailyBookingPaymentTypeView.PaymentType paymentType//
        , View.OnClickListener onClickListener, DialogInterface.OnCancelListener cancelListener);
}
