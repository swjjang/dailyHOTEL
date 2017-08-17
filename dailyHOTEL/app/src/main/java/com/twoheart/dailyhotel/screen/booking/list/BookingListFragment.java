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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.entity.Booking;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.ListItem;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.repository.remote.BookingRemoteImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.daily.dailyhotel.screen.booking.detail.stay.outbound.StayOutboundBookingDetailActivity;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
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
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.PinnedSectionRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import retrofit2.Call;
import retrofit2.Response;

/**
 * 예약한 호텔의 리스트들을 출력.
 *
 * @author jangjunho
 */
public class BookingListFragment extends BaseMenuNavigationFragment implements View.OnClickListener
{
    private BookingListAdapter mAdapter;
    private RelativeLayout mEmptyLayout;
    private PinnedSectionRecyclerView mRecyclerView;
    private View mLoginView;
    boolean mDontReload;

    private CommonDateTime mCommonDateTime;
    private DailyDeepLink mDailyDeepLink;

    boolean mCheckVerify; // 인증이 해지되었는지 예약 리스트 진입시 한번만 체크한다.

    private CommonRemoteImpl mCommonRemoteImpl;
    private BookingRemoteImpl mBookingRemoteImpl;
    private ProfileRemoteImpl mProfileRemoteImpl;

    public interface OnUserActionListener
    {
        void onDeleteClick(Booking booking);

        void onBookingClick(Booking booking);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mCommonRemoteImpl = new CommonRemoteImpl(getContext());
        mBookingRemoteImpl = new BookingRemoteImpl(getContext());
        mProfileRemoteImpl = new ProfileRemoteImpl(getContext());
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
        DailyToolbarView dailyToolbarView = (DailyToolbarView) view.findViewById(R.id.toolbarView);
        dailyToolbarView.setTitleText(R.string.actionbar_title_booking_list_frag);
        dailyToolbarView.setBackVisible(false);
    }

    private void initLayout(View view)
    {
        mRecyclerView = (PinnedSectionRecyclerView) view.findViewById(R.id.bookingRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        EdgeEffectColor.setEdgeGlowColor(mRecyclerView, getContext().getResources().getColor(R.color.default_over_scroll_edge));
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
            int mOldY;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (mOnScreenScrollChangeListener != null && recyclerView.getChildCount() > 0)
                {
                    mOnScreenScrollChangeListener.onScrollChange(recyclerView, 0, dy, 0, mOldY);

                    mOldY = dy;
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    mOldY = 0;
                }
            }
        });

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
            mRecyclerView.setVisibility(View.GONE);
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
                mRecyclerView.setVisibility(View.GONE);
                mEmptyLayout.setVisibility(View.VISIBLE);
                mLoginView.setVisibility(View.INVISIBLE);
            } else
            {
                if (mAdapter == null)
                {
                    mAdapter = new BookingListAdapter(baseActivity, new ArrayList<>());
                    mAdapter.setOnUserActionListener(mOnUserActionListener);
                    mRecyclerView.setAdapter(mAdapter);
                } else
                {
                    mAdapter.clear();
                }

                mAdapter.addAll(listItemList);
                mAdapter.notifyDataSetChanged();

                mRecyclerView.setVisibility(View.VISIBLE);
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
                onRefresh();
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

                mDontReload = true;
                break;
            }

            case CODE_REQUEST_ACTIVITY_BOOKING_DETAIL:
            {
                if (resultCode == CODE_RESULT_ACTIVITY_REFRESH)
                {
                    mDontReload = false;
                } else
                {
                    mDontReload = true;
                }
                break;
            }
        }
    }

    private void onRefresh()
    {
        lockUIImmediately();

        addCompositeDisposable(Observable.zip(mCommonRemoteImpl.getCommonDateTime()//
            , mBookingRemoteImpl.getBookingList(), mBookingRemoteImpl.getStayOutboundBookingList()//
            , new Function3<CommonDateTime, List<Booking>, List<Booking>, List<ListItem>>()
            {
                @Override
                public List<ListItem> apply(@NonNull CommonDateTime commonDateTime//
                    , @NonNull List<Booking> bookingList, @NonNull List<Booking> stayOutboundBookingList) throws Exception
                {
                    setCommonDateTime(commonDateTime);

                    bookingList.addAll(stayOutboundBookingList);

                    List<ListItem> listItemList = null;

                    if (bookingList.size() == 0)
                    {
                        listItemList = new ArrayList<>();
                    } else
                    {
                        listItemList = getBookingSortList(bookingList);

                        if (listItemList == null)
                        {
                            listItemList = new ArrayList<>();
                        }
                    }

                    return listItemList;
                }
            }).subscribe(new Consumer<List<ListItem>>()
        {
            @Override
            public void accept(@NonNull List<ListItem> listItemList) throws Exception
            {
                onBookingList(listItemList);

                unLockUI();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception
            {
                onHandleError(throwable);

                updateLayout(true, null);
            }
        }));
    }

    private void onBookingList(List<ListItem> listItemList)
    {
        if (listItemList == null || listItemList.size() == 0)
        {
            updateLayout(true, null);

            AnalyticsManager.getInstance(getActivity()).recordScreen(getActivity(), Screen.BOOKING_LIST_EMPTY, null);
        } else
        {
            updateLayout(true, listItemList);

            Map<String, String> analyticsParams = new HashMap<>();
            analyticsParams.put(AnalyticsManager.KeyType.NUM_OF_BOOKING, Integer.toString(listItemList.size()));

            AnalyticsManager.getInstance(getActivity()).recordScreen(getActivity(), Screen.BOOKING_LIST, null, analyticsParams);
        }

        if (mDailyDeepLink != null && mDailyDeepLink.isValidateLink() == true)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            onDeepLink(baseActivity, listItemList);
        } else
        {
            if (mCheckVerify == true)
            {
                return;
            }

            mCheckVerify = true;

            addCompositeDisposable(mProfileRemoteImpl.getProfile().subscribe(new Consumer<User>()
            {
                @Override
                public void accept(@NonNull User user) throws Exception
                {
                    BaseActivity baseActivity = (BaseActivity) getActivity();

                    // 인증 후 인증이 해지된 경우
                    if (user.verified == true && user.phoneVerified == false && DailyPreference.getInstance(baseActivity).isVerification() == true)
                    {
                        baseActivity.showSimpleDialog(null, getString(R.string.message_invalid_verification), getString(R.string.dialog_btn_text_confirm), null);

                        DailyPreference.getInstance(baseActivity).setVerification(false);
                    }
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception
                {
                    // 실패시에 아무것도 하지 않음.
                }
            }));
        }
    }

    private int searchStayFromPaymentInformation(Context context, String placeName//
        , PlacePaymentInformation.PaymentType paymentType, String checkInTime, String checkOutTime//
        , List<ListItem> bookingList)
    {
        if (DailyTextUtils.isTextEmpty(placeName, checkInTime, checkOutTime) == true//
            || bookingList == null || bookingList.size() == 0)
        {
            DailyPreference.getInstance(context).clearPaymentInformation();
            DailyPreference.getInstance(context).setVirtualAccountReadyFlag(-1);
            return -1;
        }

        int size = bookingList.size();

        try
        {
            // "yyyy-MM-dd'T'HH:mm:ssZZZZZ"
            String checkInDate = DailyCalendar.convertDateFormatString(checkInTime, DailyCalendar.ISO_8601_FORMAT, "yyyy-MM-dd");
            String checkOutDate = DailyCalendar.convertDateFormatString(checkOutTime, DailyCalendar.ISO_8601_FORMAT, "yyyy-MM-dd");
            ListItem listItem;
            Booking booking;

            for (int i = 0; i < size; i++)
            {
                listItem = bookingList.get(i);

                if (listItem.mType != ListItem.TYPE_ENTRY)
                {
                    continue;
                }

                booking = listItem.getItem();

                if (PlacePaymentInformation.PaymentType.VBANK == paymentType)
                {
                    if (booking.statePayment == Booking.PAYMENT_WAITING//
                        && booking.placeName.equalsIgnoreCase(placeName)//
                        && booking.placeType == Booking.PlaceType.STAY//
                        && booking.checkInDateTime.equalsIgnoreCase(checkInDate) == true//
                        && booking.checkOutDateTime.equalsIgnoreCase(checkOutDate) == true)
                    {
                        return i;
                    }
                } else
                {
                    if (booking.statePayment != Booking.PAYMENT_WAITING //
                        && booking.readyForRefund == false//
                        && booking.placeName.equalsIgnoreCase(placeName)//
                        && booking.placeType == Booking.PlaceType.STAY//
                        && booking.checkInDateTime.equalsIgnoreCase(checkInDate) == true//
                        && booking.checkOutDateTime.equalsIgnoreCase(checkOutDate) == true)
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
        , PlacePaymentInformation.PaymentType paymentType, String visitTime, List<ListItem> bookingList)
    {
        if (DailyTextUtils.isTextEmpty(placeName, visitTime) == true//
            || bookingList == null || bookingList.size() == 0)
        {
            DailyPreference.getInstance(context).clearPaymentInformation();
            DailyPreference.getInstance(context).setVirtualAccountReadyFlag(-1);
            return -1;
        }

        int size = bookingList.size();

        try
        {
            String visitDate = DailyCalendar.convertDateFormatString(visitTime, DailyCalendar.ISO_8601_FORMAT, "yyyy-MM-dd");
            ListItem listItem;
            Booking booking;

            for (int i = 0; i < size; i++)
            {
                listItem = bookingList.get(i);

                if (listItem.mType != ListItem.TYPE_ENTRY)
                {
                    continue;
                }

                booking = listItem.getItem();

                if (PlacePaymentInformation.PaymentType.VBANK == paymentType)
                {
                    if (booking.statePayment == Booking.PAYMENT_WAITING//
                        && booking.placeName.equalsIgnoreCase(placeName)//
                        && booking.placeType == Booking.PlaceType.GOURMET//
                        && booking.checkInDateTime.equalsIgnoreCase(visitDate) == true)
                    {
                        return i;
                    }
                } else
                {
                    if (booking.statePayment != Booking.PAYMENT_WAITING//
                        && booking.readyForRefund == false//
                        && booking.placeName.equalsIgnoreCase(placeName)//
                        && booking.placeType == Booking.PlaceType.GOURMET//
                        && booking.checkInDateTime.equalsIgnoreCase(visitDate) == true)
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

    boolean startBookingDetail(BaseActivity baseActivity, Booking.PlaceType placeType,//
                               int reservationIndex, String imageUrl, boolean isDeepLink, int bookingState)
    {
        Intent intent;

        switch (placeType)
        {
            case STAY:
                intent = StayReservationDetailActivity.newInstance(baseActivity, reservationIndex, imageUrl, isDeepLink, bookingState);
                break;

            case GOURMET:
                intent = GourmetReservationDetailActivity.newInstance(baseActivity, reservationIndex, imageUrl, isDeepLink, bookingState);
                break;

            case STAY_OUTBOUND:
                intent = StayOutboundBookingDetailActivity.newInstance(baseActivity, reservationIndex, imageUrl, bookingState);
                break;

            default:
                return false;
        }

        baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_BOOKING_DETAIL);

        switch (bookingState)
        {
            case Booking.BOOKING_STATE_WAITING_REFUND:
                AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                    , AnalyticsManager.Action.BOOKING_DETAIL_CLICK, AnalyticsManager.Label.CANCELLEATION_PROGRESS, null);
                break;

            case Booking.BOOKING_STATE_BEFORE_USE:
                AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                    , AnalyticsManager.Action.BOOKING_DETAIL_CLICK, AnalyticsManager.Label.COMPLETE_PAYMENT, null);
                break;

            case Booking.BOOKING_STATE_AFTER_USE:
                AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                    , AnalyticsManager.Action.BOOKING_DETAIL_CLICK, AnalyticsManager.Label.POST_VISIT, null);
                break;
        }

        return true;
    }

    @Override
    public void setOnScrollChangedListener(BaseMenuNavigationFragment.OnScreenScrollChangeListener listener)
    {
        mOnScreenScrollChangeListener = listener;
    }

    @Override
    public void scrollTop()
    {
        if (mRecyclerView != null)
        {
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    private List<ListItem> getBookingSortList(List<Booking> bookingList) throws Exception
    {
        if (bookingList == null || bookingList.size() == 0)
        {
            return null;
        }

        // 무료취소대기, 입금대기, 결제완료, 이용완료
        List<Booking> waitRefundList = new ArrayList<>();
        List<Booking> depositWaitingList = new ArrayList<>();
        List<Booking> beforeUseList = new ArrayList<>();
        List<Booking> afterUseList = new ArrayList<>();

        for (Booking booking : bookingList)
        {
            booking.remainingDays = DailyCalendar.compareDateDay(DailyCalendar.convertDateFormatString(booking.checkInDateTime, "yyyy-MM-dd", DailyCalendar.ISO_8601_FORMAT)//
                , mCommonDateTime.currentDateTime);

            if (booking.readyForRefund == true)
            {
                waitRefundList.add(booking);
            } else
            {
                switch (booking.statePayment)
                {
                    case Booking.PAYMENT_COMPLETED:
                        booking.isUsed = DailyCalendar.compareDateDay(DailyCalendar.convertDateFormatString(booking.checkOutDateTime, "yyyy-MM-dd", DailyCalendar.ISO_8601_FORMAT)//
                            , mCommonDateTime.currentDateTime) < 0;

                        if (booking.isUsed)
                        {
                            afterUseList.add(booking);
                        } else
                        {
                            beforeUseList.add(booking);
                        }
                        break;

                    case Booking.PAYMENT_WAITING:
                        depositWaitingList.add(booking);
                        break;
                }
            }
        }

        Comparator<Booking> ascComparator = new Comparator<Booking>()
        {
            public int compare(Booking booking1, Booking booking2)
            {
                int compareDay;

                try
                {
                    compareDay = DailyCalendar.compareDateDay(DailyCalendar.convertDateFormatString(booking1.checkInDateTime, "yyyy-MM-dd", DailyCalendar.ISO_8601_FORMAT)//
                        , DailyCalendar.convertDateFormatString(booking2.checkInDateTime, "yyyy-MM-dd", DailyCalendar.ISO_8601_FORMAT));
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
                    if (booking1.index < booking2.index)
                    {
                        return -1;
                    } else
                    {
                        return 1;
                    }
                }
            }
        };

        Comparator<Booking> descComparator = new Comparator<Booking>()
        {
            public int compare(Booking booking1, Booking booking2)
            {
                int compareDay;

                try
                {
                    compareDay = DailyCalendar.compareDateDay(DailyCalendar.convertDateFormatString(booking1.checkInDateTime, "yyyy-MM-dd", DailyCalendar.ISO_8601_FORMAT)//
                        , DailyCalendar.convertDateFormatString(booking2.checkInDateTime, "yyyy-MM-dd", DailyCalendar.ISO_8601_FORMAT));
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
                    if (booking1.index < booking2.index)
                    {
                        return 1;
                    } else
                    {
                        return -1;
                    }
                }
            }
        };

        List<ListItem> listItemList = new ArrayList<>(bookingList.size() + 5);

        // 무료취소대기가 있는 경우
        if (waitRefundList.size() > 0)
        {
            Collections.sort(waitRefundList, ascComparator);

            ListItem sectionListItem = new ListItem(ListItem.TYPE_SECTION, getString(R.string.frag_booking_wait_refund));
            listItemList.add(sectionListItem);

            for (Booking booking : waitRefundList)
            {
                booking.bookingState = Booking.BOOKING_STATE_WAITING_REFUND;
                listItemList.add(new ListItem(ListItem.TYPE_ENTRY, booking));
            }

            AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                , AnalyticsManager.Action.BOOKING_STATUS_CHECK, AnalyticsManager.Label.CANCELLEATION_PROGRESS, null);
        }

        // 입금 대기가 있는 경우.
        if (depositWaitingList.size() > 0)
        {
            Collections.sort(depositWaitingList, ascComparator);

            ListItem sectionListItem = new ListItem(ListItem.TYPE_SECTION, getString(R.string.frag_booking_wait_account));
            listItemList.add(sectionListItem);

            for (Booking booking : depositWaitingList)
            {
                booking.bookingState = Booking.BOOKING_STATE_NONE;
                listItemList.add(new ListItem(ListItem.TYPE_ENTRY, booking));
            }

            AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                , AnalyticsManager.Action.BOOKING_STATUS_CHECK, AnalyticsManager.Label.TEMPORARY_ACCOUNT, null);
        }

        // 결제 완료가 있는 경우.
        if (beforeUseList.size() > 0)
        {
            Collections.sort(beforeUseList, ascComparator);

            ListItem sectionListItem = new ListItem(ListItem.TYPE_SECTION, getString(R.string.frag_booking_complete_payment));
            listItemList.add(sectionListItem);

            for (Booking booking : beforeUseList)
            {
                booking.bookingState = Booking.BOOKING_STATE_BEFORE_USE;
                listItemList.add(new ListItem(ListItem.TYPE_ENTRY, booking));
            }

            AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                , AnalyticsManager.Action.BOOKING_STATUS_CHECK, AnalyticsManager.Label.COMPLETE_PAYMENT, null);
        }

        // 이용 완료가 있는 경우.
        if (afterUseList.size() > 0)
        {
            Collections.sort(afterUseList, descComparator);

            ListItem sectionListItem = new ListItem(ListItem.TYPE_SECTION, getString(R.string.frag_booking_use));
            listItemList.add(sectionListItem);

            for (Booking booking : afterUseList)
            {
                booking.bookingState = Booking.BOOKING_STATE_AFTER_USE;
                listItemList.add(new ListItem(ListItem.TYPE_ENTRY, booking));
            }

            AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                , AnalyticsManager.Action.BOOKING_STATUS_CHECK, AnalyticsManager.Label.POST_VISIT, null);
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
        public void onDeleteClick(final Booking booking)
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
                        case STAY:
                            DailyMobileAPI.getInstance(baseActivity).requestStayHiddenBooking(mNetworkTag, booking.index, mReservationHiddenCallback);
                            break;

                        case GOURMET:
                            DailyMobileAPI.getInstance(baseActivity).requestGourmetHiddenBooking(mNetworkTag, booking.index, mReservationHiddenCallback);
                            break;

                        case STAY_OUTBOUND:
                            addCompositeDisposable(mBookingRemoteImpl.getStayOutboundHideBooking(booking.index).subscribe(new Consumer<Boolean>()
                            {
                                @Override
                                public void accept(@NonNull Boolean result) throws Exception
                                {
                                    unLockUI();

                                    if (result == true)
                                    {
                                        baseActivity.showSimpleDialog(getString(R.string.dialog_notice2)//
                                            , getString(R.string.message_booking_delete_booking)//
                                            , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                                            {
                                                @Override
                                                public void onDismiss(DialogInterface dialog)
                                                {
                                                    onRefresh();
                                                }
                                            });
                                    } else
                                    {
                                        baseActivity.showSimpleDialog(getString(R.string.dialog_notice2)//
                                            , getString(R.string.message_booking_failed_delete_booking)//
                                            , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                                            {
                                                @Override
                                                public void onDismiss(DialogInterface dialog)
                                                {
                                                    onRefresh();
                                                }
                                            });
                                    }
                                }
                            }, new Consumer<Throwable>()
                            {
                                @Override
                                public void accept(@NonNull Throwable throwable) throws Exception
                                {
                                    unLockUI();

                                    baseActivity.showSimpleDialog(getString(R.string.dialog_notice2)//
                                        , getString(R.string.message_booking_failed_delete_booking)//
                                        , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                                        {
                                            @Override
                                            public void onDismiss(DialogInterface dialog)
                                            {
                                                onRefresh();
                                            }
                                        });
                                }
                            }));
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

        @Override
        public void onBookingClick(Booking booking)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null || booking == null)
            {
                return;
            }

            if (isLockUiComponent() == true)
            {
                return;
            }

            lockUiComponent();

            Intent intent;

            switch (booking.statePayment)
            {
                case Booking.PAYMENT_COMPLETED:
                    if (startBookingDetail(baseActivity, booking.placeType, booking.index, booking.imageUrl, false, booking.bookingState) == false)
                    {
                        releaseUiComponent();
                    }
                    break;

                case Booking.PAYMENT_WAITING:
                    // 가상계좌 입금대기
                    intent = PaymentWaitActivity.newInstance(baseActivity, booking);
                    baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_VIRTUAL_BOOKING_DETAIL);

                    AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                        , AnalyticsManager.Action.BOOKING_DETAIL_CLICK, AnalyticsManager.Label.TEMPORARY_ACCOUNT, null);
                    break;

                default:
                    releaseUiComponent();
                    break;
            }
        }
    };

    private boolean onDeepLink(BaseActivity baseActivity, List<ListItem> bookingList)
    {
        if (baseActivity == null || bookingList == null || bookingList.size() == 0)
        {
            return false;
        }

        if (mDailyDeepLink == null || mDailyDeepLink.isValidateLink() == false)
        {
            return false;
        }

        try
        {
            if (mDailyDeepLink.isInternalDeepLink() == true)
            {
                DailyInternalDeepLink internalDeepLink = (DailyInternalDeepLink) mDailyDeepLink;

                if (internalDeepLink.isBookingDetailView() == true)
                {
                    Booking.PlaceType placeType = null;

                    switch (internalDeepLink.getPlaceType())
                    {
                        case DailyDeepLink.GOURMET:
                            placeType = Booking.PlaceType.GOURMET;
                            break;

                        case DailyDeepLink.STAY:
                            placeType = Booking.PlaceType.STAY;
                            break;

                        case DailyDeepLink.STAY_OUTBOUND:
                            placeType = Booking.PlaceType.STAY_OUTBOUND;
                            break;

                        default:
                            throw new NullPointerException("Booking.PlaceType placeType = null");
                    }

                    int bookingIndex = internalDeepLink.getBookingIndex();
                    int size = bookingList.size();
                    ListItem listItem;
                    Booking booking;

                    for (int i = 0; i < size; i++)
                    {
                        listItem = bookingList.get(i);

                        if (listItem.mType != ListItem.TYPE_ENTRY)
                        {
                            continue;
                        }

                        booking = bookingList.get(i).getItem();

                        if (booking.index == bookingIndex && placeType == booking.placeType)
                        {
                            unLockUI();
                            mOnUserActionListener.onBookingClick(booking);
                            break;
                        }
                    }
                }
            } else
            {
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) mDailyDeepLink;

                if (externalDeepLink.isBookingDetailView() == true)
                {
                    Booking.PlaceType placeType = null;

                    switch (externalDeepLink.getPlaceType())
                    {
                        case DailyDeepLink.GOURMET:
                            placeType = Booking.PlaceType.GOURMET;
                            break;

                        case DailyDeepLink.STAY:
                            placeType = Booking.PlaceType.STAY;
                            break;

                        case DailyDeepLink.STAY_OUTBOUND:
                            placeType = Booking.PlaceType.STAY_OUTBOUND;
                            break;

                        default:
                            throw new NullPointerException("Booking.PlaceType placeType = null");
                    }

                    final int reservationIndex = externalDeepLink.getReservationIndex();

                    if (placeType != null)
                    {
                        String imageUrl = null;
                        Booking booking;

                        for (ListItem listItem : bookingList)
                        {
                            if (listItem.mType != ListItem.TYPE_ENTRY)
                            {
                                continue;
                            }

                            booking = listItem.getItem();

                            if (booking.index == reservationIndex)
                            {
                                imageUrl = booking.imageUrl;
                                break;
                            }
                        }

                        startBookingDetail(baseActivity, placeType, reservationIndex, imageUrl, true, Booking.BOOKING_STATE_NONE);
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

                                onRefresh();
                            }
                        };

                        AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                            , AnalyticsManager.Action.BOOKING_HISTORY_DELETE, AnalyticsManager.ValueType.EMPTY, null);
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

                                onRefresh();
                            }
                        };
                    }

                    switch (msgCode)
                    {
                        case 0:
                        {
                            message = responseJSONObject.getString("msg");
                            DailyToast.showToast(baseActivity, message, Toast.LENGTH_SHORT);

                            onRefresh();
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

                            onRefresh();
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
                                onRefresh();
                            }
                            break;
                        }
                    }
                } catch (Exception e)
                {
                    onError(e);

                    onRefresh();
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
}
