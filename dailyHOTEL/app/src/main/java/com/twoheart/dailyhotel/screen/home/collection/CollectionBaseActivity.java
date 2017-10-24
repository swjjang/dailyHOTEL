package com.twoheart.dailyhotel.screen.home.collection;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.View;

import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.Recommendation;
import com.twoheart.dailyhotel.network.model.RecommendationPlace;
import com.twoheart.dailyhotel.network.model.Sticker;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class CollectionBaseActivity extends BaseActivity
{
    protected PlaceBookingDay mPlaceBookingDay;
    protected TodayDateTime mTodayDateTime;

    int mRecommendationIndex;
    CollectionBaseLayout mCollectionBaseLayout;
    private boolean checkRequestCollection; // 추천 목록 진입시에만 노출 하도록 한다.
    protected boolean mIsUsedMultiTransition;

    protected PlaceViewItem mPlaceViewItemByLongPress;
    protected int mListCountByLongPress;
    protected View mViewByLongPress;
    protected int mWishPosition;

    private Handler mHandler = new Handler();

    protected abstract void requestRecommendationPlaceList(PlaceBookingDay placeBookingDay);

    protected abstract String getCalendarDate(PlaceBookingDay placeBookingDay);

    protected abstract void setPlaceBookingDay(TodayDateTime todayDateTime);

    protected abstract void onCalendarActivityResult(int resultCode, Intent data);

    protected abstract void startCalendarActivity(TodayDateTime todayDateTime, PlaceBookingDay placeBookingDay);

    protected abstract String getSectionTitle(int count);

    protected abstract void onCommonDateTime(TodayDateTime todayDateTime);

    protected abstract ArrayList<PlaceViewItem> makePlaceList(String imageBaseUrl, List<? extends RecommendationPlace> placeList, List<Sticker> stickerList);

    protected abstract void onPlaceDetailClickByLongPress(View view, PlaceViewItem placeViewItem, int listCount);

    protected abstract void onChangedWish(int position, boolean wish);

    @Override
    protected void onResume()
    {
        super.onResume();

        if (mCollectionBaseLayout != null && mCollectionBaseLayout.getBlurVisibility() == true)
        {
            mCollectionBaseLayout.setBlurVisibility(this, false);
        }
    }

    void requestCommonDateTime()
    {
        DailyMobileAPI.getInstance(this).requestCommonDateTime(mNetworkTag, new Callback<BaseDto<TodayDateTime>>()
        {
            @Override
            public void onResponse(Call<BaseDto<TodayDateTime>> call, Response<BaseDto<TodayDateTime>> response)
            {
                if (response != null && response.isSuccessful() && response.body() != null)
                {
                    try
                    {
                        BaseDto<TodayDateTime> baseDto = response.body();

                        if (baseDto.msgCode == 100)
                        {
                            onCommonDateTime(baseDto.data);
                        } else
                        {
                            onErrorPopupMessage(baseDto.msgCode, baseDto.msg);
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
            public void onFailure(Call<BaseDto<TodayDateTime>> call, Throwable t)
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
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        Util.restartApp(this);
    }

    @Override
    public void onBackPressed()
    {
        if (mIsUsedMultiTransition == true)
        {
            lockUiComponent();

            mCollectionBaseLayout.setListScrollTop();

            mHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    CollectionBaseActivity.super.onBackPressed();
                }
            }, 300);

            return;
        }

        super.onBackPressed();
    }

    @Override
    public void finish()
    {
        super.finish();

        if (mIsUsedMultiTransition == false)
        {
            overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
        }
    }

    @TargetApi(value = 21)
    void initTransition()
    {
        if (mIsUsedMultiTransition == true)
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

    protected void onPlaceList(String imageBaseUrl, Recommendation recommendation, ArrayList<? extends RecommendationPlace> list, List<Sticker> stickerList)
    {
        if (isFinishing() == true)
        {
            return;
        }

        long currentTime, endTime;
        try
        {
            currentTime = DailyCalendar.convertDate(mTodayDateTime.currentDateTime, DailyCalendar.ISO_8601_FORMAT).getTime();
            endTime = DailyCalendar.convertDate(recommendation.endedAt, DailyCalendar.ISO_8601_FORMAT).getTime();
        } catch (Exception e)
        {
            ExLog.d(e.toString());

            currentTime = 0;
            endTime = -1;
        }

        mCollectionBaseLayout.setTitleLayout(recommendation.title, recommendation.subtitle, ScreenUtils.getResolutionImageUrl(this, recommendation.defaultImageUrl, recommendation.lowResolutionImageUrl));

        if (endTime < currentTime)
        {
            mCollectionBaseLayout.setData(null, mPlaceBookingDay);

            ArrayList<PlaceViewItem> placeViewItems = makePlaceList(imageBaseUrl, null, stickerList);

            mCollectionBaseLayout.setData(placeViewItems, mPlaceBookingDay);

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
            ArrayList<PlaceViewItem> placeViewItems = makePlaceList(imageBaseUrl, list, stickerList);

            mCollectionBaseLayout.setData(placeViewItems, mPlaceBookingDay);

            if ((list == null || list.size() == 0) && checkRequestCollection == false)
            {
                showSimpleDialog(null, getString(R.string.message_collection_empty_popup_message)//
                    , getString(R.string.dialog_btn_text_yes)//
                    , getString(R.string.dialog_btn_text_no)//
                    , new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            startCalendarActivity(mTodayDateTime, mPlaceBookingDay);
                        }
                    }, null);
            }
        }

        checkRequestCollection = true;
    }
}