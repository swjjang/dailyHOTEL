package com.daily.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.adapter.rxjava2.HttpException;

public abstract class BasePresenter<T1 extends BaseActivity, T2 extends BaseViewInterface> implements BaseActivityInterface
{
    private T1 mActivity;

    private T2 mOnViewInterface;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public BasePresenter(@NonNull T1 activity)
    {
        mActivity = activity;

        mOnViewInterface = createInstanceViewInterface();

        initialize(activity);
    }

    protected abstract
    @NonNull
    T2 createInstanceViewInterface();

    public abstract void initialize(T1 activity);

    public abstract void setAnalytics(BaseAnalyticsInterface analytics);

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

    public void onHandleError(Throwable throwable)
    {
        if (throwable instanceof BaseException)
        {
            // 팝업 에러 보여주기
            BaseException baseException = (BaseException) throwable;


        } else if (throwable instanceof HttpException)
        {
            retrofit2.HttpException httpException = (HttpException) throwable;

            if (httpException.code() == BaseException.CODE_UNAUTHORIZED)
            {
            }
        } else
        {

        }
    }
}
