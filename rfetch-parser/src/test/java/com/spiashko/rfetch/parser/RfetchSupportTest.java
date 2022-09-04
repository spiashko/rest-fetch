package com.spiashko.rfetch.parser;

import com.spiashko.rfetch.parser.entites.cats.Person;
import com.spiashko.rfetch.parser.entites.courses.Course;
import com.spiashko.rfetch.parser.entites.courses.CourseExtraInfo;
import com.spiashko.rfetch.parser.entites.courses.Enrollment;
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

    @Test
    void courseExtraInfoTest() {
        RfetchNode root = RfetchSupport.compile("(course(modules(lessons),teacher))", CourseExtraInfo.class);
        List<String> strings = RfetchSupport.effectedPaths(root);
        assertEquals(strings.size(), 2);
    }

    @Test
    void enrollmentTest() {
        RfetchNode root = RfetchSupport.compile("(course(teacher))", Enrollment.class);
        List<String> strings = RfetchSupport.effectedPaths(root);
        assertEquals(strings.size(), 1);
    }

    @Test
    void cloneTest() {
        RfetchNode root = RfetchSupport.compile("(course(modules(lessons)),completedLessons)", Enrollment.class);
        assert root != null;

        RfetchNode clone = root.deepClone(null);

        List<String> strings = RfetchSupport.effectedPaths(clone);
        assertEquals(strings.size(), 2);
    }

    @Test
    void mergeTest() {
        RfetchNode root = RfetchSupport.compile("(course(teacher))", Enrollment.class);
        RfetchNode anotherRoot = RfetchSupport.compile("(modules(lessons))", Course.class);
        assert root != null;
        assert anotherRoot != null;

        RfetchNode course = root.getChildren().stream()
                .filter(n -> n.getName().equals("course"))
                .findAny()
                .orElse(null);

        assert course != null;
        course.merge(anotherRoot);

        List<String> strings = RfetchSupport.effectedPaths(root);
        assertEquals(strings.size(), 2);
    }

    @Test
    void mergeRootsTest() {
        RfetchNode root = RfetchSupport.compile("(student)", Enrollment.class);
        RfetchNode anotherRoot = RfetchSupport.compile("(course(modules(lessons)),completedLessons)", Enrollment.class);
        assert root != null;
        assert anotherRoot != null;

        root.merge(anotherRoot);

        List<String> strings = RfetchSupport.effectedPaths(root);
        assertEquals(strings.size(), 3);
    }


}
