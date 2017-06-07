package com.daily.dailyhotel.screen.booking.detail.stayoutbound.receipt;

import android.view.View;
import android.view.WindowManager;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.StayOutboundReceipt;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundReceiptDataBinding;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

public class StayOutboundReceiptView extends BaseDialogView<StayOutboundReceiptView.OnEventListener, ActivityStayOutboundReceiptDataBinding> implements StayOutboundReceiptInterface
{
    private DailyToolbarLayout mDailyToolbarLayout;
    private boolean mFullScreen;

    public interface OnEventListener extends OnBaseEventListener
    {
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
    }

    @Override
    public void setToolbarTitle(String title)
    {
    }

    private void setReceipt(StayOutboundReceipt stayOutboundReceipt)
    {
        if (getViewDataBinding() == null || stayOutboundReceipt == null)
        {
            return;
        }
        // **예약 세부 정보**
        // 예약 번호
        getViewDataBinding().bookingIndexTextView.setText(stayOutboundReceipt.index);

        // 호텔명
        getViewDataBinding().stayNameTextView.setText(stayOutboundReceipt.placeName);

        // 호텔주소
        getViewDataBinding().addressTextView.setText(stayOutboundReceipt.placeAddress);

        // 고객성명/번호
        getViewDataBinding().guestTxtView.setText(stayOutboundReceipt.userName + " / " + Util.addHyphenMobileNumber(getContext(), stayOutboundReceipt.userPhone));

        // 체크인/아웃
        getViewDataBinding().checkInOutTextView.setText(stayOutboundReceipt.checkInDateTime + " - " + stayOutboundReceipt.checkOutDateTime);

        int nights = 1;
        int rooms = 1;

        // 숙박 일수/객실수
        getViewDataBinding().roomTxtView.setText(getString(R.string.label_stay_outbound_booking_night_room, nights, rooms));

        // **결제 정보**
        // 결제일
        getViewDataBinding().paymentDateTextView.setText(stayOutboundReceipt.paymentDate);

        // 결제수단
        if (DailyTextUtils.isTextEmpty(stayOutboundReceipt.paymentType) == true)
        {
            getViewDataBinding().paymentTypeLayout.setVisibility(View.GONE);
        } else
        {
            getViewDataBinding().paymentTypeLayout.setVisibility(View.VISIBLE);
            getViewDataBinding().paymentTypeTextView.setText(stayOutboundReceipt.paymentType);
        }

        getViewDataBinding().saleLayout.setVisibility(View.VISIBLE);

        // 총금액
        getViewDataBinding().totalPriceTextView.setText(DailyTextUtils.getPriceFormat(getContext(), stayOutboundReceipt.discountPrice, false));

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
        getViewDataBinding().totalPaymentTextView.setText(DailyTextUtils.getPriceFormat(getContext(), stayOutboundReceipt.totalPrice, false));

        // **공급자**
        String phone = DailyPreference.getInstance(getContext()).getRemoteConfigCompanyPhoneNumber();
        String fax = DailyPreference.getInstance(getContext()).getRemoteConfigCompanyFax();
        String address = DailyPreference.getInstance(getContext()).getRemoteConfigCompanyAddress();
        String ceoName = DailyPreference.getInstance(getContext()).getRemoteConfigCompanyCEO();
        String registrationNo = DailyPreference.getInstance(getContext()).getRemoteConfigCompanyBizRegNumber();
        String companyName = DailyPreference.getInstance(getContext()).getRemoteConfigCompanyName();

        // 상호
        getViewDataBinding().companyNameTextView.setText(getString(R.string.label_receipt_business_license, companyName, ceoName, phone, fax));

        // 주소
        getViewDataBinding().companyAddressTextView.setText(getString(R.string.label_receipt_address, address));


        // 등록번호
        getViewDataBinding().registrationNoTextView.setText(getString(R.string.label_receipt_registeration_number, registrationNo));

        // 코멘트
        getViewDataBinding().commentTextView.setText(stayOutboundReceipt.memo);

        getViewDataBinding().receiptLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mFullScreen = !mFullScreen;
                updateFullscreenStatus(mFullScreen);
            }
        });
    }

    private void updateFullscreenStatus(boolean fullSreen)
    {
        if (fullSreen)
        {
            mDailyToolbarLayout.setToolbarVisibility(false, false);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

            getViewDataBinding().bottomLayout.setVisibility(View.GONE);
        } else
        {
            mDailyToolbarLayout.setToolbarVisibility(true, false);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            getViewDataBinding().bottomLayout.setVisibility(View.VISIBLE);
        }
    }
}
