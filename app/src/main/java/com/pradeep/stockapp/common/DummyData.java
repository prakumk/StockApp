package com.pradeep.stockapp.common;


import com.pradeep.stockapp.domain.Name;

import java.util.ArrayList;
import java.util.List;

public class DummyData {

    public static List<Name> getDummyName() {

        List<Name> nameList = new ArrayList<>();

        nameList.add(new Name("Giraffe"));
        nameList.add(new Name("Tiger"));
        nameList.add(new Name("Rhinoceros"));
        nameList.add(new Name("Cat"));
        nameList.add(new Name("Dog"));
        nameList.add(new Name("Bird"));
        nameList.add(new Name("Lion"));
        nameList.add(new Name("Elephant"));
        nameList.add(new Name("Bear"));
        nameList.add(new Name("Cattle"));
        nameList.add(new Name("Wolf"));
        nameList.add(new Name("Rabbit"));
        nameList.add(new Name("Snakes"));
        nameList.add(new Name("Whales"));
        nameList.add(new Name("Fish"));
        nameList.add(new Name("Cow"));
        nameList.add(new Name("Shark"));
        nameList.add(new Name("Deer"));
        nameList.add(new Name("Fox"));
        nameList.add(new Name("Crocodile"));
        nameList.add(new Name("Fox"));

        return nameList;
    }
}
