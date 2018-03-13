package com.daily.dailyhotel.entity;

import com.daily.base.util.DailyTextUtils;

import java.io.Serializable;

/**
 * Created by android_sam on 2018. 3. 5..
 */

public class StaySuggestV2
{
    public interface Category
    {
        int STATION = 1;
        int STAY = 2;
        int PROVINCE = 3;
        int DIRECT = 4;
        int LOCATION = 5;
        int CAMPAIGN_TAG = 6;
        int SECTION = 7;
    }

    public interface MenuType
    {
        int DIRECT = 1;
        int LOCATION = 2;
        int RECENTLY_SEARCH = 3;
        int RECENTLY_STAY = 4;
        int SUGGEST = 5;
        int CAMPAIGN_TAG = 6;
    }

    public int menuType; // 검색어 입력창에서 선택 된 메뉴 - 주로 Analytics 에서 사용,  선택된 메뉴가 필요할때 사용
    public SuggestItem suggestItem;
    //    public int category; // 카테고리 비교 용
    //    public String text1;
    //    public String text2;

    public StaySuggestV2()
    {
    }

    public StaySuggestV2(int menuType, SuggestItem suggestItem)
    {
        this.menuType = menuType;
        this.suggestItem = suggestItem;
        //        this.category = getCategory();
        //        this.text1 = getText1();
        //        this.text2 = getText2();
    }

    public int getCategory()
    {
        if (suggestItem == null)
        {
            return 0;
        }

        if (suggestItem instanceof Stay)
        {
            return Category.STAY;
        } else if (suggestItem instanceof Station)
        {
            return Category.STATION;

        } else if (suggestItem instanceof Province)
        {
            return Category.PROVINCE;
        } else if (suggestItem instanceof Direct)
        {
            return Category.DIRECT;
        } else if (suggestItem instanceof Location)
        {
            return Category.LOCATION;
        } else if (suggestItem instanceof CampaignTag)
        {
            return Category.CAMPAIGN_TAG;
        } else if (suggestItem instanceof Section)
        {
            return Category.SECTION;
        }

        return 0;
    }

    public String getText1()
    {
        if (suggestItem == null)
        {
            return null;
        }

        if (suggestItem instanceof Stay)
        {
            return suggestItem.name;
        } else if (suggestItem instanceof Station)
        {
            return ((Station) suggestItem).getDisplayName();
        } else if (suggestItem instanceof Province)
        {
            Province province = (Province) suggestItem;
            if (province.area == null)
            {
                return province.name;
            } else
            {
                Area area = province.area;
                if (area == null || DailyTextUtils.isTextEmpty(area.name))
                {
                    return province.name;
                }

                return area.name;
            }
        } else if (suggestItem instanceof Direct)
        {
            return suggestItem.name;
        } else if (suggestItem instanceof Location)
        {
            return suggestItem.name;
        } else if (suggestItem instanceof CampaignTag)
        {
            return suggestItem.name;
        } else if (suggestItem instanceof Section)
        {
            return suggestItem.name;
        }

        return null;
    }

    public String getText2()
    {
        if (suggestItem == null)
        {
            return null;
        }

        if (suggestItem instanceof Stay)
        {
            Province province = ((Stay) suggestItem).province;
            return province.name;
        } else if (suggestItem instanceof Station)
        {
            return ((Station) suggestItem).region;
        } else if (suggestItem instanceof Province)
        {
            Province province = (Province) suggestItem;
            if (province.area == null)
            {
                return null;
            } else
            {
                Area area = province.area;
                if (area == null || DailyTextUtils.isTextEmpty(area.name))
                {
                    return null;
                }

                return province.name;
            }
        } else if (suggestItem instanceof Direct)
        {
            return null;
        } else if (suggestItem instanceof Location)
        {
            return ((Location) suggestItem).address;
        } else if (suggestItem instanceof CampaignTag)
        {
            return null;
        } else if (suggestItem instanceof Section)
        {
            return null;
        }

        return null;
    }

    @SuppressWarnings("serial")
    public static class SuggestItem implements Serializable
    {
        public String name;

        public SuggestItem()
        {

        }

        public SuggestItem(String name)
        {
            this.name = name;
        }
    }

    public static class Station extends SuggestItem
    {
        public int index;
        public String region;
        public String line;
        //        public String name;

        public String getDisplayName()
        {
            return "[" + line + "] " + name;
        }
    }

    public static class Stay extends SuggestItem
    {
        public int index;
        //        public String name;
        public int discountAvg;
        public boolean available;
        public Province province;

        public String getProvinceName()
        {
            return province == null ? null : province.getProvinceName();
        }
    }

    public static class Province extends SuggestItem
    {
        public int index;
        //        public String name;
        public Area area;

        public String getProvinceName()
        {
            return area == null ? name : area.name;
        }
    }

    public static class Area extends SuggestItem
    {
        public int index;
        //        public int name;
    }

    public static class Direct extends SuggestItem
    {
        //        public String name;

        public Direct(String name)
        {
            super(name);
        }
    }

    public static class Location extends SuggestItem
    {
        public double latitude;
        public double longitude;
        public String address;
        //        public String name;
    }

    public static class CampaignTag extends SuggestItem
    {
        public int index;
        public String startDate; // ISO-8601
        public String endDate; // ISO-8601
        //        public String campaignTag; // 이 항목은 name 으로 대체
        public String serviceType;
    }

    // 서버에서 받은 타입이 아님, 리스트 노출용 섹션
    public static class Section extends SuggestItem
    {
        //        public String name;
        public Section(String title)
        {
            super(title);
        }
    }
}
