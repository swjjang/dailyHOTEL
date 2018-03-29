package com.twoheart.dailyhotel.screen.mydaily.recentplace;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.daily.dailyhotel.entity.RecentlyPlace;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.view.DailyStayCardView;
import com.daily.dailyhotel.view.DailyStayOutboundCardView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 10. 13..
 */

public class RecentStayListAdapter extends RecentPlacesListAdapter
{
    private int mNights;

    public RecentStayListAdapter(Context context, ArrayList<PlaceViewItem> list, OnRecentPlacesItemListener listener)
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

                return new StayInboundViewHolder(stayCardView);
            }

            case PlaceViewItem.TYPE_OB_ENTRY:
            {
                DailyStayOutboundCardView stayOutboundCardView = new DailyStayOutboundCardView(mContext);
                stayOutboundCardView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                return new StayOutboundViewHolder(stayOutboundCardView);
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
                onBindViewHolder((StayInboundViewHolder) viewHolder, item, position);
                break;

            case PlaceViewItem.TYPE_OB_ENTRY:
                onBindViewHolder((StayOutboundViewHolder) viewHolder, item, position);
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void onBindViewHolder(StayInboundViewHolder holder, PlaceViewItem placeViewItem, int position)
    {
        final RecentlyPlace recentlyPlace = placeViewItem.getItem();

        holder.stayCardView.setStickerVisible(false);
        holder.stayCardView.setDeleteVisible(true);
        holder.stayCardView.setWishVisible(true);
        holder.stayCardView.setWish(recentlyPlace.myWish);

        holder.stayCardView.setImage(recentlyPlace.imageUrl);

        holder.stayCardView.setGradeText(Stay.Grade.valueOf(recentlyPlace.details.grade).getName(mContext));
        holder.stayCardView.setVRVisible(recentlyPlace.details.isTrueVr && mTrueVREnabled);
        holder.stayCardView.setReviewText(recentlyPlace.rating, recentlyPlace.reviewCount);
        holder.stayCardView.setNewVisible(recentlyPlace.newItem);
        holder.stayCardView.setStayNameText(recentlyPlace.title);
        holder.stayCardView.setDistanceVisible(false);
        holder.stayCardView.setAddressText(recentlyPlace.addrSummary);
        holder.stayCardView.setPriceVisible(false);
        holder.stayCardView.setBenefitText(null);
        holder.stayCardView.setDividerVisible(true);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void onBindViewHolder(StayOutboundViewHolder holder, PlaceViewItem placeViewItem, int position)
    {
        if (holder == null || placeViewItem == null)
        {
            return;
        }

        StayOutbound stayOutbound = placeViewItem.getItem();

        holder.stayOutboundCardView.setStickerVisible(false);
        holder.stayOutboundCardView.setDeleteVisible(true);
        holder.stayOutboundCardView.setWishVisible(true);
        holder.stayOutboundCardView.setWish(stayOutbound.myWish);

        holder.stayOutboundCardView.setImage(stayOutbound.getImageMap());

        if ((int) stayOutbound.rating == 0)
        {
            holder.stayOutboundCardView.setGradeText(null);
        } else
        {
            holder.stayOutboundCardView.setGradeText(mContext.getString(R.string.label_stay_outbound_filter_x_star_rate, (int) stayOutbound.rating));
        }

        holder.stayOutboundCardView.setVRVisible(false);
        holder.stayOutboundCardView.setRatingText(stayOutbound.tripAdvisorRating);

        holder.stayOutboundCardView.setNewVisible(false);

        holder.stayOutboundCardView.setStayNameText(stayOutbound.name, stayOutbound.nameEng);
        holder.stayOutboundCardView.setDistanceVisible(false);

        holder.stayOutboundCardView.setAddressText(stayOutbound.locationDescription);
        holder.stayOutboundCardView.setPriceVisible(false);
        holder.stayOutboundCardView.setBenefitText(null);
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder
    {
        public FooterViewHolder(View itemView)
        {
            super(itemView);
        }
    }

    class StayInboundViewHolder extends RecyclerView.ViewHolder
    {
        DailyStayCardView stayCardView;

        public StayInboundViewHolder(DailyStayCardView stayCardView)
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

            stayCardView.setOnDeleteClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mListener != null)
                    {
                        mListener.onDeleteClick(stayCardView);
                    }
                }
            });

            stayCardView.setOnWishClickListener(v -> {
                if (mListener != null)
                {
                    mListener.onWishClick(stayCardView);
                }
            });
        }
    }

    class StayOutboundViewHolder extends RecyclerView.ViewHolder
    {
        DailyStayOutboundCardView stayOutboundCardView;

        public StayOutboundViewHolder(DailyStayOutboundCardView stayOutboundCardView)
        {
            super(stayOutboundCardView);

            this.stayOutboundCardView = stayOutboundCardView;

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

            stayOutboundCardView.setOnDeleteClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mListener != null)
                    {
                        mListener.onDeleteClick(stayOutboundCardView);
                    }
                }
            });

            stayOutboundCardView.setOnWishClickListener(v -> {
                if (mListener != null)
                {
                    mListener.onWishClick(stayOutboundCardView);
                }
            });
        }
    }
}
