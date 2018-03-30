package com.daily.dailyhotel.screen.home.stay.outbound.payment.coupon;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseDialogViewInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.Coupon;

import java.util.List;

public interface SelectStayOutboundCouponDialogInterface
{
    interface ViewInterface extends BaseDialogViewInterface
    {
        void setVisible(boolean visible);

        void setCouponListDialog(String title, List<Coupon> couponList);
    }

    interface OnEventListener extends OnBaseEventListener
    {
        void onConfirm(Coupon coupon);

        void onDownloadCouponClick(Coupon coupon);
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
    }
}
