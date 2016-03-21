package com.twoheart.dailyhotel.screen.gourmet.list;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetCurationOption;
import com.twoheart.dailyhotel.model.GourmetFilter;
import com.twoheart.dailyhotel.model.GourmetFilters;
import com.twoheart.dailyhotel.place.activity.PlaceCurationActivity;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.view.widget.DailyTextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class GourmetCurationActivity extends PlaceCurationActivity implements RadioGroup.OnCheckedChangeListener
{
    public static final String INTENT_EXTRA_DATA_CURATION_OPTIONS = "curationOptions";
    public static final String INTENT_EXTRA_DATA_VIEWTYPE = "viewType";

    private static final int GOURMET_CATEGORY_COLUMN = 5;

    private GourmetCurationOption mGourmetCurationOption;

    private boolean mIsGlobal;
    private ViewType mViewType;

    private RadioGroup mSortRadioGroup;
    private android.support.v7.widget.GridLayout mGridLayout;

    private ViewGroup mAmenitiesLayout;
    private ViewGroup mTimeRangeLayout;

    public static Intent newInstance(Context context, boolean isGlobal, ViewType viewType, GourmetCurationOption gourmetCurationOption)
    {
        Intent intent = new Intent(context, GourmetCurationActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_REGION, isGlobal);
        intent.putExtra(INTENT_EXTRA_DATA_VIEWTYPE, viewType.name());
        intent.putExtra(INTENT_EXTRA_DATA_CURATION_OPTIONS, gourmetCurationOption);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mIsShowStatusBar = false;

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent == null)
        {
            finish();
            return;
        }

        mIsGlobal = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_REGION, false);
        mViewType = ViewType.valueOf(intent.getStringExtra(INTENT_EXTRA_DATA_VIEWTYPE));
        mGourmetCurationOption = intent.getParcelableExtra(INTENT_EXTRA_DATA_CURATION_OPTIONS);

        initLayout();
    }

    @Override
    protected void initContentLayout(ViewGroup contentLayout)
    {
        View sortLayout = LayoutInflater.from(this).inflate(R.layout.layout_gourmet_sort, null);
        initSortLayout(sortLayout, mViewType, mGourmetCurationOption);

        contentLayout.addView(sortLayout);

        View filterLayout = LayoutInflater.from(this).inflate(R.layout.layout_gourmet_filter, null);
        initFilterLayout(filterLayout, mGourmetCurationOption);

        contentLayout.addView(filterLayout);

        requestUpdateResultDelayed();
    }

    private void initSortLayout(View view, ViewType viewType, GourmetCurationOption gourmetCurationOption)
    {
        mSortRadioGroup = (RadioGroup) view.findViewById(R.id.sortLayout);

        if (viewType == ViewType.MAP)
        {
            setDisabledSortLayout(view, mSortRadioGroup);
            return;
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

    private void initFilterLayout(View view, GourmetCurationOption gourmetCurationOption)
    {
        mGridLayout = (android.support.v7.widget.GridLayout) view.findViewById(R.id.foodGridLayout);

        final HashMap<String, Integer> categroySequenceMap = gourmetCurationOption.getCategorySequenceMap();
        TreeMap<String, Integer> categoryMap = new TreeMap<String, Integer>(new Comparator<String>()
        {
            @Override
            public int compare(String o1, String o2)
            {
                Integer sequence1 = categroySequenceMap.get(o1);
                Integer sequence2 = categroySequenceMap.get(o2);

                if (sequence1.intValue() < 0)
                {
                    sequence1 = Integer.MAX_VALUE;
                }

                if (sequence2.intValue() < 0)
                {
                    sequence2 = Integer.MAX_VALUE;
                }

                int result = sequence1.compareTo(sequence2);

                if (result == 0)
                {
                    return o1.compareTo(o2);
                } else
                {
                    return sequence1.compareTo(sequence2);
                }
            }
        });

        categoryMap.putAll(categroySequenceMap);

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                HashMap<String, Integer> filterMap = mGourmetCurationOption.getFilterMap();
                DailyTextView dailyTextView = (DailyTextView) v;
                String key = dailyTextView.getText().toString();

                if (dailyTextView.isSelected() == true)
                {
                    dailyTextView.setSelected(false);
                    filterMap.remove(key);

                    AnalyticsManager.getInstance(GourmetCurationActivity.this).recordEvent(AnalyticsManager.Category.POPUPBOXES//
                        , AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_UNCLICKED, key, null);
                } else
                {
                    dailyTextView.setSelected(true);
                    filterMap.put(key, 0);

                    AnalyticsManager.getInstance(GourmetCurationActivity.this).recordEvent(AnalyticsManager.Category.POPUPBOXES//
                        , AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_CLICKED, key, null);
                }

                requestUpdateResultDelayed();
            }
        };

        List<String> keyList = new ArrayList<>(categoryMap.keySet());
        HashMap<String, Integer> categroyCodeMap = gourmetCurationOption.getCategoryCoderMap();
        HashMap<String, Integer> filterMap = gourmetCurationOption.getFilterMap();

        boolean isSingleLine = keyList.size() <= GOURMET_CATEGORY_COLUMN ? true : false;


        for (String key : keyList)
        {
            DailyTextView categoryView = getGridLayoutItemView(key, getCategoryResourceId(categroyCodeMap.get(key).intValue()), isSingleLine);
            categoryView.setOnClickListener(onClickListener);

            if (filterMap.containsKey(key) == true)
            {
                categoryView.setSelected(true);
            }

            mGridLayout.addView(categoryView);
        }

        if (isSingleLine == false)
        {
            mGridLayout.setPadding(Util.dpToPx(this, 10), 0, Util.dpToPx(this, 10), Util.dpToPx(this, 10));
        }

        initAmenitiesLayout(view, gourmetCurationOption);
        initTimeRangeFilterLayout(view, gourmetCurationOption);

        requestUpdateResultDelayed();
    }

    private void initAmenitiesLayout(View view, GourmetCurationOption gourmetCurationOption)
    {
        mAmenitiesLayout = (ViewGroup) view.findViewById(R.id.amenitiesLayout);

        View parkingCheckView = mAmenitiesLayout.findViewById(R.id.parkingCheckView);
        parkingCheckView.setTag(parkingCheckView.getId(), AnalyticsManager.Label.SORTFILTER_PARKINGAVAILABEL);

        if ((gourmetCurationOption.flagAmenitiesFilters & GourmetFilters.FLAG_HOTEL_FILTER_AMENITIES_PARKING) == GourmetFilters.FLAG_HOTEL_FILTER_AMENITIES_PARKING)
        {
            parkingCheckView.setSelected(true);
        }

        parkingCheckView.setOnClickListener(this);
    }

    private void initTimeRangeFilterLayout(View view, GourmetCurationOption gourmetCurationOption)
    {
        mTimeRangeLayout = (ViewGroup) view.findViewById(R.id.timeRangeLayout);

        View time0611View = mTimeRangeLayout.findViewById(R.id.time0611View);
        View time1115View = mTimeRangeLayout.findViewById(R.id.time1115View);
        View time1517View = mTimeRangeLayout.findViewById(R.id.time1517View);
        View time1721View = mTimeRangeLayout.findViewById(R.id.time1721View);
        View time2106View = mTimeRangeLayout.findViewById(R.id.time2106View);

        time0611View.setTag(AnalyticsManager.Label.SORTFILTER_0611);
        time1115View.setTag(AnalyticsManager.Label.SORTFILTER_1115);
        time1517View.setTag(AnalyticsManager.Label.SORTFILTER_1517);
        time1721View.setTag(AnalyticsManager.Label.SORTFILTER_1721);
        time2106View.setTag(AnalyticsManager.Label.SORTFILTER_2106);

        time0611View.setOnClickListener(this);
        time1115View.setOnClickListener(this);
        time1517View.setOnClickListener(this);
        time1721View.setOnClickListener(this);
        time2106View.setOnClickListener(this);

        if (gourmetCurationOption.flagTimeFilter == GourmetFilter.FLAG_GOURMET_FILTER_TIME_NONE)
        {
            return;
        }

        if ((gourmetCurationOption.flagTimeFilter & GourmetFilter.FLAG_GOURMET_FILTER_TIME_06_11) == GourmetFilter.FLAG_GOURMET_FILTER_TIME_06_11)
        {
            time0611View.setSelected(true);
        }

        if ((gourmetCurationOption.flagTimeFilter & GourmetFilter.FLAG_GOURMET_FILTER_TIME_11_15) == GourmetFilter.FLAG_GOURMET_FILTER_TIME_11_15)
        {
            time1115View.setSelected(true);
        }

        if ((gourmetCurationOption.flagTimeFilter & GourmetFilter.FLAG_GOURMET_FILTER_TIME_15_17) == GourmetFilter.FLAG_GOURMET_FILTER_TIME_15_17)
        {
            time1517View.setSelected(true);
        }

        if ((gourmetCurationOption.flagTimeFilter & GourmetFilter.FLAG_GOURMET_FILTER_TIME_17_21) == GourmetFilter.FLAG_GOURMET_FILTER_TIME_17_21)
        {
            time1721View.setSelected(true);
        }

        if ((gourmetCurationOption.flagTimeFilter & GourmetFilter.FLAG_GOURMET_FILTER_TIME_21_06) == GourmetFilter.FLAG_GOURMET_FILTER_TIME_21_06)
        {
            time2106View.setSelected(true);
        }
    }

    private void updateAmenitiesFilter(View view, int flag)
    {
        if (view == null)
        {
            return;
        }

        if (view.isSelected() == true)
        {
            view.setSelected(false);
            mGourmetCurationOption.flagAmenitiesFilters ^= flag;

            AnalyticsManager.getInstance(GourmetCurationActivity.this).recordEvent(AnalyticsManager.Category.POPUPBOXES//
                , AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_UNCLICKED, (String) view.getTag(view.getId()), null);
        } else
        {
            view.setSelected(true);
            mGourmetCurationOption.flagAmenitiesFilters |= flag;

            AnalyticsManager.getInstance(GourmetCurationActivity.this).recordEvent(AnalyticsManager.Category.POPUPBOXES//
                , AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_CLICKED, (String) view.getTag(view.getId()), null);
        }

        requestUpdateResultDelayed();
    }

    private void updateTimeRangeFilter(View view, int flag)
    {
        if (view.isSelected() == true)
        {
            view.setSelected(false);
            mGourmetCurationOption.flagTimeFilter ^= flag;

            AnalyticsManager.getInstance(GourmetCurationActivity.this).recordEvent(AnalyticsManager.Category.POPUPBOXES//
                , AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_UNCLICKED, (String) view.getTag(), null);
        } else
        {
            view.setSelected(true);
            mGourmetCurationOption.flagTimeFilter |= flag;

            AnalyticsManager.getInstance(GourmetCurationActivity.this).recordEvent(AnalyticsManager.Category.POPUPBOXES//
                , AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_CLICKED, (String) view.getTag(), null);
        }

        requestUpdateResultDelayed();
    }

    private void resetCuration()
    {
        mGourmetCurationOption.clear();

        if (mViewType == ViewType.LIST)
        {
            mSortRadioGroup.setOnCheckedChangeListener(null);
            mSortRadioGroup.check(R.id.regionCheckView);
            mSortRadioGroup.setOnCheckedChangeListener(this);
        }

        if (mIsGlobal == false)
        {
            resetLayout(mGridLayout);
            resetLayout(mAmenitiesLayout);
            resetLayout(mTimeRangeLayout);
        }

        requestUpdateResult();
    }

    @Override
    protected void updateResultMessage()
    {
        setConfirmOnClickListener(null);

        new AsyncTask<Void, Void, Integer>()
        {
            @Override
            protected Integer doInBackground(Void... params)
            {
                int count = 0;
                HashMap<String, Integer> filterMap = mGourmetCurationOption.getFilterMap();
                ArrayList<GourmetFilters> gourmetFiltersList = mGourmetCurationOption.getFiltersList();

                if (filterMap == null || filterMap.size() == 0)
                {
                    for (GourmetFilters gourmetFilters : gourmetFiltersList)
                    {
                        if (gourmetFilters.isFiltered(mGourmetCurationOption) == true)
                        {
                            count++;
                        }
                    }
                } else
                {
                    for (GourmetFilters gourmetFilters : gourmetFiltersList)
                    {
                        if (filterMap.containsKey(gourmetFilters.category) == true//
                            && gourmetFilters.isFiltered(mGourmetCurationOption) == true)
                        {
                            count++;
                        }
                    }
                }

                return count;
            }

            @Override
            protected void onPostExecute(Integer count)
            {
                setResultMessage(getString(R.string.label_gourmet_filter_result_count, count));

                setConfirmOnClickListener(GourmetCurationActivity.this);
                setConfirmEnable(count.intValue() == 0 ? false : true);
            }
        }.execute();
    }

    private int getCategoryResourceId(int index)
    {
        final int[] resourceIndex = new int[]{R.drawable.selector_gourmet_category_button00//
            , R.drawable.selector_gourmet_category_button01//
            , R.drawable.selector_gourmet_category_button02//
            , R.drawable.selector_gourmet_category_button03//
            , R.drawable.selector_gourmet_category_button04//
            , R.drawable.selector_gourmet_category_button05//
            , R.drawable.selector_gourmet_category_button06//
            , R.drawable.selector_gourmet_category_button07//
            , R.drawable.selector_gourmet_category_button08//
            , R.drawable.selector_gourmet_category_button09//
            , R.drawable.selector_gourmet_category_button10//
            , R.drawable.selector_gourmet_category_button11//
            , R.drawable.selector_gourmet_category_button12//
            , R.drawable.selector_gourmet_category_button13//
            , R.drawable.selector_gourmet_category_button14};

        if (index < 1 || index >= resourceIndex.length)
        {
            index = 0;
        }

        return resourceIndex[index];
    }

    private void setDisabledSortLayout(View view, RadioGroup sortLayout)
    {
        if (sortLayout == null)
        {
            return;
        }

        sortLayout.setEnabled(false);

        int childCount = sortLayout.getChildCount();

        for (int i = 0; i < childCount; i++)
        {
            sortLayout.getChildAt(i).setEnabled(false);
        }

        View dimView = view.findViewById(R.id.dimView);
        dimView.setVisibility(View.VISIBLE);
    }

    private void resetLayout(ViewGroup viewGroup)
    {
        if (viewGroup == null)
        {
            return;
        }

        int childCount = viewGroup.getChildCount();

        for (int i = 0; i < childCount; i++)
        {
            viewGroup.getChildAt(i).setSelected(false);
        }
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_CURATION, null);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId)
    {
        RadioButton radioButton = (RadioButton) group.findViewById(checkedId);

        if (radioButton == null)
        {
            return;
        }

        String label = AnalyticsManager.Label.SORTFILTER_DISTRICT;

        boolean isChecked = radioButton.isChecked();

        if (isChecked == false)
        {
            return;
        }

        switch (checkedId)
        {
            case R.id.regionCheckView:
                mGourmetCurationOption.setSortType(SortType.DEFAULT);
                label = AnalyticsManager.Label.SORTFILTER_DISTRICT;
                break;

            case R.id.distanceCheckView:
                mGourmetCurationOption.setSortType(SortType.DISTANCE);
                label = AnalyticsManager.Label.SORTFILTER_DISTANCE;
                break;

            case R.id.lowPriceCheckView:
                mGourmetCurationOption.setSortType(SortType.LOW_PRICE);
                label = AnalyticsManager.Label.SORTFILTER_LOWTOHIGHPRICE;
                break;

            case R.id.highPriceCheckView:
                mGourmetCurationOption.setSortType(SortType.HIGH_PRICE);
                label = AnalyticsManager.Label.SORTFILTER_HIGHTOLOWPRICE;
                break;

            case R.id.satisfactionCheckView:
                mGourmetCurationOption.setSortType(SortType.SATISFACTION);
                label = AnalyticsManager.Label.SORTFILTER_RATING;
                break;

            default:
                return;
        }

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUPBOXES//
            , AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_CLICKED, label, null);
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);

        switch (v.getId())
        {
            case R.id.parkingCheckView:
                updateAmenitiesFilter(v, GourmetFilters.FLAG_HOTEL_FILTER_AMENITIES_PARKING);
                break;

            case R.id.time0611View:
                updateTimeRangeFilter(v, GourmetFilter.FLAG_GOURMET_FILTER_TIME_06_11);
                break;

            case R.id.time1115View:
                updateTimeRangeFilter(v, GourmetFilter.FLAG_GOURMET_FILTER_TIME_11_15);
                break;

            case R.id.time1517View:
                updateTimeRangeFilter(v, GourmetFilter.FLAG_GOURMET_FILTER_TIME_15_17);
                break;

            case R.id.time1721View:
                updateTimeRangeFilter(v, GourmetFilter.FLAG_GOURMET_FILTER_TIME_17_21);
                break;

            case R.id.time2106View:
                updateTimeRangeFilter(v, GourmetFilter.FLAG_GOURMET_FILTER_TIME_21_06);
                break;
        }
    }

    @Override
    protected void onComplete()
    {
        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUPBOXES//
            , AnalyticsManager.Action.GOURMET_SORT_FILTER_APPLY_BUTTON_CLICKED, mGourmetCurationOption.toString(), null);

        if (DEBUG == true)
        {
            ExLog.d(mGourmetCurationOption.toString());
        }

        Intent intent = new Intent();
        intent.putExtra(INTENT_EXTRA_DATA_CURATION_OPTIONS, mGourmetCurationOption);

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onCancel()
    {
        if (mGourmetCurationOption.isDefaultFilter() == true)
        {
            Intent intent = new Intent();
            intent.putExtra(INTENT_EXTRA_DATA_CURATION_OPTIONS, mGourmetCurationOption);
            setResult(RESULT_OK, intent);
        }

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUPBOXES//
            , AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_CLICKED, AnalyticsManager.Label.CLOSE_BUTTON_CLICKED, null);

        finish();
    }

    @Override
    protected void onReset()
    {
        resetCuration();

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUPBOXES//
            , AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_CLICKED, AnalyticsManager.Label.RESET_BUTTON_CLICKED, null);
    }
}