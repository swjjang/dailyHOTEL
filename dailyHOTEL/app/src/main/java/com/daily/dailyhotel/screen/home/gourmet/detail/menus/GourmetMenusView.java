package com.daily.dailyhotel.screen.home.gourmet.detail.menus;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.GourmetMenu;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityGourmetMenusDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.List;

public class GourmetMenusView extends BaseDialogView<GourmetMenusView.OnEventListener, ActivityGourmetMenusDataBinding>//
    implements GourmetMenusInterface, View.OnClickListener
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onCloseClick();

        void onReservationClick(int index);

        void onScrolled(int position);
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

        viewDataBinding.closeImageView.setOnClickListener(this);

        viewDataBinding.recyclerView.setLayoutManager(new ZoomCenterLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.recyclerView, getColor(R.color.default_over_scroll_edge));

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(viewDataBinding.recyclerView);

        viewDataBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);

                View view = snapHelper.findSnapView(viewDataBinding.recyclerView.getLayoutManager());
                getEventListener().onScrolled(viewDataBinding.recyclerView.getChildAdapterPosition(view));
            }
        });
    }

    @Override
    public void setToolbarTitle(String title)
    {
    }

    @Override
    public void setSubTitle(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().menuTextView.setText(text);
    }

    @Override
    public void setGourmetMenus(List<GourmetMenu> gourmetMenuList, int position)
    {
        if (getViewDataBinding() == null || gourmetMenuList == null || gourmetMenuList.size() == 0)
        {
            return;
        }

        GourmetMenusAdapter gourmetMenusAdapter = new GourmetMenusAdapter(getContext(), gourmetMenuList);
        gourmetMenusAdapter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getEventListener().onReservationClick(getViewDataBinding().recyclerView.getChildAdapterPosition((View) (view.getParent().getParent())));
            }
        });

        getViewDataBinding().recyclerView.setAdapter(gourmetMenusAdapter);

        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(getContext())
        {
            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics)
            {
                return 0.1f / displayMetrics.densityDpi;
            }
        };

        linearSmoothScroller.setTargetPosition(position);
        getViewDataBinding().recyclerView.getLayoutManager().startSmoothScroll(linearSmoothScroller);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.closeImageView:
                getEventListener().onCloseClick();
                break;
        }
    }

    class ZoomCenterLayoutManager extends LinearLayoutManager
    {
        private static final float MIN_SCALE = 0.90f;
        private static final float AMOUNT = 1.0f - MIN_SCALE; // 1.0f - AMOUNT = MIN_SCALE
        private static final float DISTANCE = 0.75f;
        private int DP_10;
        private int STANDARD_X;

        public ZoomCenterLayoutManager(Context context)
        {
            super(context);

            initialize(context);
        }

        public ZoomCenterLayoutManager(Context context, int orientation, boolean reverseLayout)
        {
            super(context, orientation, reverseLayout);

            initialize(context);
        }

        public ZoomCenterLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
        {
            super(context, attrs, defStyleAttr, defStyleRes);

            initialize(context);
        }

        private void initialize(Context context)
        {
            DP_10 = ScreenUtils.dpToPx(context, 10);
            STANDARD_X = ScreenUtils.getScreenWidth(getContext()) / 12;
        }

        @Override
        public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state)
        {
            int scrolled = super.scrollHorizontallyBy(dx, recycler, state);
            float midpoint = getWidth() / 2.f;
            float d0 = 0.f;
            float d1 = DISTANCE * midpoint;
            float s0 = 1.f;
            float s1 = 1.f - AMOUNT;
            int childCount = getChildCount();

            for (int i = 0; i < childCount; i++)
            {
                View childView = getChildAt(i);
                float childMidpoint = (getDecoratedRight(childView) + getDecoratedLeft(childView)) / 2.f;
                float d = Math.min(d1, Math.abs(midpoint - childMidpoint));
                float scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0);
                childView.setScaleX(scale);
                childView.setScaleY(scale);

                float childX = childView.getX();
                float vectorValue = (1.0f - scale) / AMOUNT;

                final float distance = DP_10 * vectorValue;

                if (childX > STANDARD_X)
                {
                    childView.setTranslationX(-distance);
                } else if (childX < STANDARD_X)
                {
                    childView.setTranslationX(distance);
                } else
                {
                    childView.setTranslationX(0.0f);
                }

                View blurView = (View) childView.getTag(R.id.blurView);

                if (blurView != null)
                {
                    blurView.setAlpha(vectorValue);
                }
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
