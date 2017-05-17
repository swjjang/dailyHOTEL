package com.daily.dailyhotel.screen.stay.outbound.search;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.Persons;
import com.daily.dailyhotel.entity.Suggest;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundSearchDataBinding;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.List;

public class StayOutboundSearchView extends BaseView<StayOutboundSearchView.OnEventListener, ActivityStayOutboundSearchDataBinding> implements StayOutboundSearchViewInterface, View.OnClickListener
{
    private DailyToolbarLayout mDailyToolbarLayout;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onSuggestClick();

        void onSearchKeyword();

        void onCalendarClick();

        void onPersonsClick();
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
        viewDataBinding.deleteKeywrodView.setOnClickListener(this);
        viewDataBinding.calendarTextView.setOnClickListener(this);
        viewDataBinding.personTextView.setOnClickListener(this);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (mDailyToolbarLayout == null)
        {
            return;
        }

        mDailyToolbarLayout.setToolbarTitle(title);
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
    public void setToolbarMenuEnable(boolean enable)
    {
        if (mDailyToolbarLayout == null)
        {
            return;
        }

        mDailyToolbarLayout.setToolbarMenuEnable(enable, enable);
    }

    @Override
    public void setPersons(Persons persons)
    {

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.suggestTextView:
                getEventListener().onSuggestClick();
                break;

            case R.id.calendarTextView:
                getEventListener().onCalendarClick();
                break;

            case R.id.personTextView:
                getEventListener().onPersonsClick();
                break;

            // 검색 하기
            case R.id.menu1View:
                getEventListener().onSearchKeyword();
                break;

            case R.id.deleteKeywrodView:
                setSuggest(null);
                break;
        }
    }

    private void initToolbar(ActivityStayOutboundSearchDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        mDailyToolbarLayout = new DailyToolbarLayout(getContext(), viewDataBinding.toolbar.findViewById(R.id.toolbar));
        mDailyToolbarLayout.initToolbar(getString(R.string.label_search_stay_outbound)//
            , v -> getEventListener().onBackClick());

        mDailyToolbarLayout.setToolbarMenu(getString(R.string.label_search), null);
        mDailyToolbarLayout.setToolbarMenuEnable(false, false);
        mDailyToolbarLayout.setToolbarMenuClickListener(this);
    }
}
