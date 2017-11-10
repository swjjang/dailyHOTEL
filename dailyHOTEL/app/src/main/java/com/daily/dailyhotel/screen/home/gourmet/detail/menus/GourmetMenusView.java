package com.daily.dailyhotel.screen.home.gourmet.detail.menus;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.GourmetMenu;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailPresenter;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityGourmetMenusDataBinding;
import com.twoheart.dailyhotel.databinding.DialogGourmetTimePickerDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Observer;

public class GourmetMenusView extends BaseDialogView<GourmetMenusView.OnEventListener, ActivityGourmetMenusDataBinding>//
    implements GourmetMenusInterface, View.OnClickListener
{
    GourmetMenusAdapter mGourmetMenusAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onCloseClick();

        void onGuideClick();

        void onReservationClick(int index);

        void onScrolled(int position, boolean real);

        void onMoreImageClick(int position);

        void onOperationTimeClick();

        void onVisitTimeClick(int time);

        void onVisitTimeClick(int time, int menuIndex);

        void onHideOperationTimesClick();

        void onMenuOderCountPlusClick(int position);

        void onMenuOderCountMinusClick(int position);
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

        viewDataBinding.operationTimeTextView.setOnClickListener(this);
        viewDataBinding.closeImageView.setOnClickListener(this);

        viewDataBinding.recyclerView.setLayoutManager(new ZoomCenterLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.recyclerView, getColor(R.color.default_over_scroll_edge));

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(viewDataBinding.recyclerView);

        viewDataBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    View view = snapHelper.findSnapView(viewDataBinding.recyclerView.getLayoutManager());
                    getEventListener().onScrolled(viewDataBinding.recyclerView.getChildAdapterPosition(view), true);
                }
            }
        });

        viewDataBinding.guideLayout.setOnClickListener(this);
        viewDataBinding.guideLayout.setVisibility(View.GONE);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().operationTimeTextView.setText(title);
    }

    @Override
    public void setMenuIndicator(int position, int totalCount)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().menuIndicatorTextView.setText(String.format(Locale.KOREA, "%d / %d", position, totalCount));
    }

    @Override
    public void setGourmetMenus(List<GourmetMenu> gourmetMenuList, int position)
    {
        if (getViewDataBinding() == null || gourmetMenuList == null || gourmetMenuList.size() == 0)
        {
            return;
        }

        if (mGourmetMenusAdapter == null)
        {
            mGourmetMenusAdapter = new GourmetMenusAdapter(getContext());
        }

        mGourmetMenusAdapter.clear();
        mGourmetMenusAdapter.addAll(gourmetMenuList);

        mGourmetMenusAdapter.setOnEventListener(new GourmetMenusAdapter.OnEventListener()
        {
            @Override
            public void onMoreImageClick(int index)
            {
                getEventListener().onMoreImageClick(index);
            }

            @Override
            public void onOderCountPlusClick(int position)
            {
                getEventListener().onMenuOderCountPlusClick(position);
            }

            @Override
            public void onOderCountMinusClick(int position)
            {
                getEventListener().onMenuOderCountMinusClick(position);
            }

            @Override
            public void onBackClick()
            {

            }
        });

        getViewDataBinding().recyclerView.setAdapter(mGourmetMenusAdapter);
        getViewDataBinding().recyclerView.post(new Runnable()
        {
            @Override
            public void run()
            {
                ((LinearLayoutManager) (getViewDataBinding().recyclerView.getLayoutManager()))//
                    .scrollToPositionWithOffset(position, (int) mGourmetMenusAdapter.getMenuMargin());
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
    public Observable<Boolean> hideGuideAnimation()
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().guideLayout, "alpha", 1.0f, 0.0f);

        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(300);

        Observable<Boolean> observable = new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                objectAnimator.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animator)
                    {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator)
                    {
                        objectAnimator.removeAllListeners();

                        getViewDataBinding().guideLayout.setVisibility(View.GONE);

                        observer.onNext(true);
                        observer.onComplete();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator)
                    {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator)
                    {
                    }
                });

                objectAnimator.start();
            }
        };

        return observable;
    }

    @Override
    public void setOperationTimes(List<Integer> operationTimeList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onVisitTimeClick((int) v.getTag());
            }
        };

        getViewDataBinding().operationTimesGridLayout.removeAllViews();

        DailyTextView fullTimeTextView = new DailyTextView(getContext());
        fullTimeTextView.setText(R.string.label_all);
        fullTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        fullTimeTextView.setTextColor(getColorStateList(R.drawable.selector_text_color_c323232_cffffff));
        fullTimeTextView.setBackgroundResource(R.drawable.shape_fillrect_selector_de8e8e9_sb70038_r2);
        fullTimeTextView.setTag(GourmetDetailPresenter.FULL_TIME);
        fullTimeTextView.setGravity(Gravity.CENTER);
        fullTimeTextView.setOnClickListener(onClickListener);

        GridLayout.LayoutParams fullTimeLayoutParams = new GridLayout.LayoutParams();
        fullTimeLayoutParams.width = ScreenUtils.dpToPx(getContext(), 56);
        fullTimeLayoutParams.height = ScreenUtils.dpToPx(getContext(), 30);
        fullTimeLayoutParams.leftMargin = ScreenUtils.dpToPx(getContext(), 5);
        fullTimeLayoutParams.rightMargin = ScreenUtils.dpToPx(getContext(), 5);
        fullTimeLayoutParams.bottomMargin = ScreenUtils.dpToPx(getContext(), 10);
        fullTimeLayoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

        getViewDataBinding().operationTimesGridLayout.addView(fullTimeTextView, fullTimeLayoutParams);

        for (int time : operationTimeList)
        {
            // 1시 이후 값은 01:00로 보이도록 한다.
            if (time >= 2500)
            {
                time -= 2400;
            }

            DailyTextView dailyTextView = new DailyTextView(getContext());
            dailyTextView.setText(String.format(Locale.KOREA, "%02d:%02d", time / 100, time % 100));
            dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            dailyTextView.setTextColor(getColorStateList(R.drawable.selector_text_color_c323232_cffffff));
            dailyTextView.setBackgroundResource(R.drawable.shape_fillrect_selector_de8e8e9_sb70038_r2);
            dailyTextView.setTag(time);
            dailyTextView.setGravity(Gravity.CENTER);
            dailyTextView.setOnClickListener(onClickListener);

            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.width = ScreenUtils.dpToPx(getContext(), 56);
            layoutParams.height = ScreenUtils.dpToPx(getContext(), 30);
            layoutParams.leftMargin = ScreenUtils.dpToPx(getContext(), 5);
            layoutParams.rightMargin = ScreenUtils.dpToPx(getContext(), 5);
            layoutParams.bottomMargin = ScreenUtils.dpToPx(getContext(), 10);
            layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

            getViewDataBinding().operationTimesGridLayout.addView(dailyTextView, layoutParams);
        }

        // 빈공간 채우기
        int columnCount = getViewDataBinding().operationTimesGridLayout.getColumnCount();
        int size = (operationTimeList.size() + 1) % columnCount;
        size = size > 0 ? columnCount - size : 0;

        for (int i = 0; i < size; i++)
        {
            DailyTextView dailyTextView = new DailyTextView(getContext());
            dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.width = ScreenUtils.dpToPx(getContext(), 56);
            layoutParams.height = 1;
            layoutParams.leftMargin = ScreenUtils.dpToPx(getContext(), 5);
            layoutParams.rightMargin = ScreenUtils.dpToPx(getContext(), 5);
            layoutParams.bottomMargin = ScreenUtils.dpToPx(getContext(), 10);
            layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

            getViewDataBinding().operationTimesGridLayout.addView(dailyTextView, layoutParams);
        }

        getViewDataBinding().operationTimesGridLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setVisitTime(int time)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        int size = getViewDataBinding().operationTimesGridLayout.getChildCount();

        for (int i = 0; i < size; i++)
        {
            View childView = getViewDataBinding().operationTimesGridLayout.getChildAt(i);
            Integer timeTag = (Integer) childView.getTag();

            if (timeTag != null)
            {
                if (time == (int) timeTag)
                {
                    childView.setSelected(true);
                } else
                {
                    childView.setSelected(false);
                }
            }
        }
    }

    @Override
    public Observable<Boolean> showOperationTimes(int selectedTimes)
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        int childCount = getViewDataBinding().operationTimesGridLayout.getChildCount();

        for (int i = 0; i < childCount; i++)
        {
            Integer time = (Integer) getViewDataBinding().operationTimesGridLayout.getChildAt(i).getTag();

            if (time != null && time == selectedTimes)
            {
                getViewDataBinding().operationTimesGridLayout.getChildAt(i).setSelected(true);
            } else
            {
                getViewDataBinding().operationTimesGridLayout.getChildAt(i).setSelected(false);
            }
        }

        ObjectAnimator transObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().operationTimesGridLayout//
            , View.TRANSLATION_Y, -getViewDataBinding().operationTimesGridLayout.getHeight(), 0);

        transObjectAnimator.setDuration(200);
        transObjectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        transObjectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                if (animation == null)
                {
                    return;
                }

                float value = (float) animation.getAnimatedValue();
                float vector = 1.0f - (value / getViewDataBinding().operationTimesGridLayout.getHeight());
                float rotation = 180.0f * vector;

                getViewDataBinding().arrowImageView.setRotation(rotation);
                getViewDataBinding().operationTimesBackgroundView.setAlpha(vector);
            }
        });

        Observable<Boolean> observable = new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                transObjectAnimator.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {
                        getViewDataBinding().operationTimesGridLayout.setVisibility(View.VISIBLE);
                        getViewDataBinding().operationTimesBackgroundView.setAlpha(0.0f);
                        getViewDataBinding().operationTimesBackgroundView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        transObjectAnimator.removeAllUpdateListeners();
                        transObjectAnimator.removeAllListeners();

                        getViewDataBinding().operationTimesGridLayout.setTranslationY(0.0f);

                        observer.onNext(true);
                        observer.onComplete();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation)
                    {

                    }
                });

                transObjectAnimator.start();
            }
        };

        return observable;
    }

    @Override
    public Observable<Boolean> hideOperationTimes()
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        ObjectAnimator transObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().operationTimesGridLayout//
            , View.TRANSLATION_Y, 0, -getViewDataBinding().operationTimesGridLayout.getHeight());

        transObjectAnimator.setDuration(200);
        transObjectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        transObjectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                if (animation == null)
                {
                    return;
                }

                float value = (float) animation.getAnimatedValue();
                float vector = 1.0f - Math.abs(value / getViewDataBinding().operationTimesGridLayout.getHeight());
                float rotation = 180.0f * vector;

                getViewDataBinding().arrowImageView.setRotation(rotation);
                getViewDataBinding().operationTimesBackgroundView.setAlpha(vector);
            }
        });

        Observable<Boolean> observable = new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                transObjectAnimator.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        transObjectAnimator.removeAllUpdateListeners();
                        transObjectAnimator.removeAllListeners();

                        getViewDataBinding().operationTimesGridLayout.setTranslationY(-getViewDataBinding().operationTimesGridLayout.getHeight());
                        getViewDataBinding().operationTimesGridLayout.setVisibility(View.INVISIBLE);
                        getViewDataBinding().operationTimesBackgroundView.setVisibility(View.GONE);

                        observer.onNext(true);
                        observer.onComplete();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation)
                    {

                    }
                });

                transObjectAnimator.start();
            }
        };

        return observable;
    }

    @Override
    public void setMenuOrderCount(int menuIndex, int orderCount)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        int position = mGourmetMenusAdapter.getPosition(menuIndex);

        getViewDataBinding().recyclerView.post(() -> mGourmetMenusAdapter.setMenuOrderCount(getViewDataBinding().recyclerView.findViewHolderForAdapterPosition(position), position, orderCount));
    }

    @Override
    public void showTimePickerDialog(List<Integer> operationTimeList, int menuIndex)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        DialogGourmetTimePickerDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_gourmet_time_picker_data, null, false);

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int size = dataBinding.timeGridLayout.getChildCount();

                for (int i = 0; i < size; i++)
                {
                    dataBinding.timeGridLayout.getChildAt(i).setSelected(v == dataBinding.timeGridLayout.getChildAt(i));
                }

                dataBinding.positiveTextView.setEnabled(true);
            }
        };

        for (int time : operationTimeList)
        {
            DailyTextView dailyTextView = new DailyTextView(getContext());
            dailyTextView.setText(String.format(Locale.KOREA, "%02d:%02d", time / 100, time % 100));
            dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            dailyTextView.setTextColor(getColorStateList(R.drawable.selector_text_color_c323232_cffffff));
            dailyTextView.setBackgroundResource(R.drawable.shape_fillrect_selector_de8e8e9_sb70038_r2);
            dailyTextView.setTag(time);
            dailyTextView.setGravity(Gravity.CENTER);
            dailyTextView.setOnClickListener(onClickListener);

            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.width = ScreenUtils.dpToPx(getContext(), 56);
            layoutParams.height = ScreenUtils.dpToPx(getContext(), 30);
            layoutParams.leftMargin = ScreenUtils.dpToPx(getContext(), 5);
            layoutParams.rightMargin = ScreenUtils.dpToPx(getContext(), 5);
            layoutParams.bottomMargin = ScreenUtils.dpToPx(getContext(), 10);
            layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

            dataBinding.timeGridLayout.addView(dailyTextView, layoutParams);
        }

        // 빈공간 채우기
        int columnCount = dataBinding.timeGridLayout.getColumnCount();
        int size = (operationTimeList.size() + 1) % columnCount;
        size = size > 0 ? columnCount - size : 0;

        for (int i = 0; i < size; i++)
        {
            DailyTextView dailyTextView = new DailyTextView(getContext());
            dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.width = ScreenUtils.dpToPx(getContext(), 56);
            layoutParams.height = 1;
            layoutParams.leftMargin = ScreenUtils.dpToPx(getContext(), 5);
            layoutParams.rightMargin = ScreenUtils.dpToPx(getContext(), 5);
            layoutParams.bottomMargin = ScreenUtils.dpToPx(getContext(), 10);
            layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

            dataBinding.timeGridLayout.addView(dailyTextView, layoutParams);
        }

        dataBinding.negativeTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();
            }
        });

        dataBinding.positiveTextView.setEnabled(false);
        dataBinding.positiveTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int size = dataBinding.timeGridLayout.getChildCount();
                int time = GourmetDetailPresenter.FULL_TIME;

                for (int i = 0; i < size; i++)
                {
                    if (dataBinding.timeGridLayout.getChildAt(i).isSelected() == true)
                    {
                        time = (int) dataBinding.timeGridLayout.getChildAt(i).getTag();
                        break;
                    }
                }

                getEventListener().onVisitTimeClick(time, menuIndex);

                hideSimpleDialog();
            }
        });

        showSimpleDialog(dataBinding.getRoot(), null, null, true);
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.operationTimeTextView:
                getEventListener().onOperationTimeClick();
                break;

            case R.id.operationTimesBackgroundView:
                getEventListener().onHideOperationTimesClick();
                break;

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
        }

        @Override
        public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state)
        {
            int scrolled = super.scrollHorizontallyBy(dx, recycler, state);
            final float midpoint = getWidth() / 2.f;
            final float d1 = DISTANCE * midpoint;
            final float s0 = 1.f;
            //            final float s1 = 1.f - AMOUNT;
            int childCount = getChildCount();

            for (int i = 0; i < childCount; i++)
            {
                View childView = getChildAt(i);
                float childMidpoint = (getDecoratedRight(childView) + getDecoratedLeft(childView)) / 2.f;
                float d = Math.min(d1, Math.abs(midpoint - childMidpoint));
                float scale = s0 - AMOUNT * d / d1;
                float vectorValue = (1.0f - scale) / AMOUNT;

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
