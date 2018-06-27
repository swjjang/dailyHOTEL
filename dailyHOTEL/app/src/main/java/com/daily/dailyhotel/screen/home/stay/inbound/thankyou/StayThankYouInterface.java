package com.daily.dailyhotel.screen.home.stay.inbound.thankyou;

import android.animation.Animator;
import android.text.SpannableString;

import com.daily.dailyhotel.base.BaseBlurViewInterface;
import com.daily.dailyhotel.entity.CarouselListItem;

import java.util.ArrayList;

import io.reactivex.Observable;

public interface StayThankYouInterface extends BaseBlurViewInterface
{
    void setUserName(String guestName);

    void setImageUrl(String imageUrl);

    void setBooking(SpannableString checkInDate, SpannableString checkOutDate, int nights, String stayName, String roomType);

    void startRecommendAnimation(Animator.AnimatorListener listener);

    void setNoticeVisible(boolean visible);

    void setNoticeText(String notice);

    void setRecommendGourmetData(ArrayList<CarouselListItem> carouselListItemList);

    Observable<Boolean> getReceiptAnimation();

    ArrayList<CarouselListItem> getRecommendGourmetData();

    void setDepositStickerCardVisible(boolean visible);

    void setDepositStickerCard(String titleText, int nights, String warningText, String descriptionText, boolean warningTextColor);

    void startRecommendGourmetViewAnimation();

    void stopRecommendGourmetViewAnimation();

    void setRecommendGourmetViewVisible(boolean visible);
}
