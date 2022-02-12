package com.spiashko.jpafetch.demo.manual;

import com.spiashko.jpafetch.demo.BaseApplicationTest;
import com.spiashko.jpafetch.demo.person.Person;
import com.spiashko.jpafetch.demo.person.PersonRepository;
import com.spiashko.jpafetch.fetch.allinone.FetchAllInOneSupport;
import com.spiashko.jpafetch.fetch.smart.FetchSmartTemplate;
import com.spiashko.jpafetch.parser.RfetchSupport;
import io.github.perplexhub.rsql.RSQLJPASupport;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
class JpaManualTest extends BaseApplicationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PersonRepository repository;
    @Autowired
    private FetchSmartTemplate fetchSmartTemplate;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    void allInOne() {

        Specification<Person> newSpec = FetchAllInOneSupport.toSpecification(
                RfetchSupport.compile("(kittens(motherForKids,fatherForKids),bestFriend)", Person.class)
        );

        List<Person> all = repository.findAll(newSpec);
        assertEquals(all.size(), 7);
    }


    @Test
    void fixCartesianProductProblem() {

        String rfetch = "(kittens(motherForKids,fatherForKids),bestFriend)";

        List<Person> result = transactionTemplate.execute(s -> {
            List<Person> people = repository.findAll();
            fetchSmartTemplate.enrichList(RfetchSupport.compile(rfetch, Person.class), people);
            return people;
        });

        assertEquals(result.size(), 7);
    }
}
