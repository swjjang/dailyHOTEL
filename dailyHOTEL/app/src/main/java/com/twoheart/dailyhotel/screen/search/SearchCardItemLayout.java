package com.twoheart.dailyhotel.screen.search;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutSearchCardItemBinding;
import com.twoheart.dailyhotel.model.SearchCardItem;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 8. 10..
 */

public class SearchCardItemLayout extends ConstraintLayout
{
    private Context mContext;
    private LayoutSearchCardItemBinding mViewDataBinding;
    OnEventListener mOnEventListener;
    private SearchCardItemListAdapter mRecyclerAdapter;
    private boolean mIsDeleteVisible;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onDeleteAllClick();

        void onItemClick(View view);
    }

    public SearchCardItemLayout(Context context)
    {
        super(context);

        mContext = context;
        initLayout();
    }

    public SearchCardItemLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        mContext = context;
        initLayout();
    }

    public SearchCardItemLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        mContext = context;
        initLayout();
    }

    private void initLayout()
    {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        mViewDataBinding = DataBindingUtil.inflate(layoutInflater, R.layout.layout_search_card_item, this, true);

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

        mRecyclerAdapter = new SearchCardItemListAdapter(mContext, null, new OnClickListener()
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

        mIsDeleteVisible = isVisible;

        mViewDataBinding.deleteLayout.setVisibility(isVisible == true ? View.VISIBLE : View.GONE);
        mViewDataBinding.deleteLayout.setClickable(isVisible);
    }

    public void setEmptyViewVisible(boolean isVisible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (isVisible == true)
        {
            mViewDataBinding.recyclerView.setVisibility(View.GONE);
            mViewDataBinding.deleteLayout.setVisibility(View.GONE);
            mViewDataBinding.emptyView.setVisibility(View.VISIBLE);
        } else
        {
            mViewDataBinding.recyclerView.setVisibility(View.VISIBLE);
            mViewDataBinding.deleteLayout.setVisibility( //
                mIsDeleteVisible == true ? View.VISIBLE : View.GONE);
            mViewDataBinding.emptyView.setVisibility(View.GONE);
        }
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

    public void setEmptyViewData(int imageResId, int titleResId)
    {
        if (mViewDataBinding == null || mContext == null)
        {
            return;
        }

        mViewDataBinding.emptyImageView.setImageResource(imageResId);

        if (titleResId != 0)
        {
            mViewDataBinding.emptyTextView.setText( //
                mContext.getString(R.string.message_empty_search_card_format, mContext.getString(titleResId)));
        }
    }

    public void setData(ArrayList<SearchCardItem> list)
    {
        if (mRecyclerAdapter == null)
        {
            return;
        }

        setEmptyViewVisible(list == null || list.size() == 0);
        mRecyclerAdapter.setData(list);
        mRecyclerAdapter.notifyDataSetChanged();
    }
}
