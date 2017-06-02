package com.twoheart.dailyhotel.screen.mydaily.wishlist;

import android.content.Context;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.text.DecimalFormat;
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
                View view = mInflater.inflate(R.layout.list_row_hotel, parent, false);

                return new StayWishListViewHolder(view);
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
                onBindViewHolder((StayWishListViewHolder) viewHolder, item, position);
                break;

            case PlaceViewItem.TYPE_FOOTER_VIEW:
                break;
        }
    }

    private void onBindViewHolder(StayWishListViewHolder holder, PlaceViewItem placeViewItem, int position)
    {
        final Stay stay = placeViewItem.getItem();

        String address = stay.addressSummary;

        int barIndex = address.indexOf('|');
        if (barIndex >= 0)
        {
            address = address.replace(" | ", "ㅣ");
        } else if (address.indexOf('l') >= 0)
        {
            address = address.replace(" l ", "ㅣ");
        }

        holder.hotelAddressView.setText(address);
        holder.hotelNameView.setText(stay.name);
        holder.hotelPriceView.setVisibility(View.INVISIBLE);
        holder.hotelPriceView.setText(null);

        // 만족도
        if (stay.satisfaction > 0)
        {
            holder.satisfactionView.setVisibility(View.VISIBLE);
            holder.satisfactionView.setText(//
                mContext.getResources().getString(R.string.label_list_satisfaction, stay.satisfaction));
        } else
        {
            holder.satisfactionView.setVisibility(View.GONE);
        }

        holder.averageView.setVisibility(View.GONE);
        holder.hotelDiscountView.setVisibility(View.GONE);

        holder.hotelNameView.setSelected(true); // Android TextView marquee bug

        if (VersionUtils.isOverAPI16() == true)
        {
            holder.gradientView.setBackground(mPaintDrawable);
        } else
        {
            holder.gradientView.setBackgroundDrawable(mPaintDrawable);
        }

        // grade
        holder.hotelGradeView.setText(stay.getGrade().getName(mContext));
        holder.hotelGradeView.setBackgroundResource(stay.getGrade().getColorResId());

        Util.requestImageResize(mContext, holder.hotelImageView, stay.imageUrl);

        // SOLD OUT 표시
        if (stay.isSoldOut == true)
        {
            holder.hotelSoldOutView.setVisibility(View.VISIBLE);
        } else
        {
            holder.hotelSoldOutView.setVisibility(View.GONE);
        }

        if (DailyTextUtils.isTextEmpty(stay.dBenefitText) == false)
        {
            holder.dBenefitTextView.setVisibility(View.VISIBLE);
            holder.dBenefitTextView.setText(stay.dBenefitText);
        } else
        {
            holder.dBenefitTextView.setVisibility(View.GONE);
        }

        if (mShowDistanceIgnoreSort == true || getSortType() == Constants.SortType.DISTANCE)
        {
            if (holder.satisfactionView.getVisibility() == View.VISIBLE || holder.trueVRView.getVisibility() == View.VISIBLE)
            {
                holder.dot1View.setVisibility(View.VISIBLE);
            } else
            {
                holder.dot1View.setVisibility(View.GONE);
            }

            holder.distanceTextView.setVisibility(View.VISIBLE);
            holder.distanceTextView.setText(mContext.getString(R.string.label_distance_km, new DecimalFormat("#.#").format(stay.distance)));
        } else
        {
            holder.dot1View.setVisibility(View.GONE);
            holder.distanceTextView.setVisibility(View.GONE);
        }

        // VR 여부
        if (stay.truevr == true && mTrueVREnabled == true)
        {
            if (holder.satisfactionView.getVisibility() == View.VISIBLE)
            {
                holder.dot2View.setVisibility(View.VISIBLE);
            } else
            {
                holder.dot2View.setVisibility(View.GONE);
            }

            holder.trueVRView.setVisibility(View.VISIBLE);
        } else
        {
            holder.dot2View.setVisibility(View.GONE);
            holder.trueVRView.setVisibility(View.GONE);
        }

        holder.deleteView.setVisibility(View.VISIBLE);
        holder.deleteView.setTag(position);
        holder.deleteView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mListener != null)
                {
                    mListener.onItemRemoveClick(v, (Integer) v.getTag());
                }
            }
        });

        if (holder.satisfactionView.getVisibility() == View.GONE//
            && holder.trueVRView.getVisibility() == View.GONE//
            && holder.distanceTextView.getVisibility() == View.GONE)
        {
            holder.informationLayout.setVisibility(View.GONE);
        } else
        {
            holder.informationLayout.setVisibility(View.VISIBLE);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder
    {
        public FooterViewHolder(View itemView)
        {
            super(itemView);
        }
    }

    private class StayWishListViewHolder extends RecyclerView.ViewHolder
    {
        View gradientView;
        com.facebook.drawee.view.SimpleDraweeView hotelImageView;
        TextView dBenefitTextView;
        TextView hotelGradeView;
        TextView hotelNameView;
        View informationLayout;
        TextView satisfactionView;
        View dot1View;
        TextView distanceTextView;
        View dot2View;
        View trueVRView;
        TextView hotelAddressView;
        TextView hotelPriceView;
        View averageView;
        TextView hotelDiscountView;
        View hotelSoldOutView;
        View deleteView;

        public StayWishListViewHolder(View itemView)
        {
            super(itemView);

            gradientView = itemView.findViewById(R.id.gradientView);
            hotelImageView = (com.facebook.drawee.view.SimpleDraweeView) itemView.findViewById(R.id.imageView);
            hotelNameView = (TextView) itemView.findViewById(R.id.nameTextView);
            hotelPriceView = (TextView) itemView.findViewById(R.id.priceTextView);
            satisfactionView = (TextView) itemView.findViewById(R.id.satisfactionView);
            hotelDiscountView = (TextView) itemView.findViewById(R.id.discountPriceTextView);
            hotelSoldOutView = itemView.findViewById(R.id.soldoutView);
            hotelAddressView = (TextView) itemView.findViewById(R.id.addressTextView);
            hotelGradeView = (TextView) itemView.findViewById(R.id.gradeTextView);
            dBenefitTextView = (TextView) itemView.findViewById(R.id.dBenefitTextView);
            averageView = itemView.findViewById(R.id.averageTextView);
            distanceTextView = (TextView) itemView.findViewById(R.id.distanceTextView);
            deleteView = itemView.findViewById(R.id.deleteView);
            informationLayout = itemView.findViewById(R.id.informationLayout);
            trueVRView = itemView.findViewById(R.id.trueVRView);
            dot1View = itemView.findViewById(R.id.dot1View);
            dot2View = itemView.findViewById(R.id.dot2View);

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
        }
    }
}
