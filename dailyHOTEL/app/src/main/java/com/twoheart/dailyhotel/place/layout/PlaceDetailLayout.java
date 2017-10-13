package com.twoheart.dailyhotel.place.layout;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.daily.base.widget.DailyTextView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceDetail;
import com.twoheart.dailyhotel.network.model.ImageInformation;
import com.twoheart.dailyhotel.place.adapter.PlaceDetailImageViewPagerAdapter;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyLineIndicator;
import com.twoheart.dailyhotel.widget.DailyLoopViewPager;
import com.twoheart.dailyhotel.widget.DailyPlaceDetailScrollView;

import java.util.ArrayList;

public abstract class PlaceDetailLayout extends BaseLayout
{
    public static final int STATUS_NONE = 0;
    public static final int STATUS_SELECT_PRODUCT = 1;
    public static final int STATUS_BOOKING = 2;
    public static final int STATUS_SOLD_OUT = 3;

    public static final int TRANS_GRADIENT_BOTTOM_TYPE_NONE = -1;
    public static final int TRANS_GRADIENT_BOTTOM_TYPE_MAP = 1;
    public static final int TRANS_GRADIENT_BOTTOM_TYPE_LIST = 2;

    protected PlaceDetail mPlaceDetail;
    protected DailyLoopViewPager mViewPager;
    protected DailyLineIndicator mDailyLineIndicator;
    protected View mVRIconView, mFakeVRIconView, mMoreIconView;

    protected DailyPlaceDetailScrollView mScrollView;

    protected PlaceDetailImageViewPagerAdapter mImageAdapter;
    protected TextView mBookingTextView;
    protected TextView mSoldoutTextView;
    protected TextView mDescriptionTextView;
    protected View mPriceOptionLayout;
    protected RadioGroup mPriceRadioGroup;

    protected int mBookingStatus; // 예약 진행 상태로 객실 찾기, 없음, 예약 진행

    protected View mBottomLayout;
    AnimatorSet mWishPopupAnimatorSet;
    int mStatusBarHeight;

    protected com.facebook.drawee.view.SimpleDraweeView mTransSimpleDraweeView;
    protected TextView mTransPlaceNameTextView;
    protected View mTransTitleLayout, mTransGradientBottomView, mTransGradientTopView;

    protected DailyTextView mWishPopupTextView;
    protected View mWishPopupScrollView;
    protected View mWishTooltipView;
    protected TextView mWishTooltipTextView;

    public enum WishPopupState
    {
        ADD,
        DELETE,
        GONE
    }

    public interface OnEventListener extends OnBaseEventListener
    {
        void showActionBar(boolean isAnimation);

        void hideActionBar(boolean isAnimation);

        void onClickImage(PlaceDetail placeDetail);

        void onSelectedImagePosition(int position);

        void onConciergeClick();

        void showMap();

        void finish();

        void clipAddress(String address);

        void showNavigatorDialog();

        void onCalendarClick();

        void onDownloadCouponClick();

        void releaseUiComponent();

        void onWishTooltipClick();

        void onTrueVRClick();
    }

    protected abstract String getProductTypeTitle();

    protected abstract View getTitleLayout();

    public abstract void setTrueReviewCount(int count);

    public abstract void setBookingStatus(int status);

    public abstract DailyPlaceDetailScrollView.OnScrollChangedListener getScrollChangedListener();

    public static int getImageLayoutHeight(Context context)
    {
        return ScreenUtils.getRatioHeightType4x3(ScreenUtils.getScreenWidth(context));
    }

    public PlaceDetailLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        mTransSimpleDraweeView = (com.facebook.drawee.view.SimpleDraweeView) view.findViewById(R.id.transImageView);
        mTransGradientBottomView = view.findViewById(R.id.transGradientBottomView);
        mTransGradientTopView = view.findViewById(R.id.transGradientTopView);

        mTransTitleLayout = view.findViewById(R.id.transTitleLayout);
        mTransPlaceNameTextView = (TextView) mTransTitleLayout.findViewById(R.id.transNameTextView);

        mScrollView = (DailyPlaceDetailScrollView) view.findViewById(R.id.placeScrollView);
        mScrollView.setOnScrollChangedListener(getScrollChangedListener());

        // 이미지 ViewPage 넣기.
        mDailyLineIndicator = (DailyLineIndicator) view.findViewById(R.id.viewpagerIndicator);

        mWishTooltipView = view.findViewById(R.id.wishTooltipView);
        mWishTooltipTextView = (TextView) view.findViewById(R.id.wishTooltipTextView);
        mWishTooltipView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mWishTooltipView.getVisibility() == View.VISIBLE)
                {
                    ((OnEventListener) mOnEventListener).onWishTooltipClick();
                }
            }
        });

        mViewPager = (DailyLoopViewPager) view.findViewById(R.id.defaultLoopViewPager);

        mImageAdapter = new PlaceDetailImageViewPagerAdapter(mContext);
        mViewPager.setAdapter(mImageAdapter);

        mDescriptionTextView = (TextView) view.findViewById(R.id.descriptionTextView);
        mDailyLineIndicator.setViewPager(mViewPager);

        mViewPager.setOnPageChangeListener(mOnPageChangeListener);
        mDailyLineIndicator.setOnPageChangeListener(mOnPageChangeListener);

        //        ViewGroup.LayoutParams layoutParams = mViewPager.getLayoutParams();
        //        layoutParams.height = getImageLayoutHeight(mContext);
        //        mViewPager.setLayoutParams(layoutParams);

        mVRIconView = view.findViewById(R.id.vrImageView);
        mFakeVRIconView = view.findViewById(R.id.fakeVrImageView);
        setVRIconVisible(false);

        mFakeVRIconView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onTrueVRClick();
            }
        });

        mMoreIconView = view.findViewById(R.id.moreIconView);
        mBottomLayout = view.findViewById(R.id.bottomLayout);

        mBookingTextView = (TextView) mBottomLayout.findViewById(R.id.bookingTextView);
        mSoldoutTextView = (TextView) mBottomLayout.findViewById(R.id.soldoutTextView);
        mBottomLayout.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // do nothing - 판매 완료 버튼이 뚤리는 이슈 수정
            }
        });

        mWishPopupScrollView = view.findViewById(R.id.wishListPopupScrollView);
        mWishPopupTextView = (DailyTextView) view.findViewById(R.id.wishListPopupView);
        //        mWishButtonTextView = (DailyTextView) view.findViewById(R.id.wishListButtonView);
        //        mWishButtonTextView.setTag(false);
        //        mWishButtonTextView.setOnClickListener(new OnClickListener()
        //        {
        //            @Override
        //            public void onClick(View v)
        //            {
        //                ((OnEventListener) mOnEventListener).onWishClick();
        //            }
        //        });

        setBookingStatus(STATUS_NONE);
        setUpdateWishPopup(WishPopupState.GONE);
    }

    public void setIsUsedMultiTransitions(boolean enabled, int gradientBottomType)
    {
        if (enabled == true)
        {
            setTransVisibility(View.VISIBLE);
            mTransTitleLayout.setVisibility(View.VISIBLE);

            if (VersionUtils.isOverAPI21() == true)
            {
                mTransSimpleDraweeView.setTransitionName(mContext.getString(R.string.transition_place_image));
                mTransGradientTopView.setTransitionName(mContext.getString(R.string.transition_gradient_top_view));
                mTransGradientBottomView.setTransitionName(mContext.getString(R.string.transition_gradient_bottom_view));

                switch (gradientBottomType)
                {

                    case TRANS_GRADIENT_BOTTOM_TYPE_LIST:
//                        mTransGradientBottomView.setBackgroundResource(R.drawable.shape_gradient_card_bottom);
                        break;

                    case TRANS_GRADIENT_BOTTOM_TYPE_MAP:
                        mTransGradientBottomView.setBackgroundResource(R.color.black_a28);
                        break;

                    case TRANS_GRADIENT_BOTTOM_TYPE_NONE:
                    default:
                        mTransGradientBottomView.setBackground(null);
                        break;
                }

            }
        } else
        {
            setTransVisibility(View.GONE);
            mTransTitleLayout.setVisibility(View.GONE);
        }
    }

    public void setScrollViewTop()
    {
        if (mScrollView == null)
        {
            return;
        }

        mScrollView.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mScrollView.fullScroll(View.FOCUS_UP);
            }
        }, 50);
    }

    public boolean isScrollViewTop()
    {
        if (mScrollView == null)
        {
            return true;
        }

        return mScrollView.getScrollY() == 0; // 임시
    }

    public void setTransVisibility(int visibility)
    {
        mTransSimpleDraweeView.setVisibility(visibility);
        mTransGradientBottomView.setVisibility(visibility);
    }

    public void setTransBottomGradientBackground(int resId)
    {
        mTransGradientBottomView.setBackgroundResource(resId);
    }

    public void setTransImageView(String url)
    {
        if (mTransSimpleDraweeView.getVisibility() == View.VISIBLE)
        {
            Util.requestImageResize(mContext, mTransSimpleDraweeView, url);
        }
    }

    public void setDefaultImage(String url)
    {
        if (DailyTextUtils.isTextEmpty(url) == true)
        {
            setLineIndicatorVisible(false);
            return;
        }

        setLineIndicatorVisible(true);

        if (mImageAdapter == null)
        {
            mImageAdapter = new PlaceDetailImageViewPagerAdapter(mContext);
        }

        ArrayList<ImageInformation> arrayList = new ArrayList<>();
        ImageInformation imageInformation = new ImageInformation();
        imageInformation.setImageUrl(url);
        arrayList.add(imageInformation);

        mImageAdapter.setData(arrayList);
        mViewPager.setAdapter(mImageAdapter);

        mDailyLineIndicator.setViewPager(mViewPager);
    }

    public void setStatusBarHeight(Activity activity)
    {
        final Window window = activity.getWindow();

        mScrollView.post(new Runnable()
        {
            @Override
            public void run()
            {
                Rect rect = new Rect();
                window.getDecorView().getWindowVisibleDisplayFrame(rect);

                mStatusBarHeight = rect.top;
            }
        });
    }

    public int getStatusBarHeight()
    {
        return mStatusBarHeight;
    }

    public void setWishTooltipVisibility(boolean visibility, int orderIcon)
    {
        if (mWishTooltipView == null)
        {
            return;
        }

        if (visibility == true)
        {
            mWishTooltipView.setVisibility(View.VISIBLE);
        } else
        {
            mWishTooltipView.setVisibility(View.GONE);
        }

        // 우측에서부터 순서
        switch (orderIcon)
        {
            case 1:
                mWishTooltipTextView.setBackgroundResource(R.drawable.tooltip_top_vr);
                break;

            case 2:
                mWishTooltipTextView.setBackgroundResource(R.drawable.tooltip_top_wish);
                break;
        }
    }

    public void hideAnimationTooltip()
    {
        if (mWishTooltipView == null || mWishTooltipView.getTag() != null)
        {
            return;
        }

        final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mWishTooltipView, "alpha", 1.0f, 0.0f);

        mWishTooltipView.setTag(objectAnimator);

        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(300);
        objectAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animator)
            {

            }

            @Override
            public void onAnimationEnd(Animator animator)
            {
                objectAnimator.removeAllListeners();
                objectAnimator.removeAllUpdateListeners();

                mWishTooltipView.setTag(null);
                setWishTooltipVisibility(false, 0);
            }

            @Override
            public void onAnimationCancel(Animator animator)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animator)
            {

            }
        });

        objectAnimator.start();
    }

    public boolean isWishTooltipVisibility()
    {
        if (mWishTooltipView == null)
        {
            return false;
        }

        return mWishTooltipView.getVisibility() == View.VISIBLE;
    }

    public int getBookingStatus()
    {
        return mBookingStatus;
    }

    public int getCurrentImage()
    {
        if (mViewPager != null)
        {
            return mViewPager.getCurrentItem();
        }

        return 0;
    }

    public void setCurrentImage(int position)
    {
        if (mImageAdapter == null || mViewPager == null)
        {
            return;
        }

        mViewPager.setCurrentItem(position, true);
    }

    public int getTotalImage()
    {
        if (mImageAdapter != null)
        {
            return mImageAdapter.getCount();
        }

        return 0;
    }


    public void setImageInformation(String description)
    {
        if (DailyTextUtils.isTextEmpty(description) == false)
        {
            mDescriptionTextView.setVisibility(View.VISIBLE);
            mDescriptionTextView.setText(description);
        } else
        {
            mDescriptionTextView.setVisibility(View.INVISIBLE);
        }
    }

    public void setVRIconVisible(boolean visible)
    {
        if (mVRIconView == null)
        {
            return;
        }

        if (visible == true)
        {
            mVRIconView.setVisibility(View.VISIBLE);
            mFakeVRIconView.setVisibility(View.VISIBLE);
        } else
        {
            mVRIconView.setVisibility(View.GONE);
            mFakeVRIconView.setVisibility(View.GONE);
        }
    }

    public void setLineIndicatorVisible(boolean isShow)
    {
        mMoreIconView.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        mDailyLineIndicator.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    public void setUpdateWishPopup(final WishPopupState state)
    {
        if (WishPopupState.GONE == state)
        {
            //            mWishPopupTextView.setVisibility(View.GONE);
            mWishPopupScrollView.setVisibility(View.GONE);

            if (mWishPopupAnimatorSet != null && mWishPopupAnimatorSet.isRunning() == true)
            {
                mWishPopupAnimatorSet.cancel();
            }
        } else
        {
            if (mWishPopupAnimatorSet != null && mWishPopupAnimatorSet.isRunning() == true)
            {
                ExLog.d("WishPopup is Already running");
                return;
            }

            if (WishPopupState.ADD == state)
            {
                mWishPopupTextView.setText(R.string.wishlist_detail_add_message);
                mWishPopupTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_heart_fill_l, 0, 0);
                mWishPopupTextView.setBackgroundResource(R.drawable.shape_filloval_ccdb2453);
            } else
            {
                mWishPopupTextView.setText(R.string.wishlist_detail_delete_message);
                mWishPopupTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_heart_stroke_l, 0, 0);
                mWishPopupTextView.setBackgroundResource(R.drawable.shape_filloval_a5000000);
            }

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

            mWishPopupAnimatorSet = new AnimatorSet();
            mWishPopupAnimatorSet.playSequentially(objectAnimator1, objectAnimator2, objectAnimator3);
            mWishPopupAnimatorSet.addListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    mWishPopupScrollView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mWishPopupAnimatorSet.removeAllListeners();
                    mWishPopupAnimatorSet = null;

                    mWishPopupScrollView.setVisibility(View.INVISIBLE);
                    ((OnEventListener) mOnEventListener).releaseUiComponent();
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

            mWishPopupAnimatorSet.start();
        }
    }

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener()
    {
        private int mScrollState = -1;
        private int mScrollPosition = -1;

        @Override
        public void onPageSelected(int position)
        {
            ((OnEventListener) mOnEventListener).onSelectedImagePosition(position);

            ImageInformation imageInformation = (ImageInformation) mPlaceDetail.getImageList().get(position);
            setImageInformation(imageInformation.description);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
            if (mScrollState == -1 || Float.compare(positionOffset, 0.0f) == 0 || positionOffsetPixels == 0)
            {
                return;
            }

            if (mScrollPosition == -1)
            {
                mScrollPosition = position;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {
            mScrollState = state;

            switch (state)
            {
                case ViewPager.SCROLL_STATE_IDLE:
                    mScrollPosition = -1;
                    break;

                case ViewPager.SCROLL_STATE_DRAGGING:
                    break;

                case ViewPager.SCROLL_STATE_SETTLING:
                    break;
            }
        }
    };

    protected View.OnTouchListener mEmptyViewOnTouchListener = new View.OnTouchListener()
    {
        private int mMoveState;
        private float mPrevX, mPrevY;

        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            switch (event.getAction() & MotionEventCompat.ACTION_MASK)
            {
                case MotionEvent.ACTION_DOWN:
                {
                    mPrevX = event.getX();
                    mPrevY = event.getY();

                    mMoveState = 0;
                    mScrollView.setScrollEnabled(false);

                    try
                    {
                        mViewPager.onTouchEvent(event);
                    } catch (Exception e)
                    {
                    }
                    break;
                }

                case MotionEvent.ACTION_UP:
                {
                    int touchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();

                    int x = (int) (mPrevX - event.getX());
                    int y = (int) (mPrevY - event.getY());

                    int distance = (int) Math.sqrt(x * x + y * y);

                    if (distance < touchSlop)
                    {
                        ((OnEventListener) mOnEventListener).onClickImage(mPlaceDetail);

                        mMoveState = 0;

                        try
                        {
                            mViewPager.onTouchEvent(event);
                        } catch (Exception e)
                        {
                            event.setAction(MotionEvent.ACTION_CANCEL);
                            event.setLocation(mViewPager.getScrollX(), mViewPager.getScrollY());
                            mViewPager.onTouchEvent(event);
                        }

                        mScrollView.setScrollEnabled(true);
                        break;
                    }
                }
                case MotionEvent.ACTION_CANCEL:
                {
                    mMoveState = 0;

                    try
                    {
                        mViewPager.onTouchEvent(event);
                    } catch (Exception e)
                    {
                        event.setAction(MotionEvent.ACTION_CANCEL);
                        event.setLocation(mViewPager.getScrollX(), mViewPager.getScrollY());
                        mViewPager.onTouchEvent(event);
                    }

                    mScrollView.setScrollEnabled(true);
                    break;
                }

                case MotionEvent.ACTION_MOVE:
                {
                    float x = event.getX();
                    float y = event.getY();

                    if (mMoveState == 0)
                    {
                        if (Math.abs(x - mPrevX) == Math.abs(y - mPrevY))
                        {

                        } else if (Math.abs(x - mPrevX) > Math.abs(y - mPrevY))
                        {
                            // x 축으로 이동한 경우.
                            mMoveState = 100;

                            try
                            {
                                mViewPager.onTouchEvent(event);
                            } catch (Exception e)
                            {
                                event.setAction(MotionEvent.ACTION_CANCEL);
                                event.setLocation(mViewPager.getScrollX(), mViewPager.getScrollY());
                                mViewPager.onTouchEvent(event);
                            }
                        } else
                        {
                            // y축으로 이동한 경우.
                            mMoveState = 10;
                            mScrollView.setScrollEnabled(true);
                            return true;
                        }
                    } else if (mMoveState == 100)
                    {
                        try
                        {
                            mViewPager.onTouchEvent(event);
                        } catch (Exception e)
                        {
                            event.setAction(MotionEvent.ACTION_CANCEL);
                            event.setLocation(mViewPager.getScrollX(), mViewPager.getScrollY());
                            mViewPager.onTouchEvent(event);
                        }
                    }
                    break;
                }
            }

            return false;
        }
    };
}