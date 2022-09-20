drop table if exists cat;
drop table if exists person;

create table person
(
    id             SERIAL PRIMARY KEY,

    name           TEXT UNIQUE NOT NULL,

    fk_best_friend INT REFERENCES person (id)
);

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
