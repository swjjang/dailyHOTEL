package com.daily.dailyhotel.screen.home.gourmet.detail.menus;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.GourmetMenu;

import java.util.List;

public interface GourmetMenusInterface extends BaseDialogViewInterface
{
    void setSubTitle(String text);

    void setGourmetMenus(List<GourmetMenu> gourmetMenuList, int position);
}
