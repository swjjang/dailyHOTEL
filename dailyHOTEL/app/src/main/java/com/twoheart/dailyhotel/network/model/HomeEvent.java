package com.twoheart.dailyhotel.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by android_sam on 2017. 1. 31..
 */

@JsonObject
public class HomeEvent implements Parcelable
{
    @JsonField(name = "defaultImageUrl")
    public String defaultImageUrl;

    @JsonField(name = "endedAt")
    public String endedAt;

    @JsonField(name = "exposeHome")
    public boolean exposeHome;

    @JsonField(name = "linkUrl")
    public String linkUrl;

    @JsonField(name = "lowResolutionImageUrl")
    public String lowResolutionImageUrl;

    @JsonField(name = "startedAt")
    public String startedAt;

    @JsonField(name = "title")
    public String title;

    public HomeEvent()
    {
    }

    // 로컬 저장용 홈 이벤트 처리를 위한 생성자
    public HomeEvent(String defaultImageUrl) {
        this.defaultImageUrl = defaultImageUrl;
    }

    public HomeEvent(Parcel in)
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
    }

    private void readFromParcel(Parcel in)
    {
        defaultImageUrl = in.readString();
        endedAt = in.readString();
        exposeHome = in.readInt() == 1 ? true : false;
        linkUrl = in.readString();
        lowResolutionImageUrl = in.readString();
        startedAt = in.readString();
        title = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @JsonIgnore
    public static final Creator CREATOR = new Creator()
    {
        public HomeEvent createFromParcel(Parcel in)
        {
            return new HomeEvent(in);
        }

        @Override
        public HomeEvent[] newArray(int size)
        {
            return new HomeEvent[size];
        }

    };
}
