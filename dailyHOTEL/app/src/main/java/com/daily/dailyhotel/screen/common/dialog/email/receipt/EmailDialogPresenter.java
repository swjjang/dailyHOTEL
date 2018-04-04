package com.daily.dailyhotel.screen.common.dialog.email.receipt;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class EmailDialogPresenter extends BaseExceptionPresenter<EmailDialogActivity, EmailDialogInterface> implements EmailDialogView.OnEventListener
{
    private EmailDialogAnalyticsInterface mAnalytics;

    private String mEmail;

    public interface EmailDialogAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public EmailDialogPresenter(@NonNull EmailDialogActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected EmailDialogInterface createInstanceViewInterface()
    {
        return new EmailDialogView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(EmailDialogActivity activity)
    {
        setContentView(0);

        mAnalytics = new EmailDialogAnalyticsImpl();

        setRefresh(true);
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mEmail = intent.getStringExtra(EmailDialogActivity.INTENT_EXTRA_DATA_EMAIL);

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
        //        screenLock(showProgress);

        showEmailDialog();
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    private void showEmailDialog()
    {
        getViewInterface().showEmailDialog(mEmail, new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialogInterface)
            {
                setResult(Activity.RESULT_CANCELED);
                onBackClick();
            }
        });
    }

    @Override
    public void onSendEmailClick(String email)
    {
        if (DailyTextUtils.isTextEmpty(email) == true)
        {
            DailyToast.showToast(getActivity(), R.string.toast_msg_please_input_email, DailyToast.LENGTH_SHORT);
            return;
        }

        if (DailyTextUtils.validEmail(email) == false)
        {
            DailyToast.showToast(getActivity(), R.string.toast_msg_wrong_email_address, DailyToast.LENGTH_SHORT);
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EmailDialogActivity.INTENT_EXTRA_DATA_EMAIL, email);

        setResult(Activity.RESULT_OK, intent);
        onBackClick();
    }
}
