package com.twoheart.dailyhotel.screen.home.collection;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Transition;
import android.transition.TransitionSet;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.model.Recommendation;
import com.twoheart.dailyhotel.network.model.RecommendationPlace;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class CollectionBaseActivity extends BaseActivity
{
    protected static final String INTENT_EXTRA_DATA_INDEX = "index";
    protected static final String INTENT_EXTRA_DATA_IMAGE_URL = "imageUrl";
    protected static final String INTENT_EXTRA_DATA_TITLE = "title";
    protected static final String INTENT_EXTRA_DATA_SUBTITLE = "subTitle";

    protected SaleTime mSaleTIme;
    protected SaleTime mStartSaleTime, mEndSaleTime;
    int mRecommendationIndex;
    CollectionBaseLayout mCollectionBaseLayout;

    private Handler mHandler = new Handler();

    protected abstract void requestRecommendationPlaceList();

    protected abstract CollectionBaseLayout getCollectionLayout(Context context);

    protected abstract String getCalendarDate();

    protected abstract void onCalendarActivityResult(int resultCode, Intent data);

    protected abstract String getSectionTitle(int count);

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

        mRecommendationIndex = intent.getIntExtra(INTENT_EXTRA_DATA_INDEX, -1);
        String title = intent.getStringExtra(INTENT_EXTRA_DATA_TITLE);
        String subTitle = intent.getStringExtra(INTENT_EXTRA_DATA_SUBTITLE);
        String imageUrl = intent.getStringExtra(INTENT_EXTRA_DATA_IMAGE_URL);

        if (mRecommendationIndex <= 0)
        {
            finish();
            return;
        }

        mCollectionBaseLayout = getCollectionLayout(this);

        setContentView(mCollectionBaseLayout.onCreateView(R.layout.activity_collection_search));

        boolean isDeepLink = Util.isTextEmpty(title, subTitle, imageUrl);

        if (isDeepLink == false && Util.isUsedMultiTransition() == true)
        {
            mCollectionBaseLayout.setTitleLayout(title, subTitle, imageUrl);

            initTransition();
        } else
        {
            mCollectionBaseLayout.setTitleLayout(title, subTitle, imageUrl);

            lockUI();

            requestCommonDateTime();
        }
    }

    void requestCommonDateTime()
    {
        DailyMobileAPI.getInstance(this).requestCommonDateTime(mNetworkTag, new Callback<JSONObject>()
        {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
            {
                if (response != null && response.isSuccessful() && response.body() != null)
                {
                    try
                    {
                        JSONObject responseJSONObject = response.body();

                        int msgCode = responseJSONObject.getInt("msgCode");

                        if (msgCode == 100)
                        {
                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                            long currentDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("currentDateTime"), DailyCalendar.ISO_8601_FORMAT);
                            long dailyDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("dailyDateTime"), DailyCalendar.ISO_8601_FORMAT);

                            onCommonDateTime(currentDateTime, dailyDateTime);
                        } else
                        {
                            String message = responseJSONObject.getString("msg");
                            onErrorPopupMessage(msgCode, message);
                        }
                    } catch (Exception e)
                    {
                        onError(e);
                    }
                } else
                {
                    onErrorResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                onError(t);
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        AnalyticsManager.getInstance(this).recordScreen(this, AnalyticsManager.Screen.RECOMMEND_LIST, null);
    }

    @Override
    public void onBackPressed()
    {
        if (Util.isUsedMultiTransition() == true)
        {
            mCollectionBaseLayout.setListScrollTop();

            mHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    CollectionBaseActivity.super.onBackPressed();
                }
            }, 100);

            return;
        }

        super.onBackPressed();
    }

    private void initTransition()
    {
        if (Util.isUsedMultiTransition() == true)
        {
            TransitionSet inTransitionSet = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP);

            getWindow().setSharedElementEnterTransition(inTransitionSet);

            TransitionSet outTransitionSet = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP);
            outTransitionSet.setDuration(200);

            getWindow().setSharedElementReturnTransition(outTransitionSet);
            inTransitionSet.addListener(new Transition.TransitionListener()
            {
                @Override
                public void onTransitionStart(Transition transition)
                {

                }

                @Override
                public void onTransitionEnd(Transition transition)
                {
                    lockUI();

                    requestCommonDateTime();
                }

                @Override
                public void onTransitionCancel(Transition transition)
                {

                }

                @Override
                public void onTransitionPause(Transition transition)
                {

                }

                @Override
                public void onTransitionResume(Transition transition)
                {

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockUI();

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_STAY_DETAIL:
            case CODE_REQUEST_ACTIVITY_GOURMET_DETAIL:
            {
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    case CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY:
                    case CODE_RESULT_ACTIVITY_GO_HOME:
                        setResult(resultCode);
                        finish();
                        break;

                    case CODE_RESULT_ACTIVITY_REFRESH:
                        lockUI();

                        requestCommonDateTime();
                        break;
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_CALENDAR:
                onCalendarActivityResult(resultCode, data);
                break;
        }
    }

    void onCommonDateTime(long currentDateTime, long dailyDateTime)
    {
        mSaleTIme = new SaleTime();
        mSaleTIme.setCurrentTime(currentDateTime);
        mSaleTIme.setDailyTime(dailyDateTime);

        mStartSaleTime = new SaleTime();
        mStartSaleTime.setCurrentTime(currentDateTime);
        mStartSaleTime.setDailyTime(dailyDateTime);

        mEndSaleTime = mStartSaleTime.getClone(1);

        mCollectionBaseLayout.setCalendarText(getCalendarDate());

        requestRecommendationPlaceList();
    }

    protected ArrayList<PlaceViewItem> makePlaceList(String imageBaseUrl, List<? extends RecommendationPlace> placeList)
    {
        ArrayList<PlaceViewItem> placeViewItemList = new ArrayList<>();

        // 빈공간
        placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_HEADER_VIEW, null));

        if (placeList == null || placeList.size() == 0)
        {
            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, null));
        } else
        {
            // 개수 넣기
            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_SECTION, getSectionTitle(placeList.size())));

            int entryPosition = 0;

            for (RecommendationPlace place : placeList)
            {
                place.imageUrl = imageBaseUrl + place.imageUrl;
                place.entryPosition = entryPosition++;
                placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, place));
            }
        }

        return placeViewItemList;
    }

    protected void onPlaceList(String imageBaseUrl, Recommendation recommendation, ArrayList<? extends RecommendationPlace> list)
    {
        if (isFinishing() == true)
        {
            return;
        }

        long currentTime, endTime;
        try
        {
            currentTime = mSaleTIme.getCurrentTime() - DailyCalendar.NINE_HOUR_MILLISECOND;
            endTime = DailyCalendar.convertDate(recommendation.endedAt, DailyCalendar.ISO_8601_FORMAT).getTime();
        } catch (Exception e)
        {
            ExLog.d(e.toString());

            currentTime = 0;
            endTime = -1;
        }

        mCollectionBaseLayout.setTitleLayout(recommendation.title, recommendation.subtitle, Util.getResolutionImageUrl(this, recommendation.defaultImageUrl, recommendation.lowResolutionImageUrl));

        if (endTime < currentTime)
        {
            mCollectionBaseLayout.setData(null);

            ArrayList<PlaceViewItem> placeViewItems = makePlaceList(imageBaseUrl, null);

            mCollectionBaseLayout.setData(placeViewItems);

            showSimpleDialog(null, getString(R.string.message_collection_finished_recommendation), getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    finish();
                }
            });
        } else
        {
            ArrayList<PlaceViewItem> placeViewItems = makePlaceList(imageBaseUrl, list);

            mCollectionBaseLayout.setData(placeViewItems);
        }
    }
}