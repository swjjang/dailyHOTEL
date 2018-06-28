package com.twoheart.dailyhotel.screen.home.collection;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.util.SparseArray;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.parcel.analytics.GourmetDetailAnalyticsParam;
import com.daily.dailyhotel.screen.common.calendar.gourmet.GourmetCalendarActivity;
import com.daily.dailyhotel.screen.common.dialog.wish.WishDialogActivity;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailActivity;
import com.daily.dailyhotel.screen.home.gourmet.preview.GourmetPreviewActivity;
import com.daily.dailyhotel.view.DailyGourmetCardView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.network.model.Recommendation;
import com.twoheart.dailyhotel.network.model.RecommendationGourmet;
import com.twoheart.dailyhotel.network.model.RecommendationPlace;
import com.twoheart.dailyhotel.network.model.RecommendationPlaceList;
import com.twoheart.dailyhotel.network.model.Sticker;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

public class CollectionGourmetActivity extends CollectionBaseActivity
{
    protected static final String INTENT_EXTRA_DATA_TYPE = "type";
    protected static final String INTENT_EXTRA_DATA_INDEX = "index";
    protected static final String INTENT_EXTRA_DATA_IMAGE_URL = "imageUrl";
    protected static final String INTENT_EXTRA_DATA_TITLE = "title";
    protected static final String INTENT_EXTRA_DATA_SUBTITLE = "subTitle";
    protected static final String INTENT_EXTRA_DATA_VISIT_DATE = "visitDate";
    protected static final String INTENT_EXTRA_DATA_AFTER_DAY = "afterDay";

    private static final int TYPE_DEFAULT = 0;
    private static final int TYPE_DATE = 1;
    private static final int TYPE_AFTER_DAY = 2;

    private GourmetBookingDay mStartGourmetBookingDay;
    private int mType;
    private int mAfterDay;

    /**
     * @param context
     * @param index
     * @param visitDateTime ISO-8601
     * @return
     */
    public static Intent newInstance(Context context, int index, String visitDateTime)
    {
        Intent intent = new Intent(context, CollectionGourmetActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_TYPE, TYPE_DATE);
        intent.putExtra(INTENT_EXTRA_DATA_INDEX, index);
        intent.putExtra(INTENT_EXTRA_DATA_VISIT_DATE, visitDateTime);

        return intent;
    }

    public static Intent newInstance(Context context, int index, int afterDay)
    {
        Intent intent = new Intent(context, CollectionGourmetActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_TYPE, TYPE_AFTER_DAY);
        intent.putExtra(INTENT_EXTRA_DATA_INDEX, index);
        intent.putExtra(INTENT_EXTRA_DATA_AFTER_DAY, afterDay);

        return intent;
    }

    public static Intent newInstance(Context context, int index, String imageUrl, String title, String subTitle //
        , String visitDateTime, boolean isUsedMultiTransition)
    {
        Intent intent = new Intent(context, CollectionGourmetActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_TYPE, TYPE_DEFAULT);
        intent.putExtra(INTENT_EXTRA_DATA_INDEX, index);
        intent.putExtra(INTENT_EXTRA_DATA_IMAGE_URL, imageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, title);
        intent.putExtra(INTENT_EXTRA_DATA_SUBTITLE, subTitle);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_USED_MULTITRANSITIOIN, isUsedMultiTransition);
        intent.putExtra(INTENT_EXTRA_DATA_VISIT_DATE, visitDateTime);

        return intent;
    }

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
        mIsUsedMultiTransition = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_IS_USED_MULTITRANSITIOIN, false);

        if (mRecommendationIndex <= 0)
        {
            finish();
            return;
        }

        mType = intent.getIntExtra(INTENT_EXTRA_DATA_TYPE, TYPE_DEFAULT);

        String title = null;
        String subTitle = null;
        String imageUrl = null;

        switch (mType)
        {
            case TYPE_DEFAULT:
            {
                title = intent.getStringExtra(INTENT_EXTRA_DATA_TITLE);
                subTitle = intent.getStringExtra(INTENT_EXTRA_DATA_SUBTITLE);
                imageUrl = intent.getStringExtra(INTENT_EXTRA_DATA_IMAGE_URL);

                String visitDateTime = intent.getStringExtra(INTENT_EXTRA_DATA_VISIT_DATE);

                if (DailyTextUtils.isTextEmpty(visitDateTime) == false)
                {
                    try
                    {
                        mStartGourmetBookingDay = new GourmetBookingDay();
                        mStartGourmetBookingDay.setVisitDay(visitDateTime);
                    } catch (Exception e)
                    {
                        mStartGourmetBookingDay = null;
                    }
                }
                break;
            }

            case TYPE_DATE:
            {
                String visitDateTime = intent.getStringExtra(INTENT_EXTRA_DATA_VISIT_DATE);

                if (DailyTextUtils.isTextEmpty(visitDateTime) == false)
                {
                    try
                    {
                        mStartGourmetBookingDay = new GourmetBookingDay();
                        mStartGourmetBookingDay.setVisitDay(visitDateTime);
                    } catch (Exception e)
                    {
                        mStartGourmetBookingDay = null;
                    }
                }
                break;
            }

            case TYPE_AFTER_DAY:
            {
                mAfterDay = intent.getIntExtra(INTENT_EXTRA_DATA_AFTER_DAY, 0);
                break;
            }
        }

        mCollectionBaseLayout = new CollectionGourmetLayout(this, mOnEventListener);

        setContentView(mCollectionBaseLayout.onCreateView(R.layout.activity_collection_search));

        boolean isDeepLink = DailyTextUtils.isTextEmpty(title, subTitle, imageUrl);

        mCollectionBaseLayout.setUsedMultiTransition(mIsUsedMultiTransition);

        if (isDeepLink == false && mIsUsedMultiTransition == true)
        {
            mCollectionBaseLayout.setTitleLayoutData(title, subTitle, imageUrl);
            mCollectionBaseLayout.notifyChangedTitleLayout();

            initTransition();
        } else
        {
            mCollectionBaseLayout.setTitleLayoutData(title, subTitle, imageUrl);
            mCollectionBaseLayout.notifyChangedTitleLayout();

            lockUI();

            requestCommonDateTime();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockUI();

        switch (requestCode)
        {
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

                    case com.daily.base.BaseActivity.RESULT_CODE_REFRESH:
                        if (data == null)
                        {
                            lockUI();

                            requestCommonDateTime();
                        } else
                        {
                            if (data.hasExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_CHANGED_PRICE) == true//
                                || data.hasExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_SOLD_OUT) == true)
                            {
                                lockUI();

                                requestCommonDateTime();
                            } else
                            {
                                onChangedWish(mWishPosition, data.getBooleanExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_WISH, false));
                            }
                        }
                        break;
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_CALENDAR:
                onCalendarActivityResult(resultCode, data);
                break;

            case CODE_REQUEST_ACTIVITY_PREVIEW:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        Observable.create(new ObservableOnSubscribe<Object>()
                        {
                            @Override
                            public void subscribe(ObservableEmitter<Object> e) throws Exception
                            {
                                onPlaceDetailClickByLongPress(mViewByLongPress, mPlaceViewItemByLongPress, mListCountByLongPress);
                            }
                        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                        break;

                    case com.daily.base.BaseActivity.RESULT_CODE_REFRESH:
                        if (data != null && data.hasExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_WISH) == true)
                        {
                            onChangedWish(mWishPosition, data.getBooleanExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_WISH, false));
                        } else
                        {
                            lockUI();

                            requestCommonDateTime();
                        }
                        break;
                }
                break;

            case Constants.CODE_REQUEST_ACTIVITY_WISH_DIALOG:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    case BaseActivity.RESULT_CODE_ERROR:
                        if (data != null)
                        {
                            onChangedWish(mWishPosition, data.getBooleanExtra(WishDialogActivity.INTENT_EXTRA_DATA_WISH, false));
                        }
                        break;

                    case com.daily.base.BaseActivity.RESULT_CODE_REFRESH:
                        lockUI();

                        requestCommonDateTime();
                        break;
                }
                break;
        }
    }

    @Override
    protected void requestRecommendationPlaceList(PlaceBookingDay placeBookingDay)
    {
        if (placeBookingDay == null)
        {
            return;
        }

        mCollectionBaseLayout.setListScrollTop();

        GourmetBookingDay gourmetBookingDay = (GourmetBookingDay) placeBookingDay;

        String period = gourmetBookingDay.getVisitDay("yyyy-MM-dd");

        addCompositeDisposable(Observable.zip(mRecommendationRemoteImpl.getRecommendationList() //
            , mRecommendationRemoteImpl.getRecommendationGourmetList(mRecommendationIndex, period, 0) //
            , new BiFunction<List<Recommendation>, RecommendationPlaceList<RecommendationGourmet>, ArrayList<PlaceViewItem>>()
            {
                @Override
                public ArrayList<PlaceViewItem> apply(List<Recommendation> recommendationList, RecommendationPlaceList<RecommendationGourmet> recommendationPlaceList) throws Exception
                {
                    Recommendation recommendation = recommendationPlaceList.recommendation;
                    mCollectionBaseLayout.setTitleLayoutData(recommendation.title, recommendation.subtitle //
                        , ScreenUtils.getResolutionImageUrl(CollectionGourmetActivity.this //
                            , recommendation.defaultImageUrl, recommendation.lowResolutionImageUrl));

                    long currentTime, endTime;
                    try
                    {
                        currentTime = DailyCalendar.convertDate(mCommonDateTime.currentDateTime, DailyCalendar.ISO_8601_FORMAT).getTime();
                        endTime = DailyCalendar.convertDate(recommendation.endedAt, DailyCalendar.ISO_8601_FORMAT).getTime();
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());

                        currentTime = 0;
                        endTime = -1;
                    }

                    mIsOverShowDate = endTime < currentTime;

                    ArrayList<RecommendationGourmet> gourmetList = new ArrayList<>(recommendationPlaceList.items);
                    ArrayList<PlaceViewItem> placeViewItems = makePlaceList( //
                        recommendationPlaceList.imageBaseUrl, (mIsOverShowDate ? null : gourmetList), recommendationPlaceList.stickers);

                    for (Recommendation item : recommendationList)
                    {
                        if (item.idx == recommendation.idx)
                        {
                            recommendationList.remove(item);
                            break;
                        }
                    }

                    placeViewItems.add(new PlaceViewItem(PlaceViewItem.TYPE_RECOMMEND_VIEW, recommendationList));

                    return placeViewItems;
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<PlaceViewItem>>()
        {
            @Override
            public void accept(ArrayList<PlaceViewItem> placeViewItems) throws Exception
            {
                mCollectionBaseLayout.notifyChangedTitleLayout();
                onPlaceList(mIsOverShowDate, placeViewItems, false);
                unLockUI();
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
    protected String getCalendarDate(PlaceBookingDay placeBookingDay)
    {
        if (placeBookingDay == null)
        {
            return null;
        }

        return ((GourmetBookingDay) placeBookingDay).getVisitDay("yyyy.MM.dd(EEE)");
    }

    @Override
    protected void setPlaceBookingDay(CommonDateTime commonDateTime)
    {
        if (commonDateTime == null)
        {
            return;
        }

        try
        {
            GourmetBookingDay gourmetBookingDay = new GourmetBookingDay();
            gourmetBookingDay.setVisitDay(commonDateTime.dailyDateTime);

            switch (mType)
            {
                case TYPE_DEFAULT:
                    if (mStartGourmetBookingDay != null)
                    {
                        try
                        {
                            int startVisitDay = Integer.parseInt(mStartGourmetBookingDay.getVisitDay("yyyyMMdd"));
                            int dailyVisitDay = Integer.parseInt(gourmetBookingDay.getVisitDay("yyyyMMdd"));

                            // 데일리타임 이후 날짜인 경우에는
                            if (startVisitDay >= dailyVisitDay)
                            {
                                gourmetBookingDay.setVisitDay(mStartGourmetBookingDay.getVisitDay(DailyCalendar.ISO_8601_FORMAT));
                            }
                        } catch (Exception e)
                        {
                            ExLog.e(e.toString());
                        }

                        mStartGourmetBookingDay = null;
                    }
                    break;

                case TYPE_DATE:
                    if (mStartGourmetBookingDay != null)
                    {
                        try
                        {
                            int startVisitDay = Integer.parseInt(mStartGourmetBookingDay.getVisitDay("yyyyMMdd"));
                            int dailyVisitDay = Integer.parseInt(gourmetBookingDay.getVisitDay("yyyyMMdd"));

                            // 데일리타임 이후 날짜인 경우에는
                            if (startVisitDay >= dailyVisitDay)
                            {
                                gourmetBookingDay.setVisitDay(mStartGourmetBookingDay.getVisitDay(DailyCalendar.ISO_8601_FORMAT));
                            }
                        } catch (Exception e)
                        {
                            ExLog.e(e.toString());
                        }

                        mStartGourmetBookingDay = null;
                    }

                    mType = TYPE_DEFAULT;
                    break;

                case TYPE_AFTER_DAY:
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

                    mType = TYPE_DEFAULT;
                    break;
            }


            mPlaceBookingDay = gourmetBookingDay;
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    protected void onCalendarActivityResult(int resultCode, Intent intent)
    {
        if (resultCode == RESULT_OK)
        {
            String visitDateTime = intent.getStringExtra(GourmetCalendarActivity.INTENT_EXTRA_DATA_VISIT_DATETIME);

            if (DailyTextUtils.isTextEmpty(visitDateTime) == true)
            {
                return;
            }

            try
            {
                mPlaceBookingDay = new GourmetBookingDay(visitDateTime);

                mCollectionBaseLayout.setCalendarText(getCalendarDate(mPlaceBookingDay));

                lockUI();

                requestRecommendationPlaceList(mPlaceBookingDay);
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
        }
    }

    @Override
    protected void startCalendarActivity(CommonDateTime commonDateTime, PlaceBookingDay placeBookingDay)
    {
        if (commonDateTime == null || placeBookingDay == null)
        {
            return;
        }

        final int DAYS_OF_MAX_COUNT = 30;

        try
        {
            Calendar calendar = DailyCalendar.getInstance(commonDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT);
            String startDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);
            calendar.add(Calendar.DAY_OF_MONTH, DAYS_OF_MAX_COUNT - 1);
            String endDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            GourmetBookingDay gourmetBookingDay = (GourmetBookingDay) placeBookingDay;

            Intent intent = com.daily.dailyhotel.screen.common.calendar.gourmet.GourmetCalendarActivity.newInstance(this//
                , startDateTime, endDateTime, gourmetBookingDay.getVisitDay(DailyCalendar.ISO_8601_FORMAT)//
                , AnalyticsManager.ValueType.SEARCH, true//
                , 0, true);

            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_CALENDAR);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            unLockUI();
        }
    }

    @Override
    protected String getSectionTitle(int count)
    {
        return getString(R.string.label_count_gourmet, count);
    }

    @Override
    protected void onCommonDateTime(CommonDateTime commonDateTime)
    {
        mCommonDateTime = commonDateTime;

        setPlaceBookingDay(commonDateTime);

        mCollectionBaseLayout.setCalendarText(getCalendarDate(mPlaceBookingDay));

        requestRecommendationPlaceList(mPlaceBookingDay);
    }

    @Override
    protected ArrayList<PlaceViewItem> makePlaceList(String imageBaseUrl, List<? extends RecommendationPlace> placeList, List<Sticker> stickerList)
    {
        ArrayList<PlaceViewItem> placeViewItemList = new ArrayList<>();

        // 빈공간
        placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_HEADER_VIEW, null));

        if (placeList == null || placeList.size() == 0)
        {
            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_EMPTY_VIEW, null));
        } else
        {
            // 개수 넣기
            //            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_SECTION, getSectionTitle(placeList.size())));

            SparseArray<String> stickerSparseArray = new SparseArray<>();
            if (stickerList != null && stickerList.size() > 0)
            {
                boolean isLowResource = false;

                if (ScreenUtils.getScreenWidth(this) <= Sticker.DEFAULT_SCREEN_WIDTH)
                {
                    isLowResource = true;
                }

                for (Sticker sticker : stickerList)
                {
                    String url;

                    if (isLowResource == true)
                    {
                        url = sticker.lowResolutionImageUrl;
                    } else
                    {
                        url = sticker.defaultImageUrl;
                    }

                    stickerSparseArray.append(sticker.index, url);
                }
            }

            int entryPosition = 0;

            for (RecommendationPlace place : placeList)
            {
                place.imageUrl = imageBaseUrl + place.imageUrl;

                if (place.stickerIdx != null)
                {
                    place.stickerUrl = stickerSparseArray.get(place.stickerIdx);
                }

                place.entryPosition = entryPosition++;
                placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, place));
            }

            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, null));
        }

        return placeViewItemList;
    }

    @Override
    protected void onPlaceDetailClickByLongPress(View view, PlaceViewItem placeViewItem, int listCount)
    {
        if (view == null || placeViewItem == null)
        {
            return;
        }

        mOnEventListener.onPlaceClick(mWishPosition, view, placeViewItem, listCount);
    }

    @Override
    protected void onChangedWish(int position, boolean wish)
    {
        if (position < 0)
        {
            return;
        }

        if (mCollectionBaseLayout == null)
        {
            Util.restartApp(this);
            return;
        }

        PlaceViewItem placeViewItem = mCollectionBaseLayout.getItem(position);

        if (placeViewItem == null)
        {
            return;
        }

        RecommendationGourmet recommendationGourmet = placeViewItem.getItem();
        recommendationGourmet.myWish = wish;
        mCollectionBaseLayout.notifyWishChanged(position, wish);
    }

    private CollectionStayLayout.OnEventListener mOnEventListener = new CollectionBaseLayout.OnEventListener()
    {
        @Override
        public void onCalendarClick()
        {
            startCalendarActivity(mCommonDateTime, mPlaceBookingDay);
        }

        @SuppressLint("RestrictedApi")
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onPlaceClick(int position, View view, PlaceViewItem placeViewItem, int count)
        {
            if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
            {
                return;
            }

            RecommendationGourmet recommendationGourmet = placeViewItem.getItem();

            mWishPosition = position;

            // --> 추후에 정리되면 메소드로 수정
            GourmetDetailAnalyticsParam analyticsParam = new GourmetDetailAnalyticsParam();
            analyticsParam.price = recommendationGourmet.price;
            analyticsParam.discountPrice = recommendationGourmet.discount;
            analyticsParam.setShowOriginalPriceYn(analyticsParam.price, analyticsParam.discountPrice);
            analyticsParam.setProvince(null);
            analyticsParam.entryPosition = recommendationGourmet.entryPosition;
            analyticsParam.totalListCount = count;
            analyticsParam.isDailyChoice = recommendationGourmet.isDailyChoice;
            analyticsParam.setAddressAreaName(recommendationGourmet.addrSummary);

            // <-- 추후에 정리되면 메소드로 수정

            if (mIsUsedMultiTransition == true)
            {
                setExitSharedElementCallback(new SharedElementCallback()
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

                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(CollectionGourmetActivity.this, ((DailyGourmetCardView) view).getOptionsCompat());

                Intent intent = GourmetDetailActivity.newInstance(CollectionGourmetActivity.this //
                    , recommendationGourmet.index, recommendationGourmet.name, recommendationGourmet.imageUrl, recommendationGourmet.discount//
                    , ((GourmetBookingDay) mPlaceBookingDay).getVisitDay(DailyCalendar.ISO_8601_FORMAT)//
                    , recommendationGourmet.category, recommendationGourmet.isSoldOut, false, false, true//
                    , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_LIST//
                    , analyticsParam);

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_DETAIL, optionsCompat.toBundle());
            } else
            {
                Intent intent = GourmetDetailActivity.newInstance(CollectionGourmetActivity.this //
                    , recommendationGourmet.index, recommendationGourmet.name, recommendationGourmet.imageUrl, recommendationGourmet.discount//
                    , ((GourmetBookingDay) mPlaceBookingDay).getVisitDay(DailyCalendar.ISO_8601_FORMAT)//
                    , recommendationGourmet.category, recommendationGourmet.isSoldOut, false, false, false//
                    , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE//
                    , analyticsParam);

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_DETAIL);

                overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
            }

            AnalyticsManager.getInstance(CollectionGourmetActivity.this).recordEvent(//
                AnalyticsManager.Category.HOME_RECOMMEND, Integer.toString(mRecommendationIndex),//
                Integer.toString(recommendationGourmet.index), null);

            // 할인 쿠폰이 보이는 경우
            if (DailyTextUtils.isTextEmpty(recommendationGourmet.couponDiscountText) == false)
            {
                AnalyticsManager.getInstance(CollectionGourmetActivity.this).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                    , AnalyticsManager.Action.COUPON_GOURMET, Integer.toString(recommendationGourmet.index), null);
            }

            if (recommendationGourmet.reviewCount > 0)
            {
                AnalyticsManager.getInstance(CollectionGourmetActivity.this).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                    , AnalyticsManager.Action.TRUE_REVIEW_GOURMET, Integer.toString(recommendationGourmet.index), null);
            }

            if (recommendationGourmet.discountRate > 0)
            {
                AnalyticsManager.getInstance(CollectionGourmetActivity.this).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                    , AnalyticsManager.Action.DISCOUNT_GOURMET, Integer.toString(recommendationGourmet.index), null);
            }
        }

        @Override
        public void onPlaceLongClick(int position, View view, PlaceViewItem placeViewItem, int count)
        {
            if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
            {
                return;
            }

            mCollectionBaseLayout.setBlurVisibility(CollectionGourmetActivity.this, true);

            RecommendationGourmet recommendationGourmet = placeViewItem.getItem();

            mWishPosition = position;

            // 기존 데이터를 백업한다.
            mViewByLongPress = view;
            mPlaceViewItemByLongPress = placeViewItem;
            mListCountByLongPress = count;

            String visitDateTime = ((GourmetBookingDay) mPlaceBookingDay).getVisitDay(DailyCalendar.ISO_8601_FORMAT);

            Intent intent = GourmetPreviewActivity.newInstance(CollectionGourmetActivity.this, visitDateTime//
                , recommendationGourmet.index, recommendationGourmet.name, recommendationGourmet.category, recommendationGourmet.discount);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PREVIEW);
        }

        @Override
        public void onWishClick(int position, PlaceViewItem placeViewItem)
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            RecommendationGourmet recommendationGourmet = placeViewItem.getItem();

            mWishPosition = position;

            boolean currentWish = recommendationGourmet.myWish;

            if (DailyHotel.isLogin() == true)
            {
                onChangedWish(position, !currentWish);
            }

            startActivityForResult(WishDialogActivity.newInstance(CollectionGourmetActivity.this, ServiceType.GOURMET//
                , recommendationGourmet.index, !currentWish, AnalyticsManager.Screen.DAILYGOURMET_LIST), Constants.CODE_REQUEST_ACTIVITY_WISH_DIALOG);

            AnalyticsManager.getInstance(CollectionGourmetActivity.this).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                , AnalyticsManager.Action.WISH_GOURMET, !currentWish ? AnalyticsManager.Label.ON.toLowerCase() : AnalyticsManager.Label.OFF.toLowerCase(), null);
        }

        @Override
        public void onRecommendationClick(Recommendation recommendation)
        {
            if (recommendation == null)
            {
                return;
            }

            startCollectionPlace(recommendation);
        }

        @Override
        public void finish()
        {
            CollectionGourmetActivity.this.onBackPressed();
        }
    };
}