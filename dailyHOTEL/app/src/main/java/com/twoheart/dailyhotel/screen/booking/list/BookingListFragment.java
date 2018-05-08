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

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.Booking;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.Review;
import com.daily.dailyhotel.entity.ReviewAnswerValue;
import com.daily.dailyhotel.entity.ReviewItem;
import com.daily.dailyhotel.entity.ReviewQuestionItem;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.parcel.analytics.GourmetDetailAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam;
import com.daily.dailyhotel.repository.remote.BookingRemoteImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.daily.dailyhotel.repository.remote.ReviewRemoteImpl;
import com.daily.dailyhotel.screen.booking.cancel.BookingCancelListActivity;
import com.daily.dailyhotel.screen.booking.detail.gourmet.GourmetBookingDetailActivity;
import com.daily.dailyhotel.screen.booking.detail.stay.StayBookingDetailActivity;
import com.daily.dailyhotel.screen.booking.detail.stay.outbound.StayOutboundBookingDetailActivity;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.detail.StayOutboundDetailActivity;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.FragmentBookingListDataBinding;
import com.twoheart.dailyhotel.model.ReviewPickQuestion;
import com.twoheart.dailyhotel.model.ReviewScoreQuestion;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseMenuNavigationFragment;
import com.twoheart.dailyhotel.screen.main.MainFragmentManager;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.screen.review.ReviewActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyExternalDeepLink;
import com.twoheart.dailyhotel.util.DailyInternalDeepLink;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

/**
 * 예약한 호텔의 리스트들을 출력.
 *
 * @author jangjunho
 */
public class BookingListFragment extends BaseMenuNavigationFragment implements View.OnClickListener
{
    static final int REQUEST_CODE_BOOKING_CANCEL = 10000;

    private BookingListAdapter mAdapter;
    FragmentBookingListDataBinding mViewDataBinding;
    boolean mDontReload;

    CommonDateTime mCommonDateTime;
    private DailyDeepLink mDailyDeepLink;

    boolean mCheckVerify; // 인증이 해지되었는지 예약 리스트 진입시 한번만 체크한다.

    CommonRemoteImpl mCommonRemoteImpl;
    ReviewRemoteImpl mReviewRemoteImpl;
    private BookingRemoteImpl mBookingRemoteImpl;
    private ProfileRemoteImpl mProfileRemoteImpl;

    public interface OnUserActionListener
    {
        void onAgainBookingClick(Booking booking);

        void onBookingClick(Booking booking);

        void onReviewClick(Booking booking);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mCommonRemoteImpl = new CommonRemoteImpl();
        mReviewRemoteImpl = new ReviewRemoteImpl();
        mBookingRemoteImpl = new BookingRemoteImpl();
        mProfileRemoteImpl = new ProfileRemoteImpl();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_booking_list_data, container, false);

        initToolbar(baseActivity, mViewDataBinding);
        initLayout(mViewDataBinding);

        return mViewDataBinding.getRoot();
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

    private void initToolbar(BaseActivity baseActivity, FragmentBookingListDataBinding dataBinding)
    {
        if (dataBinding == null)
        {
            return;
        }

        dataBinding.toolbarView.setTitleText(R.string.actionbar_title_booking_list_frag);
        dataBinding.toolbarView.setBackVisible(false);
    }

    private void initLayout(FragmentBookingListDataBinding dataBinding)
    {
        if (dataBinding == null)
        {
            return;
        }

        dataBinding.cancelHistoryButtonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = BookingCancelListActivity.newInstance(getActivity());
                startActivityForResult(intent, REQUEST_CODE_BOOKING_CANCEL);

                try
                {
                    AnalyticsManager.getInstance(getActivity()).recordEvent(AnalyticsManager.Category.RESERVATION_CANCEL, AnalyticsManager.Action.CANCEL_HISTORY, null, null);
                } catch (Exception e)
                {
                    ExLog.d(e.getMessage());
                }
            }
        });

        dataBinding.bookingSwipeRefreshLayout.setColorSchemeResources(R.color.dh_theme_color);
        dataBinding.bookingSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                BookingListFragment.this.onRefresh(false);
            }
        });

        dataBinding.bookingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        EdgeEffectColor.setEdgeGlowColor(dataBinding.bookingRecyclerView, getContext().getResources().getColor(R.color.default_over_scroll_edge));
        dataBinding.bookingRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener()
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

        dataBinding.loginTextView.setOnClickListener(this);
        dataBinding.viewStayLayout.setOnClickListener(this);
        dataBinding.viewGourmetLayout.setOnClickListener(this);
    }

    private void logoutLayout()
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.logoutLayout.setVisibility(View.VISIBLE);
        mViewDataBinding.bookingSwipeRefreshLayout.setVisibility(View.GONE);
        mViewDataBinding.emptyListLayout.setVisibility(View.GONE);

        setCancelHistoryButtonVisible(false);
    }

    void setBookingList(List<Booking> bookingList)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (bookingList == null || bookingList.size() == 0)
        {
            if (mAdapter != null)
            {
                mAdapter.clear();
            }

            //예약한 호텔이 없는 경우
            mViewDataBinding.bookingSwipeRefreshLayout.setVisibility(View.GONE);
            mViewDataBinding.emptyListLayout.setVisibility(View.VISIBLE);
            mViewDataBinding.loginTextView.setVisibility(View.GONE);
            mViewDataBinding.logoutLayout.setVisibility(View.GONE);
        } else
        {
            if (mAdapter == null)
            {
                mAdapter = new BookingListAdapter(getActivity(), new ArrayList<>());
                mAdapter.setOnUserActionListener(mOnUserActionListener);
                mViewDataBinding.bookingRecyclerView.setAdapter(mAdapter);
            } else
            {
                mAdapter.clear();
            }

            mAdapter.addAll(bookingList);
            mAdapter.notifyDataSetChanged();

            mViewDataBinding.bookingSwipeRefreshLayout.setVisibility(View.VISIBLE);
            mViewDataBinding.emptyListLayout.setVisibility(View.GONE);
            mViewDataBinding.loginTextView.setVisibility(View.GONE);
            mViewDataBinding.logoutLayout.setVisibility(View.GONE);
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
            logoutLayout();
        } else
        {
            if (mDontReload == true)
            {
                mDontReload = false;
            } else
            {
                onRefresh(true);
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
        switch (v.getId())
        {
            case R.id.loginTextView:
            {
                BaseActivity baseActivity = (BaseActivity) getActivity();

                if (baseActivity == null)
                {
                    return;
                }

                Intent intent = LoginActivity.newInstance(baseActivity);
                startActivity(intent);

                //            AnalyticsManager.getInstance(getActivity()).recordEvent(Screen.BOOKING_LIST, Action.CLICK, Label.LOGIN_, 0L);
                break;
            }

            case R.id.viewStayLayout:
                if (mOnMenuChangeListener != null && lockUiComponentAndIsLockUiComponent() == false)
                {
                    mOnMenuChangeListener.onMenu(MainFragmentManager.INDEX_HOME_FRAGMENT, Constants.CODE_RESULT_ACTIVITY_STAY_LIST);
                }

                AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                    , AnalyticsManager.Action.NO_RESULT_MOVE, AnalyticsManager.Label.STAY, null);
                break;

            case R.id.viewGourmetLayout:
                if (mOnMenuChangeListener != null && lockUiComponentAndIsLockUiComponent() == false)
                {
                    mOnMenuChangeListener.onMenu(MainFragmentManager.INDEX_HOME_FRAGMENT, Constants.CODE_RESULT_ACTIVITY_GOURMET_LIST);
                }

                AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                    , AnalyticsManager.Action.NO_RESULT_MOVE, AnalyticsManager.Label.GOURMET, null);
                break;
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
                mDontReload = false;
                break;
            }

            case REQUEST_CODE_BOOKING_CANCEL:
            {
                switch (resultCode)
                {
                    case Constants.CODE_RESULT_ACTIVITY_STAY_LIST:
                        mDontReload = true;

                        if (mOnMenuChangeListener != null && lockUiComponentAndIsLockUiComponent() == false)
                        {
                            mOnMenuChangeListener.onMenu(MainFragmentManager.INDEX_HOME_FRAGMENT, Constants.CODE_RESULT_ACTIVITY_STAY_LIST);
                        }
                        break;

                    case Constants.CODE_RESULT_ACTIVITY_GOURMET_LIST:
                        mDontReload = true;

                        if (mOnMenuChangeListener != null && lockUiComponentAndIsLockUiComponent() == false)
                        {
                            mOnMenuChangeListener.onMenu(MainFragmentManager.INDEX_HOME_FRAGMENT, Constants.CODE_RESULT_ACTIVITY_GOURMET_LIST);
                        }
                        break;

                    default:
                        mDontReload = false;
                        break;
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_BOOKING_DETAIL:
                mDontReload = false;
                break;

            case CODE_REQUEST_ACTIVITY_SATISFACTION_HOTEL:
            case CODE_REQUEST_ACTIVITY_SATISFACTION_GOURMET:
            case CODE_REQUEST_ACTIVITY_SATISFACTION_STAYOUTBOUND:
            default:
                mDontReload = false;
                break;
        }
    }

    void onRefresh(boolean showProgress)
    {
        lockUI(showProgress);

        setCancelHistoryButtonVisible(true);

        addCompositeDisposable(Observable.zip(mCommonRemoteImpl.getCommonDateTime()//
            , mBookingRemoteImpl.getBookingList(), mBookingRemoteImpl.getStayOutboundBookingList(getContext())//
            , new Function3<CommonDateTime, List<Booking>, List<Booking>, List<Booking>>()
            {
                @Override
                public List<Booking> apply(@NonNull CommonDateTime commonDateTime//
                    , @NonNull List<Booking> bookingList, @NonNull List<Booking> stayOutboundBookingList) throws Exception
                {
                    setCommonDateTime(commonDateTime);

                    bookingList.addAll(stayOutboundBookingList);

                    return new ArrayList<>(getBookingSortList(bookingList));
                }
            }).subscribe(new Consumer<List<Booking>>()
        {
            @Override
            public void accept(@NonNull List<Booking> bookingList) throws Exception
            {
                onBookingList(bookingList);

                mViewDataBinding.bookingSwipeRefreshLayout.setRefreshing(false);
                unLockUI();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception
            {
                onHandleError(throwable);

                setBookingList(null);

                unLockUI();
            }
        }));
    }

    void onBookingList(List<Booking> bookingList)
    {
        if (bookingList == null || bookingList.size() == 0)
        {
            setBookingList(null);

            AnalyticsManager.getInstance(getActivity()).recordScreen(getActivity(), Screen.BOOKING_LIST_EMPTY, null);
        } else
        {
            setBookingList(bookingList);

            Map<String, String> analyticsParams = new HashMap<>();
            analyticsParams.put(AnalyticsManager.KeyType.NUM_OF_BOOKING, Integer.toString(bookingList.size()));

            AnalyticsManager.getInstance(getActivity()).recordScreen(getActivity(), Screen.BOOKING_LIST, null, analyticsParams);

            addCompositeDisposable(Observable.just(bookingList).subscribe(new Consumer<List<Booking>>()
            {
                @Override
                public void accept(@NonNull List<Booking> bookingList) throws Exception
                {
                    boolean hasAfterUse = false;
                    boolean availableReview = false;

                    for (Booking booking : bookingList)
                    {
                        if (booking.bookingState == Booking.BOOKING_STATE_AFTER_USE)
                        {
                            hasAfterUse = true;

                            if (booking.availableReview == true)
                            {
                                availableReview = true;
                            }
                        }
                    }

                    if (hasAfterUse == true)
                    {
                        AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                            , AnalyticsManager.Action.BUTTONS_AVAILABLE, AnalyticsManager.Label.RESERVATION, null);
                    }

                    if (availableReview == true)
                    {
                        AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                            , AnalyticsManager.Action.BUTTONS_AVAILABLE, AnalyticsManager.Label.REVIEW, null);
                    }
                }
            }));
        }

        if (mDailyDeepLink != null)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            onDeepLink(baseActivity, bookingList);
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

    boolean startBookingDetail(BaseActivity baseActivity, Booking.PlaceType placeType,//
                               int reservationIndex, String aggregationId, String imageUrl, boolean isDeepLink, int bookingState)
    {
        Intent intent;

        switch (placeType)
        {
            case STAY:
                intent = StayBookingDetailActivity.newInstance(baseActivity, reservationIndex, aggregationId, imageUrl, isDeepLink, bookingState);
                break;

            case GOURMET:
                intent = GourmetBookingDetailActivity.newInstance(baseActivity, reservationIndex, aggregationId, imageUrl, isDeepLink, bookingState);
                break;

            case STAY_OUTBOUND:
                intent = StayOutboundBookingDetailActivity.newInstance(baseActivity, reservationIndex, aggregationId, imageUrl, bookingState);
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
    public void setOnMenuChangeListener(OnMenuChangeListener listener)
    {
        mOnMenuChangeListener = listener;
    }

    @Override
    public void scrollTop()
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.bookingRecyclerView.smoothScrollToPosition(0);
    }

    List<Booking> getBookingSortList(List<Booking> bookingList) throws Exception
    {
        if (bookingList == null || bookingList.size() == 0)
        {
            return new ArrayList<>();
        }

        // 예약 대기, 무료취소대기, 입금대기, 결제완료, 이용완료
        List<Booking> waitingForBookingList = new ArrayList<>();
        List<Booking> waitRefundList = new ArrayList<>();
        List<Booking> depositWaitingList = new ArrayList<>();
        List<Booking> beforeUseList = new ArrayList<>();
        List<Booking> afterUseList = new ArrayList<>();

        for (Booking booking : bookingList)
        {
            booking.remainingDays = DailyCalendar.compareDateDay(booking.checkInDateTime, mCommonDateTime.currentDateTime);

            if (booking.readyForRefund == true)
            {
                waitRefundList.add(booking);
            } else
            {
                switch (booking.statePayment)
                {
                    case Booking.PAYMENT_COMPLETED:
                    {
                        if (booking.waitingForBooking == true)
                        {
                            waitingForBookingList.add(booking);
                        } else
                        {
                            boolean used;

                            if (booking.placeType == Booking.PlaceType.STAY_OUTBOUND)
                            {
                                used = DailyCalendar.compareDateDay(booking.checkOutDateTime, mCommonDateTime.currentDateTime) < 0;
                            } else
                            {
                                used = DailyCalendar.compareDateTime(booking.checkOutDateTime, mCommonDateTime.currentDateTime) < 0;
                            }

                            if (used)
                            {
                                afterUseList.add(booking);
                            } else
                            {
                                beforeUseList.add(booking);
                            }
                        }
                        break;
                    }

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
                    compareDay = DailyCalendar.compareDateDay(booking1.checkInDateTime, booking2.checkInDateTime);
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
                    if (booking1.reservationIndex < booking2.reservationIndex)
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
                    compareDay = DailyCalendar.compareDateDay(booking1.checkInDateTime, booking2.checkInDateTime);
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
                    if (booking1.reservationIndex < booking2.reservationIndex)
                    {
                        return 1;
                    } else
                    {
                        return -1;
                    }
                }
            }
        };

        List<Booking> sortBookingList = new ArrayList<>(bookingList.size());

        // 예약 대기가 있는 경우
        if (waitingForBookingList.size() > 0)
        {
            Collections.sort(waitingForBookingList, ascComparator);

            for (Booking booking : waitingForBookingList)
            {
                booking.bookingState = Booking.BOOKING_STATE_RESERVATION_WAITING;
                sortBookingList.add(booking);
            }
        }

        // 무료취소대기가 있는 경우
        if (waitRefundList.size() > 0)
        {
            Collections.sort(waitRefundList, ascComparator);

            for (Booking booking : waitRefundList)
            {
                booking.bookingState = Booking.BOOKING_STATE_WAITING_REFUND;
                sortBookingList.add(booking);
            }

            AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                , AnalyticsManager.Action.BOOKING_STATUS_CHECK, AnalyticsManager.Label.CANCELLEATION_PROGRESS, null);
        }

        // 입금 대기가 있는 경우.
        if (depositWaitingList.size() > 0)
        {
            Collections.sort(depositWaitingList, ascComparator);

            for (Booking booking : depositWaitingList)
            {
                booking.bookingState = Booking.BOOKING_STATE_DEPOSIT_WAITING;
                sortBookingList.add(booking);
            }

            AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                , AnalyticsManager.Action.BOOKING_STATUS_CHECK, AnalyticsManager.Label.TEMPORARY_ACCOUNT, null);
        }

        // 결제 완료가 있는 경우.
        if (beforeUseList.size() > 0)
        {
            Collections.sort(beforeUseList, ascComparator);

            for (Booking booking : beforeUseList)
            {
                booking.bookingState = Booking.BOOKING_STATE_BEFORE_USE;
                sortBookingList.add(booking);
            }

            AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                , AnalyticsManager.Action.BOOKING_STATUS_CHECK, AnalyticsManager.Label.COMPLETE_PAYMENT, null);
        }

        // 이용 완료가 있는 경우.
        if (afterUseList.size() > 0)
        {
            Collections.sort(afterUseList, descComparator);

            for (Booking booking : afterUseList)
            {
                booking.bookingState = Booking.BOOKING_STATE_AFTER_USE;
                sortBookingList.add(booking);
            }

            AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                , AnalyticsManager.Action.BOOKING_STATUS_CHECK, AnalyticsManager.Label.POST_VISIT, null);
        }

        return sortBookingList;
    }

    void setCommonDateTime(CommonDateTime commonDateTime)
    {
        mCommonDateTime = commonDateTime;
    }

    private void setCancelHistoryButtonVisible(boolean isShow)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.cancelHistoryButtonView.setVisibility(isShow == true ? View.VISIBLE : View.GONE);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UserActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private OnUserActionListener mOnUserActionListener = new OnUserActionListener()
    {
        @Override
        public void onAgainBookingClick(Booking booking)
        {
            if (booking == null || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            try
            {
                switch (booking.placeType)
                {
                    case STAY:
                    {
                        StayBookingDay stayBookingDay = new StayBookingDay();
                        stayBookingDay.setCheckInDay(mCommonDateTime.dailyDateTime);
                        stayBookingDay.setCheckOutDay(mCommonDateTime.dailyDateTime, 1);

                        //                        Intent intent = StayDetailActivity.newInstance(getActivity(), stayBookingDay//
                        //                            , false, booking.placeIndex, 0, false, false, false);

                        Intent intent = StayDetailActivity.newInstance(getActivity() //
                            , booking.placeIndex, booking.placeName, null, StayDetailActivity.NONE_PRICE//
                            , stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
                            , stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT)//
                            , false, StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE, new StayDetailAnalyticsParam());

                        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL);

                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

                        AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                            , AnalyticsManager.Action.RE_RESERVATION, "stay_" + booking.placeIndex, null);
                        break;
                    }

                    case GOURMET:
                    {
                        Intent intent = GourmetDetailActivity.newInstance(getActivity() //
                            , booking.placeIndex, booking.placeName, null, GourmetDetailActivity.NONE_PRICE//
                            , mCommonDateTime.dailyDateTime//
                            , null, false, false, false, false//
                            , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE//
                            , new GourmetDetailAnalyticsParam());

                        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_DETAIL);

                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

                        AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                            , AnalyticsManager.Action.RE_RESERVATION, "gourmet_" + booking.placeIndex, null);
                        break;
                    }

                    case STAY_OUTBOUND:
                    {
                        StayBookDateTime stayBookDateTime = new StayBookDateTime();
                        stayBookDateTime.setCheckInDateTime(mCommonDateTime.currentDateTime, 7);
                        stayBookDateTime.setCheckOutDateTime(stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), 1);

                        startActivityForResult(StayOutboundDetailActivity.newInstance(getActivity(), booking.placeIndex//
                            , booking.placeName, null, null, StayOutboundDetailActivity.NONE_PRICE//
                            , stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                            , stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                            , 2, null, false, StayOutboundDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE, null), CODE_REQUEST_ACTIVITY_STAY_OB_DETAIL);

                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

                        AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                            , AnalyticsManager.Action.RE_RESERVATION, "ob_" + booking.placeIndex, null);
                        break;
                    }
                }
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
        }

        @Override
        public void onBookingClick(Booking booking)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null || booking == null)
            {
                return;
            }

            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent;

            switch (booking.statePayment)
            {
                case Booking.PAYMENT_COMPLETED:
                    if (startBookingDetail(baseActivity, booking.placeType, booking.reservationIndex, booking.aggregationId, booking.imageUrl, false, booking.bookingState) == false)
                    {
                        releaseUiComponent();
                    }
                    break;

                case Booking.PAYMENT_WAITING:
                    // 가상계좌 입금대기
                    //                    intent = PaymentWaitActivity.newInstance(baseActivity, booking);
                    intent = com.daily.dailyhotel.screen.booking.detail.wait.PaymentWaitActivity.newInstance(baseActivity, booking);
                    baseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_VIRTUAL_BOOKING_DETAIL);

                    AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                        , AnalyticsManager.Action.BOOKING_DETAIL_CLICK, AnalyticsManager.Label.TEMPORARY_ACCOUNT, null);
                    break;

                default:
                    releaseUiComponent();
                    break;
            }
        }

        @Override
        public void onReviewClick(Booking booking)
        {
            if (booking == null || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            switch (booking.placeType)
            {
                case STAY:
                {
                    addCompositeDisposable(mReviewRemoteImpl.getStayReview(booking.reservationIndex) //
                        .subscribeOn(Schedulers.io()).map(new Function<Review, com.twoheart.dailyhotel.model.Review>()
                        {
                            @Override
                            public com.twoheart.dailyhotel.model.Review apply(@NonNull Review review) throws Exception
                            {
                                return reviewToReviewParcelable(review);
                            }
                        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<com.twoheart.dailyhotel.model.Review>()
                        {
                            @Override
                            public void accept(@NonNull com.twoheart.dailyhotel.model.Review review) throws Exception
                            {
                                Intent intent = ReviewActivity.newInstance(getActivity(), review, booking.reviewStatusType);
                                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SATISFACTION_HOTEL);
                            }
                        }, new Consumer<Throwable>()
                        {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception
                            {
                                onHandleError(throwable);
                            }
                        }));

                    AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                        , AnalyticsManager.Action.LEAVE_REVIEW, "stay_" + booking.placeIndex, null);
                    break;
                }

                case GOURMET:
                {
                    addCompositeDisposable(mReviewRemoteImpl.getGourmetReview(booking.reservationIndex) //
                        .subscribeOn(Schedulers.io()).map(new Function<Review, com.twoheart.dailyhotel.model.Review>()
                        {
                            @Override
                            public com.twoheart.dailyhotel.model.Review apply(@NonNull Review review) throws Exception
                            {
                                return reviewToReviewParcelable(review);
                            }
                        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<com.twoheart.dailyhotel.model.Review>()
                        {
                            @Override
                            public void accept(@NonNull com.twoheart.dailyhotel.model.Review review) throws Exception
                            {
                                Intent intent = ReviewActivity.newInstance(getActivity(), review, booking.reviewStatusType);
                                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SATISFACTION_GOURMET);
                            }
                        }, new Consumer<Throwable>()
                        {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception
                            {
                                onHandleError(throwable);
                            }
                        }));

                    AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                        , AnalyticsManager.Action.LEAVE_REVIEW, "gourmet_" + booking.placeIndex, null);
                    break;
                }

                case STAY_OUTBOUND:
                {
                    addCompositeDisposable(mReviewRemoteImpl.getStayOutboundReview(getActivity(), booking.reservationIndex) //
                        .subscribeOn(Schedulers.io()).map(new Function<Review, com.twoheart.dailyhotel.model.Review>()
                        {
                            @Override
                            public com.twoheart.dailyhotel.model.Review apply(@NonNull Review review) throws Exception
                            {
                                return reviewToReviewParcelable(review);
                            }
                        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<com.twoheart.dailyhotel.model.Review>()
                        {
                            @Override
                            public void accept(@NonNull com.twoheart.dailyhotel.model.Review review) throws Exception
                            {
                                Intent intent = ReviewActivity.newInstance(getActivity(), review, booking.reviewStatusType);
                                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SATISFACTION_STAYOUTBOUND);
                            }
                        }, new Consumer<Throwable>()
                        {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception
                            {
                                onHandleError(throwable);
                            }
                        }));
                    break;
                }

                default:
                {
                    unLockUI();
                    break;
                }
            }
        }
    };

    private boolean onDeepLink(BaseActivity baseActivity, List<Booking> bookingList)
    {
        if (baseActivity == null || bookingList == null || bookingList.size() == 0)
        {
            return false;
        }

        if (mDailyDeepLink == null)
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
                    Booking.PlaceType placeType;

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

                    String aggregationId = internalDeepLink.getAggregationId();

                    if (DailyTextUtils.isTextEmpty(aggregationId) == true)
                    {
                        int reservationIndex = internalDeepLink.getReservationIndex();

                        for (Booking booking : bookingList)
                        {
                            if (booking.reservationIndex == reservationIndex && placeType == booking.placeType)
                            {
                                unLockUI();
                                mOnUserActionListener.onBookingClick(booking);
                                break;
                            }
                        }
                    } else
                    {
                        for (Booking booking : bookingList)
                        {
                            if ((aggregationId != null && aggregationId.equalsIgnoreCase(booking.aggregationId))//
                                && placeType == booking.placeType)
                            {
                                unLockUI();
                                mOnUserActionListener.onBookingClick(booking);
                                break;
                            }
                        }
                    }
                }
            } else
            {
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) mDailyDeepLink;

                if (externalDeepLink.isBookingDetailView() == true)
                {
                    Booking.PlaceType placeType;

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

                    String aggregationId = externalDeepLink.getAggregationId();
                    int reservationIndex = 0;
                    String imageUrl = null;

                    for (Booking booking : bookingList)
                    {
                        if (aggregationId.equalsIgnoreCase(booking.aggregationId))
                        {
                            reservationIndex = booking.reservationIndex;
                            imageUrl = booking.imageUrl;
                            break;
                        }
                    }

                    startBookingDetail(baseActivity, placeType, reservationIndex, aggregationId, imageUrl, true, Booking.BOOKING_STATE_NONE);
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

    com.twoheart.dailyhotel.model.Review reviewToReviewParcelable(Review review)
    {
        com.twoheart.dailyhotel.model.Review reviewParcelable = new com.twoheart.dailyhotel.model.Review();

        if (review == null)
        {
            return reviewParcelable;
        }

        reviewParcelable.requiredCommentReview = review.requiredCommentReview;
        reviewParcelable.reserveIdx = review.reserveIdx;

        com.twoheart.dailyhotel.model.ReviewItem reviewItemParcelable = new com.twoheart.dailyhotel.model.ReviewItem();

        ReviewItem reviewItem = review.getReviewItem();

        if (reviewItem != null)
        {
            reviewItemParcelable.itemIdx = reviewItem.itemIdx;
            reviewItemParcelable.itemName = reviewItem.itemName;
            reviewItemParcelable.setImageMap(reviewItem.getImageMap());

            switch (reviewItem.serviceType)
            {
                case "HOTEL":
                    reviewItemParcelable.serviceType = Constants.ServiceType.HOTEL;
                    break;

                case "GOURMET":
                    reviewItemParcelable.serviceType = Constants.ServiceType.GOURMET;
                    break;

                case "OUTBOUND":
                    reviewItemParcelable.serviceType = Constants.ServiceType.OB_STAY;
                    break;

                default:
                    ExLog.d("unKnown service type");
                    break;
            }

            reviewItemParcelable.useEndDate = reviewItem.useEndDate;
            reviewItemParcelable.useStartDate = reviewItem.useStartDate;
        }

        reviewParcelable.setReviewItem(reviewItemParcelable);

        //
        ArrayList<ReviewPickQuestion> reviewPickQuestionListParcelable = new ArrayList<>();

        List<ReviewQuestionItem> reviewPickQuestionList = review.getReviewPickQuestionList();

        if (reviewPickQuestionList != null && reviewPickQuestionList.size() > 0)
        {
            for (ReviewQuestionItem reviewQuestionItem : reviewPickQuestionList)
            {
                ReviewPickQuestion reviewPickQuestion = new ReviewPickQuestion();
                reviewPickQuestion.title = reviewQuestionItem.title;
                reviewPickQuestion.description = reviewQuestionItem.description;
                reviewPickQuestion.answerCode = reviewQuestionItem.answerCode;

                //
                ArrayList<com.twoheart.dailyhotel.model.ReviewAnswerValue> reviewAnswerValueListParcelable = new ArrayList<>();

                List<ReviewAnswerValue> reviewAnswerValueList = reviewQuestionItem.getAnswerValueList();

                if (reviewAnswerValueList != null && reviewAnswerValueList.size() > 0)
                {
                    for (ReviewAnswerValue reviewAnswerValue : reviewAnswerValueList)
                    {
                        com.twoheart.dailyhotel.model.ReviewAnswerValue reviewAnswerValueParcelable = new com.twoheart.dailyhotel.model.ReviewAnswerValue();

                        reviewAnswerValueParcelable.code = reviewAnswerValue.code;
                        reviewAnswerValueParcelable.description = reviewAnswerValue.description;

                        reviewAnswerValueListParcelable.add(reviewAnswerValueParcelable);
                    }

                    // 짝수개로 맞춘다.
                    if (reviewAnswerValueListParcelable.size() % 2 == 1)
                    {
                        reviewAnswerValueListParcelable.add(new com.twoheart.dailyhotel.model.ReviewAnswerValue());
                    }
                }

                reviewPickQuestion.setAnswerValueList(reviewAnswerValueListParcelable);
                reviewPickQuestionListParcelable.add(reviewPickQuestion);
            }
        }

        reviewParcelable.setReviewPickQuestionList(reviewPickQuestionListParcelable);

        //
        ArrayList<ReviewScoreQuestion> reviewScoreQuestionListParcelable = new ArrayList<>();

        List<ReviewQuestionItem> reviewScoreQuestionList = review.getReviewScoreQuestionList();

        if (reviewScoreQuestionList != null && reviewScoreQuestionList.size() > 0)
        {
            for (ReviewQuestionItem reviewQuestionItem : reviewScoreQuestionList)
            {
                ReviewScoreQuestion reviewScoreQuestion = new ReviewScoreQuestion();
                reviewScoreQuestion.title = reviewQuestionItem.title;
                reviewScoreQuestion.description = reviewQuestionItem.description;
                reviewScoreQuestion.answerCode = reviewQuestionItem.answerCode;

                reviewScoreQuestionListParcelable.add(reviewScoreQuestion);
            }
        }

        reviewParcelable.setReviewScoreQuestionList(reviewScoreQuestionListParcelable);

        return reviewParcelable;
    }
}
