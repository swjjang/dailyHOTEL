package com.twoheart.dailyhotel.model;

import com.twoheart.dailyhotel.network.model.ImageInformation;

import java.util.List;

public abstract class PlaceDetail<E>
{
    // GA용
    public int index = -1;
    //    public int listCount = -1; //  -1 값 일 경우 Deeplink 로 진입 된 경우이며, 상세 진입시 진입 전 list의 노출 갯수 GA 용
    //    public int entryPosition; // -1 값 일 경우 Deeplink 로 진입 된 경우이며, index는 1부터 시작함 GA 용
    //    public String isShowOriginalPrice; // "Y", "N", empty
    //    public boolean isDailyChoice;

    public abstract List<E> getProductList();

    public abstract E getProduct(int index);

    public abstract List getPictogramList();

    public abstract List<ImageInformation> getImageList();

    public abstract List getDetailList();

    public abstract List getBenefitList();
}
