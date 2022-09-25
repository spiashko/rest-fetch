delete
from cat;

delete
from person;

insert into person
values (1, 'bob', 7),
       (2, 'alice', 7),
       (3, 'jackson', 1),
       (4, 'christian', 1),
       (5, 'Helen', 2),
       (6, 'Sonya', 2),
       (7, 'olivier', null)
;

insert into cat
values (1, 'vasily', '2019-01-01', 'MALE', 1, null, null),
       (2, 'marusia', '2019-02-01', 'FEMALE', 2, null, null),
       (3, 'scooter', '2020-07-01', 'MALE', 1, 1, 2),
       (4, 'rose', '2020-07-01', 'FEMALE', 2, 1, 2)
;
