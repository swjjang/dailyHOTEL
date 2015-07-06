package com.twoheart.dailyhotel.ui;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.ViewPager.PageTransformer;
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
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
	public static final int STATUS_NONE = 0;
	public static final int STATUS_SEARCH_ROOM = 1;
	public static final int STATUS_BOOKING = 2;
	public static final int STATUS_SOLD_OUT = 3;

	private static final int NUMBER_OF_ROWSLIST = 7;
	private static final int MAX_OF_ROOMTYPE = 3;

	private HotelDetail mHotelDetail;
	private Activity mActivity;
	private View mViewRoot;
	private LoopViewPager mViewPager;
	private View mHotelTitleLaout;
	private TextView mHotelGradeTextView;
	private TextView mActionBarTextView;
	private HotelDetailListView mListView;
	private HotelDetailImageViewPagerAdapter mImageAdapter;
	private HotelDetailListAdapter mListAdapter;

	private View mRoomTypeLayout;
	private View mBottomLayout;
	private View mRoomTypeBackgroundView;
	private View[] mRoomTypeView;

	private ANIMATION_STATUS mAnimationStatus = ANIMATION_STATUS.HIDE_END;
	private ANIMATION_STATE mAnimationState = ANIMATION_STATE.END;
	private ObjectAnimator mObjectAnimator;
	private AlphaAnimation mAlphaAnimation;

	private int mStatusBarHeight;
	private int mImageHeight;

	private View[] mDeatilView;

	private int mBookingStatus; // 예약 진행 상태로 객실 찾기, 없음, 예약 진행

	private HotelDetailActivity.OnUserActionListener mOnUserActionListener;

	private enum ANIMATION_STATE
	{
		START, END, CANCEL
	};

	private enum ANIMATION_STATUS
	{
		SHOW, HIDE, SHOW_END, HIDE_END
	};

	public HotelDetailLayout(Activity activity)
	{
		mActivity = activity;

		initLayout(activity);
	}

	private void initLayout(Context context)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mViewRoot = inflater.inflate(R.layout.layout_hoteldetail, null, false);

		mActionBarTextView = (TextView) mViewRoot.findViewById(R.id.actionBarTextView);
		mActionBarTextView.setVisibility(View.INVISIBLE);

		mListView = (HotelDetailListView) mViewRoot.findViewById(R.id.hotelListView);
		mListView.setOnScrollListener(mOnScrollListener);

		// 이미지 ViewPage 넣기.
		mViewPager = (LoopViewPager) mViewRoot.findViewById(R.id.defaultHotelImageView);
		mViewPager.setOnPageChangeListener(mOnPageChangeListener);
		mViewPager.setScrollDurationFactor(4);

		mImageHeight = Util.getLCDWidth(context);
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

		// 호텔 상세 정보를 얻어와서 리스트 개수가 몇개 필요한지 검색한다.
		mDeatilView = new View[NUMBER_OF_ROWSLIST];

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

		mActionBarTextView.setText(hotelDetail.getHotel().getName());

		if (mImageAdapter == null)
		{
			mImageAdapter = new HotelDetailImageViewPagerAdapter(mActivity);
			mImageAdapter.setOnAnimationListener(mOnUserActionListener);
		}

		mImageAdapter.setData(hotelDetail.getImageUrl());
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

		if (hotelDetail.getHotel().getAvailableRoom() == 0)
		{
			bookingView.setVisibility(View.GONE);
			soldoutView.setVisibility(View.VISIBLE);
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
								mOnUserActionListener.doBooking();
							}
							break;

						case STATUS_SEARCH_ROOM:
							//객실 애니매이션 시작.
							if (mOnUserActionListener != null)
							{
								mOnUserActionListener.showRoomType();
							}
							break;
					}
				}
			});

			soldoutView.setVisibility(View.GONE);
		}

		// 객실 타입 세팅
		mRoomTypeView[0] = mViewRoot.findViewById(R.id.roomType01View);
		mRoomTypeView[1] = mViewRoot.findViewById(R.id.roomType02View);
		mRoomTypeView[2] = mViewRoot.findViewById(R.id.roomType03View);

		selectRoomType(mRoomTypeView[0]);
		mRoomTypeView[0].setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				selectRoomType(v);

			}
		});

		mRoomTypeView[1].setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				selectRoomType(v);

			}
		});

		mRoomTypeView[2].setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				selectRoomType(v);

			}
		});

		if (hotelDetail.getHotel().getAvailableRoom() == 0)
		{
			setBookingStatus(STATUS_SOLD_OUT);
		} else
		{
			setBookingStatus(STATUS_SEARCH_ROOM);
		}
	}

	private void selectRoomType(View view)
	{
		//		for (View roomView : mRoomTypeView)
		//		{
		//			TextView textView = (TextView) roomView.findViewById(R.id.simpleInfoTextView);
		//
		//			if (roomView == view)
		//			{
		//				roomView.setSelected(true);
		//				textView.setEllipsize(TruncateAt.MARQUEE);
		//			} else
		//			{
		//				roomView.setSelected(false);
		//				textView.setEllipsize(TruncateAt.END);
		//			}
		//		}
	}

	private void setBookingStatus(int status)
	{
		mBookingStatus = status;

		TextView bookingView = (TextView) mBottomLayout.findViewById(R.id.bookingTextView);
		View soldoutView = mBottomLayout.findViewById(R.id.soldoutTextView);

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

				bookingView.setText(null);
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

	public int getBookingStatus()
	{
		return mBookingStatus;
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

	public int getCurrentImage()
	{
		if (mViewPager != null)
		{
			return mViewPager.getCurrentItem();
		}

		return 0;
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
		int position = mViewPager.getCurrentItem();

		AnimationImageView imageView = (AnimationImageView) mViewPager.findViewWithTag(position);

		if (imageView != null)
		{
			imageView.startAnimation();
		}
	}

	public void stopAnimationImageView(boolean initDuration)
	{
		int count = mViewPager.getChildCount();

		for (int i = 0; i < count; i++)
		{
			AnimationImageView imageView = (AnimationImageView) mViewPager.getChildAt(i);
			imageView.stopAnimation(initDuration);
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
			TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, mRoomTypeLayout.getHeight(), 0);
			translateAnimation.setDuration(300);
			translateAnimation.setFillBefore(true);
			translateAnimation.setFillAfter(true);
			translateAnimation.setInterpolator(mActivity, android.R.anim.decelerate_interpolator);

			translateAnimation.setAnimationListener(new AnimationListener()
			{
				@Override
				public void onAnimationStart(Animation animation)
				{
					if (mRoomTypeLayout.getVisibility() != View.VISIBLE)
					{
						mRoomTypeLayout.setVisibility(View.VISIBLE);
					}

					mAnimationState = ANIMATION_STATE.START;
					mAnimationStatus = ANIMATION_STATUS.SHOW;
				}

				@Override
				public void onAnimationRepeat(Animation animation)
				{

				}

				@Override
				public void onAnimationEnd(Animation animation)
				{
					mAnimationStatus = ANIMATION_STATUS.SHOW_END;
					mAnimationState = ANIMATION_STATE.END;

					setRoomTypeLayoutEnabled(true);

					setBookingStatus(STATUS_BOOKING);
				}
			});

			if (mRoomTypeLayout != null)
			{
				mRoomTypeLayout.startAnimation(translateAnimation);
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
		} else
		{
			//			View underlineView02 = baseActivity.findViewById(R.id.tabindicator_underLine);

			TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, mRoomTypeLayout.getHeight());
			translateAnimation.setDuration(300);
			translateAnimation.setFillBefore(true);
			translateAnimation.setFillAfter(true);
			translateAnimation.setInterpolator(mActivity, android.R.anim.decelerate_interpolator);

			translateAnimation.setAnimationListener(new AnimationListener()
			{
				@Override
				public void onAnimationStart(Animation animation)
				{
					mAnimationState = ANIMATION_STATE.START;
					mAnimationStatus = ANIMATION_STATUS.HIDE;

					setRoomTypeLayoutEnabled(false);
				}

				@Override
				public void onAnimationRepeat(Animation animation)
				{
				}

				@Override
				public void onAnimationEnd(Animation animation)
				{
					mAnimationStatus = ANIMATION_STATUS.HIDE_END;
					mAnimationState = ANIMATION_STATE.END;

					hideRoomType();

					setBookingStatus(STATUS_SEARCH_ROOM);
				}
			});

			if (mRoomTypeLayout != null)
			{
				mRoomTypeLayout.startAnimation(translateAnimation);
			}
		}

		showAnimationFadeIn();
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

	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////

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
			ExLog.d("onPageSelected : " + position);
			mIsRefresh = false;
			mSelectedPosition = position;

			if (mOnUserActionListener != null)
			{
				mOnUserActionListener.onSelectedImagePosition(position);
			}

			if (mScrollState == -1)
			{
				stopAnimationImageView(true);
				
				AnimationImageView imageView = (AnimationImageView) mViewPager.findViewWithTag(position);

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
			
			stopAnimationImageView(true);

			ExLog.d("onPageScrolled : " + position + ", arg1 : " + positionOffset + ", " + positionOffsetPixels);

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

				AnimationImageView nextImageView = (AnimationImageView) mViewPager.findViewWithTag(nextPosition);

				// 방향에 따라서 초기화가 달라야한다.
				if (nextImageView != null)
				{
					nextImageView.initAnimation(mDirection > 0);
					nextImageView.invalidate();
				}
			}
		}

		@Override
		public void onPageScrollStateChanged(int state)
		{
			ExLog.d("onPageScrollStateChanged : " + state);

			mScrollState = state;

			switch (state)
			{
				case ViewPager.SCROLL_STATE_IDLE:
				{
					stopAnimationImageView(true);

					AnimationImageView imageView = (AnimationImageView) mViewPager.findViewWithTag(mSelectedPosition);

					if (imageView != null)
					{
						imageView.startAnimation();
					}

					mDirection = 0;
					mScrollPosition = -1;
					break;
				}

				case ViewPager.SCROLL_STATE_DRAGGING:
					stopAnimationImageView(true);
					break;

				case ViewPager.SCROLL_STATE_SETTLING:
					break;
			}

			//			{
			//				
			//			}
		}
	};

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
			if (view.getAdapter() == null)
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

			float offset = rect.top - mStatusBarHeight - Util.dpToPx(mActivity, 56);
			float max = mImageHeight - Util.dpToPx(mActivity, 56);
			float alphaFactor = offset / max;

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
				if (Float.compare(alphaFactor, 0.0f) <= 0)
				{
					mHotelGradeTextView.setVisibility(View.INVISIBLE);
				} else
				{
					mHotelGradeTextView.setVisibility(View.VISIBLE);
				}
			}

			//			if (mScrollState != OnScrollListener.SCROLL_STATE_FLING)
			//			{
			//				if (Float.compare(mAlphaFactor, alphaFactor) < 0)
			//				{
			//					mDirection = MotionEvent.ACTION_DOWN;
			//				} else if (Float.compare(mAlphaFactor, alphaFactor) > 0)
			//				{
			//					mDirection = MotionEvent.ACTION_UP;
			//				}
			//			}
			//
			//			mAlphaFactor = alphaFactor;
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
					if (mOnUserActionListener != null)
					{
						mOnUserActionListener.stopAutoSlide();
					}

					mPrevX = event.getX();
					mPrevY = event.getY();

					mMoveState = 0;
					mListView.setScrollEnabled(false);
					mViewPager.onTouchEvent(event);
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
							mViewPager.onTouchEvent(event);
							mListView.setScrollEnabled(true);
							break;
						}
					}
				}
				case MotionEvent.ACTION_CANCEL:
				{
					mMoveState = 0;
					mViewPager.onTouchEvent(event);
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
						if (Math.abs(x - mPrevX) > Math.abs(y - mPrevY))
						{
							// x 축으로 이동한 경우.
							mMoveState = 100;
							mViewPager.onTouchEvent(event);
						} else
						{
							// y축으로 이동한 경우. 
							mMoveState = 10;
							mListView.setScrollEnabled(true);
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

	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Adapter
	//////////////////////////////////////////////////////////////////////////////////////////////////////////

	private class HotelDetailListAdapter extends BaseAdapter
	{
		private FragmentActivity mFragmentActivity;
		private GoogleMap mGoogleMap;

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
			return NUMBER_OF_ROWSLIST;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			LayoutInflater layoutInflater = (LayoutInflater) mFragmentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			switch (position)
			{
			// 빈화면
				case 0:
					if (mDeatilView[0] == null)
					{
						mDeatilView[0] = layoutInflater.inflate(R.layout.list_row_detail01, parent, false);

						getDetail00View(mDeatilView[0]);
					}
					break;

				// 호텔 등급과 이름.
				case 1:
					if (mDeatilView[1] == null)
					{
						mDeatilView[1] = layoutInflater.inflate(R.layout.list_row_detail02, parent, false);
						getDetail01View(mDeatilView[1], mHotelDetail);
					}

					break;

				case 2:
					if (mDeatilView[2] == null)
					{
						mDeatilView[2] = layoutInflater.inflate(R.layout.list_row_detail03, parent, false);

						getDetail02View(mDeatilView[2], mHotelDetail);
					}
					break;

				case 3:
					if (mDeatilView[3] == null)
					{
						mDeatilView[3] = layoutInflater.inflate(R.layout.list_row_detail04, parent, false);
					}
					break;

				case 4:
					if (mDeatilView[4] == null)
					{
						mDeatilView[4] = layoutInflater.inflate(R.layout.list_row_detail05, parent, false);
						getDeatil04View(mDeatilView[4]);
					}
					break;

				case 5:
					if (mDeatilView[5] == null)
					{
						mDeatilView[5] = layoutInflater.inflate(R.layout.list_row_detail06, parent, false);

					}
					break;

				case 6:
					if (mDeatilView[6] == null)
					{
						mDeatilView[6] = layoutInflater.inflate(R.layout.list_row_detail07, parent, false);
						getDeatil06View(mDeatilView[6]);
					}
					break;
			}

			return mDeatilView[position];
		}

		private View getDetail00View(View view)
		{
			View emptyView = view.findViewById(R.id.imageEmptyHeight);
			emptyView.getLayoutParams().height = mImageHeight;

			emptyView.setClickable(true);
			emptyView.setOnTouchListener(mEmptyViewOnTouchListener);

			return view;
		}

		private View getDetail01View(View view, HotelDetail hotelDetail)
		{
			mHotelTitleLaout = view.findViewById(R.id.hotelTitleLaout);

			// 등급
			mHotelGradeTextView = (TextView) view.findViewById(R.id.hotelGradeTextView);
			mHotelGradeTextView.setVisibility(View.VISIBLE);
			mHotelGradeTextView.setText(hotelDetail.getHotel().getCategory().getName(mFragmentActivity));
			mHotelGradeTextView.setBackgroundResource(hotelDetail.getHotel().getCategory().getColorResId());

			// 호텔명
			TextView hotelNameTextView = (TextView) view.findViewById(R.id.hotelNameTextView);
			hotelNameTextView.setText(hotelDetail.getHotel().getName());

			return view;
		}

		private View getDetail02View(final View view, HotelDetail hotelDetail)
		{
			// 주소지
			TextView hotelAddressTextView = (TextView) view.findViewById(R.id.hotelAddressTextView);
			TextView hotelSimpleLocationTextView = (TextView) view.findViewById(R.id.hotelSimpleLocationTextView);

			hotelAddressTextView.setText(hotelDetail.getHotel().getAddress());
			hotelSimpleLocationTextView.setText(hotelDetail.getHotel().getAddress());

			// 맵
			SupportMapFragment mapFragment = (SupportMapFragment) mFragmentActivity.getSupportFragmentManager().findFragmentById(R.id.googleMapFragment);

			if (mGoogleMap == null)
			{
				mapFragment.getMapAsync(new OnMapReadyCallback()
				{
					@Override
					public void onMapReady(GoogleMap googleMap)
					{
						mGoogleMap = googleMap;

						final LatLng latlng = new LatLng(mHotelDetail.getLatitude(), mHotelDetail.getLongitude());

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
					}
				});
			} else
			{
				LatLng latlng = new LatLng(mHotelDetail.getLatitude(), mHotelDetail.getLongitude());

				Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(latlng));
				marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.info_ic_map_large));

				CameraPosition cameraPosition = new CameraPosition.Builder().target(latlng).zoom(15).build();
				mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			}

			return view;
		}

		private View getDeatil03View(View view)
		{

			return view;
		}

		private View getDeatil04View(View view)
		{
			View moreInfoView = view.findViewById(R.id.moreInfoView);
			moreInfoView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (mOnUserActionListener != null)
					{
						mOnUserActionListener.viewMoreInfomation();
					}
				}
			});

			return view;
		}

		private View getDeatil05View(View view)
		{
			return view;
		}

		private View getDeatil06View(View view)
		{
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
	}
}