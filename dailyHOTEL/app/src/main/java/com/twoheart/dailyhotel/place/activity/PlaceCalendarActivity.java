package com.twoheart.dailyhotel.place.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyTextView;

import java.util.Calendar;
import java.util.TimeZone;

public abstract class PlaceCalendarActivity extends BaseActivity implements View.OnClickListener
{
    public static final String INTENT_EXTRA_DATA_SCREEN = "screen";
    public static final String INTENT_EXTRA_DATA_ANIMATION = "animation";
    public static final String INTENT_EXTRA_DATA_ISSELECTED = "isSelected";
    public static final String INTENT_EXTRA_DATA_START_SALETIME = "startSaleTime";
    public static final String INTENT_EXTRA_DATA_END_SALETIME = "endSaleTime";

    private static final int ANIMATION_DEALY = 200;

    protected View[] mDailyViews;

    protected View mAnimationLayout; // 애니메이션 되는 뷰
    private View mDisableLayout; // 전체 화면을 덮는 뷰
    protected View mExitView;
    private View mBackgroundView; // 뒷배경
    private ViewGroup mCalendarsLayout;

    private TextView mTitleTextView;
    protected String mCallByScreen;

    protected SaleTime mStartSaleTime, mEndSaleTime;

    private ANIMATION_STATUS mAnimationStatus = ANIMATION_STATUS.HIDE_END;
    private ANIMATION_STATE mAnimationState = ANIMATION_STATE.END;
    private ObjectAnimator mObjectAnimator;
    private AlphaAnimation mAlphaAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (Util.isOverAPI21() == true)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.black_a67));
        }
    }

    protected void initLayout(int layoutResID, final SaleTime dailyTime, final int dayCountOfMax)
    {
        setContentView(layoutResID);

        mCalendarsLayout = (ViewGroup) findViewById(R.id.calendarLayout);
        ScrollView scrollView = (ScrollView) findViewById(R.id.calendarScrollLayout);
        EdgeEffectColor.setEdgeGlowColor(scrollView, getResources().getColor(R.color.default_over_scroll_edge));

        View closeView = findViewById(R.id.closeView);
        closeView.setOnClickListener(this);

        mExitView = findViewById(R.id.exitView);
        mExitView.setOnClickListener(this);

        mAnimationLayout = findViewById(R.id.animationLayout);
        mDisableLayout = findViewById(R.id.disableLayout);
        mBackgroundView = (View) mExitView.getParent();

        mDailyViews = new View[dayCountOfMax];
    }

    protected void makeCalendar(SaleTime dailyTime, int dayCountOfMax)
    {
        Calendar calendar = DailyCalendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(dailyTime.getDailyTime());

        int maxMonth = getMonthInterval(calendar, dayCountOfMax);
        int maxDay = dayCountOfMax;
        int dayCount = 0;

        for (int i = 0; i <= maxMonth; i++)
        {
            int maxDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            View calendarLayout = getMonthCalendarView(this, dailyTime.getClone(dayCount)//
                , calendar, day + maxDay - 1 > maxDayOfMonth ? maxDayOfMonth : day + maxDay - 1);

            if (i >= 0 && i < maxMonth)
            {
                calendarLayout.setPadding(calendarLayout.getPaddingLeft(), calendarLayout.getPaddingTop()//
                    , calendarLayout.getPaddingRight(), calendarLayout.getPaddingBottom() + Util.dpToPx(this, 30));
            }

            mCalendarsLayout.addView(calendarLayout);

            dayCount += maxDayOfMonth - day + 1;
            maxDay = dayCountOfMax - dayCount;

            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        }
    }

    protected void initToolbar(String title)
    {
        mTitleTextView = (TextView) findViewById(R.id.titleTextView);
        setToolbarText(title);
    }

    protected void setToolbarText(String title)
    {
        mTitleTextView.setText(title);
    }

    private View getMonthCalendarView(Context context, final SaleTime dailyTime, final Calendar calendar, final int maxDayOfMonth)
    {
        View calendarLayout = LayoutInflater.from(context).inflate(R.layout.view_calendar, null);

        TextView monthTextView = (TextView) calendarLayout.findViewById(R.id.monthTextView);
        android.support.v7.widget.GridLayout calendarGridLayout = (android.support.v7.widget.GridLayout) calendarLayout.findViewById(R.id.calendarGridLayout);

        //        SimpleDateFormat simpleDayFormat = new SimpleDateFormat("yyyy.MM", Locale.KOREA);
        //        simpleDayFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        //
        //        monthTextView.setText(simpleDayFormat.format(calendar.getTime()));
        monthTextView.setText(DailyCalendar.format(calendar.getTimeInMillis(), "yyyy.MM", TimeZone.getTimeZone("GMT")));

        // day
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        int length = maxDayOfMonth - day + 1 + dayOfWeek;
        final int LENGHT_OF_WEEK = 7;

        if (length % LENGHT_OF_WEEK != 0)
        {
            length += (LENGHT_OF_WEEK - (length % LENGHT_OF_WEEK));
        }

        Day[] days = new Day[length];

        Calendar cloneCalendar = (Calendar) calendar.clone();

        for (int i = 0, j = dayOfWeek, k = day; k <= maxDayOfMonth; i++, j++, k++)
        {
            days[j] = new Day();
            days[j].dayTime = dailyTime.getClone(dailyTime.getOffsetDailyDay() + i);
            days[j].day = Integer.toString(cloneCalendar.get(Calendar.DAY_OF_MONTH));
            days[j].dayOfWeek = cloneCalendar.get(Calendar.DAY_OF_WEEK);

            cloneCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        View dayView;

        for (Day dayClass : days)
        {
            dayView = getDayView(context, dayClass);

            if (dayClass != null)
            {
                mDailyViews[dayClass.dayTime.getOffsetDailyDay()] = dayView;
            }

            calendarGridLayout.addView(dayView);
        }

        return calendarLayout;
    }

    public View getDayView(Context context, Day day)
    {
        RelativeLayout relativeLayout = new RelativeLayout(context);

        DailyTextView visitTextView = new DailyTextView(context);
        visitTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        visitTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        visitTextView.setTextColor(getResources().getColor(R.color.white));
        visitTextView.setDuplicateParentStateEnabled(true);
        visitTextView.setId(R.id.textView);
        visitTextView.setVisibility(View.INVISIBLE);

        RelativeLayout.LayoutParams visitLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        visitLayoutParams.topMargin = Util.dpToPx(context, 5);

        relativeLayout.addView(visitTextView, visitLayoutParams);

        DailyTextView dayTextView = new DailyTextView(context);
        dayTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        dayTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        dayTextView.setDuplicateParentStateEnabled(true);

        RelativeLayout.LayoutParams dayLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dayLayoutParams.bottomMargin = Util.dpToPx(context, 6);
        dayLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        relativeLayout.addView(dayTextView, dayLayoutParams);

        android.support.v7.widget.GridLayout.LayoutParams layoutParams = new android.support.v7.widget.GridLayout.LayoutParams();
        layoutParams.width = 0;
        layoutParams.height = Util.dpToPx(context, 45);
        layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

        relativeLayout.setLayoutParams(layoutParams);
        relativeLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_calendar_day_background));

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
                case 1:
                    dayTextView.setTextColor(context.getResources().getColorStateList(R.color.selector_calendar_sunday_textcolor));
                    break;

                default:
                    dayTextView.setTextColor(context.getResources().getColorStateList(R.color.selector_calendar_default_text_color));
                    break;
            }

            dayTextView.setText(day.day);
            relativeLayout.setTag(day);
        }

        relativeLayout.setOnClickListener(this);

        return relativeLayout;
    }

    private int getMonthInterval(final Calendar calendar, int interval)
    {
        Calendar lastMonthCalendar = (Calendar) calendar.clone();
        lastMonthCalendar.add(Calendar.DAY_OF_MONTH, interval - 1);

        int lastMonth = lastMonthCalendar.get(Calendar.MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);

        if (currentMonth > lastMonth)
        {
            return 12 - currentMonth + lastMonth;
        } else
        {
            return lastMonth - currentMonth;
        }
    }

    protected void setTouchEnabled(boolean enabled)
    {
        if (enabled == true)
        {
            mDisableLayout.setVisibility(View.GONE);
            mDisableLayout.setOnClickListener(null);
        } else
        {
            mDisableLayout.setVisibility(View.VISIBLE);
            mDisableLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                }
            });
        }
    }

    protected void showAnimation()
    {
        if (mAnimationLayout == null)
        {
            Util.restartApp(this);
            return;
        }

        if (mAnimationState == ANIMATION_STATE.START && mAnimationStatus == ANIMATION_STATUS.SHOW)
        {
            return;
        }

        if (Util.isOverAPI12() == true)
        {
            final float y = mAnimationLayout.getBottom();

            if (mObjectAnimator != null)
            {
                if (mObjectAnimator.isRunning() == true)
                {
                    mObjectAnimator.cancel();
                    mObjectAnimator.removeAllListeners();
                }

                mObjectAnimator = null;
            }

            // 리스트 높이 + 아이콘 높이(실제 화면에 들어나지 않기 때문에 높이가 정확하지 않아서 내부 높이를 더함)
            int height = mAnimationLayout.getHeight();

            mAnimationLayout.setTranslationY(Util.dpToPx(this, height));

            mObjectAnimator = ObjectAnimator.ofFloat(mAnimationLayout, "y", y, y - height);
            mObjectAnimator.setDuration(ANIMATION_DEALY);

            mObjectAnimator.addListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    if (mAnimationLayout.getVisibility() != View.VISIBLE)
                    {
                        mAnimationLayout.setVisibility(View.VISIBLE);
                    }

                    setTouchEnabled(false);

                    mAnimationState = ANIMATION_STATE.START;
                    mAnimationStatus = ANIMATION_STATUS.SHOW;
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    if (mAnimationState != ANIMATION_STATE.CANCEL)
                    {
                        mAnimationStatus = ANIMATION_STATUS.SHOW_END;
                        mAnimationState = ANIMATION_STATE.END;
                    }

                    setTouchEnabled(true);
                }

                @Override
                public void onAnimationCancel(Animator animation)
                {
                    mAnimationState = ANIMATION_STATE.CANCEL;

                    setTouchEnabled(true);
                }

                @Override
                public void onAnimationRepeat(Animator animation)
                {

                }
            });

            mObjectAnimator.start();

            //            showAnimationFadeOut();
        } else
        {
            if (mAnimationLayout != null && mAnimationLayout.getVisibility() != View.VISIBLE)
            {
                mAnimationLayout.setVisibility(View.VISIBLE);

                mAnimationStatus = ANIMATION_STATUS.SHOW_END;
                mAnimationState = ANIMATION_STATE.END;
            }
        }
    }

    protected void hideAnimation()
    {
        if (mAnimationLayout == null)
        {
            Util.restartApp(this);
            return;
        }

        if (mAnimationState == ANIMATION_STATE.START && mAnimationStatus == ANIMATION_STATUS.HIDE)
        {
            return;
        }

        if (Util.isOverAPI12() == true)
        {
            final float y = mAnimationLayout.getTop();

            if (mObjectAnimator != null)
            {
                if (mObjectAnimator.isRunning() == true)
                {
                    mObjectAnimator.cancel();
                    mObjectAnimator.removeAllListeners();
                }

                mObjectAnimator = null;
            }

            mObjectAnimator = ObjectAnimator.ofFloat(mAnimationLayout, "y", y, mAnimationLayout.getBottom());
            mObjectAnimator.setDuration(ANIMATION_DEALY);

            mObjectAnimator.addListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {

                    mAnimationState = ANIMATION_STATE.START;
                    mAnimationStatus = ANIMATION_STATUS.HIDE;

                    setTouchEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    if (mAnimationState != ANIMATION_STATE.CANCEL)
                    {
                        mAnimationStatus = ANIMATION_STATUS.HIDE_END;
                        mAnimationState = ANIMATION_STATE.END;

                        mBackgroundView.setVisibility(View.GONE);

                        finish();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation)
                {
                    mAnimationState = ANIMATION_STATE.CANCEL;

                    mBackgroundView.setVisibility(View.GONE);

                    finish();
                }

                @Override
                public void onAnimationRepeat(Animator animation)
                {
                }
            });

            mObjectAnimator.start();

            showAnimationFadeIn();
        } else
        {
            mAnimationStatus = ANIMATION_STATUS.HIDE_END;
            mAnimationState = ANIMATION_STATE.END;

            finish();
        }
    }

    /**
     * 점점 밝아짐.
     */
    private void showAnimationFadeIn()
    {
        if (mAlphaAnimation != null)
        {
            if (mAlphaAnimation.hasEnded() == false)
            {
                mAlphaAnimation.cancel();
            }

            mAlphaAnimation = null;
        }

        mAlphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        mAlphaAnimation.setDuration(ANIMATION_DEALY);
        mAlphaAnimation.setFillBefore(true);
        mAlphaAnimation.setFillAfter(true);

        mAlphaAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }
        });

        mBackgroundView.startAnimation(mAlphaAnimation);
    }

    protected static class Day
    {
        public SaleTime dayTime;
        String day;
        int dayOfWeek;
    }
}
