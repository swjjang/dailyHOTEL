package com.daily.dailyhotel.screen.home.stay.outbound.thankyou;

import android.animation.Animator;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.FontManager;
import com.daily.dailyhotel.animation.ThankYouScreenAnimator;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundPaymentThankYouDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.schedulers.Schedulers;

public class StayOutboundThankYouView extends BaseDialogView<StayOutboundThankYouView.OnEventListener, ActivityStayOutboundPaymentThankYouDataBinding> implements StayOutboundThankYouInterface, View.OnClickListener
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onConfirmClick();
    }

    public StayOutboundThankYouView(BaseActivity baseActivity, StayOutboundThankYouView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayOutboundPaymentThankYouDataBinding viewDataBinding)
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

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.scrollLayout, getColor(R.color.transparent));
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
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.confirmView:
                getEventListener().onConfirmClick();
                break;
        }
    }

    private void initToolbar(ActivityStayOutboundPaymentThankYouDataBinding viewDataBinding)
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

        ThankYouScreenAnimator animator = new ThankYouScreenAnimator(getContext()//
            , getViewDataBinding().checkImageView, getViewDataBinding().thankYouInformationView);

        animator.setListener(listener);
        animator.start();
    }
}
