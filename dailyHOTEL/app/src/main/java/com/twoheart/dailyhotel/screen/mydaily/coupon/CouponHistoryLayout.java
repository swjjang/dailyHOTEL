package com.twoheart.dailyhotel.screen.mydaily.coupon;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.CouponHistory;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sam Lee on 2016. 5. 23..
 */
public class CouponHistoryLayout extends BaseLayout
{
    private RecyclerView mRecyclerView;
    private View mTopLine;
    private View mEmptyView;
    private CouponHistoryListAdapter mListAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onHomeClick();
    }

    public CouponHistoryLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbar(view);
        initListView(view);
    }

    private void initToolbar(View view)
    {
        DailyToolbarView dailyToolbarView = (DailyToolbarView) view.findViewById(R.id.toolbarView);
        dailyToolbarView.setTitleText(R.string.actionbar_title_coupon_history);
        dailyToolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.finish();
            }
        });
    }

    private void initListView(View view)
    {
        mTopLine = view.findViewById(R.id.listTopLine);
        mTopLine.setVisibility(View.GONE);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.couponHistoryRecyclerView);

        mEmptyView = view.findViewById(R.id.emptyView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(layoutManager);

        EdgeEffectColor.setEdgeGlowColor(mRecyclerView, mContext.getResources().getColor(R.color.default_over_scroll_edge));

        View homeButtonView = view.findViewById(R.id.homeButtonView);
        homeButtonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onHomeClick();
            }
        });
    }

    public void setData(List<CouponHistory> list)
    {
        if (list != null && list.size() != 0)
        {
            mTopLine.setVisibility(View.VISIBLE);
            mListAdapter = new CouponHistoryListAdapter(mContext, list);
            mEmptyView.setVisibility(View.GONE);
        } else
        {
            mListAdapter = new CouponHistoryListAdapter(mContext, new ArrayList<CouponHistory>());
            mEmptyView.setVisibility(View.VISIBLE);

        }

        mRecyclerView.setAdapter(mListAdapter);
    }
}
