package com.hairyworld.dms.repository;

import com.hairyworld.dms.model.EntityType;
import com.hairyworld.dms.model.entity.ClientEntity;
import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.repository.rowmapper.ClientResultSetExtractor;
import com.hairyworld.dms.repository.rowmapper.DateResultSetExtractor;
import com.hairyworld.dms.repository.rowmapper.PaymentResultSetExtractor;
import com.hairyworld.dms.repository.rowmapper.ServiceResultSetExtractor;
import com.sun.javafx.binding.StringFormatter;
import org.apache.commons.collections.CollectionUtils;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class EntityRepositoryImpl implements EntityRepository {
    private static final Logger LOGGER = LogManager.getLogger(EntityRepositoryImpl.class);
    private static final String NAME_FIELD = "name";
    private static final String PHONE_FIELD = "phone";
    private static final String OBSERVATIONS_FIELD = "observations";
    private static final String ID_FIELD = "id";
    public static final String QUERY_PARAMS_LOG = "Query -> [{}], Params -> [{}]";

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
            return ObjectUtils.defaultIfNull(result, new HashMap<>());
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

        LOGGER.info("Loading {} info...", entityType.name());

        try {
            final Map<Long, Entity> result = jdbcTemplate.query(query, new MapSqlParameterSource(), resultSetExtractor);
            return ObjectUtils.defaultIfNull(result, new HashMap<>());
        } catch (final Exception e) {
            LOGGER.error("Error loading {} info...", entityType.name().toLowerCase(), e);
            throw e;
        }
    }

    public Long saveEntity(final Entity entity, final EntityType entityType) {
        LOGGER.info("Saving {} info...", entityType.name());

        try {
            switch (entityType) {
                case CLIENT:
                    return saveClient(entity);
                default:
                    LOGGER.error("Unsupported entity type -> {}", entityType.name());
                    return null;
            }
        } catch (final Exception e) {
            LOGGER.error("Error saving {} info...", entityType.name().toLowerCase(), e);
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
        LOGGER.debug("Query -> [{}], params -> [{}]", query, parameters.getValues());

        jdbcTemplate.update(query, parameters, keyHolder, new String[]{ID_FIELD});
        return keyHolder.getKey().longValue();
    }

    public List<Long> deleteEntity(final Long id, final EntityType entityType) {
        LOGGER.info("Deleting {} info...", entityType.name());

        try {
            switch (entityType) {
                case CLIENT:
                    return deleteClient(id);
                default:
                    LOGGER.error("Unsupported entity type -> {}", entityType.name());
                    return Collections.emptyList();
            }
        } catch (final Exception e) {
            LOGGER.error("Error deleting {} info...", entityType.name().toLowerCase(), e);
            throw e;
        }
    }

    private List<Long> deleteClient(final Long id) {
        final MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue(ID_FIELD, id);

        LOGGER.debug(QUERY_PARAMS_LOG, Query.SELECT_TO_DELETE_DOG_FROM_CLIENT, parameters.getValues());
        final List<Long> iddogs = jdbcTemplate.queryForList(Query.SELECT_TO_DELETE_DOG_FROM_CLIENT, parameters,
                Long.class);

        if (!CollectionUtils.isEmpty(iddogs)) {
            final String iddogsstring = iddogs.stream().map(String::valueOf).collect(Collectors.joining(","));

            final String query = StringFormatter.format(Query.DELETE_DOG_FROM_CLIENT, iddogsstring).getValue();
            LOGGER.debug(QUERY_PARAMS_LOG, query, iddogs);
            jdbcTemplate.update(query, new MapSqlParameterSource());
        }

        LOGGER.debug(QUERY_PARAMS_LOG, Query.DELETE_CLIENT, parameters.getValues());
        jdbcTemplate.update(Query.DELETE_CLIENT, parameters);

        return iddogs;
    }
}
