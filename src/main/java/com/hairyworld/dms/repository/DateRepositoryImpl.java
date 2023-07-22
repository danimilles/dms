package com.hairyworld.dms.repository;

import com.hairyworld.dms.model.entity.DateEntity;
import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.model.entity.EntityType;
import com.hairyworld.dms.repository.rowmapper.DateResultSetExtractor;
import com.hairyworld.dms.util.DmsUtils;
import com.sun.javafx.binding.StringFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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
        if (entity instanceof DateEntity dateEntity) {
            LOGGER.info("Saving date info...");

            final MapSqlParameterSource parameters = new MapSqlParameterSource();

            final String query = dateEntity.getId() != null ?
                    StringFormatter.format(Query.INSERT_DATE, ID_FIELD + ", ", ":" + ID_FIELD + ", ").getValue()
                    : StringFormatter.format(Query.INSERT_DATE, Strings.EMPTY, Strings.EMPTY).getValue();

            if (dateEntity.getId() != null) {
                parameters.addValue(ID_FIELD, dateEntity.getId());
            }

            parameters.addValue(DESCRIPTION_FIELD, dateEntity.getDescription());
            parameters.addValue(DATETIMESTART_FIELD, DmsUtils.dateToString(dateEntity.getDatetimestart()));
            parameters.addValue(DATETIMEEND_FIELD, DmsUtils.dateToString(dateEntity.getDatetimeend()));
            parameters.addValue(IDDOG_FIELD, dateEntity.getIddog());
            parameters.addValue(IDCLIENT_FIELD, dateEntity.getIdclient());
            parameters.addValue(IDSERVICE_FIELD, dateEntity.getIdservice());

            return super.save(query, parameters);
        }
        return null;
    }


    @Override
    public void delete(final Long id) {
        LOGGER.info("Deleting date info...");
        super.delete(Query.DELETE_DATE, id);
    }
}
