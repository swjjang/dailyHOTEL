package com.daily.dailyhotel.screen.common.area.stay.inbound;


import android.support.annotation.NonNull;

import com.daily.base.OnBaseFragmentEventListener;
import com.daily.dailyhotel.base.BasePagerFragment;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayAreaFragment extends BasePagerFragment<StayAreaFragmentPresenter, StayAreaFragment.OnEventListener>
{
    public interface OnEventListener extends OnBaseFragmentEventListener
    {
    }

    @NonNull
    @Override
    protected StayAreaFragmentPresenter createInstancePresenter()
    {
        return new StayAreaFragmentPresenter(this);
    }

    @Override
    protected OnEventListener getFragmentEventListener()
    {
        return super.getFragmentEventListener();
    }
}
