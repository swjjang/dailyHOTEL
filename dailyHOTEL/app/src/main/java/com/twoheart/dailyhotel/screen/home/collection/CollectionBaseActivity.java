package com.twoheart.dailyhotel.screen.home.collection;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.View;

import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.RecommendationRemoteImpl;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.Recommendation;
import com.twoheart.dailyhotel.network.model.RecommendationPlace;
import com.twoheart.dailyhotel.network.model.Sticker;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public abstract class CollectionBaseActivity extends BaseActivity
{
    protected CommonRemoteImpl mCommonRemoteImpl;
    protected RecommendationRemoteImpl mRecommendationRemoteImpl;
    protected PlaceBookingDay mPlaceBookingDay;
    protected CommonDateTime mCommonDateTime;

    int mRecommendationIndex;
    CollectionBaseLayout mCollectionBaseLayout;
    private boolean checkRequestCollection; // 추천 목록 진입시에만 노출 하도록 한다.
    protected boolean mIsUsedMultiTransition;

    protected PlaceViewItem mPlaceViewItemByLongPress;
    protected int mListCountByLongPress;
    protected View mViewByLongPress;
    protected int mWishPosition;
    protected boolean mIsOverShowDate;

    protected abstract void requestRecommendationPlaceList(PlaceBookingDay placeBookingDay);

    protected abstract String getCalendarDate(PlaceBookingDay placeBookingDay);

    protected abstract void setPlaceBookingDay(CommonDateTime commonDateTime);

    protected abstract void onCalendarActivityResult(int resultCode, Intent data);

    protected abstract void startCalendarActivity(CommonDateTime commonDateTime, PlaceBookingDay placeBookingDay);

    protected abstract String getSectionTitle(int count);

    protected abstract void onCommonDateTime(CommonDateTime commonDateTime);

    protected abstract ArrayList<PlaceViewItem> makePlaceList(String imageBaseUrl, List<? extends RecommendationPlace> placeList, List<Sticker> stickerList);

    protected abstract void onPlaceDetailClickByLongPress(View view, PlaceViewItem placeViewItem, int listCount);

    protected abstract void onChangedWish(int position, boolean wish);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mCommonRemoteImpl = new CommonRemoteImpl();
        mRecommendationRemoteImpl = new RecommendationRemoteImpl();
    }

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
        addCompositeDisposable(mCommonRemoteImpl.getCommonDateTime().subscribe(new Consumer<CommonDateTime>()
        {
            @Override
            public void accept(CommonDateTime commonDateTime) throws Exception
            {
                onCommonDateTime(commonDateTime);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                onHandleError(throwable);
            }
        }));
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

            Single.just(mIsUsedMultiTransition).delaySubscription(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Boolean aBoolean) throws Exception
                {
                    CollectionBaseActivity.super.onBackPressed();
                }
            });

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

    protected void onPlaceList(boolean isOverShowDate, ArrayList<PlaceViewItem> list, boolean activeReward)
    {
        if (isFinishing() == true)
        {
            return;
        }

        DailyRemoteConfigPreference.getInstance(this).setKeyRemoteConfigRewardStickerEnabled(activeReward);

        mCollectionBaseLayout.setData(list, mPlaceBookingDay, activeReward);

        if (isOverShowDate)
        {
            showSimpleDialog(null, getString(R.string.message_collection_finished_recommendation) //
                , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        finish();
                    }
                });
        } else if ((list == null || list.size() == 0) && checkRequestCollection == false)
        {
            showSimpleDialog(null, getString(R.string.message_collection_empty_popup_message)//
                , getString(R.string.dialog_btn_text_yes)//
                , getString(R.string.dialog_btn_text_no)//
                , new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        startCalendarActivity(mCommonDateTime, mPlaceBookingDay);
                    }
                }, null);
        }

        checkRequestCollection = true;
    }

    protected void startCollectionPlace(Recommendation recommendation)
    {
        if (recommendation == null)
        {
            return;
        }

        Intent intent;


        String checkInDateTime = null;
        String checkOutDateTime = null;
        if (mPlaceBookingDay != null)
        {
            try
            {
                if (mPlaceBookingDay instanceof StayBookingDay)
                {
                    checkInDateTime = ((StayBookingDay) mPlaceBookingDay).getCheckInDay(DailyCalendar.ISO_8601_FORMAT);
                    checkOutDateTime = ((StayBookingDay) mPlaceBookingDay).getCheckOutDay(DailyCalendar.ISO_8601_FORMAT);
                } else if (mPlaceBookingDay instanceof GourmetBookingDay) {
                    checkInDateTime = ((GourmetBookingDay) mPlaceBookingDay).getVisitDay(DailyCalendar.ISO_8601_FORMAT);
                }
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
        }

        switch (recommendation.serviceType)
        {
            case "GOURMET":
                intent = CollectionGourmetActivity.newInstance(this, recommendation.idx//
                    , ScreenUtils.getResolutionImageUrl(this, recommendation.defaultImageUrl, recommendation.lowResolutionImageUrl)//
                    , recommendation.title, recommendation.subtitle, checkInDateTime, false);
                break;

            case "HOTEL":
            default:
                intent = CollectionStayActivity.newInstance(this, recommendation.idx//
                    , ScreenUtils.getResolutionImageUrl(this, recommendation.defaultImageUrl, recommendation.lowResolutionImageUrl)//
                    , recommendation.title, recommendation.subtitle, checkInDateTime, checkOutDateTime, false);
                break;
        }

        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_COLLECTION);

        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        finish();

        //        AnalyticsManager.getInstance(this).recordEvent(//
        //            AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.HOME_RECOMMEND_LIST_CLICK,//
        //            Integer.toString(recommendation.idx), null);
    }
}