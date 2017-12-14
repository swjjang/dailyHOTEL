package com.daily.dailyhotel.screen.booking.detail.stay.outbound.receipt;

import android.view.View;
import android.view.WindowManager;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.StayOutboundReceipt;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundReceiptDataBinding;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;

public class StayOutboundReceiptView extends BaseDialogView<StayOutboundReceiptView.OnEventListener, ActivityStayOutboundReceiptDataBinding> implements StayOutboundReceiptInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onEmailClick();

        void onSendEmailClick(String email);

        void onScreenClick();
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
        getViewDataBinding().paymentInfoLayout.setData(stayOutboundReceipt.paymentDate, stayOutboundReceipt.paymentTypeName //
            , stayOutboundReceipt.totalPrice, stayOutboundReceipt.bonus, stayOutboundReceipt.coupon //
            ,stayOutboundReceipt.paymentAmount);

        // **공급자** 레이아웃에서 처리

        // 코멘트
        getViewDataBinding().commentTextView.setText(stayOutboundReceipt.comment);

        getViewDataBinding().receiptLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onScreenClick();
            }
        });
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
