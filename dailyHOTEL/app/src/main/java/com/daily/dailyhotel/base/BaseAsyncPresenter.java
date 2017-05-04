package com.daily.dailyhotel.base;

import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.BaseViewInterface;

import io.reactivex.Observable;

public abstract class BaseAsyncPresenter<T1 extends BaseActivity, T2 extends BaseViewInterface> extends BaseExceptionPresenter<T1, T2>
{
    // Activity 시작시에 Async로 동작해야하는 경우가 있는 경우.
    protected abstract void startAsync(@NonNull Observable<Boolean> observable);

    public BaseAsyncPresenter(@NonNull T1 activity)
    {
        super(activity);
    }
}
