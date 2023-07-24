package com.hairyworld.dms.repository;

import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.model.entity.EntityType;
import com.hairyworld.dms.model.entity.PaymentEntity;
import com.hairyworld.dms.repository.rowmapper.PaymentResultSetExtractor;
import com.hairyworld.dms.util.DmsUtils;
import com.sun.javafx.binding.StringFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.joda.time.DateTime;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Map;

@Repository
public class PaymentRepositoryImpl extends EntityRepositoryImpl {
    private static final Logger LOGGER = LogManager.getLogger(PaymentRepositoryImpl.class);

    public PaymentRepositoryImpl(final DataSource dataSource) {
        super(dataSource, LOGGER);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.PAYMENT;
    }

    @Override
    public Map<Long, Entity> loadAll() {
        LOGGER.info("Loading payments ...");
        return super.loadAll(Query.SELECT_PAYMENTS, new PaymentResultSetExtractor());
    }

    @Override
    public Long save(final Entity entity) {
        if (entity instanceof PaymentEntity paymentEntity) {
            LOGGER.info("Saving payment info...");

            final MapSqlParameterSource parameters = new MapSqlParameterSource();

            final String query = paymentEntity.getId() != null ?
                    StringFormatter.format(Query.INSERT_PAYMENT, ID_FIELD + ", ", ":" + ID_FIELD + ", ").getValue()
                    : StringFormatter.format(Query.INSERT_PAYMENT, Strings.EMPTY, Strings.EMPTY).getValue();

            if (paymentEntity.getId() != null) {
                parameters.addValue(ID_FIELD, paymentEntity.getId());
            }

            parameters.addValue(DESCRIPTION_FIELD, paymentEntity.getDescription());
            parameters.addValue(AMOUNT, paymentEntity.getAmount());
            parameters.addValue(IDSERVICE_FIELD, paymentEntity.getIdservice());
            parameters.addValue(IDCLIENT_FIELD, paymentEntity.getIdclient());
            parameters.addValue(DATETIME_FIELD, paymentEntity.getDatetime() != null ?
                    DmsUtils.dateToString(paymentEntity.getDatetime()) :
                    DmsUtils.dateToString(DateTime.now()));

            return super.save(query, parameters);
        }
        return null;
    }


    @Override
    public void delete(final Long id) {
        LOGGER.info("Deleting payment info...");
        super.delete(Query.DELETE_PAYMENT, id);
    }
}
