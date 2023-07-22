package com.hairyworld.dms.repository;

import com.hairyworld.dms.model.entity.EntityType;
import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.model.entity.ServiceEntity;
import com.hairyworld.dms.repository.rowmapper.ServiceResultSetExtractor;
import com.sun.javafx.binding.StringFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Map;

@Repository
public class ServiceRepositoryImpl extends EntityRepositoryImpl {
    private static final Logger LOGGER = LogManager.getLogger(ServiceRepositoryImpl.class);
    private static final String DESCRIPTION_FIELD = "description";

    public ServiceRepositoryImpl(final DataSource dataSource) {
        super(dataSource, LOGGER);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.SERVICE;
    }

    @Override
    public Map<Long, Entity> loadAll() {
        LOGGER.info("Loading services info...");
        return super.loadAll(Query.SELECT_SERVICES, new ServiceResultSetExtractor());
    }

    @Override
    public Long save(final Entity entity) {
        if (entity instanceof ServiceEntity serviceEntity) {
            LOGGER.info("Saving service info...");

            final MapSqlParameterSource parameters = new MapSqlParameterSource();

            final String query = serviceEntity.getId() != null ?
                    StringFormatter.format(Query.INSERT_SERVICE, ID_FIELD + ", ", ":" + ID_FIELD + ", ").getValue()
                    : StringFormatter.format(Query.INSERT_SERVICE, Strings.EMPTY, Strings.EMPTY).getValue();

            if (serviceEntity.getId() != null) {
                parameters.addValue(ID_FIELD, serviceEntity.getId());
            }

            parameters.addValue(DESCRIPTION_FIELD, serviceEntity.getDescription());

            return super.save(query, parameters);
        }
        return null;
    }


    @Override
    public void delete(final Long id) {
        LOGGER.info("Deleting service info...");
        super.delete(Query.DELETE_SERVICE, id);
    }
}
