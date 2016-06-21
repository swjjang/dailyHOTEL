package com.twoheart.dailyhotel.screen.hotel.list;

/**
 * Created by sheldon on 2016. 6. 17..
 */
public class StayMapListManager
{
    private static StayMapListManager mInstance;

    public synchronized StayMapListManager getInstance()
    {
        if(mInstance == null)
        {
            mInstance = new StayMapListManager();
        }

        return mInstance;
    }

    private StayMapListManager()
    {
    }

    public void clear()
    {

    }
}
