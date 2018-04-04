package com.daily.dailyhotel.screen.booking.detail.wait;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Booking;
import com.daily.dailyhotel.entity.GourmetOldWaitingDeposit;
import com.daily.dailyhotel.entity.StayOldWaitingDeposit;
import com.daily.dailyhotel.entity.WaitingDeposit;
import com.daily.dailyhotel.parcel.BookingParcel;
import com.daily.dailyhotel.repository.remote.BookingRemoteImpl;
import com.daily.dailyhotel.screen.common.dialog.call.CallDialogActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.HappyTalkCategoryDialog;
import com.twoheart.dailyhotel.screen.information.FAQActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class PaymentWaitPresenter extends BaseExceptionPresenter<PaymentWaitActivity, PaymentWaitInterface> implements PaymentWaitView.OnEventListener
{
    PaymentWaitAnalyticsInterface mAnalytics;

    BookingRemoteImpl mBookingRemoteImpl;
    Booking mBooking;

    public interface PaymentWaitAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onEventConciergeClick(Activity activity, Booking.PlaceType placeType);

        void onEventConciergeFaqClick(Activity activity, Booking.PlaceType placeType);

        void onEventConciergeHappyTalkClick(Activity activity, Booking.PlaceType placeType);

        void onEventConciergeHappyTalkClick2(Activity activity);

        void onEventConciergeCallClick(Activity activity);

        void onEventConciergeCallResultOK(Activity activity);

        void onEventConciergeCallResultCancel(Activity activity);
    }

    public PaymentWaitPresenter(@NonNull PaymentWaitActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected PaymentWaitInterface createInstanceViewInterface()
    {
        return new PaymentWaitView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(PaymentWaitActivity activity)
    {
        setContentView(R.layout.activity_payment_wait_data);

        mAnalytics = new PaymentWaitAnalyticsImpl();

        mBookingRemoteImpl = new BookingRemoteImpl(getActivity());

        setRefresh(true);
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        BookingParcel bookingParcel = intent.getParcelableExtra(PaymentWaitActivity.INTENT_EXTRA_DATA_BOOKING);

        if (bookingParcel == null)
        {
            Util.restartApp(getActivity());
            return false;
        }

        mBooking = bookingParcel.getBooking();

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
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

        switch (requestCode)
        {
            case PaymentWaitActivity.REQUEST_CODE_CALL:
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    mAnalytics.onEventConciergeCallResultOK(getActivity());
                } else
                {
                    mAnalytics.onEventConciergeCallResultCancel(getActivity());
                }
                break;
            }
        }
    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

        if (mBooking == null)
        {
            return;
        }

        setRefresh(false);
        screenLock(showProgress);

        Observable<WaitingDeposit> observable = Observable.defer(new Callable<ObservableSource<? extends WaitingDeposit>>()
        {
            @Override
            public ObservableSource<? extends WaitingDeposit> call() throws Exception
            {
                if (DailyTextUtils.isTextEmpty(mBooking.aggregationId) == true)
                {
                    switch (mBooking.placeType)
                    {
                        case STAY:
                        {
                            return mBookingRemoteImpl.getStayOldWaitingDeposit(mBooking.reservationIndex) //
                                .map(new Function<StayOldWaitingDeposit, WaitingDeposit>()
                                {
                                    @Override
                                    public WaitingDeposit apply(StayOldWaitingDeposit stayOldWaitingDeposit) throws Exception
                                    {
                                        return getWaitingDeposit(stayOldWaitingDeposit);
                                    }
                                });
                        }
                        case GOURMET:
                        {
                            return mBookingRemoteImpl.getGourmetOldWaitingDeposit(mBooking.reservationIndex) //
                                .map(new Function<GourmetOldWaitingDeposit, WaitingDeposit>()
                                {
                                    @Override
                                    public WaitingDeposit apply(GourmetOldWaitingDeposit gourmetOldWaitingDeposit) throws Exception
                                    {
                                        return getWaitingDeposit(gourmetOldWaitingDeposit);
                                    }
                                });
                        }
                    }
                }

                return mBookingRemoteImpl.getWaitingDeposit(mBooking.aggregationId);
            }
        });

        addCompositeDisposable(observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<WaitingDeposit>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull WaitingDeposit waitingDeposit) throws Exception
            {
                unLockAll();

                getViewInterface().setPlaceName(mBooking.placeName);
                getViewInterface().setWaitingDeposit(waitingDeposit);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
            {
                onHandleErrorAndFinish(throwable);
            }
        }));
    }

    WaitingDeposit getWaitingDeposit(StayOldWaitingDeposit stayOldWaitingDeposit)
    {
        if (stayOldWaitingDeposit == null)
        {
            return null;
        }

        WaitingDeposit waitingDeposit = new WaitingDeposit();

        waitingDeposit.accountHolder = stayOldWaitingDeposit.reservation.accountHolder;
        waitingDeposit.accountNumber = stayOldWaitingDeposit.reservation.accountNumber;
        waitingDeposit.bankName = stayOldWaitingDeposit.reservation.bankName;
        waitingDeposit.bonusAmount = stayOldWaitingDeposit.reservation.bonusAmount;
        waitingDeposit.couponAmount = stayOldWaitingDeposit.reservation.couponAmount;
        waitingDeposit.depositWaitingAmount = stayOldWaitingDeposit.reservation.depositWaitingAmount;
        waitingDeposit.totalPrice = stayOldWaitingDeposit.reservation.totalPrice;
        waitingDeposit.expiredAt = stayOldWaitingDeposit.reservation.expiredAt;

        String[] messages1 = stayOldWaitingDeposit.message1.split("\\.");
        List<String> message1List = new ArrayList<>(Arrays.asList(messages1));
        waitingDeposit.setMessage1List(message1List);

        // 기존에 강제 코딩하고 있어서 그대로 옮김!
        //                                        String[] messages2 = stayOldWaitingDeposit.message2.split("\\.");
        //                                        List<String> message2List = new ArrayList<>(Arrays.asList(messages2));
        List<String> message2List = new ArrayList<>();
        message2List.add(getString(R.string.message__wait_payment03));
        waitingDeposit.setMessage2List(message2List);

        return waitingDeposit;
    }

    WaitingDeposit getWaitingDeposit(GourmetOldWaitingDeposit gourmetOldWaitingDeposit)
    {
        if (gourmetOldWaitingDeposit == null)
        {
            return null;
        }

        WaitingDeposit waitingDeposit = new WaitingDeposit();

        waitingDeposit.accountHolder = gourmetOldWaitingDeposit.accountHolder;
        waitingDeposit.accountNumber = gourmetOldWaitingDeposit.accountNumber;
        waitingDeposit.bankName = gourmetOldWaitingDeposit.bankName;
        waitingDeposit.bonusAmount = 0; // 고메는 적립금 사용 못함!
        waitingDeposit.couponAmount = gourmetOldWaitingDeposit.couponAmount;
        waitingDeposit.depositWaitingAmount = gourmetOldWaitingDeposit.depositWaitingAmount;
        waitingDeposit.totalPrice = gourmetOldWaitingDeposit.totalPrice;

        // 2017-12-19T14:29:00+09:00 이어야 하나 "2017\/12\/19", "14:36:00" 의 조합 임
        String[] dateSlice = gourmetOldWaitingDeposit.expireDate.split("/");
        String[] timeSlice = gourmetOldWaitingDeposit.expireTime.split(":");

        Calendar calendar = DailyCalendar.getInstance();
        calendar.set(Integer.parseInt(dateSlice[0]), Integer.parseInt(dateSlice[1]) - 1, Integer.parseInt(dateSlice[2]) //
            , Integer.parseInt(timeSlice[0]), Integer.parseInt(timeSlice[1]), 0);
        calendar.set(Calendar.MILLISECOND, 0);

        waitingDeposit.expiredAt = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

        String[] messages1 = gourmetOldWaitingDeposit.message1.split("\\.");
        List<String> message1List = new ArrayList<>(Arrays.asList(messages1));
        waitingDeposit.setMessage1List(message1List);

        // 기존에 강제 코딩하고 있어서 그대로 옮김!
        //                                        String[] messages2 = gourmetOldWaitingDeposit.message2.split("\\.");
        //                                        List<String> message2List = new ArrayList<>(Arrays.asList(messages2));
        List<String> message2List = new ArrayList<>();
        message2List.add(getString(R.string.message__wait_payment03));
        waitingDeposit.setMessage2List(message2List);

        return waitingDeposit;
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onConciergeClick()
    {
        if (mBooking == null || getViewInterface() == null || getActivity() == null)
        {
            return;
        }

        getViewInterface().showConciergeDialog(mBooking.placeType, new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialogInterface)
            {
                unLockAll();
            }
        });

        mAnalytics.onEventConciergeClick(getActivity(), mBooking.placeType);
    }

    @Override
    public void onConciergeFaqClick(Booking.PlaceType placeType)
    {
        if (getActivity() == null)
        {
            return;
        }

        startActivityForResult(FAQActivity.newInstance(getActivity()), PaymentWaitActivity.REQUEST_CODE_FAQ);

        mAnalytics.onEventConciergeFaqClick(getActivity(), placeType);
    }

    @Override
    public void onConciergeHappyTalkClick(Booking.PlaceType placeType)
    {
        if (getActivity() == null || getViewInterface() == null)
        {
            return;
        }

        try
        {
            getActivity().getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

            mAnalytics.onEventConciergeHappyTalkClick2(getActivity());

            switch (mBooking.placeType)
            {
                case STAY:
                    startActivityForResult(HappyTalkCategoryDialog.newInstance(getActivity()//
                        , HappyTalkCategoryDialog.CallScreen.SCREEN_STAY_PAYMENT_WAIT, 0//
                        , mBooking.reservationIndex, mBooking.placeName), Constants.CODE_REQUEST_ACTIVITY_HAPPY_TALK);
                    break;

                case GOURMET:
                    startActivityForResult(HappyTalkCategoryDialog.newInstance(getActivity()//
                        , HappyTalkCategoryDialog.CallScreen.SCREEN_GOURMET_PAYMENT_WAIT, 0//
                        , mBooking.reservationIndex, mBooking.placeName), Constants.CODE_REQUEST_ACTIVITY_HAPPY_TALK);
                    break;
            }
        } catch (Exception e)
        {
            getViewInterface().showSimpleDialog(null, getString(R.string.dialog_msg_not_installed_kakaotalk)//
                , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no)//
                , new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Util.installPackage(getActivity(), "com.kakao.talk");
                    }
                }, null);
        }


        mAnalytics.onEventConciergeHappyTalkClick(getActivity(), placeType);
    }

    @Override
    public void onConciergeCallClick(Booking.PlaceType placeType)
    {
        if (getActivity() == null)
        {
            return;
        }

        startActivityForResult(CallDialogActivity.newInstance(getActivity()), PaymentWaitActivity.REQUEST_CODE_CALL);

        mAnalytics.onEventConciergeCallClick(getActivity());
    }

    @Override
    public void onClipAccountNumberClick(String accountNumber)
    {
        if (getActivity() == null || DailyTextUtils.isTextEmpty(accountNumber) == true)
        {
            return;
        }

        DailyTextUtils.clipText(getActivity(), accountNumber);

        DailyToast.showToast(getActivity(), R.string.message_detail_copy_account_number, Toast.LENGTH_SHORT);
    }
}
