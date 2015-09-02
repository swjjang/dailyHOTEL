package com.twoheart.dailyhotel.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.adapter.TicketViewPagerAdapter;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.TicketDto;
import com.twoheart.dailyhotel.model.TicketRenderer;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.LoadingDialog;
import com.twoheart.dailyhotel.view.LocationFactory;
import com.twoheart.dailyhotel.view.LoopViewPager;
import com.twoheart.dailyhotel.view.MyLocationMarker;
import com.twoheart.dailyhotel.view.TicketClusterItem;
import com.twoheart.dailyhotel.view.TicketClusterRenderer;
import com.twoheart.dailyhotel.view.TicketViewItem;
import com.twoheart.dailyhotel.view.TicketClusterRenderer.OnSelectedClusterItemListener;
import com.twoheart.dailyhotel.view.TicketClusterRenderer.Renderer;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;

public abstract class TicketListMapFragment extends
		com.google.android.gms.maps.SupportMapFragment implements ClusterManager.OnClusterClickListener<TicketClusterItem>, ClusterManager.OnClusterItemClickListener<TicketClusterItem>
{
	private GoogleMap mGoogleMap;
	private ArrayList<TicketViewItem> mTicketViewItemArrayList; // 선택된 호텔을 위한 리스트
	private ArrayList<TicketViewItem> mTicketViewItemArrangeArrayList; // ViewPager을 위한 리스트
	private LoadingDialog mLoadingDialog;
	private MarkerOptions mMyLocationMarkerOptions;
	private Marker mMyLocationMarker;

	protected TicketMainFragment.OnUserActionListener mUserActionListener;
	private SaleTime mSaleTime;
	private boolean mIsCreateView = false;
	private boolean mCallMakeMarker = false;

	private TicketViewItem mSelectedTicketViewItem;
	private boolean mIsOpenMakrer; // 마커를 선택한 경우.
	private HashMap<String, ArrayList<TicketDto>> mDuplicateTicketDto;

	private ClusterManager<TicketClusterItem> mClusterManager;
	private TicketClusterRenderer mTicketClusterRenderer;
	private Marker mSelectedMarker;
	private View mMyLocationView;
	private ViewPager mViewPager;
	private TicketViewPagerAdapter mTicketViewPagerAdapter;

	public interface OnUserActionListener
	{
		public void onInfoWindowClickListener(TicketDto selectedTicketDto);

		public void onCloseInfoWindowClickListener();
	}

	protected abstract TicketViewPagerAdapter getViewPagerAdapter(BaseActivity baseActivity);

	public TicketListMapFragment()
	{
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);

		if (mLoadingDialog == null)
		{
			mLoadingDialog = new LoadingDialog((BaseActivity) getActivity());
		}

		if (mDuplicateTicketDto == null)
		{
			mDuplicateTicketDto = new HashMap<String, ArrayList<TicketDto>>();
		}

		getMapAsync(new OnMapReadyCallback()
		{
			@Override
			public void onMapReady(GoogleMap googleMap)
			{
				BaseActivity baseActivity = (BaseActivity) getActivity();

				if (baseActivity == null)
				{
					return;
				}

				mGoogleMap = googleMap;

				//				mGoogleMap.setMyLocationEnabled(true);
				mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
				mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
				mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);
				mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
				mGoogleMap.setOnMapClickListener(mOnMapClickListener);

				relocationMyLocation();
				relocationZoomControl();

				mClusterManager = new ClusterManager<TicketClusterItem>(baseActivity, mGoogleMap);
				mTicketClusterRenderer = new TicketClusterRenderer(baseActivity, mGoogleMap, mClusterManager);
				mTicketClusterRenderer.setOnClusterRenderedListener(mOnClusterRenderedListener);

				mClusterManager.setRenderer(mTicketClusterRenderer);
				mClusterManager.setAlgorithm(new NonHierarchicalDistanceBasedAlgorithm<TicketClusterItem>());

				mGoogleMap.setInfoWindowAdapter(new MapWindowAdapter(baseActivity));

				// 서울이 아니고 상세 지역 정보가 아닌 경우..지역별 중심값으로 이동.
				LatLng latlng = new LatLng(35.856899430657805, 127.73446206003428);

				if (latlng != null)
				{
					CameraPosition cp = new CameraPosition.Builder().target(latlng).zoom(6.791876f).build();
					mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
				}

				mIsCreateView = true;

				makeMarker(true);
			}
		});

		// Add Hotel Info ViewPager 
		mViewPager = new LoopViewPager(view.getContext());
		mViewPager.setOffscreenPageLimit(1);
		mViewPager.setOnPageChangeListener(mOnPageChangeListener);

		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, Util.dpToPx(view.getContext(), 133));
		layoutParams.gravity = Gravity.BOTTOM;

		container.addView(mViewPager, layoutParams);

		mViewPager.setVisibility(View.INVISIBLE);

		return view;
	}

	@Override
	public void onDestroyView()
	{
		if (mLoadingDialog != null)
		{
			mLoadingDialog.close();
		}

		if (mGoogleMap != null)
		{
			mGoogleMap.stopAnimation();
			mGoogleMap.clear();
		}

		super.onDestroyView();
	}

	@Override
	public boolean onClusterItemClick(TicketClusterItem item, Marker marker)
	{
		if (getActivity() == null)
		{
			return false;
		}

		boolean result = mOnMarkerClickListener.onMarkerClick(marker);

		if (mLoadingDialog != null)
		{
			mLoadingDialog.hide();
		}

		return result;
	}

	@Override
	public boolean onClusterClick(Cluster<TicketClusterItem> cluster)
	{
		LatLngBounds.Builder builder = new LatLngBounds.Builder();

		for (TicketClusterItem ticketClusterItem : cluster.getItems())
		{
			LatLng latlng = ticketClusterItem.getPosition();
			builder.include(latlng);
		}

		directCameraSetting(builder.build(), cluster.getSize());

		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
			case Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION:
				mOnMyLocationClickListener.onClick(null);
				break;
		}
	}

	public void setUserActionListener(TicketMainFragment.OnUserActionListener userActionLister)
	{
		mUserActionListener = userActionLister;
	}

	public void setTicketList(ArrayList<TicketViewItem> arrayList, SaleTime saleTime, boolean isChangedRegion)
	{
		mTicketViewItemArrayList = arrayList;
		mSaleTime = saleTime;

		// Marker 만들기.
		if (mIsCreateView == true)
		{
			makeMarker(isChangedRegion);
		}
	}

	/**
	 * 추후 UI추가 필요 구글맵 버전이 바뀌면 문제가 될수도 있음.
	 */
	private void relocationZoomControl()
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		// Find myLocationButton view
		View zoomControl = getView().findViewById(0x1);

		if (zoomControl != null && zoomControl.getLayoutParams() instanceof RelativeLayout.LayoutParams)
		{
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) zoomControl.getLayoutParams();
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);

			zoomControl.setPadding(zoomControl.getPaddingLeft(), Util.dpToPx(baseActivity, 50), zoomControl.getPaddingRight(), zoomControl.getPaddingBottom());
			zoomControl.setLayoutParams(params);
		}
	}

	private void relocationMyLocation()
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		mMyLocationView = getView().findViewById(0x2);

		if (mMyLocationView != null)
		{
			mMyLocationView.setVisibility(View.VISIBLE);
			mMyLocationView.setOnClickListener(mOnMyLocationClickListener);
		}
	}

	private void makeMarker(boolean isChangedRegion)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null || baseActivity.isFinishing() == true)
		{
			return;
		}

		if (mGoogleMap == null)
		{
			return;
		}

		mGoogleMap.clear();
		mSelectedMarker = null;

		if (mSelectedMarker == null)
		{
			mSelectedMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).visible(false).anchor(0.0f, 1.0f));
		}

		if (mTicketViewItemArrayList == null || mTicketViewItemArrayList.size() == 0)
		{
			return;
		}

		if (mLoadingDialog != null)
		{
			mLoadingDialog.show();
		}

		if (mCallMakeMarker == false && isChangedRegion == false)
		{
			isChangedRegion = true;
		}

		mCallMakeMarker = true;

		if (isChangedRegion == true)
		{
			mIsOpenMakrer = false;

			if (mOnInfoWindowUserActionListener != null)
			{
				mOnInfoWindowUserActionListener.onCloseInfoWindowClickListener();
			}
		}

		double latitude = 0.0;
		double longitude = 0.0;
		int count = 0;
		boolean isOpenMarker = false;

		if (mIsOpenMakrer == true && mSelectedTicketViewItem != null)
		{
			latitude = mSelectedTicketViewItem.getTicketDto().latitude;
			longitude = mSelectedTicketViewItem.getTicketDto().longitude;
		}

		LatLngBounds.Builder builder = new LatLngBounds.Builder();

		if (mDuplicateTicketDto == null)
		{
			mDuplicateTicketDto = new HashMap<String, ArrayList<TicketDto>>();
		}

		mDuplicateTicketDto.clear();

		// 중복 지역을 찾아내기 위한 로직.
		if (mTicketViewItemArrangeArrayList != null)
		{
			mTicketViewItemArrangeArrayList.clear();
		}

		mTicketViewItemArrangeArrayList = null;

		mTicketViewItemArrangeArrayList = searchDuplicateLocateion(mTicketViewItemArrayList, mDuplicateTicketDto);

		mClusterManager.clearItems();
		mGoogleMap.setOnMarkerClickListener(mClusterManager);
		mClusterManager.setOnClusterClickListener(TicketListMapFragment.this);
		mClusterManager.setOnClusterItemClickListener(TicketListMapFragment.this);

		for (TicketViewItem ticketViewItem : mTicketViewItemArrangeArrayList)
		{
			TicketDto baseTicketDto = ticketViewItem.getTicketDto();

			count++;

			TicketClusterItem ticketClusterItem = new TicketClusterItem(baseTicketDto);
			mClusterManager.addItem(ticketClusterItem);

			LatLng latlng = new LatLng(baseTicketDto.latitude, baseTicketDto.longitude);
			builder.include(latlng);

			// 기존의 마커 정보 창을 보여준다.
			if (mIsOpenMakrer == true)
			{
				if (latitude == baseTicketDto.latitude && longitude == baseTicketDto.longitude)
				{
					isOpenMarker = true;

					mTicketClusterRenderer.setSelectedClusterItem(ticketClusterItem);
					mTicketClusterRenderer.setSelectedClusterItemListener(new OnSelectedClusterItemListener()
					{
						@Override
						public void onSelectedClusterItemListener(Marker marker)
						{
							if (marker != null)
							{
								mClusterManager.onMarkerClick(marker);
							} else
							{
								if (mLoadingDialog != null)
								{
									mLoadingDialog.hide();
								}
							}
						}
					});
				}
			}
		}

		mIsOpenMakrer = false;

		if (isChangedRegion == true)
		{
			try
			{
				cameraSetting(builder.build(), count);
			} catch (Exception e)
			{
				ExLog.d(e.toString());
			}
		} else
		{
			mGoogleMap.setOnCameraChangeListener(mClusterManager);
			mClusterManager.cluster();
		}

		if (isOpenMarker == false)
		{
			if (mLoadingDialog != null)
			{
				mLoadingDialog.hide();
			}

			mViewPager.setVisibility(View.INVISIBLE);
		}

		if (mMyLocationMarker != null)
		{
			mMyLocationMarker = mGoogleMap.addMarker(mMyLocationMarkerOptions);
		}
	}

	private void cameraSetting(final LatLngBounds bounds, int hotelCount)
	{
		if (hotelCount <= 0)
		{
			return;
		} else if (hotelCount == 1)
		{
			mGoogleMap.setOnCameraChangeListener(new OnCameraChangeListener()
			{
				@Override
				public void onCameraChange(CameraPosition cameraPosition)
				{
					mGoogleMap.setOnCameraChangeListener(mClusterManager);

					if (getActivity() == null)
					{
						return;
					}

					CameraPosition cp = new CameraPosition.Builder().target(bounds.getCenter()).zoom(14.0f).build();
					mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));

					mClusterManager.cluster();
				}
			});
		} else
		{
			mGoogleMap.setOnCameraChangeListener(new OnCameraChangeListener()
			{
				@Override
				public void onCameraChange(CameraPosition cameraPosition)
				{
					mGoogleMap.setOnCameraChangeListener(mClusterManager);

					if (getActivity() == null)
					{
						return;
					}

					CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, Util.dpToPx(getActivity(), 50));
					mGoogleMap.moveCamera(cameraUpdate);

					mClusterManager.cluster();
				}
			});
		}

		CameraPosition cp = new CameraPosition.Builder().target(bounds.getCenter()).zoom(mGoogleMap.getCameraPosition().zoom).build();
		mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
	}

	private void directCameraSetting(final LatLngBounds bounds, int hotelCount)
	{
		if (getActivity() == null)
		{
			return;
		}

		mGoogleMap.setOnCameraChangeListener(mClusterManager);

		if (hotelCount == 1)
		{
			CameraPosition cp = new CameraPosition.Builder().target(bounds.getCenter()).zoom(14.0f).build();
			mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
		} else
		{
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, Util.dpToPx(getActivity(), 50));
			mGoogleMap.moveCamera(cameraUpdate);
		}

		mClusterManager.cluster();
	}

	/**
	 * 같은 영역에 있는 호텔들을 찾아낸다.
	 * 
	 * @param hotelArrayList
	 * @param hashMap
	 * @return
	 */
	private ArrayList<TicketViewItem> searchDuplicateLocateion(ArrayList<TicketViewItem> hotelArrayList, HashMap<String, ArrayList<TicketDto>> hashMap)
	{
		ArrayList<TicketViewItem> arrangeList = new ArrayList<TicketViewItem>(hotelArrayList);

		int size = arrangeList.size();
		TicketViewItem ticketViewItem = null;

		// 섹션 정보와 솔드 아웃인 경우 목록에서 제거 시킨다.
		for (int i = size - 1; i >= 0; i--)
		{
			ticketViewItem = arrangeList.get(i);

			if (ticketViewItem.type == TicketViewItem.TYPE_SECTION)
			{
				arrangeList.remove(i);
			} else
			{
				if (ticketViewItem.getTicketDto().isSoldOut)
				{
					arrangeList.remove(i);
				}
			}
		}

		// 중복된 위치에 있는 호텔들은 위해서 소팅한다.
		Comparator<TicketViewItem> comparator = new Comparator<TicketViewItem>()
		{
			final LatLng latlng = new LatLng(37.23945, 131.8689);

			public int compare(TicketViewItem o1, TicketViewItem o2)
			{
				TicketDto item01 = o1.getTicketDto();
				TicketDto item02 = o2.getTicketDto();

				float[] results1 = new float[3];
				Location.distanceBetween(latlng.latitude, latlng.longitude, item01.latitude, item01.longitude, results1);

				float[] results2 = new float[3];
				Location.distanceBetween(latlng.latitude, latlng.longitude, item02.latitude, item02.longitude, results2);

				return Float.compare(results1[0], results2[0]);
			}
		};

		Collections.sort(arrangeList, comparator);

		size = arrangeList.size();

		// 중복된 호텔들은 낮은 가격을 노출하도록 한다.
		if (size > 1)
		{
			TicketDto item01 = null;
			TicketDto item02 = null;

			for (int i = size - 1; i > 0; i--)
			{
				item01 = arrangeList.get(i).getTicketDto();
				item02 = arrangeList.get(i - 1).getTicketDto();

				if (item01.latitude == item02.latitude && item01.longitude == item02.longitude)
				{
					int item01DisCount = item01.discountPrice;
					int item02DisCount = item02.discountPrice;

					if (item01DisCount >= item02DisCount)
					{
						arrangeList.remove(i);
					} else
					{
						arrangeList.remove(i - 1);
					}

					String key = String.valueOf(item01.latitude) + String.valueOf(item01.longitude);

					if (hashMap.containsKey(key) == true)
					{
						ArrayList<TicketDto> dulicateArrayList = hashMap.get(key);

						if (dulicateArrayList.contains(item01) == false)
						{
							dulicateArrayList.add(item01);
						}

						if (dulicateArrayList.contains(item02) == false)
						{
							dulicateArrayList.add(item02);
						}
					} else
					{
						ArrayList<TicketDto> dulicateArrayList = new ArrayList<TicketDto>();

						dulicateArrayList.add(item01);
						dulicateArrayList.add(item02);

						hashMap.put(key, dulicateArrayList);
					}
				}
			}
		}

		return arrangeList;
	}

	private void onMarkerTempClick(final LatLng latlng)
	{

		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		mIsOpenMakrer = true;

		int position = -1;
		int size = mTicketViewItemArrangeArrayList.size();

		for (int i = 0; i < size; i++)
		{
			TicketViewItem ticketViewItem = mTicketViewItemArrangeArrayList.get(i);
			TicketDto ticketDto = ticketViewItem.getTicketDto();

			if (latlng.latitude == ticketDto.latitude && latlng.longitude == ticketDto.longitude)
			{
				position = i;

				TicketRenderer ticketRenderer = new TicketRenderer(baseActivity, ticketDto.discountPrice, ticketDto.grade.getMarkerResId());
				BitmapDescriptor icon = ticketRenderer.getBitmap(true);

				if (mSelectedMarker != null)
				{
					mSelectedMarker.setVisible(false);
				}

				if (icon != null)
				{
					mSelectedMarker.setVisible(true);
					mSelectedMarker.setPosition(latlng);
					mSelectedMarker.setIcon(icon);
				}
				break;
			}
		}

		if (position >= 0)
		{
			mViewPager.setCurrentItem(position);
			mTicketViewPagerAdapter.notifyDataSetChanged();

			mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(mSelectedMarker.getPosition()));

			mSelectedMarker.showInfoWindow();
		}
	}

	private void onMarkerClick(final LatLng latlng)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		mViewPager.setVisibility(View.VISIBLE);
		mViewPager.bringToFront();

		Comparator<TicketViewItem> comparator = new Comparator<TicketViewItem>()
		{
			public int compare(TicketViewItem o1, TicketViewItem o2)
			{
				TicketDto item01 = o1.getTicketDto();
				TicketDto item02 = o2.getTicketDto();

				float[] results1 = new float[3];
				Location.distanceBetween(latlng.latitude, latlng.longitude, item01.latitude, item01.longitude, results1);

				float[] results2 = new float[3];
				Location.distanceBetween(latlng.latitude, latlng.longitude, item02.latitude, item02.longitude, results2);

				return Float.compare(results1[0], results2[0]);
			}
		};

		Collections.sort(mTicketViewItemArrangeArrayList, comparator);

		if (mTicketViewPagerAdapter == null)
		{
			mTicketViewPagerAdapter = getViewPagerAdapter(baseActivity);
			mTicketViewPagerAdapter.setOnUserActionListener(mOnInfoWindowUserActionListener);
		}

		mTicketViewPagerAdapter.setData(mTicketViewItemArrangeArrayList);
		mViewPager.setAdapter(mTicketViewPagerAdapter);
		mTicketViewPagerAdapter.notifyDataSetChanged();

		mIsOpenMakrer = true;

		int position = -1;
		int size = mTicketViewItemArrangeArrayList.size();

		for (int i = 0; i < size; i++)
		{
			TicketViewItem ticketViewItem = mTicketViewItemArrangeArrayList.get(i);
			TicketDto ticketDto = ticketViewItem.getTicketDto();

			if (latlng.latitude == ticketDto.latitude && latlng.longitude == ticketDto.longitude)
			{
				position = i;

				TicketRenderer ticketRenderer = new TicketRenderer(baseActivity, ticketDto.discountPrice, ticketDto.grade.getMarkerResId());
				BitmapDescriptor icon = ticketRenderer.getBitmap(true);

				if (mSelectedMarker != null)
				{
					mSelectedMarker.setVisible(false);
				}

				if (icon != null)
				{
					mSelectedMarker.setVisible(true);
					mSelectedMarker.setPosition(latlng);
					mSelectedMarker.setIcon(icon);
				}
				break;
			}
		}

		// 항상 마커를 선택하면 position == 0이다.
		if (position >= 0)
		{
			mViewPager.setCurrentItem(position);

			mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(mSelectedMarker.getPosition()));
			mSelectedMarker.showInfoWindow();
		}
	}

	/////////////////////////////////////////////////////////////////////////////////
	// 
	////////////////////////////////////////////////////////////////////////////////

	private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener()
	{
		@Override
		public void onPageSelected(int page)
		{
			if (mTicketViewItemArrangeArrayList == null || mTicketViewItemArrangeArrayList.size() <= page)
			{
				return;
			}

			TicketViewItem ticketViewItem = mTicketViewItemArrangeArrayList.get(page);

			TicketDto ticketDto = ticketViewItem.getTicketDto();

			if (ticketDto != null)
			{
				TicketClusterItem hotelClusterItem = new TicketClusterItem(ticketDto);
				mTicketClusterRenderer.setSelectedClusterItem(hotelClusterItem);

				onMarkerTempClick(hotelClusterItem.getPosition());
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2)
		{
		}

		@Override
		public void onPageScrollStateChanged(int arg0)
		{
		}
	};

	private TicketClusterRenderer.OnClusterRenderedListener mOnClusterRenderedListener = new TicketClusterRenderer.OnClusterRenderedListener()
	{
		@Override
		public void onClusterRenderedListener(com.twoheart.dailyhotel.view.TicketClusterRenderer.Renderer renderer)
		{
			if (renderer == Renderer.CLUSTER)
			{
				mOnMapClickListener.onMapClick(null);
			}
		}
	};
	/////////////////////////////////////////////////////////////////////////////////
	// Listener
	////////////////////////////////////////////////////////////////////////////////

	private View.OnClickListener mOnMyLocationClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			LocationFactory.getInstance(baseActivity).startLocationMeasure(TicketListMapFragment.this, mMyLocationView, new LocationListener()
			{
				@Override
				public void onStatusChanged(String provider, int status, Bundle extras)
				{
					// TODO Auto-generated method stub

				}

				@Override
				public void onProviderEnabled(String provider)
				{
					// TODO Auto-generated method stub

				}

				@Override
				public void onProviderDisabled(String provider)
				{
					BaseActivity baseActivity = (BaseActivity) getActivity();

					if (baseActivity == null || baseActivity.isFinishing() == true)
					{
						return;
					}

					// Fragment가 added가 되지 않은 상태에서 터치가 될경우.
					if (isAdded() == false)
					{
						return;
					}

					// 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
					LocationFactory.getInstance(baseActivity).stopLocationMeasure();

					baseActivity.showSimpleDialog(getString(R.string.dialog_title_used_gps), getString(R.string.dialog_msg_used_gps), getString(R.string.dialog_btn_text_dosetting), getString(R.string.dialog_btn_text_cancel), new View.OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivityForResult(intent, Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION);
						}
					}, null, true);
				}

				@Override
				public void onLocationChanged(Location location)
				{
					BaseActivity baseActivity = (BaseActivity) getActivity();

					if (baseActivity == null)
					{
						return;
					}

					LocationFactory.getInstance(baseActivity).stopLocationMeasure();

					if (mMyLocationMarkerOptions == null)
					{
						mMyLocationMarkerOptions = new MarkerOptions();
						mMyLocationMarkerOptions.icon(new MyLocationMarker(baseActivity).makeIcon());
						mMyLocationMarkerOptions.anchor(0.5f, 0.5f);
					}

					if (mMyLocationMarker != null)
					{
						mMyLocationMarker.remove();
					}

					mMyLocationMarkerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
					mMyLocationMarker = mGoogleMap.addMarker(mMyLocationMarkerOptions);

					CameraPosition cameraPosition = new CameraPosition.Builder().target(mMyLocationMarkerOptions.getPosition()).zoom(13f).build();
					mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				}
			});
		}
	};

	private OnMapClickListener mOnMapClickListener = new OnMapClickListener()
	{
		@Override
		public void onMapClick(LatLng arg0)
		{
			if (mSelectedMarker != null)
			{
				mSelectedMarker.setVisible(false);
			}

			mIsOpenMakrer = false;
			mSelectedTicketViewItem = null;

			if (mViewPager != null)
			{
				mViewPager.setVisibility(View.INVISIBLE);
			}
		}
	};

	private OnMarkerClickListener mOnMarkerClickListener = new OnMarkerClickListener()
	{
		@Override
		public boolean onMarkerClick(Marker marker)
		{
			TicketListMapFragment.this.onMarkerClick(marker.getPosition());

			return true;
		}
	};

	private OnUserActionListener mOnInfoWindowUserActionListener = new OnUserActionListener()
	{
		@Override
		public void onInfoWindowClickListener(TicketDto selectedTicketDto)
		{
			if (getActivity() == null)
			{
				return;
			}

			if (mUserActionListener != null)
			{
				for (TicketViewItem ticketViewItem : mTicketViewItemArrayList)
				{
					if (ticketViewItem.type == TicketViewItem.TYPE_SECTION)
					{
						continue;
					}

					TicketDto ticketDto = ticketViewItem.getTicketDto();

					if (ticketDto.equals(selectedTicketDto) == true)
					{
						mSelectedTicketViewItem = ticketViewItem;
						mUserActionListener.selectedTicket(ticketViewItem, mSaleTime);
						break;
					}
				}
			}
		}

		@Override
		public void onCloseInfoWindowClickListener()
		{
			if (getActivity() == null)
			{
				return;
			}

			if (mOnMapClickListener != null)
			{
				mOnMapClickListener.onMapClick(null);
			}
		}
	};

	private class MapWindowAdapter implements GoogleMap.InfoWindowAdapter
	{
		private Context mContext;

		public MapWindowAdapter(Context context)
		{
			mContext = context;
		}

		@Override
		public View getInfoWindow(Marker marker)
		{
			LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = layoutInflater.inflate(R.layout.no_info_window, null);
			return view;
		}

		@Override
		public View getInfoContents(Marker marker)
		{
			return null;
		}
	}
}
