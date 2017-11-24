package com.daily.dailyhotel.screen.booking.detail.gourmet;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyScrollView;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.GourmetBookingDetail;
import com.daily.dailyhotel.view.DailyBookingProductView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityGourmetBookingDetailDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetBookingCancelDetail01DataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetBookingDetail01DataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetBookingDetail02DataBinding;
import com.twoheart.dailyhotel.databinding.LayoutPlaceBookingCancelDetailDataBinding;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.adapter.PlaceNameInfoWindowAdapter;
import com.twoheart.dailyhotel.place.layout.PlaceReservationDetailLayout;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;

import java.util.Date;
import java.util.Locale;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.Observer;

public class GourmetBookingDetailView extends BaseDialogView<GourmetBookingDetailView.OnEventListener, ActivityGourmetBookingDetailDataBinding> //
    implements GourmetBookingDetailInterface, View.OnClickListener
{
    ImageView mMyLocationView;
    Drawable mMyLocationDrawable;
    View mZoomControl;
    private MarkerOptions mMyLocationMarkerOptions;
    Marker mMyLocationMarker, mPlaceLocationMarker;
    GoogleMap mGoogleMap;
    LatLng mCenterLatLng;
    boolean mMapLoaded;

    private LayoutGourmetBookingDetail01DataBinding mBookingDetail01DataBinding;
    private LayoutGourmetBookingDetail02DataBinding mBookingDetail02DataBinding;

    public interface OnEventListener extends OnBaseEventListener
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

    public GourmetBookingDetailView(BaseActivity baseActivity, GourmetBookingDetailView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityGourmetBookingDetailDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.scrollView, getColor(R.color.default_over_scroll_edge));

        double width = ScreenUtils.getScreenWidth(getContext());
        double height = ScreenUtils.getRatioHeightType16x9(ScreenUtils.getScreenWidth(getContext()));
        final float PLACE_INFORMATION_LAYOUT_RATIO = 0.65f;

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
            , R.layout.layout_gourmet_booking_detail_01_data, viewDataBinding.detailsLayout, true);

        mBookingDetail02DataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()) //
            , R.layout.layout_gourmet_booking_detail_02_data, viewDataBinding.detailsLayout, true);

        viewDataBinding.deleteReservationTextView.setPaintFlags(viewDataBinding.deleteReservationTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(title);
    }

    @Override
    public void setBookingDetail(GourmetBookingDetail gourmetBookingDetail)
    {
//        initHeaderInformationLayout(mContext, mScrollLayout, placeBookingDetail);
//        initPlaceInformationLayout(mContext, mScrollLayout, todayDateTime, placeBookingDetail);
//        initTimeInformationLayout(mContext, mScrollLayout, placeBookingDetail);
//        initGuestInformationLayout(mContext, mScrollLayout, placeBookingDetail);
//        initPaymentInformationLayout(mContext, mScrollLayout, placeBookingDetail);
//        initRefundPolicyLayout(mContext, mScrollLayout, placeBookingDetail);

        setHeaderLayout(getContext(), gourmetBookingDetail);

        setBookingInformation(getContext(), mBookingDetail01DataBinding, gourmetBookingDetail);
        setGuestInformation(getContext(), mBookingDetail01DataBinding, gourmetBookingDetail);
        setPaymentInformation(getContext(), mBookingDetail02DataBinding, gourmetBookingDetail);
    }

    private void initToolbar(ActivityGourmetBookingDetailDataBinding viewDataBinding)
    {
        setBookingDetailToolbar();
    }

    private void setHeaderLayout(Context context, GourmetBookingDetail gourmetBookingDetail)
    {
        if (context == null || gourmetBookingDetail == null || getViewDataBinding() == null)
        {
            return;
        }

        double width = ScreenUtils.getScreenWidth(context);
        double height = ScreenUtils.getRatioHeightType16x9(ScreenUtils.getScreenWidth(context));

        if (Util.isInstallGooglePlayService(context) == false)
        {
            getViewDataBinding().googleMapLayout.setVisibility(View.GONE);

            setImageMapLayout(context, gourmetBookingDetail.latitude, gourmetBookingDetail.longitude, (int) width, (int) height);
        } else
        {
            getViewDataBinding().googleMapLayout.setVisibility(View.VISIBLE);

            setGoogleMapLayout(context, gourmetBookingDetail, (int) width, (int) height);
        }

        getViewDataBinding().viewDetailView.setOnClickListener(this);
        getViewDataBinding().viewMapView.setOnClickListener(this);
        getViewDataBinding().placeNameTextView.setText(gourmetBookingDetail.gourmetName);

        getViewDataBinding().inputReviewView.setOnClickListener(this);

        setReviewButtonLayout(gourmetBookingDetail.reviewStatusType);
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

    private void setGoogleMapLayout(Context context, GourmetBookingDetail gourmetBookingDetail, int width, int height)
    {
        if (context == null || getViewDataBinding() == null || gourmetBookingDetail == null)
        {
            return;
        }

        getViewDataBinding().addressLayout.setVisibility(View.GONE);
        getViewDataBinding().searchMapsLayout.setVisibility(View.GONE);
        getViewDataBinding().addressTextView.setText(gourmetBookingDetail.gourmetAddress);

        getViewDataBinding().copyAddressView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onClipAddressClick();
            }
        });

        getViewDataBinding().searchMapView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onSearchMapClick();
            }
        });

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

                relocationMyLocation(getViewDataBinding().mapLayout);
                relocationZoomControl(getViewDataBinding().mapLayout);
                addMarker(mGoogleMap, gourmetBookingDetail.latitude, gourmetBookingDetail.longitude, gourmetBookingDetail.gourmetName);

                mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback()
                {
                    @Override
                    public void onMapLoaded()
                    {
                        mMapLoaded = true;

                        Projection projection = mGoogleMap.getProjection();

                        Point point = projection.toScreenLocation(new LatLng(gourmetBookingDetail.latitude, gourmetBookingDetail.longitude));
                        point.y += point.y - (getViewDataBinding().fakeMapLayout.getHeight() * 0.43);

                        mCenterLatLng = projection.fromScreenLocation(point);
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(mCenterLatLng), 200, null);
                    }
                });
            }
        });
    }

    @Override
    public void setReviewButtonLayout(String reviewStatus)
    {
        if (getContext() == null || getViewDataBinding() == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(reviewStatus) == true)
        {
            reviewStatus = PlaceBookingDetail.ReviewStatusType.NONE;
        }

        getViewDataBinding().inputReviewView.setTag(reviewStatus);

        if (PlaceBookingDetail.ReviewStatusType.ADDABLE.equalsIgnoreCase(reviewStatus) == true)
        {
            getViewDataBinding().inputReviewVerticalLine.setVisibility(View.VISIBLE);
            getViewDataBinding().inputReviewView.setVisibility(View.VISIBLE);
            getViewDataBinding().inputReviewView.setDrawableVectorTint(R.color.default_background_c454545);
            getViewDataBinding().inputReviewView.setTextColor(getContext().getResources().getColor(R.color.default_text_c323232));
        } else if (PlaceBookingDetail.ReviewStatusType.COMPLETE.equalsIgnoreCase(reviewStatus) == true)
        {
            getViewDataBinding().inputReviewVerticalLine.setVisibility(View.VISIBLE);
            getViewDataBinding().inputReviewView.setVisibility(View.VISIBLE);
            getViewDataBinding().inputReviewView.setDrawableVectorTint(R.color.default_background_c454545_alpha_20);
            getViewDataBinding().inputReviewView.setTextColor(getContext().getResources().getColor(R.color.default_text_cc5c5c5));
            getViewDataBinding().inputReviewView.setText(R.string.label_booking_completed_input_review);
            getViewDataBinding().inputReviewView.setOnClickListener(null);
            getViewDataBinding().inputReviewView.setEnabled(false);
        } else
        {
            getViewDataBinding().inputReviewVerticalLine.setVisibility(View.GONE);
            getViewDataBinding().inputReviewView.setVisibility(View.GONE);
            getViewDataBinding().inputReviewView.setDrawableVectorTint(R.color.default_background_c454545);
            getViewDataBinding().inputReviewView.setTextColor(getContext().getResources().getColor(R.color.default_text_c323232));
        }
    }

    private void setBookingInformation(Context context, LayoutGourmetBookingDetail01DataBinding dataBinding, GourmetBookingDetail gourmetBookingDetail)
    {
        if (context == null || dataBinding == null || gourmetBookingDetail == null)
        {
            return;
        }

        // TODO : 리마인드 날짜 계산 부분 확인 필요.
//        // 3일전 부터 몇일 남음 필요.
//        View remainedDayLayout = view.findViewById(R.id.remainedDayLayout);
//        TextView remainedDayTextView = (TextView) view.findViewById(R.id.remainedDayTextView);
//        String remainedDayText;
//
//        try
//        {
//            Date checkInDate = DailyCalendar.convertStringToDate(gourmetBookingDetail.reservationTime);
//            Date currentDate = DailyCalendar.convertStringToDate(todayDateTime.currentDateTime);
//
//            int dayOfDays = (int) ((DailyCalendar.clearTField(checkInDate.getTime()) - DailyCalendar.clearTField(currentDate.getTime())) / DailyCalendar.DAY_MILLISECOND);
//            if (dayOfDays < 0 || dayOfDays > 3)
//            {
//                remainedDayText = null;
//            } else if (dayOfDays > 0)
//            {
//                // 하루이상 남음
//                remainedDayText = context.getString(R.string.frag_booking_duedate_formet_gourmet, dayOfDays);
//            } else
//            {
//                // 당일
//                remainedDayText = context.getString(R.string.frag_booking_today_type_gourmet);
//            }
//
//            if (DailyTextUtils.isTextEmpty(remainedDayText) == true)
//            {
//                remainedDayLayout.setVisibility(View.GONE);
//            } else
//            {
//                remainedDayLayout.setVisibility(View.VISIBLE);
//                remainedDayTextView.setText(remainedDayText);
//            }
//        } catch (Exception e)
//        {
//            ExLog.d(e.toString());
//        }

        dataBinding.gourmetNameTextView.setText(gourmetBookingDetail.gourmetName);
        dataBinding.addressTextView.setText(gourmetBookingDetail.gourmetAddress);

        // TODO : Test Code 서버 연결 작업 후 재 작업 필요.
        int randPersons = new Random(5).nextInt() -1;
        int tempPrice = gourmetBookingDetail.priceTotal;

        dataBinding.productInformationView.addInformation(gourmetBookingDetail.ticketName, gourmetBookingDetail.ticketCount, randPersons, tempPrice);
        // TODO : 임시 두줄
        //         dataBinding.productInformationView.addInformation(gourmetBookingDetail.ticketName +"\n" + gourmetBookingDetail.ticketName, gourmetBookingDetail.ticketCount, randPersons, tempPrice);
    }

    private void setGuestInformation(Context context, LayoutGourmetBookingDetail01DataBinding dataBinding, GourmetBookingDetail gourmetBookingDetail)
    {
        if (context == null || dataBinding == null || gourmetBookingDetail == null)
        {
            return;
        }

        dataBinding.guestNameTextView.setText(gourmetBookingDetail.guestName);
        dataBinding.guestPhoneTextView.setText(Util.addHyphenMobileNumber(context, gourmetBookingDetail.guestPhone));
        dataBinding.guestEmailTextView.setText(gourmetBookingDetail.guestEmail);
    }

    private void setPaymentInformation(Context context, LayoutGourmetBookingDetail02DataBinding dataBinding, GourmetBookingDetail gourmetBookingDetail)
    {
        if (context == null || getViewDataBinding() == null || gourmetBookingDetail == null)
        {
            return;
        }

        dataBinding.bonusLayout.setVisibility(View.GONE);

        try
        {
            dataBinding.paymentDateTextView.setText(DailyCalendar.convertDateFormatString(gourmetBookingDetail.pamentDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        // TODO : 가격 노출 확인 필요. discountTotal, priceTotal
        dataBinding.priceTextView.setText(DailyTextUtils.getPriceFormat(context, gourmetBookingDetail.discountTotal, false));

        if (gourmetBookingDetail.couponAmount > 0)
        {
            dataBinding.couponLayout.setVisibility(View.VISIBLE);
            dataBinding.couponTextView.setText("- " + DailyTextUtils.getPriceFormat(context, gourmetBookingDetail.couponAmount, false));
        } else
        {
            dataBinding.couponLayout.setVisibility(View.GONE);
        }

        dataBinding.totalPriceTextView.setText(DailyTextUtils.getPriceFormat(context, gourmetBookingDetail.priceTotal, false));

        // 영수증 발급
        dataBinding.buttonLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onIssuingReceiptClick();
            }
        });
    }

    @SuppressWarnings("ResourceType")
    void relocationMyLocation(View view)
    {
        mMyLocationView = (ImageView) view.findViewById(0x2);

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

            mMyLocationDrawable = mMyLocationView.getDrawable();
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

            mZoomControl.setPadding(mZoomControl.getPaddingLeft(), ScreenUtils.dpToPx(getContext(), 50), mZoomControl.getPaddingRight(), mZoomControl.getPaddingBottom());
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

    @Override
    public void onClick(View view)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        switch (view.getId())
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
                        getEventListener().onLoadingMap();
                    }
                } else
                {
                    getEventListener().onMapClick(false);
                }
                break;
            }

            case R.id.viewDetailView:
            {
                getEventListener().onViewDetailClick();
                break;
            }

            case R.id.viewMapView:
            {
                getEventListener().onViewMapClick();
                break;
            }

            case R.id.refundButtonLayout:
            {
                getEventListener().onRefundClick();
                break;
            }

            case R.id.inputReviewView:
            {
                if (view.getTag() == null)
                {
                    return;
                }

                if ((view.getTag() instanceof String) == false)
                {
                    return;
                }

                String reviewStatus = (String) view.getTag();

                getEventListener().onReviewClick(reviewStatus);
                break;
            }

            case R.id.deleteReservationTextView:
                getEventListener().onDeleteReservationClick();
                break;
        }
    }

    public boolean isExpandedMap()
    {
        if (getViewDataBinding() == null)
        {
            return false;
        }

        return getViewDataBinding().searchMapsLayout != null && getViewDataBinding().searchMapsLayout.getVisibility() != View.GONE;
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

                        int height = (int) getViewDataBinding().fakeMapLayout.getTag();
                        getViewDataBinding().fakeMapLayout.setPadding(0, 0, 0, (int) (getViewDataBinding().addressLayout.getY() - height));

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
}
