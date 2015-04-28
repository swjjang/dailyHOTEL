package com.twoheart.dailyhotel.fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
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
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
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
	private BaseActivity mHotelActivity;

	private HotelListViewItem mSelectedHotelListViewItem;
	private int mSelectedHotelIndex;
	private Marker mMarker;
	private boolean mOpenMakrer;
	private boolean mIsPause;
	private String mRegion;
	private String mDetailRegion;

	public HotelListMapFragment()
	{
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);

		mHotelActivity = (BaseActivity) getActivity();
		mHotelActivity.lockUI();

		getMapAsync(new OnMapReadyCallback()
		{
			@Override
			public void onMapReady(GoogleMap googleMap)
			{
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

				makeMarker();

				mHotelActivity.unLockUI();
			}
		});

		return view;
	}

	@Override
	public void onPause()
	{
		mIsPause = true;

		super.onPause();
	}

	public void setRegion(String region)
	{
		mRegion = region;
	}

	public void setDetailRegion(String region)
	{
		mDetailRegion = region;
	}

	public void setUserActionListener(HotelMainFragment.UserActionListener userActionLister)
	{
		mUserActionListener = userActionLister;
	}

	public void setHotelList(ArrayList<HotelListViewItem> hotelArrayList, SaleTime saleTime)
	{
		mHotelArrayList = hotelArrayList;
		mSaleTime = saleTime;

		// Marker 만들기.
		if (mIsCreateView == true)
		{
			makeMarker();
		}
	}

	private void makeMarker()
	{
		if (mGoogleMap == null)
		{
			return;
		}

		mGoogleMap.clear();

		if (mHotelArrayList == null)
		{
			return;
		}

		double latitude = 0.0;
		double longitude = 0.0;
		int count = 0;

		if (mOpenMakrer == true)
		{
			latitude = mSelectedHotelListViewItem.getItem().mLatitude;
			longitude = mSelectedHotelListViewItem.getItem().mLongitude;
		}

		LatLngBounds.Builder builder = new LatLngBounds.Builder();

		boolean isSeoul = "서울".equalsIgnoreCase(mRegion);

		for (HotelListViewItem hotelListViewItem : mHotelArrayList)
		{
			if (hotelListViewItem.getType() == HotelListViewItem.TYPE_SECTION)
			{
				continue;
			}

			Hotel hotel = hotelListViewItem.getItem();
			Marker marker = addMarker(hotel);

			if (isSeoul == true)
			{
				if (TextUtils.isEmpty(mDetailRegion) == false && mDetailRegion.contains(hotel.getDetailRegion()) == true)
				{
					LatLng latlng = new LatLng(hotel.mLatitude, hotel.mLongitude);
					builder.include(latlng);

					count++;
				}
			} else
			{
				LatLng latlng = new LatLng(hotel.mLatitude, hotel.mLongitude);
				builder.include(latlng);

				count++;
			}

			if (mOpenMakrer == true)
			{
				if (latitude == hotel.mLatitude && longitude == hotel.mLongitude)
				{
					mMarker = marker;
				}
			}
		}

		// 특정 지역의 마커들을 모아서 한 화면에 보이도록 한다.
		// 해당 개수가 1개이면 줌 레벨 15이다.
		// 만일 해당 지역에 마커가 없으면 고민해보기
		// onPause()가 호출 되면 애니매이션 하지 않가.

		final LatLngBounds bounds = builder.build();

		if (mOpenMakrer == false && mIsPause == false)
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
				//				mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback()
				//				{
				//					@Override
				//					public void onMapLoaded()
				//					{
				//						if (mOpenMakrer == true)
				//						{
				//							mOpenMakrer = false;
				//
				//							if (mMarker != null)
				//							{
				//								mHotelInfoWindowAdapter.setHotelListViewItem(mHotelListViewItem);
				//								mHotelInfoWindowAdapter.setHotelIndex(mHotelIndex);
				//								mMarker.showInfoWindow();
				//							}
				//						} else
				//						{
				//							CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, Util.dpToPx(getActivity(), 50));
				//							mGoogleMap.animateCamera(cameraUpdate);
				//						}
				//					}
				//				});
				
//				CameraPosition cp = new CameraPosition.Builder().target(bounds.getCenter()).zoom(15).build();
//				mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));

//				mGoogleMap.setOnCameraChangeListener(new OnCameraChangeListener()
//				{
//					@Override
//					public void onCameraChange(CameraPosition arg0)
//					{
//						mGoogleMap.setOnCameraChangeListener(null);

						if (mOpenMakrer == true)
						{
							mOpenMakrer = false;

							if (mMarker != null)
							{
								mHotelInfoWindowAdapter.setHotelListViewItem(mSelectedHotelListViewItem);
								mHotelInfoWindowAdapter.setHotelIndex(mSelectedHotelIndex);
								mMarker.showInfoWindow();
							}
						} else
						{
							if (mIsPause == false)
							{
								CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, Util.dpToPx(getActivity(), 50));
								mGoogleMap.animateCamera(cameraUpdate);
							}

							mIsPause = false;
						}
//					}
//				});
			}
		}

		mHotelInfoWindowAdapter = new HotelInfoWindowAdapter(getActivity());

		mGoogleMap.setInfoWindowAdapter(mHotelInfoWindowAdapter);
		mGoogleMap.setOnMarkerClickListener(new OnMarkerClickListener()
		{
			@Override
			public boolean onMarkerClick(Marker marker)
			{
				LatLng latlng = marker.getPosition();

				int index = 0;

				for (HotelListViewItem hotelListViewItem : mHotelArrayList)
				{
					if (hotelListViewItem.getType() == HotelListViewItem.TYPE_SECTION)
					{
						continue;
					}

					Hotel hotel = hotelListViewItem.getItem();

					if (latlng.latitude == hotel.mLatitude && latlng.longitude == hotel.mLongitude)
					{
						mHotelInfoWindowAdapter.setHotelListViewItem(hotelListViewItem);
						mHotelInfoWindowAdapter.setHotelIndex(index);
						marker.showInfoWindow();

						mOpenMakrer = true;
						break;
					}

					index++;
				}

				return false;
			}
		});

		mGoogleMap.setOnMapClickListener(new OnMapClickListener()
		{
			@Override
			public void onMapClick(LatLng latlng)
			{
				mOpenMakrer = false;
			}
		});

		mGoogleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener()
		{
			@Override
			public void onInfoWindowClick(Marker arg0)
			{
				mSelectedHotelListViewItem = mHotelInfoWindowAdapter.getHotelListViewItem();
				mSelectedHotelIndex = mHotelInfoWindowAdapter.getHotelIndex();

				if (mUserActionListener != null)
				{
					mUserActionListener.selectHotel(mSelectedHotelListViewItem, mSelectedHotelIndex, mSaleTime);
				}
			}
		});
	}

	private Marker addMarker(Hotel hotel)
	{
		if (mGoogleMap != null)
		{
			HotelPriceRenderer hotelPriceRenderer = new HotelPriceRenderer(hotel);

			MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(hotel.mLatitude, hotel.mLongitude)).title(hotel.getDiscount()).icon(hotelPriceRenderer.getBitmap());

			return mGoogleMap.addMarker(markerOptions);
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

	private class HotelPriceRenderer
	{
		private String mPrice;
		private IconGenerator mIconGenerator;
		private boolean mIsSoldOut;

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
			Bitmap icon = mIconGenerator.makeIcon(mPrice, mIsSoldOut);

			return BitmapDescriptorFactory.fromBitmap(icon);
		}
	}
}
