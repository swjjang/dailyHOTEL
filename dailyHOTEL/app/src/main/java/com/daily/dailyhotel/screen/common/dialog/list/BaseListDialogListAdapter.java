package com.daily.dailyhotel.screen.common.dialog.list;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.dailyhotel.parcel.ListDialogItemParcel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ListRowBaseListDataBinding;

import java.util.List;

/**
 * Created by android_sam on 2018. 2. 21..
 */

public class BaseListDialogListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener
{
    private List<ListDialogItemParcel> mList;
    private Context mContext;
    private ListDialogItemParcel mSelectedItem;
    private View.OnClickListener mOnClickListener;

    public BaseListDialogListAdapter(Context context, List<ListDialogItemParcel> list)
    {
        mContext = context;
        mList = list;

        mSelectedItem = null;
    }

    public void setList(List<ListDialogItemParcel> list)
    {
        mList = list;
    }

    public ListDialogItemParcel getItem(int position)
    {
        return mList.get(position);
    }

    @Override
    public int getItemCount()
    {
        return mList == null ? 0 : mList.size();
    }

    public ListDialogItemParcel getSelectedItem()
    {
        return mSelectedItem;
    }

    public void setSelectedItem(ListDialogItemParcel item)
    {
        mSelectedItem = item;
    }

    public void setOnItemClickListener(View.OnClickListener listener)
    {
        mOnClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        ListRowBaseListDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.list_row_base_list_data, parent, false);
        return new ListViewHolder(viewDataBinding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        onBindViewHolder((ListViewHolder) holder, position);
    }


    private void onBindViewHolder(ListViewHolder holder, int position)
    {
        ListDialogItemParcel item = getItem(position);
        if (item == null)
        {
            return;
        }

        holder.dataBinding.textView.setTag(item);
        holder.dataBinding.textView.setText(item.displayName);

        if (mSelectedItem == null || mSelectedItem != item)
        {
            holder.dataBinding.textView.setSelected(false);
            holder.dataBinding.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else
        {
            holder.dataBinding.textView.setSelected(true);
            holder.dataBinding.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.card_btn_v_select, 0);
        }

        if (position == getItemCount() - 1)
        {
            holder.dataBinding.dividerView.setVisibility(View.GONE);
        } else
        {
            holder.dataBinding.dividerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view)
    {
        Object tag = view.getTag();

        if (tag != null && tag instanceof ListDialogItemParcel)
        {
            mSelectedItem = (ListDialogItemParcel) tag;

            if (mOnClickListener != null)
            {
                mOnClickListener.onClick(view);
            }

            notifyDataSetChanged();
        }
    }

    private class ListViewHolder extends RecyclerView.ViewHolder
    {
        public ListRowBaseListDataBinding dataBinding;

        public ListViewHolder(ListRowBaseListDataBinding dataBinding)
        {
            super(dataBinding.getRoot());
            this.dataBinding = dataBinding;

            this.dataBinding.textView.setOnClickListener(BaseListDialogListAdapter.this);
        }
    }
}
