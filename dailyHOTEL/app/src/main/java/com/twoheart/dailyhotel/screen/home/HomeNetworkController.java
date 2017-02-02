package com.twoheart.dailyhotel.screen.home;

import android.content.Context;

import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.Review;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseListDto;
import com.twoheart.dailyhotel.network.model.Event;
import com.twoheart.dailyhotel.network.model.Recommendation;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by android_sam on 2017. 1. 18..
 */

public class HomeNetworkController extends BaseNetworkController
{
    public HomeNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onCommonDateTime(long currentDateTime, long dailyDateTime);

        void onReviewInformation(Review review);

        void onEventList(ArrayList<Event> list);

        void onWishList(ArrayList<? extends Place> list);

        void onRecommendationList(ArrayList<Recommendation> list);
    }

    public void requestCommonDateTime()
    {
        DailyMobileAPI.getInstance(mContext).requestCommonDateTime(mNetworkTag, mDateTimeJsonCallback);
    }

    public void requestReviewInformation()
    {
        DailyMobileAPI.getInstance(mContext).requestStayReviewInformation(mNetworkTag, mReviewStayCallback);
    }

    public void requestEventList()
    {
        String store;

        if (Constants.RELEASE_STORE == Constants.Stores.PLAY_STORE)
        {
            store = "GOOGLE";
        } else
        {
            store = "ONE";
        }

        DailyMobileAPI.getInstance(mContext).requestHomeEvents(mNetworkTag, store, mEventCallback);
    }

    public void requestWishList()
    {
        // 임시 테스트 데이터
        ArrayList<Stay> placeList = new ArrayList<>();
        Random random = new Random();
        int size = random.nextInt(14);
        for (int i = 0; i < size; i++)
        {
            Stay stay = new Stay();

            stay.price = Math.abs(random.nextInt(100000));
            stay.name = "Stay " + i;
            stay.discountPrice = Math.abs(stay.price - random.nextInt(10000));
            stay.districtName = "서울";
            stay.isSoldOut = i % 5 == 0;

            if (i % 3 == 0)
            {
                stay.imageUrl = "https://img.dailyhotel.me/resources/images/dh_23351/01.jpg";
            } else if (i % 3 == 1)
            {
                stay.imageUrl = "https://img.dailyhotel.me/resources/images/dh_23351/02.jpg";
            } else
            {
                stay.imageUrl = "https://img.dailyhotel.me/resources/images/dh_23351/03.jpg";
            }
            placeList.add(stay);

            stay.setGrade(Stay.Grade.special2);
        }

        ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onWishList(placeList);
    }

    public void requestRecommendationList()
    {
        DailyMobileAPI.getInstance(mContext).requestRecommendationList(mNetworkTag, mRecommendationCallback);
    }

    private retrofit2.Callback mDateTimeJsonCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");

                    if (msgCode == 100)
                    {
                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                        long currentDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("currentDateTime"), DailyCalendar.ISO_8601_FORMAT);
                        long dailyDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("dailyDateTime"), DailyCalendar.ISO_8601_FORMAT);

                        ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onCommonDateTime(currentDateTime, dailyDateTime);
                    } else
                    {
                        String message = responseJSONObject.getString("msg");
                        mOnNetworkControllerListener.onError(new RuntimeException(message));
                    }
                } catch (Exception e)
                {
                    mOnNetworkControllerListener.onError(e);
                }
            } else
            {
                mOnNetworkControllerListener.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            mOnNetworkControllerListener.onError(t);
        }
    };

    private retrofit2.Callback mReviewStayCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    // 리뷰가 존재하지 않는 경우 msgCode : 701
                    int msgCode = responseJSONObject.getInt("msgCode");

                    if (msgCode == 100 && responseJSONObject.has("data") == true)
                    {
                        Review review = new Review(responseJSONObject.getJSONObject("data"));

                        ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onReviewInformation(review);
                    } else
                    {
                        ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onReviewInformation(null);
                    }
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                    ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onReviewInformation(null);
                }
            } else
            {
                ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onReviewInformation(null);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onReviewInformation(null);
        }
    };

    private retrofit2.Callback mEventCallback = new retrofit2.Callback<BaseListDto<Event>>()
    {
        @Override
        public void onResponse(Call<BaseListDto<Event>> call, Response<BaseListDto<Event>> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    BaseListDto<Event> baseListDto = response.body();

                    if (baseListDto.msgCode == 100)
                    {
                        ArrayList<Event> homeEventList = (ArrayList<Event>) baseListDto.data;

                        ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onEventList(homeEventList);
                    }
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                    ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onEventList(null);
                }
            } else
            {
                ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onEventList(null);
            }
        }

        @Override
        public void onFailure(Call call, Throwable t)
        {
            ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onEventList(null);
        }
    };

    private retrofit2.Callback mRecommendationCallback = new retrofit2.Callback<BaseListDto<Recommendation>>()
    {
        @Override
        public void onResponse(Call<BaseListDto<Recommendation>> call, Response<BaseListDto<Recommendation>> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    BaseListDto<Recommendation> baseListDto = response.body();

                    if (baseListDto.msgCode == 100)
                    {
                        ArrayList<Recommendation> recommendationList = (ArrayList<Recommendation>) baseListDto.data;

                        ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onRecommendationList(recommendationList);
                    }
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                    ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onRecommendationList(null);
                }
            } else
            {
                ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onRecommendationList(null);
            }
        }

        @Override
        public void onFailure(Call call, Throwable t)
        {
            ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onRecommendationList(null);
        }
    };
}
