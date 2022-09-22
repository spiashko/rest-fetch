--liquibase formatted sql
--changeset spiashko:add_person_table dbms:postgresql

create table person
(
    id             SERIAL PRIMARY KEY,

    name           TEXT UNIQUE NOT NULL,

    fk_best_friend INT REFERENCES person (id)
);

--rollback drop table person;


--changeset spiashko:add_cat_table dbms:postgresql

create table cat
(
    id        SERIAL PRIMARY KEY,

    name      TEXT UNIQUE                NOT NULL,
    dob       DATE                       NOT NULL,

    gender    TEXT                       NOT NULL,

    fk_owner  INT REFERENCES person (id) NOT NULL,

    fk_father INT REFERENCES cat (id),
    fk_mother INT REFERENCES cat (id)
);

--rollback drop table cat;

--changeset spiashko:add_person_demo_data context:demo dbms:postgresql

insert into person
values (1, 'bob', 7),
       (2, 'alice', 7),
       (3, 'jackson', 1),
       (4, 'christian', 1),
       (5, 'Helen', 2),
       (6, 'Sonya', 2),
       (7, 'olivier', null)
;

--rollback delete from person;


--changeset spiashko:add_cat_demo_data context:demo dbms:postgresql

insert into cat
values (1, 'vasily', '2019-01-01', 'MALE', 1, null, null),
       (2, 'marusia', '2019-02-01', 'FEMALE', 2, null, null),
       (3, 'scooter', '2020-07-01', 'MALE', 1, 1, 2),
       (4, 'rose', '2020-07-01', 'FEMALE', 2, 1, 2)
;

--rollback delete from cat;
