package com.daily.dailyhotel.screen.home.campaigntag.gourmet;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.view.View;
import android.widget.Toast;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.GourmetCampaignTags;
import com.daily.dailyhotel.repository.local.model.AnalyticsParam;
import com.daily.dailyhotel.repository.remote.CampaignTagRemoteImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.screen.home.campaigntag.CampaignTagListAnalyticsImpl;
import com.daily.dailyhotel.screen.home.campaigntag.CampaignTagListAnalyticsInterface;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.layout.PlaceDetailLayout;
import com.twoheart.dailyhotel.screen.gourmet.detail.GourmetDetailActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCalendarActivity;
import com.twoheart.dailyhotel.screen.gourmet.preview.GourmetPreviewActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCalendarActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by iseung-won on 2017. 8. 4..
 */

public class GourmetCampaignTagListPresenter //
    extends BaseExceptionPresenter<GourmetCampaignTagListActivity, GourmetCampaignTagListInterface> //
    implements GourmetCampaignTagListView.OnEventListener
{
    private int mTagIndex;
    private int mType;
    private int mAfterDay;
    private String mTitle;
    private GourmetBookingDay mGourmetBookingDay;
    private CommonDateTime mCommonDateTime;
    private GourmetCampaignTags mGourmetCampaignTags;
    private boolean mIsFirstUiUpdateCheck;

    private CommonRemoteImpl mCommonRemoteImpl;
    private CampaignTagRemoteImpl mCampaignTagRemoteImpl;

    private PlaceViewItem mPlaceViewItemByLongPress;
    private int mListCountByLongPress;
    private View mViewByLongPress;

    private CampaignTagListAnalyticsInterface mAnalytics;

    protected interface OnCallDialogListener
    {
        void onShowDialog();

        void onPositiveButtonClick(View v);

        void onNativeButtonClick(View v);

        void onDismissDialog();
    }

    // showCallDialog 용 interface
    private interface OnOperatingTimeListener
    {
        void onInValidOperatingTime(boolean isInValidOperatingTime);
    }

    public GourmetCampaignTagListPresenter(@NonNull GourmetCampaignTagListActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected GourmetCampaignTagListInterface createInstanceViewInterface()
    {
        return new GourmetCampaignTagListView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(GourmetCampaignTagListActivity activity)
    {
        setContentView(R.layout.activity_place_campaign_tag_list_data);

        setAnalytics(new CampaignTagListAnalyticsImpl());

        mCommonRemoteImpl = new CommonRemoteImpl(activity);
        mCampaignTagRemoteImpl = new CampaignTagRemoteImpl(activity);

        setRefresh(false);
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

        mTagIndex = intent.getIntExtra(GourmetCampaignTagListActivity.INTENT_EXTRA_DATA_INDEX, -1);

        mType = intent.getIntExtra(GourmetCampaignTagListActivity.INTENT_EXTRA_DATA_TYPE, GourmetCampaignTagListActivity.TYPE_DEFAULT);

        mTitle = intent.getStringExtra(GourmetCampaignTagListActivity.INTENT_EXTRA_DATA_TITLE);

        switch (mType)
        {
            case GourmetCampaignTagListActivity.TYPE_DEFAULT:
            {
                mGourmetBookingDay = intent.getParcelableExtra(GourmetCampaignTagListActivity.INTENT_EXTRA_DATA_PLACEBOOKINGDAY);
                break;
            }

            case GourmetCampaignTagListActivity.TYPE_DATE:
            {
                String visitDateTime = intent.getStringExtra(GourmetCampaignTagListActivity.INTENT_EXTRA_DATA_VISIT_DATE);

                if (DailyTextUtils.isTextEmpty(visitDateTime) == false)
                {
                    try
                    {
                        mGourmetBookingDay = new GourmetBookingDay();
                        mGourmetBookingDay.setVisitDay(visitDateTime);
                    } catch (Exception e)
                    {
                        mGourmetBookingDay = null;
                    }
                }
                break;
            }

            case GourmetCampaignTagListActivity.TYPE_AFTER_DAY:
            {
                mAfterDay = intent.getIntExtra(GourmetCampaignTagListActivity.INTENT_EXTRA_DATA_AFTER_DAY, 0);
                break;
            }
        }

        return true;
    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(mTitle);
        onRefresh(true);
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

        getActivity().overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
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
                    mGourmetBookingDay = data.getParcelableExtra(GourmetCampaignTagListActivity.INTENT_EXTRA_DATA_PLACEBOOKINGDAY);

                    if (mGourmetBookingDay == null)
                    {
                        return;
                    }

                    setCalendarText(mGourmetBookingDay);

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

        // commonDateTime after campainTagList;
        addCompositeDisposable(mCommonRemoteImpl.getCommonDateTime().map(new Function<CommonDateTime, GourmetBookingDay>()
        {
            @Override
            public GourmetBookingDay apply(@io.reactivex.annotations.NonNull CommonDateTime commonDateTime) throws Exception
            {
                mCommonDateTime = commonDateTime;

                GourmetBookingDay gourmetBookingDay = mGourmetBookingDay == null //
                    ? getGourmetBookingDay(mCommonDateTime) : mGourmetBookingDay;

                return gourmetBookingDay;
            }
        }).observeOn(Schedulers.io()).flatMap(new Function<GourmetBookingDay, Observable<GourmetCampaignTags>>()
        {
            @Override
            public Observable<GourmetCampaignTags> apply(@io.reactivex.annotations.NonNull GourmetBookingDay gourmetBookingDay) throws Exception
            {
                mGourmetBookingDay = gourmetBookingDay;

                return mCampaignTagRemoteImpl.getGourmetCampaignTags(mTagIndex, mGourmetBookingDay);
            }
        }).map(new Function<GourmetCampaignTags, ArrayList<PlaceViewItem>>()
        {
            @Override
            public ArrayList<PlaceViewItem> apply(@io.reactivex.annotations.NonNull GourmetCampaignTags gourmetCampaignTags) throws Exception
            {
                mGourmetCampaignTags = gourmetCampaignTags;

                mTitle = getTitleText(mGourmetCampaignTags);

                if (mGourmetCampaignTags == null)
                {
                    mGourmetCampaignTags = new GourmetCampaignTags();
                }

                return makePlaceList(mGourmetCampaignTags.getGourmetList());
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<PlaceViewItem>>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull ArrayList<PlaceViewItem> placeViewItemList) throws Exception
            {
                setCampaignTagLayout(mTitle, mCommonDateTime, mGourmetBookingDay, mGourmetCampaignTags, placeViewItemList);

                unLockAll();

                try
                {
                    mAnalytics.onCampaignTagEvent(getActivity() //
                        , mGourmetCampaignTags.getCampaignTag() //
                        , mGourmetCampaignTags.getGourmetList().size());
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
        , GourmetBookingDay gourmetBookingDay, GourmetCampaignTags gourmetCampaignTags, ArrayList<PlaceViewItem> placeViewItemList)
    {
        setTitleText(title);
        setCalendarText(gourmetBookingDay);

        long currentTime;
        long endTime;

        try
        {
            currentTime = DailyCalendar.convertDate(commonDateTime.currentDateTime, DailyCalendar.ISO_8601_FORMAT).getTime();
            endTime = DailyCalendar.convertDate(gourmetCampaignTags.getCampaignTag().endDate, DailyCalendar.ISO_8601_FORMAT).getTime();
        } catch (Exception e)
        {
            ExLog.d(e.toString());

            currentTime = 0;
            endTime = -1;
        }

        if (endTime < currentTime)
        {
            setData(null, gourmetBookingDay);

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
            setData(placeViewItemList, gourmetBookingDay);

            ArrayList<Gourmet> list = gourmetCampaignTags.getGourmetList();
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

    public String getTitleText(GourmetCampaignTags gourmetCampaignTags)
    {
        if (gourmetCampaignTags == null)
        {
            return mTitle;
        }

        CampaignTag campaignTag = gourmetCampaignTags.getCampaignTag();
        String campaignTagName = campaignTag == null ? null : campaignTag.campaignTag;

        return DailyTextUtils.isTextEmpty(campaignTagName) == true ? mTitle : campaignTag.campaignTag;
    }

    public void setCalendarText(GourmetBookingDay stayBookingDay)
    {
        getViewInterface().setCalendarText(getCalendarText(stayBookingDay));
    }

    private String getCalendarText(GourmetBookingDay gourmetBookingDay)
    {
        if (gourmetBookingDay == null)
        {
            return null;
        }

        try
        {
            return gourmetBookingDay.getVisitDay("yyyy.MM.dd(EEE)");
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return null;
    }

    private void setData(ArrayList<PlaceViewItem> list, GourmetBookingDay gourmetBookingDay)
    {
        getViewInterface().setData(list, gourmetBookingDay);
    }

    private ArrayList<PlaceViewItem> makePlaceList(ArrayList<Gourmet> gourmetList)
    {
        ArrayList<PlaceViewItem> placeViewItemList = new ArrayList<>();

        if (gourmetList == null || gourmetList.size() == 0)
        {
            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, null));
        } else
        {
            int entryPosition = 0;

            for (Gourmet gourmet : gourmetList)
            {
                gourmet.entryPosition = entryPosition++;

                placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, gourmet));
            }

            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_GUIDE_VIEW, null));
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

        Intent intent = GourmetCalendarActivity.newInstance(getActivity(), todayDateTime //
            , mGourmetBookingDay, StayCalendarActivity.DEFAULT_DOMESTIC_CALENDAR_DAY_OF_MAX_COUNT //
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
        showDailyCallDialog(null);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onPlaceClick(View view, PlaceViewItem placeViewItem, int count)
    {
        if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
        {
            return;
        }

        Gourmet gourmet = placeViewItem.getItem();

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
            analyticsParam.setParam(getActivity(), gourmet);
            analyticsParam.setProvince(null);
            analyticsParam.setTotalListCount(count);

            View simpleDraweeView = view.findViewById(R.id.imageView);
            View nameTextView = view.findViewById(R.id.nameTextView);
            View gradientTopView = view.findViewById(R.id.gradientTopView);
            View gradientBottomView = view.findViewById(R.id.gradientView);

            Object mapTag = gradientBottomView.getTag();
            Intent intent;

            if (mapTag != null && "map".equals(mapTag) == true)
            {
                intent = GourmetDetailActivity.newInstance(getActivity() //
                    , mGourmetBookingDay, gourmet.index, gourmet.name //
                    , gourmet.imageUrl, gourmet.category, gourmet.isSoldOut//
                    , analyticsParam, true, PlaceDetailLayout.TRANS_GRADIENT_BOTTOM_TYPE_MAP);
            } else
            {
                intent = GourmetDetailActivity.newInstance(getActivity()//
                    , mGourmetBookingDay, gourmet.index, gourmet.name //
                    , gourmet.imageUrl, gourmet.category, gourmet.isSoldOut//
                    , analyticsParam, true, PlaceDetailLayout.TRANS_GRADIENT_BOTTOM_TYPE_LIST);
            }

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),//
                android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)),//
                android.support.v4.util.Pair.create(nameTextView, getString(R.string.transition_place_name)),//
                android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view)),//
                android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view)));

            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_GOURMET_DETAIL, options.toBundle());
        } else
        {
            AnalyticsParam analyticsParam = new AnalyticsParam();
            analyticsParam.setParam(getActivity(), gourmet);
            analyticsParam.setProvince(null);
            analyticsParam.setTotalListCount(count);

            Intent intent = GourmetDetailActivity.newInstance(getActivity() //
                , mGourmetBookingDay, gourmet.index, gourmet.name //
                , gourmet.imageUrl, gourmet.category, gourmet.isSoldOut//
                , analyticsParam, false, PlaceDetailLayout.TRANS_GRADIENT_BOTTOM_TYPE_NONE);

            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_GOURMET_DETAIL);

            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
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

        Gourmet gourmet = placeViewItem.getItem();

        // 기존 데이터를 백업한다.
        mViewByLongPress = view;
        mPlaceViewItemByLongPress = placeViewItem;
        mListCountByLongPress = count;

        Intent intent = GourmetPreviewActivity.newInstance(getActivity(), mGourmetBookingDay, gourmet);

        startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PREVIEW);
    }

    private GourmetBookingDay getGourmetBookingDay(CommonDateTime commonDateTime)
    {
        if (commonDateTime == null)
        {
            return mGourmetBookingDay;
        }

        GourmetBookingDay gourmetBookingDay = new GourmetBookingDay();

        try
        {
            gourmetBookingDay.setVisitDay(commonDateTime.dailyDateTime);

            switch (mType)
            {
                case GourmetCampaignTagListActivity.TYPE_DEFAULT:
                    break;

                case GourmetCampaignTagListActivity.TYPE_DATE:
                    if (mGourmetBookingDay != null)
                    {
                        try
                        {
                            int startVisitDay = Integer.parseInt(mGourmetBookingDay.getVisitDay("yyyyMMdd"));
                            int dailyVisitDay = Integer.parseInt(gourmetBookingDay.getVisitDay("yyyyMMdd"));

                            // 데일리타임 이후 날짜인 경우에는
                            if (startVisitDay >= dailyVisitDay)
                            {
                                gourmetBookingDay.setVisitDay(mGourmetBookingDay.getVisitDay(DailyCalendar.ISO_8601_FORMAT));
                            }
                        } catch (Exception e)
                        {
                            ExLog.e(e.toString());
                        }
                    }

                    mType = GourmetCampaignTagListActivity.TYPE_DEFAULT;
                    break;

                case GourmetCampaignTagListActivity.TYPE_AFTER_DAY:
                    if (mAfterDay >= 0)
                    {
                        try
                        {
                            gourmetBookingDay.setVisitDay(commonDateTime.dailyDateTime, mAfterDay);
                        } catch (Exception e)
                        {
                            ExLog.e(e.toString());
                        }

                        mAfterDay = -1;
                    }

                    mType = GourmetCampaignTagListActivity.TYPE_DEFAULT;
                    break;
            }


        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return gourmetBookingDay;
    }

    private void checkInValidOperatingTime(OnOperatingTimeListener operatingTimeListener)
    {
        if (operatingTimeListener == null)
        {
            return;
        }

        retrofit2.Callback dateTimeCallback = new retrofit2.Callback<BaseDto<TodayDateTime>>()
        {
            @Override
            public void onResponse(Call<BaseDto<TodayDateTime>> call, Response<BaseDto<TodayDateTime>> response)
            {
                boolean isInValidOperatingTime = false;

                if (response != null && response.isSuccessful() && response.body() != null)
                {
                    try
                    {
                        BaseDto<TodayDateTime> baseDto = response.body();

                        if (baseDto.msgCode == 100)
                        {
                            TodayDateTime todayDateTime = baseDto.data;

                            Calendar todayCalendar = DailyCalendar.getInstance(todayDateTime.currentDateTime, false);
                            int hour = todayCalendar.get(Calendar.HOUR_OF_DAY);
                            int minute = todayCalendar.get(Calendar.MINUTE);

                            String startHourString = DailyCalendar.convertDateFormatString(todayDateTime.openDateTime, DailyCalendar.ISO_8601_FORMAT, "H");
                            String endHourString = DailyCalendar.convertDateFormatString(todayDateTime.closeDateTime, DailyCalendar.ISO_8601_FORMAT, "H");

                            int startHour = Integer.parseInt(startHourString);
                            int endHour = Integer.parseInt(endHourString);

                            String[] lunchTimes = DailyRemoteConfigPreference.getInstance(getActivity()).getRemoteConfigOperationLunchTime().split("\\,");
                            String[] startLunchTime = lunchTimes[0].split(":");
                            String[] endLunchTime = lunchTimes[1].split(":");

                            int startLunchHour = Integer.parseInt(startLunchTime[0]);
                            int startLunchMinute = Integer.parseInt(startLunchTime[1]);
                            int endLunchHour = Integer.parseInt(endLunchTime[0]);

                            boolean isOverStartTime = hour > startLunchHour || (hour == startLunchHour && minute >= startLunchMinute);
                            boolean isOverEndTime = hour >= endLunchHour;

                            if (hour < startHour && hour > endHour)
                            {
                                // 운영 안하는 시간 03:00:01 ~ 08:59:59 - 팝업 발생
                                isInValidOperatingTime = true;
                            } else if (isOverStartTime == true && isOverEndTime == false)
                            {
                                // 점심시간 11:50:01~12:59:59 - 해피톡의 경우 팝업 발생 안함
                                isInValidOperatingTime = true;
                            }
                        } else
                        {
                            isInValidOperatingTime = false;
                        }
                    } catch (Exception e)
                    {
                        isInValidOperatingTime = false;
                    }
                } else
                {
                    isInValidOperatingTime = false;
                }

                operatingTimeListener.onInValidOperatingTime(isInValidOperatingTime);
            }

            @Override
            public void onFailure(Call<BaseDto<TodayDateTime>> call, Throwable t)
            {
                operatingTimeListener.onInValidOperatingTime(false);
            }
        };

        DailyMobileAPI.getInstance(getActivity()).requestCommonDateTime(getClass().getName(), dateTimeCallback);
    }

    public void showDailyCallDialog(final OnCallDialogListener listener)
    {
        OnOperatingTimeListener operatingTimeListener = new OnOperatingTimeListener()
        {
            @Override
            public void onInValidOperatingTime(boolean isInValidOperatingTime)
            {
                if (isInValidOperatingTime == true)
                {
                    showNonOperatingTimeDialog(listener);
                } else
                {
                    View.OnClickListener positiveListener = new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            unLockAll();

                            if (listener != null)
                            {
                                listener.onPositiveButtonClick(v);
                            }

                            String remoteConfigPhoneNumber = DailyRemoteConfigPreference.getInstance(getActivity()).getRemoteConfigCompanyPhoneNumber();
                            String phoneNumber = DailyTextUtils.isTextEmpty(remoteConfigPhoneNumber) == false //
                                ? remoteConfigPhoneNumber : Constants.PHONE_NUMBER_DAILYHOTEL;

                            String noCallMessage = getActivity().getResources().getString(R.string.toast_msg_no_call_format, phoneNumber);

                            if (Util.isTelephonyEnabled(getActivity()) == true)
                            {
                                try
                                {
                                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));
                                } catch (ActivityNotFoundException e)
                                {
                                    DailyToast.showToast(getActivity(), noCallMessage, Toast.LENGTH_LONG);
                                }
                            } else
                            {
                                DailyToast.showToast(getActivity(), noCallMessage, Toast.LENGTH_LONG);
                            }
                        }
                    };

                    View.OnClickListener nativeListener = new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if (listener != null)
                            {
                                listener.onNativeButtonClick(v);
                            }
                        }
                    };

                    DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                            unLockAll();

                            if (listener != null)
                            {
                                listener.onDismissDialog();
                            }
                        }
                    };

                    String[] hour = DailyPreference.getInstance(getActivity()).getOperationTime().split("\\,");
                    String startHour = hour[0];
                    String endHour = hour[1];

                    String[] lunchTimes = DailyRemoteConfigPreference.getInstance(getActivity()).getRemoteConfigOperationLunchTime().split("\\,");
                    String startLunchTime = lunchTimes[0];
                    String endLunchTime = lunchTimes[1];

                    String operatingTimeMessage = getString(R.string.dialog_msg_call) //
                        + "\n" + getActivity().getResources().getString(R.string.message_consult02, startHour, endHour, startLunchTime, endLunchTime);

                    getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), operatingTimeMessage, //
                        getString(R.string.dialog_btn_call), getString(R.string.dialog_btn_text_cancel) //
                        , positiveListener, nativeListener, null, dismissListener, true);

                    if (listener != null)
                    {
                        listener.onShowDialog();
                    }
                }
            }
        };

        checkInValidOperatingTime(operatingTimeListener);
    }

    public void showNonOperatingTimeDialog(final OnCallDialogListener listener)
    {
        View.OnClickListener positiveListener = v ->
        {
            unLockAll();

            if (listener != null)
            {
                listener.onPositiveButtonClick(v);
            }
        };

        DialogInterface.OnDismissListener dismissListener = dialog ->
        {
            unLockAll();
            if (listener != null)
            {
                listener.onDismissDialog();
            }
        };

        String[] hour = DailyPreference.getInstance(getActivity()).getOperationTime().split("\\,");
        String startHour = hour[0];
        String endHour = hour[1];

        String[] lunchTimes = DailyRemoteConfigPreference.getInstance(getActivity()).getRemoteConfigOperationLunchTime().split("\\,");
        String startLunchTime = lunchTimes[0];
        String endLunchTime = lunchTimes[1];

        // 우선 점심시간의 경우 로컬에서 시간 픽스
        String noneOperatingTimeMessage = getActivity().getResources().getString( //
            R.string.dialog_message_none_operating_time, startHour, endHour, startLunchTime, endLunchTime);

        getViewInterface().showSimpleDialog(getString(R.string.dialog_information), noneOperatingTimeMessage, //
            getString(R.string.dialog_btn_text_confirm), positiveListener, dismissListener);

        if (listener != null)
        {
            listener.onShowDialog();
        }
    }
}
