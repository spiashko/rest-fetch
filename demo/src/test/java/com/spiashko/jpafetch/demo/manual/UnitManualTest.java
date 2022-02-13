package com.spiashko.jpafetch.demo.manual;

import com.spiashko.jpafetch.demo.person.Person;
import com.spiashko.jpafetch.parser.RfetchSupport;
import com.spiashko.jpafetch.parser.RfetchNode;
import org.junit.jupiter.api.Test;

import java.util.List;

public class UnitManualTest {

    @Test
    void tryNewInclude_justBuild() {
        RfetchNode root = RfetchSupport.compile("(bestFriend)", Person.class);
        List<String> strings = RfetchSupport.effectedPaths(root);
        System.out.println(root);
    }

    @Test
    void tryNewInclude() {
        RfetchNode root = RfetchSupport.compile("(kittens(motherForKids,fatherForKids),bestFriend)", Person.class);


        System.out.println(root);
    }

}
