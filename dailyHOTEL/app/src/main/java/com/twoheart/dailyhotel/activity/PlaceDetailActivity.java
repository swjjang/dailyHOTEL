/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * 호텔 리스트에서 호텔 선택 시 호텔의 정보들을 보여주는 화면이다.
 * 예약, 정보, 지도 프래그먼트를 담고 있는 액티비티이다.
 */
package com.twoheart.dailyhotel.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.PlaceDetail;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.TicketInformation;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.HotelDetailLayout;
import com.twoheart.dailyhotel.view.PlaceDetailLayout;
import com.twoheart.dailyhotel.view.widget.DailyToast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public abstract class PlaceDetailActivity extends BaseActivity
{
    private static final int DURATION_HOTEL_IMAGE_SHOW = 4000;

    protected Toolbar mToolbar;
    protected PlaceDetailLayout mPlaceDetailLayout;
    protected PlaceDetail mPlaceDetail;
    protected int mCurrentImage;
    protected boolean mIsStartByShare;
    private SaleTime mCheckInSaleTime;
    private TicketInformation mSelectedTicketInformation;
    private String mDefaultImageUrl;

    public interface OnUserActionListener
    {
        void showActionBar();

        void hideActionBar();

        void onClickImage(PlaceDetail placeDetail);

        void onSelectedImagePosition(int position);

        void doBooking(TicketInformation ticketInformation);

        void doKakaotalkConsult();

        void showTicketInformationLayout();

        void hideTicketInformationLayout();

        void showMap();

        void finish();
    }

    public interface OnImageActionListener
    {
        void nextSlide();

        void prevSlide();
    }

    private Handler mImageHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            if (isFinishing() == true || mPlaceDetailLayout == null)
            {
                return;
            }

            int direction = msg.arg1;

            mCurrentImage = mPlaceDetailLayout.getCurrentImage();

            if (direction > 0)
            {
                mCurrentImage++;
            } else if (direction < 0)
            {
                mCurrentImage--;
            }

            mPlaceDetailLayout.setCurrentImage(mCurrentImage);

            int autoSlide = msg.arg2;

            if (autoSlide == 1)
            {
                sendEmptyMessageDelayed(0, DURATION_HOTEL_IMAGE_SHOW);
            }
        }
    };

    protected abstract PlaceDetailLayout getLayout(BaseActivity activity, String imageUrl);

    protected abstract void requestPlaceDetailInformation(PlaceDetail placeDetail, SaleTime checkInSaleTime);

    protected abstract PlaceDetail createPlaceDetail(Intent intent);

    protected abstract void shareKakao(PlaceDetail placeDetail, String imageUrl, SaleTime checkInSaleTime, SaleTime checkOutSaleTime);

    protected abstract void processBooking(TicketInformation ticketInformation, SaleTime checkInSaleTime);

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

            mPlaceDetail = createPlaceDetail(intent);

            if (mPlaceDetail == null)
            {
                Util.restartApp(this);
                return;
            }

            initLayout(null, null);
        } else
        {
            mIsStartByShare = false;

            mCheckInSaleTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);

            mPlaceDetail = createPlaceDetail(intent);

            String placeName = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_PLACENAME);
            String imageUrl = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL);
            mDefaultImageUrl = imageUrl;

            if (mCheckInSaleTime == null || mPlaceDetail == null || placeName == null)
            {
                Util.restartApp(this);
                return;
            }

            initLayout(placeName, imageUrl);
        }
    }

    protected void initLayout(String placeName, String imageUrl)
    {
        if (mPlaceDetailLayout == null)
        {
            mPlaceDetailLayout = getLayout(this, imageUrl);
            mPlaceDetailLayout.setUserActionListener(mOnUserActionListener);
            mPlaceDetailLayout.setImageActionListener(mOnImageActionListener);

            setContentView(mPlaceDetailLayout.getLayout());
        }

        if (placeName != null)
        {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            initToolbar(mToolbar, placeName, true);
        }

        mOnUserActionListener.hideActionBar();
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(PlaceDetailActivity.this).recordScreen(Screen.HOTEL_DETAIL);
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
    public void onBackPressed()
    {
        if (mPlaceDetailLayout != null)
        {
            switch (mPlaceDetailLayout.getBookingStatus())
            {
                case HotelDetailLayout.STATUS_BOOKING:
                case HotelDetailLayout.STATUS_NONE:
                    mOnUserActionListener.hideTicketInformationLayout();
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

                if (resultCode == RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
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
                    if (mPlaceDetail.getImageInformationList() != null && mPlaceDetail.getImageInformationList().size() > 0)
                    {
                        mDefaultImageUrl = mPlaceDetail.getImageInformationList().get(0).url;
                    }
                }

                shareKakao(mPlaceDetail, mDefaultImageUrl, mCheckInSaleTime, null);

                // 호텔 공유하기 로그 추가
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(Label.HOTEL_NAME, mPlaceDetail.name);
                params.put(Label.CHECK_IN, mCheckInSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"));

                SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);
                params.put(Label.CURRENT_TIME, dateFormat2.format(new Date()));

                AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.HOTEL_DETAIL, Action.CLICK, Label.SHARE, params);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void moveToUserInfoUpdate(Customer user, int recommender, boolean isDailyUser)
    {
        Intent intent = new Intent(PlaceDetailActivity.this, SignupActivity.class);
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
            setToolbarTransparent(mToolbar, false);
        }

        @Override
        public void hideActionBar()
        {
            setToolbarTransparent(mToolbar, true);
        }

        @Override
        public void onClickImage(PlaceDetail ticketDetailDto)
        {
            if (isLockUiComponent() == true)
            {
                return;
            }

            lockUiComponent();

            Intent intent = new Intent(PlaceDetailActivity.this, ImageDetailListActivity.class);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURLLIST, ticketDetailDto.getImageInformationList());
            intent.putExtra(NAME_INTENT_EXTRA_DATA_SELECTED_POSOTION, mCurrentImage);
            startActivity(intent);
        }

        @Override
        public void onSelectedImagePosition(int position)
        {
            mCurrentImage = position;
        }

        @Override
        public void doBooking(TicketInformation ticketInformation)
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            mSelectedTicketInformation = ticketInformation;

            lockUI();
            DailyNetworkAPI.getInstance().requestUserAlive(mNetworkTag, mUserAliveStringResponseListener, PlaceDetailActivity.this);

            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Label.FNB_TICKET_NAME, ticketInformation.name);
            params.put(Label.FNB_TICKET_INDEX, String.valueOf(ticketInformation.index));
            params.put(Label.FNB_INDEX, String.valueOf(mPlaceDetail.index));

            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Label.BOOKING, Action.CLICK, mPlaceDetail.name, params);
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
                startActivity(new Intent(Intent.ACTION_SEND, Uri.parse("kakaolink://friend/%40%EB%8D%B0%EC%9D%BC%EB%A6%AC%EA%B3%A0%EB%A9%94")));
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
        public void showTicketInformationLayout()
        {
            if (isLockUiComponent() == true || isFinishing() == true)
            {
                return;
            }

            lockUiComponent();

            if (mPlaceDetailLayout != null)
            {
                mPlaceDetailLayout.showAnimationTicketInformationLayout();
            }

            releaseUiComponent();
        }

        @Override
        public void hideTicketInformationLayout()
        {
            if (isLockUiComponent() == true || isFinishing() == true)
            {
                return;
            }

            lockUiComponent();

            if (mPlaceDetailLayout != null)
            {
                mPlaceDetailLayout.hideAnimationTicketInformationLayout();
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

            Intent intent = new Intent(PlaceDetailActivity.this, ZoomMapActivity.class);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACENAME, mPlaceDetail.name);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_LATITUDE, mPlaceDetail.latitude);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_LONGITUDE, mPlaceDetail.longitude);

            startActivity(intent);

            // 호텔 공유하기 로그 추가
            SaleTime checkOutSaleTime = mCheckInSaleTime.getClone(mCheckInSaleTime.getOffsetDailyDay());
            String label = String.format("%s (%s-%s)", mPlaceDetail.name, mCheckInSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"), checkOutSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"));

            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.HOTEL_DETAIL, Action.CLICK, label, (long) mPlaceDetail.index);
        }

        @Override
        public void finish()
        {
            PlaceDetailActivity.super.finish();
        }
    };

    private OnImageActionListener mOnImageActionListener = new OnImageActionListener()
    {
        @Override
        public void nextSlide()
        {
            Message message = mImageHandler.obtainMessage();
            message.what = 0;
            message.arg1 = 1; // 오른쪽으로 이동.
            message.arg2 = 0; // 수동

            mImageHandler.removeMessages(0);
            mImageHandler.sendMessage(message);
        }

        @Override
        public void prevSlide()
        {
            Message message = mImageHandler.obtainMessage();
            message.what = 0;
            message.arg1 = -1; // 왼쪽으로 이동.
            message.arg2 = 0; // 수동

            mImageHandler.removeMessages(0);
            mImageHandler.sendMessage(message);
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mUserSocialInformationJsonResponseListener = new DailyHotelJsonResponseListener()
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
                        processBooking(mSelectedTicketInformation, mCheckInSaleTime);
                    } else
                    {
                        // 정보 업데이트 화면으로 이동.
                        moveToUserInfoUpdate(user, recommender, isDailyUser);
                    }
                } else
                {
                    unLockUI();

                    String msg = response.getString("msg");

                    DailyToast.showToast(PlaceDetailActivity.this, msg, Toast.LENGTH_SHORT);
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
                DailyNetworkAPI.getInstance().requestUserInformationEx(mNetworkTag, mUserSocialInformationJsonResponseListener, PlaceDetailActivity.this);
            } else if ("dead".equalsIgnoreCase(result) == true)
            {
                // session dead
                // 재로그인
                if (DailyPreference.getInstance(PlaceDetailActivity.this).isAutoLogin() == true)
                {
                    HashMap<String, String> params = Util.getLoginParams(PlaceDetailActivity.this);

                    DailyNetworkAPI.getInstance().requestUserSignin(mNetworkTag, params, mUserLoginJsonResponseListener, PlaceDetailActivity.this);
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
                        DailyNetworkAPI.getInstance().requestUserAlive(mNetworkTag, mUserAliveStringResponseListener, PlaceDetailActivity.this);
                        return;
                    }
                }

                // 로그인 실패
                // data 초기화
                DailyPreference.getInstance(PlaceDetailActivity.this).removeUserInformation();

                unLockUI();
                startLoginActivity();
            } catch (Exception e)
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
                        unLockUI();
                        DailyToast.showToast(PlaceDetailActivity.this, R.string.toast_msg_dont_past_hotelinfo, Toast.LENGTH_LONG);
                        finish();
                        return;
                    }

                    requestPlaceDetailInformation(mPlaceDetail, mCheckInSaleTime);
                } else
                {
                    SaleTime saleTime = new SaleTime();

                    saleTime.setCurrentTime(response.getLong("currentDateTime"));

                    long todayDailyTime = response.getLong("dailyDateTime");
                    saleTime.setDailyTime(todayDailyTime);

                    long shareDailyTime = mCheckInSaleTime.getDayOfDaysHotelDate().getTime();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                    int shareDailyDay = Integer.parseInt(simpleDateFormat.format(new Date(shareDailyTime)));
                    int todayDailyDay = Integer.parseInt(simpleDateFormat.format(new Date(todayDailyTime)));

                    // 지난 날의 호텔인 경우.
                    if (shareDailyDay < todayDailyDay)
                    {
                        unLockUI();

                        DailyToast.showToast(PlaceDetailActivity.this, R.string.toast_msg_dont_past_hotelinfo, Toast.LENGTH_LONG);
                        finish();
                        return;
                    }

                    requestPlaceDetailInformation(mPlaceDetail, mCheckInSaleTime);
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
