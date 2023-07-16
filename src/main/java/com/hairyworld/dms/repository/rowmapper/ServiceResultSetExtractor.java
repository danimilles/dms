package com.hairyworld.dms.repository.rowmapper;

import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.model.entity.ServiceEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ServiceResultSetExtractor implements ResultSetExtractor<Map<Long, Entity>> {

    private static final String ID_FIELD = "id";
    private static final String DESCRIPTION_FIELD = "description";

    public ServiceResultSetExtractor() {
        super();
    }

    @Override
    public Map<Long, Entity> extractData(final ResultSet resultSet) throws DataAccessException, SQLException {
        final Map<Long, Entity> serviceEntityMap = new HashMap<>();

        while (resultSet.next()) {
            final Entity serviceEntity = ServiceEntity.builder()
                    .id(resultSet.getLong(ID_FIELD))
                    .description(resultSet.getString(DESCRIPTION_FIELD))
                    .build();

            serviceEntityMap.put(serviceEntity.getId(), serviceEntity);
        }

        return serviceEntityMap;
    }
}
