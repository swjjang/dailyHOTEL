package com.twoheart.dailyhotel.screen.booking.detail.hotel;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.location.Location;
import android.net.Uri;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
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
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.StayBookingDetail;
import com.twoheart.dailyhotel.place.adapter.PlaceNameInfoWindowAdapter;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;
import com.twoheart.dailyhotel.widget.DailyScrollView;
import com.twoheart.dailyhotel.widget.DailyTextView;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.widget.FontManager;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class StayReservationDetailLayout extends BaseLayout implements View.OnClickListener
{
    private DailyScrollView mScrollLayout;
    private View mRefundPolicyLayout, mButtonBottomMarginView;
    private View mDefaultRefundPolicyLayout, mWaitRefundPolicyLayout;
    private View mPlaceInformationLayout;

    private View mInputReviewVerticalLine, mMapExpandedView;
    private DailyTextView mInputReviewView;
    private DailyToolbarLayout mDailyToolbarLayout;

    // Map
    private boolean mIsReadyMap;
    private RelativeLayout mGoogleMapLayout;
    private FrameLayout mMapLayout;
    private GoogleMap mGoogleMap;
    private View mMyLocationView, mZoomControl;
    MarkerOptions mMyLocationMarkerOptions;
    Marker mMyLocationMarker, mPlaceLocationMarker;

    private View mAddressLayout, mSearchMapsLayout;

    interface OnEventListener extends OnBaseEventListener
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
    }

    public StayReservationDetailLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbar(view, mContext.getString(R.string.actionbar_title_booking_list_frag));

        mGoogleMapLayout = (RelativeLayout) view.findViewById(R.id.googleMapLayout);
        mScrollLayout = (DailyScrollView) view.findViewById(R.id.scrollLayout);
        EdgeEffectColor.setEdgeGlowColor(mScrollLayout, mContext.getResources().getColor(R.color.default_over_scroll_edge));
    }

    protected void initToolbar(View view, String title)
    {
        View toolbar = view.findViewById(R.id.toolbar);
        mDailyToolbarLayout = new DailyToolbarLayout(mContext, toolbar);

        setBookingToolbar();
    }

    private void setBookingToolbar()
    {
        mDailyToolbarLayout.initToolbar(mContext.getString(R.string.actionbar_title_booking_list_frag), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.finish();
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
                        ((OnEventListener) mOnEventListener).showCallDialog();
                        break;

                    case R.id.menu2View:
                        ((OnEventListener) mOnEventListener).showShareDialog();
                        break;
                }
            }
        });
    }

    private void setMapToolbar()
    {
        mDailyToolbarLayout.initToolbar(mContext.getString(R.string.frag_tab_map_title), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.finish();
            }
        });

        mDailyToolbarLayout.setToolbarMenu(-1, -1);
        mDailyToolbarLayout.setToolbarMenuClickListener(null);
    }

    public void initLayout(StayBookingDetail stayBookingDetail)
    {
        initPlaceInformationLayout(mContext, mScrollLayout, stayBookingDetail);
        initHotelInformationLayout(mContext, mScrollLayout, stayBookingDetail);
        initGuestInformationLayout(mScrollLayout, stayBookingDetail);
        initPaymentInformationLayout(mScrollLayout, stayBookingDetail);
        initRefundPolicyLayout(mScrollLayout, stayBookingDetail);
    }

    private void initPlaceInformationLayout(Context context, View view, StayBookingDetail stayBookingDetail)
    {
        if (view == null || stayBookingDetail == null)
        {
            return;
        }

        double width = Util.getLCDWidth(context);
        double height = Util.getListRowHeight(context);
        final float PLACE_INFORMATION_LAYOUT_RATIO = 0.72f;

        // Map 4 :2 비율 맞추기
        com.facebook.drawee.view.SimpleDraweeView mapImageView = (com.facebook.drawee.view.SimpleDraweeView) view.findViewById(R.id.mapImageView);
        mapImageView.setOnClickListener(this);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mapImageView.getLayoutParams();
        layoutParams.width = (int) width;
        layoutParams.height = (int) height;

        mapImageView.setLayoutParams(layoutParams);

        mMapExpandedView = view.findViewById(R.id.mapExpandedView);

        if (Util.isInstallGooglePlayService(context) == false)
        {
            mGoogleMapLayout.setVisibility(View.GONE);

            initImageMapLayout(context, mapImageView, stayBookingDetail, (int) width, (int) height);
        } else
        {
            mGoogleMapLayout.setVisibility(View.VISIBLE);

            initGoogleMapLayout(context, mGoogleMapLayout, stayBookingDetail, (int) width, (int) height);
        }

        mPlaceInformationLayout = view.findViewById(R.id.placeInformationLayout);
        RelativeLayout.LayoutParams placeInformationLayoutParams = (RelativeLayout.LayoutParams) mPlaceInformationLayout.getLayoutParams();
        placeInformationLayoutParams.topMargin = (int) (PLACE_INFORMATION_LAYOUT_RATIO * height);
        mPlaceInformationLayout.setLayoutParams(placeInformationLayoutParams);

        TextView placeNameTextView = (TextView) view.findViewById(R.id.placeNameTextView);
        placeNameTextView.setText(stayBookingDetail.placeName);

        View viewDetailView = view.findViewById(R.id.viewDetailView);
        View viewMapView = view.findViewById(R.id.viewMapView);

        viewDetailView.setOnClickListener(this);
        viewMapView.setOnClickListener(this);

        mPlaceInformationLayout.setVisibility(View.VISIBLE);

        initReviewButtonLayout(view, stayBookingDetail);
    }

    private void initImageMapLayout(Context context, com.facebook.drawee.view.SimpleDraweeView mapImageView, StayBookingDetail stayBookingDetail, int height, int width)
    {
        final double ratio = height / width;
        final float PLACE_INFORMATION_LAYOUT_RATIO = 0.72f;

        mapImageView.getHierarchy().setActualImageFocusPoint(new PointF(0.5f, PLACE_INFORMATION_LAYOUT_RATIO));

        if (width >= 720)
        {
            width = 720;
        }

        String size = String.format("%dx%d", (int) width * 3 / 5, (int) (width * ratio * 5) / 7);
        String iconUrl = "http://img.dailyhotel.me/app_static/info_ic_map_large.png";
        String url = String.format("https://maps.googleapis.com/maps/api/staticmap?zoom=17&size=%s&markers=icon:%s|%s,%s&sensor=false&scale=2&format=png8&mobile=true&key=%s"//
            , size, iconUrl, stayBookingDetail.latitude, stayBookingDetail.longitude, Crypto.getUrlDecoderEx(Constants.GOOGLE_MAP_KEY));

        mapImageView.setImageURI(Uri.parse(url));
    }

    private void initGoogleMapLayout(Context context, View view, final StayBookingDetail stayBookingDetail, int width, int height)
    {
        if (context == null || view == null || stayBookingDetail == null)
        {
            return;
        }

        mAddressLayout = view.findViewById(R.id.addressLayout);
        mSearchMapsLayout = view.findViewById(R.id.searchMapsLayout);

        mAddressLayout.setVisibility(View.GONE);
        mSearchMapsLayout.setVisibility(View.GONE);

        TextView addressTextView = (TextView) mAddressLayout.findViewById(R.id.addressTextView);
        addressTextView.setText(stayBookingDetail.address);

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

        mMapLayout = (FrameLayout) view.findViewById(R.id.mapLayout);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mMapLayout.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;

        mMapLayout.setLayoutParams(layoutParams);
        mMapLayout.setTag(height);

        mScrollLayout.setOnScrollChangedListener(new DailyScrollView.OnScrollChangedListener()
        {
            @Override
            public void onScrollChanged(ScrollView scrollView, int l, int t, int oldl, int oldt)
            {
                mMapLayout.setTranslationY(-t);
            }
        });

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();

        ((BaseActivity) mContext).getSupportFragmentManager().beginTransaction().add(mMapLayout.getId(), mapFragment).commitAllowingStateLoss();

        mapFragment.getMapAsync(new OnMapReadyCallback()
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

                relocationMyLocation(mMapLayout);
                relocationZoomControl(mMapLayout);
                addMarker(mGoogleMap, stayBookingDetail.latitude, stayBookingDetail.longitude, stayBookingDetail.placeName);

                mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback()
                {
                    @Override
                    public void onMapLoaded()
                    {
                        mIsReadyMap = true;
                    }
                });
            }
        });
    }

    private void initReviewButtonLayout(View view, StayBookingDetail bookingDetail)
    {
        if (view == null || bookingDetail == null)
        {
            return;
        }

        mInputReviewVerticalLine = view.findViewById(R.id.inputReviewVerticalLine);
        mInputReviewView = (DailyTextView) view.findViewById(R.id.inputReviewView);
        mInputReviewView.setOnClickListener(this);

        String reviewStatus = bookingDetail.reviewStatusType;
        updateReviewButtonLayout(reviewStatus);
    }

    public void updateReviewButtonLayout(String reviewStatus)
    {
        if (Util.isTextEmpty(reviewStatus) == true)
        {
            reviewStatus = PlaceBookingDetail.ReviewStatusType.NONE;
        }

        mInputReviewView.setTag(reviewStatus);

        if (PlaceBookingDetail.ReviewStatusType.ADDABLE.equalsIgnoreCase(reviewStatus) == true)
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
        } else
        {
            mInputReviewVerticalLine.setVisibility(View.GONE);
            mInputReviewView.setVisibility(View.GONE);
            mInputReviewView.setDrawableVectorTint(R.color.default_background_c454545);
            mInputReviewView.setTextColor(mContext.getResources().getColor(R.color.default_text_c323232));
        }
    }

    private void initHotelInformationLayout(Context context, View view, StayBookingDetail bookingDetail)
    {
        if (context == null || view == null || bookingDetail == null)
        {
            return;
        }

        // 3일전 부터 몇일 남음 필요.
        View remainedDayLayout = view.findViewById(R.id.remainedDayLayout);
        TextView remainedDayTextView = (TextView) view.findViewById(R.id.remainedDayTextView);

        if (bookingDetail.readyForRefund == true)
        {
            remainedDayLayout.setVisibility(View.GONE);
        } else
        {
            try
            {
                String remainedDayText;

                Date checkInDate = DailyCalendar.convertDate(bookingDetail.checkInDate, DailyCalendar.ISO_8601_FORMAT);

                int dayOfDays = (int) ((getCompareDate(checkInDate.getTime()) - getCompareDate(bookingDetail.currentDateTime)) / SaleTime.MILLISECOND_IN_A_DAY);
                if (dayOfDays < 0 || dayOfDays > 3)
                {
                    remainedDayText = null;
                } else if (dayOfDays > 0)
                {
                    // 하루이상 남음
                    remainedDayText = context.getString(R.string.frag_booking_duedate_formet, dayOfDays);
                } else
                {
                    // 당일
                    remainedDayText = context.getString(R.string.frag_booking_today_type_stay);
                }

                if (Util.isTextEmpty(remainedDayText) == true)
                {
                    remainedDayLayout.setVisibility(View.GONE);
                } else
                {
                    remainedDayLayout.setVisibility(View.VISIBLE);
                    remainedDayTextView.setText(remainedDayText);
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }

        // 체크인 체크아웃
        initTimeInformationLayout(context, view, bookingDetail);

        // 예약 장소
        TextView hotelNameTextView = (TextView) view.findViewById(R.id.hotelNameTextView);
        TextView roomTypeTextView = (TextView) view.findViewById(R.id.roomTypeTextView);
        TextView addressTextView = (TextView) view.findViewById(R.id.addressTextView);

        hotelNameTextView.setText(bookingDetail.placeName);
        roomTypeTextView.setText(bookingDetail.roomName);
        addressTextView.setText(bookingDetail.address);
    }

    private void initTimeInformationLayout(Context context, View view, StayBookingDetail bookingDetail)
    {
        if (context == null || view == null || bookingDetail == null)
        {
            return;
        }

        TextView checkinDayTextView = (TextView) view.findViewById(R.id.checkinDayTextView);
        TextView checkoutDayTextView = (TextView) view.findViewById(R.id.checkoutDayTextView);
        TextView nightsTextView = (TextView) view.findViewById(R.id.nightsTextView);

        try
        {
            String checkInDateFormat = DailyCalendar.convertDateFormatString(bookingDetail.checkInDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.M.d(EEE) HH시");
            SpannableStringBuilder checkInSpannableStringBuilder = new SpannableStringBuilder(checkInDateFormat);
            checkInSpannableStringBuilder.setSpan(new CustomFontTypefaceSpan(FontManager.getInstance(context).getMediumTypeface()),//
                checkInDateFormat.length() - 3, checkInDateFormat.length(),//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            checkinDayTextView.setText(checkInSpannableStringBuilder);
        } catch (Exception e)
        {
            checkinDayTextView.setText(null);
        }

        try
        {
            String checkOutDateFormat = DailyCalendar.convertDateFormatString(bookingDetail.checkOutDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.M.d(EEE) HH시");
            SpannableStringBuilder checkOutSpannableStringBuilder = new SpannableStringBuilder(checkOutDateFormat);
            checkOutSpannableStringBuilder.setSpan(new CustomFontTypefaceSpan(FontManager.getInstance(context).getMediumTypeface()),//
                checkOutDateFormat.length() - 3, checkOutDateFormat.length(),//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            checkoutDayTextView.setText(checkOutSpannableStringBuilder);
        } catch (Exception e)
        {
            checkoutDayTextView.setText(null);
        }

        try
        {
            // 날짜로 계산한다. 서버에 체크인시간, 체크아웃시간이 잘못 기록되어있는 경우 발생해서 예외 처리 추가
            String[] checkInDates = bookingDetail.checkInDate.split("T");
            String[] checkOutDates = bookingDetail.checkOutDate.split("T");

            Date checkInDate = DailyCalendar.convertDate(checkInDates[0] + "T00:00:00+09:00", DailyCalendar.ISO_8601_FORMAT);
            Date checkOutDate = DailyCalendar.convertDate(checkOutDates[0] + "T00:00:00+09:00", DailyCalendar.ISO_8601_FORMAT);

            int nights = (int) ((getCompareDate(checkOutDate.getTime()) - getCompareDate(checkInDate.getTime())) / SaleTime.MILLISECOND_IN_A_DAY);
            nightsTextView.setText(context.getString(R.string.label_nights, nights));
        } catch (Exception e)
        {
            nightsTextView.setText(null);
        }
    }

    private void initGuestInformationLayout(View view, StayBookingDetail bookingDetail)
    {
        if (view == null || bookingDetail == null)
        {
            return;
        }

        TextView guestNameTextView = (TextView) view.findViewById(R.id.guestNameTextView);
        TextView guestPhoneTextView = (TextView) view.findViewById(R.id.guestPhoneTextView);
        TextView guestEmailTextView = (TextView) view.findViewById(R.id.guestEmailTextView);

        guestNameTextView.setText(bookingDetail.guestName);
        guestPhoneTextView.setText(Util.addHyphenMobileNumber(mContext, bookingDetail.guestPhone));
        guestEmailTextView.setText(bookingDetail.guestEmail);

        View visitTypeLayout = view.findViewById(R.id.visitTypeLayout);
        View guideVisitMemoLayout = view.findViewById(R.id.guideVisitMemoLayout);

        TextView visitTypeTitleTextView = (TextView) view.findViewById(R.id.visitTypeTitleTextView);
        TextView visitTypeTextView = (TextView) view.findViewById(R.id.visitTypeTextView);
        TextView guideVisitMemoView = (TextView) view.findViewById(R.id.guideVisitMemoView);

        switch (bookingDetail.visitType)
        {
            case StayBookingDetail.VISIT_TYPE_CAR:
                visitTypeLayout.setVisibility(View.VISIBLE);

                visitTypeTitleTextView.setText(R.string.label_how_to_visit);
                visitTypeTextView.setText(R.string.label_visit_car);

                guideVisitMemoLayout.setVisibility(View.VISIBLE);
                guideVisitMemoView.setText(R.string.message_visit_car_memo);
                break;

            case StayBookingDetail.VISIT_TYPE_NO_PARKING:
                visitTypeLayout.setVisibility(View.VISIBLE);

                visitTypeTitleTextView.setText(R.string.label_parking_information);
                visitTypeTextView.setText(R.string.label_no_parking);

                guideVisitMemoLayout.setVisibility(View.VISIBLE);
                guideVisitMemoView.setText(R.string.message_visit_no_parking_memo);
                break;

            case StayBookingDetail.VISIT_TYPE_WALKING:
                visitTypeLayout.setVisibility(View.VISIBLE);

                visitTypeTitleTextView.setText(R.string.label_how_to_visit);
                visitTypeTextView.setText(R.string.label_visit_walk);

                guideVisitMemoLayout.setVisibility(View.GONE);
                break;

            default:
                visitTypeLayout.setVisibility(View.GONE);
                guideVisitMemoLayout.setVisibility(View.GONE);
                break;
        }
    }

    private void initPaymentInformationLayout(View view, StayBookingDetail bookingDetail)
    {
        if (view == null || bookingDetail == null)
        {
            return;
        }

        TextView paymentDateTextView = (TextView) view.findViewById(R.id.paymentDateTextView);
        TextView priceTextView = (TextView) view.findViewById(R.id.priceTextView);

        View bonusLayout = view.findViewById(R.id.bonusLayout);
        View couponLayout = view.findViewById(R.id.couponLayout);
        TextView bonusTextView = (TextView) view.findViewById(R.id.bonusTextView);
        TextView couponTextView = (TextView) view.findViewById(R.id.couponTextView);
        TextView totalPriceTextView = (TextView) view.findViewById(R.id.totalPriceTextView);

        try
        {
            //            paymentDateTextView.setText(Util.simpleDateFormatISO8601toFormat(bookingDetail.paymentDate, "yyyy.MM.dd"));
            paymentDateTextView.setText(DailyCalendar.convertDateFormatString(bookingDetail.paymentDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        priceTextView.setText(Util.getPriceFormat(mContext, bookingDetail.price, false));


        if (bookingDetail.bonus > 0)
        {
            bonusLayout.setVisibility(View.VISIBLE);
            bonusTextView.setText("- " + Util.getPriceFormat(mContext, bookingDetail.bonus, false));
        } else
        {
            bonusLayout.setVisibility(View.GONE);
        }

        if (bookingDetail.coupon > 0)
        {
            couponLayout.setVisibility(View.VISIBLE);
            couponTextView.setText("- " + Util.getPriceFormat(mContext, bookingDetail.coupon, false));
        } else
        {
            couponLayout.setVisibility(View.GONE);
        }

        totalPriceTextView.setText(Util.getPriceFormat(mContext, bookingDetail.paymentPrice, false));

        // 영수증 발급
        View confirmView = view.findViewById(R.id.buttonLayout);
        mButtonBottomMarginView = confirmView.findViewById(R.id.buttonBottomMarginView);


        confirmView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onIssuingReceiptClick();
            }
        });
    }

    private void initRefundPolicyLayout(View view, StayBookingDetail bookingDetail)
    {
        if (view == null || bookingDetail == null)
        {
            return;
        }

        mRefundPolicyLayout = view.findViewById(R.id.refundPolicyLayout);
        mDefaultRefundPolicyLayout = view.findViewById(R.id.defaultRefundPolicyLayout);
        mWaitRefundPolicyLayout = view.findViewById(R.id.waitRefundPolicyLayout);

        updateRefundPolicyLayout(bookingDetail);
    }

    public void updateRefundPolicyLayout(StayBookingDetail bookingDetail)
    {
        TextView refundPolicyTextView = (TextView) mRefundPolicyLayout.findViewById(R.id.refundPolicyTextView);

        if (Util.isTextEmpty(bookingDetail.mRefundComment) == false)
        {
            String comment = bookingDetail.mRefundComment.replaceAll("900034", "B70038");
            refundPolicyTextView.setText(Html.fromHtml(comment));
        }

        View refundButtonLayout = mRefundPolicyLayout.findViewById(R.id.refundButtonLayout);
        TextView buttonTextView = (TextView) refundButtonLayout.findViewById(R.id.buttonTextView);

        // 정책을 보여주지 않을 경우
        if (bookingDetail.isVisibleRefundPolicy == false)
        {
            setRefundLayoutVisible(false);
        } else
        {
            setRefundLayoutVisible(true);

            switch (getRefundPolicyStatus(bookingDetail))
            {
                case StayBookingDetail.STATUS_NO_CHARGE_REFUND:
                {
                    mDefaultRefundPolicyLayout.setVisibility(View.VISIBLE);
                    mWaitRefundPolicyLayout.setVisibility(View.GONE);

                    refundButtonLayout.setOnClickListener(this);
                    buttonTextView.setText(R.string.label_request_free_refund);
                    break;
                }

                case StayBookingDetail.STATUS_WAIT_REFUND:
                {
                    mDefaultRefundPolicyLayout.setVisibility(View.GONE);
                    mWaitRefundPolicyLayout.setVisibility(View.VISIBLE);

                    TextView waitRefundPolicyTextView = (TextView) mWaitRefundPolicyLayout.findViewById(R.id.waitRefundPolicyTextView);
                    waitRefundPolicyTextView.setText(Html.fromHtml(mContext.getString(R.string.message_please_wait_refund01)));

                    refundButtonLayout.setOnClickListener(this);
                    buttonTextView.setText(R.string.label_contact_refund);
                    break;
                }

                case StayBookingDetail.STATUS_SURCHARGE_REFUND:
                {
                    mDefaultRefundPolicyLayout.setVisibility(View.VISIBLE);
                    mWaitRefundPolicyLayout.setVisibility(View.GONE);

                    refundButtonLayout.setOnClickListener(this);
                    buttonTextView.setText(R.string.label_contact_refund);
                    break;
                }

                default:
                {
                    mDefaultRefundPolicyLayout.setVisibility(View.VISIBLE);
                    mWaitRefundPolicyLayout.setVisibility(View.GONE);
                    refundButtonLayout.setOnClickListener(null);
                    refundButtonLayout.setVisibility(View.GONE);
                    break;
                }
            }
        }
    }

    private String getRefundPolicyStatus(StayBookingDetail bookingDetail)
    {
        // 환불 대기 상태
        if (bookingDetail.readyForRefund == true)
        {
            return StayBookingDetail.STATUS_WAIT_REFUND;
        } else
        {
            if (Util.isTextEmpty(bookingDetail.refundPolicy) == false)
            {
                return bookingDetail.refundPolicy;
            } else
            {
                return StayBookingDetail.STATUS_SURCHARGE_REFUND;
            }
        }
    }

    public void setRefundLayoutVisible(boolean visible)
    {
        if (mRefundPolicyLayout == null)
        {
            return;
        }

        if (visible == true)
        {
            mRefundPolicyLayout.setVisibility(View.VISIBLE);
            mButtonBottomMarginView.setVisibility(View.GONE);
        } else
        {
            mRefundPolicyLayout.setVisibility(View.GONE);
            mButtonBottomMarginView.setVisibility(View.VISIBLE);
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
        }
    }

    public boolean isExpandedMap()
    {
        return mSearchMapsLayout != null && mSearchMapsLayout.getVisibility() != View.GONE;
    }

    public void expandMap()
    {
        setMapToolbar();

        mScrollLayout.setScrollingEnabled(false);
        mScrollLayout.scrollTo(0, 0);

        mSearchMapsLayout.setVisibility(View.INVISIBLE);
        mAddressLayout.setVisibility(View.INVISIBLE);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);

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

                int height = (int) mMapLayout.getTag();

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mMapLayout.getLayoutParams();
                layoutParams.height = (int) (height + ((int) mAddressLayout.getY() - height) * value);

                mMapLayout.setLayoutParams(layoutParams);

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
                mMapLayout.setTranslationY(0.0f);

                mMapExpandedView.setVisibility(View.GONE);
                mSearchMapsLayout.setVisibility(View.VISIBLE);
                mAddressLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                mZoomControl.setVisibility(View.VISIBLE);
                mMyLocationView.setVisibility(View.VISIBLE);

                mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
                mScrollLayout.setVisibility(View.INVISIBLE);

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
    }

    public void collapseMap(double latitude, double longitude)
    {
        setBookingToolbar();

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);

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

                int height = (int) mMapLayout.getTag();

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mMapLayout.getLayoutParams();
                layoutParams.height = (int) (height + ((int) mAddressLayout.getY() - height) * value);

                mMapLayout.setLayoutParams(layoutParams);

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

                mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                mScrollLayout.scrollTo(0, 0);
                mScrollLayout.setScrollingEnabled(true);
                mMapLayout.setTranslationY(0.0f);

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

        LatLng latLng = new LatLng(latitude, longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder().target((latLng)).zoom(15).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 500, null);
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

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), Util.dpToPx(mContext, 50));
        mGoogleMap.animateCamera(cameraUpdate);
    }

    public View getMyLocationView()
    {
        return mMyLocationView;
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
                    ((OnEventListener) mOnEventListener).onMyLocationClick();
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

            mZoomControl.setPadding(mZoomControl.getPaddingLeft(), Util.dpToPx(mContext, 50), mZoomControl.getPaddingRight(), mZoomControl.getPaddingBottom());
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

    private long getCompareDate(long timeInMillis)
    {
        Calendar calendar = DailyCalendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(timeInMillis);

        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }
}
