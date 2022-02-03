create table cat
(
    id        UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    name      TEXT UNIQUE                 NOT NULL,
    dob       DATE                        NOT NULL,

    gender    TEXT                        NOT NULL,

    fk_owner  UUID REFERENCES person (id) NOT NULL,

    fk_father UUID REFERENCES cat (id),
    fk_mother UUID REFERENCES cat (id)
);
