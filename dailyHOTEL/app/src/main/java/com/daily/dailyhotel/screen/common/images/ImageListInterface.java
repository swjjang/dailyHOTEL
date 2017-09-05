package com.daily.dailyhotel.screen.common.images;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.StayOutboundDetailImage;

import java.util.List;

public interface ImageListInterface extends BaseDialogViewInterface
{
    void setImageList(List<StayOutboundDetailImage> imageList, int position);
}
