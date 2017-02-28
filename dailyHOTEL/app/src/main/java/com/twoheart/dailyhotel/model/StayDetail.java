package com.twoheart.dailyhotel.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.ImageInformation;
import com.twoheart.dailyhotel.network.model.StayDetailParams;
import com.twoheart.dailyhotel.network.model.StayProduct;

import java.util.List;

public class StayDetail extends PlaceDetail<StayProduct> implements Parcelable
{
    private StayDetailParams mStayDetailParams;

    public int nights;
    public boolean hasCoupon;

    public StayDetail(int hotelIndex, int nights, int entryIndex, String isShowOriginalPrice, int listCount, boolean isDailyChoice)
    {
        this.index = hotelIndex;
        this.nights = nights;
        this.entryPosition = entryIndex;
        this.isShowOriginalPrice = isShowOriginalPrice;
        this.listCount = listCount;
        this.isDailyChoice = isDailyChoice;
    }

    public StayDetail(Parcel in)
    {
        readFromParcel(in);
    }

    //    public void setData(JSONObject jsonObject) throws Exception
    //    {
    //        try
    //        {
    //            grade = Stay.Grade.valueOf(jsonObject.getString("grade"));
    //        } catch (Exception e)
    //        {
    //            grade = Stay.Grade.etc;
    //        }
    //
    //        name = jsonObject.getString("name");
    //        address = jsonObject.getString("address");
    //
    //        longitude = jsonObject.getDouble("longitude");
    //        latitude = jsonObject.getDouble("latitude");
    //        isOverseas = jsonObject.getBoolean("overseas");
    //
    //        boolean ratingShow = jsonObject.getBoolean("ratingShow");
    //
    //        if (ratingShow == true)
    //        {
    //            ratingValue = jsonObject.getInt("ratingValue");
    //            ratingPersons = jsonObject.getInt("ratingPersons");
    //        }
    //
    //        if (jsonObject.has("singleStay") == true)
    //        {
    //            isSingleStay = jsonObject.getBoolean("singleStay");
    //        } else
    //        {
    //            isSingleStay = false;
    //        }
    //
    //        // Pictogram
    //        if (mPictogramList == null)
    //        {
    //            mPictogramList = new ArrayList<>();
    //        }
    //
    //        mPictogramList.clear();
    //
    //        // 주차
    //        if (jsonObject.getBoolean("parking") == true)
    //        {
    //            mPictogramList.add(Pictogram.PARKING);
    //        }
    //
    //        // 주차금지
    //        if (jsonObject.getBoolean("noParking") == true)
    //        {
    //            mPictogramList.add(Pictogram.NO_PARKING);
    //        }
    //
    //        // 수영장
    //        if (jsonObject.getBoolean("pool") == true)
    //        {
    //            mPictogramList.add(Pictogram.POOL);
    //        }
    //
    //        // TODO : business center
    //        if (jsonObject.getBoolean("businessCenter") == true)
    //        {
    //            mPictogramList.add(Pictogram.BUSINESS_CENTER);
    //        }
    //
    //        // 피트니스
    //        if (jsonObject.getBoolean("fitness") == true)
    //        {
    //            mPictogramList.add(Pictogram.FITNESS);
    //        }
    //
    //        // TODO : 사우나
    //        if (jsonObject.getBoolean("sauna") == true)
    //        {
    //            mPictogramList.add(Pictogram.SAUNA);
    //        }
    //
    //        // 애완동물
    //        if (jsonObject.getBoolean("pet") == true)
    //        {
    //            mPictogramList.add(Pictogram.PET);
    //        }
    //
    //        // TODO : kids_play_room
    //        if (jsonObject.getBoolean("kidsPlayRoom") == true) {
    //            mPictogramList.add(Pictogram.KIDS_PLAY_ROOM);
    //        }
    //
    //        // Image Url
    //        String imageUrl = jsonObject.getString("imgUrl");
    //        JSONObject pathUrlJSONObject = jsonObject.getJSONObject("imgPath");
    //
    //        Iterator<String> iterator = pathUrlJSONObject.keys();
    //        while (iterator.hasNext())
    //        {
    //            String key = iterator.next();
    //
    //            try
    //            {
    //                JSONArray pathJSONArray = pathUrlJSONObject.getJSONArray(key);
    //
    //                int length = pathJSONArray.length();
    //                mImageInformationList = new ArrayList<>(pathJSONArray.length());
    //
    //                for (int i = 0; i < length; i++)
    //                {
    //                    JSONObject imageInformationJSONObject = pathJSONArray.getJSONObject(i);
    //
    //                    ImageInformation imageInformation = new ImageInformation();
    //                    imageInformation.description = imageInformationJSONObject.getString("description");
    //                    imageInformation.name = imageInformationJSONObject.getString("name");
    //                    imageInformation.setImageUrl(imageUrl + key + imageInformation.name);
    //
    //                    mImageInformationList.add(imageInformation);
    //                }
    //                break;
    //            } catch (JSONException e)
    //            {
    //            }
    //        }
    //
    //        // benefit
    //        if (jsonObject.has("benefit") == true)
    //        {
    //            benefit = jsonObject.getString("benefit");
    //
    //            if (Util.isTextEmpty(benefit) == false && jsonObject.has("benefitContents") == true && jsonObject.isNull("benefitContents") == false)
    //            {
    //                JSONArray benefitJSONArray = jsonObject.getJSONArray("benefitContents");
    //
    //                int length = benefitJSONArray.length();
    //
    //                if (length > 0)
    //                {
    //                    mBenefitInformation = new ArrayList<>(length);
    //
    //                    for (int i = 0; i < length; i++)
    //                    {
    //                        mBenefitInformation.add(benefitJSONArray.getString(i));
    //                    }
    //                } else
    //                {
    //                    mBenefitInformation = new ArrayList<>();
    //                }
    //
    //                if (jsonObject.has("benefitWarning") == true && jsonObject.isNull("benefitWarning") == false)
    //                {
    //                    String benefitWarning = jsonObject.getString("benefitWarning");
    //
    //                    if (Util.isTextEmpty(benefitWarning) == false)
    //                    {
    //                        mBenefitInformation.add(benefitWarning);
    //                    }
    //                }
    //            }
    //        }
    //
    //        // Detail
    //        JSONArray detailJSONArray = jsonObject.getJSONArray("details");
    //        int detailLength = detailJSONArray.length();
    //
    //        mInformationList = new ArrayList<>(detailLength);
    //
    //        for (int i = 0; i < detailLength; i++)
    //        {
    //            mInformationList.add(new DetailInformation(detailJSONArray.getJSONObject(i)));
    //        }
    //
    //        // Room Sale Info
    //
    //        if (jsonObject.has("rooms") == true && jsonObject.isNull("rooms") == false)
    //        {
    //            JSONArray saleRoomJSONArray = jsonObject.getJSONArray("rooms");
    //
    //            int saleRoomLength = saleRoomJSONArray.length();
    //
    //            mSaleRoomList = new ArrayList<>(saleRoomLength);
    //
    //            for (int i = 0; i < saleRoomLength; i++)
    //            {
    //                RoomInformation roomInformation = new RoomInformation(name, saleRoomJSONArray.getJSONObject(i), isOverseas, nights);
    //                roomInformation.grade = grade;
    //                roomInformation.address = address;
    //                mSaleRoomList.add(roomInformation);
    //            }
    //        } else
    //        {
    //            mSaleRoomList = new ArrayList<>();
    //        }
    //
    //        if (jsonObject.has("myWish") == true)
    //        {
    //            myWish = jsonObject.getBoolean("myWish");
    //        }
    //
    //        if (jsonObject.has("wishCount") == true)
    //        {
    //            wishCount = jsonObject.getInt("wishCount");
    //        }
    //    }

    public StayDetailParams getStayDetailParams()
    {
        return mStayDetailParams;
    }

    public void setStayDetailParams(StayDetailParams stayDetailParams)
    {
        this.mStayDetailParams = stayDetailParams;
    }

    @Override
    public List<StayProduct> getProductList()
    {
        if (mStayDetailParams == null)
        {
            return null;
        }

        return mStayDetailParams.getProductList();
    }

    @Override
    public StayProduct getProduct(int index)
    {
        if (mStayDetailParams == null || index < 0)
        {
            return null;
        }

        List<StayProduct> stayProductList = mStayDetailParams.getProductList();
        if (stayProductList == null || stayProductList.size() == 0)
        {
            return null;
        }

        return stayProductList.get(index);
    }

    @Override
    public List<Pictogram> getPictogramList()
    {
        if (mStayDetailParams == null)
        {
            return null;
        }

        return mStayDetailParams.getPictogramList();
    }

    @Override
    public List<ImageInformation> getImageList()
    {
        if (mStayDetailParams == null)
        {
            return null;
        }

        return mStayDetailParams.getImageList();
    }

    @Override
    public List<DetailInformation> getDetailList()
    {
        if (mStayDetailParams == null)
        {
            return null;
        }

        return mStayDetailParams.getDetailList();
    }

    @Override
    public List<String> getBenefitList()
    {
        if (mStayDetailParams == null)
        {
            return null;
        }

        return mStayDetailParams.getBenefitList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(mStayDetailParams, flags);
        dest.writeInt(hasCoupon == true ? 1 : 0);
        dest.writeInt(index);
        dest.writeInt(listCount);
        dest.writeInt(entryPosition);
        dest.writeString(isShowOriginalPrice);
        dest.writeInt(isDailyChoice == true ? 1 : 0);
    }

    protected void readFromParcel(Parcel in)
    {
        mStayDetailParams = in.readParcelable(StayDetailParams.class.getClassLoader());
        hasCoupon = in.readInt() == 1;
        index = in.readInt();
        listCount = in.readInt();
        entryPosition = in.readInt();
        isShowOriginalPrice = in.readString();
        isDailyChoice = in.readInt() == 1;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public StayDetail createFromParcel(Parcel in)
        {
            return new StayDetail(in);
        }

        @Override
        public StayDetail[] newArray(int size)
        {
            return new StayDetail[size];
        }
    };

    public enum Pictogram implements Parcelable
    {
        PARKING(R.string.label_parking, R.drawable.ic_detail_facilities_01_parking),
        NO_PARKING(R.string.label_unabled_parking, R.drawable.ic_detail_facilities_02_no_parking),
        SHARED_BBQ(R.string.label_allowed_barbecue, R.drawable.ic_detail_facilities_06_bbq),
        POOL(R.string.label_pool, R.drawable.ic_detail_facilities_03_pool),
        BUSINESS_CENTER(R.string.label_business_center, R.drawable.ic_detail_facilities_01_parking),
        FITNESS(R.string.label_fitness, R.drawable.ic_detail_facilities_04_fitness),
        SAUNA(R.string.label_sauna, R.drawable.ic_detail_facilities_01_parking),
        PET(R.string.label_allowed_pet, R.drawable.ic_detail_facilities_05_pet),
        KIDS_PLAY_ROOM(R.string.label_kids_play_room, R.drawable.ic_detail_facilities_01_parking),
        NONE(0, 0);

        private int mNameResId;
        private int mImageResId;

        Pictogram(int nameResId, int imageResId)
        {
            mNameResId = nameResId;
            mImageResId = imageResId;
        }

        public String getName(Context context)
        {
            if (mNameResId == 0)
            {
                return null;
            }

            return context.getString(mNameResId);
        }

        public int getImageResId()
        {
            return mImageResId;
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeString(name());
        }

        public static final Parcelable.Creator<Pictogram> CREATOR = new Creator<Pictogram>()
        {
            @Override
            public Pictogram createFromParcel(Parcel in)
            {
                return Pictogram.valueOf(in.readString());
            }

            @Override
            public Pictogram[] newArray(int size)
            {
                return new Pictogram[size];
            }
        };
    }
}