package com.daily.dailyhotel.screen.common.images;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.DetailImageInformation;

import java.util.List;

public interface ImageListInterface extends BaseDialogViewInterface
{
    void setImageList(List<DetailImageInformation> imageList, int position);
}
