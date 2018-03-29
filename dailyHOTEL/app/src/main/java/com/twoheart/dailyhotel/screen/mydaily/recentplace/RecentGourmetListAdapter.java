package com.twoheart.dailyhotel.screen.mydaily.recentplace;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.RecentlyPlace;
import com.daily.dailyhotel.view.DailyGourmetCardView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.network.model.Sticker;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 10. 13..
 */

public class RecentGourmetListAdapter extends RecentPlacesListAdapter
{
    public RecentGourmetListAdapter(Context context, ArrayList<PlaceViewItem> list, OnRecentPlacesItemListener listener)
    {
        super(context, list, listener);
    }

    @Override
    public void setPlaceBookingDay(PlaceBookingDay placeBookingDay)
    {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case PlaceViewItem.TYPE_ENTRY:
            {
                DailyGourmetCardView gourmetCardView = new DailyGourmetCardView(mContext);
                gourmetCardView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                return new GourmetViewHolder(gourmetCardView);
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
                onBindViewHolder((GourmetViewHolder) viewHolder, item, position);
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void onBindViewHolder(GourmetViewHolder holder, PlaceViewItem placeViewItem, int position)
    {
        final RecentlyPlace recentlyPlace = placeViewItem.getItem();

        holder.gourmetCardView.setStickerVisible(false);
        holder.gourmetCardView.setDeleteVisible(true);
        holder.gourmetCardView.setWishVisible(true);
        holder.gourmetCardView.setWish(recentlyPlace.myWish);

        // 스티커
        String stickerUrl = null;
        if (recentlyPlace.details.sticker != null)
        {
            stickerUrl = ScreenUtils.getScreenWidth(mContext) <= Sticker.DEFAULT_SCREEN_WIDTH == true ? recentlyPlace.details.sticker.lowResolutionImageUrl : recentlyPlace.details.sticker.defaultImageUrl;
        }

        holder.gourmetCardView.setTagStickerImage(stickerUrl);
        holder.gourmetCardView.setImage(recentlyPlace.imageUrl);

        holder.gourmetCardView.setGradeText(DailyTextUtils.isTextEmpty(recentlyPlace.details.subCategory) == false ? recentlyPlace.details.subCategory : recentlyPlace.details.category);
        holder.gourmetCardView.setVRVisible(recentlyPlace.details.isTrueVr && mTrueVREnabled);
        holder.gourmetCardView.setReviewText(recentlyPlace.rating, recentlyPlace.reviewCount);
        holder.gourmetCardView.setNewVisible(recentlyPlace.newItem);
        holder.gourmetCardView.setGourmetNameText(recentlyPlace.title);
        holder.gourmetCardView.setDistanceVisible(false);
        holder.gourmetCardView.setAddressText(recentlyPlace.addrSummary);
        holder.gourmetCardView.setPriceVisible(false);
        holder.gourmetCardView.setBenefitText(null);
        holder.gourmetCardView.setDividerVisible(true);
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder
    {
        public FooterViewHolder(View itemView)
        {
            super(itemView);
        }
    }

    class GourmetViewHolder extends RecyclerView.ViewHolder
    {
        DailyGourmetCardView gourmetCardView;

        public GourmetViewHolder(DailyGourmetCardView gourmetCardView)
        {
            super(gourmetCardView);

            this.gourmetCardView = gourmetCardView;

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

            gourmetCardView.setOnDeleteClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mListener != null)
                    {
                        mListener.onDeleteClick(gourmetCardView);
                    }
                }
            });

            gourmetCardView.setOnWishClickListener(v -> {
                if (mListener != null)
                {
                    mListener.onWishClick(gourmetCardView);
                }
            });
        }
    }
}
