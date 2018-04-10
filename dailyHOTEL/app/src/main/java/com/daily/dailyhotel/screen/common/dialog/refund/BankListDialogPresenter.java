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
import com.daily.dailyhotel.entity.Bank;
import com.daily.dailyhotel.parcel.BankParcel;
import com.daily.dailyhotel.repository.remote.RefundRemoteImpl;
import com.twoheart.dailyhotel.R;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class BankListDialogPresenter extends BaseMultiWindowPresenter<BankListDialogActivity, BankListDialogInterface> implements BankListDialogView.OnEventListener
{
    private BankListDialogAnalyticsInterface mAnalytics;
    private RefundRemoteImpl mRefundRemoteImpl;
    private Bank mSelectedBank;
    private List<Bank> mBankList;

    public interface BankListDialogAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public BankListDialogPresenter(@NonNull BankListDialogActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected BankListDialogInterface createInstanceViewInterface()
    {
        return new BankListDialogView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(BankListDialogActivity activity)
    {
        setContentView(R.layout.dialog_refund_bank_list_data);

        mAnalytics = new BankListDialogAnalyticsImpl();

        mRefundRemoteImpl = new RefundRemoteImpl();

        setRefresh(true);
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        BankParcel bankParcel = intent.getParcelableExtra(BankListDialogActivity.INTENT_EXTRA_DATA_SELECTED_BANK);
        mSelectedBank = bankParcel == null ? null : bankParcel.getBank();

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

        addCompositeDisposable(mRefundRemoteImpl.getBankList().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Bank>>()
        {
            @Override
            public void accept(List<Bank> bankList) throws Exception
            {
                unLockAll();

                setBankList(bankList);
                notifyBankList();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                onHandleErrorAndFinish(throwable);
            }
        }));
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
        finish();
    }

    @Override
    public void onPositiveButtonClick(Bank selectedBank)
    {
        Intent intent = new Intent();
        intent.putExtra(BankListDialogActivity.INTENT_EXTRA_DATA_SELECTED_BANK, new BankParcel(selectedBank));

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

    void setBankList(List<Bank> bankList)
    {
        mBankList = bankList;
    }

    void notifyBankList()
    {
        getViewInterface().setBankList(mSelectedBank, mBankList);
    }
}
