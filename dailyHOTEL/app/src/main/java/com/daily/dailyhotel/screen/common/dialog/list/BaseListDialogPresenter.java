package com.daily.dailyhotel.screen.common.dialog.list;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Surface;

import com.daily.base.util.ExLog;
import com.daily.base.util.VersionUtils;
import com.daily.dailyhotel.base.BaseMultiWindowPresenter;
import com.daily.dailyhotel.parcel.ListDialogItemParcel;
import com.twoheart.dailyhotel.R;

import java.util.ArrayList;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class BaseListDialogPresenter extends BaseMultiWindowPresenter<BaseListDialogActivity, BaseListDialogInterface.ViewInterface> implements BaseListDialogInterface.OnEventListener
{
    private BaseListDialogInterface.AnalyticsInterface mAnalytics;

    private String mTitleText;
    private ListDialogItemParcel mSelectedItem;
    private ArrayList<ListDialogItemParcel> mList;
    private String mAnalyticsScreenName;

    public BaseListDialogPresenter(@NonNull BaseListDialogActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected BaseListDialogInterface.ViewInterface createInstanceViewInterface()
    {
        return new BaseListDialogView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(BaseListDialogActivity activity)
    {
        setContentView(R.layout.dialog_list_data);

        mAnalytics = new BaseListDialogAnalyticsImpl();

        setRefresh(true);
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mTitleText = intent.getStringExtra(BaseListDialogActivity.INTENT_EXTRA_DATA_TITLE);

        ArrayList<ListDialogItemParcel> list = intent.getParcelableArrayListExtra(BaseListDialogActivity.INTENT_EXTRA_DATA_LIST);
        if (list == null || list.size() == 0)
        {
            return false;
        }

        mList = list;

        mSelectedItem = intent.getParcelableExtra(BaseListDialogActivity.INTENT_EXTRA_DATA_SELECTED_DATA);

        mAnalyticsScreenName = intent.getStringExtra(BaseListDialogActivity.INTENT_EXTRA_DATA_ANALYTICS_SCREEN);

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(mTitleText);
        getViewInterface().setData(mSelectedItem, mList);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefresh(true);
        }

        try
        {
            mAnalytics.onScreen(getActivity(), mAnalyticsScreenName);
        } catch (Exception e)
        {
            ExLog.d(e.getMessage());
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
        getViewInterface().setRootViewVisible(false);
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
        setResult(Activity.RESULT_CANCELED);
        getViewInterface().setRootViewVisible(false);
        finish();
    }

    @Override
    public void onPositiveButtonClick(ListDialogItemParcel selectedItem)
    {
        mSelectedItem = selectedItem;

        Intent intent = new Intent();
        intent.putExtra(BaseListDialogActivity.INTENT_EXTRA_DATA_SELECTED_DATA, selectedItem);

        setResult(Activity.RESULT_OK, intent);
        getViewInterface().setRootViewVisible(false);
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
