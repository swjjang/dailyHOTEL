package com.daily.dailyhotel.screen.home.gourmet.detail.menus;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyRoundedConstraintLayout;
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

        void onGuideClick();

        void onReservationClick(int index);

        void onScrolled(int position, boolean real);

        void onMoreImageClick(int position);
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
                getEventListener().onScrolled(viewDataBinding.recyclerView.getChildAdapterPosition(view), true);
            }
        });

        viewDataBinding.guideLayout.setOnClickListener(this);
        viewDataBinding.guideLayout.setVisibility(View.GONE);
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
        gourmetMenusAdapter.setOnEventListener(new GourmetMenusAdapter.OnEventListener()
        {
            @Override
            public void onReservationClick(int index)
            {
                getEventListener().onReservationClick(index);
            }

            @Override
            public void onMoreImageClick(int index)
            {
                getEventListener().onMoreImageClick(index);
            }

            @Override
            public void onBackClick()
            {

            }
        });

        getViewDataBinding().recyclerView.setAdapter(gourmetMenusAdapter);
        getViewDataBinding().recyclerView.post(new Runnable()
        {
            @Override
            public void run()
            {
                ((LinearLayoutManager) (getViewDataBinding().recyclerView.getLayoutManager())).scrollToPositionWithOffset(position, ScreenUtils.getScreenWidth(getContext()) / 12);
            }
        });
    }

    @Override
    public void setGuideVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().guideLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void hideGuideAnimation(Animator.AnimatorListener listener)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().guideLayout, "alpha", 1.0f, 0.0f);

        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(300);
        objectAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animator)
            {
                if (listener != null)
                {
                    listener.onAnimationStart(animator);
                }
            }

            @Override
            public void onAnimationEnd(Animator animator)
            {
                objectAnimator.removeAllListeners();

                getViewDataBinding().guideLayout.setVisibility(View.GONE);

                if (listener != null)
                {
                    listener.onAnimationEnd(animator);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator)
            {
                if (listener != null)
                {
                    listener.onAnimationCancel(animator);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator)
            {
                if (listener != null)
                {
                    listener.onAnimationRepeat(animator);
                }
            }
        });

        objectAnimator.start();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.closeImageView:
                getEventListener().onCloseClick();
                break;

            case R.id.guideLayout:
                getEventListener().onGuideClick();
                break;
        }
    }

    class ZoomCenterLayoutManager extends LinearLayoutManager
    {
        private static final float MIN_SCALE = 0.90f;
        private static final float AMOUNT = 1.0f - MIN_SCALE; // 1.0f - AMOUNT = MIN_SCALE
        private static final float DISTANCE = 0.75f;
        private int DP_10;
        private int DP_5;
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
            DP_5 = ScreenUtils.dpToPx(context, 5);
            STANDARD_X = (int)(ScreenUtils.getScreenWidth(context) * (1.0f - GourmetMenusAdapter.MENU_WIDTH_RATIO) / 2.0f);
        }

        @Override
        public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state)
        {
            int scrolled = super.scrollHorizontallyBy(dx, recycler, state);
            final float midpoint = getWidth() / 2.f;
            //            final float d0 = 0.f;
            final float d1 = DISTANCE * midpoint;
            final float s0 = 1.f;
            final float s1 = 1.f - AMOUNT;
            int childCount = getChildCount();

            for (int i = 0; i < childCount; i++)
            {
                View childView = getChildAt(i);
                float childMidpoint = (getDecoratedRight(childView) + getDecoratedLeft(childView)) / 2.f;
                float d = Math.min(d1, Math.abs(midpoint - childMidpoint));
                float scale = s0 - AMOUNT * d / d1;
                //                float scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0);

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

                DailyRoundedConstraintLayout roundedConstraintLayout = (DailyRoundedConstraintLayout) childView.findViewById(R.id.roundedConstraintLayout);

                final float width = roundedConstraintLayout.getWidth();
                final float height = roundedConstraintLayout.getHeight();
                final float scaleWidth = (1.0f - scale) * width;
                final float scaleHeight = (1.0f - scale) * height;

                roundedConstraintLayout.setRound(0, 0, width - scaleWidth, height - scaleHeight, DP_5);
                roundedConstraintLayout.invalidate();

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
