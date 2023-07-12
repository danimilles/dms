package com.hairyworld.dms.repository.rowmapper;

import com.hairyworld.dms.model.EntityType;
import com.hairyworld.dms.model.entity.ClientEntity;
import com.hairyworld.dms.model.entity.DogEntity;
import com.hairyworld.dms.model.entity.Entity;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ClientResultSetExtractor implements ResultSetExtractor<Map<EntityType, Map<Long, Entity>>> {

    private static final String IDCLIENT_FIELD = "idclient";
    private static final String CLIENTNAME_FIELD = "clientname";
    private static final String PHONE_FIELD = "phone";
    private static final String CLIENTOBSERVATIONS_FIELD = "clientobservations";
    private static final String IDDOG_FIELD = "iddog";
    private static final String DOGNAME_FIELD = "dogname";
    private static final String DOGOBSERVATIONS_FIELD = "dogobservations";
    private static final String MAINTAINMENT_FIELD = "maintainment";
    private static final String RACE_FIELD = "race";

    public ClientResultSetExtractor() {
        super();
    }

    @Override
    public Map<EntityType, Map<Long, Entity>> extractData(final ResultSet resultSet) throws DataAccessException, SQLException {
        final Map<EntityType, Map<Long, Entity>> entityTypeMap = new HashMap<>();
        final Map<Long, Entity> clientEntityMap = new HashMap<>();
        final Map<Long, Entity> dogEntityMap = new HashMap<>();

        entityTypeMap.put(EntityType.CLIENT, clientEntityMap);
        entityTypeMap.put(EntityType.DOG, dogEntityMap);

        while (resultSet.next()) {
            ClientEntity clientEntity = (ClientEntity) clientEntityMap.get(resultSet.getLong(IDCLIENT_FIELD));

            if (clientEntity == null) {
                clientEntity = ClientEntity.builder()
                        .id(resultSet.getLong(IDCLIENT_FIELD))
                        .name(resultSet.getString(CLIENTNAME_FIELD))
                        .phone(resultSet.getInt(PHONE_FIELD))
                        .observations(resultSet.getString(CLIENTOBSERVATIONS_FIELD))
                        .dogIds(new HashSet<>())
                        .build();
            }


            if (resultSet.getBigDecimal(IDDOG_FIELD) != null) {
                clientEntity.getDogIds().add(resultSet.getLong(IDDOG_FIELD));

                DogEntity dogEntity = (DogEntity) dogEntityMap.get(resultSet.getLong(IDDOG_FIELD));

                if (dogEntity == null) {
                    dogEntity = DogEntity.builder().race(resultSet.getString(RACE_FIELD))
                            .maintainment(resultSet.getString(MAINTAINMENT_FIELD))
                            .observations(resultSet.getString(DOGOBSERVATIONS_FIELD))
                            .name(resultSet.getString(DOGNAME_FIELD))
                            .id(resultSet.getLong(IDDOG_FIELD))
                            .clientIds(new HashSet<>())
                            .build();
                }

                dogEntity.getClientIds().add(resultSet.getLong(IDCLIENT_FIELD));
                dogEntityMap.put(resultSet.getLong(IDDOG_FIELD), dogEntity);
            }

            clientEntityMap.put(resultSet.getLong(IDCLIENT_FIELD), clientEntity);
        }

        return entityTypeMap;
    }
}