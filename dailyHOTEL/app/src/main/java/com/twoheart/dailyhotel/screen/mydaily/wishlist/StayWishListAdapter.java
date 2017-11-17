package com.twoheart.dailyhotel.screen.mydaily.wishlist;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.daily.dailyhotel.view.DailyStayCardView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 11. 1..
 */

public class StayWishListAdapter extends PlaceWishListAdapter
{
    private int mNights;

    public StayWishListAdapter(Context context, ArrayList<PlaceViewItem> list, OnPlaceWishListItemListener listener)
    {
        super(context, list, listener);
    }

    @Override
    public void setPlaceBookingDay(PlaceBookingDay placeBookingDay)
    {
        if (placeBookingDay == null)
        {
            return;
        }

        try
        {
            mNights = ((StayBookingDay) placeBookingDay).getNights();
        } catch (Exception e)
        {
            mNights = 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case PlaceViewItem.TYPE_ENTRY:
            {
                DailyStayCardView stayCardView = new DailyStayCardView(mContext);
                stayCardView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                return new StayWishViewHolder(stayCardView);
            }

            case PlaceViewItem.TYPE_FOOTER_VIEW:
            {
                View view = mInflater.inflate(R.layout.list_row_users_place_footer, parent, false);

                return new FooterViewHolder(view);
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        PlaceViewItem item = getItem(position);
        if (item == null)
        {
            return;
        }

        switch (item.mType)
        {
            case PlaceViewItem.TYPE_ENTRY:
                onBindViewHolder((StayWishViewHolder) viewHolder, item, position);
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void onBindViewHolder(StayWishViewHolder holder, PlaceViewItem placeViewItem, int position)
    {
        final Stay stay = placeViewItem.getItem();

        holder.stayCardView.setStickerVisible(mRewardEnabled && stay.provideRewardSticker);
        holder.stayCardView.setDeleteVisible(false);
        holder.stayCardView.setWishVisible(true);
        holder.stayCardView.setWish(true);

        holder.stayCardView.setImage(stay.imageUrl);

        holder.stayCardView.setGradeText(stay.getGrade().getName(mContext));
        holder.stayCardView.setVRVisible(stay.truevr && mTrueVREnabled);
        holder.stayCardView.setReviewText(stay.satisfaction, stay.reviewCount);
        holder.stayCardView.setNewVisible(stay.newItem);
        holder.stayCardView.setStayNameText(stay.name);
        holder.stayCardView.setDistanceVisible(false);
        holder.stayCardView.setAddressText(stay.addressSummary);
        holder.stayCardView.setPriceVisible(false);
        holder.stayCardView.setBenefitText(stay.dBenefitText);
        holder.stayCardView.setDividerVisible(true);
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder
    {
        public FooterViewHolder(View itemView)
        {
            super(itemView);
        }
    }

    private class StayWishViewHolder extends RecyclerView.ViewHolder
    {
        DailyStayCardView stayCardView;

        public StayWishViewHolder(DailyStayCardView stayCardView)
        {
            super(stayCardView);

            this.stayCardView = stayCardView;

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mListener != null)
                    {
                        mListener.onItemClick(v);

                    }
                }
            });

            if (Util.supportPreview(mContext) == true)
            {
                itemView.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        if (mListener == null)
                        {
                            return false;
                        } else
                        {
                            Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(70);

                            mListener.onItemLongClick(v);
                            return true;
                        }
                    }
                });
            }

            stayCardView.setOnWishClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    stayCardView.setWish(false);

                    if (mListener != null)
                    {
                        mListener.onItemRemoveClick(stayCardView);
                    }
                }
            });
        }
    }
}
