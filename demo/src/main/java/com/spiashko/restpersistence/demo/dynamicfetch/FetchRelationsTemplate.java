package com.spiashko.restpersistence.demo.dynamicfetch;

import com.spiashko.restpersistence.rfetch.core.RfetchSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class FetchRelationsTemplate {

    private final RfetchSupport rfetchSupport;

    @Transactional(readOnly = true)
    public <T> List<T> executeAndEnrich(List<String> includePaths,
                                        JpaSpecificationExecutor<T> repository,
                                        Function<JpaSpecificationExecutor<T>, List<T>> actualOperation) {
        List<T> result = executeAndEnrich(includePaths,
                () -> actualOperation.apply(repository),
                list -> list,
                repository::findAll
        );

        return result;
    }

    @Transactional(readOnly = true)
    public <R, T> R executeAndEnrich(List<String> includePaths,
                                     Supplier<R> actualOperation,
                                     Function<R, Collection<T>> extractor,
                                     Consumer<Specification<T>> fetchExecutor) {
        List<Specification<Object>> includeSpecifications = rfetchSupport.toSpecificationList(includePaths);

        R result = actualOperation.get();
        Collection<T> entities = extractor.apply(result);

        Specification<Object> limitedByCollection = (root, query, builder) -> root.in(entities);

        for (Specification<Object> spec : includeSpecifications) {
            Specification joinedSpec = spec.and(limitedByCollection);
            fetchExecutor.accept(joinedSpec);
        }

        return result;
    }
}
