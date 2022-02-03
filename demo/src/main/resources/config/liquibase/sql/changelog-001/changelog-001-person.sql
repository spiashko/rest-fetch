create table person
(
    id   UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    name TEXT UNIQUE NOT NULL
);
