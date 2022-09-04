package com.spiashko.rfetch.jpa.allinone;

import com.spiashko.rfetch.parser.RfetchNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;


@Slf4j
public class AllInOneFetchTemplate {

    public static final AllInOneFetchTemplate INSTANCE = new AllInOneFetchTemplate();

    public <T> Specification<T> toFetchSpecification(RfetchNode rfetchRoot) {
        if (rfetchRoot == null || CollectionUtils.isEmpty(rfetchRoot.getChildren())) {
            log.debug("no additional fetch is requested");
            return (root, query, builder) -> null;
        }
        return (root, query, builder) -> {
            query.distinct(true);
            rfetchRoot.accept(AllInOneRfetchVisitor.INSTANCE, root);
            return null;
        };
    }

}
