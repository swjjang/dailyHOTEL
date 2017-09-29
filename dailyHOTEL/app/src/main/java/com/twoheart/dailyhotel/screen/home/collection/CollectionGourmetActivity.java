package com.twoheart.dailyhotel.screen.home.collection;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.util.SparseArray;
import android.view.View;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.parcel.analytics.GourmetDetailAnalyticsParam;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.RecommendationGourmet;
import com.twoheart.dailyhotel.network.model.RecommendationPlace;
import com.twoheart.dailyhotel.network.model.RecommendationPlaceList;
import com.twoheart.dailyhotel.network.model.Sticker;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCalendarActivity;
import com.twoheart.dailyhotel.screen.gourmet.preview.GourmetPreviewActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

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

    public static Intent newInstance(Context context, int index, String imageUrl, String title, String subTitle, boolean isUsedMultiTransition)
    {
        Intent intent = new Intent(context, CollectionGourmetActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_TYPE, TYPE_DEFAULT);
        intent.putExtra(INTENT_EXTRA_DATA_INDEX, index);
        intent.putExtra(INTENT_EXTRA_DATA_IMAGE_URL, imageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, title);
        intent.putExtra(INTENT_EXTRA_DATA_SUBTITLE, subTitle);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_USED_MULTITRANSITIOIN, isUsedMultiTransition);

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
            mCollectionBaseLayout.setTitleLayout(title, subTitle, imageUrl);

            initTransition();
        } else
        {
            mCollectionBaseLayout.setTitleLayout(title, subTitle, imageUrl);

            lockUI();

            requestCommonDateTime();
        }
    }

    @Override
    protected void requestRecommendationPlaceList(PlaceBookingDay placeBookingDay)
    {
        if (placeBookingDay == null)
        {
            return;
        }

        GourmetBookingDay gourmetBookingDay = (GourmetBookingDay) placeBookingDay;

        String period = gourmetBookingDay.getVisitDay("yyyy-MM-dd");

        DailyMobileAPI.getInstance(this).requestRecommendationGourmetList(mNetworkTag, mRecommendationIndex, period, 0, mRecommendationGourmetListCallback);
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
    protected void setPlaceBookingDay(TodayDateTime todayDateTime)
    {
        if (todayDateTime == null)
        {
            return;
        }

        try
        {
            GourmetBookingDay gourmetBookingDay = new GourmetBookingDay();
            gourmetBookingDay.setVisitDay(todayDateTime.dailyDateTime);

            switch (mType)
            {
                case TYPE_DEFAULT:
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
                            gourmetBookingDay.setVisitDay(todayDateTime.dailyDateTime, mAfterDay);
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
    protected void onCalendarActivityResult(int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            GourmetBookingDay gourmetBookingDay = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);

            if (gourmetBookingDay == null)
            {
                return;
            }

            mPlaceBookingDay = gourmetBookingDay;

            mCollectionBaseLayout.setCalendarText(getCalendarDate(gourmetBookingDay));

            lockUI();

            requestRecommendationPlaceList(gourmetBookingDay);
        }
    }

    @Override
    protected void startCalendarActivity(TodayDateTime todayDateTime, PlaceBookingDay placeBookingDay)
    {
        if (todayDateTime == null || placeBookingDay == null)
        {
            return;
        }

        Intent intent = GourmetCalendarActivity.newInstance(CollectionGourmetActivity.this, todayDateTime//
            , (GourmetBookingDay) placeBookingDay, GourmetCalendarActivity.DEFAULT_CALENDAR_DAY_OF_MAX_COUNT //
            , AnalyticsManager.ValueType.SEARCH, true, true);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CALENDAR);
    }

    @Override
    protected String getSectionTitle(int count)
    {
        return getString(R.string.label_count_gourmet, count);
    }

    @Override
    protected void onCommonDateTime(TodayDateTime todayDateTime)
    {
        mTodayDateTime = todayDateTime;

        setPlaceBookingDay(todayDateTime);

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

        mOnEventListener.onPlaceClick(view, placeViewItem, listCount);
    }

    private CollectionStayLayout.OnEventListener mOnEventListener = new CollectionBaseLayout.OnEventListener()
    {
        @Override
        public void onCalendarClick()
        {
            startCalendarActivity(mTodayDateTime, mPlaceBookingDay);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onPlaceClick(View view, PlaceViewItem placeViewItem, int count)
        {
            if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
            {
                return;
            }

            RecommendationGourmet recommendationGourmet = placeViewItem.getItem();

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

                View simpleDraweeView = view.findViewById(R.id.imageView);
                View nameTextView = view.findViewById(R.id.nameTextView);
                View gradientTopView = view.findViewById(R.id.gradientTopView);
                View gradientBottomView = view.findViewById(R.id.gradientView);

                Object mapTag = gradientBottomView.getTag();
                Intent intent;

                if (mapTag != null && "map".equals(mapTag) == true)
                {
                    //                    intent = GourmetDetailActivity.newInstance(CollectionGourmetActivity.this //
                    //                        , (GourmetBookingDay) mPlaceBookingDay, recommendationGourmet.index, recommendationGourmet.name //
                    //                        , recommendationGourmet.imageUrl, recommendationGourmet.category, recommendationGourmet.isSoldOut//
                    //                        , analyticsParam, true, PlaceDetailLayout.TRANS_GRADIENT_BOTTOM_TYPE_MAP);


                    intent = GourmetDetailActivity.newInstance(CollectionGourmetActivity.this //
                        , recommendationGourmet.index, recommendationGourmet.name, recommendationGourmet.imageUrl, recommendationGourmet.discount//
                        , ((GourmetBookingDay) mPlaceBookingDay).getVisitDay(DailyCalendar.ISO_8601_FORMAT)//
                        , recommendationGourmet.category, recommendationGourmet.isSoldOut, false, false, true//
                        , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_MAP//
                        , analyticsParam);
                } else
                {
                    //                    intent = GourmetDetailActivity.newInstance(CollectionGourmetActivity.this //
                    //                        , (GourmetBookingDay) mPlaceBookingDay, recommendationGourmet.index, recommendationGourmet.name //
                    //                        , recommendationGourmet.imageUrl, recommendationGourmet.category, recommendationGourmet.isSoldOut//
                    //                        , analyticsParam, true, PlaceDetailLayout.TRANS_GRADIENT_BOTTOM_TYPE_LIST);

                    intent = GourmetDetailActivity.newInstance(CollectionGourmetActivity.this //
                        , recommendationGourmet.index, recommendationGourmet.name, recommendationGourmet.imageUrl, recommendationGourmet.discount//
                        , ((GourmetBookingDay) mPlaceBookingDay).getVisitDay(DailyCalendar.ISO_8601_FORMAT)//
                        , recommendationGourmet.category, recommendationGourmet.isSoldOut, false, false, true//
                        , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_LIST//
                        , analyticsParam);
                }

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(CollectionGourmetActivity.this,//
                    android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)),//
                    android.support.v4.util.Pair.create(nameTextView, getString(R.string.transition_place_name)),//
                    android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view)),//
                    android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view)));

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_DETAIL, options.toBundle());
            } else
            {
                //                Intent intent = GourmetDetailActivity.newInstance(CollectionGourmetActivity.this //
                //                    , (GourmetBookingDay) mPlaceBookingDay, recommendationGourmet.index, recommendationGourmet.name //
                //                    , recommendationGourmet.imageUrl, recommendationGourmet.category, recommendationGourmet.isSoldOut//
                //                    , analyticsParam, false, PlaceDetailLayout.TRANS_GRADIENT_BOTTOM_TYPE_NONE);


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
        }

        @Override
        public void onPlaceLongClick(View view, PlaceViewItem placeViewItem, int count)
        {
            if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
            {
                return;
            }

            mCollectionBaseLayout.setBlurVisibility(CollectionGourmetActivity.this, true);

            RecommendationGourmet recommendationGourmet = placeViewItem.getItem();

            // 기존 데이터를 백업한다.
            mViewByLongPress = view;
            mPlaceViewItemByLongPress = placeViewItem;
            mListCountByLongPress = count;

            Intent intent = GourmetPreviewActivity.newInstance(CollectionGourmetActivity.this, (GourmetBookingDay) mPlaceBookingDay, recommendationGourmet);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PREVIEW);
        }

        @Override
        public void finish()
        {
            CollectionGourmetActivity.this.onBackPressed();
        }
    };

    private retrofit2.Callback mRecommendationGourmetListCallback = new retrofit2.Callback<BaseDto<RecommendationPlaceList<RecommendationGourmet>>>()
    {
        @Override
        public void onResponse(Call<BaseDto<RecommendationPlaceList<RecommendationGourmet>>> call, Response<BaseDto<RecommendationPlaceList<RecommendationGourmet>>> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    BaseDto<RecommendationPlaceList<RecommendationGourmet>> baseDto = response.body();

                    switch (baseDto.msgCode)
                    {
                        case 100:
                            ArrayList<RecommendationGourmet> gourmetList = new ArrayList<>();
                            gourmetList.addAll(baseDto.data.items);

                            onPlaceList(baseDto.data.imageBaseUrl, baseDto.data.recommendation, gourmetList, baseDto.data.stickers);
                            break;

                        // 인트라넷에서 숨김처리가 된경우
                        case 801:
                            onErrorPopupMessage(baseDto.msgCode, baseDto.msg);
                            break;

                        default:
                            onErrorPopupMessage(baseDto.msgCode, baseDto.msg);
                            break;
                    }
                } catch (Exception e)
                {
                    onError(e);
                } finally
                {
                    unLockUI();
                }
            } else
            {
                CollectionGourmetActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<BaseDto<RecommendationPlaceList<RecommendationGourmet>>> call, Throwable t)
        {
            CollectionGourmetActivity.this.onError(t);
        }
    };
}