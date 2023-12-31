package com.hairyworld.dms.repository;

import com.hairyworld.dms.model.entity.DogEntity;
import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.model.entity.EntityType;
import com.hairyworld.dms.repository.rowmapper.DogResultSetExtractor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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
        if (entity instanceof DogEntity dogEntity) {
            LOGGER.info("Saving dog info...");

            final MapSqlParameterSource parameters = new MapSqlParameterSource();

            String query = Query.INSERT_DOG;
            if (dogEntity.getId() != null) {
                parameters.addValue(ID_FIELD, dogEntity.getId());
                query = Query.UPDATE_DOG;
            }

            parameters.addValue(NAME_FIELD, dogEntity.getName());
            parameters.addValue(RACE_FIELD, dogEntity.getRace());
            parameters.addValue(OBSERVATIONS_FIELD, dogEntity.getObservations());
            parameters.addValue(MAINTAINMENT_FIELD, dogEntity.getMaintainment());
            parameters.addValue(IMAGE_FIELD, dogEntity.getImage());

            return super.save(query, parameters);
        }
        return null;
    }


    @Override
    public void delete(final Long id) {
        LOGGER.info("Deleting dog info...");
        super.delete(Query.DELETE_DOG, id);
    }
}
