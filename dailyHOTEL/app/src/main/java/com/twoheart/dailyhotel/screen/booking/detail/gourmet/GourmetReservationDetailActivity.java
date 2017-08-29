package com.twoheart.dailyhotel.screen.booking.detail.gourmet;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.entity.Booking;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetBookingDetail;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.model.Review;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.place.activity.PlaceReservationDetailActivity;
import com.twoheart.dailyhotel.screen.common.HappyTalkCategoryDialog;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.screen.common.ZoomMapActivity;
import com.twoheart.dailyhotel.screen.gourmet.detail.GourmetDetailActivity;
import com.twoheart.dailyhotel.screen.information.FAQActivity;
import com.twoheart.dailyhotel.screen.review.ReviewActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Response;

public class GourmetReservationDetailActivity extends PlaceReservationDetailActivity
{
    GourmetReservationDetailNetworkController mNetworkController;

    public static Intent newInstance(Context context, int reservationIndex, String imageUrl, boolean isDeepLink, int bookingState)
    {
        Intent intent = new Intent(context, GourmetReservationDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_BOOKINGIDX, reservationIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_URL, imageUrl);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DEEPLINK, isDeepLink);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_BOOKING_STATE, bookingState);

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

                AnalyticsManager.getInstance(GourmetReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                    , AnalyticsManager.Action.FNQ_CLICK, AnalyticsManager.Label.GOURMET_BOOKING_DETAIL, null);
            }
        });

        final String phone;

        if (DailyTextUtils.isTextEmpty(mPlaceBookingDetail.phone2) == false)
        {
            phone = mPlaceBookingDetail.phone2;
        } else if (DailyTextUtils.isTextEmpty(mPlaceBookingDetail.phone1) == false)
        {
            phone = mPlaceBookingDetail.phone1;
        } else if (DailyTextUtils.isTextEmpty(mPlaceBookingDetail.phone3) == false)
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

                AnalyticsManager.getInstance(GourmetReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                    , AnalyticsManager.Action.HAPPYTALK_CLICK, AnalyticsManager.Label.GOURMET_BOOKING_DETAIL, null);
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

                    @Override
                    public void onDismissDialog()
                    {
                        // do nothing!
                    }
                });

                AnalyticsManager.getInstance(GourmetReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                    , AnalyticsManager.Action.CALL_CLICK, AnalyticsManager.Label.GOURMET_BOOKING_DETAIL, null);
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

            WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(this, dialog);

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
                    // 카카오톡 패키지 설치 여부
                    getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

                    GourmetBookingDetail gourmetBookingDetail = ((GourmetBookingDetail) mPlaceBookingDetail);

                    String message = getString(R.string.message_booking_gourmet_share_kakao, //
                        gourmetBookingDetail.userName, gourmetBookingDetail.placeName, gourmetBookingDetail.guestName,//
                        DailyTextUtils.getPriceFormat(GourmetReservationDetailActivity.this, gourmetBookingDetail.paymentPrice, false), //
                        DailyCalendar.convertDateFormatString(gourmetBookingDetail.reservationTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)"),//
                        DailyCalendar.convertDateFormatString(gourmetBookingDetail.reservationTime, DailyCalendar.ISO_8601_FORMAT, "HH:mm"), //
                        gourmetBookingDetail.ticketName, getString(R.string.label_booking_count, gourmetBookingDetail.ticketCount), //
                        gourmetBookingDetail.address);

                    KakaoLinkManager.newInstance(GourmetReservationDetailActivity.this).shareBookingGourmet(message, gourmetBookingDetail.placeIndex,//
                        mImageUrl, DailyCalendar.convertDateFormatString(gourmetBookingDetail.reservationTime, DailyCalendar.ISO_8601_FORMAT, "yyyyMMdd"));

                    AnalyticsManager.getInstance(GourmetReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.SHARE//
                        , AnalyticsManager.Action.GOURMET_BOOKING_SHARE, AnalyticsManager.ValueType.KAKAO, null);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());

                    showSimpleDialog(null, getString(R.string.dialog_msg_not_installed_kakaotalk)//
                        , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no)//
                        , new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Util.installPackage(GourmetReservationDetailActivity.this, "com.kakao.talk");
                            }
                        }, null);
                }
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

                lockUI();

                try
                {
                    GourmetBookingDetail gourmetBookingDetail = ((GourmetBookingDetail) mPlaceBookingDetail);

                    String longUrl = String.format(Locale.KOREA, "https://mobile.dailyhotel.co.kr/gourmet/%d?reserveDate=%s"//
                        , gourmetBookingDetail.placeIndex, DailyCalendar.convertDateFormatString(gourmetBookingDetail.reservationTime, DailyCalendar.ISO_8601_FORMAT, "yyyy-MM-dd"));

                    final String message = getString(R.string.message_booking_gourmet_share_sms, //
                        gourmetBookingDetail.userName, gourmetBookingDetail.placeName, gourmetBookingDetail.guestName,//
                        DailyTextUtils.getPriceFormat(GourmetReservationDetailActivity.this, gourmetBookingDetail.paymentPrice, false), //
                        DailyCalendar.convertDateFormatString(gourmetBookingDetail.reservationTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)"),//
                        DailyCalendar.convertDateFormatString(gourmetBookingDetail.reservationTime, DailyCalendar.ISO_8601_FORMAT, "HH:mm"), //
                        gourmetBookingDetail.ticketName, getString(R.string.label_booking_count, gourmetBookingDetail.ticketCount), //
                        gourmetBookingDetail.address);

                    CommonRemoteImpl commonRemote = new CommonRemoteImpl(GourmetReservationDetailActivity.this);

                    addCompositeDisposable(commonRemote.getShortUrl(longUrl).subscribe(new Consumer<String>()
                    {
                        @Override
                        public void accept(@NonNull String shortUrl) throws Exception
                        {
                            unLockUI();

                            Util.sendSms(GourmetReservationDetailActivity.this, message + shortUrl);
                        }
                    }, new Consumer<Throwable>()
                    {
                        @Override
                        public void accept(@NonNull Throwable throwable) throws Exception
                        {
                            unLockUI();

                            Util.sendSms(GourmetReservationDetailActivity.this, message + "https://mobile.dailyhotel.co.kr/gourmet/" + gourmetBookingDetail.placeIndex);
                        }
                    }));
                } catch (Exception e)
                {
                    unLockUI();

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

            WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(this, shareDialog);

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
        try
        {
            // 카카오톡 패키지 설치 여부
            getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

            AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED,//
                AnalyticsManager.Action.BOOKING_DETAIL, AnalyticsManager.Label.KAKAO, null);

            startActivityForResult(HappyTalkCategoryDialog.newInstance(this//
                , HappyTalkCategoryDialog.CallScreen.SCREEN_GOURMET_BOOKING//
                , mPlaceBookingDetail.placeIndex, mReservationIndex, mPlaceBookingDetail.placeName), Constants.CODE_REQUEST_ACTIVITY_HAPPY_TALK);
        } catch (Exception e)
        {
            showSimpleDialog(null, getString(R.string.dialog_msg_not_installed_kakaotalk)//
                , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no)//
                , new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Util.installPackage(GourmetReservationDetailActivity.this, "com.kakao.talk");
                    }
                }, null);
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
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_RECEIPT);
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

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_ZOOMMAP);
            } else
            {
                mPlaceReservationDetailLayout.expandMap(mPlaceBookingDetail.latitude, mPlaceBookingDetail.longitude);
            }

            AnalyticsManager.getInstance(GourmetReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                , AnalyticsManager.Action.MAP_CLICK, AnalyticsManager.ValueType.EMPTY, null);
        }

        @Override
        public void onViewDetailClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            try
            {
                GourmetBookingDay gourmetBookingDay = new GourmetBookingDay();
                gourmetBookingDay.setVisitDay(mTodayDateTime.dailyDateTime);

                Intent intent = GourmetDetailActivity.newInstance(GourmetReservationDetailActivity.this, gourmetBookingDay, mPlaceBookingDetail.placeIndex, false, false, false);
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_DETAIL);

                overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

                AnalyticsManager.getInstance(GourmetReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                    , AnalyticsManager.Action.BOOKING_ITEM_DETAIL_CLICK, AnalyticsManager.ValueType.EMPTY, null);
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
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

                AnalyticsManager.getInstance(GourmetReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                    , AnalyticsManager.Action.WRITE_REVIEW, AnalyticsManager.ValueType.EMPTY, null);
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

            AnalyticsManager.getInstance(GourmetReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.CONTACT_DAILY_CONCIERGE, AnalyticsManager.Label.GOURMET_BOOKING_DETAIL, null);
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
            DailyTextUtils.clipText(GourmetReservationDetailActivity.this, mPlaceBookingDetail.address);

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

        @Override
        public void onDeleteReservationClick()
        {
            if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_delete_booking)//
                , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no)//
                , new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        lockUI();

                        mNetworkController.requestHiddenReservation(mPlaceBookingDetail.reservationIndex);
                    }
                }, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        unLockUI();
                    }
                }, new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        unLockUI();
                    }
                }, null, true);
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

            if (jsonObject == null || isFinishing() == true)
            {
                finish();
                return;
            }

            try
            {
                GourmetBookingDetail gourmetBookingDetail = (GourmetBookingDetail) mPlaceBookingDetail;
                gourmetBookingDetail.setData(jsonObject);
                mPlaceReservationDetailLayout.initLayout(mTodayDateTime, gourmetBookingDetail);

                HashMap<String, String> params = new HashMap();
                params.put(AnalyticsManager.KeyType.PLACE_TYPE, "gourmet");
                params.put(AnalyticsManager.KeyType.COUNTRY, "domestic");
                params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(gourmetBookingDetail.placeIndex));

                switch (mBookingState)
                {
                    case Booking.BOOKING_STATE_WAITING_REFUND:
                        AnalyticsManager.getInstance(GourmetReservationDetailActivity.this).recordScreen(GourmetReservationDetailActivity.this//
                            , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_CANCELLATION_PROGRESS, null, params);
                        break;

                    case Booking.BOOKING_STATE_BEFORE_USE:
                        AnalyticsManager.getInstance(GourmetReservationDetailActivity.this).recordScreen(GourmetReservationDetailActivity.this//
                            , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO, null, params);
                        break;

                    case Booking.BOOKING_STATE_AFTER_USE:
                        AnalyticsManager.getInstance(GourmetReservationDetailActivity.this).recordScreen(GourmetReservationDetailActivity.this//
                            , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_POST_VISIT, null, params);
                        break;

                    default:
                        AnalyticsManager.getInstance(GourmetReservationDetailActivity.this).recordScreen(GourmetReservationDetailActivity.this//
                            , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO, null, params);
                        break;
                }

                mPlaceReservationDetailLayout.setDeleteReservationVisible(mBookingState == Booking.BOOKING_STATE_AFTER_USE);
            } catch (Exception e)
            {
                Crashlytics.logException(e);
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
                    GourmetReservationDetailActivity.this.onBackPressed();
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
        public void onHiddenReservation(boolean success, String message)
        {
            unLockUI();

            if (success == true)
            {
                showSimpleDialog(getString(R.string.dialog_notice2), message, getString(R.string.dialog_btn_text_confirm), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        finish();
                    }
                });

                AnalyticsManager.getInstance(GourmetReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                    , AnalyticsManager.Action.BOOKING_HISTORY_DELETE, AnalyticsManager.ValueType.EMPTY, null);
            } else
            {
                showSimpleDialog(getString(R.string.dialog_notice2), message, getString(R.string.dialog_btn_text_confirm), null);
            }
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            GourmetReservationDetailActivity.this.onError(call, e, onlyReport);
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
