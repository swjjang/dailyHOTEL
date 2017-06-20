package com.daily.dailyhotel.screen.home.stay.outbound.people;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.People;

import java.util.ArrayList;

public interface SelectPeopleViewInterface extends BaseDialogViewInterface
{
    void setPeople(People people, int adultMin, int adultMax, int childMin, int childMax);

    void setAdultCount(int numberOfAdults, int min, int max);

    void setChildAgeList(ArrayList<Integer> childAgeList, int min, int max);
}
