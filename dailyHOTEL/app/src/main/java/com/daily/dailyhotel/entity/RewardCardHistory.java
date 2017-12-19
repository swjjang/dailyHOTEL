package com.daily.dailyhotel.entity;

import java.util.List;

/**
 * Created by android_sam on 2016. 11. 25..
 */

public class RewardCardHistory
{
    public String startedDate;
    public String couponIssuedDate;
    public List<String> mStickerTypeList;

    public List<String> getStickerTypeList()
    {
        return mStickerTypeList;
    }

    public void setStickerTypeList(List<String> stickerTypeList)
    {
        mStickerTypeList = stickerTypeList;
    }
}
