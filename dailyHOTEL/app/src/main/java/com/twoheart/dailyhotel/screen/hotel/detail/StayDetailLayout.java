package com.twoheart.dailyhotel.screen.hotel.detail;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayDetail;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.ImageInformation;
import com.twoheart.dailyhotel.network.model.PlaceReviewScores;
import com.twoheart.dailyhotel.network.model.StayDetailParams;
import com.twoheart.dailyhotel.network.model.StayProduct;
import com.twoheart.dailyhotel.place.adapter.PlaceDetailImageViewPagerAdapter;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.layout.PlaceDetailLayout;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.List;

/**
 * 호텔 상세 정보 화면
 *
 * @author sheldon
 */
public class StayDetailLayout extends PlaceDetailLayout implements RadioGroup.OnCheckedChangeListener
{
    private static final int PRODUCT_VIEW_DURATION = 250;

    public static final int VIEW_AVERAGE_PRICE = 0;
    public static final int VIEW_TOTAL_PRICE = 1;

    private StayDetailListAdapter mListAdapter;
    StayProduct mSelectedStayProduct;

    StayDetailRoomTypeListAdapter mRoomTypeListAdapter;
    protected RecyclerView mProductTypeRecyclerView;
    protected View mProductTypeLayout;
    protected View mProductTypeBackgroundView;

    Constants.ANIMATION_STATUS mAnimationStatus = Constants.ANIMATION_STATUS.HIDE_END;
    Constants.ANIMATION_STATE mAnimationState = Constants.ANIMATION_STATE.END;
    ObjectAnimator mObjectAnimator;
    private AlphaAnimation mAlphaAnimation;

    public interface OnEventListener extends PlaceDetailLayout.OnEventListener
    {
        void doBooking(StayProduct stayProduct);

        void onChangedViewPrice(int type);

        void showProductInformationLayout();

        void hideProductInformationLayout(boolean isAnimation);

        void onStampClick();

        void onReviewClick();
    }

    public StayDetailLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        super.initLayout(view);

        mProductTypeLayout = view.findViewById(R.id.productTypeLayout);

        TextView productTypeTextView = (TextView) mProductTypeLayout.findViewById(R.id.productTypeTextView);

        productTypeTextView.setText(getProductTypeTitle());
        productTypeTextView.setClickable(true);

        mPriceOptionLayout = mProductTypeLayout.findViewById(R.id.priceOptionLayout);
        mPriceRadioGroup = (RadioGroup) mPriceOptionLayout.findViewById(R.id.priceRadioGroup);

        mPriceOptionLayout.setVisibility(View.GONE);

        mProductTypeRecyclerView = (RecyclerView) mProductTypeLayout.findViewById(R.id.productTypeRecyclerView);
        mProductTypeRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        EdgeEffectColor.setEdgeGlowColor(mProductTypeRecyclerView, mContext.getResources().getColor(R.color.default_over_scroll_edge));
        mProductTypeLayout.setVisibility(View.INVISIBLE);

        mProductTypeBackgroundView = view.findViewById(R.id.productTypeBackgroundView);
        mProductTypeBackgroundView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).hideProductInformationLayout(true);
            }
        });

        hideProductInformationLayout();
    }


    @Override
    protected String getProductTypeTitle()
    {
        return mContext.getString(R.string.act_hotel_search_room);
    }

    @Override
    protected View getTitleLayout()
    {
        if (mListAdapter == null)
        {
            return null;
        }

        return mListAdapter.getTitleLayout();
    }

    public void setTitleText(Stay.Grade grade, String placeName)
    {
        if (grade != null)
        {
            mTransTotalGradeTextView.setText(grade.getName(mContext));
            mTransTotalGradeTextView.setBackgroundResource(grade.getColorResId());
            mTransTotalGradeTextView.setTransitionName(mContext.getString(R.string.transition_place_grade));
        }

        if (DailyTextUtils.isTextEmpty(placeName) == false)
        {
            mTransPlaceNameTextView.setText(placeName);
            mTransPlaceNameTextView.setTransitionName(mContext.getString(R.string.transition_place_name));
        }
    }

    public void setDetail(StayBookingDay stayBookingDay, StayDetail stayDetail, PlaceReviewScores placeReviewScores, int imagePosition)
    {
        if (stayDetail == null)
        {
            setLineIndicatorVisible(false);
            setWishButtonSelected(false);
            setWishButtonCount(0);
            return;
        }

        mPlaceDetail = stayDetail;

        StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();
        if (stayDetailParams == null)
        {
            setLineIndicatorVisible(false);
            setWishButtonSelected(false);
            setWishButtonCount(0);
            return;
        }

        if (mImageAdapter == null)
        {
            mImageAdapter = new PlaceDetailImageViewPagerAdapter(mContext);
        }

        List<ImageInformation> imageInformationList = stayDetail.getImageList();
        mImageAdapter.setData(imageInformationList);
        mViewPager.setAdapter(mImageAdapter);

        mDailyLineIndicator.setViewPager(mViewPager);
        setLineIndicatorVisible(imageInformationList.size() > 0);
        setImageInformation((imageInformationList.size() > 0) ? imageInformationList.get(0).description : null);

        if (mListAdapter == null)
        {
            mListAdapter = new StayDetailListAdapter(mContext, stayBookingDay, stayDetail, placeReviewScores, (StayDetailLayout.OnEventListener) mOnEventListener, mEmptyViewOnTouchListener);
            mListView.setAdapter(mListAdapter);
        } else
        {
            mListAdapter.setData(stayBookingDay, stayDetail, placeReviewScores);
        }

        setCurrentImage(imagePosition);

        hideProductInformationLayout();
        showWishButton();

        // SOLD OUT 판단 조건.
        List<StayProduct> stayProductList = stayDetail.getProductList();

        if (stayProductList == null || stayProductList.size() == 0)
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
                    switch (mBookingStatus)
                    {
                        case STATUS_BOOKING:
                            ((StayDetailLayout.OnEventListener) mOnEventListener).doBooking(mSelectedStayProduct);
                            break;

                        case STATUS_SELECT_PRODUCT:
                            ((OnEventListener) mOnEventListener).showProductInformationLayout();
                            break;
                    }
                }
            });

            mSoldoutTextView.setVisibility(View.GONE);

            setBookingStatus(STATUS_SELECT_PRODUCT);

            updateRoomTypeInformationLayout(stayBookingDay, stayProductList);
        }

        try
        {
            if (stayBookingDay.getNights() > 1)
            {
                mPriceRadioGroup.check(R.id.averageRadioButton);
                mPriceOptionLayout.setVisibility(View.VISIBLE);
                mPriceRadioGroup.setOnCheckedChangeListener(this);
            } else
            {
                mPriceOptionLayout.setVisibility(View.GONE);
                mPriceRadioGroup.setOnCheckedChangeListener(null);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            mPriceOptionLayout.setVisibility(View.GONE);
            mPriceRadioGroup.setOnCheckedChangeListener(null);
        }

        setWishButtonSelected(stayDetailParams.myWish);
        setWishButtonCount(stayDetailParams.wishCount);

        if (placeReviewScores != null)
        {
            setTrueReviewCount(placeReviewScores.reviewScoreTotalCount);
        }

        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setTrueReviewCount(int count)
    {
        if (mListAdapter != null)
        {
            mListAdapter.setTrueReviewCount(count);
        }
    }

    private void updateRoomTypeInformationLayout(StayBookingDay stayBookingDay, List<StayProduct> stayProductList)
    {
        if (stayBookingDay == null || stayProductList == null || stayProductList.size() == 0)
        {
            return;
        }

        final int nights;

        try
        {
            nights = stayBookingDay.getNights();
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            return;
        }

        mSelectedStayProduct = stayProductList.get(0);

        // 처음 세팅하는 경우 객실 타입 세팅
        if (mRoomTypeListAdapter == null)
        {
            mRoomTypeListAdapter = new StayDetailRoomTypeListAdapter(mContext, stayProductList, nights, new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int position = mProductTypeRecyclerView.getChildAdapterPosition(v);

                    if (position < 0)
                    {
                        return;
                    }

                    mSelectedStayProduct = mRoomTypeListAdapter.getItem(position);
                    mRoomTypeListAdapter.setSelected(position);
                    mRoomTypeListAdapter.notifyDataSetChanged();

                    AnalyticsManager.getInstance(mContext).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
                        , AnalyticsManager.Action.ROOM_TYPE_ITEM_CLICKED, mSelectedStayProduct.roomName, null);
                }
            });
        } else
        {
            // 재세팅 하는 경우
            mRoomTypeListAdapter.addAll(stayProductList, nights);
            mRoomTypeListAdapter.setSelected(0);
            mRoomTypeListAdapter.notifyDataSetChanged();
        }

        mProductTypeRecyclerView.setAdapter(mRoomTypeListAdapter);

        // 객실 개수로 높이를 재지정해준다.
        final int productTitleBarHeight = ScreenUtils.dpToPx(mContext, 52) + (nights > 1 ? ScreenUtils.dpToPx(mContext, 40) : 0);

        mProductTypeRecyclerView.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                // 화면 높이 - 상단 타이틀 - 하단 버튼
                final int maxHeight = ((View) mProductTypeLayout.getParent()).getHeight() //
                    - ScreenUtils.dpToPx(mContext, 52) - ScreenUtils.dpToPx(mContext, 64);

                ViewGroup.LayoutParams layoutParams = mProductTypeRecyclerView.getLayoutParams();

                /* mProductTypeRecyclerView.getHeight() 를 사용하는 이유 - layoutParams.height 를 사용할 경우
                   속성 값을 리턴 하여 WRAP_CONTENT 등의 값인 -2 등이 리턴 됨 */
                int productLayoutHeight = mProductTypeRecyclerView.getHeight() + productTitleBarHeight;

                layoutParams.height = Math.min(maxHeight, productLayoutHeight) - productTitleBarHeight;
                mProductTypeRecyclerView.setLayoutParams(layoutParams);
            }
        }, 100);
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
                mWishButtonTextView.setVisibility(View.VISIBLE);
                break;
            }

            case STATUS_SELECT_PRODUCT:
            {
                mBookingTextView.setVisibility(View.VISIBLE);
                mSoldoutTextView.setVisibility(View.GONE);
                mWishButtonTextView.setVisibility(View.VISIBLE);

                mBookingTextView.setText(R.string.act_hotel_search_room);
                break;
            }

            case STATUS_BOOKING:
            {
                mBookingTextView.setVisibility(View.VISIBLE);
                mSoldoutTextView.setVisibility(View.GONE);
                mWishButtonTextView.setVisibility(View.VISIBLE);

                mBookingTextView.setText(R.string.act_hotel_booking);
                break;
            }

            case STATUS_SOLD_OUT:
            {
                mBookingTextView.setVisibility(View.GONE);
                mSoldoutTextView.setVisibility(View.VISIBLE);
                mWishButtonTextView.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    public void setSelectProduct(int index)
    {
        if (mRoomTypeListAdapter == null)
        {
            return;
        }

        int position = mRoomTypeListAdapter.setSelectIndex(index);
        mProductTypeRecyclerView.scrollToPosition(position);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId)
    {
        switch (checkedId)
        {
            case R.id.averageRadioButton:
                ((OnEventListener) mOnEventListener).onChangedViewPrice(VIEW_AVERAGE_PRICE);
                break;

            case R.id.totalRadioButton:
                ((OnEventListener) mOnEventListener).onChangedViewPrice(VIEW_TOTAL_PRICE);
                break;
        }
    }

    public void setChangedViewPrice(int type)
    {
        if (mRoomTypeListAdapter == null)
        {
            return;
        }

        mRoomTypeListAdapter.setChangedViewPrice(type);
        mRoomTypeListAdapter.notifyDataSetChanged();
    }

    void setProductInformationLayoutEnabled(boolean enabled)
    {
        mProductTypeLayout.setEnabled(enabled);
        mProductTypeRecyclerView.setEnabled(enabled);
        mProductTypeBackgroundView.setEnabled(enabled);
    }

    public void showProductInformationLayout(int index)
    {
        if (mProductTypeBackgroundView == null || mProductTypeLayout == null)
        {
            Util.restartApp(mContext);
            return;
        }

        if (mObjectAnimator != null && mObjectAnimator.isRunning() == true)
        {
            mObjectAnimator.cancel();
        }

        mProductTypeBackgroundView.setAnimation(null);
        mProductTypeLayout.setAnimation(null);

        mProductTypeBackgroundView.setVisibility(View.VISIBLE);

        mProductTypeLayout.setVisibility(View.VISIBLE);

        mAnimationStatus = Constants.ANIMATION_STATUS.SHOW_END;
        mAnimationState = Constants.ANIMATION_STATE.END;

        setProductInformationLayoutEnabled(true);

        setBookingStatus(STATUS_BOOKING);

        setSelectProduct(index);
    }

    public void hideProductInformationLayout()
    {
        if (mProductTypeBackgroundView == null || mProductTypeLayout == null)
        {
            Util.restartApp(mContext);
            return;
        }

        if (mObjectAnimator != null && mObjectAnimator.isRunning() == true)
        {
            mObjectAnimator.cancel();
        }

        mProductTypeBackgroundView.setAnimation(null);
        mProductTypeLayout.setAnimation(null);

        mProductTypeBackgroundView.setVisibility(View.GONE);

        if (VersionUtils.isOverAPI12() == true)
        {
            mProductTypeLayout.setVisibility(View.INVISIBLE);
        } else
        {
            mProductTypeLayout.setVisibility(View.GONE);
        }

        mAnimationStatus = Constants.ANIMATION_STATUS.HIDE_END;
        mAnimationState = Constants.ANIMATION_STATE.END;
    }

    public void showAnimationProductInformationLayout()
    {
        if (mAnimationState == Constants.ANIMATION_STATE.START && mAnimationStatus == Constants.ANIMATION_STATUS.SHOW)
        {
            return;
        }

        setBookingStatus(STATUS_NONE);

        if (VersionUtils.isOverAPI12() == true)
        {
            final float fromAnimationY = mBottomLayout.getTop();

            if (mObjectAnimator != null && mObjectAnimator.isRunning() == true)
            {
                mObjectAnimator.cancel();
            }

            // 리스트 높이 + 아이콘 높이(실제 화면에 들어나지 않기 때문에 높이가 정확하지 않아서 내부 높이를 더함)
            int height = mProductTypeLayout.getHeight();
            int toolbarHeight = mContext.getResources().getDimensionPixelSize(R.dimen.toolbar_height);
            int maxHeight = ScreenUtils.getScreenHeight(mContext) - (mBottomLayout.getBottom() - mBottomLayout.getTop()) - toolbarHeight - getStatusBarHeight();

            float toAnimationY = fromAnimationY - Math.min(height, maxHeight);

            int startTransY = ScreenUtils.dpToPx(mContext, height);
            mProductTypeLayout.setTranslationY(startTransY);

            mObjectAnimator = ObjectAnimator.ofFloat(mProductTypeLayout, "y", fromAnimationY, toAnimationY);
            mObjectAnimator.setDuration(PRODUCT_VIEW_DURATION);

            mObjectAnimator.addListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    if (mProductTypeLayout.getVisibility() != View.VISIBLE)
                    {
                        mProductTypeLayout.setVisibility(View.VISIBLE);
                    }

                    mAnimationState = Constants.ANIMATION_STATE.START;
                    mAnimationStatus = Constants.ANIMATION_STATUS.SHOW;
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mObjectAnimator.removeAllListeners();
                    mObjectAnimator.removeAllUpdateListeners();
                    mObjectAnimator = null;

                    if (mAnimationState != Constants.ANIMATION_STATE.CANCEL)
                    {
                        mAnimationStatus = Constants.ANIMATION_STATUS.SHOW_END;
                        mAnimationState = Constants.ANIMATION_STATE.END;

                        setProductInformationLayoutEnabled(true);

                        setBookingStatus(STATUS_BOOKING);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation)
                {
                    mAnimationState = Constants.ANIMATION_STATE.CANCEL;
                }

                @Override
                public void onAnimationRepeat(Animator animation)
                {

                }
            });

            mObjectAnimator.start();
        } else
        {
            if (mProductTypeLayout != null && mProductTypeLayout.getVisibility() != View.VISIBLE)
            {
                mProductTypeLayout.setVisibility(View.VISIBLE);

                mAnimationStatus = Constants.ANIMATION_STATUS.SHOW_END;
                mAnimationState = Constants.ANIMATION_STATE.END;

                setProductInformationLayoutEnabled(true);

                setBookingStatus(STATUS_BOOKING);
            }
        }

        showAnimationFadeOut();
    }

    public void showAnimationProductInformationLayout(float dy)
    {
        if (mAnimationState == Constants.ANIMATION_STATE.START && mAnimationStatus == Constants.ANIMATION_STATUS.SHOW)
        {
            return;
        }

        setBookingStatus(STATUS_NONE);

        if (VersionUtils.isOverAPI12() == true)
        {
            final float y = mBottomLayout.getTop();

            if (mObjectAnimator != null && mObjectAnimator.isRunning() == true)
            {
                mObjectAnimator.cancel();
            }

            // 리스트 높이 + 아이콘 높이(실제 화면에 들어나지 않기 때문에 높이가 정확하지 않아서 내부 높이를 더함)
            int height = mProductTypeLayout.getHeight();
            mProductTypeLayout.setTranslationY(ScreenUtils.dpToPx(mContext, height));

            mObjectAnimator = ObjectAnimator.ofFloat(mProductTypeLayout, "y", y, dy);
            mObjectAnimator.setDuration(PRODUCT_VIEW_DURATION);

            mObjectAnimator.addListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    if (mProductTypeLayout.getVisibility() != View.VISIBLE)
                    {
                        mProductTypeLayout.setVisibility(View.VISIBLE);
                    }

                    mAnimationState = Constants.ANIMATION_STATE.START;
                    mAnimationStatus = Constants.ANIMATION_STATUS.SHOW;
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mObjectAnimator.removeAllListeners();
                    mObjectAnimator.removeAllUpdateListeners();
                    mObjectAnimator = null;

                    if (mAnimationState != Constants.ANIMATION_STATE.CANCEL)
                    {
                        mAnimationStatus = Constants.ANIMATION_STATUS.SHOW_END;
                        mAnimationState = Constants.ANIMATION_STATE.END;

                        setProductInformationLayoutEnabled(true);

                        setBookingStatus(STATUS_BOOKING);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation)
                {
                    mAnimationState = Constants.ANIMATION_STATE.CANCEL;
                }

                @Override
                public void onAnimationRepeat(Animator animation)
                {

                }
            });

            mObjectAnimator.start();
        } else
        {
            if (mProductTypeLayout != null && mProductTypeLayout.getVisibility() != View.VISIBLE)
            {
                mProductTypeLayout.setVisibility(View.VISIBLE);

                mAnimationStatus = Constants.ANIMATION_STATUS.SHOW_END;
                mAnimationState = Constants.ANIMATION_STATE.END;

                setProductInformationLayoutEnabled(true);

                setBookingStatus(STATUS_BOOKING);
            }
        }

        showAnimationFadeOut();
    }

    public void hideAnimationProductInformationLayout()
    {
        if (mAnimationState == Constants.ANIMATION_STATE.START && mAnimationStatus == Constants.ANIMATION_STATUS.HIDE)
        {
            return;
        }

        if (mPlaceDetail == null)
        {
            Util.restartApp(mContext);
            return;
        }

        StayDetailParams stayDetailParams = ((StayDetail) mPlaceDetail).getStayDetailParams();

        setBookingStatus(STATUS_NONE);

        final float y = mProductTypeLayout.getY();

        if (mObjectAnimator != null && mObjectAnimator.isRunning() == true)
        {
            mObjectAnimator.cancel();
        }

        mObjectAnimator = ObjectAnimator.ofFloat(mProductTypeLayout, "y", y, mBottomLayout.getTop());
        mObjectAnimator.setDuration(PRODUCT_VIEW_DURATION);

        mObjectAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mAnimationState = Constants.ANIMATION_STATE.START;
                mAnimationStatus = Constants.ANIMATION_STATUS.HIDE;

                setProductInformationLayoutEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                mObjectAnimator.removeAllListeners();
                mObjectAnimator.removeAllUpdateListeners();
                mObjectAnimator = null;

                if (mAnimationState != Constants.ANIMATION_STATE.CANCEL)
                {
                    mAnimationStatus = Constants.ANIMATION_STATUS.HIDE_END;
                    mAnimationState = Constants.ANIMATION_STATE.END;

                    ((OnEventListener) mOnEventListener).hideProductInformationLayout(false);

                    setBookingStatus(STATUS_SELECT_PRODUCT);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                mAnimationState = Constants.ANIMATION_STATE.CANCEL;
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {
            }
        });

        mObjectAnimator.start();

        showAnimationFadeIn();
        showWishButtonAnimation();

        AnalyticsManager.getInstance(mContext).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , AnalyticsManager.Action.ROOM_TYPE_CANCEL_CLICKED, stayDetailParams.name, null);
    }

    /**
     * 점점 밝아짐.
     */
    private void showAnimationFadeIn()
    {
        if (mAlphaAnimation != null)
        {
            if (mAlphaAnimation.hasEnded() == false)
            {
                mAlphaAnimation.cancel();
            }

            mAlphaAnimation = null;
        }

        mAlphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        mAlphaAnimation.setDuration(300);
        mAlphaAnimation.setFillBefore(true);
        mAlphaAnimation.setFillAfter(true);

        mAlphaAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }
        });

        if (mProductTypeBackgroundView != null)
        {
            mProductTypeBackgroundView.startAnimation(mAlphaAnimation);
        }
    }

    /**
     * 점점 어두워짐.
     */
    private void showAnimationFadeOut()
    {
        if (mAlphaAnimation != null)
        {
            if (mAlphaAnimation.hasEnded() == false)
            {
                mAlphaAnimation.cancel();
            }

            mAlphaAnimation = null;
        }

        mAlphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        mAlphaAnimation.setDuration(300);
        mAlphaAnimation.setFillBefore(true);
        mAlphaAnimation.setFillAfter(true);

        mAlphaAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
                if (mProductTypeBackgroundView.getVisibility() != View.VISIBLE)
                {
                    mProductTypeBackgroundView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }
        });

        if (mProductTypeBackgroundView != null)
        {
            mProductTypeBackgroundView.startAnimation(mAlphaAnimation);
        }
    }
}