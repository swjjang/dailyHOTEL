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
        holder.gourmetCardView.setVRVisible(false);
        holder.gourmetCardView.setReviewText(recentlyPlace.rating, recentlyPlace.reviewCount);
        holder.gourmetCardView.setNewVisible(recentlyPlace.newItem);
        holder.gourmetCardView.setGourmetNameText(recentlyPlace.title);
        holder.gourmetCardView.setDistanceVisible(false);
        holder.gourmetCardView.setAddressText(recentlyPlace.addrSummary);
        holder.gourmetCardView.setPriceVisible(false);
        holder.gourmetCardView.setBenefitText(null);
        holder.gourmetCardView.setDividerVisible(true);

        //
        //
        //        String address = recentlyPlace.addrSummary;
        //
        //        int barIndex = address.indexOf('|');
        //        if (barIndex >= 0)
        //        {
        //            address = address.replace(" | ", "ㅣ");
        //        } else if (address.indexOf('l') >= 0)
        //        {
        //            address = address.replace(" l ", "ㅣ");
        //        }
        //
        //        holder.dataBinding.addressTextView.setText(address);
        //        holder.dataBinding.nameTextView.setText(recentlyPlace.title);
        //
        //        // 인원
        //        //        if (recentlyPlace.details.persons > 1)
        //        //        {
        //        //            holder.dataBinding.personsTextView.setVisibility(View.VISIBLE);
        //        //            holder.dataBinding.personsTextView.setText(mContext.getString(R.string.label_persions, recentlyPlace.details.persons));
        //        //        } else
        //        //        {
        //        holder.dataBinding.personsTextView.setVisibility(View.GONE);
        //        //        }
        //
        //        holder.dataBinding.priceTextView.setVisibility(View.INVISIBLE);
        //        holder.dataBinding.priceTextView.setText(null);
        //
        //        // 만족도
        //        if (recentlyPlace.rating > 0)
        //        {
        //            holder.dataBinding.satisfactionView.setVisibility(View.VISIBLE);
        //            holder.dataBinding.satisfactionView.setText(//
        //                mContext.getResources().getString(R.string.label_list_satisfaction, recentlyPlace.rating));
        //        } else
        //        {
        //            holder.dataBinding.satisfactionView.setVisibility(View.GONE);
        //        }
        //
        //        holder.dataBinding.discountPriceTextView.setVisibility(View.GONE);
        //        holder.dataBinding.nameTextView.setSelected(true); // Android TextView marquee bug
        //
        //        if (VersionUtils.isOverAPI16() == true)
        //        {
        //            holder.dataBinding.gradientView.setBackground(mPaintDrawable);
        //        } else
        //        {
        //            holder.dataBinding.gradientView.setBackgroundDrawable(mPaintDrawable);
        //        }
        //
        //        String displayCategory;
        //        if (DailyTextUtils.isTextEmpty(recentlyPlace.details.subCategory) == false)
        //        {
        //            displayCategory = recentlyPlace.details.subCategory;
        //        } else
        //        {
        //            displayCategory = recentlyPlace.details.category;
        //        }
        //
        //        // grade
        //        if (DailyTextUtils.isTextEmpty(displayCategory) == true)
        //        {
        //            holder.dataBinding.gradeTextView.setVisibility(View.GONE);
        //        } else
        //        {
        //            holder.dataBinding.gradeTextView.setVisibility(View.VISIBLE);
        //            holder.dataBinding.gradeTextView.setText(displayCategory);
        //        }
        //
        //        boolean isLowResource = false;
        //
        //        if (ScreenUtils.getScreenWidth(mContext) <= Sticker.DEFAULT_SCREEN_WIDTH)
        //        {
        //            isLowResource = true;
        //        }
        //
        //        // 스티커
        //        String stickerUrl = null;
        //        Sticker sticker = recentlyPlace.details.sticker != null ? recentlyPlace.details.sticker : null;
        //        if (sticker != null)
        //        {
        //            stickerUrl = isLowResource == false ? sticker.defaultImageUrl : sticker.lowResolutionImageUrl;
        //        }
        //
        //        if (DailyTextUtils.isTextEmpty(stickerUrl) == false)
        //        {
        //            holder.dataBinding.stickerSimpleDraweeView.setVisibility(View.VISIBLE);
        //
        //            DraweeController controller = Fresco.newDraweeControllerBuilder().setControllerListener(new BaseControllerListener<ImageInfo>()
        //            {
        //                @Override
        //                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable)
        //                {
        //                    ViewGroup.LayoutParams layoutParams = holder.dataBinding.stickerSimpleDraweeView.getLayoutParams();
        //
        //                    int screenWidth = ScreenUtils.getScreenWidth(mContext);
        //                    if (screenWidth > Sticker.DEFAULT_SCREEN_WIDTH && screenWidth < Sticker.LARGE_SCREEN_WIDTH)
        //                    {
        //                        layoutParams.width = (int) (Sticker.MEDIUM_RATE * imageInfo.getWidth());
        //                        layoutParams.height = (int) (Sticker.MEDIUM_RATE * imageInfo.getHeight());
        //                    } else
        //                    {
        //                        layoutParams.width = imageInfo.getWidth();
        //                        layoutParams.height = imageInfo.getHeight();
        //                    }
        //
        //                    holder.dataBinding.stickerSimpleDraweeView.setLayoutParams(layoutParams);
        //                }
        //            }).setUri(Uri.parse(stickerUrl)).build();
        //
        //            holder.dataBinding.stickerSimpleDraweeView.setController(controller);
        //        } else
        //        {
        //            holder.dataBinding.stickerSimpleDraweeView.setVisibility(View.GONE);
        //        }
        //
        //        Util.requestImageResize(mContext, holder.dataBinding.imageView, recentlyPlace.imageUrl);
        //
        //        // SOLD OUT 표시
        //        if (recentlyPlace.isSoldOut)
        //        {
        //            holder.dataBinding.soldoutView.setVisibility(View.VISIBLE);
        //        } else
        //        {
        //            holder.dataBinding.soldoutView.setVisibility(View.GONE);
        //        }
        //
        //        //        if (DailyTextUtils.isTextEmpty(recentlyPlace.dBenefitText) == false)
        //        //        {
        //        //            holder.dataBinding.dBenefitTextView.setVisibility(View.VISIBLE);
        //        //            holder.dataBinding.dBenefitTextView.setText(recentlyPlace.dBenefitText);
        //        //        } else
        //        //        {
        //        holder.dataBinding.dBenefitTextView.setVisibility(View.GONE);
        //        //        }
        //
        //        //        if (mShowDistanceIgnoreSort == true || getSortType() == Constants.SortType.DISTANCE)
        //        //        {
        //        //            if (holder.dataBinding.satisfactionView.getVisibility() == View.VISIBLE || holder.dataBinding.trueVRView.getVisibility() == View.VISIBLE)
        //        //            {
        //        //                holder.dataBinding.dot1View.setVisibility(View.VISIBLE);
        //        //            } else
        //        //            {
        //        //                holder.dataBinding.dot1View.setVisibility(View.GONE);
        //        //            }
        //        //
        //        //            holder.dataBinding.distanceTextView.setVisibility(View.VISIBLE);
        //        //            holder.dataBinding.distanceTextView.setText(mContext.getString(R.string.label_distance_km, new DecimalFormat("#.#").format(recentlyPlace.distance)));
        //        //        } else
        //        //        {
        //        holder.dataBinding.dot1View.setVisibility(View.GONE);
        //        holder.dataBinding.distanceTextView.setVisibility(View.GONE);
        //        //        }
        //
        //        // VR 여부
        //        //        if (gourmet.truevr == true && mTrueVREnabled == true)
        //        //        {
        //        //            if (holder.satisfactionView.getVisibility() == View.VISIBLE)
        //        //            {
        //        //                holder.dot2View.setVisibility(View.VISIBLE);
        //        //            } else
        //        //            {
        //        //                holder.dot2View.setVisibility(View.GONE);
        //        //            }
        //        //
        //        //            holder.trueVRView.setVisibility(View.VISIBLE);
        //        //        } else
        //        {
        //            holder.dataBinding.dot2View.setVisibility(View.GONE);
        //            holder.dataBinding.trueVRView.setVisibility(View.GONE);
        //        }
        //
        //        holder.dataBinding.deleteView.setVisibility(View.VISIBLE);
        //        holder.dataBinding.deleteView.setTag(position);
        //        holder.dataBinding.deleteView.setOnClickListener(new View.OnClickListener()
        //        {
        //            @Override
        //            public void onClick(View v)
        //            {
        //                if (mListener != null)
        //                {
        //                    mListener.onDeleteClick(v, (Integer) v.getTag());
        //                }
        //            }
        //        });
        //
        //        if (holder.dataBinding.satisfactionView.getVisibility() == View.GONE//
        //            && holder.dataBinding.trueVRView.getVisibility() == View.GONE//
        //            && holder.dataBinding.distanceTextView.getVisibility() == View.GONE)
        //        {
        //            holder.dataBinding.informationLayout.setVisibility(View.GONE);
        //        } else
        //        {
        //            holder.dataBinding.informationLayout.setVisibility(View.VISIBLE);
        //        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder
    {
        public FooterViewHolder(View itemView)
        {
            super(itemView);
        }
    }

    private class GourmetViewHolder extends RecyclerView.ViewHolder
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
        }
    }
}
