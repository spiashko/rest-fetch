package com.spiashko.jpafetch.demo.manual;

import com.spiashko.jpafetch.demo.BaseApplicationTest;
import com.spiashko.jpafetch.demo.person.Person;
import com.spiashko.jpafetch.demo.person.PersonRepository;
import com.spiashko.jpafetch.fetch.FetchSmartTemplate;
import io.github.perplexhub.rsql.RSQLJPASupport;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
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
    void fixCartesianProductProblem() {

        List<Person> result = transactionTemplate.execute(s -> {
            List<Person> people = repository.findAll(RSQLJPASupport.rsql("name!=kek"));
            fetchSmartTemplate.enrichList(Arrays.asList("bestFriendForPeople.kittens", "kittens.father"), Person.class, people);
            return people;
        });

        assertEquals(result.size(), 7);
    }
}
