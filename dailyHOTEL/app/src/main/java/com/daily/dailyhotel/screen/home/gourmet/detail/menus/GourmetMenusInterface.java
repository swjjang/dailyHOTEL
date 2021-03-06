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

    void setOperationTimes(List<String> operationTimeList);

    void setVisitTime(String time);

    Observable<Boolean> openOperationTimes(String selectedTimes);

    Observable<Boolean> closeOperationTimes();

    void setMenuOrderCount(int menuIndex, int menuOrderCount, int minimumOrderQuantity, int maximumOrderQuantity, int saleOrderQuantity);

    void showTimePickerDialog(List<String> operationTimeList, int menuIndex);

    void setSummeryCart(String text, int totalCount, int totalPrice);

    void setCartVisible(boolean visible);

    void setGourmetCart(GourmetCart gourmetCart);

    void setGourmetCartMenu(int menuIndex, int menuOrderCount, int minimumOrderQuantity, int maximumOrderQuantity, int saleOrderQuantity);

    Observable<Boolean> openCartMenus(int gourmetMenuCount);

    Observable<Boolean> closeCartMenus(int gourmetMenuCount);

    void notifyGourmetMenusChanged();

    Observable<Boolean> removeGourmetCartMenu(int menuIndex);
}
