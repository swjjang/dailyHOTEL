package com.twoheart.dailyhotel.screen.hotel.list;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.daily.dailyhotel.view.DailyStayMapCardView;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.place.adapter.PlaceMapViewPagerAdapter;

public class StayMapViewPagerAdapter extends PlaceMapViewPagerAdapter
{
    private int mNights;

    public StayMapViewPagerAdapter(Context context)
    {
        super(context);
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
    public Object instantiateItem(ViewGroup container, int position)
    {
        if (mPlaceViewItemList == null || mPlaceViewItemList.size() < position)
        {
            return null;
        }

        //        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //        View view = layoutInflater.inflate(R.layout.viewpager_column_hotel, null);

        DailyStayMapCardView stayMapCardView = new DailyStayMapCardView(mContext);
        stayMapCardView.setLayoutParams(new ViewPager.LayoutParams());

        PlaceViewItem item = mPlaceViewItemList.get(position);

        makeLayout(stayMapCardView, item.getItem());

        container.addView(stayMapCardView, 0);

        return stayMapCardView;
    }

    private void makeLayout(final DailyStayMapCardView stayMapCardView, final Stay stay)
    {
        if (stayMapCardView == null || stay == null)
        {
            return;
        }

        stayMapCardView.setAddressText(stay.addressSummary);
        stayMapCardView.setStayNameText(stay.name);
        stayMapCardView.setBenefitText(stay.dBenefitText);

        if (stay.isSoldOut == true)
        {
            stayMapCardView.setPriceText(0, 0, 0, null, false);
        } else
        {
            stayMapCardView.setPriceText(stay.discountRate, stay.discountPrice, stay.price, stay.couponDiscountText, mNights > 1);
        }

        stayMapCardView.setReviewText(stay.satisfaction);
        stayMapCardView.setGradeText(stay.getGrade().getName(mContext));
        stayMapCardView.setImage(stay.imageUrl);
        stayMapCardView.setStickerVisible(mRewardEnabled && stay.provideRewardSticker);

        stayMapCardView.setOnCloseClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnPlaceMapViewPagerAdapterListener != null)
                {
                    mOnPlaceMapViewPagerAdapterListener.onCloseClick();
                }
            }
        });

        stayMapCardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnPlaceMapViewPagerAdapterListener != null)
                {
                    mOnPlaceMapViewPagerAdapterListener.onInformationClick(stayMapCardView, stay);
                }
            }
        });
    }

    //    private void makeLayout(final View view, final Stay stay)
    //    {
    //        com.facebook.drawee.view.SimpleDraweeView hotelImageView = view.findViewById(R.id.imageView);
    //        TextView name = view.findViewById(R.id.nameTextView);
    //        TextView priceTextView = view.findViewById(R.id.priceTextView);
    //        TextView satisfactionView = view.findViewById(R.id.satisfactionView);
    //        View averageTextView = view.findViewById(R.id.averageTextView);
    //        TextView discountTextView = view.findViewById(R.id.discountPriceTextView);
    //        TextView addressTextView = view.findViewById(R.id.addressTextView);
    //        TextView grade = view.findViewById(R.id.gradeTextView);
    //        View closeView = view.findViewById(R.id.closeImageView);
    //        View dBenefitLayout = view.findViewById(R.id.dBenefitLayout);
    //        TextView dBenefitTextView = view.findViewById(R.id.dBenefitTextView);
    //        View stickerImageView = view.findViewById(R.id.stickerImageView);
    //
    //        addressTextView.setText(stay.addressSummary);
    //        name.setText(stay.name);
    //
    //        // D.benefit
    //        if (DailyTextUtils.isTextEmpty(stay.dBenefitText) == false)
    //        {
    //            dBenefitLayout.setVisibility(View.VISIBLE);
    //            dBenefitTextView.setText(stay.dBenefitText);
    //        } else
    //        {
    //            dBenefitLayout.setVisibility(View.GONE);
    //        }
    //
    //        if (stay.price <= 0)
    //        {
    //            priceTextView.setVisibility(View.INVISIBLE);
    //
    //            priceTextView.setText(null);
    //        } else
    //        {
    //            priceTextView.setVisibility(View.VISIBLE);
    //
    //            priceTextView.setText(DailyTextUtils.getPriceFormat(mContext, stay.price, false));
    //            priceTextView.setPaintFlags(priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    //        }
    //
    //        // 만족도
    //        if (stay.satisfaction > 0)
    //        {
    //            satisfactionView.setVisibility(View.VISIBLE);
    //            satisfactionView.setText(//
    //                mContext.getResources().getString(R.string.label_list_satisfaction, stay.satisfaction));
    //        } else
    //        {
    //            satisfactionView.setVisibility(View.GONE);
    //        }
    //
    //        if (mNights > 1)
    //        {
    //            averageTextView.setVisibility(View.VISIBLE);
    //        } else
    //        {
    //            averageTextView.setVisibility(View.GONE);
    //        }
    //
    //        discountTextView.setText(DailyTextUtils.getPriceFormat(mContext, stay.discountPrice, false));
    //
    //        name.setSelected(true); // Android TextView marquee bug
    //
    //        // grade
    //        grade.setText(stay.getGrade().getName(mContext));
    //
    //        hotelImageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);
    //        Util.requestImageResize(mContext, hotelImageView, stay.imageUrl);
    //
    //        // Reward 스티커
    //        stickerImageView.setVisibility((mRewardEnabled && stay.provideRewardSticker) ? View.VISIBLE : View.GONE);
    //
    //        closeView.setOnClickListener(new View.OnClickListener()
    //        {
    //            @Override
    //            public void onClick(View v)
    //            {
    //                if (mOnPlaceMapViewPagerAdapterListener != null)
    //                {
    //                    mOnPlaceMapViewPagerAdapterListener.onCloseClick();
    //                }
    //            }
    //        });
    //
    //        hotelImageView.setOnClickListener(new View.OnClickListener()
    //        {
    //            @Override
    //            public void onClick(View v)
    //            {
    //                if (mOnPlaceMapViewPagerAdapterListener != null)
    //                {
    //                    mOnPlaceMapViewPagerAdapterListener.onInformationClick(view, stay);
    //                }
    //            }
    //        });
    //    }
}
