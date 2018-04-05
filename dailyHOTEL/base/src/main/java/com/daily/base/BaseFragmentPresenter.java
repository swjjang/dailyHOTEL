package com.daily.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseFragmentPresenter<T1 extends BaseFragment, T2 extends BaseFragmentDialogViewInterface> implements BaseFragmentInterface
{
    private T1 mFragment;

    private T2 mOnViewInterface;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public BaseFragmentPresenter(@NonNull T1 fragment)
    {
        mFragment = fragment;

        mOnViewInterface = createInstanceViewInterface();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        mOnViewInterface.setActivity(getActivity());

        constructorInitialize(getActivity());
    }

    protected abstract @NonNull
    T2 createInstanceViewInterface();

    public abstract void constructorInitialize(BaseActivity baseActivity);

    public abstract void setAnalytics(BaseAnalyticsInterface analytics);

    public BaseActivity getActivity()
    {
        return (BaseActivity) mFragment.getActivity();
    }

    public T1 getFragment()
    {
        return mFragment;
    }

    public BasePresenter getActivityPresenter()
    {
        return getActivity() == null ? null : getActivity().getPresenter();
    }

    protected String getString(int resId)
    {
        return mFragment.getString(resId);
    }

    protected String getString(int resId, Object... formatArgs)
    {
        return mFragment.getString(resId, formatArgs);
    }

    public @NonNull
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

        disposeCompositeDisposable();
    }

    protected void startActivity(Intent intent)
    {
        mFragment.startActivity(intent);
    }

    protected void startActivityForResult(Intent intent, int requestCode)
    {
        mFragment.startActivityForResult(intent, requestCode);
    }

    protected void startActivityForResult(Intent intent, int requestCode, Bundle options)
    {
        mFragment.startActivityForResult(intent, requestCode, options);
    }

    protected void startActivity(Activity activity, Intent intent)
    {
        if (activity == null)
        {
            return;
        }

        activity.startActivity(intent);
    }

    protected void startActivityForResult(Activity activity, Intent intent, int requestCode)
    {
        if (activity == null)
        {
            return;
        }

        activity.startActivityForResult(intent, requestCode);
    }

    protected void addCompositeDisposable(Disposable disposable)
    {
        if (disposable == null)
        {
            return;
        }

        mCompositeDisposable.add(disposable);
    }

    protected void removeCompositeDisposable(Disposable disposable)
    {
        if (disposable == null)
        {
            return;
        }

        mCompositeDisposable.remove(disposable);
    }

    protected void clearCompositeDisposable()
    {
        mCompositeDisposable.clear();
    }

    private void disposeCompositeDisposable()
    {
        mCompositeDisposable.dispose();
    }

    protected void onHandleError(Throwable throwable)
    {
        getActivityPresenter().onHandleError(throwable);
    }

    protected boolean isLock()
    {
        return getActivityPresenter().isLock();
    }

    protected boolean isScreenLock()
    {
        return getActivityPresenter().isScreenLock();
    }

    protected boolean lock()
    {
        return getActivityPresenter().lock();
    }

    protected void unLock()
    {
        getActivityPresenter().unLock();
    }

    protected void screenLock(boolean showProgress)
    {
        getActivityPresenter().screenLock(showProgress);
    }

    protected void screenUnLock()
    {
        getActivityPresenter().screenUnLock();
    }

    protected void unLockAll()
    {
        unLock();
        screenUnLock();
    }
}
