package com.daily.dailyhotel.base;

import android.content.DialogInterface;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogViewInterface;
import com.daily.base.BasePresenter;
import com.daily.base.exception.BaseException;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.repository.local.ConfigLocalImpl;
import com.daily.dailyhotel.repository.remote.FacebookRemoteImpl;
import com.daily.dailyhotel.repository.remote.KakaoRemoteImpl;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Util;

import retrofit2.HttpException;

public abstract class BaseExceptionPresenter<T1 extends BaseActivity, T2 extends BaseDialogViewInterface> extends BasePresenter<T1, T2>
{
    private boolean mIsRefresh;

    protected abstract void onRefresh(boolean showProgress);

    public BaseExceptionPresenter(@NonNull T1 activity)
    {
        super(activity);
    }

    protected void onHandleError(Throwable throwable)
    {
        unLockAll();

        onReportError(throwable);

        if (throwable instanceof BaseException)
        {
            // 팝업 에러 보여주기
            BaseException baseException = (BaseException) throwable;

            getViewInterface().showSimpleDialog(null, baseException.getMessage()//
                , getString(R.string.dialog_btn_text_confirm), null, null, null, null, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        if (getActivity().isFinishing() == true)
                        {
                            return;
                        }

                        getActivity().onBackPressed();
                    }
                }, true);
        } else if (throwable instanceof HttpException)
        {
            retrofit2.HttpException httpException = (HttpException) throwable;

            if (httpException.code() == BaseException.CODE_UNAUTHORIZED)
            {
                onHandleAuthorizedError();
            } else
            {
                DailyToast.showToast(getActivity(), getString(R.string.act_base_network_connect), DailyToast.LENGTH_LONG);
            }
        } else
        {
            DailyToast.showToast(getActivity(), getString(R.string.act_base_network_connect), DailyToast.LENGTH_LONG);
        }
    }

    protected void onReportError(Throwable throwable)
    {
        if (throwable == null)
        {
            return;
        }

        try
        {
            if (throwable instanceof BaseException)
            {
                BaseException baseException = (BaseException) throwable;
                Crashlytics.log("msgCode : " + baseException.getCode() + ", message : " + baseException.getMessage());
                Crashlytics.logException(throwable);

                ExLog.e("msgCode : " + baseException.getCode() + ", message : " + baseException.getMessage());
                ExLog.e(throwable.toString());
            } else if (throwable instanceof HttpException)
            {
                retrofit2.HttpException httpException = (HttpException) throwable;
                Crashlytics.log(httpException.response().raw().request().url().toString());
                Crashlytics.logException(throwable);

                ExLog.e(httpException.response().raw().request().url().toString());
                ExLog.e(throwable.toString());
            } else
            {
                Crashlytics.logException(throwable);

                ExLog.e(throwable.toString());
            }
        } catch (Exception e)
        {
            // 리포팅 하다가 죽지는 않겠죠?
        }
    }

    protected void onHandleErrorAndFinish(Throwable throwable)
    {
        unLockAll();

        if (throwable instanceof BaseException)
        {
            // 팝업 에러 보여주기
            BaseException baseException = (BaseException) throwable;

            getViewInterface().showSimpleDialog(null, baseException.getMessage()//
                , getString(R.string.dialog_btn_text_confirm), null, null, null, null, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        if (getActivity().isFinishing() == true)
                        {
                            return;
                        }

                        getActivity().onBackPressed();
                    }
                }, true);
        } else if (throwable instanceof HttpException)
        {
            retrofit2.HttpException httpException = (HttpException) throwable;

            if (httpException.code() == BaseException.CODE_UNAUTHORIZED)
            {
                onHandleAuthorizedError();
            } else
            {
                DailyToast.showToast(getActivity(), getString(R.string.act_base_network_connect), DailyToast.LENGTH_LONG);

                Crashlytics.log(httpException.response().raw().request().url().toString());
                Crashlytics.logException(throwable);

                getActivity().onBackPressed();
            }
        } else
        {
            DailyToast.showToast(getActivity(), getString(R.string.act_base_network_connect), DailyToast.LENGTH_LONG);

            getActivity().onBackPressed();
        }
    }

    private void onHandleAuthorizedError()
    {
        addCompositeDisposable(new ConfigLocalImpl(getActivity()).clear().subscribe(object -> {
            new FacebookRemoteImpl().logOut();
            new KakaoRemoteImpl().logOut();

            restartExpiredSession();
        }));
    }

    protected void restartExpiredSession()
    {
        DailyToast.showToast(getActivity(), R.string.dialog_msg_session_expired, DailyToast.LENGTH_SHORT);

        Util.restartApp(getActivity());
    }

    protected boolean isRefresh()
    {
        return mIsRefresh;
    }

    protected void setRefresh(boolean refresh)
    {
        mIsRefresh = refresh;
    }
}
