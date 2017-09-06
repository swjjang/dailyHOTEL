package com.daily.dailyhotel.screen.common.images;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.BaseDetailImage;

import java.util.List;

public interface ImageListInterface extends BaseDialogViewInterface
{
    void setImageList(List<BaseDetailImage> imageList, int position);
}
