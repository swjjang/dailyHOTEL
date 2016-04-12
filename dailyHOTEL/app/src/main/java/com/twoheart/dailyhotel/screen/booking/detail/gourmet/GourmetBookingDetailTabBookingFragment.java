/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * HotelBookingDetailTabBookingFragment (예약한 호텔의 예약 탭)
 * <p>
 * 예약한 호텔 탭 중 예약 탭 프래그먼트
 */
package com.twoheart.dailyhotel.screen.booking.detail.gourmet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetBookingDetail;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyToast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class GourmetBookingDetailTabBookingFragment extends BaseFragment implements Constants
{
    private static final String KEY_BUNDLE_ARGUMENTS_BOOKING_DETAIL = "bookingDetail";
    private static final String KEY_BUNDLE_ARGUMENTS_RESERVATION_INDEX = "reservationIndex";
    private static final String KEY_BUNDLE_ARGUMENTS_ISUSED = "isUsed";

    private GourmetBookingDetail mBookingDetail;
    private int mReservationIndex;
    private boolean mIsUsed;

    public static GourmetBookingDetailTabBookingFragment newInstance(PlaceBookingDetail bookingDetail, int reservationIndex, boolean isUsed)
    {
        GourmetBookingDetailTabBookingFragment newFragment = new GourmetBookingDetailTabBookingFragment();

        //관련 정보는 BookingTabActivity에서 넘겨받음.
        Bundle arguments = new Bundle();
        arguments.putParcelable(KEY_BUNDLE_ARGUMENTS_BOOKING_DETAIL, bookingDetail);
        arguments.putInt(KEY_BUNDLE_ARGUMENTS_RESERVATION_INDEX, reservationIndex);
        arguments.putBoolean(KEY_BUNDLE_ARGUMENTS_ISUSED, isUsed);

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
            mIsUsed = bundle.getBoolean(KEY_BUNDLE_ARGUMENTS_ISUSED);
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
        EdgeEffectColor.setEdgeGlowColor(scrollLayout, getResources().getColor(R.color.over_scroll_edge));

        initGourmetInformationLayout(view, mBookingDetail);
        initGuestInformationLayout(view, mBookingDetail);

        // 영수증 발급
        TextView viewReceiptTextView = (TextView) view.findViewById(R.id.viewReceiptTextView);

        if (DEBUG == true)
        {
            mIsUsed = true;
        }

        if (mIsUsed == true)
        {
            viewReceiptTextView.setTextColor(getResources().getColor(R.color.white));
            viewReceiptTextView.setBackgroundResource(R.color.dh_theme_color);
            viewReceiptTextView.setOnClickListener(new View.OnClickListener()
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
        } else
        {
            viewReceiptTextView.setTextColor(getResources().getColor(R.color.hoteldetail_soldout_text));
            viewReceiptTextView.setBackgroundResource(R.color.hoteldetail_soldout_background);
            viewReceiptTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    DailyToast.showToast(getActivity(), R.string.message_cant_issuing_ticketreceipt, Toast.LENGTH_SHORT);
                }
            });
        }

        return view;
    }

    private void initGourmetInformationLayout(View view, GourmetBookingDetail bookingDetail)
    {
        TextView ticketNameTextView = (TextView) view.findViewById(R.id.ticketNameTextView);
        TextView addressTextView = (TextView) view.findViewById(R.id.addressTextView);
        TextView ticketTypeTextView = (TextView) view.findViewById(R.id.ticketTypeTextView);
        TextView ticketCountTextView = (TextView) view.findViewById(R.id.ticketCountTextView);
        TextView dateTextView = (TextView) view.findViewById(R.id.dateTextView);

        ticketNameTextView.setText(bookingDetail.placeName);
        addressTextView.setText(bookingDetail.address);
        ticketTypeTextView.setText(bookingDetail.ticketName);
        ticketCountTextView.setText(getString(R.string.label_booking_count, bookingDetail.ticketCount));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd(EEE) HH:mm", Locale.KOREA);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        dateTextView.setText(simpleDateFormat.format(new Date(bookingDetail.reservationTime)));
    }

    private void initGuestInformationLayout(View view, GourmetBookingDetail bookingDetail)
    {
        TextView userNameTextView = (TextView) view.findViewById(R.id.userNameTextView);
        TextView userPhoneTextView = (TextView) view.findViewById(R.id.userPhoneTextView);

        userNameTextView.setText(bookingDetail.guestName);
        userPhoneTextView.setText(Util.addHippenMobileNumber(getContext(), bookingDetail.guestPhone));
    }
}
