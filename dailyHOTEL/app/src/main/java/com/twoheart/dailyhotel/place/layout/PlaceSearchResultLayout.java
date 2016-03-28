package com.twoheart.dailyhotel.place.layout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.LinearLayoutManager;
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
    protected boolean mIsLoading;

    protected abstract PlaceListAdapter getListAdapter();

    protected abstract void addSearchResultList(ArrayList<PlaceViewItem> placeViewItemList);

    public PlaceSearchResultLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    public interface OnEventListener extends OnBaseEventListener
    {
        void finish(int resultCode);

        void research(int resultCode);

        void onItemClick(PlaceViewItem placeViewItem);

        void onShowCallDialog();

        void onLoadMoreList();

        void onShowProgressBar();
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbarLayout(view);

        mEmptyLayout = view.findViewById(R.id.emptyLayout);
        mResultListLayout = view.findViewById(R.id.resultListLayout);

        initEmptyLayout(mEmptyLayout);
        initListLayout(mResultListLayout);
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
                ((PlaceSearchResultLayout.OnEventListener) mOnEventListener).finish(Constants.CODE_RESULT_ACTIVITY_HOME);
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
                ((PlaceSearchResultLayout.OnEventListener) mOnEventListener).research(Activity.RESULT_CANCELED);
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

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(getListAdapter());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (dy > 0)
                {
                    if (mIsLoading == false)
                    {
                        int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

                        // 2/3위치에 오면 로딩한다.
                        if (firstVisibleItemPosition > linearLayoutManager.getItemCount() * 2 / 3)
                        {
                            mIsLoading = true;

                            ((PlaceSearchResultLayout.OnEventListener) mOnEventListener).onLoadMoreList();
                        }
                    } else
                    {
                        int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();

                        if (lastVisibleItemPosition == linearLayoutManager.getItemCount())
                        {
                            ((PlaceSearchResultLayout.OnEventListener) mOnEventListener).onShowProgressBar();
                        }
                    }
                }
            }
        });
    }

    public void updateResultCount(int count)
    {
        if (mResultTextView == null)
        {
            return;
        }

        mResultTextView.setText(mContext.getString(R.string.label_searchresult_resultcount, count));
    }

    public void showEmptyLayout()
    {
        mEmptyLayout.setVisibility(View.VISIBLE);
        mResultListLayout.setVisibility(View.GONE);
    }

    public void showListLayout()
    {
        mEmptyLayout.setVisibility(View.GONE);
        mResultListLayout.setVisibility(View.VISIBLE);
    }
}
