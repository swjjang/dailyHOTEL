package com.daily.dailyhotel.base;

import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;

/**
 * Created by android_sam on 2018. 1. 10..
 */

public abstract class BaseMultiWindowPresenter<T1 extends BaseActivity, T2 extends BaseMultiWindowViewInterface> extends BaseExceptionPresenter<T1, T2> implements BaseMultiWindowActivityInterface
{
    public BaseMultiWindowPresenter(@NonNull T1 activity)
    {
        super(activity);
    }
}
