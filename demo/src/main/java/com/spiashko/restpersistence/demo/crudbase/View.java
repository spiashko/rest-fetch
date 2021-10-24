package com.spiashko.restpersistence.demo.crudbase;

public interface View {
    //@formatter:off
    interface Create {}
    interface Retrieve {}

    interface CatCreate extends Create {}
    interface PersonCreate extends Create {}
    //@formatter:on
}
