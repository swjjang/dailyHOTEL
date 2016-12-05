package com.twoheart.dailyhotel.screen.common;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.widget.DailyEditText;

/**
 * Created by android_sam on 2016. 12. 5..
 */

public class WriteReviewCommentLayout extends BaseLayout
{
    private View mConfirmView;
    private TextView mToolbarTitleView;
    private View mBodyTitleLayout;
    private DailyEditText mEditTextView;
    private TextView mBottomTextCountView;
    private View mBottomLayout;


    public interface OnEventListener
    {
        void onConfirmClick(String text);

    }

    public WriteReviewCommentLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        mToolbarTitleView = (TextView) view.findViewById(R.id.titleTextView);
        mConfirmView = view.findViewById(R.id.confirmTextView);
        mBodyTitleLayout = view.findViewById(R.id.bodyTitleLayout);
        mBottomLayout = view.findViewById(R.id.textCountLayout);
        mBottomTextCountView = (TextView) view.findViewById(R.id.textCountView);
        mEditTextView = (DailyEditText) view.findViewById(R.id.writeReviewEditText);
    }

}
