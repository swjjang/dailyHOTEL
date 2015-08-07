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

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Typeface;
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
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
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

	private SaleTime mSelectedCheckInSaleTime;
	private SaleTime mSelectedCheckOutSaleTime;
	private TextView mCheckInOutTextView;
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
				switch (mCheckStatus)
				{
					case CHECK_NONE_STATUS:
						break;

					case CHECK_IN_STATUS:
						// 여기서 호텔 리스트를 다시 갱신해야 한다.
						if (mUserActionListener != null)
						{
							mUserActionListener.selectDay(mSelectedCheckInSaleTime, mSelectedCheckOutSaleTime, true);
						}
						break;

					case CHECK_OUT_STATUS:
						break;
				}

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

		mCheckInOutTextView = (TextView) mDaysLayout.findViewById(R.id.checkInOutTextView);

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

		DAYSLIST_HEIGHT = Util.dpToPx(baseActivity, 131);

		initCheckInDateLayout(mSaleTime);
	}

	public void initSelectedCheckInOutDate(SaleTime checkInSaleTime, SaleTime checkOutSaleTime)
	{
		mSelectedCheckInSaleTime = checkInSaleTime;
		mSelectedCheckOutSaleTime = checkOutSaleTime;
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

		dayOfTheWeekTextView.setTypeface(dayOfTheWeekTextView.getTypeface(), Typeface.NORMAL);
		dayTextView.setTypeface(dayTextView.getTypeface(), Typeface.NORMAL);

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
				if (Util.isOverAPI12() == true)
				{
					hideAnimationDaysList();
				} else
				{
					return;
				}
				break;

			case HIDE:
				if (Util.isOverAPI12() == true)
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
		if (isLockUiComponent() == true)
		{
			return;
		}

		lockUiComponent();

		switch (mCheckStatus)
		{
			case CHECK_NONE_STATUS:
				mCheckStatus = CHECK_IN_STATUS;

				setSelectedCheckInDays(v);
				setDaysLayoutEnabled(false);

				// 체크아웃 화면을 만들어야 한다.
				initCheckOutDateLayout((SaleTime) v.getTag());
				setSelectedCheckOutDays(mCheckOutViews[0]);

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
				releaseUiComponent();
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parentView, View childView, int position, long id)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		position -= mHotelListView.getHeaderViewsCount();

		if (position < 0)
		{
			refreshHotelList(mSelectedProvince, true);
			return;
		}

		mSelectedHotelListViewItem = mHotelListAdapter.getItem(position);

		Map<String, String> params = new HashMap<String, String>();
		params.put("timeZone", "Asia/Seoul");

		mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_COMMON_DATETIME).toString(), params, mDateTimeJsonResponseListener, baseActivity));
	}

	private void resetCheckDays()
	{
		if (mCheckInViews == null || mCheckOutViews == null)
		{
			return;
		}

		//		mSelectedCheckInSaleTime = (SaleTime) mCheckInViews[0].getTag();
		//		mSelectedCheckOutSaleTime = (SaleTime) mCheckInViews[1].getTag();

		mCheckInOutTextView.setText(R.string.frag_hotel_list_checkin);

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

		if (Util.isOverAPI12() == true)
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
				LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) dayView.getLayoutParams();
				layoutParams.weight = 1;
				layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;

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

		setCheckInLayoutEnabled(enabled);
		setCheckOutLayoutEnabled(enabled);

		mDaysBackgroundView.setEnabled(enabled);
	}

	private void setCheckInLayoutEnabled(boolean enabled)
	{
		if (mCheckInViews == null)
		{
			return;
		}

		for (View view : mCheckInViews)
		{
			view.setEnabled(enabled);
		}
	}

	private void setCheckOutLayoutEnabled(boolean enabled)
	{
		if (mCheckOutViews == null)
		{
			return;
		}

		for (View view : mCheckOutViews)
		{
			view.setEnabled(enabled);
		}
	}

	private void setSelectedCheckInDays(View view)
	{
		if (view == null || mCheckInViews == null)
		{
			return;
		}

		mSelectedCheckInSaleTime = (SaleTime) view.getTag();

		TextView dayOfTheWeekTextView = (TextView) view.findViewById(R.id.textView1);
		TextView dayTextView = (TextView) view.findViewById(R.id.textView2);

		dayOfTheWeekTextView.setTypeface(dayOfTheWeekTextView.getTypeface(), Typeface.BOLD);
		dayTextView.setTypeface(dayTextView.getTypeface(), Typeface.BOLD);

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

		mSelectedCheckOutSaleTime = (SaleTime) view.getTag();

		for (View dayView : mCheckOutViews)
		{
			TextView dayOfTheWeekTextView = (TextView) dayView.findViewById(R.id.textView1);
			TextView dayTextView = (TextView) dayView.findViewById(R.id.textView2);

			boolean selected = view == dayView ? true : false;

			if (selected == true)
			{
				dayOfTheWeekTextView.setTypeface(dayOfTheWeekTextView.getTypeface(), Typeface.BOLD);
				dayTextView.setTypeface(dayTextView.getTypeface(), Typeface.BOLD);
			} else
			{
				dayOfTheWeekTextView.setTypeface(dayOfTheWeekTextView.getTypeface(), Typeface.NORMAL);
				dayTextView.setTypeface(dayTextView.getTypeface(), Typeface.NORMAL);
			}

			dayOfTheWeekTextView.setSelected(selected);
			dayTextView.setSelected(selected);
			((View) dayTextView.getParent()).setSelected(selected);
		}
	}

	private void hideDaysList()
	{
		releaseUiComponent();

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
			}

			mObjectAnimator.removeAllListeners();
			mObjectAnimator = null;
		}

		mDaysBackgroundView.setAnimation(null);
		mDaysLayout.setAnimation(null);

		mDaysBackgroundView.setVisibility(View.GONE);

		if (Util.isOverAPI12() == true)
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

	@Override
	public void refreshHotelList(Province province, boolean isSelectionTop)
	{
		mSelectedProvince = province;
		mIsSelectionTop = isSelectionTop;

		fetchHotelList(province, mSelectedCheckInSaleTime, mSelectedCheckOutSaleTime);
	}

	private void showAnimationCheckIn(final View view, final int position)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null || baseActivity.isFinishing() == true)
		{
			return;
		}

		if (Util.isOverAPI12() == true)
		{
			final View daysLayout02 = mDaysLayout.findViewById(R.id.daysLayout02);
			final View toTextView = daysLayout02.findViewById(R.id.toTextView);
			daysLayout02.setVisibility(View.INVISIBLE);

			ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
			valueAnimator.setDuration(400).addUpdateListener(new AnimatorUpdateListener()
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

					float toX = mCheckOutViews[CHECK_OUT_DATE - 1].getX() - (toTextView.getX() - toTextView.getTranslationX());
					toTextView.setTranslationX(toX - value * toX / 100.0f);

					for (int i = 0; i < CHECK_OUT_DATE; i++)
					{
						float x1 = mCheckOutViews[CHECK_OUT_DATE - 1].getX() - (mCheckOutViews[i].getX() - mCheckOutViews[i].getTranslationX());
						float translationX = x1 - value * x1 / 100.0f;
						mCheckOutViews[i].setTranslationX(translationX);

						daysLayout02.setAlpha(value / 100.0f);
					}
				}
			});

			valueAnimator.addListener(new AnimatorListener()
			{
				@Override
				public void onAnimationStart(Animator animation)
				{
					daysLayout02.setVisibility(View.VISIBLE);

					// 체크아웃 카드를 오른쪽으로 정렬시킨다.
					toTextView.setTranslationX(mCheckInViews[CHECK_IN_DATE - 1].getX() - mCheckInViews[1].getX());

					for (int i = 0; i < CHECK_OUT_DATE; i++)
					{
						float translationX = mCheckInViews[CHECK_IN_DATE - 1].getX() - mCheckInViews[i + 2].getX();
						mCheckOutViews[i].setTranslationX(translationX);
					}
				}

				@Override
				public void onAnimationRepeat(Animator animation)
				{
				}

				@Override
				public void onAnimationEnd(Animator animation)
				{
					setDaysLayoutEnabled(true);
					setCheckInLayoutEnabled(false);

					for (int i = 0; i < CHECK_IN_DATE; i++)
					{
						if (i != position)
						{
							mCheckInViews[i].setVisibility(View.INVISIBLE);
						}
					}

					mCheckInOutTextView.setText(R.string.frag_hotel_list_checkout);

					animation.removeAllListeners();

					releaseUiComponent();
				}

				@Override
				public void onAnimationCancel(Animator animation)
				{

				}
			});

			valueAnimator.start();
		} else
		{
			for (int i = 0; i < CHECK_IN_DATE; i++)
			{
				if (i != position)
				{
					mCheckInViews[i].setVisibility(View.INVISIBLE);
				}
			}

			TranslateAnimation translateAnimation = new TranslateAnimation(0, mCheckInViews[0].getLeft() - mCheckInViews[position].getLeft(), 0, 0);
			translateAnimation.setDuration(300);
			translateAnimation.setFillBefore(true);
			translateAnimation.setFillAfter(true);
			translateAnimation.setInterpolator(baseActivity, android.R.anim.decelerate_interpolator);

			translateAnimation.setAnimationListener(new AnimationListener()
			{

				@Override
				public void onAnimationStart(Animation animation)
				{
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animation animation)
				{
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation animation)
				{
					mCheckInViews[position].setAnimation(null);
					setDaysLayoutEnabled(true);
					setCheckInLayoutEnabled(false);

					mCheckInOutTextView.setText(R.string.frag_hotel_list_checkout);

					for (int i = 0; i < CHECK_IN_DATE; i++)
					{
						if (i == position)
						{
							((LinearLayout.LayoutParams) (mCheckInViews[i].getLayoutParams())).weight = 0;
							((LinearLayout.LayoutParams) (mCheckInViews[i].getLayoutParams())).width = LinearLayout.LayoutParams.WRAP_CONTENT;
						} else
						{
							mCheckInViews[i].setVisibility(View.GONE);
						}
					}

					View daysLayout02 = mDaysLayout.findViewById(R.id.daysLayout02);
					daysLayout02.setVisibility(View.VISIBLE);

					releaseUiComponent();
				}
			});

			mCheckInViews[position].startAnimation(translateAnimation);
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

				// 여기서 호텔 리스트를 다시 갱신해야 한다.
				if (mUserActionListener != null)
				{
					mUserActionListener.selectDay(mSelectedCheckInSaleTime, mSelectedCheckOutSaleTime, true);
				}
			}
		}, 300);
	}

	private void showAnimationDaysList()
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null || baseActivity.isFinishing() == true)
		{
			return;
		}

		mCheckStatus = CHECK_NONE_STATUS;

		if (Util.isOverAPI12() == true)
		{
			final float y = mDaysLayout.getY();

			if (mObjectAnimator != null)
			{
				if (mObjectAnimator.isRunning() == true)
				{
					mObjectAnimator.cancel();
				}

				mObjectAnimator.removeAllListeners();
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

		if (baseActivity == null || baseActivity.isFinishing() == true)
		{
			return;
		}

		if (Util.isOverAPI12() == true)
		{
			final float y = mDaysLayout.getY();

			if (mObjectAnimator != null)
			{
				if (mObjectAnimator.isRunning() == true)
				{
					mObjectAnimator.cancel();
				}

				mObjectAnimator.removeAllListeners();
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

					releaseUiComponent();
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

					releaseUiComponent();
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

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private DailyHotelJsonResponseListener mDateTimeJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		@Override
		public void onResponse(String url, JSONObject response)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				SaleTime saleTime = new SaleTime();
				saleTime.setCurrentTime(response.getLong("currentDateTime"));
				saleTime.setOpenTime(response.getLong("openDateTime"));
				saleTime.setCloseTime(response.getLong("closeDateTime"));
				saleTime.setDailyTime(response.getLong("dailyDateTime"));

				if (saleTime.isSaleTime() == true)
				{
					if (mUserActionListener != null)
					{
						mUserActionListener.selectHotel(mSelectedHotelListViewItem, mSelectedCheckInSaleTime);
					}
				} else
				{
					((MainActivity) baseActivity).replaceFragment(WaitTimerFragment.newInstance(mSaleTime));
					unLockUI();
				}
			} catch (Exception e)
			{
				onError(e);
				unLockUI();
			}
		}
	};
}
