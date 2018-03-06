package com.twoheart.dailyhotel.screen.home.category.list;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.layout.PlaceMainLayout;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by android_sam on 2017. 4. 19..
 */

@Deprecated
public class StayCategoryTabLayout extends PlaceMainLayout
{
    private String mTitleText;
    private DailyCategoryType mDailyCategoryType;
    View mTooltipLayout;

    public StayCategoryTabLayout(Context context, String titleText, DailyCategoryType dailyCategoryType, PlaceMainLayout.OnEventListener onEventListener)
    {
        super(context, onEventListener);

        mTitleText = titleText;
        mDailyCategoryType = dailyCategoryType;
    }

    @Override
    protected void initLayout(View view)
    {
        super.initLayout(view);

        mTooltipLayout = view.findViewById(R.id.tooltipLayout);

        if (DailyPreference.getInstance(mContext).isStayCategoryListTooltip() == true)
        {
            mTooltipLayout.setVisibility(View.GONE);
            return;
        }

        DailyPreference.getInstance(mContext).setStayCategoryListTooltip(true);
        mTooltipLayout.setVisibility(View.VISIBLE);
        mTooltipLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                hideAnimationTooltip();
            }
        });

        // 10초 후에 터치가 없으면 자동으로 사라짐.(기획서상 10초이지만 실제 보이기까지 여분의 시간을 넣음)
        mTooltipLayout.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (mTooltipLayout.getVisibility() != View.GONE)
                {
                    hideAnimationTooltip();
                }
            }
        }, 10000);
    }

    void hideAnimationTooltip()
    {
        if (mTooltipLayout.getTag() != null)
        {
            return;
        }

        final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mTooltipLayout, "alpha", 1.0f, 0.0f);

        mTooltipLayout.setTag(objectAnimator);

        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(300);
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
                objectAnimator.removeAllUpdateListeners();

                mTooltipLayout.setTag(null);
                mTooltipLayout.setVisibility(View.GONE);
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

    @Override
    protected PlaceListFragmentPagerAdapter getPlaceListFragmentPagerAdapter(FragmentManager fragmentManager//
        , int count, View bottomOptionLayout //
        , PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        PlaceListFragmentPagerAdapter placeListFragmentPagerAdapter = new PlaceListFragmentPagerAdapter(fragmentManager);

        ArrayList<StayCategoryListFragment> list = new ArrayList<>(count);

        boolean isBoutiqueBMEnabled = DailyRemoteConfigPreference.getInstance(mContext).isRemoteConfigBoutiqueBMEnabled();

        for (int i = 0; i < count; i++)
        {
            StayCategoryListFragment stayCategoryListFragment = new StayCategoryListFragment();
            stayCategoryListFragment.setPlaceOnListFragmentListener(listener);
            stayCategoryListFragment.setBottomOptionLayout(bottomOptionLayout);
            stayCategoryListFragment.setIsShowLocalPlus( //
                isBoutiqueBMEnabled == true && DailyCategoryType.STAY_BOUTIQUE.equals(mDailyCategoryType) == true);
            list.add(stayCategoryListFragment);
        }

        placeListFragmentPagerAdapter.setPlaceFragmentList(list);

        return placeListFragmentPagerAdapter;
    }

    @Override
    protected void onAnalyticsCategoryFlicking(String category)
    {
        //        AnalyticsManager.getInstance(mContext).recordEvent(AnalyticsManager.Category.NAVIGATION_//
        //            , AnalyticsManager.Action.DAILY_HOTEL_CATEGORY_FLICKING, category, null);
    }

    @Override
    protected void onAnalyticsCategoryClick(String category)
    {
        //        AnalyticsManager.getInstance(mContext).recordEvent(AnalyticsManager.Category.NAVIGATION_//
        //            , AnalyticsManager.Action.HOTEL_CATEGORY_CLICKED, category, null);
    }

    @Override
    protected String getAppBarTitle()
    {
        return DailyTextUtils.isTextEmpty(mTitleText) == false //
            ? mTitleText //
            : mContext.getString(R.string.label_daily_hotel);
    }

    protected void setToolbarDateText(StayBookingDay stayBookingDay)
    {
        if (stayBookingDay == null)
        {
            return;
        }

        String checkInDay = stayBookingDay.getCheckInDay("M.d(EEE)");
        String checkOutDay = stayBookingDay.getCheckOutDay("M.d(EEE)");

        setToolbarDateText(String.format(Locale.KOREA, "%s - %s", checkInDay, checkOutDay));
    }
}
