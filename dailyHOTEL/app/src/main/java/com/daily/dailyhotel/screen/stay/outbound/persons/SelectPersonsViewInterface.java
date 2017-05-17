package com.daily.dailyhotel.screen.stay.outbound.persons;

import com.daily.base.BaseViewInterface;
import com.daily.dailyhotel.entity.Persons;

import java.util.ArrayList;

public interface SelectPersonsViewInterface extends BaseViewInterface
{
    void setPersons(Persons persons);

    void setAdultCount(int numberOfAdults);

    void setChildAgeList(ArrayList<String> childAgeList);
}
