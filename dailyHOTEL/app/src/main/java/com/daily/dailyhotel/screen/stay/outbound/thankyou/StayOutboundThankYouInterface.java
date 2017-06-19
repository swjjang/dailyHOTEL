package com.daily.dailyhotel.screen.stay.outbound.thankyou;

import android.text.SpannableString;

import com.daily.base.BaseDialogViewInterface;

public interface StayOutboundThankYouInterface extends BaseDialogViewInterface
{
    void setUserName(String guestName);

    void setImageUrl(String imageUrl);

    void setBooking(SpannableString checkInDate, SpannableString checkOutDate, int nights, String stayName, String roomType);

    void startAnimation();
}
