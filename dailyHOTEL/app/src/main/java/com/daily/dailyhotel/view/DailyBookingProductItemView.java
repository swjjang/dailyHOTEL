package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewBookingProductItemDataBinding;

/**
 * Created by android_sam on 2017. 11. 20..
 */

public class DailyBookingProductItemView extends ConstraintLayout
{
    private DailyViewBookingProductItemDataBinding mViewDataBinding;

    public DailyBookingProductItemView(Context context)
    {
        super(context);
        initLayout(context);
    }

    public DailyBookingProductItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initLayout(context);
    }

    public DailyBookingProductItemView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_booking_product_item_data, this, true);
    }

    public void setInformation(boolean isTopLineVisible, String title, int count, int persons, int price)
    {
        if (mViewDataBinding == null || getContext() == null)
        {
            return;
        }

        mViewDataBinding.topLineView.setVisibility(isTopLineVisible == true ? View.VISIBLE : View.GONE);
        mViewDataBinding.titleTextView.setText(title);
        mViewDataBinding.countTextView.setText(getContext().getString(R.string.label_booking_count, count));

        if (persons > 0)
        {
            mViewDataBinding.personsTextView.setText(getContext().getString(R.string.label_persons, persons));
            mViewDataBinding.personsTextView.setVisibility(View.VISIBLE);
        } else {
            mViewDataBinding.personsTextView.setText("");
            mViewDataBinding.personsTextView.setVisibility(View.GONE);
        }

        String priceString = DailyTextUtils.getPriceFormat(getContext(), price, false);
        mViewDataBinding.priceTextView.setText(priceString);
    }
}
