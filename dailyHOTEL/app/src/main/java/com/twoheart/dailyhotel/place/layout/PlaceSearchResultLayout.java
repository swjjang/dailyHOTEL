package com.twoheart.dailyhotel.place.layout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;

public abstract class PlaceSearchResultLayout extends BaseLayout
{
    private View mToolbar;
    protected RecyclerView mRecyclerView;
    protected TextView mResultTextView;
    private View mEmptyLayout;
    private View mResultListLayout;

    protected abstract PlaceListAdapter getListAdapter();

    protected abstract void setSearchResultList(ArrayList<PlaceViewItem> placeViewItemList);

    public PlaceSearchResultLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    public interface OnEventListener extends OnBaseEventListener
    {
        void finish(int resultCode);

        void onItemClick(PlaceViewItem placeViewItem);

        void onShowCallDialog();
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbarLayout(view);

        mEmptyLayout = view.findViewById(R.id.emptyLayout);
        mResultListLayout = view.findViewById(R.id.resultListLayout);

        initEmptyLayout(mEmptyLayout);
        initListLayout(mResultListLayout);

        mRecyclerView.setAdapter(getListAdapter());
    }

    private void initToolbarLayout(View view)
    {
        mToolbar = view.findViewById(R.id.toolbar);

        View backView = mToolbar.findViewById(R.id.backImageView);
        backView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((PlaceSearchResultLayout.OnEventListener) mOnEventListener).finish(Activity.RESULT_CANCELED);
            }
        });

        View searchCancelView = mToolbar.findViewById(R.id.searchCancelView);
        searchCancelView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((PlaceSearchResultLayout.OnEventListener) mOnEventListener).finish(Constants.CODE_RESULT_ACTVITY_HOME);
            }
        });
    }

    public void setToolbarText(String title, String date)
    {
        TextView titleView = (TextView) mToolbar.findViewById(R.id.titleView);
        TextView dateView = (TextView) mToolbar.findViewById(R.id.dateView);

        titleView.setText(title);
        dateView.setText(date);
    }

    private void initEmptyLayout(View view)
    {
        View researchView = view.findViewById(R.id.researchView);
        TextView callTextView = (TextView) view.findViewById(R.id.callTextView);

        researchView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((PlaceSearchResultLayout.OnEventListener) mOnEventListener).finish(Activity.RESULT_CANCELED);
            }
        });

        callTextView.setPaintFlags(callTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        callTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((PlaceSearchResultLayout.OnEventListener) mOnEventListener).onShowCallDialog();
            }
        });
    }

    private void initListLayout(View view)
    {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        mResultTextView = (TextView) view.findViewById(R.id.resultCountView);
    }

    protected void updateResultCount(int count)
    {
        if (mResultTextView == null)
        {
            return;
        }

        mResultTextView.setText(mContext.getString(R.string.label_searchresult_resultcount, count));
    }

    protected void showEmptyLayout()
    {
        mEmptyLayout.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    protected void showListLayout()
    {
        mEmptyLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}
