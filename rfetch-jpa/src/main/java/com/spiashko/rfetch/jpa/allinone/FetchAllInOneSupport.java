package com.spiashko.rfetch.jpa.allinone;

import com.spiashko.rfetch.parser.RfetchNode;
import org.springframework.data.jpa.domain.Specification;


public class FetchAllInOneSupport {

    public static <T> Specification<T> toSpecification(RfetchNode rfetchRoot) {
        return (root, query, builder) -> {
            query.distinct(true);
            rfetchRoot.accept(AllInOneRfetchVisitor.INSTANCE, root);
            return null;
        };
    }

}
