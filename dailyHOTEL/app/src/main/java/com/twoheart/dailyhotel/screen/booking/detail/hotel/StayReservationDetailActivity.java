package com.twoheart.dailyhotel.screen.booking.detail.hotel;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.model.Review;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.StayBookingDetail;
import com.twoheart.dailyhotel.place.activity.PlaceReservationDetailActivity;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.screen.common.ZoomMapActivity;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailActivity;
import com.twoheart.dailyhotel.screen.information.FAQActivity;
import com.twoheart.dailyhotel.screen.review.ReviewActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyTextView;
import com.twoheart.dailyhotel.widget.DailyToast;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;

public class StayReservationDetailActivity extends PlaceReservationDetailActivity
{
    StayReservationDetailNetworkController mNetworkController;

    public static Intent newInstance(Context context, int reservationIndex, String imageUrl, boolean isDeepLink)
    {
        Intent intent = new Intent(context, StayReservationDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_BOOKINGIDX, reservationIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_URL, imageUrl);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DEEPLINK, isDeepLink);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // StayBookingDetail Class는 원래 처음부터 생성하면 안되는데
        // 내부에 시간값을 미리 받아서 진행되는 부분이 있어서 어쩔수 없이 미리 생성.
        mPlaceBookingDetail = new StayBookingDetail();
        mPlaceReservationDetailLayout = new StayReservationDetailLayout(this, mOnEventListener);
        mNetworkController = new StayReservationDetailNetworkController(this, mNetworkTag, mNetworkControllerListener);

        setContentView(mPlaceReservationDetailLayout.onCreateView(R.layout.activity_stay_reservation_detail));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case CODE_RESULT_ACTIVITY_STAY_AUTOREFUND:
            {
                switch (resultCode)
                {
                    case CODE_RESULT_ACTIVITY_REFRESH:
                    {
                        lockUI();

                        requestCommonDatetime();

                        setResult(CODE_RESULT_ACTIVITY_REFRESH);
                        break;
                    }

                    case RESULT_OK:
                    {
                        setResult(CODE_RESULT_ACTIVITY_REFRESH);
                        finish();
                        break;
                    }
                }
                break;
            }
        }
    }

    @Override
    protected void showCallDialog()
    {
        if (isFinishing())
        {
            return;
        }

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_dialog_contact_us_layout, null, false);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        // 버튼
        View contactUs01Layout = dialogView.findViewById(R.id.contactUs01Layout);
        View contactUs02Layout = dialogView.findViewById(R.id.contactUs02Layout);
        View contactUs03Layout = dialogView.findViewById(R.id.contactUs03Layout);

        DailyTextView contactUs01TextView = (DailyTextView) contactUs01Layout.findViewById(R.id.contactUs01TextView);
        contactUs01TextView.setText(R.string.frag_faqs);
        contactUs01TextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.popup_ic_ops_05_faq, 0, 0, 0);

        contactUs01Layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog.isShowing() == true)
                {
                    dialog.dismiss();
                }

                startFAQ();
            }
        });

        if (Util.isTextEmpty(mPlaceBookingDetail.phone1) == false)
        {
            DailyTextView contactUs02TextView = (DailyTextView) contactUs02Layout.findViewById(R.id.contactUs02TextView);
            contactUs02TextView.setText(R.string.label_hotel_front_phone);
            contactUs02TextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.popup_ic_ops_01_store_call, 0, 0, 0);

            contactUs02Layout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (dialog.isShowing() == true)
                    {
                        dialog.dismiss();
                    }

                    startFrontCall(mPlaceBookingDetail.phone1);
                }
            });
        } else
        {
            contactUs02Layout.setVisibility(View.GONE);
        }

        Calendar calendar = DailyCalendar.getInstance();
        calendar.setTimeInMillis(mPlaceBookingDetail.currentDateTime - DailyCalendar.NINE_HOUR_MILLISECOND);
        int time = calendar.get(Calendar.HOUR_OF_DAY) * 100 + calendar.get(Calendar.MINUTE);

        if (Util.isTextEmpty(mPlaceBookingDetail.phone2) == false && (time >= 900 && time <= 2000))
        {
            contactUs03Layout.setVisibility(View.VISIBLE);

            DailyTextView contactUs03TextView = (DailyTextView) contactUs03Layout.findViewById(R.id.contactUs03TextView);
            contactUs03TextView.setText(R.string.label_hotel_reservation_phone);
            contactUs03TextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.popup_ic_ops_01_store_call, 0, 0, 0);

            contactUs03Layout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (dialog.isShowing() == true)
                    {
                        dialog.dismiss();
                    }

                    startReservationCall(mPlaceBookingDetail.phone2);
                }
            });
        } else
        {
            contactUs03Layout.setVisibility(View.GONE);
        }

        View kakaoDailyView = dialogView.findViewById(R.id.kakaoDailyView);
        View callDailyView = dialogView.findViewById(R.id.callDailyView);

        kakaoDailyView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog.isShowing() == true)
                {
                    dialog.dismiss();
                }

                startKakao(false);
            }
        });

        callDailyView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog.isShowing() == true)
                {
                    dialog.dismiss();
                }

                showDailyCallDialog(new OnCallDialogListener()
                {
                    @Override
                    public void onShowDialog()
                    {

                    }

                    @Override
                    public void onPositiveButtonClick(View v)
                    {
                        AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordEvent(//
                            AnalyticsManager.Category.CALL_BUTTON_CLICKED, AnalyticsManager.Action.BOOKING_DETAIL, //
                            AnalyticsManager.Label.CUSTOMER_CENTER_CALL, null);
                    }

                    @Override
                    public void onNativeButtonClick(View v)
                    {

                    }
                });
            }
        });

        View closeView = dialogView.findViewById(R.id.closeView);
        closeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog.isShowing() == true)
                {
                    dialog.dismiss();
                }
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                unLockUI();
            }
        });

        try
        {
            dialog.setContentView(dialogView);

            WindowManager.LayoutParams layoutParams = Util.getDialogWidthLayoutParams(this, dialog);

            dialog.show();

            dialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    protected void showShareDialog()
    {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_sharedialog_layout, null, false);

        final Dialog shareDialog = new Dialog(this);
        shareDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        shareDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        shareDialog.setCanceledOnTouchOutside(true);

        if (Util.isTelephonyEnabled(this) == false)
        {
            View smsShareLayout = dialogView.findViewById(R.id.smsShareLayout);
            smsShareLayout.setVisibility(View.GONE);
        }

        // 버튼
        View kakaoShareView = dialogView.findViewById(R.id.kakaoShareView);

        kakaoShareView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (shareDialog.isShowing() == true)
                {
                    shareDialog.dismiss();
                }

                try
                {
                    StayBookingDetail stayBookingDetail = (StayBookingDetail) mPlaceBookingDetail;

                    String message = getString(R.string.message_booking_stay_share_kakao, //
                        stayBookingDetail.userName, stayBookingDetail.placeName, stayBookingDetail.guestName,//
                        Util.getPriceFormat(StayReservationDetailActivity.this, stayBookingDetail.paymentPrice, false), //
                        stayBookingDetail.roomName, DailyCalendar.convertDateFormatString(stayBookingDetail.checkInDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE) HH시"),//
                        DailyCalendar.convertDateFormatString(stayBookingDetail.checkOutDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE) HH시"), //
                        stayBookingDetail.address);

                    String[] checkInDates = stayBookingDetail.checkInDate.split("T");
                    String[] checkOutDates = stayBookingDetail.checkOutDate.split("T");

                    Date checkInDate = DailyCalendar.convertDate(checkInDates[0] + "T00:00:00+09:00", DailyCalendar.ISO_8601_FORMAT);
                    Date checkOutDate = DailyCalendar.convertDate(checkOutDates[0] + "T00:00:00+09:00", DailyCalendar.ISO_8601_FORMAT);

                    int nights = (int) ((getCompareDate(checkOutDate.getTime()) - getCompareDate(checkInDate.getTime())) / SaleTime.MILLISECOND_IN_A_DAY);

                    KakaoLinkManager.newInstance(StayReservationDetailActivity.this).shareBookingStay(message, stayBookingDetail.placeIndex,//
                        mImageUrl, DailyCalendar.convertDateFormatString(stayBookingDetail.checkInDate, DailyCalendar.ISO_8601_FORMAT, "yyyyMMdd"), nights);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }

                AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.SHARE//
                    , AnalyticsManager.Action.STAY_BOOKING_SHARE, AnalyticsManager.ValueType.KAKAO, null);
            }
        });

        View smsShareView = dialogView.findViewById(R.id.smsShareView);

        smsShareView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (shareDialog.isShowing() == true)
                {
                    shareDialog.dismiss();
                }

                try
                {
                    StayBookingDetail stayBookingDetail = (StayBookingDetail) mPlaceBookingDetail;

                    String message = getString(R.string.message_booking_stay_share_sms, //
                        stayBookingDetail.userName, stayBookingDetail.placeName, stayBookingDetail.guestName,//
                        Util.getPriceFormat(StayReservationDetailActivity.this, stayBookingDetail.paymentPrice, false), //
                        stayBookingDetail.roomName, DailyCalendar.convertDateFormatString(stayBookingDetail.checkInDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE) HH시"),//
                        DailyCalendar.convertDateFormatString(stayBookingDetail.checkOutDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE) HH시"), //
                        stayBookingDetail.address);

                    Util.sendSms(StayReservationDetailActivity.this, message);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }

                AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.SHARE//
                    , AnalyticsManager.Action.STAY_BOOKING_SHARE, AnalyticsManager.ValueType.MESSAGE, null);
            }
        });

        View closeTextView = dialogView.findViewById(R.id.closeTextView);
        closeTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (shareDialog.isShowing() == true)
                {
                    shareDialog.dismiss();
                }
            }
        });

        shareDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                unLockUI();
            }
        });

        try
        {
            shareDialog.setContentView(dialogView);

            WindowManager.LayoutParams layoutParams = Util.getDialogWidthLayoutParams(this, shareDialog);

            shareDialog.show();

            shareDialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.SHARE//
            , AnalyticsManager.Action.BOOKING_SHARE, AnalyticsManager.Label.STAY, null);
    }

    void showRefundCallDialog()
    {
        if (isFinishing())
        {
            return;
        }

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_dialog_contact_us_layout, null, false);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        // 버튼
        View contactUs01Layout = dialogView.findViewById(R.id.contactUs01Layout);
        View contactUs02Layout = dialogView.findViewById(R.id.contactUs02Layout);
        contactUs01Layout.setVisibility(View.GONE);
        contactUs02Layout.setVisibility(View.GONE);

        TextView kakaoDailyView = (TextView) dialogView.findViewById(R.id.kakaoDailyView);
        TextView callDailyView = (TextView) dialogView.findViewById(R.id.callDailyView);

        kakaoDailyView.setText(R.string.label_contact_refund_kakao);
        callDailyView.setText(R.string.label_contact_refund_daily);

        kakaoDailyView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog.isShowing() == true)
                {
                    dialog.dismiss();
                }

                startKakao(true);
            }
        });

        callDailyView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog.isShowing() == true)
                {
                    dialog.dismiss();
                }

                showDailyCallDialog(null);

                AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                    , AnalyticsManager.Action.REFUND_INQUIRY, AnalyticsManager.Label.CALL, null);
            }
        });

        View closeView = dialogView.findViewById(R.id.closeView);
        closeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog.isShowing() == true)
                {
                    dialog.dismiss();
                }
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                unLockUI();
            }
        });

        try
        {
            dialog.setContentView(dialogView);

            WindowManager.LayoutParams layoutParams = Util.getDialogWidthLayoutParams(this, dialog);

            dialog.show();

            dialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    protected void requestPlaceReservationDetail(int reservationIndex)
    {
        mNetworkController.requestStayReservationDetail(reservationIndex);
    }

    void startFAQ()
    {
        startActivityForResult(new Intent(this, FAQActivity.class), CODE_REQUEST_ACTIVITY_FAQ);
    }

    void startFrontCall(final String phoneNumber)
    {
        View.OnClickListener positiveListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                releaseUiComponent();

                String noCallMessage = getString(R.string.toast_msg_no_hotel_call, phoneNumber);

                if (Util.isTelephonyEnabled(StayReservationDetailActivity.this) == true)
                {
                    try
                    {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));

                        AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED,//
                            AnalyticsManager.Action.BOOKING_DETAIL, AnalyticsManager.Label.DIRECTCALL_FRONT, null);
                    } catch (ActivityNotFoundException e)
                    {
                        DailyToast.showToast(StayReservationDetailActivity.this, noCallMessage, Toast.LENGTH_LONG);
                    }
                } else
                {
                    DailyToast.showToast(StayReservationDetailActivity.this, noCallMessage, Toast.LENGTH_LONG);
                }
            }
        };

        View.OnClickListener nativeListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            }
        };

        DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                releaseUiComponent();
            }
        };

        showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_front_call_stay), //
            getString(R.string.dialog_btn_call), getString(R.string.dialog_btn_text_cancel) //
            , positiveListener, nativeListener, null, dismissListener, true);
    }

    void startReservationCall(final String phoneNumber)
    {
        View.OnClickListener positiveListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                releaseUiComponent();

                String noCallMessage = getString(R.string.toast_msg_no_hotel_call, phoneNumber);

                if (Util.isTelephonyEnabled(StayReservationDetailActivity.this) == true)
                {
                    try
                    {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));

                        AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED,//
                            AnalyticsManager.Action.BOOKING_DETAIL, AnalyticsManager.Label.DIRECTCALL_RESERVATION, null);
                    } catch (ActivityNotFoundException e)
                    {
                        DailyToast.showToast(StayReservationDetailActivity.this, noCallMessage, Toast.LENGTH_LONG);
                    }
                } else
                {
                    DailyToast.showToast(StayReservationDetailActivity.this, noCallMessage, Toast.LENGTH_LONG);
                }
            }
        };

        View.OnClickListener nativeListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            }
        };

        DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                releaseUiComponent();
            }
        };

        showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_reservation_call_stay), //
            getString(R.string.dialog_btn_call), getString(R.string.dialog_btn_text_cancel) //
            , positiveListener, nativeListener, null, dismissListener, true);
    }

    void startKakao(boolean isRefund)
    {
        if (isRefund == true)
        {
            AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                , AnalyticsManager.Action.REFUND_INQUIRY, AnalyticsManager.Label.KAKAO, null);
        } else
        {
            AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED,//
                AnalyticsManager.Action.BOOKING_DETAIL, AnalyticsManager.Label.KAKAO, null);
        }

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

    String getRefundPolicyStatus(StayBookingDetail bookingDetail)
    {
        // 환불 대기 상태
        if (bookingDetail.readyForRefund == true)
        {
            return StayBookingDetail.STATUS_WAIT_REFUND;
        } else
        {
            if (Util.isTextEmpty(bookingDetail.refundPolicy) == false)
            {
                return bookingDetail.refundPolicy;
            } else
            {
                return StayBookingDetail.STATUS_SURCHARGE_REFUND;
            }
        }
    }

    long getCompareDate(long timeInMillis)
    {
        Calendar calendar = DailyCalendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(timeInMillis);

        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private StayReservationDetailLayout.OnEventListener mOnEventListener = new StayReservationDetailLayout.OnEventListener()
    {
        @Override
        public void finish()
        {
            StayReservationDetailActivity.this.onBackPressed();
        }

        @Override
        public void onIssuingReceiptClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = new Intent(StayReservationDetailActivity.this, IssuingReceiptActivity.class);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_BOOKINGIDX, mReservationIndex);
            startActivity(intent);
        }

        @Override
        public void onMapClick(boolean isGoogleMap)
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            if (isGoogleMap == false)
            {
                Intent intent = ZoomMapActivity.newInstance(StayReservationDetailActivity.this//
                    , ZoomMapActivity.SourceType.HOTEL_BOOKING, mPlaceBookingDetail.placeName, mPlaceBookingDetail.address//
                    , mPlaceBookingDetail.latitude, mPlaceBookingDetail.longitude, mPlaceBookingDetail.isOverseas);

                startActivity(intent);
            } else
            {
                mPlaceReservationDetailLayout.expandMap();
            }
        }

        @Override
        public void onViewDetailClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            SaleTime saleTime = new SaleTime();
            saleTime.setCurrentTime(mPlaceBookingDetail.currentDateTime);
            saleTime.setDailyTime(mPlaceBookingDetail.dailyDateTime);

            Intent intent = StayDetailActivity.newInstance(StayReservationDetailActivity.this, saleTime, 1, mPlaceBookingDetail.placeIndex, 0, false, false);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL);
        }

        @Override
        public void onViewMapClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Util.showShareMapDialog(StayReservationDetailActivity.this, mPlaceBookingDetail.placeName//
                , mPlaceBookingDetail.latitude, mPlaceBookingDetail.longitude, mPlaceBookingDetail.isOverseas//
                , AnalyticsManager.Category.HOTEL_BOOKINGS//
                , AnalyticsManager.Action.HOTEL_DETAIL_NAVIGATION_APP_CLICKED//
                , null);
        }

        @Override
        public void onRefundClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            StayBookingDetail stayBookingDetail = (StayBookingDetail) mPlaceBookingDetail;

            switch (getRefundPolicyStatus(stayBookingDetail))
            {
                case StayBookingDetail.STATUS_NO_CHARGE_REFUND:
                {
                    Intent intent = StayAutoRefundActivity.newInstance(StayReservationDetailActivity.this, stayBookingDetail);
                    startActivityForResult(intent, CODE_RESULT_ACTIVITY_STAY_AUTOREFUND);

                    AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                        , AnalyticsManager.Action.FREE_CANCELLATION_CLICKED, null, null);
                    break;
                }

                default:
                    StayReservationDetailActivity.this.showRefundCallDialog();

                    AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                        , AnalyticsManager.Action.REFUND_INQUIRY_CLICKED, null, null);
                    break;
            }
        }

        @Override
        public void onReviewClick(String reviewStatus)
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            if (PlaceBookingDetail.ReviewStatusType.ADDABLE.equalsIgnoreCase(reviewStatus) == true)
            {
                lockUI();
                mNetworkController.requestReviewInformation(mReservationIndex);
            }
        }

        @Override
        public void showCallDialog()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            StayReservationDetailActivity.this.showCallDialog();
        }

        @Override
        public void showShareDialog()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            StayReservationDetailActivity.this.showShareDialog();
        }

        @Override
        public void onMyLocationClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = PermissionManagerActivity.newInstance(StayReservationDetailActivity.this, PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER);
        }

        @Override
        public void onClipAddressClick()
        {
            Util.clipText(StayReservationDetailActivity.this, mPlaceBookingDetail.address);

            DailyToast.showToast(StayReservationDetailActivity.this, R.string.message_detail_copy_address, Toast.LENGTH_SHORT);
        }

        @Override
        public void onSearchMapClick()
        {
            onViewMapClick();
        }

        @Override
        public void onReleaseUiComponent()
        {
            releaseUiComponent();
        }

        @Override
        public void onLoadingMap()
        {
            DailyToast.showToast(StayReservationDetailActivity.this, R.string.message_loading_map, Toast.LENGTH_SHORT);
        }
    };

    private StayReservationDetailNetworkController.OnNetworkControllerListener //
        mNetworkControllerListener = new StayReservationDetailNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onReviewInformation(Review review)
        {
            Intent intent = ReviewActivity.newInstance(StayReservationDetailActivity.this, review);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SATISFACTION_HOTEL);
        }

        @Override
        public void onPolicyRefund(boolean isSuccess, String comment, String refundPolicy, boolean refundManual, String message)
        {
            StayBookingDetail stayBookingDetail = (StayBookingDetail) mPlaceBookingDetail;

            if (isSuccess == true)
            {
                // 환불 킬스위치 ON
                if (refundManual == true)
                {
                    if (StayBookingDetail.STATUS_NRD.equalsIgnoreCase(refundPolicy) == true)
                    {
                        stayBookingDetail.refundPolicy = refundPolicy;
                        stayBookingDetail.mRefundComment = comment;
                    } else
                    {
                        stayBookingDetail.refundPolicy = StayBookingDetail.STATUS_SURCHARGE_REFUND;
                        stayBookingDetail.mRefundComment = message;
                    }

                    mPlaceReservationDetailLayout.initLayout(stayBookingDetail);
                } else
                {
                    if (StayBookingDetail.STATUS_NONE.equalsIgnoreCase(refundPolicy) == true)
                    {
                        stayBookingDetail.isVisibleRefundPolicy = false;
                    } else
                    {
                        stayBookingDetail.mRefundComment = comment;
                    }

                    stayBookingDetail.refundPolicy = refundPolicy;
                    mPlaceReservationDetailLayout.initLayout(stayBookingDetail);
                }

                // Analytics
                if (Util.isTextEmpty(refundPolicy) == false)
                {
                    switch (refundPolicy)
                    {
                        case StayBookingDetail.STATUS_NO_CHARGE_REFUND:
                            AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordScreen(StayReservationDetailActivity.this, AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_CANCELABLE, null);
                            break;

                        case StayBookingDetail.STATUS_SURCHARGE_REFUND:
                            AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordScreen(StayReservationDetailActivity.this, AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_CANCELLATIONFEE, null);
                            break;

                        default:
                            AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordScreen(StayReservationDetailActivity.this, AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_NOREFUNDS, null);
                            break;
                    }
                } else
                {
                    AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordScreen(StayReservationDetailActivity.this, AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_NOREFUNDS, null);
                }
            } else
            {
                stayBookingDetail.isVisibleRefundPolicy = false;

                mPlaceReservationDetailLayout.initLayout(stayBookingDetail);

                AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordScreen(StayReservationDetailActivity.this, AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_NOREFUNDS, null);
            }

            unLockUI();
        }

        @Override
        public void onReservationDetail(JSONObject jsonObject)
        {
            unLockUI();

            if (jsonObject == null)
            {
                finish();
                return;
            }

            try
            {
                StayBookingDetail stayBookingDetail = (StayBookingDetail) mPlaceBookingDetail;

                stayBookingDetail.setData(jsonObject);

                long checkOutDateTime = DailyCalendar.getTimeGMT9(stayBookingDetail.checkOutDate, DailyCalendar.ISO_8601_FORMAT);

                if (stayBookingDetail.currentDateTime < checkOutDateTime)
                {
                    stayBookingDetail.isVisibleRefundPolicy = true;

                    if (stayBookingDetail.readyForRefund == true)
                    {
                        // 환불 대기 인 상태에서는 문구가 고정이다.
                        mPlaceReservationDetailLayout.initLayout(stayBookingDetail);
                    } else
                    {
                        mNetworkController.requestPolicyRefund(stayBookingDetail.reservationIndex, stayBookingDetail.transactionType);
                    }
                } else
                {
                    stayBookingDetail.isVisibleRefundPolicy = false;

                    mPlaceReservationDetailLayout.initLayout(stayBookingDetail);
                }
            } catch (Exception e)
            {
                if (Constants.DEBUG == false)
                {
                    Crashlytics.logException(e);
                }

                finish();
                return;
            }
        }

        @Override
        public void onEnterOtherUserReservationDetailError(int msgCode, String message)
        {
            StayReservationDetailActivity.this.onErrorPopupMessage(msgCode, message, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Util.restartApp(StayReservationDetailActivity.this);
                }
            });
        }

        @Override
        public void onExpiredSessionError()
        {
            StayReservationDetailActivity.this.onExpiredSessionError();
        }

        @Override
        public void onReservationDetailError(Throwable throwable)
        {
            onError(throwable);
            finish();
        }

        @Override
        public void onError(Throwable e)
        {
            StayReservationDetailActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            StayReservationDetailActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            StayReservationDetailActivity.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            StayReservationDetailActivity.this.onErrorResponse(call, response);
        }
    };
}
