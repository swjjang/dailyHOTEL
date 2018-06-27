package com.daily.dailyhotel.screen.home.stay.inbound.thankyou;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ScrollView;

import com.daily.base.BaseActivity;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyScrollView;
import com.daily.dailyhotel.animation.ThankYouScaleAnimator;
import com.daily.dailyhotel.animation.ThankYouScreenAnimator;
import com.daily.dailyhotel.base.BaseBlurView;
import com.daily.dailyhotel.entity.CarouselListItem;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.daily.dailyhotel.view.carousel.DailyCarouselLayout;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayPaymentThankYouDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.schedulers.Schedulers;

public class StayThankYouView extends BaseBlurView<StayThankYouView.OnEventListener, ActivityStayPaymentThankYouDataBinding> implements StayThankYouInterface, View.OnClickListener
{
    private ObjectAnimator mRecommendGourmetAnimator;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onConfirmClick();

        void onRecommendGourmetViewAllClick();

        void onRecommendGourmetItemClick(View view);

        void onRecommendGourmetItemLongClick(View view);
    }

    public StayThankYouView(BaseActivity baseActivity, StayThankYouView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayPaymentThankYouDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        viewDataBinding.thankYouInformationView.setReservationTitle(R.string.label_booking_room_info);
        viewDataBinding.confirmView.setOnClickListener(this);

        viewDataBinding.thankYouInformationView.setVisibility(View.INVISIBLE);
        viewDataBinding.checkImageView.setVisibility(View.INVISIBLE);
        viewDataBinding.recommendGourmetLayout.setTitleText(R.string.label_booking_reservation_recommend_gourmet_title);
        viewDataBinding.recommendGourmetLayout.setVisibility(View.GONE);
        viewDataBinding.recommendGourmetLayout.setCarouselListener(new DailyCarouselLayout.OnCarouselListener()
        {
            @Override
            public void onViewAllClick()
            {
                getEventListener().onRecommendGourmetViewAllClick();
            }

            @Override
            public void onItemClick(View view, android.support.v4.util.Pair[] pairs)
            {
                getEventListener().onRecommendGourmetItemClick(view);
            }

            @Override
            public void onItemLongClick(View view, android.support.v4.util.Pair[] pairs)
            {
                getEventListener().onRecommendGourmetItemLongClick(view);
            }
        });

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.scrollLayout, getColor(R.color.transparent));

        viewDataBinding.scrollLayout.setOnScrollChangedListener(new DailyScrollView.OnScrollChangedListener()
        {
            @Override
            public void onScrollChanged(ScrollView scrollView, int l, int t, int oldl, int oldt)
            {
                if (getViewDataBinding().recommendGourmetLayout.hasData() == false //
                    || View.VISIBLE != getViewDataBinding().recommendGourmetLayout.getVisibility())
                {
                    return;
                }

                if (getViewDataBinding().recommendGourmetLayout.getTop() <= t + scrollView.getHeight() - ScreenUtils.dpToPx(getContext(), 50))
                {
                    setRecommendGourmetViewVisible(false);
                    stopRecommendGourmetViewAnimation();
                } else
                {
                    setRecommendGourmetViewVisible(true);
                    startRecommendGourmetViewAnimation();
                }
            }
        });

        viewDataBinding.recommendGourmetButtonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                viewDataBinding.scrollLayout.smoothScrollTo(0, getViewDataBinding().recommendGourmetLayout.getBottom());
            }
        });
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(title);
    }

    @Override
    public void setUserName(String userName)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(userName) == false)
        {
            SpannableString spannableString = new SpannableString(getString(R.string.message_completed_payment_format, userName));
            spannableString.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(getContext()).getMediumTypeface()),//
                0, userName.length(),//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            getViewDataBinding().thankYouInformationView.setMessageText(spannableString);
        } else
        {
            getViewDataBinding().thankYouInformationView.setMessageText(R.string.message_completed_payment_default);
        }
    }

    @Override
    public void setImageUrl(String imageUrl)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        Util.requestImageResize(getContext(), getViewDataBinding().simpleDraweeView, imageUrl);
    }

    @Override
    public void setBooking(SpannableString checkInDate, SpannableString checkOutDate, int nights, String stayName, String roomType)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().thankYouInformationView.setDate1Text(getString(R.string.act_booking_chkin), checkInDate);
        getViewDataBinding().thankYouInformationView.setDate2Text(getString(R.string.act_booking_chkout), checkOutDate);

        getViewDataBinding().thankYouInformationView.setCenterNightsVisible(true);
        getViewDataBinding().thankYouInformationView.setCenterNightsText(getString(R.string.label_nights, nights));

        getViewDataBinding().thankYouInformationView.removeAllReservationInformation();
        getViewDataBinding().thankYouInformationView.addReservationInformation(getString(R.string.label_booking_place_name), stayName);
        getViewDataBinding().thankYouInformationView.addReservationInformation(getString(R.string.label_booking_room_type), roomType);
    }

    @Override
    public void startRecommendAnimation(Animator.AnimatorListener listener)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        boolean recommendGourmetEnable = getViewDataBinding().recommendGourmetLayout.hasData();
        if (recommendGourmetEnable == false)
        {
            return;
        }

        ThankYouScaleAnimator animator;

        animator = new ThankYouScaleAnimator(getContext() //
            , recommendGourmetEnable == true ? getViewDataBinding().recommendGourmetLayout : null);

        animator.setListener(listener);
        animator.start();
    }


    @Override
    public Observable<Boolean> getReceiptAnimation()
    {
        Observable<Boolean> observable = new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                startReceiptAnimation(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        observer.onNext(true);
                        observer.onComplete();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation)
                    {

                    }
                });
            }
        };

        observable.subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
        return observable;
    }

    @Override
    public ArrayList<CarouselListItem> getRecommendGourmetData()
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        return getViewDataBinding().recommendGourmetLayout.getData();
    }

    @Override
    public void setDepositStickerCardVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().rewardCardView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setDepositStickerCard(String titleText, int nights, String warningText, String descriptionText, boolean warningTextColor)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().rewardCardView.setGuideVisible(false);
        getViewDataBinding().rewardCardView.setOptionVisible(false);
        getViewDataBinding().rewardCardView.setRewardTitleText(titleText);
        getViewDataBinding().rewardCardView.setStickerCount(nights);
        getViewDataBinding().rewardCardView.setWarningTextColor(warningTextColor);

        if (DailyTextUtils.isTextEmpty(warningText) == true)
        {
            getViewDataBinding().rewardCardView.setWarningVisible(false);
        } else
        {
            getViewDataBinding().rewardCardView.setWarningVisible(true);
            getViewDataBinding().rewardCardView.setWarningText(warningText);
        }

        if (DailyTextUtils.isTextEmpty(descriptionText) == true)
        {
            getViewDataBinding().rewardCardView.setDescriptionVisible(false);
        } else
        {
            getViewDataBinding().rewardCardView.setDescriptionVisible(true);
            getViewDataBinding().rewardCardView.setDescriptionText(descriptionText);
        }
    }

    @Override
    public void startRecommendGourmetViewAnimation()
    {
        if (getViewDataBinding() == null || mRecommendGourmetAnimator != null//
            || getViewDataBinding().recommendGourmetButtonView.getVisibility() != View.VISIBLE)
        {
            return;
        }

        float transY = ScreenUtils.dpToPx(getContext(), 6d);

        mRecommendGourmetAnimator = ObjectAnimator.ofFloat(getViewDataBinding().recommendGourmetButtonView, "translationY", 0.0f, transY, 0.0f);
        mRecommendGourmetAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mRecommendGourmetAnimator.setDuration(1600);
        mRecommendGourmetAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        mRecommendGourmetAnimator.start();
    }

    @Override
    public void stopRecommendGourmetViewAnimation()
    {
        if (getViewDataBinding() == null || mRecommendGourmetAnimator == null)
        {
            return;
        }

        mRecommendGourmetAnimator.cancel();
        mRecommendGourmetAnimator = null;
    }

    @Override
    public void setRecommendGourmetViewVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().recommendGourmetButtonView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setNoticeVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().thankYouInformationView.setNoticeVisible(visible);
    }

    @Override
    public void setNoticeText(String notice)
    {
        getViewDataBinding().thankYouInformationView.setNoticeText(notice);
    }

    @Override
    public void setRecommendGourmetData(ArrayList<CarouselListItem> carouselListItemList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().recommendGourmetLayout.setData(carouselListItemList, false);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.confirmView:
                getEventListener().onConfirmClick();
                break;
        }
    }

    private void initToolbar(ActivityStayPaymentThankYouDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setBackVisible(false);
        viewDataBinding.toolbarView.clearMenuItem();
        viewDataBinding.toolbarView.addMenuItem(DailyToolbarView.MenuItem.CLOSE, null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onBackClick();
            }
        });
    }

    void startReceiptAnimation(Animator.AnimatorListener listener)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        ThankYouScreenAnimator animator;
        animator = new ThankYouScreenAnimator(getContext(), getViewDataBinding().checkImageView, getViewDataBinding().thankYouInformationView);
        animator.setListener(listener);
        animator.start();
    }
}
