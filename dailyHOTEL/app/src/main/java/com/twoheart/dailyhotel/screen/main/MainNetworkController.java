package com.twoheart.dailyhotel.screen.main;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Review;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyAssert;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONObject;

import java.util.Map;

public class MainNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void updateNewEvent(boolean isNewEvent, boolean isNewCoupon, boolean isNewNotices);

        void onReviewGourmet(Review review);

        void onReviewHotel(Review review);

        void onCheckServerResponse(String title, String message);

        void onAppVersionResponse(String currentVersion, String forceVersion);

        void onConfigurationResponse();

        void onNoticeAgreement(String message, boolean isFirstTimeBuyer);

        void onNoticeAgreementResult(String agreeMessage, String cancelMessage);

        void onCommonDateTime(long currentDateTime, long openDateTime, long closeDateTime);
    }

    public MainNetworkController(Context context, String networkTag, OnNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestCheckServer()
    {
        // 서버 상태 체크
        DailyNetworkAPI.getInstance(mContext).requestCheckServer(mNetworkTag, mStatusHealthCheckJsonResponseListener);
    }

    public void requestUserInformation()
    {
        DailyNetworkAPI.getInstance(mContext).requestUserProfile(mNetworkTag, mUserProfileJsonResponseListener);
    }

    /**
     * 이벤트가 있는지를 요청한다
     */
    public void requestCommonDatetime()
    {
        DailyNetworkAPI.getInstance(mContext).requestCommonDateTime(mNetworkTag, new DailyHotelJsonResponseListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
            }

            @Override
            public void onResponse(String url, Map<String, String> params, JSONObject response)
            {
                try
                {
                    int msgCode = response.getInt("msgCode");
                    DailyAssert.assertEquals(100, msgCode);

                    if (msgCode == 100)
                    {
                        JSONObject dataJSONObject = response.getJSONObject("data");
                        DailyAssert.assertNotNull(dataJSONObject);

                        long currentDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("currentDateTime"), DailyCalendar.ISO_8601_FORMAT);
                        long openDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("openDateTime"), DailyCalendar.ISO_8601_FORMAT);
                        long closeDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("closeDateTime"), DailyCalendar.ISO_8601_FORMAT);

                        DailyAssert.assertNotNull(currentDateTime);
                        DailyAssert.assertNotNull(openDateTime);
                        DailyAssert.assertNotNull(closeDateTime);

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onCommonDateTime(currentDateTime, openDateTime, closeDateTime);
                    } else
                    {
                        String message = response.getString("msg");
                        DailyAssert.fail(message);
                    }
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                    DailyAssert.fail(e.getMessage());
                }
            }
        });
    }

    protected void requestEventNCouponNNoticeNewCount(String lastEventTime, String lastCouponTime, String lastNoticeTime)
    {
        if (Util.isTextEmpty(lastEventTime, lastCouponTime, lastNoticeTime) == true)
        {
            return;
        }

        DailyNetworkAPI.getInstance(mContext).requestEventNCouponNNoticeNewCount(mNetworkTag, lastEventTime, lastCouponTime, lastNoticeTime, mDailyEventCountJsonResponseListener);
    }

    public void requestVersion()
    {
        DailyNetworkAPI.getInstance(mContext).requestCommonVer(mNetworkTag, mAppVersionJsonResponseListener);
    }

    public void requestReviewGourmet()
    {
        DailyNetworkAPI.getInstance(mContext).requestGourmetReviewInformation(mNetworkTag, mReviewGourmetJsonResponseListener);
    }

    public void requestNoticeAgreement()
    {
        DailyNetworkAPI.getInstance(mContext).requestNoticeAgreement(mNetworkTag, mNoticeAgreementJsonResponseListener);
    }

    public void requestNoticeAgreementResult(boolean isAgree)
    {
        DailyNetworkAPI.getInstance(mContext).requestNoticeAgreementResult(mNetworkTag, isAgree, mNoticeAgreementResultJsonResponseListener);
    }

    private DailyHotelJsonResponseListener mStatusHealthCheckJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msg_code");
                DailyAssert.assertEquals(200, msgCode);

                if (msgCode == 200)
                {
                    JSONObject jsonObject = response.getJSONObject("data");
                    DailyAssert.assertNotNull(jsonObject);

                    boolean isSuspend = jsonObject.getBoolean("isSuspend");

                    if (isSuspend == true)
                    {
                        String title = jsonObject.getString("messageTitle");
                        String message = jsonObject.getString("messageBody");

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onCheckServerResponse(title, message);
                    } else
                    {
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onCheckServerResponse(null, null);
                    }
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
                DailyAssert.fail(e.getMessage());
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            ((OnNetworkControllerListener) mOnNetworkControllerListener).onCheckServerResponse(null, null);
            DailyAssert.fail(volleyError.getMessage());
        }
    };

    private DailyHotelJsonResponseListener mAppVersionJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");
                DailyAssert.assertEquals(100, msgCode);

                if (msgCode != 100)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");

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

                    DailyAssert.assertNotNull(minVersionName);
                    DailyAssert.assertNotNull(maxVersionName);

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onAppVersionResponse(maxVersionName, minVersionName);
                } else
                {
                    String message = response.getString("msg");

                    mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                }
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mOnNetworkControllerListener.onErrorPopupMessage(-1, mContext.getString(R.string.act_base_network_connect));
        }
    };

    private DailyHotelJsonResponseListener mDailyEventCountJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
        }

        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                boolean isExistNewEvent = false;
                boolean isExistNewCoupon = false;
                boolean isExistNewNotices = false;

                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");

                    isExistNewEvent = dataJSONObject.getBoolean("isExistNewEvent");
                    isExistNewCoupon = dataJSONObject.getBoolean("isExistNewCoupon");
                    isExistNewNotices = dataJSONObject.getBoolean("isExistNewNotices");
                }

                ((OnNetworkControllerListener) mOnNetworkControllerListener).updateNewEvent(isExistNewEvent, isExistNewCoupon, isExistNewNotices);
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
    };

    private DailyHotelJsonResponseListener mReviewGourmetJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 1000 && response.has("data") == true)
                {
                    Review review = new Review(response.getJSONObject("data"));

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
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }
    };

    private DailyHotelJsonResponseListener mReviewHotelJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100 && response.has("data") == true)
                {
                    Review review = new Review(response.getJSONObject("data"));

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onReviewHotel(review);
                } else
                {
                    requestReviewGourmet();
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }
    };

    private DailyHotelJsonResponseListener mUserProfileJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");
                DailyAssert.assertEquals(100, msgCode);

                if (msgCode == 100)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    final String userIndex = jsonObject.getString("userIdx");
                    final String userType = jsonObject.has("userType") == true ? jsonObject.getString("userType") : AnalyticsManager.ValueType.EMPTY;
                    AnalyticsManager.getInstance(mContext).setUserInformation(userIndex, userType);

                    AnalyticsManager.getInstance(mContext).startApplication();

                    // 누적 적립금 판단.
                    DailyNetworkAPI.getInstance(mContext).requestUserProfileBenefit(mNetworkTag, mUserProfileBenefitJsonResponseListener);

                    // 호텔 평가요청
                    DailyNetworkAPI.getInstance(mContext).requestStayReviewInformation(mNetworkTag, mReviewHotelJsonResponseListener);

//                    String text = "{\n" +
//                        "  \"msgCode\": 100,\n" +
//                        "  \"msg\": \"Request success!\",\n" +
//                        "  \"data\": {\n" +
//                        "    \"reserveIdx\": 1283001,\n" +
//                        "    \"requiredCommentReview\": true,\n" +
//                        "    \"reviewItem\": {\n" +
//                        "      \"serviceType\": \"HOTEL\",\n" +
//                        "      \"itemIdx\": 23173,\n" +
//                        "      \"itemName\": \"인계동 벅스79 2호점\",\n" +
//                        "      \"baseImagePath\": \"https://img.dailyhotel.me/resources/images/\",\n" +
//                        "      \"itemImagePath\": \"{\\\"dh_23173/\\\": [\\\"01.jpg\\\"]}\",\n" +
//                        "      \"useStartDate\": \"2016-11-23T15:00:00+09:00\",\n" +
//                        "      \"useEndDate\": \"2016-11-24T13:00:00+09:00\"\n" +
//                        "    },\n" +
//                        "    \"reviewScoreQuestions\": [\n" +
//                        "      {\n" +
//                        "        \"title\": \"청결\",\n" +
//                        "        \"description\": \"방이 정말 깨끗했나요?\",\n" +
//                        "        \"answerCode\": \"H_CLEAN\",\n" +
//                        "        \"answerValues\": []\n" +
//                        "      },\n" +
//                        "      {\n" +
//                        "        \"title\": \"위치\",\n" +
//                        "        \"description\": \"접근성이 좋은 위치에 있나요?\",\n" +
//                        "        \"answerCode\": \"H_LOCATION\",\n" +
//                        "        \"answerValues\": []\n" +
//                        "      },\n" +
//                        "      {\n" +
//                        "        \"title\": \"서비스\",\n" +
//                        "        \"description\": \"직원들이 친절하고 세심했나요?\",\n" +
//                        "        \"answerCode\": \"H_KIND\",\n" +
//                        "        \"answerValues\": []\n" +
//                        "      },\n" +
//                        "      {\n" +
//                        "        \"title\": \"시설\",\n" +
//                        "        \"description\": \"전체적으로 시설이 좋았나요?\",\n" +
//                        "        \"answerCode\": \"H_FACILITY\",\n" +
//                        "        \"answerValues\": []\n" +
//                        "      }\n" +
//                        "    ],\n" +
//                        "    \"reviewPickQuestions\": [\n" +
//                        "      {\n" +
//                        "        \"title\": \"방문 형태\",\n" +
//                        "        \"description\": \"어떤 여행이었나요?\",\n" +
//                        "        \"answerCode\": \"H_USE_CATEGORY\",\n" +
//                        "        \"answerValues\": [\n" +
//                        "          {\n" +
//                        "            \"code\": \"H_FAMILY_CHILD\",\n" +
//                        "            \"description\": \"가족 여행\\n(13세 이하 자녀 동반)\"\n" +
//                        "          },\n" +
//                        "          {\n" +
//                        "            \"code\": \"H_FAMILY\",\n" +
//                        "            \"description\": \"가족 여행\"\n" +
//                        "          },\n" +
//                        "          {\n" +
//                        "            \"code\": \"H_BUSINESS\",\n" +
//                        "            \"description\": \"비즈니스\"\n" +
//                        "          },\n" +
//                        "          {\n" +
//                        "            \"code\": \"H_COUPLE\",\n" +
//                        "            \"description\": \"커플 여행\"\n" +
//                        "          },\n" +
//                        "          {\n" +
//                        "            \"code\": \"H_FRIEND\",\n" +
//                        "            \"description\": \"친구와의 여행\"\n" +
//                        "          },\n" +
//                        "          {\n" +
//                        "            \"code\": \"H_ALONE\",\n" +
//                        "            \"description\": \"나홀로 여행\"\n" +
//                        "          }\n" +
//                        "        ]\n" +
//                        "      }\n" +
//                        "    ]\n" +
//                        "  }\n" +
//                        "}";
//
//                    mReviewHotelJsonResponseListener.onResponse(null, null, new JSONObject(text));

                } else
                {
                    mOnNetworkControllerListener.onError(null);
                }
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            if (volleyError.networkResponse != null && volleyError.networkResponse.statusCode == 401)
            {
                mOnNetworkControllerListener.onErrorResponse(volleyError);
            } else
            {
                mOnNetworkControllerListener.onErrorPopupMessage(-1, mContext.getString(R.string.act_base_network_connect));
            }
        }
    };

    private DailyHotelJsonResponseListener mUserProfileBenefitJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");
                DailyAssert.assertEquals(100, msgCode);

                if (msgCode == 100)
                {
                    JSONObject jsonObject = response.getJSONObject("data");
                    DailyAssert.assertNotNull(jsonObject);

                    boolean isExceedBonus = jsonObject.getBoolean("exceedLimitedBonus");

                    DailyPreference.getInstance(mContext).setUserExceedBonus(isExceedBonus);
                    AnalyticsManager.getInstance(mContext).setExceedBonus(isExceedBonus);
                } else
                {
                    // 에러가 나도 특별히 해야할일은 없다.
                    DailyAssert.fail();
                }
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mOnNetworkControllerListener.onErrorResponse(volleyError);
        }
    };

    private DailyHotelJsonResponseListener mNoticeAgreementJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");

                    String message01 = dataJSONObject.getString("description1");
                    String message02 = dataJSONObject.getString("description2");
                    boolean isFirstTimeBuyer = dataJSONObject.getBoolean("isFirstTimeBuyer");

                    String message = message01 + "\n\n" + message02;

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onNoticeAgreement(message, isFirstTimeBuyer);
                }
            } catch (Exception e)
            {
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mOnNetworkControllerListener.onErrorResponse(volleyError);
        }
    };

    private DailyHotelJsonResponseListener mNoticeAgreementResultJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");

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
                //            } catch (ParseException e)
                //            {
                //                if (Constants.DEBUG == false)
                //                {
                //                    Crashlytics.log("Url: " + url);
                //                }
                //
                //                mOnNetworkControllerListener.onError(e);
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mOnNetworkControllerListener.onErrorResponse(volleyError);
        }
    };
}
