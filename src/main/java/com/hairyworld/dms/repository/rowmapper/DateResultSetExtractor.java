package com.hairyworld.dms.repository.rowmapper;

import com.hairyworld.dms.model.entity.DateEntity;
import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.util.DmsUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DateResultSetExtractor implements ResultSetExtractor<Map<Long, Entity>> {

    public static final String ID_FIELD = "id";
    public static final String DATETIMESTART_FIELD = "datetimestart";
    public static final String DATETIMEEND_FIELD = "datetimeend";
    public static final String DESCRIPTION_FIELD = "description";
    public static final String IDSERVICE_FIELD = "idservice";
    public static final String IDCLIENT_FIELD = "idclient";
    public static final String IDDOG_FIELD = "iddog";

    public DateResultSetExtractor() {
        super();
    }

    @Override
    public Map<Long, Entity> extractData(final ResultSet resultSet) throws DataAccessException, SQLException {
        final Map<Long, Entity> dateEntityMap = new HashMap<>();

        while (resultSet.next()) {
            final Entity dateEntity = DateEntity.builder()
                    .id(resultSet.getLong(ID_FIELD))
                    .datetimestart(DmsUtils.parseDate(resultSet.getString(DATETIMESTART_FIELD)))
                    .datetimeend(DmsUtils.parseDate(resultSet.getString(DATETIMEEND_FIELD)))
                    .description(resultSet.getString(DESCRIPTION_FIELD))
                    .iddog(resultSet.getLong(IDDOG_FIELD))
                    .idclient(resultSet.getLong(IDCLIENT_FIELD))
                    .idservice(resultSet.getLong(IDSERVICE_FIELD))
                    .build();

            dateEntityMap.put(dateEntity.getId(), dateEntity);
        }

        return dateEntityMap;
    }
}
