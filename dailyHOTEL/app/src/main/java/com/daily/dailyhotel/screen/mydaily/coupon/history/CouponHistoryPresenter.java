package com.daily.dailyhotel.screen.mydaily.coupon.history;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Coupon;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.repository.remote.CouponRemoteImpl;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by android_sam on 2017. 9. 28..
 */

public class CouponHistoryPresenter extends BaseExceptionPresenter<CouponHistoryActivity, CouponHistoryViewInterface> implements CouponHistoryView.OnEventListener
{
    private CouponHistoryAnalyticsInterface mAnalytics;

    private CouponRemoteImpl mCouponRemoteImpl;

    public interface CouponHistoryAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity);
    }

    public CouponHistoryPresenter(@NonNull CouponHistoryActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected CouponHistoryViewInterface createInstanceViewInterface()
    {
        return new CouponHistoryView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(CouponHistoryActivity activity)
    {
        setContentView(R.layout.activity_coupon_history_data);

        mAnalytics = new CouponHistoryAnalyticsImpl();

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

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        //        getViewInterface().setToolbarTitle(getActivity().getResources().getString(R.string.actionbar_title_coupon_history)); // 뷰 자체에서 생성
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefresh(true);
        }

        mAnalytics.onScreen(getActivity());
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

        addCompositeDisposable(mCouponRemoteImpl.getCouponHistoryList() //
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Coupon>>()
            {
                @Override
                public void accept(List<Coupon> coupons) throws Exception
                {
                    List<ObjectItem> objectItemList = new ArrayList<>(coupons.size() + 1);

                    for (Coupon coupon : coupons)
                    {
                        objectItemList.add(new ObjectItem(ObjectItem.TYPE_ENTRY, coupon));
                    }

                    objectItemList.add(new ObjectItem(ObjectItem.TYPE_FOOTER_VIEW, null));

                    getViewInterface().setData(objectItemList);

                    unLockAll();
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

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onHomeClick()
    {
        setResult(Constants.CODE_RESULT_ACTIVITY_GO_HOME);
        finish();
    }
}
