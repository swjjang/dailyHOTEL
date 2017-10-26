package com.daily.dailyhotel.screen.booking.cancel;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.BookingCancel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityBookingCancelListDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.ArrayList;
import java.util.List;

public class BookingCancelListView extends BaseDialogView<BookingCancelListView.OnEventListener, ActivityBookingCancelListDataBinding> implements BookingCancelListInterface, View.OnClickListener
{
    private BookingCancelListAdapter mAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onRefreshAll(boolean isShowProgress);

        void onAgainBookingClick(BookingCancel bookingCancel);

        void onBookingClick(BookingCancel bookingCancel);

        void onLoginClick();

        void onViewStayClick();

        void onViewGourmetClick();
    }

    public interface OnUserActionListener
    {
        void onAgainBookingClick(BookingCancel bookingCancel);

        void onBookingClick(BookingCancel bookingCancel);
    }

    public BookingCancelListView(BaseActivity baseActivity, BookingCancelListView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityBookingCancelListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(R.string.actionbar_title_booking_cancel_list_activity);
        getViewDataBinding().toolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onBackClick();
            }
        });

        viewDataBinding.bookingCancelSwipeRefreshLayout.setColorSchemeResources(R.color.dh_theme_color);
        viewDataBinding.bookingCancelSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                getEventListener().onRefreshAll(false);
            }
        });

        getViewDataBinding().bookingCancelRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        EdgeEffectColor.setEdgeGlowColor(getViewDataBinding().bookingCancelRecyclerView, getContext().getResources().getColor(R.color.default_over_scroll_edge));

        getViewDataBinding().loginTextView.setOnClickListener(this);
        getViewDataBinding().viewStayLayout.setOnClickListener(this);
        getViewDataBinding().viewGourmetLayout.setOnClickListener(this);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(title);
    }

    @Override
    public void setRefreshing(boolean refreshing)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().bookingCancelSwipeRefreshLayout.setRefreshing(refreshing);
    }

    @Override
    public void logoutLayout()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().logoutLayout.setVisibility(View.VISIBLE);
        getViewDataBinding().bookingCancelRecyclerView.setVisibility(View.GONE);
        getViewDataBinding().emptyListLayout.setVisibility(View.GONE);
    }

    @Override
    public void setBookingCancelList(List<BookingCancel> bookingCancelList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        // empty
        if (bookingCancelList == null || bookingCancelList.size() == 0)
        {
            if (mAdapter != null)
            {
                mAdapter.clear();
            }

            //취소한 호텔이 없는 경우
            getViewDataBinding().bookingCancelSwipeRefreshLayout.setVisibility(View.GONE);
            getViewDataBinding().emptyListLayout.setVisibility(View.VISIBLE);
            getViewDataBinding().loginTextView.setVisibility(View.GONE);
            getViewDataBinding().logoutLayout.setVisibility(View.GONE);
            return;
        }

        // hasData
        if (mAdapter == null)
        {
            mAdapter = new BookingCancelListAdapter(getContext(), new ArrayList<>());
            mAdapter.setOnUserActionListener(mOnUserActionListener);
            getViewDataBinding().bookingCancelRecyclerView.setAdapter(mAdapter);
        } else
        {
            mAdapter.clear();
        }

        mAdapter.addAll(bookingCancelList);
        mAdapter.notifyDataSetChanged();

        getViewDataBinding().bookingCancelSwipeRefreshLayout.setVisibility(View.VISIBLE);
        getViewDataBinding().emptyListLayout.setVisibility(View.GONE);
        getViewDataBinding().loginTextView.setVisibility(View.GONE);
        getViewDataBinding().logoutLayout.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v)
    {
        if (getEventListener() == null)
        {
            return;
        }

        switch (v.getId())
        {
            case R.id.loginTextView:
            {
                getEventListener().onLoginClick();
                break;
            }

            case R.id.viewStayLayout:
            {
                getEventListener().onViewStayClick();
                break;
            }

            case R.id.viewGourmetLayout:
            {
                getEventListener().onViewGourmetClick();
                break;
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UserActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private OnUserActionListener mOnUserActionListener = new OnUserActionListener()
    {
        @Override
        public void onAgainBookingClick(BookingCancel bookingCancel)
        {
           getEventListener().onAgainBookingClick(bookingCancel);
        }

        @Override
        public void onBookingClick(BookingCancel bookingCancel)
        {
            getEventListener().onBookingClick(bookingCancel);
        }
    };
}
