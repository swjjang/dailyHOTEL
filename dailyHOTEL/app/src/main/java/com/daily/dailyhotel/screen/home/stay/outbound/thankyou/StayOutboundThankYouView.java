package com.daily.dailyhotel.screen.home.stay.outbound.thankyou;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundPaymentThankYouDataBinding;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

public class StayOutboundThankYouView extends BaseDialogView<StayOutboundThankYouView.OnEventListener, ActivityStayOutboundPaymentThankYouDataBinding> implements StayOutboundThankYouInterface, View.OnClickListener
{
    public interface OnEventListener extends OnBaseEventListener
    {
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

        viewDataBinding.closeView.setOnClickListener(this);
        viewDataBinding.confirmView.setOnClickListener(this);
    }

    @Override
    public void setToolbarTitle(String title)
    {
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
            String message = getString(R.string.message_completed_payment_format, userName);
            SpannableStringBuilder userNameBuilder = new SpannableStringBuilder(message);
            userNameBuilder.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(getContext()).getMediumTypeface()),//
                0, userName.length(),//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            getViewDataBinding().messageTextView.setText(userNameBuilder);
        } else
        {
            getViewDataBinding().messageTextView.setText(R.string.message_completed_payment_default);
        }
    }

    @Override
    public void setImageUrl(String imageUrl)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        int imageHeight = ScreenUtils.getRatioHeightType4x3(ScreenUtils.getScreenWidth(getContext()));
        ViewGroup.LayoutParams layoutParams = getViewDataBinding().simpleDraweeView.getLayoutParams();
        layoutParams.height = imageHeight;
        getViewDataBinding().simpleDraweeView.setLayoutParams(layoutParams);

        Util.requestImageResize(getContext(), getViewDataBinding().simpleDraweeView, imageUrl);
    }

    @Override
    public void setBooking(SpannableString checkInDate, SpannableString checkOutDate, int nights, String stayName, String roomType)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().checkInDateTextView.setText(checkInDate);
        getViewDataBinding().checkOutDateTextView.setText(checkOutDate);
        getViewDataBinding().nightsTextView.setText(getString(R.string.label_nights, nights));
        getViewDataBinding().bookingPlaceTextView.setText(stayName);
        getViewDataBinding().productTypeTextView.setText(roomType);
    }

    @Override
    public void startAnimation()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().confirmImageView.setVisibility(View.INVISIBLE);

        float startY = 0f - ScreenUtils.getScreenHeight(getContext());
        final float endY = 0.0f;

        final float startScaleY = 2.3f;
        final float endScaleY = 1.0f;

        int animatorSetStartDelay;
        int receiptLayoutAnimatorDuration;
        int confirmImageAnimatorStartDelay;
        int confirmImageAnimatorDuration;
        int stampLayoutAnimatorStartDelay;
        int stampLayoutAnimatorDuration;

        if (VersionUtils.isOverAPI21() == true)
        {
            animatorSetStartDelay = 400;
            receiptLayoutAnimatorDuration = 300;
            confirmImageAnimatorStartDelay = receiptLayoutAnimatorDuration - 50;
            confirmImageAnimatorDuration = 200;
            stampLayoutAnimatorStartDelay = receiptLayoutAnimatorDuration - 50;
            stampLayoutAnimatorDuration = 200;
        } else
        {
            animatorSetStartDelay = 600;
            receiptLayoutAnimatorDuration = 400;
            confirmImageAnimatorStartDelay = receiptLayoutAnimatorDuration - 50;
            confirmImageAnimatorDuration = 200;
            stampLayoutAnimatorStartDelay = receiptLayoutAnimatorDuration - 50;
            stampLayoutAnimatorDuration = 200;
        }

        getViewDataBinding().receiptLayout.setTranslationY(startY);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setStartDelay(animatorSetStartDelay);

        final ObjectAnimator confirmImageAnimator = ObjectAnimator.ofPropertyValuesHolder(getViewDataBinding().confirmImageView //
            , PropertyValuesHolder.ofFloat("scaleX", startScaleY, endScaleY) //
            , PropertyValuesHolder.ofFloat("scaleY", startScaleY, endScaleY) //
            , PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f) //
        );

        confirmImageAnimator.setDuration(confirmImageAnimatorDuration);
        confirmImageAnimator.setStartDelay(confirmImageAnimatorStartDelay);
        confirmImageAnimator.setInterpolator(new OvershootInterpolator(1.6f));
        confirmImageAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                getViewDataBinding().confirmImageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                getViewDataBinding().confirmImageView.setScaleX(endScaleY);
                getViewDataBinding().confirmImageView.setScaleY(endScaleY);
                getViewDataBinding().confirmImageView.setVisibility(View.VISIBLE);
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

        ObjectAnimator receiptLayoutAnimator = ObjectAnimator.ofPropertyValuesHolder(getViewDataBinding().receiptLayout //
            , PropertyValuesHolder.ofFloat("translationY", startY, endY) //
        );

        receiptLayoutAnimator.setDuration(receiptLayoutAnimatorDuration);
        receiptLayoutAnimator.setInterpolator(new OvershootInterpolator(0.82f));
        receiptLayoutAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                getViewDataBinding().receiptLayout.setTranslationY(endY);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                getViewDataBinding().receiptLayout.setTranslationY(endY);
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        animatorSet.playTogether(receiptLayoutAnimator, confirmImageAnimator);
        animatorSet.start();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.closeView:
            case R.id.confirmView:
                getEventListener().onBackClick();
                break;
        }
    }
}
