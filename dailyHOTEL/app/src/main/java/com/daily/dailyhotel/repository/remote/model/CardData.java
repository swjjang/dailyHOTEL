package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.Card;

@JsonObject
public class CardData
{
    @JsonField(name = "card_name")
    public String card_name;

    @JsonField(name = "print_cardno")
    public String print_cardno;

    @JsonField(name = "billkey")
    public String billkey;

    @JsonField(name = "cardcd")
    public String cardcd;

    public CardData()
    {

    }

    public Card getCard()
    {
        Card card = new Card();
        card.name = card_name;
        card.number = print_cardno;
        card.billkey = billkey;
        card.cd = cardcd;

        return card;
    }
}
