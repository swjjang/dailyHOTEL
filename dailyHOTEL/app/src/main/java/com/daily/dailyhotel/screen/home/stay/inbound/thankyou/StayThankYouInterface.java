package com.daily.dailyhotel.screen.home.stay.inbound.thankyou;

import android.animation.Animator;
import android.text.SpannableString;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.CarouselListItem;

import java.util.ArrayList;

import io.reactivex.Observable;

public interface StayThankYouInterface extends BaseDialogViewInterface
{
    void setUserName(String guestName);

    void setImageUrl(String imageUrl);

    void setBooking(SpannableString checkInDate, SpannableString checkOutDate, int nights, String stayName, String roomType);

    void startReceiptAnimation(Animator.AnimatorListener listener);

    void startRecommendNStampAnimation(Animator.AnimatorListener listener, boolean stampEnable);

    void setNoticeVisible(boolean visible);

    void setNoticeText(String notice);

    void setStampVisible(boolean visible);

    void setStampMessages(String message1, String message2, String message3);

    void setRecommendGourmetData(ArrayList<CarouselListItem> carouselListItemList);

    Observable<Boolean> getReceiptAnimation();
}
