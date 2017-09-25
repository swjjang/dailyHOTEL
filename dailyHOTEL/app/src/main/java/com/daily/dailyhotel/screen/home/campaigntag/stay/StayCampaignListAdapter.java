package com.daily.dailyhotel.screen.home.campaigntag.stay;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.view.DailyStayCardView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ViewEmptyCampaignTagListBinding;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

/**
 * Created by iseung-won on 2017. 8. 8..
 */

public class StayCampaignListAdapter extends PlaceListAdapter
{
    private int mNights;

    //    View.OnClickListener mOnClickListener;

    OnEventListener mOnEventListener;

    public interface OnEventListener
    {
        void onItemClick(View view);

        void onEmptyChangeDateClick();

        void onEmptyResearchClick();

        void onEmptyCallClick();
    }

    public StayCampaignListAdapter(Context context, ArrayList<PlaceViewItem> arrayList, OnEventListener listener)
    {
        super(context, arrayList);

        mOnEventListener = listener;

        setSortType(Constants.SortType.DEFAULT);
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
            case PlaceViewItem.TYPE_SECTION:
            {
                View view = mInflater.inflate(R.layout.list_row_default_section, parent, false);
                return new SectionViewHolder(view);
            }

            case PlaceViewItem.TYPE_ENTRY:
            {
                DailyStayCardView stayCardView = new DailyStayCardView(mContext);
                stayCardView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                return new StayViewHolder(stayCardView);
            }

            case PlaceViewItem.TYPE_HEADER_VIEW:
            {
                View view = mInflater.inflate(R.layout.list_row_collection_header, parent, false);

                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT//
                    , ScreenUtils.getRatioHeightType16x9(ScreenUtils.getScreenWidth(mContext)) //
                    + ScreenUtils.dpToPx(mContext, 81) - ScreenUtils.dpToPx(mContext, 97));
                view.setLayoutParams(layoutParams);

                return new StayCampaignListAdapter.HeaderViewHolder(view);
            }

            case PlaceViewItem.TYPE_FOOTER_VIEW:
            {
                ViewEmptyCampaignTagListBinding dataBinding = DataBindingUtil.inflate(mInflater, R.layout.view_empty_campaign_tag_list, parent, false);

                return new EmptyViewHolder(dataBinding);
            }

            case PlaceViewItem.TYPE_FOOTER_GUIDE_VIEW:
            {
                View view = mInflater.inflate(R.layout.list_row_footer, parent, false);
                return new StayCampaignListAdapter.FooterGuideViewHolder(view);
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        PlaceViewItem item = getItem(position);

        if (item == null)
        {
            return;
        }

        switch (item.mType)
        {
            case PlaceViewItem.TYPE_ENTRY:
                onBindViewHolder((StayViewHolder) holder, item, position);
                break;

            case PlaceViewItem.TYPE_SECTION:
                onBindViewHolder((SectionViewHolder) holder, item);
                break;

            case PlaceViewItem.TYPE_FOOTER_VIEW:
                onBindViewHolder((EmptyViewHolder) holder, item);
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onBindViewHolder(StayViewHolder holder, PlaceViewItem placeViewItem, int position)
    {
        final Stay stay = placeViewItem.getItem();

        holder.stayCardView.setStickerVisible(false);
        holder.stayCardView.setDeleteVisible(false);
        holder.stayCardView.setWishVisible(false);

        holder.stayCardView.setImage(stay.imageUrl);

        holder.stayCardView.setGradeText(stay.getGrade().getName(mContext));
        holder.stayCardView.setVRVisible(stay.truevr && mTrueVREnabled);
        holder.stayCardView.setReviewText(stay.satisfaction, 0);

        holder.stayCardView.setNewVisible(false);

        holder.stayCardView.setStayNameText(stay.name);

        if (mShowDistanceIgnoreSort == true || getSortType() == Constants.SortType.DISTANCE)
        {
            holder.stayCardView.setDistanceVisible(true);
            holder.stayCardView.setDistanceText(stay.distance);
        } else
        {
            holder.stayCardView.setDistanceVisible(false);
        }

        holder.stayCardView.setAddressText(stay.addressSummary);

        if (stay.availableRooms > 0)
        {
            if (stay.price > 0 && stay.price > stay.discountPrice)
            {
                holder.stayCardView.setPriceText(stay.price > 0 ? 100 * (stay.price - stay.discountPrice) / stay.price : 0, stay.discountPrice, stay.price, null, mNights);
            } else
            {
                holder.stayCardView.setPriceText(0, stay.discountPrice, stay.price, null, mNights);
            }
        } else
        {
            holder.stayCardView.setPriceText(0, 0, 0, null, 0);
        }

        holder.stayCardView.setBenefitText(stay.dBenefitText);

        if (position < getItemCount() - 1 && getItem(position + 1).mType == PlaceViewItem.TYPE_SECTION)
        {
            holder.stayCardView.setDividerVisible(false);
        } else
        {
            holder.stayCardView.setDividerVisible(true);
        }


        //
        //        String strPrice = DailyTextUtils.getPriceFormat(mContext, stay.price, false);
        //        String strDiscount = DailyTextUtils.getPriceFormat(mContext, stay.discountPrice, false);
        //
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
        //
        //        if (stay.price <= 0 || stay.price <= stay.discountPrice)
        //        {
        //            holder.dataBinding.priceTextView.setVisibility(View.INVISIBLE);
        //            holder.dataBinding.priceTextView.setText(null);
        //        } else
        //        {
        //            holder.dataBinding.priceTextView.setVisibility(View.VISIBLE);
        //            holder.dataBinding.priceTextView.setText(strPrice);
        //            holder.dataBinding.priceTextView.setPaintFlags(holder.dataBinding.priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        //        }
        //
        //        // 만족도
        //        if (stay.satisfaction > 0)
        //        {
        //            holder.dataBinding.satisfactionView.setVisibility(View.VISIBLE);
        //            holder.dataBinding.satisfactionView.setText(//
        //                mContext.getResources().getString(R.string.label_list_satisfaction, stay.satisfaction));
        //        } else
        //        {
        //            holder.dataBinding.satisfactionView.setVisibility(View.GONE);
        //        }
        //
        //        // 판매 완료인 경우에는 보여주지 않는다.
        //        if (mNights > 1 && stay.availableRooms > 0)
        //        {
        //            holder.dataBinding.averageTextView.setVisibility(View.VISIBLE);
        //        } else
        //        {
        //            holder.dataBinding.averageTextView.setVisibility(View.GONE);
        //        }
        //
        //        holder.dataBinding.discountPriceTextView.setText(strDiscount);
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
        //        // grade
        //        Stay.Grade grade = stay.getGrade();
        //        holder.dataBinding.gradeTextView.setText(grade.getName(mContext));
        //        holder.dataBinding.gradeTextView.setBackgroundResource(grade.getColorResId());
        //
        //        Util.requestImageResize(mContext, holder.dataBinding.imageView, stay.imageUrl);
        //
        //        // SOLD OUT 표시
        //        holder.dataBinding.soldoutView.setVisibility(View.GONE);
        //
        //        if (stay.availableRooms == 0)
        //        {
        //            holder.dataBinding.priceTextView.setVisibility(View.INVISIBLE);
        //            holder.dataBinding.priceTextView.setText(null);
        //            holder.dataBinding.discountPriceTextView.setText(mContext.getString(R.string.act_hotel_soldout));
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
        //        holder.dataBinding.dot1View.setVisibility(View.GONE);
        //        holder.dataBinding.distanceTextView.setVisibility(View.GONE);
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

    private void onBindViewHolder(StayCampaignListAdapter.EmptyViewHolder holder, PlaceViewItem placeViewItem)
    {
        holder.dataBinding.changeDateView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnEventListener == null)
                {
                    return;
                }

                mOnEventListener.onEmptyChangeDateClick();
            }
        });

        holder.dataBinding.researchView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnEventListener == null)
                {
                    return;
                }

                mOnEventListener.onEmptyResearchClick();
            }
        });

        holder.dataBinding.callTextView.setPaintFlags(holder.dataBinding.callTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.dataBinding.callTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnEventListener == null)
                {
                    return;
                }

                mOnEventListener.onEmptyCallClick();
            }
        });
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder
    {
        public HeaderViewHolder(View itemView)
        {
            super(itemView);
        }
    }

    private class FooterGuideViewHolder extends RecyclerView.ViewHolder
    {
        public FooterGuideViewHolder(View itemView)
        {
            super(itemView);
        }
    }

    private class EmptyViewHolder extends RecyclerView.ViewHolder
    {
        ViewEmptyCampaignTagListBinding dataBinding;

        public EmptyViewHolder(ViewEmptyCampaignTagListBinding dataBinding)
        {
            super(dataBinding.getRoot());

            this.dataBinding = dataBinding;
        }
    }

    private class StayViewHolder extends RecyclerView.ViewHolder
    {
        DailyStayCardView stayCardView;

        public StayViewHolder(DailyStayCardView stayCardView)
        {
            super(stayCardView);

            this.stayCardView = stayCardView;

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mOnEventListener == null)
                    {
                        return;
                    }

                    mOnEventListener.onItemClick(v);
                }
            });

            if (Util.supportPreview(mContext) == true)
            {
                itemView.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        if (mOnLongClickListener == null)
                        {
                            return false;
                        } else
                        {
                            Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(70);

                            return mOnLongClickListener.onLongClick(v);
                        }
                    }
                });
            }
        }
    }
}
