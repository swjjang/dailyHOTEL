/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * CreditFragment (적립금 화면)
 * 
 * 로그인 여부에 따라 적립금을 안내하는 화면이다. 적립금을 표시하며 카카오톡
 * 친구 초대 버튼이 있다. 세부 내역을 따로 표시해주는 버튼을 가지고 있어 
 * 해당 화면을 띄워주기도 한다.
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.HotelDetailActivity;
import com.twoheart.dailyhotel.adapter.HotelDetailImageViewPagerAdapter;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

/**
 * 호텔 상세 정보 화면
 * 
 * @author sheldon
 *
 */
public class HotelDetailLayout
{
	private HotelDetail mHotelDetail;
	private Activity mActivity;
	private View mViewRoot;
	private ViewPager mViewPager;
	private View mHotelTitleLaout;
	private TextView mHotelGradeTextView;
	private HotelDetailScrollView mScrollView;
	private HotelDetailImageViewPagerAdapter mAdapter;

	private int mStatusBarHeight;
	private int mImageHeight;
	private float mLastAlphaFactor;

	private HotelDetailActivity.OnUserActionListener mOnUserActionListener;
	
	public HotelDetailLayout(Activity activity)
	{
		mActivity = activity;

		initLayout(activity);
	}
	
	private void initLayout(Context context)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mViewRoot = inflater.inflate(R.layout.layout_hoteldetail, null, false);

		mScrollView = (HotelDetailScrollView) mViewRoot.findViewById(R.id.hotelScrollView);
		
		if(isOverAPI11() == true)
		{
			try
			{
				mScrollView.getViewTreeObserver().removeOnScrollChangedListener(mOnScrollChangedListener);
			} catch (Exception e)
			{
			}
			
			mScrollView.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangedListener);
		} else
		{
			mScrollView.setOnTouchListener(mOnScrollTouchListener);
		}

		// 이미지 ViewPage 넣기.
		mViewPager = (ViewPager) mViewRoot.findViewById(R.id.defaultHotelImageView);
		mViewPager.setOnPageChangeListener(mOnPageChangeListener);

		mImageHeight = Util.getLCDWidth(context);
		LayoutParams layoutParams = (LayoutParams) mViewPager.getLayoutParams();
		layoutParams.height = mImageHeight;

		View emptyView = mViewRoot.findViewById(R.id.imageEmptyHeight);
		emptyView.getLayoutParams().height = mImageHeight;

		emptyView.setClickable(true);
		emptyView.setOnTouchListener(mEmptyViewOnTouchListener);

		mHotelTitleLaout = mViewRoot.findViewById(R.id.hotelTitleLaout);
	}

	private void initHotelDetailLayout(HotelDetail hotelDetail)
	{
		// 등급
		mHotelGradeTextView = (TextView) mViewRoot.findViewById(R.id.hotelGradeTextView);
		mHotelGradeTextView.setVisibility(View.VISIBLE);
		mHotelGradeTextView.setText(hotelDetail.getHotel().getCategory().getName(mActivity));
		mHotelGradeTextView.setBackgroundResource(hotelDetail.getHotel().getCategory().getColorResId());

		// 호텔명
		TextView hotelNameTextView = (TextView) mViewRoot.findViewById(R.id.hotelNameTextView);
		hotelNameTextView.setText(hotelDetail.getHotel().getName());
	}

	public View getView()
	{
		return mViewRoot;
	}

	public void setHotelDetail(HotelDetail hotelDetail, int imagePosition)
	{
		mHotelDetail = hotelDetail;

		if (mAdapter == null)
		{
			mAdapter = new HotelDetailImageViewPagerAdapter(mActivity);
		}

		if (hotelDetail != null)
		{
			initHotelDetailLayout(hotelDetail);

			mAdapter.setData(hotelDetail.getImageUrl());
			mViewPager.setAdapter(mAdapter);
			
			setCurrentImage(imagePosition);
			
			if(mOnUserActionListener != null)
			{
				mOnUserActionListener.startAutoSlide();
			}
		}
	}
	
	public void setCurrentImage(int position)
	{
		if(mViewPager != null)
		{
			mViewPager.setCurrentItem(position, true);
		}
	}
	
	public int getCurrentImage()
	{
		if(mViewPager != null)
		{
			return mViewPager.getCurrentItem();
		}
		
		return 0;
	}
	
	private boolean isOverAPI11()
	{
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public void setUserActionListener(HotelDetailActivity.OnUserActionListener listener)
	{
		mOnUserActionListener = listener;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener()
	{
		@Override
		public void onPageSelected(int position)
		{
			if(mOnUserActionListener != null)
			{
				mOnUserActionListener.onSelectedImagePosition(position);
			}
		}
		
		@Override
		public void onPageScrolled(int position, float arg1, int arg2)
		{
		}
		
		@Override
		public void onPageScrollStateChanged(int position)
		{
		}
	};
	

	private OnScrollChangedListener mOnScrollChangedListener = new OnScrollChangedListener()
	{
		@Override
		public void onScrollChanged()
		{
			if (mStatusBarHeight == 0)
			{
				Rect rect = new Rect();
				mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

				mStatusBarHeight = rect.top;
			}

			Rect rect = new Rect();
			mHotelTitleLaout.getGlobalVisibleRect(rect);
			
			if (rect.top == rect.right)
			{

			} else
			{
				if (rect.top <= mStatusBarHeight)
				{
					if (mOnUserActionListener != null)
					{
						mOnUserActionListener.showActionBar();
					}
				} else
				{
					if (mOnUserActionListener != null)
					{
						mOnUserActionListener.hideActionBar();
					}
				}
			}

			float offset = rect.top - mStatusBarHeight - Util.dpToPx(mActivity, 56);
			float max = mImageHeight - Util.dpToPx(mActivity, 56);
			float alphaFactor = offset / max;

			mHotelGradeTextView.setAlpha(alphaFactor);
		}
	};
	
	
	private OnTouchListener mOnScrollTouchListener = new OnTouchListener()
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			if (mStatusBarHeight == 0)
			{
				Rect rect = new Rect();
				mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

				mStatusBarHeight = rect.top;
			}

			Rect rect = new Rect();
			mHotelTitleLaout.getGlobalVisibleRect(rect);
			
			ExLog.d("rect : " + rect + ", mStatusBarHeight : " + mStatusBarHeight);

			if (rect.top == rect.right)
			{

			} else
			{
				if (rect.top <= mStatusBarHeight)
				{
					if (mOnUserActionListener != null)
					{
						mOnUserActionListener.showActionBar();
					}
				} else
				{
					if (mOnUserActionListener != null)
					{
						mOnUserActionListener.hideActionBar();
					}
				}
			}

			float offset = rect.top - mStatusBarHeight - Util.dpToPx(mActivity, 56);
			float max = mImageHeight - Util.dpToPx(mActivity, 56);
			float alphaFactor = offset / max;

//			AlphaAnimation anim = new AlphaAnimation(mLastAlphaFactor, alphaFactor);
//			anim.setDuration(0);
//			anim.setFillAfter(true);
//			mHotelGradeTextView.startAnimation(anim);
//
//			mLastAlphaFactor = alphaFactor;
			
			return false;
		}
	};

	private View.OnTouchListener mEmptyViewOnTouchListener = new View.OnTouchListener()
	{
		private int mMoveState;
		private float mPrevX, mPrevY;

		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			switch (event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
				{
					if(mOnUserActionListener != null)
					{
						mOnUserActionListener.stopAutoSlide();
					}
					
					mPrevX = event.getX();
					mPrevY = event.getY();

					mMoveState = 0;
					mScrollView.setScrollEnabled(false);
					mViewPager.onTouchEvent(event);
					break;
				}

				case MotionEvent.ACTION_UP:
				{
					if (mMoveState == 0 && mPrevX == event.getX() && mPrevY == event.getY())
					{
						if (mOnUserActionListener != null)
						{
							if(mOnUserActionListener != null)
							{
								mOnUserActionListener.stopAutoSlide();
							}
							
							mOnUserActionListener.onClickImage(mHotelDetail);
							
							mMoveState = 0;
							mViewPager.onTouchEvent(event);
							mScrollView.setScrollEnabled(true);
							break;
						}
					}
				}
				case MotionEvent.ACTION_CANCEL:
				{
					mMoveState = 0;
					mViewPager.onTouchEvent(event);
					mScrollView.setScrollEnabled(true);
					
					if(mOnUserActionListener != null)
					{
						mOnUserActionListener.startAutoSlide();
					}
					break;
				}

				case MotionEvent.ACTION_MOVE:
				{
					if(mOnUserActionListener != null)
					{
						mOnUserActionListener.stopAutoSlide();
					}
					
					float x = event.getX();
					float y = event.getY();

					if (mMoveState == 0)
					{
						if (Math.abs(x - mPrevX) > Math.abs(y - mPrevY))
						{
							// x 축으로 이동한 경우.
							mMoveState = 100;
							mViewPager.onTouchEvent(event);
						} else
						{
							// y축으로 이동한 경우. 
							mMoveState = 10;
							mScrollView.setScrollEnabled(true);
							return true;
						}
					} else if (mMoveState == 100)
					{
						mViewPager.onTouchEvent(event);
					}
					break;
				}
			}

			return false;
		}
	};
}