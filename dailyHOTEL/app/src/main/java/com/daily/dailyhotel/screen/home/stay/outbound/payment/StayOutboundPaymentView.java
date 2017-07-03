package com.daily.dailyhotel.screen.home.stay.outbound.payment;

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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyScrollView;
import com.daily.dailyhotel.entity.Card;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayOutboundPayment;
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

        void onCardManagerClick();

        void onRegisterCardClick();

        void onPaymentTypeClick(StayOutboundPayment.PaymentType paymentType);

        void onPaymentClick(String firstName, String lastName, String phone, String email);

        void onPhoneNumberClick(String phoneNumber);
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

        mBookingDataBinding.checkInDayTextView.setText(checkInDate);
        mBookingDataBinding.checkOutDayTextView.setText(checkOutDate);
        mBookingDataBinding.nightsTextView.setText(getString(R.string.label_nights, nights));

        mBookingDataBinding.placeNameTextView.setText(stayName);
        mBookingDataBinding.roomTypeTextView.setText(roomType);
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

        setBonus(bonus, discountPrice);

        if (nights > 1)
        {
            mDiscountDataBinding.amountNightsTextView.setText(getString(R.string.label_booking_hotel_nights, nights));
            mDiscountDataBinding.amountNightsTextView.setVisibility(View.VISIBLE);
        } else
        {
            mDiscountDataBinding.amountNightsTextView.setVisibility(View.GONE);
        }

        mDiscountDataBinding.originalPriceTextView.setText(DailyTextUtils.getPriceFormat(getContext(), totalPrice, false));

        if (discountPrice < 0)
        {
            discountPrice = 0;
        }

        if (discountPrice == 0)
        {
            mDiscountDataBinding.discountPriceTextView.setText(DailyTextUtils.getPriceFormat(getContext(), discountPrice, false));
        } else
        {
            mDiscountDataBinding.discountPriceTextView.setText("-" + DailyTextUtils.getPriceFormat(getContext(), discountPrice, false));
        }

        int paymentPrice = totalPrice - discountPrice;

        mDiscountDataBinding.totalPaymentPriceTextView.setText(DailyTextUtils.getPriceFormat(getContext(), paymentPrice, false));

        if (paymentPrice == 0)
        {
            setFreePaymentEnabled(true);
        } else
        {
            setFreePaymentEnabled(false);
        }

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

        if (card == null)
        {
            mPayDataBinding.cardManagerLayout.setVisibility(View.GONE);
            mPayDataBinding.emptySimpleCardLayout.setVisibility(View.VISIBLE);
            mPayDataBinding.selectedSimpleCardLayout.setVisibility(View.GONE);

            mPayDataBinding.logoTextView.setText(null);
            mPayDataBinding.numberTextView.setText(null);
        } else
        {
            mPayDataBinding.cardManagerLayout.setVisibility(View.VISIBLE);
            mPayDataBinding.emptySimpleCardLayout.setVisibility(View.GONE);
            mPayDataBinding.selectedSimpleCardLayout.setVisibility(View.VISIBLE);

            mPayDataBinding.logoTextView.setText(card.name);
            mPayDataBinding.numberTextView.setText(card.number);
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
            mRefundDataBinding.getRoot().setVisibility(View.GONE);
        } else
        {
            mRefundDataBinding.getRoot().setVisibility(View.VISIBLE);
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
    public void setMemoPaymentType(String memo)
    {
        if (getViewDataBinding() == null || mPayDataBinding == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(memo) == true)
        {
            mPayDataBinding.guidePaymentMemoTextView.setVisibility(View.GONE);
        } else
        {
            mPayDataBinding.guidePaymentMemoTextView.setVisibility(View.VISIBLE);
            mPayDataBinding.guidePaymentMemoTextView.setText(memo);
        }
    }


    @Override
    public void setPaymentTypeEnabled(StayOutboundPayment.PaymentType paymentType, boolean enabled)
    {
        if (getViewDataBinding() == null || mPayDataBinding == null)
        {
            return;
        }

        switch (paymentType)
        {
            case EASY_CARD:
                setPaymentTypeEnabled(mPayDataBinding.disableSimpleCardView, enabled);
                break;

            case CARD:
                setPaymentTypeEnabled(mPayDataBinding.disableCardView, enabled);
                break;

            case PHONE_PAY:
                setPaymentTypeEnabled(mPayDataBinding.disablePhoneView, enabled);
                break;
        }
    }

    @Override
    public void setPaymentType(StayOutboundPayment.PaymentType paymentType)
    {
        if (getViewDataBinding() == null || mPayDataBinding == null)
        {
            return;
        }

        if (paymentType == null)
        {
            ((View) mPayDataBinding.simpleCardLayout.getParent()).setSelected(false);
            mPayDataBinding.simpleCardLayout.setSelected(false);
            mPayDataBinding.cardLayout.setSelected(false);
            mPayDataBinding.phoneLayout.setSelected(false);
            return;
        }

        switch (paymentType)
        {
            case EASY_CARD:
            {
                ((View) mPayDataBinding.simpleCardLayout.getParent()).setSelected(true);
                mPayDataBinding.simpleCardLayout.setSelected(true);
                mPayDataBinding.cardLayout.setSelected(false);
                mPayDataBinding.phoneLayout.setSelected(false);
                break;
            }

            case CARD:
            {
                ((View) mPayDataBinding.simpleCardLayout.getParent()).setSelected(false);
                mPayDataBinding.simpleCardLayout.setSelected(false);
                mPayDataBinding.cardLayout.setSelected(true);
                mPayDataBinding.phoneLayout.setSelected(false);
                break;
            }

            case PHONE_PAY:
            {
                ((View) mPayDataBinding.simpleCardLayout.getParent()).setSelected(false);
                mPayDataBinding.simpleCardLayout.setSelected(false);
                mPayDataBinding.cardLayout.setSelected(false);
                mPayDataBinding.phoneLayout.setSelected(true);
                break;
            }
        }
    }

    @Override
    public void setBonusEnabled(boolean enabled)
    {
        mDiscountDataBinding.bonusRadioButton.setEnabled(enabled);
        mDiscountDataBinding.bonusLayout.setEnabled(enabled);
        mDiscountDataBinding.usedBonusLayout.setEnabled(enabled);
    }

    @Override
    public void setBonusSelected(boolean selected)
    {
        if (getViewDataBinding() == null || mDiscountDataBinding == null)
        {
            return;
        }

        //selected가 true enabled가 false일수는 없다.
        if (mDiscountDataBinding.bonusLayout.isEnabled() == false)
        {
            return;
        }

        if (selected == true)
        {
            mDiscountDataBinding.bonusRadioButton.setSelected(true);
            mDiscountDataBinding.bonusLayout.setSelected(true);
            mDiscountDataBinding.bonusLayout.setOnClickListener(null);

            mDiscountDataBinding.usedBonusLayout.setOnClickListener(this);
            mDiscountDataBinding.usedBonusLayout.setSelected(true);
        } else
        {
            mDiscountDataBinding.bonusRadioButton.setSelected(false);
            mDiscountDataBinding.bonusLayout.setSelected(false);
            mDiscountDataBinding.bonusLayout.setOnClickListener(this);

            mDiscountDataBinding.usedBonusLayout.setOnClickListener(this);
            mDiscountDataBinding.usedBonusLayout.setSelected(false);
        }
    }

    @Override
    public void showAgreeTermDialog(StayOutboundPayment.PaymentType paymentType//
        , View.OnClickListener onClickListener, DialogInterface.OnCancelListener cancelListener)
    {
        hideSimpleDialog();

        switch (paymentType)
        {
            case EASY_CARD:
            {
                showSimpleDialog(getEasyPaymentAgreeLayout(onClickListener), cancelListener, null, true);
                break;
            }

            case CARD:
            case PHONE_PAY:
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

            case R.id.cardManagerLayout:
                getEventListener().onCardManagerClick();
                break;

            case R.id.emptySimpleCardLayout:
                getEventListener().onRegisterCardClick();
                break;

            case R.id.simpleCardLayout:
            case R.id.selectedSimpleCardLayout:
                getEventListener().onPaymentTypeClick(StayOutboundPayment.PaymentType.EASY_CARD);
                break;

            case R.id.cardLayout:
                getEventListener().onPaymentTypeClick(StayOutboundPayment.PaymentType.CARD);
                break;

            case R.id.phoneLayout:
                getEventListener().onPaymentTypeClick(StayOutboundPayment.PaymentType.PHONE_PAY);
                break;

            case R.id.usedBonusLayout:
            {
                if (mDiscountDataBinding == null)
                {
                    return;
                }

                getEventListener().onBonusClick(mDiscountDataBinding.bonusRadioButton.isSelected() == false);
                break;
            }

            case R.id.bonusLayout:
                getEventListener().onBonusClick(true);
                break;

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

        mDiscountDataBinding.bonusLayout.setOnClickListener(this);
        mDiscountDataBinding.usedBonusLayout.setOnClickListener(this);
    }

    private void setPaymentLayout(Context context, ViewGroup viewGroup)
    {
        if (context == null)
        {
            return;
        }

        mPayDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context)//
            , R.layout.layout_stay_outbound_payment_pay_data, viewGroup, true);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mPayDataBinding.simpleCreditCardLayout.getLayoutParams();

        if (layoutParams == null)
        {
            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(ScreenUtils.dpToPx(getContext(), 15), ScreenUtils.dpToPx(getContext(), 15), ScreenUtils.dpToPx(getContext(), 15), ScreenUtils.dpToPx(getContext(), 15));
        }

        layoutParams.height = (ScreenUtils.getScreenWidth(getContext()) - ScreenUtils.dpToPx(getContext(), 60)) * 9 / 16;
        mPayDataBinding.simpleCreditCardLayout.setLayoutParams(layoutParams);

        mPayDataBinding.cardManagerLayout.setOnClickListener(this);
        mPayDataBinding.emptySimpleCardLayout.setOnClickListener(this);
        mPayDataBinding.simpleCardLayout.setOnClickListener(this);
        mPayDataBinding.selectedSimpleCardLayout.setOnClickListener(this);
        mPayDataBinding.cardLayout.setOnClickListener(this);
        mPayDataBinding.phoneLayout.setOnClickListener(this);
    }

    private void setRefundLayout(Context context, ViewGroup viewGroup)
    {
        if (context == null)
        {
            return;
        }

        mRefundDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context)//
            , R.layout.layout_stay_outbound_payment_refund_data, viewGroup, true);

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

        String priceFormat = DailyTextUtils.getPriceFormat(getContext(), bonus, false);
        String text = getString(R.string.label_booking_own_bonus, priceFormat);

        if (bonus > 0)
        {
            int startIndex = text.indexOf(priceFormat);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);

            spannableStringBuilder.setSpan(new ForegroundColorSpan(getColor(R.color.default_text_c323232)), //
                startIndex, text.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            mDiscountDataBinding.bonusTextView.setText(spannableStringBuilder);
        } else
        {
            mDiscountDataBinding.bonusTextView.setText(text);
        }

        if (discountPrice > 0)
        {
            mDiscountDataBinding.usedBonusTextView.setText(DailyTextUtils.getPriceFormat(getContext(), discountPrice, false));
        } else
        {
            mDiscountDataBinding.usedBonusTextView.setText(R.string.label_booking_used_bonus);
        }
    }

    private void setFreePaymentEnabled(boolean enabled)
    {
        if (enabled == true)
        {
            mPayDataBinding.freePaymentView.setVisibility(View.VISIBLE);
            mPayDataBinding.paymentTypeInformationLayout.setVisibility(View.GONE);
        } else
        {
            mPayDataBinding.freePaymentView.setVisibility(View.GONE);
            mPayDataBinding.paymentTypeInformationLayout.setVisibility(View.VISIBLE);
        }
    }

    public boolean isPaymentTypeEnabled(StayOutboundPayment.PaymentType paymentType)
    {
        if (getViewDataBinding() == null || mPayDataBinding == null)
        {
            return false;
        }

        switch (paymentType)
        {
            case EASY_CARD:
                return mPayDataBinding.disableSimpleCardView.getVisibility() == View.VISIBLE;

            case CARD:
                return mPayDataBinding.disableCardView.getVisibility() == View.VISIBLE;

            case PHONE_PAY:
                return mPayDataBinding.disablePhoneView.getVisibility() == View.VISIBLE;
        }

        return false;
    }

    private void setPaymentTypeEnabled(final View view, boolean enabled)
    {
        if (view == null)
        {
            return;
        }

        if (enabled == true)
        {
            view.setOnClickListener(null);
            view.setVisibility(View.GONE);
        } else
        {
            view.setVisibility(View.VISIBLE);
            view.post(new Runnable()
            {
                @Override
                public void run()
                {
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    layoutParams.height = ((View) view.getParent()).getHeight();
                    view.setLayoutParams(layoutParams);
                }
            });

            view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                }
            });
        }
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
