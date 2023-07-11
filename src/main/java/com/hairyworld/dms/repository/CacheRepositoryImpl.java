package com.hairyworld.dms.repository;

import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.repository.rowmapper.ClientResultSetExtractor;
import com.hairyworld.dms.repository.rowmapper.DateResultSetExtractor;
import com.hairyworld.dms.repository.rowmapper.PaymentResultSetExtractor;
import com.hairyworld.dms.repository.rowmapper.ServiceResultSetExtractor;
import com.hairyworld.dms.util.EntityType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Map;

@Repository
public class CacheRepositoryImpl implements CacheRepository {
    private static final Logger LOGGER = LogManager.getLogger(CacheRepositoryImpl.class);

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CacheRepositoryImpl(final DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Map<EntityType, Map<Long, Entity>> loadClientsAndDogs() {
        LOGGER.info("Loading clients and dog info...");

        try {
            return jdbcTemplate.query(Query.SELECT_CLIENT_AND_DOGS, new ClientResultSetExtractor());
        } catch (final Exception e) {
            LOGGER.error("Error loading clients and dog info", e);
            throw e;
        }
    }

    @Override
    public Map<Long, Entity> loadDates() {
        return loadEntity(Query.SELECT_DATES, new DateResultSetExtractor(), EntityType.DATE);
    }

    @Override
    public Map<Long, Entity> loadPayments() {
        return loadEntity(Query.SELECT_PAYMENTS, new PaymentResultSetExtractor(), EntityType.PAYMENT);
    }

    @Override
    public Map<Long, Entity> loadServices() {
        return loadEntity(Query.SELECT_SERVICES, new ServiceResultSetExtractor(), EntityType.SERVICE);
    }

    private Map<Long, Entity> loadEntity(final String query,
                                         final ResultSetExtractor<Map<Long, Entity>> resultSetExtractor,
                                         final EntityType entityType) {

        LOGGER.info("Loading {} info...", entityType.name().toLowerCase());

        try {
            return jdbcTemplate.query(query, new MapSqlParameterSource(), resultSetExtractor);
        } catch (final Exception e) {
            LOGGER.error("Error loading {} info...", entityType.name().toLowerCase(), e);
            throw e;
        }
    }
}
