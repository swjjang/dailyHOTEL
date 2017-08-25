package com.daily.dailyhotel.screen.home.gourmet.payment;

import android.content.DialogInterface;
import android.view.View;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.Card;
import com.daily.dailyhotel.view.DailyBookingPaymentTypeView;

import java.util.List;

public interface GourmetPaymentInterface extends BaseDialogViewInterface
{
    void setBooking(String visitDate, String gourmetName, String menuName);

    void setVisitTime(String time);

    void setMenuCount(int menuCount);

    void setUserInformation(String name, String mobile, String email);

    void setGuestInformation(String name, String mobile, String email);

    void setGuestMobileInformation(String mobile);

    void setBonus(boolean selected, int bonus, int discountPrice);

    void setCoupon(boolean selected, int couponPrice);

    void setGourmetPayment(int menuCount, int totalPrice, int discountPrice);

    void setEasyCard(Card card);

    void setVendorName(String vendorName);

    void setGuidePaymentType(String text);

    void setPaymentTypeEnabled(DailyBookingPaymentTypeView.PaymentType paymentType, boolean enabled);

    void setPaymentType(DailyBookingPaymentTypeView.PaymentType paymentType);

    void setBonusGuideText(String text);

    void setBonusEnabled(boolean enabled);

    void setGuestInformationVisible(boolean visible);

    void setOverseas(boolean overseas);

    void setMenuMinusEnabled(boolean enabled);

    void setMenuPlusEnabled(boolean enabled);

    void showAgreeTermDialog(DailyBookingPaymentTypeView.PaymentType paymentType, int[] messages//
        , View.OnClickListener onClickListener, DialogInterface.OnCancelListener cancelListener);

    void showDatePickerDialog(String titleText, List<String> visitTimeList, String selectedVisitTime//
        , View.OnClickListener onClickListener, DialogInterface.OnDismissListener dismissListener);
}
