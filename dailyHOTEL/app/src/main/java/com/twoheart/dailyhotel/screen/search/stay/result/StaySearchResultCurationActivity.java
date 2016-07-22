package com.twoheart.dailyhotel.screen.search.stay.result;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.StayCuration;
import com.twoheart.dailyhotel.model.StayCurationOption;
import com.twoheart.dailyhotel.model.StayFilter;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCurationActivity;

public class StaySearchResultCurationActivity extends StayCurationActivity
{
    private static final String INTENT_EXTRA_DATA_SEARCHTYPE = "searchType";

    private SearchType mSearchType;

    public static Intent newInstance(Context context, ViewType viewType, SearchType searchType, StayCuration stayCuration)
    {
        Intent intent = new Intent(context, StaySearchResultCurationActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_VIEWTYPE, viewType.name());
        intent.putExtra(INTENT_EXTRA_DATA_SEARCHTYPE, searchType.name());
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACECURATION, stayCuration);

        return intent;
    }

    @Override
    protected void initIntent(Intent intent)
    {
        super.initIntent(intent);

        mSearchType = SearchType.valueOf(intent.getStringExtra(INTENT_EXTRA_DATA_SEARCHTYPE));
    }

    @Override
    protected void initSortLayout(View view, ViewType viewType, StayCurationOption stayCurationOption)
    {
        mSortRadioGroup = (RadioGroup) view.findViewById(R.id.sortLayout);

        if (viewType == ViewType.MAP)
        {
            setDisabledSortLayout(view, mSortRadioGroup);
            return;
        }

        RadioButton radioButton = (RadioButton) mSortRadioGroup.findViewById(R.id.regionCheckView);

        if (mSearchType == SearchType.LOCATION)
        {
            radioButton.setVisibility(View.GONE);

            stayCurationOption.setSortType(SortType.DISTANCE);
        } else
        {
            radioButton.setText(R.string.label_sort_by_rank);
            radioButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selector_sort_rank_button, 0, 0);
        }

        switch (stayCurationOption.getSortType())
        {
            case DEFAULT:
                mSortRadioGroup.check(R.id.regionCheckView);
                break;

            case DISTANCE:
                mSortRadioGroup.check(R.id.distanceCheckView);
                break;

            case LOW_PRICE:
                mSortRadioGroup.check(R.id.lowPriceCheckView);
                break;

            case HIGH_PRICE:
                mSortRadioGroup.check(R.id.highPriceCheckView);
                break;

            case SATISFACTION:
                mSortRadioGroup.check(R.id.satisfactionCheckView);
                break;
        }

        mSortRadioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    protected void resetCuration()
    {
        mStayCuration.getCurationOption().clear();

        if (mViewType == ViewType.LIST)
        {
            mSortRadioGroup.setOnCheckedChangeListener(null);

            if (mSearchType == SearchType.LOCATION)
            {
                mSortRadioGroup.check(R.id.distanceCheckView);
            } else
            {
                mSortRadioGroup.check(R.id.regionCheckView);
            }

            mSortRadioGroup.setOnCheckedChangeListener(this);
        }

        if (mStayCuration.getProvince().isOverseas == false)
        {
            updatePersonFilter(StayFilter.MIN_PERSON);

            resetLayout(mBedTypeLayout);
            resetLayout(mGridLayout);
        }

        requestUpdateResult();
    }
}