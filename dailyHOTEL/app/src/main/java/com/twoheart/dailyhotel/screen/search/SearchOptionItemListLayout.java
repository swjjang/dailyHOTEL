package com.twoheart.dailyhotel.screen.search;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.screen.booking.detail.map.PlaceBookingDetailMapView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutSearchOptionItemListBinding;
import com.twoheart.dailyhotel.databinding.ListRowSearchOptionItemBinding;
import com.twoheart.dailyhotel.model.SearchOptionItem;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.ArrayList;

/**
 * Created by iseung-won on 2017. 8. 10..
 */

public class SearchOptionItemListLayout extends ConstraintLayout
{
    private Context mContext;
    private LayoutSearchOptionItemListBinding mViewDataBinding;
    private OnEventListener mOnEventListener;
    private SearchOptionItemListAdapter mRecyclerAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onDeleteAllClick();

        void onItemClick(View view);
    }

    public SearchOptionItemListLayout(Context context)
    {
        super(context);

        mContext = context;
        initLayout();
    }

    public SearchOptionItemListLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        mContext = context;
        initLayout();
    }

    public SearchOptionItemListLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        mContext = context;
        initLayout();
    }

    private void initLayout()
    {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        mViewDataBinding = DataBindingUtil.inflate(layoutInflater, R.layout.layout_search_option_item_list, this, true);

        EdgeEffectColor.setEdgeGlowColor(mViewDataBinding.recyclerView, mContext.getResources().getColor(R.color.default_over_scroll_edge));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);

        mViewDataBinding.recyclerView.setLayoutManager(linearLayoutManager);

        mViewDataBinding.deleteLayout.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnEventListener == null)
                {
                    return;
                }

                mOnEventListener.onDeleteAllClick();
            }
        });

        mRecyclerAdapter = new SearchOptionItemListAdapter(mContext, null, new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnEventListener == null)
                {
                    return;
                }

                mOnEventListener.onItemClick(v);
            }
        });

        mViewDataBinding.recyclerView.setAdapter(mRecyclerAdapter);
    }

    public void setOnEventListener(OnEventListener listener)
    {
        mOnEventListener = listener;
    }

    public void setDeleteButtonVisible(boolean isVisible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.deleteLayout.setVisibility(isVisible == true ? View.VISIBLE : View.GONE);
        mViewDataBinding.deleteLayout.setClickable(isVisible);
    }

    public void setTitleText(String text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.titleTextView.setText(text);
    }

    public void setTitleText(int textResId)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.titleTextView.setText(textResId);
    }

    public void setData(ArrayList<SearchOptionItem> list)
    {
        if (mRecyclerAdapter == null)
        {
            return;
        }

        mRecyclerAdapter.setData(list);
        mRecyclerAdapter.notifyDataSetChanged();
    }
}
