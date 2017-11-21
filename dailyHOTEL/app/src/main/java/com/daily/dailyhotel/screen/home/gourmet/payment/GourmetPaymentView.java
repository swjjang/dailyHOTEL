package com.daily.dailyhotel.screen.home.gourmet.payment;

import android.content.DialogInterface;
import android.support.v4.view.MotionEventCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
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
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyScrollView;
import com.daily.dailyhotel.entity.Card;
import com.daily.dailyhotel.view.DailyBookingAgreementThirdPartyView;
import com.daily.dailyhotel.view.DailyBookingGuestInformationsView;
import com.daily.dailyhotel.view.DailyBookingPaymentTypeView;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityGourmetPaymentDataBinding;
import com.twoheart.dailyhotel.screen.common.FinalCheckLayout;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailySignatureView;

import net.simonvt.numberpicker.NumberPicker;

import java.util.List;

public class GourmetPaymentView extends BaseDialogView<GourmetPaymentView.OnEventListener, ActivityGourmetPaymentDataBinding>//
    implements GourmetPaymentInterface, View.OnClickListener
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onCallClick();

        void onVisitTimeClick();

        void onMenuCountPlusClick();

        void onMenuCountMinusClick();

        void onBonusClick(boolean selected);

        void onCouponClick(boolean selected);

        void onChangedGuestClick(boolean show);

        void onEasyCardManagerClick();

        void onRegisterEasyCardClick();

        void onPaymentTypeClick(DailyBookingPaymentTypeView.PaymentType paymentType);

        void onPaymentClick(String guestName, String guestMobile, String guestEmail);

        void onPhoneNumberClick(String phoneNumber);

        void onAgreedThirdPartyTermsClick(boolean checked);
    }

    public GourmetPaymentView(BaseActivity baseActivity, GourmetPaymentView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityGourmetPaymentDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.scrollView, getColor(R.color.default_over_scroll_edge));

        initBookingLayout();
        initDiscountLayout();
        initPaymentLayout();
        initRefundLayout();
        initPayButtonLayout();
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(title);
    }

    @Override
    public void setBooking(String visitDate, String gourmetName, String menuName)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().dateInformationView.setDate1Text(getString(R.string.label_visit_day), visitDate);
        getViewDataBinding().dateInformationView.setCenterNightsVisible(false);

        getViewDataBinding().menuInformationView.setTitle(R.string.label_booking_ticket_info);
        getViewDataBinding().menuInformationView.removeAllInformation();
        getViewDataBinding().menuInformationView.addInformation(getString(R.string.label_booking_place_name), gourmetName);
        getViewDataBinding().menuInformationView.addInformation(getString(R.string.label_booking_ticket_type), menuName);

        getViewDataBinding().guestCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                getEventListener().onChangedGuestClick(isChecked);
            }
        });
    }

    @Override
    public void setVisitTime(String time)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (time == null)
        {
            getViewDataBinding().dateInformationView.setDate2Text(getString(R.string.label_booking_select_ticket_time), getString(R.string.message_booking_selected_time));
        } else
        {
            getViewDataBinding().dateInformationView.setDate2Text(getString(R.string.label_booking_select_ticket_time), time);
        }

        getViewDataBinding().dateInformationView.setDate2DescriptionTextColor(getColor(R.color.default_text_cb70038));
        getViewDataBinding().dateInformationView.setDate2DescriptionTextDrawable(0, 0, R.drawable.navibar_m_burg_ic_v, 0);
    }

    @Override
    public void setMenuCount(int menuCount)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().menuCountTextView.setText(getString(R.string.label_booking_count, menuCount));
    }

    public void setOverseas(boolean overseas)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (overseas == true)
        {
            getViewDataBinding().userInformationView.setVisibility(View.GONE);
            getViewDataBinding().userInformationUnderLineView.setVisibility(View.GONE);
            getViewDataBinding().guestCheckLayout.setVisibility(View.GONE);

            getViewDataBinding().guestInformationView.setGuideTextVisible(true);
            getViewDataBinding().guestInformationView.setVisibility(View.VISIBLE);
        } else
        {
            getViewDataBinding().userInformationView.setVisibility(View.VISIBLE);
            getViewDataBinding().userInformationUnderLineView.setVisibility(View.VISIBLE);
            getViewDataBinding().guestCheckLayout.setVisibility(View.VISIBLE);

            getViewDataBinding().guestInformationView.setGuideTextVisible(false);
            getViewDataBinding().guestInformationView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setMenuMinusEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().menuCountMinus.setEnabled(enabled);
    }

    @Override
    public void setMenuPlusEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().menuCountPlus.setEnabled(enabled);
    }

    @Override
    public void setUserInformation(String name, String mobile, String email)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().userInformationView.setTitle(R.string.label_booking_user_info);
        getViewDataBinding().userInformationView.removeAllInformation();
        getViewDataBinding().userInformationView.addInformation(getString(R.string.label_booking_user_name), name);
        getViewDataBinding().userInformationView.addInformation(getString(R.string.act_booking_mobile), Util.addHyphenMobileNumber(getContext(), mobile));
        getViewDataBinding().userInformationView.addInformation(getString(R.string.label_email), email);
    }

    @Override
    public void setGuestInformation(String name, String mobile, String email)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().guestInformationView.updateInformation(DailyBookingGuestInformationsView.InformationType.NAME, name);

        if (DailyTextUtils.isTextEmpty(mobile) == false)
        {
            getViewDataBinding().guestInformationView.updateInformation(DailyBookingGuestInformationsView.InformationType.MOBILE//
                , Util.addHyphenMobileNumber(getContext(), mobile));
        }

        if (DailyTextUtils.isTextEmpty(email) == false)
        {
            getViewDataBinding().guestInformationView.updateInformation(DailyBookingGuestInformationsView.InformationType.EMAIL, email);
        }
    }

    @Override
    public void setGuestMobileInformation(String mobile)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(mobile) == true)
        {
            getViewDataBinding().guestInformationView.updateInformation(DailyBookingGuestInformationsView.InformationType.MOBILE, null);
        } else
        {
            getViewDataBinding().guestInformationView.updateInformation(DailyBookingGuestInformationsView.InformationType.MOBILE//
                , Util.addHyphenMobileNumber(getContext(), mobile));
        }
    }

    @Override
    public void setBonus(boolean selected, int bonus, int discountPrice)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        setBonusSelected(selected);
        getViewDataBinding().informationView.setTotalBonus(bonus);
        getViewDataBinding().informationView.setBonus(selected ? discountPrice : 0);
    }

    @Override
    public void setCoupon(boolean selected, int couponPrice)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        setCouponSelected(selected);
        getViewDataBinding().informationView.setCoupon(couponPrice);
    }

    @Override
    public void setGourmetPayment(int menuCount, int totalPrice, int discountPrice)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().informationView.setReservationPrice(menuCount > 1 ? getString(R.string.label_booking_gourmet_count, menuCount) : null//
            , totalPrice);

        getViewDataBinding().informationView.setDiscountPrice(discountPrice);

        int paymentPrice = totalPrice - discountPrice;

        getViewDataBinding().informationView.setTotalPaymentPrice(paymentPrice);
    }

    @Override
    public void setEasyCard(Card card)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (card == null)
        {
            getViewDataBinding().paymentTypeView.setEasyCard(null, null);
        } else
        {
            getViewDataBinding().paymentTypeView.setEasyCard(card.name, card.number);
        }
    }

    @Override
    public void setVendorName(String vendorName)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().agreementThirdPartyView.setVendorBusinessName(vendorName);
    }

    @Override
    public void setGuidePaymentType(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(text) == true)
        {
            getViewDataBinding().paymentTypeView.setGuideTextVisible(false);
        } else
        {
            getViewDataBinding().paymentTypeView.setGuideTextVisible(true);
            getViewDataBinding().paymentTypeView.setGuideText(text);
        }
    }


    @Override
    public void setPaymentTypeEnabled(DailyBookingPaymentTypeView.PaymentType paymentType, boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().paymentTypeView.setPaymentTypeEnabled(paymentType, enabled);
    }

    @Override
    public void setPaymentType(DailyBookingPaymentTypeView.PaymentType paymentType)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().paymentTypeView.setPaymentType(paymentType);
    }

    @Override
    public void setBonusGuideText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().informationView.setBonusGuideText(text);
    }

    @Override
    public void setBonusEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().informationView.setBonusEnabled(enabled);
    }

    @Override
    public void setGuestInformationVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().guestInformationView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showAgreeTermDialog(DailyBookingPaymentTypeView.PaymentType paymentType//
        , int[] messages, View.OnClickListener onClickListener, DialogInterface.OnCancelListener cancelListener)
    {
        hideSimpleDialog();

        switch (paymentType)
        {
            case EASY_CARD:
                showSimpleDialog(getEasyPaymentAgreeLayout(messages, onClickListener), cancelListener, null, true);
                break;

            case CARD:
            case PHONE:
            case VBANK:
            case FREE:
                showSimpleDialog(getPaymentAgreeLayout(messages, onClickListener), cancelListener, null, true);
                break;

            default:
                return;
        }
    }

    @Override
    public void showDatePickerDialog(String titleText, List<String> visitDateTimeList, String selectedVisitDateTime////
        , View.OnClickListener onClickListener, DialogInterface.OnDismissListener dismissListener)
    {
        if (visitDateTimeList == null || visitDateTimeList.size() == 0)
        {
            return;
        }

        hideSimpleDialog();

        try
        {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.view_pickerdialog_layout, null);

            // 상단
            TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
            titleTextView.setVisibility(View.VISIBLE);

            if (DailyTextUtils.isTextEmpty(titleText) == true)
            {
                titleTextView.setText(getString(R.string.dialog_notice2));
            } else
            {
                titleTextView.setText(titleText);
            }

            int size = visitDateTimeList.size();
            String[] visitTime = new String[size];
            int equalsTime = -1;

            for (int i = 0; i < size; i++)
            {
                visitTime[i] = DailyCalendar.convertDateFormatString(visitDateTimeList.get(i), DailyCalendar.ISO_8601_FORMAT, "HH:mm");

                if (visitDateTimeList.get(i).equalsIgnoreCase(selectedVisitDateTime) == true)
                {
                    equalsTime = i;
                }
            }

            // 메시지
            final NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(size - 1);
            numberPicker.setFocusable(true);
            numberPicker.setFocusableInTouchMode(true);
            numberPicker.setDisplayedValues(visitTime);
            numberPicker.setTextTypeface(FontManager.getInstance(getContext()).getRegularTypeface());

            if (equalsTime >= 0)
            {
                numberPicker.setValue(equalsTime);
            }

            TextView confirmTextView = (TextView) view.findViewById(R.id.confirmTextView);

            confirmTextView.setText(R.string.dialog_btn_text_confirm);
            confirmTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (onClickListener != null)
                    {
                        v.setTag(numberPicker.getValue());
                        onClickListener.onClick(v);
                    }
                }
            });

            showSimpleDialog(view, null, dismissListener, true);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void scrollTop()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().scrollView.scrollTo(0, 0);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.menuCountMinus:
                getEventListener().onMenuCountMinusClick();
                break;

            case R.id.menuCountPlus:
                getEventListener().onMenuCountPlusClick();
                break;

            case R.id.doPaymentView:
            {
                if (getViewDataBinding() == null)
                {
                    return;
                }

                // 투숙자명, 연락처, 이메일
                // 해당 인자들은 투숙자 정보가 달라서 체크한 경우에만 해당 값이 내려갑니다
                if (getViewDataBinding().guestCheckBox.isChecked() == true)
                {
                    getEventListener().onPaymentClick(getViewDataBinding().guestInformationView.getInformationTypeValue(DailyBookingGuestInformationsView.InformationType.NAME)//
                        , getViewDataBinding().guestInformationView.getInformationTypeValue(DailyBookingGuestInformationsView.InformationType.MOBILE)//
                        , getViewDataBinding().guestInformationView.getInformationTypeValue(DailyBookingGuestInformationsView.InformationType.EMAIL));
                } else
                {
                    getEventListener().onPaymentClick(null, null, null);
                }
                break;
            }
        }
    }

    private void initToolbar(ActivityGourmetPaymentDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onBackClick();
            }
        });

        viewDataBinding.toolbarView.clearMenuItem();
        viewDataBinding.toolbarView.addMenuItem(DailyToolbarView.MenuItem.CALL, null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onCallClick();
            }
        });
    }

    private void initBookingLayout()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().dateInformationView.setOnDateClickListener(null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onVisitTimeClick();
            }
        });

        getViewDataBinding().menuCountMinus.setOnClickListener(this);
        getViewDataBinding().menuCountPlus.setOnClickListener(this);

        getViewDataBinding().guestInformationView.setOnGuestInformationsClickListener(new DailyBookingGuestInformationsView.OnGuestInformationsClickListener()
        {
            @Override
            public void onMobileClick(String mobile)
            {
                getEventListener().onPhoneNumberClick(mobile);
            }
        });

        getViewDataBinding().guestInformationView.setTitle(R.string.label_booking_visitor_info, R.string.label_booking_required_fileds);
        getViewDataBinding().guestInformationView.setGuideTextVisible(false);

        getViewDataBinding().guestInformationView.addInformation(DailyBookingGuestInformationsView.InformationType.NAME//
            , getString(R.string.label_booking_visitor_name)//
            , null, getString(R.string.label_booking_input_name));

        getViewDataBinding().guestInformationView.addInformation(DailyBookingGuestInformationsView.InformationType.MOBILE//
            , getString(R.string.act_booking_mobile)//
            , null, getString(R.string.label_booking_input_phone));

        getViewDataBinding().guestInformationView.addInformation(DailyBookingGuestInformationsView.InformationType.EMAIL//
            , getString(R.string.act_booking_email)//
            , null, getString(R.string.label_booking_input_email));
    }

    private void initDiscountLayout()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().informationView.setDiscountTypeVisible(false, true);
        getViewDataBinding().informationView.setDepositStickerVisible(false);

        setBonusSelected(false);
        setCouponSelected(false);
    }

    private void initPaymentLayout()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().paymentTypeView.setPaymentTypeVisible(DailyBookingPaymentTypeView.PaymentType.EASY_CARD, true);
        getViewDataBinding().paymentTypeView.setPaymentTypeVisible(DailyBookingPaymentTypeView.PaymentType.CARD, true);
        getViewDataBinding().paymentTypeView.setPaymentTypeVisible(DailyBookingPaymentTypeView.PaymentType.PHONE, true);
        getViewDataBinding().paymentTypeView.setPaymentTypeVisible(DailyBookingPaymentTypeView.PaymentType.VBANK, true);

        getViewDataBinding().paymentTypeView.setOnPaymentTypeClickListener(new DailyBookingPaymentTypeView.OnPaymentTypeClickListener()
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

    private void initRefundLayout()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().refundPolicyView.setRefundPolicyList(null);

        getViewDataBinding().agreementThirdPartyView.setOnAgreementClickListener(new DailyBookingAgreementThirdPartyView.OnAgreementClickListener()
        {
            @Override
            public void onExpandInformationClick()
            {
                getViewDataBinding().agreementThirdPartyView.expandInformation();
                getViewDataBinding().scrollView.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        getViewDataBinding().scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }

            @Override
            public void onCollapseInformationClick()
            {
                getViewDataBinding().agreementThirdPartyView.collapseInformation();
            }

            @Override
            public void onAgreementClick(boolean isChecked)
            {
                getEventListener().onAgreedThirdPartyTermsClick(isChecked);
            }
        });
    }

    private void initPayButtonLayout()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().doPaymentView.setOnClickListener(this);
    }

    private void setBonusSelected(boolean selected)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        //selected가 true enabled가 false일수는 없다.
        if (getViewDataBinding().informationView.isBonusEnabled() == false)
        {
            return;
        }

        if (selected == true)
        {
            getViewDataBinding().informationView.setBonusSelected(true);
            getViewDataBinding().informationView.setOnBonusClickListener(null);
            getViewDataBinding().informationView.setOnBonusTabClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (getViewDataBinding() == null)
                    {
                        return;
                    }

                    getEventListener().onBonusClick(getViewDataBinding().informationView.isBonusSelected() == false);
                }
            });
        } else
        {
            getViewDataBinding().informationView.setBonusSelected(false);
            getViewDataBinding().informationView.setOnBonusClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    getEventListener().onBonusClick(true);
                }
            });

            getViewDataBinding().informationView.setOnBonusTabClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (getViewDataBinding() == null)
                    {
                        return;
                    }

                    getEventListener().onBonusClick(getViewDataBinding().informationView.isBonusSelected() == false);
                }
            });
        }
    }

    private void setCouponSelected(boolean selected)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        //selected가 true enabled가 false일수는 없다.
        if (getViewDataBinding().informationView.isCouponEnabled() == false)
        {
            return;
        }

        if (selected == true)
        {
            getViewDataBinding().informationView.setCouponSelected(true);
            getViewDataBinding().informationView.setOnCouponClickListener(null);
            getViewDataBinding().informationView.setOnCouponTabClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (getViewDataBinding() == null)
                    {
                        return;
                    }

                    getEventListener().onCouponClick(getViewDataBinding().informationView.isCouponSelected() == false);
                }
            });
        } else
        {
            getViewDataBinding().informationView.setCouponSelected(false);
            getViewDataBinding().informationView.setOnCouponClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    getEventListener().onCouponClick(true);
                }
            });

            getViewDataBinding().informationView.setOnCouponTabClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (getViewDataBinding() == null)
                    {
                        return;
                    }

                    getEventListener().onCouponClick(getViewDataBinding().informationView.isCouponSelected() == false);
                }
            });
        }
    }

    protected ViewGroup getEasyPaymentAgreeLayout(int[] messages, View.OnClickListener onClickListener)
    {
        final FinalCheckLayout finalCheckLayout = new FinalCheckLayout(getContext());
        finalCheckLayout.setMessages(messages);

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

    private View getPaymentAgreeLayout(int[] messages, View.OnClickListener onClickListener)
    {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_dialog_confirm_payment, null);
        ViewGroup messageLayout = (ViewGroup) view.findViewById(R.id.messageLayout);

        makeMessagesLayout(messageLayout, messages);

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
}
