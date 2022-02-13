package com.spiashko.jpafetch.fetch.allinone;

import com.spiashko.jpafetch.parser.RfetchNode;
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
