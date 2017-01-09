package com.twoheart.dailyhotel.screen.booking.detail.hotel;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.model.StayBookingDetail;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.activity.PlaceBookingDetailTabActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.screen.booking.detail.BookingDetailFragmentPagerAdapter;
import com.twoheart.dailyhotel.screen.information.FAQActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyTextView;
import com.twoheart.dailyhotel.widget.DailyToast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Response;

public class StayBookingDetailTabActivity extends PlaceBookingDetailTabActivity
{
    public StayBookingDetail mStayBookingDetail;
    public StayBookingDetailTabBookingFragment mStayBookingDetailTabBookingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mStayBookingDetail = new StayBookingDetail();
    }

    @Override
    protected void loadFragments(ViewPager viewPager, PlaceBookingDetail placeBookingDetail)
    {
        String tag = (String) viewPager.getTag();

        if (tag != null)
        {
            return;
        }

        if (mStayBookingDetailTabBookingFragment != null)
        {
            mStayBookingDetailTabBookingFragment.updateRefundPolicyLayout((StayBookingDetail) placeBookingDetail);
            return;
        }

        ArrayList<BaseFragment> fragmentList = new ArrayList<>();

        mStayBookingDetailTabBookingFragment = StayBookingDetailTabBookingFragment.newInstance(placeBookingDetail, mReservationIndex);
        fragmentList.add(mStayBookingDetailTabBookingFragment);

        BookingDetailFragmentPagerAdapter fragmentPagerAdapter = new BookingDetailFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(fragmentPagerAdapter);
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

        if (Util.isTextEmpty(mStayBookingDetail.phone1) == false)
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

                    startFrontCall(mStayBookingDetail.phone1);
                }
            });
        } else
        {
            contactUs02Layout.setVisibility(View.GONE);
        }

        Calendar calendar = DailyCalendar.getInstance();
        calendar.setTimeInMillis(mStayBookingDetail.currentDateTime - DailyCalendar.NINE_HOUR_MILLISECOND);
        int time = calendar.get(Calendar.HOUR_OF_DAY) * 100 + calendar.get(Calendar.MINUTE);

        if (Util.isTextEmpty(mStayBookingDetail.phone2) == false && (time >= 900 && time <= 2000))
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

                    startReservationCall(mStayBookingDetail.phone2);
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
                        AnalyticsManager.getInstance(StayBookingDetailTabActivity.this).recordEvent(//
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
        View dialogView = layoutInflater.inflate(R.layout.view_sharedialog_booking_layout, null, false);

        final Dialog shareDialog = new Dialog(this);
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

                //                if (mDefaultImageUrl == null)
                //                {
                //                    if (mPlaceDetail.getImageInformationList() != null && mPlaceDetail.getImageInformationList().size() > 0)
                //                    {
                //                        mDefaultImageUrl = mPlaceDetail.getImageInformationList().get(0).url;
                //                    }
                //                }
                //
                //                shareKakao(mPlaceDetail, mDefaultImageUrl);
            }
        });

        View smsShareLayout = dialogView.findViewById(R.id.smsShareLayout);

        smsShareLayout.setOnClickListener(new View.OnClickListener()
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
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.putExtra("sms_body", getString(R.string.message_booking_stay_share_sms, //
                        mStayBookingDetail.userName, mStayBookingDetail.placeName, mStayBookingDetail.guestName,//
                        mStayBookingDetail.roomName, DailyCalendar.convertDateFormatString(mStayBookingDetail.checkInDate, DailyCalendar.ISO_8601_FORMAT, "MM/dd (EEE) HH시"),//
                        DailyCalendar.convertDateFormatString(mStayBookingDetail.checkOutDate, DailyCalendar.ISO_8601_FORMAT, "MM/dd (EEE) HH시"), //
                        mStayBookingDetail.address));

                    intent.setType("vnd.android-dir/mms-sms");
                    startActivity(intent);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
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
    }

    @Override
    protected void requestPlaceBookingDetail(final int reservationIndex)
    {
        lockUI();

        DailyMobileAPI.getInstance(this).requestUserProfile(mNetworkTag, new retrofit2.Callback<JSONObject>()
        {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
            {
                if (response != null && response.isSuccessful() && response.body() != null)
                {
                    try
                    {
                        JSONObject responseJSONObject = response.body();

                        int msgCode = responseJSONObject.getInt("msgCode");

                        if (msgCode == 100)
                        {
                            JSONObject jsonObject = responseJSONObject.getJSONObject("data");

                            mStayBookingDetail.userName = jsonObject.getString("name");

                            DailyMobileAPI.getInstance(StayBookingDetailTabActivity.this).requestStayBookingDetailInformation(mNetworkTag, reservationIndex, mReservationBookingDetailCallback);
                        } else
                        {
                            String msg = responseJSONObject.getString("msg");
                            DailyToast.showToast(StayBookingDetailTabActivity.this, msg, Toast.LENGTH_SHORT);
                            finish();
                        }
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                } else
                {
                    StayBookingDetailTabActivity.this.onErrorResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                StayBookingDetailTabActivity.this.onError(t);
                finish();
            }
        });
    }

    @Override
    protected void setCurrentDateTime(long currentDateTime, long dailyDateTime)
    {
        mStayBookingDetail.currentDateTime = currentDateTime;
        mStayBookingDetail.dailyDateTime = dailyDateTime;
    }

    private void startFAQ()
    {
        startActivityForResult(new Intent(this, FAQActivity.class), CODE_REQUEST_ACTIVITY_FAQ);
    }

    private void startFrontCall(final String phoneNumber)
    {
        View.OnClickListener positiveListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                releaseUiComponent();

                String noCallMessage = getString(R.string.toast_msg_no_hotel_call, phoneNumber);

                if (Util.isTelephonyEnabled(StayBookingDetailTabActivity.this) == true)
                {
                    try
                    {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));

                        AnalyticsManager.getInstance(StayBookingDetailTabActivity.this).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED,//
                            AnalyticsManager.Action.BOOKING_DETAIL, AnalyticsManager.Label.DIRECTCALL_FRONT, null);
                    } catch (ActivityNotFoundException e)
                    {
                        DailyToast.showToast(StayBookingDetailTabActivity.this, noCallMessage, Toast.LENGTH_LONG);
                    }
                } else
                {
                    DailyToast.showToast(StayBookingDetailTabActivity.this, noCallMessage, Toast.LENGTH_LONG);
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

    private void startReservationCall(final String phoneNumber)
    {
        View.OnClickListener positiveListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                releaseUiComponent();

                String noCallMessage = getString(R.string.toast_msg_no_hotel_call, phoneNumber);

                if (Util.isTelephonyEnabled(StayBookingDetailTabActivity.this) == true)
                {
                    try
                    {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));

                        AnalyticsManager.getInstance(StayBookingDetailTabActivity.this).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED,//
                            AnalyticsManager.Action.BOOKING_DETAIL, AnalyticsManager.Label.DIRECTCALL_RESERVATION, null);
                    } catch (ActivityNotFoundException e)
                    {
                        DailyToast.showToast(StayBookingDetailTabActivity.this, noCallMessage, Toast.LENGTH_LONG);
                    }
                } else
                {
                    DailyToast.showToast(StayBookingDetailTabActivity.this, noCallMessage, Toast.LENGTH_LONG);
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

    private void startKakao()
    {
        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED,//
            AnalyticsManager.Action.BOOKING_DETAIL, AnalyticsManager.Label.KAKAO, null);

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

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private retrofit2.Callback mReservationBookingDetailCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");

                    switch (msgCode)
                    {
                        case 100:
                            JSONObject jsonObject = responseJSONObject.getJSONObject("data");

                            mStayBookingDetail.setData(jsonObject);

                            long checkOutDateTime = DailyCalendar.getTimeGMT9(mStayBookingDetail.checkOutDate, DailyCalendar.ISO_8601_FORMAT);

                            if (mStayBookingDetail.currentDateTime < checkOutDateTime)
                            {
                                mStayBookingDetail.isVisibleRefundPolicy = true;

                                if (mStayBookingDetail.readyForRefund == true)
                                {
                                    // 환불 대기 인 상태에서는 문구가 고정이다.
                                    loadFragments(getViewPager(), mStayBookingDetail);
                                } else
                                {
                                    DailyMobileAPI.getInstance(StayBookingDetailTabActivity.this).requestPolicyRefund(mNetworkTag//
                                        , mStayBookingDetail.reservationIndex, mStayBookingDetail.transactionType, mPolicyRefundCallback);
                                }
                            } else
                            {
                                mStayBookingDetail.isVisibleRefundPolicy = false;

                                loadFragments(getViewPager(), mStayBookingDetail);
                            }
                            break;

                        // 예약 내역 진입시에 다른 사용자가 딥링크로 진입시 예외 처리 추가
                        case 501:
                            onErrorPopupMessage(msgCode, responseJSONObject.getString("msg"), new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    Util.restartApp(StayBookingDetailTabActivity.this);
                                }
                            });
                            break;

                        default:
                            onErrorPopupMessage(msgCode, responseJSONObject.getString("msg"));
                            break;
                    }
                } catch (Exception e)
                {
                    onError(e);
                } finally
                {
                    unLockUI();
                }
            } else
            {
                StayBookingDetailTabActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            StayBookingDetailTabActivity.this.onError(t);
            finish();
        }
    };

    private retrofit2.Callback mPolicyRefundCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (isFinishing() == true)
            {
                return;
            }

            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");

                    switch (msgCode)
                    {
                        case 100:
                        case 1015:
                        {
                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                            String comment = dataJSONObject.getString("comment");
                            String refundPolicy = dataJSONObject.getString("refundPolicy");
                            boolean refundManual = dataJSONObject.getBoolean("refundManual");

                            // 환불 킬스위치 ON
                            if (refundManual == true)
                            {
                                if (StayBookingDetail.STATUS_NRD.equalsIgnoreCase(refundPolicy) == true)
                                {
                                    mStayBookingDetail.refundPolicy = refundPolicy;
                                    mStayBookingDetail.mRefundComment = comment;
                                } else
                                {
                                    mStayBookingDetail.refundPolicy = StayBookingDetail.STATUS_SURCHARGE_REFUND;
                                    mStayBookingDetail.mRefundComment = responseJSONObject.getString("msg");
                                }

                                loadFragments(getViewPager(), mStayBookingDetail);
                            } else
                            {
                                if (StayBookingDetail.STATUS_NONE.equalsIgnoreCase(refundPolicy) == true)
                                {
                                    mStayBookingDetail.isVisibleRefundPolicy = false;
                                } else
                                {
                                    mStayBookingDetail.mRefundComment = comment;
                                }

                                mStayBookingDetail.refundPolicy = refundPolicy;
                                loadFragments(getViewPager(), mStayBookingDetail);
                            }

                            // Analytics
                            if (Util.isTextEmpty(refundPolicy) == false)
                            {
                                switch (refundPolicy)
                                {
                                    case StayBookingDetail.STATUS_NO_CHARGE_REFUND:
                                        AnalyticsManager.getInstance(StayBookingDetailTabActivity.this).recordScreen(AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_CANCELABLE);
                                        break;

                                    case StayBookingDetail.STATUS_SURCHARGE_REFUND:
                                        AnalyticsManager.getInstance(StayBookingDetailTabActivity.this).recordScreen(AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_CANCELLATIONFEE);
                                        break;

                                    default:
                                        AnalyticsManager.getInstance(StayBookingDetailTabActivity.this).recordScreen(AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_NOREFUNDS);
                                        break;
                                }
                            } else
                            {
                                AnalyticsManager.getInstance(StayBookingDetailTabActivity.this).recordScreen(AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_NOREFUNDS);
                            }
                            break;
                        }

                        default:
                            mStayBookingDetail.isVisibleRefundPolicy = false;

                            loadFragments(getViewPager(), mStayBookingDetail);

                            AnalyticsManager.getInstance(StayBookingDetailTabActivity.this).recordScreen(AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_NOREFUNDS);
                            break;
                    }
                } catch (Exception e)
                {
                    if (DEBUG == false)
                    {
                        Crashlytics.logException(e);
                    }

                    mStayBookingDetail.isVisibleRefundPolicy = false;

                    loadFragments(getViewPager(), mStayBookingDetail);
                } finally
                {
                    unLockUI();
                }
            } else
            {
                mStayBookingDetail.isVisibleRefundPolicy = false;

                loadFragments(getViewPager(), mStayBookingDetail);

                unLockUI();
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            if (DEBUG == false)
            {
                Crashlytics.logException(t);
            }

            mStayBookingDetail.isVisibleRefundPolicy = false;

            loadFragments(getViewPager(), mStayBookingDetail);

            unLockUI();
        }
    };
}
