package com.daily.dailyhotel.screen.booking.cancel;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.BookingCancel;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.repository.remote.BookingRemoteImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
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
    private BookingCancelListAnalyticsInterface mAnalytics;

    private CommonRemoteImpl mCommonRemoteImpl;
    private BookingRemoteImpl mBookingRemoteImpl;
    private ProfileRemoteImpl mProfileRemoteImpl;

    private CommonDateTime mCommonDateTime;

    boolean mCheckVerify; // 인증이 해지되었는지 취소 리스트 진입시 한번만 체크한다. - 예약 내역에 있어서 추가

    public interface BookingCancelListAnalyticsInterface extends BaseAnalyticsInterface
    {
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

        setAnalytics(new BookingCancelListAnalyticsImpl());

        mCommonRemoteImpl = new CommonRemoteImpl(activity);
        mBookingRemoteImpl = new BookingRemoteImpl(activity);
        mProfileRemoteImpl = new ProfileRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (BookingCancelListAnalyticsInterface) analytics;
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
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(getString(R.string.actionbar_title_booking_cancel_list_activity));
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (DailyHotel.isLogin() == false)
        {
            if (getViewInterface() != null)
            {
                getViewInterface().logoutLayout();
            }
        } else
        {
            if (isRefresh() == true)
            {
                onRefresh(true);
            }
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
            , mBookingRemoteImpl.getBookingCancelList(), mBookingRemoteImpl.getStayOutboundBookingCancelList() //
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

    private void onBookingCancelList(List<BookingCancel> bookingCancelList)
    {
        if (bookingCancelList == null || bookingCancelList.size() == 0)
        {
            getViewInterface().setBookingCancelList(null);

            //            AnalyticsManager.getInstance(getActivity()).recordScreen(getActivity(), AnalyticsManager.Screen.BOOKING_LIST_EMPTY, null);
        } else
        {
            getViewInterface().setBookingCancelList(bookingCancelList);

            //            Map<String, String> analyticsParams = new HashMap<>();
            //            analyticsParams.put(AnalyticsManager.KeyType.NUM_OF_BOOKING, Integer.toString(bookingCancelList.size()));
            //
            //            AnalyticsManager.getInstance(getActivity()).recordScreen(getActivity(), AnalyticsManager.Screen.BOOKING_LIST, null, analyticsParams);
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

    private void setCommonDateTime(CommonDateTime commonDateTime)
    {
        mCommonDateTime = commonDateTime;
    }

    private List<BookingCancel> getBookingCancelSortList(List<BookingCancel> list)
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // EventListener

    @Override
    public void onRefreshAll(boolean isShowProgress)
    {
        onRefresh(isShowProgress);
    }

    @Override
    public void onAgainBookingClick(BookingCancel bookingCancel)
    {

    }

    @Override
    public void onBookingClick(BookingCancel bookingCancel)
    {

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
    }
}
