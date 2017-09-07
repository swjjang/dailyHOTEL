package com.daily.dailyhotel.screen.home.gourmet.detail;

import android.app.Dialog;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetDetail;

import io.reactivex.Observable;

public interface GourmetDetailViewInterface extends BaseDialogViewInterface
{
    void setGourmetDetail(GourmetBookDateTime gourmetBookDateTime, GourmetDetail gourmetDetail, int trueReviewCount, int shownMenuCount);

    Observable<Boolean> getSharedElementTransition();

    void setInitializedImage(String url);

    void setInitializedTransLayout(String name, String url);

    void setSharedElementTransitionEnabled(boolean enabled, int gradientType);

    void setBottomButtonLayout(int status);

    void setDetailImageCaption(String caption);

    void showConciergeDialog(Dialog.OnDismissListener listener);

    void showShareDialog(Dialog.OnDismissListener listener);

    void setWishCount(int count);

    void setWishSelected(boolean selected);

    Observable<Boolean> showWishView(boolean myWish);

    void scrollTop();

    void scrollTopMenu();

    Observable<Boolean> openMoreMenuList();

    boolean isOpenedMoreMenuList();

    Observable<Boolean> closeMoreMenuList();
}
