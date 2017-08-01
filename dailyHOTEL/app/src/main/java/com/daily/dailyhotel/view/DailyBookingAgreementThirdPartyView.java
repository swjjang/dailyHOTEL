package com.daily.dailyhotel.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewBookingAgreementThirdPartyDataBinding;

public class DailyBookingAgreementThirdPartyView extends LinearLayout
{
    private DailyViewBookingAgreementThirdPartyDataBinding mViewDataBinding;

    private ValueAnimator mValueAnimator;
    private int mInformationHeight;
    private OnAgreementClickListener mOnAgreementClickListener;

    public interface OnAgreementClickListener
    {
        void onAgreementClick(boolean isChecked);
    }

    public DailyBookingAgreementThirdPartyView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyBookingAgreementThirdPartyView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyBookingAgreementThirdPartyView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        setOrientation(VERTICAL);

        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_booking_agreement_third_party_data, this, true);

        mViewDataBinding.arrowImageView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mViewDataBinding.thirdPartyTermsLayout.getVisibility() == VISIBLE)
                {
                    collapseInformation();
                } else
                {
                    expandInformation();
                }
            }
        });

        mViewDataBinding.thirdPartyTermsLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                mInformationHeight = mViewDataBinding.thirdPartyTermsLayout.getHeight();
                mViewDataBinding.thirdPartyTermsLayout.setVisibility(View.GONE);
            }
        });

        mViewDataBinding.agreeThirdPartyTermsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (mOnAgreementClickListener != null)
                {
                    mOnAgreementClickListener.onAgreementClick(isChecked);
                }
            }
        });

        mViewDataBinding.agreeThirdPartyTermsTextView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mViewDataBinding.agreeThirdPartyTermsCheckBox.performClick();
            }
        });
    }

    public void setOnAgreementClickListener(OnAgreementClickListener listener)
    {
        mOnAgreementClickListener = listener;
    }

    public void setVendorBusinessName(String businessName)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.vendorBusinessNameTextView.setText(businessName);
    }

    private void expandInformation()
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (mValueAnimator != null && mValueAnimator.isRunning() == true)
        {
            return;
        }

        if (mInformationHeight == 0)
        {
            return;
        }

        mValueAnimator = ValueAnimator.ofInt(0, mInformationHeight);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                if (valueAnimator == null)
                {
                    return;
                }

                int val = (int) valueAnimator.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = mViewDataBinding.thirdPartyTermsLayout.getLayoutParams();
                layoutParams.height = val;
                mViewDataBinding.thirdPartyTermsLayout.requestLayout();

                mViewDataBinding.arrowImageView.setRotation(-180.0f * val / mInformationHeight);
            }
        });

        mValueAnimator.setDuration(200);
        mValueAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mViewDataBinding.thirdPartyTermsLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                mValueAnimator.removeAllUpdateListeners();
                mValueAnimator.removeAllListeners();
                mValueAnimator = null;

                mViewDataBinding.arrowImageView.setRotation(-180);
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

        mValueAnimator.start();
    }

    private void collapseInformation()
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (mValueAnimator != null && mValueAnimator.isRunning() == true)
        {
            return;
        }

        if (mInformationHeight == 0)
        {
            return;
        }

        mValueAnimator = ValueAnimator.ofInt(mInformationHeight, 0);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                if (valueAnimator == null)
                {
                    return;
                }

                int val = (int) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = mViewDataBinding.thirdPartyTermsLayout.getLayoutParams();
                layoutParams.height = val;
                mViewDataBinding.thirdPartyTermsLayout.requestLayout();

                mViewDataBinding.arrowImageView.setRotation(-180.0f * val / mInformationHeight);
            }
        });

        mValueAnimator.setDuration(200);
        mValueAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                mValueAnimator.removeAllUpdateListeners();
                mValueAnimator.removeAllListeners();
                mValueAnimator = null;

                mViewDataBinding.arrowImageView.setRotation(0);
                mViewDataBinding.thirdPartyTermsLayout.setVisibility(View.GONE);
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

        mValueAnimator.start();
    }
}
