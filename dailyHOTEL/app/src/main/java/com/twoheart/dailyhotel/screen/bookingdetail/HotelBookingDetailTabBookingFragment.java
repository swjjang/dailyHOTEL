/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * HotelBookingDetailTabBookingFragment (예약한 호텔의 예약 탭)
 * <p>
 * 예약한 호텔 탭 중 예약 탭 프래그먼트
 */
package com.twoheart.dailyhotel.screen.bookingdetail;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.activity.IssuingReceiptActivity;
import com.twoheart.dailyhotel.activity.LoginActivity;
import com.twoheart.dailyhotel.fragment.BaseFragment;
import com.twoheart.dailyhotel.model.HotelBookingDetail;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class HotelBookingDetailTabBookingFragment extends BaseFragment implements Constants
{
    private static final String KEY_BUNDLE_ARGUMENTS_BOOKING_DETAIL = "bookingDetail";
    private static final String KEY_BUNDLE_ARGUMENTS_RESERVATION_INDEX = "reservationIndex";
    private static final String KEY_BUNDLE_ARGUMENTS_ISUSED = "isUsed";

    private HotelBookingDetail mBookingDetail;
    private int mReservationIndex;
    private boolean mIsUsed;

    public static HotelBookingDetailTabBookingFragment newInstance(PlaceBookingDetail bookingDetail, int reservationIndex, boolean isUsed)
    {
        HotelBookingDetailTabBookingFragment newFragment = new HotelBookingDetailTabBookingFragment();

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

        mBookingDetail = (HotelBookingDetail) getArguments().getParcelable(KEY_BUNDLE_ARGUMENTS_BOOKING_DETAIL);
        mReservationIndex = getArguments().getInt(KEY_BUNDLE_ARGUMENTS_RESERVATION_INDEX);
        mIsUsed = getArguments().getBoolean(KEY_BUNDLE_ARGUMENTS_ISUSED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return null;
        }

        View view = inflater.inflate(R.layout.fragment_booking_tab_booking, container, false);
        TextView tvCustomerName = (TextView) view.findViewById(R.id.tv_booking_tab_user_name);
        TextView tvCustomerPhone = (TextView) view.findViewById(R.id.tv_booking_tab_user_phone);
        TextView tvHotelName = (TextView) view.findViewById(R.id.tv_booking_tab_hotel_name);
        TextView tvAddress = (TextView) view.findViewById(R.id.tv_booking_tab_address);
        TextView tvBedtype = (TextView) view.findViewById(R.id.tv_booking_tab_bedtype);
        TextView tvCheckIn = (TextView) view.findViewById(R.id.tv_booking_tab_checkin);
        TextView tvCheckOut = (TextView) view.findViewById(R.id.tv_booking_tab_checkout);

        tvHotelName.setText(mBookingDetail.placeName);
        tvAddress.setText(mBookingDetail.address);
        tvBedtype.setText(mBookingDetail.roomName);
        tvCustomerName.setText(mBookingDetail.guestName);
        tvCustomerPhone.setText(Util.addHippenMobileNumber(baseActivity, mBookingDetail.guestPhone));
        tvCheckIn.setText(mBookingDetail.checkInDay);
        tvCheckOut.setText(mBookingDetail.checkOutDay);

        // Android Marquee bug...
        tvCustomerName.setSelected(true);
        tvCustomerPhone.setSelected(true);
        tvHotelName.setSelected(true);
        tvAddress.setSelected(true);
        tvBedtype.setSelected(true);
        tvCheckIn.setSelected(true);
        tvCheckOut.setSelected(true);

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

                    Intent intent = new Intent(baseActivity, IssuingReceiptActivity.class);
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
                    DailyToast.showToast(getActivity(), R.string.message_cant_issuing_receipt, Toast.LENGTH_SHORT);
                }
            });
        }

        lockUI();
        DailyNetworkAPI.getInstance().requestUserAlive(mNetworkTag, mUserAliveStringResponseListener, baseActivity);
        return view;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mUserLoginJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            try
            {
                int msg_code = response.getInt("msg_code");

                if (msg_code == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    boolean isSignin = jsonObject.getBoolean("is_signin");

                    if (isSignin == true)
                    {
                        VolleyHttpClient.createCookie();
                        return;
                    }
                }

                // 로그인 실패
                // data 초기화
                DailyPreference.getInstance(baseActivity).removeUserInformation();

                DailyToast.showToast(baseActivity, getString(R.string.toast_msg_failed_to_login), Toast.LENGTH_SHORT);
            } catch (JSONException e)
            {
                onError(e);
            } finally
            {
                unLockUI();
            }
        }
    };

    private DailyHotelStringResponseListener mUserAliveStringResponseListener = new DailyHotelStringResponseListener()
    {

        @Override
        public void onResponse(String url, String response)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            String result = null;

            if (Util.isTextEmpty(response) == false)
            {
                result = response.trim();
            }

            if ("alive".equalsIgnoreCase(result) == true)
            {
            } else if ("dead".equalsIgnoreCase(result) == true)
            { // session dead
                // 재로그인
                if (DailyPreference.getInstance(baseActivity).isAutoLogin() == true)
                {
                    HashMap<String, String> params = Util.getLoginParams(baseActivity);
                    DailyNetworkAPI.getInstance().requestUserSignin(mNetworkTag, params, mUserLoginJsonResponseListener, baseActivity);
                } else
                {
                    startActivity(new Intent(baseActivity, LoginActivity.class));
                }
            } else
            {
                unLockUI();
            }
        }
    };
}
