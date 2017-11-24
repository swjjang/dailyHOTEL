package com.daily.dailyhotel.screen.home.gourmet.detail.menus;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.GourmetCart;
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

    Observable<Boolean> openOperationTimes(int selectedTimes);

    Observable<Boolean> closeOperationTimes();

    void setMenuOrderCount(int menuIndex, int menuOrderCount, int minimumOrderQuantity, int maximumOrderQuantity, int saleOrderQuantity);

    void showTimePickerDialog(List<Integer> operationTimeList, int menuIndex);

    void setSummeryCart(String text, int totalCount, int totalPrice);

    void setCartVisible(boolean visible);

    void setGourmetCart(GourmetCart gourmetCart);

    void setGourmetCartMenu(int menuIndex, int menuOrderCount, int minimumOrderQuantity, int maximumOrderQuantity, int saleOrderQuantity);

    Observable<Boolean> openCartMenus(GourmetCart gourmetCart);

    Observable<Boolean> closeCartMenus();

    void notifyGourmetMenusChanged();
}
