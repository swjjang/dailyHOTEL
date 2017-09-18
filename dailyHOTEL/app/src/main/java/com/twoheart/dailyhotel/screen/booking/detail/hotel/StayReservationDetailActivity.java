package com.twoheart.dailyhotel.screen.booking.detail.hotel;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.entity.Booking;
import com.daily.dailyhotel.entity.CarouselListItem;
import com.daily.dailyhotel.parcel.analytics.GourmetDetailAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.NavigatorAnalyticsParam;
import com.daily.dailyhotel.repository.remote.BookingRemoteImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.GourmetRemoteImpl;
import com.daily.dailyhotel.screen.booking.detail.map.GourmetBookingDetailMapActivity;
import com.daily.dailyhotel.screen.common.dialog.navigator.NavigatorDialogActivity;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetCurationOption;
import com.twoheart.dailyhotel.model.GourmetSearchCuration;
import com.twoheart.dailyhotel.model.GourmetSearchParams;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.model.Review;
import com.twoheart.dailyhotel.model.StayBookingDetail;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.activity.PlaceReservationDetailActivity;
import com.twoheart.dailyhotel.screen.common.HappyTalkCategoryDialog;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.screen.common.ZoomMapActivity;
import com.twoheart.dailyhotel.screen.gourmet.preview.GourmetPreviewActivity;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailActivity;
import com.twoheart.dailyhotel.screen.information.FAQActivity;
import com.twoheart.dailyhotel.screen.review.ReviewActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

public class StayReservationDetailActivity extends PlaceReservationDetailActivity
{
    public static final int CODE_REQUEST_ACTIVITY_PLACE_BOOKING_DETAIL_MAP = 10000;

    StayReservationDetailNetworkController mNetworkController;

    private BookingRemoteImpl mBookingRemoteImpl;
    private GourmetRemoteImpl mGourmetRemoteImpl;

    private View mViewByLongPress;
    private Gourmet mGourmetByLongPress;

    private List<Gourmet> mRecommendGourmetList;

    public static Intent newInstance(Context context, int reservationIndex, String aggregationId, String imageUrl, boolean isDeepLink, int bookingState)
    {
        Intent intent = new Intent(context, StayReservationDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_BOOKINGIDX, reservationIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_AGGREGATION_ID, aggregationId);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_URL, imageUrl);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DEEPLINK, isDeepLink);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_BOOKING_STATE, bookingState);

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

        mBookingRemoteImpl = new BookingRemoteImpl(this);
        mGourmetRemoteImpl = new GourmetRemoteImpl(this);

        setContentView(mPlaceReservationDetailLayout.onCreateView(R.layout.activity_stay_reservation_detail));
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        if (isFinishing() == false)
        {
            boolean isShow = mRecommendGourmetList != null && mRecommendGourmetList.size() > 0;
            ((StayReservationDetailLayout) mPlaceReservationDetailLayout).setRecommendGourmetButtonAnimation(isShow);
        }
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

            case CODE_REQUEST_ACTIVITY_PREVIEW:
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    Observable.create(new ObservableOnSubscribe<Object>()
                    {
                        @Override
                        public void subscribe(ObservableEmitter<Object> e) throws Exception
                        {
                            startGourmetDetail(mViewByLongPress, mGourmetByLongPress, mTodayDateTime, (StayBookingDetail) mPlaceBookingDetail);
                        }
                    }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
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

                AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                    , AnalyticsManager.Action.FNQ_CLICK, AnalyticsManager.Label.STAY_BOOKING_DETAIL, null);
            }
        });

        if (DailyTextUtils.isTextEmpty(mPlaceBookingDetail.phone1) == false)
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

        try
        {
            DailyCalendar.setCalendarDateString(calendar, mTodayDateTime.currentDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        int time = calendar.get(Calendar.HOUR_OF_DAY) * 100 + calendar.get(Calendar.MINUTE);

        if (DailyTextUtils.isTextEmpty(mPlaceBookingDetail.phone2) == false && (time >= 900 && time <= 2000))
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

                AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                    , AnalyticsManager.Action.HAPPYTALK_CLICK, AnalyticsManager.Label.STAY_BOOKING_DETAIL, null);
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

                    @Override
                    public void onDismissDialog()
                    {
                        // do nothing!
                    }
                });

                AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                    , AnalyticsManager.Action.CALL_CLICK, AnalyticsManager.Label.STAY_BOOKING_DETAIL, null);
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

                    StayBookingDetail stayBookingDetail = (StayBookingDetail) mPlaceBookingDetail;

                    String message = getString(R.string.message_booking_stay_share_kakao, //
                        stayBookingDetail.userName, stayBookingDetail.placeName, stayBookingDetail.guestName,//
                        DailyTextUtils.getPriceFormat(StayReservationDetailActivity.this, stayBookingDetail.paymentPrice, false), //
                        stayBookingDetail.roomName, DailyCalendar.convertDateFormatString(stayBookingDetail.checkInDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE) HH시"),//
                        DailyCalendar.convertDateFormatString(stayBookingDetail.checkOutDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE) HH시"), //
                        stayBookingDetail.address);

                    String[] checkInDates = stayBookingDetail.checkInDate.split("T");
                    String[] checkOutDates = stayBookingDetail.checkOutDate.split("T");

                    Date checkInDate = DailyCalendar.convertDate(checkInDates[0] + "T00:00:00+09:00", DailyCalendar.ISO_8601_FORMAT);
                    Date checkOutDate = DailyCalendar.convertDate(checkOutDates[0] + "T00:00:00+09:00", DailyCalendar.ISO_8601_FORMAT);

                    int nights = (int) ((DailyCalendar.clearTField(checkOutDate.getTime()) - DailyCalendar.clearTField(checkInDate.getTime())) / DailyCalendar.DAY_MILLISECOND);

                    KakaoLinkManager.newInstance(StayReservationDetailActivity.this).shareBookingStay(message, stayBookingDetail.placeIndex,//
                        mImageUrl, DailyCalendar.convertDateFormatString(stayBookingDetail.checkInDate, DailyCalendar.ISO_8601_FORMAT, "yyyyMMdd"), nights);

                    AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.SHARE//
                        , AnalyticsManager.Action.STAY_BOOKING_SHARE, AnalyticsManager.ValueType.KAKAO, null);
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
                                Util.installPackage(StayReservationDetailActivity.this, "com.kakao.talk");
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
                    StayBookingDetail stayBookingDetail = (StayBookingDetail) mPlaceBookingDetail;

                    String[] checkInDates = stayBookingDetail.checkInDate.split("T");
                    String[] checkOutDates = stayBookingDetail.checkOutDate.split("T");

                    Date checkInDate = DailyCalendar.convertDate(checkInDates[0] + "T00:00:00+09:00", DailyCalendar.ISO_8601_FORMAT);
                    Date checkOutDate = DailyCalendar.convertDate(checkOutDates[0] + "T00:00:00+09:00", DailyCalendar.ISO_8601_FORMAT);

                    int nights = (int) ((DailyCalendar.clearTField(checkOutDate.getTime()) - DailyCalendar.clearTField(checkInDate.getTime())) / DailyCalendar.DAY_MILLISECOND);

                    String longUrl = String.format(Locale.KOREA, "https://mobile.dailyhotel.co.kr/stay/%d?dateCheckIn=%s&stays=%d"//
                        , mPlaceBookingDetail.placeIndex, DailyCalendar.convertDateFormatString(stayBookingDetail.checkInDate, DailyCalendar.ISO_8601_FORMAT, "yyyy-MM-dd")//
                        , nights);

                    final String message = getString(R.string.message_booking_stay_share_sms, //
                        stayBookingDetail.userName, stayBookingDetail.placeName, stayBookingDetail.guestName,//
                        DailyTextUtils.getPriceFormat(StayReservationDetailActivity.this, stayBookingDetail.paymentPrice, false), //
                        stayBookingDetail.roomName, DailyCalendar.convertDateFormatString(stayBookingDetail.checkInDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE) HH시"),//
                        DailyCalendar.convertDateFormatString(stayBookingDetail.checkOutDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE) HH시"), //
                        stayBookingDetail.address);

                    CommonRemoteImpl commonRemote = new CommonRemoteImpl(StayReservationDetailActivity.this);

                    addCompositeDisposable(commonRemote.getShortUrl(longUrl).subscribe(new Consumer<String>()
                    {
                        @Override
                        public void accept(@NonNull String shortUrl) throws Exception
                        {
                            unLockUI();

                            Util.sendSms(StayReservationDetailActivity.this, message + shortUrl);
                        }
                    }, new Consumer<Throwable>()
                    {
                        @Override
                        public void accept(@NonNull Throwable throwable) throws Exception
                        {
                            unLockUI();

                            Util.sendSms(StayReservationDetailActivity.this, message + "https://mobile.dailyhotel.co.kr/stay/" + stayBookingDetail.placeIndex);
                        }
                    }));
                } catch (Exception e)
                {
                    unLockUI();

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

            WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(this, shareDialog);

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

            WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(this, dialog);

            dialog.show();

            dialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    protected void requestPlaceReservationDetail(int reservationIndex, String aggregationId)
    {
        if (DailyTextUtils.isTextEmpty(aggregationId) == true)
        {
            mNetworkController.requestStayReservationDetail(reservationIndex);
        } else
        {
            addCompositeDisposable(mBookingRemoteImpl.getStayBookingDetail(aggregationId).subscribe(new Consumer<com.daily.dailyhotel.entity.StayBookingDetail>()
            {
                @Override
                public void accept(@NonNull com.daily.dailyhotel.entity.StayBookingDetail stayBookingDetail) throws Exception
                {
                    ((StayBookingDetail) mPlaceBookingDetail).setData(stayBookingDetail);
                    onReservationDetail(((StayBookingDetail) mPlaceBookingDetail));
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception
                {
                    onHandleError(throwable);
                }
            }));
        }
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
        try
        {
            // 카카오톡 패키지 설치 여부
            getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

            if (isRefund == true)
            {
                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                    , AnalyticsManager.Action.REFUND_INQUIRY, AnalyticsManager.Label.KAKAO, null);
            } else
            {
                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED,//
                    AnalyticsManager.Action.BOOKING_DETAIL, AnalyticsManager.Label.KAKAO, null);
            }

            if (isRefund == true)
            {
                startActivityForResult(HappyTalkCategoryDialog.newInstance(this//
                    , HappyTalkCategoryDialog.CallScreen.SCREEN_STAY_REFUND//
                    , mPlaceBookingDetail.placeIndex, mReservationIndex, mPlaceBookingDetail.placeName), Constants.CODE_REQUEST_ACTIVITY_HAPPY_TALK);
            } else
            {
                startActivityForResult(HappyTalkCategoryDialog.newInstance(this//
                    , HappyTalkCategoryDialog.CallScreen.SCREEN_STAY_BOOKING//
                    , mPlaceBookingDetail.placeIndex, mReservationIndex, mPlaceBookingDetail.placeName), Constants.CODE_REQUEST_ACTIVITY_HAPPY_TALK);
            }
        } catch (Exception e)
        {
            showSimpleDialog(null, getString(R.string.dialog_msg_not_installed_kakaotalk)//
                , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no)//
                , new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Util.installPackage(StayReservationDetailActivity.this, "com.kakao.talk");
                    }
                }, null);
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
            if (DailyTextUtils.isTextEmpty(bookingDetail.refundPolicy) == false)
            {
                return bookingDetail.refundPolicy;
            } else
            {
                return StayBookingDetail.STATUS_SURCHARGE_REFUND;
            }
        }
    }

    private ArrayList<CarouselListItem> convertCarouselListItemList(List<Gourmet> list)
    {
        ArrayList<Gourmet> gourmetList = new ArrayList<>();
        ArrayList<CarouselListItem> carouselListItemList = new ArrayList<CarouselListItem>();

        if (list == null || list.size() == 0)
        {
            mRecommendGourmetList = gourmetList;
            return carouselListItemList;
        }

        for (Gourmet gourmet : list)
        {
            try
            {
                if (gourmet.isSoldOut == true)
                {
                    // sold out 업장 제외하기로 함
                    // ExLog.d(gourmet.name + " , " + gourmet.isSoldOut + " : " + gourmet.availableTicketNumbers);
                    continue;
                }

                gourmetList.add(gourmet);

                CarouselListItem item = new CarouselListItem(CarouselListItem.TYPE_GOURMET, gourmet);
                carouselListItemList.add(item);
            } catch (Exception e)
            {
                if (gourmet != null)
                {
                    ExLog.w(gourmet.index + " | " + gourmet.name + " :: " + e.getMessage());
                }
            }
        }

        mRecommendGourmetList = gourmetList;

        return carouselListItemList;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void startGourmetDetail(View view, Gourmet gourmet, TodayDateTime todayDateTime, StayBookingDetail stayBookingDetail)
    {
        if (view == null || gourmet == null || todayDateTime == null || stayBookingDetail == null)
        {
            return;
        }

        try
        {
            long currentDateTime = DailyCalendar.convertStringToDate(mTodayDateTime.currentDateTime).getTime();
            long checkInDateTime = DailyCalendar.convertStringToDate(stayBookingDetail.checkInDate).getTime();

            String visitDateTime = stayBookingDetail.checkInDate;
            if (currentDateTime > checkInDateTime)
            {
                visitDateTime = todayDateTime.dailyDateTime;
            }

            // --> 추후에 정리되면 메소드로 수정
            GourmetDetailAnalyticsParam analyticsParam = new GourmetDetailAnalyticsParam();
            analyticsParam.price = gourmet.price;
            analyticsParam.discountPrice = gourmet.discountPrice;
            analyticsParam.setShowOriginalPriceYn(analyticsParam.price, analyticsParam.discountPrice);
            analyticsParam.setProvince(null);
            analyticsParam.entryPosition = gourmet.entryPosition;
            analyticsParam.totalListCount = -1;
            analyticsParam.isDailyChoice = gourmet.isDailyChoice;
            analyticsParam.setAddressAreaName(gourmet.addressSummary);

            // <-- 추후에 정리되면 메소드로 수정

            if (Util.isUsedMultiTransition() == true)
            {
                StayReservationDetailActivity.this.setExitSharedElementCallback(new SharedElementCallback()
                {
                    @Override
                    public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots)
                    {
                        super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);

                        for (View view : sharedElements)
                        {
                            if (view instanceof SimpleDraweeView)
                            {
                                view.setVisibility(View.VISIBLE);
                                break;
                            }
                        }
                    }
                });

                //                Intent intent = GourmetDetailActivity.newInstance(StayReservationDetailActivity.this //
                //                    , gourmetBookingDay, gourmet.index, gourmet.name //
                //                    , gourmet.imageUrl, gourmet.category, gourmet.isSoldOut, analyticsParam, true //
                //                    , PlaceDetailLayout.TRANS_GRADIENT_BOTTOM_TYPE_NONE);


                Intent intent = GourmetDetailActivity.newInstance(StayReservationDetailActivity.this //
                    , gourmet.index, gourmet.name, gourmet.imageUrl, gourmet.discountPrice//
                    , visitDateTime, gourmet.category, gourmet.isSoldOut, false, false, true//
                    , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE//
                    , analyticsParam);

                if (intent == null)
                {
                    Util.restartApp(StayReservationDetailActivity.this);
                    return;
                }

                View simpleDraweeView = view.findViewById(R.id.contentImageView);
                View gradientTopView = view.findViewById(R.id.gradientTopView);
                View gradientBottomView = view.findViewById(R.id.gradientBottomView);

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(StayReservationDetailActivity.this//
                    , android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)) //
                    , android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view)) //
                    , android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view)));

                StayReservationDetailActivity.this.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_DETAIL, options.toBundle());
            } else
            {
                //                Intent intent = GourmetDetailActivity.newInstance(StayReservationDetailActivity.this //
                //                    , gourmetBookingDay, gourmet.index, gourmet.name //
                //                    , gourmet.imageUrl, gourmet.category, gourmet.isSoldOut, analyticsParam, false //
                //                    , PlaceDetailLayout.TRANS_GRADIENT_BOTTOM_TYPE_NONE);

                Intent intent = GourmetDetailActivity.newInstance(StayReservationDetailActivity.this //
                    , gourmet.index, gourmet.name, gourmet.imageUrl, gourmet.discountPrice//
                    , visitDateTime, gourmet.category, gourmet.isSoldOut, false, false, false//
                    , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE//
                    , analyticsParam);

                if (intent == null)
                {
                    Util.restartApp(StayReservationDetailActivity.this);
                    return;
                }

                StayReservationDetailActivity.this.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_DETAIL);

                StayReservationDetailActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private void onReservationDetail(StayBookingDetail stayBookingDetail)
    {
        if (stayBookingDetail == null)
        {
            finish();
            return;
        }

        try
        {
            if (stayBookingDetail.readyForRefund == true)
            {
                stayBookingDetail.isVisibleRefundPolicy = true;

                // 환불 대기 인 상태에서는 문구가 고정이다.
                mPlaceReservationDetailLayout.initLayout(mTodayDateTime, stayBookingDetail);

                analyticsOnScreen(stayBookingDetail, null);
            } else
            {
                long checkOutDateTime = DailyCalendar.convertStringToDate(stayBookingDetail.checkOutDate).getTime();
                long currentDateTime = DailyCalendar.convertStringToDate(mTodayDateTime.currentDateTime).getTime();

                if (currentDateTime < checkOutDateTime)
                {
                    stayBookingDetail.isVisibleRefundPolicy = true;

                    mNetworkController.requestPolicyRefund(stayBookingDetail.reservationIndex, stayBookingDetail.transactionType);
                } else
                {
                    stayBookingDetail.isVisibleRefundPolicy = false;

                    mPlaceReservationDetailLayout.initLayout(mTodayDateTime, stayBookingDetail);

                    analyticsOnScreen(stayBookingDetail, null);
                }
            }

            mPlaceReservationDetailLayout.setDeleteReservationVisible(mBookingState == Booking.BOOKING_STATE_AFTER_USE);

            long currentDateTime = DailyCalendar.convertStringToDate(mTodayDateTime.currentDateTime).getTime();
            long checkInDateTime = DailyCalendar.convertStringToDate(stayBookingDetail.checkInDate).getTime();

            if (currentDateTime > checkInDateTime || (stayBookingDetail.latitude == 0.0d && stayBookingDetail.longitude == 0.0d))
            {
                // 고메 추천 Hidden - 현재 시간이 체크인 시간보다 큰 경우
                ((StayReservationDetailLayout) mPlaceReservationDetailLayout).setRecommendGourmetLayoutVisible(false);
            } else
            {
                // 고메 추천 Show
                GourmetBookingDay gourmetBookingDay = new GourmetBookingDay();

                Date checkInDate = DailyCalendar.convertStringToDate(stayBookingDetail.checkInDate);
                gourmetBookingDay.setVisitDay(DailyCalendar.format(checkInDate, DailyCalendar.ISO_8601_FORMAT));

                Location location = new Location((String) null);
                location.setLatitude(stayBookingDetail.latitude);
                location.setLongitude(stayBookingDetail.longitude);

                GourmetSearchCuration gourmetCuration = new GourmetSearchCuration();
                GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) gourmetCuration.getCurationOption();
                gourmetCurationOption.setSortType(SortType.DISTANCE);

                gourmetCuration.setGourmetBookingDay(gourmetBookingDay);
                gourmetCuration.setLocation(location);
                gourmetCuration.setCurationOption(gourmetCurationOption);
                gourmetCuration.setRadius(10d);

                GourmetSearchParams gourmetParams = (GourmetSearchParams) gourmetCuration.toPlaceParams(1, 10, true);

                addCompositeDisposable(mGourmetRemoteImpl.getGourmetList(gourmetParams) //
                    .observeOn(Schedulers.io()).map(new Function<List<Gourmet>, ArrayList<CarouselListItem>>()
                    {
                        @Override
                        public ArrayList<CarouselListItem> apply(@NonNull List<Gourmet> gourmets) throws Exception
                        {
                            //                                mRecommendGourmetList = gourmets;
                            return convertCarouselListItemList(gourmets);
                        }
                    }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<CarouselListItem>>()
                    {
                        @Override
                        public void accept(@NonNull ArrayList<CarouselListItem> carouselListItemList) throws Exception
                        {
                            unLockUI();

                            ((StayReservationDetailLayout) mPlaceReservationDetailLayout).setRecommendGourmetData(carouselListItemList);

                            boolean hasData = !(carouselListItemList == null || carouselListItemList.size() == 0);

                            AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.BOOKING_DETAIL//
                                , AnalyticsManager.Action.GOURMET_RECOMMEND, hasData ? AnalyticsManager.Label.Y : AnalyticsManager.Label.N, null);
                        }
                    }, new Consumer<Throwable>()
                    {
                        @Override
                        public void accept(@NonNull Throwable throwable) throws Exception
                        {
                            ((StayReservationDetailLayout) mPlaceReservationDetailLayout).setRecommendGourmetData(null);

                            AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.BOOKING_DETAIL//
                                , AnalyticsManager.Action.GOURMET_RECOMMEND, AnalyticsManager.Label.N, null);
                        }
                    }));
            }

        } catch (Exception e)
        {
            Crashlytics.logException(e);
            finish();
            return;
        }
    }

    private void analyticsOnScreen(StayBookingDetail stayBookingDetail, String refundPolicy)
    {
        if (stayBookingDetail == null)
        {
            return;
        }

        HashMap<String, String> params = new HashMap();
        params.put(AnalyticsManager.KeyType.PLACE_TYPE, "stay");
        params.put(AnalyticsManager.KeyType.COUNTRY, stayBookingDetail.isOverseas == false ? "domestic" : "overseas");
        params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(stayBookingDetail.placeIndex));

        if (stayBookingDetail.readyForRefund == true)
        {
            AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordScreen(StayReservationDetailActivity.this//
                , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_CANCELLATION_PROGRESS, null);
        } else
        {
            switch (mBookingState)
            {
                case Booking.BOOKING_STATE_WAITING_REFUND:
                    AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordScreen(StayReservationDetailActivity.this//
                        , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_CANCELLATION_PROGRESS, null);
                    break;

                case Booking.BOOKING_STATE_BEFORE_USE:
                    if (DailyTextUtils.isTextEmpty(refundPolicy) == false)
                    {
                        switch (refundPolicy)
                        {
                            case StayBookingDetail.STATUS_NO_CHARGE_REFUND:
                                AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordScreen(StayReservationDetailActivity.this//
                                    , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_CANCELABLE, null, params);
                                break;

                            case StayBookingDetail.STATUS_SURCHARGE_REFUND:
                                AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordScreen(StayReservationDetailActivity.this//
                                    , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_CANCELLATIONFEE, null, params);
                                break;

                            case StayBookingDetail.STATUS_NRD:
                                AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordScreen(StayReservationDetailActivity.this//
                                    , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_NOREFUNDS, null, params);
                                break;

                            default:
                                AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordScreen(StayReservationDetailActivity.this//
                                    , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO, null, params);
                                break;
                        }
                    } else
                    {
                        AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordScreen(StayReservationDetailActivity.this//
                            , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO, null, params);
                    }
                    break;

                case Booking.BOOKING_STATE_AFTER_USE:
                    AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordScreen(StayReservationDetailActivity.this//
                        , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_POST_VISIT, null, params);
                    break;

                default:
                    AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordScreen(StayReservationDetailActivity.this//
                        , AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO, null, params);
                    break;
            }
        }
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

            startActivityForResult(IssuingReceiptActivity.newInstance(StayReservationDetailActivity.this, mReservationIndex), CODE_REQUEST_ACTIVITY_RECEIPT);
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

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_ZOOMMAP);
            } else
            {
                mPlaceReservationDetailLayout.expandMap(mPlaceBookingDetail.latitude, mPlaceBookingDetail.longitude);
                ((StayReservationDetailLayout) mPlaceReservationDetailLayout).setRecommendGourmetButtonAnimation(false);
            }

            AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
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
                StayBookingDay stayBookingDay = new StayBookingDay();
                stayBookingDay.setCheckInDay(mTodayDateTime.dailyDateTime);
                stayBookingDay.setCheckOutDay(mTodayDateTime.dailyDateTime, 1);

                Intent intent = StayDetailActivity.newInstance(StayReservationDetailActivity.this, stayBookingDay//
                    , mPlaceBookingDetail.isOverseas, mPlaceBookingDetail.placeIndex, 0, false, false, false);
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL);

                overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

                AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
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

            NavigatorAnalyticsParam analyticsParam = new NavigatorAnalyticsParam();
            analyticsParam.category = AnalyticsManager.Category.HOTEL_BOOKINGS;
            analyticsParam.action = AnalyticsManager.Action.HOTEL_DETAIL_NAVIGATION_APP_CLICKED;

            startActivityForResult(NavigatorDialogActivity.newInstance(StayReservationDetailActivity.this, mPlaceBookingDetail.placeName//
                , mPlaceBookingDetail.latitude, mPlaceBookingDetail.longitude, mPlaceBookingDetail.isOverseas, analyticsParam), Constants.CODE_REQUEST_ACTIVITY_NAVIGATOR);
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
                    Intent intent = StayAutoRefundActivity.newInstance(StayReservationDetailActivity.this, stayBookingDetail, mAggregationId);
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

                AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
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

            StayReservationDetailActivity.this.showCallDialog();

            AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.CONTACT_DAILY_CONCIERGE, AnalyticsManager.Label.STAY_BOOKING_DETAIL, null);
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
            DailyTextUtils.clipText(StayReservationDetailActivity.this, mPlaceBookingDetail.address);

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

            AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                , AnalyticsManager.Action.BOOKING_HISTORY_DELETE_TRY, "stay_" + mPlaceBookingDetail.placeIndex, null);
        }

        @Override
        public void onRecommendListItemViewAllClick()
        {
            if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            try
            {
                String title = getResources().getString(R.string.label_home_view_all);

                StayBookingDetail stayBookingDetail = (StayBookingDetail) mPlaceBookingDetail;

                long currentDateTime = DailyCalendar.convertStringToDate(mTodayDateTime.currentDateTime).getTime();
                long checkInDateTime = DailyCalendar.convertStringToDate(stayBookingDetail.checkInDate).getTime();

                String visitDay = stayBookingDetail.checkInDate;
                if (currentDateTime > checkInDateTime)
                {
                    visitDay = mTodayDateTime.dailyDateTime;
                }

                GourmetBookingDay gourmetBookingDay = new GourmetBookingDay();
                gourmetBookingDay.setVisitDay(visitDay);

                ArrayList<Gourmet> gourmetList = new ArrayList<>();
                if (mRecommendGourmetList != null && mRecommendGourmetList.size() > 0)
                {
                    gourmetList.addAll(mRecommendGourmetList);
                }

                Location location = new Location((String) null);
                location.setLatitude(stayBookingDetail.latitude);
                location.setLongitude(stayBookingDetail.longitude);

                Intent intent = GourmetBookingDetailMapActivity.newInstance( //
                    StayReservationDetailActivity.this, title, gourmetBookingDay, gourmetList, location, stayBookingDetail.placeName);

                StayReservationDetailActivity.this.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PLACE_BOOKING_DETAIL_MAP);

                AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordEvent( //
                    AnalyticsManager.Category.BOOKING_GOURMET_RECOMMEND_LIST_CLICK //
                    , AnalyticsManager.Action.LIST_CLICK, null, null);

            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

        }

        @Override
        public void onRecommendListItemClick(View view)
        {
            if (isFinishing() == true || view == null || mPlaceReservationDetailLayout == null || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            CarouselListItem item = (CarouselListItem) view.getTag();
            if (item == null)
            {
                return;
            }

            Gourmet gourmet = item.getItem();
            if (gourmet == null)
            {
                return;
            }

            startGourmetDetail(view, gourmet, mTodayDateTime, (StayBookingDetail) mPlaceBookingDetail);

            String distanceString = String.format(Locale.KOREA, "%.1f", gourmet.distance);

            AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordEvent(//
                AnalyticsManager.Category.BOOKING_GOURMET_RECOMMEND_CLICK, distanceString//
                , Integer.toString(gourmet.index), null);
        }

        @Override
        public void onRecommendListItemLongClick(View view)
        {
            if (isFinishing() == true || view == null || mPlaceReservationDetailLayout == null || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            CarouselListItem item = (CarouselListItem) view.getTag();
            if (item == null)
            {
                return;
            }

            Gourmet gourmet = item.getItem();
            if (gourmet == null)
            {
                return;
            }

            try
            {
                mViewByLongPress = view;
                mGourmetByLongPress = gourmet;

                mPlaceReservationDetailLayout.setBlurVisibility(StayReservationDetailActivity.this, true);

                GourmetBookingDay gourmetBookingDay = new GourmetBookingDay();
                gourmetBookingDay.setVisitDay(mTodayDateTime.dailyDateTime);

                Intent intent = GourmetPreviewActivity.newInstance(StayReservationDetailActivity.this, gourmetBookingDay, gourmet);

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PREVIEW);
            } catch (Exception e)
            {
                unLockUI();
            }

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
            if (isFinishing() == true)
            {
                finish();
                return;
            }

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

                    mPlaceReservationDetailLayout.initLayout(mTodayDateTime, stayBookingDetail);
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
                    mPlaceReservationDetailLayout.initLayout(mTodayDateTime, stayBookingDetail);
                }

                analyticsOnScreen(stayBookingDetail, refundPolicy);
            } else
            {
                stayBookingDetail.isVisibleRefundPolicy = false;

                mPlaceReservationDetailLayout.initLayout(mTodayDateTime, stayBookingDetail);

                analyticsOnScreen(stayBookingDetail, null);
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

                StayReservationDetailActivity.this.onReservationDetail(stayBookingDetail);
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
            StayReservationDetailActivity.this.onErrorPopupMessage(msgCode, message, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    StayReservationDetailActivity.this.onBackPressed();
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

                AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                    , AnalyticsManager.Action.BOOKING_HISTORY_DELETE, "stay_" + mPlaceBookingDetail.placeIndex, null);
            } else
            {
                showSimpleDialog(getString(R.string.dialog_notice2), message, getString(R.string.dialog_btn_text_confirm), null);
            }
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            StayReservationDetailActivity.this.onError(call, e, onlyReport);
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
