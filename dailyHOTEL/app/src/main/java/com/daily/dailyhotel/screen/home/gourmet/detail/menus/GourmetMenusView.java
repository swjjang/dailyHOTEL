package com.daily.dailyhotel.screen.home.gourmet.detail.menus;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.AttributeSet;
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

        viewDataBinding.recyclerView.setLayoutManager(new ZoomCenterLayoutManaber(getContext(), LinearLayoutManager.HORIZONTAL, false));
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
                int position = getViewDataBinding().recyclerView.getChildAdapterPosition((View) (view.getParent().getParent()));
                getEventListener().onReservationClick(position);
            }
        });

        getViewDataBinding().recyclerView.setAdapter(gourmetMenusAdapter);
        getViewDataBinding().recyclerView.scrollToPosition(position);
    }

    class ZoomCenterLayoutManaber extends LinearLayoutManager
    {
        private final float mShrinkAmount = 0.10f;
        private final float mShrinkDistance = 0.75f;

        public ZoomCenterLayoutManaber(Context context)
        {
            super(context);
        }

        public ZoomCenterLayoutManaber(Context context, int orientation, boolean reverseLayout)
        {
            super(context, orientation, reverseLayout);
        }

        public ZoomCenterLayoutManaber(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
        {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state)
        {
            int scrolled = super.scrollHorizontallyBy(dx, recycler, state);
            float midpoint = getWidth() / 2.f;
            float d0 = 0.f;
            float d1 = mShrinkDistance * midpoint;
            float s0 = 1.f;
            float s1 = 1.f - mShrinkAmount;

            for (int i = 0; i < getChildCount(); i++)
            {
                View child = getChildAt(i);
                float childMidpoint = (getDecoratedRight(child) + getDecoratedLeft(child)) / 2.f;
                float d = Math.min(d1, Math.abs(midpoint - childMidpoint));
                float scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0);
                child.setScaleX(scale);
                child.setScaleY(scale);
            }

            return scrolled;
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position)
        {
            super.smoothScrollToPosition(recyclerView, state, position);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state)
        {
            super.onLayoutChildren(recycler, state);
            scrollHorizontallyBy(0, recycler, state);
        }
    }
}
