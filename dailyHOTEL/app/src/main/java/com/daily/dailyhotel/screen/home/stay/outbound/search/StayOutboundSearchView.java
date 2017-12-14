package com.daily.dailyhotel.screen.home.stay.outbound.search;

import android.support.constraint.ConstraintLayout;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.Suggest;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundSearchDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.List;

public class StayOutboundSearchView extends BaseDialogView<StayOutboundSearchView.OnEventListener, ActivityStayOutboundSearchDataBinding> implements StayOutboundSearchViewInterface, View.OnClickListener
{
    private static final float CARD_WIDTH_RATIO = 0.772f; // 270/360 = 0.772222222222222;

    StayOutboundSearchPopularAreaListAdapter mPopularAreaListAdapter;

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

        //
        int margin = (int) (ScreenUtils.getScreenWidth(getContext()) * (1.0f - CARD_WIDTH_RATIO) / 2.0f);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) viewDataBinding.popularLayout.getLayoutParams();
        layoutParams.leftMargin = margin;
        layoutParams.rightMargin = margin;
        viewDataBinding.popularLayout.setLayoutParams(layoutParams);

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.popularRecyclerView, getColor(R.color.default_over_scroll_edge));
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

    @Override
    public void setPopularAreaList(List<Suggest> suggestList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().popularLayout.setVisibility((suggestList == null || suggestList.size() == 0) ? View.GONE : View.VISIBLE);

        if (mPopularAreaListAdapter == null)
        {
            mPopularAreaListAdapter = new StayOutboundSearchPopularAreaListAdapter(getContext(), new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                }
            });

            getViewDataBinding().popularRecyclerView.setAdapter(mPopularAreaListAdapter);
        }

        mPopularAreaListAdapter.setData(suggestList);
        mPopularAreaListAdapter.notifyDataSetChanged();
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
