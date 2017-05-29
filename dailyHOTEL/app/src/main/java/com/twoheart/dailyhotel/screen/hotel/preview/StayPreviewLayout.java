package com.twoheart.dailyhotel.screen.hotel.preview;

import android.content.Context;
import android.view.View;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayDetail;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.StayDetailParams;
import com.twoheart.dailyhotel.network.model.StayProduct;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.layout.PlacePreviewLayout;

/**
 * 호텔 상세 정보 화면
 *
 * @author sheldon
 */
public class StayPreviewLayout extends PlacePreviewLayout implements View.OnClickListener
{
    public StayPreviewLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    public void setGrade(Stay.Grade grade)
    {
        if (grade == null)
        {
            mPlaceGradeTextView.setVisibility(View.INVISIBLE);
            mPlaceSubGradeTextView.setVisibility(View.GONE);
            return;
        }

        // 등급
        mPlaceGradeTextView.setText(grade.getName(mContext));
        mPlaceGradeTextView.setBackgroundResource(grade.getColorResId());
        mPlaceSubGradeTextView.setVisibility(View.GONE);
    }

    protected void updateLayout(StayBookingDay stayBookingDay, StayDetail stayDetail, int reviewCount, boolean changedPrice, boolean soldOut)
    {
        if (stayBookingDay == null || stayDetail == null)
        {
            return;
        }

        StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

        if (stayDetailParams == null)
        {
            return;
        }

        updateImageLayout(stayDetailParams.getImageList());

        if (soldOut == true)
        {
            mBookingTextView.setText(R.string.label_booking_view_detail);
        } else
        {
            mBookingTextView.setText(R.string.label_preview_booking);
        }

        // 가격
        if (soldOut == true || changedPrice == true)
        {
            mProductCountTextView.setText(R.string.message_preview_changed_price);

            mPriceTextView.setVisibility(View.GONE);
            mStayAverageView.setVisibility(View.GONE);
        } else
        {
            // N개의 객실타입
            mProductCountTextView.setText(mContext.getString(R.string.label_detail_stay_product_count, stayDetailParams.getProductList().size()));

            try
            {
                if (stayBookingDay.getNights() > 1)
                {
                    mStayAverageView.setVisibility(View.VISIBLE);
                } else
                {
                    mStayAverageView.setVisibility(View.GONE);
                }
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }

            int minPrice = Integer.MAX_VALUE;
            int maxPrice = Integer.MIN_VALUE;

            for (StayProduct stayProduct : stayDetailParams.getProductList())
            {
                if (minPrice > stayProduct.averageDiscount)
                {
                    minPrice = stayProduct.averageDiscount;
                }

                if (maxPrice < stayProduct.averageDiscount)
                {
                    maxPrice = stayProduct.averageDiscount;
                }
            }

            String priceFormat;

            if (minPrice == maxPrice)
            {
                priceFormat = DailyTextUtils.getPriceFormat(mContext, maxPrice, false);
            } else
            {
                priceFormat = DailyTextUtils.getPriceFormat(mContext, minPrice, false) + " ~ " + DailyTextUtils.getPriceFormat(mContext, maxPrice, false);
            }

            mPriceTextView.setText(priceFormat);
        }

        updateMoreInformation(reviewCount, stayDetailParams.wishCount);
        updateBottomLayout(stayDetailParams.myWish);
    }
}