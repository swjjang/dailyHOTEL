package com.twoheart.dailyhotel.place.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.ScrollView;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyDayStrikeTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Deprecated
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
    protected static final String INTENT_EXTRA_DATA_VISIT_DATE_TIME = "visitDateTime";

    private static final int ANIMATION_DELAY = 200;

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

    protected abstract void setResult(int resultCode, PlaceBookingDay placeBookingDay);

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

            for (String holiday : holidays)
            {
                try
                {
                    mHolidayList.add(Integer.parseInt(holiday));
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

    protected void initLayout(int layoutResID, final int dayCountOfMax)
    {
        setContentView(layoutResID);

        mCalendarsLayout = findViewById(R.id.calendarLayout);
        ScrollView scrollView = findViewById(R.id.calendarScrollLayout);
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

    protected ArrayList<Pair<String, Day[]>> makeCalendarList(Calendar startCalendar, Calendar endCalendar, boolean isLastSoldOutCheck)
    {
        ArrayList<Pair<String, Day[]>> arrayList = new ArrayList<>();
        if (startCalendar == null || endCalendar == null)
        {
            return arrayList;
        }

        ArrayList<Integer> holidayList = new ArrayList<>();
        if (mHolidayList != null && mHolidayList.size() > 0)
        {
            holidayList.addAll(mHolidayList);
        }

        ArrayList<Integer> soldOutDayList = new ArrayList<>();
        if (mSoldOutDayList != null && mSoldOutDayList.size() > 0)
        {
            //            soldOutDayList.setData(mSoldOutDayList);

            int endDateValue = Integer.parseInt(DailyCalendar.format(endCalendar.getTime(), "yyyyMMdd"));
            for (int soldOutDayValue : mSoldOutDayList)
            {
                // 고메의 경우 단 1일만 선택 가능하기에 마지막 날짜도 SoldOut 체크를 해야 함 - 스테이는 마직막 전날까지 계산
                boolean isSoldOut = isLastSoldOutCheck == true //
                    ? soldOutDayValue <= endDateValue : soldOutDayValue < endDateValue;
                if (isSoldOut == true)
                {
                    soldOutDayList.add(soldOutDayValue);
                }
            }
        }

        Calendar todayCalendar = (Calendar) startCalendar.clone();

        int maxMonth = getMonthInterval(startCalendar, endCalendar);

        for (int i = 0; i <= maxMonth; i++)
        {
            String titleMonth = DailyCalendar.format(todayCalendar.getTime(), "yyyy.MM");

            Day[] days = getMonthCalendar(todayCalendar, startCalendar, endCalendar, holidayList, soldOutDayList);

            arrayList.add(new Pair(titleMonth, days));

            todayCalendar.set(Calendar.DAY_OF_MONTH, 1);
            todayCalendar.add(Calendar.MONTH, 1);
        }

        return arrayList;
    }

    private Day[] getMonthCalendar(Calendar todayCalendar, Calendar startCalendar, Calendar endCalendar, ArrayList<Integer> holidayList, ArrayList<Integer> soldOutDayList)
    {
        long startTimeInMillis = startCalendar.getTimeInMillis();
        long endTimeInMillis = endCalendar.getTimeInMillis();

        int endDayValue = endCalendar.get(Calendar.DAY_OF_MONTH);
        int endMonthValue = endCalendar.get(Calendar.MONTH);

        int todayMaxDayOfMonth = todayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int todayMonth = todayCalendar.get(Calendar.MONTH);
        int todayDayOfWeek = todayCalendar.get(Calendar.DAY_OF_WEEK);

        boolean isLast = false;
        if (todayMonth == endMonthValue)
        {
            isLast = true;
            todayMaxDayOfMonth = endDayValue;
        }

        int startGap = 0;
        if (todayMonth == startCalendar.get(Calendar.MONTH))
        {
            startGap = Calendar.SUNDAY - todayDayOfWeek;
        }

        Calendar cloneCalendar = (Calendar) todayCalendar.clone();
        // 처음 달인 경우만 스타트 데이의 갭을 설정해 준다.
        if (startGap != 0)
        {
            cloneCalendar.add(Calendar.DAY_OF_MONTH, startGap);
        }

        int currentDayOfWeek = cloneCalendar.get(Calendar.DAY_OF_WEEK);

        int length = getMonthCalendarLength(todayCalendar, startCalendar, endCalendar);

        Day[] days = new Day[length];

        for (int i = currentDayOfWeek - 1; i < length; i++)
        {
            int dayValue = cloneCalendar.get(Calendar.DAY_OF_MONTH);
            int monthValue = cloneCalendar.get(Calendar.MONTH);
            long currentTimeMillis = cloneCalendar.getTimeInMillis();

            days[i] = new Day();
            days[i].dateTime = DailyCalendar.format(cloneCalendar.getTime(), DailyCalendar.ISO_8601_FORMAT);
            days[i].dayOfMonth = Integer.toString(dayValue);
            days[i].dayOfWeek = cloneCalendar.get(Calendar.DAY_OF_WEEK);
            days[i].isHoliday = isHoliday(cloneCalendar, holidayList);
            days[i].isSoldOut = isSoldOutDay(cloneCalendar, soldOutDayList);

            // 현재 날짜의 시간이 시작 날짜보다 작거나 종료 날짜보다 클 경우
            days[i].isDefaultDimmed = currentTimeMillis < startTimeInMillis || currentTimeMillis > endTimeInMillis;

            if (isLast == false && monthValue == todayMonth && dayValue == todayMaxDayOfMonth)
            {
                break;
            }

            cloneCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return days;
    }

    protected void makeCalendar(TodayDateTime todayDateTime, int dayCountOfMax, boolean isLastSoldOutCheck)
    {
        Date todayDate;

        try
        {
            todayDate = DailyCalendar.convertStringToDate(todayDateTime.dailyDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            return;
        }

        Calendar startCalendar = DailyCalendar.getInstance();
        startCalendar.setTime(todayDate);

        Calendar endCalendar = (Calendar) startCalendar.clone();
        // 마지막 날짜는 start day 를 1로 잡음으로 하루를 빼고 계산 해야 함
        endCalendar.add(Calendar.DAY_OF_MONTH, dayCountOfMax - 1);

        ArrayList<Pair<String, Day[]>> calendarList = makeCalendarList(startCalendar, endCalendar, isLastSoldOutCheck);

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

            if (i < size - 1)
            {
                monthCalendarLayout.setPadding(monthCalendarLayout.getPaddingLeft(), monthCalendarLayout.getPaddingTop()//
                    , monthCalendarLayout.getPaddingRight(), monthCalendarLayout.getPaddingBottom() + ScreenUtils.dpToPx(this, 30));
            }

            mCalendarsLayout.addView(monthCalendarLayout);
        }
    }

    protected void initToolbar(String title)
    {
        mTitleTextView = findViewById(R.id.titleTextView);
        setToolbarText(title);
    }

    protected void setToolbarText(String title)
    {
        mTitleTextView.setText(title);
    }

    private View getMonthCalendarView(Context context, Pair<String, Day[]> pair)
    {
        View monthCalendarLayout = LayoutInflater.from(context).inflate(R.layout.view_calendar, null);
        TextView monthTextView = monthCalendarLayout.findViewById(R.id.monthTextView);
        android.support.v7.widget.GridLayout calendarGridLayout = monthCalendarLayout.findViewById(R.id.calendarGridLayout);

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

        DailyDayStrikeTextView dayTextView = new DailyDayStrikeTextView(context);
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

        TextView visitTextView = dayView.findViewById(R.id.textView);
        DailyDayStrikeTextView dayTextView = dayView.findViewById(R.id.dateTextView);

        if (isShow == true)
        {
            visitTextView.setText(null);

            //            visitTextView.setText(R.string.label_calendar_soldout);
            //            visitTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 8);
            //            visitTextView.setTypeface(FontManager.getInstance(this).getMediumTypeface());
            //            visitTextView.setVisibility(View.VISIBLE);
            //
            //            RelativeLayout.LayoutParams visitLayoutParams = (RelativeLayout.LayoutParams) visitTextView.getLayoutParams();
            //            visitLayoutParams.topMargin = ScreenUtils.dpToPx(this, 8);
            //            visitTextView.setLayoutParams(visitLayoutParams);

            dayTextView.setStrikeFlag(true);
        } else
        {
            visitTextView.setText(null);

            //            visitTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
            //            visitTextView.setTypeface(FontManager.getInstance(this).getRegularTypeface());
            //            visitTextView.setVisibility(View.INVISIBLE);
            //
            //            RelativeLayout.LayoutParams visitLayoutParams = (RelativeLayout.LayoutParams) visitTextView.getLayoutParams();
            //            visitLayoutParams.topMargin = ScreenUtils.dpToPx(this, 5);
            //            visitTextView.setLayoutParams(visitLayoutParams);

            dayTextView.setStrikeFlag(false);
        }
    }

    protected void setDayOfWeekTextColor(View dayView)
    {
        TextView dayTextView = dayView.findViewById(R.id.dateTextView);

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

    private int getMonthInterval(final Calendar startCalendar, final Calendar endCalendar)
    {
        int startMonth = startCalendar.get(Calendar.MONTH);
        int endMonth = endCalendar.get(Calendar.MONTH);

        if (startMonth > endMonth)
        {
            return 12 - startMonth + endMonth;
        } else
        {
            return endMonth - startMonth;
        }
    }

    private int getMonthCalendarLength(final Calendar todayCalendar, final Calendar startCalendar, final Calendar endCalendar)
    {
        if (startCalendar == null || endCalendar == null || todayCalendar == null)
        {
            return 0;
        }

        int todayMonth = todayCalendar.get(Calendar.MONTH);
        int endMonth = endCalendar.get(Calendar.MONTH);

        int today = todayCalendar.get(Calendar.DAY_OF_MONTH);
        int endDay = endCalendar.get(Calendar.DAY_OF_MONTH);

        int availableLastDay = todayMonth == endMonth ? endDay : todayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int monthDay = availableLastDay - today + 1; // 이달이 31일 일경우 오늘이 최소 1일임으로 31 - 1 하면 30일이 되어 버린다 채워야 하는 칸은 31개 이므로 1일을 보정한다.
        int todayDayOfWeek = todayCalendar.get(Calendar.DAY_OF_WEEK); // 요일은 1부터 시작하지만 배열은 0부터
        // 시작함으로 1 빼고 계산 해야 함
        int endDayOfWeek = (((todayDayOfWeek - 1) + monthDay) % 7); // monthDay 에 오늘이 포함 되었음으로 1일을 빼고 이전 날을 계산 해야 함
        if (endDayOfWeek == 0)
        {
            endDayOfWeek = Calendar.SATURDAY;
        }

        int length = (todayDayOfWeek - 1) + monthDay + (7 - endDayOfWeek);

        //        ExLog.d("length : " + length + " , availableLastDay : " + availableLastDay + " , today : " + today + " , todayweek : " + todayDayOfWeek + " , endDay Week : " + endDayOfWeek);
        return length;
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

    protected void smoothScrollStartDayPosition(View startDayView)
    {
        ScrollView scrollView = findViewById(R.id.calendarScrollLayout);

        if (startDayView == null)
        {
            if (mDayViewList == null || mDayViewList.size() == 0)
            {
                return;
            }

            for (View dayView : mDayViewList)
            {
                if (dayView.isSelected() == true)
                {
                    startDayView = dayView;
                    break;
                }
            }

            if (startDayView == null)
            {
                return;
            }

        }

        final View selectView = startDayView;

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
                    if (mAnimatorSet != null)
                    {
                        mAnimatorSet.removeAllListeners();
                        mAnimatorSet = null;
                    }

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

                    if (mAnimatorSet != null)
                    {
                        mAnimatorSet.removeAllListeners();
                        mAnimatorSet = null;
                    }

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
    void setStatusBarColor(int color)
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
}
