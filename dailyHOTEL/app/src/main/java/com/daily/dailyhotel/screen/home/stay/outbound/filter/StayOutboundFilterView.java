package com.daily.dailyhotel.screen.home.stay.outbound.filter;

import android.support.annotation.IdRes;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.StayOutboundFilters;
import com.daily.dailyhotel.view.DailyToolbarView;
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

        getViewDataBinding().sortRadioGroup.setOnCheckedChangeListener(this);

        getViewDataBinding().rating1TextView.setOnClickListener(this);
        getViewDataBinding().rating2TextView.setOnClickListener(this);
        getViewDataBinding().rating3TextView.setOnClickListener(this);
        getViewDataBinding().rating4TextView.setOnClickListener(this);
        getViewDataBinding().rating5TextView.setOnClickListener(this);
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

            case R.id.rating1TextView:
                setRatingChecked(v, 1);
                break;

            case R.id.rating2TextView:
                setRatingChecked(v, 2);
                break;

            case R.id.rating3TextView:
                setRatingChecked(v, 3);
                break;

            case R.id.rating4TextView:
                setRatingChecked(v, 4);
                break;

            case R.id.rating5TextView:
                setRatingChecked(v, 5);
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
                setRatingChecked(getViewDataBinding().rating1TextView, 1);
                break;

            case 2:
                setRatingChecked(getViewDataBinding().rating2TextView, 2);
                break;

            case 3:
                setRatingChecked(getViewDataBinding().rating3TextView, 3);
                break;

            case 4:
                setRatingChecked(getViewDataBinding().rating4TextView, 4);
                break;

            case 5:
                setRatingChecked(getViewDataBinding().rating5TextView, 5);
                break;

            default:
                setRatingClear();
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
                getViewDataBinding().rating1TextView.setEnabled(enabledLines[i]);
                getViewDataBinding().rating2TextView.setEnabled(enabledLines[i]);
                getViewDataBinding().rating3TextView.setEnabled(enabledLines[i]);
                getViewDataBinding().rating4TextView.setEnabled(enabledLines[i]);
                getViewDataBinding().rating5TextView.setEnabled(enabledLines[i]);
            }
        }
    }

    private void initToolbar(ActivityStayOutboundFilterDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setBackVisible(false);
        viewDataBinding.toolbarView.clearMenuItem();
        viewDataBinding.toolbarView.addMenuItem(DailyToolbarView.MenuItem.CLOSE, null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onBackClick();
            }
        });
    }

    private void setRatingChecked(View view, int rating)
    {
        if (getViewDataBinding() == null || view == null)
        {
            return;
        }

        if (view.isSelected() == false)
        {
            getEventListener().onRatingClick(rating);

            int count = getViewDataBinding().ratingRadioGroup.getChildCount();

            for (int i = 0; i < count; i++)
            {
                if (getViewDataBinding().ratingRadioGroup.getChildAt(i) == view)
                {
                    getViewDataBinding().ratingRadioGroup.getChildAt(i).setSelected(true);
                } else
                {
                    getViewDataBinding().ratingRadioGroup.getChildAt(i).setSelected(false);
                }
            }
        } else
        {
            getEventListener().onRatingClick(-1);
            view.setSelected(false);
        }
    }

    private void setRatingClear()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        int count = getViewDataBinding().ratingRadioGroup.getChildCount();

        for (int i = 0; i < count; i++)
        {
            getViewDataBinding().ratingRadioGroup.getChildAt(i).setSelected(false);
        }
    }
}
