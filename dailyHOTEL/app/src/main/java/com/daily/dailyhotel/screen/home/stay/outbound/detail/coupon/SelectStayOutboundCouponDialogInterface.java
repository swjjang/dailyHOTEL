package com.daily.dailyhotel.screen.home.stay.outbound.detail.coupon;

import android.view.View;

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

        void setCouponListDialog(String title, List<Coupon> couponList, View.OnClickListener confirmListener);
    }

    interface OnEventListener extends OnBaseEventListener
    {
        void onDownloadCouponClick(Coupon coupon);
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
    }
}
