package com.spiashko.jpafetch.parser;

public interface RfetchVisitor<R, A> {

    R visit(RfetchNode node, A param);

}
