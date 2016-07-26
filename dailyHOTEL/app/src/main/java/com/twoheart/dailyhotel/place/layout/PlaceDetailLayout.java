package com.twoheart.dailyhotel.place.layout;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.ImageInformation;
import com.twoheart.dailyhotel.model.PlaceDetail;
import com.twoheart.dailyhotel.place.adapter.PlaceDetailImageViewPagerAdapter;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyLoopViewPager;
import com.twoheart.dailyhotel.widget.DailyPlaceDetailListView;
import com.twoheart.dailyhotel.widget.DailyViewPagerIndicator;

import java.util.ArrayList;

public abstract class PlaceDetailLayout extends BaseLayout
{
    public static final int STATUS_NONE = 0;
    public static final int STATUS_SELECT_PRODUCT = 1;
    public static final int STATUS_BOOKING = 2;
    public static final int STATUS_SOLD_OUT = 3;

    private static final int VIEW_COUNT = 4;

    protected PlaceDetail mPlaceDetail;
    protected DailyLoopViewPager mViewPager;
    protected DailyViewPagerIndicator mDailyViewPagerIndicator;

    protected DailyPlaceDetailListView mListView;
    protected PlaceDetailImageViewPagerAdapter mImageAdapter;
    protected TextView mBookingTextView;
    protected TextView mSoldoutTextView;

    protected int mImageHeight;
    protected int mBookingStatus; // 예약 진행 상태로 객실 찾기, 없음, 예약 진행

    protected RecyclerView mProductTypeRecyclerView;
    protected View mProductTypeLayout;
    private View mBottomLayout;
    protected View mProductTypeBackgroundView;
    private Constants.ANIMATION_STATUS mAnimationStatus = Constants.ANIMATION_STATUS.HIDE_END;
    private Constants.ANIMATION_STATE mAnimationState = Constants.ANIMATION_STATE.END;
    private ObjectAnimator mObjectAnimator;
    private AlphaAnimation mAlphaAnimation;
    private int mStatusBarHeight;
    private float mLastFactor;

    public interface OnEventListener extends OnBaseEventListener
    {
        void showActionBar();

        void hideActionBar();

        void onClickImage(PlaceDetail placeDetail);

        void onSelectedImagePosition(int position);

        void doKakaotalkConsult();

        void showProductInformationLayout();

        void hideProductInformationLayout();

        void showMap();

        void finish();

        void clipAddress(String address);

        void showNavigatorDialog();

        void onCalendarClick();

        void doBooking();
    }

    protected abstract View getTitleLayout();

    protected abstract View getGradeTextView();

    protected abstract View getMagicToolbarView();

    public abstract void setBookingStatus(int status);

    public PlaceDetailLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        mListView = (DailyPlaceDetailListView) view.findViewById(R.id.placeListView);
        mListView.setOnScrollListener(mOnScrollListener);

        // 이미지 ViewPage 넣기.
        mDailyViewPagerIndicator = (DailyViewPagerIndicator) view.findViewById(R.id.viewpagerIndicator);

        mViewPager = (DailyLoopViewPager) view.findViewById(R.id.defaulLoopViewPager);
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);

        mImageHeight = Util.getLCDWidth(mContext);
        ViewGroup.LayoutParams layoutParams = mViewPager.getLayoutParams();
        layoutParams.height = mImageHeight;

        mProductTypeLayout = view.findViewById(R.id.productTypeLayout);
        mProductTypeRecyclerView = (RecyclerView) mProductTypeLayout.findViewById(R.id.productTypeRecyclerView);
        mProductTypeRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        EdgeEffectColor.setEdgeGlowColor(mProductTypeRecyclerView, mContext.getResources().getColor(R.color.default_over_scroll_edge));
        mProductTypeLayout.setVisibility(View.INVISIBLE);

        mBottomLayout = view.findViewById(R.id.bottomLayout);

        mProductTypeBackgroundView = view.findViewById(R.id.productTypeBackgroundView);
        mProductTypeBackgroundView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideAnimationProductInformationLayout();
            }
        });

        mBookingTextView = (TextView) mBottomLayout.findViewById(R.id.bookingTextView);
        mSoldoutTextView = (TextView) mBottomLayout.findViewById(R.id.soldoutTextView);

        setBookingStatus(STATUS_NONE);
        hideProductInformationLayout();
    }

    public void setDefaultImage(String url)
    {
        if (Util.isTextEmpty(url) == true)
        {
            return;
        }

        if (mImageAdapter == null)
        {
            mImageAdapter = new PlaceDetailImageViewPagerAdapter(mContext);
        }

        ArrayList<ImageInformation> arrayList = new ArrayList<>();
        arrayList.add(new ImageInformation(url, null));

        mImageAdapter.setData(arrayList);
        mViewPager.setAdapter(mImageAdapter);
    }

    public void setStatusBarHeight(Activity activity)
    {
        final Window window = activity.getWindow();

        mListView.post(new Runnable()
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

    private void setProductInformationLayoutEnabled(boolean enabled)
    {
        mProductTypeLayout.setEnabled(enabled);
        mProductTypeRecyclerView.setEnabled(enabled);
        mProductTypeBackgroundView.setEnabled(enabled);
    }

    public void hideProductInformationLayout()
    {
        if (mObjectAnimator != null)
        {
            if (mObjectAnimator.isRunning() == true)
            {
                mObjectAnimator.cancel();
                mObjectAnimator.removeAllListeners();
            }

            mObjectAnimator = null;
        }

        mProductTypeBackgroundView.setAnimation(null);
        mProductTypeLayout.setAnimation(null);

        mProductTypeBackgroundView.setVisibility(View.GONE);

        if (Util.isOverAPI12() == true)
        {
            mProductTypeLayout.setVisibility(View.INVISIBLE);
            mProductTypeLayout.setTranslationY(Util.dpToPx(mContext, VIEW_COUNT * 92));
        } else
        {
            mProductTypeLayout.setVisibility(View.GONE);
        }

        mAnimationStatus = Constants.ANIMATION_STATUS.HIDE_END;
    }

    public void showAnimationProductInformationLayout()
    {
        if (mAnimationState == Constants.ANIMATION_STATE.START && mAnimationStatus == Constants.ANIMATION_STATUS.SHOW)
        {
            return;
        }

        setBookingStatus(STATUS_NONE);

        if (Util.isOverAPI12() == true)
        {
            final float y = mBottomLayout.getTop();

            if (mObjectAnimator != null)
            {
                if (mObjectAnimator.isRunning() == true)
                {
                    mObjectAnimator.cancel();
                    mObjectAnimator.removeAllListeners();
                }

                mObjectAnimator = null;
            }

            // 리스트 높이 + 아이콘 높이(실제 화면에 들어나지 않기 때문에 높이가 정확하지 않아서 내부 높이를 더함)
            int height = mProductTypeRecyclerView.getHeight() + Util.dpToPx(mContext, 52);

            mProductTypeLayout.setTranslationY(Util.dpToPx(mContext, height));

            mObjectAnimator = ObjectAnimator.ofFloat(mProductTypeLayout, "y", y, mBottomLayout.getTop() - height);
            mObjectAnimator.setDuration(300);

            mObjectAnimator.addListener(new AnimatorListener()
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

        setBookingStatus(STATUS_NONE);

        if (Util.isOverAPI12() == true)
        {
            final float y = mProductTypeLayout.getY();

            if (mObjectAnimator != null)
            {
                if (mObjectAnimator.isRunning() == true)
                {
                    mObjectAnimator.cancel();
                    mObjectAnimator.removeAllListeners();
                }

                mObjectAnimator = null;
            }

            mObjectAnimator = ObjectAnimator.ofFloat(mProductTypeLayout, "y", y, mBottomLayout.getTop());
            mObjectAnimator.setDuration(300);

            mObjectAnimator.addListener(new AnimatorListener()
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
                    if (mAnimationState != Constants.ANIMATION_STATE.CANCEL)
                    {
                        mAnimationStatus = Constants.ANIMATION_STATUS.HIDE_END;
                        mAnimationState = Constants.ANIMATION_STATE.END;

                        hideProductInformationLayout();

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
        } else
        {
            setProductInformationLayoutEnabled(false);

            mAnimationStatus = Constants.ANIMATION_STATUS.HIDE_END;
            mAnimationState = Constants.ANIMATION_STATE.END;

            hideProductInformationLayout();

            setBookingStatus(STATUS_SELECT_PRODUCT);
        }
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

        mAlphaAnimation.setAnimationListener(new AnimationListener()
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

        mAlphaAnimation.setAnimationListener(new AnimationListener()
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

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener()
    {
        private int mSelectedPosition = -1;
        private boolean mIsRefresh;
        private int mScrollState = -1;
        private int mScrollPosition = -1;

        @Override
        public void onPageSelected(int position)
        {
            mIsRefresh = false;
            mSelectedPosition = position;

            ((OnEventListener) mOnEventListener).onSelectedImagePosition(position);
            mDailyViewPagerIndicator.setImageInformation(mPlaceDetail.getImageInformationList().get(position).description, position);
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

    private OnScrollListener mOnScrollListener = new OnScrollListener()
    {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState)
        {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
            if (view.getAdapter() == null || getBookingStatus() == STATUS_BOOKING)
            {
                return;
            }

            if (firstVisibleItem > 1)
            {
                ((OnEventListener) mOnEventListener).showActionBar();
                return;
            }

            if (mStatusBarHeight == 0)
            {
                return;
            }

            View titleLaoyout = getTitleLayout();

            if (titleLaoyout == null)
            {
                return;
            }

            Rect rect = new Rect();
            titleLaoyout.getGlobalVisibleRect(rect);

            if (rect.top == rect.right)
            {

            } else
            {
                if (rect.top <= mStatusBarHeight)
                {
                    ((OnEventListener) mOnEventListener).showActionBar();
                } else
                {
                    ((OnEventListener) mOnEventListener).hideActionBar();
                }
            }

            View gradeTextView = getGradeTextView();

            if (gradeTextView == null)
            {
                return;
            }

            final int TOOLBAR_HEIGHT = mContext.getResources().getDimensionPixelSize(R.dimen.toolbar_height);
            float max = ((float) mImageHeight - TOOLBAR_HEIGHT) / 2;
            float offset = rect.top - mStatusBarHeight - TOOLBAR_HEIGHT;
            float alphaFactor = offset / max;

            if (Util.isOverAPI11() == true)
            {
                if (Float.compare(alphaFactor, 0.0f) <= 0)
                {
                    gradeTextView.setAlpha(0.0f);
                } else
                {
                    gradeTextView.setAlpha(alphaFactor);
                }
            } else
            {
                if (Float.compare(alphaFactor, 0.2f) <= 0)
                {
                    gradeTextView.setVisibility(View.INVISIBLE);
                } else
                {
                    gradeTextView.setVisibility(View.VISIBLE);
                }
            }

            View nameTextView = getGradeTextView();
            Rect firstRect = (Rect) nameTextView.getTag();
            Integer firstWidth = (Integer) nameTextView.getTag(nameTextView.getId());

            if (firstRect != null && firstWidth != null)
            {
                final int TOOLBAR_TEXT_X = Util.dpToPx(mContext, 60);
                float gradeMax = ((float) mImageHeight - TOOLBAR_HEIGHT) / 3;
                float gradeOffset = rect.top - mStatusBarHeight - TOOLBAR_HEIGHT;
                float xFactor = gradeOffset / gradeMax;
                float nameMax = firstRect.left - TOOLBAR_TEXT_X;

                if (Float.compare(xFactor, 0.0f) < 0)
                {
                    xFactor = 0.0f;
                }

                if (Util.isOverAPI11() == true)
                {
                    if (Float.compare(xFactor, 1.0f) <= 0)
                    {
                        nameTextView.setTranslationX(-nameMax * (1.0f - xFactor));
                    } else
                    {
                        nameTextView.setTranslationX(0);
                    }
                } else
                {
                    if (Float.compare(xFactor, 1.0f) <= 0)
                    {
                        TranslateAnimation anim = new TranslateAnimation(mLastFactor, -nameMax * (1.0f - xFactor), 0.0f, 0.0f);
                        anim.setDuration(0);
                        anim.setFillAfter(true);
                        nameTextView.startAnimation(anim);

                        mLastFactor = -nameMax * (1.0f - xFactor);
                    } else
                    {
                        nameTextView.setAnimation(null);
                    }
                }

                float widthNameMax = firstRect.width() - firstWidth;
                int newWidth;

                if (Float.compare(xFactor, 1.0f) <= 0)
                {
                    newWidth = (int) (firstRect.width() - (widthNameMax * (1.0f - xFactor)));
                } else
                {
                    newWidth = firstRect.width();
                }

                ViewGroup.LayoutParams layoutParams = nameTextView.getLayoutParams();

                if (layoutParams.width != newWidth)
                {
                    layoutParams.width = newWidth;
                    nameTextView.setLayoutParams(layoutParams);
                }
            }

            View magicToolbarView = getMagicToolbarView();

            if (magicToolbarView != null)
            {
                if (magicToolbarView.getVisibility() != View.VISIBLE)
                {
                    magicToolbarView.setVisibility(View.VISIBLE);
                }

                magicToolbarView.setY(mStatusBarHeight - rect.top);
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
                    mListView.setScrollEnabled(false);

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

                        mListView.setScrollEnabled(true);
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

                    mListView.setScrollEnabled(true);
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
                            mListView.setScrollEnabled(true);
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