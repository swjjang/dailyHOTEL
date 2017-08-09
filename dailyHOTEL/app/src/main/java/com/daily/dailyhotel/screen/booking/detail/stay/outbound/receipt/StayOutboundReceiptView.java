package com.daily.dailyhotel.screen.booking.detail.stay.outbound.receipt;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyEditText;
import com.daily.dailyhotel.entity.StayOutboundReceipt;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundReceiptDataBinding;
import com.twoheart.dailyhotel.databinding.DialogStayOutboundReceiptEmailDataBinding;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.util.Util;

public class StayOutboundReceiptView extends BaseDialogView<StayOutboundReceiptView.OnEventListener, ActivityStayOutboundReceiptDataBinding> implements StayOutboundReceiptInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onEmailClick();

        void onSendEmailClick(String email);

        void onFullScreenClick();
    }

    public StayOutboundReceiptView(BaseActivity baseActivity, StayOutboundReceiptView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayOutboundReceiptDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        viewDataBinding.sendEmailView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onEmailClick();
            }
        });
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
    public void setStayOutboundReceipt(StayOutboundReceipt stayOutboundReceipt)
    {
        if (getViewDataBinding() == null || stayOutboundReceipt == null)
        {
            return;
        }
        // **예약 세부 정보**
        // 예약 번호
        getViewDataBinding().bookingIndexTextView.setText(Integer.toString(stayOutboundReceipt.index));

        // 호텔명
        getViewDataBinding().stayNameTextView.setText(stayOutboundReceipt.placeName);

        // 호텔주소
        getViewDataBinding().addressTextView.setText(stayOutboundReceipt.placeAddress);

        // 고객성명/번호
        getViewDataBinding().guestTxtView.setText(stayOutboundReceipt.userName + " / " + Util.addHyphenMobileNumber(getContext(), stayOutboundReceipt.userPhone));

        // 체크인/아웃
        getViewDataBinding().checkInOutTextView.setText(stayOutboundReceipt.checkInDate + " - " + stayOutboundReceipt.checkOutDate);

        try
        {
            int nights = DailyCalendar.compareDateDay(DailyCalendar.convertDateFormatString(stayOutboundReceipt.checkOutDate, "yyyy-MM-dd", DailyCalendar.ISO_8601_FORMAT)//
                , DailyCalendar.convertDateFormatString(stayOutboundReceipt.checkInDate, "yyyy-MM-dd", DailyCalendar.ISO_8601_FORMAT));

            // 숙박 일수/객실수
            getViewDataBinding().roomTxtView.setText(getString(R.string.label_stay_outbound_booking_night_room, nights, stayOutboundReceipt.roomCount));
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        // **결제 정보**
        // 결제일
        try
        {
            getViewDataBinding().paymentDateTextView.setText(DailyCalendar.convertDateFormatString(stayOutboundReceipt.paymentDate, DailyCalendar.ISO_8601_FORMAT, "yyyy/MM/dd"));
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        // 결제수단
        if (DailyTextUtils.isTextEmpty(stayOutboundReceipt.paymentTypeName) == true)
        {
            getViewDataBinding().paymentTypeLayout.setVisibility(View.GONE);
        } else
        {
            getViewDataBinding().paymentTypeLayout.setVisibility(View.VISIBLE);
            getViewDataBinding().paymentTypeTextView.setText(stayOutboundReceipt.paymentTypeName);
        }

        getViewDataBinding().saleLayout.setVisibility(View.VISIBLE);

        // 총금액
        getViewDataBinding().totalPriceTextView.setText(DailyTextUtils.getPriceFormat(getContext(), stayOutboundReceipt.totalPrice, false));

        // 적립금 혹은 쿠폰 사용
        if (stayOutboundReceipt.bonus > 0 || stayOutboundReceipt.coupon > 0)
        {
            if (stayOutboundReceipt.bonus < 0)
            {
                stayOutboundReceipt.bonus = 0;
            }

            if (stayOutboundReceipt.coupon < 0)
            {
                stayOutboundReceipt.coupon = 0;
            }

            getViewDataBinding().discountLayout.setVisibility(View.VISIBLE);
            getViewDataBinding().discountPriceTextView.setText("- " + DailyTextUtils.getPriceFormat(getContext(), stayOutboundReceipt.bonus + stayOutboundReceipt.coupon, false));
        } else
        {
            getViewDataBinding().discountLayout.setVisibility(View.GONE);
        }

        if (stayOutboundReceipt.bonus > 0 || stayOutboundReceipt.coupon > 0)
        {
            getViewDataBinding().saleLayout.setVisibility(View.VISIBLE);
        } else
        {
            getViewDataBinding().saleLayout.setVisibility(View.GONE);
        }

        // 총 입금(실 결제) 금액
        getViewDataBinding().totalPaymentTextView.setText(DailyTextUtils.getPriceFormat(getContext(), stayOutboundReceipt.paymentAmount, false));

        // **공급자**
        String phone = DailyRemoteConfigPreference.getInstance(getContext()).getRemoteConfigCompanyPhoneNumber();
        String fax = DailyRemoteConfigPreference.getInstance(getContext()).getRemoteConfigCompanyFax();
        String address = DailyRemoteConfigPreference.getInstance(getContext()).getRemoteConfigCompanyAddress();
        String ceoName = DailyRemoteConfigPreference.getInstance(getContext()).getRemoteConfigCompanyCEO();
        String registrationNo = DailyRemoteConfigPreference.getInstance(getContext()).getRemoteConfigCompanyBizRegNumber();
        String companyName = DailyRemoteConfigPreference.getInstance(getContext()).getRemoteConfigCompanyName();

        // 상호
        getViewDataBinding().companyNameTextView.setText(getString(R.string.label_receipt_business_license, companyName, ceoName, phone, fax));

        // 주소
        getViewDataBinding().companyAddressTextView.setText(getString(R.string.label_receipt_address, address));


        // 등록번호
        getViewDataBinding().registrationNoTextView.setText(getString(R.string.label_receipt_registeration_number, registrationNo));

        // 코멘트
        getViewDataBinding().commentTextView.setText(stayOutboundReceipt.comment);

        getViewDataBinding().receiptLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onFullScreenClick();
            }
        });
    }

    @Override
    public void setFullScreenEnabled(boolean enabled)
    {
        if (enabled)
        {
            getViewDataBinding().toolbarView.setVisibility(View.GONE);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

            getViewDataBinding().bottomLayout.setVisibility(View.GONE);
        } else
        {
            getViewDataBinding().toolbarView.setVisibility(View.VISIBLE);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            getViewDataBinding().bottomLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showSendEmailDialog(String email)
    {
        DialogStayOutboundReceiptEmailDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_stay_outbound_receipt_email_data, null, false);


        dataBinding.emailEditTExt.setDeleteButtonVisible(new DailyEditText.OnDeleteTextClickListener()
        {
            @Override
            public void onDelete(DailyEditText dailyEditText)
            {
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(dailyEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        dataBinding.emailEditTExt.setText(email);
        dataBinding.emailEditTExt.setSelection(dataBinding.emailEditTExt.length());

        // 버튼
        dataBinding.negativeTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();
            }
        });

        dataBinding.positiveTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onSendEmailClick(dataBinding.emailEditTExt.getText().toString());
            }
        });

        dataBinding.emailEditTExt.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                if (editable == null || editable.length() == 0)
                {
                    dataBinding.positiveTextView.setEnabled(false);
                } else
                {
                    dataBinding.positiveTextView.setEnabled(true);
                }
            }
        });

        showSimpleDialog(dataBinding.getRoot(), null, null, true);
    }

    private void initToolbar(ActivityStayOutboundReceiptDataBinding viewDataBinding)
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
    }
}
