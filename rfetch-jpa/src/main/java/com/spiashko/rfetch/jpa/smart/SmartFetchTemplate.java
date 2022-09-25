package com.spiashko.rfetch.jpa.smart;

import com.spiashko.rfetch.jpa.allinone.AllInOneFetchTemplate;
import com.spiashko.rfetch.parser.RfetchNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class SmartFetchTemplate {

    private final EntityManager em;

    public <T> List<T> fetchList(RfetchNode rfetchRoot, JpaSpecificationExecutor<T> repo, Specification<T> spec) {
        List<T> fetchResult = fetch(rfetchRoot,
                fetchSpec -> repo.findAll(Specification.where(spec).and(fetchSpec)),
                Function.identity());

        if (fetchResult == null) {
            return Collections.emptyList();
        }

        return fetchResult;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> Page<T> fetchPage(RfetchNode rfetchRoot, JpaSpecificationExecutor<T> repo, Specification<T> spec,
                                 Pageable pageable) {
        Page fetchResult = fetch(rfetchRoot,
                fetchSpec -> repo.findAll(Specification.where(fetchSpec).and((Specification) spec), pageable),
                Slice::getContent);

        if (fetchResult == null) {
            return Page.empty();
        }

        return fetchResult;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> Optional<T> fetchOne(RfetchNode rfetchRoot, JpaSpecificationExecutor<T> repo, Specification<T> spec) {
        return fetch(rfetchRoot,
                fetchSpec -> repo.findOne(Specification.where(fetchSpec).and((Specification) spec)),
                object -> object.isPresent() ? Collections.singletonList(object.get()) : Collections.emptyList());
    }

    @Transactional(readOnly = true)
    public <T, R> R fetch(RfetchNode rfetchRoot,
                          Function<Specification<T>, R> initialLoadFunction,
                          Function<R, List<T>> returnedObjectToListMapper) {
        if (rfetchRoot == null) {
            log.debug("nothing to fetch");
            return null;
        }

        SubFetchesBuilder subFetchesBuilder = new SubFetchesBuilder();
        RfetchNode initialSubFetch = subFetchesBuilder.buildSubFetches(rfetchRoot);
        Map<NodeKey, List<RfetchNode>> subFetches = subFetchesBuilder.getSubFetches();

        R returnedObject = initialLoadFunction.apply(AllInOneFetchTemplate.INSTANCE
                .toFetchSpecification(initialSubFetch));
        List<?> objects = returnedObjectToListMapper.apply(returnedObject);
        rfetchRoot.accept(new SmartRfetchVisitor(em, subFetches, rfetchRoot, initialSubFetch, objects), null);

        return returnedObject;
    }

}
