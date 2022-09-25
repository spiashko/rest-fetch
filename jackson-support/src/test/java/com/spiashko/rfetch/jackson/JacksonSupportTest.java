package com.spiashko.rfetch.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spiashko.rfetch.aat.common.person.Person;
import com.spiashko.rfetch.aat.common.person.PersonRepository;
import com.spiashko.rfetch.jpa.smart.SmartFetchTemplate;
import com.spiashko.rfetch.parser.RfetchNode;
import com.spiashko.rfetch.parser.RfetchSupport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        log.info("allAsString: " + allAsString);
        assertNotNull(allAsString);
        assertEquals(
                "[{\"id\":4,\"name\":\"christian\",\"bestFriend\":{\"id\":1,\"name\":\"bob\",\"bestFriend\":{\"id\":7}}},{\"id\":2,\"name\":\"alice\",\"kittens\":[{\"id\":4,\"name\":\"rose\",\"dob\":\"2020-07-01\",\"owner\":{\"id\":2},\"father\":{\"id\":1},\"mother\":{\"id\":2},\"gender\":\"FEMALE\"},{\"id\":2,\"name\":\"marusia\",\"dob\":\"2019-02-01\",\"owner\":{\"id\":2},\"gender\":\"FEMALE\",\"motherForKids\":[{\"id\":3,\"name\":\"scooter\",\"dob\":\"2020-07-01\",\"owner\":{\"id\":1},\"father\":{\"id\":1},\"mother\":{\"id\":2},\"gender\":\"MALE\"},{\"id\":4,\"name\":\"rose\",\"dob\":\"2020-07-01\",\"owner\":{\"id\":2},\"father\":{\"id\":1},\"mother\":{\"id\":2},\"gender\":\"FEMALE\"}]}],\"bestFriend\":{\"id\":7,\"name\":\"olivier\"}},{\"id\":7,\"name\":\"olivier\"},{\"id\":6,\"name\":\"Sonya\",\"bestFriend\":{\"id\":2,\"name\":\"alice\",\"bestFriend\":{\"id\":7}}},{\"id\":1,\"name\":\"bob\",\"kittens\":[{\"id\":3,\"name\":\"scooter\",\"dob\":\"2020-07-01\",\"owner\":{\"id\":1},\"father\":{\"id\":1},\"mother\":{\"id\":2},\"gender\":\"MALE\"},{\"id\":1,\"name\":\"vasily\",\"dob\":\"2019-01-01\",\"owner\":{\"id\":1},\"gender\":\"MALE\"}],\"bestFriend\":{\"id\":7,\"name\":\"olivier\"}},{\"id\":3,\"name\":\"jackson\",\"bestFriend\":{\"id\":1,\"name\":\"bob\",\"bestFriend\":{\"id\":7}}},{\"id\":5,\"name\":\"Helen\",\"bestFriend\":{\"id\":2,\"name\":\"alice\",\"bestFriend\":{\"id\":7}}}]",
                allAsString
        );
    }

}
