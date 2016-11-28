package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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
public class CouponListLayout extends BaseLayout
{

    private DailyTextView mHeaderTextView;
    private RecyclerView mRecyclerView;
    private View mEmptyView;
    private CouponListAdapter mListAdapter;
    private Spinner mSortSpinner;
    private SortArrayAdapter mSortArrayAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void startCouponHistory();

        void startNotice();

        void startRegisterCoupon();

        void showListItemNotice(Coupon coupon);

        void onListItemDownLoadClick(Coupon coupon);

        void onClickSpinner(int position);
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
        mSortSpinner = (Spinner) view.findViewById(R.id.sortSpinner);

        CharSequence[] strings = mContext.getResources().getTextArray(R.array.coupon_sort_array);
        mSortArrayAdapter = new SortArrayAdapter(mContext, R.layout.list_row_coupon_spinner, strings);

        mSortArrayAdapter.setDropDownViewResource(R.layout.list_row_coupon_sort_dropdown_item);
        mSortSpinner.setAdapter(mSortArrayAdapter);

        mSortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                mSortArrayAdapter.setSelection(position);

                if (mListAdapter != null)
                {
                    ((OnEventListener) mOnEventListener).onClickSpinner(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        updateHeaderTextView(0);
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

        View registerCouponView = view.findViewById(R.id.registerCouponView);
        registerCouponView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).startRegisterCoupon();
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

    public void setSelectionSpinner(CouponListActivity.SortType sortType)
    {
        if (mSortSpinner == null || sortType == null)
        {
            return;
        }

        int position;

        switch (sortType)
        {
            case STAY:
                position = 1;
                break;

            case GOURMET:
                position = 2;
                break;

            default:
                position = 0;
                break;
        }

        AdapterView.OnItemSelectedListener onItemSelectedListener = mSortSpinner.getOnItemSelectedListener();
        mSortSpinner.setOnItemSelectedListener(null);
        mSortSpinner.setSelection(position);
        mSortArrayAdapter.setSelection(position);
        mSortSpinner.setOnItemSelectedListener(onItemSelectedListener);
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
        public void startCouponHistory()
        {
            ((OnEventListener) mOnEventListener).startCouponHistory();
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

    private class SortArrayAdapter extends ArrayAdapter<CharSequence>
    {
        private int mSelectedPosition;

        public SortArrayAdapter(Context context, int resourceId, CharSequence[] list)
        {
            super(context, resourceId, list);
        }

        public void setSelection(int position)
        {
            mSelectedPosition = position;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            View view = super.getDropDownView(position, convertView, parent);

            if (view != null)
            {
                TextView textView = (TextView) view.findViewById(R.id.textView);

                if (textView != null)
                {
                    textView.setSelected(mSelectedPosition == position ? true : false);

                    if (mSelectedPosition == position)
                    {
                        textView.setTextColor(mContext.getResources().getColor(R.color.default_text_c900034));
                    } else
                    {
                        textView.setTextColor(mContext.getResources().getColor(R.color.default_text_c323232));
                    }
                }

            }
            return view;
        }
    }
}
