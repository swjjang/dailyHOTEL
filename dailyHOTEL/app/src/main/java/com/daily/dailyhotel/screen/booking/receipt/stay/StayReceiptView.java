package com.daily.dailyhotel.screen.booking.receipt.stay;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.widget.DailyEditText;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.entity.Booking;
import com.daily.dailyhotel.entity.StayReceipt;
import com.daily.dailyhotel.entity.StayReceiptItem;
import com.daily.dailyhotel.entity.StayReceiptProvider;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayReceiptDataBinding;
import com.twoheart.dailyhotel.databinding.DialogSendEmailDataBinding;
import com.twoheart.dailyhotel.util.Util;

public class StayReceiptView extends BaseDialogView<StayReceiptView.OnEventListener, ActivityStayReceiptDataBinding> implements StayReceiptInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onShowEmailDialogClick();

        void onSendEmailClick(String email);

        void onReceiptLayoutClick();
    }

    public StayReceiptView(BaseActivity baseActivity, StayReceiptView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayReceiptDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);
        initLayout(viewDataBinding);
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

    private void initToolbar(ActivityStayReceiptDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setTitleText(R.string.frag_issuing_receipt);
        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }

    private void initLayout(ActivityStayReceiptDataBinding viewDataBinding)
    {
        if (viewDataBinding == null || getContext() == null)
        {
            return;
        }

        setBookingState(Booking.BOOKING_STATE_NONE);

        getViewDataBinding().receiptLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onReceiptLayoutClick();
            }
        });

        // 영수증 다음 버전으로
        viewDataBinding.sendEmailView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //                if (DailyTextUtils.isTextEmpty(mReservationIndex) == true)
                //                {
                //                    restartExpiredSession();
                //                } else
                //                {
                //                    showSendEmailDialog();
                //                }

                getEventListener().onShowEmailDialogClick();
            }
        });
    }

    @Override
    public void setReceipt(StayReceipt stayReceipt)
    {
        if (getViewDataBinding() == null || stayReceipt == null || getContext() == null)
        {
            return;
        }

        StayReceiptItem item = stayReceipt.receipt;
        StayReceiptProvider provider = stayReceipt.provider;

        // **예약 세부 정보**
        // 예약 번호
        getViewDataBinding().textView13.setText(Integer.toString(stayReceipt.reservationIndex));

        // 호텔명
        getViewDataBinding().textView3.setText(item.stayName);

        // 호텔주소
        getViewDataBinding().textView5.setText(item.stayAddress);

        // 고객성명/번호
        getViewDataBinding().textView7.setText(item.userName + " / " + Util.addHyphenMobileNumber(getContext(), item.userPhone));

        // 체크인/아웃
        getViewDataBinding().textView9.setText(item.checkInDate + " - " + item.checkOutDate);

        // 숙박 일수/객실수
        getViewDataBinding().textView11.setText(item.nights + "일/" + item.rooms + "객실");

        // **결제 정보**
        // 결제일
        getViewDataBinding().textView23.setText(item.paidAt);

        // 결제수단
        if (DailyTextUtils.isTextEmpty(item.paymentType) == true)
        {
            getViewDataBinding().paymentTypeLayout.setVisibility(View.GONE);
        } else
        {
            getViewDataBinding().paymentTypeLayout.setVisibility(View.VISIBLE);

            getViewDataBinding().textView33.setText(item.paymentType);
        }

        getViewDataBinding().saleLayout.setVisibility(View.VISIBLE);

        // 총금액
        getViewDataBinding().textView29.setText(DailyTextUtils.getPriceFormat(getContext(), item.price, false));

        // 적립금 혹은 쿠폰 사용
        if (item.bonusAmount > 0 || item.couponAmount > 0)
        {
            if (item.bonusAmount < 0)
            {
                item.bonusAmount = 0;
            }

            if (item.couponAmount < 0)
            {
                item.couponAmount = 0;
            }

            getViewDataBinding().discountLayout.setVisibility(View.VISIBLE);
            getViewDataBinding().discountedTextView.setText("- " + DailyTextUtils.getPriceFormat(getContext() //
                , item.bonusAmount + item.couponAmount, false));
        } else
        {
            getViewDataBinding().discountLayout.setVisibility(View.GONE);
        }

        if (item.bonusAmount > 0 || item.couponAmount > 0)
        {
            getViewDataBinding().saleLayout.setVisibility(View.VISIBLE);
        } else
        {
            getViewDataBinding().saleLayout.setVisibility(View.GONE);
        }

        // 총 입금(실 결제) 금액
        getViewDataBinding().totalPaymentTextView.setText(DailyTextUtils.getPriceFormat(getContext(), item.paymentAmount, false));

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
        getViewDataBinding().addressTextView.setText(getString(R.string.label_receipt_address, address));

        // 등록번호
        getViewDataBinding().registrationNoTextView.setText(getString(R.string.label_receipt_registeration_number, registrationNo));

        // 코멘트
        getViewDataBinding().commentTextView.setText(provider.memo);
    }

    @Override
    public void updateFullScreenStatus(boolean isUseFullScreen)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (isUseFullScreen)
        {
            getViewDataBinding().toolbarView.setVisibility(View.GONE);
            getViewDataBinding().bookingStateLayout.setVisibility(View.GONE);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

            getViewDataBinding().bottomLayout.setVisibility(View.GONE);
        } else
        {
            getViewDataBinding().toolbarView.setVisibility(View.VISIBLE);
            getViewDataBinding().bookingStateLayout.setVisibility(View.VISIBLE);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            getViewDataBinding().bottomLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showSendEmailDialog(DialogInterface.OnDismissListener listener)
    {
        if (getContext() == null || getViewDataBinding() == null)
        {
            return;
        }

        DialogSendEmailDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_send_email_data, null, false);

        dataBinding.emailEditTExt.setDeleteButtonVisible(new DailyEditText.OnDeleteTextClickListener()
        {
            @Override
            public void onDelete(DailyEditText dailyEditText)
            {
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(dailyEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        dataBinding.emailEditTExt.setText(DailyUserPreference.getInstance(getContext()).getEmail());
        dataBinding.emailEditTExt.setSelection(dataBinding.emailEditTExt.length());

        // 버튼
        //        dataBinding.twoButtonLayout.setVisibility(View.VISIBLE);

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
                String email = dataBinding.emailEditTExt.getText().toString();

                if (DailyTextUtils.validEmail(email) == false)
                {
                    DailyToast.showToast(getContext(), R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
                    return;
                }

                hideSimpleDialog();

                getEventListener().onSendEmailClick(email);
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

        showSimpleDialog(dataBinding.getRoot(), null, listener, false);
    }

    @Override
    public void setBookingState(int bookingState)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (Booking.BOOKING_STATE_RESERVATION_WAITING == bookingState)
        {
            getViewDataBinding().bookingStateTextView.setVisibility(View.VISIBLE);
            getViewDataBinding().toolbarView.setUnderLineHeight(1);

            getViewDataBinding().bookingStateTextView.setText(Html.fromHtml(getString(R.string.label_receipt_booking_state_reservation_wait)));
        } else
        {
            getViewDataBinding().bookingStateTextView.setVisibility(View.GONE);
            getViewDataBinding().toolbarView.setUnderLineHeight(getContext().getResources().getDimensionPixelSize(R.dimen.gradient_1dp_line_height_under_21));
        }
    }
}
