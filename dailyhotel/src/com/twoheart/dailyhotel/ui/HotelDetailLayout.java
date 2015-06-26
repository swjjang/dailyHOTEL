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
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.view.animation.AlphaAnimation;

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
	private View mHotelGradeTextView;
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

		try
		{
			mScrollView.getViewTreeObserver().removeOnScrollChangedListener(mOnScrollChangedListener);
		} catch (Exception e)
		{
		}

		mScrollView.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangedListener);

		// 이미지 ViewPage 넣기.
		mViewPager = (ViewPager) mViewRoot.findViewById(R.id.defaultHotelImageView);

		mImageHeight = Util.getLCDWidth(context);
		LayoutParams layoutParams = (LayoutParams) mViewPager.getLayoutParams();
		layoutParams.height = mImageHeight;

		View emptyView = mViewRoot.findViewById(R.id.imageEmptyHeight);
		emptyView.getLayoutParams().height = mImageHeight;

		emptyView.setClickable(true);
		emptyView.setOnTouchListener(mEmptyViewOnTouchListener);

		mHotelTitleLaout = mViewRoot.findViewById(R.id.hotelTitleLaout);
		mHotelGradeTextView = mViewRoot.findViewById(R.id.hotelGradeTextView);
	}

	public View getView()
	{
		return mViewRoot;
	}

	public void setHotelDetail(HotelDetail hotelDetail)
	{
		mHotelDetail = hotelDetail;

		if (mAdapter == null)
		{
			mAdapter = new HotelDetailImageViewPagerAdapter(mActivity);
		}

		if (hotelDetail != null)
		{
			mAdapter.setData(hotelDetail.getImageUrl());
			mViewPager.setAdapter(mAdapter);
		}
	}

	public void setUserActionListener(HotelDetailActivity.OnUserActionListener listener)
	{
		mOnUserActionListener = listener;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////

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

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			{
				mHotelGradeTextView.setAlpha(alphaFactor);
			} else
			{
				AlphaAnimation anim = new AlphaAnimation(mLastAlphaFactor, alphaFactor);
				anim.setDuration(0);
				anim.setFillAfter(true);
				mHotelGradeTextView.startAnimation(anim);

				mLastAlphaFactor = alphaFactor;
			}
		}
	};

	View.OnTouchListener mEmptyViewOnTouchListener = new View.OnTouchListener()
	{
		private GestureDetector mGestureDetector;
		private int mMoveState;

		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			if (mGestureDetector == null)
			{
				mGestureDetector = new GestureDetector(mActivity, new XScrollDetector());
			}

			boolean isXScroll = mGestureDetector.onTouchEvent(event);

			ExLog.d("isXScroll : " + isXScroll);

			switch (event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
				{
					mMoveState = 0;
					mScrollView.setScrollEnabled(false);
					mViewPager.onTouchEvent(event);
					break;
				}

				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
				{
					mMoveState = 0;
					mViewPager.onTouchEvent(event);
					break;
				}

				case MotionEvent.ACTION_MOVE:
				{
					if (mMoveState < 5)
					{
						mMoveState++;

						if (isXScroll == true)
						{
							mMoveState = 100;
							mViewPager.onTouchEvent(event);
						}
					} else if (mMoveState >= 5 && mMoveState != 100)
					{
						mMoveState = 10;
						mScrollView.setScrollEnabled(true);
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

	private class XScrollDetector extends
			GestureDetector.SimpleOnGestureListener
	{

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
		{
			return Math.abs(distanceX) > Math.abs(distanceY);
		}

	}
}