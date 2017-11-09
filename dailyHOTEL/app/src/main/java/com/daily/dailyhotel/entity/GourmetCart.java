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
    public int visitTime; // 방문 시간

    public int gourmetIndex;
    public String gourmetName;
    public String visitDateTime; // ISO-8601, 방문 날짜

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
}
