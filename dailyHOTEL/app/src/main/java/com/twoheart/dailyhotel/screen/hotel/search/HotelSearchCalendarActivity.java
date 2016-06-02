package com.twoheart.dailyhotel.screen.hotel.search;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.activity.PlaceCalendarActivity;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyTextView;

import java.util.HashMap;
import java.util.Map;

public class HotelSearchCalendarActivity extends PlaceCalendarActivity
{
    public static final String INTENT_EXTRA_DATA_ANIMATION = "animation";
    public static final String INTENT_EXTRA_DATA_ISSELECTED = "isSelected";

    private static final int DAYCOUNT_OF_MAX = 60;
    private static final int ENABLE_DAYCOUNT_OF_MAX = 60;

    private Day mCheckInDay;
    private Day mCheckOutDay;
    private TextView mConfirmTextView;
    private String mCallByScreen;
    private TextView mTitleTextView;
    private View mAnimationLayout;
    private View mDisableLayout;
    private View mBackgroundView;

    private ANIMATION_STATUS mAnimationStatus = ANIMATION_STATUS.HIDE_END;
    private ANIMATION_STATE mAnimationState = ANIMATION_STATE.END;
    private ObjectAnimator mObjectAnimator;
    private AlphaAnimation mAlphaAnimation;

    private enum ANIMATION_STATE
    {
        START,
        END,
        CANCEL
    }

    private enum ANIMATION_STATUS
    {
        SHOW,
        HIDE,
        SHOW_END,
        HIDE_END
    }

    public static Intent newInstance(Context context, SaleTime saleTime, int nights, String screen, boolean isSelected, boolean isAnimation)
    {
        Intent intent = new Intent(context, HotelSearchCalendarActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, nights);
        intent.putExtra(INTENT_EXTRA_DATA_SCREEN, screen);
        intent.putExtra(INTENT_EXTRA_DATA_ISSELECTED, isSelected);
        intent.putExtra(INTENT_EXTRA_DATA_ANIMATION, isAnimation);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        SaleTime saleTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);
        mCallByScreen = intent.getStringExtra(INTENT_EXTRA_DATA_SCREEN);
        int nights = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, 1);
        boolean isSelected = intent.getBooleanExtra(INTENT_EXTRA_DATA_ISSELECTED, true);
        boolean isAnimation = intent.getBooleanExtra(INTENT_EXTRA_DATA_ANIMATION, false);

        initLayout(R.layout.activity_search_calendar, saleTime.getClone(0), ENABLE_DAYCOUNT_OF_MAX, DAYCOUNT_OF_MAX);
        initToolbar(getString(R.string.label_calendar_hotel_select_checkin));

        if (isSelected == true)
        {
            setSelectedRangeDay(saleTime, saleTime.getClone(saleTime.getOffsetDailyDay() + nights));
        }

        if (isAnimation == true)
        {
            mAnimationLayout.setVisibility(View.INVISIBLE);
            mAnimationLayout.post(new Runnable()
            {
                @Override
                public void run()
                {
                    showAnimation();
                }
            });
        } else
        {
            setTouchEnabled(true);
        }
    }

    @Override
    protected void initLayout(int layoutResID, SaleTime dailyTime, int enableDayCountOfMax, int dayCountOfMax)
    {
        super.initLayout(layoutResID, dailyTime, enableDayCountOfMax, dayCountOfMax);

        mAnimationLayout = findViewById(R.id.animationLayout);
        mDisableLayout = findViewById(R.id.disableLayout);

        mTitleTextView = (TextView) findViewById(R.id.titleTextView);
        mConfirmTextView = (TextView) findViewById(R.id.confirmView);
        mConfirmTextView.setVisibility(View.VISIBLE);
        mConfirmTextView.setOnClickListener(this);
        mConfirmTextView.setEnabled(false);

        View closeView = findViewById(R.id.closeView);
        closeView.setOnClickListener(this);

        View exitView = findViewById(R.id.exitView);
        exitView.setOnClickListener(this);

        mBackgroundView = (View) exitView.getParent();

        if (AnalyticsManager.ValueType.LIST.equalsIgnoreCase(mCallByScreen) == true)
        {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Util.dpToPx(this, 83));
            exitView.setLayoutParams(layoutParams);
        }
    }

    @Override
    protected void initToolbar(String title)
    {
        mTitleTextView.setText(title);
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_CALENDAR);

        super.onStart();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed()
    {
        hideAnimation();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.exitView:
            case R.id.closeView:
                hideAnimation();
                break;

            case R.id.confirmView:
            {
                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                String checkInDate = mCheckInDay.dayTime.getDayOfDaysDateFormat("yyyyMMdd");
                String checkOutDate = mCheckOutDay.dayTime.getDayOfDaysDateFormat("yyyyMMdd");

                Map<String, String> params = new HashMap<>();
                params.put(AnalyticsManager.KeyType.CHECK_IN_DATE, Long.toString(mCheckInDay.dayTime.getDayOfDaysDate().getTime()));
                params.put(AnalyticsManager.KeyType.CHECK_OUT_DATE, Long.toString(mCheckOutDay.dayTime.getDayOfDaysDate().getTime()));
                params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(mCheckOutDay.dayTime.getOffsetDailyDay() - mCheckInDay.dayTime.getOffsetDailyDay()));
                params.put(AnalyticsManager.KeyType.SCREEN, mCallByScreen);

                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.HOTEL_BOOKING_DATE_CLICKED, checkInDate + "-" + checkOutDate, params);

                Intent intent = new Intent();
                intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE, mCheckInDay.dayTime);
                intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECKOUTDATE, mCheckOutDay.dayTime);

                setResult(RESULT_OK, intent);
                hideAnimation();
                break;
            }

            case R.id.cancelView:
            {
                if (mCheckInDay == null)
                {
                    return;
                }

                reset();
                break;
            }

            default:
            {
                Day day = (Day) view.getTag();
                DailyTextView dailyTextView = (DailyTextView) view;

                if (day == null)
                {
                    return;
                }

                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                if (mCheckInDay == null)
                {
                    mCheckInDay = day;
                    dailyTextView.setSelected(true);
                    setToolbarText(getString(R.string.label_calendar_hotel_select_checkout));
                    setRangePreviousDaysEnable(view, false);
                    mDailyTextViews[mDailyTextViews.length - 1].setEnabled(true);
                } else
                {
                    if (mCheckInDay.dayTime.getOffsetDailyDay() >= day.dayTime.getOffsetDailyDay())
                    {
                        releaseUiComponent();
                        return;
                    }

                    mCheckOutDay = day;

                    dailyTextView.setSelected(true);

                    String checkInDate = mCheckInDay.dayTime.getDayOfDaysDateFormat("yyyy.MM.dd");
                    String checkOutDate = mCheckOutDay.dayTime.getDayOfDaysDateFormat("yyyy.MM.dd");
                    String title = String.format("%s - %s(%d박)", checkInDate, checkOutDate, //
                        (mCheckOutDay.dayTime.getOffsetDailyDay() - mCheckInDay.dayTime.getOffsetDailyDay()));
                    setToolbarText(title);

                    setRangeDaysAlpha(view);
                    setRangeNextDaysEnable(view, false);
                    setCancelViewVisibility(View.VISIBLE);
                    mConfirmTextView.setEnabled(true);
                    setToastVisibility(View.VISIBLE);
                }

                releaseUiComponent();
                break;
            }
        }
    }

    private void setSelectedRangeDay(SaleTime checkInTime, SaleTime checkOutTime)
    {
        if (checkInTime == null || checkOutTime == null)
        {
            return;
        }

        for (TextView dayTextView : mDailyTextViews)
        {
            Day day = (Day) dayTextView.getTag();

            if (checkInTime.isDayOfDaysDateEquals(day.dayTime) == true)
            {
                dayTextView.performClick();
            } else if (checkOutTime.isDayOfDaysDateEquals(day.dayTime) == true)
            {
                dayTextView.performClick();
                break;
            }
        }
    }

    private void setRangePreviousDaysEnable(View view, boolean enable)
    {
        for (TextView textview : mDailyTextViews)
        {
            if (view == textview)
            {
                break;
            } else
            {
                textview.setEnabled(enable);
            }
        }
    }

    private void setRangeNextDaysEnable(View view, boolean enable)
    {
        boolean isStart = false;

        for (TextView textview : mDailyTextViews)
        {
            if (isStart == false)
            {
                if (view == textview)
                {
                    isStart = true;
                }
            } else
            {
                textview.setEnabled(enable);
            }
        }
    }

    private void setRangeDaysAlpha(View view)
    {
        boolean isStartPosition = false;

        for (TextView textview : mDailyTextViews)
        {
            if (isStartPosition == false)
            {
                if (textview.isSelected() == true)
                {
                    isStartPosition = true;
                }
            } else
            {
                if (view == textview)
                {
                    break;
                }

                textview.setSelected(true);
                textview.setEnabled(false);
            }
        }
    }

    private void reset()
    {
        mCheckInDay = null;

        for (TextView textview : mDailyTextViews)
        {
            textview.setEnabled(true);
            textview.setSelected(false);
        }

        setToolbarText(getString(R.string.label_calendar_hotel_select_checkin));
        mConfirmTextView.setEnabled(false);

        setCancelViewVisibility(View.GONE);
        mDailyTextViews[mDailyTextViews.length - 1].setEnabled(false);

        setToastVisibility(View.GONE);
    }

    private void setTouchEnabled(boolean enabled)
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

    private void showAnimation()
    {
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
            mObjectAnimator.setDuration(300);

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

            showAnimationFadeOut();
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

    private void hideAnimation()
    {
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
            mObjectAnimator.setDuration(300);

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
        mAlphaAnimation.setDuration(300);
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

    /**
     * 점점 어두워짐.
     */
    private void showAnimationFadeOut()
    {
        if (mAlphaAnimation != null)
        {
            if (mAlphaAnimation.hasEnded() == false)
            {
                mAlphaAnimation.cancel();
            }

            mAlphaAnimation = null;
        }

        mAlphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        mAlphaAnimation.setDuration(300);
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
}
