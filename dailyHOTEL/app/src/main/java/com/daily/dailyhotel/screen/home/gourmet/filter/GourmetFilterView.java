package com.daily.dailyhotel.screen.home.gourmet.filter;

import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.GourmetFilter;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityGourmetFilterDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.HashMap;

public class GourmetFilterView extends BaseDialogView<GourmetFilterInterface.OnEventListener, ActivityGourmetFilterDataBinding> implements GourmetFilterInterface.ViewInterface, View.OnClickListener
{
    public GourmetFilterView(BaseActivity baseActivity, GourmetFilterInterface.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityGourmetFilterDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        EdgeEffectColor.setEdgeGlowColor(getViewDataBinding().nestedScrollView, getColor(R.color.default_over_scroll_edge));

        initSortLayout(viewDataBinding);
        initCategoryLayout(viewDataBinding);
        iniTimeLayout(viewDataBinding);
        initAmenitiesLayout(viewDataBinding);

        getViewDataBinding().resetTextView.setOnClickListener(v -> getEventListener().onResetClick());
        getViewDataBinding().confirmTextView.setOnClickListener(v -> getEventListener().onConfirmClick());
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
    public void setSortLayout(GourmetFilter.SortType sortType)
    {
        if (getViewDataBinding() == null || sortType == null)
        {
            return;
        }

        switch (sortType)
        {
            case DEFAULT:
                getViewDataBinding().sortInclude.sortRadioGroup.check(R.id.regionRadioButton);
                break;

            case DISTANCE:
                getViewDataBinding().sortInclude.sortRadioGroup.check(R.id.distanceRadioButton);
                break;

            case LOW_PRICE:
                getViewDataBinding().sortInclude.sortRadioGroup.check(R.id.lowPriceRadioButton);
                break;

            case HIGH_PRICE:
                getViewDataBinding().sortInclude.sortRadioGroup.check(R.id.highPriceRadioButton);
                break;

            case SATISFACTION:
                getViewDataBinding().sortInclude.sortRadioGroup.check(R.id.satisfactionRadioButton);
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

        getViewDataBinding().sortInclude.sortRadioGroup.setEnabled(enabled);

        int childCount = getViewDataBinding().sortInclude.sortRadioGroup.getChildCount();

        for (int i = 0; i < childCount; i++)
        {
            getViewDataBinding().sortInclude.sortRadioGroup.getChildAt(i).setEnabled(enabled);
        }
    }

    @Override
    public void setCategoriesCheck(HashMap<String, Integer> flagCategoryFilterMap)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }
    }

    @Override
    public void setTimesCheck(int flagBedTypeFilters)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

    }

    @Override
    public void setAmenitiesCheck(int flagAmenitiesFilters)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().amenityInclude.parkingCheckView.setSelected((flagAmenitiesFilters & GourmetFilter.FLAG_AMENITIES_PARKING) == GourmetFilter.FLAG_AMENITIES_PARKING);
        getViewDataBinding().amenityInclude.valetCheckView.setSelected((flagAmenitiesFilters & GourmetFilter.FLAG_AMENITIES_VALET) == GourmetFilter.FLAG_AMENITIES_VALET);
        getViewDataBinding().amenityInclude.babySeatCheckView.setSelected((flagAmenitiesFilters & GourmetFilter.FLAG_AMENITIES_BABYSEAT) == GourmetFilter.FLAG_AMENITIES_BABYSEAT);
        getViewDataBinding().amenityInclude.privateRoomCheckView.setSelected((flagAmenitiesFilters & GourmetFilter.FLAG_AMENITIES_PRIVATEROOM) == GourmetFilter.FLAG_AMENITIES_PRIVATEROOM);
        getViewDataBinding().amenityInclude.groupBookingCheckView.setSelected((flagAmenitiesFilters & GourmetFilter.FLAG_AMENITIES_GROUPBOOKING) == GourmetFilter.FLAG_AMENITIES_GROUPBOOKING);
        getViewDataBinding().amenityInclude.corkageCheckView.setSelected((flagAmenitiesFilters & GourmetFilter.FLAG_AMENITIES_CORKAGE) == GourmetFilter.FLAG_AMENITIES_CORKAGE);
    }

    @Override
    public void setConfirmText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().confirmTextView.setText(text);
    }

    @Override
    public void setConfirmEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().confirmTextView.setEnabled(enabled);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            // Amenity
            case R.id.parkingTextView:
                getEventListener().onCheckedChangedAmenities(GourmetFilter.FLAG_AMENITIES_PARKING);
                break;
            case R.id.valetCheckView:
                getEventListener().onCheckedChangedAmenities(GourmetFilter.FLAG_AMENITIES_VALET);
                break;
            case R.id.babySeatCheckView:
                getEventListener().onCheckedChangedAmenities(GourmetFilter.FLAG_AMENITIES_BABYSEAT);
                break;
            case R.id.privateRoomCheckView:
                getEventListener().onCheckedChangedAmenities(GourmetFilter.FLAG_AMENITIES_PRIVATEROOM);
                break;
            case R.id.groupBookingCheckView:
                getEventListener().onCheckedChangedAmenities(GourmetFilter.FLAG_AMENITIES_GROUPBOOKING);
                break;
            case R.id.corkageCheckView:
                getEventListener().onCheckedChangedAmenities(GourmetFilter.FLAG_AMENITIES_CORKAGE);
                break;
        }
    }

    private void initToolbar(ActivityGourmetFilterDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }

    private void initSortLayout(ActivityGourmetFilterDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.sortInclude.sortRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId)
            {
                RadioButton radioButton = radioGroup.findViewById(checkedId);

                if (radioButton == null)
                {
                    return;
                }

                boolean isChecked = radioButton.isChecked();

                if (isChecked == false)
                {
                    return;
                }

                switch (checkedId)
                {
                    case R.id.regionRadioButton:
                        getEventListener().onCheckedChangedSort(GourmetFilter.SortType.DEFAULT);
                        break;

                    case R.id.distanceRadioButton:
                        getEventListener().onCheckedChangedSort(GourmetFilter.SortType.DISTANCE);
                        break;

                    case R.id.lowPriceRadioButton:
                        getEventListener().onCheckedChangedSort(GourmetFilter.SortType.LOW_PRICE);
                        break;

                    case R.id.highPriceRadioButton:
                        getEventListener().onCheckedChangedSort(GourmetFilter.SortType.HIGH_PRICE);
                        break;

                    case R.id.satisfactionRadioButton:
                        getEventListener().onCheckedChangedSort(GourmetFilter.SortType.SATISFACTION);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void initCategoryLayout(ActivityGourmetFilterDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

    }

    private void iniTimeLayout(ActivityGourmetFilterDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }
    }

    private void initAmenitiesLayout(ActivityGourmetFilterDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        int count = viewDataBinding.amenityInclude.amenityGridLayout.getChildCount();

        View view;

        for (int i = 0; i < count; i++)
        {
            view = viewDataBinding.amenityInclude.amenityGridLayout.getChildAt(i);

            if (view instanceof DailyTextView)
            {
                view.setOnClickListener(this);
            } else
            {
                view.setEnabled(false);
            }
        }
    }
}
