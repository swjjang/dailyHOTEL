package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.Suggest;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2017. 9. 29..
 */

public interface SuggestLocalInterface
{
    Observable addSuggestDb(Suggest suggest);
}
