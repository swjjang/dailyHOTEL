package com.daily.dailyhotel.screen.home.stay.outbound.search;

import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundSearchDataBinding;

public class StayOutboundSearchView extends BaseDialogView<StayOutboundSearchView.OnEventListener, ActivityStayOutboundSearchDataBinding> implements StayOutboundSearchViewInterface, View.OnClickListener
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onSuggestClick(boolean isUserAction);

        void onSearchKeyword();

        void onCalendarClick();

        void onPeopleClick();
    }

    public StayOutboundSearchView(BaseActivity baseActivity, StayOutboundSearchView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayOutboundSearchDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        viewDataBinding.suggestTextView.setOnClickListener(this);
        viewDataBinding.calendarTextView.setOnClickListener(this);
        viewDataBinding.peopleTextView.setOnClickListener(this);
        viewDataBinding.doSearchView.setOnClickListener(this);
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

    @Override
    public void setCalendarText(String calendarText)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().calendarTextView.setText(calendarText);
    }

    @Override
    public void setSuggest(String suggest)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().suggestTextView.setText(suggest);
    }

    @Override
    public void setSearchEnable(boolean enable)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().doSearchView.setEnabled(enable);
    }

    @Override
    public void setPeopleText(String peopleText)
    {
        if (getViewDataBinding() == null || DailyTextUtils.isTextEmpty(peopleText) == true)
        {
            return;
        }

        getViewDataBinding().peopleTextView.setText(peopleText);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.suggestTextView:
                getEventListener().onSuggestClick(true);
                break;

            case R.id.calendarTextView:
                getEventListener().onCalendarClick();
                break;

            case R.id.peopleTextView:
                getEventListener().onPeopleClick();
                break;

            // 검색 하기
            case R.id.doSearchView:
                getEventListener().onSearchKeyword();
                break;
        }
    }

    private void initToolbar(ActivityStayOutboundSearchDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setBackImageResource(R.drawable.navibar_ic_x);
        viewDataBinding.toolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onBackClick();
            }
        });
    }
}
