package com.spiashko.rfetch.aat.common.person;


import com.spiashko.rfetch.aat.common.crudbase.BaseRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.lang.Nullable;

import javax.persistence.QueryHint;
import java.util.List;

public interface PersonRepository extends BaseRepository<Person> {

    @QueryHints(value = {@QueryHint(name = org.hibernate.annotations.QueryHints.PASS_DISTINCT_THROUGH, value = "false")},
            forCounting = false)
    List<Person> findAll(@Nullable Specification<Person> spec);

}
