package com.spiashko.jpafetch.demo.manual;

import com.spiashko.jpafetch.demo.BaseApplicationTest;
import com.spiashko.jpafetch.demo.person.Person;
import com.spiashko.jpafetch.demo.person.PersonRepository;
import io.github.perplexhub.rsql.RSQLJPASupport;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PropertyPath;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
class JpaManualTest extends BaseApplicationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PersonRepository repository;


    @Test
    void fixCartesianProductProblem() {
        List<Person> people = repository.findAll(Arrays.asList("bestFriendForPeople.kittens", "kittens.father"), RSQLJPASupport.rsql("name!=kek"));
        assertEquals(people.size(), 7);
    }
}
