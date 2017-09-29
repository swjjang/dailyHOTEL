package com.daily.dailyhotel.screen.mydaily.coupon;

import com.daily.base.BaseDialogViewInterface;
import com.twoheart.dailyhotel.model.Coupon;

import java.util.List;

/**
 * Created by iseung-won on 2017. 9. 28..
 */

public interface CouponHistoryViewInterface extends BaseDialogViewInterface
{
    void setData(List<Coupon> list);
}
