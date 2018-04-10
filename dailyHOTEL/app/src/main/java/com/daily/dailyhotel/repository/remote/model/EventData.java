package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.twoheart.dailyhotel.network.model.Event;

@JsonObject
public class EventData
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

    public Event getEvent()
    {
        Event event = new Event();
        event.defaultImageUrl = defaultImageUrl;
        event.endedAt = endedAt;
        event.exposeHome = exposeHome;
        event.linkUrl = linkUrl;
        event.lowResolutionImageUrl = lowResolutionImageUrl;
        event.startedAt = startedAt;
        event.title = title;
        event.description = description;
        event.index = index;

        return event;
    }
}
