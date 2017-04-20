package com.daily.dailyhotel.entity;

public class CommonDateTime
{
    public String openDateTime; // ISO-8601
    public String closeDateTime; // ISO-8601
    public String currentDateTime; // ISO-8601
    public String dailyDateTime; // ISO-8601

    public CommonDateTime()
    {
    }

    public CommonDateTime(String openDateTime, String closeDateTime, String currentDateTime, String dailyDateTime)
    {
        setDateTime(openDateTime, closeDateTime, currentDateTime, dailyDateTime);
    }

    public CommonDateTime getClone()
    {
        return new CommonDateTime(openDateTime, closeDateTime, currentDateTime, dailyDateTime);
    }

    public void setDateTime(String openDateTime, String closeDateTime, String currentDateTime, String dailyDateTime)
    {
        this.openDateTime = openDateTime;
        this.closeDateTime = closeDateTime;
        this.currentDateTime = currentDateTime;
        this.dailyDateTime = dailyDateTime;
    }
}
