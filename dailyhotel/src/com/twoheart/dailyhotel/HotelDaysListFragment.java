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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class HotelDaysListFragment extends HotelListFragment implements OnClickListener
{
	private static final int HANDLER_MESSAGE_SHOWDAYSLIST = 1;
	private static final int CHECK_IN_DATE = 7;
	private static final int CHECK_OUT_DATE = 5;

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

	private View[] mCheckInViews;
	private View[] mCheckOutViews;

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

		if (mCheckInViews == null)
		{
			mCheckInViews = new View[CHECK_IN_DATE];
		}

		if (mCheckOutViews == null)
		{
			mCheckOutViews = new View[CHECK_OUT_DATE];
		}

		View daysLayout01 = mDaysLayout.findViewById(R.id.daysLayout01);
		View daysLayout02 = mDaysLayout.findViewById(R.id.daysLayout02);

		daysLayout01.setVisibility(View.VISIBLE);
		daysLayout02.setVisibility(View.GONE);

		for (int i = 0; i < CHECK_IN_DATE; i++)
		{
			mCheckInViews[i] = daysLayout01.findViewById(R.id.item01 + i);
		}

		for (int i = 0; i < CHECK_OUT_DATE; i++)
		{
			mCheckOutViews[i] = daysLayout02.findViewById(R.id.item08 + i);
		}

		DAYSLIST_HEIGHT = Util.dpToPx(baseActivity, 110);

		initCheckInDateLayout(mSaleTime);
	}

	private void initCheckInDateLayout(SaleTime defaultSaleTime)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		for (int i = 0; i < CHECK_IN_DATE; i++)
		{
			initLayoutDays(mCheckInViews[i], defaultSaleTime.getClone(i));
			mCheckInViews[i].setTag(mCheckInViews[i].getId(), i);
		}
	}

	private void initCheckOutDateLayout(SaleTime checkInSaleTime)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		View daysLayout02 = mDaysLayout.findViewById(R.id.daysLayout02);
		daysLayout02.setVisibility(View.GONE);

		for (int i = 0; i < CHECK_OUT_DATE; i++)
		{
			initLayoutDays(mCheckOutViews[i], checkInSaleTime.getClone(checkInSaleTime.getOffsetDailyDay() + i + 1));
			mCheckOutViews[i].setTag(mCheckOutViews[i].getId(), i);
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
		switch (mCheckStatus)
		{
			case CHECK_NONE_STATUS:
				mCheckStatus = CHECK_IN_STATUS;

				setSelectedCheckInDays(v);
				setDaysLayoutEnabled(false);

				// 체크아웃 화면을 만들어야 한다.
				initCheckOutDateLayout((SaleTime) v.getTag());

				showAnimationCheckIn(v, (Integer) v.getTag(v.getId()));
				break;

			case CHECK_IN_STATUS:
				mCheckStatus = CHECK_OUT_STATUS;

				setSelectedCheckOutDays(v);
				setDaysLayoutEnabled(false);

				showAnimationCheckOut();
				break;

			case CHECK_OUT_STATUS:
				mCheckStatus = CHECK_OK_STATUS;

				break;
		}
	}

	private void resetCheckDays()
	{
		if (mCheckInViews == null || mCheckOutViews == null)
		{
			return;
		}

		mSelectedCheckInView = mCheckInViews[0];
		mSelectedCheckOutView = mCheckInViews[1];

		for (View dayView : mCheckInViews)
		{
			TextView dayOfTheWeekTextView = (TextView) dayView.findViewById(R.id.textView1);
			TextView dayTextView = (TextView) dayView.findViewById(R.id.textView2);

			dayOfTheWeekTextView.setSelected(false);
			dayTextView.setSelected(false);
			((View) dayTextView.getParent()).setSelected(false);
		}

		for (View dayView : mCheckOutViews)
		{
			TextView dayOfTheWeekTextView = (TextView) dayView.findViewById(R.id.textView1);
			TextView dayTextView = (TextView) dayView.findViewById(R.id.textView2);

			dayOfTheWeekTextView.setSelected(false);
			dayTextView.setSelected(false);
			((View) dayTextView.getParent()).setSelected(false);
		}

		if (isUsedAnimatorApi() == true)
		{
			for (View dayView : mCheckInViews)
			{
				dayView.setAlpha(1.0f);
				dayView.setVisibility(View.VISIBLE);
				dayView.setTranslationX(0);
			}

			for (View dayView : mCheckOutViews)
			{
				dayView.setAlpha(1.0f);
			}
		} else
		{
			for (View dayView : mCheckInViews)
			{
				dayView.setVisibility(View.VISIBLE);
			}
		}
	}

	private void setDaysLayoutEnabled(boolean enabled)
	{
		if (mCheckInViews == null)
		{
			return;
		}

		for (View view : mCheckInViews)
		{
			view.setEnabled(enabled);
		}

		if (mCheckOutViews != null)
		{
			for (View view : mCheckOutViews)
			{
				view.setEnabled(enabled);
			}
		}

		mDaysBackgroundView.setEnabled(enabled);
	}

	private void setSelectedCheckInDays(View view)
	{
		if (view == null || mCheckInViews == null)
		{
			return;
		}

		mSelectedCheckInView = view;

		TextView dayOfTheWeekTextView = (TextView) view.findViewById(R.id.textView1);
		TextView dayTextView = (TextView) view.findViewById(R.id.textView2);

		dayOfTheWeekTextView.setSelected(true);
		dayTextView.setSelected(true);
		((View) dayTextView.getParent()).setSelected(true);
	}

	private void setSelectedCheckOutDays(View view)
	{
		if (view == null || mCheckOutViews == null)
		{
			return;
		}

		mSelectedCheckOutView = view;

		for (View dayView : mCheckOutViews)
		{
			TextView dayOfTheWeekTextView = (TextView) dayView.findViewById(R.id.textView1);
			TextView dayTextView = (TextView) dayView.findViewById(R.id.textView2);

			boolean selected = view == dayView ? true : false;

			dayOfTheWeekTextView.setSelected(selected);
			dayTextView.setSelected(selected);
			((View) dayTextView.getParent()).setSelected(selected);
		}
	}

	private void hideDaysList()
	{
		if (mCheckStatus != CHECK_OK_STATUS)
		{
			mCheckStatus = CHECK_NONE_STATUS;

			resetCheckDays();
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

	private void showAnimationCheckIn(final View view, final int position)
	{
		if (isUsedAnimatorApi() == true)
		{
			final View daysLayout02 = mDaysLayout.findViewById(R.id.daysLayout02);
			daysLayout02.setVisibility(View.GONE);

			ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
			valueAnimator.setDuration(300).addUpdateListener(new AnimatorUpdateListener()
			{
				@Override
				public void onAnimationUpdate(ValueAnimator animation)
				{
					int value = (Integer) animation.getAnimatedValue();

					for (int i = 0; i < CHECK_IN_DATE; i++)
					{
						float translationX = value * (mCheckInViews[0].getX() - (mCheckInViews[i].getX() - mCheckInViews[i].getTranslationX())) / 100;
						mCheckInViews[i].setTranslationX(translationX);

						if (i != position)
						{
							mCheckInViews[i].setAlpha((100.0f - value) / 100.0f);
						}
					}

					daysLayout02.setAlpha(value / 100.0f);
				}
			});

			valueAnimator.addListener(new AnimatorListener()
			{
				@Override
				public void onAnimationStart(Animator animation)
				{
					daysLayout02.setVisibility(View.VISIBLE);
				}

				@Override
				public void onAnimationRepeat(Animator animation)
				{
				}

				@Override
				public void onAnimationEnd(Animator animation)
				{
					setDaysLayoutEnabled(true);

					for (int i = 0; i < CHECK_IN_DATE; i++)
					{
						if (i != position)
						{
							mCheckInViews[i].setVisibility(View.INVISIBLE);
						}
					}

					setSelectedCheckOutDays(mCheckOutViews[0]);

					animation.removeAllListeners();
				}

				@Override
				public void onAnimationCancel(Animator animation)
				{

				}
			});

			valueAnimator.start();
		} else
		{

		}
	}

	private void showAnimationCheckOut()
	{
		mHandler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				hideAnimationDaysList();
			}
		}, 500);

		// 여기서 호텔 리스트를 다시 갱신해야 한다.
		if (mUserActionListener != null)
		{
			mUserActionListener.selectDay((SaleTime) mSelectedCheckInView.getTag(), (SaleTime) mSelectedCheckOutView.getTag(), true);
		}
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

					resetCheckDays();

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
