package com.daily.dailyhotel.screen.common.dialog.refund;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Surface;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.VersionUtils;
import com.daily.dailyhotel.base.BaseMultiWindowPresenter;
import com.twoheart.dailyhotel.R;

/**
 * Created by android_sam on 2018. 1. 10..
 */

public class AutoRefundDialogPresenter extends BaseMultiWindowPresenter<AutoRefundDialogActivity, AutoRefundDialogInterface> implements AutoRefundDialogView.OnEventListener
{
    private AutoRefundAnalyticsInterface mAnalytics;

    private int mCancelType;
    private String mCancelMessage;

    public interface AutoRefundAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public AutoRefundDialogPresenter(@NonNull AutoRefundDialogActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected AutoRefundDialogInterface createInstanceViewInterface()
    {
        return new AutoRefundDialogView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(AutoRefundDialogActivity activity)
    {
        setContentView(R.layout.dialog_type_refund_layout_data);

        mAnalytics = new AutoRefundDialogAnalyticsImpl();

        setRefresh(true);
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mCancelType = intent.getIntExtra(AutoRefundDialogActivity.INTENT_EXTRA_DATA_CANCEL_TYPE, -1);
        mCancelMessage = intent.getStringExtra(AutoRefundDialogActivity.INTENT_EXTRA_DATA_CANCEL_MESSAGE);

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setCancelType(mCancelType);
        getViewInterface().setEtcMessage(mCancelMessage);
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
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        if (getActivity() == null)
        {
            return;
        }

        int orientation;
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation)
        {
            orientation = Configuration.ORIENTATION_LANDSCAPE;
        } else
        {
            orientation = Configuration.ORIENTATION_PORTRAIT;
        }

        boolean isInMultiWindowMode = VersionUtils.isOverAPI24() == true && getActivity().isInMultiWindowMode();
        getViewInterface().onConfigurationChange(orientation, isInMultiWindowMode);
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode)
    {
        int orientation;
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation)
        {
            orientation = Configuration.ORIENTATION_LANDSCAPE;
        } else
        {
            orientation = Configuration.ORIENTATION_PORTRAIT;
        }

        //        ExLog.d("isInMultiWindowMode : " + isInMultiWindowMode + " , rotation orientation : " + orientation);
        getViewInterface().onConfigurationChange(orientation, isInMultiWindowMode);
    }

    @Override
    public void onNativeButtonClick()
    {
        getViewInterface().hideInputKeyboard();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public void onPositiveButtonClick(int position, String cancelReason, String message)
    {
        getViewInterface().hideInputKeyboard();

        Intent intent = new Intent();
        intent.putExtra(AutoRefundDialogActivity.INTENT_EXTRA_DATA_CANCEL_TYPE, position);
        intent.putExtra(AutoRefundDialogActivity.INTENT_EXTRA_DATA_CANCEL_TYPE_NAME, cancelReason);
        intent.putExtra(AutoRefundDialogActivity.INTENT_EXTRA_DATA_CANCEL_MESSAGE, message);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void checkConfigChange()
    {
        if (getActivity() == null)
        {
            return;
        }

        int orientation;
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation)
        {
            orientation = Configuration.ORIENTATION_LANDSCAPE;
        } else
        {
            orientation = Configuration.ORIENTATION_PORTRAIT;
        }

        boolean isInMultiWindowMode = VersionUtils.isOverAPI24() == true && getActivity().isInMultiWindowMode();
        getViewInterface().onConfigurationChange(orientation, isInMultiWindowMode);
    }
}
