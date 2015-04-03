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

import com.twoheart.dailyhotel.util.ExLog;

public class HotelDaysListFragment extends HotelListFragment
{
	// 날짜가 나오는 탭의 높이이다. 마진이 있는 경우 고려해서 넣을것.px 로 넣어야 함.
	private static final int DAYSLIST_HEIGHT = 168;

	private View mDaysBackgroundView;
	private View mDaysLayout;

	private boolean mIsShowDaysList;
	private boolean mIsAnimationStart;
	private ObjectAnimator mObjectAnimator;

	private Handler mHandler = new Handler();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);

		mDaysBackgroundView = view.findViewById(R.id.daysBackgroundView);
		mDaysLayout = view.findViewById(R.id.daysLayout);

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

		hideDaysList();

		return view;
	}

	@Override
	public void onPageSelected(boolean isRequestHotelList)
	{
		super.onPageSelected(isRequestHotelList);

		if (mIsShowDaysList == false)
		{
			// 네트워크 요청이 없으면 바로 애니매이션 시작.
			if (isRequestHotelList == false)
			{
				if (mIsAnimationStart == true)
				{
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
					{
						hideAnimationDaysList();
					} else
					{
						return;
					}
				} else
				{
					showAnimationDaysList();
				}
			} else
			{
				showAnimationFadeOut();
			}
		} else
		{
			if (mIsAnimationStart == true)
			{
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
				{
					showAnimationDaysList();
				} else
				{
					return;
				}

			} else
			{
				hideAnimationDaysList();
			}
		}
	}

	@Override
	public void onPageUnSelected()
	{
		super.onPageUnSelected();

		mIsShowDaysList = false;

		hideDaysList();
	}

	@Override
	public void onRefreshComplete(boolean isSelectedNavigationItem)
	{
		if (isSelectedNavigationItem == false && mIsShowDaysList == false)
		{
			mHandler.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					showAnimationDaysList();
				}
			}, 500);
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

			// 위치 숨겨놓기.
			mHandler.post(new Runnable()
			{
				@Override
				public void run()
				{
					mDaysLayout.animate().translationY(-mDaysLayout.getHeight()).withLayer();
				}
			});
		} else
		{
			mDaysLayout.setVisibility(View.GONE);
		}
	}

	private void showAnimationDaysList()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
		{
			final float y = mDaysLayout.getY();

			if (mObjectAnimator != null)
			{
				mObjectAnimator.cancel();
				mObjectAnimator.removeAllListeners();
				mObjectAnimator = null;
			}

			mObjectAnimator = ObjectAnimator.ofFloat(mDaysLayout, "y", y, 0);
			mObjectAnimator.setDuration(500);

			mObjectAnimator.addListener(new AnimatorListener()
			{
				@Override
				public void onAnimationStart(Animator animation)
				{
					mDaysLayout.setVisibility(View.VISIBLE);
					mIsAnimationStart = true;
				}

				@Override
				public void onAnimationEnd(Animator animation)
				{
					mIsShowDaysList = true;
					mIsAnimationStart = false;
				}

				@Override
				public void onAnimationCancel(Animator animation)
				{
					mIsAnimationStart = false;

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
			translateAnimation.setDuration(500);
			translateAnimation.setFillBefore(true);
			translateAnimation.setFillAfter(true);
			translateAnimation.setInterpolator(mHostActivity, android.R.anim.decelerate_interpolator);

			translateAnimation.setAnimationListener(new AnimationListener()
			{
				@Override
				public void onAnimationStart(Animation animation)
				{
					mDaysLayout.setVisibility(View.VISIBLE);
					mIsAnimationStart = true;
				}

				@Override
				public void onAnimationRepeat(Animation animation)
				{

				}

				@Override
				public void onAnimationEnd(Animation animation)
				{
					mIsShowDaysList = true;
					mIsAnimationStart = false;
				}
			});

			mDaysLayout.startAnimation(translateAnimation);
		}

		showAnimationFadeOut();
	}

	private void hideAnimationDaysList()
	{
		final float y = mDaysLayout.getY();

		ExLog.d("pinkred y :" + y);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
		{
			if (mObjectAnimator != null)
			{
				mObjectAnimator.cancel();
				mObjectAnimator.removeAllListeners();
				mObjectAnimator = null;
			}

			mObjectAnimator = ObjectAnimator.ofFloat(mDaysLayout, "y", y, -DAYSLIST_HEIGHT);
			mObjectAnimator.setDuration(500);

			mObjectAnimator.addListener(new AnimatorListener()
			{
				@Override
				public void onAnimationStart(Animator animation)
				{
					mIsAnimationStart = true;
				}

				@Override
				public void onAnimationEnd(Animator animation)
				{
					mIsShowDaysList = false;
					mIsAnimationStart = false;

					mDaysLayout.setVisibility(View.INVISIBLE);
				}

				@Override
				public void onAnimationCancel(Animator animation)
				{
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
			translateAnimation.setDuration(500);
			translateAnimation.setFillBefore(true);
			translateAnimation.setFillAfter(true);
			translateAnimation.setInterpolator(mHostActivity, android.R.anim.decelerate_interpolator);

			translateAnimation.setAnimationListener(new AnimationListener()
			{
				@Override
				public void onAnimationStart(Animation animation)
				{
					mIsAnimationStart = true;
				}

				@Override
				public void onAnimationRepeat(Animation animation)
				{
				}

				@Override
				public void onAnimationEnd(Animation animation)
				{
					mIsShowDaysList = false;
					mIsAnimationStart = false;
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
		if (mDaysBackgroundView.getVisibility() == View.GONE)
		{
			return;
		}

		AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
		alphaAnimation.setDuration(500);
		alphaAnimation.setFillBefore(true);
		alphaAnimation.setFillAfter(true);

		alphaAnimation.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationStart(Animation animation)
			{
			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				hideDaysList();
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
			}
		});

		mDaysBackgroundView.startAnimation(alphaAnimation);
	}

	/**
	 * 점점 어두워짐.
	 */
	private void showAnimationFadeOut()
	{
		if (mDaysBackgroundView.getVisibility() == View.VISIBLE)
		{
			return;
		}

		AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
		alphaAnimation.setDuration(500);
		alphaAnimation.setFillBefore(true);
		alphaAnimation.setFillAfter(true);

		alphaAnimation.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationStart(Animation animation)
			{
				mDaysBackgroundView.setVisibility(View.VISIBLE);
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

		mDaysBackgroundView.startAnimation(alphaAnimation);
	}
}
