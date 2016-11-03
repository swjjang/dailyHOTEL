package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.DailyTextView;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sam Lee on 2016. 5. 20..
 */
public class CouponListLayout extends BaseLayout implements View.OnClickListener
{

    private DailyTextView mHeaderTextView;
    private RecyclerView mRecyclerView;
    private View mEmptyView;
    private CouponListAdapter mListAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void startCouponHistory();

        void startNotice();

        void startRegisterCoupon();

        void showListItemNotice(Coupon coupon);

        void onListItemDownLoadClick(Coupon coupon);

        void onSortingButtonClick(CouponListActivity.CouponSortType sortType);
    }

    public CouponListLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbar(view);
        initListView(view);

        mHeaderTextView = (DailyTextView) view.findViewById(R.id.couponTextView);

        View couponSortingButton = view.findViewById(R.id.couponSortingButton);
        if (couponSortingButton.getTag() == null)
        {
            couponSortingButton.setTag(CouponListActivity.CouponSortType.ALL);
        }
        couponSortingButton.setOnClickListener(this);

        View registerCouponView = view.findViewById(R.id.registerCouponView);
        registerCouponView.setOnClickListener(this);

        updateHeaderTextView(0);

        //        setData(new ArrayList<Coupon>());
    }

    private void initToolbar(View view)
    {
        View toolbar = view.findViewById(R.id.toolbar);

        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(mContext, toolbar);
        dailyToolbarLayout.initToolbar(mContext.getString(R.string.actionbar_title_coupon_list), new View.OnClickListener()
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
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        mEmptyView = view.findViewById(R.id.emptyView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(layoutManager);

        EdgeEffectColor.setEdgeGlowColor(mRecyclerView, mContext.getResources().getColor(R.color.default_over_scroll_edge));
    }

    private void updateHeaderTextView(int count)
    {
        if (mContext == null)
        {
            return;
        }

        if (mHeaderTextView == null)
        {
            return;
        }

        String text = mContext.getString(R.string.coupon_header_text, count);
        mHeaderTextView.setText(text);
    }

    private boolean isEmpty(List<Coupon> list)
    {
        return (list == null || list.size() == 0);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.couponSortingButton:
                //                ((OnEventListener) mOnEventListener).startCouponHistory();
                if (v.getTag() == null)
                {
                    v.setTag(CouponListActivity.CouponSortType.ALL);
                }

                ((OnEventListener) mOnEventListener).onSortingButtonClick((CouponListActivity.CouponSortType) v.getTag());
                break;

//            case R.id.registerCouponLayout:
//                ((OnEventListener) mOnEventListener).startRegisterCoupon();
//                break;
        }
    }

    public void setData(List<Coupon> list)
    {
        if (isEmpty(list) == false)
        {
            mEmptyView.setVisibility(View.GONE);
        } else
        {
            list = new ArrayList<>();
            mEmptyView.setVisibility(View.VISIBLE);
        }

        updateHeaderTextView(list.size());

        if (mListAdapter == null)
        {
            mListAdapter = new CouponListAdapter(mContext, list, mCouponItemListener);
            mRecyclerView.setAdapter(mListAdapter);
        } else
        {
            mListAdapter.setData(list);
            mListAdapter.notifyDataSetChanged();
        }
    }

    public Coupon getCoupon(String userCouponCode)
    {
        return mListAdapter.getCoupon(userCouponCode);
    }

    private CouponListAdapter.OnCouponItemListener mCouponItemListener = new CouponListAdapter.OnCouponItemListener()
    {
        @Override
        public void startNotice()
        {
            ((OnEventListener) mOnEventListener).startNotice();
        }

        @Override
        public void showNotice(View view, int position)
        {
            Coupon coupon = mListAdapter.getItem(position);
            ((OnEventListener) mOnEventListener).showListItemNotice(coupon);
        }

        @Override
        public void onDownloadClick(View view, int position)
        {
            Coupon coupon = mListAdapter.getItem(position);
            ((OnEventListener) mOnEventListener).onListItemDownLoadClick(coupon);
        }
    };
}
