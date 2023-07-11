package com.hairyworld.dms.repository.rowmapper;

import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.model.entity.PaymentEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PaymentResultSetExtractor implements ResultSetExtractor<Map<Long, Entity>> {

    public static final String ID_FIELD = "id";
    public static final String DATETIME_FIELD = "datetime";
    public static final String DESCRIPTION_FIELD = "description";
    public static final String IDSERVICE_FIELD = "idservice";
    public static final String IDCLIENT_FIELD = "idclient";

    public PaymentResultSetExtractor() {
        super();
    }

    @Override
    public Map<Long, Entity> extractData(final ResultSet resultSet) throws DataAccessException, SQLException {
        final Map<Long, Entity> paymentEntityMap = new HashMap<>();

        while (resultSet.next()) {
            final Entity paymentEntity = PaymentEntity.builder()
                    .id(resultSet.getLong(ID_FIELD))
                    .datetime(resultSet.getTimestamp(DATETIME_FIELD))
                    .description(resultSet.getString(DESCRIPTION_FIELD))
                    .idservice(resultSet.getLong(IDSERVICE_FIELD))
                    .idclient(resultSet.getLong(IDCLIENT_FIELD))
                    .build();

            paymentEntityMap.put(paymentEntity.getId(), paymentEntity);
        }

        return paymentEntityMap;
    }
}
