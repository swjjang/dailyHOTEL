package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewDetailTitleInformationDataBinding;

public class DailyDetailTitleInformationView extends ConstraintLayout
{
    private DailyViewDetailTitleInformationDataBinding mViewDataBinding;

    public DailyDetailTitleInformationView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyDetailTitleInformationView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyDetailTitleInformationView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_title_information_data, this, true);

        setBackgroundResource(R.color.white);
        setPadding(0, 0, 0, ScreenUtils.dpToPx(context, 18));
    }

    public void setNameText(String text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.nameTextView.setText(text);
    }

    public void setEnglishNameText(String text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.englishNameTextView.setText(text);
    }

    public void setEnglishNameVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.englishNameTextView.setVisibility(visible ? VISIBLE : GONE);

        if (visible == true)
        {
            final int DP_5 = ScreenUtils.dpToPx(getContext(), 5);

            ((LayoutParams) mViewDataBinding.categoryTextView.getLayoutParams()).topMargin = DP_5;
            mViewDataBinding.categoryTextView.setLayoutParams(mViewDataBinding.categoryTextView.getLayoutParams());

            ((LayoutParams) mViewDataBinding.rewardTextView.getLayoutParams()).topMargin = DP_5;
            mViewDataBinding.rewardTextView.setLayoutParams(mViewDataBinding.rewardTextView.getLayoutParams());
        }
    }

    public void setCategoryText(String text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.categoryTextView.setText(text);
    }

    public void setRewardVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        int flag = visible ? VISIBLE : GONE;

        mViewDataBinding.rewardTextView.setVisibility(flag);
        mViewDataBinding.dotImageView.setVisibility(flag);
    }

    public void setCategory(String category, String subCategory)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.categoryTextView.setText(category);

        if (DailyTextUtils.isTextEmpty(subCategory) == false)
        {
            mViewDataBinding.dotImageView.setVisibility(View.VISIBLE);
            mViewDataBinding.dotImageView.setPadding(ScreenUtils.dpToPx(getContext(), 5), 0, ScreenUtils.dpToPx(getContext(), 5), 0);
            mViewDataBinding.dotImageView.setVectorImageResource(R.drawable.vector_ic_gourmet_category_arrow);

            mViewDataBinding.subCategoryTextView.setVisibility(View.VISIBLE);
            mViewDataBinding.subCategoryTextView.setText(subCategory);
        } else
        {
            mViewDataBinding.dotImageView.setVisibility(View.GONE);
            mViewDataBinding.subCategoryTextView.setVisibility(View.GONE);
        }
    }

    public void setCouponVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.couponLayout.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setOnCouponClickListener(OnClickListener listener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.couponLayout.setOnClickListener(listener);
    }

    public void setCouponPriceText(String text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.couponTextView.setText(text);
    }
}
