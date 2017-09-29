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
import com.twoheart.dailyhotel.util.Constants;
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

            case PlaceViewItem.TYPE_FOOTER_VIEW:
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void onBindViewHolder(StayWishViewHolder holder, PlaceViewItem placeViewItem, int position)
    {
        final Stay stay = placeViewItem.getItem();

        holder.stayCardView.setStickerVisible(false);
        holder.stayCardView.setDeleteVisible(false);
        holder.stayCardView.setWishVisible(true);
        holder.stayCardView.setWish(true);
        holder.stayCardView.setImage(stay.imageUrl);
        holder.stayCardView.setGradeText(stay.getGrade().getName(mContext));
        holder.stayCardView.setVRVisible(stay.truevr && mTrueVREnabled);
        holder.stayCardView.setReviewText(stay.satisfaction, 0);
        holder.stayCardView.setNewVisible(false);
        holder.stayCardView.setStayNameText(stay.name);
        holder.stayCardView.setDistanceVisible(false);
        holder.stayCardView.setAddressText(stay.addressSummary);
        holder.stayCardView.setPriceVisible(false);
        holder.stayCardView.setBenefitText(stay.dBenefitText);
        holder.stayCardView.setDividerVisible(true);


        //        String address = stay.addressSummary;
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
        //        holder.dataBinding.nameTextView.setText(stay.name);
        //        holder.dataBinding.priceTextView.setVisibility(View.INVISIBLE);
        //        holder.dataBinding.priceTextView.setText(null);

        // 만족도
        //        if (stay.satisfaction > 0)
        //        {
        //            holder.dataBinding.satisfactionView.setVisibility(View.VISIBLE);
        //            holder.dataBinding.satisfactionView.setText(//
        //                mContext.getResources().getString(R.string.label_list_satisfaction, stay.satisfaction));
        //        } else
        //        {
        //            holder.dataBinding.satisfactionView.setVisibility(View.GONE);
        //        }

        //        holder.dataBinding.averageTextView.setVisibility(View.GONE);
        //        holder.dataBinding.discountPriceTextView.setVisibility(View.GONE);

        //        holder.dataBinding.nameTextView.setSelected(true); // Android TextView marquee bug

        //        if (VersionUtils.isOverAPI16() == true)
        //        {
        //            holder.dataBinding.gradientView.setBackground(mPaintDrawable);
        //        } else
        //        {
        //            holder.dataBinding.gradientView.setBackgroundDrawable(mPaintDrawable);
        //        }
        //
        //        // grade
        //        holder.dataBinding.gradeTextView.setText(stay.getGrade().getName(mContext));
        //        holder.dataBinding.gradeTextView.setBackgroundResource(stay.getGrade().getColorResId());
        //
        //        Util.requestImageResize(mContext, holder.dataBinding.imageView, stay.imageUrl);
        //
        //        // SOLD OUT 표시
        //        if (stay.isSoldOut == true)
        //        {
        //            holder.dataBinding.soldoutView.setVisibility(View.VISIBLE);
        //        } else
        //        {
        //            holder.dataBinding.soldoutView.setVisibility(View.GONE);
        //        }
        //
        //        if (DailyTextUtils.isTextEmpty(stay.dBenefitText) == false)
        //        {
        //            holder.dataBinding.dBenefitTextView.setVisibility(View.VISIBLE);
        //            holder.dataBinding.dBenefitTextView.setText(stay.dBenefitText);
        //        } else
        //        {
        //            holder.dataBinding.dBenefitTextView.setVisibility(View.GONE);
        //        }
        //
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
        //            holder.dataBinding.distanceTextView.setText(mContext.getString(R.string.label_distance_km, new DecimalFormat("#.#").format(stay.distance)));
        //        } else
        //        {
        //            holder.dataBinding.dot1View.setVisibility(View.GONE);
        //            holder.dataBinding.distanceTextView.setVisibility(View.GONE);
        //        }
        //
        //        // VR 여부
        //        if (stay.truevr == true && mTrueVREnabled == true)
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
