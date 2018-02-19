package com.daily.dailyhotel.base;

import com.daily.base.BaseFragmentInterface;

public interface BasePagerFragmentInterface extends BaseFragmentInterface
{
    void onSelected(); //

    void onUnselected();

    void onRefresh();

    void scrollTop();

    boolean onBackPressed();
}
