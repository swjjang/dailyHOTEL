package com.twoheart.dailyhotel.screen.hoteldetail;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.adapter.DetailImageViewPagerAdapter;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.model.ImageInformation;
import com.twoheart.dailyhotel.model.SaleRoomInformation;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.LoopViewPager;
import com.twoheart.dailyhotel.view.widget.DailyViewPagerIndicator;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * 호텔 상세 정보 화면
 *
 * @author sheldon
 */
public class HotelDetailLayout
{
    public static final int STATUS_NONE = 0;
    public static final int STATUS_SEARCH_ROOM = 1;
    public static final int STATUS_BOOKING = 2;
    public static final int STATUS_SOLD_OUT = 3;

    private static final int MAX_OF_ROOMTYPE = 4;

    private HotelDetail mHotelDetail;
    private BaseActivity mActivity;
    private LoopViewPager mViewPager;
    private DailyViewPagerIndicator mDailyViewPagerIndicator;
    private HotelDetailListView mListView;
    private DetailImageViewPagerAdapter mImageAdapter;
    private HotelDetailListAdapter mListAdapter;
    private SaleRoomInformation mSelectedSaleRoomInformation;

    private View mRoomTypeLayout;
    private View mBottomLayout;
    private View mRoomTypeBackgroundView;
    private View[] mRoomTypeView;
    private View mImageViewBlur;

    private ANIMATION_STATUS mAnimationStatus = ANIMATION_STATUS.HIDE_END;
    private ANIMATION_STATE mAnimationState = ANIMATION_STATE.END;
    private ObjectAnimator mObjectAnimator;
    private AlphaAnimation mAlphaAnimation;

    private int mStatusBarHeight;
    private int mImageHeight;

    private int mBookingStatus; // 예약 진행 상태로 객실 찾기, 없음, 예약 진행

    private HotelDetailActivity.OnUserActionListener mOnUserActionListener;

    public HotelDetailLayout(BaseActivity activity, String defaultImageUrl)
    {
        mActivity = activity;

        initLayout(activity, defaultImageUrl);
    }

    private void initLayout(BaseActivity activity, String defaultImageUrl)
    {
        mListView = (HotelDetailListView) activity.findViewById(R.id.hotelListView);
        mListView.setOnScrollListener(mOnScrollListener);

        // 이미지 ViewPage 넣기.
        mDailyViewPagerIndicator = (DailyViewPagerIndicator) activity.findViewById(R.id.viewpagerIndicator);

        mViewPager = (LoopViewPager) activity.findViewById(R.id.defaultHotelImageView);
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);

        if (defaultImageUrl != null)
        {
            if (mImageAdapter == null)
            {
                mImageAdapter = new DetailImageViewPagerAdapter(mActivity);
            }

            ArrayList<ImageInformation> arrayList = new ArrayList<>();
            arrayList.add(new ImageInformation(defaultImageUrl, null));

            mImageAdapter.setData(arrayList);
            mViewPager.setAdapter(mImageAdapter);
        }

        mImageViewBlur = activity.findViewById(R.id.imageViewBlur);
        mImageViewBlur.setVisibility(View.INVISIBLE);

        mImageHeight = Util.getLCDWidth(activity);
        ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) mViewPager.getLayoutParams();
        layoutParams.height = mImageHeight;

        mRoomTypeLayout = activity.findViewById(R.id.roomTypeLayout);
        mRoomTypeLayout.setVisibility(View.INVISIBLE);

        mRoomTypeView = new View[MAX_OF_ROOMTYPE];

        mBottomLayout = activity.findViewById(R.id.bottomLayout);

        mRoomTypeBackgroundView = activity.findViewById(R.id.roomTypeBackgroundView);

        mRoomTypeBackgroundView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideAnimationRoomType();
            }
        });

        setBookingStatus(STATUS_NONE);
        hideRoomType();
    }

    public void setHotelDetail(HotelDetail hotelDetail, int imagePosition)
    {
        if (hotelDetail == null)
        {
            return;
        }

        mHotelDetail = hotelDetail;

        if (mImageAdapter == null)
        {
            mImageAdapter = new DetailImageViewPagerAdapter(mActivity);
        }

        mImageAdapter.setData(hotelDetail.getImageInformationList());
        mViewPager.setAdapter(mImageAdapter);
        mDailyViewPagerIndicator.setTotalCount(hotelDetail.getImageInformationList().size());
        mDailyViewPagerIndicator.setImageInformation(hotelDetail.getImageInformationList().get(0).description, 0);

        if (mListAdapter == null)
        {
            mListAdapter = new HotelDetailListAdapter((FragmentActivity) mActivity, hotelDetail, mOnUserActionListener, mEmptyViewOnTouchListener);
            mListView.setAdapter(mListAdapter);
        }

        setCurrentImage(imagePosition);

        hideRoomType();

        // 호텔 sold out시
        View bookingView = mActivity.findViewById(R.id.bookingTextView);
        View soldoutView = mActivity.findViewById(R.id.soldoutTextView);

        // SOLD OUT 판단 조건.
        ArrayList<SaleRoomInformation> saleRoomList = hotelDetail.getSaleRoomList();

        if (saleRoomList == null || saleRoomList.size() == 0)
        {
            bookingView.setVisibility(View.GONE);
            soldoutView.setVisibility(View.VISIBLE);

            setBookingStatus(STATUS_SOLD_OUT);
        } else
        {
            bookingView.setVisibility(View.VISIBLE);
            bookingView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    switch (mBookingStatus)
                    {
                        case STATUS_BOOKING:
                            if (mOnUserActionListener != null)
                            {
                                mOnUserActionListener.doBooking(mSelectedSaleRoomInformation);
                            }
                            break;

                        case STATUS_SEARCH_ROOM:
                            if (mOnUserActionListener != null)
                            {
                                mOnUserActionListener.showRoomType();
                            }
                            break;
                    }
                }
            });

            soldoutView.setVisibility(View.GONE);

            setBookingStatus(STATUS_SEARCH_ROOM);

            initRoomTypeLayout(hotelDetail.nights, saleRoomList);
        }

        mListAdapter.notifyDataSetChanged();
    }

    private void initRoomTypeLayout(int nights, ArrayList<SaleRoomInformation> saleRoomList)
    {
        if (saleRoomList == null || saleRoomList.size() == 0)
        {
            return;
        }

        // 객실 타입 세팅
        mRoomTypeView[0] = mActivity.findViewById(R.id.roomType01View);
        mRoomTypeView[1] = mActivity.findViewById(R.id.roomType02View);
        mRoomTypeView[2] = mActivity.findViewById(R.id.roomType03View);
        mRoomTypeView[3] = mActivity.findViewById(R.id.roomType04View);

        int size = saleRoomList.size();

        for (int i = 0; i < MAX_OF_ROOMTYPE; i++)
        {
            if (i < size)
            {
                mRoomTypeView[i].setVisibility(View.VISIBLE);
                mRoomTypeView[i].setTag(saleRoomList.get(i));
                makeRoomTypeLayout(mRoomTypeView[i], saleRoomList.get(i), nights);
            } else
            {
                mRoomTypeView[i].setVisibility(View.GONE);
                mRoomTypeView[i].setTag(null);
            }

            mRoomTypeView[i].setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    selectRoomType(v, (SaleRoomInformation) v.getTag());
                }
            });
        }

        selectRoomType(mRoomTypeView[0], saleRoomList.get(0));
    }

    private void makeRoomTypeLayout(View view, SaleRoomInformation information, int nights)
    {
        if (view == null || information == null || nights <= 0)
        {
            return;
        }

        TextView roomTypeTextView = (TextView) view.findViewById(R.id.roomTypeTextView);
        TextView priceTextView = (TextView) view.findViewById(R.id.priceTextView);
        TextView optionTextView = (TextView) view.findViewById(R.id.optionTextView);
        TextView benefitTextView = (TextView) view.findViewById(R.id.benefitTextView);

        roomTypeTextView.setText(information.roomName);

        DecimalFormat comma = new DecimalFormat("###,##0");
        String currency = mActivity.getString(R.string.currency);
        String price = comma.format(information.averageDiscount);

        priceTextView.setText(price + currency);

        if (Util.isTextEmpty(information.option) == true)
        {
            optionTextView.setVisibility(View.GONE);
        } else
        {
            optionTextView.setVisibility(View.VISIBLE);
            optionTextView.setText(information.option);
        }

        if (Util.isTextEmpty(information.roomBenefit) == true)
        {
            benefitTextView.setVisibility(View.GONE);
        } else
        {
            benefitTextView.setVisibility(View.VISIBLE);
            benefitTextView.setText(information.roomBenefit);
        }
    }

    private void selectRoomType(View view, SaleRoomInformation saleRoomInformation)
    {
        if (view == null || saleRoomInformation == null)
        {
            return;
        }

        for (View roomView : mRoomTypeView)
        {
            if (roomView == view)
            {
                mSelectedSaleRoomInformation = saleRoomInformation;

                roomView.setSelected(true);
            } else
            {
                roomView.setSelected(false);
            }
        }
    }

    public int getBookingStatus()
    {
        return mBookingStatus;
    }

    private void setBookingStatus(int status)
    {
        mBookingStatus = status;

        TextView bookingView = (TextView) mActivity.findViewById(R.id.bookingTextView);
        View soldoutView = mActivity.findViewById(R.id.soldoutTextView);

        if (bookingView == null || soldoutView == null)
        {
            mActivity.restartApp();
            return;
        }

        switch (status)
        {
            case STATUS_NONE:
            {
                bookingView.setVisibility(View.VISIBLE);
                soldoutView.setVisibility(View.GONE);

                //				bookingView.setText("");
                break;
            }

            case STATUS_SEARCH_ROOM:
            {
                bookingView.setVisibility(View.VISIBLE);
                soldoutView.setVisibility(View.GONE);

                bookingView.setText(R.string.act_hotel_search_room);
                break;
            }

            case STATUS_BOOKING:
            {
                bookingView.setVisibility(View.VISIBLE);
                soldoutView.setVisibility(View.GONE);

                bookingView.setText(R.string.act_hotel_booking);
                break;
            }

            case STATUS_SOLD_OUT:
            {
                bookingView.setVisibility(View.GONE);
                soldoutView.setVisibility(View.VISIBLE);
                break;
            }
        }
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

        //		if (position < 0 || position >= mImageAdapter.getCount())
        //		{
        //			return;
        //		}

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

    public void setUserActionListener(HotelDetailActivity.OnUserActionListener listener)
    {
        mOnUserActionListener = listener;
    }

    protected boolean isUsedAnimatorApi()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    private void setRoomTypeLayoutEnabled(boolean enabled)
    {
        if (mRoomTypeLayout == null || mRoomTypeView == null)
        {
            return;
        }

        for (View view : mRoomTypeView)
        {
            if (view == null)
            {
                break;
            }

            view.setEnabled(enabled);
        }

        mRoomTypeBackgroundView.setEnabled(enabled);
    }

    private void hideRoomType()
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

        mRoomTypeBackgroundView.setAnimation(null);
        mRoomTypeLayout.setAnimation(null);

        mRoomTypeBackgroundView.setVisibility(View.GONE);

        if (isUsedAnimatorApi() == true)
        {
            mRoomTypeLayout.setVisibility(View.INVISIBLE);
            mRoomTypeLayout.setTranslationY(Util.dpToPx(mActivity, MAX_OF_ROOMTYPE * 92));
        } else
        {
            mRoomTypeLayout.setVisibility(View.GONE);
        }

        mAnimationStatus = ANIMATION_STATUS.HIDE_END;
    }

    public void showAnimationRoomType()
    {
        if (mAnimationState == ANIMATION_STATE.START && mAnimationStatus == ANIMATION_STATUS.SHOW)
        {
            return;
        }

        setBookingStatus(STATUS_NONE);

        if (isUsedAnimatorApi() == true)
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

            mObjectAnimator = ObjectAnimator.ofFloat(mRoomTypeLayout, "y", y, mBottomLayout.getTop() - mRoomTypeLayout.getHeight());
            mObjectAnimator.setDuration(300);

            mObjectAnimator.addListener(new AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    if (mRoomTypeLayout.getVisibility() != View.VISIBLE)
                    {
                        mRoomTypeLayout.setVisibility(View.VISIBLE);
                    }

                    mAnimationState = ANIMATION_STATE.START;
                    mAnimationStatus = ANIMATION_STATUS.SHOW;
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    if (mAnimationState != ANIMATION_STATE.CANCEL)
                    {
                        mAnimationStatus = ANIMATION_STATUS.SHOW_END;
                        mAnimationState = ANIMATION_STATE.END;

                        setRoomTypeLayoutEnabled(true);

                        setBookingStatus(STATUS_BOOKING);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation)
                {
                    mAnimationState = ANIMATION_STATE.CANCEL;
                }

                @Override
                public void onAnimationRepeat(Animator animation)
                {

                }
            });

            mObjectAnimator.start();
        } else
        {
            if (mRoomTypeLayout != null && mRoomTypeLayout.getVisibility() != View.VISIBLE)
            {
                mRoomTypeLayout.setVisibility(View.VISIBLE);

                mAnimationStatus = ANIMATION_STATUS.SHOW_END;
                mAnimationState = ANIMATION_STATE.END;

                setRoomTypeLayoutEnabled(true);

                setBookingStatus(STATUS_BOOKING);
            }
        }

        showAnimationFadeOut();
    }

    public void hideAnimationRoomType()
    {
        if (mAnimationState == ANIMATION_STATE.START && mAnimationStatus == ANIMATION_STATUS.HIDE)
        {
            return;
        }

        setBookingStatus(STATUS_NONE);

        if (isUsedAnimatorApi() == true)
        {
            final float y = mRoomTypeLayout.getY();

            if (mObjectAnimator != null)
            {
                if (mObjectAnimator.isRunning() == true)
                {
                    mObjectAnimator.cancel();
                    mObjectAnimator.removeAllListeners();
                }

                mObjectAnimator = null;
            }

            mObjectAnimator = ObjectAnimator.ofFloat(mRoomTypeLayout, "y", y, mBottomLayout.getTop());
            mObjectAnimator.setDuration(300);

            mObjectAnimator.addListener(new AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    mAnimationState = ANIMATION_STATE.START;
                    mAnimationStatus = ANIMATION_STATUS.HIDE;

                    setRoomTypeLayoutEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    if (mAnimationState != ANIMATION_STATE.CANCEL)
                    {
                        mAnimationStatus = ANIMATION_STATUS.HIDE_END;
                        mAnimationState = ANIMATION_STATE.END;

                        hideRoomType();

                        setBookingStatus(STATUS_SEARCH_ROOM);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation)
                {
                    mAnimationState = ANIMATION_STATE.CANCEL;
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
            setRoomTypeLayoutEnabled(false);

            mAnimationStatus = ANIMATION_STATUS.HIDE_END;
            mAnimationState = ANIMATION_STATE.END;

            hideRoomType();

            setBookingStatus(STATUS_SEARCH_ROOM);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

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

        if (mRoomTypeBackgroundView != null)
        {
            mRoomTypeBackgroundView.startAnimation(mAlphaAnimation);
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
                if (mRoomTypeBackgroundView.getVisibility() != View.VISIBLE)
                {
                    mRoomTypeBackgroundView.setVisibility(View.VISIBLE);
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

        if (mRoomTypeBackgroundView != null)
        {
            mRoomTypeBackgroundView.startAnimation(mAlphaAnimation);
        }
    }

    private enum ANIMATION_STATE
    {
        START,
        END,
        CANCEL
    }

    private enum ANIMATION_STATUS
    {
        SHOW,
        HIDE,
        SHOW_END,
        HIDE_END
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

            if (mOnUserActionListener != null)
            {
                mOnUserActionListener.onSelectedImagePosition(position);
            }

            mDailyViewPagerIndicator.setImageInformation(mHotelDetail.getImageInformationList().get(position).description, position);
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

    private float mLastFactor;

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
                mOnUserActionListener.showActionBar();
                return;
            }

            if (mStatusBarHeight == 0)
            {
                Rect rect = new Rect();
                mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

                mStatusBarHeight = rect.top;
            }

            View hotelTitleLayout = mListAdapter.getHotelTitleLayout();

            if (hotelTitleLayout == null)
            {
                return;
            }

            Rect rect = new Rect();
            hotelTitleLayout.getGlobalVisibleRect(rect);

            if (rect.top == rect.right)
            {

            } else
            {
                if (rect.top <= mStatusBarHeight)
                {
                    mOnUserActionListener.showActionBar();
                } else
                {
                    mOnUserActionListener.hideActionBar();
                }
            }

            View hotelGradeTextView = mListAdapter.getHotelGradeTextView();

            if (hotelGradeTextView == null)
            {
                return;
            }

            final int TOOLBAR_HEIGHT = Util.dpToPx(mActivity, 50);
            float max = ((float) mImageHeight - TOOLBAR_HEIGHT) / 2;
            float offset = rect.top - mStatusBarHeight - TOOLBAR_HEIGHT;
            float alphaFactor = offset / max;

            if (Util.isTextEmpty(mHotelDetail.hotelBenefit) == false)
            {
                if (Float.compare(alphaFactor, 0.0f) <= 0)
                {
                    if (mImageViewBlur.getVisibility() != View.VISIBLE)
                    {
                        mImageViewBlur.setVisibility(View.VISIBLE);
                    }
                } else
                {
                    if (mImageViewBlur.getVisibility() != View.INVISIBLE)
                    {
                        mImageViewBlur.setVisibility(View.INVISIBLE);
                    }
                }
            }

            if (Util.isOverAPI11() == true)
            {
                if (Float.compare(alphaFactor, 0.0f) <= 0)
                {
                    hotelGradeTextView.setAlpha(0.0f);
                } else
                {
                    hotelGradeTextView.setAlpha(alphaFactor);
                }
            } else
            {
                if (Float.compare(alphaFactor, 0.2f) <= 0)
                {
                    hotelGradeTextView.setVisibility(View.INVISIBLE);
                } else
                {
                    hotelGradeTextView.setVisibility(View.VISIBLE);
                }
            }

            View hotelNameTextView = mListAdapter.getHotelNameTextView();

            Rect firstRect = (Rect) hotelNameTextView.getTag();
            Integer firstWidth = (Integer) hotelNameTextView.getTag(hotelNameTextView.getId());

            if (firstRect != null && firstWidth != null)
            {
                final int TOOLBAR_TEXT_X = Util.dpToPx(mActivity, 60);
                float gradeMax = ((float) mImageHeight - TOOLBAR_HEIGHT) / 3;
                float gradeOffset = rect.top - mStatusBarHeight - TOOLBAR_HEIGHT;
                float xFactor = gradeOffset / gradeMax;
                float nameMax = firstRect.left - TOOLBAR_TEXT_X;

                if (Float.compare(xFactor, 1.0f) > 0)
                {
                    if (Util.isOverAPI11() == true)
                    {
                        hotelNameTextView.setTranslationX(0);
                    }
                    return;
                }

                if (Util.isOverAPI11() == true)
                {
                    if (Float.compare(xFactor, 0.0f) >= 0)
                    {
                        hotelNameTextView.setTranslationX(-nameMax * (1.0f - xFactor));
                    } else
                    {
                        hotelNameTextView.setTranslationX(-nameMax);
                    }
                } else
                {
                    if (Float.compare(xFactor, 0.0f) >= 0)
                    {
                        TranslateAnimation anim = new TranslateAnimation(mLastFactor, -nameMax * (1.0f - xFactor), 0.0f, 0.0f);
                        anim.setDuration(0);
                        anim.setFillAfter(true);
                        hotelNameTextView.startAnimation(anim);

                        mLastFactor = -nameMax * (1.0f - xFactor);
                    } else
                    {
                        TranslateAnimation anim = new TranslateAnimation(mLastFactor, -nameMax, 0.0f, 0.0f);
                        anim.setDuration(0);
                        anim.setFillAfter(true);
                        hotelNameTextView.startAnimation(anim);

                        mLastFactor = -nameMax;
                    }
                }

                float widthNameMax = firstRect.width() - firstWidth;

                ViewGroup.LayoutParams layoutParams = hotelNameTextView.getLayoutParams();
                if (Float.compare(xFactor, 0.0f) >= 0)
                {
                    layoutParams.width = (int) (firstWidth - (widthNameMax * (1.0f - xFactor)));
                } else
                {
                    layoutParams.width = firstWidth;
                }

                hotelNameTextView.setLayoutParams(layoutParams);
            }
        }
    };

    private View.OnTouchListener mEmptyViewOnTouchListener = new View.OnTouchListener()
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
                    int touchSlop = ViewConfiguration.get(mActivity).getScaledTouchSlop();

                    int x = (int) (mPrevX - event.getX());
                    int y = (int) (mPrevY - event.getY());

                    int distance = (int) Math.sqrt(x * x + y * y);

                    if (distance < touchSlop)
                    {
                        if (mOnUserActionListener != null)
                        {
                            mOnUserActionListener.onClickImage(mHotelDetail);

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