package com.twoheart.dailyhotel.screen.gourmet.payment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.GourmetPaymentInformation;
import com.twoheart.dailyhotel.model.Guest;
import com.twoheart.dailyhotel.model.PlacePaymentInformation;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.TicketInformation;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.request.DailyHotelRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.activity.PlacePaymentActivity;
import com.twoheart.dailyhotel.screen.common.FinalCheckLayout;
import com.twoheart.dailyhotel.screen.information.creditcard.CreditCardListActivity;
import com.twoheart.dailyhotel.screen.information.member.InputMobileNumberDialogActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyScrollView;
import com.twoheart.dailyhotel.widget.DailySignatureView;
import com.twoheart.dailyhotel.widget.DailyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@SuppressLint({"NewApi", "ResourceAsColor"})
public class GourmetPaymentActivity extends PlacePaymentActivity
{
    private GourmetPaymentLayout mGourmetPaymentLayout;
    //
    private boolean mIsChangedTime;
    private boolean mIsChangedPrice; // 가격이 변경된 경우.
    private String mPlaceImageUrl;
    private Province mProvince;
    private String mArea; // Analytics용 소지역
    private Dialog mTimeDialog;

    public static Intent newInstance(Context context, TicketInformation ticketInformation, SaleTime checkInSaleTime//
        , String imageUrl, String category, int gourmetIndex, boolean isDBenefit, Province province, String area)
    {
        Intent intent = new Intent(context, GourmetPaymentActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_TICKETINFORMATION, ticketInformation);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, checkInSaleTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_URL, imageUrl);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_GOURMETIDX, gourmetIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CATEGORY, category);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DBENEFIT, isDBenefit);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, province);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_AREA, area);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mGourmetPaymentLayout = new GourmetPaymentLayout(this, mOnEventListener);

        setContentView(mGourmetPaymentLayout.onCreateView(R.layout.activity_booking_place));

        Intent intent = getIntent();

        if (intent == null || initIntent(intent) == false)
        {
            setResult(CODE_RESULT_ACTIVITY_REFRESH);
            finish();
            return;
        }

        mIsChangedPrice = false;
        mIsChangedTime = false;

        mGourmetPaymentLayout.setToolbarTitle(getString(R.string.actionbar_title_payment_activity));

        setAvailabledDefaultPaymentType();
    }

    private boolean initIntent(Intent intent)
    {
        mPaymentInformation = new GourmetPaymentInformation();
        GourmetPaymentInformation gourmetPaymentInformation = (GourmetPaymentInformation) mPaymentInformation;

        gourmetPaymentInformation.setTicketInformation((TicketInformation) intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_TICKETINFORMATION));
        mCheckInSaleTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);
        mPlaceImageUrl = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_URL);
        gourmetPaymentInformation.placeIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_GOURMETIDX, -1);
        gourmetPaymentInformation.category = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_CATEGORY);
        gourmetPaymentInformation.isDBenefit = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_DBENEFIT, false);
        mProvince = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);
        mArea = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_AREA);

        if (gourmetPaymentInformation.getTicketInformation() == null)
        {
            return false;
        } else
        {
            return true;
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (mTimeDialog != null && mTimeDialog.isShowing() == true)
        {
            mTimeDialog.cancel();
            mTimeDialog = null;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            View view = getCurrentFocus();
            if (view instanceof EditText)
            {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);

                if (outRect.contains((int) event.getRawX(), (int) event.getRawY()) == false)
                {
                    mGourmetPaymentLayout.clearFocus();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void requestUserInformationForPayment()
    {
        DailyNetworkAPI.getInstance(this).requestUserInformationForPayment(mNetworkTag, mUserInformationJsonResponseListener);
    }

    @Override
    protected void requestEasyPayment(PlacePaymentInformation paymentInformation, SaleTime checkInSaleTime)
    {
        if (paymentInformation == null || checkInSaleTime == null || mSelectedCreditCard == null)
        {
            Util.restartApp(this);
            return;
        }

        lockUI();

        Guest guest = mGourmetPaymentLayout.getGuest();

        if (guest == null)
        {
            Customer customer = paymentInformation.getCustomer();

            guest = new Guest();
            guest.name = customer.getName();
            guest.phone = customer.getPhone();
            guest.email = customer.getEmail();
        }

        GourmetPaymentInformation gourmetPaymentInformation = (GourmetPaymentInformation) paymentInformation;
        TicketInformation ticketInformation = gourmetPaymentInformation.getTicketInformation();

        Map<String, String> params = new HashMap<>();
        params.put("sale_reco_idx", String.valueOf(ticketInformation.index));
        params.put("billkey", mSelectedCreditCard.billingkey);
        params.put("ticket_count", String.valueOf(gourmetPaymentInformation.ticketCount));
        params.put("customer_name", guest.name);
        params.put("customer_phone", guest.phone.replace("-", ""));
        params.put("customer_email", guest.email);
        params.put("arrival_time", String.valueOf(gourmetPaymentInformation.ticketTime));
        params.put("customer_msg", "");

        //        if (DEBUG == true)
        //        {
        //            showSimpleDialog(null, params.toString(), getString(R.string.dialog_btn_text_confirm), null);
        //        }

        DailyNetworkAPI.getInstance(this).requestGourmetPayment(mNetworkTag, params, mPaymentEasyCreditCardJsonResponseListener);
    }

    @Override
    protected void requestPlacePaymentInfomation(PlacePaymentInformation paymentInformation, SaleTime checkInSaleTime)
    {
        DailyNetworkAPI.getInstance(this).requestGourmetPaymentInformation(mNetworkTag, //
            ((GourmetPaymentInformation) paymentInformation).getTicketInformation().index, //
            mGourmetPaymentInformationJsonResponseListener);
    }

    @Override
    protected void setSimpleCardInformation(PlacePaymentInformation paymentInformation, CreditCard selectedCreditCard)
    {
        mGourmetPaymentLayout.setPaymentInformation((GourmetPaymentInformation) paymentInformation, selectedCreditCard);
    }

    @Override
    protected void setGuestInformation(String phoneNumber)
    {
        mGourmetPaymentLayout.setGuestPhoneInformation(phoneNumber);
    }

    @Override
    protected void changedPaymentType(PlacePaymentInformation.PaymentType paymentType, CreditCard creditCard)
    {
        mSelectedCreditCard = creditCard;
        mOnEventListener.changedPaymentType(paymentType);
    }

    @Override
    protected boolean isChangedPrice()
    {
        return mIsChangedPrice;
    }

    @Override
    protected boolean hasWarningMessage()
    {
        return false;
    }

    @Override
    protected void showWarningMessageDialog()
    {

    }

    @Override
    protected void showChangedPriceDialog()
    {
        unLockUI();

        setPaymentInformation((GourmetPaymentInformation) mPaymentInformation);

        showChangedValueDialog(R.string.message_gourmet_detail_changed_price, new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                mDontReload = false;
                mIsChangedPrice = false;
                setResult(CODE_RESULT_ACTIVITY_REFRESH);
            }
        });

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES, //
            AnalyticsManager.Action.SOLDOUT_CHANGEPRICE, ((GourmetPaymentInformation) mPaymentInformation).getTicketInformation().placeName, null);
    }

    @Override
    protected void showStopOnSaleDialog()
    {
        showChangedValueDialog(R.string.dialog_msg_gourmet_stop_onsale, null);

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES, //
            AnalyticsManager.Action.SOLDOUT, ((GourmetPaymentInformation) mPaymentInformation).getTicketInformation().placeName, null);
    }

    @Override
    protected void showPaymentWeb(PlacePaymentInformation paymentInformation, SaleTime checkInSaleTime)
    {
        Intent intent = new Intent(this, GourmetPaymentWebActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PAYMENTINFORMATION, paymentInformation);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, checkInSaleTime);

        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PAYMENT);
    }

    @Override
    protected void showPaymentThankyou(PlacePaymentInformation paymentInformation, String imageUrl)
    {
        if (paymentInformation.paymentType == PlacePaymentInformation.PaymentType.EASY_CARD)
        {
            DailyPreference.getInstance(this).setSelectedSimpleCard(DailyHotelRequest.urlEncrypt(mSelectedCreditCard.billingkey));
        }

        GourmetPaymentInformation gourmetPaymentInformation = (GourmetPaymentInformation) paymentInformation;
        TicketInformation ticketInformation = gourmetPaymentInformation.getTicketInformation();

        String placeName = ticketInformation.placeName;
        String placeType = ticketInformation.name;
        int productCount = gourmetPaymentInformation.ticketCount;

        String date = gourmetPaymentInformation.checkInTime;
        String visitTime = DailyCalendar.format(gourmetPaymentInformation.ticketTime, "HH시 mm분", TimeZone.getTimeZone("GMT"));

        String userName = gourmetPaymentInformation.getCustomer().getName();

        Map<String, String> params = getMapPaymentInformation(gourmetPaymentInformation);

        Intent intent = GourmetPaymentThankyouActivity.newInstance(this, imageUrl, placeName, placeType, //
            userName, date, visitTime, productCount, paymentInformation.paymentType.getName(), params);

        startActivityForResult(intent, REQUEST_CODE_PAYMETRESULT_ACTIVITY);
    }

    @Override
    protected Dialog getEasyPaymentConfirmDialog()
    {
        final Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        layoutParams.copyFrom(window.getAttributes());

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);

        int[] messageResIds = {R.string.dialog_msg_gourmet_payment_message01//
            , R.string.dialog_msg_gourmet_payment_message02//
            , R.string.dialog_msg_gourmet_payment_message03//
            , R.string.dialog_msg_gourmet_payment_message07};

        final FinalCheckLayout finalCheckLayout = new FinalCheckLayout(this);
        finalCheckLayout.setMessages(messageResIds);

        final TextView agreeSinatureTextView = (TextView) finalCheckLayout.findViewById(R.id.agreeSinatureTextView);
        final View confirmTextView = finalCheckLayout.findViewById(R.id.confirmTextView);

        confirmTextView.setEnabled(false);

        // 화면이 작은 곳에서 스크롤 뷰가 들어가면서 발생하는 이슈
        final DailyScrollView scrollLayout = (DailyScrollView) finalCheckLayout.findViewById(R.id.scrollLayout);

        View dailySignatureView = finalCheckLayout.getDailySignatureView();

        dailySignatureView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction() & MotionEventCompat.ACTION_MASK)
                {
                    case MotionEvent.ACTION_DOWN:
                    {
                        scrollLayout.setScrollingEnabled(false);
                        break;
                    }

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                    {
                        scrollLayout.setScrollingEnabled(true);
                        break;
                    }
                }

                return false;
            }
        });

        finalCheckLayout.setOnUserActionListener(new DailySignatureView.OnUserActionListener()
        {
            @Override
            public void onConfirmSignature()
            {
                AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
                animation.setDuration(500);
                animation.setFillBefore(true);
                animation.setFillAfter(true);

                animation.setAnimationListener(new Animation.AnimationListener()
                {
                    @Override
                    public void onAnimationStart(Animation animation)
                    {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation)
                    {
                        agreeSinatureTextView.setAnimation(null);
                        agreeSinatureTextView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation)
                    {
                    }
                });

                agreeSinatureTextView.startAnimation(animation);

                //                TransitionDrawable transition = (TransitionDrawable) confirmTextView.getBackground();
                //                transition.startTransition(500);

                confirmTextView.setEnabled(true);
                confirmTextView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        synchronized (GourmetPaymentActivity.this)
                        {
                            if (isLockUiComponent() == true)
                            {
                                return;
                            }

                            dialog.dismiss();

                            lockUI();

                            // 1. 세션이 살아있는지 검사 시작.
                            DailyNetworkAPI.getInstance(GourmetPaymentActivity.this).requestUserInformationForPayment(mNetworkTag, mUserInformationFinalCheckJsonResponseListener);

                            AnalyticsManager.getInstance(GourmetPaymentActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                                , AnalyticsManager.Action.PAYMENT_AGREEMENT_POPPEDUP, AnalyticsManager.Label.AGREE, null);
                        }
                    }
                });
            }
        });

        AnalyticsManager.getInstance(GourmetPaymentActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
            , AnalyticsManager.Action.START_PAYMENT, mPaymentInformation.paymentType.getName(), null);

        dialog.setContentView(finalCheckLayout);

        return dialog;
    }

    @Override
    protected Dialog getPaymentConfirmDialog(PlacePaymentInformation.PaymentType paymentType)
    {
        final Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        View view = LayoutInflater.from(this).inflate(R.layout.fragment_dialog_confirm_payment, null);
        ViewGroup messageLayout = (ViewGroup) view.findViewById(R.id.messageLayout);

        int[] textResIds;

        switch (paymentType)
        {
            // 신용카드 일반 결제
            case CARD:
                textResIds = new int[]{R.string.dialog_msg_gourmet_payment_message01//
                    , R.string.dialog_msg_gourmet_payment_message02//
                    , R.string.dialog_msg_gourmet_payment_message03//
                    , R.string.dialog_msg_gourmet_payment_message06};
                break;

            // 핸드폰 결제
            case PHONE_PAY:
                textResIds = new int[]{R.string.dialog_msg_gourmet_payment_message01//
                    , R.string.dialog_msg_gourmet_payment_message02//
                    , R.string.dialog_msg_gourmet_payment_message03//
                    , R.string.dialog_msg_gourmet_payment_message06};
                break;

            // 계좌 이체
            case VBANK:
                textResIds = new int[]{R.string.dialog_msg_gourmet_payment_message01//
                    , R.string.dialog_msg_gourmet_payment_message02//
                    , R.string.dialog_msg_gourmet_payment_message03//
                    , R.string.dialog_msg_gourmet_payment_message05//
                    , R.string.dialog_msg_gourmet_payment_message06};
                break;

            default:
                return null;
        }

        makeDialogMessages(messageLayout, textResIds);

        View confirmTextView = view.findViewById(R.id.confirmTextView);

        View.OnClickListener buttonOnClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();

                synchronized (GourmetPaymentActivity.this)
                {
                    if (isLockUiComponent() == true)
                    {
                        return;
                    }

                    lockUI();

                    // 1. 세션이 살아있는지 검사 시작.
                    DailyNetworkAPI.getInstance(GourmetPaymentActivity.this).requestUserInformationForPayment(mNetworkTag, mUserInformationFinalCheckJsonResponseListener);

                    AnalyticsManager.getInstance(GourmetPaymentActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                        , AnalyticsManager.Action.PAYMENT_AGREEMENT_POPPEDUP, AnalyticsManager.Label.AGREE, null);

                    AnalyticsManager.getInstance(GourmetPaymentActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
                        , AnalyticsManager.Action.START_PAYMENT, mPaymentInformation.paymentType.getName(), null);
                }
            }
        };

        confirmTextView.setOnClickListener(buttonOnClickListener);

        dialog.setContentView(view);

        return dialog;
    }

    @Override
    protected void onActivityPaymentResult(int requestCode, int resultCode, Intent intent)
    {
        String title = getString(R.string.dialog_title_payment);
        String msg;
        String posTitle = getString(R.string.dialog_btn_text_confirm);
        View.OnClickListener posListener = null;

        switch (resultCode)
        {
            // 결제가 성공한 경우 GA와 믹스패널에 등록
            case CODE_RESULT_ACTIVITY_PAYMENT_COMPLETE:
            case CODE_RESULT_ACTIVITY_PAYMENT_SUCCESS:
                // 가상계좌완료후에는 예약화면의 가상계좌 화면까지 이동한다.
                if (mPaymentInformation.paymentType == PlacePaymentInformation.PaymentType.VBANK)
                {
                    onActivityPaymentResult(requestCode, CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY, intent);
                } else
                {
                    recordAnalyticsPaymentComplete((GourmetPaymentInformation) mPaymentInformation);

                    showPaymentThankyou(mPaymentInformation, mPlaceImageUrl);
                }
                return;

            case CODE_RESULT_ACTIVITY_PAYMENT_SOLD_OUT:
                msg = getString(R.string.act_toast_payment_soldout);
                break;

            case CODE_RESULT_ACTIVITY_PAYMENT_NOT_AVAILABLE:
                title = getString(R.string.dialog_notice2);
                msg = getString(R.string.act_toast_payment_not_available);
                break;

            case CODE_RESULT_ACTIVITY_PAYMENT_NETWORK_ERROR:
                msg = getString(R.string.act_toast_payment_network_error);
                break;

            case CODE_RESULT_ACTIVITY_PAYMENT_INVALID_SESSION:
                restartExpiredSession();
                return;

            case CODE_RESULT_ACTIVITY_PAYMENT_INVALID_DATE:
                msg = getString(R.string.act_toast_payment_invalid_date);
                break;

            case CODE_RESULT_ACTIVITY_PAYMENT_FAIL:
                if (intent != null && intent.hasExtra(NAME_INTENT_EXTRA_DATA_RESULT) == true)
                {
                    msg = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_RESULT);
                } else
                {
                    msg = getString(R.string.act_toast_payment_fail);
                }
                break;

            case CODE_RESULT_ACTIVITY_PAYMENT_CANCELED:
                msg = getString(R.string.act_toast_payment_canceled);

                posListener = new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                    }
                };
                break;

            case CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY:
                /**
                 * 가상계좌선택시 해당 가상계좌 정보를 보기위해 화면 스택을 쌓으면서 들어가야함. 이를 위한 정보를 셋팅.
                 * 예약 리스트 프래그먼트에서 찾아 들어가기 위해서 필요함. 들어간 후에는 다시 프리퍼런스를 초기화해줌.
                 * 플로우) 예약 액티비티 => 호텔탭 액티비티 => 메인액티비티 => 예약 리스트 프래그먼트 => 예약
                 * 리스트 갱신 후 최상단 아이템 인텐트
                 */
                DailyPreference.getInstance(this).setVirtualAccountReadyFlag(CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY);

                if (intent != null && intent.hasExtra(NAME_INTENT_EXTRA_DATA_RESULT) == true)
                {
                    msg = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_RESULT);
                } else
                {
                    msg = getString(R.string.dialog_msg_issuing_account);
                }

                posListener = new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        setResult(CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY);
                        finish();
                    }
                };
                break;

            case CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_TIME_ERROR:
                msg = getString(R.string.act_toast_payment_account_time_error);
                break;

            case CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_DUPLICATE:
                msg = getString(R.string.act_toast_payment_account_duplicate);
                break;

            case CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER:
                msg = getString(R.string.act_toast_payment_account_timeover);
                break;

            case CODE_RESULT_ACTIVITY_PAYMENT_UNKNOW_ERROR:
                if (intent != null && intent.hasExtra(NAME_INTENT_EXTRA_DATA_MESSAGE) == true)
                {
                    msg = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_MESSAGE);
                } else
                {
                    msg = getString(R.string.act_toast_payment_fail);
                }
                break;

            case CODE_RESULT_ACTIVITY_PAYMENT_CANCEL:
            {
                if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_RESULT) == true)
                {
                    msg = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_RESULT);
                } else
                {
                    msg = getString(R.string.act_toast_payment_fail);
                }

                posListener = new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                    }
                };
                break;
            }

            default:
                return;
        }

        if (posListener == null)
        {
            posListener = new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    setResult(CODE_RESULT_ACTIVITY_REFRESH);
                    finish();
                }
            };
        }

        showSimpleDialog(title, msg, posTitle, null, posListener, null, false);
    }

    @Override
    protected void recordAnalyticsAgreeTermDialog(PlacePaymentInformation paymentInformation)
    {
        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_PAYMENT_AGREEMENT_POPUP//
            , getMapPaymentInformation((GourmetPaymentInformation) paymentInformation));
    }

    @Override
    protected void setCoupon(Coupon coupon)
    {
        // do nothing.
    }

    @Override
    protected void setCancelCoupon()
    {
        // do nothing.
    }

    private void setAvailabledDefaultPaymentType()
    {
        boolean isSimpleCardPaymentEnabled = DailyPreference.getInstance(this).isRemoteConfigGourmetSimpleCardPaymentEnabled();
        boolean isCardPaymentEnabled = DailyPreference.getInstance(this).isRemoteConfigGourmetCardPaymentEnabled();
        boolean isPhonePaymentEnabled = DailyPreference.getInstance(this).isRemoteConfigGourmetPhonePaymentEnabled();
        boolean isVirtualPaymentEnabled = DailyPreference.getInstance(this).isRemoteConfigGourmetVirtualPaymentEnabled();

        StringBuilder guideMemo = new StringBuilder();

        if (isSimpleCardPaymentEnabled == false)
        {
            guideMemo.append(getString(R.string.label_simple_payment));
            guideMemo.append(", ");
        }

        if (isCardPaymentEnabled == false)
        {
            guideMemo.append(getString(R.string.label_card_payment));
            guideMemo.append(", ");
        }

        if (isPhonePaymentEnabled == false)
        {
            guideMemo.append(getString(R.string.act_booking_pay_mobile));
            guideMemo.append(", ");
        }

        if (isVirtualPaymentEnabled == false)
        {
            guideMemo.append(getString(R.string.act_booking_pay_account));
            guideMemo.append(", ");
        }

        if (guideMemo.length() > 0)
        {
            guideMemo.setLength(guideMemo.length() - 2);

            mGourmetPaymentLayout.setPaymentMemoTextView(getString(R.string.message_dont_support_payment_type, guideMemo.toString()), true);
        } else
        {
            mGourmetPaymentLayout.setPaymentMemoTextView(null, false);
        }

        mGourmetPaymentLayout.setPaymentTypeEnabled(PlacePaymentInformation.PaymentType.EASY_CARD, isSimpleCardPaymentEnabled);
        mGourmetPaymentLayout.setPaymentTypeEnabled(PlacePaymentInformation.PaymentType.CARD, isCardPaymentEnabled);
        mGourmetPaymentLayout.setPaymentTypeEnabled(PlacePaymentInformation.PaymentType.PHONE_PAY, isPhonePaymentEnabled);
        mGourmetPaymentLayout.setPaymentTypeEnabled(PlacePaymentInformation.PaymentType.VBANK, isVirtualPaymentEnabled);

        if (isSimpleCardPaymentEnabled == true)
        {
            changedPaymentType(PlacePaymentInformation.PaymentType.EASY_CARD, mSelectedCreditCard);
        } else if (isCardPaymentEnabled == true)
        {
            changedPaymentType(PlacePaymentInformation.PaymentType.CARD, mSelectedCreditCard);
        } else if (isPhonePaymentEnabled == true)
        {
            changedPaymentType(PlacePaymentInformation.PaymentType.PHONE_PAY, mSelectedCreditCard);
        } else if (isVirtualPaymentEnabled == true)
        {
            changedPaymentType(PlacePaymentInformation.PaymentType.VBANK, mSelectedCreditCard);
        }
    }

    private void requestValidateTicketPayment(GourmetPaymentInformation gourmetPaymentInformation, SaleTime saleTime)
    {
        if (gourmetPaymentInformation == null || saleTime == null)
        {
            Util.restartApp(this);
            return;
        }

        DailyNetworkAPI.getInstance(this).requestGourmetCheckTicket(mNetworkTag//
            , gourmetPaymentInformation.getTicketInformation().index//
            , saleTime.getDayOfDaysDateFormat("yyMMdd")//
            , gourmetPaymentInformation.ticketCount//
            , Long.toString(gourmetPaymentInformation.ticketTime), mCheckAvailableTicketJsonResponseListener);
    }

    private void recordAnalyticsPaymentComplete(GourmetPaymentInformation gourmetPaymentInformation)
    {
        try
        {
            String strDate = DailyCalendar.format(new Date(), "yyMMddHHmmss");
            String userIndex = gourmetPaymentInformation.getCustomer().getUserIdx();
            String transId = strDate + '_' + userIndex;

            Map<String, String> params = getMapPaymentInformation(gourmetPaymentInformation);

            AnalyticsManager.getInstance(getApplicationContext()).purchaseCompleteGourmet(transId, params);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    protected void recordAnalyticsPayment(PlacePaymentInformation placePaymentInformation)
    {
        if (placePaymentInformation == null)
        {
            return;
        }

        GourmetPaymentInformation gourmetPaymentInformation = (GourmetPaymentInformation) placePaymentInformation;

        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.NAME, gourmetPaymentInformation.getTicketInformation().placeName);
            params.put(AnalyticsManager.KeyType.PRICE, Integer.toString(gourmetPaymentInformation.getTicketInformation().discountPrice));
            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(gourmetPaymentInformation.placeIndex));
            params.put(AnalyticsManager.KeyType.DATE, mCheckInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.TICKET_NAME, gourmetPaymentInformation.getTicketInformation().name);
            params.put(AnalyticsManager.KeyType.TICKET_INDEX, Integer.toString(gourmetPaymentInformation.getTicketInformation().index));
            params.put(AnalyticsManager.KeyType.CATEGORY, gourmetPaymentInformation.category);
            params.put(AnalyticsManager.KeyType.DBENEFIT, gourmetPaymentInformation.isDBenefit ? "yes" : "no");
            params.put(AnalyticsManager.KeyType.REGISTERED_SIMPLE_CARD, mSelectedCreditCard != null ? "y" : "n");

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
                    params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.ALL_LOCALE_KR);
                }

                params.put(AnalyticsManager.KeyType.AREA, Util.isTextEmpty(mArea) ? AnalyticsManager.ValueType.EMPTY : mArea);
            }

            AnalyticsManager.getInstance(GourmetPaymentActivity.this).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_PAYMENT, params);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    private Map<String, String> getMapPaymentInformation(GourmetPaymentInformation gourmetPaymentInformation)
    {
        if (gourmetPaymentInformation == null)
        {
            return null;
        }

        Map<String, String> params = new HashMap<>();

        try
        {
            TicketInformation ticketInformation = gourmetPaymentInformation.getTicketInformation();

            params.put(AnalyticsManager.KeyType.NAME, ticketInformation.placeName);
            params.put(AnalyticsManager.KeyType.PRICE, Integer.toString(ticketInformation.discountPrice));
            params.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(gourmetPaymentInformation.ticketCount));
            params.put(AnalyticsManager.KeyType.TOTAL_PRICE, Integer.toString(ticketInformation.discountPrice * gourmetPaymentInformation.ticketCount));
            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(gourmetPaymentInformation.placeIndex));
            params.put(AnalyticsManager.KeyType.TICKET_NAME, ticketInformation.name);
            params.put(AnalyticsManager.KeyType.TICKET_INDEX, Integer.toString(ticketInformation.index));
            params.put(AnalyticsManager.KeyType.DATE, mCheckInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.PAYMENT_PRICE, Integer.toString(ticketInformation.discountPrice * gourmetPaymentInformation.ticketCount));
            params.put(AnalyticsManager.KeyType.USED_BOUNS, "0");
            params.put(AnalyticsManager.KeyType.CATEGORY, gourmetPaymentInformation.category);
            params.put(AnalyticsManager.KeyType.DBENEFIT, gourmetPaymentInformation.isDBenefit ? "yes" : "no");
            params.put(AnalyticsManager.KeyType.PAYMENT_TYPE, gourmetPaymentInformation.paymentType.getName());
            params.put(AnalyticsManager.KeyType.RESERVATION_TIME, DailyCalendar.format(gourmetPaymentInformation.ticketTime, "HH:mm", TimeZone.getTimeZone("GMT")));
            params.put(AnalyticsManager.KeyType.VISIT_HOUR, Long.toString(gourmetPaymentInformation.ticketTime));

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
                    params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.ALL_LOCALE_KR);
                }

                params.put(AnalyticsManager.KeyType.AREA, Util.isTextEmpty(mArea) ? AnalyticsManager.ValueType.EMPTY : mArea);
            }

            params.put(AnalyticsManager.KeyType.VISIT_DATE, Long.toString(mCheckInSaleTime.getDayOfDaysDate().getTime()));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return params;
    }

    private void setPaymentInformation(GourmetPaymentInformation gourmetPaymentInformation)
    {
        if (gourmetPaymentInformation == null)
        {
            return;
        }

        int payPrice = gourmetPaymentInformation.getPaymentToPay();

        // 50만원 한도 핸드폰 결제 금지
        if (payPrice > PHONE_PAYMENT_LIMIT)
        {
            if (gourmetPaymentInformation.paymentType == PlacePaymentInformation.PaymentType.PHONE_PAY)
            {
                mOnEventListener.changedPaymentType(getAvailableDefaultPaymentType());
            }

            mGourmetPaymentLayout.setPaymentTypeEnabled(PlacePaymentInformation.PaymentType.PHONE_PAY, false);
        } else
        {
            if (DailyPreference.getInstance(this).isRemoteConfigGourmetPhonePaymentEnabled() == true)
            {
                mGourmetPaymentLayout.setPaymentTypeEnabled(PlacePaymentInformation.PaymentType.PHONE_PAY, true);
            }
        }

        mGourmetPaymentLayout.setPaymentInformation(gourmetPaymentInformation.getPaymentToPay());
    }

    private PlacePaymentInformation.PaymentType getAvailableDefaultPaymentType()
    {
        if (DailyPreference.getInstance(this).isRemoteConfigGourmetSimpleCardPaymentEnabled() == true &&//
            mGourmetPaymentLayout.isPaymentTypeEnabled(PlacePaymentInformation.PaymentType.EASY_CARD) == true)
        {
            return PlacePaymentInformation.PaymentType.EASY_CARD;
        } else if (DailyPreference.getInstance(this).isRemoteConfigGourmetCardPaymentEnabled() == true &&//
            mGourmetPaymentLayout.isPaymentTypeEnabled(PlacePaymentInformation.PaymentType.CARD) == true)
        {
            return PlacePaymentInformation.PaymentType.CARD;
        } else if (DailyPreference.getInstance(this).isRemoteConfigGourmetPhonePaymentEnabled() == true &&//
            mGourmetPaymentLayout.isPaymentTypeEnabled(PlacePaymentInformation.PaymentType.PHONE_PAY) == true)
        {
            return PlacePaymentInformation.PaymentType.PHONE_PAY;
        } else if (DailyPreference.getInstance(this).isRemoteConfigGourmetVirtualPaymentEnabled() == true &&//
            mGourmetPaymentLayout.isPaymentTypeEnabled(PlacePaymentInformation.PaymentType.VBANK) == true)
        {
            return PlacePaymentInformation.PaymentType.VBANK;
        } else
        {
            return null;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // User ActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private GourmetPaymentLayout.OnEventListener mOnEventListener = new GourmetPaymentLayout.OnEventListener()
    {
        @Override
        public void selectTicketTime(String selectedTime)
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            final GourmetPaymentInformation gourmetPaymentInformation = (GourmetPaymentInformation) mPaymentInformation;

            if (mTimeDialog != null)
            {
                mTimeDialog.cancel();
                mTimeDialog = null;
            }

            mTimeDialog = Util.showDatePickerDialog(GourmetPaymentActivity.this//
                , getString(R.string.label_booking_select_ticket_time)//
                , gourmetPaymentInformation.getTicketTimes(), selectedTime, getString(R.string.dialog_btn_text_confirm), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        int select = (Integer) v.getTag();

                        if (gourmetPaymentInformation.ticketTimes.length - 1 < select)
                        {
                            return;
                        }

                        try
                        {
                            gourmetPaymentInformation.ticketTime = gourmetPaymentInformation.ticketTimes[select];
                            mGourmetPaymentLayout.setTicketTime(gourmetPaymentInformation.ticketTime);
                        } catch (Exception e)
                        {
                            ExLog.d(e.toString());

                            onError(e);
                        }
                    }
                });

            if (mTimeDialog != null)
            {
                mTimeDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        mTimeDialog = null;
                        releaseUiComponent();
                    }
                });
            }
        }

        @Override
        public void plusTicketCount()
        {
            GourmetPaymentInformation gourmetPaymentInformation = (GourmetPaymentInformation) mPaymentInformation;

            int count = gourmetPaymentInformation.ticketCount;
            int maxCount = gourmetPaymentInformation.ticketMaxCount;

            if (count >= maxCount)
            {
                mGourmetPaymentLayout.setTicketCountPlusButtonEnabled(false);
                DailyToast.showToast(GourmetPaymentActivity.this, getString(R.string.toast_msg_maxcount_ticket, maxCount), Toast.LENGTH_LONG);
            } else
            {
                gourmetPaymentInformation.ticketCount = count + 1;
                mGourmetPaymentLayout.setTicketCount(gourmetPaymentInformation.ticketCount);
                mGourmetPaymentLayout.setTicketCountMinusButtonEnabled(true);

                // 결제 가격을 바꾸어야 한다.
                setPaymentInformation(gourmetPaymentInformation);
            }
        }

        @Override
        public void minusTicketCount()
        {
            GourmetPaymentInformation gourmetPaymentInformation = (GourmetPaymentInformation) mPaymentInformation;

            int count = gourmetPaymentInformation.ticketCount;

            if (count <= 1)
            {
                mGourmetPaymentLayout.setTicketCountMinusButtonEnabled(false);
            } else
            {
                gourmetPaymentInformation.ticketCount = count - 1;
                mGourmetPaymentLayout.setTicketCount(gourmetPaymentInformation.ticketCount);
                mGourmetPaymentLayout.setTicketCountPlusButtonEnabled(true);

                // 결제 가격을 바꾸어야 한다.
                setPaymentInformation(gourmetPaymentInformation);
            }
        }

        @Override
        public void startCreditCardManager()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            mPaymentInformation.setGuest(mGourmetPaymentLayout.getGuest());

            AnalyticsManager.getInstance(GourmetPaymentActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
                , AnalyticsManager.Action.EDIT_BUTTON_CLICKED, AnalyticsManager.Label.PAYMENT_CARD_EDIT, null);

            Intent intent = new Intent(GourmetPaymentActivity.this, CreditCardListActivity.class);
            intent.setAction(Intent.ACTION_PICK);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_CREDITCARD, mSelectedCreditCard);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CREDITCARD_MANAGER);
        }

        @Override
        public void changedPaymentType(PlacePaymentInformation.PaymentType paymentType)
        {
            mPaymentInformation.paymentType = paymentType;
            mGourmetPaymentLayout.checkPaymentType(paymentType);
        }

        @Override
        public void doPayment()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            GourmetPaymentInformation gourmetPaymentInformation = (GourmetPaymentInformation) mPaymentInformation;

            if (gourmetPaymentInformation.ticketTime == 0)
            {
                releaseUiComponent();
                mGourmetPaymentLayout.scrollTop();

                DailyToast.showToast(GourmetPaymentActivity.this, R.string.toast_msg_please_select_reservationtime, Toast.LENGTH_SHORT);
                return;
            }

            Guest guest = mGourmetPaymentLayout.getGuest();

            // 수정 모드인 경우 데이터를 다시 받아와야 한다.
            if (guest != null)
            {
                if (Util.isTextEmpty(guest.name) == true)
                {
                    releaseUiComponent();

                    mGourmetPaymentLayout.requestGuestInformationFocus(Constants.UserInformationType.NAME);

                    DailyToast.showToast(GourmetPaymentActivity.this, R.string.message_gourmet_please_input_guest, Toast.LENGTH_SHORT);
                    return;
                } else if (Util.isTextEmpty(guest.phone) == true)
                {
                    releaseUiComponent();

                    mGourmetPaymentLayout.requestGuestInformationFocus(Constants.UserInformationType.PHONE);

                    DailyToast.showToast(GourmetPaymentActivity.this, R.string.toast_msg_please_input_contact, Toast.LENGTH_SHORT);
                    return;
                } else if (Util.isTextEmpty(guest.email) == true)
                {
                    releaseUiComponent();

                    mGourmetPaymentLayout.requestGuestInformationFocus(Constants.UserInformationType.EMAIL);

                    DailyToast.showToast(GourmetPaymentActivity.this, R.string.toast_msg_please_input_email, Toast.LENGTH_SHORT);
                    return;
                } else if (android.util.Patterns.EMAIL_ADDRESS.matcher(guest.email).matches() == false)
                {
                    releaseUiComponent();

                    mGourmetPaymentLayout.requestGuestInformationFocus(Constants.UserInformationType.EMAIL);

                    DailyToast.showToast(GourmetPaymentActivity.this, R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
                    return;
                }
            }

            gourmetPaymentInformation.setGuest(guest);

            if (gourmetPaymentInformation.paymentType == PlacePaymentInformation.PaymentType.VBANK && DailyPreference.getInstance(GourmetPaymentActivity.this).getNotificationUid() < 0)
            {
                // 가상계좌 결제시 푸쉬를 받지 못하는 경우
                String title = getString(R.string.dialog_notice2);
                String positive = getString(R.string.dialog_btn_text_confirm);
                String msg = getString(R.string.dialog_msg_none_gcmid);

                showSimpleDialog(title, msg, positive, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        processAgreeTermDialog();
                    }
                }, new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        unLockUI();
                    }
                });
            } else
            {
                processAgreeTermDialog();
            }

            String label = String.format("%s-%s", gourmetPaymentInformation.getTicketInformation().placeName, gourmetPaymentInformation.getTicketInformation().name);
            AnalyticsManager.getInstance(GourmetPaymentActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
                , AnalyticsManager.Action.PAYMENT_CLICKED, label, null);
        }

        @Override
        public void showInputMobileNumberDialog(String mobileNumber)
        {
            if (isFinishing() == true)
            {
                return;
            }

            mPaymentInformation.setGuest(mGourmetPaymentLayout.getGuest());

            Intent intent = InputMobileNumberDialogActivity.newInstance(GourmetPaymentActivity.this, mobileNumber);
            startActivityForResult(intent, REQUEST_CODE_COUNTRYCODE_DIALOG_ACTIVITY);
        }

        @Override
        public void showCallDialog()
        {
            GourmetPaymentActivity.this.showCallDialog();
        }

        @Override
        public void finish()
        {
            GourmetPaymentActivity.this.finish();
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Network Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected DailyHotelJsonResponseListener mUserInformationJsonResponseListener = new DailyHotelJsonResponseListener()
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

                if (msg_code != 0)
                {
                    if (response.has("msg") == true)
                    {
                        String msg = response.getString("msg");

                        DailyToast.showToast(GourmetPaymentActivity.this, msg, Toast.LENGTH_SHORT);
                        setResult(CODE_RESULT_ACTIVITY_REFRESH);
                        finish();
                        return;
                    } else
                    {
                        throw new NullPointerException("response == null");
                    }
                }

                JSONObject jsonData = response.getJSONObject("data");

                String name = jsonData.getString("user_name");
                String phone = jsonData.getString("user_phone");
                String email = jsonData.getString("user_email");
                String userIndex = jsonData.getString("user_idx");
                int bonus = jsonData.getInt("user_bonus");

                if (bonus < 0)
                {
                    bonus = 0;
                }

                GourmetPaymentInformation gourmetPaymentInformation = (GourmetPaymentInformation) mPaymentInformation;
                gourmetPaymentInformation.bonus = bonus;

                Customer buyer = new Customer();
                buyer.setEmail(email);
                buyer.setName(name);
                buyer.setPhone(phone);
                buyer.setUserIdx(userIndex);

                gourmetPaymentInformation.setCustomer(buyer);

                Guest guest = gourmetPaymentInformation.getGuest();

                mGourmetPaymentLayout.setUserInformation(buyer, false);
                mGourmetPaymentLayout.setGuestInformation(guest, false);

                // 2. 화면 정보 얻기
                DailyNetworkAPI.getInstance(GourmetPaymentActivity.this).requestGourmetPaymentInformation(mNetworkTag//
                    , gourmetPaymentInformation.getTicketInformation().index//
                    , mGourmetPaymentInformationJsonResponseListener);
            } catch (Exception e)
            {
                onError(e);
            }
        }
    };

    private DailyHotelJsonResponseListener mGourmetPaymentInformationJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                GourmetPaymentInformation gourmetPaymentInformation = (GourmetPaymentInformation) mPaymentInformation;

                int msgCode = response.getInt("msg_code");

                if (msgCode == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    //					jsonObject.getInt("fnb_sale_reco_idx");
                    //					jsonObject.getInt("is_sale_time_over");
                    //					jsonObject.getInt("name");
                    int discountPrice = jsonObject.getInt("discount");
                    long sday = jsonObject.getLong("sday");
                    //					jsonObject.getInt("available_ticket_count");
                    int maxCount = jsonObject.getInt("max_sale_count");

                    JSONArray timeJSONArray = jsonObject.getJSONArray("eating_time_list");

                    int length = timeJSONArray.length();
                    long[] times = new long[length];

                    for (int i = 0; i < length; i++)
                    {
                        times[i] = timeJSONArray.getLong(i);
                    }

                    if (gourmetPaymentInformation.ticketTime == 0)
                    {

                    } else
                    {
                        boolean isExistTime = false;

                        for (long time : times)
                        {
                            if (gourmetPaymentInformation.ticketTime == time)
                            {
                                isExistTime = true;
                                break;
                            }
                        }

                        // 시간 값이 없어진 경우
                        if (isExistTime == false)
                        {
                            mIsChangedTime = true;
                        }
                    }

                    gourmetPaymentInformation.ticketTimes = times;

                    // 가격이 변동 되었다.
                    if (gourmetPaymentInformation.getTicketInformation().discountPrice != discountPrice)
                    {
                        mIsChangedPrice = true;
                    }

                    gourmetPaymentInformation.getTicketInformation().discountPrice = discountPrice;
                    gourmetPaymentInformation.ticketMaxCount = maxCount;
                    gourmetPaymentInformation.checkInTime = DailyCalendar.format(sday, "yyyy.MM.dd (EEE)", TimeZone.getTimeZone("GMT"));

                    if (gourmetPaymentInformation.ticketTime == 0)
                    {
                        // 방문시간을 선택하지 않은 경우
                        DailyNetworkAPI.getInstance(GourmetPaymentActivity.this).requestUserBillingCardList(mNetworkTag, mUserCreditCardListJsonResponseListener);
                    } else
                    {
                        requestValidateTicketPayment(gourmetPaymentInformation, mCheckInSaleTime);
                    }

                    mGourmetPaymentLayout.setTicketInformation(gourmetPaymentInformation);
                    setPaymentInformation(gourmetPaymentInformation);
                } else
                {
                    setResult(CODE_RESULT_ACTIVITY_REFRESH);
                    onErrorPopupMessage(msgCode, response.getString("msg"));
                }
            } catch (Exception e)
            {
                setResult(CODE_RESULT_ACTIVITY_REFRESH);
                onError(e);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            GourmetPaymentActivity.this.onErrorResponse(volleyError);
        }
    };

    protected DailyHotelJsonResponseListener mUserInformationFinalCheckJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msg_code");

                if (msgCode == 0)
                {
                    JSONObject jsonData = response.getJSONObject("data");

                    int bonus = jsonData.getInt("user_bonus");

                    if (bonus < 0)
                    {
                        bonus = 0;
                    }

                    if (mPaymentInformation.discountType == PlacePaymentInformation.DiscountType.BONUS //
                        && bonus != mPaymentInformation.bonus)
                    {
                        // 보너스 값이 변경된 경우
                        mPaymentInformation.bonus = bonus;
                        showChangedBonusDialog();
                        return;
                    }

                    DailyNetworkAPI.getInstance(GourmetPaymentActivity.this).requestGourmetPaymentInformation(mNetworkTag, //
                        ((GourmetPaymentInformation) mPaymentInformation).getTicketInformation().index, //
                        mFinalCheckPayJsonResponseListener);
                } else
                {
                    onErrorPopupMessage(msgCode, response.getString("msg"));
                }
            } catch (Exception e)
            {
                onError(e);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            GourmetPaymentActivity.this.onErrorResponse(volleyError);
        }
    };

    private DailyHotelJsonResponseListener mFinalCheckPayJsonResponseListener = new DailyHotelJsonResponseListener()
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
                GourmetPaymentInformation gourmetPaymentInformation = (GourmetPaymentInformation) mPaymentInformation;

                int msgCode = response.getInt("msg_code");

                if (msgCode == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    //					jsonObject.getInt("fnb_sale_reco_idx");
                    //					jsonObject.getInt("is_sale_time_over");
                    //					jsonObject.getInt("name");
                    int discountPrice = jsonObject.getInt("discount");
                    //                    long sday = jsonObject.getLong("sday");
                    //					jsonObject.getInt("available_ticket_count");
                    //                    int maxCount = jsonObject.getInt("max_sale_count");

                    JSONArray timeJSONArray = jsonObject.getJSONArray("eating_time_list");

                    int length = timeJSONArray.length();
                    long[] times = new long[length];

                    for (int i = 0; i < length; i++)
                    {
                        times[i] = timeJSONArray.getLong(i);
                    }

                    if (gourmetPaymentInformation.ticketTime == 0)
                    {
                        mIsChangedTime = true;
                    } else
                    {
                        boolean isExistTime = false;

                        for (long time : times)
                        {
                            if (gourmetPaymentInformation.ticketTime == time)
                            {
                                isExistTime = true;
                                break;
                            }
                        }

                        // 시간 값이 없어진 경우
                        if (isExistTime == false)
                        {
                            mIsChangedTime = true;
                        }
                    }

                    gourmetPaymentInformation.ticketTimes = times;

                    TicketInformation ticketInformation = gourmetPaymentInformation.getTicketInformation();

                    // 가격이 변동 되었다.
                    if (ticketInformation.discountPrice != discountPrice)
                    {
                        mIsChangedPrice = true;
                    }

                    ticketInformation.discountPrice = discountPrice;

                    if (mIsChangedPrice == true)
                    {
                        mIsChangedPrice = false;

                        // 현재 있는 팝업을 없애도록 한다.
                        if (mFinalCheckDialog != null && mFinalCheckDialog.isShowing() == true)
                        {
                            mFinalCheckDialog.cancel();
                            mFinalCheckDialog = null;
                        }

                        showChangedPriceDialog();
                    } else if (mIsChangedTime == true)
                    {
                        mIsChangedTime = false;

                        // 현재 있는 팝업을 없애도록 한다.
                        if (mFinalCheckDialog != null && mFinalCheckDialog.isShowing() == true)
                        {
                            mFinalCheckDialog.cancel();
                            mFinalCheckDialog = null;
                        }

                        showChangedTimeDialog();
                    } else
                    {
                        processPayment(mPaymentInformation, mCheckInSaleTime);
                    }
                } else
                {
                    onErrorPopupMessage(msgCode, response.getString("msg"));
                }
            } catch (Exception e)
            {
                onError(e);
            }
        }
    };

    private DailyHotelJsonResponseListener mCheckAvailableTicketJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                JSONObject jsonObject = response.getJSONObject("data");

                boolean isOnSale = jsonObject.getBoolean("on_sale");

                int msgCode = response.getInt("msg_code");

                if (isOnSale == true && msgCode == 0)
                {
                    DailyNetworkAPI.getInstance(GourmetPaymentActivity.this).requestUserBillingCardList(mNetworkTag, mUserCreditCardListJsonResponseListener);
                } else
                {
                    onErrorPopupMessage(msgCode, response.getString("msg"));
                }
            } catch (Exception e)
            {
                onError(e);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            GourmetPaymentActivity.this.onErrorResponse(volleyError);
        }
    };

    private DailyHotelJsonResponseListener mPaymentEasyCreditCardJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msg_code");

                hidePorgressDialog();

                if (msgCode == 0)
                {
                    // 결제 관련 로그 남기기
                    recordAnalyticsPaymentComplete((GourmetPaymentInformation) mPaymentInformation);

                    showPaymentThankyou(mPaymentInformation, mPlaceImageUrl);
                } else
                {
                    int resultCode;
                    Intent intent = new Intent();

                    if (response.has("msg") == false)
                    {
                        resultCode = CODE_RESULT_ACTIVITY_PAYMENT_FAIL;
                    } else
                    {
                        String msg = response.getString("msg");

                        String[] result = msg.split("\\^");

                        if (result.length >= 1)
                        {
                            intent.putExtra(NAME_INTENT_EXTRA_DATA_RESULT, result[1]);
                        }

                        if ("SUCCESS".equalsIgnoreCase(result[0]) == true)
                        {
                            resultCode = CODE_RESULT_ACTIVITY_PAYMENT_SUCCESS;
                        } else if ("FAIL".equalsIgnoreCase(result[0]) == true)
                        {
                            resultCode = CODE_RESULT_ACTIVITY_PAYMENT_CANCEL;
                        } else
                        {
                            resultCode = CODE_RESULT_ACTIVITY_PAYMENT_FAIL;
                        }
                    }

                    onActivityPaymentResult(CODE_REQUEST_ACTIVITY_PAYMENT, resultCode, intent);
                }
            } catch (Exception e)
            {
                onError(e);
            } finally
            {
                unLockUI();
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            GourmetPaymentActivity.this.onErrorResponse(volleyError);
        }
    };
}
