package com.daily.dailyhotel.screen.home.search.stay.inbound.research;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.databinding.ActivityResearchStayDataBinding;

public class ResearchStayView extends BaseDialogView<ResearchStayView.OnEventListener, ActivityResearchStayDataBinding> implements ResearchStayInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onStaySuggestClick();

        void onStayCalendarClick();

        void onStayDoSearchClick();
    }

    public ResearchStayView(BaseActivity baseActivity, OnEventListener listener)
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
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().dailyTitleTextView.setText(title);
    }

    private void initToolbar(ActivityResearchStayDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.dailyTitleImageView.setOnClickListener(v -> getEventListener().onBackClick());
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
}
