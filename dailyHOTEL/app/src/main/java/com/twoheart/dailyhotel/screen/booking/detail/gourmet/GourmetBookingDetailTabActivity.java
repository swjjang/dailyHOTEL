package com.twoheart.dailyhotel.screen.booking.detail.gourmet;

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

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetBookingDetail;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.activity.PlaceBookingDetailTabActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.screen.booking.detail.BookingDetailFragmentPagerAdapter;
import com.twoheart.dailyhotel.screen.information.FAQActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyTextView;
import com.twoheart.dailyhotel.widget.DailyToast;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class GourmetBookingDetailTabActivity extends PlaceBookingDetailTabActivity
{
    private GourmetBookingDetail mGourmetBookingDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mGourmetBookingDetail = new GourmetBookingDetail();

        AnalyticsManager.getInstance(this).recordScreen(this, AnalyticsManager.Screen.BOOKING_DETAIL, null);
    }

    @Override
    protected void loadFragments(ViewPager viewPager, PlaceBookingDetail placeBookingDetail)
    {
        String tag = (String) viewPager.getTag();

        if (tag != null)
        {
            return;
        }

        viewPager.setTag("GourmetBookingDetailTabActivity");

        ArrayList<BaseFragment> fragmentList = new ArrayList<>();

        BaseFragment baseFragment01 = GourmetBookingDetailTabBookingFragment.newInstance(placeBookingDetail, mReservationIndex);
        fragmentList.add(baseFragment01);

        BookingDetailFragmentPagerAdapter fragmentPagerAdapter = new BookingDetailFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(fragmentPagerAdapter);
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

        if (Util.isTextEmpty(mGourmetBookingDetail.phone2) == false)
        {
            phone = mGourmetBookingDetail.phone2;
        } else if (Util.isTextEmpty(mGourmetBookingDetail.phone1) == false)
        {
            phone = mGourmetBookingDetail.phone1;
        } else if (Util.isTextEmpty(mGourmetBookingDetail.phone3) == false)
        {
            phone = mGourmetBookingDetail.phone3;
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
                        AnalyticsManager.getInstance(GourmetBookingDetailTabActivity.this).recordEvent(//
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
                    String message = getString(R.string.message_booking_gourmet_share_kakao, //
                        mGourmetBookingDetail.userName, mGourmetBookingDetail.placeName, mGourmetBookingDetail.guestName,//
                        Util.getPriceFormat(GourmetBookingDetailTabActivity.this, mGourmetBookingDetail.paymentPrice, false), //
                        DailyCalendar.convertDateFormatString(mGourmetBookingDetail.reservationTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)"),//
                        DailyCalendar.convertDateFormatString(mGourmetBookingDetail.reservationTime, DailyCalendar.ISO_8601_FORMAT, "HH:mm"), //
                        mGourmetBookingDetail.ticketName, getString(R.string.label_booking_count, mGourmetBookingDetail.ticketCount), //
                        mGourmetBookingDetail.address);

                    KakaoLinkManager.newInstance(GourmetBookingDetailTabActivity.this).shareBookingGourmet(message, mGourmetBookingDetail.placeIndex,//
                        mImageUrl, DailyCalendar.convertDateFormatString(mGourmetBookingDetail.reservationTime, DailyCalendar.ISO_8601_FORMAT, "yyyyMMdd"));
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }

                AnalyticsManager.getInstance(GourmetBookingDetailTabActivity.this).recordEvent(AnalyticsManager.Category.SHARE//
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
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.putExtra("sms_body", getString(R.string.message_booking_gourmet_share_sms, //
                        mGourmetBookingDetail.userName, mGourmetBookingDetail.placeName, mGourmetBookingDetail.guestName,//
                        Util.getPriceFormat(GourmetBookingDetailTabActivity.this, mGourmetBookingDetail.paymentPrice, false), //
                        DailyCalendar.convertDateFormatString(mGourmetBookingDetail.reservationTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)"),//
                        DailyCalendar.convertDateFormatString(mGourmetBookingDetail.reservationTime, DailyCalendar.ISO_8601_FORMAT, "HH:mm"), //
                        mGourmetBookingDetail.ticketName, getString(R.string.label_booking_count, mGourmetBookingDetail.ticketCount), //
                        mGourmetBookingDetail.address));

                    intent.setType("vnd.android-dir/mms-sms");
                    startActivity(intent);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }

                AnalyticsManager.getInstance(GourmetBookingDetailTabActivity.this).recordEvent(AnalyticsManager.Category.SHARE//
                    , AnalyticsManager.Action.GOURMET_BOOKING_SHARE, AnalyticsManager.ValueType.MESSAGE, null);
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

                            mGourmetBookingDetail.userName = jsonObject.getString("name");

                            DailyMobileAPI.getInstance(GourmetBookingDetailTabActivity.this).requestGourmetBookingDetailInformation(mNetworkTag, reservationIndex, mReservationBookingDetailCallback);
                        } else
                        {
                            String msg = responseJSONObject.getString("msg");
                            DailyToast.showToast(GourmetBookingDetailTabActivity.this, msg, Toast.LENGTH_SHORT);
                            finish();
                        }
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                } else
                {
                    GourmetBookingDetailTabActivity.this.onErrorResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                GourmetBookingDetailTabActivity.this.onError(t);
                finish();
            }
        });
    }

    @Override
    protected void setCurrentDateTime(long currentDateTime, long dailyDateTime)
    {
        mGourmetBookingDetail.currentDateTime = currentDateTime;
        mGourmetBookingDetail.dailyDateTime = dailyDateTime;
    }

    private void startFAQ()
    {
        startActivityForResult(new Intent(this, FAQActivity.class), CODE_REQUEST_ACTIVITY_FAQ);
    }

    private void startGourmetCall(final String phoneNumber)
    {
        View.OnClickListener positiveListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                releaseUiComponent();

                String noCallMessage = getString(R.string.toast_msg_no_gourmet_call, phoneNumber);

                if (Util.isTelephonyEnabled(GourmetBookingDetailTabActivity.this) == true)
                {
                    try
                    {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));

                        AnalyticsManager.getInstance(GourmetBookingDetailTabActivity.this).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED,//
                            AnalyticsManager.Action.BOOKING_DETAIL, AnalyticsManager.Label.DIRECT_CALL, null);
                    } catch (ActivityNotFoundException e)
                    {
                        DailyToast.showToast(GourmetBookingDetailTabActivity.this, noCallMessage, Toast.LENGTH_LONG);
                    }
                } else
                {
                    DailyToast.showToast(GourmetBookingDetailTabActivity.this, noCallMessage, Toast.LENGTH_LONG);
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

    private void startKakao()
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
                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                            mGourmetBookingDetail.setData(dataJSONObject);

                            loadFragments(getViewPager(), mGourmetBookingDetail);
                            break;

                        // 예약 내역 진입시에 다른 사용자가 딥링크로 진입시 예외 처리 추가
                        case 501:
                            onErrorPopupMessage(msgCode, responseJSONObject.getString("msg"), new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    Util.restartApp(GourmetBookingDetailTabActivity.this);
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
                GourmetBookingDetailTabActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            GourmetBookingDetailTabActivity.this.onError(t);
        }
    };
}
