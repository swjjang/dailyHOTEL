package com.twoheart.dailyhotel.screen.main;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainPresenter implements Response.ErrorListener
{
    private BaseActivity mBaseActivity;
    private MainActivity.OnResponsePresenterListener mListener;

    public MainPresenter(BaseActivity baseActivity, MainActivity.OnResponsePresenterListener listener)
    {
        mBaseActivity = baseActivity;
        mListener = listener;
    }

    @Override
    public void onErrorResponse(VolleyError volleyError)
    {
        if (mListener != null)
        {
            mListener.onErrorResponse(volleyError);
        }
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

                String params = String.format("?date_time=%s", Long.toString(lastLookupDateTime));
                DailyNetworkAPI.getInstance().requestEventNewCount(mBaseActivity.getNetworkTag(), params, mDailyEventCountJsonResponseListener, null);
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

                        if (mListener != null)
                        {
                            mListener.setNewIconVisible(true);
                        }
                    } else
                    {
                        DailyPreference.getInstance(mBaseActivity).setNewEvent(false);

                        long currentDateTime = DailyPreference.getInstance(mBaseActivity).getLookUpEventTime();
                        DailyPreference.getInstance(mBaseActivity).setNewEventTime(currentDateTime);

                        if (mListener != null)
                        {
                            mListener.setNewIconVisible(false);
                        }
                    }
                } else
                {

                }
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

                    if (mListener != null)
                    {
                        mListener.onSatisfactionGourmet(ticketName, reservationIndex, checkInTime);
                    }
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

                    if (mListener != null)
                    {
                        mListener.onSatisfactionHotel(hotelName, reservationIndex, checkInDate, checkOutDate);
                    }
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

                if (Util.isTextEmpty(DailyPreference.getInstance(mBaseActivity).getGCMRegistrationId()) == true)
                {
                    Util.requestGoogleCloudMessaging(mBaseActivity, new Util.OnGoogleCloudMessagingListener()
                    {
                        @Override
                        public void onResult(String registrationId)
                        {
                            if (Util.isTextEmpty(registrationId) == true)
                            {
                                return;
                            }

                            Map<String, String> paramHashMap = new HashMap<>();
                            paramHashMap.put("user_idx", userIndex);
                            paramHashMap.put("notification_id", registrationId);
                            paramHashMap.put("device_type", Constants.GCM_DEVICE_TYPE_ANDROID);

                            DailyPreference.getInstance(mBaseActivity).setGCMRegistrationId(registrationId);

                            DailyNetworkAPI.getInstance().requestUserRegisterNotification(mBaseActivity.getNetworkTag(), paramHashMap, null, null);
                        }
                    });
                }

                // 호텔 평가요청
                DailyNetworkAPI.getInstance().requestHotelIsExistRating(mBaseActivity.getNetworkTag(), mHotelSatisfactionRatingExistJsonResponseListener, null);
            } catch (Exception e)
            {
                if (mListener != null)
                {
                    mListener.onError();
                }
            } finally
            {
                if (mListener != null)
                {
                    mListener.unLockUI();
                }
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
                if (Util.isTextEmpty(DailyPreference.getInstance(mBaseActivity).getGCMRegistrationId()) == true)
                {
                    Util.requestGoogleCloudMessaging(mBaseActivity, new Util.OnGoogleCloudMessagingListener()
                    {
                        @Override
                        public void onResult(String registrationId)
                        {
                            if (Util.isTextEmpty(registrationId) == true)
                            {
                                return;
                            }

                            Map<String, String> paramHashMap = new HashMap<>();
                            paramHashMap.put("user_idx", "-1");
                            paramHashMap.put("notification_id", registrationId);
                            paramHashMap.put("device_type", Constants.GCM_DEVICE_TYPE_ANDROID);

                            DailyPreference.getInstance(mBaseActivity).setGCMRegistrationId(registrationId);

                            DailyNetworkAPI.getInstance().requestUserRegisterNotification(mBaseActivity.getNetworkTag(), paramHashMap, null, null);
                        }
                    });
                }
            }
        }
    };
}
