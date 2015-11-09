/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p/>
 * 호텔 리스트에서 호텔 선택 시 호텔의 정보들을 보여주는 화면이다.
 * 예약, 정보, 지도 프래그먼트를 담고 있는 액티비티이다.
 */
package com.twoheart.dailyhotel.activity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.model.SaleRoomInformation;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.HotelDetailLayout;
import com.twoheart.dailyhotel.view.widget.DailyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class HotelDetailActivity extends BaseActivity
{
    private static final int DURATION_HOTEL_IMAGE_SHOW = 4000;

    private HotelDetail mHotelDetail;
    private SaleTime mCheckInSaleTime;

    private int mCurrentImage;
    private SaleRoomInformation mSelectedSaleRoomInformation;
    private boolean mIsStartByShare;
    private String mDefaultImageUrl;

    private HotelDetailLayout mHotelDetailLayout;

    public interface OnUserActionListener
    {
        public void showActionBar();

        public void hideActionBar();

        public void onClickImage(HotelDetail hotelDetail);

        public void startAutoSlide();

        public void stopAutoSlide();

        public void nextSlide();

        public void prevSlide();

        public void onSelectedImagePosition(int position);

        public void doBooking(SaleRoomInformation saleRoomInformation);

        public void doKakaotalkConsult();

        public void showRoomType();

        public void hideRoomType();

        public void showMap();
    }

    private Handler mImageHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            if (isFinishing() == true || mHotelDetailLayout == null)
            {
                return;
            }

            int direction = msg.arg1;

            mCurrentImage = mHotelDetailLayout.getCurrentImage();

            if (direction > 0)
            {
                mCurrentImage++;
            } else if (direction < 0)
            {
                mCurrentImage--;
            }

            mHotelDetailLayout.setCurrentImage(mCurrentImage);

            int autoSlide = msg.arg2;

            if (autoSlide == 1)
            {
                sendEmptyMessageDelayed(0, DURATION_HOTEL_IMAGE_SHOW);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent == null)
        {
            finish();
            return;
        }

        if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_TYPE) == true)
        {
            mIsStartByShare = true;

            long dailyTime = intent.getLongExtra(NAME_INTENT_EXTRA_DATA_DAILYTIME, 0);
            int dayOfDays = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_DAYOFDAYS, -1);

            mCheckInSaleTime = new SaleTime();
            mCheckInSaleTime.setDailyTime(dailyTime);
            mCheckInSaleTime.setOffsetDailyDay(dayOfDays);

            int hotelIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, -1);
            int nights = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, 0);

            mHotelDetail = new HotelDetail(hotelIndex, nights);

            initLayout(null, null);
        } else
        {
            mIsStartByShare = false;

            mCheckInSaleTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);

            int hotelIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, -1);
            int nights = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, 0);

            mHotelDetail = new HotelDetail(hotelIndex, nights);

            String hotelName = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME);
            String hotelImageUrl = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL);
            mDefaultImageUrl = hotelImageUrl;

            if (mCheckInSaleTime == null || hotelIndex == -1 || hotelName == null || nights <= 0)
            {
                Util.restartApp(this);
                return;
            }

            initLayout(hotelName, hotelImageUrl);
        }
    }

    private void initLayout(String hotelName, String imageUrl)
    {
        if (mHotelDetailLayout == null)
        {
            mHotelDetailLayout = new HotelDetailLayout(this, imageUrl);
            mHotelDetailLayout.setUserActionListener(mOnUserActionListener);

            setContentView(mHotelDetailLayout.getView());
        }

        if (hotelName != null)
        {
            setActionBar(hotelName);
        }

        mOnUserActionListener.hideActionBar();
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(HotelDetailActivity.this).recordScreen(Screen.HOTEL_DETAIL);
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        lockUI();
        DailyNetworkAPI.getInstance().requestCommonDatetime(mNetworkTag, mDateTimeJsonResponseListener, this);

        super.onResume();
    }

    @Override
    protected void onPause()
    {
        mOnUserActionListener.stopAutoSlide();

        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        mOnUserActionListener.stopAutoSlide();

        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        if (mHotelDetailLayout != null)
        {
            switch (mHotelDetailLayout.getBookingStatus())
            {
                case HotelDetailLayout.STATUS_BOOKING:
                case HotelDetailLayout.STATUS_NONE:
                    mOnUserActionListener.hideRoomType();
                    return;
            }
        }

        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        releaseUiComponent();

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_BOOKING:
            {
                setResult(resultCode);

                if (resultCode == RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER)
                {
                    finish();
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_LOGIN:
            case CODE_REQUEST_ACTIVITY_USERINFO_UPDATE:
            {
                if (resultCode == RESULT_OK)
                {
                    DailyNetworkAPI.getInstance().requestUserAlive(mNetworkTag, mUserAliveStringResponseListener, this);
                }
                break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onError()
    {
        super.onError();

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.share_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_share:
                if (mDefaultImageUrl == null)
                {
                    if (mHotelDetail.getImageUrlList() != null && mHotelDetail.getImageUrlList().size() > 0)
                    {
                        mDefaultImageUrl = mHotelDetail.getImageUrlList().get(0);
                    }
                }

                KakaoLinkManager.newInstance(this).shareHotel(mHotelDetail.hotelName, mHotelDetail.hotelIndex, //
                    mDefaultImageUrl, //
                    mCheckInSaleTime.getDailyTime(), //
                    mCheckInSaleTime.getOffsetDailyDay(), mHotelDetail.nights);

                // 호텔 공유하기 로그 추가
                SaleTime checkOutSaleTime = mCheckInSaleTime.getClone(mCheckInSaleTime.getOffsetDailyDay() + mHotelDetail.nights);

                HashMap<String, String> params = new HashMap<String, String>();
                params.put(Label.HOTEL_NAME, mHotelDetail.hotelName);
                params.put(Label.CHECK_IN, mCheckInSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"));
                params.put(Label.CHECK_OUT, checkOutSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"));

                SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);
                params.put(Label.CURRENT_TIME, dateFormat2.format(new Date()));

                AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.HOTEL_DETAIL, Action.CLICK, Label.SHARE, params);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void moveToBooking(SaleRoomInformation saleRoomInformation)
    {
        if (saleRoomInformation == null)
        {
            return;
        }

        Intent intent = new Intent(HotelDetailActivity.this, BookingActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALEROOMINFORMATION, saleRoomInformation);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, mHotelDetail.hotelIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, mCheckInSaleTime);

        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_BOOKING);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
    }

    private void moveToUserInfoUpdate(Customer user, int recommender, boolean isDailyUser)
    {
        Intent intent = new Intent(HotelDetailActivity.this, SignupActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CUSTOMER, user);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_RECOMMENDER, recommender);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_ISDAILYUSER, isDailyUser);

        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_USERINFO_UPDATE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
    }

    private boolean isEmptyTextField(String... fieldText)
    {
        for (int i = 0; i < fieldText.length; i++)
        {
            if (Util.isTextEmpty(fieldText[i]) == true)
            {
                return true;
            }
        }

        return false;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UserActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    private OnUserActionListener mOnUserActionListener = new OnUserActionListener()
    {
        @Override
        public void showActionBar()
        {
            setActionBarBackgroundVisible(true);
        }

        @Override
        public void hideActionBar()
        {
            setActionBarBackgroundVisible(false);
        }

        @Override
        public void onClickImage(HotelDetail hotelDetail)
        {
            if (isLockUiComponent() == true)
            {
                return;
            }

            lockUiComponent();

            stopAutoSlide();

            Intent intent = new Intent(HotelDetailActivity.this, ImageDetailListActivity.class);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURLLIST, hotelDetail.getImageUrlList());
            intent.putExtra(NAME_INTENT_EXTRA_DATA_SELECTED_POSOTION, mCurrentImage);
            startActivity(intent);
        }

        @Override
        public void startAutoSlide()
        {
            if (Util.isOverAPI11() == false)
            {
                Message message = mImageHandler.obtainMessage();
                message.what = 0;
                message.arg1 = 1; // 오른쪽으로 이동.
                message.arg2 = 1; // 자동

                mImageHandler.removeMessages(0);
                mImageHandler.sendMessageDelayed(message, DURATION_HOTEL_IMAGE_SHOW);
            } else
            {
                mImageHandler.removeMessages(0);
                mHotelDetailLayout.startAnimationImageView();
            }
        }

        @Override
        public void stopAutoSlide()
        {
            if (Util.isOverAPI11() == false)
            {
                mImageHandler.removeMessages(0);
            } else
            {
                mImageHandler.removeMessages(0);
                mHotelDetailLayout.stopAnimationImageView(false);
            }
        }

        @Override
        public void nextSlide()
        {
            if (Util.isOverAPI11() == true)
            {
                Message message = mImageHandler.obtainMessage();
                message.what = 0;
                message.arg1 = 1; // 오른쪽으로 이동.
                message.arg2 = 0; // 수동

                mImageHandler.removeMessages(0);
                mImageHandler.sendMessage(message);
            }
        }

        @Override
        public void prevSlide()
        {
            if (Util.isOverAPI11() == true)
            {
                Message message = mImageHandler.obtainMessage();
                message.what = 0;
                message.arg1 = -1; // 왼쪽으로 이동.
                message.arg2 = 0; // 수동

                mImageHandler.removeMessages(0);
                mImageHandler.sendMessage(message);
            }
        }

        @Override
        public void onSelectedImagePosition(int position)
        {
            mCurrentImage = position;
        }

        @Override
        public void doBooking(SaleRoomInformation saleRoomInformation)
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            mSelectedSaleRoomInformation = saleRoomInformation;

            lockUI();
            DailyNetworkAPI.getInstance().requestUserAlive(mNetworkTag, mUserAliveStringResponseListener, HotelDetailActivity.this);

            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Label.HOTEL_ROOM_NAME, saleRoomInformation.roomName);
            params.put(Label.HOTEL_ROOM_INDEX, String.valueOf(saleRoomInformation.roomIndex));
            params.put(Label.HOTEL_INDEX, String.valueOf(mHotelDetail.hotelIndex));

            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Label.BOOKING, Action.CLICK, mHotelDetail.hotelName, params);
        }

        @Override
        public void doKakaotalkConsult()
        {
            if (isLockUiComponent() == true || isFinishing() == true)
            {
                return;
            }

            lockUiComponent();

            try
            {
                startActivity(new Intent(Intent.ACTION_SEND, Uri.parse("kakaolink://friend/@%EB%8D%B0%EC%9D%BC%EB%A6%AC%ED%98%B8%ED%85%94")));
            } catch (ActivityNotFoundException e)
            {
                try
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_STORE_GOOGLE_KAKAOTALK)));
                } catch (ActivityNotFoundException e1)
                {
                    Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
                    marketLaunch.setData(Uri.parse(URL_STORE_GOOGLE_KAKAOTALK_WEB));
                    startActivity(marketLaunch);
                }
            }
        }

        @Override
        public void showRoomType()
        {
            if (isLockUiComponent() == true || isFinishing() == true)
            {
                return;
            }

            lockUiComponent();

            if (mHotelDetailLayout != null)
            {
                mHotelDetailLayout.showAnimationRoomType();
            }

            releaseUiComponent();
        }

        @Override
        public void hideRoomType()
        {
            if (isLockUiComponent() == true || isFinishing() == true)
            {
                return;
            }

            lockUiComponent();

            if (mHotelDetailLayout != null)
            {
                mHotelDetailLayout.hideAnimationRoomType();
            }

            releaseUiComponent();
        }

        @Override
        public void showMap()
        {
            if (isLockUiComponent() == true || isFinishing() == true)
            {
                return;
            }

            lockUiComponent();

            Intent intent = new Intent(HotelDetailActivity.this, ZoomMapActivity.class);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME, mHotelDetail.hotelName);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_LATITUDE, mHotelDetail.latitude);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_LONGITUDE, mHotelDetail.longitude);

            startActivity(intent);

            // 호텔 공유하기 로그 추가
            SaleTime checkOutSaleTime = mCheckInSaleTime.getClone(mCheckInSaleTime.getOffsetDailyDay() + mHotelDetail.nights);
            String label = String.format("%s (%s-%s)", mHotelDetail.hotelName, mCheckInSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"), checkOutSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"));

            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.HOTEL_DETAIL, Action.CLICK, label, (long) mHotelDetail.hotelIndex);
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private DailyHotelJsonResponseListener mHotelSaleDetailJsonResponseListener = new DailyHotelJsonResponseListener()
    {

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                if (response == null)
                {
                    throw new NullPointerException("response == null");
                }

                int msgCode = response.getInt("msgCode");

                // 0	성공
                // 4	데이터가 없을시
                // 5	판매 마감시
                switch (msgCode)
                {
                    case 0:
                    {
                        JSONObject dataJSONObject = response.getJSONObject("data");

                        mHotelDetail.setData(dataJSONObject);

                        if (mIsStartByShare == true)
                        {
                            mIsStartByShare = false;
                            initLayout(mHotelDetail.hotelName, null);
                        }

                        if (mHotelDetailLayout != null)
                        {
                            mHotelDetailLayout.setHotelDetail(mHotelDetail, mCurrentImage);
                        }
                        break;
                    }

                    case 5:
                    {
                        if (response.has("msg") == true)
                        {
                            String msg = response.getString("msg");

                            showSimpleDialog(getString(R.string.dialog_notice2), msg, getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                            {
                                @Override
                                public void onDismiss(DialogInterface dialog)
                                {
                                    finish();
                                }
                            });
                        } else
                        {
                            throw new NullPointerException("response == null");
                        }
                        break;
                    }

                    case 4:
                    default:
                    {
                        if (response.has("msg") == true)
                        {
                            String msg = response.getString("msg");

                            DailyToast.showToast(HotelDetailActivity.this, msg, Toast.LENGTH_SHORT);
                            finish();
                        } else
                        {
                            throw new NullPointerException("response == null");
                        }
                        break;
                    }
                }
            } catch (Exception e)
            {
                onError(e);
                finish();
            } finally
            {
                unLockUI();
            }
        }
    };

    private DailyHotelJsonResponseListener mUserInformationJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                if (response == null)
                {
                    throw new NullPointerException("response == null");
                }

                int msg_code = response.getInt("msg_code");

                if (msg_code == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    Customer user = new Customer();
                    user.setEmail(jsonObject.getString("email"));
                    user.setName(jsonObject.getString("name"));
                    user.setPhone(jsonObject.getString("phone"));
                    user.setUserIdx(jsonObject.getString("idx"));

                    // 추천인
                    int recommender = jsonObject.getInt("recommender_code");
                    boolean isDailyUser = jsonObject.getBoolean("is_daily_user");

                    if (isEmptyTextField(new String[]{user.getEmail(), user.getPhone(), user.getName()}) == false && Util.isValidatePhoneNumber(user.getPhone()) == true)
                    {
                        moveToBooking(mSelectedSaleRoomInformation);
                    } else
                    {
                        // 정보 업데이트 화면으로 이동.
                        moveToUserInfoUpdate(user, recommender, isDailyUser);
                    }
                } else
                {
                    unLockUI();

                    String msg = response.getString("msg");

                    DailyToast.showToast(HotelDetailActivity.this, msg, Toast.LENGTH_SHORT);
                }
            } catch (Exception e)
            {
                onError(e);
            }
        }
    };
    private DailyHotelStringResponseListener mUserAliveStringResponseListener = new DailyHotelStringResponseListener()
    {

        @Override
        public void onResponse(String url, String response)
        {

            unLockUI();

            String result = null;

            if (Util.isTextEmpty(response) == false)
            {
                result = response.trim();
            }

            if ("alive".equalsIgnoreCase(result) == true)
            {
                // session alive
                // 사용자 정보 요청.
                DailyNetworkAPI.getInstance().requestUserInformationEx(mNetworkTag, mUserInformationJsonResponseListener, HotelDetailActivity.this);
            } else if ("dead".equalsIgnoreCase(result) == true)
            {
                // session dead
                // 재로그인
                if (sharedPreference.getBoolean(KEY_PREFERENCE_AUTO_LOGIN, false))
                {
                    HashMap<String, String> params = Util.getLoginParams(sharedPreference);

                    DailyNetworkAPI.getInstance().requestUserSignin(mNetworkTag, params, mUserLoginJsonResponseListener, HotelDetailActivity.this);
                } else
                {
                    startLoginActivity();
                }
            } else
            {
                onError();
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
                if (response == null)
                {
                    throw new NullPointerException("response == null.");
                }

                int msg_code = response.getInt("msg_code");

                if (msg_code == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    boolean isSignin = jsonObject.getBoolean("is_signin");

                    if (isSignin == true)
                    {
                        //로그인 성공
                        VolleyHttpClient.createCookie();
                        DailyNetworkAPI.getInstance().requestUserAlive(mNetworkTag, mUserAliveStringResponseListener, HotelDetailActivity.this);
                        return;
                    }
                }

                // 로그인 실패
                // data 초기화
                SharedPreferences.Editor ed = sharedPreference.edit();
                ed.putBoolean(KEY_PREFERENCE_AUTO_LOGIN, false);
                ed.putString(KEY_PREFERENCE_USER_ACCESS_TOKEN, null);
                ed.putString(KEY_PREFERENCE_USER_ID, null);
                ed.putString(KEY_PREFERENCE_USER_PWD, null);
                ed.commit();

                unLockUI();
                startLoginActivity();

            } catch (JSONException e)
            {
                unLockUI();
                onError(e);
            }
        }
    };

    private DailyHotelJsonResponseListener mDateTimeJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            if (isFinishing() == true)
            {
                return;
            }

            try
            {
                if (response == null)
                {
                    throw new NullPointerException("response == null");
                }

                if (mIsStartByShare == true)
                {
                    mCheckInSaleTime.setCurrentTime(response.getLong("currentDateTime"));

                    long shareDailyTime = mCheckInSaleTime.getDayOfDaysHotelDate().getTime();
                    long todayDailyTime = response.getLong("dailyDateTime");

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                    int shareDailyDay = Integer.parseInt(simpleDateFormat.format(new Date(shareDailyTime)));
                    int todayDailyDay = Integer.parseInt(simpleDateFormat.format(new Date(todayDailyTime)));

                    // 지난 날의 호텔인 경우.
                    if (shareDailyDay < todayDailyDay)
                    {
                        DailyToast.showToast(HotelDetailActivity.this, R.string.toast_msg_dont_past_hotelinfo, Toast.LENGTH_LONG);
                        finish();
                        return;
                    }

                    // 호텔 정보를 가져온다.
                    String params = String.format("?hotel_idx=%d&checkin_date=%s&nights=%d", mHotelDetail.hotelIndex, mCheckInSaleTime.getDayOfDaysHotelDateFormat("yyyyMMdd"), mHotelDetail.nights);

                    //                    if (DEBUG == true)
                    //                    {
                    //                        showSimpleDialog(null, params, getString(R.string.dialog_btn_text_confirm), null);
                    //                    }

                    DailyNetworkAPI.getInstance().requestHotelDetailInformation(mNetworkTag, params, mHotelSaleDetailJsonResponseListener, HotelDetailActivity.this);
                } else
                {
                    SaleTime saleTime = new SaleTime();

                    saleTime.setCurrentTime(response.getLong("currentDateTime"));
                    saleTime.setDailyTime(response.getLong("dailyDateTime"));

                    // 호텔 정보를 가져온다.
                    String params = String.format("?hotel_idx=%d&checkin_date=%s&nights=%d", mHotelDetail.hotelIndex, mCheckInSaleTime.getDayOfDaysHotelDateFormat("yyyyMMdd"), mHotelDetail.nights);

                    if (DEBUG == true)
                    {
                        showSimpleDialog(null, params, getString(R.string.dialog_btn_text_confirm), null);
                    }

                    DailyNetworkAPI.getInstance().requestHotelDetailInformation(mNetworkTag, params, mHotelSaleDetailJsonResponseListener, HotelDetailActivity.this);
                }
            } catch (Exception e)
            {
                onError(e);
                unLockUI();

                finish();
            }
        }
    };
}
