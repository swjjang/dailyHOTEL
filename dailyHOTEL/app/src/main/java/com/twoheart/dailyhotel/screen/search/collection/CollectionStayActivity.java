package com.twoheart.dailyhotel.screen.search.collection;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.RecommendationPlace;
import com.twoheart.dailyhotel.network.model.RecommendationPlaceList;
import com.twoheart.dailyhotel.network.model.RecommendationStay;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCalendarActivity;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class CollectionStayActivity extends CollectionBaseActivity
{
    public static Intent newInstance(Context context, int index)
    {
        Intent intent = new Intent(context, CollectionStayActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_INDEX, index);

        return intent;
    }

    public static Intent newInstance(Context context, int index, String imageUrl, String title, String subTitle)
    {
        Intent intent = new Intent(context, CollectionStayActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_INDEX, index);
        intent.putExtra(INTENT_EXTRA_DATA_IMAGE_URL, imageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, title);
        intent.putExtra(INTENT_EXTRA_DATA_SUBTITLE, subTitle);

        return intent;
    }

    @Override
    protected void initIntentTime(Intent intent)
    {
        //        if (intent.hasExtra(INTENT_EXTRA_DATA_SALE_TIME) == true)
        //        {
        //            mCheckInSaleTime = intent.getParcelableExtra(INTENT_EXTRA_DATA_SALE_TIME);
        //            mNights = intent.getIntExtra(INTENT_EXTRA_DATA_NIGHT, 1);
        //
        //            mStartSaleTime = mCheckInSaleTime.getClone(0);
        //            mEndSaleTime = null;
        //        } else
        //        {
        //            mStartSaleTime = intent.getParcelableExtra(INTENT_EXTRA_DATA_START_SALETIME);
        //            mEndSaleTime = intent.getParcelableExtra(INTENT_EXTRA_DATA_END_SALETIME);
        //
        //            // 범위 지정인데 이미 날짜가 지난 경우
        //            if (mStartSaleTime.getOffsetDailyDay() == 0 && mEndSaleTime.getOffsetDailyDay() == 0)
        //            {
        //                showSimpleDialog(null, getString(R.string.message_end_event), getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), new View.OnClickListener()
        //                {
        //                    @Override
        //                    public void onClick(View v)
        //                    {
        //                        Intent eventIntent = new Intent();
        //                        eventIntent.setData(Uri.parse("dailyhotel://dailyhotel.co.kr?vc=10&v=el"));
        //                        startActivity(eventIntent);
        //                    }
        //                }, null);
        //
        //                mEndSaleTime = null;
        //            }
        //
        //            mCheckInSaleTime = mStartSaleTime.getClone();
        //            mNights = 1;
        //        }
    }

    @Override
    protected PlaceListAdapter getPlaceListAdapter(View.OnClickListener listener)
    {
        return new CollectionStayAdapter(this, new ArrayList<PlaceViewItem>(), mOnItemClickListener);
    }

    @Override
    protected void requestRecommendationPlaceList()
    {
        String salesDate = mStartSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd");
        int period = mEndSaleTime.getOffsetDailyDay() - mStartSaleTime.getOffsetDailyDay();

        DailyMobileAPI.getInstance(this).requestRecommendationStayList(mNetworkTag, mRecommendationIndex, salesDate, period, mRecommendationStayListCallback);
    }

    @Override
    protected void onPlaceClick(View view, PlaceViewItem placeViewItem, int count)
    {
        if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
        {
            return;
        }

        Stay stay = placeViewItem.getItem();

        Intent intent = StayDetailActivity.newInstance(this, mStartSaleTime, stay, mStartSaleTime, null, count);

        if (Util.isUsedMultiTransition() == true)
        {
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

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,//
                android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)),//
                android.support.v4.util.Pair.create(gradeTextView, getString(R.string.transition_place_grade)),//
                android.support.v4.util.Pair.create(nameTextView, getString(R.string.transition_place_name)),//
                android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view)),//
                android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view)));

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL, options.toBundle());
        } else
        {
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL);
        }
    }

    @Override
    protected String getCalendarDate()
    {
        if (mStartSaleTime == null || mEndSaleTime == null)
        {
            return null;
        }

        String checkInDate = mStartSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");
        String checkOutDate = mEndSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");

        int nights = mEndSaleTime.getOffsetDailyDay() - mStartSaleTime.getOffsetDailyDay();

        return String.format("%s - %s, %d박", checkInDate, checkOutDate, nights);
    }

    @Override
    protected void onCalendarActivityResult(int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            SaleTime checkInSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE);
            SaleTime checkOutSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CHECKOUTDATE);

            if (checkInSaleTime == null || checkOutSaleTime == null)
            {
                return;
            }

            mStartSaleTime = checkInSaleTime;
            mEndSaleTime = checkOutSaleTime;
        }
    }

    @Override
    protected String getSectionTitle(int count)
    {
        return getString(R.string.label_count_stay, count);
    }

    @Override
    protected void onCalendarClick()
    {
        final int nights = mEndSaleTime.getOffsetDailyDay() - mStartSaleTime.getOffsetDailyDay();

        Intent intent = StayCalendarActivity.newInstance(CollectionStayActivity.this, mStartSaleTime, nights //
            , mStartSaleTime, mEndSaleTime, AnalyticsManager.ValueType.SEARCH, true, true);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CALENDAR);
    }

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
            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_SECTION, getSectionTitle(placeList.size())));

            int nights = mEndSaleTime.getOffsetDailyDay() - mStartSaleTime.getOffsetDailyDay();

            for (RecommendationPlace place : placeList)
            {
                place.imageUrl = imageBaseUrl + place.imageUrl;
                ((RecommendationStay) place).nights = nights;

                placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, place));
            }
        }

        return placeViewItemList;
    }

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

                    if (baseDto.msgCode == 100)
                    {
                        ArrayList<RecommendationStay> stayList = new ArrayList<>();
                        stayList.addAll(baseDto.data.items);

                        onPlaceList(baseDto.data.imageBaseUrl, stayList);
                    } else
                    {
                        onErrorPopupMessage(baseDto.msgCode, baseDto.msg);
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