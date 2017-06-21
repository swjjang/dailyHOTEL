package com.daily.dailyhotel.base;

import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.daily.base.BaseActivity;
import com.daily.base.BaseException;
import com.daily.base.BasePresenter;
import com.daily.base.BaseViewInterface;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.repository.local.ConfigLocalImpl;
import com.daily.dailyhotel.repository.remote.FacebookImpl;
import com.daily.dailyhotel.repository.remote.KakaoImpl;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Util;

import retrofit2.HttpException;

public abstract class BaseExceptionPresenter<T1 extends BaseActivity, T2 extends BaseViewInterface> extends BasePresenter<T1, T2>
{
    public BaseExceptionPresenter(@NonNull T1 activity)
    {
        super(activity);
    }

    protected void onHandleError(Throwable throwable)
    {
        screenUnLock();

        if (throwable instanceof BaseException)
        {
            // 팝업 에러 보여주기
            BaseException baseException = (BaseException) throwable;

            getViewInterface().showSimpleDialog(null, baseException.getMessage()//
                , getString(R.string.dialog_btn_text_confirm), null, null, null, null, dialogInterface -> getActivity().onBackPressed(), true);
        } else if (throwable instanceof HttpException)
        {
            retrofit2.HttpException httpException = (HttpException) throwable;

            if (httpException.code() == BaseException.CODE_UNAUTHORIZED)
            {
                onHandleAuthorizedError();
            } else
            {
                Crashlytics.log(httpException.response().raw().request().url().toString());
                Crashlytics.logException(throwable);
            }
        } else
        {
            DailyToast.showToast(getActivity(), getString(R.string.act_base_network_connect), DailyToast.LENGTH_LONG);
        }
    }

    private void onHandleAuthorizedError()
    {
        addCompositeDisposable(new ConfigLocalImpl(getActivity()).clear().subscribe(object ->
        {
            new FacebookImpl().logOut();
            new KakaoImpl().logOut();

            restartExpiredSession();
        }));
    }

    private void restartExpiredSession()
    {
        DailyToast.showToast(getActivity(), R.string.dialog_msg_session_expired, DailyToast.LENGTH_SHORT);

        Util.restartApp(getActivity());
    }
}
