package com.twoheart.dailyhotel.screen.hotel.detail;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.ImageInformation;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleRoomInformation;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.StayDetail;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.common.ImageDetailListActivity;
import com.twoheart.dailyhotel.screen.common.ZoomMapActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.StayDetailCalendarActivity;
import com.twoheart.dailyhotel.screen.hotel.payment.HotelPaymentActivity;
import com.twoheart.dailyhotel.screen.information.member.AddProfileSocialActivity;
import com.twoheart.dailyhotel.screen.information.member.EditProfilePhoneActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class StayDetailActivity extends BaseActivity
{
    private static final int DURATION_HOTEL_IMAGE_SHOW = 4000;

    private StayDetail mStayDetail;
    private SaleTime mCheckInSaleTime;

    private Province mProvince;
    private String mArea; // Analytics용 소지역
    private int mViewPrice; // Analytics용 리스트 가격

    private int mCurrentImage;
    private SaleRoomInformation mSelectedSaleRoomInformation;
    private boolean mIsStartByShare;
    private String mDefaultImageUrl;

    private StayDetailLayout mStayDetailLayout;
    private DailyToolbarLayout mDailyToolbarLayout;

    private Handler mImageHandler;
    private boolean mDontReloadAtOnResume;

    public interface OnUserActionListener
    {
        void showActionBar();

        void hideActionBar();

        void onClickImage(StayDetail stayDetail);

        void nextSlide();

        void prevSlide();

        void onSelectedImagePosition(int position);

        void doBooking(SaleRoomInformation saleRoomInformation);

        void doKakaotalkConsult();

        void showRoomType();

        void hideRoomType();

        void showMap();

        void clipAddress(String address);

        void showNavigatorDialog();

        void onCalendarClick(SaleTime checkInSaleTime, int nights, int placeIndex);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent == null)
        {
            finish();
            return;
        }

        mImageHandler = new ImageHandler(this);

        mCheckInSaleTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);

        int hotelIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, -1);
        int nights = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, 0);
        int calendarFlag = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, 0);
        int entryIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_ENTRY_INDEX, -1);
        String showTagPriceYn = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_SHOW_TAGPRICE_YN);

        mStayDetail = new StayDetail(hotelIndex, nights, entryIndex, showTagPriceYn);

        if (mCheckInSaleTime == null || hotelIndex == -1 || nights <= 0)
        {
            Util.restartApp(this);
            return;
        }

        if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_TYPE) == true)
        {
            mIsStartByShare = true;

            initLayout(null, null);

            if (calendarFlag == 1)
            {
                startCalendar(mCheckInSaleTime, nights, hotelIndex, false);
            }
        } else
        {
            mIsStartByShare = false;

            String hotelName = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME);
            String hotelImageUrl = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL);
            mDefaultImageUrl = hotelImageUrl;

            mStayDetail.categoryCode = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_CATEGORY);

            if (hotelName == null)
            {
                Util.restartApp(this);
                return;
            }

            mProvince = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);
            mArea = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_AREA);
            mViewPrice = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_PRICE, 0);

            initLayout(hotelName, hotelImageUrl);

            if (calendarFlag == 1)
            {
                startCalendar(mCheckInSaleTime, nights, hotelIndex, true);
            }
        }
    }

    private void initLayout(String hotelName, String imageUrl)
    {
        setContentView(R.layout.activity_hoteldetail);

        if (mStayDetailLayout == null)
        {
            mStayDetailLayout = new StayDetailLayout(this, imageUrl);
            mStayDetailLayout.setUserActionListener(mOnUserActionListener);
        }

        setLockUICancelable(true);
        initToolbar(hotelName);

        mOnUserActionListener.hideActionBar();
    }

    private void initToolbar(String title)
    {
        View toolbar = findViewById(R.id.toolbar);
        mDailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        mDailyToolbarLayout.initToolbar(title, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        }, false);

        mDailyToolbarLayout.setToolbarMenu(R.drawable.navibar_ic_share_01_black, -1);
        mDailyToolbarLayout.setToolbarMenuClickListener(mToolbarOptionsItemSelected);

        View backImage = findViewById(R.id.backView);
        View shareView = findViewById(R.id.shareView);

        backImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });


        shareView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mToolbarOptionsItemSelected.onClick(null);
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        if (mStayDetailLayout != null)
        {
            mStayDetailLayout.hideRoomType();

            if (mStayDetailLayout.getBookingStatus() != StayDetailLayout.STATUS_SOLD_OUT)
            {
                mStayDetailLayout.setBookingStatus(StayDetailLayout.STATUS_SEARCH_ROOM);
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
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed()
    {
        if (mStayDetailLayout != null)
        {
            switch (mStayDetailLayout.getBookingStatus())
            {
                case StayDetailLayout.STATUS_BOOKING:
                case StayDetailLayout.STATUS_NONE:
                    mOnUserActionListener.hideRoomType();
                    return;
            }
        }

        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockUI();

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_BOOKING:
            {
                setResult(resultCode);

                switch (resultCode)
                {
                    case RESULT_OK:
                    case CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY:
                    case CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER:
                        finish();
                        break;

                    case CODE_RESULT_ACTIVITY_REFRESH:
                        mDontReloadAtOnResume = false;
                        break;

                    default:
                        mDontReloadAtOnResume = true;
                        break;
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_LOGIN:
            case CODE_REQUEST_ACTIVITY_USERINFO_UPDATE:
            {
                mDontReloadAtOnResume = true;

                if (resultCode == RESULT_OK)
                {
                    mOnUserActionListener.doBooking(mSelectedSaleRoomInformation);
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_IMAGELIST:
            case CODE_REQUEST_ACTIVITY_ZOOMMAP:
            case CODE_REQUEST_ACTIVITY_SHAREKAKAO:
                mDontReloadAtOnResume = true;
                break;

            case CODE_REQUEST_ACTIVITY_CALENDAR:
                mDontReloadAtOnResume = true;

                if (resultCode == RESULT_OK)
                {
                    SaleTime checkInSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE);
                    SaleTime checkOutSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CHECKOUTDATE);

                    if (checkInSaleTime == null || checkOutSaleTime == null)
                    {
                        return;
                    }

                    mCheckInSaleTime = checkInSaleTime;

                    int nights = checkOutSaleTime.getOffsetDailyDay() - checkInSaleTime.getOffsetDailyDay();
                    mStayDetail = new StayDetail(mStayDetail.hotelIndex, nights, mStayDetail.entryIndex, mStayDetail.showTagPriceYn);

                    DailyNetworkAPI.getInstance(StayDetailActivity.this).requestHotelDetailInformation(mNetworkTag, mStayDetail.hotelIndex, mCheckInSaleTime.getDayOfDaysDateFormat("yyyyMMdd"), mStayDetail.nights, mHotelDetailInformationJsonResponseListener, StayDetailActivity.this);
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onError()
    {
        super.onError();

        finish();
    }

    private void moveToBooking(StayDetail stayDetail, SaleRoomInformation saleRoomInformation, SaleTime checkInSaleTime)
    {
        if (stayDetail == null || saleRoomInformation == null || checkInSaleTime == null)
        {
            return;
        }

        String imageUrl = null;
        ArrayList<ImageInformation> mImageInformationList = stayDetail.getImageInformationList();

        if (mImageInformationList != null && mImageInformationList.size() > 0)
        {
            imageUrl = mImageInformationList.get(0).url;
        }

        saleRoomInformation.categoryCode = stayDetail.categoryCode;

        Intent intent = HotelPaymentActivity.newInstance(StayDetailActivity.this, saleRoomInformation//
            , checkInSaleTime, imageUrl, stayDetail.hotelIndex, !Util.isTextEmpty(stayDetail.hotelBenefit), mProvince, mArea);

        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_BOOKING);
    }

    private void moveToAddSocialUserInformation(Customer user)
    {
        Intent intent = AddProfileSocialActivity.newInstance(this, user);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_USERINFO_UPDATE);
    }

    private void moveToUpdateUserPhoneNumber(Customer user, EditProfilePhoneActivity.Type type)
    {
        Intent intent = EditProfilePhoneActivity.newInstance(this, user.getUserIdx(), type);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_USERINFO_UPDATE);
    }

    private void startCalendar(SaleTime checkInSaleTime, int nights, int hotelIndex, boolean isAnimation)
    {
        if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        Intent intent = StayDetailCalendarActivity.newInstance(StayDetailActivity.this, checkInSaleTime, nights, hotelIndex, AnalyticsManager.ValueType.DETAIL, true, isAnimation);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CALENDAR);

        AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , Action.HOTEL_BOOKING_CALENDAR_CLICKED, AnalyticsManager.ValueType.DETAIL, null);
    }

    private void recordAnalyticsStayDetail(String screen, StayDetail stayDetail)
    {
        if (stayDetail == null)
        {
            return;
        }

        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.NAME, stayDetail.hotelName);
            params.put(AnalyticsManager.KeyType.GRADE, stayDetail.grade.getName(StayDetailActivity.this)); // 14
            params.put(AnalyticsManager.KeyType.DBENEFIT, Util.isTextEmpty(stayDetail.hotelBenefit) ? "no" : "yes"); // 3

            if (stayDetail.getSaleRoomList() == null || stayDetail.getSaleRoomList().size() == 0)
            {
                params.put(AnalyticsManager.KeyType.PRICE, "0");
            } else
            {
                params.put(AnalyticsManager.KeyType.PRICE, Integer.toString(stayDetail.getSaleRoomList().get(0).averageDiscount));
            }

            params.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(mStayDetail.nights));
            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(mStayDetail.hotelIndex)); // 15

            SaleTime checkOutSaleTime = mCheckInSaleTime.getClone(mCheckInSaleTime.getOffsetDailyDay() + stayDetail.nights);

            params.put(AnalyticsManager.KeyType.CHECK_IN, mCheckInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd")); // 1
            params.put(AnalyticsManager.KeyType.CHECK_OUT, checkOutSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd")); // 2

            params.put(AnalyticsManager.KeyType.ADDRESS, stayDetail.address);

            if (Util.isTextEmpty(stayDetail.categoryCode) == true) //
            {
                params.put(AnalyticsManager.KeyType.HOTEL_CATEGORY, AnalyticsManager.ValueType.EMPTY);
                params.put(AnalyticsManager.KeyType.CATEGORY, AnalyticsManager.ValueType.EMPTY);
            } else
            {
                params.put(AnalyticsManager.KeyType.HOTEL_CATEGORY, stayDetail.categoryCode);
                params.put(AnalyticsManager.KeyType.CATEGORY, stayDetail.categoryCode);
            }

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
            params.put(AnalyticsManager.KeyType.CHECK_IN_DATE, Long.toString(mCheckInSaleTime.getDayOfDaysDate().getTime()));
            params.put(AnalyticsManager.KeyType.LIST_INDEX, Integer.toString(stayDetail.entryIndex));
            params.put(AnalyticsManager.KeyType.RATING, stayDetail.satisfaction);
            params.put(AnalyticsManager.KeyType.SHOW_TAG_PRICE_YN, stayDetail.showTagPriceYn);

            AnalyticsManager.getInstance(StayDetailActivity.this).recordScreen(screen, params);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    private Map<String, String> recordAnalyticsBooking(StayDetail stayDetail, SaleRoomInformation saleRoomInformation)
    {
        if (stayDetail == null || saleRoomInformation == null)
        {
            return null;
        }

        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.NAME, mStayDetail.hotelName);
            params.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(mStayDetail.nights));
            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(mStayDetail.hotelIndex));
            params.put(AnalyticsManager.KeyType.HOTEL_CATEGORY, mStayDetail.categoryCode);

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

            params.put(AnalyticsManager.KeyType.PRICE_OF_SELECTED_ROOM, Integer.toString(saleRoomInformation.averageDiscount));
            params.put(AnalyticsManager.KeyType.CHECK_IN_DATE, Long.toString(mCheckInSaleTime.getDayOfDaysDate().getTime()));

            return params;
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return null;
    }

    private void shareKakao()
    {
        if (mDefaultImageUrl == null && mStayDetail.getImageInformationList() != null && mStayDetail.getImageInformationList().size() > 0)
        {
            mDefaultImageUrl = mStayDetail.getImageInformationList().get(0).url;
        }

        String name = DailyPreference.getInstance(StayDetailActivity.this).getUserName();

        if (Util.isTextEmpty(name) == true)
        {
            name = getString(R.string.label_friend) + "가";
        } else
        {
            name += "님이";
        }

        KakaoLinkManager.newInstance(StayDetailActivity.this).shareHotel(name, mStayDetail.hotelName, mStayDetail.address//
            , mStayDetail.hotelIndex//
            , mDefaultImageUrl//
            , mCheckInSaleTime, mStayDetail.nights);

        SaleTime checkOutSaleTime = mCheckInSaleTime.getClone(mCheckInSaleTime.getOffsetDailyDay() + mStayDetail.nights);

        HashMap<String, String> params = new HashMap<>();
        params.put(AnalyticsManager.KeyType.NAME, mStayDetail.hotelName);
        params.put(AnalyticsManager.KeyType.CHECK_IN, mCheckInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));
        params.put(AnalyticsManager.KeyType.CHECK_OUT, checkOutSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));

        //        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);
        //        params.put(AnalyticsManager.KeyType.CURRENT_TIME, dateFormat2.format(new Date()));
        params.put(AnalyticsManager.KeyType.CURRENT_TIME, DailyCalendar.format(new Date(), "yyyy-MM-dd'T'HH:mm:ss"));

        AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , Action.SOCIAL_SHARE_CLICKED, mStayDetail.hotelName, params);
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

            final Dialog shareDialog = new Dialog(StayDetailActivity.this);
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

                    shareKakao();
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
            mDailyToolbarLayout.setToolbarVisibility(true);
        }

        @Override
        public void hideActionBar()
        {
            mDailyToolbarLayout.setToolbarVisibility(false);
        }

        @Override
        public void onClickImage(StayDetail stayDetail)
        {
            if (isLockUiComponent() == true)
            {
                return;
            }

            lockUiComponent();

            Intent intent = new Intent(StayDetailActivity.this, ImageDetailListActivity.class);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURLLIST, stayDetail.getImageInformationList());
            intent.putExtra(NAME_INTENT_EXTRA_DATA_SELECTED_POSOTION, mCurrentImage);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_IMAGELIST);
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
            if (saleRoomInformation == null)
            {
                finish();
                return;
            }

            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            mSelectedSaleRoomInformation = saleRoomInformation;

            if (DailyHotel.isLogin() == false)
            {
                startLoginActivity();
            } else
            {
                lockUI();

                DailyNetworkAPI.getInstance(StayDetailActivity.this).requestUserInformationEx(mNetworkTag, mUserInformationExJsonResponseListener, StayDetailActivity.this);
            }

            String label = String.format("%s-%s", mStayDetail.hotelName, mSelectedSaleRoomInformation.roomName);
            AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
                , Action.BOOKING_CLICKED, label, recordAnalyticsBooking(mStayDetail, saleRoomInformation));
        }

        @Override
        public void doKakaotalkConsult()
        {
            if (isLockUiComponent() == true || isFinishing() == true)
            {
                return;
            }

            lockUiComponent();

            Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse("kakaolink://friend/@%EB%8D%B0%EC%9D%BC%EB%A6%AC%ED%98%B8%ED%85%94"));
            if (intent.resolveActivity(getPackageManager()) == null)
            {
                Util.installPackage(StayDetailActivity.this, "com.kakao.talk");
            } else
            {
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SHAREKAKAO);
            }

            AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
                , Action.KAKAO_INQUIRY_CLICKED, mStayDetail.hotelName, null);
        }

        @Override
        public void showRoomType()
        {
            if (isLockUiComponent() == true || isFinishing() == true)
            {
                return;
            }

            lockUiComponent();

            if (mStayDetailLayout != null)
            {
                mStayDetailLayout.showAnimationRoomType();
            }

            releaseUiComponent();

            recordAnalyticsStayDetail(Screen.DAILYHOTEL_DETAIL_ROOMTYPE, mStayDetail);
            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
                , Action.ROOM_TYPE_CLICKED, mStayDetail.hotelName, null);
        }

        @Override
        public void hideRoomType()
        {
            if (isLockUiComponent() == true || isFinishing() == true)
            {
                return;
            }

            lockUiComponent();

            if (mStayDetailLayout != null)
            {
                mStayDetailLayout.hideAnimationRoomType();
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

            Intent intent = ZoomMapActivity.newInstance(StayDetailActivity.this//
                , ZoomMapActivity.SourceType.HOTEL, mStayDetail.hotelName, mStayDetail.address//
                , mStayDetail.latitude, mStayDetail.longitude, mStayDetail.isOverseas);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_ZOOMMAP);

            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, Action.HOTEL_DETAIL_MAP_CLICKED, mStayDetail.hotelName, null);
        }

        @Override
        public void clipAddress(String address)
        {
            Util.clipText(StayDetailActivity.this, address);

            DailyToast.showToast(StayDetailActivity.this, R.string.message_detail_copy_address, Toast.LENGTH_SHORT);

            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, Action.HOTEL_DETAIL_ADDRESS_COPY_CLICKED, mStayDetail.hotelName, null);
        }

        @Override
        public void showNavigatorDialog()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Util.showShareMapDialog(StayDetailActivity.this, mStayDetail.hotelName//
                , mStayDetail.latitude, mStayDetail.longitude, mStayDetail.isOverseas//
                , AnalyticsManager.Category.HOTEL_BOOKINGS//
                , AnalyticsManager.Action.HOTEL_DETAIL_NAVIGATION_APP_CLICKED//
                , null);
        }

        @Override
        public void onCalendarClick(SaleTime checkInSaleTime, int nights, int placeIndex)
        {
            startCalendar(checkInSaleTime, nights, placeIndex, true);
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private DailyHotelJsonResponseListener mHotelDetailInformationJsonResponseListener = new DailyHotelJsonResponseListener()
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
                int msgCode = response.getInt("msgCode");

                JSONObject dataJSONObject = null;

                if (response.has("data") == true && response.isNull("data") == false)
                {
                    dataJSONObject = response.getJSONObject("data");
                }

                if (msgCode == 100 && dataJSONObject == null)
                {
                    msgCode = 4;
                }

                // 100	성공
                // 4	데이터가 없을시
                // 5	판매 마감시
                switch (msgCode)
                {
                    case 100:
                    {
                        mStayDetail.setData(dataJSONObject);

                        if (mIsStartByShare == true)
                        {
                            mStayDetail.categoryCode = mStayDetail.grade.getName(StayDetailActivity.this);

                            mIsStartByShare = false;
                            mDailyToolbarLayout.setToolbarText(mStayDetail.hotelName);
                        }

                        if (mStayDetailLayout != null)
                        {
                            mStayDetailLayout.setHotelDetail(mStayDetail, mCheckInSaleTime, mCurrentImage);
                        }

                        checkStayPrice(mIsStartByShare, mStayDetail, mViewPrice);

                        recordAnalyticsStayDetail(Screen.DAILYHOTEL_DETAIL, mStayDetail);
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
                                    setResult(CODE_RESULT_ACTIVITY_REFRESH);
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

                            DailyToast.showToast(StayDetailActivity.this, msg, Toast.LENGTH_SHORT);
                            setResult(CODE_RESULT_ACTIVITY_REFRESH);
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
                onError();
                setResult(CODE_RESULT_ACTIVITY_REFRESH);
                finish();
            } finally
            {
                unLockUI();
            }
        }

        private void checkStayPrice(boolean isStartByShare, StayDetail stayDetail, int listViewPrice)
        {
            // 판매 완료 혹은 가격이 변동되었는지 조사한다
            ArrayList<SaleRoomInformation> saleRoomList = stayDetail.getSaleRoomList();

            if (saleRoomList == null || saleRoomList.size() == 0)
            {
                showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_stay_detail_sold_out)//
                    , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                            setResult(CODE_RESULT_ACTIVITY_REFRESH);
                            finish();
                        }
                    });
            } else
            {
                if (isStartByShare == false)
                {
                    boolean hasPrice = false;

                    for (SaleRoomInformation saleRoomInformation : saleRoomList)
                    {
                        if (listViewPrice == saleRoomInformation.averageDiscount)
                        {
                            hasPrice = true;
                            break;
                        }
                    }

                    if (hasPrice == false)
                    {
                        setResult(CODE_RESULT_ACTIVITY_REFRESH);

                        showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_stay_detail_changed_price)//
                            , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                            {
                                @Override
                                public void onDismiss(DialogInterface dialog)
                                {
                                    mOnUserActionListener.showRoomType();
                                }
                            });
                    }
                }
            }
        }
    };

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
                    //                    int recommender = jsonObject.getInt("recommender_code");
                    boolean isDailyUser = jsonObject.getBoolean("is_daily_user");

                    if (isDailyUser == true)
                    {
                        DailyNetworkAPI.getInstance(StayDetailActivity.this).requestUserProfile(mNetworkTag, mUserProfileJsonResponseListener);
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
                            moveToBooking(mStayDetail, mSelectedSaleRoomInformation, mCheckInSaleTime);
                        }
                    }
                } else
                {
                    unLockUI();

                    String msg = response.getString("msg");

                    DailyToast.showToast(StayDetailActivity.this, msg, Toast.LENGTH_SHORT);
                }
            } catch (Exception e)
            {
                onError(e);
            }
        }
    };

    private DailyHotelJsonResponseListener mUserProfileJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONObject jsonObjectData = response.getJSONObject("data");

                    Customer user = new Customer();
                    user.setEmail(jsonObjectData.getString("email"));
                    user.setName(jsonObjectData.getString("name"));
                    user.setPhone(jsonObjectData.getString("phone"));
                    user.setUserIdx(jsonObjectData.getString("userIdx"));

                    boolean isVerified = jsonObjectData.getBoolean("verified");
                    boolean isPhoneVerified = jsonObjectData.getBoolean("phoneVerified");

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
                            moveToBooking(mStayDetail, mSelectedSaleRoomInformation, mCheckInSaleTime);
                        }
                    }
                } else
                {
                    String msg = response.getString("msg");
                    DailyToast.showToast(StayDetailActivity.this, msg, Toast.LENGTH_SHORT);

                    finish();
                }
            } catch (Exception e)
            {
                onError(e);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            StayDetailActivity.this.onErrorResponse(volleyError);
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

            if (mCheckInSaleTime == null)
            {
                Util.restartApp(StayDetailActivity.this);
                return;
            }

            try
            {
                if (mIsStartByShare == true)
                {
                    long todayDailyTime = response.getLong("dailyDateTime");

                    mCheckInSaleTime.setCurrentTime(response.getLong("currentDateTime"));
                    long shareDailyTime = mCheckInSaleTime.getDayOfDaysDate().getTime();

                    //                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
                    //                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                    //
                    //                    int shareDailyDay = Integer.parseInt(simpleDateFormat.format(new Date(shareDailyTime)));
                    //                    int todayDailyDay = Integer.parseInt(simpleDateFormat.format(new Date(todayDailyTime)));

                    int shareDailyDay = Integer.parseInt(DailyCalendar.format(shareDailyTime, "yyyyMMdd", TimeZone.getTimeZone("GMT")));
                    int todayDailyDay = Integer.parseInt(DailyCalendar.format(todayDailyTime, "yyyyMMdd", TimeZone.getTimeZone("GMT")));

                    // 지난 날의 호텔인 경우.
                    if (shareDailyDay < todayDailyDay)
                    {
                        DailyToast.showToast(StayDetailActivity.this, R.string.toast_msg_dont_past_hotelinfo, Toast.LENGTH_LONG);
                        finish();
                        return;
                    }
                }

                DailyNetworkAPI.getInstance(StayDetailActivity.this).requestHotelDetailInformation(mNetworkTag//
                    , mStayDetail.hotelIndex, mCheckInSaleTime.getDayOfDaysDateFormat("yyyyMMdd")//
                    , mStayDetail.nights, mHotelDetailInformationJsonResponseListener, StayDetailActivity.this);
            } catch (Exception e)
            {
                onError(e);
                unLockUI();

                finish();
            }
        }
    };

    private static class ImageHandler extends Handler
    {
        private final WeakReference<StayDetailActivity> mWeakReference;

        public ImageHandler(StayDetailActivity activity)
        {
            mWeakReference = new WeakReference<>(activity);
        }

        public void handleMessage(Message msg)
        {
            StayDetailActivity stayDetailActivity = mWeakReference.get();

            if (stayDetailActivity == null)
            {
                return;
            }

            if (stayDetailActivity.isFinishing() == true || stayDetailActivity.mStayDetailLayout == null)
            {
                return;
            }

            int direction = msg.arg1;

            stayDetailActivity.mCurrentImage = stayDetailActivity.mStayDetailLayout.getCurrentImage();

            if (direction > 0)
            {
                stayDetailActivity.mCurrentImage++;
            } else if (direction < 0)
            {
                stayDetailActivity.mCurrentImage--;
            }

            stayDetailActivity.mStayDetailLayout.setCurrentImage(stayDetailActivity.mCurrentImage);

            int autoSlide = msg.arg2;

            if (autoSlide == 1)
            {
                sendEmptyMessageDelayed(0, DURATION_HOTEL_IMAGE_SHOW);
            }
        }
    }
}
