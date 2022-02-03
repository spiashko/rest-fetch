### Rest-persistence

#### Objectives

The main point of this repo is show how we can write web services with less code and at the same time get flexible and
extendable solution.

#### How to play with demo

- `mvn clean install`
- `cd demo`
- `docker compose up -d`
- `mvn spring-boot:run -Dspring-boot.run.profiles=demo`

in separate terminal

- `cd demo/postman`
- `newman run rest-persistence-demo.postman_collection.json --folder cats --verbose`
- `newman run rest-persistence-demo.postman_collection.json --folder persons --verbose`

each folder contains 4 request:

- find All - simple query without any param
- find All both - query which will involve rsql and fetch
- find All filter - only rsql
- find All include - only rfetch

in app logs you should find corresponded sql for each request.

#### Rfetch

This is something that is inspired by graphql and json:api but implemented in more simple way where you can control
joining of relations. So with its help you can just add request param like `include=relation1;relation2.nestedrelation`
and in the end spring data Specification will be generated which will basically include fetch join of specified
relations which will give us additional bonus of solving N+1 problem. So in the end frontend can control amount of data
to retrieve, and it will be always only one sql query to database regardless of amount of relations we want to include.

**usage:**

```
    @GetMapping("/cats")
    public List<Cat> findAll(
            @RfetchSpec Specification<Cat> rFetchSpec
    ) {
        List<Cat> result = searchService.findAll(rFetchSpec);
        return result;
    }
```

**security:**

With help of `JsonViewSecurityInterceptor` defined as bean we may add security which will basically depends on JsonView
so if relation is not meant to be present, but it is present in `include` or `filter` param then this interceptor will
throw an Exception.

#### TODO

- need to resolve Cartesian Product problem
- try to apply JSON:API concept
- add swagger integration
- calculated fields in jpa
- services like repositories
- cursor pagination
- tests

#### implementation thoughts:

- JSON:API concept should be applied if we are going to include relations but if we are going to include on level of
  aggregation root then there is no need in JSON:API concept
- BUT looks like it will be easier to create that JSON:API wrapper anyway

#### usage thoughts

- as this something that configured on each separate endpoint then it means that for some tricky case we can always
  write endpoint in our usual way with dto and other boring things


