package com.daily.dailyhotel.screen.stay.outbound.filter;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.Suggest;
import com.daily.dailyhotel.screen.stay.outbound.StayOutboundViewInterface;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundDataBinding;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundFilterDataBinding;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.List;

public class StayOutboundFilterView extends BaseView<StayOutboundFilterView.OnEventListener, ActivityStayOutboundFilterDataBinding> implements StayOutboundFilterViewInterface, View.OnClickListener
{



    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public StayOutboundFilterView(BaseActivity baseActivity, StayOutboundFilterView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void initLayout(final ActivityStayOutboundFilterDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

    }

    @Override
    public void setToolbarTitle(String title)
    {
    }

    @Override
    public void onClick(View v)
    {

    }
}
