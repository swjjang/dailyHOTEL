package com.twoheart.dailyhotel.screen.home;

import android.content.Context;

import com.twoheart.dailyhotel.model.HomeRecentParam;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.dto.BaseListDto;
import com.twoheart.dailyhotel.network.model.Event;
import com.twoheart.dailyhotel.network.model.HomePlace;
import com.twoheart.dailyhotel.network.model.HomePlaces;
import com.twoheart.dailyhotel.network.model.Recommendation;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

        void onEventList(ArrayList<Event> list);

        void onWishList(ArrayList<HomePlace> list, boolean isError);

        void onRecentList(ArrayList<HomePlace> list, boolean isError);

        void onRecommendationList(ArrayList<Recommendation> list, boolean isError);
    }

    public void requestCommonDateTime()
    {
        DailyMobileAPI.getInstance(mContext).requestCommonDateTime(mNetworkTag, mDateTimeJsonCallback);
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
        DailyMobileAPI.getInstance(mContext).requestHomeWishList(mNetworkTag, mWishListCallBack);
    }

    public void requestRecentList(ArrayList<HomeRecentParam> recentParamList)
    {
        // 리스트가 비었을때 서버에 리퀘스트시 필수 값 없어 에러 발생, 하지만 해당 경우는 정상인 상태로 서버에 리퀘스트 하지 않음 - 아이폰 동일
        if (recentParamList == null || recentParamList.size() == 0)
        {
            ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onRecentList(null, false);
            return;
        }

        JSONArray recentJsonArray = getRecentJsonArray(recentParamList);

        JSONObject recentJsonObject = new JSONObject();
        try
        {
            recentJsonObject.put("keys", recentJsonArray);
        } catch (JSONException e)
        {
            ExLog.d(e.getMessage());
        }

        DailyMobileAPI.getInstance(mContext).requestHomeRecentList(mNetworkTag, recentJsonObject, mRecentListCallBack);
    }

    public void requestRecommendationList()
    {
        DailyMobileAPI.getInstance(mContext).requestRecommendationList(mNetworkTag, mRecommendationCallback);
    }

    private JSONArray getRecentJsonArray(ArrayList<HomeRecentParam> list)
    {
        if (list == null || list.size() == 0)
        {
            return null;
        }

        JSONArray jsonArray = new JSONArray();

        for (HomeRecentParam param : list)
        {
            JSONObject jsonObject = new JSONObject();

            try
            {
                jsonObject.put("serviceType", param.serviceType);
                jsonObject.put("idx", param.index);

                jsonArray.put(jsonObject);
            } catch (JSONException e)
            {
                ExLog.d(e.getMessage());
            }
        }

        return jsonArray;
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
                    } else
                    {
                        ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onEventList(null);
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

                        ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onRecommendationList(recommendationList, false);
                    } else
                    {
                        ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onRecommendationList(null, true);
                    }
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                    ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onRecommendationList(null, true);
                }
            } else
            {
                ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onRecommendationList(null, true);
            }
        }

        @Override
        public void onFailure(Call call, Throwable t)
        {
            ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onRecommendationList(null, true);
        }
    };

    private retrofit2.Callback mWishListCallBack = new retrofit2.Callback<BaseDto<HomePlaces<HomePlace>>>()
    {
        @Override
        public void onResponse(Call<BaseDto<HomePlaces<HomePlace>>> call, Response<BaseDto<HomePlaces<HomePlace>>> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    BaseDto<HomePlaces<HomePlace>> baseDto = response.body();

                    if (baseDto.msgCode == 100)
                    {
                        ArrayList<HomePlace> homePlaceList = new ArrayList<>();
                        homePlaceList.addAll(baseDto.data.items);

                        String imageBaseUrl = baseDto.data.imageBaseUrl;

                        for (HomePlace wishItem : homePlaceList)
                        {
                            if (Util.isTextEmpty(wishItem.imageUrl) == false)
                            {
                                wishItem.imageUrl = imageBaseUrl + wishItem.imageUrl;
                            }
                        }

                        ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onWishList(homePlaceList, false);
                    } else
                    {
                        ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onWishList(null, true);
                    }
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                    ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onWishList(null, true);
                }
            } else
            {
                ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onWishList(null, true);
            }
        }

        @Override
        public void onFailure(Call call, Throwable t)
        {
            ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onWishList(null, true);
        }
    };

    private retrofit2.Callback mRecentListCallBack = new retrofit2.Callback<BaseDto<HomePlaces<HomePlace>>>()
    {
        @Override
        public void onResponse(Call<BaseDto<HomePlaces<HomePlace>>> call, Response<BaseDto<HomePlaces<HomePlace>>> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    BaseDto<HomePlaces<HomePlace>> baseDto = response.body();

                    if (baseDto.msgCode == 100)
                    {
                        ArrayList<HomePlace> homePlaceList = new ArrayList<>();
                        homePlaceList.addAll(baseDto.data.items);

                        String imageBaseUrl = baseDto.data.imageBaseUrl;

                        for (HomePlace recentItem : homePlaceList)
                        {
                            if (Util.isTextEmpty(recentItem.imageUrl) == false)
                            {
                                recentItem.imageUrl = imageBaseUrl + recentItem.imageUrl;
                            }
                        }

                        ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onRecentList(homePlaceList, false);
                    } else
                    {
                        ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onRecentList(null, true);
                    }
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                    ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onRecentList(null, true);
                }
            } else
            {
                ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onRecentList(null, true);
            }
        }

        @Override
        public void onFailure(Call call, Throwable t)
        {
            ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onRecentList(null, true);
        }
    };
}
