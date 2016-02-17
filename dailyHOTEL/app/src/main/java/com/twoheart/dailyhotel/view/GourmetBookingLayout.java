/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * BookingActivity (예약 화면)
 * <p>
 * 결제 화면으로 넘어가기 전 예약 정보를 보여주고 결제방식을 선택할 수 있는 화면
 */
package com.twoheart.dailyhotel.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.activity.GourmetPaymentActivity;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.model.Guest;
import com.twoheart.dailyhotel.model.TicketInformation;
import com.twoheart.dailyhotel.model.TicketPayment;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.StringFilter;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class GourmetBookingLayout implements OnCheckedChangeListener
{
    private BaseActivity mActivity;

    private ViewGroup mViewGroupRoot;
    private View mUserInformation;
    private View mTicketInformation;
    private TextView mTicketCountTextView, mTicketTimeTextView;
    private EditText mUserNameEditText, mUserPhoneEditText, mUserEmailEditText;
    private Drawable[] mEditTextBackgrounds;

    private TextView mTicketPaymentTextView;
    private RadioGroup mPaymentGroup;
    private RadioButton mEasyPaymentButton, mCardPaymentButton, mHpPaymentButton, mAccountPaymentButton;
    private View mCardManagerButton;
    private View mTicketCountMinusButton, mTicketCountPlusButton;

    private ScrollView mScrollLayout;

    private GourmetPaymentActivity.OnUserActionListener mOnUserActionListener;

    public GourmetBookingLayout(BaseActivity activity, GourmetPaymentActivity.OnUserActionListener listener)
    {
        mActivity = activity;
        mOnUserActionListener = listener;

        initLayout(activity);
    }

    private void initLayout(BaseActivity activity)
    {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewGroupRoot = (ViewGroup) inflater.inflate(R.layout.activity_booking_place, null, false);

        mScrollLayout = (ScrollView) mViewGroupRoot.findViewById(R.id.scrollLayout);
        ViewGroup bookingLayout = (ViewGroup) mViewGroupRoot.findViewById(R.id.bookingLayout);

        // 결제 정보 넣기
        View paymentInformation = inflater.inflate(R.layout.layout_booking_gourmet_payment_information, null, false);
        bookingLayout.addView(paymentInformation, 0);

        initPaymentInformationLayout(activity, paymentInformation);

        // 예약자 정보 넣기
        mUserInformation = inflater.inflate(R.layout.layout_booking_gourmet_user_information, null, false);
        bookingLayout.addView(mUserInformation, 0);

        initUserInformationLayout(activity, mUserInformation);

        // 상품 정보 넣기
        mTicketInformation = inflater.inflate(R.layout.layout_booking_gourmet_ticket_information, null, false);
        bookingLayout.addView(mTicketInformation, 0);

        initTicketInformationLayout(activity, mTicketInformation);

        View payButton = mViewGroupRoot.findViewById(R.id.payButton);
        payButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.pay();
                }
            }
        });
    }

    public ViewGroup getLayout()
    {
        return mViewGroupRoot;
    }

    public void updateTicketPaymentInformation(TicketPayment ticketPayment, CreditCard creditCard)
    {
        // 상품정보
        updateTicketInformationLayout(mActivity, ticketPayment);

        // 예약자 정보
        updateUserInformationLayout(ticketPayment);

        // 결제 정보
        updatePaymentInformationLayout(mActivity, ticketPayment, creditCard);
    }

    private void initTicketInformationLayout(BaseActivity activity, View viewRoot)
    {
        if (activity == null || viewRoot == null)
        {
            return;
        }

        // 타입
        //		TextView ticketTypeTextView = (TextView) viewRoot.findViewById(R.id.ticketTypeTextView);

        // 날짜
        //		TextView ticketDateTextView = (TextView) viewRoot.findViewById(R.id.ticketDateTextView);


        // 방문 시간
        mTicketTimeTextView = (TextView) viewRoot.findViewById(R.id.ticketTimeTextView);
        mTicketTimeTextView.setText(R.string.label_booking_select);
        mTicketTimeTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.selectTicketTime(mTicketTimeTextView.getText().toString());
                }
            }
        });

        View ticketTimeTab = viewRoot.findViewById(R.id.ticketTimeTab);
        ticketTimeTab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mTicketTimeTextView.performClick();
            }
        });

        // 수량
        mTicketCountTextView = (TextView) viewRoot.findViewById(R.id.ticketCountTextView);

        mTicketCountMinusButton = viewRoot.findViewById(R.id.ticketCountMinus);
        mTicketCountPlusButton = viewRoot.findViewById(R.id.ticketCountPlus);

        setTicketCountMinusButtonEnabled(false);
        setTicketCountPlusButtonEnabled(true);

        mTicketCountMinusButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.minusTicketCount();
                }
            }
        });

        mTicketCountPlusButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.plusTicketCount();
                }
            }
        });
    }

    public void updateTicketInformationLayout(BaseActivity activity, TicketPayment ticketPayment)
    {
        if (activity == null || ticketPayment == null)
        {
            return;
        }

        TicketInformation ticketInformation = ticketPayment.getTicketInformation();

        if (ticketInformation == null)
        {
            return;
        }

        // 타입
        TextView ticketTypeTextView = (TextView) mTicketInformation.findViewById(R.id.ticketTypeTextView);
        ticketTypeTextView.setText(ticketInformation.name);

        // 날짜
        TextView ticketDateTextView = (TextView) mTicketInformation.findViewById(R.id.ticketDateTextView);
        ticketDateTextView.setText(ticketPayment.checkInTime);

        if (ticketPayment.ticketTime != 0)
        {
            Calendar calendarTime = DailyCalendar.getInstance();
            calendarTime.setTimeZone(TimeZone.getTimeZone("GMT"));
            calendarTime.setTimeInMillis(ticketPayment.ticketTime);

            SimpleDateFormat formatDay = new SimpleDateFormat("HH:mm", Locale.KOREA);
            formatDay.setTimeZone(TimeZone.getTimeZone("GMT"));

            // 방문시간
            mTicketTimeTextView.setText(formatDay.format(calendarTime.getTime()));
        }

        // 수량
        mTicketCountTextView.setText(activity.getString(R.string.label_booking_count, ticketPayment.ticketCount));
    }

    private void initUserInformationLayout(BaseActivity activity, View viewRoot)
    {
        if (activity == null || viewRoot == null)
        {
            return;
        }

        // 예약자
        mUserNameEditText = (EditText) viewRoot.findViewById(R.id.userNameEditText);

        // 연락처
        mUserPhoneEditText = (EditText) viewRoot.findViewById(R.id.userPhoneEditText);

        // 이메일
        mUserEmailEditText = (EditText) viewRoot.findViewById(R.id.userEmailEditText);

        mEditTextBackgrounds = new Drawable[3];
        mEditTextBackgrounds[0] = mUserNameEditText.getBackground();
        mEditTextBackgrounds[1] = mUserPhoneEditText.getBackground();
        mEditTextBackgrounds[2] = mUserEmailEditText.getBackground();

        mUserNameEditText.setBackgroundResource(0);
        mUserPhoneEditText.setBackgroundResource(0);
        mUserEmailEditText.setBackgroundResource(0);

        mUserNameEditText.setEnabled(false);
        mUserPhoneEditText.setEnabled(false);
        mUserEmailEditText.setEnabled(false);

        mUserPhoneEditText.setCursorVisible(false);

        // 수정
        View editLayout = viewRoot.findViewById(R.id.editLinearLayout);
        editLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.setVisibility(View.INVISIBLE);

                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.editUserInformation();
                }
            }
        });
    }

    private void updateUserInformationLayout(TicketPayment ticketPayment)
    {
        if (ticketPayment == null)
        {
            return;
        }

        Guest guest = ticketPayment.getGuest();

        if (guest == null)
        {
            return;
        }

        // 예약자
        mUserNameEditText.setText(guest.name);

        // 연락처
        mUserPhoneEditText.setText(Util.addHippenMobileNumber(mActivity, guest.phone));

        // 이메일
        mUserEmailEditText.setText(guest.email);
    }

    /**
     * @param mobileNumber
     */
    public void updateUserInformationLayout(String mobileNumber)
    {
        mUserPhoneEditText.setText(Util.addHippenMobileNumber(mActivity, mobileNumber));
    }

    private void initPaymentInformationLayout(BaseActivity activity, View viewRoot)
    {
        if (activity == null || viewRoot == null)
        {
            return;
        }

        // 결제금액
        mTicketPaymentTextView = (TextView) viewRoot.findViewById(R.id.ticketPaymentTextView);

        // 라디오 그룹
        mPaymentGroup = (RadioGroup) viewRoot.findViewById(R.id.paymentRadioGroup);

        mEasyPaymentButton = (RadioButton) viewRoot.findViewById(R.id.easyPaymentRadioButton);
        mCardPaymentButton = (RadioButton) viewRoot.findViewById(R.id.cardPaymentRadionButton);
        mHpPaymentButton = (RadioButton) viewRoot.findViewById(R.id.hpPaymentRadioButton);
        mAccountPaymentButton = (RadioButton) viewRoot.findViewById(R.id.accountPaymentRadioButton);

        mPaymentGroup.setOnCheckedChangeListener(this);

        // 간편결제 관리
        mCardManagerButton = viewRoot.findViewById(R.id.cardManagerButton);
        mCardManagerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.showCreditCardManager();
                }
            }
        });

        mPaymentGroup.check(mEasyPaymentButton.getId());
        mEasyPaymentButton.setText(R.string.label_booking_easypayment);
    }

    public void updatePaymentInformationLayout(BaseActivity activity, TicketPayment ticketPayment, CreditCard creditCard)
    {
        if (activity == null || ticketPayment == null)
        {
            return;
        }

        DecimalFormat comma = new DecimalFormat("###,##0");
        String price = comma.format(ticketPayment.getPaymentToPay()) + Html.fromHtml(activity.getString(R.string.currency));

        // 결제금액
        mTicketPaymentTextView.setText(price);

        // 라디오 그룹

        // 간편결제 관리
        if (creditCard == null)
        {
            // 카드 관리 관련 화면을 보여주지 않는다.
            mCardManagerButton.setVisibility(View.INVISIBLE);
            mEasyPaymentButton.setText(R.string.label_booking_easypayment);
        } else
        {
            mCardManagerButton.setVisibility(View.VISIBLE);
            mEasyPaymentButton.setText(String.format("%s %s", creditCard.name.replace("카드", ""), creditCard.number));
        }
    }

    public void updatePaymentInformationLayout(BaseActivity activity, TicketPayment ticketPayment)
    {
        if (activity == null || ticketPayment == null)
        {
            return;
        }

        DecimalFormat comma = new DecimalFormat("###,##0");
        String price = comma.format(ticketPayment.getPaymentToPay()) + Html.fromHtml(activity.getString(R.string.currency));

        // 결제금액
        mTicketPaymentTextView.setText(price);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId)
    {
        if (mOnUserActionListener == null)
        {
            return;
        }

        if (checkedId == mEasyPaymentButton.getId())
        {
            mOnUserActionListener.setPaymentType(TicketPayment.PaymentType.EASY_CARD);
        } else if (checkedId == mCardPaymentButton.getId())
        {
            mOnUserActionListener.setPaymentType(TicketPayment.PaymentType.CARD);
        } else if (checkedId == mHpPaymentButton.getId())
        {
            mOnUserActionListener.setPaymentType(TicketPayment.PaymentType.PHONE_PAY);
        } else if (checkedId == mAccountPaymentButton.getId())
        {
            mOnUserActionListener.setPaymentType(TicketPayment.PaymentType.VBANK);
        }
    }

    public void setTicketCount(int count)
    {
        if (mTicketCountTextView == null)
        {
            return;
        }

        mTicketCountTextView.setText(mActivity.getString(R.string.label_booking_count, count));
    }

    public void setTicketCountMinusButtonEnabled(boolean isEnabled)
    {
        setButtonVisibility(mTicketCountMinusButton, isEnabled);
    }

    public void setTicketCountPlusButtonEnabled(boolean isEnabled)
    {
        setButtonVisibility(mTicketCountPlusButton, isEnabled);
    }

    //    public void setTicketTimeMinusButtonEnabled(boolean isEnabled)
    //    {
    //        setButtonVisibility(mTicketTimeMinusButton, isEnabled);
    //    }
    //
    //    public void setTicketTimePlusButtonEnabled(boolean isEnabled)
    //    {
    //        setButtonVisibility(mTicketTimePlusButton, isEnabled);
    //    }

    private void setButtonVisibility(View view, boolean isEnabled)
    {
        if (view == null)
        {
            return;
        }

        view.setEnabled(isEnabled);
    }

    public void setTicketTime(long time)
    {
        if (mTicketTimeTextView == null)
        {
            return;
        }

        Calendar calendarTime = DailyCalendar.getInstance();
        calendarTime.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendarTime.setTimeInMillis(time);

        SimpleDateFormat formatDay = new SimpleDateFormat("HH:mm", Locale.KOREA);
        formatDay.setTimeZone(TimeZone.getTimeZone("GMT"));

        mTicketTimeTextView.setText(formatDay.format(calendarTime.getTime()));
    }

    public Guest getGuest()
    {
        Guest guest = new Guest();

        guest.name = mUserNameEditText.getText().toString().trim();
        guest.phone = mUserPhoneEditText.getText().toString().trim();
        guest.email = mUserEmailEditText.getText().toString().trim();

        return guest;
    }

    public void requestUserInformationFocus(UserInformationType type)
    {
        switch (type)
        {
            case NAME:
                mUserNameEditText.requestFocus();
                break;

            case PHONE:
                mUserPhoneEditText.requestFocus();
                break;

            case EMAIL:
                mUserEmailEditText.requestFocus();
                break;
        }
    }

    public void enabledEditUserInformation()
    {
        if (mUserNameEditText != null && mUserNameEditText.isEnabled() == false)
        {
            mUserNameEditText.setEnabled(true);

            // 회원 가입시 이름 필터 적용.
            StringFilter stringFilter = new StringFilter(mActivity);
            InputFilter[] allowAlphanumericHangul = new InputFilter[1];
            allowAlphanumericHangul[0] = stringFilter.allowAlphanumericHangul;

            mUserNameEditText.setFilters(allowAlphanumericHangul);
            mUserNameEditText.setInputType(InputType.TYPE_CLASS_TEXT);

            if (Util.isOverAPI16() == true)
            {
                mUserNameEditText.setBackground(mEditTextBackgrounds[0]);
            } else
            {
                mUserNameEditText.setBackgroundDrawable(mEditTextBackgrounds[0]);
            }
        }

        if (mUserPhoneEditText != null && mUserPhoneEditText.isEnabled() == false)
        {
            mUserPhoneEditText.setEnabled(true);

            if (Util.isOverAPI16() == true)
            {
                mUserPhoneEditText.setBackground(mEditTextBackgrounds[1]);
            } else
            {
                mUserPhoneEditText.setBackgroundDrawable(mEditTextBackgrounds[1]);
            }

            mUserPhoneEditText.setOnFocusChangeListener(new View.OnFocusChangeListener()
            {
                @Override
                public void onFocusChange(View v, boolean hasFocus)
                {
                    if (hasFocus == true)
                    {
                        if (mOnUserActionListener != null)
                        {
                            mOnUserActionListener.showInputMobileNumberDialog(mUserPhoneEditText.getText().toString());
                        }
                    } else
                    {
                        mUserPhoneEditText.setSelected(false);
                    }
                }
            });

            View fakeMobileEditView = mUserInformation.findViewById(R.id.fakeMobileEditView);

            fakeMobileEditView.setFocusable(true);
            fakeMobileEditView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mUserPhoneEditText.isSelected() == true)
                    {
                        if (mOnUserActionListener != null)
                        {
                            mOnUserActionListener.showInputMobileNumberDialog(mUserPhoneEditText.getText().toString());
                        }
                    } else
                    {
                        mUserPhoneEditText.requestFocus();
                        mUserPhoneEditText.setSelected(true);
                    }
                }
            });
        }

        if (mUserEmailEditText != null && mUserEmailEditText.isEnabled() == false)
        {
            mUserEmailEditText.setEnabled(true);

            if (Util.isOverAPI16() == true)
            {
                mUserEmailEditText.setBackground(mEditTextBackgrounds[2]);
            } else
            {
                mUserEmailEditText.setBackgroundDrawable(mEditTextBackgrounds[2]);
            }

            mUserEmailEditText.setOnEditorActionListener(new OnEditorActionListener()
            {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
                {
                    if (actionId == EditorInfo.IME_ACTION_DONE)
                    {
                        textView.clearFocus();

                        if (mActivity.getWindow() == null || mActivity.getWindow().getDecorView() == null || mActivity.getWindow().getDecorView().getWindowToken() == null)
                        {
                            return false;
                        }

                        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                        return true;
                    } else
                    {
                        return false;
                    }
                }
            });
        }
    }

    public void checkPaymentType(TicketPayment.PaymentType type)
    {
        if (mPaymentGroup == null || type == null)
        {
            return;
        }

        switch (type)
        {
            case EASY_CARD:
                mPaymentGroup.check(mEasyPaymentButton.getId());
                break;

            case CARD:
                mPaymentGroup.check(mCardPaymentButton.getId());
                break;

            case PHONE_PAY:
                mPaymentGroup.check(mHpPaymentButton.getId());
                break;

            case VBANK:
                mPaymentGroup.check(mAccountPaymentButton.getId());
                break;
        }

        AnalyticsManager.getInstance(mActivity).recordEvent(AnalyticsManager.Category.GOURMETBOOKINGS//
            , AnalyticsManager.Action.PAYMENT_TYPE_ITEM_CLICKED, type.getName(), null);
    }

    public void scrollTop()
    {
        if (mScrollLayout == null)
        {
            return;
        }

        mScrollLayout.scrollTo(0, 0);
    }


    public enum UserInformationType
    {
        NAME,
        PHONE,
        EMAIL
    }

    private class TelophoneClickSpannable extends ClickableSpan
    {
        private Context mContext;
        private GourmetPaymentActivity.OnUserActionListener mOnUserActionListener;

        public TelophoneClickSpannable(Context context, GourmetPaymentActivity.OnUserActionListener listener)
        {
            mContext = context;
            mOnUserActionListener = listener;
        }

        @Override
        public void updateDrawState(TextPaint textPain)
        {
            textPain.setColor(mContext.getResources().getColor(R.color.booking_tel_link));
            textPain.setFakeBoldText(true);
            textPain.setUnderlineText(true);
        }

        @Override
        public void onClick(View widget)
        {
            if (mOnUserActionListener != null)
            {
                mOnUserActionListener.showCallDialog();
            }
        }
    }
}
