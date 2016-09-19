package com.twoheart.dailyhotel.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Util;

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
        if (context == null || Util.isTextEmpty(cardcd) == true)
        {
            return null;
        }

        switch (cardcd)
        {
            case "01":
                return context.getString(R.string.label_cardcd01);
            case "03":
                return context.getString(R.string.label_cardcd03);
            case "04":
                return context.getString(R.string.label_cardcd04);
            case "06":
                return context.getString(R.string.label_cardcd06);
            case "11":
                return context.getString(R.string.label_cardcd11);
            case "12":
                return context.getString(R.string.label_cardcd12);
            case "14":
                return context.getString(R.string.label_cardcd14);
            case "15":
                return context.getString(R.string.label_cardcd15);
            case "16":
                return context.getString(R.string.label_cardcd16);
            case "17":
                return context.getString(R.string.label_cardcd17);
            case "21":
                return context.getString(R.string.label_cardcd21);
            case "22":
                return context.getString(R.string.label_cardcd22);
            case "23":
                return context.getString(R.string.label_cardcd23);
            case "24":
                return context.getString(R.string.label_cardcd24);
            case "25":
                return context.getString(R.string.label_cardcd25);
            case "26":
                return context.getString(R.string.label_cardcd26);
            case "32":
                return context.getString(R.string.label_cardcd32);
            case "33":
                return context.getString(R.string.label_cardcd33);
            case "34":
                return context.getString(R.string.label_cardcd34);
            case "35":
                return context.getString(R.string.label_cardcd35);
            case "41":
                return context.getString(R.string.label_cardcd41);
            case "43":
                return context.getString(R.string.label_cardcd43);
            case "44":
                return context.getString(R.string.label_cardcd44);
            case "48":
                return context.getString(R.string.label_cardcd48);
            case "51":
                return context.getString(R.string.label_cardcd51);
            case "52":
                return context.getString(R.string.label_cardcd52);
            case "54":
                return context.getString(R.string.label_cardcd54);
            case "71":
                return context.getString(R.string.label_cardcd71);
            case "95":
                return context.getString(R.string.label_cardcd95);
            default:
                return null;
        }
    }
}
