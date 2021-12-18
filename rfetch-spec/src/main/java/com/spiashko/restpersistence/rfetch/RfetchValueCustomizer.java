package com.spiashko.restpersistence.rfetch;

import java.util.List;

public interface RfetchValueCustomizer {

    List<String> customize(final List<String> includedPaths);

}
