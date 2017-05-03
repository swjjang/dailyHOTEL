package com.daily.dailyhotel.screen.common.calendar;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.daily.base.widget.DailyTextView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityCalendarDataBinding;
import com.twoheart.dailyhotel.databinding.ViewCalendarDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public abstract class PlaceCalendarView<T1 extends PlaceCalendarView.OnEventListener, T2 extends ActivityCalendarDataBinding> extends BaseView<T1, T2> implements View.OnClickListener
{
    private static final int ANIMATION_DELAY = 200;
    private List<View> mDaysViewList;

    private AnimatorSet mAnimatorSet;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onShowAnimationEnd();

        void onHideAnimationEnd();
    }

    public PlaceCalendarView(BaseActivity baseActivity, T1 listener)
    {
        super(baseActivity, listener);

        // VersionUtils.isOverAPI21()
        setStatusBarColor(getColor(R.color.black_a67));
    }

    @Override
    protected void initLayout(final ActivityCalendarDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.calendarScrollView, getColor(R.color.default_over_scroll_edge));

        viewDataBinding.closeView.setOnClickListener(this);
        viewDataBinding.exitView.setOnClickListener(this);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if(getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().titleTextView.setText(title);
    }

    void makeCalendarView(ArrayList<Pair<String, PlaceCalendarPresenter.Day[]>> arrayList)
    {
        if (arrayList == null)
        {
            return;
        }

        int size = arrayList.size();

        for (int i = 0; i < size; i++)
        {
            Pair<String, PlaceCalendarPresenter.Day[]> pair = arrayList.get(i);
            ViewCalendarDataBinding calendarDataBinding = getMonthCalendarView(getContext(), pair);

            View monthCalendarLayout = calendarDataBinding.getRoot();

            if (i >= 0 && i < size)
            {
                monthCalendarLayout.setPadding(monthCalendarLayout.getPaddingLeft(), monthCalendarLayout.getPaddingTop()//
                    , monthCalendarLayout.getPaddingRight(), monthCalendarLayout.getPaddingBottom() + ScreenUtils.dpToPx(getContext(), 30));
            }

            getViewDataBinding().calendarLayout.addView(monthCalendarLayout);
        }
    }

    void showAnimation()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (mAnimatorSet != null && mAnimatorSet.isStarted() == true)
        {
            return;
        }

        final View animationLayout = getViewDataBinding().animationLayout;
        final float y = animationLayout.getBottom();

        int height = animationLayout.getHeight();
        animationLayout.setTranslationY(ScreenUtils.dpToPx(getContext(), height));

        ObjectAnimator transAnimator = ObjectAnimator.ofFloat(animationLayout, "y", y, y - height);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(transAnimator);
        mAnimatorSet.setDuration(ANIMATION_DELAY);
        mAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimatorSet.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                setVisibility(true);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                mAnimatorSet.removeAllListeners();
                mAnimatorSet = null;

                getEventListener().onShowAnimationEnd();
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

        mAnimatorSet.start();
    }

    void hideAnimation()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (mAnimatorSet != null && mAnimatorSet.isStarted() == true)
        {
            return;
        }

        final View animationLayout = getViewDataBinding().animationLayout;
        final float y = animationLayout.getTop();

        ObjectAnimator transAnimator = ObjectAnimator.ofFloat(animationLayout, "y", y, animationLayout.getBottom());
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(getViewDataBinding().getRoot(), "alpha", 1f, 0f);

        if(VersionUtils.isOverAPI21() == true)
        {
            alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                @Override
                public void onAnimationUpdate(ValueAnimator animation)
                {
                    if(animation == null)
                    {
                        return;
                    }

                    float value = (float)alphaAnimator.getAnimatedValue();

                    int color = (int)(0xab * value);

                    setStatusBarColor((color << 24) & 0xff000000);
                }
            });
        }

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(transAnimator, alphaAnimator);
        mAnimatorSet.setDuration(ANIMATION_DELAY);
        mAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimatorSet.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                mAnimatorSet.removeAllListeners();
                mAnimatorSet = null;

                getEventListener().onHideAnimationEnd();
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

        mAnimatorSet.start();
    }

    void setVisibility(boolean visibility)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().animationLayout.setVisibility(visibility == true ? View.VISIBLE : View.INVISIBLE);
    }

    private ViewCalendarDataBinding getMonthCalendarView(Context context, Pair<String, PlaceCalendarPresenter.Day[]> pair)
    {
        if (context == null || pair == null)
        {
            return null;
        }

        ViewCalendarDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_calendar_data, null, false);

        dataBinding.monthTextView.setText(pair.first);

        View dayView;

        if (mDaysViewList == null)
        {
            mDaysViewList = new ArrayList<>();
        }

        mDaysViewList.clear();

        for (PlaceCalendarPresenter.Day dayClass : pair.second)
        {
            dayView = getDayView(context, dayClass);

            if (dayClass != null)
            {
                mDaysViewList.add(dayView);
            }

            dataBinding.calendarGridLayout.addView(dayView);
        }

        return dataBinding;
    }

    private View getDayView(Context context, PlaceCalendarPresenter.Day day)
    {
        RelativeLayout relativeLayout = new RelativeLayout(context);

        DailyTextView visitTextView = new DailyTextView(context);
        visitTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        visitTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        visitTextView.setTextColor(getColor(R.color.white));
        visitTextView.setDuplicateParentStateEnabled(true);
        visitTextView.setId(R.id.textView);
        visitTextView.setVisibility(View.INVISIBLE);

        RelativeLayout.LayoutParams visitLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        visitLayoutParams.topMargin = ScreenUtils.dpToPx(context, 5);

        relativeLayout.addView(visitTextView, visitLayoutParams);

        DailyTextView dayTextView = new DailyTextView(context);
        dayTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        dayTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        dayTextView.setDuplicateParentStateEnabled(true);

        RelativeLayout.LayoutParams dayLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dayLayoutParams.bottomMargin = ScreenUtils.dpToPx(context, 6);
        dayLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        relativeLayout.addView(dayTextView, dayLayoutParams);

        android.support.v7.widget.GridLayout.LayoutParams layoutParams = new android.support.v7.widget.GridLayout.LayoutParams();
        layoutParams.width = 0;
        layoutParams.height = ScreenUtils.dpToPx(context, 45);
        layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

        relativeLayout.setLayoutParams(layoutParams);
        relativeLayout.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.selector_calendar_day_background));

        if (day == null)
        {
            dayTextView.setText(null);
            relativeLayout.setTag(null);
            relativeLayout.setEnabled(false);
        } else
        {
            switch (day.dayOfWeek)
            {
                // 일요일
                case Calendar.SUNDAY:
                    dayTextView.setTextColor(context.getResources().getColorStateList(R.color.selector_calendar_sunday_textcolor));
                    break;

                case Calendar.SATURDAY:
                    if (day.isHoliday == true)
                    {
                        dayTextView.setTextColor(context.getResources().getColorStateList(R.color.selector_calendar_sunday_textcolor));
                    } else
                    {
                        dayTextView.setTextColor(context.getResources().getColorStateList(R.color.selector_calendar_saturday_textcolor));
                    }
                    break;

                default:
                    if (day.isHoliday == true)
                    {
                        dayTextView.setTextColor(context.getResources().getColorStateList(R.color.selector_calendar_sunday_textcolor));
                    } else
                    {
                        dayTextView.setTextColor(context.getResources().getColorStateList(R.color.selector_calendar_default_text_color));
                    }
                    break;
            }

            dayTextView.setText(day.dayOfMonth);
            relativeLayout.setTag(day);
        }

        relativeLayout.setOnClickListener(this);

        return relativeLayout;
    }

    private void setStatusBarColor(int color)
    {
        if (VersionUtils.isOverAPI21() == true)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }
}
