package com.daily.dailyhotel.screen.common.dialog.call.restaurant;


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
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Util;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class RestaurantCallDialogPresenter extends BaseExceptionPresenter<RestaurantCallDialogActivity, RestaurantCallDialogInterface> implements RestaurantCallDialogView.OnEventListener
{
    private RestaurantCallDialogAnalyticsInterface mAnalytics;

    private String mPhone;

    public interface RestaurantCallDialogAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public RestaurantCallDialogPresenter(@NonNull RestaurantCallDialogActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected RestaurantCallDialogInterface createInstanceViewInterface()
    {
        return new RestaurantCallDialogView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(RestaurantCallDialogActivity activity)
    {
        setContentView(0);

        mAnalytics = new RestaurantCallDialogAnalyticsImpl();

        setRefresh(true);
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mPhone = intent.getStringExtra(RestaurantCallDialogActivity.INTENT_EXTRA_DATA_PHONE);

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

        showCallDialog();
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onCallClick()
    {
        if (DailyTextUtils.isTextEmpty(mPhone) == true)
        {
            onBackClick();
            return;
        }

        String noCallMessage = getString(R.string.toast_msg_no_gourmet_call, mPhone);

        if (Util.isTelephonyEnabled(getActivity()) == true)
        {
            try
            {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mPhone)));

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

    private void showCallDialog()
    {
        String message = getString(R.string.dialog_msg_direct_call_gourmet);

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
}
