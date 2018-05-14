package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.CampaignTag;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewSearchResultEmptyDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.List;

public class DailySearchResultEmptyView extends ScrollView
{
    OnEventListener mOnEventListener;
    private DailyViewSearchResultEmptyDataBinding mViewDataBinding;

    public interface OnEventListener
    {
        void onCampaignTagClick(CampaignTag campaignTag);

        void onBottomLeftButtonClick();

        void onBottomRightButtonClick();
    }

    public DailySearchResultEmptyView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailySearchResultEmptyView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailySearchResultEmptyView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DailySearchResultEmptyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        setBackgroundResource(R.color.default_background);

        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_search_result_empty_data, this, true);

        EdgeEffectColor.setEdgeGlowColor(this, getContext().getResources().getColor(R.color.default_over_scroll_edge));

        mViewDataBinding.tagFlexboxLayout.setFlexDirection(FlexDirection.ROW);
        mViewDataBinding.tagFlexboxLayout.setFlexWrap(FlexWrap.WRAP);
    }

    public void setOnEventListener(DailySearchResultEmptyView.OnEventListener listener)
    {
        mOnEventListener = listener;
    }

    public void setImage(int resId)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.imageView.setVectorImageResource(resId);
    }

    public void setMessage(int resId)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.messageTextView02.setText(resId);
    }

    public void setCampaignTagVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.popularSearchTagLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setCampaignTag(String title, List<CampaignTag> campaignTagList)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.popularSearchTagTextView.setText(title);

        if (mViewDataBinding.tagFlexboxLayout.getChildCount() > 0)
        {
            mViewDataBinding.tagFlexboxLayout.removeAllViews();
        }

        for (CampaignTag campaignTag : campaignTagList)
        {
            View view = getCampaignTagView(getContext(), campaignTag);

            if (view != null)
            {
                mViewDataBinding.tagFlexboxLayout.addView(view);
            }
        }
    }

    private View getCampaignTagView(Context context, CampaignTag campaignTag)
    {
        if (context == null || campaignTag == null)
        {
            return null;
        }

        final int DP_12 = ScreenUtils.dpToPx(context, 12);
        final int DP_5 = ScreenUtils.dpToPx(context, 5);

        DailyTextView dailyTextView = new DailyTextView(context);
        dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        dailyTextView.setTextColor(context.getResources().getColor(R.color.default_text_c666666));
        dailyTextView.setPadding(DP_12, 0, DP_12, 0);
        dailyTextView.setBackgroundResource(R.drawable.shape_fillrect_le7e7e7_bffffff_r50);
        dailyTextView.setGravity(Gravity.CENTER_VERTICAL);
        dailyTextView.setMaxLines(1);
        dailyTextView.setSingleLine();

        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ScreenUtils.dpToPx(context, 29));
        layoutParams.setMargins(DP_5, DP_5, DP_5, DP_5);

        dailyTextView.setLayoutParams(layoutParams);
        dailyTextView.setText("#" + campaignTag.campaignTag);
        dailyTextView.setTag(campaignTag);
        dailyTextView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnEventListener != null)
                {
                    mOnEventListener.onCampaignTagClick((CampaignTag) v.getTag());
                }
            }
        });

        return dailyTextView;
    }

    public void setBottomLeftButton(int iconResId, int titleResId)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.searchLeftTextView.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);
        mViewDataBinding.searchLeftTextView.setText(titleResId);

        mViewDataBinding.searchLeftLayout.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnEventListener != null)
                {
                    mOnEventListener.onBottomLeftButtonClick();
                }
            }
        });
    }

    public void setBottomRightButton(int iconResId, int titleResId)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.searchRightTextView.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);
        mViewDataBinding.searchRightTextView.setText(titleResId);

        mViewDataBinding.searchRightLayout.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnEventListener != null)
                {
                    mOnEventListener.onBottomRightButtonClick();
                }
            }
        });
    }
}
