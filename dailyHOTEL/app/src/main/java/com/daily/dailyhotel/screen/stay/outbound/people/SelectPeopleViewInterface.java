package com.daily.dailyhotel.screen.stay.outbound.people;

import com.daily.base.BaseViewInterface;
import com.daily.dailyhotel.entity.People;

import java.util.ArrayList;

public interface SelectPeopleViewInterface extends BaseViewInterface
{
    void setPeople(People people, int adultMin, int adultMax, int childMin, int childMax);

    void setAdultCount(int numberOfAdults, int min, int max);

    void setChildAgeList(ArrayList<Integer> childAgeList, int min, int max);
}
