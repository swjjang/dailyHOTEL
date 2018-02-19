package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewCampaignTagTitleDataBinding;

/**
 * Created by android_sam on 2017. 8. 4..
 */

public class DailyCampaignTagTitleView extends ConstraintLayout
{
    private DailyViewCampaignTagTitleDataBinding mViewDataBinding;
    private Context mContext;

    DailyCampaignTagTitleView.OnEventListener mEventListener;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onCalendarClick();
    }

    public DailyCampaignTagTitleView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyCampaignTagTitleView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyCampaignTagTitleView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mContext = context;

        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_campaign_tag_title_data, this, true);

        mViewDataBinding.backImageView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mEventListener == null)
                {
                    return;
                }

                mEventListener.onBackClick();
            }
        });

        initCalendarView();

        setResultCount(0);
    }

    private void initCalendarView()
    {
        setCalendarText(null);

        mViewDataBinding.titleBackgroundView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mEventListener == null)
                {
                    return;
                }

                mEventListener.onCalendarClick();
            }
        });
    }

    public void setOnEventListener(OnEventListener eventListener)
    {
        mEventListener = eventListener;
    }

    public void setResultCount(int count)
    {
        if (mViewDataBinding == null || mContext == null)
        {
            return;
        }

        if (count > 0)
        {
            String resultString = mContext.getString(R.string.label_searchresult_resultcount, count);
            mViewDataBinding.resultCountTextView.setText(resultString);
            mViewDataBinding.resultCountTextView.setVisibility(View.VISIBLE);
        } else
        {
            mViewDataBinding.resultCountTextView.setText(null);
            mViewDataBinding.resultCountTextView.setVisibility(View.GONE);
        }
    }

    public void setTitleText(String title)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.titleView.setText(title);
    }

    public void setTitleText(int titleResId)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.titleView.setText(titleResId);
    }

    public void setCalendarText(String text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.calendarTextView.setText(text);
    }
}
