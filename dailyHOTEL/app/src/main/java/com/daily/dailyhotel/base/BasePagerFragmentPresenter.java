package com.daily.dailyhotel.base;

import android.support.annotation.NonNull;

import com.daily.base.BaseFragment;
import com.daily.base.BaseFragmentDialogViewInterface;

public abstract class BasePagerFragmentPresenter<T1 extends BaseFragment, T2 extends BaseFragmentDialogViewInterface> extends BaseFragmentExceptionPresenter<T1, T2> implements BasePagerFragmentInterface
{
    public BasePagerFragmentPresenter(@NonNull T1 fragment)
    {
        super(fragment);
    }
}
