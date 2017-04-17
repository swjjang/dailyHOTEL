package com.twoheart.dailyhotel.screen.home.collection;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.RecommendationPlace;
import com.twoheart.dailyhotel.network.model.RecommendationPlaceList;
import com.twoheart.dailyhotel.network.model.RecommendationStay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCalendarActivity;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

public class CollectionStayActivity extends CollectionBaseActivity
{
    public static Intent newInstance(Context context, int index, boolean isUsedMultiTransition)
    {
        Intent intent = new Intent(context, CollectionStayActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_INDEX, index);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_USED_MULTITRANSITIOIN, isUsedMultiTransition);

        return intent;
    }

    public static Intent newInstance(Context context, int index, String imageUrl, String title, String subTitle, boolean isUsedMultiTransition)
    {
        Intent intent = new Intent(context, CollectionStayActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_INDEX, index);
        intent.putExtra(INTENT_EXTRA_DATA_IMAGE_URL, imageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, title);
        intent.putExtra(INTENT_EXTRA_DATA_SUBTITLE, subTitle);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_USED_MULTITRANSITIOIN, isUsedMultiTransition);

        return intent;
    }

    @Override
    protected void requestRecommendationPlaceList(PlaceBookingDay placeBookingDay)
    {
        if (placeBookingDay == null)
        {
            return;
        }

        try
        {
            StayBookingDay stayBookingDay = (StayBookingDay) placeBookingDay;

            String salesDate = stayBookingDay.getCheckInDay("yyyy-MM-dd");
            int period = stayBookingDay.getNights();

            DailyMobileAPI.getInstance(this).requestRecommendationStayList(mNetworkTag, mRecommendationIndex, salesDate, period, mRecommendationStayListCallback);

        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    protected CollectionBaseLayout getCollectionLayout(Context context)
    {
        return new CollectionStayLayout(context, mOnEventListener);
    }

    @Override
    protected String getCalendarDate(PlaceBookingDay placeBookingDay)
    {
        if (placeBookingDay == null)
        {
            return null;
        }

        StayBookingDay stayBookingDay = (StayBookingDay) placeBookingDay;

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

    @Override
    protected void setPlaceBookingDay(TodayDateTime todayDateTime)
    {
        if (todayDateTime == null)
        {
            return;
        }

        try
        {
            StayBookingDay stayBookingDay = new StayBookingDay();
            stayBookingDay.setCheckInDay(todayDateTime.dailyDateTime);
            stayBookingDay.setCheckOutDay(todayDateTime.dailyDateTime, 1);

            mPlaceBookingDay = stayBookingDay;
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
            StayBookingDay stayBookingDay = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);

            if (stayBookingDay == null)
            {
                return;
            }

            mPlaceBookingDay = stayBookingDay;

            mCollectionBaseLayout.setCalendarText(getCalendarDate(stayBookingDay));

            lockUI();

            requestRecommendationPlaceList(stayBookingDay);
        }
    }

    @Override
    protected void startCalendarActivity(TodayDateTime todayDateTime, PlaceBookingDay placeBookingDay)
    {
        if (todayDateTime == null || placeBookingDay == null)
        {
            return;
        }

        Intent intent = StayCalendarActivity.newInstance(CollectionStayActivity.this, todayDateTime, (StayBookingDay) placeBookingDay, AnalyticsManager.ValueType.SEARCH, true, true);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CALENDAR);
    }

    @Override
    protected String getSectionTitle(int count)
    {
        return getString(R.string.label_count_stay, count);
    }

    @Override
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
            //            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_SECTION, getSectionTitle(placeList.size())));

            int entryPosition = 0;

            for (RecommendationPlace place : placeList)
            {
                place.imageUrl = imageBaseUrl + place.imageUrl;
                place.entryPosition = entryPosition++;

                placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, place));
            }

            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_GUIDE_VIEW, null));
        }

        return placeViewItemList;
    }

    private CollectionStayLayout.OnEventListener mOnEventListener = new CollectionBaseLayout.OnEventListener()
    {
        @Override
        public void onCalendarClick()
        {
            startCalendarActivity(mTodayDateTime, mPlaceBookingDay);
        }

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
                Intent intent = StayDetailActivity.newInstance(CollectionStayActivity.this, (StayBookingDay) mPlaceBookingDay, recommendationStay, count, true);

                View simpleDraweeView = view.findViewById(R.id.imageView);
                View gradeTextView = view.findViewById(R.id.gradeTextView);
                View nameTextView = view.findViewById(R.id.nameTextView);
                View gradientTopView = view.findViewById(R.id.gradientTopView);
                View gradientBottomView = view.findViewById(R.id.gradientView);

                Object mapTag = gradientBottomView.getTag();

                if (mapTag != null && "map".equals(mapTag) == true)
                {
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_FROM_MAP, true);
                }

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(CollectionStayActivity.this,//
                    android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)),//
                    android.support.v4.util.Pair.create(gradeTextView, getString(R.string.transition_place_grade)),//
                    android.support.v4.util.Pair.create(nameTextView, getString(R.string.transition_place_name)),//
                    android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view)),//
                    android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view)));

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL, options.toBundle());
            } else
            {
                Intent intent = StayDetailActivity.newInstance(CollectionStayActivity.this, (StayBookingDay) mPlaceBookingDay, recommendationStay, count, false);

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL);

                overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
            }

            AnalyticsManager.getInstance(CollectionStayActivity.this).recordEvent(//
                AnalyticsManager.Category.HOME_RECOMMEND, Integer.toString(mRecommendationIndex),//
                Integer.toString(recommendationStay.index), null);
        }

        @Override
        public void finish()
        {
            CollectionStayActivity.this.onBackPressed();
        }
    };

    private retrofit2.Callback mRecommendationStayListCallback = new retrofit2.Callback<BaseDto<RecommendationPlaceList<RecommendationStay>>>()
    {
        @Override
        public void onResponse(Call<BaseDto<RecommendationPlaceList<RecommendationStay>>> call, Response<BaseDto<RecommendationPlaceList<RecommendationStay>>> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    BaseDto<RecommendationPlaceList<RecommendationStay>> baseDto = response.body();

                    switch (baseDto.msgCode)
                    {
                        case 100:
                            ArrayList<RecommendationStay> stayList = new ArrayList<>();
                            stayList.addAll(baseDto.data.items);

                            onPlaceList(baseDto.data.imageBaseUrl, baseDto.data.recommendation, stayList);
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
                CollectionStayActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<BaseDto<RecommendationPlaceList<RecommendationStay>>> call, Throwable t)
        {
            CollectionStayActivity.this.onError(t);
        }
    };
}