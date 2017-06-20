package com.daily.dailyhotel.screen.home.stay.outbound.list.map;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.entity.StayOutboundClusterItem;
import com.daily.dailyhotel.entity.StayOutboundClusterRenderer;
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
import com.twoheart.dailyhotel.model.PlaceRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class StayOutboundMapFragment extends com.google.android.gms.maps.SupportMapFragment//
    implements OnMapReadyCallback, GoogleMap.OnMapClickListener//
    , ClusterManager.OnClusterClickListener<StayOutboundClusterItem>, ClusterManager.OnClusterItemClickListener<StayOutboundClusterItem>
{
    private GoogleMap mGoogleMap;

    private ImageView mMyLocationView;
    private Drawable mMyLocationDrawable;

    private Marker mMyLocationMarker, mSelectedMarker;
    private ClusterManager mClusterManager;
    private StayOutboundClusterRenderer mClusterRenderer;

    // 특별히 많은 데이터를 관리하기 때문에 넣어주었다.
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private OnEventListener mOnEventListener;

    public interface OnEventListener
    {
        void onMapReady();

        void onMarkerClick(StayOutbound stayOutbound);

        void onMarkersCompleted();

        void onMapClick();

        void onMyLocationClick();
    }

    public StayOutboundMapFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle)
    {
        View view = super.onCreateView(layoutInflater, viewGroup, bundle);

        getMapAsync(this);

        return view;
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();

        System.gc();
    }

    @Override
    public void onDestroyView()
    {
        if (mGoogleMap != null)
        {
            mGoogleMap.stopAnimation();
            mGoogleMap.clear();
        }

        if (mCompositeDisposable != null)
        {
            mCompositeDisposable.clear();
        }

        if (mSelectedMarker != null)
        {
            mSelectedMarker.remove();
            mSelectedMarker = null;
        }

        if (mMyLocationMarker != null)
        {
            mMyLocationMarker.remove();
            mMyLocationMarker = null;
        }

        super.onDestroyView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        if (googleMap == null || isFinishing() == true)
        {
            return;
        }

        mGoogleMap = googleMap;

        //                mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.setOnMapClickListener(this);

        relocationMyLocation();
        relocationZoomControl();

        mClusterManager = new ClusterManager<>(getContext(), mGoogleMap);
        mClusterRenderer = new StayOutboundClusterRenderer(getContext(), mGoogleMap, mClusterManager);
        mClusterRenderer.setOnClusterRenderedListener(new StayOutboundClusterRenderer.OnClusterRenderedListener()
        {
            @Override
            public void onClusterRenderedListener(StayOutboundClusterRenderer.Renderer renderer)
            {
                if (renderer == StayOutboundClusterRenderer.Renderer.CLUSTER)
                {
                    onMapClick(null);
                }
            }
        });

        mClusterManager.setRenderer(mClusterRenderer);
        mClusterManager.setAlgorithm(new NonHierarchicalDistanceBasedAlgorithm<StayOutboundClusterItem>());

        mGoogleMap.setInfoWindowAdapter(new MapWindowAdapter(getContext()));

        // 서울이 아니고 상세 지역 정보가 아닌 경우..지역별 중심값으로 이동.
        LatLng latlng = new LatLng(35.856899430657805, 127.73446206003428);
        CameraPosition cp = new CameraPosition.Builder().target(latlng).zoom(6.791876f).build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));

        if (mOnEventListener != null)
        {
            mOnEventListener.onMapReady();
        }
    }

    @Override
    public void onMapClick(LatLng latLng)
    {
        if (mSelectedMarker != null)
        {
            mSelectedMarker.setVisible(false);
            mSelectedMarker.setTag(null);
        }

        if (mOnEventListener != null)
        {
            mOnEventListener.onMapClick();
        }
    }

    @Override
    public boolean onClusterClick(Cluster<StayOutboundClusterItem> cluster)
    {
        if (isFinishing() == true)
        {
            return false;
        }

        mCompositeDisposable.add(Observable.just(cluster).subscribeOn(Schedulers.io()).map(new Function<Cluster<StayOutboundClusterItem>, LatLngBounds.Builder>()
        {
            @Override
            public LatLngBounds.Builder apply(@NonNull Cluster<StayOutboundClusterItem> stayOutboundClusterItemCluster) throws Exception
            {
                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

                for (StayOutboundClusterItem stayOutboundClusterItem : cluster.getItems())
                {
                    boundsBuilder.include(stayOutboundClusterItem.getPosition());
                }

                return boundsBuilder;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<LatLngBounds.Builder>()
        {
            @Override
            public void accept(@NonNull LatLngBounds.Builder builder) throws Exception
            {
                directCameraSetting(builder.build(), cluster.getSize());
            }
        }));

        return true;
    }

    @Override
    public boolean onClusterItemClick(StayOutboundClusterItem clusterItem, Marker marker)
    {
        if (isFinishing() == true)
        {
            return false;
        }

        onMarkerClick(getContext(), clusterItem, marker);

        return true;
    }

    public void setOnEventListener(OnEventListener listener)
    {
        mOnEventListener = listener;
    }

    public void setStayOutboundList(List<StayOutbound> stayOutboundList)
    {
        if (mGoogleMap == null || stayOutboundList == null || stayOutboundList.size() == 0)
        {
            return;
        }

        if (mSelectedMarker == null)
        {
            makeMarker(stayOutboundList, null, true);
        } else
        {
            makeMarker(stayOutboundList, (StayOutbound) mSelectedMarker.getTag(), true);
        }
    }

    public void setSelectedMarker(StayOutbound stayOutbound)
    {
        if (mClusterManager == null || stayOutbound == null)
        {
            return;
        }

        for (Marker marker : mClusterManager.getMarkerCollection().getMarkers())
        {
            LatLng latLng = marker.getPosition();

            if (stayOutbound.latitude == latLng.latitude && stayOutbound.longitude == latLng.longitude)
            {
                PlaceRenderer placeRenderer = new PlaceRenderer(getContext(), stayOutbound.nightlyRate, R.drawable.bg_hotel_price_special1);
                BitmapDescriptor icon = placeRenderer.getBitmap(true);

                if (icon == null)
                {
                    mSelectedMarker.setVisible(false);
                } else
                {
                    mSelectedMarker.setVisible(true);
                    mSelectedMarker.setPosition(marker.getPosition());
                    mSelectedMarker.setIcon(icon);
                }

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

                mSelectedMarker.setTag(stayOutbound);

                // 마커의 order을 상단으로 옮긴다.
                mSelectedMarker.showInfoWindow();
                break;
            }
        }
    }

    public void setMyLocation(LatLng latLng, boolean isVisibleMarker)
    {
        if (mGoogleMap == null || latLng == null)
        {
            return;
        }

        if (mMyLocationMarker != null)
        {
            mMyLocationMarker.remove();
            mMyLocationMarker = null;
        }

        mMyLocationMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng)//
            .icon(new MyLocationMarker(getContext()).makeIcon()).visible(isVisibleMarker).anchor(0.5f, 0.5f).zIndex(1.0f));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(13f).build();

        if (VersionUtils.isOverAPI21() == true)
        {
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else
        {
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void hideSelectedMarker()
    {
        if (mSelectedMarker == null)
        {
            return;
        }

        mSelectedMarker.setVisible(false);
        mSelectedMarker.setTag(null);
    }

    public Observable<Long> getLocationAnimation()
    {
        return Observable.interval(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<Long>()
        {
            @Override
            public void accept(@NonNull Long time) throws Exception
            {
                Drawable wrapDrawable = DrawableCompat.wrap(mMyLocationDrawable);

                if (time % 2 == 0)
                {
                    wrapDrawable.setColorFilter(mMyLocationView.getContext().getResources().getColor(R.color.dh_theme_color), PorterDuff.Mode.MULTIPLY);
                } else
                {
                    DrawableCompat.clearColorFilter(wrapDrawable);
                }
            }
        }).doOnDispose(new Action()
        {
            @Override
            public void run() throws Exception
            {
                Drawable wrapDrawable = DrawableCompat.wrap(mMyLocationDrawable);
                wrapDrawable.clearColorFilter();
            }
        }).doOnComplete(new Action()
        {
            @Override
            public void run() throws Exception
            {
                Drawable wrapDrawable = DrawableCompat.wrap(mMyLocationDrawable);
                wrapDrawable.clearColorFilter();
            }
        });
    }

    /**
     * 추후 UI추가 필요 구글맵 버전이 바뀌면 문제가 될수도 있음.
     */
    private void relocationZoomControl()
    {
        if (isFinishing() == true || getView() == null)
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

            zoomControl.setPadding(zoomControl.getPaddingLeft(), ScreenUtils.dpToPx(getContext(), 50), zoomControl.getPaddingRight(), zoomControl.getPaddingBottom());
            zoomControl.setLayoutParams(params);
        }
    }

    private void relocationMyLocation()
    {
        if (isFinishing() == true || getView() == null)
        {
            return;
        }

        mMyLocationView = (ImageView) getView().findViewById(0x2);

        if (mMyLocationView != null)
        {
            mMyLocationView.setVisibility(View.VISIBLE);
            mMyLocationView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mOnEventListener != null)
                    {
                        mOnEventListener.onMyLocationClick();
                    }
                }
            });

            mMyLocationDrawable = mMyLocationView.getDrawable();
        }
    }

    private void makeMarker(List<StayOutbound> stayOutboundList, StayOutbound selectedStayOutbound, boolean refreshAll)
    {
        if (isFinishing() == true || mGoogleMap == null)
        {
            return;
        }

        final LatLng myLatLng;

        if (mMyLocationMarker != null)
        {
            myLatLng = mMyLocationMarker.getPosition();
        } else
        {
            myLatLng = null;
        }

        mGoogleMap.clear();

        if (stayOutboundList == null || stayOutboundList.size() == 0)
        {
            return;
        }

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        // 나의 위치 마커를 생성시켜 놓는다.
        if (myLatLng != null)
        {
            setMyLocation(myLatLng, true);

            boundsBuilder.include(myLatLng);
        }

        // 선택된 마커는 미리 임시로 넣는다.
        mSelectedMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).visible(false).anchor(0.0f, 1.0f));
        mSelectedMarker.setTag(null);

        mCompositeDisposable.add(Observable.just(stayOutboundList).subscribeOn(Schedulers.io()).map(new Function<List<StayOutbound>, Integer>()
        {
            @Override
            public Integer apply(@NonNull List<StayOutbound> stayOutboundList) throws Exception
            {
                List<StayOutbound> stayOutboundArrangeList = reLocationDuplicateStayOutbound(stayOutboundList);
                List<StayOutboundClusterItem> stayOutboundClusterItemList = new ArrayList<>(stayOutboundArrangeList.size());

                for (StayOutbound stayOutbound : stayOutboundArrangeList)
                {
                    StayOutboundClusterItem stayOutboundClusterItem = new StayOutboundClusterItem(stayOutbound);

                    // 시작시에 전체 영역을 계산하여 화면에 보일수 있도록 한다.
                    boundsBuilder.include(stayOutboundClusterItem.getPosition());

                    // 기존의 마커 정보 창을 보여준다.
                    if (refreshAll == false && selectedStayOutbound != null//
                        && selectedStayOutbound.latitude == stayOutbound.latitude && selectedStayOutbound.longitude == stayOutbound.longitude)
                    {
                        mClusterRenderer.setSelectedClusterItem(stayOutboundClusterItem);
                        mClusterRenderer.setSelectedClusterItemListener(new StayOutboundClusterRenderer.OnSelectedClusterItemListener()
                        {
                            @Override
                            public void onSelectedClusterItemListener(Marker marker)
                            {
                                if (marker != null)
                                {
                                    mClusterManager.onMarkerClick(marker);
                                }
                            }
                        });
                    }

                    stayOutboundClusterItemList.add(stayOutboundClusterItem);
                }

                mClusterManager.clearItems();
                mClusterManager.addItems(stayOutboundClusterItemList);

                return stayOutboundClusterItemList.size();
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>()
        {
            @Override
            public void accept(@NonNull Integer size) throws Exception
            {
                if (refreshAll == true)
                {
                    try
                    {
                        moveCameraBounds(boundsBuilder.build(), size + (myLatLng == null ? 0 : 1));
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                } else
                {
                    mGoogleMap.setOnCameraIdleListener(mClusterManager);
                    mClusterManager.cluster();
                }

                mGoogleMap.setOnMarkerClickListener(mClusterManager);
                mClusterManager.setOnClusterClickListener(StayOutboundMapFragment.this);
                mClusterManager.setOnClusterItemClickListener(StayOutboundMapFragment.this);

                if (mOnEventListener != null)
                {
                    mOnEventListener.onMarkersCompleted();
                }
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception
            {
                if (mOnEventListener != null)
                {
                    mOnEventListener.onMarkersCompleted();
                }
            }
        }));
    }

    /**
     * 같은 영역에 있는 호텔들을 찾아낸다.
     *
     * @param stayOutboundList
     * @return
     */
    private List<StayOutbound> reLocationDuplicateStayOutbound(List<StayOutbound> stayOutboundList)
    {
        List<StayOutbound> stayOutboundArrangeList = new ArrayList<>();

        if (stayOutboundList == null || stayOutboundList.size() == 0)
        {
            return stayOutboundArrangeList;
        }

        stayOutboundArrangeList.addAll(stayOutboundList);

        int size = stayOutboundArrangeList.size();

        // 중복된 위치에 있는 호텔들은 위해서 소팅한다.
        Comparator<StayOutbound> comparator = new Comparator<StayOutbound>()
        {
            final LatLng latlng = new LatLng(37.23945, 131.8689);

            public int compare(StayOutbound stayOutbound1, StayOutbound stayOutbound2)
            {
                float[] results1 = new float[3];
                Location.distanceBetween(latlng.latitude, latlng.longitude, stayOutbound1.latitude, stayOutbound1.longitude, results1);

                float[] results2 = new float[3];
                Location.distanceBetween(latlng.latitude, latlng.longitude, stayOutbound2.latitude, stayOutbound2.longitude, results2);

                return Float.compare(results1[0], results2[0]);
            }
        };

        Collections.sort(stayOutboundArrangeList, comparator);

        // 중복된 호텔들은 위치를 수정하도록 한다.
        StayOutbound stayOutbound1;
        StayOutbound stayOutbound2;
        double duplicateCount = 0.000001d;

        for (int i = size - 1; i > 0; i--)
        {
            stayOutbound1 = stayOutboundArrangeList.get(i);
            stayOutbound2 = stayOutboundArrangeList.get(i - 1);

            if (stayOutbound1.latitude == stayOutbound2.latitude && stayOutbound1.longitude == stayOutbound2.longitude)
            {
                // 위치를 살짝 수정한다.
                stayOutbound1.latitude += duplicateCount;
                duplicateCount += 0.000001d;
            } else
            {
                duplicateCount = 0.000001d;
            }
        }

        return stayOutboundArrangeList;
    }

    private void onMarkerClick(Context context, StayOutboundClusterItem stayOutboundClusterItem, Marker marker)
    {
        if (context == null || stayOutboundClusterItem == null || marker == null)
        {
            return;
        }

        PlaceRenderer placeRenderer = new PlaceRenderer(context, stayOutboundClusterItem.getStayOutbound().nightlyRate, R.drawable.bg_hotel_price_special1);
        BitmapDescriptor icon = placeRenderer.getBitmap(true);

        if (icon == null)
        {
            mSelectedMarker.setVisible(false);
        } else
        {
            mSelectedMarker.setVisible(true);
            mSelectedMarker.setPosition(marker.getPosition());
            mSelectedMarker.setIcon(icon);
        }

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

        mSelectedMarker.setTag(stayOutboundClusterItem.getStayOutbound());

        // 마커의 order을 상단으로 옮긴다.
        mSelectedMarker.showInfoWindow();

        if (mOnEventListener != null)
        {
            mOnEventListener.onMarkerClick(stayOutboundClusterItem.getStayOutbound());
        }
    }

    private void moveCameraBounds(final LatLngBounds bounds, int placeCount)
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

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, ScreenUtils.dpToPx(getContext(), 50));
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
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, ScreenUtils.dpToPx(getContext(), 50));
            mGoogleMap.moveCamera(cameraUpdate);
        }

        mClusterManager.cluster();
    }

    private boolean isFinishing()
    {
        return (isAdded() == false || getActivity() == null || getActivity().isFinishing() == true);
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
}
