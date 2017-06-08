package com.daily.dailyhotel.screen.booking.detail.stay.outbound;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.graphics.PointF;
import android.location.Location;
import android.net.Uri;
import android.text.Html;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyScrollView;
import com.daily.dailyhotel.entity.StayOutboundBookingDetail;
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
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundBookingDetailDataBinding;
import com.twoheart.dailyhotel.databinding.DialogConciergeDataBinding;
import com.twoheart.dailyhotel.databinding.DialogShareDataBinding;
import com.twoheart.dailyhotel.databinding.DialogStayOutboundMapDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundBookingDetail01DataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundBookingDetail02DataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundBookingDetail03DataBinding;
import com.twoheart.dailyhotel.model.MyLocationMarker;
import com.twoheart.dailyhotel.place.adapter.PlaceNameInfoWindowAdapter;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Observer;

public class StayOutboundBookingDetailView extends BaseDialogView<StayOutboundBookingDetailView.OnEventListener, ActivityStayOutboundBookingDetailDataBinding>//
    implements StayOutboundBookingDetailInterface, View.OnClickListener
{
    private DailyToolbarLayout mDailyToolbarLayout;
    private View mMyLocationView, mZoomControl;
    private MarkerOptions mMyLocationMarkerOptions;
    private Marker mMyLocationMarker, mPlaceLocationMarker;
    private GoogleMap mGoogleMap;
    private LatLng mCenterLatLng;
    private boolean mMapLoaded;


    private LayoutStayOutboundBookingDetail01DataBinding mBookingDetail01DataBinding;
    private LayoutStayOutboundBookingDetail02DataBinding mBookingDetail02DataBinding;
    private LayoutStayOutboundBookingDetail03DataBinding mBookingDetail03DataBinding;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onShareClick();

        void onMapLoaded();

        void onMapLoading();

        void onMapClick(boolean isGoogleMap);

        void onViewDetailClick();

        void onNavigatorClick();

        void onRefundClick();

        void onClipAddressClick();

        void onMyLocationClick();

        void onIssuingReceiptClick();

        void onShareMapClick();

        void onConciergeHappyTalkClick();

        void onConciergeCallClick();

        void onShareSmsClick();
    }

    public StayOutboundBookingDetailView(BaseActivity baseActivity, StayOutboundBookingDetailView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayOutboundBookingDetailDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.scrollView, getColor(R.color.default_over_scroll_edge));

        double width = ScreenUtils.getScreenWidth(getContext());
        double height = ScreenUtils.getRatioHeightType16x9(ScreenUtils.getScreenWidth(getContext()));
        final float PLACE_INFORMATION_LAYOUT_RATIO = 0.72f;

        // Map 4 :2 비율 맞추기
        viewDataBinding.mapImageView.setOnClickListener(this);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewDataBinding.mapImageView.getLayoutParams();
        layoutParams.width = (int) width;
        layoutParams.height = (int) height;

        viewDataBinding.mapImageView.setLayoutParams(layoutParams);

        if (Util.isInstallGooglePlayService(getContext()) == false)
        {
            viewDataBinding.googleMapLayout.setVisibility(View.GONE);
        } else
        {
            viewDataBinding.googleMapLayout.setVisibility(View.VISIBLE);
        }

        RelativeLayout.LayoutParams placeInformationLayoutParams = (RelativeLayout.LayoutParams) viewDataBinding.placeInformationLayout.getLayoutParams();
        placeInformationLayoutParams.topMargin = (int) (PLACE_INFORMATION_LAYOUT_RATIO * height);
        viewDataBinding.placeInformationLayout.setLayoutParams(placeInformationLayoutParams);
        viewDataBinding.placeInformationLayout.setVisibility(View.VISIBLE);

        mBookingDetail01DataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext())//
            , R.layout.layout_stay_outbound_booking_detail_01_data, viewDataBinding.detailsLayout, true);

        mBookingDetail02DataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext())//
            , R.layout.layout_stay_outbound_booking_detail_02_data, viewDataBinding.detailsLayout, true);

        mBookingDetail03DataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext())//
            , R.layout.layout_stay_outbound_booking_detail_03_data, viewDataBinding.detailsLayout, true);
    }

    @Override
    public void setToolbarTitle(String title)
    {
    }

    @Override
    public void setBookingDetail(StayOutboundBookingDetail stayOutboundBookingDetail)
    {

    }

    @Override
    public Observable<Boolean> expandMap(double latitude, double longitude)
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        Observable<Boolean> observable = new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                setMapToolbar();

                getViewDataBinding().scrollView.setScrollingEnabled(false);
                getViewDataBinding().scrollView.scrollTo(0, 0);

                getViewDataBinding().searchMapsLayout.setVisibility(View.INVISIBLE);
                getViewDataBinding().addressLayout.setVisibility(View.INVISIBLE);

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

                        int height = (int) getViewDataBinding().fakeMapLayout.getTag();

                        getViewDataBinding().fakeMapLayout.setPadding(0, 0, 0, (int) ((getViewDataBinding().addressLayout.getY() - height) * value));

                        getViewDataBinding().scrollView.setTranslationY(((int) getViewDataBinding().addressLayout.getY() - height) * value);
                        getViewDataBinding().scrollView.setAlpha(1.0f - value);

                        getViewDataBinding().searchMapsLayout.setAlpha(value);
                        getViewDataBinding().addressLayout.setAlpha(value);
                    }
                });

                valueAnimator.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {
                        getViewDataBinding().fakeMapLayout.setTranslationY(0.0f);

                        getViewDataBinding().mapExpandedView.setVisibility(View.GONE);
                        getViewDataBinding().searchMapsLayout.setVisibility(View.VISIBLE);
                        getViewDataBinding().addressLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        valueAnimator.removeAllUpdateListeners();
                        valueAnimator.removeAllListeners();

                        int height = (int) getViewDataBinding().mapExpandedView.getTag();
                        getViewDataBinding().mapExpandedView.setPadding(0, 0, 0, (int) (getViewDataBinding().addressLayout.getY() - height));

                        mZoomControl.setVisibility(View.VISIBLE);
                        mMyLocationView.setVisibility(View.VISIBLE);

                        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
                        getViewDataBinding().scrollView.setVisibility(View.INVISIBLE);
                        mPlaceLocationMarker.showInfoWindow();

                        observer.onNext(true);
                        observer.onComplete();
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
        };

        return observable;
    }

    @Override
    public Observable<Boolean> collapseMap()
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        Observable<Boolean> observable = new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                setBookingDetailToolbar();

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

                        int height = (int) getViewDataBinding().fakeMapLayout.getTag();

                        getViewDataBinding().fakeMapLayout.setPadding(0, 0, 0, (int) ((getViewDataBinding().addressLayout.getY() - height) * value));

                        getViewDataBinding().scrollView.setTranslationY(((int) getViewDataBinding().addressLayout.getY() - height) * value);
                        getViewDataBinding().scrollView.setAlpha(1.0f - value);

                        getViewDataBinding().searchMapsLayout.setAlpha(value);
                        getViewDataBinding().addressLayout.setAlpha(value);
                    }
                });

                valueAnimator.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {
                        getViewDataBinding().scrollView.setVisibility(View.VISIBLE);
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

                        getViewDataBinding().scrollView.scrollTo(0, 0);
                        getViewDataBinding().scrollView.setScrollingEnabled(true);
                        getViewDataBinding().fakeMapLayout.setTranslationY(0.0f);

                        int height = (int) getViewDataBinding().fakeMapLayout.getTag();
                        getViewDataBinding().fakeMapLayout.setPadding(0, 0, 0, (int) ((getViewDataBinding().addressLayout.getY() - height)));

                        getViewDataBinding().searchMapsLayout.setVisibility(View.GONE);
                        getViewDataBinding().addressLayout.setVisibility(View.GONE);
                        getViewDataBinding().mapExpandedView.setVisibility(View.VISIBLE);

                        observer.onNext(true);
                        observer.onComplete();
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
        };

        return observable;
    }

    @Override
    public void setBookingDate(SpannableString checkInDate, SpannableString checkOutDate, int nights)
    {
        if (getViewDataBinding() == null || mBookingDetail01DataBinding == null)
        {
            return;
        }

        mBookingDetail01DataBinding.checkInDayTextView.setText(checkInDate);
        mBookingDetail01DataBinding.checkOutDayTextView.setText(checkInDate);
        mBookingDetail01DataBinding.nightsTextView.setText(getString(R.string.label_nights, nights));
    }

    @Override
    public void showNavigatorDialog(Dialog.OnDismissListener listener)
    {
        DialogStayOutboundMapDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_stay_outbound_map_data, null, false);

        dataBinding.googleMapTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onShareMapClick();
            }
        });

        showSimpleDialog(dataBinding.getRoot(), null, listener, true);
    }

    @Override
    public void showConciergeDialog(Dialog.OnDismissListener listener)
    {
        DialogConciergeDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_concierge_data, null, false);

        // 버튼
        dataBinding.contactUs01Layout.setVisibility(View.GONE);
        dataBinding.contactUs02Layout.setVisibility(View.GONE);

        dataBinding.kakaoDailyView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onConciergeHappyTalkClick();
            }
        });

        dataBinding.callDailyView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onConciergeCallClick();
            }
        });

        dataBinding.closeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();
            }
        });

        showSimpleDialog(dataBinding.getRoot(), null, listener, true);
    }

    @Override
    public void showShareDialog(Dialog.OnDismissListener listener)
    {
        DialogShareDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_share_data, null, false);

        //        dataBinding.kakaoShareView.setOnClickListener(new View.OnClickListener()
        //        {
        //            @Override
        //            public void onClick(View v)
        //            {
        //                hideSimpleDialog();
        //
        //                getEventListener().onShareKakaoClick();
        //            }
        //        });

        dataBinding.smsShareLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onShareSmsClick();
            }
        });

        dataBinding.closeTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();
            }
        });

        showSimpleDialog(dataBinding.getRoot(), null, listener, true);
    }

    @Override
    public void setMyLocation(Location location)
    {

    }

    @Override
    public void onClick(View v)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        switch (v.getId())
        {
            case R.id.mapImageView:
            {
                if (getViewDataBinding().googleMapLayout.getVisibility() == View.VISIBLE)
                {
                    if (mMapLoaded == true)
                    {
                        getEventListener().onMapClick(true);
                    } else
                    {
                        getEventListener().onMapLoading();
                    }
                } else
                {
                    getEventListener().onMapClick(false);
                }
                break;
            }

            case R.id.viewDetailView:
                getEventListener().onViewDetailClick();
                break;

            case R.id.viewMapView:
                getEventListener().onNavigatorClick();
                break;

            case R.id.refundButtonLayout:
                getEventListener().onRefundClick();
                break;

            case R.id.buttonLayout:
                getEventListener().onIssuingReceiptClick();
                break;

            case R.id.copyAddressView:
                getEventListener().onClipAddressClick();
                break;

            case R.id.searchMapView:
                getEventListener().onNavigatorClick();
                break;
        }
    }

    private void initToolbar(ActivityStayOutboundBookingDetailDataBinding viewDataBinding)
    {
        mDailyToolbarLayout = new DailyToolbarLayout(getContext(), viewDataBinding.toolbar.findViewById(R.id.toolbar));

        setBookingDetailToolbar();
    }

    private void setBookingDetailToolbar()
    {
        if (mDailyToolbarLayout == null)
        {
            return;
        }

        mDailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_booking_list_frag), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onBackClick();
            }
        });

        mDailyToolbarLayout.setToolbarMenu(R.drawable.navibar_ic_help, R.drawable.navibar_ic_share_01_black);
        mDailyToolbarLayout.setToolbarMenuClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switch (v.getId())
                {
                    case R.id.menu1View:
                        getEventListener().onConciergeCallClick();
                        break;

                    case R.id.menu2View:
                        getEventListener().onShareClick();
                        break;
                }
            }
        });
    }

    private void setMapToolbar()
    {
        if (mDailyToolbarLayout == null)
        {
            return;
        }

        mDailyToolbarLayout.initToolbar(getString(R.string.frag_tab_map_title), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onBackClick();
            }
        });

        mDailyToolbarLayout.setToolbarMenu(-1, -1);
        mDailyToolbarLayout.setToolbarMenuClickListener(null);
    }

    private void setHeaderLayout(Context context, StayOutboundBookingDetail stayOutboundBookingDetail)
    {
        if (context == null || stayOutboundBookingDetail == null || getViewDataBinding() == null)
        {
            return;
        }

        double width = ScreenUtils.getScreenWidth(context);
        double height = ScreenUtils.getRatioHeightType16x9(ScreenUtils.getScreenWidth(context));

        if (Util.isInstallGooglePlayService(context) == false)
        {
            getViewDataBinding().googleMapLayout.setVisibility(View.GONE);

            setImageMapLayout(context, stayOutboundBookingDetail.latitude, stayOutboundBookingDetail.longitude, (int) width, (int) height);
        } else
        {
            getViewDataBinding().googleMapLayout.setVisibility(View.VISIBLE);

            setGoogleMapLayout(context, stayOutboundBookingDetail, (int) width, (int) height);
        }

        getViewDataBinding().viewDetailView.setOnClickListener(this);
        getViewDataBinding().viewMapView.setOnClickListener(this);
        getViewDataBinding().placeNameTextView.setText(stayOutboundBookingDetail.name);
    }

    private void setImageMapLayout(Context context, double latitude, double longitude, int height, int width)
    {
        if (context == null || getViewDataBinding() == null)
        {
            return;
        }


        final double ratio = height / width;

        getViewDataBinding().mapImageView.getHierarchy().setActualImageFocusPoint(new PointF(0.5f, 0.58f));

        if (width >= 720)
        {
            width = 720;
        }

        String size = String.format(Locale.KOREA, "%dx%d", width * 3 / 5, (int) (width * ratio * 5) / 7);
        String iconUrl = "http://img.dailyhotel.me/app_static/info_ic_map_large.png";
        String url = String.format(Locale.KOREA, "https://maps.googleapis.com/maps/api/staticmap?zoom=17&size=%s&markers=icon:%s|%s,%s&sensor=false&scale=2&format=png8&mobile=true&key=%s"//
            , size, iconUrl, latitude, longitude, Crypto.getUrlDecoderEx(Constants.GOOGLE_MAP_KEY));

        getViewDataBinding().mapImageView.setImageURI(Uri.parse(url));
    }

    private void setGoogleMapLayout(Context context, StayOutboundBookingDetail stayOutboundBookingDetail, int width, int height)
    {
        if (context == null || getViewDataBinding() == null || stayOutboundBookingDetail == null)
        {
            return;
        }

        getViewDataBinding().addressLayout.setVisibility(View.GONE);
        getViewDataBinding().searchMapsLayout.setVisibility(View.GONE);
        getViewDataBinding().addressTextView.setText(stayOutboundBookingDetail.address);
        getViewDataBinding().copyAddressView.setOnClickListener(this);
        getViewDataBinding().searchMapView.setOnClickListener(this);

        RelativeLayout.LayoutParams fakeMapLayoutParams = (RelativeLayout.LayoutParams) getViewDataBinding().fakeMapLayout.getLayoutParams();
        fakeMapLayoutParams.width = width;
        fakeMapLayoutParams.height = height;

        getViewDataBinding().fakeMapLayout.setLayoutParams(fakeMapLayoutParams);
        getViewDataBinding().fakeMapLayout.setTag(height);

        getViewDataBinding().scrollView.setOnScrollChangedListener(new DailyScrollView.OnScrollChangedListener()
        {
            @Override
            public void onScrollChanged(ScrollView scrollView, int l, int t, int oldl, int oldt)
            {
                getViewDataBinding().fakeMapLayout.setTranslationY(-t);
            }
        });

        SupportMapFragment supportMapFragment = (SupportMapFragment) findFragmentById(R.id.supportMapFragment);
        supportMapFragment.getMapAsync(new OnMapReadyCallback()
        {
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

                relocationMyLocation(getViewDataBinding().mapLayout);
                relocationZoomControl(getViewDataBinding().mapLayout);
                addMarker(mGoogleMap, stayOutboundBookingDetail.latitude, stayOutboundBookingDetail.longitude, stayOutboundBookingDetail.name);

                mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback()
                {
                    @Override
                    public void onMapLoaded()
                    {
                        mMapLoaded = true;

                        Projection projection = mGoogleMap.getProjection();

                        Point point = projection.toScreenLocation(new LatLng(stayOutboundBookingDetail.latitude, stayOutboundBookingDetail.longitude));
                        point.y += (point.y - getViewDataBinding().fakeMapLayout.getHeight() / 2);

                        mCenterLatLng = projection.fromScreenLocation(point);
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(mCenterLatLng), 200, null);
                    }
                });
            }
        });
    }

    private void setBookingInformation(Context context, LayoutStayOutboundBookingDetail01DataBinding dataBinding, StayOutboundBookingDetail stayOutboundBookingDetail)
    {
        if (context == null || dataBinding == null || stayOutboundBookingDetail == null)
        {
            return;
        }

        dataBinding.hotelNameTextView.setText(stayOutboundBookingDetail.name);
        dataBinding.roomTypeTextView.setText(stayOutboundBookingDetail.roomName);
        dataBinding.addressTextView.setText(stayOutboundBookingDetail.address);
    }

    private void setGuestInformation(Context context, LayoutStayOutboundBookingDetail01DataBinding dataBinding, StayOutboundBookingDetail stayOutboundBookingDetail)
    {
        if (context == null || dataBinding == null || stayOutboundBookingDetail == null)
        {
            return;
        }

        dataBinding.guestNameTextView.setText(stayOutboundBookingDetail.guestLastName + " " + stayOutboundBookingDetail.guestFirstName);
        dataBinding.guestPhoneTextView.setText(Util.addHyphenMobileNumber(context, stayOutboundBookingDetail.guestPhone));
        dataBinding.guestEmailTextView.setText(stayOutboundBookingDetail.guestEmail);
        dataBinding.peopleTextView.setText(stayOutboundBookingDetail.getPeople().toString(context));
    }

    protected void setPaymentInformation(Context context, LayoutStayOutboundBookingDetail02DataBinding dataBinding, StayOutboundBookingDetail stayOutboundBookingDetail)
    {
        if (context == null || dataBinding == null || stayOutboundBookingDetail == null)
        {
            return;
        }

        try
        {
            dataBinding.paymentDateTextView.setText(DailyCalendar.convertDateFormatString(stayOutboundBookingDetail.paymentDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        dataBinding.priceTextView.setText(DailyTextUtils.getPriceFormat(context, stayOutboundBookingDetail.totalPrice, false));


        if (stayOutboundBookingDetail.bonus > 0)
        {
            dataBinding.bonusLayout.setVisibility(View.VISIBLE);
            dataBinding.bonusTextView.setText("- " + DailyTextUtils.getPriceFormat(context, stayOutboundBookingDetail.bonus, false));
        } else
        {
            dataBinding.bonusLayout.setVisibility(View.GONE);
        }

        dataBinding.totalPriceTextView.setText(DailyTextUtils.getPriceFormat(context, stayOutboundBookingDetail.paymentPrice, false));

        // 영수증 발급
        dataBinding.buttonLayout.setOnClickListener(this);
    }

    protected void setRefundPolicyLayout(Context context, StayOutboundBookingDetail stayOutboundBookingDetail, String statusRefund)
    {
        if (context == null || mBookingDetail03DataBinding == null || stayOutboundBookingDetail == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(stayOutboundBookingDetail.refundComment) == false)
        {
            mBookingDetail03DataBinding.refundPolicyTextView.setText(Html.fromHtml(stayOutboundBookingDetail.refundComment));
        }

        // 정책을 보여주지 않을 경우
        if (stayOutboundBookingDetail.readyForRefund == false)
        {
            setRefundLayoutVisible(false);
        } else
        {
            setRefundLayoutVisible(true);

            switch (statusRefund)
            {
                case StayOutboundBookingDetail.STATUS_NO_CHARGE_REFUND:
                {
                    mBookingDetail03DataBinding.defaultRefundPolicyLayout.setVisibility(View.VISIBLE);
                    mBookingDetail03DataBinding.waitRefundPolicyLayout.setVisibility(View.GONE);
                    mBookingDetail03DataBinding.refundButtonLayout.setOnClickListener(this);
                    mBookingDetail03DataBinding.buttonTextView.setText(R.string.label_request_free_refund);
                    break;
                }

                case StayOutboundBookingDetail.STATUS_WAIT_REFUND:
                {
                    mBookingDetail03DataBinding.defaultRefundPolicyLayout.setVisibility(View.GONE);
                    mBookingDetail03DataBinding.waitRefundPolicyLayout.setVisibility(View.VISIBLE);
                    mBookingDetail03DataBinding.waitRefundPolicyTextView.setText(Html.fromHtml(context.getString(R.string.message_please_wait_refund01)));
                    mBookingDetail03DataBinding.refundButtonLayout.setOnClickListener(this);
                    mBookingDetail03DataBinding.buttonTextView.setText(R.string.label_contact_refund);
                    break;
                }

                case StayOutboundBookingDetail.STATUS_SURCHARGE_REFUND:
                {
                    mBookingDetail03DataBinding.defaultRefundPolicyLayout.setVisibility(View.VISIBLE);
                    mBookingDetail03DataBinding.waitRefundPolicyLayout.setVisibility(View.GONE);
                    mBookingDetail03DataBinding.refundButtonLayout.setOnClickListener(this);
                    mBookingDetail03DataBinding.buttonTextView.setText(R.string.label_contact_refund);
                    break;
                }

                default:
                {
                    mBookingDetail03DataBinding.defaultRefundPolicyLayout.setVisibility(View.VISIBLE);
                    mBookingDetail03DataBinding.waitRefundPolicyLayout.setVisibility(View.GONE);
                    mBookingDetail03DataBinding.refundButtonLayout.setOnClickListener(null);
                    mBookingDetail03DataBinding.refundButtonLayout.setVisibility(View.GONE);
                    break;
                }
            }
        }
    }

    public void setRefundLayoutVisible(boolean visible)
    {
        if (mBookingDetail03DataBinding == null)
        {
            return;
        }

        if (mBookingDetail03DataBinding.refundPolicyLayout == null)
        {
            return;
        }

        if (visible == true)
        {
            mBookingDetail03DataBinding.refundPolicyLayout.setVisibility(View.VISIBLE);
            mBookingDetail02DataBinding.buttonBottomMarginView.setVisibility(View.GONE);
        } else
        {
            mBookingDetail03DataBinding.refundPolicyLayout.setVisibility(View.GONE);
            mBookingDetail02DataBinding.buttonBottomMarginView.setVisibility(View.VISIBLE);
        }
    }

    public boolean isExpandedMap()
    {
        return getViewDataBinding().searchMapsLayout != null && getViewDataBinding().searchMapsLayout.getVisibility() != View.GONE;
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
            mMyLocationMarkerOptions.icon(new MyLocationMarker(getContext()).makeIcon());
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

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), ScreenUtils.dpToPx(getContext(), 50));
        mGoogleMap.animateCamera(cameraUpdate);
    }

    private void relocationMyLocation(View view)
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
                    getEventListener().onMyLocationClick();
                }
            });
        }
    }

    private void relocationZoomControl(View view)
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

            mZoomControl.setPadding(mZoomControl.getPaddingLeft(), ScreenUtils.dpToPx(getContext(), 50), mZoomControl.getPaddingRight(), mZoomControl.getPaddingBottom());
            mZoomControl.setLayoutParams(params);
        }
    }

    private void addMarker(GoogleMap googleMap, double lat, double lng, String hotel_name)
    {
        if (googleMap != null)
        {
            mPlaceLocationMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(hotel_name));
            mPlaceLocationMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.info_ic_map_large));

            LatLng address = new LatLng(lat, lng);
            CameraPosition cp = new CameraPosition.Builder().target((address)).zoom(15).build();

            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
            googleMap.setInfoWindowAdapter(new PlaceNameInfoWindowAdapter(getContext()));
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
