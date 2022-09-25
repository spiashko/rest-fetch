package com.spiashko.rfetch.jpa.smart;

import com.spiashko.rfetch.parser.RfetchNode;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
class NodeKey {
    private final Class<?> type;
    private final String name;

    public NodeKey(RfetchNode node) {
        this.name = node.getName();
        this.type = node.getType();
    }
}
