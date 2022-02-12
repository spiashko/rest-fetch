package com.spiashko.jpafetch.demo.manual;

import com.spiashko.jpafetch.demo.person.Person;
import com.spiashko.jpafetch.parser.RfetchCompiler;
import com.spiashko.jpafetch.parser.RfetchNode;
import org.junit.jupiter.api.Test;

public class UnitManualTest {

    @Test
    void tryNewInclude_justBuild() {
        RfetchNode root = RfetchCompiler.compile("(kittens(motherForKids,fatherForKids),bestFriend)", Person.class);
        System.out.println(root);
    }

    @Test
    void tryNewInclude() {
        RfetchNode root = RfetchCompiler.compile("(kittens(motherForKids,fatherForKids),bestFriend)", Person.class);


        System.out.println(root);
    }

}
