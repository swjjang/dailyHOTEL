package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.NoticeAgreementResultMessage;

@JsonObject
public class NoticeAgreementMessageResultData
{
    @JsonField(name = "agreedAt")
    public String agreedAt;

    @JsonField(name = "description1InAgree")
    public String description1InAgree;

    @JsonField(name = "description2InAgree")
    public String description2InAgree;

    @JsonField(name = "description1InReject")
    public String description1InReject;

    @JsonField(name = "description2InReject")
    public String description2InReject;

    public NoticeAgreementMessageResultData()
    {

    }

    public NoticeAgreementResultMessage getNoticeAgreementResultMessage()
    {
        NoticeAgreementResultMessage noticeAgreementResultMessage = new NoticeAgreementResultMessage();
        noticeAgreementResultMessage.agreedAt = agreedAt;
        noticeAgreementResultMessage.description1InAgree = description1InAgree;
        noticeAgreementResultMessage.description2InAgree = description2InAgree;
        noticeAgreementResultMessage.description1InReject = description1InReject;
        noticeAgreementResultMessage.description2InReject = description2InReject;

        return noticeAgreementResultMessage;
    }
}
