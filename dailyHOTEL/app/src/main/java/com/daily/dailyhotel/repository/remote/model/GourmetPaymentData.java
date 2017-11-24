package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.GourmetPayment;
import com.daily.dailyhotel.entity.GourmetPaymentMenu;

import java.util.ArrayList;
import java.util.List;

@JsonObject
public class GourmetPaymentData
{
    @JsonField(name = "restaurantName")
    public String restaurantName;

    @JsonField(name = "businessName")
    public String businessName;

    @JsonField(name = "totalPrice")
    public int totalPrice;

    @JsonField(name = "items")
    public List<MenuData> items;

    public GourmetPaymentData()
    {

    }

    public GourmetPayment getGourmetPayment()
    {
        GourmetPayment gourmetPayment = new GourmetPayment();

        gourmetPayment.restaurantName = restaurantName;
        gourmetPayment.businessName = businessName;
        gourmetPayment.totalPrice = totalPrice;

        if (items != null)
        {
            List<GourmetPaymentMenu> gourmetPaymentMenuList = new ArrayList<>();

            for (MenuData menuData : items)
            {
                gourmetPaymentMenuList.add(menuData.getMenu());
            }

            gourmetPayment.setGourmetPaymentMenuList(gourmetPaymentMenuList);
        }

        return gourmetPayment;
    }

    @JsonObject
    static class MenuData
    {
        @JsonField(name = "saleRecoIdx")
        public int saleIndex;

        @JsonField(name = "ticketName")
        public String ticketName;

        @JsonField(name = "price")
        public int price;

        @JsonField(name = "count")
        public int count;

        @JsonField(name = "subTotalPrice")
        public int subTotalPrice;

        public GourmetPaymentMenu getMenu()
        {
            GourmetPaymentMenu gourmetPaymentMenu = new GourmetPaymentMenu();

            gourmetPaymentMenu.saleIndex = saleIndex;
            gourmetPaymentMenu.ticketName = ticketName;
            gourmetPaymentMenu.price = price;
            gourmetPaymentMenu.count = count;
            gourmetPaymentMenu.subTotalPrice = subTotalPrice;

            return gourmetPaymentMenu;
        }
    }
}
