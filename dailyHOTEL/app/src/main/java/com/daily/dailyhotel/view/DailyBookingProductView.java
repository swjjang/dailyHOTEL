package com.daily.dailyhotel.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;

/**
 * Created by android_sam on 2017. 11. 20..
 */

public class DailyBookingProductView extends LinearLayout
{
    private int mItemCount;


    public DailyBookingProductView(Context context)
    {
        super(context);
        initLayout(context);
    }

    public DailyBookingProductView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        initLayout(context);
    }

    public DailyBookingProductView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DailyBookingProductView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        initLayout(context);
    }

    private void initLayout(Context context)
    {
        setBackgroundResource(R.drawable.background_booking_product_view);
        setOrientation(LinearLayout.VERTICAL);

        int padding = ScreenUtils.dpToPx(context, 10d);
        setPadding(padding, 0, padding, 0);
    }

    public int getItemCount()
    {
        return mItemCount < 0 ? 0 : mItemCount;
    }

    public void addInformation(String title, int count, int persons, int price)
    {
        boolean isTopLineVisible = mItemCount != 0;

        mItemCount++;

        DailyBookingProductItemView itemView = new DailyBookingProductItemView(getContext());
        itemView.setInformation(isTopLineVisible, title, count, persons, price);

        this.addView(itemView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    public void removeAllInformation()
    {
        if (getChildCount() > 0)
        {
            removeAllViews();
        }
    }

    @Override
    public void removeAllViews()
    {
        super.removeAllViews();
        mItemCount = 0;
    }
}
