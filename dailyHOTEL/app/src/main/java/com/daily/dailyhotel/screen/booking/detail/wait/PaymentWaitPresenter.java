package com.daily.dailyhotel.screen.booking.detail.wait;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Booking;
import com.daily.dailyhotel.parcel.BookingParcel;
import com.daily.dailyhotel.repository.remote.BookingRemoteImpl;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.HappyTalkCategoryDialog;
import com.twoheart.dailyhotel.screen.information.FAQActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class PaymentWaitPresenter extends BaseExceptionPresenter<PaymentWaitActivity, PaymentWaitInterface> implements PaymentWaitView.OnEventListener
{
    private PaymentWaitAnalyticsInterface mAnalytics;

    private BookingRemoteImpl mBookingRemoteImpl;
    private Booking mBooking;

    public interface PaymentWaitAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onEventConciergeClick(Activity activity, Booking.PlaceType placeType);

        void onEventConciergeFaqClick(Activity activity, Booking.PlaceType placeType);

        void onEventConciergeHappyTalkClick(Activity activity, Booking.PlaceType placeType);

        void onEventConciergeHappyTalkClick2(Activity activity);

        void onEventConciergeCallClick(Activity activity, Booking.PlaceType placeType);
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

        setAnalytics(new PaymentWaitAnalyticsImpl());

        mBookingRemoteImpl = new BookingRemoteImpl(getActivity());

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (PaymentWaitAnalyticsInterface) analytics;
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
        if (mBooking == null)
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
        startActivityForResult(FAQActivity.newInstance(getActivity()), PaymentWaitActivity.REQUEST_CODE_FAQ);

        mAnalytics.onEventConciergeFaqClick(getActivity(), placeType);
    }

    @Override
    public void onConciergeHappyTalkClick(Booking.PlaceType placeType)
    {
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

    }
}
