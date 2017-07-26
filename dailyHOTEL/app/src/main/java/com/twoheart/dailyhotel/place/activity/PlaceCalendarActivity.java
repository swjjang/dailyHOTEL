package com.twoheart.dailyhotel.place.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.repository.local.ConfigLocalImpl;
import com.daily.dailyhotel.repository.remote.FacebookRemoteImpl;
import com.daily.dailyhotel.repository.remote.KakaoRemoteImpl;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

public abstract class PlaceCalendarActivity extends BaseActivity implements View.OnClickListener
{
    protected static final String INTENT_EXTRA_DATA_SCREEN = "screen";
    protected static final String INTENT_EXTRA_DATA_ANIMATION = "animation";
    protected static final String INTENT_EXTRA_DATA_ISSELECTED = "isSelected";
    protected static final String INTENT_EXTRA_DATA_TODAYDATETIME = "todayDateTime";
    protected static final String INTENT_EXTRA_DATA_ISSINGLE_DAY = "isSingleDay"; // 연박 불가
    protected static final String INTENT_EXTRA_DATA_OVERSEAS = "overseas";
    protected static final String INTENT_EXTRA_DATA_SOLDOUT_LIST = "soldoutList";
    protected static final String INTENT_EXTRA_DATA_DAY_OF_MAXCOUNT = "dayOfMaxCount";

    private static final int ANIMATION_DELAY = 200;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    protected List<View> mDayViewList;

    protected View mAnimationLayout; // 애니메이션 되는 뷰
    private View mDisableLayout; // 전체 화면을 덮는 뷰
    protected View mExitView;
    View mBackgroundView; // 뒷배경
    private ViewGroup mCalendarsLayout;

    private TextView mTitleTextView;
    protected String mCallByScreen;

    protected TodayDateTime mTodayDateTime;
    protected PlaceBookingDay mPlaceBookingDay;

    ANIMATION_STATUS mAnimationStatus = ANIMATION_STATUS.HIDE_END;
    ANIMATION_STATE mAnimationState = ANIMATION_STATE.END;
    AnimatorSet mAnimatorSet;

    protected ArrayList<Integer> mHolidayList;

    protected ArrayList<Integer> mSoldOutDayList;

    private int mDayOfMaxCount;

    protected void setDayOfMaxCount(int dayOfMaxCount)
    {
        mDayOfMaxCount = dayOfMaxCount;
    }

    protected int getDayOfMaxCount()
    {
        return mDayOfMaxCount < 0 ? 0 : mDayOfMaxCount;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // VersionUtils.isOverAPI21()
        setStatusBarColor(getResources().getColor(R.color.black_a67));

        // 휴일 정보를 얻어온다.
        initHolidays();
    }

    private void initHolidays()
    {
        String calendarHolidays = DailyPreference.getInstance(this).getCalendarHolidays();

        if (DailyTextUtils.isTextEmpty(calendarHolidays) == false)
        {
            String[] holidays = calendarHolidays.split("\\,");
            mHolidayList = new ArrayList<>();

            for (int i = 0; i < holidays.length; i++)
            {
                try
                {
                    mHolidayList.add(Integer.parseInt(holidays[i]));
                } catch (NumberFormatException e)
                {
                    ExLog.e(e.toString());
                }
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        Util.restartApp(this);
    }

    @Override
    protected void onDestroy()
    {
        clearCompositeDisposable();

        super.onDestroy();
    }

    protected void initLayout(int layoutResID, final int dayCountOfMax)
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

        mDayViewList = new ArrayList<>();
    }

    protected ArrayList<Pair<String, Day[]>> makeCalendarList(TodayDateTime todayDateTime, int dayCountOfMax)
    {
        ArrayList<Pair<String, Day[]>> arrayList = new ArrayList<>();

        Date todayDate;

        try
        {
            todayDate = DailyCalendar.convertStringToDate(todayDateTime.dailyDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            return null;
        }

        Calendar todayCalendar = DailyCalendar.getInstance();
        todayCalendar.setTime(todayDate);

        ArrayList<Integer> holidayList = new ArrayList<>();
        if (mHolidayList != null && mHolidayList.size() > 0)
        {
            holidayList.addAll(mHolidayList);
        }

        ArrayList<Integer> soldOutDayList = new ArrayList<>();
        if (mSoldOutDayList != null && mSoldOutDayList.size() > 0)
        {
            soldOutDayList.addAll(mSoldOutDayList);
        }

        int maxMonth = getMonthInterval(todayCalendar, dayCountOfMax);

        // 초기 설정 - for 문을 돌면서 카운트 감소 됨
        int dayCount = dayCountOfMax;

        for (int i = 0; i <= maxMonth; i++)
        {
            String titleMonth = DailyCalendar.format(todayCalendar.getTime(), "yyyy.MM");

            Pair<Integer, Day[]> daysPair = getMonthCalendar(todayCalendar, dayCount, dayCountOfMax, holidayList, soldOutDayList);
            Day[] days = null;
            if (daysPair != null)
            {
                dayCount = daysPair.first;
                days = daysPair.second;
            }

            arrayList.add(new Pair(titleMonth, days));

            todayCalendar.set(Calendar.DAY_OF_MONTH, 1);
            todayCalendar.add(Calendar.MONTH, 1);
        }

        return arrayList;
    }

    private Pair<Integer, Day[]> getMonthCalendar(Calendar calendar, int dayCount, final int dayCountOfMax, ArrayList<Integer> holidayList, ArrayList<Integer> soldOutDayList)
    {
        int todayDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int maxDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int todayValue = calendar.get(Calendar.DAY_OF_MONTH);
        int todayMonthValue = calendar.get(Calendar.MONTH);

        boolean isStart = false;
        boolean isLast = false;

        // 남은 값과 최대 값이 같으면 시작 달력
        if (dayCount == dayCountOfMax)
        {
            isStart = true;
        }

        // 남은 값이 이달의 최대 값과 같거나 작으면 마지막 달력
        if (todayValue + dayCount + 1 <= maxDayOfMonth)
        {
            isLast = true;
        }

        maxDayOfMonth = isLast == false ? maxDayOfMonth : todayValue + dayCount + 1;

        int startGap = 0;

        if (isStart == true)
        {
            startGap = Calendar.SUNDAY - todayDayOfWeek;
        }

        Calendar cloneCalendar = (Calendar) calendar.clone();
        if (startGap != 0)
        {
            cloneCalendar.add(Calendar.DAY_OF_MONTH, startGap);
        }

        int startDayValue = cloneCalendar.get(Calendar.DAY_OF_MONTH);
        int startDayOfWeek = cloneCalendar.get(Calendar.DAY_OF_WEEK);

        int endCount = dayCount - maxDayOfMonth + todayValue;

        final int LENGTH_OF_WEEK = 7;
        int length = maxDayOfMonth - startDayValue + startDayOfWeek;
        if (length % LENGTH_OF_WEEK != 0)
        {
            length += (LENGTH_OF_WEEK - (length % LENGTH_OF_WEEK));
        }

        Day[] days = new Day[length];

        for (int i = startDayOfWeek - 1; i < length; i++)
        {
            int dayValue = cloneCalendar.get(Calendar.DAY_OF_MONTH);
            int monthValue = cloneCalendar.get(Calendar.MONTH);

            days[i] = new Day();
            days[i].dateTime = DailyCalendar.format(cloneCalendar.getTime(), DailyCalendar.ISO_8601_FORMAT);
            days[i].dayOfMonth = Integer.toString(dayValue);
            days[i].dayOfWeek = cloneCalendar.get(Calendar.DAY_OF_WEEK);
            days[i].isHoliday = isHoliday(cloneCalendar, holidayList);
            days[i].isSoldOut = isSoldOutDay(cloneCalendar, soldOutDayList);
            days[i].isDefaultDimmed = isStart == true && (dayValue < todayValue || monthValue < todayMonthValue) //
                || isLast == true && dayCount < 0;

            if (isLast == false && dayCount <= endCount)
            {
                break;
            }

            if (days[i].isDefaultDimmed == false)
            {
                dayCount--;
            }

            cloneCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return new Pair(dayCount, days);
    }

    protected void makeCalendar(TodayDateTime mTodayDateTime, int dayCountOfMax)
    {
        ArrayList<Pair<String, Day[]>> calendarList = makeCalendarList(mTodayDateTime, dayCountOfMax);

        if (calendarList == null)
        {
            return;
        }

        int size = calendarList.size();

        if (mDayViewList == null)
        {
            mDayViewList = new ArrayList<>();
        }

        mDayViewList.clear();

        for (int i = 0; i < size; i++)
        {
            Pair<String, Day[]> pair = calendarList.get(i);

            View monthCalendarLayout = getMonthCalendarView(this, pair);

            if (i >= 0 && i < size)
            {
                monthCalendarLayout.setPadding(monthCalendarLayout.getPaddingLeft(), monthCalendarLayout.getPaddingTop()//
                    , monthCalendarLayout.getPaddingRight(), monthCalendarLayout.getPaddingBottom() + ScreenUtils.dpToPx(this, 30));
            }

            mCalendarsLayout.addView(monthCalendarLayout);

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

    private View getMonthCalendarView(Context context, Pair<String, Day[]> pair)
    {
        View monthCalendarLayout = LayoutInflater.from(context).inflate(R.layout.view_calendar, null);
        TextView monthTextView = (TextView) monthCalendarLayout.findViewById(R.id.monthTextView);
        android.support.v7.widget.GridLayout calendarGridLayout = (android.support.v7.widget.GridLayout) monthCalendarLayout.findViewById(R.id.calendarGridLayout);

        monthTextView.setText(pair.first);

        View dayView;
        for (Day day : pair.second)
        {
            dayView = getDayView(context, day);

            if (day != null && day.isDefaultDimmed == false)
            {
                mDayViewList.add(dayView);
            }

            calendarGridLayout.addView(dayView);
        }

        return monthCalendarLayout;
    }

    private boolean isHoliday(Calendar calendar, ArrayList<Integer> holidayList)
    {
        if (calendar == null)
        {
            return false;
        }

        if (holidayList == null || holidayList.size() == 0)
        {
            return false;
        }

        try
        {
            int calendarDay = Integer.parseInt(DailyCalendar.format(calendar.getTime(), "yyyyMMdd"));
            return holidayList.remove((Integer) calendarDay);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return false;
    }

    private boolean isSoldOutDay(Calendar calendar, ArrayList<Integer> soldOutDayList)
    {
        if (calendar == null)
        {
            return false;
        }

        if (soldOutDayList == null || soldOutDayList.size() == 0)
        {
            return false;
        }

        try
        {
            int calendarDay = Integer.parseInt(DailyCalendar.format(calendar.getTime(), "yyyyMMdd"));
            return soldOutDayList.remove((Integer) calendarDay);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return false;
    }

    public View getDayView(Context context, Day day)
    {
        RelativeLayout relativeLayout = new RelativeLayout(context);

        DailyTextView visitTextView = new DailyTextView(context);
        visitTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        visitTextView.setTypeface(FontManager.getInstance(this).getRegularTypeface());
        visitTextView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        visitTextView.setTextColor(context.getResources().getColorStateList(R.color.selector_calendar_default_text_color));
        visitTextView.setDuplicateParentStateEnabled(true);
        visitTextView.setId(R.id.textView);
        visitTextView.setVisibility(View.INVISIBLE);

        RelativeLayout.LayoutParams visitLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        visitLayoutParams.topMargin = ScreenUtils.dpToPx(context, 5);

        relativeLayout.addView(visitTextView, visitLayoutParams);

        DailyTextView dayTextView = new DailyTextView(context);
        dayTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        dayTextView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        dayTextView.setId(R.id.dateTextView);
        dayTextView.setDuplicateParentStateEnabled(true);

        RelativeLayout.LayoutParams dayLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dayLayoutParams.bottomMargin = ScreenUtils.dpToPx(context, 6);
        dayLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        relativeLayout.addView(dayTextView, dayLayoutParams);

        android.support.v7.widget.GridLayout.LayoutParams layoutParams = new android.support.v7.widget.GridLayout.LayoutParams();
        layoutParams.width = 0;
        layoutParams.height = ScreenUtils.dpToPx(context, 45);
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
            dayTextView.setText(day.dayOfMonth);
            relativeLayout.setTag(day);
        }

        updateDayView(relativeLayout);

        relativeLayout.setOnClickListener(this);

        return relativeLayout;
    }

    protected void updateDayView(View dayView)
    {
        if (dayView == null)
        {
            return;
        }

        Day day = (Day) dayView.getTag();
        if (day == null)
        {
            dayView.setEnabled(false);
            return;
        }

        setDayOfWeekTextColor(dayView);

        if (dayView.isSelected() == true)
        {
            return;
        }

        setSoldOutTextView(dayView, day.isSoldOut);

        if (day.isSoldOut == true)
        {
            dayView.setEnabled(false);
            return;
        }

        if (day.isDefaultDimmed == true)
        {
            dayView.setEnabled(false);
        } else
        {
            dayView.setEnabled(true);
        }
    }

    protected void setSoldOutTextView(View dayView, boolean isShow)
    {
        if (dayView == null)
        {
            return;
        }

        TextView visitTextView = (TextView) dayView.findViewById(R.id.textView);
        TextView dayTextView = (TextView) dayView.findViewById(R.id.dateTextView);

        if (isShow == false)
        {
            visitTextView.setText(null);
            visitTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
            visitTextView.setTypeface(FontManager.getInstance(this).getRegularTypeface());
            visitTextView.setVisibility(View.INVISIBLE);

            RelativeLayout.LayoutParams visitLayoutParams = (RelativeLayout.LayoutParams) visitTextView.getLayoutParams();
            visitLayoutParams.topMargin = ScreenUtils.dpToPx(this, 5);
            visitTextView.setLayoutParams(visitLayoutParams);

            if ((dayTextView.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) == Paint.STRIKE_THRU_TEXT_FLAG)
            {
                dayTextView.setPaintFlags(dayTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            return;
        }

        visitTextView.setText(R.string.label_calendar_soldout);
        visitTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 8);
        visitTextView.setTypeface(FontManager.getInstance(this).getMediumTypeface());
        visitTextView.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams visitLayoutParams = (RelativeLayout.LayoutParams) visitTextView.getLayoutParams();
        visitLayoutParams.topMargin = ScreenUtils.dpToPx(this, 8);
        visitTextView.setLayoutParams(visitLayoutParams);

        TextPaint textPaint = dayTextView.getPaint();
        ExLog.d(textPaint.getStrokeCap() + " : " + textPaint.getStrokeJoin() + " : " + textPaint.getStrokeWidth() + " : " + textPaint.getStrokeMiter());

        dayTextView.setPaintFlags(dayTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    protected void setDayOfWeekTextColor(View dayView)
    {
        TextView dayTextView = (TextView) dayView.findViewById(R.id.dateTextView);

        Day day = (Day) dayView.getTag();

        if (day == null)
        {
            return;
        }

        switch (day.dayOfWeek)
        {
            // 일요일
            case Calendar.SUNDAY:
                dayTextView.setTextColor(getResources().getColorStateList(R.color.selector_calendar_sunday_textcolor));
                break;

            case Calendar.SATURDAY:
                if (day.isHoliday == true)
                {
                    dayTextView.setTextColor(getResources().getColorStateList(R.color.selector_calendar_sunday_textcolor));
                } else
                {
                    dayTextView.setTextColor(getResources().getColorStateList(R.color.selector_calendar_saturday_textcolor));
                }
                break;

            default:
                if (day.isHoliday == true)
                {
                    dayTextView.setTextColor(getResources().getColorStateList(R.color.selector_calendar_sunday_textcolor));
                } else
                {
                    dayTextView.setTextColor(getResources().getColorStateList(R.color.selector_calendar_default_text_color));
                }
                break;
        }
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

    protected void smoothScrollCheckInDayPosition(View checkInDayView)
    {
        ScrollView scrollView = (ScrollView) findViewById(R.id.calendarScrollLayout);

        if (checkInDayView == null)
        {
            if (mDayViewList == null || mDayViewList.size() == 0)
            {
                return;
            }

            for (View dayView : mDayViewList)
            {
                if (dayView.isSelected() == true)
                {
                    checkInDayView = dayView;
                    break;
                }
            }

            if (checkInDayView == null)
            {
                return;
            }

        }

        final View selectView = checkInDayView;

        scrollView.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                View view = (View) selectView.getParent().getParent();
                int scrollTop = view.getTop();

                scrollView.smoothScrollBy(0, scrollTop);
            }
        }, 200);
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

        if (VersionUtils.isOverAPI12() == true)
        {
            final float y = mAnimationLayout.getBottom();

            if (mAnimatorSet != null && mAnimatorSet.isRunning() == true)
            {
                mAnimatorSet.cancel();
            }

            // 리스트 높이 + 아이콘 높이(실제 화면에 들어나지 않기 때문에 높이가 정확하지 않아서 내부 높이를 더함)
            int height = mAnimationLayout.getHeight();

            mAnimationLayout.setTranslationY(ScreenUtils.dpToPx(this, height));

            ObjectAnimator transAnimator = ObjectAnimator.ofFloat(mAnimationLayout, "y", y, y - height);

            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.play(transAnimator);
            mAnimatorSet.setDuration(ANIMATION_DELAY);
            mAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            mAnimatorSet.addListener(new Animator.AnimatorListener()
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
                    mAnimatorSet.removeAllListeners();
                    mAnimatorSet = null;

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

            mAnimatorSet.start();
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

        if (VersionUtils.isOverAPI12() == true)
        {
            final float y = mAnimationLayout.getTop();

            if (mAnimatorSet != null && mAnimatorSet.isRunning() == true)
            {
                mAnimatorSet.cancel();
            }

            ObjectAnimator transAnimator = ObjectAnimator.ofFloat(mAnimationLayout, "y", y, mAnimationLayout.getBottom());
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mBackgroundView, "alpha", 1f, 0f);

            if (VersionUtils.isOverAPI21() == true)
            {
                alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation)
                    {
                        if (animation == null)
                        {
                            return;
                        }

                        float value = (float) alphaAnimator.getAnimatedValue();

                        int color = (int) (0xab * value);

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

                    mAnimationState = ANIMATION_STATE.START;
                    mAnimationStatus = ANIMATION_STATUS.HIDE;

                    setTouchEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    if (VersionUtils.isOverAPI21() == true)
                    {
                        alphaAnimator.removeAllUpdateListeners();
                    }

                    mAnimatorSet.removeAllListeners();
                    mAnimatorSet = null;

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

            mAnimatorSet.start();
        } else
        {
            mAnimationStatus = ANIMATION_STATUS.HIDE_END;
            mAnimationState = ANIMATION_STATE.END;

            finish();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(int color)
    {
        if (VersionUtils.isOverAPI21() == true)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    protected static class Day
    {
        public String dateTime; // ISO-8601 format
        public String dayOfMonth;
        public int dayOfWeek;
        public boolean isHoliday;
        public boolean isSoldOut;
        public boolean isDefaultDimmed;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // 기존의 BaseActivity에 있는 정보 가져오기
    ///////////////////////////////////////////////////////////////////////////////////////////////

    protected void addCompositeDisposable(Disposable disposable)
    {
        if (disposable == null)
        {
            return;
        }

        mCompositeDisposable.add(disposable);
    }

    protected void clearCompositeDisposable()
    {
        mCompositeDisposable.clear();
    }

    protected void onHandleError(Throwable throwable)
    {
        unLockUI();

        BaseActivity baseActivity = PlaceCalendarActivity.this;

        if (baseActivity == null || baseActivity.isFinishing() == true)
        {
            return;
        }

        if (throwable instanceof BaseException)
        {
            // 팝업 에러 보여주기
            BaseException baseException = (BaseException) throwable;

            baseActivity.showSimpleDialog(null, baseException.getMessage()//
                , getString(R.string.dialog_btn_text_confirm), null, null, null, null, dialogInterface -> PlaceCalendarActivity.this.onBackPressed(), true);
        } else if (throwable instanceof HttpException)
        {
            retrofit2.HttpException httpException = (HttpException) throwable;

            if (httpException.code() == BaseException.CODE_UNAUTHORIZED)
            {
                addCompositeDisposable(new ConfigLocalImpl(PlaceCalendarActivity.this).clear().subscribe(object ->
                {
                    new FacebookRemoteImpl().logOut();
                    new KakaoRemoteImpl().logOut();

                    baseActivity.restartExpiredSession();
                }));
            } else
            {
                DailyToast.showToast(PlaceCalendarActivity.this, getString(R.string.act_base_network_connect), DailyToast.LENGTH_LONG);

                Crashlytics.log(httpException.response().raw().request().url().toString());
                Crashlytics.logException(throwable);

                PlaceCalendarActivity.this.finish();
            }
        } else
        {
            DailyToast.showToast(PlaceCalendarActivity.this, getString(R.string.act_base_network_connect), DailyToast.LENGTH_LONG);

            PlaceCalendarActivity.this.finish();
        }
    }
}
