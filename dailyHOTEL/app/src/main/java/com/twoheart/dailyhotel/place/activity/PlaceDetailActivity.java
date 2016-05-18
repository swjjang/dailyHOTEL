package com.twoheart.dailyhotel.place.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.GourmetDetail;
import com.twoheart.dailyhotel.model.PlaceDetail;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.TicketInformation;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.common.ImageDetailListActivity;
import com.twoheart.dailyhotel.screen.common.ZoomMapActivity;
import com.twoheart.dailyhotel.screen.gourmet.detail.GourmetDetailLayout;
import com.twoheart.dailyhotel.screen.hotel.detail.HotelDetailLayout;
import com.twoheart.dailyhotel.screen.information.member.AddProfileSocialActivity;
import com.twoheart.dailyhotel.screen.information.member.EditProfilePhoneActivity;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public abstract class PlaceDetailActivity extends BaseActivity
{
    protected GourmetDetailLayout mPlaceDetailLayout;
    protected PlaceDetail mPlaceDetail;
    protected int mCurrentImage;
    protected boolean mIsStartByShare;
    protected SaleTime mCheckInSaleTime;
    private TicketInformation mSelectedTicketInformation;
    private String mDefaultImageUrl;
    protected DailyToolbarLayout mDailyToolbarLayout;
    private View mToolbarUnderline;
    private boolean mDontReloadAtOnResume;

    protected Province mProvince;
    protected String mArea; // Analytics용 소지역
    private int mViewPrice; // Analytics용 리스트 가격

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

        void clipAddress(String address);
    }

    public interface OnImageActionListener
    {
        void nextSlide();

        void prevSlide();
    }

    protected abstract GourmetDetailLayout getLayout(BaseActivity activity, String imageUrl);

    protected abstract void requestPlaceDetailInformation(PlaceDetail placeDetail, SaleTime checkInSaleTime);

    protected abstract PlaceDetail createPlaceDetail(Intent intent);

    protected abstract void shareKakao(PlaceDetail placeDetail, String imageUrl, SaleTime checkInSaleTime, SaleTime checkOutSaleTime);

    protected abstract void processBooking(PlaceDetail placeDetail, TicketInformation ticketInformation, SaleTime checkInSaleTime, boolean isBenefit);

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
            String category = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_CATEGORY);
            mDefaultImageUrl = imageUrl;

            if (mPlaceDetail instanceof GourmetDetail && Util.isTextEmpty(category) == false)
            {
                ((GourmetDetail) mPlaceDetail).category = category;
            }

            if (mCheckInSaleTime == null || mPlaceDetail == null || placeName == null)
            {
                Util.restartApp(this);
                return;
            }

            mProvince = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);
            mArea = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_AREA);
            mViewPrice = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_PRICE, 0);

            initLayout(placeName, imageUrl);
        }
    }

    protected void initLayout(String placeName, String imageUrl)
    {
        setContentView(R.layout.activity_placedetail);

        if (mPlaceDetailLayout == null)
        {
            mPlaceDetailLayout = getLayout(this, imageUrl);
            mPlaceDetailLayout.setUserActionListener(mOnUserActionListener);
        }

        setLockUICancelable(true);
        initToolbar(placeName);

        mOnUserActionListener.hideActionBar();
    }

    private void initToolbar(String title)
    {
        mToolbarUnderline = findViewById(R.id.toolbarUnderline);
        mToolbarUnderline.setVisibility(View.INVISIBLE);

        View toolbar = findViewById(R.id.toolbar);
        mDailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        mDailyToolbarLayout.initToolbar(title, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        }, true);

        mDailyToolbarLayout.setToolbarMenu(R.drawable.navibar_ic_share, -1);
        mDailyToolbarLayout.setToolbarMenuClickListener(mToolbarOptionsItemSelected);
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(PlaceDetailActivity.this).recordScreen(Screen.DAILYGOURMET_DETAIL);

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
        if (mPlaceDetailLayout != null)
        {
            mPlaceDetailLayout.hideTicketInformationLayout();

            if (mPlaceDetailLayout.getBookingStatus() != GourmetDetailLayout.STATUS_SOLD_OUT)
            {
                mPlaceDetailLayout.setBookingStatus(GourmetDetailLayout.STATUS_SEARCH_TICKET);
            }
        }

        if (mDontReloadAtOnResume == true)
        {
            mDontReloadAtOnResume = false;
        } else
        {
            lockUI();
            DailyNetworkAPI.getInstance(this).requestCommonDatetime(mNetworkTag, mDateTimeJsonResponseListener, this);
        }

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
        try
        {
            releaseUiComponent();

            switch (requestCode)
            {
                case CODE_REQUEST_ACTIVITY_BOOKING:
                {
                    mDontReloadAtOnResume = true;

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
                    mDontReloadAtOnResume = true;

                    if (resultCode == RESULT_OK)
                    {
                        mOnUserActionListener.doBooking(mSelectedTicketInformation);
                    }
                    break;
                }

                case CODE_REQUEST_ACTIVITY_IMAGELIST:
                case CODE_REQUEST_ACTIVITY_ZOOMMAP:
                case CODE_REQUEST_ACTIVITY_SHAREKAKAO:
                    mDontReloadAtOnResume = true;
                    break;
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

    private void moveToAddSocialUserInformation(Customer user)
    {
        Intent intent = AddProfileSocialActivity.newInstance(this, user);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_USERINFO_UPDATE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
    }

    private void moveToUpdateUserPhoneNumber(Customer user, EditProfilePhoneActivity.Type type)
    {
        Intent intent = EditProfilePhoneActivity.newInstance(this, user.getUserIdx(), type);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_USERINFO_UPDATE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
    }

    protected void recordAnalyticsGourmetDetail(String screen, PlaceDetail placeDetail)
    {
        if (placeDetail == null)
        {
            return;
        }

        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.NAME, placeDetail.name);
            params.put(AnalyticsManager.KeyType.CATEGORY, ((GourmetDetail) placeDetail).category);
            params.put(AnalyticsManager.KeyType.DBENEFIT, Util.isTextEmpty(placeDetail.benefit) ? "no" : "yes");

            if (placeDetail.getTicketInformation() == null || placeDetail.getTicketInformation().size() == 0)
            {
                params.put(AnalyticsManager.KeyType.PRICE, "0");
            } else
            {
                params.put(AnalyticsManager.KeyType.PRICE, Integer.toString(placeDetail.getTicketInformation().get(0).discountPrice));
            }

            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(placeDetail.index));
            params.put(AnalyticsManager.KeyType.DATE, mCheckInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));

            if (mProvince == null)
            {
                params.put(AnalyticsManager.KeyType.PROVINCE, AnalyticsManager.ValueType.EMPTY);
                params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
                params.put(AnalyticsManager.KeyType.AREA, AnalyticsManager.ValueType.EMPTY);
            } else
            {
                if (mProvince instanceof Area)
                {
                    Area area = (Area) mProvince;
                    params.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
                    params.put(AnalyticsManager.KeyType.DISTRICT, area.name);
                } else
                {
                    params.put(AnalyticsManager.KeyType.PROVINCE, mProvince.name);
                    params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
                }

                params.put(AnalyticsManager.KeyType.AREA, Util.isTextEmpty(mArea) ? AnalyticsManager.ValueType.EMPTY : mArea);
            }

            params.put(AnalyticsManager.KeyType.UNIT_PRICE, Integer.toString(mViewPrice));
            params.put(AnalyticsManager.KeyType.VISIT_DATE, Long.toString(mCheckInSaleTime.getDayOfDaysDate().getTime()));

            AnalyticsManager.getInstance(this).recordScreen(screen, params);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    protected Map<String, String> recordAnalyticsBooking(PlaceDetail placeDetail, TicketInformation ticketInformation)
    {
        if (placeDetail == null || ticketInformation == null)
        {
            return null;
        }

        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.NAME, placeDetail.name);
            params.put(AnalyticsManager.KeyType.CATEGORY, ((GourmetDetail) placeDetail).category);

            if (mProvince == null)
            {
                params.put(AnalyticsManager.KeyType.PROVINCE, AnalyticsManager.ValueType.EMPTY);
                params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
                params.put(AnalyticsManager.KeyType.AREA, AnalyticsManager.ValueType.EMPTY);
            } else
            {
                if (mProvince instanceof Area)
                {
                    Area area = (Area) mProvince;
                    params.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
                    params.put(AnalyticsManager.KeyType.DISTRICT, area.name);
                } else
                {
                    params.put(AnalyticsManager.KeyType.PROVINCE, mProvince.name);
                    params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
                }

                params.put(AnalyticsManager.KeyType.AREA, Util.isTextEmpty(mArea) ? AnalyticsManager.ValueType.EMPTY : mArea);
            }

            params.put(AnalyticsManager.KeyType.PRICE_OF_SELECTED_TICKET, Integer.toString(ticketInformation.discountPrice));
            params.put(AnalyticsManager.KeyType.VISIT_DATE, Long.toString(mCheckInSaleTime.getDayOfDaysDate().getTime()));

            return params;
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return null;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UserActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    private View.OnClickListener mToolbarOptionsItemSelected = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = layoutInflater.inflate(R.layout.view_sharedialog_layout, null, false);

            final Dialog shareDialog = new Dialog(PlaceDetailActivity.this);
            shareDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            shareDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            shareDialog.setCanceledOnTouchOutside(true);

            // 버튼
            View kakaoShareLayout = dialogView.findViewById(R.id.kakaoShareLayout);

            kakaoShareLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (shareDialog.isShowing() == true)
                    {
                        shareDialog.dismiss();
                    }

                    if (mDefaultImageUrl == null)
                    {
                        if (mPlaceDetail.getImageInformationList() != null && mPlaceDetail.getImageInformationList().size() > 0)
                        {
                            mDefaultImageUrl = mPlaceDetail.getImageInformationList().get(0).url;
                        }
                    }

                    shareKakao(mPlaceDetail, mDefaultImageUrl, mCheckInSaleTime, null);
                }
            });

            try
            {
                shareDialog.setContentView(dialogView);
                shareDialog.show();
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
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
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_IMAGELIST);
        }

        @Override
        public void onSelectedImagePosition(int position)
        {
            mCurrentImage = position;
        }

        @Override
        public void doBooking(TicketInformation ticketInformation)
        {
            if (ticketInformation == null)
            {
                finish();
                return;
            }

            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            mSelectedTicketInformation = ticketInformation;

            if (Util.isTextEmpty(DailyPreference.getInstance(PlaceDetailActivity.this).getAuthorization()) == true)
            {
                startLoginActivity();
            } else
            {
                lockUI();

                DailyNetworkAPI.getInstance(PlaceDetailActivity.this).requestUserInformationEx(mNetworkTag, mUserInformationExJsonResponseListener, PlaceDetailActivity.this);
            }

            String label = String.format("%s-%s", mPlaceDetail.name, mSelectedTicketInformation.name);
            AnalyticsManager.getInstance(PlaceDetailActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
                , Action.BOOKING_CLICKED, label, recordAnalyticsBooking(mPlaceDetail, ticketInformation));
        }

        @Override
        public void doKakaotalkConsult()
        {
            if (isLockUiComponent() == true || isFinishing() == true)
            {
                return;
            }

            lockUiComponent();

            Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse("kakaolink://friend/%40%EB%8D%B0%EC%9D%BC%EB%A6%AC%EA%B3%A0%EB%A9%94"));
            if (intent.resolveActivity(getPackageManager()) == null)
            {
                Util.installPackage(PlaceDetailActivity.this, "com.kakao.talk");
            } else
            {
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SHAREKAKAO);
            }

            AnalyticsManager.getInstance(PlaceDetailActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
                , Action.KAKAO_INQUIRY_CLICKED, mPlaceDetail.name, null);
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

            recordAnalyticsGourmetDetail(Screen.DAILYGOURMET_DETAIL_TICKETTYPE, mPlaceDetail);
            AnalyticsManager.getInstance(PlaceDetailActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
                , Action.TICKET_TYPE_CLICKED, mPlaceDetail.name, null);
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

            Intent intent = ZoomMapActivity.newInstance(PlaceDetailActivity.this//
                , ZoomMapActivity.SourceType.GOURMET, mPlaceDetail.name//
                , mPlaceDetail.latitude, mPlaceDetail.longitude, false);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_ZOOMMAP);

            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS, Action.GOURMET_DETAIL_MAP_CLICKED, mPlaceDetail.name, null);
        }

        @Override
        public void finish()
        {
            PlaceDetailActivity.super.finish();
        }

        @Override
        public void clipAddress(String address)
        {
            Util.clipText(PlaceDetailActivity.this, address);

            DailyToast.showToast(PlaceDetailActivity.this, R.string.message_detail_copy_address, Toast.LENGTH_SHORT);

            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS, Action.GOURMET_DETAIL_ADDRESS_COPY_CLICKED, mPlaceDetail.name, null);
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mUserInformationExJsonResponseListener = new DailyHotelJsonResponseListener()
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

                    Customer user = new Customer();
                    user.setEmail(jsonObject.getString("email"));
                    user.setName(jsonObject.getString("name"));
                    user.setPhone(jsonObject.getString("phone"));
                    user.setUserIdx(jsonObject.getString("idx"));

                    // 추천인
                    int recommender = jsonObject.getInt("recommender_code");
                    boolean isDailyUser = jsonObject.getBoolean("is_daily_user");

                    if (isDailyUser == true)
                    {
                        DailyNetworkAPI.getInstance(PlaceDetailActivity.this).requestUserInformation(mNetworkTag, mUserInformationJsonResponseListener, this);
                    } else
                    {
                        // 입력된 정보가 부족해.
                        if (Util.isTextEmpty(user.getEmail(), user.getPhone(), user.getName()) == true)
                        {
                            moveToAddSocialUserInformation(user);
                        } else if (Util.isValidatePhoneNumber(user.getPhone()) == false)
                        {
                            moveToUpdateUserPhoneNumber(user, EditProfilePhoneActivity.Type.WRONG_PHONENUMBER);
                        } else
                        {
                            processBooking(mPlaceDetail, mSelectedTicketInformation, mCheckInSaleTime, false);
                        }
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

    private DailyHotelJsonResponseListener mUserInformationJsonResponseListener = new DailyHotelJsonResponseListener()
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
                Customer user = new Customer();
                user.setEmail(response.getString("email"));
                user.setName(response.getString("name"));
                user.setPhone(response.getString("phone"));
                user.setUserIdx(response.getString("idx"));

                boolean isPhoneVerified = response.getBoolean("is_phone_verified");
                boolean isVerified = response.getBoolean("is_verified");

                if (Util.isValidatePhoneNumber(user.getPhone()) == false)
                {
                    moveToUpdateUserPhoneNumber(user, EditProfilePhoneActivity.Type.NEED_VERIFICATION_PHONENUMBER);
                } else
                {
                    // 기존에 인증이 되었는데 인증이 해지되었다.
                    if (isVerified == true && isPhoneVerified == false)
                    {
                        moveToUpdateUserPhoneNumber(user, EditProfilePhoneActivity.Type.NEED_VERIFICATION_PHONENUMBER);
                    } else
                    {
                        processBooking(mPlaceDetail, mSelectedTicketInformation, mCheckInSaleTime, false);
                    }
                }
            } catch (Exception e)
            {
                onError(e);
            }
        }
    };

    private DailyHotelJsonResponseListener mDateTimeJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

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

                    long shareDailyTime = mCheckInSaleTime.getDayOfDaysDate().getTime();
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

                    long shareDailyTime = mCheckInSaleTime.getDayOfDaysDate().getTime();

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
