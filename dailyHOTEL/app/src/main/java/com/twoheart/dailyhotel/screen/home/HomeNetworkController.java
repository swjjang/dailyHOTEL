package com.twoheart.dailyhotel.screen.home;

import android.content.Context;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.dto.BaseListDto;
import com.twoheart.dailyhotel.network.model.Event;
import com.twoheart.dailyhotel.network.model.HomePlace;
import com.twoheart.dailyhotel.network.model.HomePlaces;
import com.twoheart.dailyhotel.network.model.Recommendation;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

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
        void onCommonDateTime(TodayDateTime todayDateTime);

        void onEventList(ArrayList<Event> list);

        void onWishList(ArrayList<HomePlace> list, boolean isError);

        void onRecommendationList(ArrayList<Recommendation> list, boolean isError);
    }

    public void requestCommonDateTime()
    {
        DailyMobileAPI.getInstance(mContext).requestCommonDateTime(mNetworkTag, mDateTimeJsonCallback);
    }

    public void requestEventList()
    {
        String store;

        if (Setting.RELEASE_STORE == Setting.Stores.PLAY_STORE)
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

    public void requestRecommendationList()
    {
        DailyMobileAPI.getInstance(mContext).requestRecommendationList(mNetworkTag, mRecommendationCallback);
    }

    private retrofit2.Callback mDateTimeJsonCallback = new retrofit2.Callback<BaseDto<TodayDateTime>>()
    {
        @Override
        public void onResponse(Call<BaseDto<TodayDateTime>> call, Response<BaseDto<TodayDateTime>> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    BaseDto<TodayDateTime> baseDto = response.body();

                    if (baseDto.msgCode == 100)
                    {
                        ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onCommonDateTime(baseDto.data);
                    } else
                    {
                        mOnNetworkControllerListener.onError(new RuntimeException(baseDto.msg));
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
        public void onFailure(Call<BaseDto<TodayDateTime>> call, Throwable t)
        {
            mOnNetworkControllerListener.onError(call, t, false);
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
            mOnNetworkControllerListener.onError(call, t, true);
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
            mOnNetworkControllerListener.onError(call, t, true);
            ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onRecommendationList(null, true);
        }
    };

    private retrofit2.Callback mWishListCallBack = new retrofit2.Callback<BaseDto<HomePlaces>>()
    {
        @Override
        public void onResponse(Call<BaseDto<HomePlaces>> call, Response<BaseDto<HomePlaces>> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    BaseDto<HomePlaces> baseDto = response.body();

                    if (baseDto.msgCode == 100)
                    {
                        ArrayList<HomePlace> homePlaceList = new ArrayList<>();
                        homePlaceList.addAll(baseDto.data.items);

                        String imageBaseUrl = baseDto.data.imageBaseUrl;

                        for (HomePlace wishItem : homePlaceList)
                        {
                            if (DailyTextUtils.isTextEmpty(wishItem.imageUrl) == false)
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
            mOnNetworkControllerListener.onError(call, t, true);
            ((HomeNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onWishList(null, true);
        }
    };
}
