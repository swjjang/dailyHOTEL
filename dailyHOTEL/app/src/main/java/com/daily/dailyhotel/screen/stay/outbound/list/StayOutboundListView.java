package com.daily.dailyhotel.screen.stay.outbound.list;

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
import com.twoheart.dailyhotel.databinding.ActivityOutboundDataBinding;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.List;

public class StayOutboundListView extends BaseView<StayOutboundListView.OnEventListener, ActivityOutboundDataBinding> implements StayOutboundListViewInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public StayOutboundListView(BaseActivity baseActivity, StayOutboundListView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void initLayout(final ActivityOutboundDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

    }

    private void initToolbar(ActivityOutboundDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(getContext(), viewDataBinding.toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.label_search)//
            , v -> getEventListener().finish());
    }
}
