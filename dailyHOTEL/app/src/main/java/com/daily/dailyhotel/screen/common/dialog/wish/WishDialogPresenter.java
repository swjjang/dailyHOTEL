package com.daily.dailyhotel.screen.common.dialog.wish;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.WishResult;
import com.daily.dailyhotel.repository.remote.GourmetRemoteImpl;
import com.daily.dailyhotel.repository.remote.StayOutboundRemoteImpl;
import com.daily.dailyhotel.repository.remote.StayRemoteImpl;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.Constants;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class WishDialogPresenter extends BaseExceptionPresenter<WishDialogActivity, WishDialogInterface> implements WishDialogView.OnEventListener
{
    private WishDialogAnalyticsInterface mAnalytics;

    private StayRemoteImpl mStayRemoteImpl;
    private GourmetRemoteImpl mGourmetRemoteImpl;
    private StayOutboundRemoteImpl mStayOutboundRemoteImpl;

    private Constants.ServiceType mServiceType;
    private int mWishIndex;
    boolean mWish;
    private String mCallByScreen;

    public interface WishDialogAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public WishDialogPresenter(@NonNull WishDialogActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected WishDialogInterface createInstanceViewInterface()
    {
        return new WishDialogView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(WishDialogActivity activity)
    {
        setContentView(R.layout.activity_wish_dialog_data);

        mAnalytics = new WishDialogAnalyticsImpl();

        mStayRemoteImpl = new StayRemoteImpl();
        mGourmetRemoteImpl = new GourmetRemoteImpl();
        mStayOutboundRemoteImpl = new StayOutboundRemoteImpl();

        setRefresh(true);
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mServiceType = Constants.ServiceType.valueOf(intent.getStringExtra(WishDialogActivity.INTENT_EXTRA_DATA_SERVICE_TYPE));
        mWishIndex = intent.getIntExtra(WishDialogActivity.INTENT_EXTRA_DATA_WISH_INDEX, 0);
        mWish = intent.getBooleanExtra(WishDialogActivity.INTENT_EXTRA_DATA_MY_WISH, false);
        mCallByScreen = intent.getStringExtra(WishDialogActivity.INTENT_EXTRA_DATA_CALL_SCREEN);

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        if (DailyHotel.isLogin() == false)
        {
            setRefresh(false);

            DailyToast.showToast(getActivity(), R.string.toast_msg_please_login, DailyToast.LENGTH_LONG);

            Intent intent = LoginActivity.newInstance(getActivity(), mCallByScreen);
            startActivityForResult(intent, WishDialogActivity.REQUEST_CODE_LOGIN);
        }
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
        return true;
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
            case WishDialogActivity.REQUEST_CODE_LOGIN:
                if (resultCode == Activity.RESULT_OK)
                {
                    setRefresh(true);

                    setResult(BaseActivity.RESULT_CODE_REFRESH);
                } else
                {
                    screenLock(false);

                    addCompositeDisposable(Observable.just(true).delaySubscription(300, TimeUnit.MILLISECONDS).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                    {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception
                        {
                            finish();
                        }
                    }));
                }
                break;
        }
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

        Observable<WishResult> wishResultObservable = null;

        switch (mServiceType)
        {
            case HOTEL:
                wishResultObservable = mWish ? mStayRemoteImpl.addWish(mWishIndex) : mStayRemoteImpl.removeWish(mWishIndex);
                break;

            case GOURMET:
                wishResultObservable = mWish ? mGourmetRemoteImpl.addWish(mWishIndex) : mGourmetRemoteImpl.removeWish(mWishIndex);
                break;

            case OB_STAY:
                wishResultObservable = mWish ? mStayOutboundRemoteImpl.addWish(getActivity(), mWishIndex) : mStayOutboundRemoteImpl.removeWish(getActivity(), mWishIndex);
                break;
        }

        if (wishResultObservable == null)
        {
            finish();
            return;
        }

        addCompositeDisposable(wishResultObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<WishResult>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull WishResult wishResult) throws Exception
            {
                showWishAnimation(wishResult, mWish);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
            {
                onHandleError(throwable);

                // 로그인 후에 변경시에는 전체 리플래쉬가 되어야 한다.
                Intent intent = new Intent();
                intent.putExtra(WishDialogActivity.INTENT_EXTRA_DATA_WISH, !mWish);

                setResult(BaseActivity.RESULT_CODE_ERROR, intent);

                finish();
            }
        }));
    }

    @Override
    public void onBackClick()
    {
    }

    @Override
    protected void setResult(int resultCode)
    {
        if (getResultCode() == BaseActivity.RESULT_CODE_REFRESH)
        {
            super.setResult(BaseActivity.RESULT_CODE_REFRESH);
        } else
        {
            super.setResult(resultCode);
        }
    }

    @Override
    protected void setResult(int resultCode, Intent resultData)
    {
        if (getResultCode() == BaseActivity.RESULT_CODE_REFRESH)
        {
            super.setResult(BaseActivity.RESULT_CODE_REFRESH, resultData);
        } else
        {
            super.setResult(resultCode, resultData);
        }
    }

    void showWishAnimation(WishResult wishResult, boolean wish)
    {
        if (wishResult == null)
        {
            finish();
            return;
        }

        screenLock(false);

        if (wishResult.success == true)
        {
            Observable<Boolean> observable = getViewInterface().showWishView(wish);

            if (observable != null)
            {
                addCompositeDisposable(observable.subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Boolean aBoolean) throws Exception
                    {
                        // 로그인 후에 변경시에는 전체 리플래쉬가 되어야 한다.
                        Intent intent = new Intent();
                        intent.putExtra(WishDialogActivity.INTENT_EXTRA_DATA_WISH, mWish);

                        setResult(Activity.RESULT_OK, intent);

                        finish();
                    }
                }));
            } else
            {
                finish();
            }
        } else
        {
            getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), wishResult.message//
                , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        finish();
                    }
                });

            unLockAll();
        }
    }
}
