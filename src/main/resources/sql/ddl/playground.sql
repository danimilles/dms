SELECT c.name as clientname, phone, c.observations as clientobservations,
       coalesce(iddog, d.id) as iddog, coalesce(idclient, c.id) as idclient, d.name as dogname,
       maintainment, race, d.observations as dogobservations, image
FROM client c
    LEFT join clientdog cd on c.id = cd.idclient
    LEFT join dog d on d.id = cd.iddog
ORDER BY idclient, iddog;

Select id, datetimestart, datetimeend, idperro, description, idservicio, idclient
from date;

select description, id
from service;

select id, idservice, idclient, description, datetime
from payment;

 delete from dog where id in (Select d.id FROM dog d INNER JOIN clientdog cd ON cd.iddog = d.id
             where cd.idclient = :id and id in
              (SELECT cd.iddog FROM clientdog cd GROUP BY cd.iddog HAVING count(cd.iddog) = 1))

INSERT INTO client (name, phone, observations, id) VALUES ('dssd', '232332', 'dssdds', 1);
INSERT INTO clientdog (iddog, idclient) VALUES (1, 1);
INSERT INTO client (name, phone, observations, id) VALUES ('dfdsssd', '232332', 'dssdds', 2);
INSERT INTO clientdog (iddog, idclient) VALUES (1, 2);
INSERT INTO dog (name, maintainment, race, observations, image, id)
VALUES ('dssd', 'sdsdds', 'sdsdsd', null, null, 1);
INSERT INTO date (datetimestart, datetimeend, iddog, description, idservice, idclient)
VALUES ('13/07/2023 10:00:00', '13/07/2023 11:00:00', 1, 'fdsfsadfa', null, 1);
INSERT INTO payment (idservice, idclient, description, amount, datetime)
VALUES (null, 1, 'hggfhfhf', 10.25, '13/07/2023 11:00:00');

Select d.id FROM dog d INNER JOIN clientdog cd ON cd.iddog = d.id
where cd.idclient = ? and id in
                          (SELECT cd.iddog FROM clientdog cd GROUP BY cd.iddog HAVING count(cd.iddog) = 1)
SELECT idclient, iddog FROM clientdog