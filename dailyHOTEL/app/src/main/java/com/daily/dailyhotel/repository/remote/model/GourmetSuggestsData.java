package com.daily.dailyhotel.repository.remote.model;

import android.content.Context;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.GourmetSuggestV2;
import com.twoheart.dailyhotel.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_sam on 2018. 3. 2..
 */
@JsonObject
public class GourmetSuggestsData
{
    @JsonField(name = "gourmet")
    public List<GourmetData> gourmetList;

    @JsonField(name = "region")
    public List<ProvinceData> provinceList;

    @JsonObject
    static class GourmetData
    {
        @JsonField(name = "idx")
        public int index;

        @JsonField(name = "name")
        public String name;

        @JsonField(name = "discount")
        public int discount;

        @JsonField(name = "availableTickets")
        public int availableTickets;

        @JsonField(name = "isExpired")
        public boolean isExpired;

        @JsonField(name = "minimumOrderQuantity")
        public int minimumOrderQuantity;

        @JsonField(name = "province")
        public ProvinceData provinceData;

        public GourmetSuggestV2.Gourmet getGourmet()
        {
            GourmetSuggestV2.Gourmet gourmet = new GourmetSuggestV2.Gourmet();
            gourmet.index = index;
            gourmet.name = name;
            gourmet.discount = discount;
            gourmet.availableTickets = availableTickets;
            gourmet.isExpired = isExpired;
            gourmet.minimumOrderQuantity = minimumOrderQuantity;
            gourmet.province = provinceData == null ? null : provinceData.getProvince();

            return gourmet;
        }
    }

    @JsonObject
    static class ProvinceData
    {
        @JsonField(name = "idx")
        public int index;

        @JsonField(name = "name")
        public String name;

        @JsonField(name = "area")
        public AreaData area;

        public GourmetSuggestV2.Province getProvince()
        {
            GourmetSuggestV2.Province province = new GourmetSuggestV2.Province();
            province.index = index;
            province.name = name;
            province.area = area == null ? null : area.getArea();

            return province;
        }
    }

    @JsonObject
    static class AreaData
    {
        @JsonField(name = "idx")
        public int index;

        @JsonField(name = "name")
        public String name;

        public GourmetSuggestV2.Area getArea()
        {
            GourmetSuggestV2.Area area = new GourmetSuggestV2.Area();
            area.index = index;
            area.name = name;

            return area;
        }
    }

    public List<GourmetSuggestV2> getSuggestList(Context context)
    {
        List<GourmetSuggestV2> list = new ArrayList<>();

        if (context == null)
        {
            return list;
        }

        if (gourmetList != null && gourmetList.size() > 0)
        {
            list.add(new GourmetSuggestV2(GourmetSuggestV2.MENU_TYPE_SUGGEST, new GourmetSuggestV2.Section(context.getString(R.string.label_search_suggest_type_gourmet))));

            for (GourmetData gourmetData : gourmetList)
            {
                GourmetSuggestV2.Gourmet gourmet = gourmetData.getGourmet();

                list.add(new GourmetSuggestV2(GourmetSuggestV2.MENU_TYPE_SUGGEST, gourmet));
            }
        }

        if (provinceList != null && provinceList.size() > 0)
        {
            list.add(new GourmetSuggestV2(GourmetSuggestV2.MENU_TYPE_SUGGEST, new GourmetSuggestV2.Section(context.getString(R.string.label_search_suggest_type_region))));

            for (ProvinceData provinceData : provinceList)
            {
                GourmetSuggestV2.Province province = provinceData.getProvince();

                list.add(new GourmetSuggestV2(GourmetSuggestV2.MENU_TYPE_SUGGEST, province));
            }
        }

        return list;
    }
}
