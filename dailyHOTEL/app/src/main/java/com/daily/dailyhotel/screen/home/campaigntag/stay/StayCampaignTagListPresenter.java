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
import com.daily.dailyhotel.screen.common.dialog.call.CallDialogActivity;
import com.daily.dailyhotel.screen.home.campaigntag.CampaignTagListAnalyticsImpl;
import com.daily.dailyhotel.screen.home.campaigntag.CampaignTagListAnalyticsInterface;
import com.daily.dailyhotel.view.DailyStayCardView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.layout.PlaceDetailLayout;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCalendarActivity;
import com.twoheart.dailyhotel.screen.hotel.preview.StayPreviewActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android_sam on 2017. 8. 4..
 */

public class StayCampaignTagListPresenter extends BaseExceptionPresenter<StayCampaignTagListActivity, StayCampaignTagListInterface> implements StayCampaignTagListView.OnEventListener
{
    CampaignTagListAnalyticsInterface mAnalytics;

    private CommonRemoteImpl mCommonRemoteImpl;
    CampaignTagRemoteImpl mCampaignTagRemoteImpl;

    int mTagIndex;
    int mListCountByLongPress;
    String mTitle;
    StayBookingDay mStayBookingDay;
    CommonDateTime mCommonDateTime;
    StayCampaignTags mStayCampaignTags;
    PlaceViewItem mPlaceViewItemByLongPress;
    View mViewByLongPress;

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

        setAnalytics(new CampaignTagListAnalyticsImpl());

        mCommonRemoteImpl = new CommonRemoteImpl(activity);
        mCampaignTagRemoteImpl = new CampaignTagRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (CampaignTagListAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mTagIndex = intent.getIntExtra(StayCampaignTagListActivity.INTENT_EXTRA_DATA_INDEX, -1);
        mTitle = intent.getStringExtra(StayCampaignTagListActivity.INTENT_EXTRA_DATA_TITLE);
        mStayBookingDay = intent.getParcelableExtra(StayCampaignTagListActivity.INTENT_EXTRA_DATA_PLACEBOOKINGDAY);

        return true;
    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(mTitle);
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
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        Util.restartApp(getActivity());
    }

    @Override
    public boolean onBackPressed()
    {
        return super.onBackPressed();
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
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

        setRefresh(false);
        screenLock(showProgress);

        // commonDateTime after campainTagList;
        addCompositeDisposable(mCommonRemoteImpl.getCommonDateTime().map(new Function<CommonDateTime, StayBookingDay>()
        {
            @Override
            public StayBookingDay apply(@io.reactivex.annotations.NonNull CommonDateTime commonDateTime) throws Exception
            {
                mCommonDateTime = commonDateTime;

                StayBookingDay stayBookingDay = mStayBookingDay == null //
                    ? getStayBookingDay(mCommonDateTime) : mStayBookingDay;

                return stayBookingDay;
            }
        }).observeOn(Schedulers.io()).flatMap(new Function<StayBookingDay, Observable<StayCampaignTags>>()
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
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<PlaceViewItem>>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull ArrayList<PlaceViewItem> placeViewItemList) throws Exception
            {
                setCampaignTagLayout(mTitle, mCommonDateTime, mStayBookingDay, mStayCampaignTags, placeViewItemList);

                unLockAll();

                try
                {
                    int size = mStayCampaignTags.getStayList() == null ? 0 : mStayCampaignTags.getStayList().size();

                    mAnalytics.onCampaignTagEvent(getActivity() //
                        , mStayCampaignTags.getCampaignTag() //
                        , size);
                } catch (Exception e)
                {
                    ExLog.w(e.toString());
                }
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

    public void setCampaignTagLayout(String title, CommonDateTime commonDateTime //
        , StayBookingDay stayBookingDay, StayCampaignTags stayCampaignTags, ArrayList<PlaceViewItem> placeViewItemList)
    {
        setTitleText(title);
        setCalendarText(stayBookingDay);

        // 서버에서 전달된 데이터가 없을때 종료된 태그로 설정!
        if (stayCampaignTags == null)
        {
            setData(null, stayBookingDay);
            showFinishedCampaignTagDialog();
            //            mIsFirstUiUpdateCheck = true;
            return;
        }

        CampaignTag campaignTag = stayCampaignTags.getCampaignTag();
        if (campaignTag != null)
        {
            if (Constants.ServiceType.HOTEL.name().equalsIgnoreCase(campaignTag.serviceType) == false)
            {
                setData(null, stayBookingDay);
                showReCheckConnectionDialog();
                //                mIsFirstUiUpdateCheck = true;
                return;
            }
        }

        int msgCode = stayCampaignTags.msgCode;
        // 메세지코드로 종료된 팝업일때
        if (msgCode == 200)
        {
            setData(null, stayBookingDay);
            showFinishedCampaignTagDialog();
            //            mIsFirstUiUpdateCheck = true;
            return;
        }

        // 메세지 코드로 조회된 데이터가 없을때
        if (msgCode == -101)
        {
            setData(placeViewItemList, stayBookingDay);

            //            if (mIsFirstUiUpdateCheck == false)
            //            {
            //                showFirstEmptyListPopup();
            //            }
            //
            //            mIsFirstUiUpdateCheck = true;
            return;
        }

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
            // 시간 체크시 종료 된 캠페인 태그 일때
            setData(null, stayBookingDay);
            showFinishedCampaignTagDialog();
        } else
        {
            // 일반적인 상황
            setData(placeViewItemList, stayBookingDay);

            //            ArrayList<Stay> list = stayCampaignTags.getStayList();
            //            if ((list == null || list.size() == 0) && mIsFirstUiUpdateCheck == false)
            //            {
            //                // 처음 진입이고 일반적인 상황에서 리스트가 비었을때
            //                showFirstEmptyListPopup();
            //            }
        }

        //        mIsFirstUiUpdateCheck = true;
    }

    private void showFinishedCampaignTagDialog()
    {
        getViewInterface().showSimpleDialog(null //
            , getString(R.string.message_campaign_tag_finished) //
            , getString(R.string.dialog_btn_text_confirm) //
            , null, new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    setResult(Constants.CODE_RESULT_ACTIVITY_REFRESH);
                    finish();
                }
            });
    }

    private void showReCheckConnectionDialog()
    {
        getViewInterface().showSimpleDialog(null //
            , getString(R.string.message_campaign_tag_recheck_connection) //
            , getString(R.string.dialog_btn_text_confirm) //
            , null, new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    //                            setResult(Constants.CODE_RESULT_ACTIVITY_REFRESH);
                    finish();
                }
            });
    }

    //    private void showFirstEmptyListPopup()
    //    {
    //        getViewInterface().showSimpleDialog(null //
    //            , getString(R.string.message_campaign_empty_popup_message)//
    //            , getString(R.string.dialog_btn_text_yes)//
    //            , getString(R.string.dialog_btn_text_no)//
    //            , new View.OnClickListener()
    //            {
    //                @Override
    //                public void onClick(View v)
    //                {
    //                    onCalendarClick();
    //                }
    //            }, null);
    //    }

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
        String campaignTagName = campaignTag == null ? null : campaignTag.campaignTag;

        return DailyTextUtils.isTextEmpty(campaignTagName) == true ? mTitle : campaignTagName;
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

    ArrayList<PlaceViewItem> makePlaceList(ArrayList<Stay> stayList)
    {
        ArrayList<PlaceViewItem> placeViewItemList = new ArrayList<>();

        if (stayList == null || stayList.size() == 0)
        {
            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_EMPTY_VIEW, null));
        } else
        {
            int entryPosition = 0;

            for (Stay stay : stayList)
            {
                stay.entryPosition = entryPosition++;

                placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, stay));
            }

            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, null));
        }

        return placeViewItemList;
    }


    @Override
    public void onBackClick()
    {
        finish();
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

    @Override
    public void onResearchClick()
    {
        onBackClick();
    }

    @Override
    public void onCallClick()
    {
        startActivityForResult(CallDialogActivity.newInstance(getActivity()), StayCampaignTagListActivity.REQUEST_CODE_CALL);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onPlaceClick(View view, PlaceViewItem placeViewItem, int count)
    {
        if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
        {
            return;
        }

        Stay stay = placeViewItem.getItem();

        if (Util.isUsedMultiTransition() == true)
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
            analyticsParam.setParam(getActivity(), stay);
            analyticsParam.setProvince(null);
            analyticsParam.setTotalListCount(count);

            ActivityOptionsCompat optionsCompat;
            Intent intent;

            if (view instanceof DailyStayCardView == true)
            {
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), ((DailyStayCardView) view).getOptionsCompat());

                intent = StayDetailActivity.newInstance(getActivity(), mStayBookingDay //
                    , stay.index, stay.name, stay.imageUrl //
                    , analyticsParam, true, PlaceDetailLayout.TRANS_GRADIENT_BOTTOM_TYPE_LIST);
            } else
            {
                View simpleDraweeView = view.findViewById(R.id.imageView);
                View nameTextView = view.findViewById(R.id.nameTextView);
                View gradientTopView = view.findViewById(R.id.gradientTopView);
                View gradientBottomView = view.findViewById(R.id.gradientView);

                Object mapTag = gradientBottomView.getTag();


                if (mapTag != null && "map".equals(mapTag) == true)
                {
                    intent = StayDetailActivity.newInstance(getActivity(), mStayBookingDay //
                        , stay.index, stay.name, stay.imageUrl //
                        , analyticsParam, true, PlaceDetailLayout.TRANS_GRADIENT_BOTTOM_TYPE_MAP);
                } else
                {
                    intent = StayDetailActivity.newInstance(getActivity(), mStayBookingDay //
                        , stay.index, stay.name, stay.imageUrl //
                        , analyticsParam, true, PlaceDetailLayout.TRANS_GRADIENT_BOTTOM_TYPE_LIST);
                }

                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),//
                    android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)),//
                    android.support.v4.util.Pair.create(nameTextView, getString(R.string.transition_place_name)),//
                    android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view)),//
                    android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view)));
            }

            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_STAY_DETAIL, optionsCompat.toBundle());
        } else
        {
            AnalyticsParam analyticsParam = new AnalyticsParam();
            analyticsParam.setParam(getActivity(), stay);
            analyticsParam.setProvince(null);
            analyticsParam.setTotalListCount(count);

            Intent intent = StayDetailActivity.newInstance(getActivity(), mStayBookingDay //
                , stay.index, stay.name, stay.imageUrl //
                , analyticsParam, false, PlaceDetailLayout.TRANS_GRADIENT_BOTTOM_TYPE_NONE);

            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_STAY_DETAIL);

            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
        }

        if (stay.truevr == true)
        {
            AnalyticsManager.getInstance(getActivity()).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.STAY_ITEM_CLICK_TRUE_VR, Integer.toString(stay.index), null);
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

    StayBookingDay getStayBookingDay(CommonDateTime commonDateTime)
    {
        if (commonDateTime == null)
        {
            return mStayBookingDay;
        }

        StayBookingDay stayBookingDay = new StayBookingDay();

        try
        {
            stayBookingDay.setCheckInDay(commonDateTime.dailyDateTime);
            stayBookingDay.setCheckOutDay(commonDateTime.dailyDateTime, 1);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return stayBookingDay;
    }
}
