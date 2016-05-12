package com.twoheart.dailyhotel.screen.hotel.filter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.HotelCurationOption;
import com.twoheart.dailyhotel.model.HotelFilter;
import com.twoheart.dailyhotel.model.HotelFilters;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.place.activity.PlaceCurationActivity;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HotelCurationActivity extends PlaceCurationActivity implements RadioGroup.OnCheckedChangeListener
{
    public static final String INTENT_EXTRA_DATA_CURATION_OPTIONS = "curationOptions";
    public static final String INTENT_EXTRA_DATA_VIEWTYPE = "viewType";

    private HotelCurationOption mHotelCurationOption;

    private boolean mIsGlobal;
    private ViewType mViewType;

    private RadioGroup mSortRadioGroup;
    private android.support.v7.widget.GridLayout mGridLayout;

    private View mMinusPersonView;
    private View mPlusPersonView;
    private TextView mPersonCountView;
    private ViewGroup mBedTypeLayout;

    public static Intent newInstance(Context context, boolean isGlobal, ViewType viewType, HotelCurationOption hotelCurationOption)
    {
        Intent intent = new Intent(context, HotelCurationActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_REGION, isGlobal);
        intent.putExtra(INTENT_EXTRA_DATA_VIEWTYPE, viewType.name());
        intent.putExtra(INTENT_EXTRA_DATA_CURATION_OPTIONS, hotelCurationOption);

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
        mHotelCurationOption = intent.getParcelableExtra(INTENT_EXTRA_DATA_CURATION_OPTIONS);

        initLayout();
    }

    @Override
    protected void initContentLayout(ViewGroup contentLayout)
    {
        View sortLayout = LayoutInflater.from(this).inflate(R.layout.layout_hotel_sort, null);
        initSortLayout(sortLayout, mViewType, mHotelCurationOption);

        contentLayout.addView(sortLayout);

        if (mIsGlobal == false)
        {
            View filterLayout = LayoutInflater.from(this).inflate(R.layout.layout_hotel_filter, null);
            initFilterLayout(filterLayout, mHotelCurationOption);

            contentLayout.addView(filterLayout);

            initAmenitiesLayout(filterLayout, mHotelCurationOption);
        } else
        {
            requestUpdateResult();
        }
    }

    private void initSortLayout(View view, ViewType viewType, HotelCurationOption hotelCurationOption)
    {
        mSortRadioGroup = (RadioGroup) view.findViewById(R.id.sortLayout);

        if (mIsGlobal == true)
        {
            View satisfactionCheckView = view.findViewById(R.id.satisfactionCheckView);
            satisfactionCheckView.setVisibility(View.INVISIBLE);
        }

        if (viewType == ViewType.MAP)
        {
            setDisabledSortLayout(view, mSortRadioGroup);
            return;
        }

        switch (hotelCurationOption.getSortType())
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

    private void initFilterLayout(View view, HotelCurationOption hotelCurationOption)
    {
        // 인원
        mMinusPersonView = view.findViewById(R.id.minusPersonView);
        mPlusPersonView = view.findViewById(R.id.plusPersonView);

        View minusDimView = view.findViewById(R.id.minusDimView);
        View plusDimView = view.findViewById(R.id.plusDimView);

        mMinusPersonView.setTag(minusDimView);
        mPlusPersonView.setTag(plusDimView);

        mPersonCountView = (TextView) view.findViewById(R.id.personCountView);

        mMinusPersonView.setOnClickListener(this);
        mPlusPersonView.setOnClickListener(this);

        updatePersonFilter(hotelCurationOption.person);

        // 베드타입
        mBedTypeLayout = (ViewGroup) view.findViewById(R.id.bedTypeLayout);
        View doubleCheckView = view.findViewById(R.id.doubleCheckView);
        View twinCheckView = view.findViewById(R.id.twinCheckView);
        View heatedFloorsCheckView = view.findViewById(R.id.heatedFloorsCheckView);

        doubleCheckView.setOnClickListener(this);
        twinCheckView.setOnClickListener(this);
        heatedFloorsCheckView.setOnClickListener(this);

        if ((hotelCurationOption.flagBedTypeFilters & HotelFilter.FLAG_HOTEL_FILTER_BED_DOUBLE) == HotelFilter.FLAG_HOTEL_FILTER_BED_DOUBLE)
        {
            updateBedTypeFilter(doubleCheckView, HotelFilter.FLAG_HOTEL_FILTER_BED_DOUBLE);
        }

        if ((hotelCurationOption.flagBedTypeFilters & HotelFilter.FLAG_HOTEL_FILTER_BED_TWIN) == HotelFilter.FLAG_HOTEL_FILTER_BED_TWIN)
        {
            updateBedTypeFilter(twinCheckView, HotelFilter.FLAG_HOTEL_FILTER_BED_TWIN);
        }

        if ((hotelCurationOption.flagBedTypeFilters & HotelFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS) == HotelFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS)
        {
            updateBedTypeFilter(heatedFloorsCheckView, HotelFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS);
        }
    }

    private void initAmenitiesLayout(View view, HotelCurationOption hotelCurationOption)
    {
        mGridLayout = (android.support.v7.widget.GridLayout) view.findViewById(R.id.amenitiesGridLayout);

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Integer flag = (Integer) v.getTag();

                if (flag == null)
                {
                    v.setSelected(false);
                    return;
                }

                if (v.isSelected() == true)
                {
                    v.setSelected(false);
                    mHotelCurationOption.flagAmenitiesFilters ^= flag;

                    AnalyticsManager.getInstance(HotelCurationActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                        , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_UNCLICKED, (String) v.getTag(v.getId()), null);
                } else
                {
                    v.setSelected(true);
                    mHotelCurationOption.flagAmenitiesFilters |= flag;

                    AnalyticsManager.getInstance(HotelCurationActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                        , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, (String) v.getTag(v.getId()), null);
                }

                requestUpdateResultDelayed();
            }
        };

        final String[] amenities = new String[]{getString(R.string.label_wifi)//
            , getString(R.string.label_breakfast)//
            , getString(R.string.label_cooking)//
            , getString(R.string.label_beth)//
            , getString(R.string.label_parking)//
            , getString(R.string.label_pool)//
            , getString(R.string.label_fitness)};

        final String[] analytics = new String[]{AnalyticsManager.Label.SORTFILTER_WIFI//
            , AnalyticsManager.Label.SORTFILTER_FREEBREAKFAST//
            , AnalyticsManager.Label.SORTFILTER_KITCHEN//
            , AnalyticsManager.Label.SORTFILTER_BATHTUB//
            , AnalyticsManager.Label.SORTFILTER_PARKINGAVAILABEL//
            , AnalyticsManager.Label.SORTFILTER_POOL//
            , AnalyticsManager.Label.SORTFILTER_FITNESS};

        final int[] amenitiesResId = new int[]{R.drawable.selector_filter_amenities_wifi_button//
            , R.drawable.selector_filter_amenities_breakfast_button//
            , R.drawable.selector_filter_amenities_cooking_button//
            , R.drawable.selector_filter_amenities_bath_button//
            , R.drawable.selector_filter_amenities_parking_button//
            , R.drawable.selector_filter_amenities_pool_button//
            , R.drawable.selector_filter_amenities_fitness_button};

        final int[] amenitiesflag = new int[]{HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_WIFI//
            , HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_BREAKFAST//
            , HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_COOKING//
            , HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_BATH//
            , HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_PARKING//
            , HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_POOL//
            , HotelFilter.FLAG_HOTEL_FILTER_AMENITIES_FITNESS};

        int length = amenities.length;

        for (int i = 0; i < length; i++)
        {
            DailyTextView amenitiesView = getGridLayoutItemView(amenities[i], amenitiesResId[i], false);
            amenitiesView.setOnClickListener(onClickListener);
            amenitiesView.setTag(amenitiesflag[i]);
            amenitiesView.setTag(amenitiesView.getId(), analytics[i]);

            if ((hotelCurationOption.flagAmenitiesFilters & amenitiesflag[i]) == amenitiesflag[i])
            {
                amenitiesView.setSelected(true);
            }

            mGridLayout.addView(amenitiesView);
        }

        mGridLayout.setPadding(Util.dpToPx(this, 10), 0, Util.dpToPx(this, 10), Util.dpToPx(this, 10));
    }

    private void updatePersonFilter(int person)
    {
        if (person < HotelFilter.MIN_PERSON)
        {
            person = HotelFilter.MIN_PERSON;
        } else if (person > HotelFilter.MAX_PERSON)
        {
            person = HotelFilter.MAX_PERSON;
        }

        mHotelCurationOption.person = person;

        mPersonCountView.setText(getString(R.string.label_more_person, person));

        View minusDimview = (View) mMinusPersonView.getTag();
        View plusDimview = (View) mPlusPersonView.getTag();

        if (person == HotelFilter.MIN_PERSON)
        {
            mMinusPersonView.setEnabled(false);
            mPlusPersonView.setEnabled(true);
            minusDimview.setVisibility(View.VISIBLE);
            plusDimview.setVisibility(View.GONE);
        } else if (person == HotelFilter.MAX_PERSON)
        {
            mMinusPersonView.setEnabled(true);
            mPlusPersonView.setEnabled(false);
            minusDimview.setVisibility(View.GONE);
            plusDimview.setVisibility(View.VISIBLE);
        } else
        {
            mMinusPersonView.setEnabled(true);
            mPlusPersonView.setEnabled(true);
            minusDimview.setVisibility(View.GONE);
            plusDimview.setVisibility(View.GONE);
        }

        requestUpdateResultDelayed();
    }

    private void updateBedTypeFilter(View view, int flag)
    {
        if (view.isSelected() == true)
        {
            view.setSelected(false);
            mHotelCurationOption.flagBedTypeFilters ^= flag;
        } else
        {
            view.setSelected(true);
            mHotelCurationOption.flagBedTypeFilters |= flag;
        }

        requestUpdateResultDelayed();
    }

    private void resetCuration()
    {
        mHotelCurationOption.clear();

        if (mViewType == ViewType.LIST)
        {
            mSortRadioGroup.setOnCheckedChangeListener(null);
            mSortRadioGroup.check(R.id.regionCheckView);
            mSortRadioGroup.setOnCheckedChangeListener(this);
        }

        if (mIsGlobal == false)
        {
            updatePersonFilter(HotelFilter.MIN_PERSON);

            resetLayout(mBedTypeLayout);
            resetLayout(mGridLayout);
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
                ArrayList<HotelFilters> hotelFiltersList = mHotelCurationOption.getFiltersList();

                if (Category.ALL.code.equalsIgnoreCase(mHotelCurationOption.getCategory().code) == true)
                {
                    for (HotelFilters hotelFilters : hotelFiltersList)
                    {
                        if (hotelFilters.isFiltered(mHotelCurationOption) == true)
                        {
                            count++;
                        }
                    }
                } else
                {
                    for (HotelFilters hotelFilters : hotelFiltersList)
                    {
                        if (mHotelCurationOption.getCategory().code.equalsIgnoreCase(hotelFilters.categoryCode) == true//
                            && hotelFilters.isFiltered(mHotelCurationOption) == true)
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
                setResultMessage(getString(R.string.label_hotel_filter_result_count, count));

                setConfirmOnClickListener(HotelCurationActivity.this);
                setConfirmEnable(count == 0 ? false : true);
            }
        }.execute();
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

        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_CURATION);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId)
    {
        RadioButton radioButton = (RadioButton) group.findViewById(checkedId);

        if (radioButton == null)
        {
            return;
        }

        String label;

        boolean isChecked = radioButton.isChecked();

        if (isChecked == false)
        {
            return;
        }

        switch (checkedId)
        {
            case R.id.regionCheckView:
                mHotelCurationOption.setSortType(SortType.DEFAULT);
                label = AnalyticsManager.Label.SORTFILTER_DISTRICT;
                break;

            case R.id.distanceCheckView:
                mHotelCurationOption.setSortType(SortType.DISTANCE);
                label = AnalyticsManager.Label.SORTFILTER_DISTANCE;
                break;

            case R.id.lowPriceCheckView:
                mHotelCurationOption.setSortType(SortType.LOW_PRICE);
                label = AnalyticsManager.Label.SORTFILTER_LOWTOHIGHPRICE;
                break;

            case R.id.highPriceCheckView:
                mHotelCurationOption.setSortType(SortType.HIGH_PRICE);
                label = AnalyticsManager.Label.SORTFILTER_HIGHTOLOWPRICE;
                break;

            case R.id.satisfactionCheckView:
                mHotelCurationOption.setSortType(SortType.SATISFACTION);
                label = AnalyticsManager.Label.SORTFILTER_RATING;
                break;

            default:
                return;
        }

        Map<String, String> eventParmas = new HashMap<>();
        Province province = mHotelCurationOption.getProvince();

        if (province instanceof Area)
        {
            Area area = (Area) province;
            eventParmas.put(AnalyticsManager.KeyType.COUNTRY, area.getProvince().isOverseas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC);
            eventParmas.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
            eventParmas.put(AnalyticsManager.KeyType.DISTRICT, area.name);
        } else
        {
            eventParmas.put(AnalyticsManager.KeyType.COUNTRY, province.isOverseas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC);
            eventParmas.put(AnalyticsManager.KeyType.PROVINCE, province.name);
            eventParmas.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
        }

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, label, eventParmas);
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);

        switch (v.getId())
        {
            case R.id.minusPersonView:
                updatePersonFilter(mHotelCurationOption.person - 1);

                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                    , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, Integer.toString(mHotelCurationOption.person), null);
                break;

            case R.id.plusPersonView:
                updatePersonFilter(mHotelCurationOption.person + 1);

                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                    , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, Integer.toString(mHotelCurationOption.person), null);
                break;

            case R.id.doubleCheckView:
                updateBedTypeFilter(v, HotelFilter.FLAG_HOTEL_FILTER_BED_DOUBLE);

                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                    , v.isSelected() ? AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED : AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_UNCLICKED//
                    , AnalyticsManager.Label.SORTFILTER_DOUBLE, null);
                break;

            case R.id.twinCheckView:
                updateBedTypeFilter(v, HotelFilter.FLAG_HOTEL_FILTER_BED_TWIN);

                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                    , v.isSelected() ? AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED : AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_UNCLICKED//
                    , AnalyticsManager.Label.SORTFILTER_TWIN, null);
                break;

            case R.id.heatedFloorsCheckView:
                updateBedTypeFilter(v, HotelFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS);

                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                    , v.isSelected() ? AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED : AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_UNCLICKED//
                    , AnalyticsManager.Label.SORTFILTER_ONDOL, null);
                break;
        }
    }

    @Override
    protected void onComplete()
    {
        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.HOTEL_SORT_FILTER_APPLY_BUTTON_CLICKED, mHotelCurationOption.toString(), null);

        if (DEBUG == true)
        {
            ExLog.d(mHotelCurationOption.toString());
        }

        Intent intent = new Intent();
        intent.putExtra(INTENT_EXTRA_DATA_CURATION_OPTIONS, mHotelCurationOption);

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onCancel()
    {
        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, AnalyticsManager.Label.CLOSE_BUTTON_CLICKED, null);

        finish();
    }

    @Override
    protected void onReset()
    {
        resetCuration();

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, AnalyticsManager.Label.RESET_BUTTON_CLICKED, null);
    }
}