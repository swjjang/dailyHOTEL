package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetDetail;
import com.twoheart.dailyhotel.model.ImageInformation;
import com.twoheart.dailyhotel.model.PlaceDetail;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.TicketInformation;
import com.twoheart.dailyhotel.place.activity.PlaceDetailActivity;
import com.twoheart.dailyhotel.place.adapter.PlaceDetailImageViewPagerAdapter;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyLoopViewPager;
import com.twoheart.dailyhotel.widget.DailyPlaceDetailListView;
import com.twoheart.dailyhotel.widget.DailyViewPagerIndicator;

import java.util.ArrayList;

public class GourmetDetailLayout
{
    public static final int STATUS_NONE = 0;
    public static final int STATUS_SEARCH_TICKET = 1;
    public static final int STATUS_BOOKING = 2;
    public static final int STATUS_SOLD_OUT = 3;

    private static final int MAX_OF_TICKETTYPE = 4;

    protected PlaceDetail mPlaceDetail;
    protected BaseActivity mActivity;
    protected DailyLoopViewPager mViewPager;
    protected DailyViewPagerIndicator mDailyViewPagerIndicator;

    protected DailyPlaceDetailListView mListView;
    private GourmetDetailListAdapter mListAdapter;
    protected PlaceDetailImageViewPagerAdapter mImageAdapter;

    private TicketInformation mSelectedTicketInformation;
    protected int mImageHeight;
    protected int mBookingStatus; // 예약 진행 상태로 객실 찾기, 없음, 예약 진행
    protected PlaceDetailActivity.OnUserActionListener mOnUserActionListener;

    private RecyclerView mTicketTypeRecyclerView;
    private GourmetDetailRoomTypeListAdapter mTicketTypeListAdapter;
    private View mTicketTypeLayout;
    private View mBottomLayout;
    private View mTicketTypeBackgroundView;
    private View mImageViewBlur;
    private Constants.ANIMATION_STATUS mAnimationStatus = Constants.ANIMATION_STATUS.HIDE_END;
    private Constants.ANIMATION_STATE mAnimationState = Constants.ANIMATION_STATE.END;
    private ObjectAnimator mObjectAnimator;
    private AlphaAnimation mAlphaAnimation;
    private int mStatusBarHeight;
    private float mLastFactor;

    public GourmetDetailLayout(BaseActivity activity, String defaultImageUrl)
    {
        mActivity = activity;

        initLayout(activity, defaultImageUrl);
    }

    private void initLayout(BaseActivity activity, String defaultImageUrl)
    {
        mListView = (DailyPlaceDetailListView) activity.findViewById(R.id.hotelListView);
        mListView.setOnScrollListener(mOnScrollListener);

        // 이미지 ViewPage 넣기.
        mDailyViewPagerIndicator = (DailyViewPagerIndicator) activity.findViewById(R.id.viewpagerIndicator);

        mViewPager = (DailyLoopViewPager) activity.findViewById(R.id.defaultHotelImageView);
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);

        if (defaultImageUrl != null)
        {
            if (mImageAdapter == null)
            {
                mImageAdapter = new PlaceDetailImageViewPagerAdapter(mActivity);
            }

            ArrayList<ImageInformation> arrayList = new ArrayList<>();
            arrayList.add(new ImageInformation(defaultImageUrl, null));

            mImageAdapter.setData(arrayList);
            mViewPager.setAdapter(mImageAdapter);
        }

        mImageViewBlur = activity.findViewById(R.id.imageViewBlur);
        mImageViewBlur.setVisibility(View.INVISIBLE);

        mImageHeight = Util.getLCDWidth(activity);
        ViewGroup.LayoutParams layoutParams = mViewPager.getLayoutParams();
        layoutParams.height = mImageHeight;

        mTicketTypeLayout = activity.findViewById(R.id.ticketTypeLayout);
        mTicketTypeRecyclerView = (RecyclerView) mTicketTypeLayout.findViewById(R.id.ticketTypeRecyclerView);
        mTicketTypeRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        EdgeEffectColor.setEdgeGlowColor(mTicketTypeRecyclerView, activity.getResources().getColor(R.color.default_over_scroll_edge));
        mTicketTypeLayout.setVisibility(View.INVISIBLE);

        mBottomLayout = activity.findViewById(R.id.bottomLayout);

        mTicketTypeBackgroundView = activity.findViewById(R.id.roomTypeBackgroundView);

        mTicketTypeBackgroundView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideAnimationTicketInformationLayout();
            }
        });

        setBookingStatus(STATUS_NONE);
        hideTicketInformationLayout();
    }

    public void setDetail(PlaceDetail placeDetail, SaleTime saleTime, int imagePosition)
    {
        if (placeDetail == null)
        {
            return;
        }

        mPlaceDetail = placeDetail;

        if (mImageAdapter == null)
        {
            mImageAdapter = new PlaceDetailImageViewPagerAdapter(mActivity);
        }

        ArrayList<ImageInformation> imageInformationList = placeDetail.getImageInformationList();
        mImageAdapter.setData(imageInformationList);
        mViewPager.setAdapter(mImageAdapter);
        mDailyViewPagerIndicator.setTotalCount(imageInformationList.size());

        if (imageInformationList.size() > 0)
        {
            mDailyViewPagerIndicator.setImageInformation(imageInformationList.get(0).description, 0);
        } else
        {
            mDailyViewPagerIndicator.setImageInformation(null, 0);
        }

        if (mListAdapter == null)
        {
            mListAdapter = new GourmetDetailListAdapter(mActivity, (GourmetDetail) mPlaceDetail, saleTime, mOnUserActionListener, mEmptyViewOnTouchListener);
            mListView.setAdapter(mListAdapter);
        }

        setCurrentImage(imagePosition);

        hideTicketInformationLayout();

        // 호텔 sold out시
        View bookingView = mActivity.findViewById(R.id.bookingTextView);
        View soldoutView = mActivity.findViewById(R.id.soldoutTextView);

        // SOLD OUT 판단 조건.
        ArrayList<TicketInformation> ticketInformationList = placeDetail.getTicketInformation();

        if (ticketInformationList == null || ticketInformationList.size() == 0)
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
                                mOnUserActionListener.doBooking(mSelectedTicketInformation);
                            }
                            break;

                        case STATUS_SEARCH_TICKET:
                            if (mOnUserActionListener != null)
                            {
                                mOnUserActionListener.showTicketInformationLayout();
                            }
                            break;
                    }
                }
            });

            soldoutView.setVisibility(View.GONE);

            setBookingStatus(STATUS_SEARCH_TICKET);

            initTicketInformationLayout(0, ticketInformationList);
        }

        mListAdapter.notifyDataSetChanged();
    }

    protected void initTicketInformationLayout(int nights, ArrayList<TicketInformation> ticketInformationList)
    {
        if (ticketInformationList == null || ticketInformationList.size() == 0)
        {
            return;
        }

        // 객실 타입 세팅
        if (mTicketTypeListAdapter == null)
        {
            mSelectedTicketInformation = ticketInformationList.get(0);

            mTicketTypeListAdapter = new GourmetDetailRoomTypeListAdapter(mActivity, ticketInformationList, new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int position = mTicketTypeRecyclerView.getChildAdapterPosition(v);

                    if (position < 0)
                    {
                        return;
                    }

                    mSelectedTicketInformation = mTicketTypeListAdapter.getItem(position);
                    mTicketTypeListAdapter.setSelected(position);
                    mTicketTypeListAdapter.notifyDataSetChanged();

                    AnalyticsManager.getInstance(mActivity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
                        , AnalyticsManager.Action.TICKET_TYPE_ITEM_CLICKED, mSelectedTicketInformation.name, null);
                }
            });
        }

        int size = ticketInformationList.size();
        int height = Util.dpToPx(mActivity, 100) * size;
        final int maxHeight = Util.dpToPx(mActivity, 350);
        ViewGroup.LayoutParams layoutParams = mTicketTypeRecyclerView.getLayoutParams();

        if (height > maxHeight)
        {
            layoutParams.height = maxHeight;
        } else
        {
            layoutParams.height = height;
        }

        mTicketTypeRecyclerView.setLayoutParams(layoutParams);
        mTicketTypeRecyclerView.setAdapter(mTicketTypeListAdapter);
    }

    public int getBookingStatus()
    {
        return mBookingStatus;
    }

    public void setBookingStatus(int status)
    {
        mBookingStatus = status;

        TextView bookingView = (TextView) mBottomLayout.findViewById(R.id.bookingTextView);
        View soldoutView = mBottomLayout.findViewById(R.id.soldoutTextView);

        if (bookingView == null || soldoutView == null)
        {
            if (mOnUserActionListener != null)
            {
                mOnUserActionListener.finish();
            }
            return;
        }

        switch (status)
        {
            case STATUS_NONE:
            {
                bookingView.setVisibility(View.VISIBLE);
                soldoutView.setVisibility(View.GONE);
                break;
            }

            case STATUS_SEARCH_TICKET:
            {
                bookingView.setVisibility(View.VISIBLE);
                soldoutView.setVisibility(View.GONE);

                bookingView.setText(R.string.act_hotel_search_ticket);
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

    public void setUserActionListener(PlaceDetailActivity.OnUserActionListener listener)
    {
        mOnUserActionListener = listener;
    }

    private void setTicketInformationLayoutEnabled(boolean enabled)
    {
        mTicketTypeLayout.setEnabled(enabled);
        mTicketTypeRecyclerView.setEnabled(enabled);
        mTicketTypeBackgroundView.setEnabled(enabled);
    }

    public void hideTicketInformationLayout()
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

        mTicketTypeBackgroundView.setAnimation(null);
        mTicketTypeLayout.setAnimation(null);

        mTicketTypeBackgroundView.setVisibility(View.GONE);

        if (Util.isOverAPI12() == true)
        {
            mTicketTypeLayout.setVisibility(View.INVISIBLE);
            mTicketTypeLayout.setTranslationY(Util.dpToPx(mActivity, MAX_OF_TICKETTYPE * 92));
        } else
        {
            mTicketTypeLayout.setVisibility(View.GONE);
        }

        mAnimationStatus = Constants.ANIMATION_STATUS.HIDE_END;
    }

    public void showAnimationTicketInformationLayout()
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
            int height = mTicketTypeRecyclerView.getHeight() + Util.dpToPx(mActivity, 34);

            mTicketTypeLayout.setTranslationY(Util.dpToPx(mActivity, height));

            mObjectAnimator = ObjectAnimator.ofFloat(mTicketTypeLayout, "y", y, mBottomLayout.getTop() - height);
            mObjectAnimator.setDuration(300);

            mObjectAnimator.addListener(new AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    if (mTicketTypeLayout.getVisibility() != View.VISIBLE)
                    {
                        mTicketTypeLayout.setVisibility(View.VISIBLE);
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

                        setTicketInformationLayoutEnabled(true);

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
            if (mTicketTypeLayout != null && mTicketTypeLayout.getVisibility() != View.VISIBLE)
            {
                mTicketTypeLayout.setVisibility(View.VISIBLE);

                mAnimationStatus = Constants.ANIMATION_STATUS.SHOW_END;
                mAnimationState = Constants.ANIMATION_STATE.END;

                setTicketInformationLayoutEnabled(true);

                setBookingStatus(STATUS_BOOKING);
            }
        }

        showAnimationFadeOut();
    }

    public void hideAnimationTicketInformationLayout()
    {
        if (mAnimationState == Constants.ANIMATION_STATE.START && mAnimationStatus == Constants.ANIMATION_STATUS.HIDE)
        {
            return;
        }

        if (mPlaceDetail == null)
        {
            Util.restartApp(mActivity);
            return;
        }

        setBookingStatus(STATUS_NONE);

        if (Util.isOverAPI12() == true)
        {
            final float y = mTicketTypeLayout.getY();

            if (mObjectAnimator != null)
            {
                if (mObjectAnimator.isRunning() == true)
                {
                    mObjectAnimator.cancel();
                    mObjectAnimator.removeAllListeners();
                }

                mObjectAnimator = null;
            }

            mObjectAnimator = ObjectAnimator.ofFloat(mTicketTypeLayout, "y", y, mBottomLayout.getTop());
            mObjectAnimator.setDuration(300);

            mObjectAnimator.addListener(new AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    mAnimationState = Constants.ANIMATION_STATE.START;
                    mAnimationStatus = Constants.ANIMATION_STATUS.HIDE;

                    setTicketInformationLayoutEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    if (mAnimationState != Constants.ANIMATION_STATE.CANCEL)
                    {
                        mAnimationStatus = Constants.ANIMATION_STATUS.HIDE_END;
                        mAnimationState = Constants.ANIMATION_STATE.END;

                        hideTicketInformationLayout();

                        setBookingStatus(STATUS_SEARCH_TICKET);
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
            setTicketInformationLayoutEnabled(false);

            mAnimationStatus = Constants.ANIMATION_STATUS.HIDE_END;
            mAnimationState = Constants.ANIMATION_STATE.END;

            hideTicketInformationLayout();

            setBookingStatus(STATUS_SEARCH_TICKET);
        }

        AnalyticsManager.getInstance(mActivity).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_DETAIL);
        AnalyticsManager.getInstance(mActivity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
            , AnalyticsManager.Action.TICKET_TYPE_CANCEL_CLICKED, mPlaceDetail.name, null);
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

        if (mTicketTypeBackgroundView != null)
        {
            mTicketTypeBackgroundView.startAnimation(mAlphaAnimation);
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
                if (mTicketTypeBackgroundView.getVisibility() != View.VISIBLE)
                {
                    mTicketTypeBackgroundView.setVisibility(View.VISIBLE);
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

        if (mTicketTypeBackgroundView != null)
        {
            mTicketTypeBackgroundView.startAnimation(mAlphaAnimation);
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

            if (mOnUserActionListener != null)
            {
                mOnUserActionListener.onSelectedImagePosition(position);
            }

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
                mOnUserActionListener.showActionBar();
                return;
            }

            if (mStatusBarHeight == 0)
            {
                Rect rect = new Rect();
                mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

                mStatusBarHeight = rect.top;
            }

            View titleLaoyout = mListAdapter.getTitleLayout();

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
                    mOnUserActionListener.showActionBar();
                } else
                {
                    mOnUserActionListener.hideActionBar();
                }
            }

            View gradeTextView = mListAdapter.getGradeTextView();

            if (gradeTextView == null)
            {
                return;
            }

            final int TOOLBAR_HEIGHT = Util.dpToPx(mActivity, 50);
            float max = ((float) mImageHeight - TOOLBAR_HEIGHT) / 2;
            float offset = rect.top - mStatusBarHeight - TOOLBAR_HEIGHT;
            float alphaFactor = offset / max;

            if (Util.isTextEmpty(mPlaceDetail.benefit) == false)
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

            View nameTextView = mListAdapter.getNameTextView();
            Rect firstRect = (Rect) nameTextView.getTag();
            Integer firstWidth = (Integer) nameTextView.getTag(nameTextView.getId());

            if (firstRect != null && firstWidth != null)
            {
                final int TOOLBAR_TEXT_X = Util.dpToPx(mActivity, 60);
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
                    int touchSlop = ViewConfiguration.get(mActivity).getScaledTouchSlop();

                    int x = (int) (mPrevX - event.getX());
                    int y = (int) (mPrevY - event.getY());

                    int distance = (int) Math.sqrt(x * x + y * y);

                    if (distance < touchSlop)
                    {
                        if (mOnUserActionListener != null)
                        {
                            mOnUserActionListener.onClickImage(mPlaceDetail);

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