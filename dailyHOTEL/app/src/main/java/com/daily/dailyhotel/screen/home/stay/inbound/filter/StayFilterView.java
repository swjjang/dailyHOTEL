package com.daily.dailyhotel.screen.home.stay.inbound.filter;

import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.StayFilter;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayFilterDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

public class StayFilterView extends BaseDialogView<StayFilterView.OnEventListener, ActivityStayFilterDataBinding> implements StayFilterInterface, View.OnClickListener
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onMinusPersonClick();

        void onPlusPersonClick();

        void onResetClick();

        void onConfirmClick();

        void onCheckedChangedSort(StayFilter.SortType sortType);

        void onCheckedChangedBedType(int flag);

        void onCheckedChangedAmenities(int flag);

        void onCheckedChangedRoomAmenities(int flag);
    }

    public StayFilterView(BaseActivity baseActivity, StayFilterView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayFilterDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        EdgeEffectColor.setEdgeGlowColor(getViewDataBinding().nestedScrollView, getColor(R.color.default_over_scroll_edge));

        initSortLayout(viewDataBinding);
        initPersonLayout(viewDataBinding);
        initBedTypeLayout(viewDataBinding);
        initAmenitiesLayout(viewDataBinding);
        initRoomAmenitiesLayout(viewDataBinding);

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
    public void defaultSortLayoutGone()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().sortInclude.regionRadioButton.setVisibility(View.GONE);
        getViewDataBinding().sortInclude.emptyRadioButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setSortLayout(StayFilter.SortType sortType)
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
    public void setPerson(int person, int personCountOfMax, int personCountOfMin)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (person < personCountOfMin)
        {
            person = personCountOfMin;
        } else if (person > personCountOfMax)
        {
            person = personCountOfMax;
        }

        getViewDataBinding().personInclude.personCountTextView.setText(getString(R.string.label_more_person, person));

        if (person == StayFilter.PERSON_COUNT_OF_MIN)
        {
            getViewDataBinding().personInclude.minusPersonImageView.setEnabled(false);
            getViewDataBinding().personInclude.plusPersonImageView.setEnabled(true);
        } else if (person == StayFilter.PERSON_COUNT_OF_MAX)
        {
            getViewDataBinding().personInclude.minusPersonImageView.setEnabled(true);
            getViewDataBinding().personInclude.plusPersonImageView.setEnabled(false);
        } else
        {
            getViewDataBinding().personInclude.minusPersonImageView.setEnabled(true);
            getViewDataBinding().personInclude.plusPersonImageView.setEnabled(true);
        }
    }

    @Override
    public void setBedTypeCheck(int flagBedTypeFilters)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().bedTypeInclude.doubleBedTextView.setSelected((flagBedTypeFilters & StayFilter.FLAG_BED_DOUBLE) == StayFilter.FLAG_BED_DOUBLE);
        getViewDataBinding().bedTypeInclude.twinBedTextView.setSelected((flagBedTypeFilters & StayFilter.FLAG_BED_TWIN) == StayFilter.FLAG_BED_TWIN);
        getViewDataBinding().bedTypeInclude.heatedFloorsTextView.setSelected((flagBedTypeFilters & StayFilter.FLAG_BED_HEATEDFLOORS) == StayFilter.FLAG_BED_HEATEDFLOORS);
    }

    @Override
    public void setAmenitiesCheck(int flagAmenitiesFilters)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().amenityInclude.parkingTextView.setSelected((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_PARKING) == StayFilter.FLAG_AMENITIES_PARKING);
        getViewDataBinding().amenityInclude.bbqTextView.setSelected((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_SHARED_BBQ) == StayFilter.FLAG_AMENITIES_SHARED_BBQ);
        getViewDataBinding().amenityInclude.poolTextView.setSelected((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_POOL) == StayFilter.FLAG_AMENITIES_POOL);
        getViewDataBinding().amenityInclude.businessCenterTextView.setSelected((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_BUSINESS_CENTER) == StayFilter.FLAG_AMENITIES_BUSINESS_CENTER);
        getViewDataBinding().amenityInclude.fitnessTextView.setSelected((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_FITNESS) == StayFilter.FLAG_AMENITIES_FITNESS);
        getViewDataBinding().amenityInclude.saunaTextView.setSelected((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_SAUNA) == StayFilter.FLAG_AMENITIES_SAUNA);
        getViewDataBinding().amenityInclude.petTextView.setSelected((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_PET) == StayFilter.FLAG_AMENITIES_PET);
        getViewDataBinding().amenityInclude.kidsPlayTextView.setSelected((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_KIDS_PLAY_ROOM) == StayFilter.FLAG_AMENITIES_KIDS_PLAY_ROOM);
    }

    @Override
    public void setRoomAmenitiesCheck(int flagRoomAmenitiesFilters)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().roomAmenityInclude.breakfastTextView.setSelected((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_BREAKFAST) == StayFilter.FLAG_ROOM_AMENITIES_BREAKFAST);
        getViewDataBinding().roomAmenityInclude.wifiTextView.setSelected((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_WIFI) == StayFilter.FLAG_ROOM_AMENITIES_WIFI);
        getViewDataBinding().roomAmenityInclude.cookingTextView.setSelected((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_COOKING) == StayFilter.FLAG_ROOM_AMENITIES_COOKING);
        getViewDataBinding().roomAmenityInclude.pcTextView.setSelected((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_PC) == StayFilter.FLAG_ROOM_AMENITIES_PC);
        getViewDataBinding().roomAmenityInclude.bathTextView.setSelected((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_BATHTUB) == StayFilter.FLAG_ROOM_AMENITIES_BATHTUB);
        getViewDataBinding().roomAmenityInclude.tvTextView.setSelected((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_TV) == StayFilter.FLAG_ROOM_AMENITIES_TV);
        getViewDataBinding().roomAmenityInclude.spaTextView.setSelected((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_SPA_WHIRLPOOL) == StayFilter.FLAG_ROOM_AMENITIES_SPA_WHIRLPOOL);
        getViewDataBinding().roomAmenityInclude.privateBbqTextView.setSelected((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_PRIVATE_BBQ) == StayFilter.FLAG_ROOM_AMENITIES_PRIVATE_BBQ);
        getViewDataBinding().roomAmenityInclude.karaokeTextView.setSelected((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_KARAOKE) == StayFilter.FLAG_ROOM_AMENITIES_KARAOKE);
        getViewDataBinding().roomAmenityInclude.partyRoomTextView.setSelected((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_PARTY_ROOM) == StayFilter.FLAG_ROOM_AMENITIES_PARTY_ROOM);
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
            // Bed Type
            case R.id.doubleBedTextView:
                getEventListener().onCheckedChangedBedType(StayFilter.FLAG_BED_DOUBLE);
                break;
            case R.id.twinBedTextView:
                getEventListener().onCheckedChangedBedType(StayFilter.FLAG_BED_TWIN);
                break;
            case R.id.heatedFloorsTextView:
                getEventListener().onCheckedChangedBedType(StayFilter.FLAG_BED_HEATEDFLOORS);
                break;

            // Amenity
            case R.id.parkingTextView:
                getEventListener().onCheckedChangedAmenities(StayFilter.FLAG_AMENITIES_PARKING);
                break;
            case R.id.poolTextView:
                getEventListener().onCheckedChangedAmenities(StayFilter.FLAG_AMENITIES_POOL);
                break;
            case R.id.fitnessTextView:
                getEventListener().onCheckedChangedAmenities(StayFilter.FLAG_AMENITIES_FITNESS);
                break;
            case R.id.saunaTextView:
                getEventListener().onCheckedChangedAmenities(StayFilter.FLAG_AMENITIES_SAUNA);
                break;
            case R.id.businessCenterTextView:
                getEventListener().onCheckedChangedAmenities(StayFilter.FLAG_AMENITIES_BUSINESS_CENTER);
                break;
            case R.id.kidsPlayTextView:
                getEventListener().onCheckedChangedAmenities(StayFilter.FLAG_AMENITIES_KIDS_PLAY_ROOM);
                break;
            case R.id.bbqTextView:
                getEventListener().onCheckedChangedAmenities(StayFilter.FLAG_AMENITIES_SHARED_BBQ);
                break;
            case R.id.petTextView:
                getEventListener().onCheckedChangedAmenities(StayFilter.FLAG_AMENITIES_PET);
                break;

            // Room Amenity
            case R.id.breakfastTextView:
                getEventListener().onCheckedChangedRoomAmenities(StayFilter.FLAG_ROOM_AMENITIES_BREAKFAST);
                break;

            case R.id.wifiTextView:
                getEventListener().onCheckedChangedRoomAmenities(StayFilter.FLAG_ROOM_AMENITIES_WIFI);
                break;

            case R.id.cookingTextView:
                getEventListener().onCheckedChangedRoomAmenities(StayFilter.FLAG_ROOM_AMENITIES_COOKING);
                break;

            case R.id.pcTextView:
                getEventListener().onCheckedChangedRoomAmenities(StayFilter.FLAG_ROOM_AMENITIES_PC);
                break;

            case R.id.bathTextView:
                getEventListener().onCheckedChangedRoomAmenities(StayFilter.FLAG_ROOM_AMENITIES_BATHTUB);
                break;

            case R.id.tvTextView:
                getEventListener().onCheckedChangedRoomAmenities(StayFilter.FLAG_ROOM_AMENITIES_TV);
                break;

            case R.id.spaTextView:
                getEventListener().onCheckedChangedRoomAmenities(StayFilter.FLAG_ROOM_AMENITIES_SPA_WHIRLPOOL);
                break;

            case R.id.privateBbqTextView:
                getEventListener().onCheckedChangedRoomAmenities(StayFilter.FLAG_ROOM_AMENITIES_PRIVATE_BBQ);
                break;

            case R.id.karaokeTextView:
                getEventListener().onCheckedChangedRoomAmenities(StayFilter.FLAG_ROOM_AMENITIES_KARAOKE);
                break;

            case R.id.partyRoomTextView:
                getEventListener().onCheckedChangedRoomAmenities(StayFilter.FLAG_ROOM_AMENITIES_PARTY_ROOM);
                break;
        }
    }

    private void initToolbar(ActivityStayFilterDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }

    private void initSortLayout(ActivityStayFilterDataBinding viewDataBinding)
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
                        getEventListener().onCheckedChangedSort(StayFilter.SortType.DEFAULT);
                        break;

                    case R.id.distanceRadioButton:
                        getEventListener().onCheckedChangedSort(StayFilter.SortType.DISTANCE);
                        break;

                    case R.id.lowPriceRadioButton:
                        getEventListener().onCheckedChangedSort(StayFilter.SortType.LOW_PRICE);
                        break;

                    case R.id.highPriceRadioButton:
                        getEventListener().onCheckedChangedSort(StayFilter.SortType.HIGH_PRICE);
                        break;

                    case R.id.satisfactionRadioButton:
                        getEventListener().onCheckedChangedSort(StayFilter.SortType.SATISFACTION);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void initPersonLayout(ActivityStayFilterDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.personInclude.minusPersonImageView.setOnClickListener(v -> getEventListener().onMinusPersonClick());
        viewDataBinding.personInclude.plusPersonImageView.setOnClickListener(v -> getEventListener().onPlusPersonClick());
    }

    private void initBedTypeLayout(ActivityStayFilterDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        int count = viewDataBinding.bedTypeInclude.bedTypeLayout.getChildCount();

        View view;

        for (int i = 0; i < count; i++)
        {
            view = viewDataBinding.bedTypeInclude.bedTypeLayout.getChildAt(i);

            if (view instanceof DailyTextView)
            {
                view.setOnClickListener(this);
            } else
            {
                view.setEnabled(false);
            }
        }
    }

    private void initAmenitiesLayout(ActivityStayFilterDataBinding viewDataBinding)
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

    private void initRoomAmenitiesLayout(ActivityStayFilterDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        int count = viewDataBinding.roomAmenityInclude.amenityRoomGridLayout.getChildCount();

        View view;

        for (int i = 0; i < count; i++)
        {
            view = viewDataBinding.roomAmenityInclude.amenityRoomGridLayout.getChildAt(i);

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
