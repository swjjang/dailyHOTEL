package com.daily.dailyhotel.repository.local.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StaySuggest;

/**
 * Created by android_sam on 2018. 3. 16..
 */
@JsonObject
public class StaySuggestData
{
    @JsonField
    public String menuType; // 검색어 입력창에서 선택 된 메뉴 - 주로 Analytics 에서 사용,  선택된 메뉴가 필요할때 사용

    @JsonField
    public SuggestItemData suggestItemData;

    @JsonField
    public StationData stationData;

    @JsonField
    public StayData stayData;

    @JsonField
    public AreaGroupData areaGroupData;

    @JsonField
    public LocationData locationData;

    @JsonField
    public DirectData directData;

    @JsonField
    public CampaignTagData campaignTagData;

    @JsonField
    public SectionData sectionData;

    @JsonObject
    public static class SuggestItemData
    {
        @JsonField
        public String name;

        public StaySuggest.SuggestItem getSuggestItem()
        {
            return new StaySuggest.SuggestItem(name);
        }
    }

    @JsonObject
    public static class StationData
    {
        @JsonField
        public int index;

        @JsonField
        public String region;

        @JsonField
        public String line;

        @JsonField
        public String name;

        public StaySuggest.Station getStation()
        {
            StaySuggest.Station station = new StaySuggest.Station();
            station.index = index;
            station.name = name;
            station.line = line;
            station.region = region;

            return station;
        }
    }

    @JsonObject
    public static class StayData
    {
        @JsonField
        public int index;

        @JsonField
        public String name;

        @JsonField
        public int discountAvg;

        @JsonField
        public boolean available;

        @JsonField
        public AreaGroupData areaGroup;

        public StaySuggest.Stay getStay()
        {
            StaySuggest.Stay stay = new StaySuggest.Stay();
            stay.index = index;
            stay.name = name;
            stay.discountAvg = discountAvg;
            stay.available = available;
            stay.areaGroup = areaGroup == null ? null : areaGroup.getAreaGroup();

            return stay;
        }
    }

    @JsonObject
    public static class AreaGroupData
    {
        @JsonField
        public int index;

        @JsonField
        public String name;

        @JsonField
        public AreaData area;

        public StaySuggest.AreaGroup getAreaGroup()
        {
            StaySuggest.AreaGroup areaGroup = new StaySuggest.AreaGroup();
            areaGroup.index = index;
            areaGroup.name = name;
            areaGroup.area = area == null ? null : area.getArea();

            return areaGroup;
        }
    }

    @JsonObject
    public static class AreaData
    {
        @JsonField
        public int index;

        @JsonField
        public String name;

        public StaySuggest.Area getArea()
        {
            StaySuggest.Area area = new StaySuggest.Area();
            area.index = index;
            area.name = name;

            return area;
        }
    }

    @JsonObject
    public static class DirectData
    {
        @JsonField
        public String name;

        public StaySuggest.Direct getDirect()
        {
            return new StaySuggest.Direct(name);
        }
    }

    @JsonObject
    public static class LocationData
    {
        @JsonField
        public double latitude;

        @JsonField
        public double longitude;

        @JsonField
        public String address;

        @JsonField
        public String name;

        public StaySuggest.Location getLocation()
        {
            StaySuggest.Location location = new StaySuggest.Location();
            location.name = name;
            location.address = address;
            location.latitude = latitude;
            location.longitude = longitude;

            return location;
        }
    }

    @JsonObject
    public static class CampaignTagData
    {
        @JsonField
        public int index;

        @JsonField
        public String startDate; // ISO-8601

        @JsonField
        public String endDate; // ISO-8601

        @JsonField
        public String serviceType;

        @JsonField
        public String name;

        public StaySuggest.CampaignTag getCampaignTag()
        {
            StaySuggest.CampaignTag campaignTag = new StaySuggest.CampaignTag();
            campaignTag.index = index;
            campaignTag.startDate = startDate;
            campaignTag.endDate = endDate;
            campaignTag.serviceType = serviceType;
            campaignTag.name = name;

            return campaignTag;
        }
    }

    // 서버에서 받은 타입이 아님, 리스트 노출용 섹션
    @JsonObject
    public static class SectionData
    {
        @JsonField
        public String name;

        public StaySuggest.Section getSection()
        {
            return new StaySuggest.Section(name);
        }
    }

    public StaySuggest getSuggest()
    {
        StaySuggest.SuggestItem suggestItem;

        if (stationData != null)
        {
            suggestItem = stationData.getStation();
        } else if (stayData != null)
        {
            suggestItem = stayData.getStay();
        } else if (areaGroupData != null)
        {
            suggestItem = areaGroupData.getAreaGroup();
        } else if (locationData != null)
        {
            suggestItem = locationData.getLocation();
        } else if (directData != null)
        {
            suggestItem = directData.getDirect();
        } else if (campaignTagData != null)
        {
            suggestItem = campaignTagData.getCampaignTag();
        } else if (sectionData != null)
        {
            suggestItem = sectionData.getSection();
        } else
        {
            suggestItem = suggestItemData.getSuggestItem();
        }

        StaySuggest.MenuType menuType1;
        try
        {
            menuType1 = StaySuggest.MenuType.valueOf(menuType);
        } catch (Exception e)
        {
            menuType1 = StaySuggest.MenuType.UNKNOWN;
        }

        return new StaySuggest(menuType1, suggestItem);
    }
}
