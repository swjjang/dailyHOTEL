/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * 호텔 리스트에서 호텔 선택 시 호텔의 정보들을 보여주는 화면이다.
 * 예약, 정보, 지도 프래그먼트를 담고 있는 액티비티이다.
 */
package com.twoheart.dailyhotel.screen.hoteldetail;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.activity.BookingActivity;
import com.twoheart.dailyhotel.activity.ImageDetailListActivity;
import com.twoheart.dailyhotel.activity.SignupActivity;
import com.twoheart.dailyhotel.activity.ZoomMapActivity;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.model.SaleRoomInformation;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.DailyToolbarLayout;

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
    private DailyToolbarLayout mDailyToolbarLayout;
    private View mToolbarUnderline;

    public interface OnUserActionListener
    {
        void showActionBar();

        void hideActionBar();

        void onClickImage(HotelDetail hotelDetail);

        void nextSlide();

        void prevSlide();

        void onSelectedImagePosition(int position);

        void doBooking(SaleRoomInformation saleRoomInformation);

        void doKakaotalkConsult();

        void showRoomType();

        void hideRoomType();

        void showMap();
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
        setContentView(R.layout.activity_hoteldetail);

        if (mHotelDetailLayout == null)
        {
            mHotelDetailLayout = new HotelDetailLayout(this, imageUrl);
            mHotelDetailLayout.setUserActionListener(mOnUserActionListener);
        }

        initToolbar(hotelName);

        mOnUserActionListener.hideActionBar();
    }

    private void initToolbar(String title)
    {
        mToolbarUnderline = findViewById(R.id.toolbarUnderline);
        mToolbarUnderline.setVisibility(View.INVISIBLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        mDailyToolbarLayout.initToolbar(title, true);
        mDailyToolbarLayout.setToolbarRegionMenu(R.drawable.navibar_ic_share, -1);
        mDailyToolbarLayout.setToolbarMenuClickListener(mToolbarOptionsItemSelected);
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(HotelDetailActivity.this).recordScreen(Screen.DAILYHOTEL_DETAIL, null);

        try
        {
            super.onStart();
        } catch (NullPointerException e)
        {
            ExLog.e(e.toString());

            Util.restartApp(this);
        }
    }

    @Override
    protected void onResume()
    {
        if (mHotelDetailLayout != null)
        {
            mHotelDetailLayout.hideRoomType();
            mHotelDetailLayout.setBookingStatus(HotelDetailLayout.STATUS_SEARCH_ROOM);
        }

        lockUI();
        DailyNetworkAPI.getInstance().requestCommonDatetime(mNetworkTag, mDateTimeJsonResponseListener, this);

        super.onResume();
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
        try
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
        } catch (NullPointerException e)
        {
            ExLog.e(e.toString());

            Util.restartApp(this);
        }
    }

    @Override
    public void onError()
    {
        super.onError();

        finish();
    }

    private void moveToBooking(HotelDetail hotelDetail, SaleRoomInformation saleRoomInformation, SaleTime checkInSaleTime)
    {
        if (saleRoomInformation == null)
        {
            return;
        }

        Intent intent = BookingActivity.newInstance(HotelDetailActivity.this, saleRoomInformation//
            , checkInSaleTime, hotelDetail.grade, hotelDetail.hotelIndex, !Util.isTextEmpty(hotelDetail.hotelBenefit));

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

    private void recordAnalyticsHotelDetail(String screen, HotelDetail hotelDetail)
    {
        if (hotelDetail == null)
        {
            return;
        }

        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.NAME, mHotelDetail.hotelName);
            params.put(AnalyticsManager.KeyType.GRADE, mHotelDetail.grade.getName(HotelDetailActivity.this));
            params.put(AnalyticsManager.KeyType.DBENEFIT, Util.isTextEmpty(mHotelDetail.hotelBenefit) ? "no" : "yes");

            if (mHotelDetail.getSaleRoomList() == null || mHotelDetail.getSaleRoomList().size() == 0)
            {
                params.put(AnalyticsManager.KeyType.PRICE, "0");
            } else
            {
                params.put(AnalyticsManager.KeyType.PRICE, Integer.toString(mHotelDetail.getSaleRoomList().get(0).averageDiscount));
            }

            params.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(mHotelDetail.nights));
            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(mHotelDetail.hotelIndex));

            SaleTime checkOutSaleTime = mCheckInSaleTime.getClone(mCheckInSaleTime.getOffsetDailyDay() + hotelDetail.nights);

            params.put(AnalyticsManager.KeyType.CHECK_IN, mCheckInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.CHECK_OUT, checkOutSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));

            AnalyticsManager.getInstance(HotelDetailActivity.this).recordScreen(screen, params);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UserActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////


    private View.OnClickListener mToolbarOptionsItemSelected = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (mDefaultImageUrl == null && mHotelDetail.getImageInformationList() != null && mHotelDetail.getImageInformationList().size() > 0)
            {
                mDefaultImageUrl = mHotelDetail.getImageInformationList().get(0).url;
            }

            String name = DailyPreference.getInstance(HotelDetailActivity.this).getUserName();

            if (Util.isTextEmpty(name) == true)
            {
                name = getString(R.string.label_friend) + "가";
            } else
            {
                name += "님이";
            }

            KakaoLinkManager.newInstance(HotelDetailActivity.this).shareHotel(name, mHotelDetail.hotelName, mHotelDetail.address//
                , mHotelDetail.hotelIndex//
                , mDefaultImageUrl//
                , mCheckInSaleTime, mHotelDetail.nights);

            SaleTime checkOutSaleTime = mCheckInSaleTime.getClone(mCheckInSaleTime.getOffsetDailyDay() + mHotelDetail.nights);

            HashMap<String, String> params = new HashMap<String, String>();
            params.put(AnalyticsManager.KeyType.NAME, mHotelDetail.hotelName);
            params.put(AnalyticsManager.KeyType.CHECK_IN, mCheckInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.CHECK_OUT, checkOutSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));

            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);
            params.put(AnalyticsManager.KeyType.CURRENT_TIME, dateFormat2.format(new Date()));

            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.HOTELBOOKINGS//
                , Action.SOCIAL_SHARE_CLICKED, mHotelDetail.hotelName, params);
        }
    };

    private OnUserActionListener mOnUserActionListener = new OnUserActionListener()
    {
        @Override
        public void showActionBar()
        {
            mDailyToolbarLayout.setToolbarTransparent(false);
            mToolbarUnderline.setVisibility(View.VISIBLE);
        }

        @Override
        public void hideActionBar()
        {
            mDailyToolbarLayout.setToolbarTransparent(true);
            mToolbarUnderline.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onClickImage(HotelDetail hotelDetail)
        {
            if (isLockUiComponent() == true)
            {
                return;
            }

            lockUiComponent();

            Intent intent = new Intent(HotelDetailActivity.this, ImageDetailListActivity.class);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURLLIST, hotelDetail.getImageInformationList());
            intent.putExtra(NAME_INTENT_EXTRA_DATA_SELECTED_POSOTION, mCurrentImage);
            startActivity(intent);
        }

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

            String label = String.format("%s_%s", mHotelDetail.hotelName, mSelectedSaleRoomInformation.roomName);
            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.HOTELBOOKINGS//
                , Action.BOOKING_CLICKED, label, null);
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

            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.HOTELBOOKINGS//
                , Action.KAKAO_INQUIRY_CLICKED, mHotelDetail.hotelName, null);
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

            recordAnalyticsHotelDetail(Screen.DAILYHOTEL_DETAIL_ROOMTYPE, mHotelDetail);
            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.HOTELBOOKINGS//
                , Action.ROOM_TYPE_CLICKED, mHotelDetail.hotelName, null);
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

            AnalyticsManager.getInstance(HotelDetailActivity.this).recordScreen(Screen.DAILYHOTEL_DETAIL, null);
            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.HOTELBOOKINGS//
                , Action.ROOM_TYPE_CANCEL_CLICKED, mHotelDetail.hotelName, null);
        }

        @Override
        public void showMap()
        {
            if (isLockUiComponent() == true || isFinishing() == true)
            {
                return;
            }

            lockUiComponent();

            Intent intent = ZoomMapActivity.newInstance(HotelDetailActivity.this//
                , ZoomMapActivity.SourceType.HOTEL, mHotelDetail.hotelName//
                , mHotelDetail.latitude, mHotelDetail.longitude);

            startActivity(intent);

            //            SaleTime checkOutSaleTime = mCheckInSaleTime.getClone(mCheckInSaleTime.getOffsetDailyDay() + mHotelDetail.nights);
            //            String label = String.format("%s (%s-%s)", mHotelDetail.hotelName, mCheckInSaleTime.getDayOfDaysDateFormat("yyMMdd"), checkOutSaleTime.getDayOfDaysDateFormat("yyMMdd"));
            //
            //            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.DAILYHOTEL_DETAIL, Action.CLICK, label, (long) mHotelDetail.hotelIndex);
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private DailyHotelJsonResponseListener mHotelDetailInformationJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
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
                            mDailyToolbarLayout.setToolbarText(mHotelDetail.hotelName);
                        }

                        if (mHotelDetailLayout != null)
                        {
                            mHotelDetailLayout.setHotelDetail(mHotelDetail, mCurrentImage);
                        }

                        recordAnalyticsHotelDetail(Screen.DAILYHOTEL_DETAIL, mHotelDetail);
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
                        moveToBooking(mHotelDetail, mSelectedSaleRoomInformation, mCheckInSaleTime);
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
                if (DailyPreference.getInstance(HotelDetailActivity.this).isAutoLogin() == true)
                {
                    HashMap<String, String> params = Util.getLoginParams(HotelDetailActivity.this);

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
                DailyPreference.getInstance(HotelDetailActivity.this).removeUserInformation();

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
                if (mIsStartByShare == true)
                {
                    mCheckInSaleTime.setCurrentTime(response.getLong("currentDateTime"));

                    long shareDailyTime = mCheckInSaleTime.getDayOfDaysDate().getTime();
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
                    String params = String.format("?hotel_idx=%d&checkin_date=%s&nights=%d", mHotelDetail.hotelIndex, mCheckInSaleTime.getDayOfDaysDateFormat("yyyyMMdd"), mHotelDetail.nights);

                    if (DEBUG == true)
                    {
                        showSimpleDialog(null, params, getString(R.string.dialog_btn_text_confirm), null);
                    }

                    DailyNetworkAPI.getInstance().requestHotelDetailInformation(mNetworkTag, params, mHotelDetailInformationJsonResponseListener, HotelDetailActivity.this);
                } else
                {
                    SaleTime saleTime = new SaleTime();

                    saleTime.setCurrentTime(response.getLong("currentDateTime"));
                    saleTime.setDailyTime(response.getLong("dailyDateTime"));

                    // 호텔 정보를 가져온다.
                    String params = String.format("?hotel_idx=%d&checkin_date=%s&nights=%d", mHotelDetail.hotelIndex, mCheckInSaleTime.getDayOfDaysDateFormat("yyyyMMdd"), mHotelDetail.nights);

                    if (DEBUG == true)
                    {
                        showSimpleDialog(null, params, getString(R.string.dialog_btn_text_confirm), null);
                    }

                    DailyNetworkAPI.getInstance().requestHotelDetailInformation(mNetworkTag, params, mHotelDetailInformationJsonResponseListener, HotelDetailActivity.this);
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
