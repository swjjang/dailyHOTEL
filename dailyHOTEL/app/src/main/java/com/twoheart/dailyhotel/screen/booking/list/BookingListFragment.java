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
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.ListItem;
import com.daily.dailyhotel.entity.Reservation;
import com.daily.dailyhotel.repository.local.ConfigLocalImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.FacebookRemoteImpl;
import com.daily.dailyhotel.repository.remote.KakaoRemoteImpl;
import com.daily.dailyhotel.repository.remote.ReservationRemoteImpl;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.model.PlacePaymentInformation;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseMenuNavigationFragment;
import com.twoheart.dailyhotel.screen.booking.detail.PaymentWaitActivity;
import com.twoheart.dailyhotel.screen.booking.detail.gourmet.GourmetReservationDetailActivity;
import com.twoheart.dailyhotel.screen.booking.detail.hotel.StayReservationDetailActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyExternalDeepLink;
import com.twoheart.dailyhotel.util.DailyInternalDeepLink;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.widget.PinnedSectionListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * 예약한 호텔의 리스트들을 출력.
 *
 * @author jangjunho
 */
public class BookingListFragment extends BaseMenuNavigationFragment implements OnItemClickListener, OnClickListener
{
    private ReservationListAdapter mAdapter;
    private RelativeLayout mEmptyLayout;
    private PinnedSectionListView mListView;
    private View mLoginView;
    boolean mDontReload;

    private CommonDateTime mCommonDateTime;
    private DailyDeepLink mDailyDeepLink;


    private CommonRemoteImpl mCommonRemoteImpl;
    private ReservationRemoteImpl mReservationRemoteImpl;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public interface OnUserActionListener
    {
        void delete(Reservation reservation);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mCommonRemoteImpl = new CommonRemoteImpl(getContext());
        mReservationRemoteImpl = new ReservationRemoteImpl(getContext());
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

    @Override
    public void onNewBundle(Bundle bundle)
    {
        if (bundle == null)
        {
            return;
        }

        if (bundle.containsKey(Constants.NAME_INTENT_EXTRA_DATA_DEEPLINK) == true)
        {
            try
            {
                mDailyDeepLink = DailyDeepLink.getNewInstance(Uri.parse(bundle.getString(Constants.NAME_INTENT_EXTRA_DATA_DEEPLINK)));
            } catch (Exception e)
            {
                mDailyDeepLink = null;
            }
        }
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
        mListView.setOnScrollChangedListener(mOnScreenScrollChangeListener);

        mEmptyLayout = (RelativeLayout) view.findViewById(R.id.emptyLayout);
        mLoginView = view.findViewById(R.id.loginView);

        mLoginView.setOnClickListener(this);
    }

    void updateLayout(boolean isSignin, List<ListItem> listItemList)
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
            if (listItemList == null || listItemList.size() == 0)
            {
                if (mAdapter != null)
                {
                    mAdapter.clear();
                }

                //예약한 호텔이 없는 경우
                mListView.setVisibility(View.GONE);
                mEmptyLayout.setVisibility(View.VISIBLE);
                mLoginView.setVisibility(View.INVISIBLE);
            } else
            {
                if (mAdapter == null)
                {
                    mAdapter = new ReservationListAdapter(baseActivity, R.layout.list_row_booking, new ArrayList<>());
                    mAdapter.setOnUserActionListener(mOnUserActionListener);
                    mListView.setOnItemClickListener(BookingListFragment.this);
                    mListView.setAdapter(mAdapter);
                } else
                {
                    mAdapter.clear();
                }

                mAdapter.addAll(listItemList);
                mAdapter.notifyDataSetChanged();

                mListView.setVisibility(View.VISIBLE);
                mEmptyLayout.setVisibility(View.GONE);
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
            if (mDailyDeepLink != null)
            {
                mDailyDeepLink.clear();
            }

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

                addCompositeDisposable(Observable.zip(mCommonRemoteImpl.getCommonDateTime()//
                    , mReservationRemoteImpl.getReservationList(), mReservationRemoteImpl.getStayOutboundReservationList()//
                    , new Function3<CommonDateTime, List<Reservation>, List<Reservation>, List<ListItem>>()
                    {
                        @Override
                        public List<ListItem> apply(@NonNull CommonDateTime commonDateTime//
                            , @NonNull List<Reservation> reservationList, @NonNull List<Reservation> stayOutboundReservationList) throws Exception
                        {
                            setCommonDateTime(commonDateTime);

                            reservationList.addAll(stayOutboundReservationList);

                            List<ListItem> listItemList = getReservationList(reservationList);

                            return listItemList;
                        }
                    }).subscribe(new Consumer<List<ListItem>>()
                {
                    @Override
                    public void accept(@NonNull List<ListItem> listItemList) throws Exception
                    {

                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception
                    {

                    }
                }));
            }
        }
    }

    @Override
    public void onDestroy()
    {
        clearCompositeDisposable();

        super.onDestroy();
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == mLoginView.getId())
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            Intent intent = LoginActivity.newInstance(baseActivity);
            startActivity(intent);

            //            AnalyticsManager.getInstance(getActivity()).recordEvent(Screen.BOOKING_LIST, Action.CLICK, Label.LOGIN_, 0L);
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

        ListItem listItem = mAdapter.getItem(position);

        if (listItem.mType == ListItem.TYPE_SECTION)
        {
            releaseUiComponent();
            return;
        }

        Reservation reservation = listItem.getItem();
        Intent intent;

        if (reservation.payType == CODE_PAY_TYPE_CARD_COMPLETE || reservation.payType == CODE_PAY_TYPE_ACCOUNT_COMPLETE)
        {
            // 카드결제 완료 || 가상계좌 완료

            if (startBookingDetail(baseActivity, reservation.placeType, reservation.reservationIndex, reservation.imageUrl, false) == false)
            {
                releaseUiComponent();
            }
        } else if (reservation.payType == CODE_PAY_TYPE_ACCOUNT_WAIT)
        {
            // 가상계좌 입금대기
            intent = PaymentWaitActivity.newInstance(baseActivity, reservation);
            baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_VIRTUAL_BOOKING_DETAIL);
        } else
        {
            releaseUiComponent();
        }
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
                                && DailyCalendar.format(booking.checkinTime, "yyyy.MM.dd", TimeZone.getTimeZone("GMT+09:00")).equalsIgnoreCase(checkInDate) == true//
                                && DailyCalendar.format(booking.checkoutTime, "yyyy.MM.dd", TimeZone.getTimeZone("GMT+09:00")).equalsIgnoreCase(checkOutDate) == true)
                            {
                                return i;
                            }
                        } else
                        {
                            if (booking.type == Booking.TYPE_ENTRY && booking.payType != CODE_PAY_TYPE_ACCOUNT_WAIT //
                                && booking.readyForRefund == false//
                                && booking.placeName.equalsIgnoreCase(placeName)//
                                && booking.placeType == placeType//
                                && DailyCalendar.format(booking.checkinTime, "yyyy.MM.dd", TimeZone.getTimeZone("GMT+09:00")).equalsIgnoreCase(checkInDate) == true//
                                && DailyCalendar.format(booking.checkoutTime, "yyyy.MM.dd", TimeZone.getTimeZone("GMT+09:00")).equalsIgnoreCase(checkOutDate) == true)
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
                                && DailyCalendar.format(booking.checkinTime, "yyyy.MM.dd", TimeZone.getTimeZone("GMT+09:00")).equalsIgnoreCase(checkInDate) == true)
                            {
                                return i;
                            }
                        } else
                        {
                            if (booking.type == Booking.TYPE_ENTRY && booking.payType != CODE_PAY_TYPE_ACCOUNT_WAIT//
                                && booking.readyForRefund == false//
                                && booking.placeName.equalsIgnoreCase(placeName)//
                                && booking.placeType == placeType//
                                && DailyCalendar.format(booking.checkinTime, "yyyy.MM.dd", TimeZone.getTimeZone("GMT+09:00")).equalsIgnoreCase(checkInDate) == true)
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

    private int searchGourmetFromPaymentInformation(Context context, String placeName//
        , PlacePaymentInformation.PaymentType paymentType//
        , String visitTime, ArrayList<Booking> bookingArrayList)
    {
        if (DailyTextUtils.isTextEmpty(placeName, visitTime) == true//
            || bookingArrayList == null || bookingArrayList.size() == 0 || paymentType == null)
        {
            DailyPreference.getInstance(context).clearPaymentInformation();
            DailyPreference.getInstance(context).setVirtualAccountReadyFlag(-1);
            return -1;
        }

        int size = bookingArrayList.size();

        try
        {
            String visitDate = DailyCalendar.convertDateFormatString(visitTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd");

            for (int i = 0; i < size; i++)
            {
                Booking booking = bookingArrayList.get(i);

                if (PlacePaymentInformation.PaymentType.VBANK == paymentType)
                {
                    if (booking.type == Booking.TYPE_ENTRY && booking.payType == CODE_PAY_TYPE_ACCOUNT_WAIT//
                        && booking.placeName.equalsIgnoreCase(placeName)//
                        && booking.placeType == PlaceType.FNB//
                        && DailyCalendar.format(booking.checkinTime, "yyyy.MM.dd", TimeZone.getTimeZone("GMT+09:00")).equalsIgnoreCase(visitDate) == true)
                    {
                        return i;
                    }
                } else
                {
                    if (booking.type == Booking.TYPE_ENTRY && booking.payType != CODE_PAY_TYPE_ACCOUNT_WAIT//
                        && booking.readyForRefund == false//
                        && booking.placeName.equalsIgnoreCase(placeName)//
                        && booking.placeType == PlaceType.FNB//
                        && DailyCalendar.format(booking.checkinTime, "yyyy.MM.dd", TimeZone.getTimeZone("GMT+09:00")).equalsIgnoreCase(visitDate) == true)
                    {
                        return i;
                    }
                }
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

    private int searchStayFromPaymentInformation(Context context, String placeName//
        , PlacePaymentInformation.PaymentType paymentType//
        , String checkInTime, String checkOutTime//
        , ArrayList<Booking> bookingArrayList)
    {
        if (DailyTextUtils.isTextEmpty(placeName, checkInTime, checkOutTime) == true//
            || bookingArrayList == null || bookingArrayList.size() == 0 || paymentType == null)
        {
            DailyPreference.getInstance(context).clearPaymentInformation();
            DailyPreference.getInstance(context).setVirtualAccountReadyFlag(-1);
            return -1;
        }

        int size = bookingArrayList.size();

        try
        {
            // "yyyy-MM-dd'T'HH:mm:ssZZZZZ"
            String checkInDate = DailyCalendar.convertDateFormatString(checkInTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd");
            String checkOutDate = DailyCalendar.convertDateFormatString(checkOutTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd");

            for (int i = 0; i < size; i++)
            {
                Booking booking = bookingArrayList.get(i);

                if (PlacePaymentInformation.PaymentType.VBANK == paymentType)
                {
                    if (booking.type == Booking.TYPE_ENTRY && booking.payType == CODE_PAY_TYPE_ACCOUNT_WAIT//
                        && booking.placeName.equalsIgnoreCase(placeName)//
                        && booking.placeType == PlaceType.HOTEL//
                        && DailyCalendar.format(booking.checkinTime, "yyyy.MM.dd", TimeZone.getTimeZone("GMT+09:00")).equalsIgnoreCase(checkInDate) == true//
                        && DailyCalendar.format(booking.checkoutTime, "yyyy.MM.dd", TimeZone.getTimeZone("GMT+09:00")).equalsIgnoreCase(checkOutDate) == true)
                    {
                        return i;
                    }
                } else
                {
                    if (booking.type == Booking.TYPE_ENTRY && booking.payType != CODE_PAY_TYPE_ACCOUNT_WAIT //
                        && booking.readyForRefund == false//
                        && booking.placeName.equalsIgnoreCase(placeName)//
                        && booking.placeType == PlaceType.HOTEL//
                        && DailyCalendar.format(booking.checkinTime, "yyyy.MM.dd", TimeZone.getTimeZone("GMT+09:00")).equalsIgnoreCase(checkInDate) == true//
                        && DailyCalendar.format(booking.checkoutTime, "yyyy.MM.dd", TimeZone.getTimeZone("GMT+09:00")).equalsIgnoreCase(checkOutDate) == true)
                    {
                        return i;
                    }
                }
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

    private int searchStayFromPaymentInformation(Context context, String placeName//
        , PlacePaymentInformation.PaymentType paymentType//
        , String checkInTime, String checkOutTime//
        , List<Reservation> reservationList)
    {
        if (DailyTextUtils.isTextEmpty(placeName, checkInTime, checkOutTime) == true//
            || reservationList == null || reservationList.size() == 0 || paymentType == null)
        {
            DailyPreference.getInstance(context).clearPaymentInformation();
            DailyPreference.getInstance(context).setVirtualAccountReadyFlag(-1);
            return -1;
        }

        int size = reservationList.size();

        try
        {
            // "yyyy-MM-dd'T'HH:mm:ssZZZZZ"
            String checkInDate = DailyCalendar.convertDateFormatString(checkInTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd");
            String checkOutDate = DailyCalendar.convertDateFormatString(checkOutTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd");

            for (int i = 0; i < size; i++)
            {
                Reservation reservation = reservationList.get(i);

                if (PlacePaymentInformation.PaymentType.VBANK == paymentType)
                {
                    if (reservation.payType == CODE_PAY_TYPE_ACCOUNT_WAIT//
                        && reservation.placeName.equalsIgnoreCase(placeName)//
                        && reservation.placeType == Reservation.PlaceType.STAY//
                        && DailyCalendar.convertDateFormatString(reservation.checkInDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd").equalsIgnoreCase(checkInDate) == true//
                        && DailyCalendar.convertDateFormatString(reservation.checkOutDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd").equalsIgnoreCase(checkOutDate) == true)
                    {
                        return i;
                    }
                } else
                {
                    if (reservation.payType != CODE_PAY_TYPE_ACCOUNT_WAIT //
                        && reservation.readyForRefund == false//
                        && reservation.placeName.equalsIgnoreCase(placeName)//
                        && reservation.placeType == Reservation.PlaceType.STAY//
                        && DailyCalendar.convertDateFormatString(reservation.checkInDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd").equalsIgnoreCase(checkInDate) == true//
                        && DailyCalendar.convertDateFormatString(reservation.checkOutDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd").equalsIgnoreCase(checkOutDate) == true)
                    {
                        return i;
                    }
                }
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

    private int searchGourmetFromPaymentInformation(Context context, String placeName//
        , PlacePaymentInformation.PaymentType paymentType//
        , String visitTime, List<Reservation> reservationList)
    {
        if (DailyTextUtils.isTextEmpty(placeName, visitTime) == true//
            || reservationList == null || reservationList.size() == 0 || paymentType == null)
        {
            DailyPreference.getInstance(context).clearPaymentInformation();
            DailyPreference.getInstance(context).setVirtualAccountReadyFlag(-1);
            return -1;
        }

        int size = reservationList.size();

        try
        {
            String visitDate = DailyCalendar.convertDateFormatString(visitTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd");

            for (int i = 0; i < size; i++)
            {
                Reservation reservation = reservationList.get(i);

                if (PlacePaymentInformation.PaymentType.VBANK == paymentType)
                {
                    if (reservation.payType == CODE_PAY_TYPE_ACCOUNT_WAIT//
                        && reservation.placeName.equalsIgnoreCase(placeName)//
                        && reservation.placeType == Reservation.PlaceType.GOURMET//
                        && DailyCalendar.convertDateFormatString(reservation.checkInDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd").equalsIgnoreCase(visitDate) == true)
                    {
                        return i;
                    }
                } else
                {
                    if (reservation.payType != CODE_PAY_TYPE_ACCOUNT_WAIT//
                        && reservation.readyForRefund == false//
                        && reservation.placeName.equalsIgnoreCase(placeName)//
                        && reservation.placeType == Reservation.PlaceType.GOURMET//
                        && DailyCalendar.convertDateFormatString(reservation.checkInDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd").equalsIgnoreCase(visitDate) == true)
                    {
                        return i;
                    }
                }
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

    boolean startBookingDetail(BaseActivity baseActivity, Reservation.PlaceType placeType,//
                               int reservationIndex, String imageUrl, boolean isDeepLink)
    {
        Intent intent;

        switch (placeType)
        {
            case STAY:
                intent = StayReservationDetailActivity.newInstance(baseActivity, reservationIndex, imageUrl, isDeepLink);
                break;

            case GOURMET:
                intent = GourmetReservationDetailActivity.newInstance(baseActivity, reservationIndex, imageUrl, isDeepLink);
                break;
            default:
                return false;
        }

        baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_BOOKING_DETAIL);

        return true;
    }

    @Override
    public void setOnScrollChangedListener(BaseMenuNavigationFragment.OnScreenScrollChangeListener listener)
    {
        mOnScreenScrollChangeListener = listener;

        if (mListView != null)
        {
            mListView.setOnScrollChangedListener(listener);
        }
    }

    @Override
    public void scrollTop()
    {
        if (mListView != null)
        {
            mListView.smoothScrollToPosition(0);
        }
    }

    private List<ListItem> getReservationList(List<Reservation> reservationList) throws Exception
    {
        if (reservationList == null || reservationList.size() == 0)
        {
            return null;
        }

        // 무료취소대기, 입금대기, 결제완료, 이용완료
        List<Reservation> waitRefundList = new ArrayList<>();
        List<Reservation> depositWaitingList = new ArrayList<>();
        List<Reservation> beforeUseList = new ArrayList<>();
        List<Reservation> afterUseList = new ArrayList<>();

        for (Reservation reservation : reservationList)
        {
            reservation.remainingDays = DailyCalendar.compareDateDay(reservation.checkInDateTime, mCommonDateTime.currentDateTime);

            if (reservation.readyForRefund == true)
            {
                waitRefundList.add(reservation);
            } else
            {
                switch (reservation.payType)
                {
                    case CODE_PAY_TYPE_CARD_COMPLETE:
                    case CODE_PAY_TYPE_ACCOUNT_COMPLETE:
                        reservation.isUsed = DailyCalendar.compareDateDay(reservation.checkOutDateTime, mCommonDateTime.currentDateTime) < 0;

                        if (reservation.isUsed)
                        {
                            afterUseList.add(reservation);
                        } else
                        {
                            beforeUseList.add(reservation);
                        }
                        break;

                    case CODE_PAY_TYPE_ACCOUNT_WAIT:
                        depositWaitingList.add(reservation);
                        break;
                }
            }
        }

        Comparator<Reservation> ascComparator = new Comparator<Reservation>()
        {
            public int compare(Reservation reservation1, Reservation reservation2)
            {
                int compareDay;

                try
                {
                    compareDay = DailyCalendar.compareDateDay(reservation1.checkInDateTime, reservation2.checkInDateTime);
                } catch (Exception e)
                {
                    ExLog.e(e.toString());

                    compareDay = 0;
                }

                if (compareDay < 0)
                {
                    return 1;
                } else if (compareDay > 0)
                {
                    return -1;
                } else
                {
                    if (reservation1.reservationIndex < reservation2.reservationIndex)
                    {
                        return 1;
                    } else
                    {
                        return -1;
                    }
                }
            }
        };

        Comparator<Reservation> descComparator = new Comparator<Reservation>()
        {
            public int compare(Reservation reservation1, Reservation reservation2)
            {
                int compareDay;

                try
                {
                    compareDay = DailyCalendar.compareDateDay(reservation1.checkInDateTime, reservation2.checkInDateTime);
                } catch (Exception e)
                {
                    ExLog.e(e.toString());

                    compareDay = 0;
                }

                if (compareDay < 0)
                {
                    return -1;
                } else if (compareDay > 0)
                {
                    return 1;
                } else
                {
                    if (reservation1.reservationIndex < reservation2.reservationIndex)
                    {
                        return -1;
                    } else
                    {
                        return 1;
                    }
                }
            }
        };

        List<ListItem> listItemList = new ArrayList<>(reservationList.size() + 5);

        // 무료취소대기가 있는 경우
        if (waitRefundList.size() > 0)
        {
            Collections.sort(waitRefundList, ascComparator);

            ListItem sectionListItem = new ListItem(ListItem.TYPE_SECTION, getString(R.string.frag_booking_wait_refund));
            listItemList.add(sectionListItem);

            for (Reservation reservation : waitRefundList)
            {
                listItemList.add(new ListItem(ListItem.TYPE_ENTRY, reservation));
            }
        }

        // 입금 대기가 있는 경우.
        if (depositWaitingList.size() > 0)
        {
            Collections.sort(depositWaitingList, ascComparator);

            ListItem sectionListItem = new ListItem(ListItem.TYPE_SECTION, getString(R.string.frag_booking_wait_account));
            listItemList.add(sectionListItem);

            for (Reservation reservation : depositWaitingList)
            {
                listItemList.add(new ListItem(ListItem.TYPE_ENTRY, reservation));
            }
        }

        // 결제 완료가 있는 경우.
        if (beforeUseList.size() > 0)
        {
            Collections.sort(beforeUseList, ascComparator);

            ListItem sectionListItem = new ListItem(ListItem.TYPE_SECTION, getString(R.string.frag_booking_complete_payment));
            listItemList.add(sectionListItem);

            for (Reservation reservation : beforeUseList)
            {
                listItemList.add(new ListItem(ListItem.TYPE_ENTRY, reservation));
            }
        }

        // 이용 완료가 있는 경우.
        if (afterUseList.size() > 0)
        {
            Collections.sort(afterUseList, descComparator);

            ListItem sectionListItem = new ListItem(ListItem.TYPE_SECTION, getString(R.string.frag_booking_use));
            listItemList.add(sectionListItem);

            for (Reservation reservation : beforeUseList)
            {
                listItemList.add(new ListItem(ListItem.TYPE_ENTRY, reservation));
            }
        }

        return listItemList;
    }

    private void setCommonDateTime(CommonDateTime commonDateTime)
    {
        mCommonDateTime = commonDateTime;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UserActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private OnUserActionListener mOnUserActionListener = new OnUserActionListener()
    {
        @Override
        public void delete(final Reservation reservation)
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

                    switch (reservation.placeType)
                    {
                        case STAY:
                            DailyMobileAPI.getInstance(baseActivity).requestStayHiddenBooking(mNetworkTag, reservation.reservationIndex, mReservationHiddenCallback);
                            break;

                        case GOURMET:
                            DailyMobileAPI.getInstance(baseActivity).requestGourmetHiddenBooking(mNetworkTag, reservation.reservationIndex, mReservationHiddenCallback);
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

    private boolean onDeepLink(BaseActivity baseActivity, List<Reservation> reservationList)
    {
        if (baseActivity == null || reservationList == null || reservationList.size() == 0)
        {
            return false;
        }

        try
        {
            if (mDailyDeepLink != null && mDailyDeepLink.isValidateLink() == true)
            {
                if (mDailyDeepLink.isInternalDeepLink() == true)
                {
                    DailyInternalDeepLink internalDeepLink = (DailyInternalDeepLink) mDailyDeepLink;

                    if (internalDeepLink.isBookingDetailView() == true)
                    {
                        Reservation.PlaceType placeType = null;

                        if ("stay".equalsIgnoreCase(internalDeepLink.getPlaceType()) == true)
                        {
                            placeType = Reservation.PlaceType.STAY;
                        } else if ("gourmet".equalsIgnoreCase(internalDeepLink.getPlaceType()) == true)
                        {
                            placeType = Reservation.PlaceType.GOURMET;
                        } else if ("stayOutbound".equalsIgnoreCase(internalDeepLink.getPlaceType()) == true)
                        {
                            placeType = Reservation.PlaceType.STAY_OUTBOUND;
                        }

                        if (placeType == Reservation.PlaceType.STAY_OUTBOUND)
                        {
                            int reservationId = internalDeepLink.getReservatoinId();
                            int size = reservationList.size();

                            for (int i = 0; i < size; i++)
                            {
                                Reservation reservation = reservationList.get(i);

                                if (reservation.reservationIndex == reservationId)
                                {
                                    unLockUI();
                                    mListView.performItemClick(null, i, 0);
                                    break;
                                }
                            }
                        } else
                        {
                            PlacePaymentInformation.PaymentType paymentType = PlacePaymentInformation.PaymentType.valueOf(internalDeepLink.getPaymentType());
                            String placeName = internalDeepLink.getPlaceName();

                            int index = -1;

                            switch (placeType)
                            {
                                case STAY:
                                    String checkInTime = internalDeepLink.getCheckInTime();
                                    String checkOutTime = internalDeepLink.getCheckOutTime();

                                    index = searchStayFromPaymentInformation(baseActivity//
                                        , placeName, paymentType, checkInTime, checkOutTime, reservationList);
                                    break;

                                case GOURMET:
                                    String visitTime = internalDeepLink.getVisitTime();

                                    index = searchGourmetFromPaymentInformation(baseActivity//
                                        , placeName, paymentType, visitTime, reservationList);
                                    break;
                            }

                            if (index >= 0)
                            {
                                unLockUI();
                                mListView.performItemClick(null, index, 0);
                            }
                        }
                    }
                } else
                {
                    DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) mDailyDeepLink;

                    if (externalDeepLink.isBookingDetailView() == true)
                    {
                        Reservation.PlaceType placeType = null;

                        if ("stay".equalsIgnoreCase(externalDeepLink.getPlaceType()) == true)
                        {
                            placeType = Reservation.PlaceType.STAY;
                        } else if ("gourmet".equalsIgnoreCase(externalDeepLink.getPlaceType()) == true)
                        {
                            placeType = Reservation.PlaceType.GOURMET;
                        } else if ("stayOutbound".equalsIgnoreCase(externalDeepLink.getPlaceType()) == true)
                        {
                            placeType = Reservation.PlaceType.STAY_OUTBOUND;
                        }

                        final int reservationIndex = externalDeepLink.getReservationIndex();

                        if (placeType != null)
                        {
                            String imageUrl = null;

                            for (Reservation reservation : reservationList)
                            {
                                if (reservation.reservationIndex == reservationIndex)
                                {
                                    imageUrl = reservation.imageUrl;
                                    break;
                                }
                            }

                            startBookingDetail(baseActivity, placeType, reservationIndex, imageUrl, true);
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            return false;

        } finally
        {
            if (mDailyDeepLink != null)
            {
                mDailyDeepLink.clear();
            }
        }

        return true;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //    retrofit2.Callback mReservationListCallback = new retrofit2.Callback<JSONObject>()
    //    {
    //        @Override
    //        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
    //        {
    //            BaseActivity baseActivity = (BaseActivity) getActivity();
    //
    //            if (baseActivity == null)
    //            {
    //                return;
    //            }
    //
    //            if (response != null && response.isSuccessful() && response.body() != null)
    //            {
    //                try
    //                {
    //                    JSONObject responseJSONObject = response.body();
    //
    //                    int msgCode = responseJSONObject.getInt("msg_code");
    //
    //                    if (msgCode == 0)
    //                    {
    //                        JSONArray dataJSONArray = responseJSONObject.getJSONArray("data");
    //                        int length = dataJSONArray.length();
    //                        ArrayList<Booking> bookingArrayList = null;
    //
    //                        if (length == 0)
    //                        {
    //                            updateLayout(true, null);
    //
    //                            AnalyticsManager.getInstance(getActivity()).recordScreen(getActivity(), Screen.BOOKING_LIST_EMPTY, null);
    //                        } else
    //                        {
    //                            bookingArrayList = makeBookingList(dataJSONArray);
    //
    //                            updateLayout(true, bookingArrayList);
    //
    //                            Map<String, String> analyticsParams = new HashMap<>();
    //                            analyticsParams.put(AnalyticsManager.KeyType.NUM_OF_BOOKING, Integer.toString(length));
    //
    //                            AnalyticsManager.getInstance(getActivity()).recordScreen(getActivity(), Screen.BOOKING_LIST, null, analyticsParams);
    //                        }
    //
    //                        try
    //                        {
    //                            if (mDailyDeepLink != null && mDailyDeepLink.isValidateLink() == true)
    //                            {
    //                                if (mDailyDeepLink.isInternalDeepLink() == true)
    //                                {
    //                                    DailyInternalDeepLink internalDeepLink = (DailyInternalDeepLink) mDailyDeepLink;
    //
    //                                    if (internalDeepLink.isBookingDetailView() == true && length != 0)
    //                                    {
    //                                        PlaceType placeType = null;
    //
    //                                        if ("stay".equalsIgnoreCase(internalDeepLink.getPlaceType()) == true)
    //                                        {
    //                                            placeType = PlaceType.HOTEL;
    //                                        } else if ("gourmet".equalsIgnoreCase(internalDeepLink.getPlaceType()) == true)
    //                                        {
    //                                            placeType = PlaceType.FNB;
    //                                        }
    //
    //                                        if (internalDeepLink.getReservatoinId() >= 0)
    //                                        {
    //                                            int reservationId = internalDeepLink.getReservatoinId();
    //                                            int size = bookingArrayList.size();
    //
    //                                            for (int i = 0; i < size; i++)
    //                                            {
    //                                                Booking booking = bookingArrayList.get(i);
    //
    //                                                if (booking.reservationIndex == reservationId)
    //                                                {
    //                                                    unLockUI();
    //                                                    mListView.performItemClick(null, i, 0);
    //                                                    break;
    //                                                }
    //                                            }
    //                                        } else
    //                                        {
    //                                            PlacePaymentInformation.PaymentType paymentType = PlacePaymentInformation.PaymentType.valueOf(internalDeepLink.getPaymentType());
    //                                            String placeName = internalDeepLink.getPlaceName();
    //
    //                                            int index = -1;
    //
    //                                            switch (placeType)
    //                                            {
    //                                                case HOTEL:
    //                                                    String checkInTime = internalDeepLink.getCheckInTime();
    //                                                    String checkOutTime = internalDeepLink.getCheckOutTime();
    //
    //                                                    index = searchStayFromPaymentInformation(baseActivity//
    //                                                        , placeName, paymentType, checkInTime, checkOutTime, bookingArrayList);
    //                                                    break;
    //
    //                                                case FNB:
    //                                                    String visitTime = internalDeepLink.getVisitTime();
    //
    //                                                    index = searchGourmetFromPaymentInformation(baseActivity//
    //                                                        , placeName, paymentType, visitTime, bookingArrayList);
    //                                                    break;
    //                                            }
    //
    //                                            if (index >= 0)
    //                                            {
    //                                                unLockUI();
    //                                                mListView.performItemClick(null, index, 0);
    //                                            }
    //                                        }
    //                                    }
    //                                } else
    //                                {
    //                                    DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) mDailyDeepLink;
    //
    //                                    if (externalDeepLink.isBookingDetailView() == true && length != 0)
    //                                    {
    //                                        PlaceType placeType = null;
    //
    //                                        if ("stay".equalsIgnoreCase(externalDeepLink.getPlaceType()) == true)
    //                                        {
    //                                            placeType = PlaceType.HOTEL;
    //                                        } else if ("gourmet".equalsIgnoreCase(externalDeepLink.getPlaceType()) == true)
    //                                        {
    //                                            placeType = PlaceType.FNB;
    //                                        }
    //
    //                                        final int reservationIndex = externalDeepLink.getReservationIndex();
    //
    //                                        if (placeType != null)
    //                                        {
    //                                            String imageUrl = null;
    //
    //                                            for (Booking booking : bookingArrayList)
    //                                            {
    //                                                if (booking.reservationIndex == reservationIndex)
    //                                                {
    //                                                    imageUrl = booking.hotelImageUrl;
    //                                                    break;
    //                                                }
    //                                            }
    //
    //                                            startBookingDetail(baseActivity, placeType, reservationIndex, imageUrl, true);
    //                                        }
    //                                    }
    //                                }
    //                            }
    //                        } catch (Exception e)
    //                        {
    //                            ExLog.e(e.toString());
    //                        } finally
    //                        {
    //                            if (mDailyDeepLink != null)
    //                            {
    //                                mDailyDeepLink.clear();
    //                            }
    //                        }
    //
    //                        // 사용자 정보 요청.
    //                        DailyMobileAPI.getInstance(baseActivity).requestUserProfile(mNetworkTag, mUserProfileCallback);
    //                    } else
    //                    {
    //                        String msg = responseJSONObject.getString("msg");
    //
    //                        if (DailyTextUtils.isTextEmpty(msg) == false)
    //                        {
    //                            DailyToast.showToast(baseActivity, msg, Toast.LENGTH_SHORT);
    //                        } else
    //                        {
    //                            DailyToast.showToast(baseActivity, R.string.act_base_network_connect, Toast.LENGTH_SHORT);
    //                        }
    //
    //                        updateLayout(true, null);
    //                    }
    //                } catch (Exception e)
    //                {
    //                    updateLayout(true, null);
    //
    //                    onError(e);
    //                } finally
    //                {
    //                    unLockUI();
    //                }
    //            } else
    //            {
    //                baseActivity.onErrorResponse(call, response);
    //            }
    //        }
    //
    //        @Override
    //        public void onFailure(Call<JSONObject> call, Throwable t)
    //        {
    //            BaseActivity baseActivity = (BaseActivity) getActivity();
    //
    //            if (baseActivity == null)
    //            {
    //                return;
    //            }
    //
    //            baseActivity.onError(t);
    //        }
    //
    //        private ArrayList<Booking> makeBookingList(JSONArray jsonArray) throws Exception
    //        {
    //            //noinspection PrivateMemberAccessBetweenOuterAndInnerClass
    //            if (jsonArray == null || jsonArray.length() == 0)
    //            {
    //                return null;
    //            }
    //
    //            int length = jsonArray.length();
    //
    //            // 무료취소대기, 입금대기, 결제완료, 이용완료
    //            ArrayList<Booking> waitRefundBookingList = new ArrayList<>();
    //            ArrayList<Booking> waitBookingList = new ArrayList<>();
    //            ArrayList<Booking> paymentBookingList = new ArrayList<>();
    //            ArrayList<Booking> usedBookingList = new ArrayList<>();
    //
    //            long currentTime = 0;
    //
    //            for (int i = 0; i < length; i++)
    //            {
    //                JSONObject jsonObject = jsonArray.getJSONObject(i);
    //
    //                Booking booking = new Booking(jsonObject);
    //
    //                booking.leftFromToDay = (int) ((DailyCalendar.clearTField(booking.checkinTime) - DailyCalendar.clearTField(currentTime)) / DailyCalendar.DAY_MILLISECOND);
    //
    //                if (booking.readyForRefund == true)
    //                {
    //                    waitRefundBookingList.add(booking);
    //                } else
    //                {
    //                    switch (booking.payType)
    //                    {
    //                        case CODE_PAY_TYPE_CARD_COMPLETE:
    //                        case CODE_PAY_TYPE_ACCOUNT_COMPLETE:
    //                            booking.isUsed = booking.checkoutTime < currentTime;
    //
    //                            if (booking.isUsed)
    //                            {
    //                                usedBookingList.add(booking);
    //                            } else
    //                            {
    //                                paymentBookingList.add(booking);
    //                            }
    //                            break;
    //
    //                        case CODE_PAY_TYPE_ACCOUNT_WAIT:
    //                            waitBookingList.add(booking);
    //                            break;
    //                    }
    //                }
    //            }
    //
    //            ArrayList<Booking> bookingArrayList = new ArrayList<>(length + 3);
    //
    //            // 무료취소대기가 있는 경우
    //            if (waitRefundBookingList.size() > 0)
    //            {
    //                Booking sectionWaitRefund = new Booking(getString(R.string.frag_booking_wait_refund));
    //                bookingArrayList.add(sectionWaitRefund);
    //                bookingArrayList.addAll(waitRefundBookingList);
    //            }
    //
    //            // 입금 대기가 있는 경우.
    //            if (waitBookingList.size() > 0)
    //            {
    //                Booking sectionWait = new Booking(getString(R.string.frag_booking_wait_account));
    //                bookingArrayList.add(sectionWait);
    //                bookingArrayList.addAll(waitBookingList);
    //            }
    //
    //            // 결제 완료가 있는 경우.
    //            if (paymentBookingList.size() > 0)
    //            {
    //                Booking sectionPay = new Booking(getString(R.string.frag_booking_complete_payment));
    //                bookingArrayList.add(sectionPay);
    //                bookingArrayList.addAll(paymentBookingList);
    //            }
    //
    //            // 이용 완료가 있는 경우.
    //            if (usedBookingList.size() > 0)
    //            {
    //                Booking sectionUsed = new Booking(getString(R.string.frag_booking_use));
    //                bookingArrayList.add(sectionUsed);
    //                bookingArrayList.addAll(usedBookingList);
    //            }
    //
    //            return bookingArrayList;
    //        }
    //    };

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
                                //                                DailyMobileAPI.getInstance(baseActivity).requestBookingList(mNetworkTag, mReservationListCallback);
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
                                //                                DailyMobileAPI.getInstance(baseActivity).requestBookingList(mNetworkTag, mReservationListCallback);
                            }
                        };
                    }

                    switch (msgCode)
                    {
                        case 0:
                        {
                            message = responseJSONObject.getString("msg");
                            DailyToast.showToast(baseActivity, message, Toast.LENGTH_SHORT);

                            //                            DailyMobileAPI.getInstance(baseActivity).requestBookingList(mNetworkTag, mReservationListCallback);
                            break;
                        }

                        // Toast
                        case 100:
                        {
                            message = responseJSONObject.getString("msg");

                            if (DailyTextUtils.isTextEmpty(message) == false)
                            {
                                DailyToast.showToast(baseActivity, message, Toast.LENGTH_SHORT);
                            }

                            //                            DailyMobileAPI.getInstance(baseActivity).requestBookingList(mNetworkTag, mReservationListCallback);
                            break;
                        }

                        // Popup
                        case 200:
                        {
                            message = responseJSONObject.getString("msg");

                            if (DailyTextUtils.isTextEmpty(message) == false)
                            {
                                unLockUI();

                                if (baseActivity.isFinishing() == true)
                                {
                                    return;
                                }

                                baseActivity.showSimpleDialog(getString(R.string.dialog_notice2), message, getString(R.string.dialog_btn_text_confirm), onClickListener);
                            } else
                            {
                                //                                DailyMobileAPI.getInstance(baseActivity).requestBookingList(mNetworkTag, mReservationListCallback);
                            }
                            break;
                        }
                    }
                } catch (Exception e)
                {
                    onError(e);

                    //                    DailyMobileAPI.getInstance(baseActivity).requestBookingList(mNetworkTag, mReservationListCallback);
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


    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // 기존의 BaseActivity에 있는 정보 가져오기
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void addCompositeDisposable(Disposable disposable)
    {
        if (disposable == null)
        {
            return;
        }

        mCompositeDisposable.add(disposable);
    }

    private void clearCompositeDisposable()
    {
        mCompositeDisposable.clear();
    }

    protected void onHandleError(Throwable throwable)
    {
        unLockUI();

        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null || baseActivity.isFinishing() == true)
        {
            return;
        }

        if (throwable instanceof BaseException)
        {
            // 팝업 에러 보여주기
            BaseException baseException = (BaseException) throwable;

            baseActivity.showSimpleDialog(null, baseException.getMessage()//
                , getString(R.string.dialog_btn_text_confirm), null, null, null, null, dialogInterface -> getActivity().onBackPressed(), true);
        } else if (throwable instanceof HttpException)
        {
            retrofit2.HttpException httpException = (HttpException) throwable;

            if (httpException.code() == BaseException.CODE_UNAUTHORIZED)
            {
                addCompositeDisposable(new ConfigLocalImpl(getActivity()).clear().subscribe(object ->
                {
                    new FacebookRemoteImpl().logOut();
                    new KakaoRemoteImpl().logOut();

                    baseActivity.restartExpiredSession();
                }));
            } else
            {
                DailyToast.showToast(getActivity(), getString(R.string.act_base_network_connect), DailyToast.LENGTH_LONG);

                if (Constants.DEBUG == false)
                {
                    Crashlytics.log(httpException.response().raw().request().url().toString());
                    Crashlytics.logException(throwable);
                } else
                {
                    ExLog.e(httpException.response().raw().request().url().toString() + ", " + httpException.toString());
                }
            }
        } else
        {
            DailyToast.showToast(getActivity(), getString(R.string.act_base_network_connect), DailyToast.LENGTH_LONG);
        }
    }
}
