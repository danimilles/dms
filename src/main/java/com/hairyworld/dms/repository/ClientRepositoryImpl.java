package com.hairyworld.dms.repository;

import com.google.common.collect.HashBiMap;
import com.hairyworld.dms.model.entity.EntityType;
import com.hairyworld.dms.model.entity.ClientEntity;
import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.repository.rowmapper.ClientResultSetExtractor;
import com.sun.javafx.binding.StringFormatter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ClientRepositoryImpl extends EntityRepositoryImpl {

    private static final Logger LOGGER = LogManager.getLogger(ClientRepositoryImpl.class);

    public ClientRepositoryImpl(final DataSource dataSource) {
        super(dataSource, LOGGER);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.CLIENT;
    }

    @Override
    public Map<Long, Entity> loadAll() {
        LOGGER.info("Loading clients ...");
        return super.loadAll(Query.SELECT_CLIENTS, new ClientResultSetExtractor());
    }

    @Override
    public Long save(final Entity entity) {
        try {
            if (entity instanceof ClientEntity clientEntity) {
                LOGGER.info("Saving client ...");

                final MapSqlParameterSource parameters = new MapSqlParameterSource();

                final String query = clientEntity.getId() != null ?
                        StringFormatter.format(Query.INSERT_CLIENT, ID_FIELD + ", ", ":" + ID_FIELD + ", ").getValue()
                        : StringFormatter.format(Query.INSERT_CLIENT, Strings.EMPTY, Strings.EMPTY).getValue();

                if (clientEntity.getId() != null) {
                    parameters.addValue(ID_FIELD, clientEntity.getId());
                }

                parameters.addValue(NAME_FIELD, clientEntity.getName());
                parameters.addValue(PHONE_FIELD, clientEntity.getPhone());
                parameters.addValue(OBSERVATIONS_FIELD, clientEntity.getObservations());
                parameters.addValue(DNI_FIELD, clientEntity.getObservations());

                final KeyHolder keyHolder = new GeneratedKeyHolder();
                LOGGER.debug(QUERY_PARAMS_LOG, query, parameters.getValues());

                jdbcTemplate.update(query, parameters, keyHolder, new String[]{ID_FIELD});
                return keyHolder.getKey().longValue();
            }
        } catch (final Exception e) {
            LOGGER.error("Error saving dog info...", e);
            throw e;
        }

        return null;
    }

    @Override
    public void delete(final Long id) {
        LOGGER.info("Deleting client {} info...", id);

        try {
            final MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue(ID_FIELD, id);

            LOGGER.debug(QUERY_PARAMS_LOG, Query.DELETE_CLIENT, parameters.getValues());
            jdbcTemplate.update(Query.DELETE_CLIENT, parameters);
        } catch (final Exception e) {
            LOGGER.error("Error deleting client info...", e);
            throw e;
        }
    }

    public Map<Long, Long> loadAllClientAndDogRelations() {
        LOGGER.info("Loading client and dog relations info...");

        LOGGER.debug(QUERY_PARAMS_LOG, Query.SELECT_CLIENTDOGS, null);
        try {
            final Map<Long, Long> result = jdbcTemplate.query(Query.SELECT_CLIENTDOGS,
                    rs -> {
                        final Map<Long, Long> clientDogRelations = new HashMap<>();

                        while (rs.next()) {
                            final Long idClient = rs.getLong(IDCLIENT_FIELD);
                            final Long idDog = rs.getLong(IDDOG_FIELD);
                            clientDogRelations.put(idClient, idDog);
                        }

                        return clientDogRelations;
                    });
            return ObjectUtils.defaultIfNull(result, HashBiMap.create());
        } catch (final Exception e) {
            LOGGER.error("Error client and dog relations info", e);
            throw e;
        }
    }


    public void saveClientDogRelation(final Long idclient, final Long iddog) {
        LOGGER.info("Saving client and dog relations info...");

        final MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue(IDCLIENT_FIELD, idclient);
        parameters.addValue(IDDOG_FIELD, iddog);

        LOGGER.debug(QUERY_PARAMS_LOG, Query.INSERT_CLIENTDOG, parameters.getValues());

        try {
            jdbcTemplate.update(Query.INSERT_CLIENTDOG, parameters);
        } catch (final Exception e) {
            LOGGER.error("Error saving client-dog relation", e);
            throw e;
        }
    }


    public List<Long> getDogToDeleteForClient(final Long id) {
        LOGGER.info("Searching dog to delete info...");

        final MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue(ID_FIELD, id);

        LOGGER.debug(QUERY_PARAMS_LOG, Query.SELECT_TO_DELETE_DOG_FROM_CLIENT, parameters.getValues());

        try {
            return jdbcTemplate.queryForList(Query.SELECT_TO_DELETE_DOG_FROM_CLIENT, parameters,
                    Long.class);
        } catch (final Exception e) {
            LOGGER.error("Error retrieving dogs to delete", e);
            throw e;
        }
    }
}
