package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class Event
{
    @JsonField
    public String title;

    @JsonField
    public String defaultImageUrl;

    @JsonField
    public String lowResolutionImageUrl;

    @JsonField
    public String linkUrl;

    @JsonField
    public String startedAt;

    @JsonField
    public String endedAt;

    //    public Event(Parcel in)
    //    {
    //        readFromParcel(in);
    //    }
    //
    //    @Override
    //    public void writeToParcel(Parcel dest, int flags)
    //    {
    //        dest.writeInt(index);
    //        dest.writeString(imageUrl);
    //        dest.writeInt(isJoin ? 1 : 0);
    //    }
    //
    //    private void readFromParcel(Parcel in)
    //    {
    //        index = in.readInt();
    //        imageUrl = in.readString();
    //        isJoin = in.readInt() != 0;
    //    }
    //
    //    @Override
    //    public int describeContents()
    //    {
    //        return 0;
    //    }
    //
    //    public static final Creator CREATOR = new Creator()
    //    {
    //        public Event createFromParcel(Parcel in)
    //        {
    //            return new Event(in);
    //        }
    //
    //        @Override
    //        public Event[] newArray(int size)
    //        {
    //            return new Event[size];
    //        }
    //
    //    };
}
