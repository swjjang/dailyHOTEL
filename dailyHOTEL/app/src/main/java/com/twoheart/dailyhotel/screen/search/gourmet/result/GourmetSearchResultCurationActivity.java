package com.twoheart.dailyhotel.screen.search.gourmet.result;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.GourmetCuration;
import com.twoheart.dailyhotel.model.GourmetCurationOption;
import com.twoheart.dailyhotel.model.GourmetFilter;
import com.twoheart.dailyhotel.model.GourmetFilters;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.place.activity.PlaceCurationActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCurationActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyTextView;
import com.twoheart.dailyhotel.widget.DailyToast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GourmetSearchResultCurationActivity extends GourmetCurationActivity
{
    private static final String INTENT_EXTRA_DATA_SEARCHTYPE = "searcyType";

    private SearchType mSearchType;

    public static Intent newInstance(Context context, ViewType viewType, SearchType searchType, GourmetCuration gourmetCuration)
    {
        Intent intent = new Intent(context, GourmetCurationActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_VIEWTYPE, viewType.name());
        intent.putExtra(INTENT_EXTRA_DATA_SEARCHTYPE, searchType.name());
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACECURATION, gourmetCuration);

        return intent;
    }

    @Override
    protected void initIntent(Intent intent)
    {
        super.initIntent(intent);

        mSearchType = SearchType.valueOf(intent.getStringExtra(INTENT_EXTRA_DATA_SEARCHTYPE));
    }

    @Override
    protected void initSortLayout(View view, ViewType viewType, GourmetCurationOption gourmetCurationOption)
    {
        mSortRadioGroup = (RadioGroup) view.findViewById(R.id.sortLayout);

        if (viewType == ViewType.MAP)
        {
            setDisabledSortLayout(view, mSortRadioGroup);
            return;
        }

        RadioButton radioButton = (RadioButton)mSortRadioGroup.findViewById(R.id.regionCheckView);

        if(mSearchType == SearchType.LOCATION)
        {
            radioButton.setVisibility(View.GONE);

            mSortRadioGroup.check(R.id.distanceCheckView);
        } else
        {
            // 추후 아이콘 변경필요
            radioButton.setText(R.string.label_sort_by_rank);
            radioButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.selector_sort_area_button, 0, 0);
        }

        switch (gourmetCurationOption.getSortType())
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
        }

        mSortRadioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    protected void resetCuration()
    {
        GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mGourmetCuration.getCurationOption();

        gourmetCurationOption.clear();

        if (mViewType == ViewType.LIST)
        {
            mSortRadioGroup.setOnCheckedChangeListener(null);

            if(mSearchType == SearchType.LOCATION)
            {
                mSortRadioGroup.check(R.id.distanceCheckView);
            } else
            {
                mSortRadioGroup.check(R.id.regionCheckView);
            }

            mSortRadioGroup.setOnCheckedChangeListener(this);
        }

        if (mGourmetCuration.getProvince().isOverseas == false)
        {
            resetLayout(mGridLayout);
            resetLayout(mAmenitiesLayout);
            resetLayout(mTimeRangeLayout);
        }

        requestUpdateResult();
    }
}