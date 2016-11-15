package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;

import java.util.List;

public class CouponSortListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener
{
    public enum SortType
    {
        ALL("전체 쿠폰 보기"),
        STAY("호텔 쿠폰 보기"),
        GOURMET("고메 쿠폰 보기");

        private String mName;

        SortType(String name)
        {
            mName = name;
        }

        public String getName()
        {
            return mName;
        }
    }

    private List<SortType> mList;
    private Context mContext;
    private SortType mSelectedSortType;
    private View.OnClickListener mOnClickListener;

    public CouponSortListAdapter(Context context, List<SortType> list)
    {
        mContext = context;
        mList = list;

        mSelectedSortType = null;
    }

    public SortType getItem(int position)
    {
        return mList.get(position);
    }

    @Override
    public int getItemCount()
    {
        return mList == null ? 0 : mList.size();
    }

    public SortType getSelectedSortType()
    {
        return mSelectedSortType;
    }

    public void setSelectedSortType(SortType sortType)
    {
        mSelectedSortType = sortType;
    }

    public void setOnItemClickListener(View.OnClickListener listener)
    {
        mOnClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_coupon_sort, parent, false);
        return new CouponSortViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        ((CouponSortViewHolder) holder).onBindViewHolder(position);
    }

    @Override
    public void onClick(View v)
    {
        Object tag = v.getTag();

        if (tag != null && tag instanceof SortType)
        {
            mSelectedSortType = (SortType) tag;

            if (mOnClickListener != null)
            {
                mOnClickListener.onClick(v);
            }

            notifyDataSetChanged();
        }
    }

    private class CouponSortViewHolder extends RecyclerView.ViewHolder
    {
        private TextView bankNameTextView;
        private View dividerView;

        public CouponSortViewHolder(View view)
        {
            super(view);

            bankNameTextView = (TextView) view.findViewById(R.id.textView);
            dividerView = view.findViewById(R.id.dividerView);

            bankNameTextView.setOnClickListener(CouponSortListAdapter.this);
        }

        public void onBindViewHolder(int position)
        {
            SortType sortType = getItem(position);

            bankNameTextView.setText(sortType.getName());
            bankNameTextView.setTag(sortType);

            if (mSelectedSortType != null && mSelectedSortType.equals(sortType) == true)
            {
                bankNameTextView.setSelected(true);
                bankNameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.card_btn_v_select, 0);
            } else
            {
                bankNameTextView.setSelected(false);
                bankNameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }

            if (position == getItemCount() - 1)
            {
                dividerView.setVisibility(View.GONE);
            } else
            {
                dividerView.setVisibility(View.VISIBLE);
            }
        }
    }
}
