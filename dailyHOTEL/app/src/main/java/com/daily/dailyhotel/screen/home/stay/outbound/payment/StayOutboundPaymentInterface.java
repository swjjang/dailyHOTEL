package com.daily.dailyhotel.screen.home.stay.outbound.payment;

import android.content.DialogInterface;
import android.text.SpannableString;
import android.util.Pair;
import android.view.View;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.Card;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.view.DailyBookingPaymentTypeView;

import java.util.List;

public interface StayOutboundPaymentInterface extends BaseDialogViewInterface
{
    void setCheeringMessage(boolean enabledSticker, String titleText, String warningText);

    void setCheeringMessageVisible(boolean visible);

    void setCardEventVisible(boolean visible);

    void setCardEventData(List<Pair<String, List<String>>> cardEventList);

    void setBooking(SpannableString checkInDate, SpannableString checkOutDate, int nights, String stayName, String roomType);

    void setGuestInformation(String firstName, String lastName, String mobile, String email);

    void setGuestMobileInformation(String mobile);

    void setPeople(People people);

    void setBonus(boolean selected, int bonus, int discountPrice);

    void setCoupon(boolean selected, int couponPrice, boolean rewardCoupon);

    void setDepositSticker(boolean selected);

    void setDepositStickerVisible(boolean visible);

    void setDepositStickerCardVisible(boolean visible);

    void setDepositStickerCard(String titleText, int nights, String warningText, CharSequence descriptionText, boolean warningTextColor);

    void setStayOutboundPayment(int nights, int totalPrice, int discountPrice, double taxPrice);

    void setEasyCard(Card card);

    void setRefundPolicyList(List<String> refundPolicyList, boolean hasRewardCard, boolean nrd);

    void setVendorName(String vendorName);

    void setGuidePaymentType(String text);

    void setPaymentTypeEnabled(DailyBookingPaymentTypeView.PaymentType paymentType, boolean enabled);

    void setPaymentType(DailyBookingPaymentTypeView.PaymentType paymentType);

    void setBonusGuideText(String text);

    void setBonusEnabled(boolean enabled);

    void setMaxCouponAmountText(int maxCouponAmount);

    void setMaxCouponAmountVisible(boolean isVisible);

    void setCouponEnabled(boolean enabled);

    void showAgreeTermDialog(DailyBookingPaymentTypeView.PaymentType paymentType//
        , View.OnClickListener onClickListener, DialogInterface.OnCancelListener cancelListener);

    void setPaymentTypeDescriptionText(DailyBookingPaymentTypeView.PaymentType paymentType, String text);

    void scrollToCheckPriceTitle();
}
