package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.Suggest;

import java.util.List;

import io.reactivex.Observable;

public interface SuggestInterface
{
    Observable<List<Suggest>> getSuggestsByStayOutbound(String keyword);
}
