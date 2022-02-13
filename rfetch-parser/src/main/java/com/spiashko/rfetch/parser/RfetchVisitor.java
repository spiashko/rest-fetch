package com.spiashko.rfetch.parser;

public interface RfetchVisitor<R, A> {

    R visit(RfetchNode node, A param);

}
