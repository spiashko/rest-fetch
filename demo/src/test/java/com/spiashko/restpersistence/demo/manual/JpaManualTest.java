package com.spiashko.restpersistence.demo.manual;

import com.spiashko.restpersistence.demo.BaseApplicationTest;
import com.spiashko.restpersistence.demo.person.Person;
import com.spiashko.restpersistence.demo.person.PersonRepository;
import io.github.perplexhub.rsql.RSQLJPASupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JpaManualTest extends BaseApplicationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PersonRepository repository;

    @Test
    void cartesianProductProblem() {

        List<Person> people = entityManager.createQuery("" +
                        "select p from Person p " +
                        "left join fetch p.kittens " +
                        "left join fetch p.bestFriendForPeople", Person.class)
                .getResultList();

        assertEquals(people.size(), 2 * 2 * 2 + 4); // 2 * 2 * 2 - two people with two bestFriendForPeople and two kittens


        // USE bestFriend/bestFriendForPeople to reproduce cartesian product problem
        // https://stackoverflow.com/questions/48520827/use-queryhint-when-using-jpaspecificationexecutor

    }


    @Test
    void fixCartesianProductProblem() {

        List<Person> people = repository.findAll(Arrays.asList("kittens", "bestFriendForPeople"), RSQLJPASupport.rsql("name!=kek"));

        assertEquals(people.size(), 6); // 2 * 2 * 2 - two people with two bestFriendForPeople and two kittens


        // USE bestFriend/bestFriendForPeople to reproduce cartesian product problem
        // https://stackoverflow.com/questions/48520827/use-queryhint-when-using-jpaspecificationexecutor

    }
}
