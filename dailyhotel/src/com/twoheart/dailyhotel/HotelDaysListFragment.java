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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

public class HotelDaysListFragment extends HotelListFragment implements OnClickListener
{
	private static final int DAY_OF_COUNT = 7;

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

	private Handler mHandler = new Handler();
	
	private enum ANIMATION_STATE
	{
		START,
		END,
		CANCEL
	};
	
	private enum ANIMATION_STATUS
	{
		SHOW,
		HIDE,
		SHOW_END,
		HIDE_END
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);

		mDaysBackgroundView = view.findViewById(R.id.daysBackgroundView);
		mDaysLayout = view.findViewById(R.id.daysLayout);

		DAYSLIST_HEIGHT = Util.dpToPx(mHostActivity, 110);

		mDaysBackgroundView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
				{
					hideAnimationDaysList();
				}
			}
		});

		if (mDaysView == null)
		{
			mDaysView = new View[DAY_OF_COUNT];
		}

		hideDaysList();

		return view;
	}

	@Override
	public void setSaleTime(SaleTime saleTime)
	{
		super.setSaleTime(saleTime);

		// 날짜를 어떻게 받을 것인지 필요.
		mDaysView[0] = mDaysLayout.findViewById(R.id.item01);
		mDaysView[1] = mDaysLayout.findViewById(R.id.item02);
		mDaysView[2] = mDaysLayout.findViewById(R.id.item03);
		mDaysView[3] = mDaysLayout.findViewById(R.id.item04);
		mDaysView[4] = mDaysLayout.findViewById(R.id.item05);
		mDaysView[5] = mDaysLayout.findViewById(R.id.item06);
		mDaysView[6] = mDaysLayout.findViewById(R.id.item07);

		initLayoutDays(mDaysView[0], mSaleTime.getClone(-2));
		initLayoutDays(mDaysView[1], mSaleTime.getClone(-1));
		initLayoutDays(mDaysView[2], mSaleTime.getClone(0));
		initLayoutDays(mDaysView[3], mSaleTime.getClone(1));
		initLayoutDays(mDaysView[4], mSaleTime.getClone(2));
		initLayoutDays(mDaysView[5], mSaleTime.getClone(3));
		initLayoutDays(mDaysView[6], mSaleTime.getClone(4));
		
		if(mSelectedView == null)
		{
			setSelectedDays(mDaysView[0]);
		} else
		{
			setSelectedDays(mSelectedView);
		}
	}

	@Override
	public void onPageSelected(boolean isRequestHotelList)
	{
		super.onPageSelected(isRequestHotelList);
		
		ExLog.d("pinkred : mAnimationStatus : " + mAnimationState + ", mAnimationStatus : " + mAnimationStatus + ", isRequestHotelList : " + isRequestHotelList);

		switch(mAnimationStatus)
		{
			case SHOW:
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
				{
					hideAnimationDaysList();
				} else
				{
					return;
				}
				break;
				
			case HIDE:
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
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
				if (isRequestHotelList == true)
				{
					showAnimationFadeOut();
				} else
				{
					showAnimationDaysList();
				}
				break;
		}
	}

	@Override
	public void onPageUnSelected()
	{
		super.onPageUnSelected();
		
		ExLog.d("onPageUnSelected");

		hideDaysList();
	}

	@Override
	public void onRefreshComplete(boolean isSelectedNavigationItem)
	{
		if (isSelectedNavigationItem == false && mAnimationStatus == ANIMATION_STATUS.HIDE_END)
		{
			mHandler.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					showAnimationDaysList();
				}
			}, 400);
		}
	}

	@Override
	public void onClick(View v)
	{
		setSelectedDays(v);
		
		mSaleTime = (SaleTime) v.getTag();
		
		mUserActionListener.selectDay(this);

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

		dayOfTheWeekTextView.setText(saleTime.getCurrentDayOftheWeek());
		dayTextView.setText(saleTime.getCurrentDayEx());

		view.setOnClickListener(this);

		view.setTag(saleTime);
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
		}
	}

	private void hideDaysList()
	{
		mDaysBackgroundView.setAnimation(null);
		mDaysLayout.setAnimation(null);

		mDaysBackgroundView.setVisibility(View.GONE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
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
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
		{
			final float y = mDaysLayout.getY();
			
			ExLog.d("pinkred showAnimationDaysList y :" + y);

			if (mObjectAnimator != null)
			{
				if (mObjectAnimator.isRunning() == true)
				{
					mObjectAnimator.cancel();
					mObjectAnimator.removeAllListeners();
				}

				mObjectAnimator = null;
			}

			mObjectAnimator = ObjectAnimator.ofFloat(mDaysLayout, "y", y, 0);
			mObjectAnimator.setDuration(300);

			mObjectAnimator.addListener(new AnimatorListener()
			{
				@Override
				public void onAnimationStart(Animator animation)
				{
					if(mDaysLayout.getVisibility() != View.VISIBLE)
					{
						mDaysLayout.setVisibility(View.VISIBLE);
					}
					
					mAnimationState = ANIMATION_STATE.START;
					mAnimationStatus = ANIMATION_STATUS.SHOW;
					
					ExLog.d("pinkred showAnimationDaysList - onAnimationStart");
				}

				@Override
				public void onAnimationEnd(Animator animation)
				{
					ExLog.d("pinkred showAnimationDaysList - onAnimationEnd");
					
					if(mAnimationState != ANIMATION_STATE.CANCEL)
					{
						mAnimationStatus = ANIMATION_STATUS.SHOW_END;
						mAnimationState = ANIMATION_STATE.END;
					}
				}

				@Override
				public void onAnimationCancel(Animator animation)
				{
					mAnimationState = ANIMATION_STATE.CANCEL;
					
					ExLog.d("pinkred showAnimationDaysList - onAnimationCancel");
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
			translateAnimation.setInterpolator(mHostActivity, android.R.anim.decelerate_interpolator);

			translateAnimation.setAnimationListener(new AnimationListener()
			{
				@Override
				public void onAnimationStart(Animation animation)
				{
					mDaysLayout.setVisibility(View.VISIBLE);
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
				}
			});

			mDaysLayout.startAnimation(translateAnimation);
		}

		showAnimationFadeOut();
	}

	private void hideAnimationDaysList()
	{
		final float y = mDaysLayout.getY();

		ExLog.d("pinkred hideAnimationDaysList y :" + y);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
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

			mObjectAnimator = ObjectAnimator.ofFloat(mDaysLayout, "y", y, -DAYSLIST_HEIGHT);
			mObjectAnimator.setDuration(300);

			mObjectAnimator.addListener(new AnimatorListener()
			{
				@Override
				public void onAnimationStart(Animator animation)
				{
					mAnimationState = ANIMATION_STATE.START;
					mAnimationStatus = ANIMATION_STATUS.HIDE;
					ExLog.d("pinkred hideAnimationDaysList - onAnimationStart");
				}

				@Override
				public void onAnimationEnd(Animator animation)
				{
					ExLog.d("pinkred hideAnimationDaysList - onAnimationEnd : " + mAnimationState);
					
					if(mAnimationState != ANIMATION_STATE.CANCEL)
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
					
					ExLog.d("pinkred hideAnimationDaysList - onAnimationCancel");
				}

				@Override
				public void onAnimationRepeat(Animator animation)
				{
				}
			});

			mObjectAnimator.start();
		} else
		{
			TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, y, -DAYSLIST_HEIGHT);
			translateAnimation.setDuration(300);
			translateAnimation.setFillBefore(true);
			translateAnimation.setFillAfter(true);
			translateAnimation.setInterpolator(mHostActivity, android.R.anim.decelerate_interpolator);

			translateAnimation.setAnimationListener(new AnimationListener()
			{
				@Override
				public void onAnimationStart(Animation animation)
				{
					mAnimationState = ANIMATION_STATE.START;
					mAnimationStatus = ANIMATION_STATUS.HIDE;
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
				}
			});

			mDaysLayout.startAnimation(translateAnimation);
		}

		showAnimationFadeIn();
	}

	/**
	 * 점점 밝아짐.
	 */
	private void showAnimationFadeIn()
	{
		if(mAlphaAnimation != null)
		{
			if(mAlphaAnimation.hasEnded() == false)
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

		mDaysBackgroundView.startAnimation(mAlphaAnimation);
	}

	/**
	 * 점점 어두워짐.
	 */
	private void showAnimationFadeOut()
	{
		if(mAlphaAnimation != null)
		{
			if(mAlphaAnimation.hasEnded() == false)
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
				if(mDaysBackgroundView.getVisibility() != View.VISIBLE)
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

		mDaysBackgroundView.startAnimation(mAlphaAnimation);
	}
}
