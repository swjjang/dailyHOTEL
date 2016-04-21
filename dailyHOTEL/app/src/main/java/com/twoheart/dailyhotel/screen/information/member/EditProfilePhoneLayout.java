package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

public class EditProfilePhoneLayout extends SignupStep2Layout
{
    public EditProfilePhoneLayout(Context context, OnEventListener mOnEventListener)
    {
        super(context, mOnEventListener);
    }

    protected void initToolbar(View view)
    {
        View toolbar = view.findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(mContext, toolbar);
        dailyToolbarLayout.initToolbar(mContext.getString(R.string.actionbar_title_edit_phone), new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.finish();
            }
        });
    }
}
