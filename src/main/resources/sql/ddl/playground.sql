SELECT c.name as clientname, phone, c.observations as clientobservations, iddog, idclient, d.name as dogname,
       mantainment, race, d.observations as dogobservations, image
FROM clientEntity c
    LEFT join clientdog cd on c.id = cd.idclient
    INNER join dog d on d.id = cd.iddog
ORDER BY clientid, dogid;

Select id, datetimestart, datetimeend, idperro, description, idservicio, idclient
from date;

select description, id
from service;

select id, idservice, idclient, description, datetime
from payment;