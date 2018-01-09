package com.daily.dailyhotel.base;

import com.daily.base.BaseActivity;
import com.daily.base.BaseFragmentDialogViewInterface;

public interface BaseBlurFragmentViewInterface extends BaseFragmentDialogViewInterface
{
    boolean isBlurVisible();

    void setBlurVisible(BaseActivity activity, boolean visible);
}
