/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * 호텔 리스트에서 호텔 선택 시 호텔의 정보들을 보여주는 화면이다.
 * 예약, 정보, 지도 프래그먼트를 담고 있는 액티비티이다.
 */
package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetDetail;
import com.twoheart.dailyhotel.model.ImageInformation;
import com.twoheart.dailyhotel.model.PlaceDetail;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.TicketInformation;
import com.twoheart.dailyhotel.place.activity.PlaceDetailActivity;
import com.twoheart.dailyhotel.place.layout.PlaceDetailLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceDetailNetworkController;
import com.twoheart.dailyhotel.screen.common.ImageDetailListActivity;
import com.twoheart.dailyhotel.screen.common.ZoomMapActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetDetailCalendarActivity;
import com.twoheart.dailyhotel.screen.gourmet.payment.GourmetPaymentActivity;
import com.twoheart.dailyhotel.screen.information.member.EditProfilePhoneActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class GourmetDetailActivity extends PlaceDetailActivity
{
    private SaleTime mSaleTime;
    private TicketInformation mSelectedTicketInformation;

    /**
     * 리스트에서 호출
     *
     * @param context
     * @param saleTime
     * @param province
     * @param gourmet
     * @return
     */
    public static Intent newInstance(Context context, SaleTime saleTime, Province province, Gourmet gourmet)
    {
        Intent intent = new Intent(context, GourmetDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, province);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, gourmet.index);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACENAME, gourmet.name);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, gourmet.imageUrl);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CATEGORY, gourmet.category);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, gourmet.discountPrice);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PRICE, gourmet.price);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, false);

        String[] area = gourmet.addressSummary.split("\\||l|ㅣ|I");
        intent.putExtra(NAME_INTENT_EXTRA_DATA_AREA, area[0].trim());

        return intent;
    }

    /**
     * 딥링크로 호출
     *
     * @param context
     * @param saleTime
     * @param gourmetIndex
     * @param isShowCalendar
     * @return
     */
    public static Intent newInstance(Context context, SaleTime saleTime, int gourmetIndex, boolean isShowCalendar)
    {
        Intent intent = new Intent(context, GourmetDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_TYPE, "share");
        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, gourmetIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, isShowCalendar);

        return intent;
    }

    /**
     * 검색 결과에서 호출
     *
     * @param context
     * @param saleTime
     * @param gourmet
     * @return
     */
    public static Intent newInstance(Context context, SaleTime saleTime, Gourmet gourmet)
    {
        Intent intent = new Intent(context, GourmetDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, gourmet.index);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACENAME, gourmet.name);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, gourmet.imageUrl);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CATEGORY, gourmet.category);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, gourmet.discountPrice);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PRICE, gourmet.price);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, false);

        return intent;
    }

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

        mSaleTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);
        boolean isShowCalendar = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, false);

        mPlaceDetail = createPlaceDetail(intent);

        if (mSaleTime == null || mPlaceDetail == null)
        {
            Util.restartApp(this);
            return;
        }

        if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_TYPE) == true)
        {
            mIsDeepLink = true;

            if (mPlaceDetail == null)
            {
                Util.restartApp(this);
                return;
            }

            initLayout(null, null);

            if (isShowCalendar == true)
            {
                startCalendar(mSaleTime, mPlaceDetail.index, false);
            }
        } else
        {
            mIsDeepLink = false;

            String placeName = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_PLACENAME);
            mDefaultImageUrl = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL);
            String category = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_CATEGORY);

            if (mPlaceDetail instanceof GourmetDetail && Util.isTextEmpty(category) == false)
            {
                ((GourmetDetail) mPlaceDetail).category = category;
            }

            if (placeName == null)
            {
                Util.restartApp(this);
                return;
            }

            mProvince = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);
            mArea = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_AREA);
            mViewPrice = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_PRICE, 0);

            initLayout(placeName, mDefaultImageUrl);

            if (isShowCalendar == true)
            {
                startCalendar(mSaleTime, mPlaceDetail.index, false);
            }
        }
    }

    private void initLayout(String placeName, String imageUrl)
    {
        setContentView(mPlaceDetailLayout.onCreateView(R.layout.activity_placedetail));

        mPlaceDetailLayout.setDefaultImage(imageUrl);
        mPlaceDetailLayout.setStatusBarHeight(this);

        setLockUICancelable(true);
        initToolbar(placeName);

        mOnEventListener.hideActionBar();
    }

    @Override
    protected PlaceDetailLayout getDetailLayout(Context context)
    {
        return new GourmetDetailLayout(context, mOnEventListener);
    }

    @Override
    protected PlaceDetailNetworkController getNetworkController(Context context)
    {
        return new GourmetDetailNetworkController(context, mNetworkTag, mOnNetworkControllerListener);
    }

    protected void requestPlaceDetailInformation(SaleTime saleTime, int placeIndex)
    {
        ((GourmetDetailNetworkController) mPlaceDetailNetworkController).requestGourmetDetailInformation(placeIndex, saleTime.getDayOfDaysDateFormat("yyMMdd"));
    }

    @Override
    protected PlaceDetail createPlaceDetail(Intent intent)
    {
        if (intent == null)
        {
            return null;
        }

        int index = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, -1);
        int entryIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_ENTRY_INDEX, -1);
        String isShowOriginalPrice = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_SHOW_TAGPRICE_YN);

        return new GourmetDetail(index, entryIndex, isShowOriginalPrice);
    }

    @Override
    protected void shareKakao(PlaceDetail placeDetail, String imageUrl)
    {
        String name = DailyPreference.getInstance(GourmetDetailActivity.this).getUserName();

        if (Util.isTextEmpty(name) == true)
        {
            name = getString(R.string.label_friend) + "가";
        } else
        {
            name += "님이";
        }

        KakaoLinkManager.newInstance(this).shareGourmet(name, placeDetail.name, placeDetail.address//
            , placeDetail.index //
            , imageUrl //
            , mSaleTime);

        HashMap<String, String> params = new HashMap<>();
        params.put(AnalyticsManager.KeyType.NAME, placeDetail.name);
        params.put(AnalyticsManager.KeyType.CHECK_IN, mSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));

        //        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);
        //        params.put(AnalyticsManager.KeyType.CURRENT_TIME, dateFormat2.format(new Date()));
        params.put(AnalyticsManager.KeyType.CURRENT_TIME, DailyCalendar.format(new Date(), "yyyy-MM-dd'T'HH:mm:ss"));

        AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
            , AnalyticsManager.Action.SOCIAL_SHARE_CLICKED, placeDetail.name, params);
    }

    protected void processBooking(SaleTime saleTime, GourmetDetail gourmetDetail, TicketInformation ticketInformation)
    {
        if (saleTime == null || gourmetDetail == null || ticketInformation == null)
        {
            return;
        }

        String imageUrl = null;
        ArrayList<ImageInformation> mImageInformationList = gourmetDetail.getImageInformationList();

        if (mImageInformationList != null && mImageInformationList.size() > 0)
        {
            imageUrl = mImageInformationList.get(0).url;
        }

        boolean isBenefit = Util.isTextEmpty(gourmetDetail.benefit) == false;

        Intent intent = GourmetPaymentActivity.newInstance(GourmetDetailActivity.this, ticketInformation//
            , saleTime, imageUrl, gourmetDetail.category, gourmetDetail.index, isBenefit, mProvince, mArea);

        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_BOOKING);
    }

    @Override
    protected void onCalendarActivityResult(int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            SaleTime checkInSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);

            if (checkInSaleTime == null)
            {
                return;
            }

            mSaleTime = checkInSaleTime;
            mPlaceDetail = new GourmetDetail(mPlaceDetail.index, mPlaceDetail.entryPosition, mPlaceDetail.isShowOriginalPrice);

            requestPlaceDetailInformation(mSaleTime, mPlaceDetail.index);
        }
    }

    @Override
    protected void hideTicketInformationLayout()
    {
        mOnEventListener.hideTicketInformationLayout();
    }

    @Override
    protected void doBooking()
    {
        mOnEventListener.doBooking();
    }

    protected void startCalendar(SaleTime saleTime, int placeIndex, boolean isAnimation)
    {
        if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        Intent intent = GourmetDetailCalendarActivity.newInstance(GourmetDetailActivity.this, //
            saleTime, placeIndex, AnalyticsManager.ValueType.DETAIL, true, isAnimation);
        startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_CALENDAR);

        AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.GOURMET_BOOKING_CALENDAR_CLICKED, AnalyticsManager.ValueType.DETAIL, null);
    }

    protected void recordAnalyticsGourmetDetail(String screen, SaleTime saleTime, GourmetDetail gourmetDetail)
    {
        if (saleTime == null || gourmetDetail == null)
        {
            return;
        }

        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.NAME, gourmetDetail.name);
            params.put(AnalyticsManager.KeyType.CATEGORY, gourmetDetail.category);
            params.put(AnalyticsManager.KeyType.DBENEFIT, Util.isTextEmpty(gourmetDetail.benefit) ? "no" : "yes");

            if (gourmetDetail.getTicketInformation() == null || gourmetDetail.getTicketInformation().size() == 0)
            {
                params.put(AnalyticsManager.KeyType.PRICE, "0");
            } else
            {
                params.put(AnalyticsManager.KeyType.PRICE, Integer.toString(gourmetDetail.getTicketInformation().get(0).discountPrice));
            }

            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(gourmetDetail.index));
            params.put(AnalyticsManager.KeyType.DATE, saleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));

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
            params.put(AnalyticsManager.KeyType.VISIT_DATE, Long.toString(saleTime.getDayOfDaysDate().getTime()));
            params.put(AnalyticsManager.KeyType.LIST_INDEX, Integer.toString(gourmetDetail.entryPosition));
            params.put(AnalyticsManager.KeyType.RATING, gourmetDetail.satisfaction);
            params.put(AnalyticsManager.KeyType.SHOW_TAG_PRICE_YN, gourmetDetail.isShowOriginalPrice);

            AnalyticsManager.getInstance(GourmetDetailActivity.this).recordScreen(screen, params);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    protected Map<String, String> recordAnalyticsBooking(SaleTime saleTime, GourmetDetail gourmetDetail, TicketInformation ticketInformation)
    {
        if (saleTime == null || gourmetDetail == null || ticketInformation == null)
        {
            return null;
        }

        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.NAME, gourmetDetail.name);
            params.put(AnalyticsManager.KeyType.CATEGORY, ((GourmetDetail) gourmetDetail).category);

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
            params.put(AnalyticsManager.KeyType.VISIT_DATE, Long.toString(saleTime.getDayOfDaysDate().getTime()));

            return params;
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return null;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private GourmetDetailLayout.OnEventListener mOnEventListener = new GourmetDetailLayout.OnEventListener()
    {
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

            if (DailyHotel.isLogin() == false)
            {
                startLoginActivity();
            } else
            {
                lockUI();
                mPlaceDetailNetworkController.requestUserInformationEx();
            }

            String label = String.format("%s-%s", mPlaceDetail.name, mSelectedTicketInformation.name);
            AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
                , AnalyticsManager.Action.BOOKING_CLICKED, label, recordAnalyticsBooking(mSaleTime, (GourmetDetail) mPlaceDetail, mSelectedTicketInformation));
        }

        @Override
        public void doBooking()
        {
            doBooking(mSelectedTicketInformation);
        }

        @Override
        public void showActionBar()
        {
            mDailyToolbarLayout.setToolbarVisibility(true);
        }

        @Override
        public void hideActionBar()
        {
            mDailyToolbarLayout.setToolbarVisibility(false);
        }

        @Override
        public void onClickImage(PlaceDetail placeDetail)
        {
            if (isLockUiComponent() == true)
            {
                return;
            }

            lockUiComponent();

            Intent intent = new Intent(GourmetDetailActivity.this, ImageDetailListActivity.class);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURLLIST, placeDetail.getImageInformationList());
            intent.putExtra(NAME_INTENT_EXTRA_DATA_SELECTED_POSOTION, mCurrentImage);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_IMAGELIST);
        }

        @Override
        public void onSelectedImagePosition(int position)
        {
            mCurrentImage = position;
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
                Util.installPackage(GourmetDetailActivity.this, "com.kakao.talk");
            } else
            {
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SHAREKAKAO);
            }

            AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
                , AnalyticsManager.Action.KAKAO_INQUIRY_CLICKED, mPlaceDetail.name, null);
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

            recordAnalyticsGourmetDetail(AnalyticsManager.Screen.DAILYGOURMET_DETAIL_TICKETTYPE, mSaleTime, (GourmetDetail) mPlaceDetail);
            AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
                , AnalyticsManager.Action.TICKET_TYPE_CLICKED, mPlaceDetail.name, null);
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
            if (Util.isInstallGooglePlayService(GourmetDetailActivity.this) == true)
            {
                if (lockUiComponentAndIsLockUiComponent() == true || isFinishing() == true)
                {
                    return;
                }

                Intent intent = ZoomMapActivity.newInstance(GourmetDetailActivity.this//
                    , ZoomMapActivity.SourceType.GOURMET, mPlaceDetail.name, mPlaceDetail.address//
                    , mPlaceDetail.latitude, mPlaceDetail.longitude, false);

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_ZOOMMAP);

                AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS,//
                    AnalyticsManager.Action.GOURMET_DETAIL_MAP_CLICKED, mPlaceDetail.name, null);
            } else
            {
                Util.installGooglePlayService(GourmetDetailActivity.this);
            }
        }

        @Override
        public void finish()
        {
            GourmetDetailActivity.this.finish();
        }

        @Override
        public void clipAddress(String address)
        {
            Util.clipText(GourmetDetailActivity.this, address);

            DailyToast.showToast(GourmetDetailActivity.this, R.string.message_detail_copy_address, Toast.LENGTH_SHORT);

            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS,//
                AnalyticsManager.Action.GOURMET_DETAIL_ADDRESS_COPY_CLICKED, mPlaceDetail.name, null);
        }

        @Override
        public void showNavigatorDialog()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Util.showShareMapDialog(GourmetDetailActivity.this, mPlaceDetail.name//
                , mPlaceDetail.latitude, mPlaceDetail.longitude, false//
                , AnalyticsManager.Category.GOURMET_BOOKINGS//
                , AnalyticsManager.Action.GOURMET_DETAIL_NAVIGATION_APP_CLICKED//
                , null);
        }

        @Override
        public void onCalendarClick()
        {
            startCalendar(mSaleTime, mPlaceDetail.index, true);
        }
    };

    private GourmetDetailNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new GourmetDetailNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onGourmetDetailInformation(JSONObject dataJSONObject)
        {
            if (dataJSONObject == null)
            {
                return;
            }

            try
            {
                mPlaceDetail.setData(dataJSONObject);

                if (mIsDeepLink == true)
                {
                    mIsDeepLink = false;
                    mDailyToolbarLayout.setToolbarText(mPlaceDetail.name);
                }

                if (mPlaceDetailLayout != null)
                {
                    ((GourmetDetailLayout) mPlaceDetailLayout).setDetail(mSaleTime, (GourmetDetail) mPlaceDetail, mCurrentImage);
                }

                recordAnalyticsGourmetDetail(AnalyticsManager.Screen.DAILYGOURMET_DETAIL, mSaleTime, (GourmetDetail) mPlaceDetail);
            } catch (Exception e)
            {
                DailyToast.showToast(GourmetDetailActivity.this, R.string.act_base_network_connect, Toast.LENGTH_LONG);
                finish();
            } finally
            {
                unLockUI();
            }
        }

        @Override
        public void onCommonDateTime(long currentDateTime, long dailyDateTime)
        {
            if (mIsDeepLink == true)
            {
                mSaleTime.setCurrentTime(currentDateTime);
                long shareDailyTime = mSaleTime.getDayOfDaysDate().getTime();

                int shareDailyDay = Integer.parseInt(DailyCalendar.format(shareDailyTime, "yyyyMMdd", TimeZone.getTimeZone("GMT")));
                int todayDailyDay = Integer.parseInt(DailyCalendar.format(dailyDateTime, "yyyyMMdd", TimeZone.getTimeZone("GMT")));

                // 지난 날의 호텔인 경우.
                if (shareDailyDay < todayDailyDay)
                {
                    unLockUI();
                    DailyToast.showToast(GourmetDetailActivity.this, R.string.toast_msg_dont_past_hotelinfo, Toast.LENGTH_LONG);
                    finish();
                    return;
                }
            }

            requestPlaceDetailInformation(mSaleTime, mPlaceDetail.index);
        }

        @Override
        public void onUserInformation(Customer user, boolean isDailyUser)
        {
            if (isDailyUser == true)
            {
                mPlaceDetailNetworkController.requestProfile();
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
                    processBooking(mSaleTime, (GourmetDetail) mPlaceDetail, mSelectedTicketInformation);
                }
            }
        }

        @Override
        public void onUserProfile(Customer user, boolean isVerified, boolean isPhoneVerified)
        {
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
                    processBooking(mSaleTime, (GourmetDetail) mPlaceDetail, mSelectedTicketInformation);
                }
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            setResult(CODE_RESULT_ACTIVITY_REFRESH);
            GourmetDetailActivity.this.onErrorResponse(volleyError);
            finish();
        }

        @Override
        public void onError(Exception e)
        {
            setResult(CODE_RESULT_ACTIVITY_REFRESH);
            GourmetDetailActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            setResult(CODE_RESULT_ACTIVITY_REFRESH);
            GourmetDetailActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            setResult(CODE_RESULT_ACTIVITY_REFRESH);
            GourmetDetailActivity.this.onErrorToastMessage(message);
            finish();
        }
    };
}
