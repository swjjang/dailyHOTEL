package com.twoheart.dailyhotel.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.R;

public class CreditCard implements Parcelable
{
    public String name;
    public String number;
    public String billingkey;
    public String cardcd;

    public CreditCard(Parcel in)
    {
        readFromParcel(in);
    }

    public CreditCard(String name, String number, String billkey, String cardcd)
    {
        this.name = name;
        this.number = number;
        this.billingkey = billkey;
        this.cardcd = cardcd;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(name);
        dest.writeString(number);
        dest.writeString(billingkey);
        dest.writeString(cardcd);
    }

    private void readFromParcel(Parcel in)
    {
        name = in.readString();
        number = in.readString();
        billingkey = in.readString();
        cardcd = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public CreditCard createFromParcel(Parcel in)
        {
            return new CreditCard(in);
        }

        @Override
        public CreditCard[] newArray(int size)
        {
            return new CreditCard[size];
        }
    };

    public static String getCardCDName(Context context, String cardcd)
    {
        if ("01".equalsIgnoreCase(cardcd) == true)
        {
            return context.getString(R.string.label_cardcd01);
        } else if ("03".equalsIgnoreCase(cardcd) == true)
        {
            return context.getString(R.string.label_cardcd03);
        } else if ("04".equalsIgnoreCase(cardcd) == true)
        {
            return context.getString(R.string.label_cardcd04);
        } else if ("06".equalsIgnoreCase(cardcd) == true)
        {
            return context.getString(R.string.label_cardcd06);
        } else if ("11".equalsIgnoreCase(cardcd) == true)
        {
            return context.getString(R.string.label_cardcd11);
        } else if ("12".equalsIgnoreCase(cardcd) == true)
        {
            return context.getString(R.string.label_cardcd12);
        } else if ("14".equalsIgnoreCase(cardcd) == true)
        {
            return context.getString(R.string.label_cardcd14);
        } else if ("15".equalsIgnoreCase(cardcd) == true)
        {
            return context.getString(R.string.label_cardcd15);
        } else if ("16".equalsIgnoreCase(cardcd) == true)
        {
            return context.getString(R.string.label_cardcd16);
        } else if ("17".equalsIgnoreCase(cardcd) == true)
        {
            return context.getString(R.string.label_cardcd17);
        } else if ("21".equalsIgnoreCase(cardcd) == true)
        {
            return context.getString(R.string.label_cardcd21);
        } else if ("22".equalsIgnoreCase(cardcd) == true)
        {
            return context.getString(R.string.label_cardcd22);
        } else if ("23".equalsIgnoreCase(cardcd) == true)
        {
            return context.getString(R.string.label_cardcd23);
        } else if ("24".equalsIgnoreCase(cardcd) == true)
        {
            return context.getString(R.string.label_cardcd24);
        } else if ("25".equalsIgnoreCase(cardcd) == true)
        {
            return context.getString(R.string.label_cardcd25);
        }

        return null;
    }
}
