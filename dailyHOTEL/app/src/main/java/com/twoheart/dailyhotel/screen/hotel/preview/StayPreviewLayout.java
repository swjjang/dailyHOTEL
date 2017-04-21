package com.twoheart.dailyhotel.screen.hotel.preview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayDetail;
import com.twoheart.dailyhotel.network.model.StayDetailParams;
import com.twoheart.dailyhotel.network.model.StayProduct;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

/**
 * 호텔 상세 정보 화면
 *
 * @author sheldon
 */
public class StayPreviewLayout extends BaseLayout
{
    private TextView mPlaceGradeTextView;
    private TextView mPlaceNameTextView;
    private View mImageLayout;
    private TextView mProductCountTextView;
    private TextView mPriceTextView;
    private View mPopupLayout, mBottomBarLayout;
    private View mMoreInformationLayout;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onWishClick();

        void onKakaoClick();

        void onMapClick();

        void onStayDetailClick();
    }

    public StayPreviewLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        mPopupLayout = view.findViewById(R.id.popupLayout);

        mPopupLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onStayDetailClick();
            }
        });

        ViewGroup.LayoutParams layoutParams = mPopupLayout.getLayoutParams();

        if (ScreenUtils.isTabletDevice((Activity) mContext) == false)
        {
            layoutParams.width = ScreenUtils.getScreenWidth(mContext) * 13 / 15;
        } else
        {
            layoutParams.width = ScreenUtils.getScreenWidth(mContext) * 10 / 15;
        }

        mPopupLayout.setLayoutParams(layoutParams);

        mPlaceGradeTextView = (TextView) view.findViewById(R.id.placeGradeTextView);
        mPlaceNameTextView = (TextView) view.findViewById(R.id.placeNameTextView);

        mImageLayout = view.findViewById(R.id.imageLayout);

        // 이미지 연동
        SimpleDraweeView simpleDraweeView01 = (SimpleDraweeView) mImageLayout.findViewById(R.id.simpleDraweeView01);
        SimpleDraweeView simpleDraweeView02 = (SimpleDraweeView) mImageLayout.findViewById(R.id.simpleDraweeView02);
        SimpleDraweeView simpleDraweeView03 = (SimpleDraweeView) mImageLayout.findViewById(R.id.simpleDraweeView03);
        SimpleDraweeView simpleDraweeView04 = (SimpleDraweeView) mImageLayout.findViewById(R.id.simpleDraweeView04);

        simpleDraweeView01.post(new Runnable()
        {
            @Override
            public void run()
            {
                int imageHeight = ScreenUtils.getRatioHeightType4x3(simpleDraweeView01.getWidth());
                ViewGroup.LayoutParams layoutParams = simpleDraweeView01.getLayoutParams();
                layoutParams.height = imageHeight;
                simpleDraweeView01.setLayoutParams(layoutParams);
            }
        });

        simpleDraweeView02.post(new Runnable()
        {
            @Override
            public void run()
            {
                int imageHeight = ScreenUtils.getRatioHeightType4x3(simpleDraweeView02.getWidth());
                ViewGroup.LayoutParams layoutParams = simpleDraweeView02.getLayoutParams();
                layoutParams.height = imageHeight;
                simpleDraweeView02.setLayoutParams(layoutParams);
            }
        });

        simpleDraweeView03.post(new Runnable()
        {
            @Override
            public void run()
            {
                int imageHeight = ScreenUtils.getRatioHeightType4x3(simpleDraweeView03.getWidth());
                ViewGroup.LayoutParams layoutParams = simpleDraweeView03.getLayoutParams();
                layoutParams.height = imageHeight;
                simpleDraweeView03.setLayoutParams(layoutParams);
            }
        });

        simpleDraweeView04.post(new Runnable()
        {
            @Override
            public void run()
            {
                int imageHeight = ScreenUtils.getRatioHeightType4x3(simpleDraweeView04.getWidth());
                ViewGroup.LayoutParams layoutParams = simpleDraweeView04.getLayoutParams();
                layoutParams.height = imageHeight;
                simpleDraweeView04.setLayoutParams(layoutParams);
            }
        });

        mProductCountTextView = (TextView) view.findViewById(R.id.productCountTextView);
        mPriceTextView = (TextView) view.findViewById(R.id.priceTextView);

        mBottomBarLayout = view.findViewById(R.id.bottomBarLayout);
        mMoreInformationLayout = view.findViewById(R.id.moreInformationLayout);

        View closeView = view.findViewById(R.id.closeView);
        closeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.finish();
            }
        });
    }

    protected void setGrade(Stay.Grade grade)
    {
        if (grade == null)
        {
            return;
        }

        // 등급
        mPlaceGradeTextView.setText(grade.getName(mContext));
        mPlaceGradeTextView.setBackgroundResource(grade.getColorResId());
    }

    protected void setPlaceName(String placeName)
    {
        // 이름
        mPlaceNameTextView.setText(placeName);
    }

    protected void updateLayout(StayDetail stayDetail, int reviewCount, boolean changedPrice)
    {
        if (stayDetail == null)
        {
            return;
        }

        StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

        if (stayDetailParams == null)
        {
            return;
        }

        // 이미지 연동
        SimpleDraweeView[] simpleDraweeViews = new SimpleDraweeView[4];
        simpleDraweeViews[0] = (SimpleDraweeView) mImageLayout.findViewById(R.id.simpleDraweeView01);
        simpleDraweeViews[1] = (SimpleDraweeView) mImageLayout.findViewById(R.id.simpleDraweeView02);
        simpleDraweeViews[2] = (SimpleDraweeView) mImageLayout.findViewById(R.id.simpleDraweeView03);
        simpleDraweeViews[3] = (SimpleDraweeView) mImageLayout.findViewById(R.id.simpleDraweeView04);

        simpleDraweeViews[0].getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);
        simpleDraweeViews[1].getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);
        simpleDraweeViews[2].getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);
        simpleDraweeViews[3].getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

        int size = Math.min(stayDetailParams.getImageList().size(), simpleDraweeViews.length);

        for (int i = 0; i < size; i++)
        {
            simpleDraweeViews[i].setImageURI(stayDetailParams.getImageList().get(i).getImageUrl());
        }

        // 가격
        if (changedPrice == true)
        {
            mProductCountTextView.setText(R.string.message_preview_changed_price);

            mPriceTextView.setVisibility(View.GONE);
        } else
        {
            // N개의 객실타입
            mProductCountTextView.setText(mContext.getString(R.string.label_detail_stay_product_count, stayDetailParams.getProductList().size()));

            mPriceTextView.setVisibility(View.VISIBLE);

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

        // 추가 메뉴
        if (reviewCount == 0 && stayDetailParams.wishCount == 0)
        {
            mMoreInformationLayout.setVisibility(View.GONE);
        } else
        {
            mMoreInformationLayout.setVisibility(View.VISIBLE);

            TextView trueReviewCountTextView = (TextView) mMoreInformationLayout.findViewById(R.id.trueReviewCountTextView);
            TextView wishCountTextView = (TextView) mMoreInformationLayout.findViewById(R.id.wishCountTextView);
            View dotView = mMoreInformationLayout.findViewById(R.id.dotView);

            if (reviewCount > 0 && stayDetailParams.wishCount > 0)
            {
                dotView.setVisibility(View.VISIBLE);

                setTrueReviewCount(trueReviewCountTextView, reviewCount);
                setWishCount(wishCountTextView, stayDetailParams.wishCount);
            } else if (reviewCount > 0)
            {
                dotView.setVisibility(View.GONE);
                wishCountTextView.setVisibility(View.GONE);

                setTrueReviewCount(trueReviewCountTextView, reviewCount);
            } else if (stayDetailParams.wishCount > 0)
            {
                dotView.setVisibility(View.GONE);
                trueReviewCountTextView.setVisibility(View.GONE);

                setWishCount(wishCountTextView, stayDetailParams.wishCount);
            }
        }
    }

    private void setTrueReviewCount(TextView textView, int count)
    {
        if (textView == null || count == 0)
        {
            return;
        }

        String trueReviewCount = mContext.getString(R.string.label_detail_truereview_count, count);

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(trueReviewCount);
        spannableStringBuilder.setSpan( //
            new CustomFontTypefaceSpan(FontManager.getInstance(mContext).getDemiLightTypeface()),//
            trueReviewCount.indexOf(" "), trueReviewCount.length(),//
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannableStringBuilder);
    }

    private void setWishCount(TextView textView, int count)
    {
        if (textView == null || count == 0)
        {
            return;
        }

        String wishCount = mContext.getString(R.string.label_detail_wish_count, count);

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(wishCount);
        spannableStringBuilder.setSpan( //
            new CustomFontTypefaceSpan(FontManager.getInstance(mContext).getDemiLightTypeface()),//
            wishCount.indexOf(" "), wishCount.length(),//
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannableStringBuilder);
    }

    protected void addWish()
    {

    }

    protected void removeWish()
    {

    }

    protected void showPopAnimation()
    {
        if (mRootView == null)
        {
            return;
        }

        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(mRootView, "scaleX", 0.7f, 1.0f);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(mRootView, "scaleY", 0.7f, 1.0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimatorX, objectAnimatorY);
        animatorSet.setDuration(200);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());

        animatorSet.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                animatorSet.removeAllListeners();

                mRootView.setScaleX(1.0f);
                mRootView.setScaleY(1.0f);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        animatorSet.start();
    }
}