package com.hairyworld.dms.repository;

import com.hairyworld.dms.model.entity.EntityType;
import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.repository.rowmapper.PaymentResultSetExtractor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
        return null;
    }


    @Override
    public void delete(final Long id) {
    }
}
