package com.twoheart.dailyhotel.fragment;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
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
import com.twoheart.dailyhotel.adapter.PlaceViewPagerAdapter;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceRenderer;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.LoadingDialog;
import com.twoheart.dailyhotel.view.LocationFactory;
import com.twoheart.dailyhotel.view.LoopViewPager;
import com.twoheart.dailyhotel.view.MyLocationMarker;
import com.twoheart.dailyhotel.view.PlaceClusterItem;
import com.twoheart.dailyhotel.view.PlaceClusterRenderer;
import com.twoheart.dailyhotel.view.PlaceClusterRenderer.OnSelectedClusterItemListener;
import com.twoheart.dailyhotel.view.PlaceClusterRenderer.Renderer;
import com.twoheart.dailyhotel.view.PlaceViewItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public abstract class PlaceMapFragment extends com.google.android.gms.maps.SupportMapFragment implements ClusterManager.OnClusterClickListener<PlaceClusterItem>, ClusterManager.OnClusterItemClickListener<PlaceClusterItem>
{
    protected PlaceMainFragment.OnUserActionListener mUserActionListener;
    private GoogleMap mGoogleMap;
    private ArrayList<PlaceViewItem> mPlaceViewItemList; // 선택된 호텔을 위한 리스트
    private ArrayList<PlaceViewItem> mPlaceViewItemViewPagerList; // ViewPager을 위한 리스트
    private LoadingDialog mLoadingDialog;
    private MarkerOptions mMyLocationMarkerOptions;
    private Marker mMyLocationMarker;
    private SaleTime mSaleTime;
    private boolean mIsCreateView = false;
    private boolean mCallMakeMarker = false;

    private PlaceViewItem mSelectedPlaceViewItem;
    private boolean mIsOpenMakrer; // 마커를 선택한 경우.
    private HashMap<String, ArrayList<Place>> mDuplicatePlace;

    private ClusterManager<PlaceClusterItem> mClusterManager;
    private PlaceClusterRenderer mPlaceClusterRenderer;
    private Marker mSelectedMarker;
    private View mMyLocationView;
    private ViewPager mViewPager;
    private PlaceViewPagerAdapter mPlaceViewPagerAdapter;

    public PlaceMapFragment()
    {
    }

    protected abstract PlaceViewPagerAdapter createPlaceViewPagerAdapter(BaseActivity baseActivity);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        if (mLoadingDialog == null)
        {
            mLoadingDialog = new LoadingDialog((BaseActivity) getActivity());
        }

        if (mDuplicatePlace == null)
        {
            mDuplicatePlace = new HashMap<String, ArrayList<Place>>();
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

                mClusterManager = new ClusterManager<PlaceClusterItem>(baseActivity, mGoogleMap);
                mPlaceClusterRenderer = new PlaceClusterRenderer(baseActivity, mGoogleMap, mClusterManager);
                mPlaceClusterRenderer.setOnClusterRenderedListener(mOnClusterRenderedListener);

                mClusterManager.setRenderer(mPlaceClusterRenderer);
                mClusterManager.setAlgorithm(new NonHierarchicalDistanceBasedAlgorithm<PlaceClusterItem>());

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

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, Util.dpToPx(view.getContext(), 132));
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

        LocationFactory.getInstance((BaseActivity) getActivity()).clear();

        super.onDestroyView();
    }

    @Override
    public boolean onClusterItemClick(PlaceClusterItem item, Marker marker)
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
    public boolean onClusterClick(Cluster<PlaceClusterItem> cluster)
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (PlaceClusterItem placeClusterItem : cluster.getItems())
        {
            LatLng latlng = placeClusterItem.getPosition();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == Constants.REQUEST_CODE_PERMISSIONS_ACCESS_FINE_LOCATION)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            boolean permission = LocationFactory.getInstance(baseActivity).hasPermission();

            if (permission == true)
            {
                searchMyLocation(baseActivity);
            }
        }
    }

    public void setUserActionListener(PlaceMainFragment.OnUserActionListener userActionLister)
    {
        mUserActionListener = userActionLister;
    }

    public void setPlaceViewItemList(ArrayList<PlaceViewItem> arrayList, SaleTime saleTime, boolean isChangedRegion)
    {
        mPlaceViewItemList = arrayList;
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

        if (mPlaceViewItemList == null || mPlaceViewItemList.size() == 0)
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

        if (mIsOpenMakrer == true && mSelectedPlaceViewItem != null)
        {
            latitude = mSelectedPlaceViewItem.getPlace().latitude;
            longitude = mSelectedPlaceViewItem.getPlace().longitude;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if (mDuplicatePlace == null)
        {
            mDuplicatePlace = new HashMap<String, ArrayList<Place>>();
        }

        mDuplicatePlace.clear();

        // 중복 지역을 찾아내기 위한 로직.
        if (mPlaceViewItemViewPagerList != null)
        {
            mPlaceViewItemViewPagerList.clear();
        }

        mPlaceViewItemViewPagerList = null;

        mPlaceViewItemViewPagerList = searchDuplicateLocateion(mPlaceViewItemList, mDuplicatePlace);

        mClusterManager.clearItems();
        mGoogleMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(PlaceMapFragment.this);
        mClusterManager.setOnClusterItemClickListener(PlaceMapFragment.this);

        for (PlaceViewItem placeViewItem : mPlaceViewItemViewPagerList)
        {
            Place place = placeViewItem.getPlace();

            count++;

            PlaceClusterItem placelusterItem = new PlaceClusterItem(place);
            mClusterManager.addItem(placelusterItem);

            LatLng latlng = new LatLng(place.latitude, place.longitude);
            builder.include(latlng);

            // 기존의 마커 정보 창을 보여준다.
            if (mIsOpenMakrer == true)
            {
                if (latitude == place.latitude && longitude == place.longitude)
                {
                    isOpenMarker = true;

                    mPlaceClusterRenderer.setSelectedClusterItem(placelusterItem);
                    mPlaceClusterRenderer.setSelectedClusterItemListener(new OnSelectedClusterItemListener()
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
    private ArrayList<PlaceViewItem> searchDuplicateLocateion(ArrayList<PlaceViewItem> hotelArrayList, HashMap<String, ArrayList<Place>> hashMap)
    {
        ArrayList<PlaceViewItem> arrangeList = new ArrayList<PlaceViewItem>(hotelArrayList);

        int size = arrangeList.size();
        PlaceViewItem placeViewItem = null;

        // 섹션 정보와 솔드 아웃인 경우 목록에서 제거 시킨다.
        for (int i = size - 1; i >= 0; i--)
        {
            placeViewItem = arrangeList.get(i);

            if (placeViewItem.type == PlaceViewItem.TYPE_SECTION)
            {
                arrangeList.remove(i);
            } else
            {
                if (placeViewItem.getPlace().isSoldOut)
                {
                    arrangeList.remove(i);
                }
            }
        }

        // 중복된 위치에 있는 호텔들은 위해서 소팅한다.
        Comparator<PlaceViewItem> comparator = new Comparator<PlaceViewItem>()
        {
            final LatLng latlng = new LatLng(37.23945, 131.8689);

            public int compare(PlaceViewItem o1, PlaceViewItem o2)
            {
                Place item01 = o1.getPlace();
                Place item02 = o2.getPlace();

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
            Place item01 = null;
            Place item02 = null;

            for (int i = size - 1; i > 0; i--)
            {
                item01 = arrangeList.get(i).getPlace();
                item02 = arrangeList.get(i - 1).getPlace();

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

                    String key = Double.toString(item01.latitude) + Double.toString(item01.longitude);

                    if (hashMap.containsKey(key) == true)
                    {
                        ArrayList<Place> dulicateArrayList = hashMap.get(key);

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
                        ArrayList<Place> dulicateArrayList = new ArrayList<Place>();

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
        int size = mPlaceViewItemViewPagerList.size();

        for (int i = 0; i < size; i++)
        {
            PlaceViewItem placeViewItem = mPlaceViewItemViewPagerList.get(i);
            Place place = placeViewItem.getPlace();

            if (latlng.latitude == place.latitude && latlng.longitude == place.longitude)
            {
                position = i;

                PlaceRenderer placeRenderer = new PlaceRenderer(baseActivity, place.discountPrice, place.grade.getMarkerResId());
                BitmapDescriptor icon = placeRenderer.getBitmap(true);

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
            mPlaceViewPagerAdapter.notifyDataSetChanged();

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

        Comparator<PlaceViewItem> comparator = new Comparator<PlaceViewItem>()
        {
            public int compare(PlaceViewItem o1, PlaceViewItem o2)
            {
                Place item01 = o1.getPlace();
                Place item02 = o2.getPlace();

                float[] results1 = new float[3];
                Location.distanceBetween(latlng.latitude, latlng.longitude, item01.latitude, item01.longitude, results1);

                float[] results2 = new float[3];
                Location.distanceBetween(latlng.latitude, latlng.longitude, item02.latitude, item02.longitude, results2);

                return Float.compare(results1[0], results2[0]);
            }
        };

        Collections.sort(mPlaceViewItemViewPagerList, comparator);

        if (mPlaceViewPagerAdapter == null)
        {
            mPlaceViewPagerAdapter = createPlaceViewPagerAdapter(baseActivity);
            mPlaceViewPagerAdapter.setOnUserActionListener(mOnInfoWindowUserActionListener);
        }

        mPlaceViewPagerAdapter.setData(mPlaceViewItemViewPagerList);
        mViewPager.setAdapter(mPlaceViewPagerAdapter);
        mPlaceViewPagerAdapter.notifyDataSetChanged();

        mIsOpenMakrer = true;

        int position = -1;
        int size = mPlaceViewItemViewPagerList.size();

        for (int i = 0; i < size; i++)
        {
            PlaceViewItem placeViewItem = mPlaceViewItemViewPagerList.get(i);
            Place place = placeViewItem.getPlace();

            if (latlng.latitude == place.latitude && latlng.longitude == place.longitude)
            {
                position = i;

                PlaceRenderer placeRenderer = new PlaceRenderer(baseActivity, place.discountPrice, place.grade.getMarkerResId());
                BitmapDescriptor icon = placeRenderer.getBitmap(true);

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

    private void searchMyLocation(BaseActivity baseActivity)
    {
        LocationFactory.getInstance(baseActivity).startLocationMeasure(PlaceMapFragment.this, mMyLocationView, new LocationFactory.LocationListenerEx()
        {
            @Override
            public void onRequirePermission()
            {
                if (Util.isOverAPI23() == true)
                {
                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_CODE_PERMISSIONS_ACCESS_FINE_LOCATION);
                }
            }

            @Override
            public void onFailed()
            {

            }

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

    /////////////////////////////////////////////////////////////////////////////////
    // Listener
    ////////////////////////////////////////////////////////////////////////////////

    public interface OnUserActionListener
    {
        public void onInfoWindowClickListener(Place selectedPlace);

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

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageSelected(int page)
        {
            if (mPlaceViewItemViewPagerList == null || mPlaceViewItemViewPagerList.size() <= page)
            {
                return;
            }

            PlaceViewItem placeViewItem = mPlaceViewItemViewPagerList.get(page);

            Place place = placeViewItem.getPlace();

            if (place != null)
            {
                PlaceClusterItem hotelClusterItem = new PlaceClusterItem(place);
                mPlaceClusterRenderer.setSelectedClusterItem(hotelClusterItem);

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

            searchMyLocation(baseActivity);
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
            mSelectedPlaceViewItem = null;

            if (mViewPager != null)
            {
                mViewPager.setVisibility(View.INVISIBLE);
            }
        }
    };

    private PlaceClusterRenderer.OnClusterRenderedListener mOnClusterRenderedListener = new PlaceClusterRenderer.OnClusterRenderedListener()
    {
        @Override
        public void onClusterRenderedListener(com.twoheart.dailyhotel.view.PlaceClusterRenderer.Renderer renderer)
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
        public void onInfoWindowClickListener(Place selectedPlace)
        {
            if (getActivity() == null)
            {
                return;
            }

            if (mUserActionListener != null)
            {
                for (PlaceViewItem placeViewItem : mPlaceViewItemList)
                {
                    if (placeViewItem.type == PlaceViewItem.TYPE_SECTION)
                    {
                        continue;
                    }

                    Place place = placeViewItem.getPlace();

                    if (place.equals(selectedPlace) == true)
                    {
                        mSelectedPlaceViewItem = placeViewItem;
                        mUserActionListener.selectPlace(placeViewItem, mSaleTime);
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

    private OnMarkerClickListener mOnMarkerClickListener = new OnMarkerClickListener()
    {
        @Override
        public boolean onMarkerClick(Marker marker)
        {
            PlaceMapFragment.this.onMarkerClick(marker.getPosition());

            return true;
        }
    };
}
