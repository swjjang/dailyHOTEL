package com.twoheart.dailyhotel.fragment;

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
import com.twoheart.dailyhotel.adapter.HotelListViewPagerAdapter;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.HotelRenderer;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.HotelClusterItem;
import com.twoheart.dailyhotel.view.HotelClusterRenderer;
import com.twoheart.dailyhotel.view.HotelClusterRenderer.OnSelectedClusterItemListener;
import com.twoheart.dailyhotel.view.HotelClusterRenderer.Renderer;
import com.twoheart.dailyhotel.view.HotelListViewItem;
import com.twoheart.dailyhotel.view.LoadingDialog;
import com.twoheart.dailyhotel.view.LocationFactory;
import com.twoheart.dailyhotel.view.LoopViewPager;
import com.twoheart.dailyhotel.view.MyLocationMarker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class HotelListMapFragment extends com.google.android.gms.maps.SupportMapFragment implements ClusterManager.OnClusterClickListener<HotelClusterItem>, ClusterManager.OnClusterItemClickListener<HotelClusterItem>
{
    protected HotelMainFragment.OnUserActionListener mUserActionListener;
    private GoogleMap mGoogleMap;
    private ArrayList<HotelListViewItem> mHotelArrayList; // 선택된 호텔을 위한 리스트
    private ArrayList<HotelListViewItem> mHotelArrangeArrayList; // ViewPager을 위한 리스트
    private LoadingDialog mLoadingDialog;
    private MarkerOptions mMyLocationMarkerOptions;
    private Marker mMyLocationMarker;
    private SaleTime mSaleTime;
    private boolean mIsCreateView = false;
    private boolean mCallMakeMarker = false;

    private HotelListViewItem mSelectedHotelListViewItem;
    private boolean mIsOpenMakrer; // 마커를 선택한 경우.
    private HashMap<String, ArrayList<Hotel>> mDuplicateHotel;

    private ClusterManager<HotelClusterItem> mClusterManager;
    private HotelClusterRenderer mHotelClusterRenderer;
    private Marker mSelectedMarker;
    private View mMyLocationView;
    private LoopViewPager mViewPager;
    private HotelListViewPagerAdapter mHotelListViewPagerAdapter;

    private OnMarkerClickListener mOnMarkerClickListener = new OnMarkerClickListener()
    {
        @Override
        public boolean onMarkerClick(Marker marker)
        {
            HotelListMapFragment.this.onMarkerClick(marker.getPosition());

            return true;
        }
    };

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

                //				mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
                mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
                mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);
                mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
                mGoogleMap.setOnMapClickListener(mOnMapClickListener);

                relocationMyLocation();
                relocationZoomControl();

                mClusterManager = new ClusterManager<HotelClusterItem>(baseActivity, mGoogleMap);
                mHotelClusterRenderer = new HotelClusterRenderer(baseActivity, mGoogleMap, mClusterManager);
                mHotelClusterRenderer.setOnClusterRenderedListener(mOnClusterRenderedListener);

                mClusterManager.setRenderer(mHotelClusterRenderer);
                mClusterManager.setAlgorithm(new NonHierarchicalDistanceBasedAlgorithm<HotelClusterItem>());

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
    public boolean onClusterItemClick(HotelClusterItem item, Marker marker)
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
    public boolean onClusterClick(Cluster<HotelClusterItem> cluster)
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (HotelClusterItem hotelClusterItem : cluster.getItems())
        {
            LatLng latlng = hotelClusterItem.getPosition();
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

    public void setUserActionListener(HotelMainFragment.OnUserActionListener userActionLister)
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
        mSelectedMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).visible(false).anchor(0.0f, 1.0f));

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

            if (mOnInfoWindowUserActionListener != null)
            {
                mOnInfoWindowUserActionListener.onCloseInfoWindowClickListener();
            }
        }

        double latitude = 0.0;
        double longitude = 0.0;
        int count = 0;
        boolean isOpenMarker = false;

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
        if (mHotelArrangeArrayList != null)
        {
            mHotelArrangeArrayList.clear();
        }

        mHotelArrangeArrayList = null;

        mHotelArrangeArrayList = searchDuplicateLocateion(mHotelArrayList, mDuplicateHotel);

        mClusterManager.clearItems();
        mGoogleMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(HotelListMapFragment.this);
        mClusterManager.setOnClusterItemClickListener(HotelListMapFragment.this);

        for (HotelListViewItem hotelListViewItem : mHotelArrangeArrayList)
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
                    isOpenMarker = true;

                    mHotelClusterRenderer.setSelectedClusterItem(hotelClusterItem);
                    mHotelClusterRenderer.setSelectedClusterItemListener(new OnSelectedClusterItemListener()
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

    /////////////////////////////////////////////////////////////////////////////////
    //
    ////////////////////////////////////////////////////////////////////////////////

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
    private ArrayList<HotelListViewItem> searchDuplicateLocateion(ArrayList<HotelListViewItem> hotelArrayList, HashMap<String, ArrayList<Hotel>> hashMap)
    {
        ArrayList<HotelListViewItem> arrangeList = new ArrayList<HotelListViewItem>(hotelArrayList);

        int size = arrangeList.size();
        HotelListViewItem hotelListViewItem = null;

        // 섹션 정보와 솔드 아웃인 경우 목록에서 제거 시킨다.
        for (int i = size - 1; i >= 0; i--)
        {
            hotelListViewItem = arrangeList.get(i);

            if (hotelListViewItem.getType() == HotelListViewItem.TYPE_SECTION)
            {
                arrangeList.remove(i);
            } else
            {
                if (hotelListViewItem.getItem().getAvailableRoom() == 0)
                {
                    arrangeList.remove(i);
                }
            }
        }

        // 중복된 위치에 있는 호텔들은 위해서 소팅한다.
        Comparator<HotelListViewItem> comparator = new Comparator<HotelListViewItem>()
        {
            final LatLng latlng = new LatLng(37.23945, 131.8689);

            public int compare(HotelListViewItem o1, HotelListViewItem o2)
            {
                Hotel item01 = o1.getItem();
                Hotel item02 = o2.getItem();

                float[] results1 = new float[3];
                Location.distanceBetween(latlng.latitude, latlng.longitude, item01.mLatitude, item01.mLongitude, results1);

                float[] results2 = new float[3];
                Location.distanceBetween(latlng.latitude, latlng.longitude, item02.mLatitude, item02.mLongitude, results2);

                return Float.compare(results1[0], results2[0]);
            }
        };

        Collections.sort(arrangeList, comparator);

        size = arrangeList.size();

        // 중복된 호텔들은 낮은 가격을 노출하도록 한다.
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
                    int item01DisCount = item01.averageDiscount;
                    int item02DisCount = item02.averageDiscount;

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

    private void onMarkerTempClick(final LatLng latlng)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        mIsOpenMakrer = true;

        int position = -1;
        int size = mHotelArrangeArrayList.size();

        for (int i = 0; i < size; i++)
        {
            HotelListViewItem hotelListViewItem = mHotelArrangeArrayList.get(i);
            Hotel hotel = hotelListViewItem.getItem();

            if (latlng.latitude == hotel.mLatitude && latlng.longitude == hotel.mLongitude)
            {
                position = i;

                HotelRenderer hotelRenderer = new HotelRenderer(baseActivity, hotel);
                BitmapDescriptor icon = hotelRenderer.getBitmap(true);

                if (mSelectedMarker != null)
                {
                    mSelectedMarker.setVisible(false);
                }

                if (icon != null && mSelectedMarker != null)
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
            mHotelListViewPagerAdapter.notifyDataSetChanged();

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

        Comparator<HotelListViewItem> comparator = new Comparator<HotelListViewItem>()
        {
            public int compare(HotelListViewItem o1, HotelListViewItem o2)
            {
                Hotel item01 = o1.getItem();
                Hotel item02 = o2.getItem();

                float[] results1 = new float[3];
                Location.distanceBetween(latlng.latitude, latlng.longitude, item01.mLatitude, item01.mLongitude, results1);

                float[] results2 = new float[3];
                Location.distanceBetween(latlng.latitude, latlng.longitude, item02.mLatitude, item02.mLongitude, results2);

                return Float.compare(results1[0], results2[0]);
            }
        };

        Collections.sort(mHotelArrangeArrayList, comparator);

        if (mHotelListViewPagerAdapter == null)
        {
            mHotelListViewPagerAdapter = new HotelListViewPagerAdapter(baseActivity);
            mHotelListViewPagerAdapter.setOnUserActionListener(mOnInfoWindowUserActionListener);
        }

        mHotelListViewPagerAdapter.setData(mHotelArrangeArrayList);
        mViewPager.setAdapter(mHotelListViewPagerAdapter);
        mHotelListViewPagerAdapter.notifyDataSetChanged();

        mIsOpenMakrer = true;

        int position = -1;
        int size = mHotelArrangeArrayList.size();

        for (int i = 0; i < size; i++)
        {
            HotelListViewItem hotelListViewItem = mHotelArrangeArrayList.get(i);
            Hotel hotel = hotelListViewItem.getItem();

            if (latlng.latitude == hotel.mLatitude && latlng.longitude == hotel.mLongitude)
            {
                position = i;

                HotelRenderer hotelRenderer = new HotelRenderer(baseActivity, hotel);
                BitmapDescriptor icon = hotelRenderer.getBitmap(true);

                if (mSelectedMarker != null)
                {
                    mSelectedMarker.setVisible(false);
                }

                if (icon != null && mSelectedMarker != null)
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

    public interface OnUserActionListener
    {
        public void onInfoWindowClickListener(Hotel hotel);

        public void onCloseInfoWindowClickListener();
    }

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
            return layoutInflater.inflate(R.layout.no_info_window, null);
        }

        @Override
        public View getInfoContents(Marker marker)
        {
            return null;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////
    // Listener
    ////////////////////////////////////////////////////////////////////////////////

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageSelected(int page)
        {
            if (mHotelArrangeArrayList == null || mHotelArrangeArrayList.size() <= page)
            {
                return;
            }

            HotelListViewItem hotelListViewItem = mHotelArrangeArrayList.get(page);

            Hotel hotel = hotelListViewItem.getItem();

            if (hotel != null)
            {
                HotelClusterItem hotelClusterItem = new HotelClusterItem(hotel);
                mHotelClusterRenderer.setSelectedClusterItem(hotelClusterItem);

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

            LocationFactory.getInstance(baseActivity).startLocationMeasure(HotelListMapFragment.this, mMyLocationView, new LocationListener()
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
            mSelectedHotelListViewItem = null;

            if (mViewPager != null)
            {
                mViewPager.setVisibility(View.INVISIBLE);
            }
        }
    };

    private HotelClusterRenderer.OnClusterRenderedListener mOnClusterRenderedListener = new HotelClusterRenderer.OnClusterRenderedListener()
    {
        @Override
        public void onClusterRenderedListener(Renderer renderer)
        {
            if (renderer == Renderer.CLUSTER)
            {
                mOnMapClickListener.onMapClick(null);
            }
        }
    };

    private OnUserActionListener mOnInfoWindowUserActionListener = new OnUserActionListener()
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
                        mUserActionListener.selectHotel(hotelListViewItem, mSaleTime);
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
}
