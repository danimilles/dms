package com.hairyworld.dms.repository;

public class Query {

    private Query() {
    }

    public static final String SELECT_CLIENT_AND_DOGS = """
            SELECT c.name as clientname, phone, c.observations as clientobservations,
            coalesce(iddog, d.id) as iddog, coalesce(idclient, c.id) as idclient, d.name as dogname,
            maintainment, race, d.observations as dogobservations, image
            FROM client c
            LEFT join clientdog cd on c.id = cd.idclient
            LEFT join dog d on d.id = cd.iddog""";

    public static final String SELECT_DATES = """ 
            Select id, datetimestart, datetimeend, iddog, description, idservice, idclient
            from date""";

    public static final String SELECT_SERVICES = """
            select description, id
            from service""";

    public static final String SELECT_PAYMENTS = """
            select id, idservice, idclient, description, datetime, amount
            from payment""";

    public static final String INSERT_CLIENT = """
            INSERT OR REPLACE INTO client (%sname, phone, observations)
            VALUES (%s:name, :phone, :observations)""";

    public static final String INSERT_DOG = """
            INSERT OR REPLACE INTO dog (%sname, race,  maintainment, observations, image)
            VALUES (%s:name, :race, :maintainment, :observations, :image)""";

    public static final String INSERT_CLIENTDOG =
            "INSERT OR REPLACE INTO clientdog (iddog, idclient) VALUES (:iddog, :idclient)";

    public static final String DELETE_CLIENT = """
            DELETE FROM client
            WHERE id = :id""";

    public static final String SELECT_TO_DELETE_DOG_FROM_CLIENT = """
            Select d.id FROM dog d INNER JOIN clientdog cd ON cd.iddog = d.id
              where cd.idclient = :id and id in
               (SELECT cd.iddog FROM clientdog cd GROUP BY cd.iddog HAVING count(cd.iddog) = 1)""";

    public static final String DELETE_DOG_FROM_CLIENT = """
            delete from dog where id in (%s)""";
}
