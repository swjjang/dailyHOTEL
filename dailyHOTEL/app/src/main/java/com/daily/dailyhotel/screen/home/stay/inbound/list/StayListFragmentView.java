package com.daily.dailyhotel.screen.home.stay.inbound.list;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.BaseFragmentDialogView;
import com.daily.base.BaseFragmentPresenter;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.ObjectItem;
import com.twoheart.dailyhotel.databinding.ActivityStayListDataBinding;
import com.twoheart.dailyhotel.util.Constants;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayListFragmentView extends BaseFragmentDialogView<StayListFragmentView.OnEventListener, ActivityStayListDataBinding>
{
    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public StayListFragmentView(OnEventListener listener)
    {
        super(listener);
    }

    @Override
    public void setToolbarTitle(String title)
    {

    }

    @Override
    protected void setContentView(ActivityStayListDataBinding viewDataBinding)
    {

    }
}
