package com.daily.dailyhotel.screen.home.gourmet.detail;

import android.app.Dialog;
import android.view.View;
import android.widget.CheckBox;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetDetail;
import com.daily.dailyhotel.entity.GourmetMenu;
import com.daily.dailyhotel.entity.TrueAwards;

import java.util.List;

import io.reactivex.Observable;

public interface GourmetDetailViewInterface extends BaseDialogViewInterface
{
    void setGourmetDetail(GourmetBookDateTime gourmetBookDateTime, GourmetDetail gourmetDetail//
        , List<String> operationTimeList, int trueReviewCount, int shownMenuCount);

    Observable<Boolean> getSharedElementTransition(int gradientType);

    void setInitializedImage(String url);

    void setInitializedTransLayout(String name, String url);

    void setTransitionVisible(boolean visible);

    void setSharedElementTransitionEnabled(boolean enabled, int gradientType);

    void setBottomButtonLayout(int status);

    void showConciergeDialog(Dialog.OnDismissListener listener);

    void showShareDialog(Dialog.OnDismissListener listener);

    void setWishCount(int count);

    void setWishSelected(boolean selected);

    void showWishTooltip();

    void hideWishTooltip();

    Observable<Boolean> showWishView(boolean myWish);

    void setTrueVRVisible(boolean visible);

    void showTrueVRDialog(CheckBox.OnCheckedChangeListener checkedChangeListener, View.OnClickListener positiveListener, Dialog.OnDismissListener onDismissListener);

    void scrollTop();

    void scrollTopMenu();

    Observable<Boolean> openMoreMenuList();

    boolean isOpenedMoreMenuList();

    Observable<Boolean> closeMoreMenuList();

    void setMenus(List<GourmetMenu> gourmetMenuList, int shownMenuCount);

    void setToolbarCartMenusVisible(boolean visible);

    void setToolbarCartMenusCount(int count);

    void performVisitTimeClick(String time);

    void performFirstMenuClick();

    void showTrueAwardsDialog(TrueAwards trueAwards, Dialog.OnDismissListener onDismissListener);
}
