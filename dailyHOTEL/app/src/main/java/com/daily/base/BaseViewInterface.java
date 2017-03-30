package com.daily.base;

import android.view.View;
import android.view.ViewGroup;

public interface BaseViewInterface
{
    View onCreateView(int layoutResID);

    View onCreateView(int layoutResID, ViewGroup viewGroup);
}
