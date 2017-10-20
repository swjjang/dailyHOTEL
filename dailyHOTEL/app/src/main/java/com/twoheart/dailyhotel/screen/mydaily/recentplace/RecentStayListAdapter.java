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
    private boolean mRewardEnabled;

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

        holder.stayCardView.setStickerVisible(mRewardEnabled && recentlyPlace.dailyReward);
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

        //
        //
        //
        //
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
        //        holder.dataBinding.averageTextView.setVisibility(View.GONE);
        //        holder.dataBinding.discountPriceTextView.setVisibility(View.GONE);
        //
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
        //        Stay.Grade grade;
        //
        //        try
        //        {
        //            grade = Stay.Grade.valueOf(recentlyPlace.details.grade);
        //        } catch (Exception e)
        //        {
        //            grade = Stay.Grade.etc;
        //        }
        //
        //        // grade
        //        holder.dataBinding.gradeTextView.setText(grade.getName(mContext));
        //        holder.dataBinding.gradeTextView.setBackgroundResource(grade.getColorResId());
        //
        //        Util.requestImageResize(mContext, holder.dataBinding.imageView, recentlyPlace.imageUrl);
        //
        //        // SOLD OUT 표시
        //        if (recentlyPlace.isSoldOut == true)
        //        {
        //            holder.dataBinding.soldoutView.setVisibility(View.VISIBLE);
        //        } else
        //        {
        //            holder.dataBinding.soldoutView.setVisibility(View.GONE);
        //        }
        //
        //        if (DailyTextUtils.isTextEmpty(recentlyPlace.dBenefitText) == false)
        //        {
        //            holder.dataBinding.dBenefitTextView.setVisibility(View.VISIBLE);
        //            holder.dataBinding.dBenefitTextView.setText(recentlyPlace.dBenefitText);
        //        } else
        //        {
        //        holder.dataBinding.dBenefitTextView.setVisibility(View.GONE);
        //        }

        //        if (mShowDistanceIgnoreSort == true || getSortType() == Constants.SortType.DISTANCE)
        //        {
        //            if (holder.dataBinding.satisfactionView.getVisibility() == View.VISIBLE || holder.dataBinding.trueVRView.getVisibility() == View.VISIBLE)
        //            {
        //                holder.dataBinding.dot1View.setVisibility(View.VISIBLE);
        //            } else
        //            {
        //                holder.dataBinding.dot1View.setVisibility(View.GONE);
        //            }
        //
        //            holder.dataBinding.distanceTextView.setVisibility(View.VISIBLE);
        //            holder.dataBinding.distanceTextView.setText(mContext.getString(R.string.label_distance_km, new DecimalFormat("#.#").format(recentlyPlace.distance)));
        //        } else
        //        {
        //        holder.dataBinding.dot1View.setVisibility(View.GONE);
        //        holder.dataBinding.distanceTextView.setVisibility(View.GONE);
        //        }
        //
        //        // VR 여부
        //        if (recentlyPlace.details.isTrueVr == true && mTrueVREnabled == true)
        //        {
        //            if (holder.dataBinding.satisfactionView.getVisibility() == View.VISIBLE)
        //            {
        //                holder.dataBinding.dot2View.setVisibility(View.VISIBLE);
        //            } else
        //            {
        //                holder.dataBinding.dot2View.setVisibility(View.GONE);
        //            }
        //
        //            holder.dataBinding.trueVRView.setVisibility(View.VISIBLE);
        //        } else
        //        {
        //            holder.dataBinding.dot2View.setVisibility(View.GONE);
        //            holder.dataBinding.trueVRView.setVisibility(View.GONE);
        //        }
        //
        //        holder.dataBinding.deleteView.setVisibility(View.VISIBLE);
        //        holder.dataBinding.deleteView.setTag(position);
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void onBindViewHolder(StayOutboundViewHolder holder, PlaceViewItem placeViewItem, int position)
    {
        if (holder == null || placeViewItem == null)
        {
            return;
        }

        StayOutbound stayOutbound = placeViewItem.getItem();

        holder.stayOutboundCardView.setStickerVisible(stayOutbound.dailyReward);
        holder.stayOutboundCardView.setDeleteVisible(true);
        holder.stayOutboundCardView.setWishVisible(false);

        holder.stayOutboundCardView.setImage(stayOutbound.getImageMap());

        holder.stayOutboundCardView.setGradeText(mContext.getString(R.string.label_stay_outbound_filter_x_star_rate, (int) stayOutbound.rating));
        holder.stayOutboundCardView.setVRVisible(false);
        holder.stayOutboundCardView.setRatingText(stayOutbound.rating);

        holder.stayOutboundCardView.setNewVisible(false);

        holder.stayOutboundCardView.setStayNameText(stayOutbound.name, stayOutbound.nameEng);
        holder.stayOutboundCardView.setDistanceVisible(false);

        holder.stayOutboundCardView.setAddressText(stayOutbound.locationDescription);
        holder.stayOutboundCardView.setPriceVisible(false);
        holder.stayOutboundCardView.setBenefitText(null);


        //        holder.dataBinding.addressTextView.setText(stayOutbound.locationDescription);
        //        holder.dataBinding.nameTextView.setText(stayOutbound.name);
        //        holder.dataBinding.nameEngTextView.setText("(" + stayOutbound.nameEng + ")");
        //
        //        ConstraintLayout.LayoutParams nameEngLayoutParams = (ConstraintLayout.LayoutParams) holder.dataBinding.nameEngTextView.getLayoutParams();
        //        nameEngLayoutParams.bottomMargin = ScreenUtils.dpToPx(mContext, 6);
        //
        //        ConstraintLayout.LayoutParams nameLayoutParams = (ConstraintLayout.LayoutParams) holder.dataBinding.nameTextView.getLayoutParams();
        //        nameLayoutParams.bottomMargin = ScreenUtils.dpToPx(mContext, -4);
        //
        //        // 가격
        //        if (stayOutbound.promo == true)
        //        {
        //            holder.dataBinding.priceTextView.setVisibility(View.VISIBLE);
        //            holder.dataBinding.priceTextView.setText(DailyTextUtils.getPriceFormat(mContext, stayOutbound.nightlyBaseRate, false));
        //            holder.dataBinding.priceTextView.setPaintFlags(holder.dataBinding.priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        //        } else
        //        {
        //            holder.dataBinding.priceTextView.setVisibility(View.INVISIBLE);
        //            holder.dataBinding.priceTextView.setText(null);
        //        }
        //
        //        holder.dataBinding.discountPriceTextView.setText(null);
        //        //        holder.dataBinding.discountPriceTextView.setText(DailyTextUtils.getPriceFormat(mContext, stayOutbound.nightlyRate, false));
        //
        //        holder.dataBinding.averageTextView.setVisibility(View.GONE);
        //
        //        if (VersionUtils.isOverAPI16() == true)
        //        {
        //            holder.dataBinding.gradientView.setBackground(mPaintDrawable);
        //        } else
        //        {
        //            holder.dataBinding.gradientView.setBackgroundDrawable(mPaintDrawable);
        //        }
        //
        //        // grade
        //        holder.dataBinding.gradeTextView.setText(mContext.getString(R.string.label_stay_outbound_filter_x_star_rate, (int) stayOutbound.rating));
        //
        //        // 별등급
        //        holder.dataBinding.ratingBar.setRating(stayOutbound.rating);
        //
        //        // tripAdvisor - 최근 본 업장의 경우 노출 안함
        //        holder.dataBinding.tripAdvisorLayout.setVisibility(View.GONE);
        //
        //        // Image
        //        holder.dataBinding.imageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);
        //
        //        ImageMap imageMap = stayOutbound.getImageMap();
        //        String url;
        //
        //        if (ScreenUtils.getScreenWidth(mContext) >= ScreenUtils.DEFAULT_STAYOUTBOUND_XXHDPI_WIDTH)
        //        {
        //            if (DailyTextUtils.isTextEmpty(imageMap.bigUrl) == true)
        //            {
        //                url = imageMap.smallUrl;
        //            } else
        //            {
        //                url = imageMap.bigUrl;
        //            }
        //        } else
        //        {
        //            if (DailyTextUtils.isTextEmpty(imageMap.mediumUrl) == true)
        //            {
        //                url = imageMap.smallUrl;
        //            } else
        //            {
        //                url = imageMap.mediumUrl;
        //            }
        //        }
        //
        //        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>()
        //        {
        //            @Override
        //            public void onFailure(String id, Throwable throwable)
        //            {
        //                if (throwable instanceof IOException == true)
        //                {
        //                    if (url.equalsIgnoreCase(imageMap.bigUrl) == true)
        //                    {
        //                        imageMap.bigUrl = null;
        //                    } else if (url.equalsIgnoreCase(imageMap.mediumUrl) == true)
        //                    {
        //                        imageMap.mediumUrl = null;
        //                    } else
        //                    {
        //                        // 작은 이미지를 로딩했지만 실패하는 경우.
        //                        return;
        //                    }
        //
        //                    holder.dataBinding.imageView.setImageURI(imageMap.smallUrl);
        //                }
        //            }
        //        };
        //
        //        DraweeController draweeController = Fresco.newDraweeControllerBuilder()//
        //            .setControllerListener(controllerListener).setUri(url).build();
        //
        //        holder.dataBinding.imageView.setController(draweeController);
        //
        //        // Promo 설명은 사용하지 않는다.
        //        holder.dataBinding.promoTextView.setVisibility(View.GONE);
        //        holder.dataBinding.dot1View.setVisibility(View.GONE);
        //        holder.dataBinding.distanceTextView.setVisibility(View.GONE);
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
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder
    {
        public FooterViewHolder(View itemView)
        {
            super(itemView);
        }
    }

    private class StayInboundViewHolder extends RecyclerView.ViewHolder
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
        }
    }

    private class StayOutboundViewHolder extends RecyclerView.ViewHolder
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
        }
    }
}
