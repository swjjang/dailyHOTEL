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

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.Stay;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayCampaignTags;
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam;
import com.daily.dailyhotel.repository.remote.CampaignTagRemoteImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.screen.common.calendar.stay.StayCalendarActivity;
import com.daily.dailyhotel.screen.common.dialog.call.CallDialogActivity;
import com.daily.dailyhotel.screen.common.dialog.wish.WishDialogActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.view.DailyStayCardView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.screen.hotel.preview.StayPreviewActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.Calendar;
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
    StayCampaignTagListAnalyticsImpl mAnalytics;

    private CommonRemoteImpl mCommonRemoteImpl;
    CampaignTagRemoteImpl mCampaignTagRemoteImpl;

    int mTagIndex;
    int mListCountByLongPress;
    String mTitle;
    StayBookDateTime mStayBookDateTime;
    CommonDateTime mCommonDateTime;
    StayCampaignTags mStayCampaignTags;
    PlaceViewItem mPlaceViewItemByLongPress;
    View mViewByLongPress;

    private int mWishPosition;

    public interface StayCampaignTagListAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onCampaignTagEvent(Activity activity, CampaignTag campaignTag, int listCount);

        void onEventStayClickOption(Activity activity, int index, boolean hasCoupon, boolean hasReview, boolean trueVR, boolean provideRewardSticker, boolean discount);

        void onEventStayWishClick(Activity activity, boolean wish);
    }

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

        setAnalytics(new StayCampaignTagListAnalyticsImpl());

        mCommonRemoteImpl = new CommonRemoteImpl(activity);
        mCampaignTagRemoteImpl = new CampaignTagRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayCampaignTagListAnalyticsImpl) analytics;
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

        StayBookDateTime stayBookDateTime = new StayBookDateTime();
        try
        {
            String checkInDate = intent.getStringExtra(StayCampaignTagListActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE);
            stayBookDateTime.setCheckInDateTime(checkInDate);

            if (intent.hasExtra(StayCampaignTagListActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE) == true)
            {
                stayBookDateTime.setCheckOutDateTime(intent.getStringExtra(StayCampaignTagListActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE));
            } else
            {
                stayBookDateTime.setCheckOutDateTime(checkInDate, 1);
            }
        } catch (Exception e)
        {
            ExLog.e(e.getMessage());
        }

        mStayBookDateTime = stayBookDateTime;

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

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

                    case com.daily.base.BaseActivity.RESULT_CODE_REFRESH:
                        if (data != null && data.hasExtra(StayDetailActivity.INTENT_EXTRA_DATA_WISH) == true)
                        {
                            onChangedWish(mWishPosition, data.getBooleanExtra(StayDetailActivity.INTENT_EXTRA_DATA_WISH, false));
                        } else
                        {
                            setRefresh(true);
                        }
                        break;
                }
                break;
            }

            case Constants.CODE_REQUEST_ACTIVITY_CALENDAR:
                if (resultCode == Activity.RESULT_OK)
                {
                    try
                    {
                        String checkInDate = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKIN_DATETIME);
                        String checkOutDate = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKOUT_DATETIME);

                        mStayBookDateTime.setCheckInDateTime(checkInDate);
                        mStayBookDateTime.setCheckOutDateTime(checkOutDate);
                    } catch (Exception e)
                    {
                        ExLog.e(e.getMessage());
                        return;
                    }

                    setCalendarText(mStayBookDateTime);

                    setRefresh(true);
                }
                break;

            case Constants.CODE_REQUEST_ACTIVITY_PREVIEW:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        Observable.create(new ObservableOnSubscribe<Object>()
                        {
                            @Override
                            public void subscribe(ObservableEmitter<Object> e) throws Exception
                            {
                                if (mViewByLongPress == null || mPlaceViewItemByLongPress == null)
                                {
                                    return;
                                }

                                onPlaceClick(-1, mViewByLongPress, mPlaceViewItemByLongPress, mListCountByLongPress);
                            }
                        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                        break;


                    case Constants.CODE_RESULT_ACTIVITY_REFRESH:
                        if (data != null && data.hasExtra(StayPreviewActivity.INTENT_EXTRA_DATA_WISH) == true)
                        {
                            onChangedWish(mWishPosition, data.getBooleanExtra(StayPreviewActivity.INTENT_EXTRA_DATA_WISH, false));
                        } else
                        {
                            setRefresh(true);
                        }
                        break;
                }
                break;

            case StayCampaignTagListActivity.REQUEST_CODE_WISH_DIALOG:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    case BaseActivity.RESULT_CODE_ERROR:
                        if (data != null)
                        {
                            onChangedWish(mWishPosition, data.getBooleanExtra(WishDialogActivity.INTENT_EXTRA_DATA_WISH, false));
                        }
                        break;

                    case BaseActivity.RESULT_CODE_REFRESH:
                        setRefresh(true);
                        break;
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
        addCompositeDisposable(mCommonRemoteImpl.getCommonDateTime().map(new Function<CommonDateTime, StayBookDateTime>()
        {
            @Override
            public StayBookDateTime apply(@io.reactivex.annotations.NonNull CommonDateTime commonDateTime) throws Exception
            {
                mCommonDateTime = commonDateTime;

                StayBookDateTime stayBookDateTime = mStayBookDateTime == null //
                    ? getStayBookDateTime(mCommonDateTime) : mStayBookDateTime;

                return stayBookDateTime;
            }
        }).observeOn(Schedulers.io()).flatMap(new Function<StayBookDateTime, Observable<StayCampaignTags>>()
        {
            @Override
            public Observable<StayCampaignTags> apply(@io.reactivex.annotations.NonNull StayBookDateTime stayBookDateTime) throws Exception
            {
                mStayBookDateTime = stayBookDateTime;

                String checkInDate = stayBookDateTime.getCheckInDateTime("yyyy-MM-dd");

                int nights = 1;
                try
                {
                    nights = stayBookDateTime.getNights();
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                }

                return mCampaignTagRemoteImpl.getStayCampaignTags(mTagIndex, checkInDate, nights);
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

                DailyRemoteConfigPreference.getInstance(getActivity()).setKeyRemoteConfigRewardStickerEnabled(mStayCampaignTags.activeReward);

                return makePlaceList(mStayCampaignTags.getStayList());
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<PlaceViewItem>>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull ArrayList<PlaceViewItem> placeViewItemList) throws Exception
            {
                setCampaignTagLayout(mTitle, mCommonDateTime, mStayBookDateTime, mStayCampaignTags, placeViewItemList);

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
        , StayBookDateTime stayBookDateTime, StayCampaignTags stayCampaignTags, ArrayList<PlaceViewItem> placeViewItemList)
    {
        setTitleText(title);
        setCalendarText(stayBookDateTime);

        // 서버에서 전달된 데이터가 없을때 종료된 태그로 설정!
        if (stayCampaignTags == null)
        {
            setData(null, stayBookDateTime, false);
            showFinishedCampaignTagDialog();
            //            mIsFirstUiUpdateCheck = true;
            return;
        }

        CampaignTag campaignTag = stayCampaignTags.getCampaignTag();
        if (campaignTag != null)
        {
            if (Constants.ServiceType.HOTEL.name().equalsIgnoreCase(campaignTag.serviceType) == false)
            {
                setData(null, stayBookDateTime, stayCampaignTags.activeReward);
                showReCheckConnectionDialog();
                //                mIsFirstUiUpdateCheck = true;
                return;
            }
        }

        int msgCode = stayCampaignTags.msgCode;
        // 메세지코드로 종료된 팝업일때
        if (msgCode == 200)
        {
            setData(null, stayBookDateTime, stayCampaignTags.activeReward);
            showFinishedCampaignTagDialog();
            //            mIsFirstUiUpdateCheck = true;
            return;
        }

        // 메세지 코드로 조회된 데이터가 없을때
        if (msgCode == -101)
        {
            setData(placeViewItemList, stayBookDateTime, stayCampaignTags.activeReward);

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
            setData(null, stayBookDateTime, stayCampaignTags.activeReward);
            showFinishedCampaignTagDialog();
        } else
        {
            // 일반적인 상황
            setData(placeViewItemList, stayBookDateTime, stayCampaignTags.activeReward);

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

    public void setCalendarText(StayBookDateTime stayBookDateTime)
    {
        getViewInterface().setCalendarText(getCalendarText(stayBookDateTime));
    }

    private String getCalendarText(StayBookDateTime stayBookDateTime)
    {
        if (stayBookDateTime == null)
        {
            return null;
        }

        try
        {
            String checkInDate = stayBookDateTime.getCheckInDateTime("MM.dd(EEE)");
            String checkOutDate = stayBookDateTime.getCheckOutDateTime("MM.dd(EEE)");

            int nights = stayBookDateTime.getNights();

            return String.format(Locale.KOREA, "%s - %s, %d박", checkInDate, checkOutDate, nights);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return null;
    }

    private void setData(ArrayList<PlaceViewItem> list, StayBookDateTime stayBookDateTime, boolean activeReward)
    {
        getViewInterface().setData(list, stayBookDateTime, activeReward);
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
        if (lock() == true)
        {
            return;
        }

        final int DAYS_OF_MAX_COUNT = 60;

        try
        {
            Calendar calendar = DailyCalendar.getInstance(mCommonDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT);
            String startDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);
            calendar.add(Calendar.DAY_OF_MONTH, DAYS_OF_MAX_COUNT - 1);
            String endDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            Intent intent = StayCalendarActivity.newInstance(getActivity()//
                , startDateTime, endDateTime, DAYS_OF_MAX_COUNT - 1//
                , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , AnalyticsManager.ValueType.SEARCH, true//
                , ScreenUtils.dpToPx(getActivity(), 44), true);

            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_CALENDAR);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            unLockAll();
        }
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
    public void onPlaceClick(int position, View view, PlaceViewItem placeViewItem, int count)
    {
        if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
        {
            return;
        }

        Stay stay = placeViewItem.getItem();

        mWishPosition = position;

        StayDetailAnalyticsParam analyticsParam = new StayDetailAnalyticsParam();
        analyticsParam.setAddressAreaName(stay.addressSummary);
        analyticsParam.discountPrice = stay.discountPrice;
        analyticsParam.price = stay.price;
        analyticsParam.setShowOriginalPriceYn(analyticsParam.price, analyticsParam.discountPrice);
        analyticsParam.setRegion(null);
        analyticsParam.entryPosition = stay.entryPosition;
        analyticsParam.totalListCount = count;
        analyticsParam.isDailyChoice = stay.dailyChoice;
        analyticsParam.gradeName = stay.grade.getName(getActivity());

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

            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), ((DailyStayCardView) view).getOptionsCompat());

            Intent intent = StayDetailActivity.newInstance(getActivity() //
                , stay.index, stay.name, stay.imageUrl, stay.discountPrice//
                , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , true, StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_LIST, analyticsParam);

            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_STAY_DETAIL, optionsCompat.toBundle());
        } else
        {
            Intent intent = StayDetailActivity.newInstance(getActivity() //
                , stay.index, stay.name, stay.imageUrl, stay.discountPrice//
                , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , false, StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE, analyticsParam);

            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_STAY_DETAIL);

            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
        }

        mAnalytics.onEventStayClickOption(getActivity(), stay.index, DailyTextUtils.isTextEmpty(stay.couponDiscountText) == false//
            , stay.reviewCount > 0, stay.trueVR == true//
            , DailyRemoteConfigPreference.getInstance(getActivity()).isKeyRemoteConfigRewardStickerEnabled() && stay.provideRewardSticker//
            , stay.discountRate > 0);
    }

    @Override
    public void onPlaceLongClick(int position, View view, PlaceViewItem placeViewItem, int count)
    {
        if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
        {
            return;
        }

        getViewInterface().setBlurVisibility(getActivity(), true);

        Stay stay = placeViewItem.getItem();

        mWishPosition = position;

        // 기존 데이터를 백업한다.
        mViewByLongPress = view;
        mPlaceViewItemByLongPress = placeViewItem;
        mListCountByLongPress = count;

        Intent intent = StayPreviewActivity.newInstance(getActivity() //
            , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT) //
            , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , stay.index, stay.name, stay.discountPrice, stay.grade.name());

        startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PREVIEW);
    }

    @Override
    public void onWishClick(int position, PlaceViewItem placeViewItem)
    {
        if (lock() == true)
        {
            return;
        }

        Stay stay = placeViewItem.getItem();

        mWishPosition = position;

        boolean currentWish = stay.myWish;

        if (DailyHotel.isLogin() == true)
        {
            onChangedWish(position, !currentWish);
        }

        startActivityForResult(WishDialogActivity.newInstance(getActivity(), Constants.ServiceType.HOTEL//
            , stay.index, !currentWish, position, AnalyticsManager.Screen.DAILYHOTEL_LIST), StayCampaignTagListActivity.REQUEST_CODE_WISH_DIALOG);

        mAnalytics.onEventStayWishClick(getActivity(), !currentWish);
    }

    StayBookDateTime getStayBookDateTime(CommonDateTime commonDateTime)
    {
        if (commonDateTime == null)
        {
            return mStayBookDateTime;
        }

        StayBookDateTime stayBookDateTime = new StayBookDateTime();

        try
        {
            stayBookDateTime.setCheckInDateTime(commonDateTime.dailyDateTime);
            stayBookDateTime.setCheckOutDateTime(commonDateTime.dailyDateTime, 1);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return stayBookDateTime;
    }

    private void onChangedWish(int position, boolean wish)
    {
        if (position < 0)
        {
            return;
        }

        PlaceViewItem placeViewItem = getViewInterface().getItem(position);

        if (placeViewItem == null)
        {
            return;
        }

        Stay stay = placeViewItem.getItem();
        stay.myWish = wish;
        getViewInterface().notifyWishChanged(position, wish);
    }
}
