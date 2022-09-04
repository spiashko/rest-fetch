package com.spiashko.rfetch.jpa;


import com.spiashko.rfetch.jpa.allinone.AllInOneFetchTemplate;
import com.spiashko.rfetch.jpa.configs.cats.entites.Person;
import com.spiashko.rfetch.jpa.configs.cats.repos.PersonRepository;
import com.spiashko.rfetch.jpa.layered.LayeredFetchTemplate;
import com.spiashko.rfetch.jpa.smart.SmartFetchTemplate;
import com.spiashko.rfetch.parser.RfetchSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("ResultOfMethodCallIgnored")
class RfetchJpaTest extends BaseApplicationTest {

    @Autowired
    private PersonRepository repository;
    @Autowired
    private LayeredFetchTemplate layeredFetchTemplate;
    @Autowired
    private SmartFetchTemplate smartFetchTemplate;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    void allInOne() {

        Specification<Person> newSpec = AllInOneFetchTemplate.INSTANCE.toFetchSpecification(
                RfetchSupport.compile("(kittens(motherForKids,fatherForKids),bestFriend)", Person.class)
        );

        List<Person> all = repository.findAll(newSpec);

        assertResult(all);
    }

    @Test
    void layered_fixCartesianProductProblem() {

        String rfetch = "(kittens(motherForKids,fatherForKids),bestFriend)";

        List<Person> all = transactionTemplate.execute(s -> {
            List<Person> people = repository.findAll();
            layeredFetchTemplate.enrichList(RfetchSupport.compile(rfetch, Person.class), people);
            return people;
        });

        //noinspection ConstantConditions
        assertResult(all);
    }

    @Test
    void smart_combineBoth() {

        String rfetch = "(kittens(motherForKids,fatherForKids),bestFriend)";

        List<Person> all = transactionTemplate.execute(s -> {
            List<Person> objects = smartFetchTemplate.fetchList(RfetchSupport.compile(rfetch, Person.class));
            return objects;
        });

        //noinspection ConstantConditions
        assertResult(all);
    }

    @Test
    void smart_combineBoth2() {

        String rfetch = "(kittens(mother,father),bestFriend)";

        List<Person> all = transactionTemplate.execute(s -> {
            List<Person> objects = smartFetchTemplate.fetchList(RfetchSupport.compile(rfetch, Person.class));
            return objects;
        });

        //noinspection ConstantConditions
        assertResult2(all);
    }

    private void assertResult(List<Person> all) {
        assertEquals(all.size(), 7);
        all.forEach(p -> {
            p.getBestFriend();
            p.getKittens().forEach(k -> {
                k.getMotherForKids();
                k.getFatherForKids();
            });
        });
    }

    private void assertResult2(List<Person> all) {
        assertEquals(all.size(), 7);
        all.forEach(p -> {
            p.getBestFriend();
            p.getKittens().forEach(k -> {
                k.getMother();
                k.getFather();
            });
        });
    }
}
