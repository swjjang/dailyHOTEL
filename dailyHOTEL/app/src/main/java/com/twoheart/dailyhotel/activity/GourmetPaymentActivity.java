package com.twoheart.dailyhotel.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.model.Guest;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.TicketInformation;
import com.twoheart.dailyhotel.model.TicketPayment;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.view.FinalCheckLayout;
import com.twoheart.dailyhotel.view.GourmetBookingLayout;
import com.twoheart.dailyhotel.view.GourmetBookingLayout.UserInformationType;
import com.twoheart.dailyhotel.view.widget.DailySignatureView;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.DailyToolbarLayout;

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
    private static final int REQUEST_CODE_COUNTRYCODE_DIALOG_ACTIVITY = 10000;

    private GourmetBookingLayout mGourmetBookingLayout;
    private boolean mIsChangedTime;

    public interface OnUserActionListener
    {
        void selectTicketTime(String selectedTime);

        void plusTicketCount();

        void minusTicketCount();

        void editUserInformation();

        void showCreditCardManager();

        void setPaymentType(TicketPayment.PaymentType type);

        void pay();

        void showCallDialog();

        void showInputMobileNumberDialog(String mobileNumber);
    }

    public static Intent newInstance(Context context, TicketInformation ticketInformation, SaleTime checkInSaleTime, String category, int gourmetIndex, boolean isDBenefit)
    {
        Intent intent = new Intent(context, GourmetPaymentActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_TICKETINFORMATION, ticketInformation);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, checkInSaleTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_GOURMETIDX, gourmetIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CATEGORY, category);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DBENEFIT, isDBenefit);

        return intent;
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
            mTicketPayment.placeIndex = bundle.getInt(NAME_INTENT_EXTRA_DATA_GOURMETIDX);
            mTicketPayment.category = bundle.getString(NAME_INTENT_EXTRA_DATA_CATEGORY);
            mTicketPayment.isDBenefit = bundle.getBoolean(NAME_INTENT_EXTRA_DATA_DBENEFIT);
        }

        if (mTicketPayment.getTicketInformation() == null)
        {
            finish();
            return;
        }

        mIsChangedPrice = false;
        mDoReload = true;

        initToolbar(mTicketPayment.getTicketInformation().placeName);
    }

    private void initToolbar(String title)
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(title);
        dailyToolbarLayout.setToolbarRegionMenu(R.drawable.navibar_ic_call, -1);
        dailyToolbarLayout.setToolbarMenuClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                showCallDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        try
        {
            unLockUI();

            if (requestCode == REQUEST_CODE_COUNTRYCODE_DIALOG_ACTIVITY)
            {
                if (resultCode == RESULT_OK && intent != null)
                {
                    String mobileNumber = intent.getStringExtra(InputMobileNumberDialogActivity.INTENT_EXTRA_MOBILE_NUMBER);

                    mTicketPayment.getGuest().phone = mobileNumber;

                    mGourmetBookingLayout.updateUserInformationLayout(mobileNumber);
                }

                return;
            }

            super.onActivityResult(requestCode, resultCode, intent);
        } catch (NullPointerException e)
        {
            ExLog.e(e.toString());

            Util.restartApp(this);
        }
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
            ticketPayment.getTicketInformation().index, checkInSaleTime.getDayOfDaysDateFormat("yyMMdd"), ticketPayment.ticketCount, String.valueOf(ticketPayment.ticketTime));

        DailyNetworkAPI.getInstance().requestGourmetCheckTicket(mNetworkTag, params, mTicketSellCheckJsonResponseListener, this);
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
        params.put("customer_phone", guest.phone.replace("-", ""));
        params.put("customer_email", guest.email);
        params.put("arrival_time", String.valueOf(ticketPayment.ticketTime));

        //        if (DEBUG == true)
        //        {
        //            showSimpleDialog(null, params.toString(), getString(R.string.dialog_btn_text_confirm), null);
        //        }

        DailyNetworkAPI.getInstance().requestGourmetPayment(mNetworkTag, params, mPayEasyPaymentJsonResponseListener, this);
    }

    @Override
    protected void requestTicketPaymentInfomation(int index)
    {
        if (index < 0)
        {
            mDoReload = true;

            onInternalError();
            return;
        }

        String params = String.format("?sale_reco_idx=%d", index);
        DailyNetworkAPI.getInstance().requestGourmetPaymentInformation(mNetworkTag, params, mTicketPaymentInformationJsonResponseListener, this);
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

        int[] messageResIds = {R.string.dialog_msg_gourmet_payment_message01, R.string.dialog_msg_gourmet_payment_message02, R.string.dialog_msg_gourmet_payment_message03, R.string.dialog_msg_hotel_payment_message08};

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
                            DailyNetworkAPI.getInstance().requestUserInformationForPayment(mNetworkTag, mUserInformationJsonResponseListener, GourmetPaymentActivity.this);

                            mFinalCheckDialog.dismiss();

                            AnalyticsManager.getInstance(GourmetPaymentActivity.this).recordEvent(AnalyticsManager.Category.POPUPBOXES//
                                , AnalyticsManager.Action.PAYMENT_AGREEMENT_POPPEDUP, AnalyticsManager.Label.AGREE, null);
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

        mFinalCheckDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                mDoReload = true;

                AnalyticsManager.getInstance(GourmetPaymentActivity.this).recordEvent(AnalyticsManager.Category.POPUPBOXES//
                    , AnalyticsManager.Action.PAYMENT_AGREEMENT_POPPEDUP, AnalyticsManager.Label.CANCEL, null);
            }
        });

        try
        {
            mFinalCheckDialog.show();

            AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_PAYMENT_AGREEMENT_POPUP//
                , getMapPaymentInformation(mTicketPayment));
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
                    mDoReload = false;

                    // 1. 세션이 살아있는지 검사 시작.
                    DailyNetworkAPI.getInstance().requestUserInformationForPayment(mNetworkTag, mUserInformationJsonResponseListener, GourmetPaymentActivity.this);

                    AnalyticsManager.getInstance(GourmetPaymentActivity.this).recordEvent(AnalyticsManager.Category.POPUPBOXES//
                        , AnalyticsManager.Action.PAYMENT_AGREEMENT_POPPEDUP, AnalyticsManager.Label.AGREE, null);
                }
            }
        };

        agreeLayout.setOnClickListener(buttonOnClickListener);

        dialog.setContentView(view);

        return dialog;
    }

    private void recordAnalyticsPayment(TicketPayment ticketPayment)
    {
        if (ticketPayment == null)
        {
            return;
        }

        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.NAME, ticketPayment.getTicketInformation().placeName);
            params.put(AnalyticsManager.KeyType.PRICE, Integer.toString(ticketPayment.getTicketInformation().discountPrice));
            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(ticketPayment.placeIndex));
            params.put(AnalyticsManager.KeyType.DATE, mCheckInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.TICKET_NAME, ticketPayment.getTicketInformation().name);
            params.put(AnalyticsManager.KeyType.TICKET_INDEX, Integer.toString(ticketPayment.getTicketInformation().index));
            params.put(AnalyticsManager.KeyType.CATEGORY, ticketPayment.category);
            params.put(AnalyticsManager.KeyType.DBENEFIT, ticketPayment.isDBenefit ? "yes" : "no");

            AnalyticsManager.getInstance(GourmetPaymentActivity.this).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_PAYMENT, params);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
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
        public void selectTicketTime(String selectedTime)
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Dialog dialog = Util.showDatePickerDialog(GourmetPaymentActivity.this, getString(R.string.label_booking_select_ticket_time), mTicketPayment.getTicketTimes(), selectedTime, getString(R.string.dialog_btn_text_confirm), new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int select = (Integer) v.getTag();

                    try
                    {
                        mTicketPayment.ticketTime = mTicketPayment.ticketTimes[select];
                        mGourmetBookingLayout.setTicketTime(mTicketPayment.ticketTime);
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());

                        onInternalError();
                    }
                }
            });

            if (dialog != null)
            {
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        releaseUiComponent();
                    }
                });
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
                DailyToast.showToast(GourmetPaymentActivity.this, getString(R.string.toast_msg_maxcount_ticket, maxCount), Toast.LENGTH_LONG);
            } else
            {
                mTicketPayment.ticketCount = count + 1;
                mGourmetBookingLayout.setTicketCount(mTicketPayment.ticketCount);
                mGourmetBookingLayout.setTicketCountMinusButtonEnabled(true);

                // 결제 가격을 바꾸어야 한다.
                mGourmetBookingLayout.updatePaymentInformationLayout(GourmetPaymentActivity.this, mTicketPayment);
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
                mGourmetBookingLayout.updatePaymentInformationLayout(GourmetPaymentActivity.this, mTicketPayment);
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

            AnalyticsManager.getInstance(GourmetPaymentActivity.this).recordEvent(AnalyticsManager.Category.GOURMETBOOKINGS//
                , AnalyticsManager.Action.EDIT_BUTTON_CLICKED, AnalyticsManager.Label.PAYMENT_CARD_EDIT, null);

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

            if (mTicketPayment.ticketTime == 0)
            {
                releaseUiComponent();
                mGourmetBookingLayout.scrollTop();

                DailyToast.showToast(GourmetPaymentActivity.this, R.string.toast_msg_please_select_reservationtime, Toast.LENGTH_SHORT);
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

            if (mTicketPayment.paymentType == TicketPayment.PaymentType.VBANK && DailyPreference.getInstance(GourmetPaymentActivity.this).getNotificationUid() < 0)
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

            String label = String.format("%s_%s", mTicketPayment.getTicketInformation().placeName, mTicketPayment.getTicketInformation().name);
            AnalyticsManager.getInstance(GourmetPaymentActivity.this).recordEvent(AnalyticsManager.Category.GOURMETBOOKINGS//
                , AnalyticsManager.Action.PAYMENT_CLICKED, label, null);
        }

        @Override
        public void showCallDialog()
        {
            GourmetPaymentActivity.this.showCallDialog();
        }

        @Override
        public void showInputMobileNumberDialog(String mobileNumber)
        {
            mTicketPayment.setGuest(mGourmetBookingLayout.getGuest());

            Intent intent = InputMobileNumberDialogActivity.newInstance(GourmetPaymentActivity.this, mobileNumber);
            startActivityForResult(intent, REQUEST_CODE_COUNTRYCODE_DIALOG_ACTIVITY);
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
                    switch (mState)
                    {
                        case STATE_REGISTER_CREDIT_CARD:
                            DailyNetworkAPI.getInstance().requestUserBillingCardList(mNetworkTag, mUserRegisterBillingCardInfoJsonResponseListener, GourmetPaymentActivity.this);
                            break;

                        default:
                            DailyNetworkAPI.getInstance().requestUserBillingCardList(mNetworkTag, mUserSessionBillingCardInfoJsonResponseListener, GourmetPaymentActivity.this);
                            break;
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

    private DailyHotelJsonResponseListener mTicketPaymentInformationJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                if (response == null)
                {
                    mDoReload = true;

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

                    mTicketPayment.startTicketTime = jsonObject.getLong("start_eating_time");
                    mTicketPayment.endTicketTime = jsonObject.getLong("end_eating_time");

                    JSONArray timeJSONArray = jsonObject.getJSONArray("eating_time_list");

                    int length = timeJSONArray.length();
                    long[] times = new long[length];

                    for (int i = 0; i < length; i++)
                    {
                        times[i] = timeJSONArray.getLong(i);
                    }

                    if (mTicketPayment.ticketTime == 0)
                    {

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

                        default:
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

                            if (mTicketPayment.ticketTime == 0)
                            {
                                // 방문시간을 선택하지 않은 경우
                                DailyNetworkAPI.getInstance().requestUserBillingCardList(mNetworkTag, mUserSessionBillingCardInfoJsonResponseListener, GourmetPaymentActivity.this);
                            } else
                            {
                                requestValidateTicketPayment(mTicketPayment, mCheckInSaleTime);
                            }

                            recordAnalyticsPayment(mTicketPayment);
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
                mDoReload = true;

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
                    mDoReload = true;

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

                    showSimpleDialog(getString(R.string.dialog_title_payment), getString(R.string.act_toast_payment_success), getString(R.string.dialog_btn_text_confirm), null, new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            mDoReload = true;

                            setResult(RESULT_OK);
                            finish();
                        }
                    }, null, false);
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
                mDoReload = true;

                onInternalError();
            }
        }
    };
}
