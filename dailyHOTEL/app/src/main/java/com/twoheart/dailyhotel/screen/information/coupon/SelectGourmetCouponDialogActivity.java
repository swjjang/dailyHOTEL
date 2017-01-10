package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class SelectGourmetCouponDialogActivity extends BaseActivity
{
    public static final String INTENT_EXTRA_SELECT_COUPON = "selectCoupon";
    public static final String INTENT_EXTRA_GOURMET_IDX = "gourmetIdx";
    public static final String INTENT_EXTRA_TICKET_IDX = "ticketIdx";
    public static final String INTENT_EXTRA_DATE = "date";
    public static final String INTENT_EXTRA_GOURMET_NAME = "gourmetName";
    public static final String INTENT_EXTRA_TICKET_PRICE = "ticketPrice";
    public static final String INTENT_EXTRA_TICKET_COUNT = "ticketCount";

    private SelectCouponDialogLayout mLayout;
    private SelectGourmetCouponNetworkController mNetworkController;

    private boolean mIsSetOk = false;

    private int mGourmetIdx;
    private int mTicketIdx;

    private int mTicketCount;
    private String mDate;
    private String mGourmetName;
    private String mCallByScreen;

    public static Intent newInstance(Context context, int gourmetIdx, int ticketIdx, String date, //
                                     String gourmetName, int ticketCount)
    {
        Intent intent = new Intent(context, SelectGourmetCouponDialogActivity.class);
        intent.putExtra(INTENT_EXTRA_GOURMET_IDX, gourmetIdx);
        intent.putExtra(INTENT_EXTRA_TICKET_IDX, ticketIdx);
        intent.putExtra(INTENT_EXTRA_DATE, date);
        intent.putExtra(INTENT_EXTRA_GOURMET_NAME, gourmetName);
        intent.putExtra(INTENT_EXTRA_TICKET_COUNT, ticketCount);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALL_BY_SCREEN, AnalyticsManager.Screen.DAILYGOURMET_PAYMENT);

        return intent;
    }

    public static Intent newInstance(Context context, int gourmetIdx, String date, String gourmetName)
    {
        Intent intent = new Intent(context, SelectGourmetCouponDialogActivity.class);
        intent.putExtra(INTENT_EXTRA_GOURMET_IDX, gourmetIdx);
        intent.putExtra(INTENT_EXTRA_DATE, date);
        intent.putExtra(INTENT_EXTRA_GOURMET_NAME, gourmetName);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALL_BY_SCREEN, AnalyticsManager.Screen.DAILYGOURMET_DETAIL);

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
            case AnalyticsManager.Screen.DAILYGOURMET_PAYMENT:
            {
                mGourmetIdx = intent.getIntExtra(INTENT_EXTRA_GOURMET_IDX, -1);
                mDate = intent.getStringExtra(INTENT_EXTRA_DATE);
                mTicketIdx = intent.getIntExtra(INTENT_EXTRA_TICKET_IDX, -1);
                mGourmetName = intent.getStringExtra(INTENT_EXTRA_GOURMET_NAME);
                mTicketCount = intent.getIntExtra(INTENT_EXTRA_TICKET_COUNT, 0);
                break;
            }

            case AnalyticsManager.Screen.DAILYGOURMET_DETAIL:
            {
                mGourmetIdx = intent.getIntExtra(INTENT_EXTRA_GOURMET_IDX, -1);
                mDate = intent.getStringExtra(INTENT_EXTRA_DATE);

                mGourmetName = intent.getStringExtra(INTENT_EXTRA_GOURMET_NAME);
                break;
            }
        }

        mLayout = new SelectCouponDialogLayout(this, mOnEventListener);
        mNetworkController = new SelectGourmetCouponNetworkController(this, mNetworkTag, mNetworkControllerListener);

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
            case AnalyticsManager.Screen.DAILYGOURMET_PAYMENT:
            {
                if (Util.isTextEmpty(mDate) == true)
                {
                    Util.restartApp(this);
                    return;
                }

                mNetworkController.requestCouponList(mTicketIdx, mTicketCount);
                break;
            }

            case AnalyticsManager.Screen.DAILYGOURMET_DETAIL:
            {
                if (Util.isTextEmpty(mDate) == true)
                {
                    Util.restartApp(this);
                    return;
                }

                mNetworkController.requestCouponList(mGourmetIdx, mDate);
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
                case AnalyticsManager.Screen.DAILYGOURMET_PAYMENT:
                {
                    if (mLayout.getCouponCount() == 0)
                    {
                        //                        // empty list
                        //                        String label = mCategoryCode + "-" + mHotelName + "-" + mRoomPrice;
                        //
                        //                        AnalyticsManager.getInstance(SelectGourmetCouponDialogActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, //
                        //                            AnalyticsManager.Action.HOTEL_COUPON_NOT_FOUND, label, null);
                    } else
                    {
                        AnalyticsManager.getInstance(SelectGourmetCouponDialogActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS, //
                            AnalyticsManager.Action.GOURMET_USING_COUPON_CANCEL_CLICKED, AnalyticsManager.Label.GOURMET_USING_COUPON_CANCEL, null);
                    }
                    break;
                }

                case AnalyticsManager.Screen.DAILYGOURMET_DETAIL:
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

            SelectGourmetCouponDialogActivity.this.setResult(RESULT_OK, intent);
            SelectGourmetCouponDialogActivity.this.finish();

            AnalyticsManager.getInstance(SelectGourmetCouponDialogActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS, //
                AnalyticsManager.Action.GOURMET_COUPON_SELECTED, coupon.title, null);
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
            SelectGourmetCouponDialogActivity.this.finish();
        }
    };

    // ///////////////////////////////////////////////////
    // NetworkController
    // ///////////////////////////////////////////////////

    private SelectGourmetCouponNetworkController.OnNetworkControllerListener mNetworkControllerListener = new SelectGourmetCouponNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onCouponList(List<Coupon> list)
        {
            boolean isEmpty = (list == null || list.size() == 0);

            switch (mCallByScreen)
            {
                case AnalyticsManager.Screen.DAILYGOURMET_PAYMENT:
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

                        //                        AnalyticsManager.getInstance(SelectGourmetCouponDialogActivity.this) //
                        //                            .recordScreen(AnalyticsManager.Screen.DAILY_GOURMET_UNAVAILABLE_COUPON_LIST);
                    } else
                    {
                        mLayout.setVisibility(true);
                        mLayout.setTitle(R.string.label_select_coupon);
                        mLayout.setTwoButtonLayout(true, R.string.dialog_btn_text_select, R.string.dialog_btn_text_cancel);

                        mLayout.setData(list, true);

                        AnalyticsManager.getInstance(SelectGourmetCouponDialogActivity.this) //
                            .recordScreen(AnalyticsManager.Screen.DAILY_GOURMET_AVAILABLE_COUPON_LIST);
                    }
                    break;
                }

                case AnalyticsManager.Screen.DAILYGOURMET_DETAIL:
                {
                    mLayout.setVisibility(true);

                    boolean hasDownloadCoupon = false;

                    for (Coupon coupon : list)
                    {
                        if (coupon.isDownloaded == false)
                        {
                            hasDownloadCoupon = true;
                            break;
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
        public void onDownloadCoupon(String userCouponCode)
        {
            lockUI();

            Coupon coupon = mLayout.getCoupon(userCouponCode);
            analyticsDownloadCoupon(coupon);

            switch (mCallByScreen)
            {
                case AnalyticsManager.Screen.DAILYGOURMET_PAYMENT:
                {
                    if (Util.isTextEmpty(mDate) == true)
                    {
                        Util.restartApp(SelectGourmetCouponDialogActivity.this);
                        return;
                    }

                    mNetworkController.requestCouponList(mTicketIdx, mTicketCount);
                    break;
                }

                case AnalyticsManager.Screen.DAILYGOURMET_DETAIL:
                {
                    if (Util.isTextEmpty(mDate) == true)
                    {
                        Util.restartApp(SelectGourmetCouponDialogActivity.this);
                        return;
                    }

                    mNetworkController.requestCouponList(mGourmetIdx, mDate);
                    break;
                }
            }
        }

        @Override
        public void onError(Throwable e)
        {
            SelectGourmetCouponDialogActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            SelectGourmetCouponDialogActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            SelectGourmetCouponDialogActivity.this.onErrorToastMessage(message);
            finish();
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            SelectGourmetCouponDialogActivity.this.onErrorResponse(call, response);
        }

        private void analyticsDownloadCoupon(Coupon coupon)
        {
            try
            {
                switch (mCallByScreen)
                {
                    case AnalyticsManager.Screen.DAILYGOURMET_PAYMENT:
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

                        AnalyticsManager.getInstance(SelectGourmetCouponDialogActivity.this).recordEvent(AnalyticsManager.Category.COUPON_BOX//
                            , AnalyticsManager.Action.COUPON_DOWNLOAD_CLICKED, "booking-" + coupon.title, paramsMap);

                        AnalyticsManager.getInstance(SelectGourmetCouponDialogActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
                            , AnalyticsManager.Action.GOURMET_COUPON_DOWNLOADED, coupon.title, null);
                        break;
                    }

                    case AnalyticsManager.Screen.DAILYGOURMET_DETAIL:
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

                        AnalyticsManager.getInstance(SelectGourmetCouponDialogActivity.this).recordEvent(AnalyticsManager.Category.COUPON_BOX//
                            , AnalyticsManager.Action.COUPON_DOWNLOAD_CLICKED, "detail-" + coupon.title, paramsMap);
                        break;
                    }
                }
            } catch (ParseException e)
            {
                if (Constants.DEBUG == false)
                {
                    Crashlytics.log("Select Coupon::coupon.validTo: " + (coupon != null ? coupon.validTo : ""));
                }
                ExLog.d(e.toString());
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
    };
}
