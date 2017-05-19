package com.daily.dailyhotel.screen.stay.outbound.persons;

import com.daily.base.BaseViewInterface;
import com.daily.dailyhotel.entity.People;

import java.util.ArrayList;

public interface SelectPeopleViewInterface extends BaseViewInterface
{
    void setPeople(People people);

    void setAdultCount(int numberOfAdults);

    void setChildAgeList(ArrayList<Integer> childAgeList);
}
