package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class HotelFilters implements Parcelable
{
    public static int FLAG_HOTEL_FILTER_BED_DOUBLE = 0x01000000;
    public static int FLAG_HOTEL_FILTER_BED_TWIN = 0x02000000;
    public static int FLAG_HOTEL_FILTER_BED_HEATEDFLOORS = 0x04000000;
    public static int FLAG_HOTEL_FILTER_BED_CHECKIN = 0x08000000;

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
        if (mHotelFilterArray.length < index)
        {
            mHotelFilterArray[index] = new HotelFilter(jsonObject);
        } else
        {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public boolean isFiltered(int flag, int person)
    {
        int index = person - HotelFilter.MIN_PERSON;

        if (flag == 0)
        {
            for (HotelFilter hotelFilter : mHotelFilterArray)
            {
                isFiltered = hotelFilter.isFiltered(FLAG_HOTEL_FILTER_BED_DOUBLE, person);

                if (isFiltered == true)
                {
                    break;
                } else
                {
                    isFiltered = hotelFilter.isFiltered(FLAG_HOTEL_FILTER_BED_TWIN, person);
                }

                if (isFiltered == true)
                {
                    break;
                } else
                {
                    isFiltered = hotelFilter.isFiltered(FLAG_HOTEL_FILTER_BED_HEATEDFLOORS, person);
                }

                if (isFiltered == true)
                {
                    break;
                } else
                {
                    isFiltered = hotelFilter.isFiltered(FLAG_HOTEL_FILTER_BED_CHECKIN, person);
                }

                if (isFiltered == true)
                {
                    break;
                }
            }
        } else
        {
            for (HotelFilter hotelFilter : mHotelFilterArray)
            {
                if ((flag & FLAG_HOTEL_FILTER_BED_DOUBLE) != 0)
                {
                    isFiltered = hotelFilter.isFiltered(FLAG_HOTEL_FILTER_BED_DOUBLE, person);
                }

                if (isFiltered == true)
                {
                    break;
                } else
                {
                    if ((flag & FLAG_HOTEL_FILTER_BED_TWIN) != 0)
                    {
                        isFiltered = hotelFilter.isFiltered(FLAG_HOTEL_FILTER_BED_TWIN, person);
                    }
                }

                if (isFiltered == true)
                {
                    break;
                } else
                {
                    if ((flag & FLAG_HOTEL_FILTER_BED_HEATEDFLOORS) != 0)
                    {
                        isFiltered = hotelFilter.isFiltered(FLAG_HOTEL_FILTER_BED_HEATEDFLOORS, person);
                    }
                }

                if (isFiltered == true)
                {
                    break;
                } else
                {
                    if ((flag & FLAG_HOTEL_FILTER_BED_CHECKIN) != 0)
                    {
                        isFiltered = hotelFilter.isFiltered(FLAG_HOTEL_FILTER_BED_CHECKIN, person);
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
        dest.writeParcelableArray(mHotelFilterArray, flags);
    }

    private void readFromParcel(Parcel in)
    {
        mHotelFilterArray = (HotelFilter[]) in.readParcelableArray(HotelFilter.class.getClassLoader());
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
