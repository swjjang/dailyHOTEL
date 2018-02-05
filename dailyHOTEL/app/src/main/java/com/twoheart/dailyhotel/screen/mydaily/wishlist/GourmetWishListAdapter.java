package com.twoheart.dailyhotel.screen.mydaily.wishlist;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.view.DailyGourmetCardView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.util.Util;

import java.util.List;

/**
 * Created by android_sam on 2016. 11. 1..
 */

public class GourmetWishListAdapter extends PlaceWishListAdapter
{
    public GourmetWishListAdapter(Context context, List<PlaceViewItem> list, OnPlaceWishListItemListener listener)
    {
        super(context, list, listener);
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

                return new GourmetWishViewHolder(gourmetCardView);
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
                onBindViewHolder((GourmetWishViewHolder) viewHolder, item, position);
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void onBindViewHolder(GourmetWishViewHolder holder, PlaceViewItem placeViewItem, int position)
    {
        if (holder == null || placeViewItem == null)
        {
            return;
        }

        final Gourmet gourmet = placeViewItem.getItem();

        holder.gourmetCardView.setStickerVisible(false);
        holder.gourmetCardView.setDeleteVisible(false);
        holder.gourmetCardView.setWishVisible(true);
        holder.gourmetCardView.setWish(true);
        holder.gourmetCardView.setTagStickerImage(gourmet.stickerUrl);
        holder.gourmetCardView.setImage(gourmet.imageUrl);
        holder.gourmetCardView.setGradeText(DailyTextUtils.isTextEmpty(gourmet.subCategory) == false ? gourmet.subCategory : gourmet.category);
        holder.gourmetCardView.setVRVisible(gourmet.truevr && mTrueVREnabled);
        holder.gourmetCardView.setReviewText(gourmet.satisfaction, gourmet.reviewCount);
        holder.gourmetCardView.setNewVisible(gourmet.newItem);
        holder.gourmetCardView.setGourmetNameText(gourmet.name);
        holder.gourmetCardView.setDistanceVisible(false);
        holder.gourmetCardView.setAddressText(gourmet.addressSummary);
        holder.gourmetCardView.setPriceVisible(false);
        holder.gourmetCardView.setBenefitText(gourmet.dBenefitText);
        holder.gourmetCardView.setDividerVisible(true);
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder
    {
        public FooterViewHolder(View itemView)
        {
            super(itemView);
        }
    }

    private class GourmetWishViewHolder extends RecyclerView.ViewHolder
    {
        DailyGourmetCardView gourmetCardView;

        public GourmetWishViewHolder(DailyGourmetCardView gourmetCardView)
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

            gourmetCardView.setOnWishClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    gourmetCardView.setWish(false);

                    if (mListener != null)
                    {
                        mListener.onItemRemoveClick(gourmetCardView);
                    }
                }
            });
        }
    }
}
