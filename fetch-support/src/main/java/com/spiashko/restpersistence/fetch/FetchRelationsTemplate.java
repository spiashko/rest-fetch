package com.spiashko.restpersistence.fetch;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
@Slf4j
@UtilityClass
public class FetchRelationsTemplate {

    public static <T> List<T> executeAndEnrichList(List<String> includePaths,
                                                   JpaSpecificationExecutor<T> repository,
                                                   Function<JpaSpecificationExecutor<T>, List<T>> actualOperation) {
        return executeAndEnrich(includePaths,
                () -> actualOperation.apply(repository),
                list -> list,
                repository::findAll
        );
    }

    public static <T> Page<T> executeAndEnrichPage(List<String> includePaths,
                                                   JpaSpecificationExecutor<T> repository,
                                                   Function<JpaSpecificationExecutor<T>, Page<T>> actualOperation) {
        return executeAndEnrich(includePaths,
                () -> actualOperation.apply(repository),
                Slice::getContent,
                repository::findAll
        );
    }

    public static <T> Optional<T> executeAndEnrichOne(List<String> includePaths,
                                                      JpaSpecificationExecutor<T> repository,
                                                      Function<JpaSpecificationExecutor<T>, Optional<T>> actualOperation) {
        return executeAndEnrich(includePaths,
                () -> actualOperation.apply(repository),
                one -> one.map(Collections::singletonList).orElse(Collections.emptyList()),
                repository::findOne
        );
    }

    public static <R, T> R executeAndEnrich(List<String> includePaths,
                                            Supplier<R> actualOperation,
                                            Function<R, Collection<T>> extractor,
                                            Consumer<Specification<T>> fetchExecutor) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new RuntimeException("method must be executed within transaction");
        }

        List<Specification<Object>> includeSpecifications = includePaths.stream()
                .map(FetchRelationsTemplate::buildFetchSpec)
                .collect(Collectors.toList());

        R result = actualOperation.get();
        Collection<T> entities = extractor.apply(result);

        if (CollectionUtils.isEmpty(entities)) {
            log.debug("nothing to enrich");
            return result;
        }

        Specification<Object> limitedByCollection = (root, query, builder) -> root.in(entities);

        for (Specification<Object> spec : includeSpecifications) {
            Specification joinedSpec = spec.and(limitedByCollection);
            fetchExecutor.accept(joinedSpec);
        }

        return result;
    }

    private static Specification<Object> buildFetchSpec(String attributePath) {
        return (root, query, builder) -> {
            PropertyPath path = PropertyPath.from(attributePath, root.getJavaType());
            FetchParent<Object, Object> f = traversePathWithFetch(root, path);
            Join<Object, Object> join = (Join<Object, Object>) f;

            query.distinct(true);

            return join.getOn();
        };
    }

    private static FetchParent<Object, Object> traversePathWithFetch(FetchParent<?, ?> root, PropertyPath path) {
        FetchParent<Object, Object> result = root.fetch(path.getSegment(), JoinType.LEFT);
        return path.hasNext() ? traversePathWithFetch(result, Objects.requireNonNull(path.next())) : result;
    }
}
