package com.daily.dailyhotel.base;

import android.app.Activity;

import com.daily.base.BaseDialogViewInterface;

public interface BaseBlurViewInterface extends BaseDialogViewInterface
{
    boolean isBlurVisible();

    void setBlurVisible(Activity activity, boolean visible);
}
