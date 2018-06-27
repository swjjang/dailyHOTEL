package com.daily.dailyhotel.screen.home.stay.outbound.thankyou;

import android.text.SpannableString;

import com.daily.base.BaseDialogViewInterface;

import io.reactivex.Observable;

public interface StayOutboundThankYouInterface extends BaseDialogViewInterface
{
    void setUserName(String guestName);

    void setImageUrl(String imageUrl);

    void setBooking(SpannableString checkInDate, SpannableString checkOutDate, int nights, String stayName, String roomType);

    void setDepositStickerCardVisible(boolean visible);

    void setDepositStickerCard(String titleText, int nights, String warningText, String descriptionText, boolean warningTextColor);

    Observable<Boolean> getReceiptAnimation();
}
