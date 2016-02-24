package com.twoheart.dailyhotel.screen.filter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.twoheart.dailyhotel.util.ExLog;

public class CurationActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener
{
    public static final String INTENT_EXTRA_DATA_CURATION_OPTIONS = "curationOptions";

    private static final int HANDLE_MESSAGE_HOTEL_RESULT = 1;
    private static final int HANDLE_MESSAGE_GOURMET_RESULT = 2;
    private static final int HANDLE_MESSAGE_DELAYTIME = 1000;

    private HotelCurationOption mHotelCurationOption;

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
            switch(msg.what)
            {
                case HANDLE_MESSAGE_HOTEL_RESULT:
                    updateHotelResultCount();
                    break;

                case HANDLE_MESSAGE_GOURMET_RESULT:
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
        TYPE type = TYPE.valueOf(intent.getStringExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE));

        switch(type)
        {
            case HOTEL:
                mHotelCurationOption = intent.getParcelableExtra(INTENT_EXTRA_DATA_CURATION_OPTIONS);
                break;

            case FNB:
                break;
        }

        initLayout(type, isGlobal);
    }

    private void initLayout(TYPE type, boolean isGlobl)
    {
        setContentView(R.layout.activity_curation);

        mResultCountView = (TextView)findViewById(R.id.resultCountView);

        mConfirmView = findViewById(R.id.confirmView);
        mConfirmView.setOnClickListener(this);

        ViewGroup contentLayout = (ViewGroup)findViewById(R.id.contentLayout);

        switch (type)
        {
            case HOTEL:
                initHotel(isGlobl, contentLayout, mHotelCurationOption);
                break;

            case FNB:
                initGourmet(contentLayout);
                break;
        }
    }

    private void initHotel(boolean isGloabl, ViewGroup contentLayout, HotelCurationOption hotelCurationOption)
    {
        View sortLayout = LayoutInflater.from(this).inflate(R.layout.layout_hotel_sort, null);
        initHotelSort(sortLayout, hotelCurationOption);

        contentLayout.addView(sortLayout);

        if(isGloabl == false)
        {
            View filterLayout = LayoutInflater.from(this).inflate(R.layout.layout_hotel_filter, null);
            initHotelFilter(filterLayout, hotelCurationOption);

            contentLayout.addView(filterLayout);
        }
    }

    private void initHotelSort(View view, HotelCurationOption hotelCurationOption)
    {
        RadioGroup sortLayout = (RadioGroup)view.findViewById(R.id.sortLayout);

        switch(hotelCurationOption.getSortType())
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
        mPersonCountView = (TextView)view.findViewById(R.id.personCountView);

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

        if((hotelCurationOption.flagFilters & HotelFilters.FLAG_HOTEL_FILTER_BED_DOUBLE) == HotelFilters.FLAG_HOTEL_FILTER_BED_DOUBLE)
        {
            updateHotelBedTypeFilter(doubleCheckView, HotelFilters.FLAG_HOTEL_FILTER_BED_DOUBLE);
        }

        if((hotelCurationOption.flagFilters & HotelFilters.FLAG_HOTEL_FILTER_BED_TWIN) == HotelFilters.FLAG_HOTEL_FILTER_BED_TWIN)
        {
            updateHotelBedTypeFilter(twinCheckView, HotelFilters.FLAG_HOTEL_FILTER_BED_TWIN);
        }

        if((hotelCurationOption.flagFilters & HotelFilters.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS) == HotelFilters.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS)
        {
            updateHotelBedTypeFilter(heatedFloorsCheckView, HotelFilters.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS);
        }
    }

    private void updateHotelPersonFilter(int person)
    {
        if(person < HotelFilter.MIN_PERSON)
        {
            person = HotelFilter.MIN_PERSON;
        } else if(person > HotelFilter.MAX_PERSON)
        {
            person = HotelFilter.MAX_PERSON;
        }

        mHotelCurationOption.person = person;

        mPersonCountView.setText(getString(R.string.label_more_person, person));

        if(person == HotelFilter.MIN_PERSON)
        {
            mMinusPersonView.setEnabled(false);
            mPlusPersonView.setEnabled(true);
        } else if(person == HotelFilter.MAX_PERSON)
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
        if(view.isSelected() == true)
        {
            view.setSelected(false);
            mHotelCurationOption.flagFilters ^= flag;
        } else
        {
            view.setSelected(true);
            mHotelCurationOption.flagFilters |= flag;
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
                return mHotelCurationOption.getFilterCount(mHotelCurationOption.flagFilters, mHotelCurationOption.person);
            }

            @Override
            protected void onPostExecute(Integer count)
            {
                ExLog.d("updateHotelResultCount : " + count);

                mResultCountView.setText(getString(R.string.label_hotel_filter_result_count, count));
                mConfirmView.setEnabled(true);
            }
        }.execute();
    }

    private void initGourmet(ViewGroup contentLayout)
    {

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
        switch(checkedId)
        {
            case R.id.regionCheckView:
                mHotelCurationOption.setSortType(SortType.DEFAULT);
                break;

            case R.id.distanceCheckView:
                mHotelCurationOption.setSortType(SortType.DISTANCE);
                break;

            case R.id.lowPriceCheckView:
                mHotelCurationOption.setSortType(SortType.LOW_PRICE);
                break;

            case R.id.highPriceCheckView:
                mHotelCurationOption.setSortType(SortType.HIGH_PRICE);
                break;

            case R.id.satisfactionCheckView:
                mHotelCurationOption.setSortType(SortType.SATISFACTION);
                break;
        }
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.minusPersonView:
                updateHotelPersonFilter(mHotelCurationOption.person - 1);
                break;

            case R.id.plusPersonView:
                updateHotelPersonFilter(mHotelCurationOption.person + 1);
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
                intent.putExtra(INTENT_EXTRA_DATA_CURATION_OPTIONS, mHotelCurationOption);
                setResult(RESULT_OK, intent);
                finish();
                break;
            }
        }
    }
}