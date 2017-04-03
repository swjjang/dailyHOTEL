package com.daily.base;

import android.content.Intent;
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

    public void setContentView(@LayoutRes int layoutResID)
    {
        if (mOnViewInterface == null)
        {
            throw new NullPointerException("mOnViewInterface is null");
        } else
        {
            getActivity().setContentView(mOnViewInterface.onCreateView(layoutResID));
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
        if(disposable == null)
        {
            return;
        }

        mCompositeDisposable.add(disposable);
    }

    public void onObservableError(Throwable throwable)
    {
        if (throwable instanceof BaseException)
        {

        } else if (throwable instanceof HttpException)
        {
            if (((HttpException) throwable).code() == BaseException.CODE_UNAUTHORIZED)
            {

            }
        }
    }
}
