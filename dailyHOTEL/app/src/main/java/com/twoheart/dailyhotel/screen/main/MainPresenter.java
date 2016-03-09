package com.twoheart.dailyhotel.screen.main;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONObject;

import java.util.HashMap;

public class MainPresenter implements Response.ErrorListener
{
    private BaseActivity mBaseActivity;
    private MainActivity.OnResponsePresenterListener mListener;

    public MainPresenter(BaseActivity baseActivity, MainActivity.OnResponsePresenterListener listener)
    {
        if (baseActivity == null || listener == null)
        {
            throw new NullPointerException("baseActivity == null || listener == null");
        }

        mBaseActivity = baseActivity;
        mListener = listener;
    }

    @Override
    public void onErrorResponse(VolleyError volleyError)
    {
        mListener.onErrorResponse(volleyError);
    }

    protected void requestCheckServer()
    {
        // 서버 상태 체크
        DailyNetworkAPI.getInstance().requestCheckServer(mBaseActivity.getNetworkTag(), mStatusHealthCheckJsonResponseListener, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                requestSignin();
            }
        });
    }

    public void requestConfiguration()
    {
        DailyNetworkAPI.getInstance().requestCompanyInformation(mBaseActivity.getNetworkTag(), mCompanyInformationJsonResponseListener, this);
    }

    private void requestSignin()
    {
        if (DailyPreference.getInstance(mBaseActivity).isAutoLogin() == true)
        {
            HashMap<String, String> params = Util.getLoginParams(mBaseActivity);
            DailyNetworkAPI.getInstance().requestUserSignin(mBaseActivity.getNetworkTag(), params, mUserLoginJsonResponseListener, this);
        }

        DailyNetworkAPI.getInstance().requestCommonVer(mBaseActivity.getNetworkTag(), mAppVersionJsonResponseListener, this);
    }

    /**
     * 이벤트가 있는지를 요청한다
     */
    protected void requestEvent()
    {
        DailyNetworkAPI.getInstance().requestCommonDatetime(mBaseActivity.getNetworkTag(), new DailyHotelJsonResponseListener()
        {
            @Override
            public void onResponse(String url, JSONObject response)
            {
                long currentDateTime = 0L;

                try
                {
                    currentDateTime = response.getLong("currentDateTime");
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }

                long lastLookupDateTime = DailyPreference.getInstance(mBaseActivity).getNewEventTime();

                DailyPreference.getInstance(mBaseActivity).setLookUpEventTime(currentDateTime);

                DailyNetworkAPI.getInstance().requestEventNewCount(mBaseActivity.getNetworkTag(), Long.toString(lastLookupDateTime), mDailyEventCountJsonResponseListener, null);
            }
        }, null);
    }

    /**
     *
     */
    protected void requestUserAlive()
    {
        DailyNetworkAPI.getInstance().requestUserAlive(mBaseActivity.getNetworkTag(), mUserAliveStringResponseListener, this);
    }

    protected void requestGourmetIsExistRating()
    {
        DailyNetworkAPI.getInstance().requestGourmetIsExistRating(mBaseActivity.getNetworkTag(), mGourmetSatisfactionRatingExistJsonResponseListener, null);
    }

    private void registerNotificationId(final String registrationId, String userIndex)
    {
        DailyHotelJsonResponseListener dailyHotelJsonResponseListener = new DailyHotelJsonResponseListener()
        {
            @Override
            public void onResponse(String url, JSONObject response)
            {
                try
                {
                    int msg_code = response.getInt("msgCode");

                    if (msg_code == 100 && response.has("data") == true)
                    {
                        JSONObject jsonObject = response.getJSONObject("data");

                        int uid = jsonObject.getInt("uid");
                        DailyPreference.getInstance(mBaseActivity).setNotificationUid(uid);
                        DailyPreference.getInstance(mBaseActivity).setGCMRegistrationId(registrationId);
                    }
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            }
        };

        int uid = DailyPreference.getInstance(mBaseActivity).getNotificationUid();
        if (uid < 0)
        {
            DailyNetworkAPI.getInstance().requestUserRegisterNotification(mBaseActivity.getNetworkTag(), registrationId, dailyHotelJsonResponseListener, null);
        } else
        {
            if (registrationId.equalsIgnoreCase(DailyPreference.getInstance(mBaseActivity).getGCMRegistrationId()) == false)
            {
                DailyNetworkAPI.getInstance().requestUserUpdateNotification(mBaseActivity.getNetworkTag(), userIndex, registrationId, Integer.toString(uid), dailyHotelJsonResponseListener, null);
            }
        }
    }


    private DailyHotelJsonResponseListener mStatusHealthCheckJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msg_code");

                if (msgCode == 200)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    boolean isSuspend = jsonObject.getBoolean("isSuspend");

                    if (isSuspend == true)
                    {
                        String title = jsonObject.getString("messageTitle");
                        String message = jsonObject.getString("messageBody");

                        mListener.onCheckServerResponse(title, message);
                    } else
                    {
                        requestSignin();
                    }
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
    };

    private DailyHotelJsonResponseListener mUserLoginJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msg_code = response.getInt("msg_code");

                if (msg_code == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    boolean isSignin = jsonObject.getBoolean("is_signin");

                    if (isSignin == true)
                    {
                        VolleyHttpClient.createCookie();
                        return;
                    }
                }

                // 로그인 실패
                // data 초기화
                DailyPreference.getInstance(mBaseActivity).removeUserInformation();
            } catch (Exception e)
            {
                mListener.onError();
            }
        }
    };

    private DailyHotelJsonResponseListener mAppVersionJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                String maxVersionName;
                String minVersionName;

                switch (Constants.RELEASE_STORE)
                {
                    case N_STORE:
                        maxVersionName = response.getString("nstore_max");
                        minVersionName = response.getString("nstore_min");
                        break;

                    case T_STORE:
                        maxVersionName = response.getString("tstore_max");
                        minVersionName = response.getString("tstore_min");
                        break;

                    case PLAY_STORE:
                    default:
                        maxVersionName = response.getString("play_max");
                        minVersionName = response.getString("play_min");
                        break;
                }

                DailyPreference.getInstance(mBaseActivity).setMaxVersion(maxVersionName);
                DailyPreference.getInstance(mBaseActivity).setMinVersion(minVersionName);

                int maxVersion = Integer.parseInt(maxVersionName.replace(".", ""));
                int minVersion = Integer.parseInt(minVersionName.replace(".", ""));

                mListener.onAppVersionResponse(maxVersion, minVersion);
            } catch (Exception e)
            {
                mListener.onError();
            }
        }
    };

    private DailyHotelJsonResponseListener mCompanyInformationJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msg_code");

                if (msgCode == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");
                    JSONObject companyJSONObject = jsonObject.getJSONObject("companyInfo");

                    String companyName = companyJSONObject.getString("name");
                    String companyCEO = companyJSONObject.getString("ceo");
                    String companyBizRegNumber = companyJSONObject.getString("bizRegNumber");
                    String companyItcRegNumber = companyJSONObject.getString("itcRegNumber");
                    String address = companyJSONObject.getString("address1");
                    String phoneNumber = companyJSONObject.getString("phoneNumber1");
                    String fax = companyJSONObject.getString("fax1");
                    String privacyEmail = "privacy.korea@dailyhotel.com";//companyJSONObject.getString("privacy");

                    DailyPreference.getInstance(mBaseActivity).setCompanyInformation(companyName//
                        , companyCEO, companyBizRegNumber, companyItcRegNumber, address, phoneNumber, fax, privacyEmail);
                }

                mListener.onConfigurationResponse();
            } catch (Exception e)
            {
                mListener.onError();
            }
        }
    };

    private DailyHotelJsonResponseListener mDailyEventCountJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msg_code");

                if (msgCode == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    int count = jsonObject.getInt("count");

                    if (count > 0)
                    {
                        DailyPreference.getInstance(mBaseActivity).setNewEvent(true);

                        long currentDateTime = DailyPreference.getInstance(mBaseActivity).getLookUpEventTime();
                        DailyPreference.getInstance(mBaseActivity).setNewEventTime(currentDateTime);
                    }
                }

                mListener.updateNewEvent();
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
    };

    private DailyHotelJsonResponseListener mGourmetSatisfactionRatingExistJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msg_code = response.getInt("msg_code");

                if (msg_code == 0 && response.has("data") == true)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    long checkInTime = jsonObject.getLong("sday");
                    String ticketName = jsonObject.getString("ticket_name");
                    int reservationIndex = jsonObject.getInt("reservation_rec_idx");

                    mListener.onSatisfactionGourmet(ticketName, reservationIndex, checkInTime);
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
    };

    private DailyHotelJsonResponseListener mHotelSatisfactionRatingExistJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msg_code = response.getInt("msg_code");

                if (msg_code == 0 && response.has("data") == true)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    //					String guestName = jsonObject.getString("guest_name");
                    //					String roomName = jsonObject.getString("room_name");
                    long checkInDate = jsonObject.getLong("checkin_date");
                    long checkOutDate = jsonObject.getLong("checkout_date");
                    String hotelName = jsonObject.getString("hotel_name");
                    int reservationIndex = jsonObject.getInt("reserv_idx");

                    mListener.onSatisfactionHotel(hotelName, reservationIndex, checkInDate, checkOutDate);
                } else
                {
                    requestGourmetIsExistRating();
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
    };

    private DailyHotelJsonResponseListener mUserInfomationJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                final String userIndex = response.getString("idx");

                AnalyticsManager.getInstance(mBaseActivity).setUserIndex(userIndex);

                Util.requestGoogleCloudMessaging(mBaseActivity, new Util.OnGoogleCloudMessagingListener()
                {
                    @Override
                    public void onResult(String registrationId)
                    {
                        if (Util.isTextEmpty(registrationId) == true)
                        {
                            return;
                        }

                        registerNotificationId(registrationId, userIndex);
                    }
                });

                // 호텔 평가요청
                DailyNetworkAPI.getInstance().requestHotelIsExistRating(mBaseActivity.getNetworkTag(), mHotelSatisfactionRatingExistJsonResponseListener, null);
            } catch (Exception e)
            {
                mListener.onError();
            }
        }
    };

    private DailyHotelStringResponseListener mUserAliveStringResponseListener = new DailyHotelStringResponseListener()
    {
        @Override
        public void onResponse(String url, String response)
        {
            String result = null;

            if (false == Util.isTextEmpty(response))
            {
                result = response.trim();
            }

            if (true == "alive".equalsIgnoreCase(result))
            {
                // session alive
                // 호텔 평가를 위한 사용자 정보 조회
                DailyNetworkAPI.getInstance().requestUserInformation(mBaseActivity.getNetworkTag(), mUserInfomationJsonResponseListener, MainPresenter.this);
            } else
            {
                AnalyticsManager.getInstance(mBaseActivity).setUserIndex(null);

                Util.requestGoogleCloudMessaging(mBaseActivity, new Util.OnGoogleCloudMessagingListener()
                {
                    @Override
                    public void onResult(String registrationId)
                    {
                        if (Util.isTextEmpty(registrationId) == true)
                        {
                            return;
                        }

                        registerNotificationId(registrationId, null);
                    }
                });
            }
        }
    };
}
