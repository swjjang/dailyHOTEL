package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Province implements Parcelable
{
    public int index;
    public String name;
    public String englishName;
    public int sequence;
    public boolean isOverseas;
    public String imageUrl;
    private List<Category> mCategoryList;

    public Province()
    {
        super();
    }

    public Province(Parcel in)
    {
        readFromParcel(in);
    }

    public Province(JSONObject jsonObject, String url) throws JSONException
    {
        index = jsonObject.getInt("idx");
        name = jsonObject.getString("name");

        if (jsonObject.has("nameEng") == true)
        {
            englishName = jsonObject.getString("nameEng");
        } else
        {
            englishName = "";
        }

        if (jsonObject.has("sequence") == true)
        {
            sequence = jsonObject.getInt("sequence");
        } else
        {
            sequence = 0;
        }

        if (jsonObject.has("overseas") == true)
        {
            isOverseas = jsonObject.getBoolean("overseas");
        } else
        {
            isOverseas = false;
        }

        if (Util.isTextEmpty(url) == false)
        {
            imageUrl = url + jsonObject.getString("imagePath");
        } else
        {
            imageUrl = "";
        }

        mCategoryList = new ArrayList<>();

        if (jsonObject.has("categories") == true)
        {
            JSONArray jsonArray = jsonObject.getJSONArray("categories");

            int length = jsonArray.length();
            for (int i = 0; i < length; i++)
            {
                JSONObject categoryJSONObject = jsonArray.getJSONObject(i);

                Iterator<String> iterator = categoryJSONObject.keys();
                if (iterator.hasNext() == true)
                {
                    String code = iterator.next();
                    String name = categoryJSONObject.getString(code);

                    mCategoryList.add(new Category(name, code));
                }
            }
        } else
        {
            mCategoryList.add(Category.ALL);
        }
    }

    public int getProvinceIndex()
    {
        return index;
    }

    public List<Category> getCategoryList()
    {
        return mCategoryList;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(index);
        dest.writeString(name);
        dest.writeString(englishName);
        dest.writeInt(sequence);
        dest.writeInt(isOverseas ? 1 : 0);
        dest.writeString(imageUrl);
        dest.writeList(mCategoryList);
    }

    protected void readFromParcel(Parcel in)
    {
        index = in.readInt();
        name = in.readString();
        englishName = in.readString();
        sequence = in.readInt();
        isOverseas = in.readInt() == 1;
        imageUrl = in.readString();
        mCategoryList = in.readArrayList(Category.class.getClassLoader());
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public Province createFromParcel(Parcel in)
        {
            return new Province(in);
        }

        @Override
        public Province[] newArray(int size)
        {
            return new Province[size];
        }

    };
}
