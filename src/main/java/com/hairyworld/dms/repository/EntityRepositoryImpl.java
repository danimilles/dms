package com.hairyworld.dms.repository;

import com.hairyworld.dms.model.entity.Entity;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


public abstract class EntityRepositoryImpl implements EntityRepository {

    private final Logger logger;

    protected static final String QUERY_PARAMS_LOG = "Query -> [{}], Params -> [{}]";
    protected static final String ID_FIELD = "id";
    protected static final String NAME_FIELD = "name";
    protected static final String DNI_FIELD = "dni";
    protected static final String OBSERVATIONS_FIELD = "observations";
    protected static final String IDDOG_FIELD = "iddog";
    protected static final String IDCLIENT_FIELD = "idclient";
    protected static final String RACE_FIELD = "race";
    protected static final String IMAGE_FIELD = "image";
    protected static final String MAINTAINMENT_FIELD = "maintainment";
    protected static final String PHONE_FIELD = "phone";


    protected final NamedParameterJdbcTemplate jdbcTemplate;

    protected EntityRepositoryImpl(final DataSource dataSource, final Logger logger) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.logger = logger;
    }

    protected Map<Long, Entity> loadAll(final String query, final ResultSetExtractor<Map<Long, Entity>> resultSetExtractor) {
        try {
            logger.debug(QUERY_PARAMS_LOG, query, null);
            final Map<Long, Entity> result =
                    jdbcTemplate.query(query, resultSetExtractor);
            return ObjectUtils.defaultIfNull(result, new HashMap<>());
        } catch (final Exception e) {
            logger.error("Error loading info", e);
            throw e;
        }
    }


    protected Long save(final String query, final MapSqlParameterSource parameters) {
        try {
            final KeyHolder keyHolder = new GeneratedKeyHolder();
            logger.debug(QUERY_PARAMS_LOG, query, parameters.getValues());

            jdbcTemplate.update(query, parameters, keyHolder, new String[]{ID_FIELD});
            return parameters.getValues().get(ID_FIELD) != null ? Long.parseLong(parameters.getValue(ID_FIELD).toString()) :
                    keyHolder.getKey().longValue();
        } catch (final Exception e) {
            logger.error("Error saving info...", e);
            throw e;
        }
    }


    protected void delete(final String query, final Long id) {
        try {
            final MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue(ID_FIELD, id);

            logger.debug(QUERY_PARAMS_LOG, query, parameters.getValues());
            jdbcTemplate.update(query, parameters);
        } catch (final Exception e) {
            logger.error("Error deleting info...", e);
            throw e;
        }
    }
}
