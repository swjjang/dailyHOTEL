package com.daily.dailyhotel.screen.home.stay.outbound.thankyou;

import android.animation.Animator;
import android.text.SpannableString;

import com.daily.base.BaseDialogViewInterface;

public interface StayOutboundThankYouInterface extends BaseDialogViewInterface
{
    void setUserName(String guestName);

    void setImageUrl(String imageUrl);

    void setBooking(SpannableString checkInDate, SpannableString checkOutDate, int nights, String stayName, String roomType);

    void startAnimation(Animator.AnimatorListener listener);
}
