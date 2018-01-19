package com.daily.dailyhotel.entity;


import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.util.DailyCalendar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class GourmetCart
{
    public String visitTime; // 방문 시간
    public int gourmetIndex;
    public String gourmetName;

    private GourmetBookDateTime mGourmetBookDateTime; // ISO-8601, 방문 날짜
    private LinkedHashMap<Integer, GourmetCartMenu> mOrderMenuMap = new LinkedHashMap<>();

    // 결제 화면에서 필요한 내용, GA를 위해서 필요한 항목
    public String gourmetCategory;
    public String imageUrl;

    public GourmetCart()
    {

    }

    public GourmetCart(JSONObject jsonObject)
    {
        if (jsonObject == null || jsonObject.length() == 0)
        {
            return;
        }

        try
        {
            visitTime = jsonObject.getString("visitTime");
            gourmetIndex = jsonObject.getInt("gourmetIndex");
            gourmetName = jsonObject.getString("gourmetName");
            mGourmetBookDateTime = new GourmetBookDateTime();
            mGourmetBookDateTime.setVisitDateTime(jsonObject.getString("gourmetBookDateTime"));

            JSONArray jsonArray = jsonObject.getJSONArray("menus");

            if (jsonArray != null)
            {
                int length = jsonArray.length();

                for (int i = 0; i < length; i++)
                {
                    GourmetCartMenu gourmetCartMenu = new GourmetCartMenu(jsonArray.getJSONObject(i));

                    mOrderMenuMap.put(gourmetCartMenu.index, gourmetCartMenu);
                }
            }

            gourmetCategory = jsonObject.getString("gourmetCategory");
            imageUrl = jsonObject.getString("imageUrl");
        } catch (Exception e)
        {
            ExLog.e(e.toString());
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

    private void plus(GourmetCartMenu gourmetCartMenu)
    {
        if (gourmetCartMenu == null)
        {
            return;
        }

        if (mOrderMenuMap.containsKey(gourmetCartMenu.index) == true)
        {
            gourmetCartMenu.count = mOrderMenuMap.get(gourmetCartMenu.index).count + 1;
        } else
        {
            gourmetCartMenu.count = 1;
        }

        mOrderMenuMap.put(gourmetCartMenu.index, gourmetCartMenu);
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
    public void setGourmetInformation(int gourmetIndex, String gourmetName, String visitDateTime, String visitTime)
    {
        this.gourmetIndex = gourmetIndex;
        this.gourmetName = gourmetName;

        setGourmetBookDateTime(visitDateTime);

        this.visitTime = visitTime;
    }

    /**
     *
     */
    public void setGourmetSubInformation(String category, String imageUrl)
    {
        this.gourmetCategory = category;
        this.imageUrl = imageUrl;
    }

    public void clear()
    {
        visitTime = null;
        gourmetIndex = 0;
        gourmetName = null;
        mGourmetBookDateTime = null;

        mOrderMenuMap.clear();
    }

    public int getMenuCount()
    {
        if (mOrderMenuMap == null)
        {
            return 0;
        }

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

    public int getMenuOrderCount(int menuIndex)
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

    public int[] getMenuSaleIndexes()
    {
        int[] menuIndexes = new int[getMenuCount()];
        int i = 0;

        for (GourmetCartMenu gourmetCartMenu : mOrderMenuMap.values())
        {
            menuIndexes[i++] = gourmetCartMenu.saleIndex;
        }

        return menuIndexes;
    }

    public int[] getCountPerMenu()
    {
        int[] counts = new int[getMenuCount()];

        int i = 0;

        for (GourmetCartMenu gourmetCartMenu : mOrderMenuMap.values())
        {
            counts[i++] = gourmetCartMenu.count;
        }

        return counts;
    }

    public int[] getDiscountPrices()
    {
        int[] discountPrices = new int[getMenuCount()];
        int i = 0;

        for (GourmetCartMenu gourmetCartMenu : mOrderMenuMap.values())
        {
            discountPrices[i++] = gourmetCartMenu.discountPrice;
        }

        return discountPrices;
    }

    public boolean equalsDay(String visitDay) throws Exception
    {
        return mGourmetBookDateTime != null ? DailyCalendar.compareDateDay(mGourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT), visitDay) == 0 : false;
    }

    public GourmetBookDateTime getGourmetBookDateTime()
    {
        return mGourmetBookDateTime;
    }

    public void setGourmetBookDateTime(String visitDateTime)
    {
        if (DailyTextUtils.isTextEmpty(visitDateTime) == true)
        {
            return;
        }

        if (mGourmetBookDateTime == null)
        {
            mGourmetBookDateTime = new GourmetBookDateTime();
        }

        try
        {
            mGourmetBookDateTime.setVisitDateTime(visitDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    public GourmetCartMenu getGourmetCartMenu(int menuIndex)
    {
        return mOrderMenuMap.get(menuIndex);
    }

    public Collection<GourmetCartMenu> getMenuList()
    {
        return mOrderMenuMap.values();
    }

    public LinkedHashMap<Integer, GourmetCartMenu> getMenuMap()
    {
        return mOrderMenuMap;
    }

    public void setMenuMap(LinkedHashMap<Integer, GourmetCartMenu> menuMap)
    {
        mOrderMenuMap = menuMap;
    }

    private GourmetCartMenu getGourmetCartMenu(GourmetMenu gourmetMenu)
    {
        if (gourmetMenu == null)
        {
            return null;
        }

        GourmetCartMenu gourmetCartMenu = new GourmetCartMenu();

        gourmetCartMenu.index = gourmetMenu.index;
        gourmetCartMenu.saleIndex = gourmetMenu.saleIndex;
        gourmetCartMenu.price = gourmetMenu.price;
        gourmetCartMenu.discountPrice = gourmetMenu.discountPrice;
        gourmetCartMenu.name = gourmetMenu.name;
        gourmetCartMenu.persons = gourmetMenu.persons;
        gourmetCartMenu.minimumOrderQuantity = gourmetMenu.minimumOrderQuantity;
        gourmetCartMenu.maximumOrderQuantity = gourmetMenu.maximumOrderQuantity;
        gourmetCartMenu.availableTicketNumbers = gourmetMenu.availableTicketNumbers;

        return gourmetCartMenu;
    }

    public JSONObject toJSONObject()
    {
        JSONObject jsonObject = new JSONObject();

        int size = mOrderMenuMap.size();

        if (size == 0)
        {
            return jsonObject;
        }

        try
        {
            jsonObject.put("visitTime", visitTime);
            jsonObject.put("gourmetIndex", gourmetIndex);
            jsonObject.put("gourmetName", gourmetName);

            if (mGourmetBookDateTime != null)
            {
                jsonObject.put("gourmetBookDateTime", mGourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT));
            } else
            {
                jsonObject.put("gourmetBookDateTime", null);
            }

            JSONArray jsonArray = new JSONArray();

            for (GourmetCartMenu gourmetCartMenu : mOrderMenuMap.values())
            {
                jsonArray.put(gourmetCartMenu.toJSONObject());
            }

            jsonObject.put("menus", jsonArray);

            jsonObject.put("gourmetCategory", gourmetCategory);
            jsonObject.put("imageUrl", imageUrl);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return jsonObject;
    }
}
