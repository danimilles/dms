package com.hairyworld.dms.repository;

public class Query {

    private Query() {
    }

    public static final String SELECT_CLIENT_AND_DOGS = """
            SELECT c.name as clientname, phone, c.observations as clientobservations, iddog, idclient, d.name as dogname,
            mantainment, race, d.observations as dogobservations, image
            FROM clientEntity c
            LEFT join clientdog cd on c.id = cd.idclient
            INNER join dog d on d.id = cd.iddog""";

    public static final String SELECT_DATES = """ 
            Select id, datetimestart, datetimeend, iddog, description, idservice, idclient
            from date""";

    public static final String SELECT_SERVICES = """
            select description, id
            from service""";

    public static final String SELECT_PAYMENTS = """
            select id, idservice, idclient, description, datetime
            from payment""";
}
