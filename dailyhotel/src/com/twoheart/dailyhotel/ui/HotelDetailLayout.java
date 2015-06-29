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
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.HotelDetailActivity;
import com.twoheart.dailyhotel.adapter.HotelDetailImageViewPagerAdapter;
import com.twoheart.dailyhotel.model.HotelDetail;
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
	private HotelDetailListView mListView;
	private HotelDetailImageViewPagerAdapter mImageAdapter;
	private HotelDetailListAdapter mListAdapter;

	private int mStatusBarHeight;
	private int mImageHeight;

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

		mListView = (HotelDetailListView) mViewRoot.findViewById(R.id.hotelListView);
		mListView.setOnScrollListener(mOnScrollListener);
		
		// 이미지 ViewPage 넣기.
		mViewPager = (ViewPager) mViewRoot.findViewById(R.id.defaultHotelImageView);
		mViewPager.setOnPageChangeListener(mOnPageChangeListener);

		mImageHeight = Util.getLCDWidth(context);
		LayoutParams layoutParams = (LayoutParams) mViewPager.getLayoutParams();
		layoutParams.height = mImageHeight;
	}

	private void initHotelDetailLayout(HotelDetail hotelDetail)
	{

	}

	public View getView()
	{
		return mViewRoot;
	}

	public void setHotelDetail(HotelDetail hotelDetail, int imagePosition)
	{
		mHotelDetail = hotelDetail;

		if (mImageAdapter == null)
		{
			mImageAdapter = new HotelDetailImageViewPagerAdapter(mActivity);
		}
		
		if(mListAdapter == null)
		{
			mListAdapter = new HotelDetailListAdapter(mActivity);
		}

		if (hotelDetail != null)
		{
			initHotelDetailLayout(hotelDetail);

			mImageAdapter.setData(hotelDetail.getImageUrl());
			mViewPager.setAdapter(mImageAdapter);
			mListView.setAdapter(mListAdapter);
			
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
	
	private class HotelDetailListAdapter extends BaseAdapter
	{
		private Context mContext;
		
		public HotelDetailListAdapter(Context context)
		{
			mContext = context;
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
			return 20;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view = null;
			
			LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			switch(position)
			{
				// 빈화면
				case 0:
					view = layoutInflater.inflate(R.layout.list_row_detail01, parent, false);
					getDetail01View(view);
					break;
				
				// 호텔 등급과 이름.
				case 1:
					view = layoutInflater.inflate(R.layout.list_row_detail02, parent, false);
					getDetail02View(view, mHotelDetail);
					break;
					
				case 2:
				default:
					view = layoutInflater.inflate(R.layout.list_row_detail03, parent, false);
					break;
			}
			
			return view;
		}
		
		private View getDetail01View(View view)
		{
			View emptyView = view.findViewById(R.id.imageEmptyHeight);
			emptyView.getLayoutParams().height = mImageHeight;

			emptyView.setClickable(true);
			emptyView.setOnTouchListener(mEmptyViewOnTouchListener);
			
			return view;
		}
		
		private View getDetail02View(View view, HotelDetail hotelDetail)
		{
			mHotelTitleLaout = view.findViewById(R.id.hotelTitleLaout);
			
			// 등급
			mHotelGradeTextView = (TextView) view.findViewById(R.id.hotelGradeTextView);
			mHotelGradeTextView.setVisibility(View.VISIBLE);
			mHotelGradeTextView.setText(hotelDetail.getHotel().getCategory().getName(mActivity));
			mHotelGradeTextView.setBackgroundResource(hotelDetail.getHotel().getCategory().getColorResId());

			// 호텔명
			TextView hotelNameTextView = (TextView) view.findViewById(R.id.hotelNameTextView);
			hotelNameTextView.setText(hotelDetail.getHotel().getName());
			
			return view;
		}
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
	
	private OnScrollListener mOnScrollListener = new OnScrollListener()
	{
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
		{
			if(view.getAdapter() == null)
			{
				return;
			}
			
			if(firstVisibleItem > 1)
			{
				if (mOnUserActionListener != null)
				{
					mOnUserActionListener.showActionBar();
				}
				return;
			}
			
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
			
			if(isOverAPI11() == true)
			{
				mHotelGradeTextView.setAlpha(alphaFactor);
			} else
			{
				if(Float.compare(alphaFactor, 0.0f) <= 0)
				{
					mHotelGradeTextView.setVisibility(View.INVISIBLE);
				} else
				{
					mHotelGradeTextView.setVisibility(View.VISIBLE);
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
					mListView.setScrollEnabled(false);
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
}