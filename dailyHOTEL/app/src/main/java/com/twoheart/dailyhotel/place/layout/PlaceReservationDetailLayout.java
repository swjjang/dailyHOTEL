package com.twoheart.dailyhotel.place.layout;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.location.Location;
import android.net.Uri;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyScrollView;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.Booking;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.MyLocationMarker;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.adapter.PlaceNameInfoWindowAdapter;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseBlurLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;

import java.util.Locale;

public abstract class PlaceReservationDetailLayout extends BaseBlurLayout implements View.OnClickListener
{
    protected DailyScrollView mScrollLayout;
    private View mPlaceInformationLayout;

    View mInputReviewVerticalLine, mMapExpandedView;
    private TextView mDeleteReservationView;
    private DailyTextView mInputReviewView;
    private DailyToolbarView mDailyToolbarView;

    // Map
    boolean mIsReadyMap;
    private RelativeLayout mGoogleMapLayout;
    FrameLayout mMapLayout;
    GoogleMap mGoogleMap;
    View mFakeMapLayout, mZoomControl;
    ImageView mMyLocationView;
    MarkerOptions mMyLocationMarkerOptions;
    Marker mMyLocationMarker, mPlaceLocationMarker;

    View mAddressLayout, mSearchMapsLayout;

    LatLng mCenterLatLng;

    protected abstract void initPlaceInformationLayout(Context context, View view, TodayDateTime todayDateTime, PlaceBookingDetail placeBookingDetail);

    protected abstract void initTimeInformationLayout(Context context, View view, PlaceBookingDetail placeBookingDetail);

    protected abstract void initGuestInformationLayout(Context context, View view, PlaceBookingDetail placeBookingDetail);

    protected abstract void initPaymentInformationLayout(Context context, View view, PlaceBookingDetail placeBookingDetail);

    protected abstract void initRefundPolicyLayout(Context context, View view, PlaceBookingDetail placeBookingDetail);

    protected abstract void onScrollChanged(ScrollView scrollView, int l, int t, int oldl, int oldt);

    public interface OnEventListener extends OnBaseEventListener // m
    {
        void onIssuingReceiptClick();

        void onMapClick(boolean isGoogleMap);

        void onViewDetailClick();

        void onViewMapClick();

        void onRefundClick();

        void onReviewClick(String reviewStatus);

        void showCallDialog();

        void showShareDialog();

        void onMyLocationClick();

        void onClipAddressClick();

        void onSearchMapClick();

        void onReleaseUiComponent();

        void onLoadingMap();

        void onDeleteReservationClick();
    }

    public PlaceReservationDetailLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbar(view);

        mGoogleMapLayout = view.findViewById(R.id.googleMapLayout);
        mScrollLayout = view.findViewById(R.id.scrollLayout);
        EdgeEffectColor.setEdgeGlowColor(mScrollLayout, mContext.getResources().getColor(R.color.default_over_scroll_edge));

        double width = ScreenUtils.getScreenWidth(mContext);
        double height = ScreenUtils.getRatioHeightType16x9(ScreenUtils.getScreenWidth(mContext));
        final float PLACE_INFORMATION_LAYOUT_RATIO = 0.65f;

        // Map 4 :2 비율 맞추기
        com.facebook.drawee.view.SimpleDraweeView mapImageView = mScrollLayout.findViewById(R.id.mapImageView);
        mapImageView.setOnClickListener(this);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mapImageView.getLayoutParams();
        layoutParams.width = (int) width;
        layoutParams.height = (int) height;

        mapImageView.setLayoutParams(layoutParams);

        mMapExpandedView = view.findViewById(R.id.mapExpandedView);

        if (Util.isInstallGooglePlayService(mContext) == false)
        {
            mGoogleMapLayout.setVisibility(View.GONE);
        } else
        {
            mGoogleMapLayout.setVisibility(View.VISIBLE);
        }

        mPlaceInformationLayout = mScrollLayout.findViewById(R.id.placeInformationLayout);
        RelativeLayout.LayoutParams placeInformationLayoutParams = (RelativeLayout.LayoutParams) mPlaceInformationLayout.getLayoutParams();
        placeInformationLayoutParams.topMargin = (int) (PLACE_INFORMATION_LAYOUT_RATIO * height);
        mPlaceInformationLayout.setLayoutParams(placeInformationLayoutParams);

        mPlaceInformationLayout.setVisibility(View.VISIBLE);

        mDeleteReservationView = view.findViewById(R.id.deleteReservationTextView);
        mDeleteReservationView.setPaintFlags(mDeleteReservationView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    private void initToolbar(View view)
    {
        mDailyToolbarView = view.findViewById(R.id.toolbarView);

        setReservationDetailToolbar();
    }

    public void initLayout(TodayDateTime todayDateTime, PlaceBookingDetail placeBookingDetail)
    {
        initHeaderInformationLayout(mContext, mScrollLayout, placeBookingDetail);
        initPlaceInformationLayout(mContext, mScrollLayout, todayDateTime, placeBookingDetail);
        initTimeInformationLayout(mContext, mScrollLayout, placeBookingDetail);
        initGuestInformationLayout(mContext, mScrollLayout, placeBookingDetail);
        initPaymentInformationLayout(mContext, mScrollLayout, placeBookingDetail);
        initRefundPolicyLayout(mContext, mScrollLayout, placeBookingDetail);
    }

    public void setDeleteReservationVisible(int bookingState)
    {
        if (mDeleteReservationView == null)
        {
            return;
        }

        if (Booking.BOOKING_STATE_CANCEL == bookingState)
        {
            mDeleteReservationView.setText(R.string.label_booking_cancel_detail_delete_reservation);
        } else
        {
            mDeleteReservationView.setText(R.string.label_booking_detail_delete_reservation);
        }

        if (Booking.BOOKING_STATE_AFTER_USE == bookingState || Booking.BOOKING_STATE_CANCEL == bookingState)
        {
            mDeleteReservationView.setVisibility(View.VISIBLE);
            mDeleteReservationView.setOnClickListener(this);

            LinearLayout.LayoutParams layoutParams = ((LinearLayout.LayoutParams) mDeleteReservationView.getLayoutParams());

            if (layoutParams.height != LinearLayout.LayoutParams.WRAP_CONTENT)
            {
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;

                final int DP_37 = ScreenUtils.dpToPx(mContext, 37);

                layoutParams.setMargins(0, DP_37, 0, 0);
                mDeleteReservationView.setPadding(0, 0, 0, DP_37);
                mDeleteReservationView.requestLayout();
            }
        } else
        {
            mDeleteReservationView.setVisibility(View.INVISIBLE);
            mDeleteReservationView.setOnClickListener(null);

            LinearLayout.LayoutParams layoutParams = ((LinearLayout.LayoutParams) mDeleteReservationView.getLayoutParams());

            final int DP_24 = ScreenUtils.dpToPx(mContext, 24);
            if (layoutParams.height != DP_24)
            {
                layoutParams.setMargins(0, 0, 0, 0);
                layoutParams.height = DP_24;
                mDeleteReservationView.setPadding(0, 0, 0, 0);
                mDeleteReservationView.requestLayout();
            }
        }
    }

    private void setReservationDetailToolbar()
    {
        if (mDailyToolbarView == null)
        {
            return;
        }

        mDailyToolbarView.setTitleText(mContext.getString(R.string.actionbar_title_booking_list_frag));
        mDailyToolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.finish();
            }
        });

        mDailyToolbarView.clearMenuItem();
        mDailyToolbarView.addMenuItem(DailyToolbarView.MenuItem.SHARE, null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).showShareDialog();
            }
        });

        mDailyToolbarView.addMenuItem(DailyToolbarView.MenuItem.HELP, null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).showCallDialog();
            }
        });
    }

    private void setReservationMapToolbar()
    {
        if (mDailyToolbarView == null)
        {
            return;
        }

        mDailyToolbarView.setTitleText(mContext.getString(R.string.frag_tab_map_title));
        mDailyToolbarView.clearMenuItem();
    }

    private void initHeaderInformationLayout(Context context, View view, PlaceBookingDetail placeBookingDetail)
    {
        if (view == null || placeBookingDetail == null)
        {
            return;
        }

        double width = ScreenUtils.getScreenWidth(context);
        double height = ScreenUtils.getRatioHeightType16x9(ScreenUtils.getScreenWidth(context));

        if (Util.isInstallGooglePlayService(context) == false)
        {
            initImageMapLayout(context, mScrollLayout, placeBookingDetail, (int) width, (int) height);
        } else
        {
            initGoogleMapLayout(context, mGoogleMapLayout, placeBookingDetail, (int) width, (int) height);
        }

        View viewDetailView = mPlaceInformationLayout.findViewById(R.id.viewDetailView);
        View viewMapView = mPlaceInformationLayout.findViewById(R.id.viewMapView);

        viewDetailView.setOnClickListener(this);
        viewMapView.setOnClickListener(this);

        TextView placeNameTextView = mPlaceInformationLayout.findViewById(R.id.placeNameTextView);
        placeNameTextView.setText(placeBookingDetail.placeName);

        initReviewButtonLayout(mPlaceInformationLayout, placeBookingDetail);
    }

    private void initImageMapLayout(Context context, View view, PlaceBookingDetail placeBookingDetail, int height, int width)
    {
        final double ratio = height / width;

        com.facebook.drawee.view.SimpleDraweeView mapImageView = view.findViewById(R.id.mapImageView);
        mapImageView.getHierarchy().setActualImageFocusPoint(new PointF(0.5f, 0.58f));

        if (width >= 720)
        {
            width = 720;
        }

        String size = String.format(Locale.KOREA, "%dx%d", width * 3 / 5, (int) (width * ratio * 5) / 7);
        String iconUrl = "http://img.dailyhotel.me/app_static/info_ic_map_large.png";
        String url = String.format(Locale.KOREA, "https://maps.googleapis.com/maps/api/staticmap?zoom=17&size=%s&markers=icon:%s|%s,%s&sensor=false&scale=2&format=png8&mobile=true&key=%s"//
            , size, iconUrl, placeBookingDetail.latitude, placeBookingDetail.longitude, Crypto.getUrlDecoderEx(Constants.GOOGLE_MAP_KEY));

        mapImageView.setImageURI(Uri.parse(url));
    }

    private void initGoogleMapLayout(Context context, View view, final PlaceBookingDetail placeBookingDetail, int width, int height)
    {
        if (context == null || view == null || placeBookingDetail == null)
        {
            return;
        }

        mAddressLayout = view.findViewById(R.id.addressLayout);
        mSearchMapsLayout = view.findViewById(R.id.searchMapsLayout);

        mAddressLayout.setVisibility(View.GONE);
        mSearchMapsLayout.setVisibility(View.GONE);

        TextView addressTextView = mAddressLayout.findViewById(R.id.addressTextView);
        addressTextView.setText(placeBookingDetail.address);

        View clipAddress = mAddressLayout.findViewById(R.id.copyAddressView);
        clipAddress.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onClipAddressClick();
            }
        });

        View searchMapView = mSearchMapsLayout.findViewById(R.id.searchMapView);
        searchMapView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onSearchMapClick();
            }
        });

        mMapLayout = view.findViewById(R.id.mapLayout);
        mFakeMapLayout = view.findViewById(R.id.fakeMapLayout);

        RelativeLayout.LayoutParams fakeMapLayoutParams = (RelativeLayout.LayoutParams) mFakeMapLayout.getLayoutParams();
        fakeMapLayoutParams.width = width;
        fakeMapLayoutParams.height = height;

        mFakeMapLayout.setLayoutParams(fakeMapLayoutParams);
        mFakeMapLayout.setTag(height);

        mScrollLayout.setOnScrollChangedListener(new DailyScrollView.OnScrollChangedListener()
        {
            @Override
            public void onScrollChanged(ScrollView scrollView, int l, int t, int oldl, int oldt)
            {
                mFakeMapLayout.setTranslationY(-t);

                PlaceReservationDetailLayout.this.onScrollChanged(scrollView, l, t, oldl, oldt);
            }
        });

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();

        ((BaseActivity) mContext).getSupportFragmentManager().beginTransaction().add(mMapLayout.getId(), mapFragment).commitAllowingStateLoss();

        mapFragment.getMapAsync(new OnMapReadyCallback()
        {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap)
            {
                mGoogleMap = googleMap;

                mGoogleMap.getUiSettings().setCompassEnabled(false);
                mGoogleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
                mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
                mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
                mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);
                mGoogleMap.getUiSettings().setZoomControlsEnabled(false);

                mGoogleMap.setMyLocationEnabled(false);

                relocationMyLocation(mMapLayout);
                relocationZoomControl(mMapLayout);
                addMarker(mGoogleMap, placeBookingDetail.latitude, placeBookingDetail.longitude, placeBookingDetail.placeName);

                mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback()
                {
                    @Override
                    public void onMapLoaded()
                    {
                        mIsReadyMap = true;

                        Projection projection = mGoogleMap.getProjection();

                        Point point = projection.toScreenLocation(new LatLng(placeBookingDetail.latitude, placeBookingDetail.longitude));
                        point.y += point.y - (mFakeMapLayout.getHeight() * 0.43);

                        mCenterLatLng = projection.fromScreenLocation(point);
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(mCenterLatLng), 200, null);
                    }
                });
            }
        });
    }

    private void initReviewButtonLayout(View view, PlaceBookingDetail placeBookingDetail)
    {
        if (view == null || placeBookingDetail == null)
        {
            return;
        }

        mInputReviewVerticalLine = view.findViewById(R.id.inputReviewVerticalLine);
        mInputReviewView = view.findViewById(R.id.inputReviewView);
        mInputReviewView.setOnClickListener(this);

        String reviewStatus = placeBookingDetail.reviewStatusType;
        updateReviewButtonLayout(reviewStatus);
    }

    public void updateReviewButtonLayout(String reviewStatus)
    {
        if (DailyTextUtils.isTextEmpty(reviewStatus) == true)
        {
            reviewStatus = PlaceBookingDetail.ReviewStatusType.NONE;
        }

        mInputReviewView.setTag(reviewStatus);

        if (PlaceBookingDetail.ReviewStatusType.ADDABLE.equalsIgnoreCase(reviewStatus) == true //
            || PlaceBookingDetail.ReviewStatusType.MODIFIABLE.equalsIgnoreCase(reviewStatus) == true)
        {
            mInputReviewVerticalLine.setVisibility(View.VISIBLE);
            mInputReviewView.setVisibility(View.VISIBLE);
            mInputReviewView.setDrawableVectorTint(R.color.default_background_c454545);
            mInputReviewView.setTextColor(mContext.getResources().getColor(R.color.default_text_c323232));
        } else if (PlaceBookingDetail.ReviewStatusType.COMPLETE.equalsIgnoreCase(reviewStatus) == true)
        {
            mInputReviewVerticalLine.setVisibility(View.VISIBLE);
            mInputReviewView.setVisibility(View.VISIBLE);
            mInputReviewView.setDrawableVectorTint(R.color.default_background_c454545_alpha_20);
            mInputReviewView.setTextColor(mContext.getResources().getColor(R.color.default_text_cc5c5c5));
            mInputReviewView.setText(R.string.label_booking_completed_input_review);
            mInputReviewView.setOnClickListener(null);
            mInputReviewView.setEnabled(false);
        } else
        {
            mInputReviewVerticalLine.setVisibility(View.GONE);
            mInputReviewView.setVisibility(View.GONE);
            mInputReviewView.setDrawableVectorTint(R.color.default_background_c454545);
            mInputReviewView.setTextColor(mContext.getResources().getColor(R.color.default_text_c323232));
        }
    }

    @Override
    public void onClick(final View v)
    {
        switch (v.getId())
        {
            case R.id.mapImageView:
            {
                if (mGoogleMapLayout.getVisibility() == View.VISIBLE)
                {
                    if (mIsReadyMap == true)
                    {
                        ((OnEventListener) mOnEventListener).onMapClick(true);
                    } else
                    {
                        ((OnEventListener) mOnEventListener).onLoadingMap();
                    }
                } else
                {
                    ((OnEventListener) mOnEventListener).onMapClick(false);
                }
                break;
            }

            case R.id.viewDetailView:
            {
                ((OnEventListener) mOnEventListener).onViewDetailClick();
                break;
            }

            case R.id.viewMapView:
            {
                ((OnEventListener) mOnEventListener).onViewMapClick();
                break;
            }

            case R.id.refundButtonLayout:
            {
                ((OnEventListener) mOnEventListener).onRefundClick();
                break;
            }

            case R.id.inputReviewView:
            {
                if (v.getTag() == null)
                {
                    return;
                }

                if ((v.getTag() instanceof String) == false)
                {
                    return;
                }

                String reviewStatus = (String) v.getTag();

                ((OnEventListener) mOnEventListener).onReviewClick(reviewStatus);
                break;
            }

            case R.id.deleteReservationTextView:
                ((OnEventListener) mOnEventListener).onDeleteReservationClick();
                break;
        }
    }

    public boolean isExpandedMap()
    {
        return mSearchMapsLayout != null && mSearchMapsLayout.getVisibility() != View.GONE;
    }

    public void expandMap(double latitude, double longitude)
    {
        setReservationMapToolbar();

        mScrollLayout.setScrollingEnabled(false);
        mScrollLayout.scrollTo(0, 0);

        mSearchMapsLayout.setVisibility(View.INVISIBLE);
        mAddressLayout.setVisibility(View.INVISIBLE);

        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                if (animation == null)
                {
                    return;
                }

                float value = (Float) animation.getAnimatedValue();

                int height = (int) mFakeMapLayout.getTag();

                mFakeMapLayout.setPadding(0, 0, 0, (int) ((mAddressLayout.getY() - height) * value));

                mScrollLayout.setTranslationY(((int) mAddressLayout.getY() - height) * value);
                mScrollLayout.setAlpha(1.0f - value);

                mSearchMapsLayout.setAlpha(value);
                mAddressLayout.setAlpha(value);
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mFakeMapLayout.setTranslationY(0.0f);

                mMapExpandedView.setVisibility(View.GONE);
                mSearchMapsLayout.setVisibility(View.VISIBLE);
                mAddressLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                valueAnimator.removeAllUpdateListeners();
                valueAnimator.removeAllListeners();

                int height = (int) mFakeMapLayout.getTag();
                mFakeMapLayout.setPadding(0, 0, 0, (int) (mAddressLayout.getY() - height));

                mZoomControl.setVisibility(View.VISIBLE);
                mMyLocationView.setVisibility(View.VISIBLE);

                mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
                mScrollLayout.setVisibility(View.INVISIBLE);
                mPlaceLocationMarker.showInfoWindow();

                ((OnEventListener) mOnEventListener).onReleaseUiComponent();
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.start();

        LatLng latLng = new LatLng(latitude, longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder().target((latLng)).zoom(15).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 200, null);
    }

    public void collapseMap()
    {
        setReservationDetailToolbar();

        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                if (animation == null)
                {
                    return;
                }

                float value = (Float) animation.getAnimatedValue();

                int height = (int) mFakeMapLayout.getTag();

                mFakeMapLayout.setPadding(0, 0, 0, (int) ((mAddressLayout.getY() - height) * value));

                mScrollLayout.setTranslationY(((int) mAddressLayout.getY() - height) * value);
                mScrollLayout.setAlpha(1.0f - value);

                mSearchMapsLayout.setAlpha(value);
                mAddressLayout.setAlpha(value);
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mScrollLayout.setVisibility(View.VISIBLE);
                mZoomControl.setVisibility(View.INVISIBLE);
                mMyLocationView.setVisibility(View.INVISIBLE);

                mPlaceLocationMarker.hideInfoWindow();
                mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                valueAnimator.removeAllUpdateListeners();
                valueAnimator.removeAllListeners();

                mScrollLayout.scrollTo(0, 0);
                mScrollLayout.setScrollingEnabled(true);
                mFakeMapLayout.setTranslationY(0.0f);

                int height = (int) mFakeMapLayout.getTag();
                mFakeMapLayout.setPadding(0, 0, 0, (int) ((mAddressLayout.getY() - height)));

                mSearchMapsLayout.setVisibility(View.GONE);
                mAddressLayout.setVisibility(View.GONE);
                mMapExpandedView.setVisibility(View.VISIBLE);

                ((OnEventListener) mOnEventListener).onReleaseUiComponent();
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.reverse();

        CameraPosition cameraPosition = new CameraPosition.Builder().target((mCenterLatLng)).zoom(15).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 300, null);
    }

    public void changeLocation(Location location)
    {
        if (mGoogleMap == null)
        {
            return;
        }

        if (mMyLocationMarkerOptions == null)
        {
            mMyLocationMarkerOptions = new MarkerOptions();
            mMyLocationMarkerOptions.icon(new MyLocationMarker(mContext).makeIcon());
            mMyLocationMarkerOptions.anchor(0.5f, 0.5f);
        }

        if (mMyLocationMarker != null)
        {
            mMyLocationMarker.remove();
        }

        mMyLocationMarkerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
        mMyLocationMarker = mGoogleMap.addMarker(mMyLocationMarkerOptions);

        LatLngBounds.Builder latLngBounds = new LatLngBounds.Builder();
        latLngBounds.include(mPlaceLocationMarker.getPosition());
        latLngBounds.include(mMyLocationMarker.getPosition());

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), ScreenUtils.dpToPx(mContext, 50));
        mGoogleMap.animateCamera(cameraUpdate);
    }

    public ImageView getMyLocationView()
    {
        return mMyLocationView;
    }

    @SuppressWarnings("ResourceType")
    void relocationMyLocation(View view)
    {
        mMyLocationView = view.findViewById(0x2);

        if (mMyLocationView != null)
        {
            mMyLocationView.setVisibility(View.INVISIBLE);
            mMyLocationView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ((OnEventListener) mOnEventListener).onMyLocationClick();
                }
            });
        }
    }

    @SuppressWarnings("ResourceType")
    void relocationZoomControl(View view)
    {
        mZoomControl = view.findViewById(0x1);

        if (mZoomControl != null && mZoomControl.getLayoutParams() instanceof RelativeLayout.LayoutParams)
        {
            mZoomControl.setVisibility(View.INVISIBLE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mZoomControl.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);

            mZoomControl.setPadding(mZoomControl.getPaddingLeft(), ScreenUtils.dpToPx(mContext, 50), mZoomControl.getPaddingRight(), mZoomControl.getPaddingBottom());
            mZoomControl.setLayoutParams(params);
        }
    }

    void addMarker(GoogleMap googleMap, double lat, double lng, String hotel_name)
    {
        if (googleMap != null)
        {
            mPlaceLocationMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(hotel_name));
            mPlaceLocationMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.info_ic_map_large));

            LatLng address = new LatLng(lat, lng);
            CameraPosition cp = new CameraPosition.Builder().target((address)).zoom(15).build();

            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
            googleMap.setInfoWindowAdapter(new PlaceNameInfoWindowAdapter(mContext));
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
            {
                @Override
                public boolean onMarkerClick(Marker marker)
                {
                    marker.showInfoWindow();
                    return true;
                }
            });

            mPlaceLocationMarker.hideInfoWindow();
        }
    }
}
