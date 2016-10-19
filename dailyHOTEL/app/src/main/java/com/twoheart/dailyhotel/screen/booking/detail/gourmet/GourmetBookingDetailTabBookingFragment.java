/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * HotelBookingDetailTabBookingFragment (예약한 호텔의 예약 탭)
 * <p>
 * 예약한 호텔 탭 중 예약 탭 프래그먼트
 */
package com.twoheart.dailyhotel.screen.booking.detail.gourmet;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetBookingDetail;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.request.DailyHotelRequest;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.screen.common.ZoomMapActivity;
import com.twoheart.dailyhotel.screen.gourmet.detail.GourmetDetailActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToast;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class GourmetBookingDetailTabBookingFragment extends BaseFragment implements Constants, View.OnClickListener
{
    private static final String KEY_BUNDLE_ARGUMENTS_BOOKING_DETAIL = "bookingDetail";
    private static final String KEY_BUNDLE_ARGUMENTS_RESERVATION_INDEX = "reservationIndex";

    private GourmetBookingDetail mBookingDetail;
    private int mReservationIndex;

    public static GourmetBookingDetailTabBookingFragment newInstance(PlaceBookingDetail bookingDetail, int reservationIndex)
    {
        GourmetBookingDetailTabBookingFragment newFragment = new GourmetBookingDetailTabBookingFragment();

        //관련 정보는 BookingTabActivity에서 넘겨받음.
        Bundle arguments = new Bundle();
        arguments.putParcelable(KEY_BUNDLE_ARGUMENTS_BOOKING_DETAIL, bookingDetail);
        arguments.putInt(KEY_BUNDLE_ARGUMENTS_RESERVATION_INDEX, reservationIndex);

        newFragment.setArguments(arguments);

        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null)
        {
            mBookingDetail = bundle.getParcelable(KEY_BUNDLE_ARGUMENTS_BOOKING_DETAIL);
            mReservationIndex = bundle.getInt(KEY_BUNDLE_ARGUMENTS_RESERVATION_INDEX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return null;
        }

        View view = inflater.inflate(R.layout.fragment_courmetbooking_tab_booking, container, false);

        ScrollView scrollLayout = (ScrollView) view.findViewById(R.id.scrollLayout);
        EdgeEffectColor.setEdgeGlowColor(scrollLayout, getResources().getColor(R.color.default_over_scroll_edge));

        initPlaceInformationLayout(baseActivity, view, mBookingDetail);
        initGourmetInformationLayout(baseActivity, view, mBookingDetail);
        initGuestInformationLayout(view, mBookingDetail);
        initPaymentInformationLayout(view, mBookingDetail);

        return view;
    }

    private void initPlaceInformationLayout(Context context, View view, GourmetBookingDetail bookingDetail)
    {
        if (view == null || bookingDetail == null)
        {
            return;
        }

        // Map 4 :2 비율 맞추기
        com.facebook.drawee.view.SimpleDraweeView mapImageView = (com.facebook.drawee.view.SimpleDraweeView) view.findViewById(R.id.mapImageView);
        mapImageView.setOnClickListener(this);

        double width = Util.getLCDWidth(context);
        double height = Util.getListRowHeight(context);
        double ratio = height / width;

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mapImageView.getLayoutParams();
        layoutParams.width = (int) width;
        layoutParams.height = (int) height + Util.dpToPx(context, 71);

        mapImageView.setLayoutParams(layoutParams);

        if (width >= 720)
        {
            width = 720;
        }

        height = width * ratio;

        String size = String.format("%dx%d", (int) width, (int) height);
        String iconUrl = "http://img.dailyhotel.me/app_static/info_ic_map_large.png";
        String url = String.format("http://maps.googleapis.com/maps/api/staticmap?zoom=17&size=%s&markers=icon:%s|%s,%s&sensor=false&scale=2&format=png8&mobile=true&key=%s"//
            , size, iconUrl, bookingDetail.latitude, bookingDetail.longitude, DailyHotelRequest.getUrlDecoderEx(Constants.GOOGLE_MAP_KEY));

        mapImageView.setImageURI(Uri.parse(url));

        TextView placeNameTextView = (TextView) view.findViewById(R.id.placeNameTextView);
        placeNameTextView.setText(bookingDetail.placeName);

        View viewDetailView = view.findViewById(R.id.viewDetailView);
        View viewMapView = view.findViewById(R.id.viewMapView);
        //        View callView = view.findViewById(R.id.callView);

        viewDetailView.setOnClickListener(this);
        viewMapView.setOnClickListener(this);
        //        callView.setOnClickListener(this);
    }

    private void initGourmetInformationLayout(Context context, View view, GourmetBookingDetail bookingDetail)
    {
        if (context == null || view == null || bookingDetail == null)
        {
            return;
        }

        // 3일전 부터 몇일 남음 필요.
        View remainedDayLayout = view.findViewById(R.id.remainedDayLayout);
        TextView remainedDayTextView = (TextView) view.findViewById(R.id.remainedDayTextView);
        String remainedDayText;

        try
        {
            Date checkInDate = DailyCalendar.convertDate(bookingDetail.reservationTime, DailyCalendar.ISO_8601_FORMAT);

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
                remainedDayText = context.getString(R.string.frag_booking_today_type_gourmet);
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

        // 방문일 방문시간
        initTimeInformatonLayout(context, view, bookingDetail);

        TextView gourmetNameTextView = (TextView) view.findViewById(R.id.gourmetNameTextView);
        TextView ticketTypeTextView = (TextView) view.findViewById(R.id.ticketTypeTextView);
        TextView ticketCountTextView = (TextView) view.findViewById(R.id.ticketCountTextView);
        TextView addressTextView = (TextView) view.findViewById(R.id.addressTextView);

        gourmetNameTextView.setText(bookingDetail.placeName);
        ticketTypeTextView.setText(bookingDetail.ticketName);
        ticketCountTextView.setText(getString(R.string.label_booking_count, bookingDetail.ticketCount));
        addressTextView.setText(bookingDetail.address);
    }

    private void initTimeInformatonLayout(Context context, View view, GourmetBookingDetail bookingDetail)
    {
        if (context == null || view == null || bookingDetail == null)
        {
            return;
        }

        TextView ticketDateTextView = (TextView) view.findViewById(R.id.ticketDateTextView);
        TextView ticketTimeTextView = (TextView) view.findViewById(R.id.ticketTimeTextView);

        try
        {
            String ticketDateFormat = DailyCalendar.convertDateFormatString(bookingDetail.reservationTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.M.d(EEE)");
            ticketDateTextView.setText(ticketDateFormat);
        } catch (Exception e)
        {
            ticketDateTextView.setText(null);
        }

        try
        {
            String timeDateFormat = DailyCalendar.convertDateFormatString(bookingDetail.reservationTime, DailyCalendar.ISO_8601_FORMAT, "HH:mm");
            ticketTimeTextView.setText(timeDateFormat);
        } catch (Exception e)
        {
            ticketTimeTextView.setText(null);
        }
    }

    private void initGuestInformationLayout(View view, GourmetBookingDetail bookingDetail)
    {
        TextView guestNameTextView = (TextView) view.findViewById(R.id.guestNameTextView);
        TextView guestPhoneTextView = (TextView) view.findViewById(R.id.guestPhoneTextView);
        TextView guestEmailTextView = (TextView) view.findViewById(R.id.guestEmailTextView);

        guestNameTextView.setText(bookingDetail.guestName);
        guestPhoneTextView.setText(Util.addHippenMobileNumber(getContext(), bookingDetail.guestPhone));
        guestEmailTextView.setText(bookingDetail.guestEmail);
    }

    private void initPaymentInformationLayout(View view, GourmetBookingDetail bookingDetail)
    {
        TextView paymentDateTextView = (TextView) view.findViewById(R.id.paymentDateTextView);
        TextView priceTextView = (TextView) view.findViewById(R.id.priceTextView);

        View bonusLayout = view.findViewById(R.id.bonusLayout);
        View couponLayout = view.findViewById(R.id.couponLayout);
        TextView totalPriceTextView = (TextView) view.findViewById(R.id.totalPriceTextView);

        try
        {
            //            paymentDateTextView.setText(Util.simpleDateFormatISO8601toFormat(bookingDetail.paymentDate, "yyyy.MM.dd"));
            paymentDateTextView.setText(DailyCalendar.convertDateFormatString(bookingDetail.paymentDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        String price = Util.getPriceFormat(getContext(), bookingDetail.paymentPrice, false);

        priceTextView.setText(price);
        totalPriceTextView.setText(price);

        bonusLayout.setVisibility(View.GONE);
        couponLayout.setVisibility(View.GONE);

        // 영수증 발급
        View confirmView = view.findViewById(R.id.buttonLayout);
        confirmView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                BaseActivity baseActivity = (BaseActivity) getActivity();

                if (baseActivity == null)
                {
                    return;
                }

                Intent intent = new Intent(baseActivity, GourmetReceiptActivity.class);
                intent.putExtra(NAME_INTENT_EXTRA_DATA_BOOKINGIDX, mReservationIndex);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.mapImageView:
            {
                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                lockUI();

                BaseActivity baseActivity = (BaseActivity) getActivity();

                Intent intent = ZoomMapActivity.newInstance(baseActivity//
                    , ZoomMapActivity.SourceType.HOTEL_BOOKING, mBookingDetail.placeName, mBookingDetail.address//
                    , mBookingDetail.latitude, mBookingDetail.longitude, false);

                startActivity(intent);
                break;
            }

            case R.id.viewDetailView:
            {
                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                lockUI();

                BaseActivity baseActivity = (BaseActivity) getActivity();

                SaleTime saleTime = new SaleTime();
                saleTime.setCurrentTime(mBookingDetail.currentDateTime);
                saleTime.setDailyTime(mBookingDetail.dailyDateTime);

                Intent intent = GourmetDetailActivity.newInstance(baseActivity, saleTime, mBookingDetail.placeIndex, false);
                baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTEL_DETAIL);
                break;
            }

            case R.id.viewMapView:
            {
                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                lockUI();

                BaseActivity baseActivity = (BaseActivity) getActivity();

                Util.showShareMapDialog(baseActivity, mBookingDetail.placeName//
                    , mBookingDetail.latitude, mBookingDetail.longitude, false//
                    , AnalyticsManager.Category.GOURMET_BOOKINGS//
                    , AnalyticsManager.Action.GOURMET_DETAIL_NAVIGATION_APP_CLICKED//
                    , null);
                break;
            }

            case R.id.callDailyView:
            {
                BaseActivity baseActivity = (BaseActivity) getActivity();

                if (Util.isTelephonyEnabled(baseActivity) == true)
                {
                    try
                    {
                        String phone = DailyPreference.getInstance(baseActivity).getCompanyPhoneNumber();

                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
                    } catch (ActivityNotFoundException e)
                    {
                        DailyToast.showToast(baseActivity, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
                    }
                } else
                {
                    DailyToast.showToast(baseActivity, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
                }
                break;
            }

            case R.id.kakaoDailyView:
            {
                try
                {
                    startActivity(new Intent(Intent.ACTION_SEND, Uri.parse("kakaolink://friend/%40%EB%8D%B0%EC%9D%BC%EB%A6%AC%EA%B3%A0%EB%A9%94")));
                } catch (ActivityNotFoundException e)
                {
                    try
                    {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_STORE_GOOGLE_KAKAOTALK)));
                    } catch (ActivityNotFoundException e1)
                    {
                        Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
                        marketLaunch.setData(Uri.parse(URL_STORE_GOOGLE_KAKAOTALK_WEB));
                        startActivity(marketLaunch);
                    }
                }
                break;
            }

            case R.id.callPlaceView:
            {
                BaseActivity baseActivity = (BaseActivity) getActivity();

                if (Util.isTelephonyEnabled(baseActivity) == true)
                {
                    String phone = mBookingDetail.gourmetPhone;

                    if (Util.isTextEmpty(mBookingDetail.gourmetPhone) == true)
                    {
                        phone = DailyPreference.getInstance(baseActivity).getCompanyPhoneNumber();
                    }

                    try
                    {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
                    } catch (ActivityNotFoundException e)
                    {
                        String message = getString(R.string.toast_msg_no_gourmet_call, mBookingDetail.gourmetPhone);
                        DailyToast.showToast(baseActivity, message, Toast.LENGTH_LONG);
                    }
                } else
                {
                    String message = getString(R.string.toast_msg_no_gourmet_call, mBookingDetail.gourmetPhone);
                    DailyToast.showToast(baseActivity, message, Toast.LENGTH_LONG);
                }
                break;
            }
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
