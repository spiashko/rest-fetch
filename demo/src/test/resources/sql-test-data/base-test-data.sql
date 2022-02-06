delete
from cat;

delete
from person;

insert into person
values ('6f4e1ed3-d8e7-47bf-8689-020c5fe54c1c'::UUID, 'bob', '752b4b5f-c5b8-4a44-99d2-a685cd0b0e33'::UUID),
       ('5ced6a48-f31c-45d3-af07-95dbc722614a'::UUID, 'alice', '752b4b5f-c5b8-4a44-99d2-a685cd0b0e33'::UUID),
       ('a99bf78d-0d45-4b59-8d72-1f0d9fb454f3'::UUID, 'jackson', '6f4e1ed3-d8e7-47bf-8689-020c5fe54c1c'::UUID),
       ('ec94e2b4-8197-4ce5-93e4-c67f65706d65'::UUID, 'christian', '6f4e1ed3-d8e7-47bf-8689-020c5fe54c1c'::UUID),
       ('c87891c4-0673-4a2d-8ae4-e3ab997ad1e0'::UUID, 'Helen', '5ced6a48-f31c-45d3-af07-95dbc722614a'::UUID),
       ('a0b08c21-f71c-4093-878b-3f64bc878926'::UUID, 'Sonya', '5ced6a48-f31c-45d3-af07-95dbc722614a'::UUID),
       ('752b4b5f-c5b8-4a44-99d2-a685cd0b0e33'::UUID, 'olivier', null)
;

insert into cat
values ('f3814ab0-71eb-4c54-a27e-c78161110752'::UUID, 'vasily', '2019-01-01',
        'MALE', '6f4e1ed3-d8e7-47bf-8689-020c5fe54c1c'::UUID, null, null),
       ('6d23e2cc-6fad-4895-9851-d31e1eeffb89'::UUID, 'marusia', '2019-02-01',
        'FEMALE', '5ced6a48-f31c-45d3-af07-95dbc722614a'::UUID, null, null),
       ('3a14e47d-3830-434e-a612-47d55b6aca46'::UUID, 'scooter', '2020-07-01',
        'MALE', '6f4e1ed3-d8e7-47bf-8689-020c5fe54c1c'::UUID,
        'f3814ab0-71eb-4c54-a27e-c78161110752'::UUID, '6d23e2cc-6fad-4895-9851-d31e1eeffb89'::UUID),
       ('c0fb2cec-f296-4e2f-9136-c9153f6f70b1'::UUID, 'rose', '2020-07-01',
        'FEMALE', '5ced6a48-f31c-45d3-af07-95dbc722614a'::UUID,
        'f3814ab0-71eb-4c54-a27e-c78161110752'::UUID, '6d23e2cc-6fad-4895-9851-d31e1eeffb89'::UUID)
;
