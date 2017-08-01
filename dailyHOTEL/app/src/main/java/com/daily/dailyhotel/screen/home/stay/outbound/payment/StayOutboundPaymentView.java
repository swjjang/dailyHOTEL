package com.daily.dailyhotel.screen.home.stay.outbound.payment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.support.v4.view.MotionEventCompat;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyScrollView;
import com.daily.dailyhotel.entity.Card;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.view.DailyBookingAgreementThirdPartyView;
import com.daily.dailyhotel.view.DailyBookingPaymentTypeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundPaymentDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundDetailInformationDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundPaymentBookingDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundPaymentButtonDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundPaymentDiscountDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundPaymentPayDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundPaymentRefundDataBinding;
import com.twoheart.dailyhotel.screen.common.FinalCheckLayout;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailySignatureView;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StayOutboundPaymentView extends BaseDialogView<StayOutboundPaymentView.OnEventListener, ActivityStayOutboundPaymentDataBinding>//
    implements StayOutboundPaymentInterface, View.OnClickListener, View.OnFocusChangeListener
{
    private DailyToolbarLayout mDailyToolbarLayout;

    private LayoutStayOutboundPaymentBookingDataBinding mBookingDataBinding;
    private LayoutStayOutboundPaymentDiscountDataBinding mDiscountDataBinding;
    private LayoutStayOutboundPaymentPayDataBinding mPayDataBinding;
    private LayoutStayOutboundPaymentRefundDataBinding mRefundDataBinding;
    private LayoutStayOutboundPaymentButtonDataBinding mButtonDataBinding;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onCallClick();

        void onBonusClick(boolean enabled);

        void onEasyCardManagerClick();

        void onRegisterEasyCardClick();

        void onPaymentTypeClick(DailyBookingPaymentTypeView.PaymentType paymentType);

        void onPaymentClick(String firstName, String lastName, String phone, String email);

        void onPhoneNumberClick(String phoneNumber);

        void onAgreedTermClick(boolean checked);
    }

    public StayOutboundPaymentView(BaseActivity baseActivity, StayOutboundPaymentView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayOutboundPaymentDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.scrollView, getColor(R.color.default_over_scroll_edge));

        setBookingLayout(getContext(), viewDataBinding.scrollLayout);
        setDiscountLayout(getContext(), viewDataBinding.scrollLayout);
        setPaymentLayout(getContext(), viewDataBinding.scrollLayout);
        setRefundLayout(getContext(), viewDataBinding.scrollLayout);
        setPayButtonLayout(getContext(), viewDataBinding.scrollLayout);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (mDailyToolbarLayout == null)
        {
            return;
        }

        mDailyToolbarLayout.setToolbarTitle(title);
    }

    @Override
    public void setBooking(SpannableString checkInDate, SpannableString checkOutDate, int nights, String stayName, String roomType)
    {
        if (getViewDataBinding() == null || mBookingDataBinding == null)
        {
            return;
        }

        mBookingDataBinding.dateInformationView.setDate1Text(getString(R.string.act_booking_chkin), checkInDate);
        mBookingDataBinding.dateInformationView.setDate2Text(getString(R.string.act_booking_chkout), checkOutDate);
        mBookingDataBinding.dateInformationView.setCenterNightsVisible(true);
        mBookingDataBinding.dateInformationView.setCenterNightsText(getString(R.string.label_nights, nights));

        mBookingDataBinding.roomInformationView.setTitle(R.string.label_booking_room_info);

        List<Pair<CharSequence, CharSequence>> reservationInformationList = new ArrayList<>();

        reservationInformationList.add(new Pair(getString(R.string.label_booking_place_name), stayName));
        reservationInformationList.add(new Pair(getString(R.string.label_booking_room_type), roomType));

        mBookingDataBinding.roomInformationView.setInformation(reservationInformationList);
    }

    @Override
    public void setGuestInformation(String firstName, String lastName, String phone, String email)
    {
        if (getViewDataBinding() == null || mBookingDataBinding == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(phone) == false)
        {
            mBookingDataBinding.guestPhoneEditText.setText(Util.addHyphenMobileNumber(getContext(), phone));
        }

        if (DailyTextUtils.isTextEmpty(email) == false)
        {
            mBookingDataBinding.guestEmailEditText.setText(email);
        }

        mBookingDataBinding.guestFirstNameEditText.removeTextChangedListener(mFirstNameTextWatcher);
        mBookingDataBinding.guestFirstNameEditText.addTextChangedListener(mFirstNameTextWatcher);

        mBookingDataBinding.guestLastNameEditText.removeTextChangedListener(mLastNameTextWatcher);
        mBookingDataBinding.guestLastNameEditText.addTextChangedListener(mLastNameTextWatcher);

        mBookingDataBinding.guestLastNameEditText.setText(lastName);
        mBookingDataBinding.guestFirstNameEditText.setText(firstName);
    }

    @Override
    public void setGuestPhoneInformation(String phone)
    {
        if (getViewDataBinding() == null || mBookingDataBinding == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(phone) == true)
        {
            mBookingDataBinding.guestPhoneEditText.setText(null);
        } else
        {
            mBookingDataBinding.guestPhoneEditText.setText(Util.addHyphenMobileNumber(getContext(), phone));
        }
    }

    @Override
    public void setPeople(People people)
    {
        if (getViewDataBinding() == null || mBookingDataBinding == null || people == null)
        {
            return;
        }

        mBookingDataBinding.guestPeopleEditText.setText(people.toString(getContext()));
    }

    @Override
    public void setStayOutboundPayment(int bonus, int nights, int totalPrice, int discountPrice, double taxPrice)
    {
        if (getViewDataBinding() == null || mDiscountDataBinding == null || nights == 0)
        {
            return;
        }

        if (discountPrice < 0)
        {
            discountPrice = 0;
        }

        setBonus(bonus, discountPrice);

        mDiscountDataBinding.informationView.setReservationPrice(nights, totalPrice);
        mDiscountDataBinding.informationView.setDiscountPrice(discountPrice);

        int paymentPrice = totalPrice - discountPrice;

        mDiscountDataBinding.informationView.setTotalPaymentPrice(paymentPrice);

        if (taxPrice > 0)
        {
            mDiscountDataBinding.additionalTaxMemoTextView.setVisibility(View.VISIBLE);
            mDiscountDataBinding.additionalTaxLayout.setVisibility(View.VISIBLE);
            mDiscountDataBinding.taxPriceTextView.setText(DailyTextUtils.getGlobalCurrency(Locale.US, getString(R.string.label_currency_usd), taxPrice));
        } else
        {
            mDiscountDataBinding.additionalTaxMemoTextView.setVisibility(View.GONE);
            mDiscountDataBinding.additionalTaxLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void setEasyCard(Card card)
    {
        if (getViewDataBinding() == null || mPayDataBinding == null)
        {
            return;
        }

        if(card == null)
        {
            mPayDataBinding.paymentTypeView.setEasyCard(null, null);
        } else
        {
            mPayDataBinding.paymentTypeView.setEasyCard(card.name, card.number);
        }
    }

    @Override
    public void setRefundPolicyList(List<String> refundPolicyList)
    {
        if (getViewDataBinding() == null || mRefundDataBinding == null)
        {
            return;
        }

        if (refundPolicyList == null || refundPolicyList.size() == 0)
        {
            mRefundDataBinding.refundPolicyLayout.setVisibility(View.GONE);
        } else
        {
            mRefundDataBinding.refundPolicyLayout.setVisibility(View.VISIBLE);
            mRefundDataBinding.refundPolicyListLayout.removeAllViews();

            int size = refundPolicyList.size();

            for (int i = 0; i < size; i++)
            {
                if (DailyTextUtils.isTextEmpty(refundPolicyList.get(i)) == true)
                {
                    continue;
                }

                LayoutStayOutboundDetailInformationDataBinding detailInformationDataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext())//
                    , R.layout.layout_stay_outbound_detail_information_data, mRefundDataBinding.refundPolicyListLayout, true);

                detailInformationDataBinding.textView.setText(Html.fromHtml(refundPolicyList.get(i)));

                if (i == size - 1)
                {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) detailInformationDataBinding.textView.getLayoutParams();
                    layoutParams.bottomMargin = 0;
                    detailInformationDataBinding.textView.setLayoutParams(layoutParams);
                }
            }
        }
    }

    @Override
    public void setVendorName(String vendorName)
    {
        if (mRefundDataBinding == null)
        {
            return;
        }

        mRefundDataBinding.agreementThirdPartyView.setVendorBusinessName(vendorName);
    }

    @Override
    public void setGuidePaymentType(String text)
    {
        if (getViewDataBinding() == null || mPayDataBinding == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(text) == true)
        {
            mPayDataBinding.paymentTypeView.setGuidePaymentTypeVisible(false);
        } else
        {
            mPayDataBinding.paymentTypeView.setGuidePaymentTypeVisible(true);
            mPayDataBinding.paymentTypeView.setGuidePaymentType(text);
        }
    }


    @Override
    public void setPaymentTypeEnabled(DailyBookingPaymentTypeView.PaymentType paymentType, boolean enabled)
    {
        if (getViewDataBinding() == null || mPayDataBinding == null)
        {
            return;
        }

        mPayDataBinding.paymentTypeView.setPaymentTypeEnable(paymentType, enabled);
    }

    @Override
    public void setPaymentType(DailyBookingPaymentTypeView.PaymentType paymentType)
    {
        if (getViewDataBinding() == null || mPayDataBinding == null)
        {
            return;
        }

        mPayDataBinding.paymentTypeView.setPaymentType(paymentType);
    }

    @Override
    public void setBonusEnabled(boolean enabled)
    {
        if (mDiscountDataBinding == null)
        {
            return;
        }

        mDiscountDataBinding.informationView.setBonusEnabled(enabled);
    }

    @Override
    public void setBonusSelected(boolean selected)
    {
        if (getViewDataBinding() == null || mDiscountDataBinding == null)
        {
            return;
        }

        //selected가 true enabled가 false일수는 없다.
        if (mDiscountDataBinding.informationView.isBonusEnabled() == false)
        {
            return;
        }

        if (selected == true)
        {
            mDiscountDataBinding.informationView.setBonusSelected(true);
            mDiscountDataBinding.informationView.setOnBonusClickListener(null);
            mDiscountDataBinding.informationView.setOnBonusTabClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mDiscountDataBinding == null)
                    {
                        return;
                    }

                    getEventListener().onBonusClick(mDiscountDataBinding.informationView.isBonusSelected() == false);
                }
            });
        } else
        {
            mDiscountDataBinding.informationView.setBonusSelected(false);
            mDiscountDataBinding.informationView.setOnBonusClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    getEventListener().onBonusClick(true);
                }
            });

            mDiscountDataBinding.informationView.setOnBonusTabClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mDiscountDataBinding == null)
                    {
                        return;
                    }

                    getEventListener().onBonusClick(mDiscountDataBinding.informationView.isBonusSelected() == false);
                }
            });
        }
    }

    @Override
    public void showAgreeTermDialog(DailyBookingPaymentTypeView.PaymentType paymentType//
        , View.OnClickListener onClickListener, DialogInterface.OnCancelListener cancelListener)
    {
        hideSimpleDialog();

        switch (paymentType)
        {
            case EASY_CARD:
                showSimpleDialog(getEasyPaymentAgreeLayout(onClickListener), cancelListener, null, true);
                break;

            case CARD:
            case PHONE:
            case VBANK:
                showSimpleDialog(getPaymentAgreeLayout(onClickListener), cancelListener, null, true);
                break;

            default:
                return;
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.fakeMobileEditView:
            {
                if (mBookingDataBinding == null)
                {
                    return;
                }

                if (mBookingDataBinding.guestPhoneEditText.isSelected() == true)
                {
                    getEventListener().onPhoneNumberClick(mBookingDataBinding.guestPhoneEditText.getText().toString());
                } else
                {
                    mBookingDataBinding.guestPhoneEditText.requestFocus();
                    mBookingDataBinding.guestPhoneEditText.setSelected(true);
                }
                break;
            }

            case R.id.doPaymentView:
            {
                if (mBookingDataBinding == null)
                {
                    return;
                }

                // 투숙자명, 연락처 이메일,
                getEventListener().onPaymentClick(mBookingDataBinding.guestFirstNameEditText.getText().toString()//
                    , mBookingDataBinding.guestLastNameEditText.getText().toString()//
                    , mBookingDataBinding.guestPhoneEditText.getText().toString()//
                    , mBookingDataBinding.guestEmailEditText.getText().toString());
                break;
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        switch (v.getId())
        {
            case R.id.guestPhoneEditText:
                if (hasFocus == true)
                {
                    getEventListener().onPhoneNumberClick(mBookingDataBinding.guestPhoneEditText.getText().toString());
                } else
                {
                    mBookingDataBinding.guestPhoneEditText.setSelected(false);
                }
                break;
        }
    }

    private void initToolbar(ActivityStayOutboundPaymentDataBinding viewDataBinding)
    {
        mDailyToolbarLayout = new DailyToolbarLayout(getContext(), viewDataBinding.toolbar.findViewById(R.id.toolbar));
        mDailyToolbarLayout.initToolbar(null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onBackClick();
            }
        });

        mDailyToolbarLayout.setToolbarMenu(R.drawable.navibar_ic_call, -1);
        mDailyToolbarLayout.setToolbarMenuClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onCallClick();
            }
        });
    }

    private void setBookingLayout(Context context, ViewGroup viewGroup)
    {
        if (context == null)
        {
            return;
        }

        mBookingDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context)//
            , R.layout.layout_stay_outbound_payment_booking_data, viewGroup, true);

        mBookingDataBinding.guestPhoneEditText.setOnFocusChangeListener(this);

        mBookingDataBinding.fakeMobileEditView.setFocusable(true);
        mBookingDataBinding.fakeMobileEditView.setOnClickListener(this);
    }

    private void setDiscountLayout(Context context, ViewGroup viewGroup)
    {
        if (context == null)
        {
            return;
        }

        mDiscountDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context)//
            , R.layout.layout_stay_outbound_payment_discount_data, viewGroup, true);

        mDiscountDataBinding.informationView.setDiscountTypeVisible(false, false);
    }

    private void setPaymentLayout(Context context, ViewGroup viewGroup)
    {
        if (context == null)
        {
            return;
        }

        mPayDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context)//
            , R.layout.layout_stay_outbound_payment_pay_data, viewGroup, true);


        mPayDataBinding.paymentTypeView.setPaymentTypeVisible(DailyBookingPaymentTypeView.PaymentType.EASY_CARD, true);
        mPayDataBinding.paymentTypeView.setPaymentTypeVisible(DailyBookingPaymentTypeView.PaymentType.CARD, true);
        mPayDataBinding.paymentTypeView.setPaymentTypeVisible(DailyBookingPaymentTypeView.PaymentType.PHONE, true);
        mPayDataBinding.paymentTypeView.setPaymentTypeVisible(DailyBookingPaymentTypeView.PaymentType.VBANK, false);

        mPayDataBinding.paymentTypeView.setOnPaymentTypeClickListener(new DailyBookingPaymentTypeView.OnPaymentTypeClickListener()
        {
            @Override
            public void onEasyCardManagerClick()
            {
                getEventListener().onEasyCardManagerClick();
            }

            @Override
            public void onRegisterEasyCardClick()
            {
                getEventListener().onRegisterEasyCardClick();
            }

            @Override
            public void onPaymentTypeClick(DailyBookingPaymentTypeView.PaymentType paymentType)
            {
                getEventListener().onPaymentTypeClick(paymentType);
            }
        });
    }

    private void setRefundLayout(Context context, ViewGroup viewGroup)
    {
        if (context == null)
        {
            return;
        }

        mRefundDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context)//
            , R.layout.layout_stay_outbound_payment_refund_data, viewGroup, true);

        mRefundDataBinding.agreementThirdPartyView.setOnAgreementClickListener(new DailyBookingAgreementThirdPartyView.OnAgreementClickListener()
        {
            @Override
            public void onAgreementClick(boolean isChecked)
            {
                getEventListener().onAgreedTermClick(isChecked);
            }
        });
    }

    private void setPayButtonLayout(Context context, ViewGroup viewGroup)
    {
        if (context == null)
        {
            return;
        }

        mButtonDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context)//
            , R.layout.layout_stay_outbound_payment_button_data, viewGroup, true);

        mButtonDataBinding.doPaymentView.setOnClickListener(this);
    }

    private void setBonus(int bonus, int discountPrice)
    {
        if (getViewDataBinding() == null || mDiscountDataBinding == null)
        {
            return;
        }

        mDiscountDataBinding.informationView.setTotalBonus(bonus);
        mDiscountDataBinding.informationView.setUsedBonus(discountPrice);
    }

    protected ViewGroup getEasyPaymentAgreeLayout(View.OnClickListener onClickListener)
    {
        int[] messageResIds = new int[]{R.string.dialog_msg_hotel_payment_message01//
            , R.string.message_stay_outbound_payment_agree_01//
            , R.string.message_stay_outbound_payment_agree_02//
            , R.string.message_stay_outbound_payment_agree_03//
            , R.string.message_stay_outbound_payment_agree_04};

        final FinalCheckLayout finalCheckLayout = new FinalCheckLayout(getContext());
        finalCheckLayout.setMessages(messageResIds);

        final TextView agreeSignatureTextView = (TextView) finalCheckLayout.findViewById(R.id.agreeSignatureTextView);
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
                        agreeSignatureTextView.setAnimation(null);
                        agreeSignatureTextView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation)
                    {
                    }
                });

                agreeSignatureTextView.startAnimation(animation);

                confirmTextView.setEnabled(true);
                confirmTextView.setOnClickListener(onClickListener);
            }
        });

        return finalCheckLayout;
    }

    private View getPaymentAgreeLayout(View.OnClickListener onClickListener)
    {
        int[] messageResIds = new int[]{R.string.dialog_msg_hotel_payment_message01//
            , R.string.message_stay_outbound_payment_agree_01//
            , R.string.message_stay_outbound_payment_agree_02//
            , R.string.message_stay_outbound_payment_agree_03//
            , R.string.message_stay_outbound_payment_agree_04};

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_dialog_confirm_payment, null);
        ViewGroup messageLayout = (ViewGroup) view.findViewById(R.id.messageLayout);

        makeMessagesLayout(messageLayout, messageResIds);

        View confirmTextView = view.findViewById(R.id.confirmTextView);

        confirmTextView.setOnClickListener(onClickListener);

        return view;
    }

    private void makeMessagesLayout(ViewGroup viewGroup, int[] textResIds)
    {
        if (viewGroup == null || textResIds == null)
        {
            return;
        }

        int length = textResIds.length;

        for (int i = 0; i < length; i++)
        {
            View messageRow = LayoutInflater.from(getContext()).inflate(R.layout.row_payment_agreedialog, viewGroup, false);

            TextView messageTextView = (TextView) messageRow.findViewById(R.id.messageTextView);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            if (i == length - 1)
            {
                layoutParams.setMargins(ScreenUtils.dpToPx(getContext(), 5), 0, 0, 0);
            } else
            {
                layoutParams.setMargins(ScreenUtils.dpToPx(getContext(), 5), 0, 0, ScreenUtils.dpToPx(getContext(), 10));
            }

            messageTextView.setLayoutParams(layoutParams);

            String message = getString(textResIds[i]);

            int startIndex = message.indexOf("<b>");

            if (startIndex >= 0)
            {
                message = message.replaceAll("<b>", "");

                int endIndex = message.indexOf("</b>");

                message = message.replaceAll("</b>", "");

                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(message);

                spannableStringBuilder.setSpan(new ForegroundColorSpan(getColor(R.color.dh_theme_color)), //
                    startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), //
                    startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                messageTextView.setText(spannableStringBuilder);
            } else
            {
                messageTextView.setText(message);
            }

            viewGroup.addView(messageRow);
        }
    }

    private TextWatcher mFirstNameTextWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {

        }

        @Override
        public void afterTextChanged(Editable s)
        {
            if (mBookingDataBinding == null)
            {
                return;
            }

            if (s == null || s.length() == 0)
            {
                mBookingDataBinding.guestFirstNameHintEditText.setVisibility(View.VISIBLE);
            } else
            {
                mBookingDataBinding.guestFirstNameHintEditText.setVisibility(View.GONE);
            }
        }
    };

    private TextWatcher mLastNameTextWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {

        }

        @Override
        public void afterTextChanged(Editable s)
        {
            if (mBookingDataBinding == null)
            {
                return;
            }

            if (s == null || s.length() == 0)
            {
                mBookingDataBinding.guestLastNameHintEditText.setVisibility(View.VISIBLE);
            } else
            {
                mBookingDataBinding.guestLastNameHintEditText.setVisibility(View.GONE);
            }
        }
    };
}
