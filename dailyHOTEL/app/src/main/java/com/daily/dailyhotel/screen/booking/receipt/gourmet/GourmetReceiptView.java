package com.daily.dailyhotel.screen.booking.receipt.gourmet;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.text.Editable;
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
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyEditText;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.entity.GourmetReceipt;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityGourmetReceiptDataBinding;
import com.twoheart.dailyhotel.databinding.DialogSendEmailDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetReceiptReservationInfoDataBinding;
import com.twoheart.dailyhotel.util.DailyCalendar;

public class GourmetReceiptView extends BaseDialogView<GourmetReceiptView.OnEventListener, ActivityGourmetReceiptDataBinding> implements GourmetReceiptInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onShowEmailDialogClick();

        void onSendEmailClick(String email);

        void onReceiptLayoutClick();
    }

    public GourmetReceiptView(BaseActivity baseActivity, GourmetReceiptView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityGourmetReceiptDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        getViewDataBinding().receiptLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onReceiptLayoutClick();
            }
        });

        getViewDataBinding().sendEmailView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onShowEmailDialogClick();
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

    private void initToolbar(ActivityGourmetReceiptDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setTitleText(R.string.frag_issuing_receipt);

        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }

    @Override
    public void setReceipt(GourmetReceipt gourmetReceipt)
    {
        if (getViewDataBinding() == null || gourmetReceipt == null)
        {
            return;
        }

        // 영수증
        // **예약 세부 정보**
        LayoutGourmetReceiptReservationInfoDataBinding reservationInfoDataBinding = getViewDataBinding().reservationInfoDataBinding;

        // 예약 번호
        reservationInfoDataBinding.textView13.setText(Integer.toString(gourmetReceipt.gourmetReservationIdx));

        // 이름
        reservationInfoDataBinding.textView3.setText(gourmetReceipt.restaurantName);

        // 주소
        reservationInfoDataBinding.textView5.setText(gourmetReceipt.restaurantAddress);

        // 고객성명/번호
        reservationInfoDataBinding.textView7.setText(gourmetReceipt.userName + " / " + gourmetReceipt.userPhone);

        // 날짜
        reservationInfoDataBinding.textView9.setText(gourmetReceipt.sday.replaceAll("-", "/"));

        // 수량
        reservationInfoDataBinding.textView11.setText(getString(R.string.label_booking_count, gourmetReceipt.ticketCount));

        // **결제 정보**
        // 결제일
        String paidAtDate = null;
        try
        {
            paidAtDate = DailyCalendar.convertDateFormatString(gourmetReceipt.paidAt, DailyCalendar.ISO_8601_FORMAT, "yyyy/MM/dd");
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        getViewDataBinding().textView23.setText(paidAtDate);

        // 결제수단
        if (DailyTextUtils.isTextEmpty(gourmetReceipt.paymentType) == true)
        {
            getViewDataBinding().paymentTypeLayout.setVisibility(View.GONE);
        } else
        {
            getViewDataBinding().paymentTypeLayout.setVisibility(View.VISIBLE);
            getViewDataBinding().textView33.setText(gourmetReceipt.paymentType);
        }

        getViewDataBinding().saleLayout.setVisibility(View.VISIBLE);

        // 총금액
        getViewDataBinding().textView29.setText(DailyTextUtils.getPriceFormat(getContext(), gourmetReceipt.price, false));

        // 적립금 혹은 쿠폰 사용
        if (gourmetReceipt.couponAmount > 0)
        {
            getViewDataBinding().discountedTextView.setText("- " + DailyTextUtils.getPriceFormat(getContext(), gourmetReceipt.couponAmount, false));
            getViewDataBinding().discountLayout.setVisibility(View.VISIBLE);
            getViewDataBinding().saleLayout.setVisibility(View.VISIBLE);
        } else
        {
            getViewDataBinding().discountLayout.setVisibility(View.GONE);
            getViewDataBinding().saleLayout.setVisibility(View.GONE);
        }

        // 총 입금 금액
        getViewDataBinding().totalPaymentTextView.setText(DailyTextUtils.getPriceFormat(getContext(), gourmetReceipt.paymentAmount, false));

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
        getViewDataBinding().commentTextView.setText(gourmetReceipt.notice);
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
}
