package com.spiashko.rfetch.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spiashko.rfetch.aat.common.cat.Cat;
import com.spiashko.rfetch.aat.common.person.Person;
import com.spiashko.rfetch.aat.common.person.PersonRepository;
import com.spiashko.rfetch.jpa.smart.SmartFetchTemplate;
import com.spiashko.rfetch.parser.RfetchNode;
import com.spiashko.rfetch.parser.RfetchSupport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class JacksonSupportTest extends BaseApplicationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SmartFetchTemplate smartFetchTemplate;

    @Autowired
    private PersonRepository repository;

    @Test
    void simpleTest() throws JsonProcessingException {

        String rfetch = "(kittens(motherForKids,fatherForKids),bestFriend)";
        RfetchNode root = RfetchSupport.compile(rfetch, Person.class);

        List<Person> all = smartFetchTemplate.fetchList(root, repository, null);
        IncludePathsHolder.setIncludedPaths(root);
        String allAsString = objectMapper.writeValueAsString(all);
        List<Person> deserializedAll = objectMapper.readValue(allAsString, new TypeReference<List<Person>>() {
        });

        boolean scootersFatherHasName = all.stream()
                .filter(p -> p.getName().equals("alice"))
                .map(Person::getKittens)
                .flatMap(Collection::stream)
                .filter(kitten -> kitten.getName().equals("scooter"))
                .map(Cat::getFather)
                .map(Cat::getName)
                .allMatch(Objects::isNull);
        assertTrue(scootersFatherHasName);

        boolean scootersFatherDontHaveName = deserializedAll.stream()
                .filter(p -> p.getName().equals("alice"))
                .map(Person::getKittens)
                .flatMap(Collection::stream)
                .filter(kitten -> kitten.getName().equals("scooter"))
                .map(Cat::getFather)
                .map(Cat::getName)
                .allMatch(Objects::isNull);
        assertTrue(scootersFatherDontHaveName);
    }

}
