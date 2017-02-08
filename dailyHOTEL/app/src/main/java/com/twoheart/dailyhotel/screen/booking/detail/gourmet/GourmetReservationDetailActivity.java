package com.twoheart.dailyhotel.screen.booking.detail.gourmet;

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
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetBookingDetail;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.model.Review;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.activity.PlaceReservationDetailActivity;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.screen.common.ZoomMapActivity;
import com.twoheart.dailyhotel.screen.gourmet.detail.GourmetDetailActivity;
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

import retrofit2.Call;
import retrofit2.Response;

public class GourmetReservationDetailActivity extends PlaceReservationDetailActivity
{
    GourmetReservationDetailNetworkController mNetworkController;

    public static Intent newInstance(Context context, int reservationIndex, String imageUrl, boolean isDeepLink)
    {
        Intent intent = new Intent(context, GourmetReservationDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_BOOKINGIDX, reservationIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_URL, imageUrl);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DEEPLINK, isDeepLink);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // GourmetBookingDetail Class는 원래 처음부터 생성하면 안되는데
        // 내부에 시간값을 미리 받아서 진행되는 부분이 있어서 어쩔수 없이 미리 생성.
        mPlaceBookingDetail = new GourmetBookingDetail();
        mPlaceReservationDetailLayout = new GourmetReservationDetailLayout(this, mOnEventListener);
        mNetworkController = new GourmetReservationDetailNetworkController(this, mNetworkTag, mNetworkControllerListener);

        setContentView(mPlaceReservationDetailLayout.onCreateView(R.layout.activity_gourmet_reservation_detail));
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

        final String phone;

        if (Util.isTextEmpty(mPlaceBookingDetail.phone2) == false)
        {
            phone = mPlaceBookingDetail.phone2;
        } else if (Util.isTextEmpty(mPlaceBookingDetail.phone1) == false)
        {
            phone = mPlaceBookingDetail.phone1;
        } else if (Util.isTextEmpty(mPlaceBookingDetail.phone3) == false)
        {
            phone = mPlaceBookingDetail.phone3;
        } else
        {
            phone = null;
        }

        if (phone == null)
        {
            contactUs02Layout.setVisibility(View.GONE);
        } else
        {
            DailyTextView contactUs02TextView = (DailyTextView) contactUs02Layout.findViewById(R.id.contactUs02TextView);
            contactUs02TextView.setText(R.string.label_restaurant_direct_phone);
            contactUs02TextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.popup_ic_ops_02_restaurant_call, 0, 0, 0);

            contactUs02Layout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (dialog.isShowing() == true)
                    {
                        dialog.dismiss();
                    }

                    startGourmetCall(phone);
                }
            });
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

                startKakao();
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
                        AnalyticsManager.getInstance(GourmetReservationDetailActivity.this).recordEvent(//
                            AnalyticsManager.Category.CALL_BUTTON_CLICKED, AnalyticsManager.Action.BOOKING_DETAIL,//
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
                    GourmetBookingDetail gourmetBookingDetail = ((GourmetBookingDetail) mPlaceBookingDetail);

                    String message = getString(R.string.message_booking_gourmet_share_kakao, //
                        gourmetBookingDetail.userName, gourmetBookingDetail.placeName, gourmetBookingDetail.guestName,//
                        Util.getPriceFormat(GourmetReservationDetailActivity.this, gourmetBookingDetail.paymentPrice, false), //
                        DailyCalendar.convertDateFormatString(gourmetBookingDetail.reservationTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)"),//
                        DailyCalendar.convertDateFormatString(gourmetBookingDetail.reservationTime, DailyCalendar.ISO_8601_FORMAT, "HH:mm"), //
                        gourmetBookingDetail.ticketName, getString(R.string.label_booking_count, gourmetBookingDetail.ticketCount), //
                        gourmetBookingDetail.address);

                    KakaoLinkManager.newInstance(GourmetReservationDetailActivity.this).shareBookingGourmet(message, gourmetBookingDetail.placeIndex,//
                        mImageUrl, DailyCalendar.convertDateFormatString(gourmetBookingDetail.reservationTime, DailyCalendar.ISO_8601_FORMAT, "yyyyMMdd"));
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }

                AnalyticsManager.getInstance(GourmetReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.SHARE//
                    , AnalyticsManager.Action.GOURMET_BOOKING_SHARE, AnalyticsManager.ValueType.KAKAO, null);
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
                    GourmetBookingDetail gourmetBookingDetail = ((GourmetBookingDetail) mPlaceBookingDetail);

                    String message = getString(R.string.message_booking_gourmet_share_sms, //
                        gourmetBookingDetail.userName, gourmetBookingDetail.placeName, gourmetBookingDetail.guestName,//
                        Util.getPriceFormat(GourmetReservationDetailActivity.this, gourmetBookingDetail.paymentPrice, false), //
                        DailyCalendar.convertDateFormatString(gourmetBookingDetail.reservationTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)"),//
                        DailyCalendar.convertDateFormatString(gourmetBookingDetail.reservationTime, DailyCalendar.ISO_8601_FORMAT, "HH:mm"), //
                        gourmetBookingDetail.ticketName, getString(R.string.label_booking_count, gourmetBookingDetail.ticketCount), //
                        gourmetBookingDetail.address);

                    Util.sendSms(GourmetReservationDetailActivity.this, message);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }

                AnalyticsManager.getInstance(GourmetReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.SHARE//
                    , AnalyticsManager.Action.GOURMET_BOOKING_SHARE, AnalyticsManager.ValueType.MESSAGE, null);
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
            , AnalyticsManager.Action.BOOKING_SHARE, AnalyticsManager.Label.GOURMET, null);
    }

    @Override
    protected void requestPlaceReservationDetail(final int reservationIndex)
    {
        mNetworkController.requestGourmetReservationDetail(reservationIndex);
    }

    void startFAQ()
    {
        startActivityForResult(new Intent(this, FAQActivity.class), CODE_REQUEST_ACTIVITY_FAQ);
    }

    void startGourmetCall(final String phoneNumber)
    {
        View.OnClickListener positiveListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                releaseUiComponent();

                String noCallMessage = getString(R.string.toast_msg_no_gourmet_call, phoneNumber);

                if (Util.isTelephonyEnabled(GourmetReservationDetailActivity.this) == true)
                {
                    try
                    {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));

                        AnalyticsManager.getInstance(GourmetReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED,//
                            AnalyticsManager.Action.BOOKING_DETAIL, AnalyticsManager.Label.DIRECT_CALL, null);
                    } catch (ActivityNotFoundException e)
                    {
                        DailyToast.showToast(GourmetReservationDetailActivity.this, noCallMessage, Toast.LENGTH_LONG);
                    }
                } else
                {
                    DailyToast.showToast(GourmetReservationDetailActivity.this, noCallMessage, Toast.LENGTH_LONG);
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

        showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_direct_call_gourmet), //
            getString(R.string.dialog_btn_call), getString(R.string.dialog_btn_text_cancel) //
            , positiveListener, nativeListener, null, dismissListener, true);
    }

    void startKakao()
    {
        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED,//
            AnalyticsManager.Action.BOOKING_DETAIL, AnalyticsManager.Label.KAKAO, null);

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

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private GourmetReservationDetailLayout.OnEventListener mOnEventListener = new GourmetReservationDetailLayout.OnEventListener()
    {

        @Override
        public void finish()
        {
            GourmetReservationDetailActivity.this.onBackPressed();
        }

        @Override
        public void onIssuingReceiptClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = new Intent(GourmetReservationDetailActivity.this, GourmetReceiptActivity.class);
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
                Intent intent = ZoomMapActivity.newInstance(GourmetReservationDetailActivity.this//
                    , ZoomMapActivity.SourceType.GOURMET_BOOKING, mPlaceBookingDetail.placeName, mPlaceBookingDetail.address//
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

            Intent intent = GourmetDetailActivity.newInstance(GourmetReservationDetailActivity.this, saleTime, mPlaceBookingDetail.placeIndex, 0, false);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL);
        }

        @Override
        public void onViewMapClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Util.showShareMapDialog(GourmetReservationDetailActivity.this, mPlaceBookingDetail.placeName//
                , mPlaceBookingDetail.latitude, mPlaceBookingDetail.longitude, mPlaceBookingDetail.isOverseas//
                , AnalyticsManager.Category.GOURMET_BOOKINGS//
                , AnalyticsManager.Action.GOURMET_DETAIL_NAVIGATION_APP_CLICKED//
                , null);
        }

        @Override
        public void onRefundClick()
        {

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

            GourmetReservationDetailActivity.this.showCallDialog();
        }

        @Override
        public void showShareDialog()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            GourmetReservationDetailActivity.this.showShareDialog();
        }

        @Override
        public void onMyLocationClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = PermissionManagerActivity.newInstance(GourmetReservationDetailActivity.this, PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER);
        }

        @Override
        public void onClipAddressClick()
        {
            Util.clipText(GourmetReservationDetailActivity.this, mPlaceBookingDetail.address);

            DailyToast.showToast(GourmetReservationDetailActivity.this, R.string.message_detail_copy_address, Toast.LENGTH_SHORT);
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
            DailyToast.showToast(GourmetReservationDetailActivity.this, R.string.message_loading_map, Toast.LENGTH_SHORT);
        }
    };

    private GourmetReservationDetailNetworkController.OnNetworkControllerListener //
        mNetworkControllerListener = new GourmetReservationDetailNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onReviewInformation(Review review)
        {
            Intent intent = ReviewActivity.newInstance(GourmetReservationDetailActivity.this, review);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SATISFACTION_GOURMET);
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
                GourmetBookingDetail gourmetBookingDetail = (GourmetBookingDetail) mPlaceBookingDetail;
                gourmetBookingDetail.setData(jsonObject);
                mPlaceReservationDetailLayout.initLayout(gourmetBookingDetail);
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
            GourmetReservationDetailActivity.this.onErrorPopupMessage(msgCode, message, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Util.restartApp(GourmetReservationDetailActivity.this);
                }
            });
        }

        @Override
        public void onExpiredSessionError()
        {
            GourmetReservationDetailActivity.this.onExpiredSessionError();
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
            GourmetReservationDetailActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            GourmetReservationDetailActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            GourmetReservationDetailActivity.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            GourmetReservationDetailActivity.this.onErrorResponse(call, response);
        }
    };
}