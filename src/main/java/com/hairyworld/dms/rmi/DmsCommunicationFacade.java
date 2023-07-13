package com.hairyworld.dms.rmi;

import com.hairyworld.dms.model.view.clientview.ClientViewData;
import com.hairyworld.dms.model.view.dogview.ClientDogViewData;
import com.hairyworld.dms.model.view.dogview.DogViewData;
import com.hairyworld.dms.model.view.mainview.ClientTableData;

import java.util.List;

public interface DmsCommunicationFacade {
    List<ClientTableData> getClientTableData();

    ClientViewData getClientViewData(Long clientId);

    void saveClient(ClientViewData clientViewData);

    void deleteClient(Long id);

    void saveDog(DogViewData dogViewName);

    void deleteDog(Long id);

    DogViewData getDogData(Long clientId);

    ClientDogViewData getClientDogViewData(Long clientId);
}
