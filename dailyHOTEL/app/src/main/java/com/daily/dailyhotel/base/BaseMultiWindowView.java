package com.daily.dailyhotel.base;

import android.databinding.ViewDataBinding;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;

/**
 * Created by android_sam on 2018. 1. 10..
 */

public abstract class BaseMultiWindowView<T1 extends OnBaseEventListener, T2 extends ViewDataBinding> extends BaseDialogView<T1, T2> implements BaseMultiWindowViewInterface
{

    public BaseMultiWindowView(BaseActivity activity, T1 listener)
    {
        super(activity, listener);
    }
}
