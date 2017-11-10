package com.daily.dailyhotel.screen.home.gourmet.detail.menus;

import android.animation.Animator;
import android.app.Dialog;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.GourmetMenu;

import java.util.List;

import io.reactivex.Observable;

public interface GourmetMenusInterface extends BaseDialogViewInterface
{
    void setMenuIndicator(int position, int totalCount);

    void setGourmetMenus(List<GourmetMenu> gourmetMenuList, int position);

    void setGuideVisible(boolean visible);

    Observable<Boolean> hideGuideAnimation();

    void setOperationTimes(List<Integer> operationTimeList);

    void setVisitTime(int time);

    Observable<Boolean> showOperationTimes(int selectedTimes);

    Observable<Boolean> hideOperationTimes();

    void setMenuOrderCount(int position, int orderCount);

    int getMenuCount();

    void showTimePickerDialog(List<Integer> operationTimeList, Dialog.OnDismissListener listener);
}
