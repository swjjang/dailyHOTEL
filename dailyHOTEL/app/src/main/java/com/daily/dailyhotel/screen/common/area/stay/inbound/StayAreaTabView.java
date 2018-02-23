package com.daily.dailyhotel.screen.common.area.stay.inbound;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.BaseFragmentPagerAdapter;
import com.daily.dailyhotel.base.BasePagerFragment;
import com.daily.dailyhotel.entity.StayArea;
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

        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
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
                public void onAreaClick(StayArea areaGroup, StayArea area)
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
                public void onAreaClick(StayArea areaGroup, StayArea area)
                {
                    getEventListener().onAreaClick(areaGroup, area);
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
}
