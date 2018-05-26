package com.twoheart.dailyhotel.screen.mydaily.coupon;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sam Lee on 2016. 5. 20..
 */
@Deprecated
public class CouponListLayout extends BaseLayout
{

    private DailyTextView mHeaderTextView;
    private RecyclerView mRecyclerView;
    private View mEmptyView, mCouponLayout;
    CouponListAdapter mListAdapter;
    private Spinner mSortSpinner;
    SortArrayAdapter mSortArrayAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void startCouponHistory();

        void startNotice();

        void startRegisterCoupon();

        void showListItemNotice(Coupon coupon);

        void onListItemDownLoadClick(Coupon coupon);

        void onItemSelectedSpinner(int position);
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

        mCouponLayout = view.findViewById(R.id.couponLayout);
        mCouponLayout.setVisibility(View.INVISIBLE);

        mHeaderTextView = mCouponLayout.findViewById(R.id.couponTextView);
        mSortSpinner = view.findViewById(R.id.sortSpinner);

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
                    ((OnEventListener) mOnEventListener).onItemSelectedSpinner(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    private void initToolbar(View view)
    {
        DailyToolbarView dailyToolbarView = view.findViewById(R.id.toolbarView);
        dailyToolbarView.setTitleText(R.string.actionbar_title_coupon_list);
        dailyToolbarView.setOnBackClickListener(new View.OnClickListener()
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
        mRecyclerView = view.findViewById(R.id.recyclerView);

        mEmptyView = view.findViewById(R.id.emptyView);

        View couponUseNoticeTextView = mEmptyView.findViewById(R.id.couponUseNoticeTextView);
        View couponHistoryTextView = mEmptyView.findViewById(R.id.couponHistoryTextView);

        couponUseNoticeTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCouponItemListener.startNotice();
            }
        });

        couponHistoryTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCouponItemListener.startCouponHistory();
            }
        });

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

        mSortArrayAdapter.setSelection(position);
        mSortSpinner.setSelection(position);
    }

    public void setData(List<Coupon> list, CouponListActivity.SortType sortType, boolean scrollTop)
    {
        if (isEmpty(list) == false)
        {
            mEmptyView.setVisibility(View.GONE);
        } else
        {
            list = new ArrayList<>();
            mEmptyView.setVisibility(View.VISIBLE);
        }

        int couponCount = list.size();

        if (couponCount == 0 && sortType == CouponListActivity.SortType.ALL)
        {
            mCouponLayout.setVisibility(View.GONE);
            return;
        } else
        {
            mCouponLayout.setVisibility(View.VISIBLE);
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

        if (scrollTop == true)
        {
            mRecyclerView.scrollToPosition(0);
        }
    }

    public Coupon getCoupon(String couponCode)
    {
        return mListAdapter.getCoupon(couponCode);
    }

    CouponListAdapter.OnCouponItemListener mCouponItemListener = new CouponListAdapter.OnCouponItemListener()
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
                TextView textView = (TextView) view;
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                textView.setSelected(mSelectedPosition == position);

                if (mSelectedPosition == position)
                {
                    textView.setTextColor(mContext.getResources().getColor(R.color.default_text_ceb2135));
                } else
                {
                    textView.setTextColor(mContext.getResources().getColor(R.color.default_text_c4d4d4d));
                }
            }

            return view;
        }
    }
}
