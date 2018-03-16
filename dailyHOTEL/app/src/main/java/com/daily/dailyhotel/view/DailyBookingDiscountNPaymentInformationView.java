package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewBookingDiscountInformationDataBinding;

public class DailyBookingDiscountNPaymentInformationView extends ConstraintLayout
{
    private DailyViewBookingDiscountInformationDataBinding mViewDataBinding;

    public DailyBookingDiscountNPaymentInformationView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyBookingDiscountNPaymentInformationView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyBookingDiscountNPaymentInformationView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_booking_discount_information_data, this, true);
    }

    public void setDiscountTypeVisible(boolean bonusVisible, boolean couponVisible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (bonusVisible == false && couponVisible == false)
        {
            setDiscountInformationVisible(false);
        } else if (bonusVisible == true && couponVisible == true)
        {
            setDiscountInformationVisible(true);

            mViewDataBinding.bonusGuideTextView.setVisibility(VISIBLE);

            mViewDataBinding.bonusLayout.setVisibility(VISIBLE);
            mViewDataBinding.couponLayout.setVisibility(VISIBLE);
        } else
        {
            setDiscountInformationVisible(true);

            mViewDataBinding.bonusGuideTextView.setVisibility(GONE);

            mViewDataBinding.bonusLayout.setVisibility(bonusVisible ? VISIBLE : GONE);
            mViewDataBinding.couponLayout.setVisibility(couponVisible ? VISIBLE : GONE);
        }
    }

    public void setBonusGuideText(CharSequence text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.bonusGuideTextView.setText(text);
    }

    public void setBonusEnabled(boolean enabled)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.bonusLayout.setEnabled(enabled);
        mViewDataBinding.bonusTabLayout.setEnabled(enabled);
    }

    public boolean isBonusEnabled()
    {
        return mViewDataBinding != null && mViewDataBinding.bonusLayout.isEnabled();
    }

    public void setBonusSelected(boolean selected)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.bonusLayout.setSelected(selected);
    }

    public boolean isBonusSelected()
    {
        return mViewDataBinding != null && mViewDataBinding.bonusLayout.isSelected();
    }

    public void setOnBonusClickListener(View.OnClickListener listener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.bonusLayout.setOnClickListener(listener);
    }

    public void setOnBonusTabClickListener(View.OnClickListener listener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.bonusTabLayout.setOnClickListener(listener);
    }

    public void setOnCouponClickListener(View.OnClickListener listener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.couponLayout.setOnClickListener(listener);
    }

    public void setOnCouponTabClickListener(View.OnClickListener listener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.couponTabLayout.setOnClickListener(listener);
    }

    public void setOnDepositStickerClickListener(View.OnClickListener listener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.depositStickerLayout.setOnClickListener(listener);
    }

    public void setTotalBonus(int bonus)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        String priceFormat = DailyTextUtils.getPriceFormat(getContext(), bonus, false);
        String text = getContext().getString(R.string.label_booking_own_bonus, priceFormat);

        mViewDataBinding.bonusTextView.setText(text);
    }

    public void setBonus(int bonus)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (bonus == 0)
        {
            mViewDataBinding.bonusTabTextView.setText(R.string.label_booking_used_bonus);
        } else
        {
            mViewDataBinding.bonusTabTextView.setText(DailyTextUtils.getPriceFormat(getContext(), bonus, false));
        }
    }

    public void setCouponEnabled(boolean enabled)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.couponLayout.setEnabled(enabled);
    }

    public boolean isCouponEnabled()
    {
        return mViewDataBinding != null && mViewDataBinding.couponLayout.isEnabled();
    }

    public void setCouponSelected(boolean selected)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.couponLayout.setSelected(selected);
    }

    public boolean isCouponSelected()
    {
        return mViewDataBinding != null && mViewDataBinding.couponLayout.isSelected();
    }

    public void setMaxCouponAmount(int maxCouponAmount)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        String text;
        if (maxCouponAmount > 0)
        {
            String priceFormat = DailyTextUtils.getPriceFormat(getContext(), maxCouponAmount, false);
            text = getContext().getString(R.string.label_booking_max_coupon_amount, priceFormat);
        } else
        {
            text = getContext().getString(R.string.label_booking_max_coupon_amount_empty);
        }

        mViewDataBinding.couponTextView.setText(text);
    }

    public void setMaxCouponAmountVisible(boolean isVisible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.couponTextView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void setCoupon(int couponPrice)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (couponPrice == 0)
        {
            mViewDataBinding.couponTabTextView.setText(R.string.label_booking_select_coupon);
        } else
        {
            mViewDataBinding.couponTabTextView.setText(DailyTextUtils.getPriceFormat(getContext(), couponPrice, false));
        }
    }

    public void setRewardEnabled(boolean enabled)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (enabled == true)
        {
            mViewDataBinding.discountInformationTextView.setText(R.string.label_booking_discount_reward_information);
        } else
        {
            mViewDataBinding.discountInformationTextView.setText(R.string.label_booking_discount_information);
        }

    }

    public boolean isDepositStickerEnabled()
    {
        return mViewDataBinding != null && mViewDataBinding.depositStickerLayout.isEnabled();
    }

    public void setRewardStickerEnabled(boolean enabled)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.depositStickerLayout.setEnabled(enabled);
    }

    public boolean isDepositStickerSelected()
    {
        return mViewDataBinding != null && mViewDataBinding.depositStickerLayout.isSelected();

    }

    public void setDepositStickerSelected(boolean selected)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.depositStickerLayout.setSelected(selected);
    }

    public void setDepositStickerVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.depositStickerLayout.setVisibility(visible ? View.VISIBLE : GONE);
    }

    public void setDepositStickerDescriptionText(String text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.depositStickerWarningTextView.setText(text);
    }

    public void setUsedRewardCouponVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.usedRewardCouponTextView.setVisibility(visible ? View.VISIBLE : GONE);
    }

    public void setUsedRewardCouponText(String text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.usedRewardCouponTextView.setText(text);
    }

    /**
     * @param description
     * @param price
     */
    public void setReservationPrice(String description, int price)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(description) == false)
        {
            mViewDataBinding.nightsTextView.setVisibility(VISIBLE);
            mViewDataBinding.nightsTextView.setText(description);
        } else
        {
            mViewDataBinding.nightsTextView.setVisibility(GONE);
        }

        mViewDataBinding.reservationPriceTextView.setText(DailyTextUtils.getPriceFormat(getContext(), price, false));
    }

    public void setDiscountPrice(int price)
    {
        if (mViewDataBinding == null || price < 0)
        {
            return;
        }

        if (price == 0)
        {
            mViewDataBinding.discountPriceTextView.setText(DailyTextUtils.getPriceFormat(getContext(), price, false));
        } else
        {
            mViewDataBinding.discountPriceTextView.setText("- " + DailyTextUtils.getPriceFormat(getContext(), price, false));
        }
    }

    public void setTotalPaymentPrice(int price)
    {
        if (mViewDataBinding == null || price < 0)
        {
            return;
        }

        mViewDataBinding.totalPaymentPriceTextView.setText(DailyTextUtils.getPriceFormat(getContext(), price, false));
    }

    private void setDiscountInformationVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (visible == true)
        {
            mViewDataBinding.discountInformationLayout.setVisibility(VISIBLE);
            mViewDataBinding.paymentInformationTitleTextView.setVisibility(VISIBLE);
            mViewDataBinding.discountPriceLayout.setVisibility(VISIBLE);
        } else
        {
            mViewDataBinding.discountInformationLayout.setVisibility(GONE);
            mViewDataBinding.paymentInformationTitleTextView.setVisibility(GONE);
            mViewDataBinding.discountPriceLayout.setVisibility(GONE);
        }
    }
}
