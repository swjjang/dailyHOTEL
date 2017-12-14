package com.daily.dailyhotel.screen.home.stay.outbound.search;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.dailyhotel.entity.Suggest;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ListRowSearchCardItemBinding;

import java.util.ArrayList;
import java.util.List;

public class StayOutboundSearchPopularAreaListAdapter extends RecyclerView.Adapter<StayOutboundSearchPopularAreaListAdapter.ItemViewHolder>
{
    private Context mContext;
    View.OnClickListener mOnClickListener;
    private List<Suggest> mSuggestList;

    public StayOutboundSearchPopularAreaListAdapter(Context context, View.OnClickListener onClickListener)
    {
        mContext = context;
        mOnClickListener = onClickListener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        ListRowSearchCardItemBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.list_row_search_card_item, parent, false);
        return new ItemViewHolder(dataBinding);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position)
    {
        Suggest suggest = getItem(position);

        holder.itemView.setTag(suggest);

        holder.dataBinding.iconImageView.setImageResource(R.drawable.vector_ob_search_ic_01_region);
        holder.dataBinding.itemTextView.setText(suggest.display);
    }

    public void setData(List<Suggest> suggestList)
    {
        if (mSuggestList == null)
        {
            mSuggestList = new ArrayList<>();
        }

        clear();

        if (suggestList == null || suggestList.size() == 0)
        {
            return;
        }

        mSuggestList.addAll(suggestList);
    }

    public void clear()
    {
        if (mSuggestList == null)
        {
            return;
        }

        mSuggestList.clear();
    }

    public Suggest getItem(int position)
    {
        if (position < 0 || mSuggestList.size() <= position)
        {
            return null;
        }

        return mSuggestList.get(position);
    }

    @Override
    public int getItemCount()
    {
        return mSuggestList == null ? 0 : mSuggestList.size();
    }

    protected class ItemViewHolder extends RecyclerView.ViewHolder
    {
        public ListRowSearchCardItemBinding dataBinding;

        public ItemViewHolder(ListRowSearchCardItemBinding dataBinding)
        {
            super(dataBinding.getRoot());

            this.dataBinding = dataBinding;

            itemView.setOnClickListener(mOnClickListener);
        }
    }
}
