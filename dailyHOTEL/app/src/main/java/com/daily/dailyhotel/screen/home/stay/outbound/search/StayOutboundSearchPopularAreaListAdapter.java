package com.daily.dailyhotel.screen.home.stay.outbound.search;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ListRowSearchCardItemBinding;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class StayOutboundSearchPopularAreaListAdapter extends RecyclerView.Adapter<StayOutboundSearchPopularAreaListAdapter.ItemViewHolder>
{
    private Context mContext;
    View.OnClickListener mOnClickListener;
    private List<StayOutboundSuggest> mStayOutboundSuggestList;

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
        StayOutboundSuggest stayOutboundSuggest = getItem(position);

        holder.itemView.setTag(stayOutboundSuggest);

        holder.dataBinding.iconImageView.setImageResource(R.drawable.vector_ob_search_ic_01_region);
        holder.dataBinding.itemTextView.setText(stayOutboundSuggest.display);
    }

    public void setData(List<StayOutboundSuggest> stayOutboundSuggestList)
    {
        if (mStayOutboundSuggestList == null)
        {
            mStayOutboundSuggestList = new ArrayList<>();
        }

        clear();

        if (stayOutboundSuggestList == null || stayOutboundSuggestList.size() == 0)
        {
            return;
        }

        mStayOutboundSuggestList.addAll(stayOutboundSuggestList);
    }

    public void clear()
    {
        if (mStayOutboundSuggestList == null)
        {
            return;
        }

        mStayOutboundSuggestList.clear();
    }

    public StayOutboundSuggest getItem(int position)
    {
        if (position < 0 || mStayOutboundSuggestList.size() <= position)
        {
            return null;
        }

        return mStayOutboundSuggestList.get(position);
    }

    @Override
    public int getItemCount()
    {
        return mStayOutboundSuggestList == null ? 0 : mStayOutboundSuggestList.size();
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
