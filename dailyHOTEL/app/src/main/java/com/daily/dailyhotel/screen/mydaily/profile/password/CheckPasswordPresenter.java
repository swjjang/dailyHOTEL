package com.daily.dailyhotel.screen.mydaily.profile.password;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class CheckPasswordPresenter //
    extends BaseExceptionPresenter<CheckPasswordActivity, CheckPasswordInterface> //
    implements CheckPasswordView.OnEventListener
{
    private CheckPasswordAnalyticsInterface mAnalytics;

    private ProfileRemoteImpl mProfileRemoteImpl;

    public interface CheckPasswordAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity);
    }

    public CheckPasswordPresenter(@NonNull CheckPasswordActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected CheckPasswordInterface createInstanceViewInterface()
    {
        return new CheckPasswordView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(CheckPasswordActivity activity)
    {
        setContentView(R.layout.activity_check_password_data);

        mAnalytics = new CheckPasswordAnalyticsImpl();

        mProfileRemoteImpl = new ProfileRemoteImpl(activity);

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

        if (DailyHotel.isLogin() == false)
        {
            restartExpiredSession();
            return;
        }

        if (isRefresh() == true)
        {
            onRefresh(true);
        }

        try
        {
            mAnalytics.onScreen(getActivity());
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
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onConfirmClick(String password)
    {
        if (getActivity() == null || DailyTextUtils.isTextEmpty(password))
        {
            return;
        }

        if (lock() == true)
        {
            return;
        }

        screenLock(true);

        addCompositeDisposable(mProfileRemoteImpl.getCheckPassword(password) //
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
            {
                @Override
                public void accept(Boolean aBoolean) throws Exception
                {
                    if (aBoolean == true)
                    {
                        setResult(Activity.RESULT_OK);
                        finish();
                        return;
                    }

                    DailyToast.showToast(getActivity(), R.string.message_check_password_error, DailyToast.LENGTH_SHORT);
                    unLockAll();
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    if (throwable instanceof BaseException)
                    {
                        BaseException baseException = (BaseException) throwable;
                        String message = baseException.getMessage();

                        if (DailyTextUtils.isTextEmpty(message))
                        {
                            message = getString(R.string.message_check_password_error);
                        }

                        DailyToast.showToast(getActivity(), message, DailyToast.LENGTH_SHORT);
                        unLockAll();
                    } else
                    {
                        onHandleError(throwable);
                    }
                }
            }));
    }

}
