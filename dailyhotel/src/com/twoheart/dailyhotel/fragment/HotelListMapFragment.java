package com.twoheart.dailyhotel.fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.adapter.HotelInfoWindowAdapter;
import com.twoheart.dailyhotel.adapter.HotelInfoWindowAdapter.OnInfoWindowClickListener;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.ui.HotelListViewItem;

public class HotelListMapFragment extends
		com.google.android.gms.maps.SupportMapFragment
{
	private GoogleMap mGoogleMap;
	private ArrayList<HotelListViewItem> mHotelArrayList;
	private HotelInfoWindowAdapter mHotelInfoWindowAdapter;

	protected HotelMainFragment.UserActionListener mUserActionListener;
	private SaleTime mSaleTime;
	private boolean mIsCreateView = false;

	private HotelListViewItem mSelectedHotelListViewItem;
	private Marker mMarker;
	private boolean mOpenMakrer; // 마커를 선택한 경우.
	private String mRegion;
	private HashMap<String, ArrayList<Hotel>> mDuplicateHotel;

	private OnMakerInfoWindowListener mOnMakerInfoWindowListener;

	//	private String mDetailRegion;

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

		mDuplicateHotel = new HashMap<String, ArrayList<Hotel>>();

		getMapAsync(new OnMapReadyCallback()
		{
			@Override
			public void onMapReady(GoogleMap googleMap)
			{
				if (getActivity() == null)
				{
					return;
				}

				mGoogleMap = googleMap;
				mGoogleMap.setMyLocationEnabled(false);
				mGoogleMap.getUiSettings().setMapToolbarEnabled(false);

				// 기본 위치 서울시청.
				//		서울시       : 37.540705, 126.956764
				//		인천광역시 : 37.469221, 126.573234
				//		광주광역시 : 35.126033, 126.831302
				//		대구광역시 : 35.798838, 128.583052
				//		울산광역시 : 35.519301, 129.239078
				//		대전광역시 : 36.321655, 127.378953
				//		부산광역시 : 35.198362, 129.053922
				//		경기도       : 37.567167, 127.190292
				//		강원도       : 37.555837, 128.209315
				//		충청남도    : 36.557229, 126.779757
				//		충청북도    : 36.628503, 127.929344
				//		경상북도    : 36.248647, 128.664734
				//		경상남도    : 35.259787, 128.664734
				//		전라북도    : 35.716705, 127.144185
				//		전라남도    : 34.819400, 126.893113
				//		제주도       : 33.364805, 126.542671

				// 서울이 아니고 상세 지역 정보가 아닌 경우..지역별 중심값으로 이동.
				CameraPosition cp = new CameraPosition.Builder().target((getRegionCenter(mRegion))).zoom(15).build();
				mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));

				mIsCreateView = true;

				makeMarker(true);
			}
		});

		return view;
	}

	@Override
	public void onPause()
	{
		super.onPause();
	}

	public void setRegion(String region)
	{
		mRegion = region;
	}

	//	public void setDetailRegion(String region)
	//	{
	//		mDetailRegion = region;
	//	}

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

		if (mUserActionListener != null)
		{
			mUserActionListener.showProgress();
		}

		if (isChangedRegion == true)
		{
			mOpenMakrer = false;
		}

		double latitude = 0.0;
		double longitude = 0.0;
		int count = 0;
		int index = 0;

		if (mOpenMakrer == true && mSelectedHotelListViewItem != null)
		{
			latitude = mSelectedHotelListViewItem.getItem().mLatitude;
			longitude = mSelectedHotelListViewItem.getItem().mLongitude;
		}

		LatLngBounds.Builder builder = new LatLngBounds.Builder();

		//		boolean isSeoul = "서울".equalsIgnoreCase(mRegion);

		mMarker = null;

		ArrayList<HotelListViewItem> arrangeList = new ArrayList<HotelListViewItem>(mHotelArrayList);
		mDuplicateHotel.clear();

		{
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

			// seq 값에 따른 역순으로 정렬
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
						return 1;
					} else if (distance < 0)
					{
						return -1;
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

						if (mDuplicateHotel.containsKey(key) == true)
						{
							ArrayList<Hotel> dulicateHotelArrayList = mDuplicateHotel.get(key);

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

							mDuplicateHotel.put(key, dulicateHotelArrayList);
						}
					}
				}
			}
		}

		//		for (HotelListViewItem hotelListViewItem : mHotelArrayList)
		for (HotelListViewItem hotelListViewItem : arrangeList)
		{
			//			if (hotelListViewItem.getType() == HotelListViewItem.TYPE_SECTION)
			//			{
			//				continue;
			//			}

			Hotel hotel = hotelListViewItem.getItem();

			Marker marker = addMarker(hotel);

			// 추후 상세 지역을 위해서 .
			index++;
			count++;

			LatLng latlng = new LatLng(hotel.mLatitude, hotel.mLongitude);
			builder.include(latlng);

			//			if (isSeoul == true)
			//			{
			//				if (TextUtils.isEmpty(mDetailRegion) == false && mDetailRegion.contains(hotel.getDetailRegion()) == true)
			//				{
			//					LatLng latlng = new LatLng(hotel.mLatitude, hotel.mLongitude);
			//					builder.include(latlng);
			//
			//					count++;
			//				}
			//			} else
			//			{
			//				LatLng latlng = new LatLng(hotel.mLatitude, hotel.mLongitude);
			//				builder.include(latlng);
			//
			//				count++;
			//			}

			if (mOpenMakrer == true)
			{
				if (mMarker == null && latitude == hotel.mLatitude && longitude == hotel.mLongitude)
				{
					mSelectedHotelListViewItem = hotelListViewItem;
					mMarker = marker;
				}
			}
		}

		// 특정 지역의 마커들을 모아서 한 화면에 보이도록 한다.
		// 해당 개수가 1개이면 줌 레벨 15이다.
		// 만일 해당 지역에 마커가 없으면 고민해보기
		// onPause()가 호출 되면 애니매이션 하지 않가.

		final LatLngBounds bounds = builder.build();

		if (isChangedRegion == true)
		{
			if (count == 0)
			{
				CameraPosition cp = new CameraPosition.Builder().target(getRegionCenter(mRegion)).zoom(15).build();
				mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
			} else if (count == 1)
			{
				CameraPosition cp = new CameraPosition.Builder().target(bounds.getCenter()).zoom(15).build();
				mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
			} else
			{
				CameraPosition cp = new CameraPosition.Builder().target(bounds.getCenter()).zoom(15).build();
				mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));

				mGoogleMap.setOnCameraChangeListener(new OnCameraChangeListener()
				{
					@Override
					public void onCameraChange(CameraPosition arg0)
					{
						mGoogleMap.setOnCameraChangeListener(null);

						if (getActivity() == null)
						{
							return;
						}

						CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, Util.dpToPx(getActivity(), 50));
						mGoogleMap.moveCamera(cameraUpdate);
					}
				});
			}
		} else
		{
			if (mOpenMakrer == true && mMarker != null)
			{
				mOnMarkerClickListener.onMarkerClick(mMarker);
			}
		}

		mOpenMakrer = false;

		if (mHotelInfoWindowAdapter == null)
		{
			mHotelInfoWindowAdapter = new HotelInfoWindowAdapter(getActivity());
		}

		mGoogleMap.setInfoWindowAdapter(mHotelInfoWindowAdapter);
		mGoogleMap.setOnMarkerClickListener(mOnMarkerClickListener);

		mHotelInfoWindowAdapter.setOnInfoWindowClickListener(mOnInfoWindowClickListener);

		if (mUserActionListener != null)
		{
			mUserActionListener.hideProgress();
		}
	}

	private Marker addMarker(Hotel hotel)
	{
		if (mGoogleMap != null)
		{
			HotelPriceRenderer hotelPriceRenderer = new HotelPriceRenderer(hotel);

			BitmapDescriptor icon = hotelPriceRenderer.getBitmap();

			if (icon != null)
			{
				MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(hotel.mLatitude, hotel.mLongitude)).title(hotel.getDiscount()).icon(icon);

				return mGoogleMap.addMarker(markerOptions);
			}
		}

		return null;
	}

	private LatLng getRegionCenter(String region)
	{
		// Default Seoul
		double latitude = 37.540705;
		double longitude = 126.956764;

		if ("서울".equalsIgnoreCase(region) == true)
		{
		} else if ("경기".equalsIgnoreCase(region) == true)
		{
			// 37.567167, 127.190292
			latitude = 37.567167;
			longitude = 127.190292;
		} else if ("인천".equalsIgnoreCase(region) == true)
		{
			// 37.469221, 126.573234
			latitude = 37.469221;
			longitude = 126.573234;
		} else if ("부산".equalsIgnoreCase(region) == true)
		{
			// 35.198362, 129.053922
			latitude = 35.198362;
			longitude = 129.053922;
		} else if ("경상".equalsIgnoreCase(region) == true)
		{
			//		경상북도    : 36.248647, 128.664734
			//		경상남도    : 35.259787, 128.664734

			latitude = (35.259787 + 36.248647) / 2;
			longitude = 128.664734;

		} else if ("전라".equalsIgnoreCase(region) == true)
		{
			//		전라북도    : 35.716705, 127.144185
			//		전라남도    : 34.819400, 126.893113
			latitude = (35.716705 + 34.819400) / 2;
			longitude = (127.144185 + 126.893113) / 2;
		} else if ("충청".equalsIgnoreCase(region) == true)
		{
			//		충청남도    : 36.557229, 126.779757
			//		충청북도    : 36.628503, 127.929344
			latitude = (36.557229 + 36.628503) / 2;
			longitude = (126.779757 + 127.929344) / 2;
		} else if ("강원".equalsIgnoreCase(region) == true)
		{
			// 37.555837, 128.209315
			latitude = 37.555837;
			longitude = 128.209315;
		} else if ("제주".equalsIgnoreCase(region) == true)
		{
			// 33.364805, 126.542671
			latitude = 33.364805;
			longitude = 126.542671;
		}

		return new LatLng(latitude, longitude);
	}

	public void setOnMakerInfoWindowListener(OnMakerInfoWindowListener listener)
	{
		mOnMakerInfoWindowListener = listener;
	}

	/////////////////////////////////////////////////////////////////////////////////
	// Listener
	////////////////////////////////////////////////////////////////////////////////

	private OnMarkerClickListener mOnMarkerClickListener = new OnMarkerClickListener()
	{
		@Override
		public boolean onMarkerClick(Marker marker)
		{
			if (getActivity() == null)
			{
				return false;
			}

			LatLng latlng = marker.getPosition();

			String key = String.valueOf(latlng.latitude) + String.valueOf(latlng.longitude);

			if (mDuplicateHotel.containsKey(key) == true)
			{
				ArrayList<Hotel> arrayList = mDuplicateHotel.get(key);

				mHotelInfoWindowAdapter.setHotelListViewItems(arrayList);

				if (mOnMakerInfoWindowListener != null)
				{
					mOnMakerInfoWindowListener.setInfoWindow(marker, mHotelInfoWindowAdapter.getInfoWindow());
				}

				marker.showInfoWindow();

				mOpenMakrer = true;
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

						ArrayList<Hotel> arrayList = new ArrayList<Hotel>(1);
						arrayList.add(hotelListViewItem.getItem());

						mHotelInfoWindowAdapter.setHotelListViewItems(arrayList);

						if (mOnMakerInfoWindowListener != null)
						{
							mOnMakerInfoWindowListener.setInfoWindow(marker, mHotelInfoWindowAdapter.getInfoWindow());
						}

						marker.showInfoWindow();

						mOpenMakrer = true;
						break;
					}
				}
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

			mOpenMakrer = true;

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

	private class HotelPriceRenderer
	{
		private String mPrice;
		private IconGenerator mIconGenerator;
		private boolean mIsSoldOut;
		private boolean mIsDailyChoice;

		public HotelPriceRenderer(Hotel hotel)
		{
			int originalPrice = Integer.parseInt(hotel.getDiscount().replaceAll(",", ""));
			DecimalFormat comma = new DecimalFormat("###,##0");

			mPrice = "₩" + comma.format(originalPrice);

			mIconGenerator = new IconGenerator(getActivity());

			// SOLD OUT
			mIsSoldOut = hotel.getAvailableRoom() == 0;

			mIconGenerator.setTextColor(getResources().getColor(R.color.white));
			mIconGenerator.setColor(getResources().getColor(hotel.getCategory().getColorResId()));
		}

		public BitmapDescriptor getBitmap()
		{
			Bitmap icon = mIconGenerator.makeIcon(mPrice, mIsSoldOut, mIsDailyChoice);

			if (icon == null)
			{
				return null;
			}

			return BitmapDescriptorFactory.fromBitmap(icon);
		}
	}
}
