package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ScrollView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewGourmetListFilterEmptyDataBinding;

public class DailyGourmetListFilterEmptyView extends ScrollView
{
    private DailyViewGourmetListFilterEmptyDataBinding mViewDataBinding;

    public DailyGourmetListFilterEmptyView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyGourmetListFilterEmptyView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyGourmetListFilterEmptyView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        setFillViewport(true);

        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_gourmet_list_filter_empty_data, this, true);
    }

    public void setOnButtonClickListener(OnClickListener listener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.filterTextView.setOnClickListener(listener);
    }
}
