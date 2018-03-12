package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.NoticeAgreementMessage;

@JsonObject
public class NoticeAgreementMessageData
{
    @JsonField(name = "description1")
    public String description1;

    @JsonField(name = "description2")
    public String description2;

    @JsonField(name = "isFirstTimeBuyer")
    public boolean isFirstTimeBuyer;

    public NoticeAgreementMessageData()
    {

    }

    public NoticeAgreementMessage getNoticeAgreementMessage()
    {
        NoticeAgreementMessage noticeAgreementMessage = new NoticeAgreementMessage();
        noticeAgreementMessage.description1 = description1;
        noticeAgreementMessage.description2 = description2;
        noticeAgreementMessage.firstBuyer = isFirstTimeBuyer;

        return noticeAgreementMessage;
    }
}
