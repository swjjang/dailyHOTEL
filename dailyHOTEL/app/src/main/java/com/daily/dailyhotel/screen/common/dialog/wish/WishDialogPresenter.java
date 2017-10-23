package com.daily.dailyhotel.screen.common.dialog.wish;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.WishResult;
import com.daily.dailyhotel.repository.remote.GourmetRemoteImpl;
import com.daily.dailyhotel.repository.remote.StayRemoteImpl;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.Constants;

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

    private Constants.ServiceType mServiceType;
    private int mPlaceIndex;
    private boolean mWish;
    private int mPositionByList;
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

        setAnalytics(new WishDialogAnalyticsImpl());

        mStayRemoteImpl = new StayRemoteImpl(activity);
        mGourmetRemoteImpl = new GourmetRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (WishDialogAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mServiceType = Constants.ServiceType.valueOf(intent.getStringExtra(WishDialogActivity.INTENT_EXTRA_DATA_SERVICE_TYPE));
        mPlaceIndex = intent.getIntExtra(WishDialogActivity.INTENT_EXTRA_DATA_PLACE_INDEX, 0);
        mWish = intent.getBooleanExtra(WishDialogActivity.INTENT_EXTRA_DATA_MY_WISH, false);
        mPositionByList = intent.getIntExtra(WishDialogActivity.INTENT_EXTRA_DATA_POSITION, -1);
        mCallByScreen = intent.getStringExtra(WishDialogActivity.INTENT_EXTRA_DATA_CALL_SCREEN);

        return true;
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

        switch (requestCode)
        {
            case WishDialogActivity.REQUEST_CODE_LOGIN:
                if (resultCode == Activity.RESULT_OK)
                {
                    setRefresh(true);
                    onRefresh(true);
                } else
                {
                    onBackClick();
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

        Observable<WishResult> wishResultObservable = null;

        switch (mServiceType)
        {
            case GOURMET:
                wishResultObservable = mWish ? mStayRemoteImpl.addWish(mPlaceIndex) : mStayRemoteImpl.removeWish(mPlaceIndex);
                break;

            case HOTEL:
                wishResultObservable = mWish ? mGourmetRemoteImpl.addWish(mPlaceIndex) : mGourmetRemoteImpl.removeWish(mPlaceIndex);
                break;

            case OB_STAY:
                break;
        }

        if (wishResultObservable == null)
        {
            onBackClick();
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
            }
        }));
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    private void showWishAnimation(WishResult wishResult, boolean wish)
    {
        if (wishResult == null)
        {
            onBackClick();
            return;
        }

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
                        unLockAll();

                        Intent intent = new Intent();
                        intent.putExtra(WishDialogActivity.INTENT_EXTRA_DATA_POSITION, mPositionByList);

                        setResult(Activity.RESULT_OK, intent);
                        onBackClick();
                    }
                }));
            } else
            {
                unLockAll();

                onBackClick();
            }
        } else
        {
            getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), wishResult.message//
                , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        onBackClick();
                    }
                });

            unLockAll();
        }
    }
}