SELECT c.name as clientname, phone, c.observations as clientobservations, iddog, idclient, d.name as dogname,
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