package com.twoheart.dailyhotel.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class Event implements Parcelable
{
    @JsonField(name = "defaultImageUrl")
    public String defaultImageUrl;

    @JsonField(name = "endedAt")
    public String endedAt; // ISO-8601

    @JsonField(name = "exposeHome")
    public boolean exposeHome;

    @JsonField(name = "linkUrl")
    public String linkUrl;

    @JsonField(name = "lowResolutionImageUrl")
    public String lowResolutionImageUrl;

    @JsonField(name = "startedAt")
    public String startedAt; // ISO-8601

    @JsonField(name = "title")
    public String title;

    @JsonField(name = "description")
    public String description;

    @JsonField(name = "idx")
    public int index;

    public Event()
    {
    }

    // 로컬 저장용 홈 이벤트 처리를 위한 생성자
    public Event(String defaultImageUrl, String lowResolutionImageUrl, String title, String linkUrl, int index)
    {
        this.defaultImageUrl = defaultImageUrl;
        this.lowResolutionImageUrl = lowResolutionImageUrl;
        this.title = title;
        this.linkUrl = linkUrl;
        this.index = index;
    }

    public Event(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(defaultImageUrl);
        dest.writeString(endedAt);
        dest.writeInt(exposeHome == true ? 1 : 0);
        dest.writeString(linkUrl);
        dest.writeString(lowResolutionImageUrl);
        dest.writeString(startedAt);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeInt(index);
    }

    private void readFromParcel(Parcel in)
    {
        defaultImageUrl = in.readString();
        endedAt = in.readString();
        exposeHome = in.readInt() == 1;
        linkUrl = in.readString();
        lowResolutionImageUrl = in.readString();
        startedAt = in.readString();
        title = in.readString();
        description = in.readString();
        index = in.readInt();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @JsonIgnore
    public static final Creator CREATOR = new Creator()
    {
        public Event createFromParcel(Parcel in)
        {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size)
        {
            return new Event[size];
        }

    };
}
