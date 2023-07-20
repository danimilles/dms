package com.hairyworld.dms.repository;

public class Query {

    private Query() {
    }

    public static final String SELECT_DOGS = """
            SELECT  d.id as iddog, d.name as dogname,
            maintainment, race, d.observations as dogobservations, image
            FROM dog d""";

    public static final String SELECT_CLIENTS = """
            SELECT c.name as clientname, dni, phone, c.observations as clientobservations, c.id as idclient
            FROM client c;""";

    public static final String SELECT_CLIENTDOGS = "SELECT idclient, iddog FROM clientdog";

    public static final String SELECT_DATES = """ 
            Select id, datetimestart, datetimeend, iddog, description, idservice, idclient
            from date""";

    public static final String INSERT_DATE = """ 
            INSERT OR REPLACE INTO date (%sdatetimestart, datetimeend, iddog, description, idservice, idclient)
            VALUES (%s:datetimestart, :datetimeend, :iddog, :description, :idservice, :idclient);""";
    public static final String DELETE_DATE = """ 
            Delete from date where id = :id""";

    public static final String SELECT_SERVICES = """
            select description, id
            from service""";

    public static final String SELECT_PAYMENTS = """
            select id, idservice, idclient, description, datetime, amount
            from payment""";

    public static final String INSERT_CLIENT = """
            INSERT OR REPLACE INTO client (name, dni, phone, observations)
            VALUES (:name, :dni, :phone, :observations)""";

    public static final String INSERT_DOG = """
            INSERT INTO dog (name, race,  maintainment, observations, image)
            VALUES (:name, :race, :maintainment, :observations, :image)""";

    public static final String UPDATE_CLIENT =
            "UPDATE client SET phone = :phone, observations = :observations, name = :name, dni = :dni WHERE id = :id";

    public static final String UPDATE_DOG = """
            UPDATE dog
            SET name = :name, race = :race, maintainment = :maintainment, observations = :observations, image = :image
            WHERE id = :id""";

    public static final String INSERT_CLIENTDOG =
            "INSERT INTO clientdog (iddog, idclient) VALUES (:iddog, :idclient)";

    public static final String DELETE_CLIENTDOG = """
            DELETE FROM clientdog
            WHERE idclient = :idclient and iddog = :iddog""";

    public static final String DELETE_CLIENT = """
            DELETE FROM client
            WHERE id = :id""";

    public static final String DELETE_DOG = """
            DELETE FROM dog
            WHERE id = :id""";

    public static final String SELECT_TO_DELETE_DOG_FROM_CLIENT = """
            Select d.id FROM dog d INNER JOIN clientdog cd ON cd.iddog = d.id
              where cd.idclient = :id and id in
               (SELECT cd.iddog FROM clientdog cd GROUP BY cd.iddog HAVING count(cd.iddog) = 1)""";
}
