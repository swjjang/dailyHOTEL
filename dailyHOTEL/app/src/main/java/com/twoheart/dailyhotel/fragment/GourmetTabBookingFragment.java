/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * BookingTabBookingFragment (예약한 호텔의 예약 탭)
 * <p>
 * 예약한 호텔 탭 중 예약 탭 프래그먼트
 */
package com.twoheart.dailyhotel.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.activity.GourmetReceiptActivity;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.model.GourmetBookingDetail;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyToast;

public class GourmetTabBookingFragment extends BaseFragment implements Constants
{
    private static final String KEY_BUNDLE_ARGUMENTS_PLACEBOOKINGDETAIL = "placeBookingDetail";
    private static final String KEY_BUNDLE_ARGUMENTS_BOOKING = "booking";

    private Booking mBooking;
    private GourmetBookingDetail mGourmetBookingDetail;

    public static GourmetTabBookingFragment newInstance(PlaceBookingDetail placeBookingDetail, Booking booking, String title)
    {
        GourmetTabBookingFragment newFragment = new GourmetTabBookingFragment();

        //관련 정보는 BookingTabActivity에서 넘겨받음.
        Bundle arguments = new Bundle();
        arguments.putParcelable(KEY_BUNDLE_ARGUMENTS_PLACEBOOKINGDETAIL, placeBookingDetail);
        arguments.putParcelable(KEY_BUNDLE_ARGUMENTS_BOOKING, booking);

        newFragment.setArguments(arguments);
        newFragment.setTitle(title);

        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mGourmetBookingDetail = (GourmetBookingDetail) getArguments().getParcelable(KEY_BUNDLE_ARGUMENTS_PLACEBOOKINGDETAIL);
        mBooking = (Booking) getArguments().getParcelable(KEY_BUNDLE_ARGUMENTS_BOOKING);
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
        TextView ticketNameTextView = (TextView) view.findViewById(R.id.ticketNameTextView);
        TextView addressTextView = (TextView) view.findViewById(R.id.addressTextView);
        TextView ticketTypeTextView = (TextView) view.findViewById(R.id.ticketTypeTextView);
        TextView ticketCountTextView = (TextView) view.findViewById(R.id.ticketCountTextView);
        TextView dateTextView = (TextView) view.findViewById(R.id.dateTextView);
        TextView userNameTextView = (TextView) view.findViewById(R.id.userNameTextView);
        TextView userPhoneTextView = (TextView) view.findViewById(R.id.userPhoneTextView);

        ticketNameTextView.setText(mBooking.placeName);
        addressTextView.setText(mGourmetBookingDetail.address);
        ticketTypeTextView.setText(mGourmetBookingDetail.ticketName);
        ticketCountTextView.setText(getString(R.string.label_booking_count, mGourmetBookingDetail.ticketCount));
        dateTextView.setText(mGourmetBookingDetail.sday);
        userNameTextView.setText(mGourmetBookingDetail.guestName);
        userPhoneTextView.setText(Util.addHippenMobileNumber(baseActivity, mGourmetBookingDetail.guestPhone));

        // Android Marquee bug...
        ticketNameTextView.setSelected(true);
        addressTextView.setSelected(true);
        ticketTypeTextView.setSelected(true);
        userNameTextView.setSelected(true);
        userPhoneTextView.setSelected(true);

        // 영수증 발급
        TextView viewReceiptTextView = (TextView) view.findViewById(R.id.viewReceiptTextView);

        if (DEBUG == true)
        {
            mBooking.isUsed = true;
        }

        if (mBooking.isUsed == true)
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
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_BOOKINGIDX, mBooking.reservationIndex);
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
}
