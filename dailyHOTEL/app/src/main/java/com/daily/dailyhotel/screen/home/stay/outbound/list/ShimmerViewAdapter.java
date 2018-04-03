package com.daily.dailyhotel.screen.home.stay.outbound.list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.daily.dailyhotel.view.DailyShimmerCardView;

public class ShimmerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context mContext;
    private int mCount;
    private boolean mAnimatonStart;

    public ShimmerViewAdapter(Context context, int count)
    {
        mContext = context;
        mCount = count;
    }

    public void setCount(int count)
    {
        mCount = count;
    }

    public void setAnimationStart(boolean start)
    {
        mAnimatonStart = start;
    }

    @Override
    public int getItemCount()
    {
        return mCount;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        DailyShimmerCardView dailyShimmerCardView = new DailyShimmerCardView(mContext);
        dailyShimmerCardView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new ShimmerViewHolder(dailyShimmerCardView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        if (holder == null)
        {
            return;
        }

        ShimmerViewHolder viewHolder = (ShimmerViewHolder) holder;

        viewHolder.shimmerCardView.setDividerVisible(true);
        viewHolder.shimmerCardView.setWishVisible(true);

        if (mAnimatonStart)
        {
            viewHolder.shimmerCardView.startShimmer();
        } else
        {
            viewHolder.shimmerCardView.cancelShimmer();
        }
    }

    protected class ShimmerViewHolder extends RecyclerView.ViewHolder
    {
        DailyShimmerCardView shimmerCardView;

        public ShimmerViewHolder(DailyShimmerCardView shimmerCardView)
        {
            super(shimmerCardView);

            this.shimmerCardView = shimmerCardView;
        }
    }
}
