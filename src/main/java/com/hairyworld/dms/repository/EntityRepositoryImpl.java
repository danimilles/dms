package com.hairyworld.dms.repository;

import com.hairyworld.dms.model.EntityType;
import com.hairyworld.dms.model.entity.ClientEntity;
import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.repository.rowmapper.ClientResultSetExtractor;
import com.hairyworld.dms.repository.rowmapper.DateResultSetExtractor;
import com.hairyworld.dms.repository.rowmapper.PaymentResultSetExtractor;
import com.hairyworld.dms.repository.rowmapper.ServiceResultSetExtractor;
import com.sun.javafx.binding.StringFormatter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class EntityRepositoryImpl implements EntityRepository {
    private static final Logger LOGGER = LogManager.getLogger(EntityRepositoryImpl.class);
    private static final String NAME_FIELD = "name";
    private static final String PHONE_FIELD = "phone";
    private static final String OBSERVATIONS_FIELD = "observations";
    private static final String ID_FIELD = "id";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public EntityRepositoryImpl(final DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Map<EntityType, Map<Long, Entity>> loadClientsAndDogs() {
        LOGGER.info("Loading clients and dog info...");

        try {
            final Map<EntityType, Map<Long, Entity>> result =
                    jdbcTemplate.query(Query.SELECT_CLIENT_AND_DOGS, new ClientResultSetExtractor());
            return ObjectUtils.defaultIfNull(result, new HashMap());
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
            final Map<Long, Entity> result = jdbcTemplate.query(query, new MapSqlParameterSource(), resultSetExtractor);
            return ObjectUtils.defaultIfNull(result, new HashMap());
        } catch (final Exception e) {
            LOGGER.error("Error loading {} info...", entityType.name().toLowerCase(), e);
            throw e;
        }
    }

    public Long saveEntity(final Entity entity, final EntityType entityType) {
        LOGGER.info("Saving {} info...", entityType.name().toLowerCase());

        try {
            switch (entityType) {
                case CLIENT:
                    return saveClient(entity);
                default:
                    LOGGER.error("Error saving {} info...", entityType.name().toLowerCase());
                    return null;
            }
        } catch (final Exception e) {
            LOGGER.error("Error loading {} info...", entityType.name().toLowerCase(), e);
            throw e;
        }
    }

    private Long saveClient(final Entity entity) {
        final MapSqlParameterSource parameters = new MapSqlParameterSource();
        final ClientEntity clientEntity = (ClientEntity) entity;

        final String query = clientEntity.getId() != null ?
                StringFormatter.format(Query.INSERT_CLIENT, ID_FIELD + ", ", ":" + ID_FIELD + ", ").getValue()
                : StringFormatter.format(Query.INSERT_CLIENT, Strings.EMPTY, Strings.EMPTY).getValue();

        if (clientEntity.getId() != null ) {
            parameters.addValue(ID_FIELD, clientEntity.getId());
        }

        parameters.addValue(NAME_FIELD, clientEntity.getName());
        parameters.addValue(PHONE_FIELD, clientEntity.getPhone());
        parameters.addValue(OBSERVATIONS_FIELD, clientEntity.getObservations());

        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(query, parameters, keyHolder, new String[]{ID_FIELD});
        return keyHolder.getKey().longValue();
    }
}
