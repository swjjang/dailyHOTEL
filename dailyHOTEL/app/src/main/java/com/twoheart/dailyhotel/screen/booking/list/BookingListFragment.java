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

import android.content.Context;
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

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.model.PlacePaymentInformation;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.screen.booking.detail.PaymentWaitActivity;
import com.twoheart.dailyhotel.screen.booking.detail.gourmet.GourmetBookingDetailTabActivity;
import com.twoheart.dailyhotel.screen.booking.detail.hotel.StayBookingDetailTabActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.widget.PinnedSectionListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;

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
    long mCurrentTime;
    boolean mDontReload;

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

    void updateLayout(boolean isSignin, ArrayList<Booking> bookingArrayList)
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
                } else
                {
                    mAdapter.clear();
                }

                mAdapter.addAll(bookingArrayList);
                mAdapter.notifyDataSetChanged();

                mListView.setVisibility(View.VISIBLE);
                mEmptyLayout.setVisibility(View.GONE);

                // flag가 가상계좌 입금 대기에서 날아온경우
                int index = searchListFromPaymentInformation(baseActivity, //
                    DailyPreference.getInstance(baseActivity).getVirtualAccountReadyFlag() == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY//
                    , bookingArrayList, DailyPreference.getInstance(baseActivity).getPaymentInformation());

                if (index >= 0)
                {
                    unLockUI();
                    mListView.performItemClick(null, index, 0);
                }
            }
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        unLockUI();

        if (DailyHotel.isLogin() == false)
        {
            DailyDeepLink.getInstance().clear();

            AnalyticsManager.getInstance(getActivity()).recordScreen(getActivity(), Screen.BOOKING_BEFORE_LOGIN_BOOKING_LIST, null);
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

                DailyMobileAPI.getInstance(baseActivity).requestCommonDateTime(mNetworkTag, mDateTimeCallBack);
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

        Intent intent;

        if (booking.payType == CODE_PAY_TYPE_CARD_COMPLETE || booking.payType == CODE_PAY_TYPE_ACCOUNT_COMPLETE)
        {
            // 카드결제 완료 || 가상계좌 완료

            if (startBookingDetail(baseActivity, booking.placeType, booking.reservationIndex, booking.hotelImageUrl, false) == false)
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

            case CODE_REQUEST_ACTIVITY_BOOKING_DETAIL:
            {
                if (resultCode == CODE_RESULT_ACTIVITY_REFRESH)
                {
                    mDontReload = false;
                } else
                {

                }
                break;
            }
        }
    }

    private int searchListFromPaymentInformation(Context context, boolean isBankTransfer, ArrayList<Booking> bookingArrayList, String[] paymentInformation)
    {
        if (bookingArrayList == null || bookingArrayList.size() == 0 || paymentInformation == null)
        {
            DailyPreference.getInstance(context).clearPaymentInformation();
            DailyPreference.getInstance(context).setVirtualAccountReadyFlag(-1);
            return -1;
        }

        int size = bookingArrayList.size();

        PlaceType placeType;
        String placeName;
        PlacePaymentInformation.PaymentType paymentType;
        String checkInDate;
        String checkOutDate;

        try
        {
            placeType = PlaceType.valueOf(paymentInformation[0]);
            placeName = paymentInformation[1];
            paymentType = PlacePaymentInformation.PaymentType.valueOf(paymentInformation[2]);

            switch (placeType)
            {
                case HOTEL:
                    // "yyyy-MM-dd'T'HH:mm:ssZZZZZ"
                    checkInDate = DailyCalendar.convertDateFormatString(paymentInformation[3], DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd");
                    checkOutDate = DailyCalendar.convertDateFormatString(paymentInformation[4], DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd");

                    for (int i = 0; i < size; i++)
                    {
                        Booking booking = bookingArrayList.get(i);

                        if (isBankTransfer == true)
                        {
                            if (booking.type == Booking.TYPE_ENTRY && booking.payType == CODE_PAY_TYPE_ACCOUNT_WAIT//
                                && booking.placeName.equalsIgnoreCase(placeName)//
                                && booking.placeType == placeType//
                                && DailyCalendar.format(booking.checkinTime, "yyyy.MM.dd", TimeZone.getTimeZone("GMT")).equalsIgnoreCase(checkInDate) == true//
                                && DailyCalendar.format(booking.checkoutTime, "yyyy.MM.dd", TimeZone.getTimeZone("GMT")).equalsIgnoreCase(checkOutDate) == true)
                            {
                                return i;
                            }
                        } else
                        {
                            if (booking.type == Booking.TYPE_ENTRY && booking.payType != CODE_PAY_TYPE_ACCOUNT_WAIT //
                                && booking.readyForRefund == false//
                                && booking.placeName.equalsIgnoreCase(placeName)//
                                && booking.placeType == placeType//
                                && DailyCalendar.format(booking.checkinTime, "yyyy.MM.dd", TimeZone.getTimeZone("GMT")).equalsIgnoreCase(checkInDate) == true//
                                && DailyCalendar.format(booking.checkoutTime, "yyyy.MM.dd", TimeZone.getTimeZone("GMT")).equalsIgnoreCase(checkOutDate) == true)
                            {
                                return i;
                            }
                        }
                    }
                    break;

                case FNB:
                    // yyyy.MM.dd (EEE)
                    checkInDate = paymentInformation[3].split(" ")[0];

                    for (int i = 0; i < size; i++)
                    {
                        Booking booking = bookingArrayList.get(i);

                        if (isBankTransfer == true)
                        {
                            if (booking.type == Booking.TYPE_ENTRY && booking.payType == CODE_PAY_TYPE_ACCOUNT_WAIT//
                                && booking.placeName.equalsIgnoreCase(placeName)//
                                && booking.placeType == placeType//
                                && DailyCalendar.format(booking.checkinTime, "yyyy.MM.dd", TimeZone.getTimeZone("GMT")).equalsIgnoreCase(checkInDate) == true)
                            {
                                return i;
                            }
                        } else
                        {
                            if (booking.type == Booking.TYPE_ENTRY && booking.payType != CODE_PAY_TYPE_ACCOUNT_WAIT//
                                && booking.readyForRefund == false//
                                && booking.placeName.equalsIgnoreCase(placeName)//
                                && booking.placeType == placeType//
                                && DailyCalendar.format(booking.checkinTime, "yyyy.MM.dd", TimeZone.getTimeZone("GMT")).equalsIgnoreCase(checkInDate) == true)
                            {
                                return i;
                            }
                        }
                    }
                    break;
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        } finally
        {
            DailyPreference.getInstance(context).setVirtualAccountReadyFlag(-1);
            DailyPreference.getInstance(context).clearPaymentInformation();
        }

        return -1;
    }

    boolean startBookingDetail(BaseActivity baseActivity, PlaceType placeType,//
                               int reservationIndex, String imageUrl, boolean isDeepLink)
    {
        Intent intent;

        switch (placeType)
        {
            case HOTEL:
                intent = new Intent(baseActivity, StayBookingDetailTabActivity.class);
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
        intent.putExtra(NAME_INTENT_EXTRA_DATA_URL, imageUrl);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DEEPLINK, isDeepLink);
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
                            DailyMobileAPI.getInstance(baseActivity).requestStayHiddenBooking(mNetworkTag, booking.reservationIndex, mReservationHiddenCallback);
                            break;

                        case FNB:
                            DailyMobileAPI.getInstance(baseActivity).requestGourmetHiddenBooking(mNetworkTag, booking.reservationIndex, mReservationHiddenCallback);
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

    retrofit2.Callback mReservationListCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msg_code");

                    if (msgCode == 0)
                    {
                        JSONArray dataJSONArray = responseJSONObject.getJSONArray("data");
                        int length = dataJSONArray.length();
                        ArrayList<Booking> bookingArrayList = null;

                        if (length == 0)
                        {
                            updateLayout(true, null);

                            AnalyticsManager.getInstance(getActivity()).recordScreen(getActivity(), Screen.BOOKING_LIST_EMPTY, null);
                        } else
                        {
                            bookingArrayList = makeBookingList(dataJSONArray);

                            updateLayout(true, bookingArrayList);

                            Map<String, String> analyticsParams = new HashMap<>();
                            analyticsParams.put(AnalyticsManager.KeyType.NUM_OF_BOOKING, Integer.toString(length));

                            AnalyticsManager.getInstance(getActivity()).recordScreen(getActivity(), Screen.BOOKING_LIST, null, analyticsParams);
                        }

                        if (DailyDeepLink.getInstance().isValidateLink() == true)
                        {
                            if (DailyDeepLink.getInstance().isBookingDetailView() == true && length != 0)
                            {
                                PlaceType placeType = null;

                                if ("stay".equalsIgnoreCase(DailyDeepLink.getInstance().getPlaceType()) == true)
                                {
                                    placeType = PlaceType.HOTEL;
                                } else if ("gourmet".equalsIgnoreCase(DailyDeepLink.getInstance().getPlaceType()) == true)
                                {
                                    placeType = PlaceType.FNB;
                                }

                                final int reservationIndex = DailyDeepLink.getInstance().getReservationIndex();

                                if (placeType != null && reservationIndex > 0)
                                {
                                    for (Booking booking : bookingArrayList)
                                    {
                                        if (booking.reservationIndex == reservationIndex)
                                        {
                                            startBookingDetail(baseActivity, placeType, reservationIndex, booking.hotelImageUrl, true);
                                            break;
                                        }
                                    }
                                }
                            }

                            DailyDeepLink.getInstance().clear();
                        }

                        // 사용자 정보 요청.
                        DailyMobileAPI.getInstance(baseActivity).requestUserProfile(mNetworkTag, mUserProfileCallback);
                    } else
                    {
                        String msg = responseJSONObject.getString("msg");

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
            } else
            {
                baseActivity.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            baseActivity.onError(t);
        }

        private ArrayList<Booking> makeBookingList(JSONArray jsonArray) throws Exception
        {
            if (jsonArray == null || jsonArray.length() == 0)
            {
                return null;
            }

            int length = jsonArray.length();

            // 무료취소대기, 입금대기, 결제완료, 이용완료
            ArrayList<Booking> waitRefundBookingList = new ArrayList<>();
            ArrayList<Booking> waitBookingList = new ArrayList<>();
            ArrayList<Booking> paymentBookingList = new ArrayList<>();
            ArrayList<Booking> usedBookingList = new ArrayList<>();

            for (int i = 0; i < length; i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                Booking booking = new Booking(jsonObject);

                if (booking.readyForRefund == true)
                {
                    waitRefundBookingList.add(booking);
                } else
                {
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
            }

            ArrayList<Booking> bookingArrayList = new ArrayList<>(length + 3);

            // 무료취소대기가 있는 경우
            if (waitRefundBookingList.size() > 0)
            {
                Booking sectionWaitRefund = new Booking(getString(R.string.frag_booking_wait_refund));
                bookingArrayList.add(sectionWaitRefund);
                bookingArrayList.addAll(waitRefundBookingList);
            }

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

    private retrofit2.Callback mDateTimeCallBack = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");

                    if (msgCode == 100)
                    {
                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                        mCurrentTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("currentDateTime"), DailyCalendar.ISO_8601_FORMAT);

                        DailyMobileAPI.getInstance(baseActivity).requestBookingList(mNetworkTag, mReservationListCallback);
                    } else
                    {
                        String message = responseJSONObject.getString("msg");
                        onErrorPopupMessage(msgCode, message);
                    }
                } catch (Exception e)
                {
                    onError(e);
                    unLockUI();
                }
            } else
            {
                baseActivity.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null || baseActivity.isFinishing() == true)
            {
                return;
            }

            baseActivity.onError(t);
        }
    };

    retrofit2.Callback mReservationHiddenCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null || baseActivity.isFinishing() == true)
            {
                return;
            }

            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    // 해당 화면은 메시지를 넣지 않는다.
                    int msgCode = responseJSONObject.getInt("msg_code");

                    JSONObject datJSONObject = responseJSONObject.getJSONObject("data");
                    String message;
                    boolean result = false;

                    if (datJSONObject != null)
                    {
                        if (datJSONObject.has("isSuccess") == true)
                        {
                            result = datJSONObject.getInt("isSuccess") == 1;
                        } else if (datJSONObject.has("is_success") == true)
                        {
                            result = datJSONObject.getBoolean("is_success");
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
                                DailyMobileAPI.getInstance(baseActivity).requestBookingList(mNetworkTag, mReservationListCallback);
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
                                DailyMobileAPI.getInstance(baseActivity).requestBookingList(mNetworkTag, mReservationListCallback);
                            }
                        };
                    }

                    switch (msgCode)
                    {
                        case 0:
                        {
                            message = responseJSONObject.getString("msg");
                            DailyToast.showToast(baseActivity, message, Toast.LENGTH_SHORT);

                            DailyMobileAPI.getInstance(baseActivity).requestBookingList(mNetworkTag, mReservationListCallback);
                            break;
                        }

                        // Toast
                        case 100:
                        {
                            message = responseJSONObject.getString("msg");

                            if (Util.isTextEmpty(message) == false)
                            {
                                DailyToast.showToast(baseActivity, message, Toast.LENGTH_SHORT);
                            }

                            DailyMobileAPI.getInstance(baseActivity).requestBookingList(mNetworkTag, mReservationListCallback);
                            break;
                        }

                        // Popup
                        case 200:
                        {
                            message = responseJSONObject.getString("msg");

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
                                DailyMobileAPI.getInstance(baseActivity).requestBookingList(mNetworkTag, mReservationListCallback);
                            }
                            break;
                        }
                    }
                } catch (Exception e)
                {
                    onError(e);

                    // credit card 요청
                    DailyMobileAPI.getInstance(baseActivity).requestBookingList(mNetworkTag, mReservationListCallback);
                }
            } else
            {
                baseActivity.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null || baseActivity.isFinishing() == true)
            {
                return;
            }

            baseActivity.onError(t);
        }
    };

    retrofit2.Callback mUserProfileCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null || baseActivity.isFinishing() == true)
            {
                return;
            }

            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");

                    if (msgCode == 100)
                    {
                        JSONObject jsonObject = responseJSONObject.getJSONObject("data");

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
            } else
            {
                BookingListFragment.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            BookingListFragment.this.onError(t);
        }
    };
}
