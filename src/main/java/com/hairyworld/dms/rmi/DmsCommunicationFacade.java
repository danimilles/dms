package com.hairyworld.dms.rmi;

import com.hairyworld.dms.model.view.ClientViewData;
import com.hairyworld.dms.model.view.DogViewData;

import java.util.List;

public interface DmsCommunicationFacade {
    List<ClientViewData> getClientTableData();

    ClientViewData getClientViewData(Long clientId);

    void saveClient(ClientViewData clientViewData);

    void deleteClient(Long id);

    void saveDog(DogViewData dogViewData);

    void deleteDog(Long id);

    DogViewData getDogData(Long clientId);

    ClientViewData getClientDogViewData(Long clientId);
}
