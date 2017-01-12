package com.twoheart.dailyhotel.screen.home;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.widget.DailyLoopViewPager;
import com.twoheart.dailyhotel.widget.DailyTextView;

/**
 * Created by android_sam on 2017. 1. 11..
 */

public class HomeLayout extends BaseLayout
{
    private DailyLoopViewPager mEventViewPager;
    private DailyTextView mEventCountTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NestedScrollView mNestedScrollView;
    private LinearLayout mContentLayout;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onSearchImageClick();

        void onStayButtonClick();

        void onGourmetButtonClick();

        void onRefreshAll(boolean isShowProgress);
    }


    public HomeLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbarLayout(view);
        initRefreshLayout(view);
        initScrollLayout(view);
        initContentLayout(view);

        initEventLayout(view);
        initProductLayout(view);
    }

    private void initToolbarLayout(View view)
    {
        View searchImageView = view.findViewById(R.id.searchImageView);
        searchImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onSearchImageClick();
            }
        });
    }

    private void initRefreshLayout(View view)
    {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

        if (mSwipeRefreshLayout == null) {
            ExLog.d("mSwipeRefreshLayout is null !!! it's Test Code !!! please delete this block");
            return;
        }
        mSwipeRefreshLayout.setEnabled(false);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.dh_theme_color);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                ((HomeLayout.OnEventListener) mOnEventListener).onRefreshAll(false);
            }
        });


    }

    private void initScrollLayout(View view)
    {
        mNestedScrollView = (NestedScrollView) view.findViewById(R.id.nestedScrollView);
        mNestedScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    private void initContentLayout(View view)
    {
        mContentLayout = (LinearLayout) view.findViewById(R.id.homeContentLayout);
    }

    private void initEventLayout(View view)
    {
        if (mContentLayout == null)
        {
            return;
        }

        mEventViewPager = (DailyLoopViewPager) view.findViewById(R.id.loopViewPager);
        mEventCountTextView = (DailyTextView) view.findViewById(R.id.pagerCountTextView);

        String defaultImage = DailyPreference.getInstance(mContext).getRemoteConfigHomeEventDefaultVersion();

        //        View eventLayout = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_event_layout, null);
        //
        //        mEventViewPager = (DailyLoopViewPager) eventLayout.findViewById(R.id.loopViewPager);
        //        mEventCountTextView = (DailyTextView) eventLayout.findViewById(R.id.pagerCountTextView);


        //        mContentLayout.addView(eventLayout);
    }

    private void initProductLayout(View view)
    {
        if (mContentLayout == null)
        {
            return;
        }

        View stayButtonLayout = view.findViewById(R.id.stayButtonLayout);
        View gourmetButtonLayout = view.findViewById(R.id.gourmetButtonLayout);

        //        View productLayout = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_product_layout, null);
        //
        //        int height = Util.dpToPx(mContext, 82);
        //        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        //        productLayout.setLayoutParams(params);
        //
        //        View stayButtonLayout = productLayout.findViewById(R.id.stayButtonLayout);
        //        View gourmetButtonLayout = productLayout.findViewById(R.id.gourmetButtonLayout);

        stayButtonLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onStayButtonClick();
            }
        });

        gourmetButtonLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onGourmetButtonClick();
            }
        });

        //        mContentLayout.addView(productLayout);
    }

    public void setRefreshing(boolean isRefreshing)
    {
        if (mSwipeRefreshLayout == null)
        {
            return;
        }

        mSwipeRefreshLayout.setRefreshing(isRefreshing);
    }

    public void setEventCountView(int pageIndex, int totalCount)
    {
        if (mEventCountTextView == null)
        {
            return;
        }

        if (totalCount == 0)
        {
            mEventCountTextView.setVisibility(View.GONE);
        } else
        {
            mEventCountTextView.setVisibility(View.VISIBLE);

            String countString = mContext.getResources().getString(R.string.format_home_event_count, pageIndex, totalCount);
            int slashIndex = countString.indexOf("/");
            int textSize = countString.length();
            if (slashIndex < textSize)
            {
                textSize++;
            }

            if (slashIndex == -1)
            {
                mEventCountTextView.setText(countString);
            } else
            {
                SpannableString spannableString = new SpannableString(countString);
                spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.dh_theme_color)), //
                    0, slashIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                mEventCountTextView.setText(spannableString);
            }
        }
    }
}
