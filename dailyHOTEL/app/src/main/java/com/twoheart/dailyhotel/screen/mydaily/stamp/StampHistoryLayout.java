package com.twoheart.dailyhotel.screen.mydaily.stamp;

import android.content.Context;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

public class StampHistoryLayout extends BaseLayout implements View.OnClickListener
{
    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public StampHistoryLayout(Context context, OnEventListener mOnEventListener)
    {
        super(context, mOnEventListener);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbar(view);

    }

    private void initToolbar(View view)
    {
        View toolbar = view.findViewById(R.id.toolbar);

        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(mContext, toolbar);
        dailyToolbarLayout.initToolbar(mContext.getString(R.string.actionbar_title_stamp_history), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.finish();
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        }
    }
}