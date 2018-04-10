package com.daily.dailyhotel.domain;

import com.twoheart.dailyhotel.network.model.Event;

import java.util.List;

import io.reactivex.Observable;

public interface EventInterface
{
    Observable<List<Event>> getEventList();
}
