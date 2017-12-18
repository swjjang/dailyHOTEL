package com.daily.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface BaseFragmentViewInterface
{
    View getContentView(LayoutInflater layoutInflater, int layoutResID, ViewGroup viewGroup);

    void setToolbarTitle(String title);

    void setActivity(BaseActivity baseActivity);
}
