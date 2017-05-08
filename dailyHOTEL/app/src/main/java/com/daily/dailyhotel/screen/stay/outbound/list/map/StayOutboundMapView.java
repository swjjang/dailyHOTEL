package com.daily.dailyhotel.screen.stay.outbound.list.map;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.ListItem;
import com.daily.dailyhotel.screen.stay.outbound.list.StayOutboundListAdapter;
import com.daily.dailyhotel.screen.stay.outbound.list.StayOutboundListViewInterface;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundSearchResultDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.List;

public class StayOutboundMapView extends BaseView<StayOutboundMapView.OnEventListener, ActivityStayOutboundSearchResultDataBinding> implements StayOutboundMapViewInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public StayOutboundMapView(BaseActivity baseActivity, StayOutboundMapView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void initLayout(final ActivityStayOutboundSearchResultDataBinding viewDataBinding)
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
}
