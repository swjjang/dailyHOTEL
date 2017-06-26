package com.daily.dailyhotel.screen.home.gourmet.detail.menus;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.SnapHelper;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.GourmetMenu;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityGourmetMenusDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.DailyPagerSnapHelper;

import java.util.List;

public class GourmetMenusView extends BaseDialogView<GourmetMenusView.OnEventListener, ActivityGourmetMenusDataBinding> implements GourmetMenusInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onReservationClick(int index);
    }

    public GourmetMenusView(BaseActivity baseActivity, GourmetMenusView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityGourmetMenusDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.recyclerView, getColor(R.color.default_over_scroll_edge));

        SnapHelper snapHelper = new DailyPagerSnapHelper();
        snapHelper.attachToRecyclerView(viewDataBinding.recyclerView);
    }

    @Override
    public void setToolbarTitle(String title)
    {
    }

    @Override
    public void setGourmetMenus(List<GourmetMenu> gourmetMenuList, int position)
    {
        if (getViewDataBinding() == null || gourmetMenuList == null || gourmetMenuList.size() == 0)
        {
            return;
        }

        if (position < 0)
        {
            position = 0;
        }

        GourmetMenusAdapter gourmetMenusAdapter = new GourmetMenusAdapter(getContext(), gourmetMenuList);
        gourmetMenusAdapter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int position = getViewDataBinding().recyclerView.getChildAdapterPosition((View)(view.getParent().getParent()));
                getEventListener().onReservationClick(position);
            }
        });

        getViewDataBinding().recyclerView.setAdapter(gourmetMenusAdapter);
        getViewDataBinding().recyclerView.scrollToPosition(position);
    }
}
