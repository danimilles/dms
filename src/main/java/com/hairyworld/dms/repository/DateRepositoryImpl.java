package com.hairyworld.dms.repository;

import com.hairyworld.dms.model.entity.EntityType;
import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.repository.rowmapper.DateResultSetExtractor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Map;

@Repository
public class DateRepositoryImpl extends EntityRepositoryImpl {
    private static final Logger LOGGER = LogManager.getLogger(DateRepositoryImpl.class);

    public DateRepositoryImpl(final DataSource dataSource) {
        super(dataSource, LOGGER);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.DATE;
    }


    @Override
    public Map<Long, Entity> loadAll() {
        LOGGER.info("Loading dates info...");
        return super.loadAll(Query.SELECT_DATES, new DateResultSetExtractor());
    }

    @Override
    public Long save(final Entity entity) {
       return null;
    }


    @Override
    public void delete(final Long id) {
    }
}
