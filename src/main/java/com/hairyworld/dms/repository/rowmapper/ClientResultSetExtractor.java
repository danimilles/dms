package com.hairyworld.dms.repository.rowmapper;

import com.hairyworld.dms.model.entity.ClientEntity;
import com.hairyworld.dms.model.entity.Entity;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ClientResultSetExtractor implements ResultSetExtractor<Map<Long, Entity>> {

    private static final String IDCLIENT_FIELD = "idclient";
    private static final String CLIENTNAME_FIELD = "clientname";
    private static final String PHONE_FIELD = "phone";
    private static final String CLIENTOBSERVATIONS_FIELD = "clientobservations";


    public ClientResultSetExtractor() {
        super();
    }

    @Override
    public Map<Long, Entity> extractData(final ResultSet resultSet) throws DataAccessException, SQLException {
        final Map<Long, Entity> clientEntityMap = new HashMap<>();

        while (resultSet.next()) {
            final Entity clientEntity = ClientEntity.builder()
                    .id(resultSet.getLong(IDCLIENT_FIELD))
                    .name(resultSet.getString(CLIENTNAME_FIELD))
                    .phone(resultSet.getString(PHONE_FIELD))
                    .observations(resultSet.getString(CLIENTOBSERVATIONS_FIELD))
                    .dogIds(new HashSet<>())
                    .build();

            clientEntityMap.put(resultSet.getLong(IDCLIENT_FIELD), clientEntity);
        }

        return clientEntityMap;
    }
}
