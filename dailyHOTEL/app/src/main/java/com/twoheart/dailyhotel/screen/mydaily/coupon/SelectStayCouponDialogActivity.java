package com.twoheart.dailyhotel.screen.mydaily.coupon;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by android_sam on 2016. 5. 26..
 */
@Deprecated
public class SelectStayCouponDialogActivity extends BaseActivity
{
    public static final String INTENT_EXTRA_SELECT_COUPON = "selectCoupon";
    public static final String INTENT_EXTRA_HOTEL_IDX = "hotelIdx";
    public static final String INTENT_EXTRA_ROOM_IDX = "roomIdx";
    public static final String INTENT_EXTRA_CATEGORY_CODE = "categoryCode";
    public static final String INTENT_EXTRA_HOTEL_NAME = "hotelName";
    public static final String INTENT_EXTRA_ROOM_PRICE = "roomPrice";
    public static final String INTENT_EXTRA_CHECK_IN_DATE = "checkInDate";
    public static final String INTENT_EXTRA_CHECK_OUT_DATE = "checkOutDate";
    public static final String INTENT_EXTRA_MAX_COUPON_AMOUNT = "maxCouponAmount";

    SelectCouponDialogLayout mLayout;
    SelectStayCouponNetworkController mNetworkController;

    boolean mIsSetOk = false;

    int mHotelIdx;
    int mRoomIdx;

    private int mRoomPrice;
    private String mCategoryCode;
    private String mHotelName;
    String mCallByScreen;
    StayBookingDay mStayBookingDay;
    int mMaxCouponAmount;

    public static Intent newInstance(Context context, int hotelIdx, int roomIdx, String checkInDate, String checkOutDate//
        , String categoryCode, String hotelName, int roomPrice)
    {
        Intent intent = new Intent(context, SelectStayCouponDialogActivity.class);
        intent.putExtra(INTENT_EXTRA_HOTEL_IDX, hotelIdx);
        intent.putExtra(INTENT_EXTRA_ROOM_IDX, roomIdx);
        intent.putExtra(INTENT_EXTRA_CHECK_IN_DATE, checkInDate);
        intent.putExtra(INTENT_EXTRA_CHECK_OUT_DATE, checkOutDate);
        intent.putExtra(INTENT_EXTRA_CATEGORY_CODE, categoryCode);
        intent.putExtra(INTENT_EXTRA_HOTEL_NAME, hotelName);
        intent.putExtra(INTENT_EXTRA_ROOM_PRICE, roomPrice);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALL_BY_SCREEN, AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE);

        return intent;
    }

    public static Intent newInstance(Context context, int hotelIdx, String checkInDate, String checkOutDate //
        , String categoryCode, String hotelName)
    {
        Intent intent = new Intent(context, SelectStayCouponDialogActivity.class);
        intent.putExtra(INTENT_EXTRA_HOTEL_IDX, hotelIdx);
        intent.putExtra(INTENT_EXTRA_CHECK_IN_DATE, checkInDate);
        intent.putExtra(INTENT_EXTRA_CHECK_OUT_DATE, checkOutDate);
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

        String checkInDate = intent.getStringExtra(INTENT_EXTRA_CHECK_IN_DATE);
        String checkOutDate = intent.getStringExtra(INTENT_EXTRA_CHECK_OUT_DATE);

        mStayBookingDay = new StayBookingDay();

        try
        {
            mStayBookingDay.setCheckInDay(checkInDate);
            mStayBookingDay.setCheckOutDay(checkOutDate);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            Util.restartApp(this);
            return;
        }


        if (DailyTextUtils.isTextEmpty(mCallByScreen) == true)
        {
            Util.restartApp(this);
            return;
        }

        switch (mCallByScreen)
        {
            case AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE:
            {
                mHotelIdx = intent.getIntExtra(INTENT_EXTRA_HOTEL_IDX, -1);
                mRoomIdx = intent.getIntExtra(INTENT_EXTRA_ROOM_IDX, -1);
                mCategoryCode = intent.getStringExtra(INTENT_EXTRA_CATEGORY_CODE);
                mHotelName = intent.getStringExtra(INTENT_EXTRA_HOTEL_NAME);
                mRoomPrice = intent.getIntExtra(INTENT_EXTRA_ROOM_PRICE, 0);
                break;
            }

            case AnalyticsManager.Screen.DAILYHOTEL_DETAIL:
            {
                mHotelIdx = intent.getIntExtra(INTENT_EXTRA_HOTEL_IDX, -1);
                mCategoryCode = intent.getStringExtra(INTENT_EXTRA_CATEGORY_CODE);
                mHotelName = intent.getStringExtra(INTENT_EXTRA_HOTEL_NAME);
                break;
            }
        }

        mLayout = new SelectCouponDialogLayout(this, mOnEventListener);
        mNetworkController = new SelectStayCouponNetworkController(this, mNetworkTag, mNetworkControllerListener);

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

        if (mStayBookingDay == null)
        {
            Util.restartApp(this);
            return;
        }

        try
        {
            switch (mCallByScreen)
            {
                case AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE:
                    mNetworkController.requestCouponList(mHotelIdx, mRoomIdx, mStayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT), mStayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT));
                    break;

                case AnalyticsManager.Screen.DAILYHOTEL_DETAIL:
                    mNetworkController.requestCouponList(mHotelIdx, mStayBookingDay.getCheckInDay("yyyy-MM-dd"), mStayBookingDay.getNights());
                    break;
            }
        } catch (Exception e)
        {
            Util.restartApp(this);
        }
    }

    @Override
    public void finish()
    {
        if (mIsSetOk == false)
        {
            recordCancelAnalytics();

            Intent intent = new Intent();
            intent.putExtra(INTENT_EXTRA_MAX_COUPON_AMOUNT, mMaxCouponAmount);
            SelectStayCouponDialogActivity.this.setResult(RESULT_CANCELED, intent);
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
                case AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE:
                {
                    if (mLayout.getCouponCount() == 0)
                    {
                        // empty list
                        String label = mCategoryCode + "-" + mHotelName + "-" + mRoomPrice;

                        AnalyticsManager.getInstance(SelectStayCouponDialogActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, //
                            AnalyticsManager.Action.HOTEL_COUPON_NOT_FOUND, label, null);
                    } else
                    {
                        AnalyticsManager.getInstance(SelectStayCouponDialogActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, //
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
            Crashlytics.logException(e);
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
            intent.putExtra(INTENT_EXTRA_MAX_COUPON_AMOUNT, mMaxCouponAmount);

            SelectStayCouponDialogActivity.this.setResult(RESULT_OK, intent);
            SelectStayCouponDialogActivity.this.finish();

            AnalyticsManager.getInstance(SelectStayCouponDialogActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, //
                AnalyticsManager.Action.HOTEL_COUPON_SELECTED, coupon.title, null);
        }

        @Override
        public void onCouponDownloadClick(Coupon coupon)
        {
            if (coupon == null || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            lockUI();

            // 쿠폰 다운로드 시도!
            mNetworkController.requestDownloadCoupon(coupon);
        }

        @Override
        public void finish()
        {
            SelectStayCouponDialogActivity.this.finish();
        }
    };

    // ///////////////////////////////////////////////////
    // NetworkController
    // ///////////////////////////////////////////////////

    private SelectStayCouponNetworkController.OnNetworkControllerListener mNetworkControllerListener = new SelectStayCouponNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onCouponList(List<Coupon> list, int maxCouponAmount)
        {
            boolean isEmpty = (list == null || list.size() == 0);
            mMaxCouponAmount = maxCouponAmount;

            switch (mCallByScreen)
            {
                case AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE:
                {
                    if (isEmpty == true)
                    {
                        mLayout.setVisibility(false);
                        showSimpleDialog(getString(R.string.label_booking_select_coupon), getString(R.string.message_select_coupon_empty), //
                            getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                            {
                                @Override
                                public void onDismiss(DialogInterface dialog)
                                {
                                    finish();
                                }
                            });

                        AnalyticsManager.getInstance(SelectStayCouponDialogActivity.this) //
                            .recordScreen(SelectStayCouponDialogActivity.this, AnalyticsManager.Screen.DAILY_HOTEL_UNAVAILABLE_COUPON_LIST, null);
                    } else
                    {
                        mLayout.setVisibility(true);
                        mLayout.setTitle(R.string.label_select_coupon);
                        mLayout.setTwoButtonLayout(true, R.string.dialog_btn_text_select, R.string.dialog_btn_text_cancel);

                        mLayout.setData(list, true);

                        AnalyticsManager.getInstance(SelectStayCouponDialogActivity.this) //
                            .recordScreen(SelectStayCouponDialogActivity.this, AnalyticsManager.Screen.DAILY_HOTEL_AVAILABLE_COUPON_LIST, null);
                    }
                    break;
                }

                case AnalyticsManager.Screen.DAILYHOTEL_DETAIL:
                {
                    mLayout.setVisibility(true);

                    boolean hasDownloadCoupon = false;

                    if (isEmpty == false)
                    {
                        for (Coupon coupon : list)
                        {
                            if (coupon.isDownloaded == false)
                            {
                                hasDownloadCoupon = true;
                                break;
                            }
                        }
                    }

                    mLayout.setTitle(hasDownloadCoupon == true ? R.string.coupon_download_coupon : R.string.coupon_dont_download_coupon);
                    mLayout.setOneButtonLayout(true, R.string.dialog_btn_text_close);
                    mLayout.setData(list, false);
                    break;
                }
            }

            unLockUI();
        }

        @Override
        public void onDownloadCoupon(String couponCode)
        {
            lockUI();

            Coupon coupon = mLayout.getCoupon(couponCode);
            recordAnalytics(coupon);

            try
            {
                switch (mCallByScreen)
                {
                    case AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE:
                        mNetworkController.requestCouponList(mHotelIdx, mRoomIdx, mStayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT), mStayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT));
                        break;

                    case AnalyticsManager.Screen.DAILYHOTEL_DETAIL:
                        mNetworkController.requestCouponList(mHotelIdx, mStayBookingDay.getCheckInDay("yyyy-MM-dd"), mStayBookingDay.getNights());
                        break;
                }
            } catch (Exception e)
            {
                Util.restartApp(SelectStayCouponDialogActivity.this);
            }
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            SelectStayCouponDialogActivity.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            SelectStayCouponDialogActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            SelectStayCouponDialogActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            SelectStayCouponDialogActivity.this.onErrorToastMessage(message);
            finish();
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            SelectStayCouponDialogActivity.this.onErrorResponse(call, response);
            finish();
        }

        private void recordAnalytics(Coupon coupon)
        {
            try
            {
                switch (mCallByScreen)
                {
                    case AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE:
                    {
                        Map<String, String> paramsMap = new HashMap<>();
                        paramsMap.put(AnalyticsManager.KeyType.COUPON_NAME, coupon.title);
                        paramsMap.put(AnalyticsManager.KeyType.COUPON_AVAILABLE_ITEM, coupon.availableItem);
                        paramsMap.put(AnalyticsManager.KeyType.PRICE_OFF, Integer.toString(coupon.amount));
                        paramsMap.put(AnalyticsManager.KeyType.DOWNLOAD_DATE, DailyCalendar.format(new Date(), "yyyyMMddHHmm"));
                        paramsMap.put(AnalyticsManager.KeyType.EXPIRATION_DATE, DailyCalendar.convertDateFormatString(coupon.validTo, DailyCalendar.ISO_8601_FORMAT, "yyyyMMddHHmm"));
                        paramsMap.put(AnalyticsManager.KeyType.DOWNLOAD_FROM, "booking");
                        paramsMap.put(AnalyticsManager.KeyType.COUPON_CODE, coupon.couponCode);

                        if (coupon.availableInGourmet == true && coupon.availableInStay == true)
                        {
                            paramsMap.put(AnalyticsManager.KeyType.KIND_OF_COUPON, AnalyticsManager.ValueType.ALL);
                        } else if (coupon.availableInStay == true)
                        {
                            paramsMap.put(AnalyticsManager.KeyType.KIND_OF_COUPON, AnalyticsManager.ValueType.STAY);
                        } else if (coupon.availableInGourmet == true)
                        {
                            paramsMap.put(AnalyticsManager.KeyType.KIND_OF_COUPON, AnalyticsManager.ValueType.GOURMET);
                        }

                        AnalyticsManager.getInstance(SelectStayCouponDialogActivity.this).recordEvent(AnalyticsManager.Category.COUPON_BOX//
                            , AnalyticsManager.Action.COUPON_DOWNLOAD_CLICKED, "booking-" + coupon.title, paramsMap);

                        break;
                    }

                    case AnalyticsManager.Screen.DAILYHOTEL_DETAIL:
                    {
                        Map<String, String> paramsMap = new HashMap<>();
                        paramsMap.put(AnalyticsManager.KeyType.COUPON_NAME, coupon.title);
                        paramsMap.put(AnalyticsManager.KeyType.COUPON_AVAILABLE_ITEM, coupon.availableItem);
                        paramsMap.put(AnalyticsManager.KeyType.PRICE_OFF, Integer.toString(coupon.amount));
                        paramsMap.put(AnalyticsManager.KeyType.DOWNLOAD_DATE, DailyCalendar.format(new Date(), "yyyyMMddHHmm"));
                        paramsMap.put(AnalyticsManager.KeyType.EXPIRATION_DATE, DailyCalendar.convertDateFormatString(coupon.validTo, DailyCalendar.ISO_8601_FORMAT, "yyyyMMddHHmm"));
                        paramsMap.put(AnalyticsManager.KeyType.DOWNLOAD_FROM, "booking");
                        paramsMap.put(AnalyticsManager.KeyType.COUPON_CODE, coupon.couponCode);

                        if (coupon.availableInGourmet == true && coupon.availableInStay == true)
                        {
                            paramsMap.put(AnalyticsManager.KeyType.KIND_OF_COUPON, AnalyticsManager.ValueType.ALL);
                        } else if (coupon.availableInStay == true)
                        {
                            paramsMap.put(AnalyticsManager.KeyType.KIND_OF_COUPON, AnalyticsManager.ValueType.STAY);
                        } else if (coupon.availableInGourmet == true)
                        {
                            paramsMap.put(AnalyticsManager.KeyType.KIND_OF_COUPON, AnalyticsManager.ValueType.GOURMET);
                        }

                        AnalyticsManager.getInstance(SelectStayCouponDialogActivity.this).recordEvent(AnalyticsManager.Category.COUPON_BOX//
                            , AnalyticsManager.Action.COUPON_DOWNLOAD_CLICKED, "detail-" + coupon.title, paramsMap);
                        break;
                    }
                }
            } catch (ParseException e)
            {
                Crashlytics.log("Select Coupon::coupon.validTo: " + (coupon != null ? coupon.validTo : ""));
                ExLog.d(e.toString());
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
    };
}
