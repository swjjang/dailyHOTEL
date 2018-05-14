package com.daily.dailyhotel.screen.booking.cancel.detail.stay.outbound;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyScrollView;
import com.daily.dailyhotel.entity.StayOutboundBookingDetail;
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
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundBookingCancelDetailDataBinding;
import com.twoheart.dailyhotel.databinding.DialogConciergeDataBinding;
import com.twoheart.dailyhotel.databinding.DialogShareDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutPlaceBookingCancelDetailDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundBookingDetail01DataBinding;
import com.twoheart.dailyhotel.model.MyLocationMarker;
import com.twoheart.dailyhotel.place.adapter.PlaceNameInfoWindowAdapter;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class StayOutboundBookingCancelDetailView extends BaseDialogView<StayOutboundBookingCancelDetailView.OnEventListener, ActivityStayOutboundBookingCancelDetailDataBinding>//
    implements StayOutboundBookingCancelDetailInterface, View.OnClickListener
{
    ImageView mMyLocationView;
    Drawable mMyLocationDrawable;
    View mZoomControl;
    private MarkerOptions mMyLocationMarkerOptions;
    Marker mMyLocationMarker, mPlaceLocationMarker;
    GoogleMap mGoogleMap;
    LatLng mCenterLatLng;
    boolean mMapLoaded;

    private LayoutStayOutboundBookingDetail01DataBinding mBookingDetail01DataBinding;
    private LayoutPlaceBookingCancelDetailDataBinding mBookingCancelDetailDataBinding;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onShareClick();

        void onMapLoading();

        void onMapClick();

        void onExpandMapClick();

        void onCollapseMapClick();

        void onViewDetailClick();

        void onNavigatorClick();

        void onClipAddressClick();

        void onMyLocationClick();

        void onConciergeClick();

        void onConciergeFaqClick();

        void onConciergeHappyTalkClick();

        void onConciergeCallClick();

        void onShareKakaoClick();

        void onMoreShareClick();

        void onHiddenReservationClick();
    }

    public StayOutboundBookingCancelDetailView(BaseActivity baseActivity, StayOutboundBookingCancelDetailView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayOutboundBookingCancelDetailDataBinding viewDataBinding)
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
        viewDataBinding.googleMapLayout.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams placeInformationLayoutParams = (RelativeLayout.LayoutParams) viewDataBinding.placeInformationLayout.getLayoutParams();
        placeInformationLayoutParams.topMargin = (int) (PLACE_INFORMATION_LAYOUT_RATIO * height);
        viewDataBinding.placeInformationLayout.setLayoutParams(placeInformationLayoutParams);
        viewDataBinding.placeInformationLayout.setVisibility(View.VISIBLE);

        mBookingDetail01DataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext())//
            , R.layout.layout_stay_outbound_booking_detail_01_data, viewDataBinding.detailsLayout, true);

        mBookingCancelDetailDataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()) //
            , R.layout.layout_place_booking_cancel_detail_data, viewDataBinding.detailsLayout, true);

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
    public void setBookingDetail(StayOutboundBookingDetail stayOutboundBookingDetail)
    {
        setHeaderLayout(getContext(), stayOutboundBookingDetail);

        setBookingInformation(getContext(), mBookingDetail01DataBinding, stayOutboundBookingDetail);

        setGuestInformation(getContext(), mBookingDetail01DataBinding, stayOutboundBookingDetail);

        setCancelInformation(getContext(), mBookingCancelDetailDataBinding, stayOutboundBookingDetail);
    }

    @Override
    public Observable<Boolean> expandMap(double latitude, double longitude)
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        return new Observable<Boolean>()
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
    }

    @Override
    public Observable<Boolean> collapseMap()
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        return new Observable<Boolean>()
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
    }

    @Override
    public void setBookingDate(SpannableString checkInDate, SpannableString checkOutDate, int nights)
    {
        if (getViewDataBinding() == null || mBookingDetail01DataBinding == null)
        {
            return;
        }

        mBookingDetail01DataBinding.dateInformationView.setDateVisible(true, true);

        mBookingDetail01DataBinding.dateInformationView.setDate1Text(getString(R.string.label_check_in), checkInDate);
        mBookingDetail01DataBinding.dateInformationView.setData1TextSize(13.0f, 13.0f);

        mBookingDetail01DataBinding.dateInformationView.setCenterNightsVisible(true);
        mBookingDetail01DataBinding.dateInformationView.setCenterNightsText(getString(R.string.label_nights, nights));

        mBookingDetail01DataBinding.dateInformationView.setDate2Text(getString(R.string.label_check_out), checkOutDate);
        mBookingDetail01DataBinding.dateInformationView.setData2TextSize(13.0f, 13.0f);
    }

    @Override
    public void showConciergeDialog(Dialog.OnDismissListener listener)
    {
        DialogConciergeDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_concierge_data, null, false);

        // 버튼
        dataBinding.contactUs02Layout.setVisibility(View.GONE);

        dataBinding.contactUs01TextView.setText(R.string.frag_faqs);
        dataBinding.contactUs01TextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.popup_ic_ops_05_faq, 0, 0, 0);

        dataBinding.contactUs01Layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onConciergeFaqClick();
            }
        });

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

        dataBinding.kakaoShareView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onShareKakaoClick();
            }
        });

        // 예약 내역, 취소 내역은 안보여 주기로 함
        dataBinding.copyLinkLayout.setVisibility(View.GONE);

        dataBinding.moreShareView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onMoreShareClick();
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
        if (mGoogleMap == null || location == null)
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

    @Override
    public boolean isExpandedMap()
    {
        return getViewDataBinding().searchMapsLayout != null && getViewDataBinding().searchMapsLayout.getVisibility() != View.GONE;
    }

    @Override
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

    @Override
    public void setBookingDetailToolbar()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(R.string.actionbar_title_booking_cancel_list_activity);
        getViewDataBinding().toolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onBackClick();
            }
        });

        getViewDataBinding().toolbarView.clearMenuItem();
        getViewDataBinding().toolbarView.addMenuItem(DailyToolbarView.MenuItem.SHARE, null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onShareClick();
            }
        });

        getViewDataBinding().toolbarView.addMenuItem(DailyToolbarView.MenuItem.HELP, null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onConciergeClick();
            }
        });
    }

    @Override
    public void setBookingDetailMapToolbar()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(R.string.frag_tab_map_title);
        getViewDataBinding().toolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onCollapseMapClick();
            }
        });

        getViewDataBinding().toolbarView.clearMenuItem();
    }

    @Override
    public void setDeleteBookingVisible(boolean isVisible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().deleteReservationTextView.setText(R.string.label_booking_cancel_detail_delete_reservation);

        if (isVisible == true)
        {
            getViewDataBinding().deleteReservationTextView.setVisibility(View.VISIBLE);
            getViewDataBinding().deleteReservationTextView.setOnClickListener(this);

            LinearLayout.LayoutParams layoutParams = ((LinearLayout.LayoutParams) getViewDataBinding().deleteReservationTextView.getLayoutParams());

            if (layoutParams.height != LinearLayout.LayoutParams.WRAP_CONTENT)
            {
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;

                final int DP_37 = ScreenUtils.dpToPx(getContext(), 37);

                layoutParams.setMargins(0, DP_37, 0, 0);
                getViewDataBinding().deleteReservationTextView.setPadding(0, 0, 0, DP_37);
                getViewDataBinding().deleteReservationTextView.requestLayout();
            }
        } else
        {
            getViewDataBinding().deleteReservationTextView.setVisibility(View.INVISIBLE);
            getViewDataBinding().deleteReservationTextView.setOnClickListener(null);

            LinearLayout.LayoutParams layoutParams = ((LinearLayout.LayoutParams) getViewDataBinding().deleteReservationTextView.getLayoutParams());

            final int DP_24 = ScreenUtils.dpToPx(getContext(), 24);
            if (layoutParams.height != DP_24)
            {
                layoutParams.setMargins(0, 0, 0, 0);
                layoutParams.height = DP_24;
                getViewDataBinding().deleteReservationTextView.setPadding(0, 0, 0, 0);
                getViewDataBinding().deleteReservationTextView.requestLayout();
            }
        }
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
                        getEventListener().onExpandMapClick();
                    } else
                    {
                        getEventListener().onMapLoading();
                    }
                } else
                {
                    getEventListener().onMapClick();
                }
                break;
            }

            case R.id.viewDetailView:
                getEventListener().onViewDetailClick();
                break;

            case R.id.viewMapView:
                getEventListener().onNavigatorClick();
                break;

            case R.id.copyAddressView:
                getEventListener().onClipAddressClick();
                break;

            case R.id.searchMapView:
                getEventListener().onNavigatorClick();
                break;

            case R.id.deleteReservationTextView:
                getEventListener().onHiddenReservationClick();
                break;
        }
    }

    private void initToolbar(ActivityStayOutboundBookingCancelDetailDataBinding viewDataBinding)
    {
        setBookingDetailToolbar();
    }

    private void setHeaderLayout(Context context, StayOutboundBookingDetail stayOutboundBookingDetail)
    {
        if (context == null || stayOutboundBookingDetail == null || getViewDataBinding() == null)
        {
            return;
        }

        double width = ScreenUtils.getScreenWidth(context);
        double height = ScreenUtils.getRatioHeightType16x9(ScreenUtils.getScreenWidth(context));

        getViewDataBinding().googleMapLayout.setVisibility(View.VISIBLE);
        setGoogleMapLayout(context, stayOutboundBookingDetail, (int) width, (int) height);

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
                addMarker(mGoogleMap, stayOutboundBookingDetail.latitude, stayOutboundBookingDetail.longitude, stayOutboundBookingDetail.name);

                mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback()
                {
                    @Override
                    public void onMapLoaded()
                    {
                        mMapLoaded = true;

                        Projection projection = mGoogleMap.getProjection();

                        Point point = projection.toScreenLocation(new LatLng(stayOutboundBookingDetail.latitude, stayOutboundBookingDetail.longitude));
                        point.y += (point.y - getViewDataBinding().fakeMapLayout.getHeight() * 0.43);

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

    private void setCancelInformation(Context context, LayoutPlaceBookingCancelDetailDataBinding dataBinding, StayOutboundBookingDetail stayOutboundBookingDetail)
    {
        if (context == null || dataBinding == null || stayOutboundBookingDetail == null)
        {
            return;
        }

        try
        {
            dataBinding.cancelDateTextView.setText(DailyCalendar.convertDateFormatString(stayOutboundBookingDetail.cancelDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
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
}
