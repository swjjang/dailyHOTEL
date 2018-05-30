package com.daily.dailyhotel.screen.booking.detail.stay;

import android.animation.Animator;
import android.animation.ObjectAnimator;
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
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.daily.base.BaseActivity;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyScrollView;
import com.daily.dailyhotel.base.BaseBlurView;
import com.daily.dailyhotel.entity.Booking;
import com.daily.dailyhotel.entity.CarouselListItem;
import com.daily.dailyhotel.entity.RefundPolicy;
import com.daily.dailyhotel.entity.StayBookingDetail;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.daily.dailyhotel.view.carousel.DailyCarouselLayout;
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
import com.twoheart.dailyhotel.databinding.ActivityStayBookingDetailDataBinding;
import com.twoheart.dailyhotel.databinding.DialogConciergeDataBinding;
import com.twoheart.dailyhotel.databinding.DialogShareDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayBookingDetail01DataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayBookingDetail02DataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayBookingDetail03DataBinding;
import com.twoheart.dailyhotel.model.MyLocationMarker;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.place.adapter.PlaceNameInfoWindowAdapter;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class StayBookingDetailView extends BaseBlurView<StayBookingDetailView.OnEventListener, ActivityStayBookingDetailDataBinding> //
    implements StayBookingDetailInterface, View.OnClickListener
{
    ImageView mMyLocationView;
    Drawable mMyLocationDrawable;
    View mZoomControl;
    private MarkerOptions mMyLocationMarkerOptions;
    Marker mMyLocationMarker, mPlaceLocationMarker;
    GoogleMap mGoogleMap;
    LatLng mCenterLatLng;
    boolean mMapLoaded;

    private ObjectAnimator mRecommendGourmetButtonAnimator;

    private LayoutStayBookingDetail01DataBinding mBookingDetail01DataBinding;
    private LayoutStayBookingDetail02DataBinding mBookingDetail02DataBinding;
    private LayoutStayBookingDetail03DataBinding mBookingDetail03DataBinding;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onIssuingReceiptClick();

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

        void onFrontCallClick(String frontPhone);

        void onFrontReservationCallClick(String frontPhone);

        void onConciergeHappyTalkClick(boolean isRefund);

        void onConciergeCallClick(boolean isRefund);

        void onShareKakaoClick();

        void onMoreShareClick();

        void onHiddenReservationClick();

        void onReviewClick(String reviewStatus);

        void onRefundClick();

        void onRecommendListItemViewAllClick();

        void onRecommendListItemClick(View view);

        void onRecommendListItemLongClick(View view);
    }

    public StayBookingDetailView(BaseActivity baseActivity, StayBookingDetailView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayBookingDetailDataBinding viewDataBinding)
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
            , R.layout.layout_stay_booking_detail_01_data, viewDataBinding.detailsLayout, true);

        mBookingDetail02DataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()) //
            , R.layout.layout_stay_booking_detail_02_data, viewDataBinding.detailsLayout, true);

        mBookingDetail03DataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()) //
            , R.layout.layout_stay_booking_detail_03_data, viewDataBinding.detailsLayout, true);

        viewDataBinding.deleteReservationTextView.setPaintFlags(viewDataBinding.deleteReservationTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        viewDataBinding.recommendGourmetButtonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                viewDataBinding.scrollView.smoothScrollTo(0, viewDataBinding.recommendGourmetLayout.getBottom());
            }
        });

        viewDataBinding.recommendGourmetCarouselLayout.setTitleText(R.string.label_booking_reservation_recommend_gourmet_title);

        viewDataBinding.recommendGourmetCarouselLayout.setCarouselListener(new DailyCarouselLayout.OnCarouselListener()
        {
            @Override
            public void onViewAllClick()
            {
                getEventListener().onRecommendListItemViewAllClick();
            }

            @Override
            public void onItemClick(View view, android.support.v4.util.Pair[] pairs)
            {
                getEventListener().onRecommendListItemClick(view);
            }

            @Override
            public void onItemLongClick(View view, android.support.v4.util.Pair[] pairs)
            {
                getEventListener().onRecommendListItemLongClick(view);
            }
        });
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

    private void initToolbar(ActivityStayBookingDetailDataBinding viewDataBinding)
    {
        setBookingDetailToolbar();
    }

    @Override
    public void setBookingDetailToolbar()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(getString(R.string.actionbar_title_booking_list_frag));
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

        getViewDataBinding().toolbarView.setTitleText(getString(R.string.frag_tab_map_title));
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
    public void setBookingDetail(StayBookingDetail stayBookingDetail)
    {
        setHeaderLayout(getContext(), stayBookingDetail);

        setBookingInformation(stayBookingDetail);
        setTimeInformation(stayBookingDetail);
        setGuestInformation(stayBookingDetail);
        setPaymentInformation(stayBookingDetail);
        //        setRefundPolicyInformation(stayBookingDetail); // 이것은 따로 진행

        //        initHeaderInformationLayout(mContext, mScrollLayout, placeBookingDetail);
        //        initPlaceInformationLayout(mContext, mScrollLayout, todayDateTime, placeBookingDetail);
        //        initTimeInformationLayout(mContext, mScrollLayout, placeBookingDetail);
        //        initGuestInformationLayout(mContext, mScrollLayout, placeBookingDetail);
        //        initPaymentInformationLayout(mContext, mScrollLayout, placeBookingDetail);
        //        initRefundPolicyLayout(mContext, mScrollLayout, placeBookingDetail);
        //    }
    }

    @Override
    public void setRemindDate(String currentDateTime, StayBookingDetail stayBookingDetail)
    {
        if (getContext() == null || mBookingDetail01DataBinding == null || stayBookingDetail == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(currentDateTime) == true)
        {
            mBookingDetail01DataBinding.remainedDayLayout.setVisibility(View.GONE);
            return;
        }

        // 3일전 부터 몇일 남음 필요.
        if (stayBookingDetail.readyForRefund == true)
        {
            mBookingDetail01DataBinding.remainedDayLayout.setVisibility(View.GONE);
        } else
        {
            try
            {
                String remainedDayText;

                if (stayBookingDetail.waitingForBooking == true)
                {
                    remainedDayText = getString(R.string.message_booking_detail_wait_message);

                    mBookingDetail01DataBinding.remainedDayTextView.getLayoutParams().height = ScreenUtils.dpToPx(getContext(), 54);
                    mBookingDetail01DataBinding.remainedDayTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                    mBookingDetail01DataBinding.remainedDayTextView.setCompoundDrawablePadding(ScreenUtils.dpToPx(getContext(), 10));
                } else
                {
                    mBookingDetail01DataBinding.remainedDayTextView.getLayoutParams().height = ScreenUtils.dpToPx(getContext(), 36);
                    mBookingDetail01DataBinding.remainedDayTextView.setGravity(Gravity.CENTER);
                    mBookingDetail01DataBinding.remainedDayTextView.setCompoundDrawablePadding(ScreenUtils.dpToPx(getContext(), -30));

                    Date checkInDate = DailyCalendar.convertStringToDate(stayBookingDetail.checkInDateTime);
                    Date currentDate = DailyCalendar.convertStringToDate(currentDateTime);

                    int dayOfDays = (int) ((DailyCalendar.clearTField(checkInDate.getTime()) - DailyCalendar.clearTField(currentDate.getTime())) / DailyCalendar.DAY_MILLISECOND);
                    if (dayOfDays < 0 || dayOfDays > 3)
                    {
                        remainedDayText = null;
                    } else if (dayOfDays > 0)
                    {
                        // 하루이상 남음
                        remainedDayText = getString(R.string.frag_booking_duedate_formet_stay, dayOfDays);
                    } else
                    {
                        // 당일
                        remainedDayText = getString(R.string.frag_booking_today_type_stay);
                    }
                }

                if (DailyTextUtils.isTextEmpty(remainedDayText) == true)
                {
                    mBookingDetail01DataBinding.remainedDayLayout.setVisibility(View.GONE);
                } else
                {
                    mBookingDetail01DataBinding.remainedDayLayout.setVisibility(View.VISIBLE);
                    mBookingDetail01DataBinding.remainedDayTextView.setText(remainedDayText);
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
    }

    @Override
    public void setTimeInformation(StayBookingDetail stayBookingDetail)
    {
        if (getContext() == null || mBookingDetail01DataBinding == null || stayBookingDetail == null)
        {
            return;
        }

        final String DATE_FORMAT = "yyyy.MM.dd(EEE)";
        final String TIME_FORMAT = "HH:mm";

        try
        {
            String checkInTime = DailyCalendar.convertDateFormatString(stayBookingDetail.checkInDateTime, DailyCalendar.ISO_8601_FORMAT, TIME_FORMAT);
            String checkInDate = DailyCalendar.convertDateFormatString(stayBookingDetail.checkInDateTime, DailyCalendar.ISO_8601_FORMAT, DATE_FORMAT);

            SpannableString checkInDateSpannableString = new SpannableString(checkInDate + " " + checkInTime);
            checkInDateSpannableString.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(getContext()).getMediumTypeface()),//
                checkInDate.length(), checkInDate.length() + checkInTime.length() + 1,//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            String checkOutTime = DailyCalendar.convertDateFormatString(stayBookingDetail.checkOutDateTime, DailyCalendar.ISO_8601_FORMAT, TIME_FORMAT);
            String checkOutDate = DailyCalendar.convertDateFormatString(stayBookingDetail.checkOutDateTime, DailyCalendar.ISO_8601_FORMAT, DATE_FORMAT);

            SpannableString checkOutDateSpannableString = new SpannableString(checkOutDate + " " + checkOutTime);
            checkOutDateSpannableString.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(getContext()).getMediumTypeface()),//
                checkOutDate.length(), checkOutDate.length() + checkOutTime.length() + 1,//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            int nights = DailyCalendar.compareDateDay(stayBookingDetail.checkOutDateTime, stayBookingDetail.checkInDateTime);

            mBookingDetail01DataBinding.dateInformationView.setDateVisible(true, true);

            mBookingDetail01DataBinding.dateInformationView.setDate1Text(getString(R.string.label_check_in), checkInDateSpannableString);
            mBookingDetail01DataBinding.dateInformationView.setData1TextSize(13.0f, 13.0f);

            mBookingDetail01DataBinding.dateInformationView.setCenterNightsVisible(true);
            mBookingDetail01DataBinding.dateInformationView.setCenterNightsText(getString(R.string.label_nights, nights));

            mBookingDetail01DataBinding.dateInformationView.setDate2Text(getString(R.string.label_check_out), checkOutDateSpannableString);
            mBookingDetail01DataBinding.dateInformationView.setData2TextSize(13.0f, 13.0f);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void setHiddenBookingVisible(int bookingState)
    {
        if (getContext() == null || getViewDataBinding() == null)
        {
            return;
        }

        if (Booking.BOOKING_STATE_CANCEL == bookingState)
        {
            getViewDataBinding().deleteReservationTextView.setText(R.string.label_booking_cancel_detail_delete_reservation);
        } else
        {
            getViewDataBinding().deleteReservationTextView.setText(R.string.label_booking_detail_delete_reservation);
        }

        if (Booking.BOOKING_STATE_AFTER_USE == bookingState || Booking.BOOKING_STATE_CANCEL == bookingState)
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

    private void setHeaderLayout(Context context, StayBookingDetail stayBookingDetail)
    {
        if (context == null || getViewDataBinding() == null //
            || stayBookingDetail == null || stayBookingDetail == null)
        {
            return;
        }

        double width = ScreenUtils.getScreenWidth(context);
        double height = ScreenUtils.getRatioHeightType16x9(ScreenUtils.getScreenWidth(context));

        getViewDataBinding().googleMapLayout.setVisibility(View.VISIBLE);
        setGoogleMapLayout(context, stayBookingDetail, (int) width, (int) height);

        getViewDataBinding().viewDetailView.setOnClickListener(this);
        getViewDataBinding().viewMapView.setOnClickListener(this);
        getViewDataBinding().placeNameTextView.setText(stayBookingDetail.stayName);
        getViewDataBinding().inputReviewView.setOnClickListener(this);

        setReviewButtonLayout(stayBookingDetail.reviewStatusType);
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

    private void setGoogleMapLayout(Context context, StayBookingDetail stayBookingDetail, int width, int height)
    {
        if (context == null || getViewDataBinding() == null || stayBookingDetail == null)
        {
            return;
        }

        getViewDataBinding().addressLayout.setVisibility(View.GONE);
        getViewDataBinding().searchMapsLayout.setVisibility(View.GONE);
        getViewDataBinding().addressTextView.setText(stayBookingDetail.stayAddress);

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
                getEventListener().onNavigatorClick();
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

                onAfterScrollChanged(scrollView, l, t, oldl, oldt);
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
                addMarker(mGoogleMap, stayBookingDetail.latitude, stayBookingDetail.longitude, stayBookingDetail.stayName);

                mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback()
                {
                    @Override
                    public void onMapLoaded()
                    {
                        mMapLoaded = true;

                        Projection projection = mGoogleMap.getProjection();

                        Point point = projection.toScreenLocation(new LatLng(stayBookingDetail.latitude, stayBookingDetail.longitude));
                        point.y += point.y - (getViewDataBinding().fakeMapLayout.getHeight() * 0.43);

                        mCenterLatLng = projection.fromScreenLocation(point);
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(mCenterLatLng), 200, null);
                    }
                });
            }
        });
    }

    @Override
    public void setReviewButtonLayout(String reviewStatusType)
    {
        if (getContext() == null || getViewDataBinding() == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(reviewStatusType) == true)
        {
            reviewStatusType = PlaceBookingDetail.ReviewStatusType.NONE;
        }

        getViewDataBinding().inputReviewView.setTag(reviewStatusType);

        if (PlaceBookingDetail.ReviewStatusType.ADDABLE.equalsIgnoreCase(reviewStatusType) == true //
            || PlaceBookingDetail.ReviewStatusType.MODIFIABLE.equalsIgnoreCase(reviewStatusType) == true)
        {
            getViewDataBinding().inputReviewVerticalLine.setVisibility(View.VISIBLE);
            getViewDataBinding().inputReviewView.setVisibility(View.VISIBLE);
            getViewDataBinding().inputReviewView.setDrawableVectorTint(R.color.default_background_c454545);
            getViewDataBinding().inputReviewView.setTextColor(getContext().getResources().getColor(R.color.default_text_c4d4d4d));
        } else if (PlaceBookingDetail.ReviewStatusType.COMPLETE.equalsIgnoreCase(reviewStatusType) == true)
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
            getViewDataBinding().inputReviewView.setTextColor(getContext().getResources().getColor(R.color.default_text_c4d4d4d));
        }
    }

    private void setBookingInformation(StayBookingDetail stayBookingDetail)
    {
        if (mBookingDetail01DataBinding == null || stayBookingDetail == null)
        {
            return;
        }

        mBookingDetail01DataBinding.hotelNameTextView.setText(stayBookingDetail.stayName);
        mBookingDetail01DataBinding.roomTypeTextView.setText(stayBookingDetail.roomName);
        mBookingDetail01DataBinding.addressTextView.setText(stayBookingDetail.stayAddress);
    }

    private void setGuestInformation(StayBookingDetail stayBookingDetail)
    {
        if (getContext() == null || mBookingDetail01DataBinding == null || stayBookingDetail == null)
        {
            return;
        }

        mBookingDetail01DataBinding.guestNameTextView.setText(stayBookingDetail.guestName);
        mBookingDetail01DataBinding.guestPhoneTextView.setText(Util.addHyphenMobileNumber(getContext(), stayBookingDetail.guestPhone));
        mBookingDetail01DataBinding.guestEmailTextView.setText(stayBookingDetail.guestEmail);


        if (DailyTextUtils.isTextEmpty(stayBookingDetail.guestTransportation) == true)
        {
            mBookingDetail01DataBinding.visitTypeLayout.setVisibility(View.GONE);
            mBookingDetail01DataBinding.guideVisitMemoLayout.setVisibility(View.GONE);
        } else
        {
            switch (stayBookingDetail.guestTransportation)
            {
                case "CAR":
                    mBookingDetail01DataBinding.visitTypeLayout.setVisibility(View.VISIBLE);

                    mBookingDetail01DataBinding.visitTypeTitleTextView.setText(R.string.label_how_to_visit);
                    mBookingDetail01DataBinding.visitTypeTextView.setText(R.string.label_visit_car);

                    mBookingDetail01DataBinding.guideVisitMemoLayout.setVisibility(View.VISIBLE);
                    mBookingDetail01DataBinding.guideVisitMemoView.setText(R.string.message_visit_car_memo);
                    break;

                case "NO_PARKING":
                    mBookingDetail01DataBinding.visitTypeLayout.setVisibility(View.VISIBLE);

                    mBookingDetail01DataBinding.visitTypeTitleTextView.setText(R.string.label_parking_information);
                    mBookingDetail01DataBinding.visitTypeTextView.setText(R.string.label_no_parking);

                    mBookingDetail01DataBinding.guideVisitMemoLayout.setVisibility(View.VISIBLE);
                    mBookingDetail01DataBinding.guideVisitMemoView.setText(R.string.message_visit_no_parking_memo);
                    break;

                case "WALKING":
                    mBookingDetail01DataBinding.visitTypeLayout.setVisibility(View.VISIBLE);

                    mBookingDetail01DataBinding.visitTypeTitleTextView.setText(R.string.label_how_to_visit);
                    mBookingDetail01DataBinding.visitTypeTextView.setText(R.string.label_visit_walk);

                    mBookingDetail01DataBinding.guideVisitMemoLayout.setVisibility(View.GONE);
                    break;

                default:
                    mBookingDetail01DataBinding.visitTypeLayout.setVisibility(View.GONE);
                    mBookingDetail01DataBinding.guideVisitMemoLayout.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private void setPaymentInformation(StayBookingDetail stayBookingDetail)
    {
        if (getContext() == null || mBookingDetail02DataBinding == null || stayBookingDetail == null)
        {
            return;
        }

        try
        {
            mBookingDetail02DataBinding.paymentDateTextView.setText( //
                DailyCalendar.convertDateFormatString(stayBookingDetail.paymentDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        mBookingDetail02DataBinding.priceTextView.setText( //
            DailyTextUtils.getPriceFormat(getContext(), stayBookingDetail.discountTotal, false));


        if (stayBookingDetail.bonusAmount > 0)
        {
            mBookingDetail02DataBinding.bonusLayout.setVisibility(View.VISIBLE);
            mBookingDetail02DataBinding.bonusTextView.setText( //
                "- " + DailyTextUtils.getPriceFormat(getContext(), stayBookingDetail.bonusAmount, false));
        } else
        {
            mBookingDetail02DataBinding.bonusLayout.setVisibility(View.GONE);
        }

        if (stayBookingDetail.couponAmount > 0)
        {
            mBookingDetail02DataBinding.couponLayout.setVisibility(View.VISIBLE);
            mBookingDetail02DataBinding.couponTextView.setText( //
                "- " + DailyTextUtils.getPriceFormat(getContext(), stayBookingDetail.couponAmount, false));
        } else
        {
            mBookingDetail02DataBinding.couponLayout.setVisibility(View.GONE);
        }

        mBookingDetail02DataBinding.totalPriceTextView.setText( //
            DailyTextUtils.getPriceFormat(getContext(), stayBookingDetail.priceTotal, false));

        // 영수증 발급
        mBookingDetail02DataBinding.buttonLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onIssuingReceiptClick();
            }
        });
    }

    @Override
    public void setRefundPolicyInformation(boolean isVisibleRefundPolicy, boolean readyForRefund, RefundPolicy refundPolicy)
    {
        if (getContext() == null || mBookingDetail03DataBinding == null || getViewDataBinding() == null)
        {
            return;
        }

        // refundPolicy 의 경우 null 체크 안함 null 로 넘어 올 수 있음
        String comment = refundPolicy == null ? "" : refundPolicy.comment;
        if (DailyTextUtils.isTextEmpty(comment) == false)
        {
            switch (refundPolicy.refundPolicy)
            {
                case RefundPolicy.STATUS_NRD:
                    comment = comment.replaceAll("900034", "EB2135");
                    break;

                case RefundPolicy.STATUS_NO_CHARGE_REFUND:
                case RefundPolicy.STATUS_SURCHARGE_REFUND:
                case RefundPolicy.STATUS_WAIT_REFUND:
                    comment = comment.replaceAll("900034", "2C8DE6");
                    break;
            }

            mBookingDetail03DataBinding.refundPolicyTextView.setText(Html.fromHtml(comment));
        }

        // 정책을 보여주지 않을 경우
        if (isVisibleRefundPolicy == false)
        {
            mBookingDetail03DataBinding.refundPolicyLayout.setVisibility(View.GONE);

            // 해당 카테고리가 없으면 번호가 바뀐다.
            getViewDataBinding().depositStickerTextView.setText("3. " + getString(R.string.label_booking_used_deposit_sticker));
        } else
        {
            mBookingDetail03DataBinding.refundPolicyLayout.setVisibility(View.VISIBLE);

            // 해당 카테고리가 없으면 번호가 바뀐다.
            getViewDataBinding().depositStickerTextView.setText("4. " + getString(R.string.label_booking_used_deposit_sticker));

            switch (getRefundPolicyStatus(readyForRefund, refundPolicy))
            {
                case RefundPolicy.STATUS_NO_CHARGE_REFUND:
                {
                    mBookingDetail03DataBinding.defaultRefundPolicyLayout.setVisibility(View.VISIBLE);
                    mBookingDetail03DataBinding.defaultRefundPolicyUnderLine.setVisibility(View.GONE);
                    mBookingDetail03DataBinding.waitRefundPolicyLayout.setVisibility(View.GONE);

                    mBookingDetail03DataBinding.refundButtonLayout.setOnClickListener(this);
                    mBookingDetail03DataBinding.buttonTextView.setText(R.string.label_request_free_refund);
                    break;
                }

                case RefundPolicy.STATUS_WAIT_REFUND:
                {
                    mBookingDetail03DataBinding.defaultRefundPolicyLayout.setVisibility(View.GONE);
                    mBookingDetail03DataBinding.defaultRefundPolicyUnderLine.setVisibility(View.GONE);
                    mBookingDetail03DataBinding.waitRefundPolicyLayout.setVisibility(View.VISIBLE);

                    mBookingDetail03DataBinding.waitRefundPolicyTextView.setText(Html.fromHtml(getString(R.string.message_please_wait_refund01)));

                    mBookingDetail03DataBinding.refundButtonLayout.setOnClickListener(this);
                    mBookingDetail03DataBinding.buttonTextView.setText(R.string.label_contact_refund);
                    break;
                }

                case RefundPolicy.STATUS_SURCHARGE_REFUND:
                {
                    mBookingDetail03DataBinding.defaultRefundPolicyLayout.setVisibility(View.VISIBLE);
                    mBookingDetail03DataBinding.defaultRefundPolicyUnderLine.setVisibility(View.GONE);
                    mBookingDetail03DataBinding.waitRefundPolicyLayout.setVisibility(View.GONE);

                    mBookingDetail03DataBinding.refundButtonLayout.setOnClickListener(this);
                    mBookingDetail03DataBinding.buttonTextView.setText(R.string.label_contact_refund);
                    break;
                }

                default:
                {
                    mBookingDetail03DataBinding.defaultRefundPolicyLayout.setVisibility(View.VISIBLE);
                    mBookingDetail03DataBinding.defaultRefundPolicyUnderLine.setVisibility(View.VISIBLE);
                    mBookingDetail03DataBinding.waitRefundPolicyLayout.setVisibility(View.GONE);

                    mBookingDetail03DataBinding.refundButtonLayout.setOnClickListener(null);
                    mBookingDetail03DataBinding.refundButtonLayout.setVisibility(View.GONE);
                    break;
                }
            }
        }
    }

    private String getRefundPolicyStatus(boolean readyForRefund, RefundPolicy refundPolicy)
    {
        // 환불 대기 상태
        if (readyForRefund == true)
        {
            return RefundPolicy.STATUS_WAIT_REFUND;
        } else
        {
            if (refundPolicy != null && DailyTextUtils.isTextEmpty(refundPolicy.refundPolicy) == false)
            {
                return refundPolicy.refundPolicy;
            } else
            {
                return RefundPolicy.STATUS_SURCHARGE_REFUND;
            }
        }
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
    public void setMyLocation(Location location)
    {
        if (mGoogleMap == null || location == null || getContext() == null)
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

    @Override
    public void onClick(final View v)
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
            {
                getEventListener().onViewDetailClick();
                break;
            }

            case R.id.viewMapView:
            {
                getEventListener().onNavigatorClick();
                break;
            }

            case R.id.refundButtonLayout:
            {
                getEventListener().onRefundClick();
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

                getEventListener().onReviewClick(reviewStatus);
                break;
            }

            case R.id.deleteReservationTextView:
                getEventListener().onHiddenReservationClick();
                break;
        }
    }

    @Override
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
    public void showConciergeDialog(String frontPhone1, String frontPhone2, String currentDateTime, Dialog.OnDismissListener listener)
    {
        DialogConciergeDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_concierge_data, null, false);

        // 버튼
        //        dataBinding.contactUs02Layout.setVisibility(View.GONE);

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

        if (DailyTextUtils.isTextEmpty(frontPhone1) == true)
        {
            dataBinding.contactUs02Layout.setVisibility(View.GONE);
        } else
        {
            dataBinding.contactUs02TextView.setText(R.string.label_hotel_front_phone);
            dataBinding.contactUs02TextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.popup_ic_ops_01_store_call, 0, 0, 0);

            dataBinding.contactUs02Layout.setVisibility(View.VISIBLE);
            dataBinding.contactUs02Layout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    hideSimpleDialog();

                    getEventListener().onFrontCallClick(frontPhone1);
                }
            });
        }

        try
        {
            Calendar calendar = DailyCalendar.getInstance(currentDateTime, DailyCalendar.ISO_8601_FORMAT);

            int time = calendar.get(Calendar.HOUR_OF_DAY) * 100 + calendar.get(Calendar.MINUTE);

            if (DailyTextUtils.isTextEmpty(frontPhone2) == false && (time >= 900 && time <= 2000))
            {
                dataBinding.contactUs03TextView.setText(R.string.label_hotel_reservation_phone);
                dataBinding.contactUs03TextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.popup_ic_ops_01_store_call, 0, 0, 0);

                dataBinding.contactUs03Layout.setVisibility(View.VISIBLE);
                dataBinding.contactUs03Layout.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        hideSimpleDialog();

                        getEventListener().onFrontReservationCallClick(frontPhone2);
                    }
                });
            } else
            {
                dataBinding.contactUs03Layout.setVisibility(View.GONE);
            }

        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        dataBinding.kakaoDailyView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onConciergeHappyTalkClick(false);
            }
        });

        dataBinding.callDailyView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onConciergeCallClick(false);
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

        // 예약 내역의 경우 상세 링크로 인하여 혼선이 있을 것으로 보여 삭제하기로 함
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
    public void showRefundCallDialog(Dialog.OnDismissListener listener)
    {
        DialogConciergeDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_concierge_data, null, false);

        dataBinding.contactUs01Layout.setVisibility(View.GONE);
        dataBinding.contactUs02Layout.setVisibility(View.GONE);
        dataBinding.contactUs02Layout.setVisibility(View.GONE);

        dataBinding.kakaoDailyView.setText(R.string.label_contact_refund_kakao);
        dataBinding.callDailyView.setText(R.string.label_contact_refund_daily);


        dataBinding.kakaoDailyView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onConciergeHappyTalkClick(true);
            }
        });

        dataBinding.callDailyView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onConciergeCallClick(true);
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////  고메  추천  ///////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void setRecommendGourmetLayoutVisible(boolean isVisible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().recommendGourmetLayout.setVisibility(isVisible == true ? View.VISIBLE : View.GONE);

        if (getViewDataBinding().recommendGourmetButtonView == null)
        {
            return;
        }

        setRecommendGourmetButtonAnimation(isVisible);
    }

    @Override
    public ArrayList<CarouselListItem> getRecommendGourmetData()
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        return getViewDataBinding().recommendGourmetCarouselLayout.getData();
    }

    @Override
    public void setRecommendGourmetData(ArrayList<CarouselListItem> list)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().recommendGourmetCarouselLayout.setData(list, false);
        setRecommendGourmetLayoutVisible(list != null && list.size() > 0);
    }

    @Override
    public void setRecommendGourmetButtonAnimation(boolean isVisible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        boolean isOldVisible = getViewDataBinding().recommendGourmetButtonView.getVisibility() == View.VISIBLE;
        if (isOldVisible == isVisible)
        {
            return;
        }

        if (mRecommendGourmetButtonAnimator != null)
        {
            mRecommendGourmetButtonAnimator.cancel();
            mRecommendGourmetButtonAnimator = null;
        }

        if (isVisible == true)
        {
            getViewDataBinding().recommendGourmetButtonView.setVisibility(View.VISIBLE);

            float transY = ScreenUtils.dpToPx(getContext(), 6d);

            mRecommendGourmetButtonAnimator = ObjectAnimator.ofFloat(getViewDataBinding().recommendGourmetButtonView, "translationY", 0.0f, transY, 0.0f);
            mRecommendGourmetButtonAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            mRecommendGourmetButtonAnimator.setDuration(1600);
            mRecommendGourmetButtonAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            mRecommendGourmetButtonAnimator.start();
        } else
        {
            getViewDataBinding().recommendGourmetButtonView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAfterScrollChanged(ScrollView scrollView, int l, int t, int oldl, int oldt)
    {
        if (getViewDataBinding() == null || getContext() == null)
        {
            return;
        }

        if (isExpandedMap() == true)
        {
            return;
        }

        if (getViewDataBinding().recommendGourmetCarouselLayout.hasData() == false //
            || View.VISIBLE != getViewDataBinding().recommendGourmetLayout.getVisibility())
        {
            return;
        }

        int expectedY = getViewDataBinding().recommendGourmetLayout.getTop() - ScreenUtils.getScreenHeight(getContext()) //
            + getContext().getResources().getDimensionPixelOffset(R.dimen.toolbar_height) + ScreenUtils.dpToPx(getContext(), 75d);

        //        ExLog.d("expectedY : " + expectedY + " , t : " + t);

        if (expectedY <= t)
        {
            setRecommendGourmetButtonAnimation(false);
        } else
        {
            setRecommendGourmetButtonAnimation(true);
        }
    }

    public void setDepositStickerCardVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().rewardCardLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setDepositStickerCard(String titleText, int nights)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().rewardCardView.setGuideVisible(false);
        getViewDataBinding().rewardCardView.setOptionVisible(false);
        getViewDataBinding().rewardCardView.setRewardTitleText(titleText);
        getViewDataBinding().rewardCardView.setStickerCount(nights);

        getViewDataBinding().rewardCardView.setWarningVisible(false);
        getViewDataBinding().rewardCardView.setDescriptionVisible(false);
        getViewDataBinding().rewardCardView.setPadding(0, 0, 0, ScreenUtils.dpToPx(getContext(), 30));
    }
}
