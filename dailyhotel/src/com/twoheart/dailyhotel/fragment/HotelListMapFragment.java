package com.twoheart.dailyhotel.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm;
import com.twoheart.dailyhotel.adapter.HotelInfoWindowAdapter;
import com.twoheart.dailyhotel.adapter.HotelInfoWindowAdapter.OnInfoWindowClickListener;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.HotelClusterItem;
import com.twoheart.dailyhotel.util.ui.HotelClusterRenderer;
import com.twoheart.dailyhotel.util.ui.HotelClusterRenderer.OnSelectedClusterItemListener;
import com.twoheart.dailyhotel.util.ui.HotelListViewItem;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;

public class HotelListMapFragment extends
		com.google.android.gms.maps.SupportMapFragment implements ClusterManager.OnClusterClickListener<HotelClusterItem>, ClusterManager.OnClusterItemClickListener<HotelClusterItem>
{
	private GoogleMap mGoogleMap;
	private ArrayList<HotelListViewItem> mHotelArrayList;
	private HotelInfoWindowAdapter mHotelInfoWindowAdapter;
	private LoadingDialog mLoadingDialog;

	protected HotelMainFragment.UserActionListener mUserActionListener;
	private SaleTime mSaleTime;
	private boolean mIsCreateView = false;
	private boolean mCallMakeMarker = false;

	private HotelListViewItem mSelectedHotelListViewItem;
	private boolean mIsOpenMakrer; // 마커를 선택한 경우.
	private HashMap<String, ArrayList<Hotel>> mDuplicateHotel;

	private ClusterManager<HotelClusterItem> mClusterManager;
	private HotelClusterRenderer mHotelClusterRenderer;

	private OnMakerInfoWindowListener mOnMakerInfoWindowListener;

	public interface OnMakerInfoWindowListener
	{
		public void setInfoWindow(Marker marker, View infoWindow);
	}

	public HotelListMapFragment()
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

		if (mDuplicateHotel == null)
		{
			mDuplicateHotel = new HashMap<String, ArrayList<Hotel>>();
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
				mGoogleMap.setMyLocationEnabled(false);
				mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
				mGoogleMap.setOnMapClickListener(mOnMapClickListener);

				mClusterManager = new ClusterManager<HotelClusterItem>(baseActivity, mGoogleMap);
				mHotelClusterRenderer = new HotelClusterRenderer(baseActivity, mGoogleMap, mClusterManager);

				mClusterManager.setRenderer(mHotelClusterRenderer);
				mClusterManager.setOnClusterClickListener(HotelListMapFragment.this);
				mClusterManager.setOnClusterItemClickListener(HotelListMapFragment.this);
				mClusterManager.setAlgorithm(new NonHierarchicalDistanceBasedAlgorithm<HotelClusterItem>());

				mHotelInfoWindowAdapter = new HotelInfoWindowAdapter(baseActivity);
				mGoogleMap.setInfoWindowAdapter(mHotelInfoWindowAdapter);

				mHotelInfoWindowAdapter.setOnInfoWindowClickListener(mOnInfoWindowClickListener);

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

		return view;
	}

	@Override
	public boolean onClusterItemClick(HotelClusterItem item)
	{
		if (getActivity() == null)
		{
			return false;
		}

		Marker marker = mHotelClusterRenderer.getMarker(item);
		boolean result = mOnMarkerClickListener.onMarkerClick(marker);

		if (mLoadingDialog != null)
		{
			mLoadingDialog.hide();
		}

		return result;
	}

	@Override
	public boolean onClusterClick(Cluster<HotelClusterItem> cluster)
	{
		LatLngBounds.Builder builder = new LatLngBounds.Builder();

		for (HotelClusterItem hotelClusterItem : cluster.getItems())
		{
			LatLng latlng = hotelClusterItem.getPosition();
			builder.include(latlng);
		}

		//		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), Util.dpToPx(getActivity(), 50));
		//		mGoogleMap.moveCamera(cameraUpdate);

		directCameraSetting(builder.build(), cluster.getSize());

		return true;
	}

	public void setUserActionListener(HotelMainFragment.UserActionListener userActionLister)
	{
		mUserActionListener = userActionLister;
	}

	public void setHotelList(ArrayList<HotelListViewItem> hotelArrayList, SaleTime saleTime, boolean isChangedRegion)
	{
		mHotelArrayList = hotelArrayList;
		mSaleTime = saleTime;

		// Marker 만들기.
		if (mIsCreateView == true)
		{
			makeMarker(isChangedRegion);
		}
	}

	private void makeMarker(boolean isChangedRegion)
	{
		if (mGoogleMap == null)
		{
			return;
		}

		mGoogleMap.clear();

		if (mHotelArrayList == null || mHotelArrayList.size() == 0)
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
		}

		double latitude = 0.0;
		double longitude = 0.0;
		int count = 0;
		boolean isOpenMarker = mIsOpenMakrer;

		if (mIsOpenMakrer == true && mSelectedHotelListViewItem != null)
		{
			latitude = mSelectedHotelListViewItem.getItem().mLatitude;
			longitude = mSelectedHotelListViewItem.getItem().mLongitude;
		}

		LatLngBounds.Builder builder = new LatLngBounds.Builder();

		if (mDuplicateHotel == null)
		{
			mDuplicateHotel = new HashMap<String, ArrayList<Hotel>>();
		}

		mDuplicateHotel.clear();

		// 중복 지역을 찾아내기 위한 로직.
		ArrayList<HotelListViewItem> arrangeList = searchDuplicateLocateion(mHotelArrayList, mDuplicateHotel);

		mClusterManager.clearItems();
		mGoogleMap.setOnMarkerClickListener(mClusterManager);

		for (HotelListViewItem hotelListViewItem : arrangeList)
		{
			Hotel hotel = hotelListViewItem.getItem();

			count++;

			HotelClusterItem hotelClusterItem = new HotelClusterItem(hotel);
			mClusterManager.addItem(hotelClusterItem);

			LatLng latlng = new LatLng(hotel.mLatitude, hotel.mLongitude);
			builder.include(latlng);

			// 기존의 마커 정보 창을 보여준다.
			if (mIsOpenMakrer == true)
			{
				if (latitude == hotel.mLatitude && longitude == hotel.mLongitude)
				{
					mIsOpenMakrer = false;
					mHotelClusterRenderer.setSelectedClusterItem(hotelClusterItem);
					mHotelClusterRenderer.setSelectedClusterItemListener(new OnSelectedClusterItemListener()
					{
						@Override
						public void onSelectedClusterItemListener(Marker marker)
						{
							mClusterManager.onMarkerClick(marker);
						}
					});
				}
			}
		}

		if (isChangedRegion == true)
		{
			cameraSetting(builder.build(), count);
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
		}
	}

	private void cameraSetting(final LatLngBounds bounds, int hotelCount)
	{
		if (hotelCount == 1)
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

					CameraPosition cp = new CameraPosition.Builder().target(bounds.getCenter()).zoom(14).build();
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
			CameraPosition cp = new CameraPosition.Builder().target(bounds.getCenter()).zoom(14).build();
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
	private ArrayList<HotelListViewItem> searchDuplicateLocateion(ArrayList<HotelListViewItem> hotelArrayList, HashMap<String, ArrayList<Hotel>> hashMap)
	{
		ArrayList<HotelListViewItem> arrangeList = new ArrayList<HotelListViewItem>(hotelArrayList);

		int size = arrangeList.size();
		HotelListViewItem hotelListViewItem = null;

		for (int i = size - 1; i >= 0; i--)
		{
			hotelListViewItem = arrangeList.get(i);

			if (hotelListViewItem.getType() == HotelListViewItem.TYPE_SECTION)
			{
				arrangeList.remove(i);
			}
		}

		// 거리에 따른 역순으로 정렬
		Comparator<HotelListViewItem> comparator = new Comparator<HotelListViewItem>()
		{
			public int compare(HotelListViewItem o1, HotelListViewItem o2)
			{
				Hotel item01 = o1.getItem();
				Hotel item02 = o2.getItem();

				double distanceLat = item01.mLatitude - item02.mLatitude;
				double distanceLng = item01.mLongitude - item02.mLongitude;

				double distance = distanceLat + distanceLng;

				if (distance > 0)
				{
					return -1;
				} else if (distance < 0)
				{
					return 1;
				} else
				{
					return 0;
				}
			}
		};

		Collections.sort(arrangeList, comparator);

		size = arrangeList.size();

		if (size > 1)
		{
			Hotel item01 = null;
			Hotel item02 = null;

			for (int i = size - 1; i > 0; i--)
			{
				item01 = arrangeList.get(i).getItem();
				item02 = arrangeList.get(i - 1).getItem();

				if (item01.mLatitude == item02.mLatitude && item01.mLongitude == item02.mLongitude)
				{
					int item01DisCount = Integer.parseInt(item01.getDiscount().replaceAll(",", ""));
					int item02DisCount = Integer.parseInt(item02.getDiscount().replaceAll(",", ""));

					if (item01DisCount >= item02DisCount)
					{
						arrangeList.remove(i);
					} else
					{
						arrangeList.remove(i - 1);
					}

					String key = String.valueOf(item01.mLatitude) + String.valueOf(item01.mLongitude);

					if (hashMap.containsKey(key) == true)
					{
						ArrayList<Hotel> dulicateHotelArrayList = hashMap.get(key);

						if (dulicateHotelArrayList.contains(item01) == false)
						{
							dulicateHotelArrayList.add(item01);
						}

						if (dulicateHotelArrayList.contains(item02) == false)
						{
							dulicateHotelArrayList.add(item02);
						}
					} else
					{
						ArrayList<Hotel> dulicateHotelArrayList = new ArrayList<Hotel>();

						dulicateHotelArrayList.add(item01);
						dulicateHotelArrayList.add(item02);

						hashMap.put(key, dulicateHotelArrayList);
					}
				}
			}
		}

		return arrangeList;
	}

	public void setOnMakerInfoWindowListener(OnMakerInfoWindowListener listener)
	{
		mOnMakerInfoWindowListener = listener;
	}

	/////////////////////////////////////////////////////////////////////////////////
	// 
	////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////
	// Listener
	////////////////////////////////////////////////////////////////////////////////

	private OnMapClickListener mOnMapClickListener = new OnMapClickListener()
	{
		@Override
		public void onMapClick(LatLng arg0)
		{
			mIsOpenMakrer = false;
			mSelectedHotelListViewItem = null;

			if (mOnMakerInfoWindowListener != null)
			{
				mOnMakerInfoWindowListener.setInfoWindow(null, null);
			}
		}
	};

	private OnMarkerClickListener mOnMarkerClickListener = new OnMarkerClickListener()
	{
		@Override
		public boolean onMarkerClick(Marker marker)
		{
			if (getActivity() == null)
			{
				return false;
			}

			if (mIsOpenMakrer == true)
			{
				mOnMapClickListener.onMapClick(null);
				marker.hideInfoWindow();
				return true;
			}

			mIsOpenMakrer = true;

			LatLng latlng = marker.getPosition();
			String key = String.valueOf(latlng.latitude) + String.valueOf(latlng.longitude);

			ArrayList<Hotel> arrayList = null;

			if (mDuplicateHotel.containsKey(key) == true)
			{
				arrayList = mDuplicateHotel.get(key);
			} else
			{
				for (HotelListViewItem hotelListViewItem : mHotelArrayList)
				{
					if (hotelListViewItem.getType() == HotelListViewItem.TYPE_SECTION)
					{
						continue;
					}

					Hotel hotel = hotelListViewItem.getItem();

					if (latlng.latitude == hotel.mLatitude && latlng.longitude == hotel.mLongitude)
					{
						mSelectedHotelListViewItem = hotelListViewItem;

						arrayList = new ArrayList<Hotel>(1);
						arrayList.add(hotelListViewItem.getItem());
						break;
					}
				}
			}

			if (arrayList != null)
			{
				mHotelInfoWindowAdapter.setHotelListViewItems(arrayList);

				if (mOnMakerInfoWindowListener != null)
				{
					mOnMakerInfoWindowListener.setInfoWindow(marker, mHotelInfoWindowAdapter.getInfoWindow());
				}

				marker.showInfoWindow();
			}

			return false;
		}
	};

	private OnInfoWindowClickListener mOnInfoWindowClickListener = new OnInfoWindowClickListener()
	{
		@Override
		public void onInfoWindowClickListener(Hotel selectedHotel)
		{
			if (getActivity() == null)
			{
				return;
			}

			if (mUserActionListener != null)
			{
				int index = 0;

				for (HotelListViewItem hotelListViewItem : mHotelArrayList)
				{
					if (hotelListViewItem.getType() == HotelListViewItem.TYPE_SECTION)
					{
						continue;
					}

					Hotel hotel = hotelListViewItem.getItem();

					if (hotel.equals(selectedHotel) == true)
					{
						mSelectedHotelListViewItem = hotelListViewItem;
						mUserActionListener.selectHotel(hotelListViewItem, index, mSaleTime);
						break;
					}

					index++;
				}
			}
		}
	};
}
