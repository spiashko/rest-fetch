package com.spiashko.rfetch.jpa;


import com.spiashko.rfetch.jpa.allinone.FetchAllInOneSpecTemplate;
import com.spiashko.rfetch.jpa.configs.cats.entites.Person;
import com.spiashko.rfetch.jpa.configs.cats.repos.PersonRepository;
import com.spiashko.rfetch.jpa.smart.FetchSmartTemplate;
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
    private FetchSmartTemplate fetchSmartTemplate;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    void allInOne() {

        Specification<Person> newSpec = FetchAllInOneSpecTemplate.INSTANCE.toSpecification(
                RfetchSupport.compile("(kittens(motherForKids,fatherForKids),bestFriend)", Person.class)
        );

        List<Person> all = repository.findAll(newSpec);

        assertResult(all);
    }

    @Test
    void smart_fixCartesianProductProblem() {

        String rfetch = "(kittens(motherForKids,fatherForKids),bestFriend)";

        List<Person> all = transactionTemplate.execute(s -> {
            List<Person> people = repository.findAll();
            fetchSmartTemplate.enrichList(RfetchSupport.compile(rfetch, Person.class), people);
            return people;
        });

        //noinspection ConstantConditions
        assertResult(all);
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
}
