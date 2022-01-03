package com.spiashko.restpersistence.rfetch.annotation;

import java.util.List;

public interface RfetchValueCustomizer {

    List<String> customize(final List<String> includedPaths);

}
