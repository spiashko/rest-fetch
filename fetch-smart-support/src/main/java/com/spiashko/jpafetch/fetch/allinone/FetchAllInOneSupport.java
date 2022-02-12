package com.spiashko.jpafetch.fetch.allinone;

import com.spiashko.jpafetch.parser.RfetchCompiler;
import com.spiashko.jpafetch.parser.RfetchNode;
import org.springframework.data.jpa.domain.Specification;


public class FetchAllInOneSupport {

    public static <T> Specification<T> toSpecification(String rfetch) {
        return (root, query, builder) -> {
            RfetchNode rfetchRoot = RfetchCompiler.compile(rfetch, root.getJavaType());
            query.distinct(true);
            rfetchRoot.accept(AllInOneRfetchVisitor.INSTANCE, root);
            return null;
        };
    }

}
