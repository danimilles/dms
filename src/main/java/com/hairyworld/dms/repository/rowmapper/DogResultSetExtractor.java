package com.hairyworld.dms.repository.rowmapper;

import com.hairyworld.dms.model.entity.DogEntity;
import com.hairyworld.dms.model.entity.Entity;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class DogResultSetExtractor implements ResultSetExtractor<Map<Long, Entity>> {

    private static final String IDDOG_FIELD = "iddog";
    private static final String DOGNAME_FIELD = "dogname";
    private static final String DOGOBSERVATIONS_FIELD = "dogobservations";
    private static final String MAINTAINMENT_FIELD = "maintainment";
    private static final String RACE_FIELD = "race";
    private static final String IMAGE_FIELD = "image";

    public DogResultSetExtractor() {
        super();
    }

    @Override
    public Map<Long, Entity> extractData(final ResultSet resultSet) throws DataAccessException, SQLException {
        final Map<Long, Entity> dogEntityMap = new HashMap<>();

        while (resultSet.next()) {
            final Entity dogEntity = DogEntity.builder().race(resultSet.getString(RACE_FIELD))
                    .maintainment(resultSet.getString(MAINTAINMENT_FIELD))
                    .observations(resultSet.getString(DOGOBSERVATIONS_FIELD))
                    .name(resultSet.getString(DOGNAME_FIELD))
                    .id(resultSet.getLong(IDDOG_FIELD))
                    .clientIds(new HashSet<>())
                    .image(resultSet.getBytes(IMAGE_FIELD))
                    .build();

            dogEntityMap.put(resultSet.getLong(IDDOG_FIELD), dogEntity);
        }

        return dogEntityMap;
    }
}
