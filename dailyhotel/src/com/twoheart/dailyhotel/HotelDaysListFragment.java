/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * HotelListFragment (호텔 목록 화면)
 * 
 * 어플리케이션의 가장 주가 되는 화면으로서 호텔들의 목록을 보여주는 화면이다.
 * 호텔 리스트는 따로 커스텀되어 구성되어 있으며, 액션바의 네비게이션을 이용
 * 하여 큰 지역을 분리하고 리스트뷰 헤더를 이용하여 세부 지역을 나누어 표시
 * 한다. 리스트뷰의 맨 첫 아이템은 이벤트 참여하기 버튼이 있으며, 이 버튼은
 * 서버의 이벤트 API에 따라 NEW 아이콘을 붙여주기도 한다.
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class HotelDaysListFragment extends HotelListFragment implements OnClickListener
{
	private static final int HANDLER_MESSAGE_SHOWDAYSLIST = 1;
	private static final int DAY_OF_TOTALCOUNT = 14;

	private static final int CHECK_NONE_STATUS = 0;
	private static final int CHECK_IN_STATUS = 1;
	private static final int CHECK_OUT_STATUS = 2;
	private static final int CHECK_OK_STATUS = 3;

	// 날짜가 나오는 탭의 높이이다. 마진이 있는 경우 고려해서 넣을것.px 로 넣어야 함.
	private int DAYSLIST_HEIGHT;

	private View mDaysBackgroundView;
	private View mDaysLayout;

	private ANIMATION_STATUS mAnimationStatus = ANIMATION_STATUS.HIDE_END;
	private ANIMATION_STATE mAnimationState = ANIMATION_STATE.END;
	private ObjectAnimator mObjectAnimator;
	private AlphaAnimation mAlphaAnimation;

	private View[] mDaysView;
	private View mSelectedCheckInView = null;
	private View mSelectedCheckOutView = null;
	private boolean mIsShowDaysList;
	private int mCheckStatus;

	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null || baseActivity.isFinishing() == true)
			{
				return;
			}

			switch (msg.what)
			{
				case HANDLER_MESSAGE_SHOWDAYSLIST:
					showAnimationDaysList();
					break;
			}
		}
	};

	private enum ANIMATION_STATE
	{
		START, END, CANCEL
	};

	private enum ANIMATION_STATUS
	{
		SHOW, HIDE, SHOW_END, HIDE_END
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return null;
		}

		View view = super.onCreateView(inflater, container, savedInstanceState);

		mDaysBackgroundView = view.findViewById(R.id.daysBackgroundView);
		mDaysLayout = view.findViewById(R.id.daysLayout);

		mDaysBackgroundView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				hideAnimationDaysList();
			}
		});

		hideDaysList();

		return view;
	}

	private void initDaysLayout()
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		if (mDaysView == null)
		{
			mDaysView = new View[DAY_OF_TOTALCOUNT];

			final View daysLayout = mDaysLayout.findViewById(R.id.daysLayout);

			daysLayout.setOnTouchListener(new OnTouchListener()
			{
				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					return true;
				}
			});

			for (int i = 0; i < DAY_OF_TOTALCOUNT; i++)
			{
				mDaysView[i] = daysLayout.findViewById(R.id.item01 + i);
			}

			daysLayout.post(new Runnable()
			{
				@Override
				public void run()
				{
					BaseActivity baseActivity = (BaseActivity) getActivity();

					if (baseActivity == null)
					{
						return;
					}

					float width = daysLayout.getWidth() - Util.dpToPx(baseActivity, 40);
					float marginRight = (width - mDaysView[0].getWidth() * DAY_OF_TOTALCOUNT / 2) / 6;

					for (int i = 0; i < DAY_OF_TOTALCOUNT; i++)
					{
						((LinearLayout.LayoutParams) mDaysView[i].getLayoutParams()).rightMargin = (int) marginRight;
					}
				}
			});
		}

		int visibleCount = DAY_OF_TOTALCOUNT / 2;

		DAYSLIST_HEIGHT = Util.dpToPx(baseActivity, 110);

		for (int i = 0; i < DAY_OF_TOTALCOUNT; i++)
		{
			//			if (i < visibleCount)
			//			{
			mDaysView[i].setVisibility(View.VISIBLE);
			//			} else
			//			{
			//				mDaysView[i].setVisibility(View.GONE);
			//			}

			initLayoutDays(mDaysView[i], mSaleTime.getClone(i));
			mDaysView[i].setTag(mDaysView[i].getId(), i);
		}
	}

	@Override
	public void onPageSelected(boolean isRequestHotelList)
	{
		super.onPageSelected(isRequestHotelList);

		initDaysLayout();

		switch (mAnimationStatus)
		{
			case SHOW:
				if (isUsedAnimatorApi() == true)
				{
					hideAnimationDaysList();
				} else
				{
					return;
				}
				break;

			case HIDE:
				if (isUsedAnimatorApi() == true)
				{
					showAnimationDaysList();
				} else
				{
					return;
				}
				break;

			case SHOW_END:
				hideAnimationDaysList();
				break;

			case HIDE_END:
			{
				if (isRequestHotelList == true)
				{
					mIsShowDaysList = true;
					showAnimationFadeOut();
				} else
				{
					showAnimationDaysList();
				}
				break;
			}
		}
	}

	@Override
	public void onPageUnSelected()
	{
		try
		{
			super.onPageUnSelected();

			if (mHandler != null)
			{
				mHandler.removeMessages(1);
			}

			hideDaysList();
		} catch (Exception e)
		{
			ExLog.d(e.toString());

			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			baseActivity.restartApp();
		}
	}

	@Override
	public void onRefreshComplete()
	{
		super.onRefreshComplete();

		initDaysLayout();

		if (mIsShowDaysList == true && mAnimationStatus == ANIMATION_STATUS.HIDE_END)
		{
			mIsShowDaysList = false;

			mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SHOWDAYSLIST, 400);
		}
	}

	@Override
	public void onClick(View v)
	{
		mSaleTime = (SaleTime) v.getTag();

		switch (mCheckStatus)
		{
			case CHECK_NONE_STATUS:
				mCheckStatus = CHECK_IN_STATUS;

				setSelectedCheckInDays(v);
				setDaysLayoutEnabled(false);
				showAnimationCheckIn(v, (Integer) v.getTag(v.getId()));
				break;

			case CHECK_IN_STATUS:
				mCheckStatus = CHECK_OUT_STATUS;

				setSelectedCheckOutDays(v);

				setDaysLayoutEnabled(false);
				break;

			case CHECK_OUT_STATUS:
				mCheckStatus = CHECK_OK_STATUS;

				break;
		}
	}

	private void showAnimationCheckOut()
	{

	}

	private void showAnimationCheckIn(final View view, final int position)
	{
		ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
		valueAnimator.setDuration(300).addUpdateListener(new AnimatorUpdateListener()
		{
			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				int value = (Integer) animation.getAnimatedValue();
				float positionValue = 0.0f;

				for (int i = 0; i <= position; i++)
				{
					float translationX = value * (mDaysView[0].getX() - (mDaysView[i].getX() - mDaysView[i].getTranslationX())) / 100;
					mDaysView[i].setTranslationX(translationX);

					if (i != position)
					{
						mDaysView[i].setAlpha((100.0f - value) / 100.0f);
					} else
					{
						positionValue = translationX;
					}
				}

				for (int i = position + 1; i < DAY_OF_TOTALCOUNT; i++)
				{
					mDaysView[i].setTranslationX(positionValue);
				}
			}
		});

		valueAnimator.addListener(new AnimatorListener()
		{
			@Override
			public void onAnimationStart(Animator animation)
			{

			}

			@Override
			public void onAnimationRepeat(Animator animation)
			{
			}

			@Override
			public void onAnimationEnd(Animator animation)
			{

			}

			@Override
			public void onAnimationCancel(Animator animation)
			{

			}
		});

		valueAnimator.start();

		//		for(int i = 0; i < position; i++)
		//		{
		//			TranslateAnimation translateAnimation = new TranslateAnimation(0, mDaysView[0].getX() - mDaysView[i].getX() - mDaysView[0].getWidth(), 0.0f, 0.0f);
		//			translateAnimation.setDuration(300);
		//			translateAnimation.setFillAfter(true);
		//			
		//			final View dayView = mDaysView[i];
		//			final int dayPosition = i;
		//			
		//			translateAnimation.setAnimationListener(new AnimationListener()
		//			{
		//				@Override
		//				public void onAnimationStart(Animation animation)
		//				{
		//					// TODO Auto-generated method stub
		//					
		//				}
		//				
		//				@Override
		//				public void onAnimationRepeat(Animation animation)
		//				{
		//					// TODO Auto-generated method stub
		//					
		//				}
		//				
		//				@Override
		//				public void onAnimationEnd(Animation animation)
		//				{
		//					dayView.setAnimation(null);
		//					dayView.setVisibility(View.GONE);
		//					mDaysView[dayPosition + DAY_OF_TOTALCOUNT / 2].setVisibility(View.VISIBLE);
		//				}
		//			});
		//			
		//			dayView.startAnimation(translateAnimation);
		//		}

		//		ValueAnimator valueAnimator = ValueAnimator.ofInt(100, 0);
		//		valueAnimator.setDuration(300).addUpdateListener(new AnimatorUpdateListener()
		//		{
		//			@Override
		//			public void onAnimationUpdate(ValueAnimator animation)
		//			{
		//				int value = (Integer) animation.getAnimatedValue();
		//				for (int i = 0; i < DAY_OF_TOTALCOUNT / 2; i++)
		//				{
		//					if (i != position)
		//					{
		//						mDaysView[i].setAlpha(value);
		//					}
		//				}
		//			}
		//		});
		//		
		//		valueAnimator.addListener(new AnimatorListener()
		//		{
		//			@Override
		//			public void onAnimationStart(Animator animation)
		//			{
		//				
		//			}
		//			@Override
		//			public void onAnimationRepeat(Animator animation)
		//			{
		//			}
		//
		//			@Override
		//			public void onAnimationEnd(Animator animation)
		//			{
		//				// 2. 선택한것이 앞으로 오는 애니메이션
		//
		//				ValueAnimator valueAnimator = ValueAnimator.ofInt(0, mDaysView[0].getLeft() - view.getLeft());
		//				valueAnimator.setDuration(300).addUpdateListener(new AnimatorUpdateListener()
		//				{
		//					@Override
		//					public void onAnimationUpdate(ValueAnimator animation)
		//					{
		//						int value = (Integer) animation.getAnimatedValue();
		//						view.setTranslationX(value);
		//					}
		//				});
		//
		//				valueAnimator.addListener(new AnimatorListener()
		//				{
		//					@Override
		//					public void onAnimationStart(Animator animation)
		//					{
		//					}
		//
		//					@Override
		//					public void onAnimationRepeat(Animator animation)
		//					{
		//					}
		//
		//					@Override
		//					public void onAnimationEnd(Animator animation)
		//					{
		//						for (int i = 0, j = 0; i < DAY_OF_TOTALCOUNT; i++)
		//						{
		//							if (i < position)
		//							{
		//								mDaysView[i].setVisibility(View.GONE);
		//							} else if (i > position)
		//							{
		//								if (++j < DAY_OF_TOTALCOUNT / 2)
		//								{
		//									mDaysView[i].setVisibility(View.VISIBLE);
		//									mDaysView[i].setAlpha(0);
		//								} else
		//								{
		//									mDaysView[i].setVisibility(View.GONE);
		//								}
		//							}
		//						}
		//
		//						mDaysView[position].setTranslationX(0);
		//
		//						final int startIndex = position + 1;
		//						final int endIndex = position + DAY_OF_TOTALCOUNT / 2;
		//
		//						ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
		//						valueAnimator.setDuration(300).addUpdateListener(new AnimatorUpdateListener()
		//						{
		//							@Override
		//							public void onAnimationUpdate(ValueAnimator animation)
		//							{
		//								int value = (Integer) animation.getAnimatedValue();
		//
		//								for (int i = startIndex; i < endIndex; i++)
		//								{
		//									mDaysView[i].setAlpha(value);
		//								}
		//							}
		//						});
		//
		//						valueAnimator.addListener(new AnimatorListener()
		//						{
		//							@Override
		//							public void onAnimationStart(Animator animation)
		//							{
		//							}
		//
		//							@Override
		//							public void onAnimationEnd(Animator animation)
		//							{
		//								setDaysLayoutEnabled(true);
		//							}
		//
		//							@Override
		//							public void onAnimationCancel(Animator animation)
		//							{
		//							}
		//
		//							@Override
		//							public void onAnimationRepeat(Animator animation)
		//							{
		//							}
		//						});
		//
		//						valueAnimator.start();
		//					}
		//
		//					@Override
		//					public void onAnimationCancel(Animator animation)
		//					{
		//					}
		//				});
		//
		//				valueAnimator.start();
		//			}
		//
		//			@Override
		//			public void onAnimationCancel(Animator animation)
		//			{
		//			}
		//		});
		//
		//		valueAnimator.start();
	}

	private void initCheckDays()
	{
		if (mDaysView == null)
		{
			return;
		}

		mSelectedCheckInView = null;
		mSelectedCheckOutView = null;

		for (View dayView : mDaysView)
		{
			if (dayView == null)
			{
				break;
			}

			TextView dayOfTheWeekTextView = (TextView) dayView.findViewById(R.id.textView1);
			TextView dayTextView = (TextView) dayView.findViewById(R.id.textView2);

			dayOfTheWeekTextView.setSelected(false);
			dayTextView.setSelected(false);
			((View) dayTextView.getParent()).setSelected(false);
		}
	}

	private void initLayoutDays(View view, SaleTime saleTime)
	{
		if (view == null)
		{
			return;
		}

		TextView dayOfTheWeekTextView = (TextView) view.findViewById(R.id.textView1);
		TextView dayTextView = (TextView) view.findViewById(R.id.textView2);

		dayOfTheWeekTextView.setText(saleTime.getDailyDayOftheWeek());
		dayTextView.setText(saleTime.getDailyDay());

		view.setOnClickListener(this);

		view.setTag(saleTime);
	}

	private void setDaysLayoutEnabled(boolean enabled)
	{
		if (mDaysView == null)
		{
			return;
		}

		for (View view : mDaysView)
		{
			if (view == null)
			{
				break;
			}

			view.setEnabled(enabled);
		}

		mDaysBackgroundView.setEnabled(enabled);
	}

	private void setSelectedCheckInDays(View view)
	{
		if (view == null || mDaysView == null)
		{
			return;
		}

		mSelectedCheckInView = view;

		for (View dayView : mDaysView)
		{
			if (dayView == null)
			{
				break;
			}

			TextView dayOfTheWeekTextView = (TextView) dayView.findViewById(R.id.textView1);
			TextView dayTextView = (TextView) dayView.findViewById(R.id.textView2);

			boolean selectedView = dayView == view;

			dayOfTheWeekTextView.setSelected(selectedView);
			dayTextView.setSelected(selectedView);
			((View) dayTextView.getParent()).setSelected(selectedView);
		}
	}

	private void setSelectedCheckOutDays(View view)
	{
		if (view == null || mDaysView == null)
		{
			return;
		}

		mSelectedCheckOutView = view;

		//		for (View dayView : mDaysView)
		//		{
		//			if (dayView == null)
		//			{
		//				break;
		//			}
		//
		//			TextView dayOfTheWeekTextView = (TextView) dayView.findViewById(R.id.textView1);
		//			TextView dayTextView = (TextView) dayView.findViewById(R.id.textView2);
		//
		//			boolean selectedView = dayView == view;
		//
		//			dayOfTheWeekTextView.setSelected(selectedView);
		//			dayTextView.setSelected(selectedView);
		//			((View) dayTextView.getParent()).setSelected(selectedView);
		//		}
	}

	private void hideDaysList()
	{
		if (mCheckStatus != CHECK_OK_STATUS)
		{
			mCheckStatus = CHECK_NONE_STATUS;

			initCheckDays();
		}

		if (mObjectAnimator != null)
		{
			if (mObjectAnimator.isRunning() == true)
			{
				mObjectAnimator.cancel();
				mObjectAnimator.removeAllListeners();
			}

			mObjectAnimator = null;
		}

		mDaysBackgroundView.setAnimation(null);
		mDaysLayout.setAnimation(null);

		mDaysBackgroundView.setVisibility(View.GONE);

		if (isUsedAnimatorApi() == true)
		{
			((RelativeLayout.LayoutParams) mDaysLayout.getLayoutParams()).topMargin = 0;

			mDaysLayout.setVisibility(View.INVISIBLE);
			mDaysLayout.setTranslationY(-DAYSLIST_HEIGHT);

			setActionBarAnimationLock(false);
		} else
		{
			mDaysLayout.setVisibility(View.GONE);
		}

		mAnimationStatus = ANIMATION_STATUS.HIDE_END;
	}

	private void showAnimationDaysList()
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		mCheckStatus = CHECK_NONE_STATUS;

		if (isUsedAnimatorApi() == true)
		{
			final float y = mDaysLayout.getY();

			if (mObjectAnimator != null)
			{
				if (mObjectAnimator.isRunning() == true)
				{
					mObjectAnimator.cancel();
					mObjectAnimator.removeAllListeners();
				}

				mObjectAnimator = null;
			}

			View underlineView02 = baseActivity.findViewById(R.id.tabindicator_underLine);

			mObjectAnimator = ObjectAnimator.ofFloat(mDaysLayout, "y", y, underlineView02.getBottom());
			mObjectAnimator.setDuration(300);

			mObjectAnimator.addListener(new AnimatorListener()
			{
				@Override
				public void onAnimationStart(Animator animation)
				{
					if (mDaysLayout.getVisibility() != View.VISIBLE)
					{
						mDaysLayout.setVisibility(View.VISIBLE);
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

						setDaysLayoutEnabled(true);
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
			TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, -DAYSLIST_HEIGHT, 0);
			translateAnimation.setDuration(300);
			translateAnimation.setFillBefore(true);
			translateAnimation.setFillAfter(true);
			translateAnimation.setInterpolator(baseActivity, android.R.anim.decelerate_interpolator);

			translateAnimation.setAnimationListener(new AnimationListener()
			{
				@Override
				public void onAnimationStart(Animation animation)
				{
					if (mDaysLayout.getVisibility() != View.VISIBLE)
					{
						mDaysLayout.setVisibility(View.VISIBLE);
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

					setDaysLayoutEnabled(true);
				}
			});

			if (mDaysLayout != null)
			{
				mDaysLayout.startAnimation(translateAnimation);
			}
		}

		showAnimationFadeOut();
	}

	private void hideAnimationDaysList()
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		if (isUsedAnimatorApi() == true)
		{
			final float y = mDaysLayout.getY();

			if (mObjectAnimator != null)
			{
				if (mObjectAnimator.isRunning() == true)
				{
					mObjectAnimator.cancel();
					mObjectAnimator.removeAllListeners();
				}

				mObjectAnimator = null;
			}

			View underlineView02 = baseActivity.findViewById(R.id.tabindicator_underLine);

			mObjectAnimator = ObjectAnimator.ofFloat(mDaysLayout, "y", y, underlineView02.getBottom() - DAYSLIST_HEIGHT);
			mObjectAnimator.setDuration(300);

			mObjectAnimator.addListener(new AnimatorListener()
			{
				@Override
				public void onAnimationStart(Animator animation)
				{
					mAnimationState = ANIMATION_STATE.START;
					mAnimationStatus = ANIMATION_STATUS.HIDE;

					setDaysLayoutEnabled(false);
				}

				@Override
				public void onAnimationEnd(Animator animation)
				{
					if (mAnimationState != ANIMATION_STATE.CANCEL)
					{
						mAnimationStatus = ANIMATION_STATUS.HIDE_END;
						mAnimationState = ANIMATION_STATE.END;

						hideDaysList();
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

			TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, -DAYSLIST_HEIGHT);
			translateAnimation.setDuration(300);
			translateAnimation.setFillBefore(true);
			translateAnimation.setFillAfter(true);
			translateAnimation.setInterpolator(baseActivity, android.R.anim.decelerate_interpolator);

			translateAnimation.setAnimationListener(new AnimationListener()
			{
				@Override
				public void onAnimationStart(Animation animation)
				{
					mAnimationState = ANIMATION_STATE.START;
					mAnimationStatus = ANIMATION_STATUS.HIDE;

					setDaysLayoutEnabled(false);
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

					hideDaysList();
				}
			});

			if (mDaysLayout != null)
			{
				mDaysLayout.startAnimation(translateAnimation);
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

		if (mDaysBackgroundView != null)
		{
			mDaysBackgroundView.startAnimation(mAlphaAnimation);
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
				if (mDaysBackgroundView.getVisibility() != View.VISIBLE)
				{
					mDaysBackgroundView.setVisibility(View.VISIBLE);
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

		if (mDaysBackgroundView != null)
		{
			mDaysBackgroundView.startAnimation(mAlphaAnimation);
		}
	}
}
