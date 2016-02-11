/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * BookingListFragment (예약 확인 화면)
 * <p>
 * 예약된 목록들을 보여주는 화면이다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.activity.LoginActivity;
import com.twoheart.dailyhotel.activity.PaymentWaitActivity;
import com.twoheart.dailyhotel.adapter.BookingListAdapter;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.screen.bookingdetail.GourmetBookingDetailTabActivity;
import com.twoheart.dailyhotel.screen.bookingdetail.HotelBookingDetailTabActivity;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.view.widget.PinnedSectionListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * 예약한 호텔의 리스트들을 출력.
 *
 * @author jangjunho
 */
public class BookingListFragment extends BaseFragment implements Constants, OnItemClickListener, OnClickListener
{
    private BookingListAdapter mAdapter;
    private RelativeLayout mEmptyLayout;
    private PinnedSectionListView mListView;
    private View btnLogin;
    private long mCurrentTime;

    public interface OnUserActionListener
    {
        void delete(Booking booking);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        View view = inflater.inflate(R.layout.fragment_booking_list, container, false);

        initToolbar(baseActivity, view);
        initLayout(view);

        return view;
    }

    private void initToolbar(BaseActivity baseActivity, View view)
    {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(baseActivity, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_booking_list_frag), false, false);
    }

    private void initLayout(View view)
    {
        mListView = (PinnedSectionListView) view.findViewById(R.id.listview_booking);
        mListView.setShadowVisible(false);
        mListView.setTag("BookingListFragment");

        mEmptyLayout = (RelativeLayout) view.findViewById(R.id.layout_booking_empty);
        btnLogin = view.findViewById(R.id.btn_booking_empty_login);

        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onStart()
    {
        AnalyticsManager.getInstance(getActivity()).recordScreen(Screen.BOOLKING_LIST);

        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        lockUI();
        DailyNetworkAPI.getInstance().requestUserAlive(mNetworkTag, mUserAliveStringResponseListener, baseActivity);
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == btnLogin.getId())
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            Intent i = new Intent(baseActivity, LoginActivity.class);
            startActivity(i);
            baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

            AnalyticsManager.getInstance(getActivity()).recordEvent(Screen.BOOLKING_LIST, Action.CLICK, Label.LOGIN, 0L);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parentView, View childView, int position, long id)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        if (isLockUiComponent() == true)
        {
            return;
        }

        lockUiComponent();

        Booking item = mAdapter.getItem(position);

        if (item.type == Booking.TYPE_SECTION)
        {
            releaseUiComponent();
            return;
        }

        Intent intent = null;

        if (item.payType == CODE_PAY_TYPE_CARD_COMPLETE || item.payType == CODE_PAY_TYPE_ACCOUNT_COMPLETE)
        {
            // 카드결제 완료 || 가상계좌 완료

            switch (item.placeType)
            {
                case HOTEL:
                    intent = new Intent(baseActivity, HotelBookingDetailTabActivity.class);
                    break;

                case FNB:
                    intent = new Intent(baseActivity, GourmetBookingDetailTabActivity.class);
                    break;
            }

        } else if (item.payType == CODE_PAY_TYPE_ACCOUNT_WAIT)
        {
            // 가상계좌 입금대기
            intent = new Intent(baseActivity, PaymentWaitActivity.class);
        }

        if (intent != null)
        {
            intent.putExtra(NAME_INTENT_EXTRA_DATA_BOOKING, item);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_BOOKING_DETAIL);
        } else
        {
            releaseUiComponent();
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Label.TYPE, String.valueOf(item.payType));
        params.put(Label.ISUSED, String.valueOf(item.isUsed));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        params.put(Label.CHECK_IN, simpleDateFormat.format(item.checkinTime));
        params.put(Label.CHECK_OUT, simpleDateFormat.format(item.checkoutTime));
        params.put(Label.RESERVATION_INDEX, String.valueOf(item.reservationIndex));

        AnalyticsManager.getInstance(getActivity()).recordEvent(Screen.BOOLKING_LIST, Action.CLICK, item.placeName, params);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        releaseUiComponent();

        if (requestCode == CODE_REQUEST_ACTIVITY_BOOKING_DETAIL)
        {
            switch (resultCode)
            {
                case CODE_RESULT_ACTIVITY_EXPIRED_PAYMENT_WAIT:
                    BaseActivity baseActivity = (BaseActivity) getActivity();

                    if (baseActivity == null)
                    {
                        return;
                    }

                    baseActivity.showSimpleDialog(getString(R.string.dialog_notice2), data.getStringExtra("msg"), getString(R.string.dialog_btn_text_confirm), null);
                    break;
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UserActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private OnUserActionListener mOnUserActionListener = new OnUserActionListener()
    {
        @Override
        public void delete(final Booking booking)
        {
            if (isLockUiComponent() == true)
            {
                return;
            }

            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            lockUI();

            // 세션 여부를 판단한다.
            DailyNetworkAPI.getInstance().requestUserAlive(mNetworkTag, new DailyHotelStringResponseListener()
            {
                @Override
                public void onResponse(String url, String response)
                {
                    BaseActivity baseActivity = (BaseActivity) getActivity();

                    if (baseActivity == null)
                    {
                        return;
                    }

                    unLockUI();

                    String result = null;

                    if (false == Util.isTextEmpty(response))
                    {
                        result = response.trim();
                    }

                    if (true == "alive".equalsIgnoreCase(result))
                    {
                        if (baseActivity.isFinishing() == true)
                        {
                            return;
                        }

                        View.OnClickListener posListener = new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                BaseActivity baseActivity = (BaseActivity) getActivity();

                                if (baseActivity == null)
                                {
                                    return;
                                }

                                lockUI();

                                switch (booking.placeType)
                                {
                                    case HOTEL:
                                    {
                                        HashMap<String, String> params = new HashMap<String, String>();
                                        params.put("idx", String.valueOf(booking.reservationIndex));

                                        DailyNetworkAPI.getInstance().requestHotelHiddenBooking(mNetworkTag, params, mReservationHiddenJsonResponseListener, baseActivity);
                                        break;
                                    }

                                    case FNB:
                                        HashMap<String, String> params = new HashMap<String, String>();
                                        params.put("reservation_rec_idx", String.valueOf(booking.reservationIndex));

                                        DailyNetworkAPI.getInstance().requestGourmetHiddenBooking(mNetworkTag, params, mReservationHiddenJsonResponseListener, baseActivity);
                                        break;
                                }

                            }
                        };

                        baseActivity.showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_delete_booking), getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), posListener, null);
                    } else
                    {
                        baseActivity.restartApp();
                    }
                }
            }, baseActivity);
        }
    };

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
            } catch (Exception e)
            {
                onError(e);
            } finally
            {
                mListView.setVisibility(View.GONE);
                mEmptyLayout.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);

                unLockUI();
            }
        }

    };

    ;
    private DailyHotelJsonResponseListener mReservationListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            int msg_code = -1;

            try
            {
                // 해당 화면은 메시지를 넣지 않는다.
                msg_code = response.getInt("msg_code");
            } catch (Exception e)
            {
                onError(e);
                unLockUI();
                return;
            }

            try
            {
                JSONArray jsonArray = response.getJSONArray("data");
                int length = jsonArray.length();

                if (length == 0)
                {
                    if (mAdapter != null)
                    {
                        mAdapter.clear();
                    }

                    //예약한 호텔이 없는 경우
                    mListView.setVisibility(View.GONE);
                    mEmptyLayout.setVisibility(View.VISIBLE);
                    btnLogin.setVisibility(View.INVISIBLE);
                } else
                {
                    // 입금대기, 결제완료, 이용완료
                    ArrayList<Booking> waitBookingList = new ArrayList<Booking>();
                    ArrayList<Booking> paymentBookingList = new ArrayList<Booking>();
                    ArrayList<Booking> usedBookingList = new ArrayList<Booking>();

                    for (int i = 0; i < length; i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        Booking booking = new Booking(jsonObject);

                        switch (booking.payType)
                        {
                            case CODE_PAY_TYPE_CARD_COMPLETE:
                            case CODE_PAY_TYPE_ACCOUNT_COMPLETE:
                                booking.isUsed = booking.checkoutTime < mCurrentTime;

                                if (booking.isUsed)
                                {
                                    usedBookingList.add(booking);
                                } else
                                {
                                    paymentBookingList.add(booking);
                                }
                                break;

                            case CODE_PAY_TYPE_ACCOUNT_WAIT:
                                waitBookingList.add(booking);
                                break;
                        }
                    }

                    ArrayList<Booking> bookingArrayList = new ArrayList<Booking>(length + 3);

                    // 입금 대기가 있는 경우.
                    if (waitBookingList.size() > 0)
                    {
                        Booking sectionWait = new Booking(getString(R.string.frag_booking_wait_account));
                        bookingArrayList.add(sectionWait);
                        bookingArrayList.addAll(waitBookingList);
                    }

                    // 결제 완료가 있는 경우.
                    if (paymentBookingList.size() > 0)
                    {
                        Booking sectionPay = new Booking(getString(R.string.frag_booking_complete_payment));
                        bookingArrayList.add(sectionPay);
                        bookingArrayList.addAll(paymentBookingList);
                    }

                    // 이용 완료가 있는 경우.
                    if (usedBookingList.size() > 0)
                    {
                        Booking sectionUsed = new Booking(getString(R.string.frag_booking_use));
                        bookingArrayList.add(sectionUsed);
                        bookingArrayList.addAll(usedBookingList);
                    }

                    if (mAdapter == null)
                    {
                        mAdapter = new BookingListAdapter(baseActivity, R.layout.list_row_booking, new ArrayList<Booking>());
                        mAdapter.setOnUserActionListener(mOnUserActionListener);
                        mListView.setOnItemClickListener(BookingListFragment.this);
                        mListView.setAdapter(mAdapter);
                    }

                    mAdapter.clear();
                    mAdapter.addAll(bookingArrayList);
                    mAdapter.notifyDataSetChanged();

                    mListView.setVisibility(View.VISIBLE);
                    mEmptyLayout.setVisibility(View.GONE);

                    // flag가 가상계좌 입금 대기에서 날아온경우
                    int flag = DailyPreference.getInstance(baseActivity).getVirtualAccountReadyFlag();
                    if (flag == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
                    {
                        unLockUI();

                        mListView.performItemClick(null, 1, 0);
                        DailyPreference.getInstance(baseActivity).setVirtualAccountReadyFlag(-1);
                    }
                }
            } catch (Exception e)
            {
                mListView.setVisibility(View.GONE);
                mEmptyLayout.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);

                onError(e);
            } finally
            {
                unLockUI();
            }
        }
    };
    private DailyHotelJsonResponseListener mDateTimeJsonResponseListener = new DailyHotelJsonResponseListener()
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
                mCurrentTime = response.getLong("currentDateTime");

                DailyNetworkAPI.getInstance().requestBookingList(mNetworkTag, mReservationListJsonResponseListener, baseActivity);
            } catch (Exception e)
            {
                onError(e);
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
                // session alive
                DailyNetworkAPI.getInstance().requestCommonDatetime(mNetworkTag, mDateTimeJsonResponseListener, baseActivity);

            } else if ("dead".equalsIgnoreCase(result) == true)
            { // session dead
                // 재로그인
                if (true == DailyPreference.getInstance(baseActivity).isAutoLogin())
                {
                    HashMap<String, String> params = Util.getLoginParams(baseActivity);
                    DailyNetworkAPI.getInstance().requestUserSignin(mNetworkTag, params, mUserLoginJsonResponseListener, baseActivity);

                    mListView.setVisibility(View.GONE);
                    mEmptyLayout.setVisibility(View.VISIBLE);
                } else
                {
                    mListView.setVisibility(View.GONE);
                    mEmptyLayout.setVisibility(View.VISIBLE);

                    unLockUI();
                }

            } else
            {
                mListView.setVisibility(View.GONE);
                mEmptyLayout.setVisibility(View.VISIBLE);

                onError();
                unLockUI();
            }
        }
    };

    private DailyHotelJsonResponseListener mReservationHiddenJsonResponseListener = new DailyHotelJsonResponseListener()
    {

        @Override
        public void onResponse(String url, JSONObject response)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null || baseActivity.isFinishing() == true)
            {
                return;
            }

            int msg_code = -1;

            try
            {
                // 해당 화면은 메시지를 넣지 않는다.
                msg_code = response.getInt("msg_code");

                JSONObject jsonObject = response.getJSONObject("data");
                String message = null;
                boolean result = false;

                if (jsonObject != null)
                {
                    if (jsonObject.has("isSuccess") == true)
                    {
                        result = jsonObject.getInt("isSuccess") == 1;
                    } else if (jsonObject.has("is_success") == true)
                    {
                        result = jsonObject.getBoolean("is_success");
                    }
                }

                // 성공 실패 여부는 팝업에서 리스너를 다르게 등록한다.
                View.OnClickListener onClickListener;

                if (result == true)
                {
                    onClickListener = new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            BaseActivity baseActivity = (BaseActivity) getActivity();

                            if (baseActivity == null)
                            {
                                return;
                            }

                            lockUI();
                            DailyNetworkAPI.getInstance().requestBookingList(mNetworkTag, mReservationListJsonResponseListener, baseActivity);
                        }
                    };
                } else
                {
                    onClickListener = new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            BaseActivity baseActivity = (BaseActivity) getActivity();

                            if (baseActivity == null)
                            {
                                return;
                            }

                            lockUI();
                            DailyNetworkAPI.getInstance().requestBookingList(mNetworkTag, mReservationListJsonResponseListener, baseActivity);
                        }
                    };
                }

                switch (msg_code)
                {
                    case 0:
                    {
                        message = response.getString("msg");
                        DailyToast.showToast(baseActivity, message, Toast.LENGTH_SHORT);

                        DailyNetworkAPI.getInstance().requestBookingList(mNetworkTag, mReservationListJsonResponseListener, baseActivity);
                        break;
                    }

                    // Toast
                    case 100:
                    {
                        message = response.getString("msg");

                        if (Util.isTextEmpty(message) == false)
                        {
                            DailyToast.showToast(baseActivity, message, Toast.LENGTH_SHORT);
                        }

                        DailyNetworkAPI.getInstance().requestBookingList(mNetworkTag, mReservationListJsonResponseListener, baseActivity);
                        break;
                    }

                    // Popup
                    case 200:
                    {
                        message = response.getString("msg");

                        if (Util.isTextEmpty(message) == false)
                        {
                            unLockUI();

                            if (baseActivity.isFinishing() == true)
                            {
                                return;
                            }

                            baseActivity.showSimpleDialog(getString(R.string.dialog_notice2), message, getString(R.string.dialog_btn_text_confirm), onClickListener);
                        } else
                        {
                            DailyNetworkAPI.getInstance().requestBookingList(mNetworkTag, mReservationListJsonResponseListener, baseActivity);
                        }
                        break;
                    }
                }
            } catch (Exception e)
            {
                onError(e);

                // credit card 요청
                DailyNetworkAPI.getInstance().requestBookingList(mNetworkTag, mReservationListJsonResponseListener, baseActivity);
            }
        }
    };
}
