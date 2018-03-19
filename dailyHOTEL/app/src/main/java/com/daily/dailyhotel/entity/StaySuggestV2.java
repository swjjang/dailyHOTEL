package com.daily.dailyhotel.entity;

import android.support.annotation.NonNull;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.repository.local.model.StaySuggestData;

import java.io.Serializable;

/**
 * Created by android_sam on 2018. 3. 5..
 */

public class StaySuggestV2
{
    public enum SuggestType
    {
        UNKNOWN,
        STATION,
        STAY,
        AREA_GROUP,
        DIRECT,
        LOCATION,
        CAMPAIGN_TAG,
        SECTION
    }

    public enum MenuType
    {
        UNKNOWN,
        DIRECT,
        LOCATION,
        RECENTLY_SEARCH,
        RECENTLY_STAY,
        SUGGEST,
        CAMPAIGN_TAG
    }

    public MenuType menuType; // 검색어 입력창에서 선택 된 메뉴 - 주로 Analytics 에서 사용,  선택된 메뉴가 필요할때 사용
    private SuggestItem suggestItem;
    //    public int suggestType; // 카테고리 비교 용
    //    public String text1;
    //    public String text2;

    public StaySuggestV2(MenuType menuType, @NonNull SuggestItem suggestItem)
    {
        this.menuType = menuType;
        this.suggestItem = suggestItem;
        //        this.suggestType = getSuggestType();
        //        this.text1 = getText1();
        //        this.text2 = getText2();
    }

    public SuggestType getSuggestType()
    {
        if (suggestItem == null)
        {
            return SuggestType.UNKNOWN;
        }

        if (suggestItem instanceof Stay)
        {
            return SuggestType.STAY;
        } else if (suggestItem instanceof Station)
        {
            return SuggestType.STATION;

        } else if (suggestItem instanceof AreaGroup)
        {
            return SuggestType.AREA_GROUP;
        } else if (suggestItem instanceof Direct)
        {
            return SuggestType.DIRECT;
        } else if (suggestItem instanceof Location)
        {
            return SuggestType.LOCATION;
        } else if (suggestItem instanceof CampaignTag)
        {
            return SuggestType.CAMPAIGN_TAG;
        } else if (suggestItem instanceof Section)
        {
            return SuggestType.SECTION;
        }

        return SuggestType.UNKNOWN;
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
        } else if (suggestItem instanceof AreaGroup)
        {
            AreaGroup areaGroup = (AreaGroup) suggestItem;
            if (areaGroup.area == null)
            {
                return areaGroup.name;
            } else
            {
                Area area = areaGroup.area;
                if (area == null || DailyTextUtils.isTextEmpty(area.name))
                {
                    return areaGroup.name;
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

        return suggestItem.name;
    }

    public String getText2()
    {
        if (suggestItem == null)
        {
            return null;
        }

        if (suggestItem instanceof Stay)
        {
            AreaGroup areaGroup = ((Stay) suggestItem).areaGroup;
            return areaGroup.name;
        } else if (suggestItem instanceof Station)
        {
            return ((Station) suggestItem).region;
        } else if (suggestItem instanceof AreaGroup)
        {
            AreaGroup areaGroup = (AreaGroup) suggestItem;
            if (areaGroup.area == null)
            {
                return null;
            } else
            {
                Area area = areaGroup.area;
                if (area == null || DailyTextUtils.isTextEmpty(area.name))
                {
                    return null;
                }

                return areaGroup.name;
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

    public SuggestItem getSuggestItem()
    {
        return suggestItem;
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

        public StaySuggestData.SuggestItemData getSuggestItemData()
        {
            StaySuggestData.SuggestItemData data = new StaySuggestData.SuggestItemData();
            data.name = name;

            return data;
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
            return name + "(" + line + ")";
            //            return "[" + line + "] " + name;
        }

        public StaySuggestData.StationData getStationData()
        {
            StaySuggestData.StationData data = new StaySuggestData.StationData();
            data.name = name;
            data.index = index;
            data.region = region;
            data.line = line;

            return data;
        }
    }

    public static class Stay extends SuggestItem
    {
        public int index;
        //        public String name;
        public int discountAvg;
        public boolean available;
        public AreaGroup areaGroup;

        public String getAreaGroupName()
        {
            return areaGroup == null ? null : areaGroup.name;
        }

        public StaySuggestData.StayData getStayData()
        {
            StaySuggestData.StayData data = new StaySuggestData.StayData();
            data.name = name;
            data.index = index;
            data.discountAvg = discountAvg;
            data.available = available;
            data.areaGroup = areaGroup == null ? null : areaGroup.getAreaGroupData();

            return data;
        }
    }

    public static class AreaGroup extends SuggestItem
    {
        public int index;
        //        public String name;
        public Area area;

        public String getDisplayName()
        {
            return area == null ? name : area.name;
        }

        public StaySuggestData.AreaGroupData getAreaGroupData()
        {
            StaySuggestData.AreaGroupData data = new StaySuggestData.AreaGroupData();
            data.name = name;
            data.index = index;
            data.area = area == null ? null : area.getAreaData();

            return data;
        }
    }

    public static class Area extends SuggestItem
    {
        public int index;
        //        public String name;

        public StaySuggestData.AreaData getAreaData()
        {
            StaySuggestData.AreaData data = new StaySuggestData.AreaData();
            data.name = name;
            data.index = index;

            return data;
        }
    }

    public static class Direct extends SuggestItem
    {
        //        public String name;

        public Direct(String name)
        {
            super(name);
        }

        public StaySuggestData.DirectData getDirectData()
        {
            StaySuggestData.DirectData data = new StaySuggestData.DirectData();
            data.name = name;

            return data;
        }
    }

    public static class Location extends SuggestItem
    {
        public double latitude;
        public double longitude;
        public String address;
        //        public String name;

        public StaySuggestData.LocationData getLocationData()
        {
            StaySuggestData.LocationData data = new StaySuggestData.LocationData();
            data.name = name;
            data.address = address;
            data.latitude = latitude;
            data.longitude = longitude;

            return data;
        }
    }

    public static class CampaignTag extends SuggestItem
    {
        public int index;
        public String startDate; // ISO-8601
        public String endDate; // ISO-8601
        //        public String campaignTag; // 이 항목은 name 으로 대체
        public String serviceType;

        public StaySuggestData.CampaignTagData getCampaignTagData()
        {
            StaySuggestData.CampaignTagData data = new StaySuggestData.CampaignTagData();
            data.name = name;
            data.index = index;
            data.startDate = startDate;
            data.endDate = endDate;
            data.serviceType = serviceType;

            return data;
        }
    }

    // 서버에서 받은 타입이 아님, 리스트 노출용 섹션
    public static class Section extends SuggestItem
    {
        //        public String name;
        public Section(String title)
        {
            super(title);
        }

        public StaySuggestData.SectionData getSectionData()
        {
            StaySuggestData.SectionData data = new StaySuggestData.SectionData();
            data.name = name;

            return data;
        }
    }

    public StaySuggestData getSuggestData()
    {
        StaySuggestData data = new StaySuggestData();
        data.menuType = menuType.name();

        switch (getSuggestType())
        {
            case STATION:
            {
                data.stationData = ((Station) suggestItem).getStationData();
                break;
            }

            case STAY:
            {
                data.stayData = ((Stay) suggestItem).getStayData();
                break;
            }

            case AREA_GROUP:
            {
                data.areaGroupData = ((AreaGroup) suggestItem).getAreaGroupData();
                break;
            }

            case LOCATION:
            {
                data.locationData = ((Location) suggestItem).getLocationData();
                break;
            }

            case DIRECT:
            {
                data.directData = ((Direct) suggestItem).getDirectData();
                break;
            }

            case CAMPAIGN_TAG:
            {
                data.campaignTagData = ((CampaignTag) suggestItem).getCampaignTagData();
                break;
            }

            case SECTION:
            {
                data.sectionData = ((Section) suggestItem).getSectionData();
                break;
            }

            default:
            {
                data.suggestItemData = suggestItem.getSuggestItemData();
                break;
            }
        }

        return data;
    }
}
