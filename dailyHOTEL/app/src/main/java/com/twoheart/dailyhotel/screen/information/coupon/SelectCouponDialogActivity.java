package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by android_sam on 2016. 5. 26..
 */
public class SelectCouponDialogActivity extends BaseActivity
{

    public static final String INTENT_EXTRA_SELECT_COUPON = "selectCoupon";
    public static final String INTENT_EXTRA_HOTEL_IDX = "hotelIdx";
    public static final String INTENT_EXTRA_ROOM_IDX = "roomIdx";
    public static final String INTENT_EXTRA_CHECK_IN_DATE = "checkInDate";
    public static final String INTENT_EXTRA_CHECK_OUT_DATE = "checkOutDate";
    public static final String INTENT_EXTRA_NIGHTS = "ngihts";
    public static final String INTENT_EXTRA_CATEGORY_CODE = "categoryCode";
    public static final String INTENT_EXTRA_HOTEL_NAME = "hotelName";
    public static final String INTENT_EXTRA_ROOM_PRICE = "roomPrice";

    private SelectCouponDialogLayout mLayout;
    private SelectCouponNetworkController mNetworkController;

    private boolean mIsSetOk = false;

    private int mHotelIdx;
    private int mRoomIdx;
    private int mNights;

    private String mRoomPrice;
    private String mCheckInDate;
    private String mCheckOutDate;
    private String mCategoryCode;
    private String mHotelName;
    private String mCallByScreen;

    public static Intent newInstance(Context context, int hotelIdx, int roomIdx, String checkInDate, //
                                     String checkOutDate, String categoryCode, String hotelName, String roomPrice)
    {
        Intent intent = new Intent(context, SelectCouponDialogActivity.class);
        intent.putExtra(INTENT_EXTRA_HOTEL_IDX, hotelIdx);
        intent.putExtra(INTENT_EXTRA_ROOM_IDX, roomIdx);
        intent.putExtra(INTENT_EXTRA_CHECK_IN_DATE, checkInDate);
        intent.putExtra(INTENT_EXTRA_CHECK_OUT_DATE, checkOutDate);
        intent.putExtra(INTENT_EXTRA_CATEGORY_CODE, categoryCode);
        intent.putExtra(INTENT_EXTRA_HOTEL_NAME, hotelName);
        intent.putExtra(INTENT_EXTRA_ROOM_PRICE, roomPrice);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALL_BY_SCREEN, AnalyticsManager.Screen.DAILYHOTEL_PAYMENT);

        return intent;
    }

    public static Intent newInstance(Context context, int hotelIdx, String checkInDate, //
                                     int nights, String categoryCode, String hotelName)
    {
        Intent intent = new Intent(context, SelectCouponDialogActivity.class);
        intent.putExtra(INTENT_EXTRA_HOTEL_IDX, hotelIdx);
        intent.putExtra(INTENT_EXTRA_CHECK_IN_DATE, checkInDate);
        intent.putExtra(INTENT_EXTRA_NIGHTS, nights);
        intent.putExtra(INTENT_EXTRA_CATEGORY_CODE, categoryCode);
        intent.putExtra(INTENT_EXTRA_HOTEL_NAME, hotelName);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALL_BY_SCREEN, AnalyticsManager.Screen.DAILYHOTEL_DETAIL);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        Intent intent = getIntent();
        if (intent == null)
        {
            finish();
            return;
        }

        mCallByScreen = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_CALL_BY_SCREEN);

        switch (mCallByScreen)
        {
            case AnalyticsManager.Screen.DAILYHOTEL_PAYMENT:
            {
                mHotelIdx = intent.getIntExtra(INTENT_EXTRA_HOTEL_IDX, -1);
                mCheckInDate = intent.getStringExtra(INTENT_EXTRA_CHECK_IN_DATE);
                mCheckOutDate = intent.getStringExtra(INTENT_EXTRA_CHECK_OUT_DATE);

                mRoomIdx = intent.getIntExtra(INTENT_EXTRA_ROOM_IDX, -1);
                mCategoryCode = intent.getStringExtra(INTENT_EXTRA_CATEGORY_CODE);
                mHotelName = intent.getStringExtra(INTENT_EXTRA_HOTEL_NAME);
                mRoomPrice = intent.getStringExtra(INTENT_EXTRA_ROOM_PRICE);
                break;
            }

            case AnalyticsManager.Screen.DAILYHOTEL_DETAIL:
            {
                mHotelIdx = intent.getIntExtra(INTENT_EXTRA_HOTEL_IDX, -1);
                mCheckInDate = intent.getStringExtra(INTENT_EXTRA_CHECK_IN_DATE);
                mNights = intent.getIntExtra(INTENT_EXTRA_NIGHTS, 1);

                mCategoryCode = intent.getStringExtra(INTENT_EXTRA_CATEGORY_CODE);
                mHotelName = intent.getStringExtra(INTENT_EXTRA_HOTEL_NAME);
                break;
            }
        }

        mLayout = new SelectCouponDialogLayout(this, mOnEventListener);
        mNetworkController = new SelectCouponNetworkController(this, mNetworkTag, mNetworkControllerListener);

        setContentView(mLayout.onCreateView(R.layout.activity_select_coupon_dialog));
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        lockUI();

        switch (mCallByScreen)
        {
            case AnalyticsManager.Screen.DAILYHOTEL_PAYMENT:
            {
                mNetworkController.requestCouponList(mHotelIdx, mRoomIdx, mCheckInDate, mCheckOutDate);
                break;
            }

            case AnalyticsManager.Screen.DAILYHOTEL_DETAIL:
            {
                mNetworkController.requestCouponList(mHotelIdx, mCheckInDate, mNights);
                break;
            }
        }
    }

    @Override
    public void finish()
    {
        if (mIsSetOk == false)
        {
            recordCancelAnalytics();
        }

        super.finish();

        overridePendingTransition(0, 0);
    }

    private void recordCancelAnalytics()
    {
        try
        {
            switch (mCallByScreen)
            {
                case AnalyticsManager.Screen.DAILYHOTEL_PAYMENT:
                {
                    if (mLayout.getCouponCount() == 0)
                    {
                        // empty list
                        String label = mCategoryCode + "-" + mHotelName + "-" + mRoomPrice;

                        AnalyticsManager.getInstance(SelectCouponDialogActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, //
                            AnalyticsManager.Action.HOTEL_COUPON_NOT_FOUND, label, null);
                    } else
                    {
                        AnalyticsManager.getInstance(SelectCouponDialogActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, //
                            AnalyticsManager.Action.HOTEL_USING_COUPON_CANCEL_CLICKED, AnalyticsManager.Label.HOTEL_USING_COUPON_CANCEL, null);
                    }
                    break;
                }

                case AnalyticsManager.Screen.DAILYHOTEL_DETAIL:
                {
                    break;
                }
            }
        } catch (Exception e)
        {
            if (Constants.DEBUG == false)
            {
                Crashlytics.logException(e);
            }
        }
    }

    private SelectCouponDialogLayout.OnEventListener mOnEventListener = new SelectCouponDialogLayout.OnEventListener()
    {
        @Override
        public void setResult(Coupon coupon)
        {
            lockUI();

            mIsSetOk = true;

            Intent intent = new Intent();
            intent.putExtra(INTENT_EXTRA_SELECT_COUPON, coupon);

            SelectCouponDialogActivity.this.setResult(RESULT_OK, intent);
            SelectCouponDialogActivity.this.finish();

            AnalyticsManager.getInstance(SelectCouponDialogActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, //
                AnalyticsManager.Action.HOTEL_COUPON_SELECTED, coupon.title, null);
        }

        @Override
        public void onCouponDownloadClick(Coupon coupon)
        {
            // 쿠폰 다운로드 시도!
            mNetworkController.requestDownloadCoupon(coupon);
        }

        @Override
        public void finish()
        {
            SelectCouponDialogActivity.this.finish();
        }
    };

    // ///////////////////////////////////////////////////
    // NetworkController
    // ///////////////////////////////////////////////////

    private SelectCouponNetworkController.OnNetworkControllerListener mNetworkControllerListener = new SelectCouponNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onCouponList(List<Coupon> list)
        {
            boolean isEmpty = (list == null || list.size() == 0);

            mLayout.setVisibility(true);

            switch (mCallByScreen)
            {
                case AnalyticsManager.Screen.DAILYHOTEL_PAYMENT:
                {
                    mLayout.setTitle(R.string.label_select_coupon);

                    if (isEmpty == true)
                    {
                        mLayout.setMessage(R.string.message_select_coupon_empty);
                        mLayout.setOneButtonLayout(true, R.string.dialog_btn_text_confirm);
                    } else
                    {
                        mLayout.setMessage(R.string.message_select_coupon_selected);
                        mLayout.setTwoButtonLayout(true, R.string.dialog_btn_text_select, R.string.dialog_btn_text_cancel);
                    }

                    mLayout.setData(list, true);

                    if (isEmpty == true)
                    {
                        AnalyticsManager.getInstance(SelectCouponDialogActivity.this) //
                            .recordScreen(AnalyticsManager.Screen.DAILY_HOTEL_UNAVAILABLE_COUPON_LIST);
                    } else
                    {
                        AnalyticsManager.getInstance(SelectCouponDialogActivity.this) //
                            .recordScreen(AnalyticsManager.Screen.DAILY_HOTEL_AVAILABLE_COUPON_LIST);
                    }
                    break;
                }

                case AnalyticsManager.Screen.DAILYHOTEL_DETAIL:
                {
                    mLayout.setTitle(R.string.coupon_download_coupon);

                    boolean isDownloadedAll = true;

                    for (Coupon coupon : list)
                    {
                        if (coupon.isDownloaded == false)
                        {
                            isDownloadedAll = false;
                            break;
                        }
                    }

                    if (isDownloadedAll == true)
                    {
                        mLayout.setMessage(R.string.message_downloaded_all_coupon);
                    } else
                    {
                        mLayout.setMessage(R.string.message_please_download_coupon);
                    }

                    mLayout.setOneButtonLayout(true, R.string.dialog_btn_text_close);
                    mLayout.setData(list, false);
                    break;
                }
            }

            unLockUI();
        }

        @Override
        public void onDownloadCoupon(String userCouponCode)
        {
            lockUI();

            Coupon coupon = mLayout.getCoupon(userCouponCode);
            recordAnalytics(coupon);

            switch (mCallByScreen)
            {
                case AnalyticsManager.Screen.DAILYHOTEL_PAYMENT:
                {
                    mNetworkController.requestCouponList(mHotelIdx, mRoomIdx, mCheckInDate, mCheckOutDate);
                    break;
                }

                case AnalyticsManager.Screen.DAILYHOTEL_DETAIL:
                {
                    mNetworkController.requestCouponList(mHotelIdx, mCheckInDate, mNights);
                    break;
                }
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            SelectCouponDialogActivity.this.onErrorResponse(volleyError);
            finish();
        }

        @Override
        public void onError(Exception e)
        {
            SelectCouponDialogActivity.this.onError(e);
            finish();
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            SelectCouponDialogActivity.this.onErrorPopupMessage(msgCode, message);
            finish();
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            SelectCouponDialogActivity.this.onErrorToastMessage(message);
            finish();
        }

        private void recordAnalytics(Coupon coupon)
        {
            try
            {
                switch (mCallByScreen)
                {
                    case AnalyticsManager.Screen.DAILYHOTEL_PAYMENT:
                    {
                        Map<String, String> paramsMap = new HashMap<>();
                        paramsMap.put(AnalyticsManager.KeyType.COUPON_NAME, coupon.title);
                        paramsMap.put(AnalyticsManager.KeyType.COUPON_AVAILABLE_ITEM, coupon.availableItem);
                        paramsMap.put(AnalyticsManager.KeyType.PRICE_OFF, Integer.toString(coupon.amount));
                        //                paramsMap.put(AnalyticsManager.KeyType.DOWNLOAD_DATE, Util.simpleDateFormat(new Date(), "yyyyMMddHHmm"));
                        paramsMap.put(AnalyticsManager.KeyType.DOWNLOAD_DATE, DailyCalendar.format(new Date(), "yyyyMMddHHmm"));
                        //                paramsMap.put(AnalyticsManager.KeyType.EXPIRATION_DATE, Util.simpleDateFormatISO8601toFormat(coupon.validTo, "yyyyMMddHHmm"));
                        paramsMap.put(AnalyticsManager.KeyType.EXPIRATION_DATE, DailyCalendar.convertDateFormatString(coupon.validTo, DailyCalendar.ISO_8601_FORMAT, "yyyyMMddHHmm"));
                        paramsMap.put(AnalyticsManager.KeyType.DOWNLOAD_FROM, "booking");
                        paramsMap.put(AnalyticsManager.KeyType.COUPON_CODE, AnalyticsManager.ValueType.EMPTY);

                        AnalyticsManager.getInstance(SelectCouponDialogActivity.this).recordEvent(AnalyticsManager.Category.COUPON_BOX//
                            , AnalyticsManager.Action.COUPON_DOWNLOAD_CLICKED, "Booking-" + coupon.title, paramsMap);

                        break;
                    }

                    case AnalyticsManager.Screen.DAILYHOTEL_DETAIL:
                    {
                        break;
                    }
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
    };
}
