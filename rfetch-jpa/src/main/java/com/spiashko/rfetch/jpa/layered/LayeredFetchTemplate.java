package com.spiashko.rfetch.jpa.layered;

import com.spiashko.rfetch.parser.RfetchNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class LayeredFetchTemplate {

    private final EntityManager em;

    public <T> void enrichList(RfetchNode rfetchRoot, List<T> entities) {
        enrich(rfetchRoot, entities);
    }

    public <T> void enrichPage(RfetchNode rfetchRoot, Page<T> entitiesPage) {
        enrich(rfetchRoot, entitiesPage.getContent());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public <T> void enrichOne(RfetchNode rfetchRoot, Optional<T> entity) {
        enrich(rfetchRoot, entity.map(Collections::singletonList).orElse(Collections.emptyList()));
    }


    public <T> void enrich(RfetchNode rfetchRoot, Collection<T> rootEntities) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new RuntimeException("method must be executed within transaction");
        }

        if (rfetchRoot == null || CollectionUtils.isEmpty(rfetchRoot.getChildren())) {
            log.debug("nothing to enrich");
            return;
        }

        if (CollectionUtils.isEmpty(rootEntities)) {
            log.debug("no entities to enrich");
            return;
        }

        rfetchRoot.accept(new LayeredRfetchVisitor(em), rootEntities);

    }
}
