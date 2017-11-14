package com.daily.dailyhotel.entity;


import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class GourmetCart
{
    public int visitTime; // 방문 시간

    public int gourmetIndex;
    public String gourmetName;
    public String visitDateTime; // ISO-8601, 방문 날짜

    private Map<Integer, GourmetCartMenu> mOrderMenuMap = new HashMap<>();

    public Map<Integer, GourmetCartMenu> getMenus()
    {
        return mOrderMenuMap;
    }

    public void plus(GourmetCartMenu gourmetCartMenu)
    {
        if (gourmetCartMenu == null)
        {
            return;
        }

        if (mOrderMenuMap.containsKey(gourmetCartMenu.index) == true)
        {
            mOrderMenuMap.get(gourmetCartMenu.index).count++;
        } else
        {
            gourmetCartMenu.count = 1;
            mOrderMenuMap.put(gourmetCartMenu.index, gourmetCartMenu);
        }
    }

    public void plus(GourmetMenu gourmetMenu)
    {
        if (gourmetMenu == null)
        {
            return;
        }

        plus(getGourmetCartMenu(gourmetMenu));
    }

    public void minus(int menuIndex)
    {
        if (menuIndex < 0)
        {
            return;
        }

        if (mOrderMenuMap.containsKey(menuIndex) == true)
        {
            if (--mOrderMenuMap.get(menuIndex).count <= 0)
            {
                mOrderMenuMap.remove(menuIndex);
            }
        }
    }

    public void remove(int menuIndex)
    {
        if (menuIndex < 0)
        {
            return;
        }

        mOrderMenuMap.remove(menuIndex);
    }

    /**
     * @param gourmetName
     * @param visitDateTime 실제는 날짜 정보지만 ISO-8601 타입으로 받음
     */
    public void setGourmetInformation(int gourmetIndex, String gourmetName, String visitDateTime, int visitTime)
    {
        this.gourmetIndex = gourmetIndex;
        this.gourmetName = gourmetName;
        this.visitDateTime = visitDateTime;
        this.visitTime = visitTime;
    }

    public void clear()
    {
        visitTime = 0;
        gourmetIndex = 0;
        gourmetName = null;
        visitDateTime = null;

        mOrderMenuMap.clear();
    }

    public int getMenuCount()
    {
        return mOrderMenuMap.size();
    }

    public int getTotalCount()
    {
        if (mOrderMenuMap == null || mOrderMenuMap.size() == 0)
        {
            return 0;
        }

        int totalCount = 0;

        for (GourmetCartMenu gourmetCartMenu : mOrderMenuMap.values())
        {
            totalCount += gourmetCartMenu.count;
        }

        return totalCount;
    }

    public int getCount(int menuIndex)
    {
        return (menuIndex > 0 && mOrderMenuMap.containsKey(menuIndex)) ? mOrderMenuMap.get(menuIndex).count : 0;
    }

    public int getTotalPrice()
    {
        if (mOrderMenuMap == null || mOrderMenuMap.size() == 0)
        {
            return 0;
        }

        int totalPrice = 0;

        for (GourmetCartMenu gourmetCartMenu : mOrderMenuMap.values())
        {
            totalPrice += gourmetCartMenu.discountPrice * gourmetCartMenu.count;
        }

        return totalPrice;
    }

    public boolean equalsDay(String visitDay) throws Exception
    {
        return DailyCalendar.compareDateDay(visitDateTime, visitDay) == 0;
    }

    private GourmetCartMenu getGourmetCartMenu(GourmetMenu gourmetMenu)
    {
        if (gourmetMenu == null)
        {
            return null;
        }

        GourmetCartMenu gourmetCartMenu = new GourmetCartMenu();

        gourmetCartMenu.index = gourmetMenu.index;
        gourmetCartMenu.price = gourmetMenu.price;
        gourmetCartMenu.discountPrice = gourmetMenu.discountPrice;
        gourmetCartMenu.name = gourmetMenu.name;
        gourmetCartMenu.persons = gourmetMenu.persons;

        return gourmetCartMenu;
    }
}
