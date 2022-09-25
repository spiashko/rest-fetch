package com.spiashko.rfetch.jpa;


import com.spiashko.rfetch.aat.common.person.Person;
import com.spiashko.rfetch.aat.common.person.PersonRepository;
import com.spiashko.rfetch.aat.common.person.Person_;
import com.spiashko.rfetch.jpa.allinone.AllInOneFetchTemplate;
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

        Specification<Person> spec = AllInOneFetchTemplate.INSTANCE.toFetchSpecification(
                RfetchSupport.compile("(kittens(motherForKids,fatherForKids),bestFriend)", Person.class)
        );

        List<Person> all = repository.findAll(spec);

        assertResult(all);
    }

    @Test
    void allInOneWithFilter() {

        Specification<Person> fetchSpec = AllInOneFetchTemplate.INSTANCE.toFetchSpecification(
                RfetchSupport.compile("(bestFriendForPeople)", Person.class)
        );

        Specification<Person> filterSpec = (r, qb, cb) ->
                cb.equal(r.join(Person_.bestFriendForPeople).get(Person_.name), cb.literal("bob"));

        Specification<Person> spec = Specification.where(fetchSpec).and(filterSpec);

        List<Person> all = repository.findAll(spec);

        assertEquals(all.size(), 1);
        assertEquals(all.get(0).getBestFriendForPeople().size(), 2);
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

        List<Person> all = transactionTemplate.execute(s ->
                smartFetchTemplate.fetchList(RfetchSupport.compile(rfetch, Person.class), repository, null)
        );

        assertResult(all);
    }

    @Test
    void smart_combineBoth2() {

        String rfetch = "(kittens(mother,father),bestFriend)";

        List<Person> all = transactionTemplate.execute(s ->
                smartFetchTemplate.fetchList(RfetchSupport.compile(rfetch, Person.class), repository, null)
        );

        assertResult2(all);
    }

    private void assertResult(List<Person> all) {
        assertEquals(all.size(), 7);
        all.forEach(p -> {
            p.getBestFriend();
            p.getKittens().forEach(k -> {
                k.getMotherForKids().size();
                k.getFatherForKids().size();
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
