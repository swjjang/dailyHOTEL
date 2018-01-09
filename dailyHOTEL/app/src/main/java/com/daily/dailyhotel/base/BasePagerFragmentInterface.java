package com.daily.dailyhotel.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.BaseFragmentInterface;

public interface BasePagerFragmentInterface extends BaseFragmentInterface
{
    void onSelected(); //

    void onUnselected();

    void onRefresh();

    void scrollTop();
}
