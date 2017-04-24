package com.twoheart.dailyhotel.screen.hotel.preview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayDetail;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
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
public class StayPreviewLayout extends BaseLayout implements View.OnClickListener
{
    private TextView mPlaceGradeTextView;
    private TextView mPlaceNameTextView;
    private View mImageLayout;
    private TextView mProductCountTextView;
    private TextView mPriceTextView;
    private View mStayAverageView;
    private View mPopupLayout, mBottomBarLayout;
    private View mMoreInformationLayout;
    private View mWishPopupLayout;
    private TextView mWishPopupTextView;

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
        mPopupLayout.setOnClickListener(this);

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
        mStayAverageView = view.findViewById(R.id.stayAverageView);
        mPriceTextView = (TextView) view.findViewById(R.id.priceTextView);

        mBottomBarLayout = view.findViewById(R.id.bottomBarLayout);
        mMoreInformationLayout = view.findViewById(R.id.moreInformationLayout);

        View closeView = view.findViewById(R.id.closeView);
        closeView.setOnClickListener(this);

        // wish Animation
        mWishPopupLayout = view.findViewById(R.id.wishPopupLayout);
        mWishPopupTextView = (DailyTextView) view.findViewById(R.id.wishPopupTextView);
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

    protected void updateLayout(StayBookingDay stayBookingDay, StayDetail stayDetail, int reviewCount, boolean changedPrice)
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

        final int IMAGE_MAX_COUNT = 4;

        // 이미지 연동
        SimpleDraweeView[] simpleDraweeViews = new SimpleDraweeView[IMAGE_MAX_COUNT];
        simpleDraweeViews[0] = (SimpleDraweeView) mImageLayout.findViewById(R.id.simpleDraweeView01);
        simpleDraweeViews[1] = (SimpleDraweeView) mImageLayout.findViewById(R.id.simpleDraweeView02);
        simpleDraweeViews[2] = (SimpleDraweeView) mImageLayout.findViewById(R.id.simpleDraweeView03);
        simpleDraweeViews[3] = (SimpleDraweeView) mImageLayout.findViewById(R.id.simpleDraweeView04);

        simpleDraweeViews[0].getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);
        simpleDraweeViews[1].getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);
        simpleDraweeViews[2].getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);
        simpleDraweeViews[3].getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

        int imageCount = stayDetailParams.getImageList().size();

        if (imageCount == 1)
        {
            simpleDraweeViews[1].setVisibility(View.GONE);

            // 하단 이미지 없애기
            ((View) simpleDraweeViews[2].getParent()).setVisibility(View.GONE);

            int imageHeight = ScreenUtils.getRatioHeightType4x3(((View) simpleDraweeViews[0].getParent()).getWidth());
            ViewGroup.LayoutParams layoutParams = simpleDraweeViews[0].getLayoutParams();
            layoutParams.height = imageHeight;
            simpleDraweeViews[0].setLayoutParams(layoutParams);
            simpleDraweeViews[0].setImageURI(stayDetailParams.getImageList().get(0).getImageUrl());
        } else
        {
            for (int i = 0; i < IMAGE_MAX_COUNT; i++)
            {
                if (imageCount <= i)
                {
                    simpleDraweeViews[i].setVisibility(View.INVISIBLE);
                } else
                {
                    simpleDraweeViews[i].setVisibility(View.VISIBLE);
                    simpleDraweeViews[i].setImageURI(stayDetailParams.getImageList().get(i).getImageUrl());
                }
            }
        }

        // 가격
        if (changedPrice == true)
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

        // 하단 메뉴 이벤트
        DailyTextView wishTextVieww = (DailyTextView) mBottomBarLayout.findViewById(R.id.wishTextView);
        View shareKakaoView = mBottomBarLayout.findViewById(R.id.shareKakaoView);
        View mapView = mBottomBarLayout.findViewById(R.id.mapView);

        wishTextVieww.setOnClickListener(this);
        if (stayDetail.getStayDetailParams().myWish == true)
        {
            wishTextVieww.setText(R.string.label_preview_remove_wish);
            wishTextVieww.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_lp_01_wishlist_on, 0, 0);
        } else
        {
            wishTextVieww.setText(R.string.label_preview_add_wish);
            wishTextVieww.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_lp_01_wishlist_off, 0, 0);
        }

        shareKakaoView.setOnClickListener(this);
        mapView.setOnClickListener(this);
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
        AnimatorSet wishAnimatorSet = new AnimatorSet();

        mWishPopupTextView.setText(R.string.wishlist_detail_add_message);
        mWishPopupTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_heart_fill_l, 0, 0);
        mWishPopupTextView.setBackgroundResource(R.drawable.shape_filloval_ccdb2453);

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofPropertyValuesHolder(mWishPopupTextView //
            , PropertyValuesHolder.ofFloat("scaleX", 0.8f, 1.2f, 1.0f) //
            , PropertyValuesHolder.ofFloat("scaleY", 0.8f, 1.2f, 1.0f) //
            , PropertyValuesHolder.ofFloat("alpha", 0.5f, 1.0f, 1.0f) //
        );
        objectAnimator1.setInterpolator(new AccelerateInterpolator());
        objectAnimator1.setDuration(300);


        ObjectAnimator objectAnimator2 = ObjectAnimator.ofPropertyValuesHolder(mWishPopupTextView //
            , PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.0f) //
            , PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.0f) //
            , PropertyValuesHolder.ofFloat("alpha", 1.0f, 1.0f) //
        );
        objectAnimator2.setDuration(600);

        ObjectAnimator objectAnimator3 = ObjectAnimator.ofPropertyValuesHolder(mWishPopupTextView //
            , PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.7f) //
            , PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.7f) //
            , PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f) //
        );
        objectAnimator3.setDuration(200);

        wishAnimatorSet.playSequentially(objectAnimator1, objectAnimator2, objectAnimator3);
        wishAnimatorSet.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mWishPopupLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                wishAnimatorSet.removeAllListeners();

                mWishPopupLayout.setVisibility(View.INVISIBLE);
                mOnEventListener.finish();
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

        wishAnimatorSet.start();
    }

    protected void removeWish()
    {
        AnimatorSet wishAnimatorSet = new AnimatorSet();

        mWishPopupTextView.setText(R.string.wishlist_detail_delete_message);
        mWishPopupTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_heart_stroke_l, 0, 0);
        mWishPopupTextView.setBackgroundResource(R.drawable.shape_filloval_75000000);

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofPropertyValuesHolder(mWishPopupTextView //
            , PropertyValuesHolder.ofFloat("scaleX", 0.8f, 1.2f, 1.0f) //
            , PropertyValuesHolder.ofFloat("scaleY", 0.8f, 1.2f, 1.0f) //
            , PropertyValuesHolder.ofFloat("alpha", 0.5f, 1.0f, 1.0f) //
        );
        objectAnimator1.setInterpolator(new AccelerateInterpolator());
        objectAnimator1.setDuration(300);

        ObjectAnimator objectAnimator2 = ObjectAnimator.ofPropertyValuesHolder(mWishPopupTextView //
            , PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.0f) //
            , PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.0f) //
            , PropertyValuesHolder.ofFloat("alpha", 1.0f, 1.0f) //
        );
        objectAnimator2.setDuration(600);

        ObjectAnimator objectAnimator3 = ObjectAnimator.ofPropertyValuesHolder(mWishPopupTextView //
            , PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.7f) //
            , PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.7f) //
            , PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f) //
        );
        objectAnimator3.setDuration(200);

        wishAnimatorSet.playSequentially(objectAnimator1, objectAnimator2, objectAnimator3);
        wishAnimatorSet.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mWishPopupLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                wishAnimatorSet.removeAllListeners();

                mWishPopupLayout.setVisibility(View.INVISIBLE);
                mOnEventListener.finish();
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

        wishAnimatorSet.start();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.popupLayout:
                ((OnEventListener) mOnEventListener).onStayDetailClick();
                break;

            case R.id.closeView:
                mOnEventListener.finish();
                break;

            case R.id.wishTextView:
                ((OnEventListener) mOnEventListener).onWishClick();
                break;

            case R.id.shareKakaoView:
                ((OnEventListener) mOnEventListener).onKakaoClick();
                break;

            case R.id.mapView:
                ((OnEventListener) mOnEventListener).onMapClick();
                break;
        }
    }

    protected void showPopAnimation()
    {
        if (mRootView == null)
        {
            return;
        }

        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(mRootView //
            , PropertyValuesHolder.ofFloat("scaleX", 0.7f, 1.0f) //
            , PropertyValuesHolder.ofFloat("scaleY", 0.7f, 1.0f));

        objectAnimator.setDuration(200);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        objectAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                objectAnimator.removeAllListeners();

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

        objectAnimator.start();
    }
}