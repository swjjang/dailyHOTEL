package com.daily.base;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseFragmentPresenter<T1 extends Fragment, T2 extends BaseViewInterface> implements BaseFragmentInterface
{
    private T1 mFragment;

    private T2 mOnViewInterface;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public BaseFragmentPresenter(@NonNull T1 fragment)
    {
        mFragment = fragment;

        mOnViewInterface = createInstanceViewInterface();

        initialize();
    }

    protected abstract
    @NonNull
    T2 createInstanceViewInterface();

    public abstract void initialize();

    public abstract void setAnalytics(BaseAnalyticsInterface analytics);

    protected abstract void onHandleError(Throwable throwable);

    public BaseActivity getActivity()
    {
        return (BaseActivity) mFragment.getActivity();
    }

    public BasePresenter getActivityPresenter()
    {
        return getActivity().getPresenter();
    }

    protected String getString(int resId)
    {
        return mFragment.getString(resId);
    }

    protected String getString(int resId, Object... formatArgs)
    {
        return mFragment.getString(resId, formatArgs);
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
        getActivityPresenter().clearLock();

        getViewInterface().hideSimpleDialog();

        clearCompositeDisposable();
    }

    protected void startActivity(Intent intent)
    {
        mFragment.startActivity(intent);
    }

    protected void startActivityForResult(Intent intent, int requestCode)
    {
        mFragment.startActivityForResult(intent, requestCode);
    }

    protected void startActivity(Activity activity, Intent intent)
    {
        activity.startActivity(intent);
    }

    protected void startActivityForResult(Activity activity, Intent intent, int requestCode)
    {
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
