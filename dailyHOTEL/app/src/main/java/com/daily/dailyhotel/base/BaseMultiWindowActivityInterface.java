package com.daily.dailyhotel.base;

import android.content.res.Configuration;

import com.daily.base.BaseActivityInterface;

/**
 * Created by android_sam on 2018. 1. 10..
 */

public interface BaseMultiWindowActivityInterface extends BaseActivityInterface
{
    void onConfigurationChanged(Configuration newConfig);

    void onMultiWindowModeChanged(boolean isInMultiWindowMode);
}
