package com.twoheart.dailyhotel.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.activity.HotelDetailActivity;
import com.twoheart.dailyhotel.adapter.DetailImageViewPagerAdapter;
import com.twoheart.dailyhotel.model.DetailInformation;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.model.SaleRoomInformation;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.FontManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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

    private static final int NUMBER_OF_ROWSLIST = 9;
    private static final int MAX_OF_ROOMTYPE = 3;

    private HotelDetail mHotelDetail;
    private BaseActivity mActivity;
    private View mViewRoot;
    private LoopViewPager mViewPager;
    private View mHotelTitleLaout;
    private TextView mHotelGradeTextView;
    private TextView mHotelNameTextView;
    private TextView mActionBarTextView;
    private HotelDetailListView mListView;
    private DetailImageViewPagerAdapter mImageAdapter;
    private HotelDetailListAdapter mListAdapter;
    private SaleRoomInformation mSelectedSaleRoomInformation;

    private View mRoomTypeLayout;
    private View mBottomLayout;
    private View mRoomTypeBackgroundView;
    private View[] mRoomTypeView;
    private View mImageViewBlur;
    private View mGoogleMapLayout;

    private ANIMATION_STATUS mAnimationStatus = ANIMATION_STATUS.HIDE_END;
    private ANIMATION_STATE mAnimationState = ANIMATION_STATE.END;
    private ObjectAnimator mObjectAnimator;
    private AlphaAnimation mAlphaAnimation;

    private int mStatusBarHeight;
    private int mImageHeight;

    private View[] mDeatilView;
    private boolean[] mNeedRefreshData;

    private int mBookingStatus; // 예약 진행 상태로 객실 찾기, 없음, 예약 진행

    private HotelDetailActivity.OnUserActionListener mOnUserActionListener;
    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener()
    {
        private int mSelectedPosition = -1;
        private boolean mIsRefresh;
        private int mDirection;
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

            if (mScrollState == -1 && Util.isOverAPI11() == true)
            {
                stopAnimationImageView(true);

                AnimationImageView imageView = (AnimationImageView) mViewPager.findViewWidthPosition(position);

                if (imageView != null)
                {
                    imageView.startAnimation();
                }
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
            if (mScrollState == -1 || Float.compare(positionOffset, 0.0f) == 0 || positionOffsetPixels == 0)
            {
                return;
            }

            if (Util.isOverAPI11() == true)
            {
                stopAnimationImageView(true);
            }

            if (mScrollPosition == -1)
            {
                mScrollPosition = position;
            }

            if (mIsRefresh == false && mImageAdapter != null)
            {
                if (mDirection == 0)
                {
                    mDirection = Float.compare(positionOffset, 0.5f) <= 0 ? 1 : -1;
                }

                int nextPosition;

                if (mDirection >= 0)
                {
                    nextPosition = mScrollPosition + 1;

                    if (nextPosition >= mImageAdapter.getCount())
                    {
                        nextPosition = 0;
                    }
                } else
                {
                    nextPosition = mScrollPosition;
                }

                if (Util.isOverAPI11() == true)
                {
                    mImageAdapter.setDirection(mDirection);

                    AnimationImageView nextImageView = (AnimationImageView) mViewPager.findViewWidthPosition(nextPosition);

                    // 방향에 따라서 초기화가 달라야한다.
                    if (nextImageView != null)
                    {
                        nextImageView.initAnimation(mDirection < 0);
                        nextImageView.invalidate();
                    }
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {
            mScrollState = state;

            switch (state)
            {
                case ViewPager.SCROLL_STATE_IDLE:
                {
                    if (Util.isOverAPI11() == true)
                    {
                        stopAnimationImageView(true);

                        AnimationImageView imageView = (AnimationImageView) mViewPager.findViewWidthPosition(mSelectedPosition);

                        if (imageView != null)
                        {
                            imageView.startAnimation();
                        }
                    }

                    mDirection = 0;
                    mScrollPosition = -1;
                    break;
                }

                case ViewPager.SCROLL_STATE_DRAGGING:
                    if (Util.isOverAPI11() == true)
                    {
                        stopAnimationImageView(true);
                    }
                    break;

                case ViewPager.SCROLL_STATE_SETTLING:
                    break;
            }

            //			{
            //
            //			}
        }
    };

    ;
    private float mLastFactor;

    ;
    private OnScrollListener mOnScrollListener = new OnScrollListener()
    {
        //		private int mDirection;
        //		private int mScrollState;
        //		private float mAlphaFactor;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState)
        {
            //			mScrollState = scrollState;
            //
            //			ExLog.d("scrollState : " + scrollState);
            //
            //			if(scrollState == OnScrollListener.SCROLL_STATE_IDLE)
            //			{
            //				if(mListView.getFirstVisiblePosition() == 0)
            //				{
            //					switch(mDirection)
            //					{
            //						case MotionEvent.ACTION_UP:
            //
            //							if(Float.compare(mAlphaFactor, 0.5f) <= 0)
            //							{
            //								mListView.smoothScrollToPosition(1);
            //							}
            //							break;
            //
            //						case MotionEvent.ACTION_DOWN:
            //							if(Float.compare(mAlphaFactor, 0.4f) >= 0)
            //							{
            //								mListView.smoothScrollToPosition(0);
            //							}
            //							break;
            //					}
            //				}
            //			}
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
                if (mActionBarTextView != null)
                {
                    mActionBarTextView.setVisibility(View.VISIBLE);
                }

                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.stopAutoSlide();
                }
                return;
            }

            if (mStatusBarHeight == 0)
            {
                Rect rect = new Rect();
                mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

                mStatusBarHeight = rect.top;
            }

            if (mHotelTitleLaout == null)
            {
                return;
            }

            Rect rect = new Rect();
            mHotelTitleLaout.getGlobalVisibleRect(rect);

            if (rect.top == rect.right)
            {

            } else
            {
                if (rect.top <= mStatusBarHeight)
                {
                    if (mActionBarTextView != null)
                    {
                        mActionBarTextView.setVisibility(View.VISIBLE);
                    }
                } else
                {
                    if (mActionBarTextView != null)
                    {
                        mActionBarTextView.setVisibility(View.INVISIBLE);
                    }

                    if (mOnUserActionListener != null)
                    {
                        mOnUserActionListener.startAutoSlide();
                    }
                }
            }

            if (mHotelGradeTextView == null)
            {
                return;
            }

            //			float offset = rect.top - mStatusBarHeight - Util.dpToPx(mActivity, 56);
            //			float max = mImageHeight - Util.dpToPx(mActivity, 56);
            //			float alphaFactor = offset / max;

            //			float max = (mImageHeight - Util.dpToPx(mActivity, 56)) / 2;
            //			float offset = rect.top - mStatusBarHeight - Util.dpToPx(mActivity, 56) - max;
            //			float alphaFactor = offset / max;

            float max = ((float) mImageHeight - Util.dpToPx(mActivity, 56)) / 2;
            float offset = rect.top - mStatusBarHeight - Util.dpToPx(mActivity, 56);
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
                    mHotelGradeTextView.setAlpha(0.0f);
                } else
                {
                    mHotelGradeTextView.setAlpha(alphaFactor);
                }
            } else
            {
                if (Float.compare(alphaFactor, 0.2f) <= 0)
                {
                    mHotelGradeTextView.setVisibility(View.INVISIBLE);
                } else
                {
                    mHotelGradeTextView.setVisibility(View.VISIBLE);
                }
            }

            Rect firstRect = (Rect) mHotelNameTextView.getTag();

            if (firstRect != null)
            {
                float gradeMax = ((float) mImageHeight - Util.dpToPx(mActivity, 56)) / 3;
                float gradeOffset = rect.top - mStatusBarHeight - Util.dpToPx(mActivity, 56);
                float xFactor = gradeOffset / gradeMax;
                float nameMax = firstRect.left - Util.dpToPx(mActivity, 55);

                if (Float.compare(xFactor, 1.0f) > 0)
                {
                    if (Util.isOverAPI11() == true)
                    {
                        mHotelNameTextView.setTranslationX(0);
                    }
                    return;
                }

                if (Util.isOverAPI11() == true)
                {
                    if (Float.compare(xFactor, 0.0f) >= 0)
                    {
                        mHotelNameTextView.setTranslationX(-nameMax * (1.0f - xFactor));
                    } else
                    {
                        mHotelNameTextView.setTranslationX(-nameMax);
                    }
                } else
                {
                    if (Float.compare(xFactor, 0.0f) >= 0)
                    {
                        TranslateAnimation anim = new TranslateAnimation(mLastFactor, -nameMax * (1.0f - xFactor), 0.0f, 0.0f);
                        anim.setDuration(0);
                        anim.setFillAfter(true);
                        mHotelNameTextView.startAnimation(anim);

                        mLastFactor = -nameMax * (1.0f - xFactor);
                    } else
                    {
                        TranslateAnimation anim = new TranslateAnimation(mLastFactor, -nameMax, 0.0f, 0.0f);
                        anim.setDuration(0);
                        anim.setFillAfter(true);
                        mHotelNameTextView.startAnimation(anim);

                        mLastFactor = -nameMax;
                    }
                }
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
                    if (mOnUserActionListener != null)
                    {
                        mOnUserActionListener.stopAutoSlide();
                    }

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
                            mOnUserActionListener.stopAutoSlide();
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

                    if (mOnUserActionListener != null)
                    {
                        mOnUserActionListener.startAutoSlide();
                    }
                    break;
                }

                case MotionEvent.ACTION_MOVE:
                {
                    if (mOnUserActionListener != null)
                    {
                        mOnUserActionListener.stopAutoSlide();
                    }

                    float x = event.getX();
                    float y = event.getY();

                    if (mMoveState == 0)
                    {
                        if (Math.abs(x - mPrevX) >= Math.abs(y - mPrevY))
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

    public HotelDetailLayout(BaseActivity activity, String defaultImageUrl)
    {
        mActivity = activity;

        initLayout(activity, defaultImageUrl);
    }

    private void initLayout(Activity activity, String defaultImageUrl)
    {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewRoot = inflater.inflate(R.layout.layout_hoteldetail, null, false);

        mActionBarTextView = (TextView) mViewRoot.findViewById(R.id.actionBarTextView);
        mActionBarTextView.setVisibility(View.INVISIBLE);

        mListView = (HotelDetailListView) mViewRoot.findViewById(R.id.hotelListView);
        mListView.setOnScrollListener(mOnScrollListener);

        // 이미지 ViewPage 넣기.
        mViewPager = (LoopViewPager) mViewRoot.findViewById(R.id.defaultHotelImageView);
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);
        mViewPager.setScrollDurationFactor(4);

        if (defaultImageUrl != null)
        {
            if (mImageAdapter == null)
            {
                mImageAdapter = new DetailImageViewPagerAdapter(mActivity);
            }

            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(defaultImageUrl);

            mImageAdapter.setData(arrayList);
            mViewPager.setAdapter(mImageAdapter);
        }

        mImageViewBlur = mViewRoot.findViewById(R.id.imageViewBlur);
        mImageViewBlur.setVisibility(View.INVISIBLE);

        mImageHeight = Util.getLCDWidth(activity);
        LayoutParams layoutParams = (LayoutParams) mViewPager.getLayoutParams();
        layoutParams.height = mImageHeight;

        mRoomTypeLayout = mViewRoot.findViewById(R.id.roomTypeLayout);
        mRoomTypeLayout.setVisibility(View.INVISIBLE);

        mRoomTypeView = new View[MAX_OF_ROOMTYPE];

        mBottomLayout = mViewRoot.findViewById(R.id.bottomLayout);

        mRoomTypeBackgroundView = mViewRoot.findViewById(R.id.roomTypeBackgroundView);

        mRoomTypeBackgroundView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideAnimationRoomType();
            }
        });

        try
        {
            mGoogleMapLayout = (ViewGroup) inflater.inflate(R.layout.view_map, null, false);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        setBookingStatus(STATUS_NONE);
        hideRoomType();
    }

    public View getView()
    {
        return mViewRoot;
    }

    public void setHotelDetail(HotelDetail hotelDetail, int imagePosition)
    {
        if (hotelDetail == null)
        {
            return;
        }

        mHotelDetail = hotelDetail;

        // 호텔 상세 정보를 얻어와서 리스트 개수가 몇개 필요한지 검색한다.
        if (mNeedRefreshData == null)
        {
            mNeedRefreshData = new boolean[NUMBER_OF_ROWSLIST];
        }

        for (int i = 0; i < NUMBER_OF_ROWSLIST; i++)
        {
            mNeedRefreshData[i] = true;
        }

        if (mDeatilView == null)
        {
            mDeatilView = new View[NUMBER_OF_ROWSLIST];
        }

        mActionBarTextView.setText(hotelDetail.hotelName);

        if (mImageAdapter == null)
        {
            mImageAdapter = new DetailImageViewPagerAdapter(mActivity);
        }

        mImageAdapter.setOnAnimationListener(mOnUserActionListener);

        mImageAdapter.setData(hotelDetail.getImageUrlList());
        mViewPager.setAdapter(mImageAdapter);

        if (mListAdapter == null)
        {
            mListAdapter = new HotelDetailListAdapter((FragmentActivity) mActivity);
            mListView.setAdapter(mListAdapter);
        }

        setCurrentImage(imagePosition);

        hideRoomType();

        if (mOnUserActionListener != null)
        {
            mOnUserActionListener.startAutoSlide();
        }

        // 호텔 sold out시
        View bookingView = mViewRoot.findViewById(R.id.bookingTextView);
        View soldoutView = mViewRoot.findViewById(R.id.soldoutTextView);

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

        if (mListAdapter != null)
        {
            mListAdapter.notifyDataSetChanged();
        }
    }

    private void initRoomTypeLayout(int nights, ArrayList<SaleRoomInformation> saleRoomList)
    {
        if (saleRoomList == null || saleRoomList.size() == 0)
        {
            return;
        }

        // 객실 타입 세팅
        mRoomTypeView[0] = mViewRoot.findViewById(R.id.roomType01View);
        mRoomTypeView[1] = mViewRoot.findViewById(R.id.roomType02View);
        mRoomTypeView[2] = mViewRoot.findViewById(R.id.roomType03View);

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

        TextView bookingView = (TextView) mBottomLayout.findViewById(R.id.bookingTextView);
        View soldoutView = mBottomLayout.findViewById(R.id.soldoutTextView);

        if (bookingView == null || soldoutView == null)
        {
            mActivity.restartApp();
            return;
        }

        switch (status)
        {
            case STATUS_NONE:
            {
                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.stopAutoSlide();
                }

                bookingView.setVisibility(View.VISIBLE);
                soldoutView.setVisibility(View.GONE);

                //				bookingView.setText("");
                break;
            }

            case STATUS_SEARCH_ROOM:
            {
                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.startAutoSlide();
                }

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

    public void startAnimationImageView()
    {
        if (Util.isOverAPI11() == true)
        {
            int position = mViewPager.getCurrentItem();

            AnimationImageView imageView = (AnimationImageView) mViewPager.findViewWidthPosition(position);

            if (imageView != null)
            {
                imageView.startAnimation();
            }
        }
    }

    public void stopAnimationImageView(boolean initDuration)
    {
        if (Util.isOverAPI11() == true)
        {
            int count = mViewPager.getChildCount();

            for (int i = 0; i < count; i++)
            {
                AnimationImageView imageView = (AnimationImageView) mViewPager.getChildAt(i);
                imageView.stopAnimation(initDuration);
            }
        }
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
            mRoomTypeLayout.setTranslationY(Util.dpToPx(mActivity, 276));
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

            //			ArrayList<SaleRoomInformation> arrayList = mHotelDetail.getSaleRoomList();
            //
            //			if(arrayList == null || arrayList.size() == 0)
            //			{
            //				return;
            //			}
            //
            //			int height = arrayList.size() * Util.dpToPx(mActivity, 92);
            //
            //			TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, height, 0);
            //			translateAnimation.setDuration(300);
            //			translateAnimation.setFillBefore(true);
            //			translateAnimation.setFillAfter(true);
            //			translateAnimation.setInterpolator(mActivity, android.R.anim.decelerate_interpolator);
            //
            //			translateAnimation.setAnimationListener(new AnimationListener()
            //			{
            //				@Override
            //				public void onAnimationStart(Animation animation)
            //				{
            //					if (mRoomTypeLayout.getVisibility() != View.VISIBLE)
            //					{
            //						mRoomTypeLayout.setVisibility(View.VISIBLE);
            //					}
            //
            //					mAnimationState = ANIMATION_STATE.START;
            //					mAnimationStatus = ANIMATION_STATUS.SHOW;
            //				}
            //
            //				@Override
            //				public void onAnimationRepeat(Animation animation)
            //				{
            //
            //				}
            //
            //				@Override
            //				public void onAnimationEnd(Animation animation)
            //				{
            //					mAnimationStatus = ANIMATION_STATUS.SHOW_END;
            //					mAnimationState = ANIMATION_STATE.END;
            //
            //					if (mRoomTypeLayout != null)
            //					{
            //						mRoomTypeLayout.startAnimation(null);
            //					}
            //
            //					setRoomTypeLayoutEnabled(true);
            //
            //					setBookingStatus(STATUS_BOOKING);
            //				}
            //			});
            //
            //			if (mRoomTypeLayout != null)
            //			{
            //				mRoomTypeLayout.startAnimation(translateAnimation);
            //			}
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

            //			TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, mRoomTypeLayout.getHeight());
            //			translateAnimation.setDuration(300);
            //			translateAnimation.setFillBefore(true);
            //			translateAnimation.setFillAfter(true);
            //			translateAnimation.setInterpolator(mActivity, android.R.anim.decelerate_interpolator);
            //
            //			translateAnimation.setAnimationListener(new AnimationListener()
            //			{
            //				@Override
            //				public void onAnimationStart(Animation animation)
            //				{
            //					mAnimationState = ANIMATION_STATE.START;
            //					mAnimationStatus = ANIMATION_STATUS.HIDE;
            //
            //					setRoomTypeLayoutEnabled(false);
            //				}
            //
            //				@Override
            //				public void onAnimationRepeat(Animation animation)
            //				{
            //				}
            //
            //				@Override
            //				public void onAnimationEnd(Animation animation)
            //				{
            //					mAnimationStatus = ANIMATION_STATUS.HIDE_END;
            //					mAnimationState = ANIMATION_STATE.END;
            //
            //					hideRoomType();
            //
            //					setBookingStatus(STATUS_SEARCH_ROOM);
            //				}
            //			});
            //
            //			if (mRoomTypeLayout != null)
            //			{
            //				mRoomTypeLayout.startAnimation(translateAnimation);
            //			}
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

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Adapter
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    private class HotelDetailListAdapter extends BaseAdapter
    {
        private FragmentActivity mFragmentActivity;
        private GoogleMap mGoogleMap;
        private SupportMapFragment mMapFragment;

        public HotelDetailListAdapter(FragmentActivity activity)
        {
            mFragmentActivity = activity;
        }

        @Override
        public Object getItem(int position)
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int getCount()
        {
            if (mDeatilView == null)
            {
                return 0;
            } else
            {
                int count = NUMBER_OF_ROWSLIST;

                if (Util.isTextEmpty(mHotelDetail.hotelBenefit) == true)
                {
                    count--;
                }

                return count;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view = null;

            LayoutInflater layoutInflater = (LayoutInflater) mFragmentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            switch (position)
            {
                // 빈화면
                case 0:
                    if (mDeatilView[0] == null)
                    {
                        mDeatilView[0] = layoutInflater.inflate(R.layout.list_row_detail01, parent, false);
                    }

                    if (mNeedRefreshData[0] == true)
                    {
                        mNeedRefreshData[0] = false;

                        getDetail00View(mDeatilView[0]);
                    }

                    view = mDeatilView[0];
                    break;

                // 호텔 등급과 이름.
                case 1:
                    if (mDeatilView[1] == null)
                    {
                        mDeatilView[1] = layoutInflater.inflate(R.layout.list_row_detail02, parent, false);
                    }

                    if (mNeedRefreshData[1] == true)
                    {
                        mNeedRefreshData[1] = false;

                        getDetail01View(mDeatilView[1], mHotelDetail);
                    }

                    view = mDeatilView[1];
                    break;

                // 주소 및 맵
                case 2:
                    if (mDeatilView[2] == null)
                    {
                        mDeatilView[2] = layoutInflater.inflate(R.layout.list_row_detail03, parent, false);
                    }

                    if (mNeedRefreshData[2] == true)
                    {
                        mNeedRefreshData[2] = false;

                        getDetail02View(mDeatilView[2], mHotelDetail);
                    }

                    view = mDeatilView[2];
                    break;

                // D Benefit or 데일리's comment
                case 3:
                    if (Util.isTextEmpty(mHotelDetail.hotelBenefit) == false)
                    {
                        if (mDeatilView[3] == null)
                        {
                            mDeatilView[3] = layoutInflater.inflate(R.layout.list_row_detail_benefit, parent, false);
                            getDetailBenefitView(mDeatilView[3], mHotelDetail);
                        }

                        if (mNeedRefreshData[3] == true)
                        {
                            mNeedRefreshData[3] = false;

                            getDetailBenefitView(mDeatilView[3], mHotelDetail);
                        }

                        view = mDeatilView[3];
                    } else
                    {
                        view = makeCommentView(layoutInflater, parent);
                    }
                    break;

                // 데일리 추천이유 or 호텔 정보
                case 4:
                    if (Util.isTextEmpty(mHotelDetail.hotelBenefit) == false)
                    {
                        view = makeCommentView(layoutInflater, parent);
                    } else
                    {
                        view = makeHotelInfoView(layoutInflater, parent);
                    }
                    break;

                // 호텔 정보 or 호텔 더보기 정보
                case 5:
                    if (Util.isTextEmpty(mHotelDetail.hotelBenefit) == false)
                    {
                        view = makeHotelInfoView(layoutInflater, parent);
                    } else
                    {
                        view = makeHotelMoreInfoView(layoutInflater, parent);
                    }
                    break;

                // 호텔 더보기 정보 or 확인 사항
                case 6:
                    if (Util.isTextEmpty(mHotelDetail.hotelBenefit) == false)
                    {
                        view = makeHotelMoreInfoView(layoutInflater, parent);
                    } else
                    {
                        view = makeCheckListView(layoutInflater, parent);
                    }
                    break;

                // 확인 사항 or 카카오톡 문의
                case 7:
                    if (Util.isTextEmpty(mHotelDetail.hotelBenefit) == false)
                    {
                        view = makeCheckListView(layoutInflater, parent);
                    } else
                    {
                        view = makeKakaoView(layoutInflater, parent);
                    }
                    break;

                // 카카오톡 문의
                case 8:
                    view = makeKakaoView(layoutInflater, parent);
                    break;
            }

            return view;
        }

        private View makeCommentView(LayoutInflater layoutInflater, ViewGroup parent)
        {
            if (layoutInflater == null || parent == null)
            {
                return null;
            }

            if (mDeatilView[4] == null)
            {
                mDeatilView[4] = layoutInflater.inflate(R.layout.list_row_detail04, parent, false);
            }

            if (mNeedRefreshData[4] == true)
            {
                mNeedRefreshData[4] = false;

                getDetail04View(layoutInflater, (ViewGroup) mDeatilView[4], mHotelDetail);
            }

            return mDeatilView[4];
        }

        private View makeHotelInfoView(LayoutInflater layoutInflater, ViewGroup parent)
        {
            if (layoutInflater == null || parent == null)
            {
                return null;
            }

            if (mDeatilView[5] == null)
            {
                mDeatilView[5] = layoutInflater.inflate(R.layout.list_row_detail05, parent, false);
            }

            if (mNeedRefreshData[5] == true)
            {
                mNeedRefreshData[5] = false;

                getDeatil05View(layoutInflater, (ViewGroup) mDeatilView[5], mHotelDetail);
            }

            return mDeatilView[5];
        }

        private View makeHotelMoreInfoView(LayoutInflater layoutInflater, ViewGroup parent)
        {
            if (layoutInflater == null || parent == null)
            {
                return null;
            }

            if (mDeatilView[6] == null)
            {
                mDeatilView[6] = layoutInflater.inflate(R.layout.list_row_detail_more, parent, false);
            }

            if (mNeedRefreshData[6] == true)
            {
                mNeedRefreshData[6] = false;

                getDeatil06View(layoutInflater, (ViewGroup) mDeatilView[6], mHotelDetail);
            }

            return mDeatilView[6];
        }

        private View makeCheckListView(LayoutInflater layoutInflater, ViewGroup parent)
        {
            if (layoutInflater == null || parent == null)
            {
                return null;
            }

            if (mDeatilView[7] == null)
            {
                mDeatilView[7] = layoutInflater.inflate(R.layout.list_row_detail06, parent, false);
            }

            if (mNeedRefreshData[7] == true)
            {
                mNeedRefreshData[7] = false;

                getDeatil07View(layoutInflater, (ViewGroup) mDeatilView[7], mHotelDetail);
            }

            return mDeatilView[7];
        }

        private View makeKakaoView(LayoutInflater layoutInflater, ViewGroup parent)
        {
            if (layoutInflater == null || parent == null)
            {
                return null;
            }

            if (mDeatilView[8] == null)
            {
                mDeatilView[8] = layoutInflater.inflate(R.layout.list_row_detail07, parent, false);
            }

            if (mNeedRefreshData[8] == true)
            {
                mNeedRefreshData[8] = false;

                getDeatil08View(mDeatilView[8]);
            }

            return mDeatilView[8];
        }

        /**
         * 빈화면
         *
         * @param view
         * @return
         */
        private View getDetail00View(View view)
        {
            View emptyView = view.findViewById(R.id.imageEmptyHeight);
            emptyView.getLayoutParams().height = mImageHeight;

            emptyView.setClickable(true);
            emptyView.setOnTouchListener(mEmptyViewOnTouchListener);

            return view;
        }

        /**
         * 호텔 등급 및 이름
         *
         * @param view
         * @param hotelDetail
         * @return
         */
        private View getDetail01View(View view, HotelDetail hotelDetail)
        {
            mHotelTitleLaout = view.findViewById(R.id.hotelTitleLayout);

            // 등급
            mHotelGradeTextView = (TextView) view.findViewById(R.id.hotelGradeTextView);
            mHotelGradeTextView.setVisibility(View.VISIBLE);

            Hotel.HotelGrade hotelGrade = Hotel.HotelGrade.valueOf(hotelDetail.grade);
            mHotelGradeTextView.setText(hotelGrade.getName(mFragmentActivity));
            mHotelGradeTextView.setBackgroundResource(hotelGrade.getColorResId());

            // 호텔명
            mHotelNameTextView = (TextView) view.findViewById(R.id.hotelNameTextView);
            mHotelNameTextView.setText(hotelDetail.hotelName);

            if (mHotelNameTextView.getTag() == null)
            {
                mHotelNameTextView.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Rect rect = new Rect();
                        mHotelNameTextView.getGlobalVisibleRect(rect);
                        mHotelNameTextView.setTag(rect);

                        Rect rect01 = new Rect();
                        mActionBarTextView.getGlobalVisibleRect(rect01);

                        int actionBarWidth = rect01.width() - mActionBarTextView.getPaddingLeft();
                        int paddingRight = actionBarWidth - rect.width();

                        mActionBarTextView.setPadding(mActionBarTextView.getPaddingLeft(), 0, paddingRight, 0);
                    }
                });
            }

            TextView satisfactionView = (TextView) view.findViewById(R.id.satisfactionView);

            // 만족도
            if (Util.isTextEmpty(hotelDetail.satisfaction) == true)
            {
                satisfactionView.setVisibility(View.GONE);
            } else
            {
                satisfactionView.setVisibility(View.VISIBLE);
                satisfactionView.setText(hotelDetail.satisfaction);
            }

            return view;
        }

        /**
         * 호텔 주소 및 맵
         *
         * @param view
         * @param hotelDetail
         * @return
         */
        private View getDetail02View(final View view, HotelDetail hotelDetail)
        {
            // 주소지
            final TextView hotelAddressTextView01 = (TextView) view.findViewById(R.id.hotelAddressTextView01);
            final TextView hotelAddressTextView02 = (TextView) view.findViewById(R.id.hotelAddressTextView02);
            final TextView hotelAddressTextView03 = (TextView) view.findViewById(R.id.hotelAddressTextView03);

            hotelAddressTextView02.setText(null);
            hotelAddressTextView02.setVisibility(View.GONE);

            hotelAddressTextView03.setText(null);
            hotelAddressTextView03.setVisibility(View.GONE);

            final String address = hotelDetail.address;

            hotelAddressTextView01.setText(address);
            hotelAddressTextView01.post(new Runnable()
            {
                @Override
                public void run()
                {
                    Layout layout = hotelAddressTextView01.getLayout();

                    if (layout == null || Util.isTextEmpty(address) == true)
                    {
                        return;
                    }

                    String[] lineString = new String[2];
                    int lineCount = layout.getLineCount();

                    // 한줄이상인 경우.
                    if (lineCount == 2)
                    {
                        int firstLineEnd = layout.getLineEnd(0);

                        try
                        {
                            if (firstLineEnd < address.length())
                            {
                                lineString[0] = address.substring(firstLineEnd, address.length());

                                hotelAddressTextView02.setVisibility(View.VISIBLE);
                                hotelAddressTextView02.setText(lineString[0]);
                            }
                        } catch (Exception e)
                        {
                            ExLog.d(e.toString());
                        }
                    } else if (lineCount > 2)
                    {
                        int firstLineEnd = layout.getLineEnd(0);
                        int secondLineEnd = layout.getLineEnd(1);

                        try
                        {
                            if (firstLineEnd < address.length())
                            {
                                lineString[0] = address.substring(firstLineEnd, secondLineEnd);

                                hotelAddressTextView02.setVisibility(View.VISIBLE);
                                hotelAddressTextView02.setText(lineString[0]);
                            }

                            if (secondLineEnd < address.length())
                            {
                                lineString[1] = address.substring(secondLineEnd, address.length());

                                hotelAddressTextView03.setVisibility(View.VISIBLE);
                                hotelAddressTextView03.setText(lineString[1]);
                            }
                        } catch (Exception e)
                        {
                            ExLog.d(e.toString());
                        }
                    }
                }
            });

            FrameLayout googleMapLayout = (FrameLayout) view.findViewById(R.id.googleMapLayout);

            if (Util.isInstallGooglePlayService(mFragmentActivity) == true)
            {
                if (googleMapLayout.getBackground() == null)
                {
                    try
                    {
                        googleMapSetting(googleMapLayout);
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());

                        googleMapLayout.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Util.installGooglePlayService((BaseActivity) mFragmentActivity);
                            }
                        });
                    }
                }
            } else
            {
                googleMapLayout.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Util.installGooglePlayService((BaseActivity) mFragmentActivity);
                    }
                });
            }

            return view;
        }

        private void googleMapSetting(final FrameLayout googleMapLayout)
        {
            if (googleMapLayout == null)
            {
                return;
            }

            googleMapLayout.setOnClickListener(null);
            googleMapLayout.removeAllViews();

            if (mGoogleMapLayout == null)
            {
                LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mGoogleMapLayout = (ViewGroup) inflater.inflate(R.layout.view_map, null, false);
            }

            googleMapLayout.addView(mGoogleMapLayout);

            mMapFragment = (SupportMapFragment) mFragmentActivity.getSupportFragmentManager().findFragmentById(R.id.mapFragment);
            mMapFragment.getMapAsync(new OnMapReadyCallback()
            {
                @Override
                public void onMapReady(GoogleMap googleMap)
                {
                    mGoogleMap = googleMap;

                    final LatLng latlng = new LatLng(mHotelDetail.latitude, mHotelDetail.longitude);

                    Marker marker = googleMap.addMarker(new MarkerOptions().position(latlng));
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.info_ic_map_large));

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latlng).zoom(15).build();
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    mGoogleMap.setOnMarkerClickListener(new OnMarkerClickListener()
                    {
                        @Override
                        public boolean onMarkerClick(Marker marker)
                        {
                            if (mOnUserActionListener != null)
                            {
                                mOnUserActionListener.showMap();
                            }

                            return true;
                        }
                    });

                    mGoogleMap.getUiSettings().setAllGesturesEnabled(false);
                    mGoogleMap.setOnMapClickListener(new OnMapClickListener()
                    {
                        @Override
                        public void onMapClick(LatLng latlng)
                        {
                            if (mOnUserActionListener != null)
                            {
                                mOnUserActionListener.showMap();
                            }
                        }
                    });

                    mGoogleMap.setOnMapLoadedCallback(new OnMapLoadedCallback()
                    {
                        @Override
                        public void onMapLoaded()
                        {
                            if (mGoogleMap == null)
                            {
                                return;
                            }

                            mGoogleMap.snapshot(new SnapshotReadyCallback()
                            {
                                @Override
                                public void onSnapshotReady(Bitmap bitmap)
                                {
                                    if (Util.isOverAPI16() == true)
                                    {
                                        googleMapLayout.setBackground(new BitmapDrawable(mFragmentActivity.getResources(), bitmap));
                                    } else
                                    {
                                        googleMapLayout.setBackgroundDrawable(new BitmapDrawable(mFragmentActivity.getResources(), bitmap));
                                    }

                                    mFragmentActivity.getSupportFragmentManager().beginTransaction().remove(mMapFragment).commitAllowingStateLoss();
                                    googleMapLayout.removeAllViews();

                                    mMapFragment = null;
                                    mGoogleMap = null;
                                    mGoogleMapLayout = null;

                                    googleMapLayout.setOnClickListener(new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View v)
                                        {
                                            if (mOnUserActionListener != null)
                                            {
                                                mOnUserActionListener.showMap();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        }

        /**
         * 호텔 Benefit
         *
         * @param view
         * @return
         */
        private View getDetailBenefitView(View view, HotelDetail hotelDetail)
        {
            if (view == null || hotelDetail == null)
            {
                return view;
            }

            final TextView textView1Line = (TextView) view.findViewById(R.id.benefit1LineTextView);
            final TextView textView2Line = (TextView) view.findViewById(R.id.benefit2LineTextView);

            textView2Line.setText(null);
            textView2Line.setVisibility(View.GONE);

            final String benefit = hotelDetail.hotelBenefit;

            textView1Line.setText(benefit);
            textView1Line.post(new Runnable()
            {
                @Override
                public void run()
                {
                    Layout layout = textView1Line.getLayout();

                    if (layout == null || Util.isTextEmpty(benefit) == true)
                    {
                        return;
                    }

                    String[] lineString = new String[1];
                    int lineCount = layout.getLineCount();

                    // 한줄이상인 경우.
                    if (lineCount > 1)
                    {
                        int firstLineEnd = layout.getLineEnd(0);

                        try
                        {
                            if (firstLineEnd < benefit.length())
                            {
                                lineString[0] = benefit.substring(firstLineEnd, benefit.length());

                                textView2Line.setVisibility(View.VISIBLE);
                                textView2Line.setText(lineString[0]);
                            }
                        } catch (Exception e)
                        {
                            ExLog.d(e.toString());
                        }
                    }
                }
            });

            return view;
        }

        /**
         * 데일리 추천 이유
         *
         * @param view
         * @return
         */
        private View getDetail04View(LayoutInflater layoutInflater, ViewGroup viewGroup, HotelDetail hotelDetail)
        {
            if (layoutInflater == null || viewGroup == null || hotelDetail == null)
            {
                return viewGroup;
            }

            ArrayList<DetailInformation> arrayList = hotelDetail.getInformation();

            if (arrayList != null)
            {
                DetailInformation information = arrayList.get(0);

                makeInformationLayout(layoutInflater, viewGroup, information);
            }

            return viewGroup;
        }

        /**
         * 호텔 정보
         *
         * @param view
         * @return
         */
        private View getDeatil05View(LayoutInflater layoutInflater, ViewGroup viewGroup, HotelDetail hotelDetail)
        {
            if (layoutInflater == null || viewGroup == null || hotelDetail == null)
            {
                return viewGroup;
            }

            ArrayList<DetailInformation> arrayList = hotelDetail.getInformation();

            if (arrayList != null)
            {
                DetailInformation information = arrayList.get(1);

                makeInformationLayout(layoutInflater, viewGroup, information);
            }

            return viewGroup;
        }

        /**
         * 호텔 더보기 정보
         *
         * @param view
         * @return
         */
        private View getDeatil06View(LayoutInflater layoutInflater, ViewGroup viewGroup, HotelDetail hotelDetail)
        {
            if (layoutInflater == null || viewGroup == null || hotelDetail == null)
            {
                return viewGroup;
            }

            ArrayList<DetailInformation> arrayList = hotelDetail.getMoreInformation();

            if (arrayList != null)
            {
                viewGroup.removeAllViews();

                for (DetailInformation information : arrayList)
                {
                    ViewGroup childGroup = (ViewGroup) layoutInflater.inflate(R.layout.list_row_detail05, viewGroup, false);

                    makeInformationLayout(layoutInflater, childGroup, information);

                    viewGroup.addView(childGroup);
                }
            }

            return viewGroup;
        }

        /**
         * 확인 사항
         *
         * @param view
         * @return
         */
        private View getDeatil07View(LayoutInflater layoutInflater, ViewGroup viewGroup, HotelDetail hotelDetail)
        {
            if (layoutInflater == null || viewGroup == null || hotelDetail == null)
            {
                return viewGroup;
            }

            ArrayList<DetailInformation> arrayList = hotelDetail.getInformation();

            if (arrayList != null && arrayList.size() >= 3)
            {
                DetailInformation information = arrayList.get(2);

                makeInformationLayout(layoutInflater, viewGroup, information);
            }

            return viewGroup;
        }

        /**
         * 카톡 실시간 상담
         *
         * @param view
         * @return
         */
        private View getDeatil08View(View view)
        {
            if (view == null)
            {
                return view;
            }

            // 카톡 1:1 실시간 상담
            View consultKakaoView = view.findViewById(R.id.kakaoImageView);
            consultKakaoView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mOnUserActionListener != null)
                    {
                        mOnUserActionListener.doKakaotalkConsult();
                    }
                }
            });

            return view;
        }

        private void makeInformationLayout(LayoutInflater layoutInflater, ViewGroup viewGroup, DetailInformation information)
        {
            if (layoutInflater == null || viewGroup == null || information == null)
            {
                return;
            }

            LinearLayout contentsLayout = (LinearLayout) viewGroup.findViewById(R.id.contentsList);
            contentsLayout.removeAllViews();

            TextView titleTextView = (TextView) viewGroup.findViewById(R.id.titleTextView);
            titleTextView.setText(information.title);

            List<String> contentsList = information.getContentsList();

            if (contentsList != null)
            {
                int size = contentsList.size();

                for (int i = 0; i < size; i++)
                {
                    View textLayout = layoutInflater.inflate(R.layout.list_row_detail_text, null, false);
                    TextView textView = (TextView) textLayout.findViewById(R.id.textView);
                    textView.setText(contentsList.get(i));
                    textView.setTypeface(FontManager.getInstance(mFragmentActivity).getDemiLightTypeface());

                    if (Util.isOverAPI21() == true)
                    {
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                        layoutParams.bottomMargin = Util.dpToPx(mFragmentActivity, 5);
                        contentsLayout.addView(textLayout, layoutParams);
                    } else
                    {
                        contentsLayout.addView(textLayout);
                    }
                }
            }
        }
    }
}