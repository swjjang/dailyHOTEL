package com.daily.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import com.daily.base.util.DailyLock;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BasePresenter<T1 extends BaseActivity, T2 extends BaseViewInterface> implements BaseActivityInterface
{
    private T1 mActivity;

    private T2 mOnViewInterface;

    private DailyLock mLock;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public BasePresenter(@NonNull T1 activity)
    {
        mActivity = activity;

        mLock = new DailyLock(activity);

        mOnViewInterface = createInstanceViewInterface();

        initialize(activity);
    }

    protected abstract
    @NonNull
    T2 createInstanceViewInterface();

    public abstract void initialize(T1 activity);

    public abstract void setAnalytics(BaseAnalyticsInterface analytics);

    protected abstract void onHandleError(Throwable throwable);

    public T1 getActivity()
    {
        return mActivity;
    }

    protected String getString(int resId)
    {
        return mActivity.getString(resId);
    }

    protected String getString(int resId, Object... formatArgs)
    {
        return mActivity.getString(resId, formatArgs);
    }

    public void setContentView(@LayoutRes int layoutResID)
    {
        if (mOnViewInterface == null)
        {
            throw new NullPointerException("mOnViewInterface is null");
        } else
        {
            mOnViewInterface.setContentView(layoutResID);
        }
    }

    public
    @NonNull
    T2 getViewInterface()
    {
        if (mOnViewInterface == null)
        {
            mOnViewInterface = createInstanceViewInterface();
        }

        return mOnViewInterface;
    }

    @Override
    public void onStart()
    {

    }

    @Override
    public void onResume()
    {

    }

    @Override
    public void onPause()
    {
    }

    @Override
    public void onDestroy()
    {
        getViewInterface().hideSimpleDialog();

        mCompositeDisposable.clear();
        mCompositeDisposable.dispose();
    }

    @Override
    public boolean onBackPressed()
    {
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {

    }

    protected void addCompositeDisposable(Disposable disposable)
    {
        if (disposable == null)
        {
            return;
        }

        mCompositeDisposable.add(disposable);
    }

    protected boolean isLock()
    {
        return mLock.isLock();
    }

    protected boolean lock()
    {
        return mLock.lock();
    }

    protected void unLock()
    {
        mLock.unLock();
    }

    protected boolean screenLock(boolean showProgress)
    {
        return mLock.screenLock(showProgress);
    }

    protected void screenUnLock()
    {
        mLock.screenUnLock();
    }

    //    protected void onHandleError(Throwable throwable)
    //    {
    //        screenUnLock();
    //
    //        if (throwable instanceof BaseException)
    //        {
    //            // 팝업 에러 보여주기
    //            BaseException baseException = (BaseException) throwable;
    //
    //            mOnViewInterface.showSimpleDialog(null, baseException.getMessage()//
    //                , getString(R.string.dialog_btn_text_confirm), null, null, null, null, new DialogInterface.OnDismissListener()
    //                {
    //                    @Override
    //                    public void onDismiss(DialogInterface dialog)
    //                    {
    //                        getActivity().onBackPressed();
    //                    }
    //                }, true);
    //        } else if (throwable instanceof HttpException)
    //        {
    //            retrofit2.HttpException httpException = (HttpException) throwable;
    //
    //            if (httpException.code() == BaseException.CODE_UNAUTHORIZED)
    //            {
    //                onHandleAuthorizedError();
    //            } else
    //            {
    //                if (Constants.DEBUG == false)
    //                {
    //                    Crashlytics.log(httpException.response().raw().request().url().toString());
    //                    Crashlytics.logException(throwable);
    //                }
    //            }
    //        } else
    //        {
    //            DailyToast.showToast(getActivity(), getString(R.string.act_base_network_connect), DailyToast.LENGTH_LONG);
    //        }
    //    }
    //
    //    private void onHandleAuthorizedError()
    //    {
    //        addCompositeDisposable(new ConfigLocalImpl(getActivity()).clear().subscribe(new Consumer()
    //        {
    //            @Override
    //            public void accept(Object o) throws Exception
    //            {
    //                new FacebookImpl().logOut();
    //                new KakaoImpl().logOut();
    //
    //                restartExpiredSession();
    //            }
    //        }));
    //    }
    //
    //    private void restartExpiredSession()
    //    {
    //        DailyToast.showToast(getActivity(), R.string.dialog_msg_session_expired, DailyToast.LENGTH_SHORT);
    //
    //        Util.restartApp(getActivity());
    //    }
}
