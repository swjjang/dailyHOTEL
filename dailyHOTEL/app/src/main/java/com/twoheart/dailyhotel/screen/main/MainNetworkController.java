package com.twoheart.dailyhotel.screen.main;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void updateNewEvent();

        void onSatisfactionGourmet(String ticketName, int reservationIndex, long checkInTime);

        void onSatisfactionHotel(String hotelName, int reservationIndex, long checkInTime, long checkOutTime);

        void onCheckServerResponse(String title, String message);

        void onAppVersionResponse(int maxVersion, int minVersion);

        void onConfigurationResponse();

        void onNoticeAgreement(String message, boolean isFirstTimeBuyer);

        void onNoticeAgreementResult(String agreeMessage, String cancelMessage);
    }

    public MainNetworkController(Context context, String networkTag, OnNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError)
    {
        mOnNetworkControllerListener.onErrorResponse(volleyError);
    }

    protected void requestCheckServer()
    {
        // 서버 상태 체크
        DailyNetworkAPI.getInstance(mContext).requestCheckServer(mNetworkTag, mStatusHealthCheckJsonResponseListener);
    }

    public void requestConfiguration()
    {
        DailyNetworkAPI.getInstance(mContext).requestCompanyInformation(mNetworkTag, mCompanyInformationJsonResponseListener, mCompanyInformationJsonResponseListener);
    }

    public void requestUserInformation()
    {
        DailyNetworkAPI.getInstance(mContext).requestUserInformation(mNetworkTag, mUserInfomationJsonResponseListener, mUserInfomationJsonResponseListener);
    }

    /**
     * 이벤트가 있는지를 요청한다
     */
    protected void requestEvent()
    {
        DailyNetworkAPI.getInstance(mContext).requestCommonDatetime(mNetworkTag, new DailyHotelJsonResponseListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {

            }

            @Override
            public void onResponse(String url, JSONObject response)
            {
                long currentDateTime = 0L;

                try
                {
                    currentDateTime = response.getLong("currentDateTime");

                    // 요청하면서 CS운영시간도 같이 받아온다.
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH", Locale.KOREA);
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                    String text = mContext.getString(R.string.dialog_message_cs_operating_time //
                        , Integer.parseInt(simpleDateFormat.format(new Date(response.getLong("openDateTime")))) //
                        , Integer.parseInt(simpleDateFormat.format(new Date(response.getLong("closeDateTime")))));

                    DailyPreference.getInstance(mContext).setOperationTimeMessage(text);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }

                long lastLookupDateTime = DailyPreference.getInstance(mContext).getNewEventTime();

                currentDateTime -= 3600 * 1000 * 9;

                DailyPreference.getInstance(mContext).setLookUpEventTime(currentDateTime);

                Calendar calendar = DailyCalendar.getInstance();
                calendar.setTimeZone(TimeZone.getTimeZone("GMT+09:00"));
                calendar.setTimeInMillis(currentDateTime);

                DailyNetworkAPI.getInstance(mContext).requestEventNewCount(mNetworkTag, Util.getISO8601String(calendar.getTime()), mDailyEventCountJsonResponseListener, null);
            }
        }, null);
    }

    protected void requestGourmetIsExistRating()
    {
        DailyNetworkAPI.getInstance(mContext).requestGourmetIsExistRating(mNetworkTag, mGourmetSatisfactionRatingExistJsonResponseListener, null);
    }

    public void registerNotificationId(final String registrationId, String userIndex)
    {
        DailyHotelJsonResponseListener dailyHotelJsonResponseListener = new DailyHotelJsonResponseListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {

            }

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
                        DailyPreference.getInstance(mContext).setNotificationUid(uid);
                        DailyPreference.getInstance(mContext).setGCMRegistrationId(registrationId);
                    }
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            }
        };

        int uid = DailyPreference.getInstance(mContext).getNotificationUid();
        if (uid < 0)
        {
            DailyNetworkAPI.getInstance(mContext).requestUserRegisterNotification(mNetworkTag, registrationId, dailyHotelJsonResponseListener, null);
        } else
        {
            if (registrationId.equalsIgnoreCase(DailyPreference.getInstance(mContext).getGCMRegistrationId()) == false)
            {
                DailyNetworkAPI.getInstance(mContext).requestUserUpdateNotification(mNetworkTag, userIndex, registrationId, Integer.toString(uid), dailyHotelJsonResponseListener, null);
            }
        }
    }

    public void requestNoticeAgreement(boolean isAuthorization)
    {
        DailyNetworkAPI.getInstance(mContext).requestNoticeAgreement(mNetworkTag, isAuthorization, mNoticeAgreementJsonResponseListener);
    }

    public void requestNoticeAgreementResult(boolean isAuthorization, boolean isAgree)
    {
        DailyNetworkAPI.getInstance(mContext).requestNoticeAgreementResult(mNetworkTag, isAuthorization, isAgree, mNoticeAgreementResultJsonResponseListener);
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

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onCheckServerResponse(title, message);
                    } else
                    {
                        DailyNetworkAPI.getInstance(mContext).requestCommonVer(mNetworkTag, mAppVersionJsonResponseListener);
                    }
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            DailyNetworkAPI.getInstance(mContext).requestCommonVer(mNetworkTag, mAppVersionJsonResponseListener);
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

                DailyPreference.getInstance(mContext).setMaxVersion(maxVersionName);
                DailyPreference.getInstance(mContext).setMinVersion(minVersionName);

                int maxVersion = Integer.parseInt(maxVersionName.replace(".", ""));
                int minVersion = Integer.parseInt(minVersionName.replace(".", ""));

                ((OnNetworkControllerListener) mOnNetworkControllerListener).onAppVersionResponse(maxVersion, minVersion);
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
                    String privacyEmail = companyJSONObject.getString("privacyManager");

                    DailyPreference.getInstance(mContext).setCompanyInformation(companyName//
                        , companyCEO, companyBizRegNumber, companyItcRegNumber, address, phoneNumber, fax, privacyEmail);
                }

                ((OnNetworkControllerListener) mOnNetworkControllerListener).onConfigurationResponse();
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
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msg_code");

                if (msgCode == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    int count = jsonObject.getInt("count");
                    long currentDateTime = DailyPreference.getInstance(mContext).getLookUpEventTime();

                    if (count > 0)
                    {
                        DailyPreference.getInstance(mContext).setNewEvent(true);
                        DailyPreference.getInstance(mContext).setNewEventTime(currentDateTime);
                    } else
                    {
                        if (currentDateTime == 0)
                        {
                            DailyPreference.getInstance(mContext).setNewEvent(false);
                            DailyPreference.getInstance(mContext).setNewEventTime(currentDateTime);
                        }
                    }
                }

                ((OnNetworkControllerListener) mOnNetworkControllerListener).updateNewEvent();
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
    };

    private DailyHotelJsonResponseListener mGourmetSatisfactionRatingExistJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

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

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onSatisfactionGourmet(ticketName, reservationIndex, checkInTime);
                } else
                {
                    // 고메 이벤트까지 없으면 첫구매 이벤트 확인한다.
                    requestNoticeAgreement(true);
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
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msg_code");

                if (msgCode == 0 && response.has("data") == true)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    //					String guestName = jsonObject.getString("guest_name");
                    //					String roomName = jsonObject.getString("room_name");
                    long checkInDate = jsonObject.getLong("checkin_date");
                    long checkOutDate = jsonObject.getLong("checkout_date");
                    String hotelName = jsonObject.getString("hotel_name");
                    int reservationIndex = jsonObject.getInt("reserv_idx");

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onSatisfactionHotel(hotelName, reservationIndex, checkInDate, checkOutDate);
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
                AnalyticsManager.getInstance(mContext).setUserIndex(userIndex);

                Util.requestGoogleCloudMessaging(mContext, new Util.OnGoogleCloudMessagingListener()
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
                DailyNetworkAPI.getInstance(mContext).requestHotelIsExistRating(mNetworkTag, mHotelSatisfactionRatingExistJsonResponseListener, null);
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

    private DailyHotelJsonResponseListener mNoticeAgreementJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mOnNetworkControllerListener.onErrorResponse(volleyError);
        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 0)
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
                mOnNetworkControllerListener.onError(e);
            }
        }
    };

    private DailyHotelJsonResponseListener mNoticeAgreementResultJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mOnNetworkControllerListener.onErrorResponse(volleyError);
        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 0)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");

                    String agreeAt = dataJSONObject.getString("agreeAt");
                    String description1InAgree = dataJSONObject.getString("description1InAgree");
                    String description2InAgree = dataJSONObject.getString("description2InAgree");
                    String description1InReject = dataJSONObject.getString("description1InReject");
                    String description2InReject = dataJSONObject.getString("description2InReject");

                    String agreeMessage = description1InAgree.replace("{{DATE}}", agreeAt) + "\n\n" + description2InAgree;
                    String cancelMessage = description1InReject.replace("{{DATE}}", agreeAt) + "\n\n" + description2InReject;

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onNoticeAgreementResult(agreeMessage, cancelMessage);
                }
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }
    };
}
