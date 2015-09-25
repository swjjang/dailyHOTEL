package com.twoheart.dailyhotel.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.model.Guest;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.TicketInformation;
import com.twoheart.dailyhotel.model.TicketPayment;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.FinalCheckLayout;
import com.twoheart.dailyhotel.view.GourmetBookingLayout;
import com.twoheart.dailyhotel.view.GourmetBookingLayout.UserInformationType;
import com.twoheart.dailyhotel.view.widget.DailySignatureView;
import com.twoheart.dailyhotel.view.widget.DailyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

@SuppressLint({"NewApi", "ResourceAsColor"})
public class GourmetPaymentActivity extends TicketPaymentActivity
{
    private GourmetBookingLayout mGourmetBookingLayout;
    private boolean mIsChangedTime;

    public interface OnUserActionListener
    {
        public void plusTicketTime();

        public void minusTicketTime();

        public void plusTicketCount();

        public void minusTicketCount();

        public void editUserInformation();

        public void showCreditCardManager();

        public void setPaymentType(TicketPayment.PaymentType type);

        public void pay();

        public void showCallDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mTicketPayment = new TicketPayment();
        mGourmetBookingLayout = new GourmetBookingLayout(this, mOnUserActionListener);

        setContentView(mGourmetBookingLayout.getLayout());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {
            mTicketPayment.setTicketInformation((TicketInformation) bundle.getParcelable(NAME_INTENT_EXTRA_DATA_TICKETINFORMATION));
            mCheckInSaleTime = bundle.getParcelable(NAME_INTENT_EXTRA_DATA_SALETIME);
        }

        if (mTicketPayment.getTicketInformation() == null)
        {
            finish();
            return;
        }

        mIsChangedPrice = false;

        setActionBar(mTicketPayment.getTicketInformation().placeName);
    }

    @Override
    protected void requestValidateTicketPayment(TicketPayment ticketPayment, SaleTime checkInSaleTime)
    {
        if (ticketPayment == null || checkInSaleTime == null)
        {
            onInternalError();
            return;
        }

        String params = String.format("?sale_reco_idx=%d&sday=%s&ticket_count=%d&arrival_time=%s", //
                ticketPayment.getTicketInformation().index, checkInSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"), ticketPayment.ticketCount, String.valueOf(ticketPayment.ticketTime));
        mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_FNB_SALE_SESSION_TICKET_SELL_CHECK).append(params).toString(), null, mTicketSellCheckJsonResponseListener, this));
    }

    @Override
    protected void requestPayEasyPayment(TicketPayment ticketPayment, SaleTime checkInSaleTime)
    {
        String bonus = "0"; // 적립금

        if (ticketPayment.isEnabledBonus == true)
        {
            bonus = String.valueOf(ticketPayment.bonus);
        }

        Map<String, String> params = new HashMap<String, String>();

        TicketInformation ticketInformation = ticketPayment.getTicketInformation();
        Guest guest = ticketPayment.getGuest();

        params.put("sale_reco_idx", String.valueOf(ticketInformation.index));
        params.put("billkey", mSelectedCreditCard.billingkey);
        params.put("ticket_count", String.valueOf(ticketPayment.ticketCount));
        params.put("customer_name", guest.name);
        params.put("customer_phone", guest.phone);
        params.put("customer_email", guest.email);
        params.put("arrival_time", String.valueOf(ticketPayment.ticketTime));

        //        if (DEBUG == true)
        //        {
        //            showSimpleDialog(null, params.toString(), getString(R.string.dialog_btn_text_confirm), null);
        //        }

        mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_FNB_PAYMENT_SESSION_EASY).toString(), params, mPayEasyPaymentJsonResponseListener, this));
    }

    @Override
    protected void requestTicketPaymentInfomation(int index)
    {
        if (index < 0)
        {
            onInternalError();
            return;
        }

        String params = String.format("?sale_reco_idx=%d", index);
        mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_FNB_SALE_TICKET_PAYMENT_INFO).append(params).toString(), null, mTicketPaymentInformationJsonResponseListener, this));
    }

    @Override
    protected void updateLayout(TicketPayment ticketPayment, CreditCard creditCard)
    {
        if (mGourmetBookingLayout == null || ticketPayment == null)
        {
            return;
        }

        mGourmetBookingLayout.updateTicketPaymentInformation(ticketPayment, creditCard);
    }

    @Override
    protected void updatePaymentInformation(TicketPayment ticketPayment, CreditCard creditCard)
    {
        if (mGourmetBookingLayout == null || ticketPayment == null)
        {
            return;
        }

        mGourmetBookingLayout.updatePaymentInformationLayout(this, ticketPayment, creditCard);
    }

    @Override
    protected void checkPaymentType(TicketPayment.PaymentType type)
    {
        if (mTicketPayment != null)
        {
            mTicketPayment.paymentType = type;
        }

        if (mGourmetBookingLayout != null)
        {
            mGourmetBookingLayout.checkPaymentType(type);
        }
    }

    @Override
    protected void showFinalCheckDialog()
    {
        if (isFinishing() == true)
        {
            return;
        }

        if (mFinalCheckDialog != null)
        {
            mFinalCheckDialog.cancel();
            mFinalCheckDialog = null;
        }

        mFinalCheckDialog = new Dialog(this);

        mFinalCheckDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mFinalCheckDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mFinalCheckDialog.setCanceledOnTouchOutside(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = mFinalCheckDialog.getWindow();
        layoutParams.copyFrom(window.getAttributes());

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);

        int[] messageResIds = {R.string.dialog_msg_gourmet_payment_message01, R.string.dialog_msg_gourmet_payment_message02, R.string.dialog_msg_gourmet_payment_message03};

        final FinalCheckLayout finalCheckLayout = new FinalCheckLayout(GourmetPaymentActivity.this, messageResIds);
        final TextView agreeSinatureTextView = (TextView) finalCheckLayout.findViewById(R.id.agreeSinatureTextView);
        final View agreeLayout = finalCheckLayout.findViewById(R.id.agreeLayout);

        agreeLayout.setEnabled(false);

        finalCheckLayout.setOnUserActionListener(new DailySignatureView.OnUserActionListener()
        {
            @Override
            public void onConfirmSignature()
            {
                agreeLayout.setEnabled(true);

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

                TransitionDrawable transition = (TransitionDrawable) agreeLayout.getBackground();
                transition.startTransition(500);

                agreeSinatureTextView.startAnimation(animation);

                agreeLayout.setOnClickListener(new View.OnClickListener()
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

                            lockUI();

                            mState = STATE_PAYMENT;

                            // 1. 세션이 살아있는지 검사 시작.
                            mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_INFORMATION).toString(), null, mUserInformationJsonResponseListener, GourmetPaymentActivity.this));

                            mFinalCheckDialog.dismiss();

                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put(AnalyticsManager.Label.PLACE_TICKET_INDEX, String.valueOf(mTicketPayment.getTicketInformation().index));
                            params.put(AnalyticsManager.Label.PLACE_TICKET_NAME, mTicketPayment.getTicketInformation().name);
                            params.put(AnalyticsManager.Label.PLACE_NAME, mTicketPayment.getTicketInformation().placeName);

                            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Screen.PAYMENT_AGREE_POPUP, AnalyticsManager.Action.CLICK, mTicketPayment.paymentType.name(), params);
                        }
                    }
                });
            }
        });

        mFinalCheckDialog.setContentView(finalCheckLayout);
        mFinalCheckDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                releaseUiComponent();
            }
        });

        try
        {
            mFinalCheckDialog.show();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    /**
     * 결제 수단에 알맞은 결제 동의 확인 다이얼로그를 만든다.
     *
     * @param type CARD, ACCOUNT, HP 세가지 타입 존재.
     * @return 타입에 맞는 결제 동의 다이얼로그 반환.
     */

    protected Dialog getPaymentConfirmDialog(int type)
    {
        final Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        View view = LayoutInflater.from(this).inflate(R.layout.fragment_dialog_confirm_payment, null);
        ViewGroup messageLayout = (ViewGroup) view.findViewById(R.id.messageLayout);

        int[] textResIds;

        switch (type)
        {
            // 핸드폰 결제
            case DIALOG_CONFIRM_PAYMENT_HP:
                textResIds = new int[]{R.string.dialog_msg_gourmet_payment_message01//
                        , R.string.dialog_msg_gourmet_payment_message02//
                        , R.string.dialog_msg_gourmet_payment_message03//
                        , R.string.dialog_msg_gourmet_payment_message04};
                break;

            // 계좌 이체
            case DIALOG_CONFIRM_PAYMENT_ACCOUNT:
                textResIds = new int[]{R.string.dialog_msg_gourmet_payment_message01//
                        , R.string.dialog_msg_gourmet_payment_message02//
                        , R.string.dialog_msg_gourmet_payment_message03//
                        , R.string.dialog_msg_gourmet_payment_message05};
                break;

            // 신용카드 일반 결제
            case DIALOG_CONFIRM_PAYMENT_CARD:
                textResIds = new int[]{R.string.dialog_msg_gourmet_payment_message01//
                        , R.string.dialog_msg_gourmet_payment_message02//
                        , R.string.dialog_msg_gourmet_payment_message03};
                break;

            default:
                return null;
        }

        int length = textResIds.length;

        for (int i = 0; i < length; i++)
        {
            View messageRow = LayoutInflater.from(this).inflate(R.layout.row_payment_agreedialog, messageLayout, false);

            TextView messageTextView = (TextView) messageRow.findViewById(R.id.messageTextView);

            String message = getString(textResIds[i]);

            if (i == 0)
            {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(message);

                int boldStartIndex = message.indexOf("예약");
                int boldLength = "예약 취소, 변경 및 환불이 불가".length();

                spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.dialog_title_text)), //
                        boldStartIndex, boldStartIndex + boldLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), //
                        boldStartIndex, boldStartIndex + boldLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                messageTextView.setText(spannableStringBuilder);
            } else
            {
                messageTextView.setText(message);
            }

            messageLayout.addView(messageRow);
        }

        View agreeLayout = view.findViewById(R.id.agreeLayout);

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

                    mState = STATE_PAYMENT;

                    // 1. 세션이 살아있는지 검사 시작.
                    mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_INFORMATION).toString(), null, mUserInformationJsonResponseListener, GourmetPaymentActivity.this));

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put(AnalyticsManager.Label.PLACE_TICKET_INDEX, String.valueOf(mTicketPayment.getTicketInformation().index));
                    params.put(AnalyticsManager.Label.PLACE_TICKET_NAME, mTicketPayment.getTicketInformation().name);
                    params.put(AnalyticsManager.Label.PLACE_NAME, mTicketPayment.getTicketInformation().placeName);

                    AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Screen.PAYMENT_AGREE_POPUP, AnalyticsManager.Action.CLICK, mTicketPayment.paymentType.name(), params);
                }
            }
        };

        agreeLayout.setOnClickListener(buttonOnClickListener);

        dialog.setContentView(view);

        return dialog;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // User ActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void checkLastChangedValue()
    {
        // 호텔 가격 정보가 변경되었습니다.
        if (mIsChangedPrice == true && mIsChangedTime == true)
        {
            mIsChangedPrice = false;
            mIsChangedTime = true;

            showChangedValueDialog(R.string.dialog_msg_changed_time_price);
        } else if (mIsChangedPrice == true)
        {
            mIsChangedPrice = false;

            showChangedPayDialog();
        } else if (mIsChangedTime == true)
        {
            mIsChangedTime = false;

            showChangedTimeDialog();
        }
    }

    private OnUserActionListener mOnUserActionListener = new OnUserActionListener()
    {
        @Override
        public void plusTicketTime()
        {
            if (mTicketPayment == null || mTicketPayment.ticketTimes == null)
            {
                return;
            }

            int position = 0;
            int length = mTicketPayment.ticketTimes.length;

            for (int i = 0; i < length; i++)
            {
                long selectedtime = mTicketPayment.ticketTimes[i];

                if (selectedtime == mTicketPayment.ticketTime)
                {
                    position = i;
                    break;
                }
            }

            if (++position >= length)
            {
                mGourmetBookingLayout.setTicketTimePlusButtonEnabled(false);
                DailyToast.showToast(GourmetPaymentActivity.this, getString(R.string.toast_msg_none_reservationtime_ticket), Toast.LENGTH_SHORT);
            } else
            {
                mTicketPayment.ticketTime = mTicketPayment.ticketTimes[position];

                mGourmetBookingLayout.setTicketTimeMinusButtonEnabled(true);
                mGourmetBookingLayout.setTicketTime(mTicketPayment.ticketTime);
            }
        }

        @Override
        public void minusTicketTime()
        {
            if (mTicketPayment == null || mTicketPayment.ticketTimes == null)
            {
                return;
            }

            int position = 0;
            int length = mTicketPayment.ticketTimes.length;

            for (int i = 0; i < length; i++)
            {
                long selectedtime = mTicketPayment.ticketTimes[i];

                if (selectedtime == mTicketPayment.ticketTime)
                {
                    position = i;
                    break;
                }
            }

            if (--position < 0)
            {
                mGourmetBookingLayout.setTicketTimeMinusButtonEnabled(false);
            } else
            {
                mTicketPayment.ticketTime = mTicketPayment.ticketTimes[position];

                mGourmetBookingLayout.setTicketTimePlusButtonEnabled(true);
                mGourmetBookingLayout.setTicketTime(mTicketPayment.ticketTime);
            }
        }

        @Override
        public void plusTicketCount()
        {
            int count = mTicketPayment.ticketCount;
            int maxCount = mTicketPayment.ticketMaxCount;

            if (count >= maxCount)
            {
                mGourmetBookingLayout.setTicketCountPlusButtonEnabled(false);
                DailyToast.showToast(GourmetPaymentActivity.this, getString(R.string.toast_msg_maxcount_ticket, maxCount), Toast.LENGTH_SHORT);
            } else
            {
                mTicketPayment.ticketCount = count + 1;
                mGourmetBookingLayout.setTicketCount(mTicketPayment.ticketCount);
                mGourmetBookingLayout.setTicketCountMinusButtonEnabled(true);

                // 결제 가격을 바꾸어야 한다.
                mGourmetBookingLayout.updatePaymentInformationLayout(GourmetPaymentActivity.this, mTicketPayment, mSelectedCreditCard);
            }
        }

        @Override
        public void minusTicketCount()
        {
            int count = mTicketPayment.ticketCount;

            if (count <= 1)
            {
                mGourmetBookingLayout.setTicketCountMinusButtonEnabled(false);
            } else
            {
                mTicketPayment.ticketCount = count - 1;
                mGourmetBookingLayout.setTicketCount(mTicketPayment.ticketCount);
                mGourmetBookingLayout.setTicketCountPlusButtonEnabled(true);

                // 결제 가격을 바꾸어야 한다.
                mGourmetBookingLayout.updatePaymentInformationLayout(GourmetPaymentActivity.this, mTicketPayment, mSelectedCreditCard);
            }
        }

        @Override
        public void editUserInformation()
        {
            if (mIsEditMode == true)
            {
                return;
            }

            mIsEditMode = true;

            if (mGourmetBookingLayout != null)
            {
                mGourmetBookingLayout.enabledEditUserInformation();
            }
        }

        @Override
        public void showCreditCardManager()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            if (mIsEditMode == true)
            {
                // 현재 수정 사항을 기억한다.
                Guest editGuest = mGourmetBookingLayout.getGuest();
                mTicketPayment.setGuest(editGuest);
            }

            Intent intent = new Intent(GourmetPaymentActivity.this, CreditCardListActivity.class);
            intent.setAction(Intent.ACTION_PICK);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_CREDITCARD, mSelectedCreditCard);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CREDITCARD_MANAGER);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        }

        @Override
        public void setPaymentType(TicketPayment.PaymentType type)
        {
            checkPaymentType(type);
        }

        @Override
        public void pay()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            // 수정 모드인 경우 데이터를 다시 받아와야 한다.
            if (mIsEditMode == true)
            {
                Guest guest = mGourmetBookingLayout.getGuest();

                if (Util.isTextEmpty(guest.name) == true)
                {
                    releaseUiComponent();

                    mGourmetBookingLayout.requestUserInformationFocus(UserInformationType.NAME);

                    DailyToast.showToast(GourmetPaymentActivity.this, R.string.toast_msg_please_input_guest, Toast.LENGTH_SHORT);
                    return;
                } else if (Util.isTextEmpty(guest.phone) == true)
                {
                    releaseUiComponent();

                    mGourmetBookingLayout.requestUserInformationFocus(UserInformationType.PHONE);

                    DailyToast.showToast(GourmetPaymentActivity.this, R.string.toast_msg_please_input_contact, Toast.LENGTH_SHORT);
                    return;
                } else if (Util.isTextEmpty(guest.email) == true)
                {
                    releaseUiComponent();

                    mGourmetBookingLayout.requestUserInformationFocus(UserInformationType.EMAIL);

                    DailyToast.showToast(GourmetPaymentActivity.this, R.string.toast_msg_please_input_email, Toast.LENGTH_SHORT);
                    return;
                } else if (android.util.Patterns.EMAIL_ADDRESS.matcher(guest.email).matches() == false)
                {
                    releaseUiComponent();

                    mGourmetBookingLayout.requestUserInformationFocus(UserInformationType.EMAIL);

                    DailyToast.showToast(GourmetPaymentActivity.this, R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
                    return;
                }

                mTicketPayment.setGuest(guest);
            }

            String gcmId = sharedPreference.getString(KEY_PREFERENCE_GCM_ID, "");

            if (mTicketPayment.paymentType == TicketPayment.PaymentType.VBANK && Util.isTextEmpty(gcmId) == true)
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
                        processValidatePayment();
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
                processValidatePayment();
            }
        }

        @Override
        public void showCallDialog()
        {
            GourmetPaymentActivity.this.showCallDialog();
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Network Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mTicketSellCheckJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                if (response == null)
                {
                    onInternalError();
                    return;
                }

                JSONObject jsonObject = response.getJSONObject("data");

                boolean isOnSale = jsonObject.getBoolean("on_sale");

                int msg_code = response.getInt("msg_code");

                if (isOnSale == true && msg_code == 0)
                {
                    mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_SESSION_BILLING_CARD_INFO).toString(), null, mUserSessionBillingCardInfoJsonResponseListener, GourmetPaymentActivity.this));
                } else
                {
                    if (response.has("msg") == true)
                    {
                        String msg = response.getString("msg");

                        showSimpleDialog(null, msg, getString(R.string.dialog_btn_text_confirm), null, new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                finish();
                            }
                        }, null, false);
                        return;
                    } else
                    {
                        onInternalError();
                    }
                }
            } catch (Exception e)
            {
                onInternalError();
            }
        }
    };
    private DailyHotelJsonResponseListener mTicketPaymentInformationJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                if (response == null)
                {
                    onInternalError();
                    return;
                }

                int msg_code = response.getInt("msg_code");

                if (msg_code == 0)
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

                    if (mTicketPayment.ticketTime == 0)
                    {
                        mTicketPayment.ticketTime = times[0];

                    } else
                    {
                        boolean isExistTime = false;

                        for (int i = 0; i < length; i++)
                        {
                            times[i] = timeJSONArray.getLong(i);

                            if (mTicketPayment.ticketTime == times[i])
                            {
                                isExistTime = true;
                            }
                        }

                        // 시간 값이 없어진 경우
                        if (isExistTime == false)
                        {
                            mIsChangedTime = true;
                        }
                    }

                    mTicketPayment.ticketTimes = times;

                    switch (mState)
                    {
                        case STATE_NONE:
                        {
                            // 가격이 변동 되었다.
                            if (mTicketPayment.getTicketInformation().discountPrice != discountPrice)
                            {
                                mIsChangedPrice = true;
                            }

                            mTicketPayment.getTicketInformation().discountPrice = discountPrice;
                            mTicketPayment.ticketMaxCount = maxCount;

                            Calendar calendarCheckin = DailyCalendar.getInstance();
                            calendarCheckin.setTimeZone(TimeZone.getTimeZone("GMT"));
                            calendarCheckin.setTimeInMillis(sday);

                            SimpleDateFormat formatDay = new SimpleDateFormat("yyyy.MM.dd (EEE)", Locale.KOREA);
                            formatDay.setTimeZone(TimeZone.getTimeZone("GMT"));

                            mTicketPayment.checkInTime = formatDay.format(calendarCheckin.getTime());

                            requestValidateTicketPayment(mTicketPayment, mCheckInSaleTime);
                            break;
                        }

                        case STATE_PAYMENT:
                        {
                            TicketInformation ticketInformation = mTicketPayment.getTicketInformation();

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

                                showChangedPayDialog();
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
                                processPayment();
                            }
                            break;
                        }
                    }
                } else
                {
                    if (response.has("msg") == true)
                    {
                        String msg = response.getString("msg");

                        showSimpleDialog(null, msg, getString(R.string.dialog_btn_text_confirm), null, new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                finish();
                            }
                        }, null, false);
                        return;
                    } else
                    {
                        onInternalError();
                    }
                }
            } catch (Exception e)
            {
                onInternalError();
            }
        }
    };
    private DailyHotelJsonResponseListener mPayEasyPaymentJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                if (response == null)
                {
                    onInternalError();
                    return;
                }

                int msg_code = response.getInt("msg_code");

                mState = STATE_NONE;

                hidePorgressDialog();

                if (msg_code == 0)
                {
                    // 결제 관련 로그 남기기
                    writeLogPaid(mTicketPayment);

                    showSimpleDialog(getString(R.string.dialog_title_payment), getString(R.string.act_toast_payment_success), getString(R.string.dialog_btn_text_confirm), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                } else
                {
                    int resultCode = 0;
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

                    activityResulted(CODE_REQUEST_ACTIVITY_PAYMENT, resultCode, intent);
                }
            } catch (Exception e)
            {
                onInternalError();
            }
        }
    };
}
