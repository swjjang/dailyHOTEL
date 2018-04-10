package com.daily.dailyhotel.screen.common.dialog.call;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;

import java.util.Calendar;

import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class CallDialogPresenter extends BaseExceptionPresenter<CallDialogActivity, CallDialogInterface> implements CallDialogView.OnEventListener
{
    private CallDialogAnalyticsInterface mAnalytics;

    private CommonRemoteImpl mCommonRemoteImpl;

    public interface CallDialogAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public CallDialogPresenter(@NonNull CallDialogActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected CallDialogInterface createInstanceViewInterface()
    {
        return new CallDialogView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(CallDialogActivity activity)
    {
        setContentView(0);

        mAnalytics = new CallDialogAnalyticsImpl();

        mCommonRemoteImpl = new CommonRemoteImpl();

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

        addCompositeDisposable(mCommonRemoteImpl.getCommonDateTime()//
            .subscribe(commonDateTime -> {
                onCommonDateTime(commonDateTime);

                screenUnLock();
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    CallDialogPresenter.this.onHandleErrorAndFinish(throwable);
                }
            }));
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onCallClick()
    {
        String remoteConfigPhoneNumber = DailyRemoteConfigPreference.getInstance(getActivity()).getRemoteConfigCompanyPhoneNumber();
        String phoneNumber = DailyTextUtils.isTextEmpty(remoteConfigPhoneNumber) == false //
            ? remoteConfigPhoneNumber : Constants.PHONE_NUMBER_DAILYHOTEL;

        String noCallMessage = getString(R.string.toast_msg_no_call_format, phoneNumber);

        if (Util.isTelephonyEnabled(getActivity()) == true)
        {
            try
            {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));

                setResult(Activity.RESULT_OK);
            } catch (ActivityNotFoundException e)
            {
                DailyToast.showToast(getActivity(), noCallMessage, DailyToast.LENGTH_LONG);
            }
        } else
        {
            DailyToast.showToast(getActivity(), noCallMessage, DailyToast.LENGTH_LONG);
        }

        onBackClick();
    }

    private void onCommonDateTime(@NonNull CommonDateTime commonDateTime)
    {
        boolean isClosedOperatingTime = false;

        if (commonDateTime == null)
        {
            return;
        }

        try
        {
            Calendar todayCalendar = DailyCalendar.getInstance(commonDateTime.currentDateTime, false);
            int hour = todayCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = todayCalendar.get(Calendar.MINUTE);

            String startHourString = DailyCalendar.convertDateFormatString(commonDateTime.openDateTime, DailyCalendar.ISO_8601_FORMAT, "H");
            String endHourString = DailyCalendar.convertDateFormatString(commonDateTime.closeDateTime, DailyCalendar.ISO_8601_FORMAT, "H");

            int startHour = Integer.parseInt(startHourString);
            int endHour = Integer.parseInt(endHourString);

            String[] lunchTimes = DailyRemoteConfigPreference.getInstance(getActivity()).getRemoteConfigOperationLunchTime().split("\\,");
            String[] startLunchTime = lunchTimes[0].split(":");
            String[] endLunchTime = lunchTimes[1].split(":");

            int startLunchHour = Integer.parseInt(startLunchTime[0]);
            int startLunchMinute = Integer.parseInt(startLunchTime[1]);
            int endLunchHour = Integer.parseInt(endLunchTime[0]);

            boolean isOverStartTime = hour > startLunchHour || (hour == startLunchHour && minute >= startLunchMinute);
            boolean isOverEndTime = hour >= endLunchHour;

            if (hour < startHour && hour > endHour)
            {
                // 운영 안하는 시간 03:00:01 ~ 08:59:59 - 팝업 발생
                isClosedOperatingTime = true;
            } else if (isOverStartTime == true && isOverEndTime == false)
            {
                // 점심시간 11:50:01~12:59:59 - 해피톡의 경우 팝업 발생 안함
                isClosedOperatingTime = true;
            }
        } catch (Exception e)
        {
            isClosedOperatingTime = false;
        }

        if (isClosedOperatingTime == true)
        {
            showClosedTimeDialog();
        } else
        {
            showCallDialog();
        }
    }

    private void showCallDialog()
    {
        String[] hour = DailyPreference.getInstance(getActivity()).getOperationTime().split("\\,");
        String startHour = hour[0];
        String endHour = hour[1];

        String[] lunchTimes = DailyRemoteConfigPreference.getInstance(getActivity()).getRemoteConfigOperationLunchTime().split("\\,");
        String startLunchTime = lunchTimes[0];
        String endLunchTime = lunchTimes[1];

        String message = getString(R.string.dialog_msg_call) //
            + "\n" + getString(R.string.message_consult02, startHour, endHour, startLunchTime, endLunchTime);

        getViewInterface().showCallDialog(message, new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                setResult(Activity.RESULT_CANCELED);

                onBackClick();
            }
        });
    }

    private void showClosedTimeDialog()
    {
        String[] hour = DailyPreference.getInstance(getActivity()).getOperationTime().split("\\,");
        String startHour = hour[0];
        String endHour = hour[1];

        String[] lunchTimes = DailyRemoteConfigPreference.getInstance(getActivity()).getRemoteConfigOperationLunchTime().split("\\,");
        String startLunchTime = lunchTimes[0];
        String endLunchTime = lunchTimes[1];

        // 우선 점심시간의 경우 로컬에서 시간 픽스
        String message = getString(R.string.dialog_message_none_operating_time, startHour, endHour, startLunchTime, endLunchTime);

        getViewInterface().showClosedTimeDialog(message, new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                onBackClick();
            }
        });
    }
}
