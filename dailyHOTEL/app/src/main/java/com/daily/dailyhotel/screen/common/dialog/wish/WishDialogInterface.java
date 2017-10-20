package com.daily.dailyhotel.screen.common.dialog.wish;

import com.daily.base.BaseDialogViewInterface;

import io.reactivex.Observable;

public interface WishDialogInterface extends BaseDialogViewInterface
{
    Observable<Boolean> showWishView(boolean myWish);
}
