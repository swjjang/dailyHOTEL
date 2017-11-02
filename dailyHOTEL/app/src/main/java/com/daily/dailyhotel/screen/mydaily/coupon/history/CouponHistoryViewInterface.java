package com.daily.dailyhotel.screen.mydaily.coupon.history;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.ObjectItem;
import com.twoheart.dailyhotel.model.Coupon;

import java.util.List;

/**
 * Created by android_sam on 2017. 9. 28..
 */

public interface CouponHistoryViewInterface extends BaseDialogViewInterface
{
    void setData(List<ObjectItem> list);
}
