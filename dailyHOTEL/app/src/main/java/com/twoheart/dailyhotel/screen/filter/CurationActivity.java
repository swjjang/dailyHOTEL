package com.twoheart.dailyhotel.screen.filter;

import android.content.Context;
import android.content.Intent;
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
import com.twoheart.dailyhotel.model.GourmetCurationOption;
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

    private static final int HANDLE_MESSAGE_HOTEL_RESULT = 1;
    private static final int HANDLE_MESSAGE_GOURMET_RESULT = 2;
    private static final int HANDLE_MESSAGE_DELAYTIME = 750;

    private PlaceCurationOption mPlaceCurationOption;

    private TYPE mType;

    private TextView mResultCountView;
    private View mConfirmView;
    //
    private View mMinusPersonView;
    private View mPlusPersonView;
    private TextView mPersonCountView;

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

    public static Intent newInstance(Context context, boolean isGlobal, HotelCurationOption hotelCurationOption)
    {
        Intent intent = new Intent(context, CurationActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_REGION, isGlobal);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE, TYPE.HOTEL.name());
        intent.putExtra(INTENT_EXTRA_DATA_CURATION_OPTIONS, hotelCurationOption);

        return intent;
    }

    public static Intent newInstance(Context context, boolean isGlobal, GourmetCurationOption gourmetCurationOption)
    {
        Intent intent = new Intent(context, CurationActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_REGION, isGlobal);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE, TYPE.FNB.name());
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

        boolean isGlobal = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_REGION, false);

        // 호텔 인지 고메인지
        mType = TYPE.valueOf(intent.getStringExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE));
        mPlaceCurationOption = intent.getParcelableExtra(INTENT_EXTRA_DATA_CURATION_OPTIONS);

        initLayout(mType, isGlobal);
    }

    private void initLayout(TYPE type, boolean isGlobal)
    {
        setContentView(R.layout.activity_curation);

        mResultCountView = (TextView) findViewById(R.id.resultCountView);

        mConfirmView = findViewById(R.id.confirmView);
        mConfirmView.setOnClickListener(this);

        View exitView = findViewById(R.id.exitView);
        exitView.setOnClickListener(this);

        View resetCurationView = findViewById(R.id.resetCurationView);
        resetCurationView.setOnClickListener(this);

        ViewGroup contentLayout = (ViewGroup) findViewById(R.id.contentLayout);

        switch (type)
        {
            case HOTEL:
                initHotel(isGlobal, contentLayout, (HotelCurationOption) mPlaceCurationOption);
                break;

            case FNB:
                initGourmet(isGlobal, contentLayout, (GourmetCurationOption) mPlaceCurationOption);
                break;
        }
    }

    private void initHotel(boolean isGloabl, ViewGroup contentLayout, HotelCurationOption hotelCurationOption)
    {
        View sortLayout = LayoutInflater.from(this).inflate(R.layout.layout_hotel_sort, null);
        initHotelSort(sortLayout, hotelCurationOption);

        contentLayout.addView(sortLayout);

        if (isGloabl == false)
        {
            View filterLayout = LayoutInflater.from(this).inflate(R.layout.layout_hotel_filter, null);
            initHotelFilter(filterLayout, hotelCurationOption);

            contentLayout.addView(filterLayout);
        }
    }

    private void initHotelSort(View view, HotelCurationOption hotelCurationOption)
    {
        RadioGroup sortLayout = (RadioGroup) view.findViewById(R.id.sortLayout);

        switch (hotelCurationOption.getSortType())
        {
            case DEFAULT:
                sortLayout.check(R.id.regionCheckView);
                break;

            case DISTANCE:
                sortLayout.check(R.id.distanceCheckView);
                break;

            case LOW_PRICE:
                sortLayout.check(R.id.lowPriceCheckView);
                break;

            case HIGH_PRICE:
                sortLayout.check(R.id.highPriceCheckView);
                break;

            case SATISFACTION:
                sortLayout.check(R.id.satisfactionCheckView);
                break;
        }

        sortLayout.setOnCheckedChangeListener(this);
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
        View doubleCheckView = view.findViewById(R.id.doubleCheckView);
        View twinCheckView = view.findViewById(R.id.twinCheckView);
        View heatedFloorsCheckView = view.findViewById(R.id.heatedFloorsCheckView);

        doubleCheckView.setOnClickListener(this);
        twinCheckView.setOnClickListener(this);
        heatedFloorsCheckView.setOnClickListener(this);

        if ((hotelCurationOption.flagFilters & HotelFilters.FLAG_HOTEL_FILTER_BED_DOUBLE) == HotelFilters.FLAG_HOTEL_FILTER_BED_DOUBLE)
        {
            updateHotelBedTypeFilter(doubleCheckView, HotelFilters.FLAG_HOTEL_FILTER_BED_DOUBLE);
        }

        if ((hotelCurationOption.flagFilters & HotelFilters.FLAG_HOTEL_FILTER_BED_TWIN) == HotelFilters.FLAG_HOTEL_FILTER_BED_TWIN)
        {
            updateHotelBedTypeFilter(twinCheckView, HotelFilters.FLAG_HOTEL_FILTER_BED_TWIN);
        }

        if ((hotelCurationOption.flagFilters & HotelFilters.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS) == HotelFilters.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS)
        {
            updateHotelBedTypeFilter(heatedFloorsCheckView, HotelFilters.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS);
        }
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
            hotelCurationOption.flagFilters ^= flag;
        } else
        {
            view.setSelected(true);
            hotelCurationOption.flagFilters |= flag;
        }

        mHandler.removeMessages(HANDLE_MESSAGE_HOTEL_RESULT);
        mHandler.sendEmptyMessageDelayed(HANDLE_MESSAGE_HOTEL_RESULT, HANDLE_MESSAGE_DELAYTIME);
    }

    private void updateHotelResultCount()
    {
        mConfirmView.setEnabled(false);

        new AsyncTask<Void, Void, Integer>()
        {
            @Override
            protected Integer doInBackground(Void... params)
            {
                int count = 0;
                HotelCurationOption hotelCurationOption = (HotelCurationOption) mPlaceCurationOption;

                ArrayList<HotelFilters> hotelFilterList = hotelCurationOption.getFilterList();

                for (HotelFilters hotelFilters : hotelFilterList)
                {
                    if (hotelFilters.isFiltered(hotelCurationOption.flagFilters, hotelCurationOption.person) == true)
                    {
                        count++;
                    }
                }

                return count;
            }

            @Override
            protected void onPostExecute(Integer count)
            {
                mResultCountView.setText(getString(R.string.label_hotel_filter_result_count, count));
                mConfirmView.setEnabled(true);
            }
        }.execute();
    }

    private void initGourmet(boolean isGloabl, ViewGroup contentLayout, GourmetCurationOption gourmetCurationOption)
    {
        View sortLayout = LayoutInflater.from(this).inflate(R.layout.layout_gourmet_sort, null);
        initGourmetSort(sortLayout, gourmetCurationOption);

        contentLayout.addView(sortLayout);

        View filterLayout = LayoutInflater.from(this).inflate(R.layout.layout_gourmet_filter, null);
        initGourmetFilter(filterLayout, gourmetCurationOption);

        contentLayout.addView(filterLayout);

        mHandler.removeMessages(HANDLE_MESSAGE_GOURMET_RESULT);
        mHandler.sendEmptyMessage(HANDLE_MESSAGE_GOURMET_RESULT);
    }

    private void initGourmetSort(View view, GourmetCurationOption gourmetCurationOption)
    {
        RadioGroup sortLayout = (RadioGroup) view.findViewById(R.id.sortLayout);

        switch (gourmetCurationOption.getSortType())
        {
            case DEFAULT:
                sortLayout.check(R.id.regionCheckView);
                break;

            case DISTANCE:
                sortLayout.check(R.id.distanceCheckView);
                break;

            case LOW_PRICE:
                sortLayout.check(R.id.lowPriceCheckView);
                break;

            case HIGH_PRICE:
                sortLayout.check(R.id.highPriceCheckView);
                break;

            case SATISFACTION:
                sortLayout.check(R.id.satisfactionCheckView);
                break;
        }

        sortLayout.setOnCheckedChangeListener(this);
    }

    private void initGourmetFilter(View view, GourmetCurationOption gourmetCurationOption)
    {
        android.support.v7.widget.GridLayout foodGridLayout = (android.support.v7.widget.GridLayout) view.findViewById(R.id.foodGridLayout);

        HashMap<String, Integer> categroyIconMap = gourmetCurationOption.getCategoryIconrMap();
        TreeMap<String, Integer> categoryMap = new TreeMap<String, Integer>(new Comparator<String>()
        {
            @Override
            public int compare(String o1, String o2)
            {
                return o2.compareTo(o1);
            }
        });

        categoryMap.putAll(gourmetCurationOption.getCategoryMap());

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mPlaceCurationOption;

                HashMap<String, Integer> filterMap = gourmetCurationOption.getFilterMap();
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

        for (String key : keyList)
        {
            if (Util.isTextEmpty(key) == true || "기타".equalsIgnoreCase(key) == true)
            {
                continue;
            }

            DailyTextView categoryView = getCategoryView(key, getCategoryResourceId(categroyIconMap.get(key).intValue()));
            categoryView.setOnClickListener(onClickListener);

            foodGridLayout.addView(categoryView);
        }
    }

    private void updateGourmetResultCount()
    {
        mConfirmView.setEnabled(false);

        new AsyncTask<Void, Void, Integer>()
        {
            @Override
            protected Integer doInBackground(Void... params)
            {
                int count = 0;
                GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mPlaceCurationOption;

                HashMap<String, Integer> categoryMap = gourmetCurationOption.getCategoryMap();
                HashMap<String, Integer> filterMap = gourmetCurationOption.getFilterMap();

                if (filterMap.size() == 0)
                {
                    ArrayList<Integer> categoryValueList = new ArrayList<Integer>(categoryMap.values());

                    for (Integer value : categoryValueList)
                    {
                        count += value.intValue();
                    }
                } else
                {
                    ArrayList<String> filterList = new ArrayList<String>(filterMap.keySet());

                    for (String key : filterList)
                    {
                        if (categoryMap.containsKey(key) == true)
                        {
                            count += categoryMap.get(key).intValue();
                        }
                    }
                }

                return count;
            }

            @Override
            protected void onPostExecute(Integer count)
            {
                mResultCountView.setText(getString(R.string.label_gourmet_filter_result_count, count));
                mConfirmView.setEnabled(true);
            }
        }.execute();
    }

    private DailyTextView getCategoryView(String text, int resId)
    {
        DailyTextView categoryView = new DailyTextView(this);
        categoryView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        categoryView.setGravity(Gravity.CENTER);
        categoryView.setTypeface(categoryView.getTypeface(), Typeface.NORMAL);
        categoryView.setTextColor(getResources().getColorStateList(R.drawable.selector_curation_textcolor));
        categoryView.setText(text);
        categoryView.setCompoundDrawablesWithIntrinsicBounds(0, resId, 0, 0);

        android.support.v7.widget.GridLayout.LayoutParams layoutParams = new android.support.v7.widget.GridLayout.LayoutParams();
        layoutParams.width = 0;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

        categoryView.setLayoutParams(layoutParams);

        return categoryView;
    }

    private int getCategoryResourceId(int index)
    {
        return R.drawable.navibar_ic_sorting_01;
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
                updateHotelBedTypeFilter(v, HotelFilters.FLAG_HOTEL_FILTER_BED_DOUBLE);
                break;

            case R.id.twinCheckView:
                updateHotelBedTypeFilter(v, HotelFilters.FLAG_HOTEL_FILTER_BED_TWIN);
                break;

            case R.id.heatedFloorsCheckView:
                updateHotelBedTypeFilter(v, HotelFilters.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS);
                break;

            case R.id.confirmView:
            {
                Intent intent = new Intent();

                switch (mType)
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

            case R.id.exitView:
                finish();
                break;

            case R.id.resetCurationView:
                mPlaceCurationOption.clear();
                break;
        }
    }
}