package com.daily.dailyhotel.screen.common.area.stay.inbound;


import android.support.annotation.NonNull;

import com.daily.base.OnBaseFragmentEventListener;
import com.daily.dailyhotel.base.BasePagerFragment;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayAreaGroup;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayAreaFragment extends BasePagerFragment<StayAreaFragmentPresenter, StayAreaFragment.OnEventListener>
{
    public interface OnEventListener extends OnBaseFragmentEventListener
    {
        void onAroundSearchClick();

        void onAreaClick(StayAreaGroup areaGroup, StayArea area);
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
