package com.twoheart.dailyhotel.screen.filter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.GourmetCurationOption;
import com.twoheart.dailyhotel.model.GourmetFilter;
import com.twoheart.dailyhotel.model.GourmetFilters;
import com.twoheart.dailyhotel.model.HotelCurationOption;
import com.twoheart.dailyhotel.model.HotelFilter;
import com.twoheart.dailyhotel.model.HotelFilters;
import com.twoheart.dailyhotel.model.PlaceCurationOption;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyTextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class CurationActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener
{
    public static final String INTENT_EXTRA_DATA_CURATION_OPTIONS = "curationOptions";
    public static final String INTENT_EXTRA_DATA_VIEWTYPE = "viewType";

    private static final int HANDLE_MESSAGE_HOTEL_RESULT = 1;
    private static final int HANDLE_MESSAGE_GOURMET_RESULT = 2;
    private static final int HANDLE_MESSAGE_DELAYTIME = 750;

    private static final int HOTEL_MAX_BEDTYPE = 3;
    private static final int GOURMET_CATEGORY_COLUMN = 5;

    private PlaceCurationOption mPlaceCurationOption;

    private PlaceType mPlaceType;

    private TextView mResultCountView;
    private View mConfirmView;

    private RadioGroup mSortRadioGroup;

    // hotel
    private View mMinusPersonView;
    private View mPlusPersonView;
    private TextView mPersonCountView;
    private ViewGroup mBedTypeLayout;

    // Gourmet
    private android.support.v7.widget.GridLayout mGridLayout;

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case HANDLE_MESSAGE_HOTEL_RESULT:
                    updateHotelResultCount();
                    break;

                case HANDLE_MESSAGE_GOURMET_RESULT:
                    updateGourmetResultCount();
                    break;
            }

        }
    };

    public static Intent newInstance(Context context, boolean isGlobal, ViewType viewType, HotelCurationOption hotelCurationOption)
    {
        Intent intent = new Intent(context, CurationActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_REGION, isGlobal);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE, PlaceType.HOTEL.name());
        intent.putExtra(INTENT_EXTRA_DATA_CURATION_OPTIONS, hotelCurationOption);
        intent.putExtra(INTENT_EXTRA_DATA_VIEWTYPE, viewType.name());

        return intent;
    }

    public static Intent newInstance(Context context, boolean isGlobal, ViewType viewType, GourmetCurationOption gourmetCurationOption)
    {
        Intent intent = new Intent(context, CurationActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_REGION, isGlobal);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE, PlaceType.FNB.name());
        intent.putExtra(INTENT_EXTRA_DATA_CURATION_OPTIONS, gourmetCurationOption);
        intent.putExtra(INTENT_EXTRA_DATA_VIEWTYPE, viewType.name());

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

        boolean isGlobal = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_REGION, false);
        ViewType viewType = ViewType.valueOf(intent.getStringExtra(INTENT_EXTRA_DATA_VIEWTYPE));
        mPlaceType = PlaceType.valueOf(intent.getStringExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE));
        mPlaceCurationOption = intent.getParcelableExtra(INTENT_EXTRA_DATA_CURATION_OPTIONS);

        initLayout(viewType, mPlaceType, isGlobal);
    }

    private void initLayout(ViewType viewType, PlaceType placeType, boolean isGlobal)
    {
        setContentView(R.layout.activity_curation);

        mResultCountView = (TextView) findViewById(R.id.resultCountView);

        mConfirmView = findViewById(R.id.confirmView);
        mConfirmView.setOnClickListener(this);

        final View contentScrollView = findViewById(R.id.contentScrollView);
        contentScrollView.post(new Runnable()
        {
            @Override
            public void run()
            {
                Rect rect = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                int scrollViewHeight = Util.getLCDHeight(CurationActivity.this) - Util.dpToPx(CurationActivity.this, 55)//
                    - Util.dpToPx(CurationActivity.this, 50) - Util.dpToPx(CurationActivity.this, 47) - Util.dpToPx(CurationActivity.this, 36) - rect.top;

                if (contentScrollView.getHeight() > scrollViewHeight)
                {
                    ViewGroup.LayoutParams layoutParams = contentScrollView.getLayoutParams();
                    layoutParams.height = scrollViewHeight;
                    contentScrollView.setLayoutParams(layoutParams);
                }
            }
        });

        View exitView = findViewById(R.id.exitView);
        exitView.setOnClickListener(this);

        View resetCurationView = findViewById(R.id.resetCurationView);
        resetCurationView.setOnClickListener(this);

        View closeView = findViewById(R.id.closeView);
        closeView.setOnClickListener(this);

        ViewGroup contentLayout = (ViewGroup) findViewById(R.id.contentLayout);

        switch (placeType)
        {
            case HOTEL:
                initHotel(isGlobal, viewType, contentLayout, (HotelCurationOption) mPlaceCurationOption);
                break;

            case FNB:
                initGourmet(isGlobal, viewType, contentLayout, (GourmetCurationOption) mPlaceCurationOption);
                break;
        }
    }

    private void initHotel(boolean isGloabl, ViewType viewType, ViewGroup contentLayout, HotelCurationOption hotelCurationOption)
    {
        View sortLayout = LayoutInflater.from(this).inflate(R.layout.layout_hotel_sort, null);
        initHotelSort(sortLayout, viewType, hotelCurationOption);

        contentLayout.addView(sortLayout);

        if (isGloabl == false)
        {
            View filterLayout = LayoutInflater.from(this).inflate(R.layout.layout_hotel_filter, null);
            initHotelFilter(filterLayout, hotelCurationOption);

            contentLayout.addView(filterLayout);

            initAmenities(filterLayout, hotelCurationOption);
        }
    }

    private void initHotelSort(View view, ViewType viewType, HotelCurationOption hotelCurationOption)
    {
        mSortRadioGroup = (RadioGroup) view.findViewById(R.id.sortLayout);

        if (viewType == ViewType.MAP)
        {
            setDisabledSortView(view, mSortRadioGroup);
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

    private void initHotelFilter(View view, HotelCurationOption hotelCurationOption)
    {
        // 인원
        mMinusPersonView = view.findViewById(R.id.minusPersonView);
        mPlusPersonView = view.findViewById(R.id.plusPersonView);
        mPersonCountView = (TextView) view.findViewById(R.id.personCountView);

        mMinusPersonView.setOnClickListener(this);
        mPlusPersonView.setOnClickListener(this);

        updateHotelPersonFilter(hotelCurationOption.person);

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
            updateHotelBedTypeFilter(doubleCheckView, HotelFilter.FLAG_HOTEL_FILTER_BED_DOUBLE);
        }

        if ((hotelCurationOption.flagBedTypeFilters & HotelFilter.FLAG_HOTEL_FILTER_BED_TWIN) == HotelFilter.FLAG_HOTEL_FILTER_BED_TWIN)
        {
            updateHotelBedTypeFilter(twinCheckView, HotelFilter.FLAG_HOTEL_FILTER_BED_TWIN);
        }

        if ((hotelCurationOption.flagBedTypeFilters & HotelFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS) == HotelFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS)
        {
            updateHotelBedTypeFilter(heatedFloorsCheckView, HotelFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS);
        }
    }

    private void initAmenities(View view, HotelCurationOption hotelCurationOption)
    {
        mGridLayout = (android.support.v7.widget.GridLayout) view.findViewById(R.id.amenitiesGridLayout);

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                HotelCurationOption curationOption = (HotelCurationOption) mPlaceCurationOption;

                Integer flag = (Integer) v.getTag();

                if (flag == null)
                {
                    v.setSelected(false);
                    return;
                }

                if (v.isSelected() == true)
                {
                    v.setSelected(false);
                    curationOption.flagAmenitiesFilters ^= flag.intValue();
                } else
                {
                    v.setSelected(true);
                    curationOption.flagAmenitiesFilters |= flag.intValue();
                }

                mHandler.removeMessages(HANDLE_MESSAGE_HOTEL_RESULT);
                mHandler.sendEmptyMessageDelayed(HANDLE_MESSAGE_HOTEL_RESULT, HANDLE_MESSAGE_DELAYTIME);
            }
        };

        final String[] amenities = new String[]{getString(R.string.label_wifi)//
            , getString(R.string.label_breakfast)//
            , getString(R.string.label_cooking)//
            , getString(R.string.label_beth)//
            , getString(R.string.label_parking)//
            , getString(R.string.label_pool)//
            , getString(R.string.label_fitness)};

        final int[] amenitiesResId = new int[]{R.drawable.f_ic_hotel_bed_01//
            , R.drawable.f_ic_hotel_bed_01//
            , R.drawable.f_ic_hotel_bed_01//
            , R.drawable.f_ic_hotel_bed_01//
            , R.drawable.f_ic_hotel_bed_01//
            , R.drawable.f_ic_hotel_bed_01//
            , R.drawable.f_ic_hotel_bed_01};

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

            if ((hotelCurationOption.flagAmenitiesFilters & amenitiesflag[i]) == amenitiesflag[i])
            {
                amenitiesView.setSelected(true);
            }

            mGridLayout.addView(amenitiesView);
        }

        mGridLayout.setPadding(0, 0, 0, Util.dpToPx(this, 10));
    }

    private void updateHotelPersonFilter(int person)
    {
        HotelCurationOption hotelCurationOption = (HotelCurationOption) mPlaceCurationOption;

        if (person < HotelFilter.MIN_PERSON)
        {
            person = HotelFilter.MIN_PERSON;
        } else if (person > HotelFilter.MAX_PERSON)
        {
            person = HotelFilter.MAX_PERSON;
        }

        hotelCurationOption.person = person;

        mPersonCountView.setText(getString(R.string.label_more_person, person));

        if (person == HotelFilter.MIN_PERSON)
        {
            mMinusPersonView.setEnabled(false);
            mPlusPersonView.setEnabled(true);
        } else if (person == HotelFilter.MAX_PERSON)
        {
            mMinusPersonView.setEnabled(true);
            mPlusPersonView.setEnabled(false);
        } else
        {
            mMinusPersonView.setEnabled(true);
            mPlusPersonView.setEnabled(true);
        }

        mHandler.removeMessages(HANDLE_MESSAGE_HOTEL_RESULT);
        mHandler.sendEmptyMessageDelayed(HANDLE_MESSAGE_HOTEL_RESULT, HANDLE_MESSAGE_DELAYTIME);
    }

    private void updateHotelBedTypeFilter(View view, int flag)
    {
        HotelCurationOption hotelCurationOption = (HotelCurationOption) mPlaceCurationOption;

        if (view.isSelected() == true)
        {
            view.setSelected(false);
            hotelCurationOption.flagBedTypeFilters ^= flag;
        } else
        {
            view.setSelected(true);
            hotelCurationOption.flagBedTypeFilters |= flag;
        }

        mHandler.removeMessages(HANDLE_MESSAGE_HOTEL_RESULT);
        mHandler.sendEmptyMessageDelayed(HANDLE_MESSAGE_HOTEL_RESULT, HANDLE_MESSAGE_DELAYTIME);
    }

    private void resetHotelCureation()
    {
        mPlaceCurationOption.clear();

        mSortRadioGroup.clearCheck();
        mSortRadioGroup.check(R.id.regionCheckView);

        updateHotelPersonFilter(HotelFilter.MIN_PERSON);

        int bedTypeCount = mBedTypeLayout.getChildCount();

        for (int i = 0; i < bedTypeCount; i++)
        {
            mBedTypeLayout.getChildAt(i).setSelected(false);
        }

        mHandler.removeMessages(HANDLE_MESSAGE_HOTEL_RESULT);
        mHandler.sendEmptyMessage(HANDLE_MESSAGE_HOTEL_RESULT);
    }

    private void updateHotelResultCount()
    {
        mConfirmView.setOnClickListener(null);

        new AsyncTask<Void, Void, Integer>()
        {
            @Override
            protected Integer doInBackground(Void... params)
            {
                int count = 0;
                HotelCurationOption hotelCurationOption = (HotelCurationOption) mPlaceCurationOption;

                ArrayList<HotelFilters> hotelFiltersList = hotelCurationOption.getFiltersList();

                if (Category.ALL.code.equalsIgnoreCase(hotelCurationOption.getCategory().code) == true)
                {
                    for (HotelFilters hotelFilters : hotelFiltersList)
                    {
                        if (hotelFilters.isFiltered(hotelCurationOption) == true)
                        {
                            count++;
                        }
                    }
                } else
                {
                    for (HotelFilters hotelFilters : hotelFiltersList)
                    {
                        if (hotelCurationOption.getCategory().code.equalsIgnoreCase(hotelFilters.categoryCode) == true//
                            && hotelFilters.isFiltered(hotelCurationOption) == true)
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
                mResultCountView.setText(getString(R.string.label_hotel_filter_result_count, count));

                mConfirmView.setOnClickListener(CurationActivity.this);
                mConfirmView.setEnabled(count.intValue() == 0 ? false : true);
            }
        }.execute();
    }

    private void initGourmet(boolean isGloabl, ViewType viewType, ViewGroup contentLayout, GourmetCurationOption gourmetCurationOption)
    {
        View sortLayout = LayoutInflater.from(this).inflate(R.layout.layout_gourmet_sort, null);
        initGourmetSort(sortLayout, viewType, gourmetCurationOption);

        contentLayout.addView(sortLayout);

        View filterLayout = LayoutInflater.from(this).inflate(R.layout.layout_gourmet_filter, null);
        initGourmetFilter(filterLayout, gourmetCurationOption);

        contentLayout.addView(filterLayout);

        mHandler.removeMessages(HANDLE_MESSAGE_GOURMET_RESULT);
        mHandler.sendEmptyMessage(HANDLE_MESSAGE_GOURMET_RESULT);
    }

    private void initGourmetSort(View view, ViewType viewType, GourmetCurationOption gourmetCurationOption)
    {
        mSortRadioGroup = (RadioGroup) view.findViewById(R.id.sortLayout);

        if (viewType == ViewType.MAP)
        {
            setDisabledSortView(view, mSortRadioGroup);
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

    private void initGourmetFilter(View view, GourmetCurationOption gourmetCurationOption)
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
                GourmetCurationOption curationOption = (GourmetCurationOption) mPlaceCurationOption;

                HashMap<String, Integer> filterMap = curationOption.getFilterMap();
                DailyTextView dailyTextView = (DailyTextView) v;
                String key = dailyTextView.getText().toString();

                if (dailyTextView.isSelected() == true)
                {
                    dailyTextView.setSelected(false);
                    filterMap.remove(key);
                } else
                {
                    dailyTextView.setSelected(true);
                    filterMap.put(key, 0);
                }

                mHandler.removeMessages(HANDLE_MESSAGE_GOURMET_RESULT);
                mHandler.sendEmptyMessageDelayed(HANDLE_MESSAGE_GOURMET_RESULT, HANDLE_MESSAGE_DELAYTIME);
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
            mGridLayout.setPadding(0, 0, 0, Util.dpToPx(this, 10));
        }

        initGourmetAmenities(view, gourmetCurationOption);
        initGourmetTimeRangeFilter(view, gourmetCurationOption);

        mHandler.removeMessages(HANDLE_MESSAGE_GOURMET_RESULT);
        mHandler.sendEmptyMessageDelayed(HANDLE_MESSAGE_GOURMET_RESULT, HANDLE_MESSAGE_DELAYTIME);
    }

    private void initGourmetAmenities(View view, GourmetCurationOption gourmetCurationOption)
    {
        View parkingCheckView = view.findViewById(R.id.parkingCheckView);

        parkingCheckView.setSelected(gourmetCurationOption.isParking);
        parkingCheckView.setOnClickListener(this);
    }

    private void initGourmetTimeRangeFilter(View view, GourmetCurationOption gourmetCurationOption)
    {
        View time0611View = view.findViewById(R.id.time0611View);
        View time1115View = view.findViewById(R.id.time1115View);
        View time1517View = view.findViewById(R.id.time1517View);
        View time1721View = view.findViewById(R.id.time1721View);
        View time2106View = view.findViewById(R.id.time2106View);

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

    private void updateGourmetAmenitiesTypeFilter(View view, GourmetCurationOption gourmetCurationOption)
    {
        if (view == null || gourmetCurationOption == null)
        {
            return;
        }

        if (view.isSelected() == true)
        {
            view.setSelected(false);
            gourmetCurationOption.isParking = false;
        } else
        {
            view.setSelected(true);
            gourmetCurationOption.isParking = true;
        }

        mHandler.removeMessages(HANDLE_MESSAGE_GOURMET_RESULT);
        mHandler.sendEmptyMessageDelayed(HANDLE_MESSAGE_GOURMET_RESULT, HANDLE_MESSAGE_DELAYTIME);
    }

    private void updateGourmetTimeRangeFilter(View view, int flag)
    {
        GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mPlaceCurationOption;

        if (view.isSelected() == true)
        {
            view.setSelected(false);
            gourmetCurationOption.flagTimeFilter ^= flag;
        } else
        {
            view.setSelected(true);
            gourmetCurationOption.flagTimeFilter |= flag;
        }

        mHandler.removeMessages(HANDLE_MESSAGE_GOURMET_RESULT);
        mHandler.sendEmptyMessageDelayed(HANDLE_MESSAGE_GOURMET_RESULT, HANDLE_MESSAGE_DELAYTIME);
    }

    private void resetGourmetCureation()
    {
        mPlaceCurationOption.clear();

        mSortRadioGroup.clearCheck();
        mSortRadioGroup.check(R.id.regionCheckView);

        int categoryCount = mGridLayout.getChildCount();

        for (int i = 0; i < categoryCount; i++)
        {
            mGridLayout.getChildAt(i).setSelected(false);
        }

        mHandler.removeMessages(HANDLE_MESSAGE_GOURMET_RESULT);
        mHandler.sendEmptyMessage(HANDLE_MESSAGE_GOURMET_RESULT);
    }

    private void updateGourmetResultCount()
    {
        mConfirmView.setOnClickListener(null);

        new AsyncTask<Void, Void, Integer>()
        {
            @Override
            protected Integer doInBackground(Void... params)
            {
                int count = 0;
                GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mPlaceCurationOption;
                HashMap<String, Integer> filterMap = gourmetCurationOption.getFilterMap();
                ArrayList<GourmetFilters> gourmetFiltersList = gourmetCurationOption.getFiltersList();

                if (filterMap == null || filterMap.size() == 0)
                {
                    for (GourmetFilters gourmetFilters : gourmetFiltersList)
                    {
                        if (gourmetFilters.isFiltered(gourmetCurationOption) == true)
                        {
                            count++;
                        }
                    }
                } else
                {
                    for (GourmetFilters gourmetFilters : gourmetFiltersList)
                    {
                        if (filterMap.containsKey(gourmetFilters.category) == true//
                            && gourmetFilters.isFiltered(gourmetCurationOption) == true)
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
                mResultCountView.setText(getString(R.string.label_gourmet_filter_result_count, count));

                mConfirmView.setOnClickListener(CurationActivity.this);
                mConfirmView.setEnabled(count.intValue() == 0 ? false : true);
            }
        }.execute();
    }

    private DailyTextView getGridLayoutItemView(String text, int resId, boolean isSingleLine)
    {
        DailyTextView categoryView = new DailyTextView(this);
        categoryView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        categoryView.setGravity(Gravity.CENTER_HORIZONTAL);
        categoryView.setTypeface(categoryView.getTypeface(), Typeface.NORMAL);
        categoryView.setTextColor(getResources().getColorStateList(R.drawable.selector_curation_textcolor));
        categoryView.setText(text);
        categoryView.setCompoundDrawablesWithIntrinsicBounds(0, resId, 0, 0);

        android.support.v7.widget.GridLayout.LayoutParams layoutParams = new android.support.v7.widget.GridLayout.LayoutParams();
        layoutParams.width = 0;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

        if (isSingleLine == true)
        {
            categoryView.setPadding(0, Util.dpToPx(this, 10), 0, Util.dpToPx(this, 15));
        } else
        {
            categoryView.setPadding(0, Util.dpToPx(this, 10), 0, Util.dpToPx(this, 2));
        }

        categoryView.setLayoutParams(layoutParams);

        return categoryView;
    }

    private int getCategoryResourceId(int index)
    {
        final int[] resourceIndex = new int[]{R.drawable.f_ic_hotel_bed_01//
            , R.drawable.f_ic_hotel_bed_01//
            , R.drawable.f_ic_hotel_bed_01//
            , R.drawable.f_ic_hotel_bed_01//
            , R.drawable.f_ic_hotel_bed_01//
            , R.drawable.f_ic_hotel_bed_01//
            , R.drawable.f_ic_hotel_bed_01//
            , R.drawable.f_ic_hotel_bed_01//
            , R.drawable.f_ic_hotel_bed_01};

        if (index < 1 || index >= resourceIndex.length)
        {
            index = 0;
        }

        return resourceIndex[index];
    }

    private void setDisabledSortView(View view, RadioGroup sortLayout)
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

    @Override
    protected void onResume()
    {
        super.onResume();

    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
    }

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_CANCELED);

        super.onBackPressed();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId)
    {
        switch (checkedId)
        {
            case R.id.regionCheckView:
                mPlaceCurationOption.setSortType(SortType.DEFAULT);
                break;

            case R.id.distanceCheckView:
                mPlaceCurationOption.setSortType(SortType.DISTANCE);
                break;

            case R.id.lowPriceCheckView:
                mPlaceCurationOption.setSortType(SortType.LOW_PRICE);
                break;

            case R.id.highPriceCheckView:
                mPlaceCurationOption.setSortType(SortType.HIGH_PRICE);
                break;

            case R.id.satisfactionCheckView:
                mPlaceCurationOption.setSortType(SortType.SATISFACTION);
                break;
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.minusPersonView:
                updateHotelPersonFilter(((HotelCurationOption) mPlaceCurationOption).person - 1);
                break;

            case R.id.plusPersonView:
                updateHotelPersonFilter(((HotelCurationOption) mPlaceCurationOption).person + 1);
                break;

            case R.id.doubleCheckView:
                updateHotelBedTypeFilter(v, HotelFilter.FLAG_HOTEL_FILTER_BED_DOUBLE);
                break;

            case R.id.twinCheckView:
                updateHotelBedTypeFilter(v, HotelFilter.FLAG_HOTEL_FILTER_BED_TWIN);
                break;

            case R.id.heatedFloorsCheckView:
                updateHotelBedTypeFilter(v, HotelFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS);
                break;

            case R.id.parkingCheckView:
                updateGourmetAmenitiesTypeFilter(v, (GourmetCurationOption) mPlaceCurationOption);
                break;

            case R.id.time0611View:
                updateGourmetTimeRangeFilter(v, GourmetFilter.FLAG_GOURMET_FILTER_TIME_06_11);
                break;

            case R.id.time1115View:
                updateGourmetTimeRangeFilter(v, GourmetFilter.FLAG_GOURMET_FILTER_TIME_11_15);
                break;

            case R.id.time1517View:
                updateGourmetTimeRangeFilter(v, GourmetFilter.FLAG_GOURMET_FILTER_TIME_15_17);
                break;

            case R.id.time1721View:
                updateGourmetTimeRangeFilter(v, GourmetFilter.FLAG_GOURMET_FILTER_TIME_17_21);
                break;

            case R.id.time2106View:
                updateGourmetTimeRangeFilter(v, GourmetFilter.FLAG_GOURMET_FILTER_TIME_21_06);
                break;

            case R.id.confirmView:
            {
                Intent intent = new Intent();

                switch (mPlaceType)
                {
                    case HOTEL:
                        intent.putExtra(INTENT_EXTRA_DATA_CURATION_OPTIONS, (HotelCurationOption) mPlaceCurationOption);
                        break;

                    case FNB:
                        intent.putExtra(INTENT_EXTRA_DATA_CURATION_OPTIONS, (GourmetCurationOption) mPlaceCurationOption);
                        break;
                }

                setResult(RESULT_OK, intent);
                finish();
                break;
            }

            case R.id.closeView:
            case R.id.exitView:
                finish();
                break;

            case R.id.resetCurationView:
                switch (mPlaceType)
                {
                    case HOTEL:
                        resetHotelCureation();
                        break;

                    case FNB:
                        resetGourmetCureation();
                        break;
                }
                break;
        }
    }
}