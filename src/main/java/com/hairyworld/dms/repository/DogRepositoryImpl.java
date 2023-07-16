package com.hairyworld.dms.repository;

import com.hairyworld.dms.model.entity.EntityType;
import com.hairyworld.dms.model.entity.DogEntity;
import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.repository.rowmapper.DogResultSetExtractor;
import com.sun.javafx.binding.StringFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Map;

@Repository
public class DogRepositoryImpl extends EntityRepositoryImpl {
    private static final Logger LOGGER = LogManager.getLogger(DogRepositoryImpl.class);

    public DogRepositoryImpl(final DataSource dataSource) {
        super(dataSource, LOGGER);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.DOG;
    }

    @Override
    public Map<Long, Entity> loadAll() {
        LOGGER.info("Loading dogs ...");
        return super.loadAll(Query.SELECT_DOGS, new DogResultSetExtractor());
    }

    @Override
    public Long save(final Entity entity) {
        LOGGER.info("Saving dog info...");

        try {
            if (entity instanceof DogEntity dogEntity) {

                final MapSqlParameterSource parameters = new MapSqlParameterSource();

                final String query = dogEntity.getId() != null ?
                        StringFormatter.format(Query.INSERT_DOG, ID_FIELD + ", ", ":" + ID_FIELD + ", ").getValue()
                        : StringFormatter.format(Query.INSERT_DOG, Strings.EMPTY, Strings.EMPTY).getValue();

                if (dogEntity.getId() != null) {
                    parameters.addValue(ID_FIELD, dogEntity.getId());
                }

                parameters.addValue(NAME_FIELD, dogEntity.getName());
                parameters.addValue(RACE_FIELD, dogEntity.getRace());
                parameters.addValue(OBSERVATIONS_FIELD, dogEntity.getObservations());
                parameters.addValue(MAINTAINMENT_FIELD, dogEntity.getMaintainment());
                parameters.addValue(IMAGE_FIELD, dogEntity.getImage());

                final KeyHolder keyHolder = new GeneratedKeyHolder();
                LOGGER.debug("Query -> [{}], params -> [{}]", query, parameters.getValues());

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
        LOGGER.info("Deleting dog {} info...", id);

        try {
            final MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue(ID_FIELD, id);

            LOGGER.debug(QUERY_PARAMS_LOG, Query.DELETE_DOG, parameters.getValues());
            jdbcTemplate.update(Query.DELETE_DOG, parameters);
        } catch (final Exception e) {
            LOGGER.error("Error deleting dog info...", e);
            throw e;
        }
    }
}
