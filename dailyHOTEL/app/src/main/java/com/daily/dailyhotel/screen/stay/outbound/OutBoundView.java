package com.daily.dailyhotel.screen.stay.outbound;

import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.screen.mydaily.profile.ProfileViewInterface;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityOutboundDataBinding;
import com.twoheart.dailyhotel.databinding.ActivityProfileDataBinding;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

public class OutBoundView extends BaseView<OutBoundView.OnEventListener, ActivityOutboundDataBinding> implements OutBoundViewInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public OutBoundView(BaseActivity baseActivity, OutBoundView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void initLayout(ActivityOutboundDataBinding viewDataBinding)
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
        dailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_profile_activity)//
            , v -> getEventListener().finish());
    }
}
