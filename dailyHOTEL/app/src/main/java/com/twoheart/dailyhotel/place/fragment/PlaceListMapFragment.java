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
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.google.android.gms.common.api.ResolvableApiException;
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
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.place.adapter.PlaceMapViewPagerAdapter;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.common.LoadingDialog;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyLocationFactory;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyOverScrollViewPager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class PlaceListMapFragment extends com.google.android.gms.maps.SupportMapFragment implements ClusterManager.OnClusterClickListener<PlaceClusterItem>, ClusterManager.OnClusterItemClickListener<PlaceClusterItem>
{
    private static final int ANIMATION_DELAY = 200;
    private static final int VIEWPAGER_HEIGHT_DP = 125;
    private static final int VIEWPAGER_TOP_PADDING_DP = 10;
    private static final int VIEWPAGER_OTHER_PADDING_DP = 15;
    private static final int VIEWPAGER_PAGE_MARGIN_DP = 5;

    GoogleMap mGoogleMap;
    protected List<PlaceViewItem> mPlaceViewItemList; // 선택된 호텔을 위한 리스트
    List<PlaceViewItem> mPlaceViewItemViewPagerList; // ViewPager을 위한 리스트
    LoadingDialog mLoadingDialog;
    MarkerOptions mMyLocationMarkerOptions;
    Marker mMyLocationMarker;
    private View mBottomOptionLayout;

    protected boolean mIsCreateView = false;
    private boolean mCallMakeMarker = false;

    private PlaceBookingDay mPlaceBookingDay;
    PlaceViewItem mSelectedPlaceViewItem;
    protected boolean mIsOpenMarker; // 마커를 선택한 경우.

    ClusterManager<PlaceClusterItem> mClusterManager;
    PlaceClusterRenderer mPlaceClusterRenderer;
    Marker mSelectedMarker;
    ImageView mMyLocationView;
    DailyOverScrollViewPager mViewPager;

    protected BaseActivity mBaseActivity;
    protected OnPlaceListMapFragmentListener mOnPlaceListMapFragmentListener;
    protected PlaceMapViewPagerAdapter mPlaceMapViewPagerAdapter;

    Constants.ANIMATION_STATUS mAnimationStatus = Constants.ANIMATION_STATUS.SHOW_END;
    Constants.ANIMATION_STATE mAnimationState = Constants.ANIMATION_STATE.END;
    ValueAnimator mValueAnimator;

    DailyLocationFactory mDailyLocationFactory;

    private boolean mRewardEnabled;

    public interface OnPlaceListMapFragmentListener
    {
        void onInformationClick(View view, PlaceViewItem placeViewItem);

        void onWishClick(int position, PlaceViewItem placeViewItem);
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

                //                mGoogleMap.setMyLocationEnabled(true);
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

                if (mMyLocationMarkerOptions != null)
                {
                    try
                    {
                        mMyLocationMarker = mGoogleMap.addMarker(mMyLocationMarkerOptions);
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }
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

    void addViewPager(BaseActivity baseActivity, ViewGroup viewGroup)
    {
        // Add Stay Info ViewPager
        if (mViewPager != null)
        {
            return;
        }

        int paddingOther = ScreenUtils.dpToPx(baseActivity, VIEWPAGER_OTHER_PADDING_DP);
        int paddingTop = ScreenUtils.dpToPx(baseActivity, VIEWPAGER_TOP_PADDING_DP);

        mViewPager = new DailyOverScrollViewPager(baseActivity);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setClipToPadding(false);
        mViewPager.setPageMargin(ScreenUtils.dpToPx(baseActivity, VIEWPAGER_PAGE_MARGIN_DP));
        mViewPager.setPadding(paddingOther, paddingTop, paddingOther, paddingOther);
        mViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(baseActivity, VIEWPAGER_HEIGHT_DP));
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);

        layoutParams.gravity = Gravity.BOTTOM;

        mViewPager.setLayoutParams(layoutParams);
        mViewPager.setVisibility(View.INVISIBLE);

        if (Util.isUsedMultiTransition() == true)
        {
            mViewPager.setTransitionGroup(true);
        }

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

        if (mDailyLocationFactory != null)
        {
            mDailyLocationFactory.stopLocationMeasure();
        }

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

    public PlaceViewItem getItem(int position)
    {
        return mPlaceViewItemViewPagerList == null ? null : mPlaceViewItemViewPagerList.get(position);
    }

    public int getPlaceViewItemListSize()
    {
        if (mPlaceViewItemList == null)
        {
            return 0;
        }

        return mPlaceViewItemList.size();
    }

    public void setPlaceViewItemList(PlaceBookingDay placeBookingDay, List<PlaceViewItem> arrayList, boolean isRefreshAll, boolean rewardEnabled)
    {
        mPlaceBookingDay = placeBookingDay;
        mPlaceViewItemList = arrayList;
        mRewardEnabled = rewardEnabled;

        // Marker 만들기.
        if (mIsCreateView == true)
        {
            makeMarker(isRefreshAll);
        }
    }

    public void setMyLocation(Location location, boolean isVisibleMarker)
    {
        if (location == null || mBaseActivity == null)
        {
            return;
        }

        if (mMyLocationMarkerOptions == null)
        {
            mMyLocationMarkerOptions = new MarkerOptions();
            mMyLocationMarkerOptions.icon(new MyLocationMarker(mBaseActivity).makeIcon());
            mMyLocationMarkerOptions.anchor(0.5f, 0.5f);
            mMyLocationMarkerOptions.zIndex(1.0f);
        }

        if (mMyLocationMarker != null)
        {
            mMyLocationMarker.remove();
            mMyLocationMarker = null;
        }

        mMyLocationMarkerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
        mMyLocationMarkerOptions.visible(isVisibleMarker);

        if (mGoogleMap != null)
        {
            mMyLocationMarker = mGoogleMap.addMarker(mMyLocationMarkerOptions);
        }
    }

    public void setOnPlaceListMapFragment(OnPlaceListMapFragmentListener listener)
    {
        mOnPlaceListMapFragmentListener = listener;
    }

    public boolean isShowPlaceInformation()
    {
        return mViewPager != null && mViewPager.getVisibility() == View.VISIBLE;
    }

    /**
     * 추후 UI추가 필요 구글맵 버전이 바뀌면 문제가 될수도 있음.
     */
    void relocationZoomControl()
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

            zoomControl.setPadding(zoomControl.getPaddingLeft(), ScreenUtils.dpToPx(mBaseActivity, 50), zoomControl.getPaddingRight(), zoomControl.getPaddingBottom());
            zoomControl.setLayoutParams(params);
        }
    }

    void relocationMyLocation()
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
            mIsOpenMarker = false;

            mOnPlaceMapViewPagerAdapterListener.onCloseClick();
        }

        double latitude = 0.0;
        double longitude = 0.0;
        int count = 0;
        boolean isOpenMarker = false;

        if (mIsOpenMarker == true && mSelectedPlaceViewItem != null)
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
        mPlaceViewItemViewPagerList = searchDuplicateLocation(mPlaceViewItemList);

        mClusterManager.clearItems();
        mGoogleMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);

        for (PlaceViewItem placeViewItem : mPlaceViewItemViewPagerList)
        {
            Place place = placeViewItem.getItem();

            count++;

            PlaceClusterItem placeClusterItem = new PlaceClusterItem(place);
            mClusterManager.addItem(placeClusterItem);

            LatLng latlng = new LatLng(place.latitude, place.longitude);
            builder.include(latlng);

            // 기존의 마커 정보 창을 보여준다.
            if (mIsOpenMarker == true)
            {
                if (latitude == place.latitude && longitude == place.longitude)
                {
                    isOpenMarker = true;

                    mPlaceClusterRenderer.setSelectedClusterItem(placeClusterItem);
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

        mIsOpenMarker = false;

        if (isRefreshAll == true)
        {
            try
            {
                if (mMyLocationMarker != null)
                {
                    builder.include(mMyLocationMarker.getPosition());
                    count++;
                }

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

    void onMarkerClick(final LatLng latlng)
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

        mPlaceMapViewPagerAdapter.setPlaceBookingDay(mPlaceBookingDay);
        mPlaceMapViewPagerAdapter.setRewardEnabled(mRewardEnabled);
        mPlaceMapViewPagerAdapter.setData(mPlaceViewItemViewPagerList);
        mViewPager.setAdapter(mPlaceMapViewPagerAdapter);
        mPlaceMapViewPagerAdapter.notifyDataSetChanged();

        mIsOpenMarker = true;

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

            if (VersionUtils.isOverAPI21() == true)
            {
                mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener()
                {
                    @Override
                    public void onCameraIdle()
                    {
                        mGoogleMap.setOnCameraIdleListener(mClusterManager);
                    }
                });
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

    public void notifyViewPagerDataSetChanged()
    {
        if (mPlaceMapViewPagerAdapter == null)
        {
            return;
        }

        mPlaceMapViewPagerAdapter.notifyDataSetChanged();
    }

    public void setMenuBarLayoutTranslationY(float dy)
    {
        if (isFinishing() == true || mBottomOptionLayout == null || mViewPager == null)
        {
            return;
        }

        mBottomOptionLayout.setTranslationY(dy - ScreenUtils.dpToPx(mBaseActivity, (VIEWPAGER_HEIGHT_DP - VIEWPAGER_OTHER_PADDING_DP)));
        mViewPager.setTranslationY(dy);
    }

    public void resetMenuBarLayoutTranslation()
    {
        if (isFinishing() == true || mBottomOptionLayout == null || mViewPager == null)
        {
            return;
        }

        mBottomOptionLayout.setTranslationY(0);

        mViewPager.setVisibility(View.INVISIBLE);
        mViewPager.setTranslationY(0);
    }

    public void clickMap()
    {
        if (mOnMapClickListener == null)
        {
            return;
        }

        mOnMapClickListener.onMapClick(null);
    }

    private void showPlaceDetailAnimation()
    {
        if (mAnimationState == Constants.ANIMATION_STATE.START && mAnimationStatus == Constants.ANIMATION_STATUS.SHOW)
        {
            return;
        }

        if (mValueAnimator != null && mValueAnimator.isRunning() == true)
        {
            mValueAnimator.cancel();
        }

        if (mViewPager.getVisibility() == View.VISIBLE)
        {
            return;
        }

        mValueAnimator = ValueAnimator.ofInt(0, 100);
        mValueAnimator.setDuration(ANIMATION_DELAY);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int value = (Integer) animation.getAnimatedValue();
                int height = ScreenUtils.dpToPx(mBaseActivity, (VIEWPAGER_HEIGHT_DP - VIEWPAGER_OTHER_PADDING_DP));
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
                mViewPager.setTranslationY(ScreenUtils.dpToPx(mBaseActivity, (VIEWPAGER_HEIGHT_DP - VIEWPAGER_OTHER_PADDING_DP)));

                mAnimationState = Constants.ANIMATION_STATE.START;
                mAnimationStatus = Constants.ANIMATION_STATUS.SHOW;
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (mValueAnimator != null)
                {
                    mValueAnimator.removeAllListeners();
                    mValueAnimator.removeAllUpdateListeners();
                    mValueAnimator = null;
                }

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

    void hidePlaceDetailAnimation()
    {
        if (mAnimationState == Constants.ANIMATION_STATE.START && mAnimationStatus == Constants.ANIMATION_STATUS.HIDE)
        {
            return;
        }

        if (mViewPager.getVisibility() != View.VISIBLE)
        {
            return;
        }

        if (mValueAnimator != null && mValueAnimator.isRunning() == true)
        {
            mValueAnimator.cancel();
        }

        mValueAnimator = ValueAnimator.ofInt(0, 100);
        mValueAnimator.setDuration(ANIMATION_DELAY);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int value = (Integer) animation.getAnimatedValue();
                int height = ScreenUtils.dpToPx(mBaseActivity, (VIEWPAGER_HEIGHT_DP - VIEWPAGER_OTHER_PADDING_DP));
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
                if (mValueAnimator != null)
                {
                    mValueAnimator.removeAllListeners();
                    mValueAnimator.removeAllUpdateListeners();
                    mValueAnimator = null;
                }

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

    void onMarkerTempClick(final LatLng latlng)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        mIsOpenMarker = true;

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

            if (VersionUtils.isOverAPI21() == true)
            {
                mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener()
                {
                    @Override
                    public void onCameraIdle()
                    {
                        mGoogleMap.setOnCameraIdleListener(mClusterManager);
                    }
                });
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

    private void cameraSetting(final LatLngBounds bounds, int placeCount)
    {
        if (placeCount <= 0)
        {
            return;
        } else if (placeCount == 1)
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

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(bounds.getCenter()).zoom(14.0f).build();
                    mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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

                    try
                    {
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, ScreenUtils.dpToPx(mBaseActivity, 50));
                        mGoogleMap.moveCamera(cameraUpdate);
                    } catch (Exception e)
                    {
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(bounds.getCenter()).zoom(10.0f).build();
                        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }

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
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, ScreenUtils.dpToPx(mBaseActivity, 50));
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
    private ArrayList<PlaceViewItem> searchDuplicateLocation(List<PlaceViewItem> hotelArrayList)
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

        if (mDailyLocationFactory == null)
        {
            mDailyLocationFactory = new DailyLocationFactory(getContext());
        }

        if (mDailyLocationFactory.measuringLocation() == true)
        {
            return;
        }

        mDailyLocationFactory.checkLocationMeasure(new DailyLocationFactory.OnCheckLocationListener()
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
            public void onProviderDisabled()
            {
                mBaseActivity.unLockUI();

                // Fragment가 added가 되지 않은 상태에서 터치가 될경우.
                if (isAdded() == false)
                {
                    return;
                }

                // 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
                mDailyLocationFactory.stopLocationMeasure();

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
            public void onProviderEnabled()
            {
                mDailyLocationFactory.startLocationMeasure(mMyLocationView, new DailyLocationFactory.OnLocationListener()
                {
                    @Override
                    public void onFailed()
                    {
                        mBaseActivity.unLockUI();
                    }

                    @Override
                    public void onAlreadyRun()
                    {

                    }

                    @Override
                    public void onLocationChanged(Location location)
                    {
                        mBaseActivity.unLockUI();

                        mDailyLocationFactory.stopLocationMeasure();

                        setMyLocation(location, true);
                        moveCameraPosition(mMyLocationMarkerOptions.getPosition());
                    }

                    @Override
                    public void onCheckSetting(ResolvableApiException exception)
                    {
                        mBaseActivity.unLockUI();

                        try
                        {
                            exception.startResolutionForResult(getActivity(), Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION);
                        } catch (Exception e)
                        {

                        }
                    }
                });
            }
        });
    }

    void moveCameraPosition(LatLng latLng)
    {
        if (latLng == null)
        {
            return;
        }

        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(13f).build();

        if (VersionUtils.isOverAPI21() == true)
        {
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else
        {
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
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
        public void onInformationClick(View view, Place place)
        {
            if (place == null)
            {
                return;
            }

            for (PlaceViewItem placeViewItem : mPlaceViewItemViewPagerList)
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
                        mOnPlaceListMapFragmentListener.onInformationClick(view, placeViewItem);
                    }
                    break;
                }
            }
        }

        @Override
        public void onWishClick(int position, Place place)
        {
            if (place == null)
            {
                return;
            }

            for (PlaceViewItem placeViewItem : mPlaceViewItemViewPagerList)
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
                        mOnPlaceListMapFragmentListener.onWishClick(position, placeViewItem);
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

    GoogleMap.OnMapClickListener mOnMapClickListener = new GoogleMap.OnMapClickListener()
    {
        @Override
        public void onMapClick(LatLng arg0)
        {
            if (mSelectedMarker != null)
            {
                mSelectedMarker.setVisible(false);
            }

            mIsOpenMarker = false;
            mSelectedPlaceViewItem = null;

            if (mViewPager != null)
            {
                hidePlaceDetailAnimation();
            }
        }
    };

    PlaceClusterRenderer.OnClusterRenderedListener mOnClusterRenderedListener = new PlaceClusterRenderer.OnClusterRenderedListener()
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
