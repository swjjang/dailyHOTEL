package com.twoheart.dailyhotel.screen.gourmet.filter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.GourmetCuration;
import com.twoheart.dailyhotel.model.GourmetCurationOption;
import com.twoheart.dailyhotel.model.GourmetFilter;
import com.twoheart.dailyhotel.model.GourmetParams;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.place.activity.PlaceCurationActivity;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
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

public class GourmetCurationActivity extends PlaceCurationActivity implements RadioGroup.OnCheckedChangeListener
{
    public static final String INTENT_EXTRA_DATA_VIEWTYPE = "viewType";

    private static final int GOURMET_CATEGORY_COLUMN = 5;

    protected GourmetCuration mGourmetCuration;
    protected GourmetParams mLastParams;
    protected ViewType mViewType;

    protected BaseNetworkController mNetworkController;

    protected RadioGroup mSortRadioGroup;
    protected android.support.v7.widget.GridLayout mGridLayout;
    protected android.support.v7.widget.GridLayout mAmenitiesGridLayout;
    protected ViewGroup mTimeRangeLayout;

    public static Intent newInstance(Context context, ViewType viewType, GourmetCuration gourmetCuration)
    {
        Intent intent = new Intent(context, GourmetCurationActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_VIEWTYPE, viewType.name());
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACECURATION, gourmetCuration);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent == null)
        {
            finish();
            return;
        }

        initIntent(intent);

        mNetworkController = getNetworkController(this);

        initLayout();

        mAnimationLayout.setVisibility(View.INVISIBLE);
        mAnimationLayout.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                showAnimation();
            }
        }, 20);
    }

    protected void initIntent(Intent intent)
    {
        mViewType = ViewType.valueOf(intent.getStringExtra(INTENT_EXTRA_DATA_VIEWTYPE));
        mGourmetCuration = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACECURATION);
    }

    @Override
    protected void initContentLayout(ViewGroup contentLayout)
    {
        GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mGourmetCuration.getCurationOption();

        View sortLayout = LayoutInflater.from(this).inflate(R.layout.layout_gourmet_sort, null);
        initSortLayout(sortLayout, mViewType, gourmetCurationOption);

        contentLayout.addView(sortLayout);

        View filterLayout = LayoutInflater.from(this).inflate(R.layout.layout_gourmet_filter, null);
        initFilterLayout(filterLayout, gourmetCurationOption);

        initAmenitiesLayout(filterLayout, gourmetCurationOption);
        initTimeRangeFilterLayout(filterLayout, gourmetCurationOption);

        contentLayout.addView(filterLayout);
    }

    protected void initSortLayout(View view, ViewType viewType, GourmetCurationOption gourmetCurationOption)
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

                searchMyLocation();
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

    private void initFilterLayout(View view, GourmetCurationOption gourmetCurationOption)
    {
        // 음식 종류
        mGridLayout = (android.support.v7.widget.GridLayout) view.findViewById(R.id.foodGridLayout);

        final HashMap<String, Integer> categroySequenceMap = gourmetCurationOption.getCategorySequenceMap();
        TreeMap<String, Integer> categoryMap = new TreeMap<>(new Comparator<String>()
        {
            @Override
            public int compare(String o1, String o2)
            {
                Integer sequence1 = categroySequenceMap.get(o1);
                Integer sequence2 = categroySequenceMap.get(o2);

                if (sequence1 < 0)
                {
                    sequence1 = Integer.MAX_VALUE;
                }

                if (sequence2 < 0)
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

        List<String> keyList = new ArrayList<>(categoryMap.keySet());
        HashMap<String, Integer> categroyCodeMap = gourmetCurationOption.getCategoryCoderMap();
        HashMap<String, Integer> filterMap = gourmetCurationOption.getFilterMap();

        boolean isSingleLine = keyList.size() <= GOURMET_CATEGORY_COLUMN;

        for (String key : keyList)
        {
            DailyTextView categoryView = getGridLayoutItemView(key, getCategoryResourceId(categroyCodeMap.get(key)), isSingleLine);
            categoryView.setOnClickListener(mOnCategoryClickListener);

            if (filterMap.containsKey(key) == true)
            {
                categoryView.setSelected(true);
            }

            mGridLayout.addView(categoryView);
        }

        // 음식 종류가 COLUMN 개수보다 작으면 위치가 맞지 않는 경우가 발생해서 추가 개수를 넣어준다.
        if (keyList.size() < GOURMET_CATEGORY_COLUMN)
        {
            int addViewCount = GOURMET_CATEGORY_COLUMN - keyList.size();

            for (int i = 0; i < addViewCount; i++)
            {
                DailyTextView categoryView = getGridLayoutItemView(null, 0, isSingleLine);

                mGridLayout.addView(categoryView);
            }
        }

        if (isSingleLine == false)
        {
            mGridLayout.setPadding(Util.dpToPx(this, 10), 0, Util.dpToPx(this, 10), Util.dpToPx(this, 10));
        }

        requestUpdateResultDelayed();
    }

    private void initAmenitiesLayout(View view, GourmetCurationOption gourmetCurationOption)
    {
        mAmenitiesGridLayout = (android.support.v7.widget.GridLayout) view.findViewById(R.id.amenitiesGridLayout);

        View parkingCheckView = mAmenitiesGridLayout.findViewById(R.id.parkingCheckView);
        parkingCheckView.setTag(parkingCheckView.getId(), AnalyticsManager.Label.SORTFILTER_PARKINGAVAILABLE);

        DailyTextView valetCheckView = (DailyTextView) mAmenitiesGridLayout.findViewById(R.id.valetCheckView);
        valetCheckView.setTag(parkingCheckView.getId(), AnalyticsManager.Label.SORTFILTER_VALET);
        valetCheckView.setDrawableVectorTintList(R.color.selector_svg_color_d929292_s900034_eeaeaea);

        DailyTextView privateRoomCheckView = (DailyTextView) mAmenitiesGridLayout.findViewById(R.id.privateRoomCheckView);
        privateRoomCheckView.setTag(privateRoomCheckView.getId(), AnalyticsManager.Label.SORTFILTER_PRIVATEROOM);
        privateRoomCheckView.setDrawableVectorTintList(R.color.selector_svg_color_d929292_s900034_eeaeaea);

        DailyTextView groupBookingCheckView = (DailyTextView) mAmenitiesGridLayout.findViewById(R.id.groupBookingCheckView);
        groupBookingCheckView.setTag(groupBookingCheckView.getId(), AnalyticsManager.Label.SORTFILTER_GROUP);
        groupBookingCheckView.setDrawableVectorTintList(R.color.selector_svg_color_d929292_s900034_eeaeaea);

        DailyTextView babySeatCheckView = (DailyTextView) mAmenitiesGridLayout.findViewById(R.id.babySeatCheckView);
        babySeatCheckView.setTag(babySeatCheckView.getId(), AnalyticsManager.Label.SORTFILTER_BABYSEAT);
        babySeatCheckView.setDrawableVectorTintList(R.color.selector_svg_color_d929292_s900034_eeaeaea);

        DailyTextView corkageCheckView = (DailyTextView) mAmenitiesGridLayout.findViewById(R.id.corkageCheckView);
        corkageCheckView.setTag(corkageCheckView.getId(), AnalyticsManager.Label.SORTFILTER_CORKAGE);
        corkageCheckView.setDrawableVectorTintList(R.color.selector_svg_color_d929292_s900034_eeaeaea);

        if ((gourmetCurationOption.flagAmenitiesFilters & GourmetFilter.Amenities.FLAG_PARKING) == GourmetFilter.Amenities.FLAG_PARKING)
        {
            parkingCheckView.setSelected(true);
        }

        if ((gourmetCurationOption.flagAmenitiesFilters & GourmetFilter.Amenities.FLAG_VALET) == GourmetFilter.Amenities.FLAG_VALET)
        {
            valetCheckView.setSelected(true);
        }

        if ((gourmetCurationOption.flagAmenitiesFilters & GourmetFilter.Amenities.FLAG_PRIVATEROOM) == GourmetFilter.Amenities.FLAG_PRIVATEROOM)
        {
            privateRoomCheckView.setSelected(true);
        }

        if ((gourmetCurationOption.flagAmenitiesFilters & GourmetFilter.Amenities.FLAG_GROUPBOOKING) == GourmetFilter.Amenities.FLAG_GROUPBOOKING)
        {
            groupBookingCheckView.setSelected(true);
        }

        if ((gourmetCurationOption.flagAmenitiesFilters & GourmetFilter.Amenities.FLAG_BABYSEAT) == GourmetFilter.Amenities.FLAG_BABYSEAT)
        {
            babySeatCheckView.setSelected(true);
        }

        if ((gourmetCurationOption.flagAmenitiesFilters & GourmetFilter.Amenities.FLAG_CORKAGE) == GourmetFilter.Amenities.FLAG_CORKAGE)
        {
            corkageCheckView.setSelected(true);
        }

        parkingCheckView.setOnClickListener(this);
        valetCheckView.setOnClickListener(this);
        privateRoomCheckView.setOnClickListener(this);
        groupBookingCheckView.setOnClickListener(this);
        babySeatCheckView.setOnClickListener(this);
        corkageCheckView.setOnClickListener(this);
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

        if (gourmetCurationOption.flagTimeFilter == GourmetFilter.Time.FLAG_NONE)
        {
            return;
        }

        if ((gourmetCurationOption.flagTimeFilter & GourmetFilter.Time.FLAG_06_11) == GourmetFilter.Time.FLAG_06_11)
        {
            time0611View.setSelected(true);
        }

        if ((gourmetCurationOption.flagTimeFilter & GourmetFilter.Time.FLAG_11_15) == GourmetFilter.Time.FLAG_11_15)
        {
            time1115View.setSelected(true);
        }

        if ((gourmetCurationOption.flagTimeFilter & GourmetFilter.Time.FLAG_15_17) == GourmetFilter.Time.FLAG_15_17)
        {
            time1517View.setSelected(true);
        }

        if ((gourmetCurationOption.flagTimeFilter & GourmetFilter.Time.FLAG_17_21) == GourmetFilter.Time.FLAG_17_21)
        {
            time1721View.setSelected(true);
        }

        if ((gourmetCurationOption.flagTimeFilter & GourmetFilter.Time.FLAG_21_06) == GourmetFilter.Time.FLAG_21_06)
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

        GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mGourmetCuration.getCurationOption();

        if (view.isSelected() == true)
        {
            view.setSelected(false);
            gourmetCurationOption.flagAmenitiesFilters ^= flag;

            AnalyticsManager.getInstance(GourmetCurationActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                , AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_UNCLICKED, (String) view.getTag(view.getId()), null);
        } else
        {
            view.setSelected(true);
            gourmetCurationOption.flagAmenitiesFilters |= flag;

            AnalyticsManager.getInstance(GourmetCurationActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                , AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_CLICKED, (String) view.getTag(view.getId()), null);
        }

        requestUpdateResultDelayed();
    }

    private void updateTimeRangeFilter(View view, int flag)
    {
        GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mGourmetCuration.getCurationOption();

        if (view.isSelected() == true)
        {
            view.setSelected(false);
            gourmetCurationOption.flagTimeFilter ^= flag;

            AnalyticsManager.getInstance(GourmetCurationActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                , AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_UNCLICKED, (String) view.getTag(), null);
        } else
        {
            view.setSelected(true);
            gourmetCurationOption.flagTimeFilter |= flag;

            AnalyticsManager.getInstance(GourmetCurationActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                , AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_CLICKED, (String) view.getTag(), null);
        }

        requestUpdateResultDelayed();
    }

    protected void resetCuration()
    {
        GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mGourmetCuration.getCurationOption();

        gourmetCurationOption.clear();

        if (mViewType == ViewType.LIST)
        {
            mSortRadioGroup.setOnCheckedChangeListener(null);
            mSortRadioGroup.check(R.id.regionCheckView);
            mSortRadioGroup.setOnCheckedChangeListener(this);
        }

        if (mGourmetCuration.getProvince().isOverseas == false)
        {
            resetLayout(mGridLayout);
            resetLayout(mAmenitiesGridLayout);
            resetLayout(mTimeRangeLayout);
        }

        requestUpdateResultDelayed();
    }

    @Override
    protected void requestUpdateResult()
    {
        setResultMessage(getResources().getString(R.string.label_searching));

        if (mGourmetCuration == null || mGourmetCuration.getSaleTime() == null)
        {
            Util.restartApp(GourmetCurationActivity.this);
            return;
        }

        setLastGourmetParams(mGourmetCuration);

        super.requestUpdateResult();
    }

    @Override
    protected void requestUpdateResultDelayed()
    {
        setResultMessage(getResources().getString(R.string.label_searching));

        if (mGourmetCuration == null || mGourmetCuration.getSaleTime() == null)
        {
            Util.restartApp(GourmetCurationActivity.this);
            return;
        }

        setLastGourmetParams(mGourmetCuration);

        super.requestUpdateResultDelayed();
    }

    @Override
    protected void updateResultMessage()
    {
        setConfirmOnClickListener(null);

        if (mLastParams != null && SortType.DISTANCE == mLastParams.getSortType() && mLastParams.hasLocation() == false)
        {
            onSearchLoacationResult(null);
            return;
        }

        ((GourmetCurationNetworkController) mNetworkController).requestGourmetList(mLastParams);
    }

    protected void setLastGourmetParams(GourmetCuration gourmetCuration)
    {
        if (gourmetCuration == null)
        {
            return;
        }

        if (mLastParams == null)
        {
            mLastParams = new GourmetParams(gourmetCuration);
        } else
        {
            mLastParams.setPlaceParams(gourmetCuration);
        }
    }

    private int getCategoryResourceId(int index)
    {
        final int[] resourceIndex = new int[]{R.drawable.f_ic_gourmet_02_food_etc//
            , R.drawable.f_ic_gourmet_02_food_01//
            , R.drawable.f_ic_gourmet_02_food_02//
            , R.drawable.f_ic_gourmet_02_food_03//
            , R.drawable.f_ic_gourmet_02_food_04//
            , R.drawable.f_ic_gourmet_02_food_05//
            , R.drawable.f_ic_gourmet_02_food_06//
            , R.drawable.f_ic_gourmet_02_food_07//
            , R.drawable.f_ic_gourmet_02_food_08//
            , R.drawable.f_ic_gourmet_02_food_09//
            , R.drawable.f_ic_gourmet_02_food_10//
            , R.drawable.f_ic_gourmet_02_food_11//
            , R.drawable.f_ic_hotel_04_facilities_02//
            , R.drawable.f_ic_gourmet_02_food_13//
            , R.drawable.f_ic_gourmet_02_food_14};

        if (index < 1 || index >= resourceIndex.length)
        {
            index = 0;
        }

        return resourceIndex[index];
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_CURATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        unLockUI();

        switch (requestCode)
        {
            case Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER:
            {
                if (resultCode == RESULT_OK)
                {
                    searchMyLocation();
                } else
                {
                    GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mGourmetCuration.getCurationOption();

                    SortType sortType;

                    if (gourmetCurationOption == null)
                    {
                        sortType = SortType.DEFAULT;
                    } else
                    {
                        sortType = gourmetCurationOption.getSortType();
                    }

                    switch (sortType)
                    {
                        case DEFAULT:
                            mSortRadioGroup.check(R.id.regionCheckView);
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

                        // 거리 소트을 요청하였으나 동의를 하지 않는 경우 다시 거리 소트로 돌아오는 경우 종료시킨다.
                        case DISTANCE:
                            finish();
                            break;
                    }
                }
                break;
            }

            case Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION:
            {
                searchMyLocation();
                break;
            }
        }
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

        GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mGourmetCuration.getCurationOption();

        switch (checkedId)
        {
            case R.id.regionCheckView:
                gourmetCurationOption.setSortType(SortType.DEFAULT);
                label = AnalyticsManager.Label.SORTFILTER_DISTRICT;
                break;

            case R.id.distanceCheckView:
                searchMyLocation();
                return;

            case R.id.lowPriceCheckView:
                gourmetCurationOption.setSortType(SortType.LOW_PRICE);
                label = AnalyticsManager.Label.SORTFILTER_LOWTOHIGHPRICE;
                break;

            case R.id.highPriceCheckView:
                gourmetCurationOption.setSortType(SortType.HIGH_PRICE);
                label = AnalyticsManager.Label.SORTFILTER_HIGHTOLOWPRICE;
                break;

            case R.id.satisfactionCheckView:
                gourmetCurationOption.setSortType(SortType.SATISFACTION);
                label = AnalyticsManager.Label.SORTFILTER_RATING;
                break;

            default:
                return;
        }

        Province province = mGourmetCuration.getProvince();
        Map<String, String> eventParams = new HashMap<>();

        if (province != null)
        {
            if (province instanceof Area)
            {
                Area area = (Area) province;
                eventParams.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.KeyType.DOMESTIC);
                eventParams.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
                eventParams.put(AnalyticsManager.KeyType.DISTRICT, area.name);
            } else
            {
                eventParams.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.KeyType.DOMESTIC);
                eventParams.put(AnalyticsManager.KeyType.PROVINCE, province.name);
                eventParams.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.ALL_LOCALE_KR);
            }
        }

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_CLICKED, label, eventParams);
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);

        switch (v.getId())
        {
            case R.id.parkingCheckView:
                updateAmenitiesFilter(v, GourmetFilter.Amenities.FLAG_PARKING);
                break;

            case R.id.valetCheckView:
                updateAmenitiesFilter(v, GourmetFilter.Amenities.FLAG_VALET);
                break;

            case R.id.privateRoomCheckView:
                updateAmenitiesFilter(v, GourmetFilter.Amenities.FLAG_PRIVATEROOM);
                break;

            case R.id.groupBookingCheckView:
                updateAmenitiesFilter(v, GourmetFilter.Amenities.FLAG_GROUPBOOKING);
                break;

            case R.id.babySeatCheckView:
                updateAmenitiesFilter(v, GourmetFilter.Amenities.FLAG_BABYSEAT);
                break;

            case R.id.corkageCheckView:
                updateAmenitiesFilter(v, GourmetFilter.Amenities.FLAG_CORKAGE);
                break;

            case R.id.time0611View:
                updateTimeRangeFilter(v, GourmetFilter.Time.FLAG_06_11);
                break;

            case R.id.time1115View:
                updateTimeRangeFilter(v, GourmetFilter.Time.FLAG_11_15);
                break;

            case R.id.time1517View:
                updateTimeRangeFilter(v, GourmetFilter.Time.FLAG_15_17);
                break;

            case R.id.time1721View:
                updateTimeRangeFilter(v, GourmetFilter.Time.FLAG_17_21);
                break;

            case R.id.time2106View:
                updateTimeRangeFilter(v, GourmetFilter.Time.FLAG_21_06);
                break;
        }
    }

    @Override
    protected void onComplete()
    {
        GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mGourmetCuration.getCurationOption();

        Intent intent = new Intent();
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACECURATION, mGourmetCuration);

        setResult(RESULT_OK, intent);
        hideAnimation();

        Province province = mGourmetCuration.getProvince();

        Map<String, String> eventParams = new HashMap<>();
        eventParams.put(AnalyticsManager.KeyType.SORTING, gourmetCurationOption.getSortType().name());

        if (province != null)
        {
            if (province instanceof Area)
            {
                Area area = (Area) province;
                eventParams.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.KeyType.DOMESTIC);
                eventParams.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
                eventParams.put(AnalyticsManager.KeyType.DISTRICT, area.name);
            } else
            {
                eventParams.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.KeyType.DOMESTIC);
                eventParams.put(AnalyticsManager.KeyType.PROVINCE, province.name);
                eventParams.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.ALL_LOCALE_KR);
            }
        }

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.GOURMET_SORT_FILTER_APPLY_BUTTON_CLICKED, gourmetCurationOption.toString(), eventParams);

        if (Constants.DEBUG == true)
        {
            ExLog.d(gourmetCurationOption.toString());
        }
    }

    @Override
    protected void onCancel()
    {
        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_CLICKED, AnalyticsManager.Label.CLOSE_BUTTON_CLICKED, null);

        hideAnimation();
    }

    @Override
    protected void onReset()
    {
        resetCuration();

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_CLICKED, AnalyticsManager.Label.RESET_BUTTON_CLICKED, null);
    }

    @Override
    protected void onSearchLoacationResult(Location location)
    {
        mGourmetCuration.setLocation(location);

        if (location == null)
        {
            DailyToast.showToast(GourmetCurationActivity.this, R.string.message_failed_mylocation, Toast.LENGTH_SHORT);
            mSortRadioGroup.check(R.id.regionCheckView);
        } else
        {
            checkedChangedDistance();
        }
    }

    @Override
    protected BaseNetworkController getNetworkController(Context context)
    {
        return new GourmetCurationNetworkController(context, mNetworkTag, mNetworkControllerListener);
    }

    @Override
    protected PlaceCuration getPlaceCuration()
    {
        return mGourmetCuration;
    }

    protected void checkedChangedDistance()
    {
        GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mGourmetCuration.getCurationOption();
        gourmetCurationOption.setSortType(SortType.DISTANCE);

        Province province = mGourmetCuration.getProvince();
        Map<String, String> eventParams = new HashMap<>();
        String label = AnalyticsManager.Label.SORTFILTER_DISTANCE;

        if (province != null)
        {
            if (province instanceof Area)
            {
                Area area = (Area) province;
                eventParams.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.KeyType.DOMESTIC);
                eventParams.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
                eventParams.put(AnalyticsManager.KeyType.DISTRICT, area.name);
            } else
            {
                eventParams.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.KeyType.DOMESTIC);
                eventParams.put(AnalyticsManager.KeyType.PROVINCE, province.name);
                eventParams.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.ALL_LOCALE_KR);
            }
        }

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
            , AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_CLICKED, label, eventParams);
    }


    private View.OnClickListener mOnCategoryClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mGourmetCuration.getCurationOption();

            HashMap<String, Integer> filterMap = gourmetCurationOption.getFilterMap();
            DailyTextView dailyTextView = (DailyTextView) v;
            String key = dailyTextView.getText().toString();

            if (dailyTextView.isSelected() == true)
            {
                dailyTextView.setSelected(false);
                filterMap.remove(key);

                AnalyticsManager.getInstance(GourmetCurationActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                    , AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_UNCLICKED, key, null);
            } else
            {
                dailyTextView.setSelected(true);
                filterMap.put(key, gourmetCurationOption.getCategoryCoderMap().get(key));

                AnalyticsManager.getInstance(GourmetCurationActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                    , AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_CLICKED, key, null);
            }

            requestUpdateResultDelayed();
        }
    };


    private GourmetCurationNetworkController.OnNetworkControllerListener mNetworkControllerListener = new GourmetCurationNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onGourmetCount(String url, int totalCount)
        {
            if (Util.isTextEmpty(url) == true && totalCount == -1)
            {
                // OnNetworkControllerListener onErrorResponse
                setResultMessage(getString(R.string.label_gourmet_filter_result_empty));

                setConfirmOnClickListener(GourmetCurationActivity.this);
                setConfirmEnable(false);
                return;
            }

            String requestParams = null;
            try
            {
                Uri requestUrl = Uri.parse(url);
                requestParams = requestUrl.getQuery();
            } catch (Exception e)
            {
                // do nothing!
            }

            String lastParams = mLastParams.toParamsString();
            if (lastParams.equalsIgnoreCase(requestParams) == false)
            {
                // already running another request!
                return;
            }

            if (totalCount <= 0)
            {
                setResultMessage(getString(R.string.label_gourmet_filter_result_empty));
            } else
            {
                setResultMessage(getString(R.string.label_gourmet_filter_result_count, totalCount));
            }

            setConfirmOnClickListener(GourmetCurationActivity.this);
            setConfirmEnable(totalCount != 0);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            GourmetCurationActivity.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            GourmetCurationActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            GourmetCurationActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            GourmetCurationActivity.this.onErrorToastMessage(message);
        }
    };
}