package com.spiashko.restpersistence.demo.manual;

import com.spiashko.restpersistence.demo.BaseApplicationTest;
import com.spiashko.restpersistence.demo.person.Person;
import com.spiashko.restpersistence.demo.person.PersonRepository;
import io.github.perplexhub.rsql.RSQLJPASupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

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

    @Autowired
    private TransactionTemplate transactionTemplate;

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

//        List<Person> result = transactionTemplate.execute((s) -> {
//            List<Person> jackson = entityManager.createQuery("" +
//                            "select p from Person p " +
//                            "where p.name = 'jackson'", Person.class)
//                    .getResultList();
//
//            List<Person> people = entityManager.createQuery("" +
//                            "select p from Person p " +
//                            "left join fetch p.bestFriend " +
//                            "where p.bestFriend in :jackson", Person.class)
//                    .setParameter("jackson", jackson)
//                    .getResultList();
//
//            return jackson;
//        });

    }


    @Test
    void fixCartesianProductProblem() {

//        List<Person> people = repository.findAll(Arrays.asList("bestFriendForPeople", "kittens"), RSQLJPASupport.rsql("name!=kek"));
        List<Person> people = repository.findAll(Arrays.asList("bestFriendForPeople.kittens"), RSQLJPASupport.rsql("name==jackson"));

        assertEquals(people.size(), 6); // 2 * 2 * 2 - two people with two bestFriendForPeople and two kittens

//        "select person0_.id as id1_1_0_, bestfriend1_.id as id1_1_1_, kittens2_.id as id1_0_2_, person0_.fk_best_friend as fk_best_3_1_0_, person0_.name as name2_1_0_, bestfriend1_.fk_best_friend as fk_best_3_1_1_, bestfriend1_.name as name2_1_1_, bestfriend1_.fk_best_friend as fk_best_3_1_0__, bestfriend1_.id as id1_1_0__, kittens2_.dob as dob2_0_2_, kittens2_.fk_father as fk_fathe5_0_2_, kittens2_.gender as gender3_0_2_, kittens2_.fk_mother as fk_mothe6_0_2_, kittens2_.name as name4_0_2_, kittens2_.fk_owner as fk_owner7_0_2_, kittens2_.fk_owner as fk_owner7_0_1__, kittens2_.id as id1_0_1__ from person person0_ left outer join person bestfriend1_ on person0_.id=bestfriend1_.fk_best_friend left outer join cat kittens2_ on bestfriend1_.id=kittens2_.fk_owner where person0_.id in (?)"
//        "a99bf78d-0d45-4b59-8d72-1f0d9fb454f3"

        // USE bestFriend/bestFriendForPeople to reproduce cartesian product problem
        // https://stackoverflow.com/questions/48520827/use-queryhint-when-using-jpaspecificationexecutor

    }
}
