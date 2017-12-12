package com.daily.dailyhotel.screen.home.stay.inbound.filter;

import android.view.View;
import android.widget.RadioGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.StayFilter;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayFilterDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

public class StayFilterView extends BaseDialogView<StayFilterView.OnEventListener, ActivityStayFilterDataBinding> implements StayFilterInterface, View.OnClickListener
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onResetClick();

        void onConfirmClick();

        void onCheckedChangedSort(StayFilter.SortType sortType);

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
    public void setSortLayout(StayFilter.SortType sortType)
    {
        if (getViewDataBinding() == null || sortType == null)
        {
            return;
        }

        switch (sortType)
        {
            case DEFAULT:
                getViewDataBinding().sortLayout.sortRadioGroup.check(R.id.regionRadioButton);
                break;

            case DISTANCE:
                getViewDataBinding().sortLayout.sortRadioGroup.check(R.id.distanceRadioButton);
                break;

            case LOW_PRICE:
                getViewDataBinding().sortLayout.sortRadioGroup.check(R.id.lowPriceRadioButton);
                break;

            case HIGH_PRICE:
                getViewDataBinding().sortLayout.sortRadioGroup.check(R.id.highPriceRadioButton);
                break;

            case SATISFACTION:
                getViewDataBinding().sortLayout.sortRadioGroup.check(R.id.satisfactionRadioButton);
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

        getViewDataBinding().sortLayout.sortRadioGroup.setEnabled(enabled);

        int childCount = getViewDataBinding().sortLayout.sortRadioGroup.getChildCount();

        for (int i = 0; i < childCount; i++)
        {
            getViewDataBinding().sortLayout.sortRadioGroup.getChildAt(i).setEnabled(enabled);
        }
    }

    @Override
    public void setPerson(int person)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (person < StayFilter.MIN_PERSON)
        {
            person = StayFilter.MIN_PERSON;
        } else if (person > StayFilter.MAX_PERSON)
        {
            person = StayFilter.MAX_PERSON;
        }

        getViewDataBinding().personLayout.personCountView.setText(getString(R.string.label_more_person, person));

        if (person == StayFilter.MIN_PERSON)
        {
            getViewDataBinding().personLayout.minusPersonView.setEnabled(false);
            getViewDataBinding().personLayout.plusPersonView.setEnabled(true);
        } else if (person == StayFilter.MAX_PERSON)
        {
            getViewDataBinding().personLayout.minusPersonView.setEnabled(true);
            getViewDataBinding().personLayout.plusPersonView.setEnabled(false);
        } else
        {
            getViewDataBinding().personLayout.minusPersonView.setEnabled(true);
            getViewDataBinding().personLayout.plusPersonView.setEnabled(true);
        }
    }

    @Override
    public void setAmenitiesCheck(int flagAmenitiesFilters)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().amenityLayout.parkingTextView.setSelected((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_PARKING) == StayFilter.FLAG_AMENITIES_PARKING);

        getViewDataBinding().amenityLayout.bbqTextView.setSelected((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_SHARED_BBQ) == StayFilter.FLAG_AMENITIES_SHARED_BBQ);

        getViewDataBinding().amenityLayout.poolTextView.setSelected((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_POOL) == StayFilter.FLAG_AMENITIES_POOL);

        getViewDataBinding().amenityLayout.businessCenterTextView.setSelected((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_BUSINESS_CENTER) == StayFilter.FLAG_AMENITIES_BUSINESS_CENTER);

        getViewDataBinding().amenityLayout.fitnessTextView.setSelected((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_FITNESS) == StayFilter.FLAG_AMENITIES_FITNESS);

        getViewDataBinding().amenityLayout.saunaTextView.setSelected((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_SAUNA) == StayFilter.FLAG_AMENITIES_SAUNA);

        getViewDataBinding().amenityLayout.petTextView.setSelected((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_PET) == StayFilter.FLAG_AMENITIES_PET);

        getViewDataBinding().amenityLayout.kidsPlayTextView.setSelected((flagAmenitiesFilters & StayFilter.FLAG_AMENITIES_KIDS_PLAY_ROOM) == StayFilter.FLAG_AMENITIES_KIDS_PLAY_ROOM);
    }

    @Override
    public void setRoomAmenitiesCheck(int flagRoomAmenitiesFilters)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().roomAmenityLayout.breakfastTextView.setSelected((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_BREAKFAST) == StayFilter.FLAG_ROOM_AMENITIES_BREAKFAST);

        getViewDataBinding().roomAmenityLayout.wifiTextView.setSelected((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_WIFI) == StayFilter.FLAG_ROOM_AMENITIES_WIFI);

        getViewDataBinding().roomAmenityLayout.cookingTextView.setSelected((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_COOKING) == StayFilter.FLAG_ROOM_AMENITIES_COOKING);

        getViewDataBinding().roomAmenityLayout.pcTextView.setSelected((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_PC) == StayFilter.FLAG_ROOM_AMENITIES_PC);

        getViewDataBinding().roomAmenityLayout.bathTextView.setSelected((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_BATHTUB) == StayFilter.FLAG_ROOM_AMENITIES_BATHTUB);

        getViewDataBinding().roomAmenityLayout.tvTextView.setSelected((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_TV) == StayFilter.FLAG_ROOM_AMENITIES_TV);

        getViewDataBinding().roomAmenityLayout.spaTextView.setSelected((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_SPA_WHIRLPOOL) == StayFilter.FLAG_ROOM_AMENITIES_SPA_WHIRLPOOL);

        getViewDataBinding().roomAmenityLayout.privateBbqTextView.setSelected((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_PRIVATE_BBQ) == StayFilter.FLAG_ROOM_AMENITIES_PRIVATE_BBQ);

        getViewDataBinding().roomAmenityLayout.karaokeTextView.setSelected((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_KARAOKE) == StayFilter.FLAG_ROOM_AMENITIES_KARAOKE);

        getViewDataBinding().roomAmenityLayout.partyRoomTextView.setSelected((flagRoomAmenitiesFilters & StayFilter.FLAG_ROOM_AMENITIES_PARTY_ROOM) == StayFilter.FLAG_ROOM_AMENITIES_PARTY_ROOM);
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

        viewDataBinding.sortLayout.sortRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId)
            {
                switch (checkedId)
                {
                    case R.id.regionRadioButton:
                        getEventListener().onCheckedChangedSort(StayFilter.SortType.DEFAULT);
                        break;

                    case R.id.distanceRadioButton:
                        getEventListener().onCheckedChangedSort(StayFilter.SortType.DISTANCE);
                        return;

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
                        return;
                }
            }
        });
    }

    private void initAmenitiesLayout(ActivityStayFilterDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        int count = viewDataBinding.amenityLayout.amenityGridLayout.getChildCount();

        for (int i = 0; i < count; i++)
        {
            viewDataBinding.amenityLayout.amenityGridLayout.getChildAt(i).setOnClickListener(this);
        }
    }

    private void initRoomAmenitiesLayout(ActivityStayFilterDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        int count = viewDataBinding.roomAmenityLayout.amenityRoomGridLayout.getChildCount();

        for (int i = 0; i < count; i++)
        {
            viewDataBinding.roomAmenityLayout.amenityRoomGridLayout.getChildAt(i).setOnClickListener(this);
        }
    }
}
