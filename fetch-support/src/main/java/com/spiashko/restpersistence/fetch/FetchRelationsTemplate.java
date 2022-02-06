package com.spiashko.restpersistence.fetch;

import com.spiashko.restpersistence.spec.SpecSupport;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Slf4j
@UtilityClass
public class FetchRelationsTemplate {

    public static <T> List<T> executeAndEnrichList(List<String> includePaths,
                                                   JpaSpecificationExecutor<T> repository,
                                                   Function<JpaSpecificationExecutor<T>, List<T>> actualOperation) {
        List<T> result = executeAndEnrich(includePaths,
                () -> actualOperation.apply(repository),
                list -> list,
                repository::findAll
        );

        return result;
    }

    public static <T> Page<T> executeAndEnrichPage(List<String> includePaths,
                                                   JpaSpecificationExecutor<T> repository,
                                                   Function<JpaSpecificationExecutor<T>, Page<T>> actualOperation) {
        Page<T> result = executeAndEnrich(includePaths,
                () -> actualOperation.apply(repository),
                Slice::getContent,
                repository::findAll
        );

        return result;
    }

    public static <T> Optional<T> executeAndEnrichOne(List<String> includePaths,
                                                      JpaSpecificationExecutor<T> repository,
                                                      Function<JpaSpecificationExecutor<T>, Optional<T>> actualOperation) {
        Optional<T> result = executeAndEnrich(includePaths,
                () -> actualOperation.apply(repository),
                one -> one.map(Collections::singletonList).orElse(Collections.EMPTY_LIST),
                repository::findOne
        );

        return result;
    }

    public static <R, T> R executeAndEnrich(List<String> includePaths,
                                            Supplier<R> actualOperation,
                                            Function<R, Collection<T>> extractor,
                                            Consumer<Specification<T>> fetchExecutor) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new RuntimeException("method must be executed within transaction");
        }

        List<Specification<Object>> includeSpecifications = includePaths.stream()
                .map(SpecSupport::buildFetchSpec)
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
}
