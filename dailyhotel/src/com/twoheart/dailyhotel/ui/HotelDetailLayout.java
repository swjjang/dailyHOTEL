package com.twoheart.dailyhotel.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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
import com.twoheart.dailyhotel.util.Util;

/**
 * 호텔 상세 정보 화면
 * 
 * @author sheldon
 *
 */
public class HotelDetailLayout
{
	private static final int NUMBER_OF_ROWSLIST = 7;

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

	private View[] mDeatilView;

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

		// 호텔 상세 정보를 얻어와서 리스트 개수가 몇개 필요한지 검색한다.
		mDeatilView = new View[NUMBER_OF_ROWSLIST];
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

		if (mListAdapter == null)
		{
			mListAdapter = new HotelDetailListAdapter((FragmentActivity) mActivity);
		}

		if (hotelDetail != null)
		{
			mImageAdapter.setData(hotelDetail.getImageUrl());
			mViewPager.setAdapter(mImageAdapter);
			mListView.setAdapter(mListAdapter);

			setCurrentImage(imagePosition);

			if (mOnUserActionListener != null)
			{
				mOnUserActionListener.startAutoSlide();
			}
		}
	}

	public void setCurrentImage(int position)
	{
		if (mViewPager != null)
		{
			mViewPager.setCurrentItem(position, true);
		}
	}

	public int getCurrentImage()
	{
		if (mViewPager != null)
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
		private FragmentActivity mFragmentActivity;

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

		private View getDetail02View(View view, HotelDetail hotelDetail)
		{
			// 주소지
			TextView hotelAddressTextView = (TextView) view.findViewById(R.id.hotelAddressTextView);
			TextView hotelSimpleLocationTextView = (TextView) view.findViewById(R.id.hotelSimpleLocationTextView);

			hotelAddressTextView.setText(hotelDetail.getHotel().getAddress());
			hotelSimpleLocationTextView.setText(hotelDetail.getHotel().getAddress());

			// 맵
			SupportMapFragment mapFragment = (SupportMapFragment) mFragmentActivity.getSupportFragmentManager().findFragmentById(R.id.googleMapFragment);

			mapFragment.getMapAsync(new OnMapReadyCallback()
			{
				@Override
				public void onMapReady(GoogleMap googleMap)
				{
					LatLng latlng = new LatLng(mHotelDetail.getLatitude(), mHotelDetail.getLongitude());

					Marker marker = googleMap.addMarker(new MarkerOptions().position(latlng));
					marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.info_ic_map_large));

					CameraPosition cameraPosition = new CameraPosition.Builder().target(latlng).zoom(15).build();
					googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				}
			});

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
			if (mOnUserActionListener != null)
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
			if (view.getAdapter() == null)
			{
				return;
			}

			if (firstVisibleItem > 1)
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

			if(mHotelTitleLaout == null)
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
			
			if(mHotelGradeTextView == null)
			{
				return;
			}

			float offset = rect.top - mStatusBarHeight - Util.dpToPx(mActivity, 56);
			float max = mImageHeight - Util.dpToPx(mActivity, 56);
			float alphaFactor = offset / max;

			if (isOverAPI11() == true)
			{
				mHotelGradeTextView.setAlpha(alphaFactor);
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
					if (mMoveState == 0 && mPrevX == event.getX() && mPrevY == event.getY())
					{
						if (mOnUserActionListener != null)
						{
							if (mOnUserActionListener != null)
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
}