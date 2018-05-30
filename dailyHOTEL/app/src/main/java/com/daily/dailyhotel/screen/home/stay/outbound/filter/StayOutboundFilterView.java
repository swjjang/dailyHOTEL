package com.daily.dailyhotel.screen.home.stay.outbound.filter;

import android.support.annotation.IdRes;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.StayOutboundFilters;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundFilterDataBinding;

public class StayOutboundFilterView extends BaseDialogView<StayOutboundFilterView.OnEventListener, ActivityStayOutboundFilterDataBinding> //
    implements StayOutboundFilterViewInterface, View.OnClickListener, RadioGroup.OnCheckedChangeListener
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onSortClick(StayOutboundFilters.SortType sortType);

        void onRatingClick(int rating);

        void onResetClick();

        void onResultClick();
    }

    public StayOutboundFilterView(BaseActivity baseActivity, StayOutboundFilterView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayOutboundFilterDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        viewDataBinding.resetFilterView.setOnClickListener(this);
        viewDataBinding.confirmView.setOnClickListener(this);

        getViewDataBinding().sortInclude.sortRadioGroup.setOnCheckedChangeListener(this);
        getViewDataBinding().sortDimmedView.setOnClickListener((View.OnClickListener) v -> {
        });

        getViewDataBinding().sortInclude.regionRadioButton.setText(R.string.label_sort_by_rank);

        getViewDataBinding().ratingRadioGroup.setOnCheckedChangeListener(this);
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
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.closeView:
                getEventListener().onBackClick();
                break;

            case R.id.resetFilterView:
                getEventListener().onResetClick();
                break;

            case R.id.confirmView:
                getEventListener().onResultClick();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId)
    {
        RadioButton radioButton = group.findViewById(checkedId);

        if (radioButton == null)
        {
            return;
        }

        boolean isChecked = radioButton.isChecked();

        if (isChecked == false)
        {
            return;
        }

        switch (group.getId())
        {
            case R.id.sortInclude:
            {
                switch (checkedId)
                {
                    case R.id.regionRadioButton:
                        getEventListener().onSortClick(StayOutboundFilters.SortType.RECOMMENDATION);
                        break;

                    case R.id.distanceRadioButton:
                        getEventListener().onSortClick(StayOutboundFilters.SortType.DISTANCE);
                        break;

                    case R.id.lowPriceRadioButton:
                        getEventListener().onSortClick(StayOutboundFilters.SortType.LOW_PRICE);
                        break;

                    case R.id.highPriceRadioButton:
                        getEventListener().onSortClick(StayOutboundFilters.SortType.HIGH_PRICE);
                        break;

                    case R.id.satisfactionRadioButton:
                        getEventListener().onSortClick(StayOutboundFilters.SortType.SATISFACTION);
                        break;
                }
                break;
            }

            case R.id.ratingRadioGroup:
            {
                switch (checkedId)
                {
                    case R.id.rating1RadioButton:
                        getEventListener().onRatingClick(1);
                        break;

                    case R.id.rating2RadioButton:
                        getEventListener().onRatingClick(2);
                        break;

                    case R.id.rating3RadioButton:
                        getEventListener().onRatingClick(3);
                        break;

                    case R.id.rating4RadioButton:
                        getEventListener().onRatingClick(4);
                        break;

                    case R.id.rating5RadioButton:
                        getEventListener().onRatingClick(5);
                        break;
                }
                break;
            }
        }
    }

    @Override
    public void setSort(StayOutboundFilters.SortType sortType)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        switch (sortType)
        {
            case RECOMMENDATION:
                getViewDataBinding().sortInclude.sortRadioGroup.check(getViewDataBinding().sortInclude.regionRadioButton.getId());
                break;

            case DISTANCE:
                getViewDataBinding().sortInclude.sortRadioGroup.check(getViewDataBinding().sortInclude.distanceRadioButton.getId());
                break;

            case LOW_PRICE:
                getViewDataBinding().sortInclude.sortRadioGroup.check(getViewDataBinding().sortInclude.lowPriceRadioButton.getId());
                break;

            case HIGH_PRICE:
                getViewDataBinding().sortInclude.sortRadioGroup.check(getViewDataBinding().sortInclude.highPriceRadioButton.getId());
                break;

            case SATISFACTION:
                getViewDataBinding().sortInclude.sortRadioGroup.check(getViewDataBinding().sortInclude.satisfactionRadioButton.getId());
                break;
        }
    }

    @Override
    public void setRating(int rating)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        switch (rating)
        {
            case 1:
                getViewDataBinding().ratingRadioGroup.check(getViewDataBinding().rating1RadioButton.getId());
                break;

            case 2:
                getViewDataBinding().ratingRadioGroup.check(getViewDataBinding().rating2RadioButton.getId());
                break;

            case 3:
                getViewDataBinding().ratingRadioGroup.check(getViewDataBinding().rating3RadioButton.getId());
                break;

            case 4:
                getViewDataBinding().ratingRadioGroup.check(getViewDataBinding().rating4RadioButton.getId());
                break;

            case 5:
                getViewDataBinding().ratingRadioGroup.check(getViewDataBinding().rating5RadioButton.getId());
                break;

            default:
                getViewDataBinding().ratingRadioGroup.clearCheck();
                break;
        }
    }

    @Override
    public void setSortLayoutEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().disabledSortFilterGroup.setVisibility(enabled ? View.GONE : View.VISIBLE);
    }

    private void initToolbar(ActivityStayOutboundFilterDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setBackImageResource(R.drawable.navibar_ic_x);
        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }
}
