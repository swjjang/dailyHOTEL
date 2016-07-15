package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.lang.reflect.Field;

/**
 * Created by android_sam on 2016. 6. 30..
 */
public class StayParams implements Parcelable
{
    public String dateCheckIn;
    public int stays;
    public int provinceIdx;
    public int areaIdx;
    public int persons;
    public Category category;
    public String bedType; // curationOption에서 가져온 스트링
    public String luxury; // curationOption에서 가져온 스트링
    public double longitude;
    public double latitude;
    public int page;
    public int limit;
    public String sortProperty;
    public String sortDirection;
    public boolean details;


    public StayParams()
    {
    }


    public StayParams(Parcel in)
    {
        readFromParcel(in);
    }

    protected void readFromParcel(Parcel in)
    {
        dateCheckIn = in.readString();
        stays = in.readInt();
        provinceIdx = in.readInt();
        areaIdx = in.readInt();
        persons = in.readInt();
        category = in.readParcelable(Category.class.getClassLoader());
        bedType = in.readString();
        luxury = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
        page = in.readInt();
        limit = in.readInt();
        sortProperty = in.readString();
        sortDirection = in.readString();
        details = in.readInt() == 1 ? true : false;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(dateCheckIn);
        dest.writeInt(stays);
        dest.writeInt(provinceIdx);
        dest.writeInt(areaIdx);
        dest.writeInt(persons);
        dest.writeParcelable(category, flags);
        dest.writeString(bedType);
        dest.writeString(luxury);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeInt(page);
        dest.writeInt(limit);
        dest.writeString(sortProperty);
        dest.writeString(sortDirection);
        dest.writeInt(details ? 1 : 0);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public StayParams createFromParcel(Parcel in)
        {
            return new StayParams(in);
        }

        @Override
        public StayParams[] newArray(int size)
        {
            return new StayParams[size];
        }

    };

    public void setSortType(Constants.SortType sortType)
    {
        switch (sortType)
        {
            case DEFAULT:
                sortProperty = null;
                sortDirection = null;
                break;

            case DISTANCE:
                sortProperty = "Distance";
                sortDirection = "Asc";
                break;

            case LOW_PRICE:
                sortProperty = "Price";
                sortDirection = "Asc";
                break;

            case HIGH_PRICE:
                sortProperty = "Price";
                sortDirection = "Desc";
                break;

            case SATISFACTION:
                sortProperty = "Rating";
                sortDirection = null;
                break;
        }
    }

    public Constants.SortType getSortType()
    {
        if (Util.isTextEmpty(sortProperty) == true)
        {
            return Constants.SortType.DEFAULT;
        }

        switch (sortProperty)
        {
            case "Distance":
                return Constants.SortType.DISTANCE;
            case "Price":
                if ("Desc".equalsIgnoreCase(sortDirection))
                {
                    return Constants.SortType.LOW_PRICE;
                }

                return Constants.SortType.HIGH_PRICE;
            case "Rating":
                return Constants.SortType.SATISFACTION;
        }

        return Constants.SortType.DEFAULT;
    }

    public boolean hasLocation()
    {
        return (latitude == 0d || longitude == 0d) ? false : true;
    }

    /**
     * http://dev.dailyhotel.me/goodnight/api/v3/hotels/sales?
     * dateCheckIn=2016-06-18
     * &stays=3
     * &provinceIdx=5000
     * &areaIdx=2
     * &persons=3
     * &category=Hotel&category=Boutique&category=GuestHouse
     * &bedType=Double&bedType=Twin&bedType=Ondol
     * &luxury=Breakfast&luxury=Cooking&luxury=Bath
     * &longitude=37.505067
     * &latitude=127.057053
     * &page=1
     * &limit=20
     * &sortProperty=Price
     * &sortDirection=Desc
     * &details=true
     */
    public String toParamString()
    {
        StringBuilder sb = new StringBuilder();

        Field[] fields = StayParams.class.getDeclaredFields();
        for (Field field : fields)
        {
            field.setAccessible(true);

            addAndCoupler(sb);

            String name = field.getName();
            //            ExLog.d(name + " , " + sb.toString());

            if ("CREATOR".equalsIgnoreCase(name))
            {
                continue;

            } else if ("category".equalsIgnoreCase(name))
            {
                String categoryString = getCategoryString();
                if (Util.isTextEmpty(categoryString) == false)
                {
                    sb.append(categoryString);
                }

            } else if ("bedtype".equalsIgnoreCase(name))
            {
                if (Util.isTextEmpty(bedType) == false)
                {
                    sb.append(bedType);
                }

            } else if ("luxury".equalsIgnoreCase(name))
            {
                if (Util.isTextEmpty(luxury) == false)
                {
                    sb.append(luxury);
                }

            } else
            {
                try
                {
                    StayParams object = this;
                    String stringValue = getParamString(name, field.get(object));

                    if (Util.isTextEmpty(stringValue) == true)
                    {
                        continue;
                    }

                    if ("longitude".equalsIgnoreCase(name) || "latitude".equalsIgnoreCase(name))
                    {
                        if (hasLocation() == false)
                        {
                            // 해당 값이 0일때 파라메터에 더하지 않음
                            continue;
                        }

                    } else if ("areaIdx".equalsIgnoreCase(name))
                    {
                        if (areaIdx == 0)
                        {
                            // 해당 값이 0일때 파라메터에 더하지 않음
                            continue;
                        }
                        //  String subValue = String.valueOf(field.get(object));
                        //
                        //  int intValue = Integer.parseInt(subValue);
                        //   if (intValue == 0)
                        //    {
                        //     // 해당 값이 0일때 파라메터에 더하지 않음
                        //    continue;
                        //    }
                    } else if ("page".equalsIgnoreCase(name) || "limit".equalsIgnoreCase(name))
                    {
                        if (page == 0)
                        {
                            // 해당 값이 0일때 전체 리스트 요청이므로 limit 값 무시
                            continue;
                        }
                    }

                    sb.append(stringValue);

                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            }
        }

        removeLastAndCoupler(sb);

        //        ExLog.d(" params : " + sb.toString());
        return sb.toString();
    }

    private String getCategoryString()
    {
        if (category == null)
        {
            return "";
        }

        if (Category.ALL.code.equalsIgnoreCase(category.code))
        {
            // 전체일경우 안보내면 전체임
            return "";
        }

        return getParamString("category", category.code);
    }

    private StringBuilder addAndCoupler(StringBuilder sb)
    {
        if (Util.isTextEmpty(sb.toString()) == false)
        {
            int length = sb.length();
            String dest = sb.substring(length > 0 ? length - 1 : 0);

            if ("&".equalsIgnoreCase(dest) == false)
            {
                sb.append("&");
            }
        }

        return sb;
    }

    private StringBuilder removeLastAndCoupler(StringBuilder sb)
    {
        if (Util.isTextEmpty(sb.toString()) == false)
        {
            int length = sb.length();
            if (length > 0)
            {
                String dest = sb.substring(length - 1);
                if ("&".equalsIgnoreCase(dest) == true)
                {
                    sb.setLength(length - 1);
                }
            }
        }

        return sb;
    }

    private String getParamString(String key, Object value)
    {
        String stringValue = String.valueOf(value);
        if (Util.isTextEmpty(stringValue))
        {
            return "";
        }

        return String.format("%s=%s", key, stringValue);
    }
}
