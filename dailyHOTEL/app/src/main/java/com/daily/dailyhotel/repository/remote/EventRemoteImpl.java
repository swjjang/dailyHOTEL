package com.daily.dailyhotel.repository.remote;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.EventInterface;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.network.dto.BaseListDto;
import com.twoheart.dailyhotel.network.model.Event;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class EventRemoteImpl extends BaseRemoteImpl implements EventInterface
{
    public EventRemoteImpl()
    {
        super();
    }

    @Override
    public Observable<List<Event>> getEventList()
    {
        String store;

        if (Setting.getStore() == Setting.Stores.PLAY_STORE)
        {
            store = "GOOGLE";
        } else
        {
            store = "ONE";
        }

        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/home/events/history"//
            : "MTAkMjgkODUkMjckNDIkNjckODkkODQkODYkNyQ5NiQ2NSQ0MSQ0OCQyMyQ1NSQ=$RjY1NTdDCNkTIwOTc2QUQyMAjFGRTTEC5NUI0RDJDNIkQT1REAU3MEVFCRkRDQUNFREUyERDJUDMTMwRkY0QjgzNTQMzZNTFQYDRAS==$";

        return mDailyMobileService.getEventList(Crypto.getUrlDecoderEx(URL), store) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseListDto<Event>, List<Event>>()
            {
                @Override
                public List<Event> apply(BaseListDto<Event> eventBaseListDto) throws Exception
                {
                    List<Event> eventList = new ArrayList<>();

                    if (eventBaseListDto != null)
                    {
                        if (eventBaseListDto.msgCode == 100 && eventBaseListDto.data != null)
                        {
                            eventList = eventBaseListDto.data;
                        } else
                        {
                            throw new BaseException(eventBaseListDto.msgCode, eventBaseListDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return eventList;
                }
            });
    }
}
