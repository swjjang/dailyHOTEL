package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewStayMapCardDataBinding;
import com.twoheart.dailyhotel.util.Util;

public class DailyStayMapCardView extends ConstraintLayout
{
    private DailyViewStayMapCardDataBinding mViewDataBinding;

    public DailyStayMapCardView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyStayMapCardView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyStayMapCardView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_stay_map_card_data, this, true);
        mViewDataBinding.simpleDraweeView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);
    }

    /**
     * @param benefit null이거나 비어있으면 항목 삭제.
     */
    public void setBenefitText(String benefit)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(benefit) == true)
        {
            mViewDataBinding.benefitTextView.setVisibility(GONE);
        } else
        {
            mViewDataBinding.benefitTextView.setVisibility(VISIBLE);
            mViewDataBinding.benefitTextView.setText(benefit);
        }
    }

    public void setImage(String url)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.simpleDraweeView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

        Util.requestImageResize(getContext(), mViewDataBinding.simpleDraweeView, url);
    }

    public void setStickerVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.stickerImageView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setGradeText(String grade)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.gradeTextView.setVisibility(DailyTextUtils.isTextEmpty(grade) ? GONE : VISIBLE);
        mViewDataBinding.gradeTextView.setText(grade);
    }

    public void setReviewText(int satisfaction)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        String reviewText;

        if (satisfaction > 0)
        {
            mViewDataBinding.satisfactionView.setVisibility(View.VISIBLE);
            mViewDataBinding.satisfactionView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_list_ic_satisfaction, 0, 0, 0);
            mViewDataBinding.satisfactionView.setText(getResources().getString(R.string.label_list_satisfaction, satisfaction));
        } else
        {
            mViewDataBinding.satisfactionView.setVisibility(View.GONE);
        }
    }

    public void setStayNameText(String stayName)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.nameTextView.setText(stayName);
    }

    public void setAddressText(String address)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.addressTextView.setText(address);
    }

    public void setPriceText(int discountPercent, int discountPrice, int price, String couponPrice, boolean nightsEnabled)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (discountPrice > 0)
        {
            mViewDataBinding.discountPriceTextView.setText(DailyTextUtils.getPriceFormat(getContext(), discountPrice, false));
        } else
        {
            mViewDataBinding.discountPriceTextView.setText(R.string.label_soldout);
        }

        if (price <= 0 || price <= discountPrice || discountPercent <= 0)
        {
            mViewDataBinding.priceTextView.setVisibility(GONE);
        } else
        {
            mViewDataBinding.priceTextView.setPaintFlags(mViewDataBinding.priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            mViewDataBinding.priceTextView.setVisibility(VISIBLE);
            mViewDataBinding.priceTextView.setText(DailyTextUtils.getPriceFormat(getContext(), price, false));
        }

        mViewDataBinding.averageTextView.setVisibility(nightsEnabled ? VISIBLE : GONE);
    }

    public void setWishVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.wishImageView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setWish(boolean wish)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.wishImageView.setVectorImageResource(wish ? R.drawable.vector_list_ic_heart_on : R.drawable.vector_list_ic_heart_off);
    }

    public void setOnWishClickListener(View.OnClickListener onClickListener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.wishImageView.setOnClickListener(onClickListener);
    }

    public Pair[] getOptionsCompat()
    {
        if (mViewDataBinding == null)
        {
            return null;
        }

        Pair[] pairs = new Pair[3];
        pairs[0] = Pair.create(mViewDataBinding.simpleDraweeView, getContext().getString(R.string.transition_place_image));
        pairs[1] = Pair.create(mViewDataBinding.gradientTopView, getContext().getString(R.string.transition_gradient_top_view));
        pairs[2] = Pair.create(mViewDataBinding.gradientBottomView, getContext().getString(R.string.transition_gradient_bottom_view));

        return pairs;
    }
}
