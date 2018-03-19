package com.daily.base;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.daily.base.util.DailyLock;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BasePresenter<T1 extends BaseActivity, T2 extends BaseDialogViewInterface> implements BaseActivityInterface
{
    private T1 mActivity;

    private T2 mOnViewInterface;

    private DailyLock mLock;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private int mResultCode;

    public BasePresenter(@NonNull T1 activity)
    {
        mActivity = activity;

        mLock = new DailyLock(activity);

        mOnViewInterface = createInstanceViewInterface();

        constructorInitialize(activity);
    }

    protected abstract @NonNull
    T2 createInstanceViewInterface();

    public abstract void constructorInitialize(T1 activity);

    public abstract void setAnalytics(BaseAnalyticsInterface analytics);

    public abstract void onPostCreate();

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
        clearLock();

        getViewInterface().hideSimpleDialog();

        disposeCompositeDisposable();
    }

    @Override
    public void onFinish()
    {

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

    protected void startActivity(Intent intent)
    {
        mActivity.startActivity(intent);
    }

    protected void startActivityForResult(Intent intent, int requestCode)
    {
        mActivity.startActivityForResult(intent, requestCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected void startActivityForResult(Intent intent, int requestCode, Bundle options)
    {
        mActivity.startActivityForResult(intent, requestCode, options);
    }

    protected void setResult(int resultCode)
    {
        mResultCode = resultCode;

        mActivity.setResult(resultCode);
    }

    protected void setResult(int resultCode, Intent data)
    {
        mResultCode = resultCode;

        mActivity.setResult(resultCode, data);
    }

    protected int getResultCode()
    {
        return mResultCode;
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

    protected boolean isLock()
    {
        return mLock.isLock();
    }

    protected boolean isScreenLock()
    {
        return mLock.isScreenLock();
    }

    protected boolean lock()
    {
        return mLock.lock();
    }

    protected boolean hasLock()
    {
        return isLock() || isScreenLock();
    }

    protected void unLock()
    {
        mLock.unLock();
    }

    void clearLock()
    {
        mLock.clear();
    }

    protected void screenLock(boolean showProgress)
    {
        mLock.screenLock(showProgress);
    }

    protected void screenUnLock()
    {
        mLock.screenUnLock();
    }

    protected void unLockAll()
    {
        unLock();
        screenUnLock();
    }

    protected void finish()
    {
        mActivity.finish();
    }

    protected boolean equalsCallingActivity(Class className)
    {
        return mActivity.equalsCallingActivity(className);
    }
}
