### rest-fetch

#### Objectives

The main point of this repo is show how we can write web services with less code and at the same time get flexible and
extendable solution.

#### How to play with demo

- `mvn clean install`
- `cd demo`
- `docker compose up -d`
- `mvn spring-boot:run -Dspring-boot.run.profiles=demo`

in separate terminal

- install newman if it is not installed yet `npm install -g newman`
- install html reporter for newman if it is not installed yet `npm install -g newman-reporter-htmlextra`
- `cd demo/postman`
- `newman run rest-fetch-demo.postman_collection.json --folder cats -r htmlextra --reporter-htmlextra-export cat-report.html`
- `newman run rest-fetch-demo.postman_collection.json --folder persons -r htmlextra --reporter-htmlextra-export person-report.html`
- look at generated reports in files `cat-report.html` and `person-report.html`

each folder contains 4 request:

- find All - simple query without any param
- find All both - query which will involve rsql and fetch
- find All filter - only rsql
- find All include - only rfetch

in app logs you should find corresponded sql for each request.

#### Rfetch

This is something that is inspired by graphql and json:api but implemented in more simple way where you can control
joining of relations. So with its help you can just add request param
like `include=(relation1,relation2(nestedrelation))`and later in controller you will have three options:

1. use **AllInOneFetchTemplate** (example:(com/spiashko/rfetch/demo/rest/CatRestController.java:33)) which basically
   produce spring data Specification which do fetch join so in the end we will get one sql with number of joins. So it
   solves N+1 but worth to mention that this approach has pitfall which is well described
   in [Vlad's post](https://vladmihalcea.com/hibernate-multiplebagfetchexception/) in short this approach leads to
   cartesian product problem and as a consequence of this force us to use Set but this cartesian product problem is
   fixed by our next option
2. use **LayeredFetchTemplate** (example:(com/spiashko/rfetch/demo/rest/PersonRestController.java:37)) which basically
   solves cartesian product problem by doing only one fetch join on each level and as a consequence of this we don't
   need to use Set. In the end this option fix N+1 and cartesian product problem, but we will have more than one sql
   executed, particularly it will depend on number of included relations
3. use **SmartFetchTemplate** which basically kind of combination of two approaches above which takes the best from
   both, in particular:
   from **AllInOneFetchTemplate** we take nested join, 
   from **LayeredFetchTemplate** we take concept of fixing cartesian product problem.
   Therefore, we have generally fewer numbers of queries then **LayeredFetchTemplate**, and we are not forced to use
   Set for our relations as it is required by **AllInOneFetchTemplate**.

#### Security

With help of `JsonViewSecurityInterceptor` defined as bean we may add security which will basically depends on JsonView
so if relation is not meant to be present, but it is present in `include` or `filter` param then this interceptor will
throw an Exception.

Example - com/spiashko/rfetch/demo/rest/BeforeRequestActionsExecutor.java:21

#### Jackson support

To make response more clear in terms of included data `com.spiashko.rfetch.jackson` package was created.
This package responsible for forcing jackson to serialise only what was initially requested. 
Examples when it may be useful:
 - when we retrieve collection of entities which have relation to itself it leads to situation when hibernate autofill 
   that relation and as result jackson also serialise them in respond, but it creates a mess in response.
 - when we want to include in response some field which is based on some nested entities, but we are actually don't 
   want to see them in response.

#### TODO

- add swagger integration
- tests

#### usage thoughts:

- as this something that configured on each separate endpoint then it means that for some tricky case we can always
  write endpoint in our usual way with dto and other boring things
