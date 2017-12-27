package com.daily.dailyhotel.base;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogViewInterface;

public interface BaseBlurViewInterface extends BaseDialogViewInterface
{
    boolean isBlurVisible();

    void setBlurVisible(BaseActivity activity, boolean visible);
}
