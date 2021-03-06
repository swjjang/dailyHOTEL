package com.daily.dailyhotel.screen.booking.detail.gourmet.receipt;

import android.view.View;
import android.view.WindowManager;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.GourmetReceipt;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityGourmetReceiptDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutGourmetReceiptReservationInfoDataBinding;

public class GourmetReceiptView extends BaseDialogView<GourmetReceiptView.OnEventListener, ActivityGourmetReceiptDataBinding> implements GourmetReceiptInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onEmailClick();

        void onSendEmailClick(String email);

        void onScreenClick();
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
                getEventListener().onScreenClick();
            }
        });

        getViewDataBinding().sendEmailView.setOnClickListener(new View.OnClickListener()
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

        getViewDataBinding().paymentInfoLayout.setData(gourmetReceipt.paidAt, gourmetReceipt.paymentType //
            , gourmetReceipt.price, 0, gourmetReceipt.couponAmount, gourmetReceipt.paymentAmount);

        // **공급자** 레이아웃에서 처리

        // 코멘트
        getViewDataBinding().commentTextView.setText(gourmetReceipt.notice);
    }

    @Override
    public void setFullScreenMode(boolean isFullScreenMode)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (isFullScreenMode == true)
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
}
