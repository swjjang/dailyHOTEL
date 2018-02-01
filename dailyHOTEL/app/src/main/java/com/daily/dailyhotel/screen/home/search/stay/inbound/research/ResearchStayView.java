package com.daily.dailyhotel.screen.home.search.stay.inbound.research;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.dailyhotel.screen.home.search.stay.inbound.SearchStayFragment;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityResearchStayDataBinding;

import io.reactivex.Observable;

public class ResearchStayView extends BaseDialogView<ResearchStayInterface.OnEventListener, ActivityResearchStayDataBinding> implements ResearchStayInterface.ViewInterface
{
    SearchStayFragment mSearchStayFragment;


    public ResearchStayView(BaseActivity baseActivity, ResearchStayInterface.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityResearchStayDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        viewDataBinding.staySuggestTextView.setOnClickListener(v -> getEventListener().onStaySuggestClick());
        viewDataBinding.stayCalendarTextView.setOnClickListener(v -> getEventListener().onStayCalendarClick());
        viewDataBinding.searchStayTextView.setOnClickListener(v -> getEventListener().onStayDoSearchClick());

        mSearchStayFragment = (SearchStayFragment) getSupportFragmentManager().findFragmentById(R.id.searchStayFragment);
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

    private void initToolbar(ActivityResearchStayDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setBackImageResource(R.drawable.navibar_ic_x);
        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }

    @Override
    public void showSearchStay()
    {
        if (getViewDataBinding() == null || mSearchStayFragment == null)
        {
            return;
        }

        mSearchStayFragment.onSelected();
    }

    @Override
    public void setSearchStaySuggestText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().staySuggestTextView.setText(text);
    }

    @Override
    public void setSearchStayCalendarText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().stayCalendarTextView.setText(text);
    }

    @Override
    public void setSearchStayButtonEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().searchStayTextView.setEnabled(enabled);
    }

    @Override
    public Observable getCompleteCreatedFragment()
    {
        if (getViewDataBinding() == null || mSearchStayFragment == null)
        {
            return null;
        }

        return mSearchStayFragment.getCompleteCreatedObservable();
    }
}
