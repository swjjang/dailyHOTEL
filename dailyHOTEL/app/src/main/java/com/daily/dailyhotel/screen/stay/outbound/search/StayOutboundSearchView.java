package com.daily.dailyhotel.screen.stay.outbound.search;

import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.People;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundSearchDataBinding;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.ArrayList;

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
        viewDataBinding.calendarTextView.setOnClickListener(this);
        viewDataBinding.peopleTextView.setOnClickListener(this);
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
    public void setSearchEnable(boolean enable)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().doSearchView.setEnabled(enable);
    }

    @Override
    public void setPeople(People people)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getString(R.string.label_search_adult_count, people.numberOfAdults));

        ArrayList<Integer> childAgeList = people.getChildAgeList();
        int childCount;

        if (childAgeList == null)
        {
            childCount = 0;
        } else
        {
            childCount = childAgeList.size();
        }

        stringBuilder.append(", ");
        stringBuilder.append(getString(R.string.label_search_child_count, childCount));

        if (childCount > 0)
        {
            StringBuilder childrenAgeStringBuilder = new StringBuilder();
            for (int childAge : childAgeList)
            {
                if (childAge == 0)
                {
                    childrenAgeStringBuilder.append(getString(R.string.label_search_under_of_1_age));
                } else
                {
                    childrenAgeStringBuilder.append(getString(R.string.label_search_child_age, childAge));
                }
            }

            stringBuilder.append(getString(R.string.label_search_children_age, childrenAgeStringBuilder.toString()));
        }

        getViewDataBinding().peopleTextView.setText(stringBuilder.toString());
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

            case R.id.peopleTextView:
                getEventListener().onPersonsClick();
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

        mDailyToolbarLayout = new DailyToolbarLayout(getContext(), viewDataBinding.toolbar.findViewById(R.id.toolbar));
        mDailyToolbarLayout.initToolbar(getString(R.string.label_search_stay_outbound)//
            , v -> getEventListener().onBackClick());
    }
}
