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
        'f3814ab0-71eb-4c54-a27e-c78161110752'::UUID, '6d23e2cc-6fad-4895-9851-d31e1eeffb89'::UUID);
