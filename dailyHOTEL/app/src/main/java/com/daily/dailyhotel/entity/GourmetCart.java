package com.daily.dailyhotel.entity;


import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class GourmetCart
{
    public int orderTime;

    public String gourmetName;
    public String imageUrl;
    public String visitDateTime; // ISO-8601
    public String category;

    private List<GourmetCartMenu> mOrderMenuList = new ArrayList<>();

    public List<GourmetCartMenu> getMenus()
    {
        return mOrderMenuList;
    }

    public void addMenu(GourmetCartMenu menu)
    {
        if (menu == null)
        {
            return;
        }

        mOrderMenuList.add(menu);
    }

    public void removeMenu(int index)
    {
        if (index < 0)
        {
            return;
        }

        for (GourmetCartMenu menu : mOrderMenuList)
        {
            if (menu.index == index)
            {
                mOrderMenuList.remove(menu);
                break;
            }
        }
    }

    /**
     *
     * @param gourmetName
     * @param imageUrl
     * @param visitDateTime 실제는 날짜 정보지만 ISO-8601 타입으로 받음
     * @param category
     */
    public void setGourmetInformation(String gourmetName, String imageUrl, String visitDateTime, String category)
    {
        this.gourmetName = gourmetName;
        this.imageUrl = imageUrl;
        this.visitDateTime = visitDateTime;
        this.category = category;
    }

    public void removeAllMenu()
    {
        mOrderMenuList.clear();
    }

    public int getMenuCount()
    {
        return mOrderMenuList.size();
    }

    public boolean equalsDay(String visitDay) throws Exception
    {
        return DailyCalendar.compareDateDay(visitDateTime, visitDay) == 0;
    }

    public boolean equalsOrderTime(int orderTime)
    {
        return this.orderTime == orderTime;
    }
}
