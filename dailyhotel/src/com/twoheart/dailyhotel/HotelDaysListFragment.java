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
import android.widget.TextView;

import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class HotelDaysListFragment extends HotelListFragment implements OnClickListener
{
	private static final int HANDLER_MESSAGE_SHOWDAYSLIST = 1;
	private static final int DAY_OF_COUNT = 5;

	// 날짜가 나오는 탭의 높이이다. 마진이 있는 경우 고려해서 넣을것.px 로 넣어야 함.
	private int DAYSLIST_HEIGHT;

	private View mDaysBackgroundView;
	private View mDaysLayout;

	private ANIMATION_STATUS mAnimationStatus = ANIMATION_STATUS.HIDE_END;
	private ANIMATION_STATE mAnimationState = ANIMATION_STATE.END;
	private ObjectAnimator mObjectAnimator;
	private AlphaAnimation mAlphaAnimation;

	private View[] mDaysView;
	private View mSelectedView = null;
	private boolean mIsShowDaysList;

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

		DAYSLIST_HEIGHT = Util.dpToPx(baseActivity, 110);

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
		if (mDaysView != null)
		{
			return;
		}

		mDaysView = new View[DAY_OF_COUNT];

		// 날짜를 어떻게 받을 것인지 필요.
		mDaysView[0] = mDaysLayout.findViewById(R.id.item01);
		mDaysView[1] = mDaysLayout.findViewById(R.id.item02);
		mDaysView[2] = mDaysLayout.findViewById(R.id.item03);
		mDaysView[3] = mDaysLayout.findViewById(R.id.item04);
		mDaysView[4] = mDaysLayout.findViewById(R.id.item05);
		//		mDaysView[5] = mDaysLayout.findViewById(R.id.item06);
		//		mDaysView[6] = mDaysLayout.findViewById(R.id.item07);

		initLayoutDays(mDaysView[0], mSaleTime.getClone(2));
		initLayoutDays(mDaysView[1], mSaleTime.getClone(3));
		initLayoutDays(mDaysView[2], mSaleTime.getClone(4));
		initLayoutDays(mDaysView[3], mSaleTime.getClone(5));
		initLayoutDays(mDaysView[4], mSaleTime.getClone(6));
		//		initLayoutDays(mDaysView[5], mSaleTime.getClone(3));
		//		initLayoutDays(mDaysView[6], mSaleTime.getClone(4));

		if (mSelectedView == null)
		{
			setSelectedDays(mDaysView[0]);
		} else
		{
			setSelectedDays(mSelectedView);

			super.setSaleTime((SaleTime) mSelectedView.getTag());
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

		if (mIsShowDaysList == true && mAnimationStatus == ANIMATION_STATUS.HIDE_END)
		{
			mIsShowDaysList = false;

			mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SHOWDAYSLIST, 400);
		}
	}

	@Override
	public void onClick(View v)
	{
		setSelectedDays(v);

		mSaleTime = (SaleTime) v.getTag();

		setDaysLayoutEnabled(false);

		mUserActionListener.selectDay(this, true);

		mHandler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				hideAnimationDaysList();
			}

		}, 500);
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
			view.setEnabled(enabled);
		}

		mDaysBackgroundView.setEnabled(enabled);
	}

	private void setSelectedDays(View view)
	{
		if (view == null || mDaysView == null)
		{
			return;
		}

		mSelectedView = view;

		for (View dayView : mDaysView)
		{
			TextView dayOfTheWeekTextView = (TextView) dayView.findViewById(R.id.textView1);
			TextView dayTextView = (TextView) dayView.findViewById(R.id.textView2);

			boolean selectedView = dayView == view;

			dayOfTheWeekTextView.setSelected(selectedView);
			dayTextView.setSelected(selectedView);
			((View) dayTextView.getParent()).setSelected(selectedView);
		}
	}

	private void hideDaysList()
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

		mDaysBackgroundView.setAnimation(null);
		mDaysLayout.setAnimation(null);

		mDaysBackgroundView.setVisibility(View.GONE);

		if (isUsedAnimatorApi() == true)
		{
			mDaysLayout.setVisibility(View.INVISIBLE);
			mDaysLayout.setTranslationY(-DAYSLIST_HEIGHT);
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
			View underlineView02 = baseActivity.findViewById(R.id.tabindicator_underLine);

			TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, underlineView02.getBottom() - DAYSLIST_HEIGHT, underlineView02.getBottom());
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
			View underlineView02 = baseActivity.findViewById(R.id.tabindicator_underLine);

			TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, underlineView02.getBottom(), underlineView02.getBottom() - DAYSLIST_HEIGHT);
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
