package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Util;

public class DailyViewPagerIndicator extends RelativeLayout
{
    private DailyTextView mDescriptionTextView;
    private DailyTextView mPageTextView;

    private int mTotalCount;

    public DailyViewPagerIndicator(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyViewPagerIndicator(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    public DailyViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mDescriptionTextView = new DailyTextView(context);
        mPageTextView = new DailyTextView(context);

        RelativeLayout.LayoutParams descLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mDescriptionTextView.setTextColor(getResources().getColor(R.color.hoteldetail_image_tag_text));
        mDescriptionTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        mDescriptionTextView.setBackgroundResource(R.color.white_a70);
        mDescriptionTextView.setPadding(Util.dpToPx(context, 5), Util.dpToPx(context, 2), Util.dpToPx(context, 5), Util.dpToPx(context, 2));

        descLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        descLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        mDescriptionTextView.setLayoutParams(descLayoutParams);
        addView(mDescriptionTextView);
        mDescriptionTextView.setVisibility(View.INVISIBLE);

        RelativeLayout.LayoutParams pageLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mPageTextView.setTextColor(context.getResources().getColor(R.color.white_a80));
        mPageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

        pageLayoutParams.rightMargin = Util.dpToPx(context, 13);
        pageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        pageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        mPageTextView.setLayoutParams(pageLayoutParams);
        mPageTextView.setShadowLayer(1, 1, 1, context.getResources().getColor(R.color.black_a40));
        mPageTextView.setPadding(Util.dpToPx(context, 5), Util.dpToPx(context, 2), Util.dpToPx(context, 5), Util.dpToPx(context, 2));
        addView(mPageTextView);
        mPageTextView.setVisibility(View.INVISIBLE);
    }

    public void setImageInformation(String description, int position)
    {
        if (Util.isTextEmpty(description) == false)
        {
            mDescriptionTextView.setVisibility(View.VISIBLE);
            mDescriptionTextView.setText(description);
        } else
        {
            mDescriptionTextView.setVisibility(View.INVISIBLE);
        }

        if (mTotalCount == 0)
        {
            mPageTextView.setVisibility(View.INVISIBLE);
        } else
        {
            mPageTextView.setVisibility(View.VISIBLE);
            mPageTextView.setText(String.format("%d/%d", position + 1, mTotalCount));
        }
    }

    public void setTotalCount(int count)
    {
        mTotalCount = count;
    }
}
