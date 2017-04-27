package com.daily.dailyhotel.screen.stay.outbound.list;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.entity.Suggest;
import com.daily.dailyhotel.parcel.SuggestParcel;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.StayOutboundRemoteImpl;
import com.daily.dailyhotel.screen.stay.outbound.StayStayOutboundAnalyticsImpl;
import com.twoheart.dailyhotel.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundListPresenter extends BaseExceptionPresenter<StayOutboundListActivity, StayOutboundListViewInterface> implements StayOutboundListView.OnEventListener
{
    private StayOutboundListAnalyticsInterface mAnalytics;
    private StayOutboundRemoteImpl mStayOutboundRemoteImpl;
    private CommonRemoteImpl mCommonRemoteImpl;

    private CommonDateTime mCommonDateTime;
    private StayBookDateTime mStayBookDateTime;

    private Suggest mSuggest;
    private int mNumberOfAdults;
    private ArrayList<String> mChildList;

    // 리스트 요청시에 다음이 있는지에 대한 인자들
    private String mCacheKey, mCacheLocation;
    private boolean mMoreResultsAvailable;

    public interface StayOutboundListAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public StayOutboundListPresenter(@NonNull StayOutboundListActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayOutboundListViewInterface createInstanceViewInterface()
    {
        return new StayOutboundListView(getActivity(), this);
    }

    @Override
    public void initialize(StayOutboundListActivity activity)
    {
        setContentView(R.layout.activity_outbound_data);

        setAnalytics(new StayStayOutboundAnalyticsImpl());

        mStayOutboundRemoteImpl = new StayOutboundRemoteImpl(activity);
        mCommonRemoteImpl = new CommonRemoteImpl(activity);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayOutboundListAnalyticsInterface) analytics;
    }

    @Override
    public void finish()
    {
        onBackPressed();
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return false;
        }

        if (intent.hasExtra(BaseActivity.INTENT_EXTRA_DATA_DEEPLINK) == true)
        {
        } else if (intent.hasExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_SUGGEST) == true)
        {
            SuggestParcel suggestParcel = intent.getParcelableExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_SUGGEST);

            if (suggestParcel != null)
            {
                mSuggest = suggestParcel.getSuggest();
            }

            String checkInDateTime = intent.getParcelableExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHECKIN);
            String checkOutDateTime = intent.getParcelableExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHECKOUT);

            try
            {
                mStayBookDateTime = new StayBookDateTime();
                mStayBookDateTime.setCheckInDateTime(checkInDateTime);
                mStayBookDateTime.setCheckOutDateTime(checkOutDateTime);
            } catch (Exception e)
            {
                ExLog.e(e.toString());
                return false;
            }

            mNumberOfAdults = intent.getIntExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, 2);
            mChildList = intent.getStringArrayListExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHILD_LIST);

        } else if (intent.hasExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_KEYWORD) == true)
        {
            String keyword = intent.getStringExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_KEYWORD);

            mSuggest = new Suggest();
            mSuggest.city = keyword;

            String checkInDateTime = intent.getParcelableExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHECKIN);
            String checkOutDateTime = intent.getParcelableExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHECKOUT);

            try
            {
                mStayBookDateTime = new StayBookDateTime();
                mStayBookDateTime.setCheckInDateTime(checkInDateTime);
                mStayBookDateTime.setCheckOutDateTime(checkOutDateTime);
            } catch (Exception e)
            {
                ExLog.e(e.toString());
                return false;
            }

            mNumberOfAdults = intent.getIntExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, 2);
            mChildList = intent.getStringArrayListExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHILD_LIST);
        } else
        {
            return false;
        }

        return true;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            screenLock(true);

            //            addCompositeDisposable(mCommonRemoteImpl.getCommonDateTime()//
            //                .subscribe(commonDateTime ->
            //                {
            //                    setRefresh(false);
            //                    onCommonDateTime(commonDateTime);
            //
            //                    screenUnLock();
            //                }, throwable -> onHandleError(throwable)));


            Observable<List<StayOutbound>> observable;

            if(DailyTextUtils.isTextEmpty(mSuggest.id) == true)
            {
                // 키워드 검색인 경우
                observable = mStayOutboundRemoteImpl.getStayOutBoundList(mStayBookDateTime, null, mSuggest.city, mNumberOfAdults, mChildList);
            } else
            {
                // Suggest 검색인 경우
                observable = mStayOutboundRemoteImpl.getStayOutBoundList(mStayBookDateTime, mSuggest.countryCode, mSuggest.city, mNumberOfAdults, mChildList);
            }

            Observable.zip(mCommonRemoteImpl.getCommonDateTime(), observable//
                , new BiFunction<CommonDateTime, List<StayOutbound>, Object>()
                {
                    @Override
                    public Object apply(CommonDateTime commonDateTime, List<StayOutbound> stayOutBounds) throws Exception
                    {
                        onCommonDateTime(commonDateTime);
                        onStayOutboundList(stayOutBounds);

                        return null;
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>()
            {
                @Override
                public void accept(Object o) throws Exception
                {

                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    // 리스트를 호출하다가 에러가 난 경우 처리 방안
                    onHandleError(throwable);
                }
            });
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        // 꼭 호출해 주세요.
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed()
    {
        return super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

    }

    private void onCommonDateTime(@NonNull CommonDateTime commonDateTime)
    {
        if (commonDateTime == null)
        {
            return;
        }

        mCommonDateTime = commonDateTime;
    }

    private void onStayOutboundList(List<StayOutbound> stayOutBounds)
    {
        if (stayOutBounds == null)
        {
            return;
        }

    }
}
