package com.daily.dailyhotel.screen.common.area.stay.inbound;

import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.FrameLayout;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.BaseFragmentPagerAdapter;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.base.BasePagerFragment;
import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayAreaGroup;
import com.daily.dailyhotel.entity.StaySubwayAreaGroup;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayAreaListDataBinding;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class StayAreaTabView extends BaseDialogView<StayAreaTabInterface.OnEventListener, ActivityStayAreaListDataBinding> implements StayAreaTabInterface.ViewInterface
{
    BaseFragmentPagerAdapter mFragmentPagerAdapter;
    StayAreaFragment mStayAreaFragment;
    StaySubwayFragment mStaySubwayFragment;

    public StayAreaTabView(BaseActivity baseActivity, StayAreaTabInterface.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayAreaListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);
        initTabLayout(viewDataBinding);
        initViewPageLayout(viewDataBinding);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(title);
    }

    private void initToolbar(ActivityStayAreaListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setBackImageResource(R.drawable.navibar_ic_x);
        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
        viewDataBinding.toolbarView.addMenuItem(DailyToolbarView.MenuItem.SEARCH, null, v -> getEventListener().onSearchClick());
    }

    private void initTabLayout(ActivityStayAreaListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.categoryTabLayout.addTab(viewDataBinding.categoryTabLayout.newTab().setText(R.string.label_area_list));
        viewDataBinding.categoryTabLayout.addTab(viewDataBinding.categoryTabLayout.newTab().setText(R.string.label_subway_list));

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewDataBinding.categoryTabLayout.getLayoutParams();
        layoutParams.topMargin = 1 - ScreenUtils.dpToPx(getContext(), 1);

        viewDataBinding.categoryTabLayout.setLayoutParams(layoutParams);
        viewDataBinding.categoryTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                if (tab.getPosition() == 0)
                {
                    getEventListener().onAreaTabClick();
                } else
                {
                    getEventListener().onSubwayTabClick();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });

        FontManager.apply(viewDataBinding.categoryTabLayout, FontManager.getInstance(getContext()).getRegularTypeface());
    }

    private void initViewPageLayout(ActivityStayAreaListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        if (mFragmentPagerAdapter == null)
        {
            mFragmentPagerAdapter = new BaseFragmentPagerAdapter<BasePagerFragment>(getSupportFragmentManager());
            mFragmentPagerAdapter.addFragment(getStayAreaFragment());
            mFragmentPagerAdapter.addFragment(getStaySubwayFragment());
        }

        viewDataBinding.viewPager.setOffscreenPageLimit(2);
        viewDataBinding.viewPager.setPagingEnabled(false);
        viewDataBinding.viewPager.setAdapter(mFragmentPagerAdapter);
    }

    private StayAreaFragment getStayAreaFragment()
    {
        if (mStayAreaFragment == null)
        {
            mStayAreaFragment = new StayAreaFragment();
            mStayAreaFragment.setOnFragmentEventListener(new StayAreaFragment.OnEventListener()
            {
                @Override
                public void onAroundSearchClick()
                {
                    getEventListener().onAroundSearchClick();
                }

                @Override
                public void onAreaClick(StayAreaGroup areaGroup, StayArea area)
                {
                    getEventListener().onAreaClick(areaGroup, area);
                }
            });
        }

        return mStayAreaFragment;
    }

    private StaySubwayFragment getStaySubwayFragment()
    {
        if (mStaySubwayFragment == null)
        {
            mStaySubwayFragment = new StaySubwayFragment();
            mStaySubwayFragment.setOnFragmentEventListener(new StaySubwayFragment.OnEventListener()
            {
                @Override
                public void onAroundSearchClick()
                {
                    getEventListener().onAroundSearchClick();
                }

                @Override
                public void onSubwayAreaClick(StaySubwayAreaGroup areaGroup, Area area)
                {
                    getEventListener().onSubwayAreaClick(areaGroup, area);
                }
            });
        }

        return mStaySubwayFragment;
    }

    @Override
    public Observable<Boolean> getCompleteCreatedFragment()
    {
        return Observable.zip(mStayAreaFragment.getCompleteCreatedObservable(), mStaySubwayFragment.getCompleteCreatedObservable(), (o, o2) -> true).subscribeOn(AndroidSchedulers.mainThread());
    }

    public void setTabVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().categoryLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setAreaTabSelection()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        final int AREA_TAB_POSITION = 0;

        getViewDataBinding().categoryTabLayout.getTabAt(AREA_TAB_POSITION).select();
        getViewDataBinding().viewPager.setCurrentItem(AREA_TAB_POSITION);
    }

    @Override
    public void setSubwayAreaTabSelection()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        final int SUBWAY_AREA_TAB_POSITION = 1;

        getViewDataBinding().categoryTabLayout.getTabAt(SUBWAY_AREA_TAB_POSITION).select();
        getViewDataBinding().viewPager.setCurrentItem(SUBWAY_AREA_TAB_POSITION);
    }

    @Override
    public void setTabSelect(int position)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().categoryTabLayout.getTabAt(position).select();
    }
}
