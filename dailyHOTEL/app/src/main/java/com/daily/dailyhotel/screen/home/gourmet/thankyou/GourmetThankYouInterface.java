package com.daily.dailyhotel.screen.home.gourmet.thankyou;

import android.animation.Animator;
import android.text.SpannableString;

import com.daily.base.BaseDialogViewInterface;

public interface GourmetThankYouInterface extends BaseDialogViewInterface
{
    void setUserName(String guestName);

    void setImageUrl(String imageUrl);

    void setBooking(String visitDate, String visitTime, String gourmetName, String productType, int productCount);

    void startAnimation(Animator.AnimatorListener listener);
}
