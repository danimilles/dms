package com.hairyworld.dms.repository;

import com.hairyworld.dms.model.entity.ClientDogRelation;
import com.hairyworld.dms.model.entity.ClientEntity;
import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.model.entity.EntityType;
import com.hairyworld.dms.repository.rowmapper.ClientResultSetExtractor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
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
        if (entity instanceof ClientEntity clientEntity) {
            LOGGER.info("Saving client ...");

            final MapSqlParameterSource parameters = new MapSqlParameterSource();

            String query = Query.INSERT_CLIENT;
            if (clientEntity.getId() != null) {
                parameters.addValue(ID_FIELD, clientEntity.getId());
                query = Query.UPDATE_CLIENT;
            }

            parameters.addValue(NAME_FIELD, clientEntity.getName());
            parameters.addValue(PHONE_FIELD, clientEntity.getPhone());
            parameters.addValue(OBSERVATIONS_FIELD, clientEntity.getObservations());
            parameters.addValue(DNI_FIELD, clientEntity.getObservations());

            return super.save(query, parameters);
        }

        return null;
    }

    @Override
    public void delete(final Long id) {
        LOGGER.info("Deleting client info...");
        super.delete(Query.DELETE_CLIENT, id);
    }

    public List<ClientDogRelation> loadAllClientAndDogRelations() {
        LOGGER.info("Loading client and dog relations info...");

        LOGGER.debug(QUERY_PARAMS_LOG, Query.SELECT_CLIENTDOGS, null);
        try {
            final List<ClientDogRelation> result = jdbcTemplate.query(Query.SELECT_CLIENTDOGS,
                    rs -> {
                        final List<ClientDogRelation> clientDogRelationRelations = new ArrayList<>();

                        while (rs.next()) {
                            final Long idClient = rs.getLong(IDCLIENT_FIELD);
                            final Long idDog = rs.getLong(IDDOG_FIELD);
                            clientDogRelationRelations.add(new ClientDogRelation(idClient, idDog));
                        }

                        return clientDogRelationRelations;
                    });
            return ObjectUtils.defaultIfNull(result, new ArrayList<>());
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
        super.save(Query.INSERT_CLIENTDOG, parameters);
    }

    public void deleteClientDogRelation(final Long idclient, final Long iddog) {
        LOGGER.info("Saving client and dog relations info...");

        final MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue(IDCLIENT_FIELD, idclient);
        parameters.addValue(IDDOG_FIELD, iddog);

        LOGGER.debug(QUERY_PARAMS_LOG, Query.DELETE_CLIENTDOG, parameters.getValues());
        try {
            jdbcTemplate.update(Query.DELETE_CLIENTDOG, parameters);
        } catch (final Exception e) {
            LOGGER.error("Error deleting client dog relation", e);
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
