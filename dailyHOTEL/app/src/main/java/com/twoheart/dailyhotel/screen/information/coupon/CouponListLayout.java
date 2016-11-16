package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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

    public interface OnEventListener extends OnBaseEventListener
    {
        void startCouponHistory();

        void startNotice();

        void startRegisterCoupon();

        void showListItemNotice(Coupon coupon);

        void onListItemDownLoadClick(Coupon coupon);

        void onUpdateList(CouponSortListAdapter.SortType sortType);
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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.coupon_sort_array, R.layout.list_row_coupon_spinner);
        adapter.setDropDownViewResource(R.layout.list_row_coupon_sort_dropdown_item);
        mSortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                CouponSortListAdapter.SortType sortType;
                switch (position)
                {
                    case 2:
                        sortType = CouponSortListAdapter.SortType.GOURMET;
                        break;
                    case 1:
                        sortType = CouponSortListAdapter.SortType.STAY;
                        break;
                    case 0:
                    default:
                        sortType = CouponSortListAdapter.SortType.ALL;
                        break;
                }

                setSortType(sortType);
                ((OnEventListener) mOnEventListener).onUpdateList(sortType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        mSortSpinner.setAdapter(adapter);


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

    public void setSortType(CouponSortListAdapter.SortType sortType)
    {
        if (sortType == null)
        {
            return;
        }

        //        if (mSortTextView == null)
        //        {
        //            return;
        //        }
        //
        //        CouponSortListAdapter.SortType oldSortType = null;
        //        Object tag = mSortTextView.getTag();
        //        if (tag != null && tag instanceof CouponSortListAdapter.SortType)
        //        {
        //            oldSortType = (CouponSortListAdapter.SortType) tag;
        //        }
        //
        //        if (sortType.equals(oldSortType) == false)
        //        {
        //            mSortTextView.setTag(sortType);
        //            mSortTextView.setText(sortType.getName());
        //        }
    }

    public CouponSortListAdapter.SortType getSortType()
    {
        //        if (mSortTextView == null)
        //        {
        //            return CouponSortListAdapter.SortType.ALL;
        //        }
        //
        //        Object tag = mSortTextView.getTag();
        //        if (tag == null)
        //        {
        //            return CouponSortListAdapter.SortType.ALL;
        //        }
        //
        //        if (tag instanceof CouponSortListAdapter.SortType)
        //        {
        //            return (CouponSortListAdapter.SortType) tag;
        //        }

        return CouponSortListAdapter.SortType.ALL;
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
}
