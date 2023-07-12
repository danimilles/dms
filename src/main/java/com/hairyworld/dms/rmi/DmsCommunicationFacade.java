package com.hairyworld.dms.rmi;

import com.hairyworld.dms.model.view.ClientTableData;
import com.hairyworld.dms.model.view.ClientViewData;

import java.util.List;

public interface DmsCommunicationFacade {
    List<ClientTableData> getClientTableData();

    ClientViewData getClientViewData(Long clientId);

    void saveClient(ClientViewData clientViewData);

    void deleteClient(Long id);
}
