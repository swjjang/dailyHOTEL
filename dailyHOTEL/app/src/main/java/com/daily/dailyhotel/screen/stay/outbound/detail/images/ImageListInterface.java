package com.daily.dailyhotel.screen.stay.outbound.detail.images;

import com.daily.base.BaseViewInterface;
import com.daily.dailyhotel.entity.StayOutboundDetailImage;

import java.util.List;

public interface ImageListInterface extends BaseViewInterface
{
    void setImageList(List<StayOutboundDetailImage> imageList, int position);
}
