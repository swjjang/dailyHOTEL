package com.twoheart.dailyhotel.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.AreaElement;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayRegion;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by android_sam on 2016. 6. 30..
 */
public class StayParams extends PlaceParams
{
    protected String dateCheckIn;
    protected int stays;
    protected Category category;

    protected List<String> mBedTypeList;
    protected List<String> mLuxuryList;
    protected List<String> mRoomLuxuryList;

    public StayParams(PlaceCuration placeCuration)
    {
        super(placeCuration);
    }

    public StayParams(Parcel in)
    {
        super(in);
    }

    @Override
    public void setPlaceParams(PlaceCuration placeCuration)
    {
        if (placeCuration == null)
        {
            return;
        }

        StayCuration stayCuration = (StayCuration) placeCuration;

        clear();

        StayBookingDay stayBookingDay = stayCuration.getStayBookingDay();

        if (stayBookingDay != null)
        {
            dateCheckIn = stayBookingDay.getCheckInDay("yyyy-MM-dd");

            try
            {
                stays = stayBookingDay.getNights();
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
        }

        StayRegion stayRegion = stayCuration.getRegion();

        if (stayRegion != null)
        {
            switch (stayRegion.getAreaType())
            {
                case AREA:
                {
                    AreaElement areaGroupElement = stayRegion.getAreaGroupElement();

                    if (areaGroupElement != null)
                    {
                        provinceIdx = areaGroupElement.index;

                        AreaElement areaElement = stayRegion.getAreaElement();

                        if (areaElement != null && areaElement.index != StayArea.ALL)
                        {
                            areaIdx = areaElement.index;
                        }
                    }
                    break;
                }

                case SUBWAY_AREA:
                {
                    AreaElement areaElement = stayRegion.getAreaElement();

                    if (areaElement != null)
                    {
                        subwayIndex = areaElement.index;
                    }
                    break;
                }
            }
        }

        category = stayCuration.getCategory();

        StayCurationOption stayCurationOption = (StayCurationOption) stayCuration.getCurationOption();

        if (stayCurationOption != null)
        {
            persons = stayCurationOption.person;

            mBedTypeList = toParamListByBedTypes(stayCurationOption.flagBedTypeFilters);
            mLuxuryList = toParamListByAmenities(stayCurationOption.flagAmenitiesFilters);
            mRoomLuxuryList = toParamListByRoomAmenities(stayCurationOption.flagRoomAmenitiesFilters);
            mSort = stayCurationOption.getSortType();
        }

        setSortType(mSort);

        if (Constants.SortType.DISTANCE == mSort)
        {
            Location location = stayCuration.getLocation();
            if (location != null)
            {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
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
    @Override
    public String toParamsString()
    {
        StringBuilder stringBuilder = new StringBuilder();

        Map<String, Object> map = toParamsMap();

        for (Map.Entry<String, Object> entry : map.entrySet())
        {
            stringBuilder.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
        }

        // 마지막 & 없애기
        stringBuilder.setLength(stringBuilder.length() - 1);

        List<String> bedTypeList = getBedTypeList();

        if (bedTypeList != null && bedTypeList.size() > 0)
        {
            for (String bedType : bedTypeList)
            {
                stringBuilder.append("&bedType").append('=').append(bedType);
            }
        }

        List<String> luxuryList = getLuxuryList();

        if (luxuryList != null && luxuryList.size() > 0)
        {
            for (String luxury : luxuryList)
            {
                stringBuilder.append("&luxury").append('=').append(luxury);
            }
        }

        return stringBuilder.toString();
    }

    public List<String> getBedTypeList()
    {
        return mBedTypeList;
    }

    public List<String> getLuxuryList()
    {
        List<String> allLuxuryList = new ArrayList<>();

        if (mLuxuryList != null && mLuxuryList.size() != 0)
        {
            allLuxuryList.addAll(mLuxuryList);
        }

        if (mRoomLuxuryList != null && mRoomLuxuryList.size() != 0)
        {
            allLuxuryList.addAll(mRoomLuxuryList);
        }

        return allLuxuryList.size() == 0 ? null : allLuxuryList;
    }

    public Map<String, Object> toParamsMap()
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("dateCheckIn", dateCheckIn);
        hashMap.put("stays", stays);

        if (provinceIdx != 0)
        {
            hashMap.put("provinceIdx", provinceIdx);
        }

        if (areaIdx > 0)
        {
            hashMap.put("areaIdx", areaIdx);
        }

        if (subwayIndex != 0)
        {
            hashMap.put("subwayIdx", subwayIndex);
        }

        if (persons != 0)
        {
            hashMap.put("persons", persons);
        }

        if (category != null && Category.ALL.code.equalsIgnoreCase(category.code) == false)
        {
            hashMap.put("category", category.code);
        }

        //        if(mBedTypeList != null && mBedTypeList.size() > 0)
        //        {
        //            hashMap.put("bedType", mBedTypeList);
        //        }
        //
        //        if(mLuxuryList != null && mLuxuryList.size() > 0)
        //        {
        //            hashMap.put("luxury", mLuxuryList);
        //        }

        if (page > 0)
        {
            hashMap.put("page", page);
            hashMap.put("limit", limit);
        }

        if (Constants.SortType.DEFAULT != mSort)
        {
            if (DailyTextUtils.isTextEmpty(sortProperty) == false)
            {
                hashMap.put("sortProperty", sortProperty);
            }

            if (DailyTextUtils.isTextEmpty(sortDirection) == false)
            {
                hashMap.put("sortDirection", sortDirection);
            }

            if (Constants.SortType.DISTANCE == mSort && hasLocation() == true)
            {
                hashMap.put("latitude", latitude);
                hashMap.put("longitude", longitude);
            }
        }

        hashMap.put("details", details);

        //        ExLog.d("params : " + hashMap.toString());
        return hashMap;
    }

    protected List<String> toParamListByBedTypes(int flagBedTypeFilters)
    {
        if (flagBedTypeFilters == StayFilter.FLAG_HOTEL_FILTER_BED_NONE)
        {
            return null;
        }

        ArrayList<String> arrayList = new ArrayList();

        if ((flagBedTypeFilters & StayFilter.FLAG_HOTEL_FILTER_BED_DOUBLE) == StayFilter.FLAG_HOTEL_FILTER_BED_DOUBLE)
        {
            arrayList.add("Double");
        }

        if ((flagBedTypeFilters & StayFilter.FLAG_HOTEL_FILTER_BED_TWIN) == StayFilter.FLAG_HOTEL_FILTER_BED_TWIN)
        {
            arrayList.add("Twin");
        }

        if ((flagBedTypeFilters & StayFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS) == StayFilter.FLAG_HOTEL_FILTER_BED_HEATEDFLOORS)
        {
            arrayList.add("Ondol");
        }

        return arrayList;
    }

    protected List<String> toParamListByAmenities(int flagAmenitiesFilters)
    {
        if (flagAmenitiesFilters == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_NONE)
        {
            return null;
        }

        ArrayList<String> arrayList = new ArrayList();

        if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_PARKING) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_PARKING)
        {
            arrayList.add("Parking");
        }

        if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_SHARED_BBQ) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_SHARED_BBQ)
        {
            arrayList.add("SharedBbq");
        }

        if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_POOL) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_POOL)
        {
            arrayList.add("Pool");
        }

        if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_BUSINESS_CENTER) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_BUSINESS_CENTER)
        {
            arrayList.add("BusinessCenter");
        }

        if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_FITNESS) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_FITNESS)
        {
            arrayList.add("Fitness");
        }

        if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_SAUNA) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_SAUNA)
        {
            arrayList.add("Sauna");
        }

        if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_PET) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_PET)
        {
            arrayList.add("Pet");
        }

        if ((flagAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_AMENITIES_KIDS_PLAY_ROOM) == StayFilter.FLAG_HOTEL_FILTER_AMENITIES_KIDS_PLAY_ROOM)
        {
            arrayList.add("KidsPlayroom");
        }

        return arrayList;
    }

    protected List<String> toParamListByRoomAmenities(int flagRoomAmenitiesFilters)
    {
        if (flagRoomAmenitiesFilters == StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_NONE)
        {
            return null;
        }

        ArrayList<String> arrayList = new ArrayList();

        if ((flagRoomAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_WIFI) == StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_WIFI)
        {
            arrayList.add("WiFi");
        }

        if ((flagRoomAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_COOKING) == StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_COOKING)
        {
            arrayList.add("Cooking");
        }

        if ((flagRoomAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_PC) == StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_PC)
        {
            arrayList.add("Pc");
        }

        if ((flagRoomAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_BATHTUB) == StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_BATHTUB)
        {
            arrayList.add("Bath");
        }

        if ((flagRoomAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_TV) == StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_TV)
        {
            arrayList.add("Tv");
        }

        if ((flagRoomAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_SPA_WHIRLPOOL) == StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_SPA_WHIRLPOOL)
        {
            arrayList.add("SpaWallpool");
        }

        if ((flagRoomAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_PRIVATE_BBQ) == StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_PRIVATE_BBQ)
        {
            arrayList.add("PrivateBbq");
        }

        if ((flagRoomAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_BREAKFAST) == StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_BREAKFAST)
        {
            arrayList.add("Breakfast");
        }

        if ((flagRoomAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_KARAOKE) == StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_KARAOKE)
        {
            arrayList.add("Karaoke");
        }

        if ((flagRoomAmenitiesFilters & StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_PARTY_ROOM) == StayFilter.FLAG_HOTEL_FILTER_ROOM_AMENITIES_PARTY_ROOM)
        {
            arrayList.add("PartyRoom");
        }


        return arrayList;
    }

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

    @Override
    protected void clear()
    {
        super.clear();

        dateCheckIn = null;
        stays = 1;
        category = Category.ALL;
        mBedTypeList = null;
        mLuxuryList = null;
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        dateCheckIn = in.readString();
        stays = in.readInt();
        category = in.readParcelable(Category.class.getClassLoader());
        mBedTypeList = in.readArrayList(String.class.getClassLoader());
        mLuxuryList = in.readArrayList(String.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeString(dateCheckIn);
        dest.writeInt(stays);
        dest.writeParcelable(category, flags);
        dest.writeList(mBedTypeList);
        dest.writeList(mLuxuryList);
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
}
