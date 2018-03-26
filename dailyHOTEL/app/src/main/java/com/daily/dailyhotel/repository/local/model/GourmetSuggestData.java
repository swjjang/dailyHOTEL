package com.daily.dailyhotel.repository.local.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.GourmetSuggest;

/**
 * Created by android_sam on 2018. 3. 15..
 */
@JsonObject
public class GourmetSuggestData
{
    @JsonField
    public String menuType; // 검색어 입력창에서 선택 된 메뉴 - 주로 Analytics 에서 사용,  선택된 메뉴가 필요할때 사용

    @JsonField
    public SuggestItemData suggestItemData;

    @JsonField
    public GourmetData gourmetData;

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

        public GourmetSuggest.SuggestItem getSuggestItem()
        {
            return new GourmetSuggest.SuggestItem(name);
        }
    }

    @JsonObject
    public static class GourmetData
    {
        @JsonField
        public int index;

        @JsonField
        public String name;

        @JsonField
        public int discount;

        @JsonField
        public boolean available;

        @JsonField
        public AreaGroupData areaGroup;

        public GourmetSuggest.Gourmet getGourmet()
        {
            GourmetSuggest.Gourmet gourmet = new GourmetSuggest.Gourmet();
            gourmet.index = index;
            gourmet.name = name;
            gourmet.discount = discount;
            gourmet.available = available;
            gourmet.areaGroup = areaGroup == null ? null : areaGroup.getAreaGroup();

            return gourmet;
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

        public GourmetSuggest.AreaGroup getAreaGroup()
        {
            GourmetSuggest.AreaGroup areaGroup = new GourmetSuggest.AreaGroup();
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

        public GourmetSuggest.Area getArea()
        {
            GourmetSuggest.Area area = new GourmetSuggest.Area();
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

        public GourmetSuggest.Direct getDirect()
        {
            GourmetSuggest.Direct direct = new GourmetSuggest.Direct(name);
            return direct;
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

        public GourmetSuggest.Location getLocation()
        {
            GourmetSuggest.Location location = new GourmetSuggest.Location();
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

        public GourmetSuggest.CampaignTag getCampaignTag()
        {
            GourmetSuggest.CampaignTag campaignTag = new GourmetSuggest.CampaignTag();
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

        public GourmetSuggest.Section getSection()
        {
            GourmetSuggest.Section section = new GourmetSuggest.Section(name);
            return section;
        }
    }

    public GourmetSuggest getSuggest()
    {
        GourmetSuggest.SuggestItem suggestItem;

        if (gourmetData != null)
        {
            suggestItem = gourmetData.getGourmet();
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

        GourmetSuggest.MenuType menuType1;
        try
        {
            menuType1 = GourmetSuggest.MenuType.valueOf(menuType);
        } catch (Exception e)
        {
            menuType1 = GourmetSuggest.MenuType.UNKNOWN;
        }

        return new GourmetSuggest(menuType1, suggestItem);
    }
}

