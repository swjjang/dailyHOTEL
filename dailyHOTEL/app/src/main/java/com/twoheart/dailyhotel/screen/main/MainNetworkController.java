package com.twoheart.dailyhotel.screen.main;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.model.Review;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

public class MainNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void updateNewEvent(boolean isNewEvent, boolean isNewCoupon, boolean isNewNotices);

        void onReviewGourmet(Review review);

        void onReviewStay(Review review);

        void onCheckServerResponse(String title, String message);

        void onAppVersionResponse(String currentVersion, String forceVersion);

        void onConfigurationResponse();

        void onNoticeAgreement(String message, boolean isFirstTimeBuyer);

        void onNoticeAgreementResult(String agreeMessage, String cancelMessage);

        void onCommonDateTime(long currentDateTime, long openDateTime, long closeDateTime);

        void onUserProfileBenefit(boolean isExceedBonus);
    }

    public MainNetworkController(Context context, String networkTag, OnNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    protected void requestCheckServer()
    {
        // 서버 상태 체크
        //        DailyNetworkAPI.getInstance(mContext).requestStatusServer(mNetworkTag, mStatusHealthCheckJsonResponseListener);

        DailyMobileAPI.getInstance(mContext).requestStatusServer(mNetworkTag, mStatusCallback);
    }

    public void requestUserInformation()
    {
        DailyMobileAPI.getInstance(mContext).requestUserProfile(mNetworkTag, mUserProfileCallback);
    }

    /**
     * 이벤트가 있는지를 요청한다. 실패 하더라도 무시한다.
     */
    protected void requestCommonDatetime()
    {
        DailyMobileAPI.getInstance(mContext).requestCommonDateTime(mNetworkTag, new retrofit2.Callback<JSONObject>()
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
                            long openDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("openDateTime"), DailyCalendar.ISO_8601_FORMAT);
                            long closeDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("closeDateTime"), DailyCalendar.ISO_8601_FORMAT);

                            ((OnNetworkControllerListener) mOnNetworkControllerListener).onCommonDateTime(currentDateTime, openDateTime, closeDateTime);
                        }
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
            }
        });
    }

    protected void requestEventNCouponNNoticeNewCount(String lastEventTime, String lastCouponTime, String lastNoticeTime)
    {
        if (Util.isTextEmpty(lastEventTime, lastCouponTime, lastNoticeTime) == true)
        {
            return;
        }

        DailyMobileAPI.getInstance(mContext).requestEventNCouponNNoticeNewCount(mNetworkTag, lastEventTime, lastCouponTime, lastNoticeTime, mDailyEventCountCallback);
    }

    protected void requestVersion()
    {
        //        DailyNetworkAPI.getInstance(mContext).requestCommonVersion(mNetworkTag, mAppVersionJsonResponseListener);
        DailyMobileAPI.getInstance(mContext).requestCommonVersion(mNetworkTag, mCommonVersionCallback);
    }

    protected void requestReviewStay()
    {
        DailyMobileAPI.getInstance(mContext).requestStayReviewInformation(mNetworkTag, mReviewStayCallback);
    }

    protected void requestReviewGourmet()
    {
        DailyMobileAPI.getInstance(mContext).requestGourmetReviewInformation(mNetworkTag, mReviewGourmetCallback);
    }

    public void requestNoticeAgreement()
    {
        DailyMobileAPI.getInstance(mContext).requestNoticeAgreement(mNetworkTag, mNoticeAgreementCallback);
    }

    public void requestNoticeAgreementResult(boolean isAgree)
    {
        DailyMobileAPI.getInstance(mContext).requestNoticeAgreementResult(mNetworkTag, isAgree, mNoticeAgreementResultCallback);
    }

    private retrofit2.Callback<JSONObject> mStatusCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msg_code");

                    if (msgCode == 200)
                    {
                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                        boolean isSuspend = dataJSONObject.getBoolean("isSuspend");

                        if (isSuspend == true)
                        {
                            String title = dataJSONObject.getString("messageTitle");
                            String message = dataJSONObject.getString("messageBody");

                            ((OnNetworkControllerListener) mOnNetworkControllerListener).onCheckServerResponse(title, message);
                        } else
                        {
                            ((OnNetworkControllerListener) mOnNetworkControllerListener).onCheckServerResponse(null, null);
                        }
                    } else
                    {
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onCheckServerResponse(null, null);
                    }
                } catch (Exception e)
                {
                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onCheckServerResponse(null, null);
                }
            } else
            {
                ((OnNetworkControllerListener) mOnNetworkControllerListener).onCheckServerResponse(null, null);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            ((OnNetworkControllerListener) mOnNetworkControllerListener).onCheckServerResponse(null, null);
        }
    };

    private retrofit2.Callback<JSONObject> mCommonVersionCallback = new retrofit2.Callback<JSONObject>()
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

                        String maxVersionName;
                        String minVersionName;

                        switch (Constants.RELEASE_STORE)
                        {
                            case T_STORE:
                                maxVersionName = dataJSONObject.getString("tstoreMax");
                                minVersionName = dataJSONObject.getString("tstoreMin");
                                break;

                            case PLAY_STORE:
                            default:
                                maxVersionName = dataJSONObject.getString("playMax");
                                minVersionName = dataJSONObject.getString("playMin");
                                break;
                        }

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onAppVersionResponse(maxVersionName, minVersionName);
                    } else
                    {
                        String message = responseJSONObject.getString("msg");

                        mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
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

    private retrofit2.Callback mDailyEventCountCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    boolean isExistNewEvent = false;
                    boolean isExistNewCoupon = false;
                    boolean isExistNewNotices = false;

                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");

                    if (msgCode == 100)
                    {
                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                        isExistNewEvent = dataJSONObject.getBoolean("isExistNewEvent");
                        isExistNewCoupon = dataJSONObject.getBoolean("isExistNewCoupon");
                        isExistNewNotices = dataJSONObject.getBoolean("isExistNewNotices");
                    }

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).updateNewEvent(isExistNewEvent, isExistNewCoupon, isExistNewNotices);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            } else
            {

            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {

        }
    };

    private retrofit2.Callback mReviewGourmetCallback = new retrofit2.Callback<JSONObject>()
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

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onReviewGourmet(review);
                    } else
                    {
                        // 고메 이벤트까지 없으면 첫구매 이벤트 확인한다.
                        requestNoticeAgreement();
                    }
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            } else
            {

            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {

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

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onReviewStay(review);
                    } else
                    {
                        requestReviewGourmet();
                    }
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            } else
            {

            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {

        }
    };

    private retrofit2.Callback mUserProfileCallback = new retrofit2.Callback<JSONObject>()
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

                        final String userIndex = dataJSONObject.getString("userIdx");
                        final String userType = dataJSONObject.has("userType") == true ? dataJSONObject.getString("userType") : AnalyticsManager.ValueType.EMPTY;
                        AnalyticsManager.getInstance(mContext).setUserInformation(userIndex, userType);

                        if (Util.isTextEmpty(userIndex) == true)
                        {
                            if (Constants.DEBUG == true)
                            {
                                ExLog.w(dataJSONObject.toString());
                            } else
                            {
                                Crashlytics.logException(new RuntimeException("JSON USER Check : " + dataJSONObject.toString(1)));
                            }
                        }

                        AnalyticsManager.getInstance(mContext).startApplication();

                        // 누적 적립금 판단.
                        DailyMobileAPI.getInstance(mContext).requestUserProfileBenefit(mNetworkTag, mUserProfileBenefitCallback);
                    } else
                    {
                        mOnNetworkControllerListener.onError(null);
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

    private retrofit2.Callback mUserProfileBenefitCallback = new retrofit2.Callback<JSONObject>()
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

                        boolean isExceedBonus = dataJSONObject.getBoolean("exceedLimitedBonus");

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onUserProfileBenefit(isExceedBonus);
                    } else
                    {
                        // 에러가 나도 특별히 해야할일은 없다.
                    }
                } catch (Exception e)
                {
                    // do nothing
                }
            } else
            {
                mOnNetworkControllerListener.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            // do nothing
        }
    };

    private retrofit2.Callback mNoticeAgreementCallback = new retrofit2.Callback<JSONObject>()
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

                        String message01 = dataJSONObject.getString("description1");
                        String message02 = dataJSONObject.getString("description2");
                        boolean isFirstTimeBuyer = dataJSONObject.getBoolean("isFirstTimeBuyer");

                        String message = message01 + "\n\n" + message02;

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onNoticeAgreement(message, isFirstTimeBuyer);
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

    private retrofit2.Callback mNoticeAgreementResultCallback = new retrofit2.Callback<JSONObject>()
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

                        String agreeAt = dataJSONObject.getString("agreedAt");
                        String description1InAgree = dataJSONObject.getString("description1InAgree");
                        String description2InAgree = dataJSONObject.getString("description2InAgree");
                        String description1InReject = dataJSONObject.getString("description1InReject");
                        String description2InReject = dataJSONObject.getString("description2InReject");

                        //                    agreeAt = Util.simpleDateFormatISO8601toFormat(agreeAt, "yyyy년 MM월 dd일");
                        agreeAt = DailyCalendar.convertDateFormatString(agreeAt, DailyCalendar.ISO_8601_FORMAT, "yyyy년 MM월 dd일");

                        String agreeMessage = description1InAgree.replace("{{DATE}}", "\n" + agreeAt) + "\n\n" + description2InAgree;
                        String cancelMessage = description1InReject.replace("{{DATE}}", "\n" + agreeAt) + "\n\n" + description2InReject;

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onNoticeAgreementResult(agreeMessage, cancelMessage);
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
}
