package com.twoheart.dailyhotel.screen.gourmet.preview;

import android.content.Context;
import android.view.View;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetDetail;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.network.model.GourmetDetailParams;
import com.twoheart.dailyhotel.network.model.GourmetProduct;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.layout.PlacePreviewLayout;

/**
 * 호텔 상세 정보 화면
 *
 * @author sheldon
 */
public class GourmetPreviewLayout extends PlacePreviewLayout implements View.OnClickListener
{
    public GourmetPreviewLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    public void setCategory(String grade, String subGrade)
    {
        if (DailyTextUtils.isTextEmpty(grade) == false && DailyTextUtils.isTextEmpty(subGrade) == false)
        {
            mCategoryTextView.setText(grade);
            mDotImageView.setVectorImageResource(R.drawable.vector_ic_gourmet_category_arrow);
            mDotImageView.setPadding(ScreenUtils.dpToPx(mContext, 5), 0, ScreenUtils.dpToPx(mContext, 5), 0);

            mSubCategoryTextView.setText(subGrade);
        } else if (DailyTextUtils.isTextEmpty(grade) == false)
        {
            mCategoryTextView.setText(grade);
            mDotImageView.setVisibility(View.GONE);
            mSubCategoryTextView.setVisibility(View.GONE);
        } else if (DailyTextUtils.isTextEmpty(subGrade) == false)
        {
            mCategoryTextView.setVisibility(View.GONE);
            mDotImageView.setVisibility(View.GONE);
            mSubCategoryTextView.setText(subGrade);
        } else
        {
            mCategoryTextView.setVisibility(View.GONE);
            mDotImageView.setVisibility(View.GONE);
            mSubCategoryTextView.setVisibility(View.GONE);
        }
    }

    protected void updateLayout(GourmetBookingDay gourmetBookingDay, GourmetDetail gourmetDetail, int reviewCount, boolean changedPrice, boolean soldOut)
    {
        if (gourmetBookingDay == null || gourmetDetail == null)
        {
            return;
        }

        GourmetDetailParams gourmetDetailParams = gourmetDetail.getGourmetDetailParams();

        if (gourmetDetailParams == null)
        {
            return;
        }

        updateImageLayout(gourmetDetailParams.getImageList());

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
            // N개의 메뉴타입
            mProductCountTextView.setText(mContext.getString(R.string.label_detail_gourmet_product_count, gourmetDetailParams.getProductList().size()));
            mStayAverageView.setVisibility(View.GONE);

            int minPrice = Integer.MAX_VALUE;
            int maxPrice = Integer.MIN_VALUE;

            for (GourmetProduct gourmetProduct : gourmetDetailParams.getProductList())
            {
                if (minPrice > gourmetProduct.discountPrice)
                {
                    minPrice = gourmetProduct.discountPrice;
                }

                if (maxPrice < gourmetProduct.discountPrice)
                {
                    maxPrice = gourmetProduct.discountPrice;
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

        updateMoreInformation(reviewCount, gourmetDetailParams.wishCount);
        updateBottomLayout(gourmetDetailParams.myWish);
    }
}