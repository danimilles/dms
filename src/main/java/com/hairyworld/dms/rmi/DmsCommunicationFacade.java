package com.hairyworld.dms.rmi;

import com.hairyworld.dms.model.view.ClientViewData;
import com.hairyworld.dms.model.view.DogViewData;
import com.hairyworld.dms.model.view.SearchTableRow;

import java.util.List;

public interface DmsCommunicationFacade {
    List<ClientViewData> getClientTableData();

    List<SearchTableRow> getSearchTableData(SearchTableRow searchTableRow);

    ClientViewData getClientViewData(Long clientId);

    void saveClient(ClientViewData clientViewData);

    void deleteClient(Long id);

    void saveDog(DogViewData dogViewData);

    void deleteDog(Long id);

    DogViewData getDogViewData(Long clientId);

    ClientViewData getClientDogViewData(Long clientId);
}
