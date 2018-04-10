package com.daily.dailyhotel.screen.booking.cancel;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.BookingCancel;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.parcel.analytics.GourmetDetailAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam;
import com.daily.dailyhotel.repository.remote.BookingRemoteImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.daily.dailyhotel.screen.booking.cancel.detail.gourmet.GourmetBookingCancelDetailActivity;
import com.daily.dailyhotel.screen.booking.cancel.detail.stay.StayBookingCancelDetailActivity;
import com.daily.dailyhotel.screen.booking.cancel.detail.stay.outbound.StayOutboundBookingCancelDetailActivity;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.detail.StayOutboundDetailActivity;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class BookingCancelListPresenter extends BaseExceptionPresenter<BookingCancelListActivity, BookingCancelListInterface> implements BookingCancelListView.OnEventListener
{
    BookingCancelListAnalyticsInterface mAnalytics;

    private CommonRemoteImpl mCommonRemoteImpl;
    private BookingRemoteImpl mBookingRemoteImpl;
    private ProfileRemoteImpl mProfileRemoteImpl;

    private CommonDateTime mCommonDateTime;

    boolean mCheckVerify; // 인증이 해지되었는지 취소 리스트 진입시 한번만 체크한다. - 예약 내역에 있어서 추가

    public interface BookingCancelListAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity);

        void onEventAgainClick(Activity activity);

        void onEventBackClick(Activity activity);

        void onEventEmptyView(Activity activity);

        void onEventViewStayClick(Activity activity);

        void onEventViewGourmetClick(Activity activity);
    }

    public BookingCancelListPresenter(@NonNull BookingCancelListActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected BookingCancelListInterface createInstanceViewInterface()
    {
        return new BookingCancelListView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(BookingCancelListActivity activity)
    {
        setContentView(R.layout.activity_booking_cancel_list_data);

        mAnalytics = new BookingCancelListAnalyticsImpl();

        mCommonRemoteImpl = new CommonRemoteImpl();
        mBookingRemoteImpl = new BookingRemoteImpl();
        mProfileRemoteImpl = new ProfileRemoteImpl();

        setRefresh(true);
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(getString(R.string.actionbar_title_booking_cancel_list_activity));
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (DailyHotel.isLogin() == false)
        {
            if (getViewInterface() != null)
            {
                getViewInterface().logoutLayout();
                setRefresh(false);
            }
        } else
        {
            if (isRefresh() == true)
            {
                onRefresh(true);
            }
        }

        mAnalytics.onScreen(getActivity());
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        // 꼭 호출해 주세요.
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed()
    {
        mAnalytics.onEventBackClick(getActivity());

        return super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();

        if (Constants.CODE_RESULT_ACTIVITY_REFRESH == resultCode)
        {
            setRefresh(true);
        }
    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

        setRefresh(false);
        screenLock(showProgress);

        addCompositeDisposable(Observable.zip(mCommonRemoteImpl.getCommonDateTime().observeOn(Schedulers.io()) //
            , mBookingRemoteImpl.getBookingCancelList(), mBookingRemoteImpl.getStayOutboundBookingCancelList(getActivity()) //
            , new Function3<CommonDateTime, List<BookingCancel>, List<BookingCancel>, List<BookingCancel>>()
            {
                @Override
                public List<BookingCancel> apply(@io.reactivex.annotations.NonNull CommonDateTime commonDateTime //
                    , @io.reactivex.annotations.NonNull List<BookingCancel> bookingCancelList //
                    , @io.reactivex.annotations.NonNull List<BookingCancel> stayOutboundBookingCancelList) throws Exception
                {
                    setCommonDateTime(commonDateTime);

                    bookingCancelList.addAll(stayOutboundBookingCancelList);

                    List<BookingCancel> cancelSortList = new ArrayList<>();
                    cancelSortList.addAll(getBookingCancelSortList(bookingCancelList));

                    return cancelSortList;
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<BookingCancel>>()
        {
            @Override
            public void accept(List<BookingCancel> bookingCancelList) throws Exception
            {
                onBookingCancelList(bookingCancelList);

                getViewInterface().setRefreshing(false);

                unLockAll();

                if (bookingCancelList == null || bookingCancelList.size() == 0)
                {
                    mAnalytics.onEventEmptyView(getActivity());
                }
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                onHandleError(throwable);

                onBookingCancelList(null);

                unLockAll();
            }
        }));
    }

    void onBookingCancelList(List<BookingCancel> bookingCancelList)
    {
        if (bookingCancelList == null || bookingCancelList.size() == 0)
        {
            getViewInterface().setBookingCancelList(null);
        } else
        {
            getViewInterface().setBookingCancelList(bookingCancelList);
        }

        if (mCheckVerify == true)
        {
            return;
        }

        mCheckVerify = true;

        addCompositeDisposable(mProfileRemoteImpl.getProfile().subscribe(new Consumer<User>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull User user) throws Exception
            {
                // 인증 후 인증이 해지된 경우
                if (user.verified == true && user.phoneVerified == false && DailyPreference.getInstance(getActivity()).isVerification() == true)
                {
                    getViewInterface().showSimpleDialog(null, getString(R.string.message_invalid_verification), getString(R.string.dialog_btn_text_confirm), null);

                    DailyPreference.getInstance(getActivity()).setVerification(false);
                }
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
            {
                // 실패시에 아무것도 하지 않음.
            }
        }));
    }

    void setCommonDateTime(CommonDateTime commonDateTime)
    {
        mCommonDateTime = commonDateTime;
    }

    List<BookingCancel> getBookingCancelSortList(List<BookingCancel> list)
    {
        if (list == null || list.size() == 0)
        {
            return new ArrayList<>();
        }

        Collections.sort(list, new Comparator<BookingCancel>()
        {
            @Override
            public int compare(BookingCancel cancel1, BookingCancel cancel2)
            {
                try
                {
                    Date date1 = DailyCalendar.convertDate(cancel1.cancelDateTime, DailyCalendar.ISO_8601_FORMAT, null);
                    Date date2 = DailyCalendar.convertDate(cancel2.cancelDateTime, DailyCalendar.ISO_8601_FORMAT, null);

                    return date1.compareTo(date2);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }

                return 0;
            }
        });

        Collections.reverse(list);

        List<BookingCancel> cancelSortList = new ArrayList<>();
        cancelSortList.addAll(list);

        return cancelSortList;
    }

    private boolean startBookingCancelDetail(BookingCancel.PlaceType placeType, int reservationIndex, String aggregationId, String imageUrl)
    {
        Intent intent;

        switch (placeType)
        {
            case STAY:
                intent = StayBookingCancelDetailActivity.newInstance(getActivity(), reservationIndex, aggregationId, imageUrl);
                break;

            case GOURMET:
                intent = GourmetBookingCancelDetailActivity.newInstance(getActivity(), reservationIndex, aggregationId, imageUrl);
                break;

            case STAY_OUTBOUND:
                intent = StayOutboundBookingCancelDetailActivity.newInstance(getActivity(), reservationIndex, imageUrl);
                break;

            default:
                return false;
        }

        startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_BOOKING_DETAIL);

        return true;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // EventListener

    @Override
    public void onRefreshAll(boolean isShowProgress)
    {
        setRefresh(true);
        onRefresh(isShowProgress);
    }

    @Override
    public void onAgainBookingClick(BookingCancel bookingCancel)
    {
        if (getActivity() == null || bookingCancel == null)
        {
            return;
        }

        if (lock() == true)
        {
            return;
        }

        try
        {
            switch (bookingCancel.placeType)
            {
                case STAY:
                {
                    StayBookingDay stayBookingDay = new StayBookingDay();
                    stayBookingDay.setCheckInDay(mCommonDateTime.dailyDateTime);
                    stayBookingDay.setCheckOutDay(mCommonDateTime.dailyDateTime, 1);

                    Intent intent = StayDetailActivity.newInstance(getActivity() //
                        , bookingCancel.itemIdx, bookingCancel.name, null, StayDetailActivity.NONE_PRICE//
                        , stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
                        , stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT)//
                        , false, StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE, new StayDetailAnalyticsParam());

                    startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_STAY_DETAIL);

                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
                    break;
                }

                case GOURMET:
                {
                    Intent intent = GourmetDetailActivity.newInstance(getActivity() //
                        , bookingCancel.itemIdx, bookingCancel.name, null, GourmetDetailActivity.NONE_PRICE//
                        , mCommonDateTime.dailyDateTime//
                        , null, false, false, false, false//
                        , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE//
                        , new GourmetDetailAnalyticsParam());

                    startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_GOURMET_DETAIL);

                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
                    break;
                }

                case STAY_OUTBOUND:
                {
                    StayBookDateTime stayBookDateTime = new StayBookDateTime();
                    stayBookDateTime.setCheckInDateTime(mCommonDateTime.currentDateTime, 7);
                    stayBookDateTime.setCheckOutDateTime(stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), 1);

                    startActivityForResult(StayOutboundDetailActivity.newInstance(getActivity() //
                        , bookingCancel.itemIdx, bookingCancel.name, null, null, StayOutboundDetailActivity.NONE_PRICE//
                        , stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                        , stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                        , 2, null, false, StayOutboundDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE, null), Constants.CODE_REQUEST_ACTIVITY_STAY_OB_DETAIL);

                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
                    break;
                }
            }

            mAnalytics.onEventAgainClick(getActivity());
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onBookingCancelClick(BookingCancel bookingCancel)
    {
        if (getActivity() == null || bookingCancel == null)
        {
            return;
        }

        if (lock() == true)
        {
            return;
        }

        startBookingCancelDetail(bookingCancel.placeType, bookingCancel.reservationIdx, bookingCancel.aggregationId, bookingCancel.imageUrl);
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onLoginClick()
    {
        if (getActivity() == null)
        {
            return;
        }

        Intent intent = LoginActivity.newInstance(getActivity());
        startActivity(intent);
    }

    @Override
    public void onViewStayClick()
    {
        if (lock() == true)
        {
            return;
        }

        setResult(Constants.CODE_RESULT_ACTIVITY_STAY_LIST);
        finish();

        mAnalytics.onEventViewStayClick(getActivity());
    }

    @Override
    public void onViewGourmetClick()
    {
        if (lock() == true)
        {
            return;
        }

        setResult(Constants.CODE_RESULT_ACTIVITY_GOURMET_LIST);
        finish();

        mAnalytics.onEventViewGourmetClick(getActivity());
    }
}
