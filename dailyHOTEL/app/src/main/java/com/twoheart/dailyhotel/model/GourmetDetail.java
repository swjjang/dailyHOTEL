package com.twoheart.dailyhotel.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.GourmetDetailParams;
import com.twoheart.dailyhotel.network.model.GourmetProduct;
import com.twoheart.dailyhotel.network.model.ImageInformation;

import java.util.List;

public class GourmetDetail extends PlaceDetail<GourmetProduct> implements Parcelable
{
    private GourmetDetailParams mGourmetDetailParams;

    public boolean hasCoupon;

    public GourmetDetail(int index, int entryPosition, String isShowOriginalPrice, int listCount, boolean isDailyChoice)
    {
        this.index = index;
        this.entryPosition = entryPosition;
        this.isShowOriginalPrice = isShowOriginalPrice;
        this.listCount = listCount;
        this.isDailyChoice = isDailyChoice;
    }

    public GourmetDetail(Parcel in)
    {
        readFromParcel(in);
    }
    //
    //    @Override
    //    public void setData(JSONObject jsonObject) throws Exception
    //    {
    //        grade = Gourmet.Grade.gourmet;
    //
    //        category = jsonObject.getString("category");
    //
    //        subCategory = jsonObject.getString("categorySub");
    //
    //        name = jsonObject.getString("name");
    //        address = jsonObject.getString("address");
    //
    //        longitude = jsonObject.getDouble("longitude");
    //        latitude = jsonObject.getDouble("latitude");
    //
    //        boolean ratingShow = jsonObject.getBoolean("ratingShow");
    //
    //        if (ratingShow == true)
    //        {
    //            ratingValue = jsonObject.getInt("ratingValue");
    //            ratingPersons = jsonObject.getInt("ratingPersons");
    //        }
    //
    //        if (mPictogramList == null)
    //        {
    //            mPictogramList = new ArrayList<>();
    //        }
    //
    //        mPictogramList.clear();
    //
    //        // 주차가능
    //        if (jsonObject.getBoolean("parking") == true)
    //        {
    //            mPictogramList.add(Pictogram.parking);
    //        }
    //        // 발렛가능
    //        if (jsonObject.getBoolean("valet") == true)
    //        {
    //            mPictogramList.add(Pictogram.valet);
    //        }
    //        // 프라이빗룸
    //        if (jsonObject.getBoolean("privateRoom") == true)
    //        {
    //            mPictogramList.add(Pictogram.privateRoom);
    //        }
    //        // 단체예약
    //        if (jsonObject.getBoolean("groupBooking") == true)
    //        {
    //            mPictogramList.add(Pictogram.groupBooking);
    //        }
    //        // 베이비시트
    //        if (jsonObject.getBoolean("babySeat") == true)
    //        {
    //            mPictogramList.add(Pictogram.babySeat);
    //        }
    //        // 코르키지
    //        if (jsonObject.getBoolean("corkage") == true)
    //        {
    //            mPictogramList.add(Pictogram.corkage);
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
    //                    String description = imageInformationJSONObject.getString("description");
    //                    String imageFullUrl = imageUrl + key + imageInformationJSONObject.getString("name");
    //                    mImageInformationList.add(new ImageInformation(imageFullUrl, description));
    //                }
    //                break;
    //            } catch (JSONException e)
    //            {
    //            }
    //        }
    //
    //        //benefit
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
    //        // Ticket Information
    //        if (jsonObject.has("tickets") == true && jsonObject.isNull("tickets") == false)
    //        {
    //            JSONArray ticketInformationJSONArray = jsonObject.getJSONArray("tickets");
    //            int ticketInformationLength = ticketInformationJSONArray.length();
    //
    //            mTicketInformationList = new ArrayList<>(ticketInformationLength);
    //
    //            for (int i = 0; i < ticketInformationLength; i++)
    //            {
    //                TicketInformation ticketInformation = new TicketInformation(name, ticketInformationJSONArray.getJSONObject(i));
    //                ticketInformation.thumbnailUrl = getImageInformationList().get(0).url;
    //                mTicketInformationList.add(ticketInformation);
    //            }
    //        } else
    //        {
    //            mTicketInformationList = new ArrayList<>();
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

    public GourmetDetailParams getGourmetDetailParmas()
    {
        return mGourmetDetailParams;
    }

    public void setGourmetDetailParmas(GourmetDetailParams gourmetDetailParmas)
    {
        mGourmetDetailParams = gourmetDetailParmas;
    }

    @Override
    public List<GourmetProduct> getProductList()
    {
        if (mGourmetDetailParams == null)
        {
            return null;
        }

        return mGourmetDetailParams.getProductList();
    }

    @Override
    public GourmetProduct getProduct(int index)
    {
        if (mGourmetDetailParams == null || index < 0)
        {
            return null;
        }

        List<GourmetProduct> gourmetProductList = mGourmetDetailParams.getProductList();

        if (gourmetProductList == null || gourmetProductList.size() <= index)
        {
            return null;
        }

        return gourmetProductList.get(index);
    }

    @Override
    public List<Pictogram> getPictogramList()
    {
        if (mGourmetDetailParams == null)
        {
            return null;
        }

        return mGourmetDetailParams.getPictogramList();
    }

    @Override
    public List<ImageInformation> getImageList()
    {
        if (mGourmetDetailParams == null)
        {
            return null;
        }

        return mGourmetDetailParams.getImageList();
    }

    @Override
    public List<DetailInformation> getDetailList()
    {
        if (mGourmetDetailParams == null)
        {
            return null;
        }

        return mGourmetDetailParams.getDetailList();
    }

    @Override
    public List<String> getBenefitList()
    {
        if (mGourmetDetailParams == null)
        {
            return null;
        }

        return mGourmetDetailParams.getBenefitList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(mGourmetDetailParams, flags);
        dest.writeInt(hasCoupon == true ? 1 : 0);
        dest.writeInt(index);
        dest.writeInt(listCount);
        dest.writeInt(entryPosition);
        dest.writeString(isShowOriginalPrice);
        dest.writeInt(isDailyChoice == true ? 1 : 0);
    }

    protected void readFromParcel(Parcel in)
    {
        mGourmetDetailParams = in.readParcelable(GourmetDetailParams.class.getClassLoader());
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
        public GourmetDetail createFromParcel(Parcel in)
        {
            return new GourmetDetail(in);
        }

        @Override
        public GourmetDetail[] newArray(int size)
        {
            return new GourmetDetail[size];
        }
    };

    public enum Pictogram implements Parcelable
    {
        parking(R.string.label_parking, R.drawable.f_ic_facilities_05),
        valet(R.string.label_valet_available, R.drawable.f_ic_facilities_10),
        privateRoom(R.string.label_private_room, R.drawable.f_ic_facilities_11),
        groupBooking(R.string.label_group_booking, R.drawable.f_ic_facilities_12),
        babySeat(R.string.label_baby_seat, R.drawable.f_ic_facilities_13),
        corkage(R.string.label_corkage, R.drawable.f_ic_facilities_14),
        none(0, 0);

        private int mNameResId;
        private int mImageResId;

        Pictogram(int nameResId, int imageResId)
        {
            this.mNameResId = nameResId;
            this.mImageResId = imageResId;
        }

        public String getName(Context context)
        {
            if (mNameResId <= 0)
            {
                return "";
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
