package com.twoheart.dailyhotel.screen.home.collection;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.RecommendationGourmet;
import com.twoheart.dailyhotel.network.model.RecommendationPlaceList;
import com.twoheart.dailyhotel.screen.gourmet.detail.GourmetDetailActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCalendarActivity;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class CollectionGourmetActivity extends CollectionBaseActivity
{
    public static Intent newInstance(Context context, int index)
    {
        Intent intent = new Intent(context, CollectionGourmetActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_INDEX, index);

        return intent;
    }

    public static Intent newInstance(Context context, int index, String imageUrl, String title, String subTitle)
    {
        Intent intent = new Intent(context, CollectionGourmetActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_INDEX, index);
        intent.putExtra(INTENT_EXTRA_DATA_IMAGE_URL, imageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, title);
        intent.putExtra(INTENT_EXTRA_DATA_SUBTITLE, subTitle);

        return intent;
    }

    @Override
    protected void requestRecommendationPlaceList()
    {
        String period = mStartSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd");

        DailyMobileAPI.getInstance(this).requestRecommendationGourmetList(mNetworkTag, mRecommendationIndex, period, 0, mRecommendationGourmetListCallback);
    }

    @Override
    protected CollectionBaseLayout getCollectionLayout(Context context)
    {
        return new CollectionGourmetLayout(this, mOnEventListener);
    }

    @Override
    protected String getCalendarDate()
    {
        if (mStartSaleTime == null)
        {
            return null;
        }

        return mStartSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");
    }

    @Override
    protected void onCalendarActivityResult(int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            SaleTime checkInSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);

            if (checkInSaleTime == null)
            {
                return;
            }

            mStartSaleTime = checkInSaleTime;

            mCollectionBaseLayout.setCalendarText(getCalendarDate());

            lockUI();

            requestRecommendationPlaceList();
        }
    }

    @Override
    protected String getSectionTitle(int count)
    {
        return getString(R.string.label_count_gourmet, count);
    }

    private CollectionStayLayout.OnEventListener mOnEventListener = new CollectionBaseLayout.OnEventListener()
    {
        @Override
        public void onCalendarClick()
        {
            Intent intent = GourmetCalendarActivity.newInstance(CollectionGourmetActivity.this, mStartSaleTime//
                , mSaleTIme, null, AnalyticsManager.ValueType.SEARCH, true, true);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CALENDAR);
        }

        @Override
        public void onPlaceClick(View view, PlaceViewItem placeViewItem, int count)
        {
            if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
            {
                return;
            }

            RecommendationGourmet recommendationGourmet = placeViewItem.getItem();

            Intent intent = GourmetDetailActivity.newInstance(CollectionGourmetActivity.this, mStartSaleTime, recommendationGourmet, null, null, count);

            if (Util.isUsedMultiTransition() == true)
            {
                View simpleDraweeView = view.findViewById(R.id.imageView);
                View nameTextView = view.findViewById(R.id.nameTextView);
                View gradientTopView = view.findViewById(R.id.gradientTopView);
                View gradientBottomView = view.findViewById(R.id.gradientView);

                Object mapTag = gradientBottomView.getTag();

                if (mapTag != null && "map".equals(mapTag) == true)
                {
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_FROM_MAP, true);
                }

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(CollectionGourmetActivity.this,//
                    android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)),//
                    android.support.v4.util.Pair.create(nameTextView, getString(R.string.transition_place_name)),//
                    android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view)),//
                    android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view)));

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_DETAIL, options.toBundle());
            } else
            {
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_DETAIL);
            }
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

                    if (baseDto.msgCode == 100)
                    {
                        ArrayList<RecommendationGourmet> gourmetList = new ArrayList<>();
                        gourmetList.addAll(baseDto.data.items);

                        onPlaceList(baseDto.data.imageBaseUrl, baseDto.data.recommendation, gourmetList);
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