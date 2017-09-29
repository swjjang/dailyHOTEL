package com.twoheart.dailyhotel.screen.home.collection;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.view.DailyStayCardView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutSectionDataBinding;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.RecommendationStay;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

public class CollectionStayAdapter extends PlaceListAdapter
{
    private boolean mIsUsedMultiTransition;
    private int mNights;

    View.OnClickListener mOnClickListener;

    public CollectionStayAdapter(Context context, ArrayList<PlaceViewItem> arrayList, View.OnClickListener listener)
    {
        super(context, arrayList);

        mOnClickListener = listener;

        setSortType(Constants.SortType.DEFAULT);
    }

    public void setUsedMultiTransition(boolean isUsedMultiTransition)
    {
        mIsUsedMultiTransition = isUsedMultiTransition;
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
                LayoutSectionDataBinding viewDataBinding = DataBindingUtil.inflate(mInflater, R.layout.layout_section_data, parent, false);

                return new SectionViewHolder(viewDataBinding);
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
                    , ScreenUtils.getRatioHeightType16x9(ScreenUtils.getScreenWidth(mContext)) + ScreenUtils.dpToPx(mContext, 81) - ScreenUtils.dpToPx(mContext, 97));
                view.setLayoutParams(layoutParams);

                return new BaseViewHolder(view);
            }

            case PlaceViewItem.TYPE_EMPTY_VIEW:
            {
                View view = mInflater.inflate(R.layout.view_empty_stay_collection, parent, false);

                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT//
                    , ScreenUtils.getScreenHeight(mContext) - ScreenUtils.dpToPx(mContext, 97) - ScreenUtils.getRatioHeightType16x9(ScreenUtils.getScreenWidth(mContext)) + ScreenUtils.dpToPx(mContext, 81) - ScreenUtils.dpToPx(mContext, 97));
                view.setLayoutParams(layoutParams);

                return new BaseViewHolder(view);
            }

            case PlaceViewItem.TYPE_FOOTER_VIEW:
            {
                View view = mInflater.inflate(R.layout.list_row_users_place_footer, parent, false);
                return new BaseViewHolder(view);
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
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onBindViewHolder(StayViewHolder holder, PlaceViewItem placeViewItem, int position)
    {
        final RecommendationStay recommendationStay = placeViewItem.getItem();

        holder.stayCardView.setStickerVisible(false);
        holder.stayCardView.setDeleteVisible(false);
        holder.stayCardView.setWishVisible(false);

        holder.stayCardView.setImage(recommendationStay.imageUrl);

        holder.stayCardView.setGradeText(Stay.Grade.valueOf(recommendationStay.grade).getName(mContext));
        holder.stayCardView.setVRVisible(recommendationStay.truevr && mTrueVREnabled);
        holder.stayCardView.setReviewText(recommendationStay.rating, 0);

        holder.stayCardView.setNewVisible(false);

        holder.stayCardView.setStayNameText(recommendationStay.name);

        if (mShowDistanceIgnoreSort == true || getSortType() == Constants.SortType.DISTANCE)
        {
            holder.stayCardView.setDistanceVisible(true);
            holder.stayCardView.setDistanceText(recommendationStay.distance);
        } else
        {
            holder.stayCardView.setDistanceVisible(false);
        }

        holder.stayCardView.setAddressText(recommendationStay.addrSummary);

        if (recommendationStay.availableRooms > 0)
        {
            holder.stayCardView.setPriceText(0, recommendationStay.discount, recommendationStay.price, null, mNights);
        } else
        {
            holder.stayCardView.setPriceText(0, 0, 0, null, 0);
        }

        holder.stayCardView.setBenefitText(recommendationStay.benefit);

        // 최상위에는 빈뷰이가 1번째가 첫번째다.
        if (position == 1)
        {
            holder.stayCardView.setDividerVisible(false);
        } else
        {
            holder.stayCardView.setDividerVisible(true);
        }


        //
        //
        //        String strPrice = DailyTextUtils.getPriceFormat(mContext, recommendationStay.price, false);
        //        String strDiscount = DailyTextUtils.getPriceFormat(mContext, recommendationStay.discount, false);
        //
        //        String address = recommendationStay.addrSummary;
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
        //        holder.dataBinding.nameTextView.setText(recommendationStay.name);
        //
        //        if (recommendationStay.price <= 0 || recommendationStay.price <= recommendationStay.discount)
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
        //        if (recommendationStay.rating > 0)
        //        {
        //            holder.dataBinding.satisfactionView.setVisibility(View.VISIBLE);
        //            holder.dataBinding.satisfactionView.setText(//
        //                mContext.getResources().getString(R.string.label_list_satisfaction, recommendationStay.rating));
        //        } else
        //        {
        //            holder.dataBinding.satisfactionView.setVisibility(View.GONE);
        //        }
        //
        //        // 판매 완료인 경우에는 보여주지 않는다.
        //        if (mNights > 1 && recommendationStay.availableRooms > 0)
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
        //        Stay.Grade grade = Stay.Grade.valueOf(recommendationStay.grade);
        //        holder.dataBinding.gradeTextView.setText(grade.getName(mContext));
        //        holder.dataBinding.gradeTextView.setBackgroundResource(grade.getColorResId());
        //
        //        if (mIsUsedMultiTransition == true && VersionUtils.isOverAPI21() == true)
        //        {
        //            holder.dataBinding.imageView.setTransitionName(null);
        //        }
        //
        //        Util.requestImageResize(mContext, holder.dataBinding.imageView, recommendationStay.imageUrl);
        //
        //        // SOLD OUT 표시
        //        holder.dataBinding.soldoutView.setVisibility(View.GONE);
        //
        //        if (recommendationStay.availableRooms == 0)
        //        {
        //            holder.dataBinding.priceTextView.setVisibility(View.INVISIBLE);
        //            holder.dataBinding.priceTextView.setText(null);
        //            holder.dataBinding.discountPriceTextView.setText(mContext.getString(R.string.act_hotel_soldout));
        //        }
        //
        //        if (DailyTextUtils.isTextEmpty(recommendationStay.benefit) == false)
        //        {
        //            holder.dataBinding.dBenefitTextView.setVisibility(View.VISIBLE);
        //            holder.dataBinding.dBenefitTextView.setText(recommendationStay.benefit);
        //        } else
        //        {
        //            holder.dataBinding.dBenefitTextView.setVisibility(View.GONE);
        //        }
        //
        //        //        if (mShowDistanceIgnoreSort == true || getSortType() == Constants.SortType.DISTANCE)
        //        //        {
        //        //            holder.dataBinding.distanceTextView.setVisibility(View.VISIBLE);
        //        //            holder.dataBinding.distanceTextView.setText(mContext.getString(R.string.label_distance_km, new DecimalFormat("#.#").format(stay.distance)));
        //        //        } else
        //        //        {
        //        holder.dataBinding.dot1View.setVisibility(View.GONE);
        //        holder.dataBinding.distanceTextView.setVisibility(View.GONE);
        //        //        }
        //
        //        // VR 여부
        //        if (recommendationStay.truevr == true && mTrueVREnabled == true)
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

    private class StayViewHolder extends RecyclerView.ViewHolder
    {
        DailyStayCardView stayCardView;

        public StayViewHolder(DailyStayCardView stayCardView)
        {
            super(stayCardView);

            this.stayCardView = stayCardView;

            itemView.setOnClickListener(mOnClickListener);

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
