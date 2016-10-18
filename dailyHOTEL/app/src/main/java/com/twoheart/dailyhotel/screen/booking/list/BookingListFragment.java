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
package com.twoheart.dailyhotel.screen.booking.list;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.screen.booking.detail.PaymentWaitActivity;
import com.twoheart.dailyhotel.screen.booking.detail.gourmet.GourmetBookingDetailTabActivity;
import com.twoheart.dailyhotel.screen.booking.detail.hotel.HotelBookingDetailTabActivity;
import com.twoheart.dailyhotel.screen.information.member.LoginActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.widget.PinnedSectionListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private boolean mDontReload;

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
        View toolbar = view.findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(baseActivity, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_booking_list_frag), null, false);
    }

    private void initLayout(View view)
    {
        mListView = (PinnedSectionListView) view.findViewById(R.id.listview_booking);
        mListView.setShadowVisible(false);
        mListView.setTag("BookingListFragment");

        mEmptyLayout = (RelativeLayout) view.findViewById(R.id.emptyLayout);
        btnLogin = view.findViewById(R.id.loginView);

        btnLogin.setOnClickListener(this);
    }

    private void updateLayout(boolean isSignin, ArrayList<Booking> bookingArrayList)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null || baseActivity.isFinishing() == true)
        {
            return;
        }

        if (isSignin == false)
        {
            mListView.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        } else
        {
            if (bookingArrayList == null || bookingArrayList.size() == 0)
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
                if (mAdapter == null)
                {
                    mAdapter = new BookingListAdapter(baseActivity, //
                        R.layout.list_row_booking, new ArrayList<Booking>(), mCurrentTime);
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
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (DailyDeepLink.getInstance().isValidateLink() == true)
        {
            if (DailyDeepLink.getInstance().isBookingDetailView() == true)
            {
                PlaceType placeType = null;

                if ("stay".equalsIgnoreCase(DailyDeepLink.getInstance().getPlaceType()) == true)
                {
                    placeType = PlaceType.HOTEL;
                } else if ("gourmet".equalsIgnoreCase(DailyDeepLink.getInstance().getPlaceType()) == true)
                {
                    placeType = PlaceType.FNB;
                }

                int reservationIndex = DailyDeepLink.getInstance().getReservationIndex();

                if (placeType != null && reservationIndex > 0)
                {
                    BaseActivity baseActivity = (BaseActivity) getActivity();

                    startBookingDetail(baseActivity, placeType, reservationIndex);
                }
            }

            DailyDeepLink.getInstance().clear();
        }

        if (DailyHotel.isLogin() == false)
        {
            AnalyticsManager.getInstance(getActivity()).recordScreen(Screen.BOOKING_BEFORE_LOGIN_BOOKING_LIST);
        }
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

        if (DailyHotel.isLogin() == false)
        {
            updateLayout(false, null);
        } else
        {
            if (mDontReload == true)
            {
                mDontReload = false;
            } else
            {
                lockUI();

                DailyNetworkAPI.getInstance(baseActivity).requestCommonDatetime(mNetworkTag, mDateTimeJsonResponseListener);
            }
        }
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

            Intent intent = LoginActivity.newInstance(baseActivity);
            startActivity(intent);

            //            AnalyticsManager.getInstance(getActivity()).recordEvent(Screen.BOOKING_LIST, Action.CLICK, Label.LOGIN, 0L);
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

        Booking booking = mAdapter.getItem(position);

        if (booking.type == Booking.TYPE_SECTION)
        {
            releaseUiComponent();
            return;
        }

        Intent intent = null;

        if (booking.payType == CODE_PAY_TYPE_CARD_COMPLETE || booking.payType == CODE_PAY_TYPE_ACCOUNT_COMPLETE)
        {
            // 카드결제 완료 || 가상계좌 완료

            if (startBookingDetail(baseActivity, booking.placeType, booking.reservationIndex) == false)
            {
                releaseUiComponent();
            }
        } else if (booking.payType == CODE_PAY_TYPE_ACCOUNT_WAIT)
        {
            // 가상계좌 입금대기
            intent = new Intent(baseActivity, PaymentWaitActivity.class);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_BOOKING, booking);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_VIRTUAL_BOOKING_DETAIL);
        } else
        {
            releaseUiComponent();
        }

        //        HashMap<String, String> params = new HashMap<String, String>();
        //        params.put(Label.TYPE, String.valueOf(item.payType));
        //        params.put(Label.ISUSED, String.valueOf(item.isUsed));
        //
        //        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
        //        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        //
        //        params.put(Label.CHECK_IN, simpleDateFormat.format(item.checkinTime));
        //        params.put(Label.CHECK_OUT, simpleDateFormat.format(item.checkoutTime));
        //        params.put(Label.RESERVATION_INDEX, String.valueOf(item.reservationIndex));
        //
        //        AnalyticsManager.getInstance(getActivity()).recordEvent(Screen.BOOKING_LIST, Action.CLICK, item.placeName, params);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockUI();

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_VIRTUAL_BOOKING_DETAIL:
            {
                if (resultCode == CODE_RESULT_ACTIVITY_EXPIRED_PAYMENT_WAIT)
                {
                    BaseActivity baseActivity = (BaseActivity) getActivity();

                    if (baseActivity == null)
                    {
                        return;
                    }

                    baseActivity.showSimpleDialog(getString(R.string.dialog_notice2), data.getStringExtra("msg"), getString(R.string.dialog_btn_text_confirm), null);
                }
                break;
            }
        }
    }

    private boolean startBookingDetail(BaseActivity baseActivity, PlaceType placeType, int reservationIndex)
    {
        Intent intent = null;

        switch (placeType)
        {
            case HOTEL:
                intent = new Intent(baseActivity, HotelBookingDetailTabActivity.class);
                break;

            case FNB:
                intent = new Intent(baseActivity, GourmetBookingDetailTabActivity.class);
                break;
            default:
                return false;
        }

        if (intent == null)
        {
            return false;
        }

        intent.putExtra(NAME_INTENT_EXTRA_DATA_BOOKINGIDX, reservationIndex);
        baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_BOOKING_DETAIL);

        return true;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UserActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private OnUserActionListener mOnUserActionListener = new OnUserActionListener()
    {
        @Override
        public void delete(final Booking booking)
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null || baseActivity.isFinishing() == true)
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
                            DailyNetworkAPI.getInstance(baseActivity).requestHotelHiddenBooking(mNetworkTag, booking.reservationIndex, mReservationHiddenJsonResponseListener);
                            break;

                        case FNB:
                            DailyNetworkAPI.getInstance(baseActivity).requestGourmetHiddenBooking(mNetworkTag, booking.reservationIndex, mReservationHiddenJsonResponseListener);
                            break;
                    }

                }
            };

            baseActivity.showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_delete_booking), getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), posListener, new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mDontReload = true;
                }
            }, new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    mDontReload = true;
                }
            }, new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    releaseUiComponent();
                }
            }, true);
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mReservationListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            try
            {
                int msgCode = response.getInt("msg_code");

                if (msgCode == 0)
                {
                    JSONArray jsonArray = response.getJSONArray("data");
                    int length = jsonArray.length();

                    if (length == 0)
                    {
                        updateLayout(true, null);

                        AnalyticsManager.getInstance(getActivity()).recordScreen(Screen.BOOKING_LIST_EMPTY);
                    } else
                    {
                        ArrayList<Booking> bookingArrayList = makeBookingList(jsonArray);

                        updateLayout(true, bookingArrayList);

                        Map<String, String> analyticsParams = new HashMap<>();
                        analyticsParams.put(AnalyticsManager.KeyType.NUM_OF_BOOKING, Integer.toString(length));

                        AnalyticsManager.getInstance(getActivity()).recordScreen(Screen.BOOKING_LIST, analyticsParams);
                    }

                    // 사용자 정보 요청.
                    DailyNetworkAPI.getInstance(baseActivity).requestUserProfile(mNetworkTag, mUserProfileJsonResponseListener);
                } else
                {
                    String msg = response.getString("msg");

                    if (Util.isTextEmpty(msg) == false)
                    {
                        DailyToast.showToast(baseActivity, msg, Toast.LENGTH_SHORT);
                    } else
                    {
                        DailyToast.showToast(baseActivity, R.string.act_base_network_connect, Toast.LENGTH_SHORT);
                    }

                    updateLayout(true, null);
                }
            } catch (Exception e)
            {
                updateLayout(true, null);

                onError(e);
            } finally
            {
                unLockUI();
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            baseActivity.onErrorResponse(volleyError);
        }

        private ArrayList<Booking> makeBookingList(JSONArray jsonArray) throws JSONException
        {
            if (jsonArray == null || jsonArray.length() == 0)
            {
                return null;
            }

            int length = jsonArray.length();

            // 입금대기, 결제완료, 이용완료
            ArrayList<Booking> waitBookingList = new ArrayList<>();
            ArrayList<Booking> paymentBookingList = new ArrayList<>();
            ArrayList<Booking> usedBookingList = new ArrayList<>();

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

            ArrayList<Booking> bookingArrayList = new ArrayList<>(length + 3);

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

            return bookingArrayList;
        }
    };

    private DailyHotelJsonResponseListener mDateTimeJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            try
            {
                mCurrentTime = response.getLong("currentDateTime");

                DailyNetworkAPI.getInstance(baseActivity).requestBookingList(mNetworkTag, mReservationListJsonResponseListener);
            } catch (Exception e)
            {
                onError(e);
                unLockUI();
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null || baseActivity.isFinishing() == true)
            {
                return;
            }

            baseActivity.onErrorResponse(volleyError);
        }
    };

    private DailyHotelJsonResponseListener mReservationHiddenJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null || baseActivity.isFinishing() == true)
            {
                return;
            }

            try
            {
                // 해당 화면은 메시지를 넣지 않는다.
                int msgCode = response.getInt("msg_code");

                JSONObject jsonObject = response.getJSONObject("data");
                String message;
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
                            DailyNetworkAPI.getInstance(baseActivity).requestBookingList(mNetworkTag, mReservationListJsonResponseListener);
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
                            DailyNetworkAPI.getInstance(baseActivity).requestBookingList(mNetworkTag, mReservationListJsonResponseListener);
                        }
                    };
                }

                switch (msgCode)
                {
                    case 0:
                    {
                        message = response.getString("msg");
                        DailyToast.showToast(baseActivity, message, Toast.LENGTH_SHORT);

                        DailyNetworkAPI.getInstance(baseActivity).requestBookingList(mNetworkTag, mReservationListJsonResponseListener);
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

                        DailyNetworkAPI.getInstance(baseActivity).requestBookingList(mNetworkTag, mReservationListJsonResponseListener);
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
                            DailyNetworkAPI.getInstance(baseActivity).requestBookingList(mNetworkTag, mReservationListJsonResponseListener);
                        }
                        break;
                    }
                }
            } catch (Exception e)
            {
                onError(e);

                // credit card 요청
                DailyNetworkAPI.getInstance(baseActivity).requestBookingList(mNetworkTag, mReservationListJsonResponseListener);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null || baseActivity.isFinishing() == true)
            {
                return;
            }

            baseActivity.onErrorResponse(volleyError);
        }
    };

    private DailyHotelJsonResponseListener mUserProfileJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                BaseActivity baseActivity = (BaseActivity) getActivity();

                if (baseActivity == null || baseActivity.isFinishing() == true)
                {
                    return;
                }

                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    boolean isVerified = jsonObject.getBoolean("verified");
                    boolean isPhoneVerified = jsonObject.getBoolean("phoneVerified");

                    // 인증 후 인증이 해지된 경우
                    if (isVerified == true && isPhoneVerified == false && DailyPreference.getInstance(baseActivity).isVerification() == true)
                    {
                        baseActivity.showSimpleDialog(null, getString(R.string.message_invalid_verification), getString(R.string.dialog_btn_text_confirm), null);

                        DailyPreference.getInstance(baseActivity).setVerification(false);
                    }
                } else
                {
                    // 인증 해지를 위한 부분이라서 에러시 아무 조치를 취하지 않아도 됨.
                }
            } catch (Exception e)
            {
                onError(e);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            BookingListFragment.this.onErrorResponse(volleyError);
        }
    };
}
