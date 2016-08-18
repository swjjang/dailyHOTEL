package com.twoheart.dailyhotel.place.fragment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
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
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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
import com.twoheart.dailyhotel.model.MyLocationMarker;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceClusterItem;
import com.twoheart.dailyhotel.model.PlaceClusterRenderer;
import com.twoheart.dailyhotel.model.PlaceRenderer;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.place.adapter.PlaceMapViewPagerAdapter;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.common.LoadingDialog;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyLocationFactory;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyViewPager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class PlaceListMapFragment extends com.google.android.gms.maps.SupportMapFragment implements ClusterManager.OnClusterClickListener<PlaceClusterItem>, ClusterManager.OnClusterItemClickListener<PlaceClusterItem>
{
    private static final int ANIMATION_DEALY = 200;
    private static final int VIEWPAGER_HEIGHT_DP = 110;

    private GoogleMap mGoogleMap;
    protected List<PlaceViewItem> mPlaceViewItemList; // 선택된 호텔을 위한 리스트
    private List<PlaceViewItem> mPlaceViewItemViewPagerList; // ViewPager을 위한 리스트
    private LoadingDialog mLoadingDialog;
    private MarkerOptions mMyLocationMarkerOptions;
    private Marker mMyLocationMarker;
    private View mBottomOptionLayout;

    protected boolean mIsCreateView = false;
    private boolean mCallMakeMarker = false;

    private PlaceViewItem mSelectedPlaceViewItem;
    protected boolean mIsOpenMakrer; // 마커를 선택한 경우.

    private ClusterManager<PlaceClusterItem> mClusterManager;
    private PlaceClusterRenderer mPlaceClusterRenderer;
    private Marker mSelectedMarker;
    private View mMyLocationView;
    private DailyViewPager mViewPager;

    protected BaseActivity mBaseActivity;
    protected OnPlaceListMapFragmentListener mOnPlaceListMapFragmentListener;
    protected PlaceMapViewPagerAdapter mPlaceMapViewPagerAdapter;

    private Constants.ANIMATION_STATUS mAnimationStatus = Constants.ANIMATION_STATUS.SHOW_END;
    private Constants.ANIMATION_STATE mAnimationState = Constants.ANIMATION_STATE.END;
    private ValueAnimator mValueAnimator;

    public interface OnPlaceListMapFragmentListener
    {
        void onInformationClick(PlaceViewItem placeViewItem);
    }

    protected abstract PlaceMapViewPagerAdapter getPlaceListMapViewPagerAdapter(Context context);

    protected abstract void onAnalyticsMarkerClick(String placeName);

    public PlaceListMapFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mBaseActivity = (BaseActivity) getActivity();

        if (mLoadingDialog == null)
        {
            mLoadingDialog = new LoadingDialog(mBaseActivity);
        }

        getMapAsync(new OnMapReadyCallback()
        {
            @Override
            public void onMapReady(GoogleMap googleMap)
            {
                if (isFinishing() == true)
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

                mClusterManager = new ClusterManager<>(mBaseActivity, mGoogleMap);
                mPlaceClusterRenderer = new PlaceClusterRenderer(mBaseActivity, mGoogleMap, mClusterManager);
                mPlaceClusterRenderer.setOnClusterRenderedListener(mOnClusterRenderedListener);

                mClusterManager.setRenderer(mPlaceClusterRenderer);
                mClusterManager.setAlgorithm(new NonHierarchicalDistanceBasedAlgorithm<PlaceClusterItem>());

                mGoogleMap.setInfoWindowAdapter(new MapWindowAdapter(mBaseActivity));

                // 서울이 아니고 상세 지역 정보가 아닌 경우..지역별 중심값으로 이동.
                LatLng latlng = new LatLng(35.856899430657805, 127.73446206003428);
                CameraPosition cp = new CameraPosition.Builder().target(latlng).zoom(6.791876f).build();
                mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));

                mIsCreateView = true;

                addViewPager(mBaseActivity, container);

                makeMarker(true);
            }
        });

        return view;
    }

    protected boolean isFinishing()
    {
        Activity activity = getActivity();

        return (isAdded() == false || activity == null//
            || activity.isFinishing() == true);
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();

        System.gc();
    }

    public void setBottomOptionLayout(View view)
    {
        mBottomOptionLayout = view;
    }

    private void addViewPager(BaseActivity baseActivity, ViewGroup viewGroup)
    {
        // Add Stay Info ViewPager
        if (mViewPager != null)
        {
            return;
        }

        int padding = Util.dpToPx(baseActivity, 15d);

        mViewPager = new DailyViewPager(baseActivity);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setClipToPadding(false);
        mViewPager.setPageMargin(Util.dpToPx(baseActivity, 5d));
        mViewPager.setPadding(padding, 0, padding, Util.dpToPx(baseActivity, 10d));
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, Util.dpToPx(baseActivity, VIEWPAGER_HEIGHT_DP));
        layoutParams.gravity = Gravity.BOTTOM;

        mViewPager.setLayoutParams(layoutParams);
        mViewPager.setVisibility(View.INVISIBLE);

        viewGroup.addView(mViewPager);
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

        DailyLocationFactory.getInstance(mBaseActivity).clear();

        super.onDestroyView();
    }

    @Override
    public boolean onClusterItemClick(PlaceClusterItem item, Marker marker)
    {
        if (isFinishing() == true)
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
            {
                searchMyLocation();
                break;
            }

            case Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER:
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    searchMyLocation();
                }
                break;
            }
        }
    }

    public int getPlaceViewItemListSize()
    {
        if (mPlaceViewItemList == null)
        {
            return 0;
        }

        return mPlaceViewItemList.size();
    }

    public void setPlaceViewItemList(List<PlaceViewItem> arrayList, boolean isRefreshAll)
    {
        mPlaceViewItemList = arrayList;

        // Marker 만들기.
        if (mIsCreateView == true)
        {
            makeMarker(isRefreshAll);
        }
    }

    public void setOnPlaceListMapFragment(OnPlaceListMapFragmentListener listener)
    {
        mOnPlaceListMapFragmentListener = listener;
    }

    public boolean isShowInformation()
    {
        return mViewPager != null && mViewPager.getVisibility() == View.VISIBLE;
    }

    /**
     * 추후 UI추가 필요 구글맵 버전이 바뀌면 문제가 될수도 있음.
     */
    private void relocationZoomControl()
    {
        if (isFinishing() == true)
        {
            return;
        }

        // Find myLocationButton view
        //noinspection ResourceType
        View zoomControl = getView().findViewById(0x1);

        if (zoomControl != null && zoomControl.getLayoutParams() instanceof RelativeLayout.LayoutParams)
        {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) zoomControl.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);

            zoomControl.setPadding(zoomControl.getPaddingLeft(), Util.dpToPx(mBaseActivity, 50), zoomControl.getPaddingRight(), zoomControl.getPaddingBottom());
            zoomControl.setLayoutParams(params);
        }
    }

    private void relocationMyLocation()
    {
        if (isFinishing() == true)
        {
            return;
        }

        //noinspection ConstantConditions,ResourceType
        mMyLocationView = getView().findViewById(0x2);

        if (mMyLocationView != null)
        {
            mMyLocationView.setVisibility(View.VISIBLE);
            mMyLocationView.setOnClickListener(mOnMyLocationClickListener);
        }
    }

    protected void makeMarker(boolean isRefreshAll)
    {
        if (isFinishing() == true)
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
            mLoadingDialog.show(true);
        }

        if (mCallMakeMarker == false && isRefreshAll == false)
        {
            isRefreshAll = true;
        }

        mCallMakeMarker = true;

        if (isRefreshAll == true)
        {
            mIsOpenMakrer = false;

            mOnPlaceMapViewPagerAdapterListener.onCloseClick();
        }

        double latitude = 0.0;
        double longitude = 0.0;
        int count = 0;
        boolean isOpenMarker = false;

        if (mIsOpenMakrer == true && mSelectedPlaceViewItem != null)
        {
            latitude = mSelectedPlaceViewItem.<Place>getItem().latitude;
            longitude = mSelectedPlaceViewItem.<Place>getItem().longitude;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        // 중복 지역을 찾아내기 위한 로직.
        if (mPlaceViewItemViewPagerList != null)
        {
            mPlaceViewItemViewPagerList.clear();
        }

        mPlaceViewItemViewPagerList = null;
        mPlaceViewItemViewPagerList = searchDuplicateLocateion(mPlaceViewItemList);

        mClusterManager.clearItems();
        mGoogleMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);

        for (PlaceViewItem placeViewItem : mPlaceViewItemViewPagerList)
        {
            Place place = placeViewItem.getItem();

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
                    mPlaceClusterRenderer.setSelectedClusterItemListener(new PlaceClusterRenderer.OnSelectedClusterItemListener()
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

        if (isRefreshAll == true)
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
            mGoogleMap.setOnCameraIdleListener(mClusterManager);
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

    private void onMarkerClick(final LatLng latlng)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        mViewPager.bringToFront();

        Comparator<PlaceViewItem> comparator = new Comparator<PlaceViewItem>()
        {
            public int compare(PlaceViewItem placeViewItem1, PlaceViewItem placeViewItem2)
            {
                Place item01 = placeViewItem1.getItem();
                Place item02 = placeViewItem2.getItem();

                float[] results1 = new float[3];
                Location.distanceBetween(latlng.latitude, latlng.longitude, item01.latitude, item01.longitude, results1);

                float[] results2 = new float[3];
                Location.distanceBetween(latlng.latitude, latlng.longitude, item02.latitude, item02.longitude, results2);

                return Float.compare(results1[0], results2[0]);
            }
        };

        Collections.sort(mPlaceViewItemViewPagerList, comparator);

        if (mPlaceMapViewPagerAdapter == null)
        {
            mPlaceMapViewPagerAdapter = getPlaceListMapViewPagerAdapter(baseActivity);
            mPlaceMapViewPagerAdapter.setOnPlaceMapViewPagerAdapterListener(mOnPlaceMapViewPagerAdapterListener);
        }

        mPlaceMapViewPagerAdapter.setData(mPlaceViewItemViewPagerList);
        mViewPager.setAdapter(mPlaceMapViewPagerAdapter);
        mPlaceMapViewPagerAdapter.notifyDataSetChanged();

        mIsOpenMakrer = true;

        int position = -1;
        int size = mPlaceViewItemViewPagerList.size();

        for (int i = 0; i < size; i++)
        {
            PlaceViewItem placeViewItem = mPlaceViewItemViewPagerList.get(i);
            Place place = placeViewItem.getItem();

            if (latlng.latitude == place.latitude && latlng.longitude == place.longitude)
            {
                position = i;

                PlaceRenderer placeRenderer = new PlaceRenderer(mBaseActivity, place.discountPrice, place.getGradeMarkerResId());
                BitmapDescriptor icon = placeRenderer.getBitmap(true);

                if (icon == null)
                {
                    mSelectedMarker.setVisible(false);
                } else
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

            if (Util.isOverAPI21() == true)
            {
                mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener()
                {
                    @Override
                    public void onCameraIdle()
                    {
                        mGoogleMap.setOnCameraIdleListener(mClusterManager);
                    }
                });
                mGoogleMap.setOnCameraIdleListener(null);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(mSelectedMarker.getPosition()));
            } else
            {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(mSelectedMarker.getPosition()));
            }

            // 마커의 order을 상단으로 옮긴다.
            mSelectedMarker.showInfoWindow();
        }

        showPlaceDetailAnimation();
    }

    public void setMenuBarLayoutTranslationY(float dy)
    {
        if (isFinishing() == true || mBottomOptionLayout == null || mViewPager == null)
        {
            return;
        }

        mBottomOptionLayout.setTranslationY(dy - Util.dpToPx(mBaseActivity, VIEWPAGER_HEIGHT_DP));
        mViewPager.setTranslationY(dy);
    }

    public void resetMenuBarLayoutranslation()
    {
        if (isFinishing() == true || mBottomOptionLayout == null || mViewPager == null)
        {
            return;
        }

        mBottomOptionLayout.setTranslationY(0);

        mViewPager.setVisibility(View.INVISIBLE);
        mViewPager.setTranslationY(0);
    }

    private void showPlaceDetailAnimation()
    {
        if (mAnimationState == Constants.ANIMATION_STATE.START && mAnimationStatus == Constants.ANIMATION_STATUS.SHOW)
        {
            return;
        }

        if (mValueAnimator != null)
        {
            if (mValueAnimator.isRunning() == true)
            {
                mValueAnimator.cancel();
                mValueAnimator.removeAllListeners();
            }

            mValueAnimator = null;
        }

        if (mViewPager.getVisibility() == View.VISIBLE)
        {
            return;
        }

        mValueAnimator = ValueAnimator.ofInt(0, 100);
        mValueAnimator.setDuration(ANIMATION_DEALY);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int value = (Integer) animation.getAnimatedValue();
                int height = Util.dpToPx(mBaseActivity, VIEWPAGER_HEIGHT_DP);
                float translationY = height - height * value / 100;

                setMenuBarLayoutTranslationY(translationY);
            }
        });

        mValueAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                //                setMenuBarLayoutEnabled(false);

                mViewPager.setVisibility(View.VISIBLE);
                mViewPager.setTranslationY(Util.dpToPx(mBaseActivity, VIEWPAGER_HEIGHT_DP));

                mAnimationState = Constants.ANIMATION_STATE.START;
                mAnimationStatus = Constants.ANIMATION_STATUS.SHOW;
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (mAnimationState != Constants.ANIMATION_STATE.CANCEL)
                {
                    mAnimationStatus = Constants.ANIMATION_STATUS.SHOW_END;
                    mAnimationState = Constants.ANIMATION_STATE.END;
                }

                //                setMenuBarLayoutEnabled(true);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                mAnimationState = Constants.ANIMATION_STATE.CANCEL;

                //                setMenuBarLayoutEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        mValueAnimator.start();
    }

    private void hidePlaceDetailAnimation()
    {
        if (mAnimationState == Constants.ANIMATION_STATE.START && mAnimationStatus == Constants.ANIMATION_STATUS.HIDE)
        {
            return;
        }

        if (mViewPager.getVisibility() != View.VISIBLE)
        {
            return;
        }

        if (mValueAnimator != null)
        {
            if (mValueAnimator.isRunning() == true)
            {
                mValueAnimator.cancel();
                mValueAnimator.removeAllListeners();
            }

            mValueAnimator = null;
        }

        mValueAnimator = ValueAnimator.ofInt(0, 100);
        mValueAnimator.setDuration(ANIMATION_DEALY);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int value = (Integer) animation.getAnimatedValue();
                int height = Util.dpToPx(mBaseActivity, VIEWPAGER_HEIGHT_DP);
                float translationY = height * value / 100;

                setMenuBarLayoutTranslationY(translationY);
            }
        });

        mValueAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mAnimationState = Constants.ANIMATION_STATE.START;
                mAnimationStatus = Constants.ANIMATION_STATUS.HIDE;

                setMenuBarLayoutTranslationY(0);

                //                setMenuBarLayoutEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (mAnimationState != Constants.ANIMATION_STATE.CANCEL)
                {
                    mAnimationStatus = Constants.ANIMATION_STATUS.HIDE_END;
                    mAnimationState = Constants.ANIMATION_STATE.END;
                }

                mViewPager.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                mAnimationState = Constants.ANIMATION_STATE.CANCEL;

                mViewPager.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        mValueAnimator.start();
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
            Place place = placeViewItem.getItem();

            if (latlng.latitude == place.latitude && latlng.longitude == place.longitude)
            {
                position = i;

                PlaceRenderer placeRenderer = new PlaceRenderer(mBaseActivity, place.discountPrice, place.getGradeMarkerResId());
                BitmapDescriptor icon = placeRenderer.getBitmap(true);

                if (icon == null)
                {
                    mSelectedMarker.setVisible(false);
                } else
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
            mPlaceMapViewPagerAdapter.notifyDataSetChanged();

            if (Util.isOverAPI21() == true)
            {
                mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener()
                {
                    @Override
                    public void onCameraIdle()
                    {
                        mGoogleMap.setOnCameraIdleListener(mClusterManager);
                    }
                });
                mGoogleMap.setOnCameraIdleListener(null);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(mSelectedMarker.getPosition()));
            } else
            {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(mSelectedMarker.getPosition()));
            }

            // 마커의 order을 상단으로 옮긴다.
            mSelectedMarker.showInfoWindow();
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
            mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener()
            {
                @Override
                public void onCameraIdle()
                {
                    mGoogleMap.setOnCameraIdleListener(mClusterManager);

                    if (isFinishing() == true)
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
            mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener()
            {
                @Override
                public void onCameraIdle()
                {
                    mGoogleMap.setOnCameraIdleListener(mClusterManager);

                    if (isFinishing() == true)
                    {
                        return;
                    }

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, Util.dpToPx(mBaseActivity, 50));
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
        if (isFinishing() == true)
        {
            return;
        }

        mGoogleMap.setOnCameraIdleListener(mClusterManager);

        if (hotelCount == 1)
        {
            CameraPosition cp = new CameraPosition.Builder().target(bounds.getCenter()).zoom(14.0f).build();
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
        } else
        {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, Util.dpToPx(mBaseActivity, 50));
            mGoogleMap.moveCamera(cameraUpdate);
        }

        mClusterManager.cluster();
    }

    /**
     * 같은 영역에 있는 호텔들을 찾아낸다.
     *
     * @param hotelArrayList
     * @return
     */
    private ArrayList<PlaceViewItem> searchDuplicateLocateion(List<PlaceViewItem> hotelArrayList)
    {
        ArrayList<PlaceViewItem> arrangeList = new ArrayList<>(hotelArrayList);

        int size = arrangeList.size();
        PlaceViewItem placeViewItem;

        // 섹션 정보와 솔드 아웃인 경우 목록에서 제거 시킨다.
        for (int i = size - 1; i >= 0; i--)
        {
            placeViewItem = arrangeList.get(i);

            if (placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
            {
                arrangeList.remove(i);
            } else
            {
                if (placeViewItem.<Place>getItem().isSoldOut == true)
                {
                    arrangeList.remove(i);
                }
            }
        }

        // 중복된 위치에 있는 호텔들은 위해서 소팅한다.
        Comparator<PlaceViewItem> comparator = new Comparator<PlaceViewItem>()
        {
            final LatLng latlng = new LatLng(37.23945, 131.8689);

            public int compare(PlaceViewItem placeViewItem1, PlaceViewItem placeViewItem2)
            {
                Place item01 = placeViewItem1.getItem();
                Place item02 = placeViewItem2.getItem();

                float[] results1 = new float[3];
                Location.distanceBetween(latlng.latitude, latlng.longitude, item01.latitude, item01.longitude, results1);

                float[] results2 = new float[3];
                Location.distanceBetween(latlng.latitude, latlng.longitude, item02.latitude, item02.longitude, results2);

                return Float.compare(results1[0], results2[0]);
            }
        };

        Collections.sort(arrangeList, comparator);

        size = arrangeList.size();

        // 중복된 호텔들은 위치를 수정하도록 한다.
        if (size > 1)
        {
            Place item01;
            Place item02;
            double duplicateCount = 0.000001d;

            for (int i = size - 1; i > 0; i--)
            {
                item01 = arrangeList.get(i).getItem();
                item02 = arrangeList.get(i - 1).getItem();

                if (item01.latitude == item02.latitude && item01.longitude == item02.longitude)
                {
                    // 위치를 살짝 수정한다.
                    item01.latitude += duplicateCount;
                    duplicateCount += 0.000001d;
                } else
                {
                    duplicateCount = 0.000001d;
                }
            }
        }

        return arrangeList;
    }

    private void searchMyLocation()
    {
        if (isFinishing() == true)
        {
            return;
        }

        DailyLocationFactory.getInstance(mBaseActivity).startLocationMeasure(this, mMyLocationView, new DailyLocationFactory.LocationListenerEx()
        {
            @Override
            public void onRequirePermission()
            {
                mBaseActivity.unLockUI();

                Intent intent = PermissionManagerActivity.newInstance(mBaseActivity, PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
                startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER);
            }

            @Override
            public void onFailed()
            {
                mBaseActivity.unLockUI();
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
                mBaseActivity.unLockUI();

                // Fragment가 added가 되지 않은 상태에서 터치가 될경우.
                if (isAdded() == false)
                {
                    return;
                }

                // 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
                DailyLocationFactory.getInstance(mBaseActivity).stopLocationMeasure();

                mBaseActivity.showSimpleDialog(getString(R.string.dialog_title_used_gps), getString(R.string.dialog_msg_used_gps), getString(R.string.dialog_btn_text_dosetting), getString(R.string.dialog_btn_text_cancel), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mBaseActivity.startActivityForResult(intent, Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION);
                    }
                }, null, true);
            }

            @Override
            public void onLocationChanged(Location location)
            {
                mBaseActivity.unLockUI();

                DailyLocationFactory.getInstance(mBaseActivity).stopLocationMeasure();

                if (mMyLocationMarkerOptions == null)
                {
                    mMyLocationMarkerOptions = new MarkerOptions();
                    mMyLocationMarkerOptions.icon(new MyLocationMarker(mBaseActivity).makeIcon());
                    mMyLocationMarkerOptions.anchor(0.5f, 0.5f);
                }

                if (mMyLocationMarker != null)
                {
                    mMyLocationMarker.remove();
                }

                mMyLocationMarkerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
                mMyLocationMarker = mGoogleMap.addMarker(mMyLocationMarkerOptions);

                CameraPosition cameraPosition = new CameraPosition.Builder().target(mMyLocationMarkerOptions.getPosition()).zoom(13f).build();

                if (Util.isOverAPI21() == true)
                {
                    mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener()
                    {
                        @Override
                        public void onCameraIdle()
                        {
                            mGoogleMap.setOnCameraIdleListener(mClusterManager);
                        }
                    });
                    mGoogleMap.setOnCameraIdleListener(null);
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } else
                {
                    mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });
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

    private PlaceMapViewPagerAdapter.OnPlaceMapViewPagerAdapterListener mOnPlaceMapViewPagerAdapterListener = new PlaceMapViewPagerAdapter.OnPlaceMapViewPagerAdapterListener()
    {
        @Override
        public void onInformationClick(Place place)
        {
            if (place == null)
            {
                return;
            }

            for (PlaceViewItem placeViewItem : mPlaceViewItemList)
            {
                if (placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
                {
                    continue;
                }

                if (place.equals(placeViewItem.getItem()) == true)
                {
                    mSelectedPlaceViewItem = placeViewItem;

                    if (mOnPlaceListMapFragmentListener != null)
                    {

                        mSelectedPlaceViewItem = placeViewItem;
                        mOnPlaceListMapFragmentListener.onInformationClick(placeViewItem);
                    }
                    break;
                }
            }
        }

        @Override
        public void onCloseClick()
        {
            if (mOnMapClickListener != null)
            {
                mOnMapClickListener.onMapClick(null);
            }
        }
    };

    private GoogleMap.OnMarkerClickListener mOnMarkerClickListener = new GoogleMap.OnMarkerClickListener()
    {
        @Override
        public boolean onMarkerClick(Marker marker)
        {
            PlaceListMapFragment.this.onMarkerClick(marker.getPosition());

            onAnalyticsMarkerClick(mPlaceViewItemViewPagerList.get(0).<Place>getItem().name);

            return true;
        }
    };

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

            Place place = placeViewItem.getItem();

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
            if (mGoogleMap == null)
            {
                return;
            }

            Intent intent = PermissionManagerActivity.newInstance(getContext(), PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
            mBaseActivity.startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER);
        }
    };

    private GoogleMap.OnMapClickListener mOnMapClickListener = new GoogleMap.OnMapClickListener()
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
                hidePlaceDetailAnimation();
            }
        }
    };

    private PlaceClusterRenderer.OnClusterRenderedListener mOnClusterRenderedListener = new PlaceClusterRenderer.OnClusterRenderedListener()
    {
        @Override
        public void onClusterRenderedListener(PlaceClusterRenderer.Renderer renderer)
        {
            if (renderer == PlaceClusterRenderer.Renderer.CLUSTER)
            {
                mOnMapClickListener.onMapClick(null);
            }
        }
    };
}
