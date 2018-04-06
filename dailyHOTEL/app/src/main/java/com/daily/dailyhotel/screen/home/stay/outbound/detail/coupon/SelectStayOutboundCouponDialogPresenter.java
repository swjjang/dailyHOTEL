package com.daily.dailyhotel.screen.home.stay.outbound.detail.coupon;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Coupon;
import com.daily.dailyhotel.entity.Coupons;
import com.daily.dailyhotel.entity.DownloadCouponResult;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.repository.remote.CouponRemoteImpl;
import com.twoheart.dailyhotel.R;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SelectStayOutboundCouponDialogPresenter//
    extends BaseExceptionPresenter<SelectStayOutboundCouponDialogActivity, SelectStayOutboundCouponDialogInterface.ViewInterface>//
    implements SelectStayOutboundCouponDialogInterface.OnEventListener
{
    private SelectStayOutboundCouponDialogInterface.AnalyticsInterface mAnalytics;

    CouponRemoteImpl mCouponRemoteImpl;

    int mStayIndex;
    String mStayName;
    StayBookDateTime mStayBookDateTime;
    String[] mVendorType;

    public SelectStayOutboundCouponDialogPresenter(@NonNull SelectStayOutboundCouponDialogActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected SelectStayOutboundCouponDialogInterface.ViewInterface createInstanceViewInterface()
    {
        return new SelectStayOutboundCouponDialogView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(SelectStayOutboundCouponDialogActivity activity)
    {
        setContentView(R.layout.activity_select_coupon_dialog_data);

        mAnalytics = new SelectStayOutboundCouponDialogAnalyticsImpl();

        mCouponRemoteImpl = new CouponRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mStayIndex = intent.getIntExtra(SelectStayOutboundCouponDialogActivity.INTENT_EXTRA_DATA_STAY_INDEX, -1);

        if (mStayIndex <= 0)
        {
            return false;
        }

        mStayName = intent.getStringExtra(SelectStayOutboundCouponDialogActivity.INTENT_EXTRA_DATA_STAY_NAME);

        try
        {
            String checkInDateTime = intent.getStringExtra(SelectStayOutboundCouponDialogActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME);
            String checkOutDateTime = intent.getStringExtra(SelectStayOutboundCouponDialogActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME);

            mStayBookDateTime = new StayBookDateTime(checkInDateTime, checkOutDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            return false;
        }

        mVendorType = intent.getStringArrayExtra(SelectStayOutboundCouponDialogActivity.INTENT_EXTRA_DATA_VENDOR_TYPE);

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

        final String DATE_FORMAT = "yyyy-MM-dd";

        addCompositeDisposable(mCouponRemoteImpl.getStayOutboundCouponListByDetail(mStayBookDateTime.getCheckInDateTime(DATE_FORMAT)//
            , mStayBookDateTime.getCheckOutDateTime(DATE_FORMAT), mStayIndex, mVendorType).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Coupons>()
        {
            @Override
            public void accept(Coupons coupons) throws Exception
            {
                if (coupons == null || coupons.coupons == null || coupons.coupons.size() == 0)
                {
                    getViewInterface().setVisible(false);

                    getViewInterface().showSimpleDialog(null, getString(R.string.message_select_coupon_empty), //
                        getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialog)
                            {
                                finish();
                            }
                        });

                } else
                {
                    getViewInterface().setVisible(true);

                    String title = getString(hasDownLoadableCoupons(coupons.coupons) ? R.string.coupon_download_coupon : R.string.coupon_dont_download_coupon);

                    getViewInterface().setCouponListDialog(title, coupons.coupons, new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            onBackClick();
                        }
                    });
                }

                unLockAll();
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

    boolean hasDownLoadableCoupons(List<Coupon> couponList)
    {
        if (couponList == null || couponList.size() == 0)
        {
            return false;
        }

        for (Coupon coupon : couponList)
        {
            if (coupon.isDownloaded == false)
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onDownloadCouponClick(Coupon coupon)
    {
        if (coupon == null || lock() == true)
        {
            return;
        }

        // 쿠폰 다운로드 시도!
        addCompositeDisposable(mCouponRemoteImpl.getDownloadCoupon(coupon.couponCode).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<DownloadCouponResult>()
        {
            @Override
            public void accept(DownloadCouponResult downloadCouponResult) throws Exception
            {
                setRefresh(true);
                onRefresh(true);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                onHandleError(throwable);
            }
        }));
    }
}
