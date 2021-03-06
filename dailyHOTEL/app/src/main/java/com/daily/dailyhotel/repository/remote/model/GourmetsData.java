package com.daily.dailyhotel.repository.remote.model;

import android.content.Context;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.Gourmet;
import com.daily.dailyhotel.entity.GourmetFilter;
import com.daily.dailyhotel.entity.Gourmets;
import com.daily.dailyhotel.entity.Sticker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by android_sam on 2017. 6. 15..
 */
@JsonObject
public class GourmetsData
{
    @JsonField(name = "gourmetSales")
    public List<GourmetData> gourmetDataList;

    @JsonField(name = "gourmetSalesCount")
    public int gourmetSalesCount;

    @JsonField(name = "imgUrl")
    public String imageUrl;

    @JsonField(name = "searchMaxCount")
    public int searchMaxCount;

    @JsonField(name = "filter")
    public Filter filter;

    @JsonField(name = "stickers")
    public List<StickerData> stickerList;

    public Gourmets getGourmets(Context context)
    {
        Gourmets gourmets = new Gourmets();

        gourmets.totalCount = gourmetSalesCount;
        gourmets.searchMaxCount = searchMaxCount;

        if (gourmetDataList != null || gourmetDataList.size() > 0)
        {
            List<Gourmet> gourmetList = new ArrayList<>();

            boolean lowDpi = false;

            if (ScreenUtils.getScreenWidth(context) <= Sticker.DEFAULT_SCREEN_WIDTH)
            {
                lowDpi = true;
            }

            for (GourmetData gourmetData : gourmetDataList)
            {
                Gourmet gourmet = gourmetData.getGourmet(imageUrl);
                gourmet.stickerUrl = getStickerUrl(gourmet.stickerIndex, stickerList, lowDpi);

                gourmetList.add(gourmet);
            }

            gourmets.setGourmetList(gourmetList);
        }

        if (filter != null)
        {
            gourmets.setCategoryMap(filter.getCategory());
        }

        return gourmets;
    }

    @JsonObject
    static class Filter
    {
        @JsonField(name = "categories")
        public List<Category> categoryList;

        public LinkedHashMap<String, GourmetFilter.Category> getCategory()
        {
            LinkedHashMap<String, GourmetFilter.Category> categoryMap = new LinkedHashMap<>();

            Comparator<Category> comparator = new Comparator<Category>()
            {
                public int compare(Category category1, Category category2)
                {
                    return category1.sequence - category2.sequence;
                }
            };

            if (categoryList != null)
            {
                Collections.sort(categoryList, comparator);

                for (Category category : categoryList)
                {
                    categoryMap.put(category.name, category.getCategory());
                }
            }

            return categoryMap;
        }
    }

    @JsonObject
    static class Category
    {
        @JsonField(name = "name")
        public String name;

        @JsonField(name = "code")
        public int code;

        @JsonField(name = "sequence")
        public int sequence;

        public GourmetFilter.Category getCategory()
        {
            GourmetFilter.Category category = new GourmetFilter.Category();

            category.name = name;
            category.code = code;
            category.sequence = sequence;

            return category;
        }
    }

    @JsonObject
    static class StickerData
    {
        @JsonField(name = "idx")
        public int index;

        @JsonField(name = "defaultImageUrl")
        public String defaultImageUrl;

        @JsonField(name = "lowResolutionImageUrl")
        public String lowResolutionImageUrl;

        public StickerData()
        {

        }
    }

    private String getStickerUrl(int index, List<StickerData> stickerList, boolean lowDpi)
    {
        if (index < 0 || stickerList == null || stickerList.size() == 0)
        {
            return null;
        }

        for (StickerData sticker : stickerList)
        {
            if (sticker.index == index)
            {
                return lowDpi ? sticker.lowResolutionImageUrl : sticker.defaultImageUrl;
            }
        }

        return null;
    }
}
