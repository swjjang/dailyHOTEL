package com.daily.dailyhotel.screen.stay.outbound.filter;

import android.support.annotation.IdRes;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.StayOutboundFilters;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundFilterDataBinding;

public class StayOutboundFilterView extends BaseView<StayOutboundFilterView.OnEventListener, ActivityStayOutboundFilterDataBinding> implements StayOutboundFilterViewInterface, View.OnClickListener, RadioGroup.OnCheckedChangeListener
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
                    case R.id.rankCheckView:
                        getEventListener().onSortClick(StayOutboundFilters.SortType.RECOMMENDATION);
                        break;

                    case R.id.distanceCheckView:
                        getEventListener().onSortClick(StayOutboundFilters.SortType.DISTANCE);
                        break;

                    case R.id.lowPriceCheckView:
                        getEventListener().onSortClick(StayOutboundFilters.SortType.LOW_PRICE);
                        break;

                    case R.id.highPriceCheckView:
                        getEventListener().onSortClick(StayOutboundFilters.SortType.HIGH_PRICE);
                        break;

                    case R.id.satisfactionCheckView:
                        getEventListener().onSortClick(StayOutboundFilters.SortType.SATISFACTION);
                        break;
                }
                break;
            }

            case R.id.ratingRadioGroup:
            {
                switch (checkedId)
                {
                    case R.id.rating1View:
                        getEventListener().onRatingClick(1);
                        break;

                    case R.id.rating2View:
                        getEventListener().onRatingClick(2);
                        break;

                    case R.id.rating3View:
                        getEventListener().onRatingClick(3);
                        break;

                    case R.id.rating4View:
                        getEventListener().onRatingClick(4);
                        break;

                    case R.id.rating5View:
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
                getViewDataBinding().sortRadioGroup.check(getViewDataBinding().rankCheckView.getId());
                break;

            case DISTANCE:
                getViewDataBinding().sortRadioGroup.check(getViewDataBinding().distanceCheckView.getId());
                break;

            case LOW_PRICE:
                getViewDataBinding().sortRadioGroup.check(getViewDataBinding().lowPriceCheckView.getId());
                break;

            case HIGH_PRICE:
                getViewDataBinding().sortRadioGroup.check(getViewDataBinding().highPriceCheckView.getId());
                break;

            case SATISFACTION:
                getViewDataBinding().sortRadioGroup.check(getViewDataBinding().satisfactionCheckView.getId());
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
                getViewDataBinding().ratingRadioGroup.check(getViewDataBinding().rating1View.getId());
                break;

            case 2:
                getViewDataBinding().ratingRadioGroup.check(getViewDataBinding().rating2View.getId());
                break;

            case 3:
                getViewDataBinding().ratingRadioGroup.check(getViewDataBinding().rating3View.getId());
                break;

            case 4:
                getViewDataBinding().ratingRadioGroup.check(getViewDataBinding().rating4View.getId());
                break;

            case 5:
                getViewDataBinding().ratingRadioGroup.check(getViewDataBinding().rating5View.getId());
                break;
        }
    }
}
