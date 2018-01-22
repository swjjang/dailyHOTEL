package com.daily.dailyhotel.screen.home.search;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.google.android.gms.maps.model.LatLng;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.FragmentSearchStayDataBinding;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.screen.search.stay.result.StaySearchResultActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;

public class SearchStayFragment extends Fragment implements View.OnClickListener
{
    FragmentSearchStayDataBinding mViewDataBinding;

    SearchPresenter.SearchModel mSearchModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_stay_data, container, false);


        mViewDataBinding.recently01Layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    StayBookDateTime stayBookDateTime = new StayBookDateTime();
                    stayBookDateTime.setCheckInDateTime("2018-02-08T09:00:00+09:00");
                    stayBookDateTime.setCheckOutDateTime("2018-02-10T09:00:00+09:00");

                    startStaySearchResult(mSearchModel.commonDateTime.getValue(), stayBookDateTime, "롯데");
                } catch (Exception e)
                {

                }
            }
        });

        mViewDataBinding.recently02Layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    StayBookDateTime stayBookDateTime = new StayBookDateTime();
                    stayBookDateTime.setCheckInDateTime("2018-01-29T09:00:00+09:00");
                    stayBookDateTime.setCheckOutDateTime("2018-01-31T09:00:00+09:00");

                    startStaySearchResult(mSearchModel.commonDateTime.getValue(), stayBookDateTime, "역삼동 (위치서비스 사용)");
                } catch (Exception e)
                {

                }
            }
        });

        mViewDataBinding.recently03Layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    StayBookDateTime stayBookDateTime = new StayBookDateTime();
                    stayBookDateTime.setCheckInDateTime("2018-01-24T09:00:00+09:00");
                    stayBookDateTime.setCheckOutDateTime("2018-01-26T09:00:00+09:00");

                    startStaySearchResult(mSearchModel.commonDateTime.getValue(), stayBookDateTime, "쉐라톤 디큐브시티 호텔");
                } catch (Exception e)
                {

                }
            }
        });

        mViewDataBinding.recentlyClose01ImageView.setOnClickListener(v -> mViewDataBinding.recently01Layout.setVisibility(View.GONE));
        mViewDataBinding.recentlyClose02ImageView.setOnClickListener(v -> mViewDataBinding.recently02Layout.setVisibility(View.GONE));
        mViewDataBinding.recentlyClose03ImageView.setOnClickListener(v -> mViewDataBinding.recently03Layout.setVisibility(View.GONE));

        mViewDataBinding.campaignTag01TextView.setOnClickListener(this);
        mViewDataBinding.campaignTag02TextView.setOnClickListener(this);
        mViewDataBinding.campaignTag03TextView.setOnClickListener(this);
        mViewDataBinding.campaignTag04TextView.setOnClickListener(this);
        mViewDataBinding.campaignTag05TextView.setOnClickListener(this);
        mViewDataBinding.campaignTag06TextView.setOnClickListener(this);
        mViewDataBinding.campaignTag07TextView.setOnClickListener(this);

        return mViewDataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mSearchModel = ViewModelProviders.of(getActivity()).get(SearchPresenter.SearchModel.class);
    }

    private void startStaySearchResult(CommonDateTime commonDateTime, StayBookDateTime stayBookDateTime, String searchWord)
    {
        if (commonDateTime == null || stayBookDateTime == null || DailyTextUtils.isTextEmpty(searchWord) == true)
        {
            return;
        }

        try
        {
            TodayDateTime todayDateTime = new TodayDateTime();
            todayDateTime.openDateTime = commonDateTime.openDateTime;
            todayDateTime.closeDateTime = commonDateTime.closeDateTime;
            todayDateTime.currentDateTime = commonDateTime.currentDateTime;
            todayDateTime.dailyDateTime = commonDateTime.dailyDateTime;

            StayBookingDay stayBookingDay = new StayBookingDay();
            stayBookingDay.setCheckInDay(stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT));
            stayBookingDay.setCheckOutDay(stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));

            switch (searchWord)
            {
                case "역삼동 (위치서비스 사용)":
                    startActivityForResult(StaySearchResultActivity.newInstance(getActivity()//
                        , todayDateTime, stayBookingDay, new LatLng(37.498337, 127.034512), 10.0f, false)//
                        , SearchActivity.REQUEST_CODE_STAY_SEARCH_RESULT);
                    break;

                default:
                    startActivity(StaySearchResultActivity.newInstance(getContext(), todayDateTime, stayBookingDay, searchWord));
                    break;
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.campaignTag01TextView:
            {
                if (v instanceof DailyTextView)
                {
                    String tag = ((DailyTextView) v).getText().toString();

                    mSearchModel.staySuggest.setValue(tag);
                    //                    tag = tag.substring(1); // #제거
                    //
                    //                    int index = 134;
                    //
                    //                    startActivity(StayCampaignTagListActivity.newInstance(getActivity() //
                    //                        , index, tag, mSearchModel.stayBookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT) //
                    //                        , mSearchModel.stayBookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)));
                }
                break;
            }

            case R.id.campaignTag02TextView:
            {
                if (v instanceof DailyTextView)
                {
                    String tag = ((DailyTextView) v).getText().toString();

                    mSearchModel.staySuggest.setValue(tag);

                    //                    tag = tag.substring(1); // #제거
                    //
                    //                    int index = 135;
                    //
                    //                    startActivity(StayCampaignTagListActivity.newInstance(getActivity() //
                    //                        , index, tag, mSearchModel.stayBookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT) //
                    //                        , mSearchModel.stayBookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)));
                }
                break;
            }

            case R.id.campaignTag03TextView:
            {
                if (v instanceof DailyTextView)
                {
                    String tag = ((DailyTextView) v).getText().toString();

                    mSearchModel.staySuggest.setValue(tag);

                    //                    tag = tag.substring(1); // #제거
                    //
                    //                    int index = 141;
                    //
                    //                    startActivity(StayCampaignTagListActivity.newInstance(getActivity() //
                    //                        , index, tag, mSearchModel.stayBookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT) //
                    //                        , mSearchModel.stayBookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)));
                }
                break;
            }

            case R.id.campaignTag04TextView:
            {
                if (v instanceof DailyTextView)
                {
                    String tag = ((DailyTextView) v).getText().toString();

                    mSearchModel.staySuggest.setValue(tag);

                    //                    tag = tag.substring(1); // #제거
                    //
                    //                    int index = 136;
                    //
                    //                    startActivity(StayCampaignTagListActivity.newInstance(getActivity() //
                    //                        , index, tag, mSearchModel.stayBookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT) //
                    //                        , mSearchModel.stayBookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)));
                }
                break;
            }

            case R.id.campaignTag05TextView:
            {
                if (v instanceof DailyTextView)
                {
                    String tag = ((DailyTextView) v).getText().toString();

                    mSearchModel.staySuggest.setValue(tag);

                    //
                    //                    tag = tag.substring(1); // #제거
                    //
                    //                    int index = 137;
                    //
                    //                    startActivity(StayCampaignTagListActivity.newInstance(getActivity() //
                    //                        , index, tag, mSearchModel.stayBookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT) //
                    //                        , mSearchModel.stayBookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)));
                }
                break;
            }

            case R.id.campaignTag06TextView:
            {
                if (v instanceof DailyTextView)
                {
                    String tag = ((DailyTextView) v).getText().toString();

                    mSearchModel.staySuggest.setValue(tag);

                    //
                    //                    tag = tag.substring(1); // #제거
                    //
                    //                    int index = 138;
                    //
                    //                    startActivity(StayCampaignTagListActivity.newInstance(getActivity() //
                    //                        , index, tag, mSearchModel.stayBookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT) //
                    //                        , mSearchModel.stayBookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)));
                }
                break;
            }

            case R.id.campaignTag07TextView:
            {
                if (v instanceof DailyTextView)
                {
                    String tag = ((DailyTextView) v).getText().toString();

                    mSearchModel.staySuggest.setValue(tag);

                    //                    tag = tag.substring(1); // #제거
                    //
                    //                    int index = 104;
                    //
                    //                    startActivity(StayCampaignTagListActivity.newInstance(getActivity() //
                    //                        , index, tag, mSearchModel.stayBookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT) //
                    //                        , mSearchModel.stayBookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)));
                }
                break;
            }
        }
    }
}
