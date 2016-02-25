package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class HotelFilters implements Parcelable
{
    public static int FLAG_HOTEL_FILTER_BED_NONE = 0x00;
    public static int FLAG_HOTEL_FILTER_BED_DOUBLE = 0x01;
    public static int FLAG_HOTEL_FILTER_BED_TWIN = 0x02;
    public static int FLAG_HOTEL_FILTER_BED_HEATEDFLOORS = 0x04;
    public static int FLAG_HOTEL_FILTER_BED_CHECKIN = 0x08;

    private HotelFilter[] mHotelFilterArray;
    public boolean isFiltered;

    public HotelFilters(int size)
    {
        mHotelFilterArray = new HotelFilter[size];
    }

    public HotelFilters(Parcel in)
    {
        readFromParcel(in);
    }

    public void setHotelFilter(int index, JSONObject jsonObject) throws JSONException, ArrayIndexOutOfBoundsException
    {
        if (index < mHotelFilterArray.length)
        {
            mHotelFilterArray[index] = new HotelFilter(jsonObject);
        } else
        {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public boolean isFiltered(int flag, int person)
    {
        if (flag == 0)
        {
            for (HotelFilter hotelFilter : mHotelFilterArray)
            {
                isFiltered = hotelFilter.isFiltered(FLAG_HOTEL_FILTER_BED_NONE, person);

                if (isFiltered == true)
                {
                    break;
                }
            }
        } else
        {
            for (HotelFilter hotelFilter : mHotelFilterArray)
            {
                isFiltered = false;

                if ((flag & FLAG_HOTEL_FILTER_BED_CHECKIN) == FLAG_HOTEL_FILTER_BED_CHECKIN)
                {
                    isFiltered = hotelFilter.isFiltered(FLAG_HOTEL_FILTER_BED_CHECKIN, person);
                }

                if (isFiltered == true)
                {
                    break;
                } else
                {
                    if ((flag & FLAG_HOTEL_FILTER_BED_DOUBLE) == FLAG_HOTEL_FILTER_BED_DOUBLE)
                    {
                        isFiltered = hotelFilter.isFiltered(FLAG_HOTEL_FILTER_BED_DOUBLE, person);
                    }
                }

                if (isFiltered == true)
                {
                    break;
                } else
                {
                    if ((flag & FLAG_HOTEL_FILTER_BED_TWIN) == FLAG_HOTEL_FILTER_BED_TWIN)
                    {
                        isFiltered = hotelFilter.isFiltered(FLAG_HOTEL_FILTER_BED_TWIN, person);
                    }
                }

                if (isFiltered == true)
                {
                    break;
                } else
                {
                    if ((flag & FLAG_HOTEL_FILTER_BED_HEATEDFLOORS) == FLAG_HOTEL_FILTER_BED_HEATEDFLOORS)
                    {
                        isFiltered = hotelFilter.isFiltered(FLAG_HOTEL_FILTER_BED_HEATEDFLOORS, person);
                    }
                }

                if (isFiltered == true)
                {
                    break;
                }
            }
        }

        return isFiltered;
    }

    public void clear()
    {
        Arrays.fill(mHotelFilterArray, 0, mHotelFilterArray.length, null);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeTypedArray(mHotelFilterArray, flags);
    }

    private void readFromParcel(Parcel in)
    {
        mHotelFilterArray = (HotelFilter[]) in.createTypedArray(HotelFilter.CREATOR);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public HotelFilters createFromParcel(Parcel in)
        {
            return new HotelFilters(in);
        }

        @Override
        public HotelFilters[] newArray(int size)
        {
            return new HotelFilters[size];
        }
    };
}
