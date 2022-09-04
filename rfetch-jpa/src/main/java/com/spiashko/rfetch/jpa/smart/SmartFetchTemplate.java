package com.spiashko.rfetch.jpa.smart;

import com.spiashko.rfetch.parser.RfetchNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class SmartFetchTemplate {

    private final EntityManager em;

    public <T> List<T> fetchList(RfetchNode rfetchRoot) {
        return fetch(rfetchRoot);
    }


    @SuppressWarnings("unchecked")
    public <T> List<T> fetch(RfetchNode rfetchRoot) {
        if (rfetchRoot == null) {
            log.debug("nothing to fetch");
            return Collections.emptyList();
        }

        return (List<T>) rfetchRoot.accept(new SmartRfetchVisitor(em, rfetchRoot), null);
    }


}
