package com.hairyworld.dms.service;

import com.hairyworld.dms.model.EntityType;
import com.hairyworld.dms.model.entity.ClientEntity;
import com.hairyworld.dms.model.entity.DateEntity;
import com.hairyworld.dms.model.entity.Entity;
import com.hairyworld.dms.model.mapper.ClientEntityToClientTableDataMapper;
import com.hairyworld.dms.model.view.ClientTableData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ServerServiceImpl implements ServerService {
    private static final Logger LOGGER = LogManager.getLogger(ServerServiceImpl.class);

    private final EntityService entityService;

    public ServerServiceImpl(final EntityServiceImpl cacheService) {
        this.entityService = cacheService;
    }

    @Override
    public List<ClientTableData> getClientTableData() {
        final List<ClientTableData> clientTableData = new ArrayList<>();
        final Collection<Entity> clients = entityService.getAll(EntityType.CLIENT);

        for (final Entity entry : clients) {
            final Set<Entity> dogs =
                    ((ClientEntity)entry).getDogIds().stream().map(iddog -> entityService.get(iddog, EntityType.DOG))
                            .collect(Collectors.toSet());

            final DateEntity nextDate = entityService.getNextDateForClient(entry.getId());

            clientTableData.add(ClientEntityToClientTableDataMapper.map((ClientEntity) entry, dogs, nextDate));
        }

        return clientTableData;
    }

}
