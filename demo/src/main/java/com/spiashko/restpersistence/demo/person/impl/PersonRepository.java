package com.spiashko.restpersistence.demo.person.impl;

import com.spiashko.restpersistence.demo.crudbase.repository.BaseRepository;
import com.spiashko.restpersistence.demo.person.Person;

interface PersonRepository extends BaseRepository<Person> {

//    @QueryHints({@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH, value = "false")})
//    List<Person> findAll(@Nullable Specification<Person> spec);
}
