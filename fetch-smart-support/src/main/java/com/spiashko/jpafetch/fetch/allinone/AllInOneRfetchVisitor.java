package com.spiashko.jpafetch.fetch.allinone;

import com.spiashko.jpafetch.parser.RfetchNode;
import com.spiashko.jpafetch.parser.RfetchVisitor;

import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.JoinType;

@SuppressWarnings({"rawtypes", "unchecked"})
class AllInOneRfetchVisitor implements RfetchVisitor<Void, FetchParent> {

    static final AllInOneRfetchVisitor INSTANCE = new AllInOneRfetchVisitor();

    @Override
    public Void visit(RfetchNode node, FetchParent fp) {

        if (node.isLeaf()) {
            return null;
        }

        for (RfetchNode child : node) {
            FetchParent nfp = fp.fetch(child.getName(), JoinType.LEFT);
            child.accept(this, nfp);
        }

        return null;
    }

}
