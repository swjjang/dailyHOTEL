package com.daily.dailyhotel.screen.stay.outbound.filter;

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

public class StayOutboundFilterView extends BaseDialogView<StayOutboundFilterView.OnEventListener, ActivityStayOutboundFilterDataBinding> implements StayOutboundFilterViewInterface, View.OnClickListener, RadioGroup.OnCheckedChangeListener
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

        viewDataBinding.closeView.setOnClickListener(this);
        viewDataBinding.resetFilterView.setOnClickListener(this);
        viewDataBinding.confirmView.setOnClickListener(this);

        getViewDataBinding().sortRadioGroup.setOnCheckedChangeListener(this);
        getViewDataBinding().ratingRadioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void setToolbarTitle(String title)
    {
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
        RadioButton radioButton = (RadioButton) group.findViewById(checkedId);

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
            case R.id.sortRadioGroup:
            {
                switch (checkedId)
                {
                    case R.id.rankRadioButton:
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
                getViewDataBinding().sortRadioGroup.check(getViewDataBinding().rankRadioButton.getId());
                break;

            case DISTANCE:
                getViewDataBinding().sortRadioGroup.check(getViewDataBinding().distanceRadioButton.getId());
                break;

            case LOW_PRICE:
                getViewDataBinding().sortRadioGroup.check(getViewDataBinding().lowPriceRadioButton.getId());
                break;

            case HIGH_PRICE:
                getViewDataBinding().sortRadioGroup.check(getViewDataBinding().highPriceRadioButton.getId());
                break;

            case SATISFACTION:
                getViewDataBinding().sortRadioGroup.check(getViewDataBinding().satisfactionRadioButton.getId());
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
    public void setEnabledLines(boolean[] enabledLines)
    {
        if (enabledLines == null)
        {
            return;
        }

        for (int i = 0; i < enabledLines.length; i++)
        {
            if (i == 0)
            {
                getViewDataBinding().rankRadioButton.setEnabled(enabledLines[i]);
                getViewDataBinding().distanceRadioButton.setEnabled(enabledLines[i]);
                getViewDataBinding().lowPriceRadioButton.setEnabled(enabledLines[i]);
                getViewDataBinding().highPriceRadioButton.setEnabled(enabledLines[i]);
                getViewDataBinding().satisfactionRadioButton.setEnabled(enabledLines[i]);
            } else if (i == 1)
            {
                getViewDataBinding().rating1RadioButton.setEnabled(enabledLines[i]);
                getViewDataBinding().rating2RadioButton.setEnabled(enabledLines[i]);
                getViewDataBinding().rating3RadioButton.setEnabled(enabledLines[i]);
                getViewDataBinding().rating4RadioButton.setEnabled(enabledLines[i]);
                getViewDataBinding().rating5RadioButton.setEnabled(enabledLines[i]);
            }
        }
    }
}
