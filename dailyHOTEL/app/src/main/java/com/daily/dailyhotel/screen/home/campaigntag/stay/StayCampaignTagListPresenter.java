package com.daily.dailyhotel.screen.home.campaigntag.stay;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.View;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.StayCampaignTags;
import com.daily.dailyhotel.repository.local.model.AnalyticsParam;
import com.daily.dailyhotel.repository.remote.CampaignTagRemoteImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.RecommendationPlace;
import com.twoheart.dailyhotel.network.model.RecommendationStay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.layout.PlaceDetailLayout;
import com.twoheart.dailyhotel.screen.home.collection.CollectionStayActivity;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCalendarActivity;
import com.twoheart.dailyhotel.screen.hotel.preview.StayPreviewActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by iseung-won on 2017. 8. 4..
 */

public class StayCampaignTagListPresenter extends BaseExceptionPresenter<StayCampaignTagListActivity, StayCampaignTagListInterface> implements StayCampaignTagListView.OnEventListener
{
    private int mTagIndex;
    private boolean mIsUsedMultiTransition;
    private int mType;
    private int mAfterDay;
    private int mNights;
    private String mTitle;
    private StayBookingDay mStayBookingDay;
    private CommonDateTime mCommonDateTime;
    private StayCampaignTags mStayCampaignTags;
    private boolean mIsFirstUiUpdateCheck;

    private CommonRemoteImpl mCommonRemoteImpl;
    private CampaignTagRemoteImpl mCampaignTagRemoteImpl;

    private PlaceViewItem mPlaceViewItemByLongPress;
    private int mListCountByLongPress;
    private View mViewByLongPress;


    public StayCampaignTagListPresenter(@NonNull StayCampaignTagListActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayCampaignTagListInterface createInstanceViewInterface()
    {
        return new StayCampaignTagListView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayCampaignTagListActivity activity)
    {
        setContentView(R.layout.activity_place_campaign_tag_list_data);

        //        setAnalytics(new StayCampaignTagListAnalyticsImpl());

        mCommonRemoteImpl = new CommonRemoteImpl(activity);
        mCampaignTagRemoteImpl = new CampaignTagRemoteImpl(activity);

        setRefresh(false);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {

    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mTagIndex = intent.getIntExtra(StayCampaignTagListActivity.INTENT_EXTRA_DATA_INDEX, -1);
        mIsUsedMultiTransition = intent.getBooleanExtra(Constants.NAME_INTENT_EXTRA_DATA_IS_USED_MULTITRANSITIOIN, false);

        //        if (mTagIndex == -1)
        //        {
        //            return false;
        //        }

        mType = intent.getIntExtra(StayCampaignTagListActivity.INTENT_EXTRA_DATA_TYPE, StayCampaignTagListActivity.TYPE_DEFAULT);

        mTitle = intent.getStringExtra(StayCampaignTagListActivity.INTENT_EXTRA_DATA_TITLE);

        switch (mType)
        {
            case StayCampaignTagListActivity.TYPE_DEFAULT:
            {
                mStayBookingDay = intent.getParcelableExtra(StayCampaignTagListActivity.INTENT_EXTRA_DATA_PLACEBOOKINGDAY);
                break;
            }

            case StayCampaignTagListActivity.TYPE_DATE:
            {
                String checkInDateTime = intent.getStringExtra(StayCampaignTagListActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE);
                String checkOutDateTime = intent.getStringExtra(StayCampaignTagListActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE);

                if (DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == false)
                {
                    try
                    {
                        mStayBookingDay = new StayBookingDay();
                        mStayBookingDay.setCheckInDay(checkInDateTime);
                        mStayBookingDay.setCheckOutDay(checkOutDateTime);
                    } catch (Exception e)
                    {
                        mStayBookingDay = null;
                    }
                }
                break;
            }

            case StayCampaignTagListActivity.TYPE_AFTER_DAY:
            {
                mAfterDay = intent.getIntExtra(StayCampaignTagListActivity.INTENT_EXTRA_DATA_AFTER_DAY, 0);
                mNights = intent.getIntExtra(StayCampaignTagListActivity.INTENT_EXTRA_DATA_NIGHTS, 1);
                break;
            }
        }

        return true;
    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setUsedMultiTransition(mIsUsedMultiTransition);

        getViewInterface().setToolbarTitle(mTitle);

        if (StayCampaignTagListActivity.TYPE_DEFAULT == mType && mIsUsedMultiTransition == true)
        {
            initTransition();
        } else
        {
            onRefresh(true);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (getViewInterface() != null && getViewInterface().getBlurVisibility() == true)
        {
            getViewInterface().setBlurVisibility(getActivity(), false);
        }

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
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
    public void onFinish()
    {
        super.onFinish();

        if (mIsUsedMultiTransition == false)
        {
            getActivity().overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
        }
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
        unLockAll();

        switch (requestCode)
        {
            case Constants.CODE_REQUEST_ACTIVITY_STAY_DETAIL:
            case Constants.CODE_REQUEST_ACTIVITY_GOURMET_DETAIL:
            {
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    case Constants.CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY:
                    case Constants.CODE_RESULT_ACTIVITY_GO_HOME:
                        setResult(resultCode);
                        finish();
                        break;

                    case Constants.CODE_RESULT_ACTIVITY_REFRESH:
                        setRefresh(true);
                        break;
                }
                break;
            }

            case Constants.CODE_REQUEST_ACTIVITY_CALENDAR:
                if (resultCode == Activity.RESULT_OK)
                {
                    mStayBookingDay = data.getParcelableExtra(StayCampaignTagListActivity.INTENT_EXTRA_DATA_PLACEBOOKINGDAY);

                    if (mStayBookingDay == null)
                    {
                        return;
                    }

                    setCalendarText(mStayBookingDay);

                    setRefresh(true);
                }
                break;

            case Constants.CODE_REQUEST_ACTIVITY_PREVIEW:
                if (resultCode == Activity.RESULT_OK)
                {
                    Observable.create(new ObservableOnSubscribe<Object>()
                    {
                        @Override
                        public void subscribe(ObservableEmitter<Object> e) throws Exception
                        {
                            if (mViewByLongPress == null || mPlaceViewItemByLongPress == null)
                            {
                                return;
                            }

                            onPlaceClick(mViewByLongPress, mPlaceViewItemByLongPress, mListCountByLongPress);
                        }
                    }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                }
                break;
        }
    }

    @Override
    protected void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true)
        {
            return;
        }

        setRefresh(false);
        screenLock(showProgress);

        if (mStayBookingDay == null)
        {
            // commonDateTime after campainTagList;
            addCompositeDisposable(mCommonRemoteImpl.getCommonDateTime().map(new Function<CommonDateTime, StayBookingDay>()
            {
                @Override
                public StayBookingDay apply(@io.reactivex.annotations.NonNull CommonDateTime commonDateTime) throws Exception
                {
                    mCommonDateTime = commonDateTime;

                    StayBookingDay stayBookingDay = new StayBookingDay();
                    stayBookingDay.setCheckInDay(commonDateTime.dailyDateTime, mAfterDay);
                    stayBookingDay.setCheckOutDay(stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT), mNights);

                    return stayBookingDay;
                }
            }).subscribeOn(Schedulers.io()).flatMap(new Function<StayBookingDay, Observable<StayCampaignTags>>()
            {
                @Override
                public Observable<StayCampaignTags> apply(@io.reactivex.annotations.NonNull StayBookingDay stayBookingDay) throws Exception
                {
                    mStayBookingDay = stayBookingDay;

                    return mCampaignTagRemoteImpl.getStayCampaignTags(mTagIndex, mStayBookingDay);
                }
            }).map(new Function<StayCampaignTags, ArrayList<PlaceViewItem>>()
            {
                @Override
                public ArrayList<PlaceViewItem> apply(@io.reactivex.annotations.NonNull StayCampaignTags stayCampaignTags) throws Exception
                {
                    mStayCampaignTags = stayCampaignTags;

                    mTitle = getTitleText(mStayCampaignTags);

                    if (mStayCampaignTags == null)
                    {
                        mStayCampaignTags = new StayCampaignTags();
                    }

                    return makePlaceList(mStayCampaignTags.getStayList());
                }
            }).subscribe(new Consumer<ArrayList<PlaceViewItem>>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull ArrayList<PlaceViewItem> placeViewItemList) throws Exception
                {
                    setCampaignTagLayout(mTitle, mCommonDateTime, mStayBookingDay, mStayCampaignTags, placeViewItemList);

                    unLockAll();
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                {
                    onHandleError(throwable);
                }
            }));
        } else
        {
            addCompositeDisposable(Observable.zip(mCommonRemoteImpl.getCommonDateTime() //
                , mCampaignTagRemoteImpl.getStayCampaignTags(mTagIndex, mStayBookingDay) //
                , new BiFunction<CommonDateTime, StayCampaignTags, ArrayList<PlaceViewItem>>()
                {
                    @Override
                    public ArrayList<PlaceViewItem> apply(@io.reactivex.annotations.NonNull CommonDateTime commonDateTime //
                        , @io.reactivex.annotations.NonNull StayCampaignTags stayCampaignTags) throws Exception
                    {
                        mCommonDateTime = commonDateTime;

                        mStayCampaignTags = stayCampaignTags;

                        mTitle = getTitleText(mStayCampaignTags);

                        if (mStayCampaignTags == null)
                        {
                            mStayCampaignTags = new StayCampaignTags();
                        }

                        return makePlaceList(stayCampaignTags.getStayList());
                    }
                }).subscribe(new Consumer<ArrayList<PlaceViewItem>>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull ArrayList<PlaceViewItem> placeViewItemList) throws Exception
                {
                    setCampaignTagLayout(mTitle, mCommonDateTime, mStayBookingDay, mStayCampaignTags, placeViewItemList);

                    unLockAll();
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                {
                    onHandleError(throwable);
                }
            }));
        }
    }

    public void setCampaignTagLayout(String title, CommonDateTime commonDateTime //
        , StayBookingDay stayBookingDay, StayCampaignTags stayCampaignTags, ArrayList<PlaceViewItem> placeViewItemList)
    {
        setTitleText(title);
        setCalendarText(stayBookingDay);


        long currentTime;
        long endTime;

        try
        {
            currentTime = DailyCalendar.convertDate(commonDateTime.currentDateTime, DailyCalendar.ISO_8601_FORMAT).getTime();
            endTime = DailyCalendar.convertDate(stayCampaignTags.getCampaignTag().endDate, DailyCalendar.ISO_8601_FORMAT).getTime();
        } catch (Exception e)
        {
            ExLog.d(e.toString());

            currentTime = 0;
            endTime = -1;
        }

        if (endTime < currentTime)
        {
            setData(null, stayBookingDay);

            getViewInterface().showSimpleDialog(null //
                , getString(R.string.message_campaign_tag_finished) //
                , getString(R.string.dialog_btn_text_confirm) //
                , null, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        finish();
                    }
                });
        } else
        {
            setData(placeViewItemList, stayBookingDay);

            ArrayList<Stay> list = stayCampaignTags.getStayList();
            if ((list == null || list.size() == 0) && mIsFirstUiUpdateCheck == false)
            {
                getViewInterface().showSimpleDialog(null //
                    , getString(R.string.message_campaign_empty_popup_message)//
                    , getString(R.string.dialog_btn_text_yes)//
                    , getString(R.string.dialog_btn_text_no)//
                    , new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            onCalendarClick();
                        }
                    }, null);
            }
        }

        mIsFirstUiUpdateCheck = true;

    }

    public void setTitleText(String title)
    {
        getViewInterface().setToolbarTitle(title);
    }

    public String getTitleText(StayCampaignTags stayCampaignTags)
    {
        if (stayCampaignTags == null)
        {
            return mTitle;
        }

        CampaignTag campaignTag = stayCampaignTags.getCampaignTag();

        return campaignTag == null ? mTitle : campaignTag.campaignTag;
    }

    public void setCalendarText(StayBookingDay stayBookingDay)
    {
        getViewInterface().setCalendarText(getCalendarText(stayBookingDay));
    }

    private String getCalendarText(StayBookingDay stayBookingDay)
    {
        if (stayBookingDay == null)
        {
            return null;
        }

        try
        {
            String checkInDate = stayBookingDay.getCheckInDay("yyyy.MM.dd(EEE)");
            String checkOutDate = stayBookingDay.getCheckOutDay("yyyy.MM.dd(EEE)");

            int nights = stayBookingDay.getNights();

            return String.format(Locale.KOREA, "%s - %s, %d박", checkInDate, checkOutDate, nights);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return null;
    }

    private void setData(ArrayList<PlaceViewItem> list, StayBookingDay stayBookingDay)
    {
        getViewInterface().setData(list, stayBookingDay);
    }

    private ArrayList<PlaceViewItem> makePlaceList(ArrayList<Stay> stayList)
    {
        ArrayList<PlaceViewItem> placeViewItemList = new ArrayList<>();

        // 빈공간
        placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_HEADER_VIEW, null));

        if (stayList == null || stayList.size() == 0)
        {
            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, null));
        } else
        {
            // 개수 넣기
            //            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_SECTION, getSectionTitle(placeList.size())));

            int entryPosition = 0;

            for (Stay stay : stayList)
            {
                stay.entryPosition = entryPosition++;

                placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, stay));
            }

            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_GUIDE_VIEW, null));
        }

        return placeViewItemList;
    }


    @Override
    public void onBackClick()
    {
        onFinish();
    }

    @Override
    public void onCalendarClick()
    {
        TodayDateTime todayDateTime = new TodayDateTime(mCommonDateTime.openDateTime //
            , mCommonDateTime.closeDateTime, mCommonDateTime.currentDateTime //
            , mCommonDateTime.dailyDateTime);

        Intent intent = StayCalendarActivity.newInstance(getActivity(), todayDateTime //
            , mStayBookingDay, StayCalendarActivity.DEFAULT_DOMESTIC_CALENDAR_DAY_OF_MAX_COUNT //
            , AnalyticsManager.ValueType.SEARCH, true, true);
        startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_CALENDAR);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onPlaceClick(View view, PlaceViewItem placeViewItem, int count)
    {
        if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
        {
            return;
        }

        RecommendationStay recommendationStay = placeViewItem.getItem();

        if (mIsUsedMultiTransition == true)
        {
            getActivity().setExitSharedElementCallback(new SharedElementCallback()
            {
                @Override
                public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots)
                {
                    super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);

                    for (View view : sharedElements)
                    {
                        if (view instanceof SimpleDraweeView)
                        {
                            view.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                }
            });

            AnalyticsParam analyticsParam = new AnalyticsParam();
            analyticsParam.setParam(getActivity(), recommendationStay);
            analyticsParam.setProvince(null);
            analyticsParam.setTotalListCount(count);

            View simpleDraweeView = view.findViewById(R.id.imageView);
            View gradeTextView = view.findViewById(R.id.gradeTextView);
            View nameTextView = view.findViewById(R.id.nameTextView);
            View gradientTopView = view.findViewById(R.id.gradientTopView);
            View gradientBottomView = view.findViewById(R.id.gradientView);

            Object mapTag = gradientBottomView.getTag();
            Intent intent;

            if (mapTag != null && "map".equals(mapTag) == true)
            {
                intent = StayDetailActivity.newInstance(getActivity(), mStayBookingDay //
                    , recommendationStay.index, recommendationStay.name, recommendationStay.imageUrl //
                    , analyticsParam, true, PlaceDetailLayout.TRANS_GRADIENT_BOTTOM_TYPE_MAP);
            } else
            {
                intent = StayDetailActivity.newInstance(getActivity(), mStayBookingDay //
                    , recommendationStay.index, recommendationStay.name, recommendationStay.imageUrl //
                    , analyticsParam, true, PlaceDetailLayout.TRANS_GRADIENT_BOTTOM_TYPE_LIST);
            }

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),//
                android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)),//
                android.support.v4.util.Pair.create(gradeTextView, getString(R.string.transition_place_grade)),//
                android.support.v4.util.Pair.create(nameTextView, getString(R.string.transition_place_name)),//
                android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view)),//
                android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view)));

            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_STAY_DETAIL, options.toBundle());
        } else
        {
            AnalyticsParam analyticsParam = new AnalyticsParam();
            analyticsParam.setParam(getActivity(), recommendationStay);
            analyticsParam.setProvince(null);
            analyticsParam.setTotalListCount(count);

            Intent intent = StayDetailActivity.newInstance(getActivity(), mStayBookingDay //
                , recommendationStay.index, recommendationStay.name, recommendationStay.imageUrl //
                , analyticsParam, false, PlaceDetailLayout.TRANS_GRADIENT_BOTTOM_TYPE_NONE);

            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_STAY_DETAIL);

            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
        }

        //        AnalyticsManager.getInstance(getActivity()).recordEvent(//
        //            AnalyticsManager.Category.HOME_RECOMMEND, Integer.toString(mRecommendationIndex),//
        //            Integer.toString(recommendationStay.index), null);


        if (recommendationStay.truevr == true)
        {
            AnalyticsManager.getInstance(getActivity()).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.STAY_ITEM_CLICK_TRUE_VR, Integer.toString(recommendationStay.index), null);
        }
    }

    @Override
    public void onPlaceLongClick(View view, PlaceViewItem placeViewItem, int count)
    {
        if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
        {
            return;
        }

        getViewInterface().setBlurVisibility(getActivity(), true);

        Stay stay = placeViewItem.getItem();

        // 기존 데이터를 백업한다.
        mViewByLongPress = view;
        mPlaceViewItemByLongPress = placeViewItem;
        mListCountByLongPress = count;

        Intent intent = StayPreviewActivity.newInstance(getActivity(), mStayBookingDay, stay);

        startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PREVIEW);
    }

    @TargetApi(value = 21)
    void initTransition()
    {
        if (mIsUsedMultiTransition == true)
        {
            TransitionSet inTransitionSet = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP);

            getActivity().getWindow().setSharedElementEnterTransition(inTransitionSet);

            TransitionSet outTransitionSet = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP);
            outTransitionSet.setDuration(200);

            getActivity().getWindow().setSharedElementReturnTransition(outTransitionSet);
            inTransitionSet.addListener(new Transition.TransitionListener()
            {
                @Override
                public void onTransitionStart(Transition transition)
                {

                }

                @Override
                public void onTransitionEnd(Transition transition)
                {
                    onRefresh(true);
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
}
