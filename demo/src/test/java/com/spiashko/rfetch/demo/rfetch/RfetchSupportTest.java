package com.spiashko.rfetch.demo.rfetch;

import com.spiashko.rfetch.demo.person.Person;
import com.spiashko.rfetch.parser.RfetchNode;
import com.spiashko.rfetch.parser.RfetchSupport;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RfetchSupportTest {

    @Test
    void tryNewInclude_simple() {
        RfetchNode root = RfetchSupport.compile("(bestFriend)", Person.class);
        List<String> strings = RfetchSupport.effectedPaths(root);
        assertEquals(strings.size(), 1);
    }

    @Test
    void tryNewInclude_complex() {
        RfetchNode root = RfetchSupport.compile("(kittens(motherForKids,fatherForKids),bestFriend)", Person.class);
        List<String> strings = RfetchSupport.effectedPaths(root);
        assertEquals(strings.size(), 3);
    }

}
