package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetDetail;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.network.model.GourmetDetailParams;
import com.twoheart.dailyhotel.network.model.GourmetProduct;
import com.twoheart.dailyhotel.network.model.ImageInformation;
import com.twoheart.dailyhotel.network.model.PlaceReviewScores;
import com.twoheart.dailyhotel.network.model.Sticker;
import com.twoheart.dailyhotel.place.adapter.PlaceDetailImageViewPagerAdapter;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.layout.PlaceDetailLayout;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyPlaceDetailScrollView;

import java.util.List;

public class GourmetDetailLayout extends PlaceDetailLayout
{
    private SimpleDraweeView mStickerSimpleDraweeView;

    private GourmetDetailItemLayout mGourmetDetailItemLayout;

    public interface OnEventListener extends PlaceDetailLayout.OnEventListener
    {
        void onProductListClick();

        void onProductClick(int index);

        void onReviewClick();

        void onMoreProductListClick();
    }

    public GourmetDetailLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        super.initLayout(view);

        mStickerSimpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.stickerSimpleDraweeView);
    }

    @Override
    protected String getProductTypeTitle()
    {
        return mContext.getString(R.string.act_hotel_search_ticket);
    }

    @Override
    protected View getTitleLayout()
    {
        if (mGourmetDetailItemLayout == null)
        {
            return null;
        }

        return mGourmetDetailItemLayout.getTitleLayout();
    }

    public void setTitleText(String placeName)
    {
        mTransTotalGradeTextView.setVisibility(View.INVISIBLE);

        if (DailyTextUtils.isTextEmpty(placeName) == false)
        {
            mTransPlaceNameTextView.setText(placeName);

            if (VersionUtils.isOverAPI21() == true)
            {
                mTransPlaceNameTextView.setTransitionName(mContext.getString(R.string.transition_place_name));
            }
        }
    }

    public void setDetail(GourmetBookingDay gourmetBookingDay, GourmetDetail gourmetDetail//
        , PlaceReviewScores placeReviewScores, int imagePosition, int dpi)
    {
        if (gourmetBookingDay == null || gourmetDetail == null || gourmetDetail.getGourmetDetailParmas() == null)
        {
            setLineIndicatorVisible(false);
            return;
        }

        mPlaceDetail = gourmetDetail;

        if (mImageAdapter == null)
        {
            mImageAdapter = new PlaceDetailImageViewPagerAdapter(mContext);
        }

        List<ImageInformation> imageInformationList = gourmetDetail.getImageList();
        mImageAdapter.setData(imageInformationList);
        mViewPager.setAdapter(mImageAdapter);

        mDailyLineIndicator.setViewPager(mViewPager);
        setLineIndicatorVisible(imageInformationList.size() > 0);
        setImageInformation((imageInformationList.size() > 0) ? imageInformationList.get(0).description : null);

        mGourmetDetailItemLayout = new GourmetDetailItemLayout(mContext);
        mGourmetDetailItemLayout.setDpi(dpi);
        mGourmetDetailItemLayout.setOnEventListener((GourmetDetailLayout.OnEventListener) mOnEventListener);
        mGourmetDetailItemLayout.setEmptyViewOnTouchListener(mEmptyViewOnTouchListener);
        mGourmetDetailItemLayout.setData(gourmetBookingDay, (GourmetDetail) mPlaceDetail, placeReviewScores);

        mScrollView.removeAllViews();
        mScrollView.addView(mGourmetDetailItemLayout);

        setCurrentImage(imagePosition);

        // SOLD OUT 판단 조건.
        List<GourmetProduct> gourmetProductList = gourmetDetail.getProductList();

        if (gourmetProductList == null || gourmetProductList.size() == 0)
        {
            mBookingTextView.setVisibility(View.GONE);
            mSoldoutTextView.setVisibility(View.VISIBLE);

            setBookingStatus(STATUS_SOLD_OUT);
        } else
        {
            mBookingTextView.setVisibility(View.VISIBLE);
            mBookingTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ((OnEventListener) mOnEventListener).onProductListClick();
                }
            });

            mSoldoutTextView.setVisibility(View.GONE);

            setBookingStatus(STATUS_SELECT_PRODUCT);
        }

        GourmetDetailParams gourmetDetailParams = gourmetDetail.getGourmetDetailParmas();

        if (placeReviewScores != null)
        {
            setTrueReviewCount(placeReviewScores.reviewScoreTotalCount);
        }

        setSticker(gourmetDetailParams.getSticker());
    }

    @Override
    public void setTrueReviewCount(int count)
    {
        if (mGourmetDetailItemLayout != null)
        {
            mGourmetDetailItemLayout.setTrueReviewCount(count);
        }
    }

    @Override
    public void setBookingStatus(int status)
    {
        mBookingStatus = status;

        if (mBookingTextView == null || mSoldoutTextView == null)
        {
            Util.restartApp(mContext);
            return;
        }

        switch (status)
        {
            case STATUS_NONE:
            {
                mBookingTextView.setVisibility(View.VISIBLE);
                mSoldoutTextView.setVisibility(View.GONE);
                //                mWishButtonTextView.setVisibility(View.VISIBLE);
                break;
            }

            case STATUS_SELECT_PRODUCT:
            {
                mBookingTextView.setVisibility(View.VISIBLE);
                mSoldoutTextView.setVisibility(View.GONE);
                //                mWishButtonTextView.setVisibility(View.VISIBLE);

                mBookingTextView.setText(R.string.act_hotel_search_ticket);
                break;
            }

            case STATUS_BOOKING:
            {
                mBookingTextView.setVisibility(View.VISIBLE);
                mSoldoutTextView.setVisibility(View.GONE);
                //                mWishButtonTextView.setVisibility(View.VISIBLE);

                mBookingTextView.setText(R.string.act_hotel_booking);
                break;
            }

            case STATUS_SOLD_OUT:
            {
                mBookingTextView.setVisibility(View.GONE);
                mSoldoutTextView.setVisibility(View.VISIBLE);
                //                mWishButtonTextView.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    @Override
    public DailyPlaceDetailScrollView.OnScrollChangedListener getScrollChangedListener()
    {
        return mOnScrollChangedListener;
    }

    public void scrollProduct()
    {
        if (mScrollView == null || mGourmetDetailItemLayout == null || mGourmetDetailItemLayout.getMoveFirstView() == 0.0f)
        {
            return;
        }

        mScrollView.smoothScrollTo(0, (int) mGourmetDetailItemLayout.getMoveFirstView()//
            - mContext.getResources().getDimensionPixelSize(R.dimen.toolbar_height));
    }

    public boolean isOpenedProductMoreList()
    {
        if (mGourmetDetailItemLayout == null)
        {
            return false;
        }

        return mGourmetDetailItemLayout.isOpenedProductMoreList();
    }

    public void openMoreProductList()
    {
        if (mGourmetDetailItemLayout == null)
        {
            return;
        }

        mGourmetDetailItemLayout.openMoreProductList();
    }

    public void closeMoreProductList()
    {
        if (mGourmetDetailItemLayout == null)
        {
            return;
        }

        scrollProduct();
        mGourmetDetailItemLayout.closeMoreProductList();
    }

    private void setSticker(Sticker sticker)
    {
        if (sticker == null)
        {
            mStickerSimpleDraweeView.setVisibility(View.GONE);
            return;
        }

        String url;
        if (ScreenUtils.getScreenWidth(mContext) <= Sticker.DEFAULT_SCREEN_WIDTH)
        {
            url = sticker.lowResolutionImageUrl;
        } else
        {
            url = sticker.defaultImageUrl;
        }

        if (DailyTextUtils.isTextEmpty(url) == true)
        {
            mStickerSimpleDraweeView.setVisibility(View.GONE);
        } else
        {
            mStickerSimpleDraweeView.setVisibility(View.VISIBLE);
        }

        DraweeController controller = Fresco.newDraweeControllerBuilder().setControllerListener(new BaseControllerListener<ImageInfo>()
        {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable)
            {
                ViewGroup.LayoutParams layoutParams = mStickerSimpleDraweeView.getLayoutParams();

                int screenWidth = ScreenUtils.getScreenWidth(mContext);
                if (screenWidth > Sticker.DEFAULT_SCREEN_WIDTH && screenWidth < Sticker.LARGE_SCREEN_WIDTH)
                {
                    layoutParams.width = (int) (Sticker.MEDIUM_RATE * imageInfo.getWidth());
                    layoutParams.height = (int) (Sticker.MEDIUM_RATE * imageInfo.getHeight());
                } else
                {
                    layoutParams.width = imageInfo.getWidth();
                    layoutParams.height = imageInfo.getHeight();
                }

                mStickerSimpleDraweeView.setLayoutParams(layoutParams);
            }
        }).setUri(Uri.parse(url)).build();

        mStickerSimpleDraweeView.setController(controller);
    }

    private DailyPlaceDetailScrollView.OnScrollChangedListener mOnScrollChangedListener = new DailyPlaceDetailScrollView.OnScrollChangedListener()
    {
        @Override
        public void onScrollChanged(ScrollView scrollView, int l, int t, int oldl, int oldt)
        {
            if (getBookingStatus() == STATUS_BOOKING)
            {
                return;
            }

            final int TOOLBAR_HEIGHT = mContext.getResources().getDimensionPixelSize(R.dimen.toolbar_height);

            int viewpagerHeight = getImageLayoutHeight(mContext);

            if (t >= viewpagerHeight - TOOLBAR_HEIGHT)
            {
                ((OnEventListener) mOnEventListener).showActionBar(true);
            } else
            {
                ((OnEventListener) mOnEventListener).hideActionBar(true);
            }

            if (mGourmetDetailItemLayout != null)
            {
                int firstProductIndex = mGourmetDetailItemLayout.getFirstProductIndex();
                int lastProductIndex = mGourmetDetailItemLayout.getLastProductIndex();

                if (firstProductIndex < 0 || lastProductIndex < 0 || firstProductIndex > lastProductIndex)
                {
                    return;
                }

                int scrollY = scrollView.getScrollY();

                // 겹치지 않은 경우
                if (scrollY == 0 || scrollY > mGourmetDetailItemLayout.getChildAt(lastProductIndex).getBottom()//
                    || scrollY + scrollView.getHeight() < mGourmetDetailItemLayout.getChildAt(firstProductIndex).getY())
                {
                    mBottomLayout.setVisibility(View.VISIBLE);
                } else
                {
                    mBottomLayout.setVisibility(View.GONE);
                }
            }
        }
    };
}