package com.daily.dailyhotel.screen.home.campaigntag.gourmet;

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
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetCampaignTags;
import com.daily.dailyhotel.entity.GourmetSuggest;
import com.daily.dailyhotel.parcel.analytics.GourmetDetailAnalyticsParam;
import com.daily.dailyhotel.repository.remote.CampaignTagRemoteImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.screen.common.dialog.call.CallDialogActivity;
import com.daily.dailyhotel.screen.common.dialog.wish.WishDialogActivity;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailActivity;
import com.daily.dailyhotel.screen.home.search.gourmet.research.ResearchGourmetActivity;
import com.daily.dailyhotel.view.DailyGourmetCardView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCalendarActivity;
import com.twoheart.dailyhotel.screen.gourmet.preview.GourmetPreviewActivity;
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
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android_sam on 2017. 8. 4..
 */

@Deprecated
public class GourmetCampaignTagListPresenter //
    extends BaseExceptionPresenter<GourmetCampaignTagListActivity, GourmetCampaignTagListInterface> //
    implements GourmetCampaignTagListView.OnEventListener
{
    GourmetCampaignTagListAnalyticsImpl mAnalytics;

    private CommonRemoteImpl mCommonRemoteImpl;
    CampaignTagRemoteImpl mCampaignTagRemoteImpl;

    int mTagIndex;
    int mListCountByLongPress;
    String mTitle;
    GourmetBookDateTime mGourmetBookDateTime;
    CommonDateTime mCommonDateTime;
    GourmetCampaignTags mGourmetCampaignTags;
    PlaceViewItem mPlaceViewItemByLongPress;
    View mViewByLongPress;

    private int mWishPosition;

    public interface GourmetCampaignTagListAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onCampaignTagEvent(Activity activity, CampaignTag campaignTag, int listCount);

        void onEventGourmetClickOption(Activity activity, int index, boolean hasCoupon, boolean hasReview, boolean trueVR, boolean discount);

        void onEventGourmetWishClick(Activity activity, boolean wish);
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

        setAnalytics(new GourmetCampaignTagListAnalyticsImpl());

        mCommonRemoteImpl = new CommonRemoteImpl(activity);
        mCampaignTagRemoteImpl = new CampaignTagRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (GourmetCampaignTagListAnalyticsImpl) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mTagIndex = intent.getIntExtra(GourmetCampaignTagListActivity.INTENT_EXTRA_DATA_INDEX, -1);
        mTitle = intent.getStringExtra(GourmetCampaignTagListActivity.INTENT_EXTRA_DATA_TITLE);

        GourmetBookDateTime gourmetBookDateTime = new GourmetBookDateTime();
        try
        {
            String visitDate = intent.getStringExtra(GourmetCampaignTagListActivity.INTENT_EXTRA_DATA_VISIT_DATE);
            gourmetBookDateTime.setVisitDateTime(visitDate);
        } catch (Exception e)
        {
            ExLog.e(e.getMessage());
        }

        mGourmetBookDateTime = gourmetBookDateTime;

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

        if (getViewInterface() != null && getViewInterface().isBlurVisible() == true)
        {
            getViewInterface().setBlurVisible(getActivity(), false);
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
                        if (data != null && data.hasExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_WISH) == true)
                        {
                            onChangedWish(mWishPosition, data.getBooleanExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_WISH, false));
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
                        String visitDate = data.getStringExtra(GourmetCalendarActivity.INTENT_EXTRA_DATA_VISIT_DATE);

                        if (DailyTextUtils.isTextEmpty(visitDate) == true)
                        {
                            return;
                        }

                        GourmetBookDateTime gourmetBookDateTime = new GourmetBookDateTime();
                        gourmetBookDateTime.setVisitDateTime(visitDate);

                        mGourmetBookDateTime = gourmetBookDateTime;
                    } catch (Exception e)
                    {
                        ExLog.e(e.getMessage());
                        return;
                    }

                    setCalendarText(mGourmetBookDateTime);

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
                        if (data != null && data.hasExtra(GourmetPreviewActivity.INTENT_EXTRA_DATA_WISH) == true)
                        {
                            onChangedWish(mWishPosition, data.getBooleanExtra(GourmetPreviewActivity.INTENT_EXTRA_DATA_WISH, false));
                        } else
                        {
                            setRefresh(true);
                        }
                        break;
                }
                break;

            case GourmetCampaignTagListActivity.REQUEST_CODE_WISH_DIALOG:
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

            case GourmetCampaignTagListActivity.REQUEST_CODE_RESEARCH:
                onResearchActivityResult(resultCode, data);
                break;
        }
    }

    private void onResearchActivityResult(int resultCode, Intent intent)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:
                if (intent != null)
                {
                    setResult(Constants.CODE_RESULT_ACTIVITY_SEARCH_GOURMET, intent);
                    onBackClick();
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
        addCompositeDisposable(mCommonRemoteImpl.getCommonDateTime().map(new Function<CommonDateTime, GourmetBookDateTime>()
        {
            @Override
            public GourmetBookDateTime apply(@io.reactivex.annotations.NonNull CommonDateTime commonDateTime) throws Exception
            {
                mCommonDateTime = commonDateTime;

                GourmetBookDateTime gourmetBookDateTime = mGourmetBookDateTime == null //
                    ? getGourmetBookDateTime(mCommonDateTime) : mGourmetBookDateTime;

                return gourmetBookDateTime;
            }
        }).observeOn(Schedulers.io()).flatMap(new Function<GourmetBookDateTime, Observable<GourmetCampaignTags>>()
        {
            @Override
            public Observable<GourmetCampaignTags> apply(@io.reactivex.annotations.NonNull GourmetBookDateTime gourmetBookDateTime) throws Exception
            {
                mGourmetBookDateTime = gourmetBookDateTime;

                String visitDate = gourmetBookDateTime.getVisitDateTime("yyyy-MM-dd");

                return mCampaignTagRemoteImpl.getGourmetCampaignTags(mTagIndex, visitDate);
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

                //                return makePlaceList(mGourmetCampaignTags.getGourmetList()); // 기존 코드
                return new ArrayList<>();
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<PlaceViewItem>>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull ArrayList<PlaceViewItem> placeViewItemList) throws Exception
            {
                setCampaignTagLayout(mTitle, mCommonDateTime, mGourmetBookDateTime, mGourmetCampaignTags, placeViewItemList);

                unLockAll();

                try
                {
                    int size = mGourmetCampaignTags.getGourmetList() == null ? 0 : mGourmetCampaignTags.getGourmetList().size();

                    mAnalytics.onCampaignTagEvent(getActivity() //
                        , mGourmetCampaignTags.getCampaignTag() //
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
        , GourmetBookDateTime gourmetBookDateTime, GourmetCampaignTags gourmetCampaignTags, ArrayList<PlaceViewItem> placeViewItemList)
    {
        setTitleText(title);
        setCalendarText(gourmetBookDateTime);

        // 서버에서 전달된 데이터가 없을때 종료된 태그로 설정!
        if (gourmetCampaignTags == null)
        {
            setData(null, gourmetBookDateTime);
            showFinishedCampaignTagDialog();
            //            mIsFirstUiUpdateCheck = true;
            return;
        }

        CampaignTag campaignTag = gourmetCampaignTags.getCampaignTag();
        if (campaignTag != null)
        {
            // 진입한 화면과 서버에서 내려받은 서비스 타입이 다른 경우 연결 체크 팝업 후 종료!
            if (Constants.ServiceType.GOURMET.name().equalsIgnoreCase(campaignTag.serviceType) == false)
            {
                setData(null, gourmetBookDateTime);
                showReCheckConnectionDialog();
                //                mIsFirstUiUpdateCheck = true;
                return;
            }
        }

        int msgCode = gourmetCampaignTags.msgCode;
        // 메세지코드로 종료된 팝업일때
        if (msgCode == 200)
        {
            setData(null, gourmetBookDateTime);
            showFinishedCampaignTagDialog();
            //            mIsFirstUiUpdateCheck = true;
            return;
        }

        // 메세지 코드로 조회된 데이터가 없을때
        if (msgCode == -101)
        {
            setData(placeViewItemList, gourmetBookDateTime);
            //
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
            endTime = DailyCalendar.convertDate(gourmetCampaignTags.getCampaignTag().endDate, DailyCalendar.ISO_8601_FORMAT).getTime();
        } catch (Exception e)
        {
            ExLog.d(e.toString());

            currentTime = 0;
            endTime = -1;
        }

        if (endTime < currentTime)
        {
            // 시간 체크시 종료 된 캠페인 태그 일때
            setData(null, gourmetBookDateTime);
            showFinishedCampaignTagDialog();
        } else
        {
            // 일반적인 상황
            setData(placeViewItemList, gourmetBookDateTime);

            //            ArrayList<Gourmet> list = gourmetCampaignTags.getGourmetList();
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

    public String getTitleText(GourmetCampaignTags gourmetCampaignTags)
    {
        if (gourmetCampaignTags == null)
        {
            return mTitle;
        }

        CampaignTag campaignTag = gourmetCampaignTags.getCampaignTag();

        return (campaignTag == null || DailyTextUtils.isTextEmpty(campaignTag.campaignTag) == true) ? mTitle : campaignTag.campaignTag;
    }

    public void setCalendarText(GourmetBookDateTime gourmetBookDateTime)
    {
        getViewInterface().setCalendarText(getCalendarText(gourmetBookDateTime));
    }

    private String getCalendarText(GourmetBookDateTime gourmetBookDateTime)
    {
        if (gourmetBookDateTime == null)
        {
            return null;
        }

        try
        {
            return gourmetBookDateTime.getVisitDateTime("MM.dd(EEE)");
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return null;
    }

    private void setData(ArrayList<PlaceViewItem> list, GourmetBookDateTime gourmetBookDateTime)
    {
        getViewInterface().setData(list, gourmetBookDateTime);
    }

    ArrayList<PlaceViewItem> makePlaceList(ArrayList<Gourmet> gourmetList)
    {
        ArrayList<PlaceViewItem> placeViewItemList = new ArrayList<>();

        if (gourmetList == null || gourmetList.size() == 0)
        {
            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_EMPTY_VIEW, null));
        } else
        {
            int entryPosition = 0;

            for (Gourmet gourmet : gourmetList)
            {
                gourmet.entryPosition = entryPosition++;

                placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, gourmet));
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

        Intent intent = GourmetCalendarActivity.newInstance(getActivity(), todayDateTime //
            , mGourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT) //
            , GourmetCalendarActivity.DEFAULT_CALENDAR_DAY_OF_MAX_COUNT //
            , AnalyticsManager.ValueType.SEARCH, true, true);
        startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_CALENDAR);
    }

    @Override
    public void onResearchClick()
    {
        if (lock() == true)
        {
            return;
        }

        try
        {
            TodayDateTime todayDateTime = mCommonDateTime.getTodayDateTime();
            GourmetBookingDay gourmetBookingDay = mGourmetBookDateTime.getGourmetBookingDay();

            GourmetSuggest.CampaignTag suggestItem = new GourmetSuggest.CampaignTag();
            suggestItem.index = mTagIndex;
            suggestItem.name = mTitle;

            GourmetSuggest suggest = new GourmetSuggest(GourmetSuggest.MenuType.CAMPAIGN_TAG, suggestItem);

            startActivityForResult(ResearchGourmetActivity.newInstance(getActivity(), todayDateTime.openDateTime, todayDateTime.closeDateTime//
                , todayDateTime.currentDateTime, todayDateTime.dailyDateTime//
                , mGourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , suggest), GourmetCampaignTagListActivity.REQUEST_CODE_RESEARCH);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            unLockAll();
        }
    }

    @Override
    public void onCallClick()
    {
        startActivityForResult(CallDialogActivity.newInstance(getActivity()), GourmetCampaignTagListActivity.REQUEST_CODE_CALL);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onPlaceClick(int position, View view, PlaceViewItem placeViewItem, int count)
    {
        if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
        {
            return;
        }

        Gourmet gourmet = placeViewItem.getItem();

        mWishPosition = position;

        // --> 추후에 정리되면 메소드로 수정
        GourmetDetailAnalyticsParam analyticsParam = new GourmetDetailAnalyticsParam();
        analyticsParam.price = gourmet.price;
        analyticsParam.discountPrice = gourmet.discountPrice;
        analyticsParam.setShowOriginalPriceYn(analyticsParam.price, analyticsParam.discountPrice);
        analyticsParam.setProvince(null);
        analyticsParam.entryPosition = gourmet.entryPosition;
        analyticsParam.totalListCount = count;
        analyticsParam.isDailyChoice = gourmet.isDailyChoice;
        analyticsParam.setAddressAreaName(gourmet.addressSummary);

        // <-- 추후에 정리되면 메소드로 수정

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

            ActivityOptionsCompat optionsCompat;
            Intent intent;

            if (view instanceof DailyGourmetCardView == true)
            {
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), ((DailyGourmetCardView) view).getOptionsCompat());

                intent = GourmetDetailActivity.newInstance(getActivity() //
                    , gourmet.index, gourmet.name, gourmet.imageUrl, gourmet.discountPrice//
                    , mGourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT)//
                    , gourmet.category, gourmet.isSoldOut, false, false, true//
                    , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_LIST//
                    , analyticsParam);
            } else
            {
                View simpleDraweeView = view.findViewById(R.id.imageView);
                View nameTextView = view.findViewById(R.id.nameTextView);
                View gradientTopView = view.findViewById(R.id.gradientTopView);
                View gradientBottomView = view.findViewById(R.id.gradientView);

                intent = GourmetDetailActivity.newInstance(getActivity() //
                    , gourmet.index, gourmet.name, gourmet.imageUrl, gourmet.discountPrice//
                    , mGourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT)//
                    , gourmet.category, gourmet.isSoldOut, false, false, true//
                    , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_MAP//
                    , analyticsParam);

                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),//
                    android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)),//
                    android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view)),//
                    android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view)));
            }

            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_GOURMET_DETAIL, optionsCompat.toBundle());
        } else
        {
            Intent intent = GourmetDetailActivity.newInstance(getActivity() //
                , gourmet.index, gourmet.name, gourmet.imageUrl, gourmet.discountPrice//
                , mGourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , gourmet.category, gourmet.isSoldOut, false, false, false//
                , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE//
                , analyticsParam);

            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_GOURMET_DETAIL);

            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
        }

        mAnalytics.onEventGourmetClickOption(getActivity(), gourmet.index//
            , DailyTextUtils.isTextEmpty(gourmet.couponDiscountText) == false//
            , gourmet.reviewCount > 0, false, gourmet.discountRate > 0);
    }

    @Override
    public void onPlaceLongClick(int position, View view, PlaceViewItem placeViewItem, int count)
    {
        if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
        {
            return;
        }

        getViewInterface().setBlurVisible(getActivity(), true);

        Gourmet gourmet = placeViewItem.getItem();

        mWishPosition = position;

        // 기존 데이터를 백업한다.
        mViewByLongPress = view;
        mPlaceViewItemByLongPress = placeViewItem;
        mListCountByLongPress = count;

        Intent intent = GourmetPreviewActivity.newInstance(getActivity() //
            , mGourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT), gourmet);

        startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PREVIEW);
    }

    @Override
    public void onWishClick(int position, PlaceViewItem placeViewItem)
    {
        if (lock() == true)
        {
            return;
        }

        Gourmet gourmet = placeViewItem.getItem();

        mWishPosition = position;

        boolean currentWish = gourmet.myWish;

        if (DailyHotel.isLogin() == true)
        {
            onChangedWish(position, !currentWish);
        }

        startActivityForResult(WishDialogActivity.newInstance(getActivity(), Constants.ServiceType.GOURMET//
            , gourmet.index, !currentWish, position, AnalyticsManager.Screen.DAILYGOURMET_DETAIL), GourmetCampaignTagListActivity.REQUEST_CODE_WISH_DIALOG);

        mAnalytics.onEventGourmetWishClick(getActivity(), !currentWish);
    }


    GourmetBookDateTime getGourmetBookDateTime(CommonDateTime commonDateTime)
    {
        if (commonDateTime == null)
        {
            return mGourmetBookDateTime;
        }

        GourmetBookDateTime gourmetBookDateTime = new GourmetBookDateTime();

        try
        {
            gourmetBookDateTime.setVisitDateTime(commonDateTime.dailyDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return gourmetBookDateTime;
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

        Gourmet gourmet = placeViewItem.getItem();
        gourmet.myWish = wish;
        getViewInterface().notifyWishChanged(position, wish);
    }
}
